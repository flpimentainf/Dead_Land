package DEAD.LAND.core;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;

public class ControladorInteracao {
    private static final int FRAMES_PARA_ACAO = 20;

    private static final int CENARIO_APOS_CAMA = 1;
    private static final int JOGADOR_X_APOS_CAMA = 320;
    private static final int JOGADOR_Y_APOS_CAMA = 200;

    private static final int CENARIO_APOS_PORTA = 5;
    private static final int JOGADOR_X_APOS_PORTA = 100;
    private static final int JOGADOR_Y_APOS_PORTA = 200;

    private int framesSegurandoInteracao;

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        tileMap.InteracaoPerto interacao = cenaDoJogo.getInteracaoPerto();

        if (interacao != null && teclado.interagir) {
            this.framesSegurandoInteracao++;

            if (this.framesSegurandoInteracao >= FRAMES_PARA_ACAO) {
                executarAcao(cenaDoJogo, interacao);
                this.framesSegurandoInteracao = 0;
            }

            return;
        }

        resetar();
    }

    private void executarAcao(panel cenaDoJogo, tileMap.InteracaoPerto interacao) {
        if (interacao.tipo == tileMap.TIPO_CAMA) {

            if (cenaDoJogo.getHistoria() != null) {
                cenaDoJogo.getHistoria().eventoDormir();
            }

            cenaDoJogo.irParaCenario(
                    CENARIO_APOS_CAMA,
                    JOGADOR_X_APOS_CAMA,
                    JOGADOR_Y_APOS_CAMA
            );

            return;
        }

        if (interacao.tipo == tileMap.TIPO_PORTA) {
            if (cenaDoJogo.getHistoria() != null
                    && cenaDoJogo.getHistoria().eventoPorta(
                            cenaDoJogo.getCenario().getCenarioAtualIndex(),
                            cenaDoJogo
                    )) {
                return;
            }

            cenaDoJogo.irParaCenario(
                    CENARIO_APOS_PORTA,
                    JOGADOR_X_APOS_PORTA,
                    JOGADOR_Y_APOS_PORTA
            );
        }
    }

    public void resetar() {
        this.framesSegurandoInteracao = 0;
    }
}