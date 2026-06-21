package DEAD.LAND.core;

import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;

public class ControladorMovimento {
    private final verificadorDeColisao verificadorDeColisao;

    public ControladorMovimento() {
        this.verificadorDeColisao = new verificadorDeColisao();
    }

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        player jogador = cenaDoJogo.getJogador();
        int movimentoX = obterMovimentoEixo(teclado.movePraEsq, teclado.movePraDir, jogador.passo);
        int movimentoY = obterMovimentoEixo(teclado.movePraCima, teclado.movePraBaixo, jogador.passo);

        if (movimentoX != 0 && movimentoY != 0) {
            int passoDiagonal = Math.max(1, (int) Math.round(jogador.passo / Math.sqrt(2)));
            movimentoX = Integer.signum(movimentoX) * passoDiagonal;
            movimentoY = Integer.signum(movimentoY) * passoDiagonal;
        }

        moverEixoSePossivel(cenaDoJogo, movimentoX, 0);
        moverEixoSePossivel(cenaDoJogo, 0, movimentoY);

        trocarCenarioAoSairPelasLaterais(cenaDoJogo);
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

    private void moverEixoSePossivel(panel cenaDoJogo, int movimentoX, int movimentoY) {
        if (movimentoX == 0 && movimentoY == 0) {
            return;
        }

        boolean bateu = this.verificadorDeColisao.ocorreuColisao(
                cenaDoJogo.getJogador(),
                cenaDoJogo.getCenario(),
                movimentoX,
                movimentoY
        );

        if (!bateu) {
            cenaDoJogo.getJogador().mover(movimentoX, movimentoY);
        }
    }

    private void trocarCenarioAoSairPelasLaterais(panel cenaDoJogo) {
        int larguraMapa = cenaDoJogo.getCenario().getLarguraTotal();
        int jogadorX = cenaDoJogo.getJogador().x;
        int jogadorW = cenaDoJogo.getJogador().width;

        if (jogadorX + jogadorW < 0) {
            cenaDoJogo.irParaCenarioAnterior();
            if (cenaDoJogo.getHistoria() != null) cenaDoJogo.getHistoria().eventoEntrouCenario(cenaDoJogo.getCenario().getCenarioAtualIndex(), cenaDoJogo);
        	
        	if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 5) {
                cenaDoJogo.irParaCenario(6, 1475, cenaDoJogo.getJogador().y);
            } else {
            	
            	 cenaDoJogo.irParaCenarioAnterior();
            }
            
        } else if (jogadorX > larguraMapa) {

        	if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 6) {
                cenaDoJogo.irParaCenario(5,
                        larguraMapa - jogadorW - 1500,
                        cenaDoJogo.getJogador().y);
        	
        	} else {
            cenaDoJogo.irParaProximoCenario();
            if (cenaDoJogo.getHistoria() != null) cenaDoJogo.getHistoria().eventoEntrouCenario(cenaDoJogo.getCenario().getCenarioAtualIndex(), cenaDoJogo);
        }
    }
}
}
