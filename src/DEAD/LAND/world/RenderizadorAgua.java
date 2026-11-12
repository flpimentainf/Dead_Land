package DEAD.LAND.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/** Mesmo acabamento pixelado para o lago da vila e os lagos por tiles. */
public final class RenderizadorAgua {
    public static final Color AGUA = new Color(42, 96, 112);

    private RenderizadorAgua() {}

    public static void desenhar(Graphics2D destino, Shape agua, Rectangle limites) {
        Graphics2D g = (Graphics2D) destino.create();
        try {
            g.setStroke(new BasicStroke(24, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g.setColor(new Color(78, 91, 47)); g.draw(agua);
            g.setStroke(new BasicStroke(12, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g.setColor(new Color(133, 128, 79)); g.draw(agua);
            g.setColor(AGUA); g.fill(agua);
            g.clip(agua);
            g.setStroke(new BasicStroke(16, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g.setColor(new Color(66, 127, 131)); g.draw(agua);
            Area fundo = new Area(agua);
            fundo.subtract(new Area(new BasicStroke(54, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER).createStrokedShape(agua)));
            g.setColor(new Color(31, 80, 103)); g.fill(fundo);
            int reflexos = Math.max(22, Math.min(160, limites.width * limites.height / 2800));
            for (int i = 0; i < reflexos; i++) {
                int x = limites.x + 18 + i * 53 % Math.max(1, limites.width - 36);
                int y = limites.y + 24 + i * 37 % Math.max(1, limites.height - 48);
                g.setColor(i % 3 == 0 ? new Color(128, 174, 166) : new Color(80, 143, 147));
                g.fillRect(x / 3 * 3, y / 3 * 3, i % 3 == 0 ? 18 : 9, 3);
            }
        } finally { g.dispose(); }
    }

    public static BufferedImage criarCamada(int[][] mapa) {
        Area agua = camada(mapa, false);
        if (agua.isEmpty()) return null;
        int largura = mapa[0].length * 48, altura = mapa.length * 48;
        BufferedImage imagem = new BufferedImage(largura / 2, altura / 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.scale(0.5, 0.5);
            // A margem pode ocupar a beira de terra, mas não cobre muralhas ou interiores.
            g.clip(camada(mapa, true));
            desenhar(g, agua, agua.getBounds());
        } finally { g.dispose(); }
        return imagem;
    }

    private static Area camada(int[][] mapa, boolean incluirMargens) {
        Area area = new Area();
        for (int l = 0; l < mapa.length; l++) {
            int inicio = -1;
            for (int c = 0; c <= mapa[l].length; c++) {
                int id = c < mapa[l].length ? mapa[l][c] : -1;
                boolean incluir = id == 36 || (incluirMargens && (id == 54 || id == 55 || RenderizadorTiles.ehTrilha(id)));
                if (incluir && inicio < 0) inicio = c;
                if (!incluir && inicio >= 0) {
                    area.add(new Area(new Rectangle(inicio * 48, l * 48, (c - inicio) * 48, 48)));
                    inicio = -1;
                }
            }
        }
        return area;
    }
}
