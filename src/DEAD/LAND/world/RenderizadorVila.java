package DEAD.LAND.world;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/** Fachadas montadas como objetos inteiros, sobre o terreno contínuo da vila. */
public final class RenderizadorVila {
    private BufferedImage terreno;
    private final Map<MapaVila.Construcao, BufferedImage> fachadas = new HashMap<>();
    private final RenderizadorFachadas fachadasPixel = new RenderizadorFachadas();
    private final RenderizadorArvores arvores = new RenderizadorArvores();

    public void desenhar(Graphics2D g, int[][] objetos) {
        if (terreno == null) terreno = criarTerreno();
        g.drawImage(terreno, 0, 0, 2400, 1200, null);
        for (MapaVila.Construcao casa : MapaVila.CONSTRUCOES) {
            if (objetos[casa.linha()][casa.coluna()] != MapaVila.CONSTRUCAO) continue;
            BufferedImage imagem = fachadas.computeIfAbsent(casa, fachadasPixel::criar);
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
            RenderizadorCaminhos.desenhar(g, MapaVila.caminhos());
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
