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

    public ControladorNPCs() {
        // NPCs pensados para o roteiro atual de Dead Land.
        // cenarioIndex segue a ordem do tileMap: 0 quarto, 1 queda, 2+, floresta/áreas da memória/final.
        adicionarNPC("ari", "Ari, o que lembra", 1, 3, 15, 5);
        adicionarNPC("mara", "Mara, a escutadora", 2, 5, 14, 7);
        adicionarNPC("guardiao_memoria", "Guardião da memória", 3, 6, 4, 7);
        adicionarNPC("porteiro_morto", "Porteiro morto", 4, 7, 24, 6);
    }

    private void adicionarNPC(String id, String nome, int numeroNpc, int cenarioIndex, int coluna, int linha) {
        int x = coluna * tiles.LARGURA;
        int y = linha * tiles.ALTURA - 24; // alinha o pé do NPC com o chão do tile.
        npcs.add(new NPC(id, nome, numeroNpc, cenarioIndex, x, y));
    }

    public void atualizar(panel cenaDoJogo) {
        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();
        player jogador = cenaDoJogo.getJogador();

        for (NPC npc : npcs) {
            if (npc.getCenarioIndex() == cenarioAtual) {
                npc.atualizar(jogador);
            }
        }
    }

    public void desenhar(Graphics2D g2, int cenarioAtual) {
        for (NPC npc : npcs) {
            if (npc.getCenarioIndex() == cenarioAtual) {
                npc.desenhar(g2);
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
            if (npc.getCenarioIndex() == cenarioAtual && npc.estaPerto(jogador)) {
                return npc;
            }
        }

        return null;
    }
}
