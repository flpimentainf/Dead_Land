package DEAD.LAND.world;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

/** A mesma margem pixelada delimita a água visível e a colisão. */
public final class GeometriaLago {
    private final Area agua = new Area();
    private final Rectangle limites;
    public GeometriaLago(Rectangle limites) {
        this.limites = new Rectangle(limites);
        Path2D p = new Path2D.Double();
        p.moveTo(.24, .08); p.curveTo(.43, -.03, .64, .14, .77, .15);
        p.curveTo(1.02, .2, .91, .4, .96, .55);
        p.curveTo(1.01, .84, .76, .99, .57, .89);
        p.curveTo(.39, .81, .3, 1.04, .12, .82);
        p.curveTo(-.07, .64, .12, .52, .07, .34);
        p.curveTo(.02, .18, .08, .12, .24, .08); p.closePath();
        for (int y = 0; y < limites.height; y += 6) {
            int inicio = -1;
            for (int x = 0; x <= limites.width; x += 6) {
                boolean dentro = x < limites.width && p.contains((x + 3.0) / limites.width, (y + 3.0) / limites.height);
                if (dentro && inicio < 0) inicio = x;
                if (!dentro && inicio >= 0) {
                    agua.add(new Area(new Rectangle(limites.x + inicio, limites.y + y, x - inicio, 6)));
                    inicio = -1;
                }
            }
        }
    }
    public Shape agua() { return new Area(agua); }
    public Rectangle limites() { return new Rectangle(limites); }
    public boolean colide(Rectangle area) { return agua.intersects(area); }
    public boolean contem(double x, double y) { return agua.contains(x, y); }
}
