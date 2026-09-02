package DEAD.LAND.core;

import DEAD.LAND.entity.NPC;
import DEAD.LAND.entity.player;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tiles;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ControladorNPCs {
    private List<NPC> npcs = new ArrayList<NPC>();
    private final verificadorDeColisao verificadorColisao = new verificadorDeColisao();

    public ControladorNPCs() {
        adicionarNPC("ari", "Ari", 1, 3, 15, 5, true);
        adicionarNPC("mara", "Mara", 2, 5, 14, 7, true);
        adicionarNPC("guardiao_memoria", "Guardião", 3, 6, 4, 7, false);
        adicionarNPC("porteiro_morto", "Porteiro", 4, 7, 24, 6, false);
        adicionarNPC("sobrevivente_perdido", "Sobrevivente", 5, 5, 6, 7, true);
        adicionarNPC("eco", "Eco", 6, 2, 22, 6, false);
        adicionarNPC("cacador_memorias", "Cacador", 7, 6, 20, 4, true);
        adicionarNPC("alma_esquecida", "Alma", 8, 8, 16, 6, false);
    }

    private void adicionarNPC(String id, String nome, int numeroNpc, int cenarioIndex,
            int coluna, int linha, boolean podeCaminhar) {
        int x = coluna * tiles.LARGURA;
        int y = linha * tiles.ALTURA - 24;
        NPC npc = new NPC(id, nome, numeroNpc, cenarioIndex, x, y);
        npc.setPodeCaminhar(podeCaminhar);
        npcs.add(npc);
    }

    public void atualizar(panel cenaDoJogo) {
        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();
        player jogador = cenaDoJogo.getJogador();

        for (NPC npc : npcs) {
            if (npc.getCenarioIndex() == cenarioAtual && npcEstaVisivel(npc, cenaDoJogo)) {
                npc.atualizar(jogador, cenaDoJogo.getCenario(), verificadorColisao);
            }
        }
    }

    public void desenhar(Graphics2D g2, panel cenaDoJogo) {
        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();
        for (NPC npc : npcs) {
            if (npc.getCenarioIndex() == cenarioAtual && npcEstaVisivel(npc, cenaDoJogo)) {
                npc.desenhar(
                        g2,
                        getSimboloEstado(npc, cenaDoJogo),
                        npcDeveBrilhar(npc, cenaDoJogo)
                );
            }
        }
    }

    public void desenharIndicadorInteracao(Graphics2D g2, panel cenaDoJogo) {
        NPC npc = getNpcPerto(cenaDoJogo);
        if (npc != null) {
            npc.desenharIndicadorInteracao(g2);
        }
    }

    public NPC getNpcPerto(panel cenaDoJogo) {
        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();
        player jogador = cenaDoJogo.getJogador();

        for (NPC npc : npcs) {
            if (npc.getCenarioIndex() == cenarioAtual
                    && npcEstaVisivel(npc, cenaDoJogo)
                    && npc.estaPerto(jogador)) {
                return npc;
            }
        }

        return null;
    }

    private boolean npcEstaVisivel(NPC npc, panel cenaDoJogo) {
        if ("porteiro_morto".equals(npc.getId())) {
            return cenaDoJogo.getControladorInimigos() != null
                    && cenaDoJogo.getControladorInimigos().bossFoiDerrotado();
        }

        return !"guardiao_memoria".equals(npc.getId())
                || !cenaDoJogo.getInventario().temChaveBoss63();
    }

    private String getSimboloEstado(NPC npc, panel cenaDoJogo) {
        if (cenaDoJogo.getHistoria() == null) {
            return "";
        }

        return cenaDoJogo.getHistoria().getSimboloNpc(npc.getId(), cenaDoJogo);
    }

    private boolean npcDeveBrilhar(NPC npc, panel cenaDoJogo) {
        if (npc == null || cenaDoJogo.getHistoria() == null) {
            return false;
        }

        String simbolo = cenaDoJogo.getHistoria().getSimboloNpc(npc.getId(), cenaDoJogo);
        return "porteiro_morto".equals(npc.getId())
                || ("cacador_memorias".equals(npc.getId()) && "!".equals(simbolo));
    }
}
