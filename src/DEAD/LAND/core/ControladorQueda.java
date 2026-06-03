package DEAD.LAND.core;

import DEAD.LAND.entity.player;
import DEAD.LAND.ui.panel;

public class ControladorQueda {
    private static final int CENARIO_DA_QUEDA = 1;
    private static final int JOGADOR_X_APOS_QUEDA = 745;
    private static final int JOGADOR_Y_APOS_QUEDA = 200;

    private boolean quedaSegundoCenarioExecutada;

    public boolean atualizar(panel cenaDoJogo) {
        verificarInicioQuedaSegundoCenario(cenaDoJogo);

        if (!cenaDoJogo.getJogador().estaCaindo()) {
            return false;
        }

        atualizarQuedaJogador(cenaDoJogo);
        return true;
    }

    private void verificarInicioQuedaSegundoCenario(panel cenaDoJogo) {
        if (cenaDoJogo.getCenario().getCenarioAtualIndex() != CENARIO_DA_QUEDA) {
            this.quedaSegundoCenarioExecutada = false;
            return;
        }

        if (this.quedaSegundoCenarioExecutada || cenaDoJogo.getJogador().estaCaindo()) {
            return;
        }

        player jogador = cenaDoJogo.getJogador();
        jogador.x = cenaDoJogo.getCenario().getLarguraTotal() / 2 - jogador.width / 2;
        jogador.y = 0;
        jogador.iniciarQueda();
        this.quedaSegundoCenarioExecutada = true;
    }

    private void atualizarQuedaJogador(panel cenaDoJogo) {
        player jogador = cenaDoJogo.getJogador();
        jogador.atualizarQueda();

        if (jogador.y + jogador.height < cenaDoJogo.getCenario().getAlturaTotal()) {
            return;
        }

        jogador.pararQueda();
        cenaDoJogo.irParaProximoCenario();
        jogador.x = JOGADOR_X_APOS_QUEDA;
        jogador.y = JOGADOR_Y_APOS_QUEDA;
        jogador.atualizarAreaColisao();
    }
}
