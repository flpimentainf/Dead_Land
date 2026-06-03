package DEAD.LAND.core;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;

public class ControladorInteracaoCama {
    private static final int FRAMES_PARA_DORMIR = 60;
    private static final int CENARIO_APOS_DORMIR = 1;
    private static final int JOGADOR_X_APOS_DORMIR = 320;
    private static final int JOGADOR_Y_APOS_DORMIR = 200;

    private int framesSegurandoInteracao;

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        if (cenaDoJogo.jogadorPertoDaCama() && teclado.interagir) {
            this.framesSegurandoInteracao++;

            if (this.framesSegurandoInteracao >= FRAMES_PARA_DORMIR) {
                cenaDoJogo.irParaCenario(
                        CENARIO_APOS_DORMIR,
                        JOGADOR_X_APOS_DORMIR,
                        JOGADOR_Y_APOS_DORMIR
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
