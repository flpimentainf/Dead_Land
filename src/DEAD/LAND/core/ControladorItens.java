package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.entity.player;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;

public class ControladorItens {
    private static final int CENARIO_DOS_ITENS = 6;
    private static final int TILE_CHAO = 41;

    private item[] itens;

    public ControladorItens() {
        this.itens = new item[] {
            new item("repos/tiles/tile (46).png", CENARIO_DOS_ITENS, 2, 2, TILE_CHAO, "chave"),
            new item("repos/tiles/tile (47).png", CENARIO_DOS_ITENS, 1, 22, TILE_CHAO, "arco"),
            new item("repos/tiles/tile (48).png", CENARIO_DOS_ITENS, 1, 23, TILE_CHAO, "flecha")
        };
    }

    public void atualizar(panel cenaDoJogo) {
        tileMap cenario = cenaDoJogo.getCenario();
        player jogador = cenaDoJogo.getJogador();
        int cenarioAtual = cenario.getCenarioAtualIndex();

        for (item itemDoJogo : this.itens) {
            if (itemDoJogo.foiColetado() || itemDoJogo.getCenarioIndex() != cenarioAtual) {
                continue;
            }

            if (itemDoJogo.intersects(jogador)) {
                itemDoJogo.coletar();
                cenario.definirTile(
                        itemDoJogo.getLinha(),
                        itemDoJogo.getColuna(),
                        itemDoJogo.getTileSubstituto()
                );
                cenaDoJogo.getInventario().adicionar(itemDoJogo);
                cenaDoJogo.atualizarInventario();
            }
        }
    }
}