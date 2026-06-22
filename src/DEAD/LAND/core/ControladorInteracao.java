package DEAD.LAND.core;

import DEAD.LAND.entity.NPC;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;

public class ControladorInteracao {

    private static final int FRAMES_PARA_ACAO = 20;

    private static final int CENARIO_APOS_CAMA = 1;
    private static final int JOGADOR_X_APOS_CAMA = 320;
    private static final int JOGADOR_Y_APOS_CAMA = 200;

    private static final int CENARIO_APOS_PORTA = 5;
    private static final int JOGADOR_X_APOS_PORTA = 725;
    private static final int JOGADOR_Y_APOS_PORTA = 400;

    private int framesSegurandoInteracao;
    private boolean interacaoNpcPressionada;

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        if (!teclado.interagir) {
            this.interacaoNpcPressionada = false;
        }

        NPC npcPerto = cenaDoJogo.getControladorNPCs() != null
                ? cenaDoJogo.getControladorNPCs().getNpcPerto(cenaDoJogo)
                : null;

        if (npcPerto != null && teclado.interagir && !this.interacaoNpcPressionada) {
            this.interacaoNpcPressionada = true;
            this.framesSegurandoInteracao = 0;

            if (cenaDoJogo.getHistoria() != null) {
                cenaDoJogo.getHistoria().eventoFalarComNpc(npcPerto.getId(), cenaDoJogo);
            }
            return;
        }

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

            if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 4) {

                cenaDoJogo.irParaCenario(
                        5,
                        JOGADOR_X_APOS_PORTA,
                        JOGADOR_Y_APOS_PORTA
                );
                return;
            }

           if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 5) {
                if (interacao.area.y == 0) {
                    if (!cenaDoJogo.getInventario().temChave()) {
                        return;
                    }
                } else {
                    if (!cenaDoJogo.getInventario().temChaveVermelha()) {
                        return;
                    }
                }

                cenaDoJogo.irParaCenario(
                        7,
                        752,
                        312
                );
                return;
            }
        }
    }

    public void resetar() {
        this.framesSegurandoInteracao = 0;
    }
}
