package DEAD.LAND.ui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public final class ViewportJogo {
    private final int larguraLogica;
    private final int alturaLogica;

    public ViewportJogo(int larguraLogica, int alturaLogica) {
        this.larguraLogica = larguraLogica;
        this.alturaLogica = alturaLogica;
    }

    public Rectangle getArea(int larguraPainel, int alturaPainel) {
        double escala = Math.min(Math.max(1, larguraPainel) / (double) larguraLogica,
                Math.max(1, alturaPainel) / (double) alturaLogica);
        int largura = Math.max(1, (int) Math.round(larguraLogica * escala));
        int altura = Math.max(1, (int) Math.round(alturaLogica * escala));
        return new Rectangle((larguraPainel - largura) / 2, (alturaPainel - altura) / 2, largura, altura);
    }

    public Point2D.Double paraLogico(Point ponto, int larguraPainel, int alturaPainel) {
        if (ponto == null || larguraPainel <= 0 || alturaPainel <= 0) return null;
        Rectangle area = getArea(larguraPainel, alturaPainel);
        if (!area.contains(ponto)) return null;
        return new Point2D.Double((ponto.x - area.x) * larguraLogica / (double) area.width,
                (ponto.y - area.y) * alturaLogica / (double) area.height);
    }
}
