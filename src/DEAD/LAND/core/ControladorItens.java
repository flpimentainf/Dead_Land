package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.entity.item.Categoria;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ControladorItens {
    private static final int CENARIO_DOS_ITENS = 6;
    private static final int TILE_CHAO = 41;
    private static final int CENARIO_CHAVE_BOSS = 5;
    private static final int TILE_TERRA = 5;

    private List<item> itens;
    private boolean teclaColetarPressionada;

    public ControladorItens() {
        this.itens = new ArrayList<item>();
        this.itens.add(new item("repos/tiles/tile (46).png", CENARIO_DOS_ITENS, 2, 2, TILE_CHAO, "chave"));
        this.itens.add(new item("repos/tiles/tile (47).png", CENARIO_DOS_ITENS, 1, 22, TILE_CHAO, "arco"));
        this.itens.add(new item("repos/tiles/tile (48).png", CENARIO_DOS_ITENS, 1, 23, TILE_CHAO, "flecha"));
        this.itens.add(new item("repos/tiles/tile (64).png", CENARIO_CHAVE_BOSS, 1, 26, TILE_TERRA, "chave_boss_63"));
        this.itens.add(new item("repos/tiles/tile (63).png", 5, 480, 144,
                "fragmento_cura", Categoria.CONSUMIVEL, 1, true));
        this.itens.add(new item("repos/tiles/tile (64).png", 2, 600, 240,
                "memoria_01", Categoria.ITEM_HISTORIA, 1, false));
        this.itens.add(new item("repos/tiles/tile (64).png", 3, 210, 245,
                "memoria_02", Categoria.ITEM_HISTORIA, 1, false));
        this.itens.add(new item("repos/tiles/tile (64).png", 5, 1080, 340,
                "memoria_03", Categoria.ITEM_HISTORIA, 1, false));
        this.itens.add(new item("repos/tiles/tile (64).png", 6, 720, 250,
                "memoria_04", Categoria.ITEM_HISTORIA, 1, false));
        this.itens.add(new item("repos/tiles/tile (64).png", 7, 520, 305,
                "memoria_05", Categoria.ITEM_HISTORIA, 1, false));
    }

    public void desenhar(Graphics2D g2, panel cenaDoJogo) {
        tileMap cenario = cenaDoJogo.getCenario();
        player jogador = cenaDoJogo.getJogador();
        int cenarioAtual = cenario.getCenarioAtualIndex();

        for (item itemDoJogo : this.itens) {
            if (itemDoJogo.foiColetado() || itemDoJogo.getCenarioIndex() != cenarioAtual) continue;

            g2.drawImage(
                    itemDoJogo.getSprite(),
                    itemDoJogo.x,
                    itemDoJogo.y,
                    itemDoJogo.width,
                    itemDoJogo.height,
                    null
            );

            int distancia = (int) Math.sqrt(
                Math.pow(jogador.x - itemDoJogo.x, 2) + Math.pow(jogador.y - itemDoJogo.y, 2)
            );

            if (distancia < 80) {
                int bx = itemDoJogo.x + itemDoJogo.width / 2 - 32;
                int by = itemDoJogo.y - 22;
                g2.setColor(new Color(30, 150, 30, 180));
                g2.fillRoundRect(bx, by, 82, 18, 6, 6);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(bx, by, 82, 18, 6, 6);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "E - Coletar";
                g2.drawString(txt, bx + (82 - fm.stringWidth(txt)) / 2, by + 13);
            }
        }
    }

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        if (!teclado.interagir) {
            this.teclaColetarPressionada = false;
            return;
        }

        if (this.teclaColetarPressionada) {
            return;
        }

        tileMap cenario = cenaDoJogo.getCenario();
        player jogador = cenaDoJogo.getJogador();
        int cenarioAtual = cenario.getCenarioAtualIndex();

        for (item itemDoJogo : this.itens) {
            if (itemDoJogo.foiColetado() || itemDoJogo.getCenarioIndex() != cenarioAtual) {
                continue;
            }

            if (itemDoJogo.intersects(jogador)) {
                this.teclaColetarPressionada = true;
                itemDoJogo.coletar();
                if (itemDoJogo.getTileSubstituto() != -1) {
                    cenario.removerObjeto(
                            itemDoJogo.getLinha(),
                            itemDoJogo.getColuna()
                    );
                }
                processarColeta(cenaDoJogo, itemDoJogo);
                cenaDoJogo.atualizarInventario();
                if (cenaDoJogo.getHistoria() != null) {
                    cenaDoJogo.getHistoria().eventoItemColetado(itemDoJogo.getNome());
                }
                AudioManager.getInstancia().tocarEfeito("item_coletado");
                salvarColetaSegura(cenaDoJogo, itemDoJogo);
                return;
            }
        }
    }

    private void processarColeta(panel cenaDoJogo, item itemDoJogo) {
        String nome = itemDoJogo.getNome();
        if (nome != null && nome.startsWith("memoria_")) {
            if (cenaDoJogo.getMemoryManager() != null
                    && cenaDoJogo.getMemoryManager().coletar(nome)) {
                if (cenaDoJogo.getHistoria() != null) {
                    cenaDoJogo.getHistoria().eventoMemoriaColetada(
                            cenaDoJogo.getMemoryManager().getMemoria(nome),
                            cenaDoJogo.getMemoryManager().getTotal(),
                            cenaDoJogo.getMemoryManager().getQuantidadeDescoberta()
                    );
                }
                if (cenaDoJogo.getSistemaVontade() != null) {
                    cenaDoJogo.getSistemaVontade().recuperar(18);
                }
                if (cenaDoJogo.getFeedback() != null) {
                    cenaDoJogo.getFeedback().brilhoItem(itemDoJogo.x + 24, itemDoJogo.y + 24);
                    cenaDoJogo.getFeedback().mostrarMensagemCentro("Fragmento de Memoria encontrado");
                }
            }
            return;
        }

        cenaDoJogo.getInventario().adicionar(itemDoJogo);
        if (cenaDoJogo.getFeedback() != null) {
            cenaDoJogo.getFeedback().brilhoItem(itemDoJogo.x + 24, itemDoJogo.y + 24);
            cenaDoJogo.getFeedback().mostrarMensagemCentro(itemDoJogo.getNomeExibicao());
        }
    }

    private void salvarColetaSegura(panel cenaDoJogo, item itemDoJogo) {
        if (itemDoJogo.getCategoria() == Categoria.MUNICAO) {
            cenaDoJogo.salvarAuto();
            return;
        }

        cenaDoJogo.salvarAutoForcado();
    }

    public void criarDrop(String nome, int cenarioIndex, int x, int y, int quantidade) {
        Categoria categoria = item.categoriaPadrao(nome);
        boolean empilhavel = item.ehEmpilhavel(nome);
        String sprite = "flecha".equals(nome)
                ? "repos/tiles/tile (48).png"
                : "repos/tiles/tile (63).png";
        this.itens.add(new item(sprite, cenarioIndex, x, y, nome, categoria, quantidade, empilhavel));
    }

    public List<item> getItensPorNomes(Set<String> nomes) {
        List<item> encontrados = new ArrayList<item>();
        if (nomes == null) {
            return encontrados;
        }

        for (item itemDoJogo : this.itens) {
            if (nomes.contains(itemDoJogo.getNome())) {
                encontrados.add(itemDoJogo);
            }
        }

        return encontrados;
    }

    public void restaurarColetas(Set<String> nomesColetados, tileMap cenario) {
        if (nomesColetados == null) {
            return;
        }

        for (item itemDoJogo : this.itens) {
            if (nomesColetados.contains(itemDoJogo.getNome())) {
                itemDoJogo.coletar();
                if (cenario != null && itemDoJogo.getTileSubstituto() != -1) {
                    cenario.removerObjeto(
                            itemDoJogo.getCenarioIndex(),
                            itemDoJogo.getLinha(),
                            itemDoJogo.getColuna()
                    );
                }
            }
        }
    }
}
