package DEAD.LAND.core;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;

public class ControladorInteracao {
    private static final int FRAMES_PARA_ACAO = 60;
    private static final int CENARIO_APOS_ACAO = 1;
    private static final int JOGADOR_X_APOS_ACAO = 320;
    private static final int JOGADOR_Y_APOS_ACAO = 200;

    private int framesSegurandoInteracao;

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        if (cenaDoJogo.jogadorPertoDaCama() && teclado.interagir) {
            this.framesSegurandoInteracao++;

            if (this.framesSegurandoInteracao >= FRAMES_PARA_ACAO) {
                cenaDoJogo.irParaCenario(
                        CENARIO_APOS_ACAO,
                        JOGADOR_X_APOS_ACAO,
                        JOGADOR_Y_APOS_ACAO
                );
                this.framesSegurandoInteracao = 0;
            }

            return;
        }

        resetar();
    }

    public void resetar() {
        this.framesSegurandoInteracao = 0;
    }
}
