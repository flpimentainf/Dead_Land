package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.entity.player;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class ControladorItens {
    private static final int CENARIO_DOS_ITENS = 6;
    private static final int TILE_CHAO = 41;
    private static final int CENARIO_CHAVE_VERMELHA = 2;
    private static final int TILE_TERRA = 2;

    private item[] itens;

    public ControladorItens() {
        this.itens = new item[] {
            new item("repos/tiles/tile (46).png", CENARIO_DOS_ITENS, 2, 2, TILE_CHAO, "chave"),
            new item("repos/tiles/tile (47).png", CENARIO_DOS_ITENS, 1, 22, TILE_CHAO, "arco"),
            new item("repos/tiles/tile (48).png", CENARIO_DOS_ITENS, 1, 23, TILE_CHAO, "flecha"),
            new item("repos/tiles/tile (64).png", CENARIO_CHAVE_VERMELHA, 4, 15, TILE_TERRA, "chave_vermelha")
            
        };
    }

    public void desenhar(Graphics2D g2, panel cenaDoJogo) {
        tileMap cenario = cenaDoJogo.getCenario();
        player jogador = cenaDoJogo.getJogador();
        int cenarioAtual = cenario.getCenarioAtualIndex();

        for (item itemDoJogo : this.itens) {
            if (itemDoJogo.foiColetado() || itemDoJogo.getCenarioIndex() != cenarioAtual) continue;

            int distancia = (int) Math.sqrt(
                Math.pow(jogador.x - itemDoJogo.x, 2) + Math.pow(jogador.y - itemDoJogo.y, 2)
            );

            if (distancia < 80) {
                int bx = itemDoJogo.x + itemDoJogo.width / 2 - 12;
                int by = itemDoJogo.y - 22;
                g2.setColor(new Color(30, 150, 30, 180));
                g2.fillRoundRect(bx, by, 60, 18, 6, 6);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(bx, by, 60, 18, 6, 6);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "Coletar";
                g2.drawString(txt, bx + (60 - fm.stringWidth(txt)) / 2, by + 13);
            }
        }
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
                if (cenaDoJogo.getHistoria() != null) {
                    cenaDoJogo.getHistoria().eventoItemColetado(itemDoJogo.getNome());
                }
            }
        }
    }
}