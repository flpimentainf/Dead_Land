package DEAD.LAND.ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

public final class LayoutMenuPausa {
    private LayoutMenuPausa() {}

    public static Rectangle opcaoPrincipal(int indice) {
        return new Rectangle(184, 132 + indice * 44, 400, 40);
    }

    public static Rectangle opcaoConfiguracao(int indice) {
        return new Rectangle(212, 109 + indice * 48, 360, 42);
    }

    public static Rectangle volume(int indice) {
        return new Rectangle(345, 117 + indice * 48, 150, 20);
    }

    public static Rectangle voltarMemorias() { return new Rectangle(264, 420, 240, 38); }

    public static int indicePrincipal(Point2D ponto) {
        for (int i = 0; i < 5; i++) if (ponto != null && opcaoPrincipal(i).contains(ponto)) return i;
        return -1;
    }

    public static int indiceConfiguracao(Point2D ponto) {
        for (int i = 0; i < 5; i++) if (ponto != null && opcaoConfiguracao(i).contains(ponto)) return i;
        return -1;
    }

    public static float volumeEmX(double x) {
        Rectangle barra = volume(0);
        return (float) Math.max(0, Math.min(1, (x - barra.x) / barra.width));
    }
}
