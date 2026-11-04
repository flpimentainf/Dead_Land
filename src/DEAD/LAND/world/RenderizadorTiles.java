package DEAD.LAND.world;

import DEAD.LAND.core.ResourceManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/** Novos materiais sobre os IDs existentes, sem alterar a geometria dos mapas. */
public final class RenderizadorTiles {
    public enum Material {
        MADEIRA_CLARA("madeira_clara"), MADEIRA_ESCURA("madeira_escura"),
        PAREDE_PEDRA("parede_pedra"), PAREDE_MUSGO("parede_musgo"),
        PISO_PEDRA("piso_pedra"), PISO_MUSGO("piso_musgo"),
        GRAMA("grama"), FOLHAGEM("folhagem"), TERRA("terra"),
        AGUA("agua"), ABISMO("abismo"), TAPETE("tapete"), OBSIDIANA("obsidiana"),
        TELHADO("telhado"), ARENITO("arenito");

        private final String arquivo;
        Material(String arquivo) { this.arquivo = arquivo; }
        public String getCaminho() { return "repos/tileset/" + arquivo + ".png"; }
    }

    private static final int TAMANHO = tiles.LARGURA;
    private final Image[] materiais = new Image[Material.values().length];
    private final Map<Integer, BufferedImage> cache = new HashMap<>();

    public RenderizadorTiles() {
        for (Material material : Material.values()) {
            materiais[material.ordinal()] = ResourceManager.getInstancia().carregarImagem(material.getCaminho());
        }
    }

    public boolean desenhar(Graphics2D g, int id, int linha, int coluna, CenarioId cenario, boolean objeto) {
        if (!temNovoVisual(id)) return false;
        int variacao = Math.floorMod((coluna * 73856093) ^ (linha * 19349663), 8);
        int chave = (cenario.indice() << 16) | (id << 4) | variacao | (objeto ? 1 << 24 : 0);
        BufferedImage imagem = cache.computeIfAbsent(chave, k -> criarTile(id, cenario, variacao, objeto));
        g.drawImage(imagem, coluna * TAMANHO, linha * TAMANHO, null);
        return true;
    }

    public static boolean temNovoVisual(int id) {
        return id >= 1 && id <= 61 && id != 45 && id != 46 && id != 47;
    }

    private BufferedImage criarTile(int id, CenarioId cenario, int variacao, boolean objeto) {
        BufferedImage tile = new BufferedImage(TAMANHO, TAMANHO, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tile.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            if (id == 37 || id == 38) {
                textura(g, Material.ABISMO, variacao);
                if (id == 37) {
                    g.setColor(new Color(2, 3, 7, 160));
                    g.fillRect(0, 0, TAMANHO, TAMANHO);
                } else if (cenario == CenarioId.QUEDA) {
                    g.setColor(new Color(105, 133, 165, 65));
                    int x = 6 + variacao * 4;
                    g.fillRect(x, 8, 2, 8);
                    g.fillRect(x + 2, 16, 2, 6);
                }
            } else if (id == 36) {
                textura(g, Material.AGUA, variacao);
            } else if (id == 39) {
                textura(g, Material.MADEIRA_CLARA, variacao);
            } else if (id == 41) {
                textura(g, piso(cenario), variacao);
                if (cenario == CenarioId.TRILHA_LATERAL) detalhesVegetacao(g, variacao);
            } else if (id == 61) {
                Material material = switch (cenario) {
                    case REGIAO_PRINCIPAL -> Material.TAPETE;
                    case AREA_MEMORIA -> Material.MADEIRA_CLARA;
                    case TRILHA_LATERAL -> Material.PISO_MUSGO;
                    default -> piso(cenario);
                };
                textura(g, material, variacao);
            } else if (id == 54 || id == 55) {
                textura(g, Material.GRAMA, variacao);
                detalhesVegetacao(g, variacao);
            } else if (id == 56 || (id == 60 && cenario == CenarioId.TRILHA_LATERAL)) {
                if (!objeto) textura(g, Material.GRAMA, variacao);
                desenharArbusto(g, variacao);
            } else if (id <= 10 || id == 53 || (id >= 57 && id <= 59)) {
                desenharTrilha(g, id, variacao);
            } else if (id >= 11 && id <= 18) {
                desenharTelhado(g, id, variacao);
            } else if ((id >= 15 && id <= 22) || id == 27) {
                desenharCasa(g, id, variacao);
            } else if ((id >= 23 && id <= 32)) {
                desenharFachada(g, id, cenario, variacao);
            } else if (id == 43 || id == 44 || id == 51 || id == 52) {
                desenharArco(g, id, cenario, variacao);
            } else if ((id >= 33 && id <= 35) || id == 40 || id == 42 || (id >= 48 && id <= 50)) {
                desenharMuralha(g, id, cenario, variacao);
            } else {
                textura(g, parede(cenario), variacao);
            }
        } finally { g.dispose(); }
        return tile;
    }

    private Material piso(CenarioId cenario) {
        return switch (cenario) {
            case QUARTO -> Material.MADEIRA_CLARA;
            case AREA_MEMORIA -> Material.PISO_MUSGO;
            case ARENA_BOSS -> Material.OBSIDIANA;
            case TRILHA_LATERAL -> Material.TERRA;
            default -> Material.PISO_PEDRA;
        };
    }

    private Material parede(CenarioId cenario) {
        return switch (cenario) {
            case RUINAS, AREA_MEMORIA -> Material.PAREDE_MUSGO;
            case ARENA_BOSS -> Material.OBSIDIANA;
            default -> Material.PAREDE_PEDRA;
        };
    }

    private void textura(Graphics2D g, Material material, int variacao) {
        Image imagem = materiais[material.ordinal()];
        boolean espelharX = (variacao & 1) != 0;
        boolean espelharY = (variacao & 2) != 0 && (material == Material.GRAMA || material == Material.TERRA
                || material == Material.FOLHAGEM || material == Material.PISO_MUSGO);
        g.drawImage(imagem, espelharX ? TAMANHO : 0, espelharY ? TAMANHO : 0,
                espelharX ? -TAMANHO : TAMANHO, espelharY ? -TAMANHO : TAMANHO, null);
        // O piso recebe menos contraste que os obstáculos e os personagens.
        Color tom = switch (material) {
            case GRAMA -> new Color(102, 132, 69, 100);
            case FOLHAGEM -> new Color(12, 42, 25, 55);
            case PISO_MUSGO -> new Color(128, 132, 111, 90);
            case PISO_PEDRA -> new Color(125, 130, 137, 80);
            case MADEIRA_CLARA -> new Color(139, 98, 60, 85);
            case TERRA -> new Color(124, 85, 51, 70);
            default -> null;
        };
        if (tom != null) {
            g.setColor(tom);
            g.fillRect(0, 0, TAMANHO, TAMANHO);
        }
    }

    private void texturaRecortada(Graphics2D g, Material material, Shape forma, int variacao) {
        Shape anterior = g.getClip();
        g.clip(forma);
        textura(g, material, variacao);
        g.setClip(anterior);
    }

    private void desenharTrilha(Graphics2D g, int id, int variacao) {
        textura(g, Material.GRAMA, variacao);
        int esquerda = id == 1 || id == 4 || id == 57 ? 6 : 0;
        int direita = id == 3 || id == 6 || id == 59 ? 42 : 48;
        int cima = id >= 57 && id <= 59 ? 8 : 0;
        int baixo = id == 4 || id == 6 ? 42 : 48;
        texturaRecortada(g, Material.TERRA, new java.awt.Rectangle(esquerda, cima, direita - esquerda, baixo - cima), variacao);
        g.setColor(new Color(53, 80, 37));
        if (esquerda > 0) {
            g.fillRect(4, 4, 4, 8);
            g.fillRect(6, 26, 4, 6);
        }
        if (direita < 48) {
            g.fillRect(40, 12, 4, 6);
            g.fillRect(38, 32, 6, 4);
        }
        if (cima > 0) {
            for (int x = 4; x < 46; x += 10) g.fillRect(x, 6, 4, 4);
        }
    }

    private void detalhesVegetacao(Graphics2D g, int variacao) {
        int x = 6 + (variacao * 7) % 32;
        int y = 8 + (variacao * 11) % 30;
        g.setColor(new Color(83, 112, 46, 180));
        g.fillRect(x, y, 4, 2);
        g.fillRect(x + 2, y - 2, 2, 4);
        if (variacao == 3) {
            g.setColor(new Color(123, 125, 96));
            g.fillRect(30, 30, 6, 4);
            g.setColor(new Color(70, 78, 62));
            g.fillRect(28, 34, 10, 2);
        }
    }

    private void desenharArbusto(Graphics2D g, int variacao) {
        g.setColor(new Color(8, 20, 15, 150));
        g.fillRect(8, 38, 34, 8);
        g.fillRect(14, 44, 22, 4);
        g.setColor(new Color(58, 40, 28));
        g.fillRect(20, 30, 8, 18);
        Polygon folhas = new Polygon(new int[]{12, 36, 36, 42, 42, 46, 46, 42, 42, 34, 34, 14, 14, 6, 6, 2, 2, 8, 8, 12},
                new int[]{2, 2, 6, 6, 12, 12, 32, 32, 38, 38, 42, 42, 38, 38, 32, 32, 14, 14, 6, 6}, 20);
        texturaRecortada(g, Material.FOLHAGEM, folhas, variacao);
        g.setColor(new Color(10, 32, 21, 180));
        g.drawPolygon(folhas);
        g.setColor(new Color(85, 122, 47));
        g.fillRect(12, 10, 6, 2);
        g.fillRect(10, 12, 4, 2);
        g.fillRect(30, 20, 6, 2);
    }

    private void desenharTelhado(Graphics2D g, int id, int variacao) {
        textura(g, Material.GRAMA, variacao);
        Polygon forma;
        if (id == 11) forma = new Polygon(new int[]{30, 48, 48, 0, 0}, new int[]{0, 0, 48, 48, 30}, 5);
        else if (id == 13) forma = new Polygon(new int[]{0, 18, 48, 48, 0}, new int[]{0, 0, 30, 48, 48}, 5);
        else forma = new Polygon(new int[]{0, 48, 48, 0}, new int[]{0, 0, 48, 48}, 4);
        texturaRecortada(g, Material.TELHADO, forma, variacao & 1);
        if (id >= 15) {
            g.setColor(new Color(51, 29, 23));
            g.fillRect(0, 42, 48, 6);
            g.setColor(new Color(130, 73, 41));
            g.fillRect(0, 40, 48, 2);
        }
        if (id == 14) desenharChamine(g);
        if (id == 18) desenharFrontao(g);
    }

    private void desenharChamine(Graphics2D g) {
        g.setColor(new Color(43, 29, 25, 180));
        g.fillRect(16, 8, 26, 36);
        g.setColor(new Color(109, 66, 49));
        g.fillRect(14, 8, 22, 32);
        g.setColor(new Color(161, 107, 76));
        g.fillRect(14, 8, 4, 32);
        g.setColor(new Color(67, 45, 39));
        for (int y = 16; y < 40; y += 8) {
            g.fillRect(18, y, 18, 2);
            g.fillRect(y % 16 == 0 ? 24 : 30, y - 6, 2, 6);
        }
        g.setColor(new Color(49, 39, 37));
        g.fillRect(10, 6, 30, 8);
        g.setColor(new Color(174, 161, 140));
        g.fillRect(10, 4, 30, 4);
        g.setColor(new Color(28, 26, 29));
        g.fillRect(16, 6, 18, 4);
        g.setColor(new Color(102, 98, 99, 130));
        g.fillRect(20, 0, 6, 2);
        g.fillRect(26, 2, 6, 2);
    }

    private void desenharFrontao(Graphics2D g) {
        Polygon frente = new Polygon(new int[]{2, 24, 46, 46, 2}, new int[]{30, 8, 30, 48, 48}, 5);
        texturaRecortada(g, Material.ARENITO, frente, 0);
        g.setColor(new Color(55, 37, 28));
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g.drawPolyline(new int[]{2, 24, 46}, new int[]{30, 8, 30}, 3);
        g.fillRect(22, 16, 4, 32);
        g.fillRect(2, 42, 44, 6);
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(163, 113, 64));
        g.drawPolyline(new int[]{2, 24, 46}, new int[]{27, 5, 27}, 3);
    }

    private void desenharCasa(Graphics2D g, int id, int variacao) {
        textura(g, Material.ARENITO, variacao);
        g.setColor(new Color(49, 36, 29, 190));
        g.fillRect(0, 0, 48, 4);
        if (id == 19) g.fillRect(0, 0, 6, 48);
        if (id == 22) g.fillRect(42, 0, 6, 48);
        if (id >= 19) {
            g.fillRect(0, 42, 48, 6);
            g.setColor(new Color(129, 91, 55));
            g.fillRect(0, 40, 48, 2);
        }
        if (id == 21 || id == 27) porta(g, id == 21);
    }

    private void janela(Graphics2D g, int x, int y, int largura, int altura) {
        g.setColor(new Color(41, 31, 28));
        g.fillRect(x - 4, y - 4, largura + 8, altura + 8);
        g.setColor(new Color(134, 94, 51));
        g.fillRect(x - 2, y - 2, largura + 4, altura + 4);
        g.setColor(new Color(44, 76, 91));
        g.fillRect(x, y, largura, altura);
        g.setColor(new Color(100, 142, 152));
        g.fillRect(x + 2, y + 2, Math.max(2, largura / 2 - 2), 4);
        g.setColor(new Color(83, 61, 39));
        g.fillRect(x + largura / 2 - 1, y, 2, altura);
        g.fillRect(x, y + altura / 2, largura, 2);
    }

    private void porta(Graphics2D g, boolean aberta) {
        g.setColor(new Color(21, 20, 24));
        g.fillRect(6, 4, 36, 44);
        if (!aberta) {
            texturaRecortada(g, Material.MADEIRA_ESCURA, new java.awt.Rectangle(10, 8, 28, 40), 0);
            g.setColor(new Color(145, 105, 55));
            g.drawRect(12, 10, 24, 34);
            g.setColor(new Color(216, 169, 76));
            g.fillRect(30, 28, 4, 4);
        }
    }

    private void desenharFachada(Graphics2D g, int id, CenarioId cenario, int variacao) {
        textura(g, parede(cenario), variacao);
        g.setColor(new Color(28, 34, 42, 180));
        g.fillRect(0, 42, 48, 6);
        if (id == 31 || id == 32) {
            desenharPortaDupla(g, id);
        } else if (id == 25 || id == 30) {
            porta(g, id == 25);
        } else if (id == 28 || id == 29) {
            janela(g, 12, 10, 24, 24);
        }
    }

    private void desenharPortaDupla(Graphics2D g, int id) {
        Graphics2D porta = (Graphics2D) g.create();
        try {
            // Os dois tiles recortam a mesma porta de 96 px, com moldura contínua.
            porta.translate(id == 32 ? -TAMANHO : 0, 0);
            porta.setColor(new Color(23, 19, 20));
            porta.fillRect(4, 2, 88, 46);
            porta.setColor(new Color(86, 58, 35));
            porta.fillRect(6, 4, 84, 4);
            porta.fillRect(6, 8, 4, 40);
            porta.fillRect(86, 8, 4, 40);
            Image madeira = materiais[Material.MADEIRA_ESCURA.ordinal()];
            porta.drawImage(madeira, 10, 8, 38, 40, null);
            porta.drawImage(madeira, 86, 8, -38, 40, null);
            porta.setColor(new Color(50, 32, 24));
            porta.drawRect(14, 12, 28, 30);
            porta.drawRect(53, 12, 28, 30);
            porta.setColor(new Color(159, 114, 64));
            porta.drawRect(16, 14, 24, 26);
            porta.drawRect(55, 14, 24, 26);
            porta.fillRect(8, 4, 80, 2);
            porta.setColor(new Color(27, 21, 21));
            porta.fillRect(47, 8, 2, 40);
            porta.setColor(new Color(70, 49, 31));
            porta.fillRect(42, 26, 4, 8);
            porta.fillRect(50, 26, 4, 8);
            porta.setColor(new Color(221, 177, 83));
            porta.fillRect(42, 28, 4, 4);
            porta.fillRect(50, 28, 4, 4);
        } finally { porta.dispose(); }
    }

    private void desenharMuralha(Graphics2D g, int id, CenarioId cenario, int variacao) {
        textura(g, parede(cenario), variacao);
        Color borda = cenario == CenarioId.ARENA_BOSS ? new Color(116, 95, 93) : new Color(137, 145, 146);
        g.setColor(new Color(18, 20, 28));
        if (id == 33 || id == 34 || id == 35) g.fillRect(0, 40, 48, 8);
        if (id == 48 || id == 49 || id == 50) g.fillRect(0, 0, 48, 8);
        if (id == 40 || id == 33 || id == 48) g.fillRect(40, 0, 8, 48);
        if (id == 42 || id == 35 || id == 50) g.fillRect(0, 0, 8, 48);
        g.setColor(borda);
        if (id == 33 || id == 34 || id == 35) g.fillRect(0, 40, 48, 2);
        if (id == 48 || id == 49 || id == 50) g.fillRect(0, 6, 48, 2);
        if (id == 40 || id == 33 || id == 48) g.fillRect(40, 0, 2, 48);
        if (id == 42 || id == 35 || id == 50) g.fillRect(6, 0, 2, 48);
    }

    private void desenharArco(Graphics2D g, int id, CenarioId cenario, int variacao) {
        textura(g, parede(cenario), variacao);
        Graphics2D arco = (Graphics2D) g.create();
        try {
            arco.translate(id == 44 || id == 52 ? -48 : 0, id == 51 || id == 52 ? -48 : 0);
            Polygon abertura = new Polygon(new int[]{16, 16, 24, 24, 32, 32, 64, 64, 72, 72, 80, 80},
                    new int[]{96, 40, 40, 32, 32, 24, 24, 32, 32, 40, 40, 96}, 12);
            arco.setColor(new Color(18, 24, 28));
            arco.setStroke(new BasicStroke(16, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            arco.drawPolygon(abertura);
            arco.setColor(new Color(116, 129, 124));
            arco.setStroke(new BasicStroke(10, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            arco.drawPolygon(abertura);
            arco.setColor(new Color(8, 13, 23));
            arco.fillPolygon(abertura);
            arco.setColor(new Color(58, 91, 53));
            arco.fillRect(18, 32, 8, 4);
            arco.fillRect(64, 20, 8, 4);
            arco.fillRect(76, 46, 4, 10);
        } finally { arco.dispose(); }
    }
}
