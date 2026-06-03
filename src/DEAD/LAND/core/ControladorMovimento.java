package DEAD.LAND.core;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;

public class ControladorMovimento {
    private verificadorDeColisao verificadorDeColisao;

    public ControladorMovimento() {
        this.verificadorDeColisao = new verificadorDeColisao();
    }

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        String direcao = obterDirecao(teclado);
        boolean bateu = this.verificadorDeColisao.ocorreuColisao(
                cenaDoJogo.getJogador(),
                cenaDoJogo.getCenario(),
                direcao
        );

        if (!bateu) {
            cenaDoJogo.getJogador().atualizarPosicaoJogador(
                    teclado.movePraEsq,
                    teclado.movePraCima,
                    teclado.movePraDir,
                    teclado.movePraBaixo
            );
        }

        trocarCenarioAoSairPelasLaterais(cenaDoJogo);
    }

    private String obterDirecao(escutadorTeclado teclado) {
        if (teclado.movePraCima) {
            return "cima";
        }
        if (teclado.movePraBaixo) {
            return "baixo";
        }
        if (teclado.movePraDir) {
            return "direita";
        }
        if (teclado.movePraEsq) {
            return "esquerda";
        }

        return "";
    }

    private void trocarCenarioAoSairPelasLaterais(panel cenaDoJogo) {
        int larguraMapa = cenaDoJogo.getCenario().getLarguraTotal();
        int jogadorX = cenaDoJogo.getJogador().x;
        int jogadorW = cenaDoJogo.getJogador().width;

        if (jogadorX + jogadorW < 0) {
            cenaDoJogo.irParaCenarioAnterior();
        } else if (jogadorX > larguraMapa) {
            cenaDoJogo.irParaProximoCenario();
        }
    }
}
