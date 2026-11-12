package DEAD.LAND.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** Copas, galhos e troncos em pixels de 2px; sem novas cópias de tiles. */
public final class RenderizadorArvores {
    private final Map<Integer, BufferedImage> copias = new HashMap<>();

    public void desenhar(Graphics2D g, int x, int y, int variacao, boolean grande) {
        BufferedImage imagem = copias.computeIfAbsent(Math.floorMod(variacao, 4), this::criar);
        int largura = grande ? 96 : 64, altura = grande ? 144 : 108;
        g.drawImage(imagem, x + 24 - largura / 2, y + 48 - altura, largura, altura, null);
    }

    private BufferedImage criar(int variacao) {
        BufferedImage imagem = new BufferedImage(48, 72, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.setColor(new Color(7, 17, 15, 100)); g.fillOval(6, 63, 38, 8);
            g.setColor(new Color(38, 29, 25)); g.fillRect(19, 29, 12, 38);
            g.fillRect(15, 65, 7, 4); g.fillRect(28, 64, 7, 4);
            g.setColor(new Color(83, 59, 35)); g.fillRect(22, 29, 6, 36);
            g.setColor(new Color(119, 84, 47)); g.fillRect(23, 42, 2, 22);
            g.setColor(new Color(53, 40, 28)); g.fillRect(27, 44, 3, 23);
            g.setColor(new Color(87, 60, 33));
            g.fillPolygon(new Polygon(new int[]{23, 12, 11, 24}, new int[]{49, 39, 33, 42}, 4));
            g.fillPolygon(new Polygon(new int[]{27, 37, 37, 27}, new int[]{43, 31, 25, 35}, 4));
            int tom = variacao * 4;
            copa(g, 4, 14, 25, 28, new Color(20 + tom, 47 + tom, 28));
            copa(g, 18, 10, 28, 29, new Color(25 + tom, 58 + tom, 31));
            copa(g, 8, 2, 29, 29, new Color(31 + tom, 69 + tom, 35));
            copa(g, 1, 25, 27, 22, new Color(29 + tom, 63 + tom, 31));
            copa(g, 20, 25, 27, 23, new Color(24 + tom, 55 + tom, 29));
            copa(g, 10, 18, 27, 26, new Color(40 + tom, 80 + tom, 39));
            Random rng = new Random(571 + variacao);
            for (int i = 0; i < 70; i++) {
                int x = 3 + rng.nextInt(42), y = 4 + rng.nextInt(40);
                if ((imagem.getRGB(x, y) >>> 24) == 0) continue;
                g.setColor(i % 3 == 0 ? new Color(73 + tom, 109 + tom, 48)
                        : new Color(27 + tom, 57 + tom, 29));
                g.fillRect(x, y, 2 + rng.nextInt(3), 1);
                if (i % 4 == 0) g.fillRect(x + 1, y - 1, 2, 1);
            }
        } finally { g.dispose(); }
        return imagem;
    }

    private void copa(Graphics2D g, int x, int y, int w, int h, Color cor) {
        g.setColor(new Color(12, 31, 23));
        g.fillRect(x + 5, y, w - 10, h);
        g.fillRect(x + 2, y + 4, w - 4, h - 8);
        g.fillRect(x, y + 8, w, h - 16);
        g.setColor(cor); g.fillRect(x + 5, y + 2, w - 10, h - 4);
        g.fillRect(x + 3, y + 5, w - 6, h - 11);
        g.fillRect(x + 1, y + 9, w - 2, h - 19);
        g.setColor(cor.brighter()); g.fillRect(x + 7, y + 3, Math.max(3, w / 3), 2);
    }
}
