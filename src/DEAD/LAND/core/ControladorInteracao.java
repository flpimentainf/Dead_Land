package DEAD.LAND.core;

import DEAD.LAND.entity.NPC;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.Portal;
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

            Portal portal = cenaDoJogo.getCenario().getPortalNaArea(interacao.area);
            if (portal != null) {
                if (!podeUsarPortal(cenaDoJogo, portal)) {
                    mostrarPortaTrancada(cenaDoJogo, portal.isPortaDoBoss());
                    AudioManager.getInstancia().tocarEfeito("porta_trancada");
                    return;
                }

                cenaDoJogo.irParaCenario(
                        portal.getDestinoIndex(),
                        portal.getDestinoX(),
                        portal.getDestinoY()
                );
                AudioManager.getInstancia().tocarEfeito("porta");
                cenaDoJogo.salvarAuto();
                avisarEntradaNoCenario(cenaDoJogo);
                return;
            }

            if (cenaDoJogo.getCenario().getCenarioAtualIndex() == 5) {
                if (interacao.area.y != 0) {
                    if (!cenaDoJogo.getInventario().temChave()) {
                        mostrarPortaTrancada(cenaDoJogo, false);
                        AudioManager.getInstancia().tocarEfeito("porta_trancada");
                        return;
                    }
                    else if (cenaDoJogo.getInventario().temChave()) {
                        cenaDoJogo.getCenario().definirTile(6, 22, 61);
                        cenaDoJogo.getCenario().definirTile(6, 23, 61);

                        AudioManager.getInstancia().tocarEfeito("porta");
                        cenaDoJogo.salvarAutoForcado();
                        cenaDoJogo.repaint();
                        return;
                    }
                }
                return;
            }
        }
    }

    private void mostrarPortaTrancada(panel cenaDoJogo, boolean portaDoBoss) {
        if (cenaDoJogo.getHistoria() != null) {
            cenaDoJogo.getHistoria().eventoPortaSemChave(portaDoBoss);
        }
    }

    private boolean podeUsarPortal(panel cenaDoJogo, Portal portal) {
        if (portal.getItemNecessario() == null) {
            return true;
        }

        if (portal.requerItem("chave")) {
            return cenaDoJogo.getInventario().temChave();
        }

        if (portal.requerItem("chave_boss_63")) {
            return cenaDoJogo.getInventario().temChaveBoss63();
        }

        return false;
    }

    private void avisarEntradaNoCenario(panel cenaDoJogo) {
        if (cenaDoJogo.getHistoria() != null) {
            cenaDoJogo.getHistoria().eventoEntrouCenario(
                    cenaDoJogo.getCenario().getCenarioAtualIndex(),
                    cenaDoJogo
            );
        }
    }

    public void resetar() {
        this.framesSegurandoInteracao = 0;
    }
}
