package DEAD.LAND.core;

import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;

public class ControladorMovimento {

    private static final int CENARIO_5 = 4;
    private static final int CENARIO_6 = 5;
    private static final int CENARIO_8 = 7;
    private static final int COLUNA_ESQUERDA_SAIDA_INFERIOR = 15;
    private static final int COLUNA_DIREITA_SAIDA_INFERIOR = 16;
    private static final int JOGADOR_X_ENTRADA_SUPERIOR = 744;
    private static final int JOGADOR_Y_CENARIO_5 = 96;
    private static final int JOGADOR_Y_CENARIO_6 = 48;

    private final verificadorDeColisao verificadorDeColisao;
    private boolean dashPressionado;
    private long ultimaAtualizacaoNanos;

    public ControladorMovimento() {
        this.verificadorDeColisao = new verificadorDeColisao();
    }

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        player jogador = cenaDoJogo.getJogador();
        long agora = System.nanoTime();
        double segundos = ultimaAtualizacaoNanos == 0 ? 1.0 / 60
                : Math.max(0, Math.min(0.1, (agora - ultimaAtualizacaoNanos) / 1_000_000_000.0));
        ultimaAtualizacaoNanos = agora;
        int movimentoY = atualizarMovimento(jogador, cenaDoJogo.getCenario(), teclado, segundos);
        trocarCenarioAoSairPelasLaterais(cenaDoJogo);
        trocarCenarioAoDescer(cenaDoJogo, movimentoY);
    }

    public void suspender(player jogador, escutadorTeclado teclado) {
        dashPressionado = teclado.dash;
        ultimaAtualizacaoNanos = 0;
        jogador.pararCorrida();
    }

    public int atualizarMovimento(player jogador, tileMap cenario,
            escutadorTeclado teclado, double segundos) {
        boolean novoDash = teclado.dash && !dashPressionado;
        dashPressionado = teclado.dash;
        if (novoDash) {
            jogador.iniciarDash(
                    teclado.movePraEsq,
                    teclado.movePraCima,
                    teclado.movePraDir,
                    teclado.movePraBaixo
            );
        }

        int antesY = jogador.y;
        boolean emDash = jogador.atualizarDash(cenario, this.verificadorDeColisao);
        boolean temDirecao = teclado.movePraEsq != teclado.movePraDir
                || teclado.movePraCima != teclado.movePraBaixo;
        jogador.atualizarCorrida(teclado.dash && temDirecao && !emDash, segundos);
        if (emDash) {
            return Integer.signum(jogador.y - antesY);
        }

        int passo = jogador.getPassoMovimento();
        int movimentoX = obterMovimentoEixo(teclado.movePraEsq, teclado.movePraDir, passo);
        int movimentoY = obterMovimentoEixo(teclado.movePraCima, teclado.movePraBaixo, passo);

        if (movimentoX != 0 && movimentoY != 0) {
            int passoDiagonal = Math.max(1, (int) Math.round(passo / Math.sqrt(2)));
            movimentoX = Integer.signum(movimentoX) * passoDiagonal;
            movimentoY = Integer.signum(movimentoY) * passoDiagonal;
        }

        moverEixoSePossivel(jogador, cenario, movimentoX, 0);
        moverEixoSePossivel(jogador, cenario, 0, movimentoY);
        return movimentoY;
    }

    private int obterMovimentoEixo(boolean negativo, boolean positivo, int passo) {
        int movimento = 0;

        if (negativo) {
            movimento -= passo;
        }
        if (positivo) {
            movimento += passo;
        }

        return movimento;
    }

    private void moverEixoSePossivel(player jogador, tileMap cenario, int movimentoX, int movimentoY) {
        if (movimentoX == 0 && movimentoY == 0) {
            return;
        }

        boolean bateu = this.verificadorDeColisao.ocorreuColisao(
                jogador,
                cenario,
                movimentoX,
                movimentoY
        );

        if (!bateu) {
            jogador.mover(movimentoX, movimentoY);
        }
    }

    private void trocarCenarioAoSairPelasLaterais(panel cenaDoJogo) {
        int larguraMapa = cenaDoJogo.getCenario().getLarguraTotal();
        int jogadorX = cenaDoJogo.getJogador().x;
        int jogadorW = cenaDoJogo.getJogador().width;

        if (jogadorX + jogadorW < 0) {

            if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 5) {
                cenaDoJogo.irParaCenario(
                        6,
                        1475,
                        cenaDoJogo.getJogador().y
                );
            } else if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 8) {
                cenaDoJogo.irParaCenario(
                        5,
                        1440,
                        cenaDoJogo.getJogador().y
                );
            } else {
                cenaDoJogo.irParaCenarioAnterior();
            }

            if (cenaDoJogo.getHistoria() != null) {
                cenaDoJogo.getHistoria().eventoEntrouCenario(
                        cenaDoJogo.getCenario().getCenarioAtualIndex(),
                        cenaDoJogo
                );
            }
        } else if (jogadorX > larguraMapa) {
            if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 6) {
                cenaDoJogo.irParaCenario(5,
                        larguraMapa - jogadorW - 1500,
                        cenaDoJogo.getJogador().y);
            } else if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 5) {
                cenaDoJogo.irParaCenario(8,
                        60,
                        cenaDoJogo.getJogador().y);
            } else {
                cenaDoJogo.irParaProximoCenario();
                if (cenaDoJogo.getHistoria() != null) {
                    cenaDoJogo.getHistoria().eventoEntrouCenario(
                            cenaDoJogo.getCenario().getCenarioAtualIndex(),
                            cenaDoJogo
                    );
                }
            }
        }
    }

    private void trocarCenarioAoDescer(panel cenaDoJogo, int movimentoY) {
        if (movimentoY <= 0 || !jogadorEstaNaSaidaInferior(cenaDoJogo)) {
            return;
        }

        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();

        if (cenarioAtual == CENARIO_6) {
            cenaDoJogo.irParaCenario(
                    CENARIO_5,
                    JOGADOR_X_ENTRADA_SUPERIOR,
                    JOGADOR_Y_CENARIO_5
            );
            avisarEntradaNoCenario(cenaDoJogo);
        } else if (cenarioAtual == CENARIO_8) {
            cenaDoJogo.irParaCenario(
                    CENARIO_6,
                    JOGADOR_X_ENTRADA_SUPERIOR,
                    JOGADOR_Y_CENARIO_6
            );
            avisarEntradaNoCenario(cenaDoJogo);
        }
    }

    private boolean jogadorEstaNaSaidaInferior(panel cenaDoJogo) {
        player jogador = cenaDoJogo.getJogador();
        int centroJogadorX = jogador.x + jogador.width / 2;
        int inicioSaidaX = COLUNA_ESQUERDA_SAIDA_INFERIOR
                * cenaDoJogo.getCenario().getTamanhoTile();
        int fimSaidaX = (COLUNA_DIREITA_SAIDA_INFERIOR + 1)
                * cenaDoJogo.getCenario().getTamanhoTile();
        // A colisão do limite inferior bloqueia o último passo do jogador.
        // Por isso, a saída deve ser reconhecida quando ele encosta na borda,
        // sem exigir que o sprite ultrapasse o tamanho total do mapa.
        boolean chegouAoFimDoMapa = jogador.y + jogador.height
                >= cenaDoJogo.getCenario().getAlturaTotal() - jogador.getPassoMovimento();

        return chegouAoFimDoMapa
                && centroJogadorX >= inicioSaidaX
                && centroJogadorX < fimSaidaX;
    }

    private void avisarEntradaNoCenario(panel cenaDoJogo) {
        if (cenaDoJogo.getHistoria() != null) {
            cenaDoJogo.getHistoria().eventoEntrouCenario(
                    cenaDoJogo.getCenario().getCenarioAtualIndex(),
                    cenaDoJogo
            );
        }
    }
}
