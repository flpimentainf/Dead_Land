package DEAD.LAND.world;

import DEAD.LAND.core.ResourceManager;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** Fachadas montadas como objetos inteiros, sobre o terreno contínuo da vila. */
public final class RenderizadorVila {
    private BufferedImage terreno;
    private final Map<MapaVila.Construcao, BufferedImage> fachadas = new HashMap<>();
    private final RenderizadorArvores arvores = new RenderizadorArvores();
    private final Image terra = ResourceManager.getInstancia().carregarImagem("repos/tileset/terra.png");

    public void desenhar(Graphics2D g, int[][] objetos) {
        if (terreno == null) terreno = criarTerreno();
        g.drawImage(terreno, 0, 0, 2400, 1200, null);
        for (MapaVila.Construcao casa : MapaVila.CONSTRUCOES) {
            if (objetos[casa.linha()][casa.coluna()] != MapaVila.CONSTRUCAO) continue;
            BufferedImage imagem = fachadas.computeIfAbsent(casa, this::criarFachada);
            Rectangle area = casa.area();
            g.drawImage(imagem, area.x - 12, area.y, area.width + 24, area.height + 24, null);
        }
        desenharFonte(g);
        for (int l = 0; l < objetos.length; l++) {
            for (int c = 0; c < objetos[l].length; c++) {
                if (objetos[l][c] == MapaVila.ARVORE) {
                    arvores.desenhar(g, c * 48, l * 48, c * 7 + l * 3,
                            l > 0 && l < 24 && c > 0 && c < 49);
                }
            }
        }
        desenharBarricada(g);
        placa(g, 46 * 48, 11 * 48, "CASTELO  >", false);
        placa(g, 2 * 48, 5 * 48, "VILA DE ARI", false);
    }

    private BufferedImage criarTerreno() {
        BufferedImage imagem = new BufferedImage(1200, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.scale(0.5, 0.5);
            RenderizadorTiles tiles = new RenderizadorTiles();
            for (int l = 0; l < 25; l++) for (int c = 0; c < 50; c++) {
                tiles.desenhar(g, 55, l, c, CenarioId.BOSQUE_ARI, false);
            }
            Shape caminhos = MapaVila.caminhos();
            g.setColor(new Color(56, 63, 37));
            g.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(caminhos);
            g.setClip(caminhos);
            for (int l = 0; l < 25; l++) for (int c = 0; c < 50; c++) {
                g.drawImage(terra, c * 48, l * 48, 48, 48, null);
            }
            g.setColor(new Color(147, 113, 74, 105)); g.fillRect(0, 0, 2400, 1200);
            Random rng = new Random(1841);
            for (int i = 0; i < 10000; i++) {
                int x = rng.nextInt(1200) * 2, y = rng.nextInt(600) * 2;
                g.setColor(i % 3 == 0 ? new Color(194, 163, 111, 95) : new Color(65, 54, 40, 55));
                g.fillRect(x, y, i % 5 == 0 ? 6 : 2, 2);
            }
            g.setClip(null);
            // Pedras pequenas e tufos espaçados quebram o recorte perfeito das estradas.
            for (int i = 0; i < 900; i++) {
                int x = rng.nextInt(1200) * 2, y = rng.nextInt(600) * 2;
                if (caminhos.contains(x, y) || !caminhos.intersects(x - 8, y - 8, 16, 16)) continue;
                g.setColor(new Color(87, 104, 51)); g.fillRect(x, y, 4, 4);
                g.setColor(new Color(45, 67, 34)); g.fillRect(x + 2, y - 2, 2, 4);
            }
            g.setColor(new Color(91, 100, 86));
            g.setStroke(new BasicStroke(22));
            g.draw(new Ellipse2D.Double(20.5 * 48, 10.5 * 48, 5 * 48, 5 * 48));
            g.setColor(new Color(164, 158, 128));
            g.setStroke(new BasicStroke(16));
            g.draw(new Ellipse2D.Double(20.5 * 48, 10.5 * 48, 5 * 48, 5 * 48));
            g.setColor(new Color(93, 98, 82)); g.setStroke(new BasicStroke(2));
            for (int i = 0; i < 28; i++) {
                double angulo = i * Math.PI * 2 / 28;
                g.drawLine((int) (23 * 48 + Math.cos(angulo) * 111), (int) (13 * 48 + Math.sin(angulo) * 111),
                        (int) (23 * 48 + Math.cos(angulo) * 130), (int) (13 * 48 + Math.sin(angulo) * 130));
            }
        } finally { g.dispose(); }
        return imagem;
    }

    private BufferedImage criarFachada(MapaVila.Construcao casa) {
        int w = casa.largura() * 48, h = casa.altura() * 48;
        BufferedImage imagem = new BufferedImage((w + 24) / 2, (h + 24) / 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.scale(0.5, 0.5); g.translate(12, 0);
            int estilo = casa.estilo();
            boolean guilda = casa.tipo() == MapaVila.Tipo.GUILDA, loja = casa.tipo() == MapaVila.Tipo.LOJA;
            int teto = guilda ? h * 54 / 100 : h * 48 / 100;
            Color[] telhas = {new Color(125, 55, 42), new Color(104, 60, 44), new Color(56, 80, 86),
                    new Color(73, 88, 66), new Color(115, 63, 48), new Color(68, 86, 103), new Color(101, 78, 47)};
            g.setColor(new Color(8, 14, 14, 105)); g.fillRect(20, teto, w - 20, h - teto + 14);
            g.fillRect(32, h + 14, w - 36, 6);
            Color parede = estilo == 3 || guilda ? new Color(155, 148, 124) : new Color(192, 168, 127);
            g.setColor(new Color(46, 43, 37)); g.fillRect(12, teto - 10, w - 24, h - teto + 10);
            g.setColor(parede); g.fillRect(18, teto, w - 36, h - teto - 12);
            if (estilo == 3 || guilda) {
                for (int y = teto + 4; y < h - 10; y += 14) {
                    g.setColor(new Color(103, 105, 90)); g.fillRect(18, y, w - 36, 2);
                    for (int x = 18 + (y / 14 % 2) * 15; x < w - 18; x += 30) g.fillRect(x, y, 2, 14);
                }
            } else {
                g.setColor(new Color(84, 61, 43));
                for (int x = 20; x < w - 10; x += 64) g.fillRect(x, teto, 8, h - teto - 10);
                g.fillRect(18, teto + 18, w - 36, 8);
                g.fillRect(18, h - 22, w - 36, 8);
            }
            g.setColor(new Color(74, 79, 72)); g.fillRect(12, h - 12, w - 24, 12);
            g.setColor(new Color(124, 125, 105)); g.fillRect(16, h - 12, w - 32, 3);
            Polygon telhado = new Polygon(new int[]{0, w / 7, w - w / 7, w, w - 4, 4},
                    new int[]{teto - 8, 4, 4, teto - 8, teto + 4, teto + 4}, 6);
            telhado(g, telhado, telhas[estilo], w, teto);
            if (guilda) {
                int cx = w / 2;
                g.setColor(new Color(50, 49, 43));
                g.fillPolygon(new Polygon(new int[]{cx - 108, cx, cx + 108}, new int[]{teto + 18, 12, teto + 18}, 3));
                g.setColor(new Color(178, 170, 138));
                g.fillPolygon(new Polygon(new int[]{cx - 86, cx, cx + 86}, new int[]{teto + 8, 40, teto + 8}, 3));
                g.setColor(new Color(74, 59, 43)); g.fillRect(cx - 4, 51, 8, teto - 44);
                g.setColor(new Color(29, 46, 50));
                g.fillPolygon(new Polygon(new int[]{cx - 27, cx + 27, cx + 24, cx, cx - 24},
                        new int[]{teto - 89, teto - 89, teto - 47, teto - 27, teto - 47}, 5));
                g.setColor(new Color(210, 173, 83)); g.setStroke(new BasicStroke(4));
                g.drawLine(cx - 13, teto - 76, cx + 14, teto - 43);
                g.drawLine(cx + 13, teto - 76, cx - 14, teto - 43);
            } else if (estilo == 0 || estilo == 5) {
                int dx = w * 2 / 3;
                g.setColor(new Color(49, 45, 37));
                g.fillPolygon(new Polygon(new int[]{dx - 32, dx, dx + 32}, new int[]{teto - 18, teto - 68, teto - 18}, 3));
                g.setColor(parede); g.fillRect(dx - 24, teto - 26, 48, 34);
                janela(g, dx - 12, teto - 22, 24, 26, false);
            }
            // Chaminé individual, com topo e argamassa legíveis.
            int ch = w - 70;
            g.setColor(new Color(63, 61, 53)); g.fillRect(ch, 4, 30, 48);
            g.setColor(new Color(129, 119, 96)); g.fillRect(ch + 4, 4, 22, 44);
            g.setColor(new Color(42, 42, 38)); g.fillRect(ch - 4, 0, 38, 8);
            g.setColor(new Color(184, 172, 139)); g.fillRect(ch - 4, 0, 38, 3);
            int portaW = guilda ? 72 : 40, portaH = Math.min(guilda ? 88 : 66, h - teto - 14);
            porta(g, w / 2 - portaW / 2, h - portaH - 8, portaW, portaH, guilda);
            int janelaY = teto + 34, janelaH = Math.min(44, h - janelaY - 30);
            if (janelaH >= 20) {
                janela(g, 42, janelaY, loja ? 96 : 44, janelaH, !guilda);
                janela(g, w - (loja ? 138 : 86), janelaY, loja ? 96 : 44, janelaH, !guilda);
            }
            if (guilda) {
                bandeira(g, 133, teto + 28); bandeira(g, w - 157, teto + 28);
                letreiro(g, w / 2, teto + 21, "GUILDA DOS ERRANTES", 18);
            } else if (loja) {
                int ay = teto + 12;
                for (int x = 24; x < w - 24; x += 24) {
                    g.setColor((x / 24) % 2 == 0 ? new Color(146, 64, 47) : new Color(218, 199, 151));
                    g.fillRect(x, ay, 24, 27); g.fillRect(x + 2, ay + 27, 20, 7);
                }
                letreiro(g, w / 2, teto - 7, "ARMAZÉM", 20);
            }
            vaso(g, 24, h - 15, estilo); vaso(g, w - 40, h - 15, estilo + 1);
            if (guilda) { lanterna(g, w / 2 - 63, h - 82); lanterna(g, w / 2 + 56, h - 82); }
        } finally { g.dispose(); }
        return imagem;
    }

    private void telhado(Graphics2D g, Polygon forma, Color cor, int w, int h) {
        g.setColor(new Color(38, 34, 29)); g.fillPolygon(forma);
        Shape anterior = g.getClip(); g.clip(forma);
        g.setColor(cor); g.fillRect(2, 8, w - 4, h - 10);
        for (int y = 10; y < h; y += 12) {
            g.setColor(cor.darker()); g.fillRect(0, y + 9, w, 3);
            g.setColor(cor.brighter()); g.fillRect(0, y, w, 2);
            for (int x = (y / 12 % 2) * 13; x < w; x += 26) {
                g.setColor(cor.darker()); g.fillRect(x, y + 2, 2, 7);
            }
        }
        g.setClip(anterior); g.setColor(new Color(40, 34, 29));
        g.setStroke(new BasicStroke(5)); g.drawPolygon(forma);
        g.setColor(new Color(176, 143, 97)); g.fillRect(w / 7, 6, w - 2 * (w / 7), 3);
        g.setColor(new Color(58, 40, 29)); g.fillRect(4, h - 2, w - 8, 8);
    }

    private void janela(Graphics2D g, int x, int y, int w, int h, boolean persiana) {
        g.setColor(new Color(60, 44, 32)); g.fillRect(x - 5, y - 5, w + 10, h + 10);
        g.setColor(new Color(45, 66, 68)); g.fillRect(x, y, w, h);
        g.setColor(new Color(186, 162, 102)); g.fillRect(x + 3, y + 3, w / 2 - 6, h - 6);
        g.setColor(new Color(232, 207, 140)); g.fillRect(x + w / 2 + 2, y + 3, w / 2 - 5, h / 2 - 4);
        g.setColor(new Color(80, 60, 39)); g.fillRect(x + w / 2 - 2, y, 4, h);
        g.fillRect(x, y + h / 2 - 2, w, 4);
        g.setColor(new Color(156, 137, 98)); g.fillRect(x - 7, y + h + 3, w + 14, 4);
        if (persiana) {
            g.setColor(new Color(62, 77, 62)); g.fillRect(x - 16, y - 2, 10, h + 4);
            g.fillRect(x + w + 6, y - 2, 10, h + 4);
            g.setColor(new Color(31, 48, 40));
            for (int yy = y + 4; yy < y + h; yy += 7) {
                g.fillRect(x - 16, yy, 10, 2); g.fillRect(x + w + 6, yy, 10, 2);
            }
        }
    }

    private void porta(Graphics2D g, int x, int y, int w, int h, boolean dupla) {
        g.setColor(new Color(57, 51, 39)); g.fillRect(x - 8, y - 8, w + 16, h + 12);
        g.setColor(new Color(136, 122, 93)); g.fillRect(x - 5, y - 5, w + 10, h + 5);
        g.setColor(new Color(75, 50, 32)); g.fillRect(x, y, w, h);
        for (int xx = x + 4; xx < x + w; xx += 9) {
            g.setColor(new Color(111, 76, 42)); g.fillRect(xx, y + 3, 3, h - 5);
        }
        g.setColor(new Color(31, 35, 32)); g.fillRect(x + 2, y + 13, w - 4, 5);
        g.fillRect(x + 2, y + h - 19, w - 4, 5);
        if (dupla) g.fillRect(x + w / 2 - 1, y, 3, h);
        g.setColor(new Color(212, 170, 77)); g.fillRect(x + w - 10, y + h / 2, 4, 7);
        g.setColor(new Color(159, 154, 130)); g.fillRect(x - 9, y + h + 1, w + 18, 5);
        g.setColor(new Color(91, 97, 86)); g.fillRect(x - 12, y + h + 6, w + 24, 6);
    }

    private void bandeira(Graphics2D g, int x, int y) {
        g.setColor(new Color(43, 48, 45)); g.fillRect(x - 4, y - 4, 38, 6);
        g.setColor(new Color(42, 80, 84)); g.fillRect(x, y, 30, 65);
        g.fillPolygon(new Polygon(new int[]{x, x + 15, x + 30}, new int[]{y + 65, y + 78, y + 65}, 3));
        g.setColor(new Color(205, 164, 78)); g.fillRect(x + 13, y + 12, 4, 37); g.fillRect(x + 6, y + 29, 18, 4);
    }

    private void vaso(Graphics2D g, int x, int y, int estilo) {
        g.setColor(new Color(111, 67, 41)); g.fillRect(x + 2, y - 5, 18, 15);
        g.setColor(new Color(167, 105, 65)); g.fillRect(x, y - 7, 22, 5);
        g.setColor(new Color(42, 79, 39)); g.fillRect(x - 2, y - 16, 24, 10);
        g.setColor(estilo % 2 == 0 ? new Color(176, 114, 108) : new Color(199, 172, 92));
        g.fillRect(x + 3, y - 18, 5, 5); g.fillRect(x + 14, y - 14, 4, 4);
    }

    private void lanterna(Graphics2D g, int x, int y) {
        g.setColor(new Color(39, 43, 37)); g.fillRect(x, y - 6, 12, 25);
        g.setColor(new Color(226, 190, 106)); g.fillRect(x + 3, y, 6, 13);
    }

    private void letreiro(Graphics2D g, int centro, int y, String texto, int tamanho) {
        g.setFont(new Font("Monospaced", Font.BOLD, tamanho));
        int w = g.getFontMetrics().stringWidth(texto) + 22;
        g.setColor(new Color(41, 37, 29)); g.fillRect(centro - w / 2 - 3, y - tamanho - 4, w + 6, tamanho + 14);
        g.setColor(new Color(104, 72, 41)); g.fillRect(centro - w / 2, y - tamanho - 1, w, tamanho + 8);
        g.setColor(new Color(225, 204, 151)); g.drawString(texto, centro - (w - 22) / 2, y + 1);
    }

    private void desenharFonte(Graphics2D destino) {
        Graphics2D g = (Graphics2D) destino.create();
        try {
            Rectangle a = MapaVila.AREA_FONTE;
            g.translate(a.x, a.y);
            g.setColor(new Color(14, 29, 27, 100)); g.fillOval(4, 79, 190, 116);
            g.setColor(new Color(58, 69, 65)); g.fillOval(0, 62, 192, 122);
            g.setColor(new Color(142, 147, 128)); g.fillOval(0, 50, 192, 120);
            g.setColor(new Color(190, 187, 151)); g.fillOval(8, 53, 176, 107);
            g.setColor(new Color(60, 82, 76)); g.fillOval(17, 62, 158, 89);
            g.setColor(new Color(37, 105, 118)); g.fillOval(20, 67, 152, 78);
            g.setColor(new Color(72, 141, 150)); g.drawOval(26, 73, 140, 65);
            g.setColor(new Color(127, 173, 169)); g.drawArc(42, 81, 110, 43, 5, 150);
            g.setColor(new Color(89, 105, 96)); g.fillRect(78, 51, 36, 72);
            g.setColor(new Color(163, 169, 140)); g.fillRect(82, 46, 20, 73);
            g.setColor(new Color(63, 79, 76)); g.fillOval(57, 27, 78, 43);
            g.setColor(new Color(184, 188, 154)); g.fillOval(53, 19, 86, 40);
            g.setColor(new Color(51, 118, 129)); g.fillOval(61, 23, 70, 29);
            g.setColor(new Color(125, 198, 197));
            g.fillRect(92, 4, 7, 34); g.fillRect(86, 10, 6, 6); g.fillRect(99, 12, 6, 6);
            g.fillRect(61, 43, 5, 60); g.fillRect(126, 43, 5, 60);
            g.setColor(new Color(185, 225, 208));
            g.fillRect(93, 4, 3, 29); g.fillRect(62, 49, 2, 38); g.fillRect(127, 52, 2, 35);
            for (int x : new int[]{55, 120}) { g.drawOval(x, 99, 23, 9); g.fillRect(x + 8, 97, 6, 2); }
            g.setColor(new Color(81, 88, 77));
            for (int x = 21; x < 182; x += 29) g.fillRect(x, 149 + Math.abs(x - 96) / 12, 2, 12);
        } finally { g.dispose(); }
    }

    private void desenharBarricada(Graphics2D g) {
        Rectangle a = MapaVila.AREA_BARRICADA;
        g.setColor(new Color(44, 38, 29));
        for (int x = a.x; x < a.x + a.width; x += 48) g.fillRect(x + 6, a.y - 19, 14, 64);
        g.setColor(new Color(122, 86, 46)); g.fillRect(a.x, a.y - 10, a.width, 13);
        g.fillRect(a.x, a.y + 15, a.width, 13);
        g.setColor(new Color(181, 142, 80)); g.fillRect(a.x, a.y - 10, a.width, 3);
        g.setColor(new Color(135, 53, 43)); g.fillRect(a.x + 103, a.y - 6, 29, 40);
        g.setColor(new Color(231, 191, 136)); g.fillRect(a.x + 116, a.y, 4, 17); g.fillRect(a.x + 116, a.y + 22, 4, 4);
        placa(g, a.x + 12, a.y - 100, "PASSAGEM FECHADA", true);
    }

    private void placa(Graphics2D g, int x, int y, String texto, boolean pequena) {
        int w = pequena ? 214 : 174;
        g.setColor(new Color(65, 47, 31)); g.fillRect(x + 9, y + 12, 8, 45);
        g.setColor(new Color(48, 39, 28)); g.fillRect(x, y, w, 34);
        g.setColor(new Color(123, 91, 49)); g.fillRect(x + 3, y + 3, w - 6, 27);
        g.setFont(new Font("Monospaced", Font.BOLD, pequena ? 16 : 18));
        g.setColor(new Color(225, 211, 169));
        g.drawString(texto, x + (w - g.getFontMetrics().stringWidth(texto)) / 2, y + 23);
    }
}
