package DEAD.LAND.ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

public final class LayoutConquistas {
    private LayoutConquistas() {}
    public static Rectangle no(int indice) {
        return switch (indice) {
            case 0 -> new Rectangle(136, 108, 48, 48);
            case 1 -> new Rectangle(360, 108, 48, 48);
            case 2 -> new Rectangle(584, 108, 48, 48);
            case 3 -> new Rectangle(248, 210, 48, 48);
            case 4 -> new Rectangle(472, 210, 48, 48);
            case 5 -> new Rectangle(360, 296, 48, 48);
            default -> throw new IllegalArgumentException("Conquista inválida: " + indice);
        };
    }
    public static Rectangle voltar() { return new Rectangle(620, 440, 104, 28); }
    public static Rectangle detalhes() { return new Rectangle(52, 376, 664, 54); }
    public static int indice(Point2D ponto) {
        for (int i = 0; i < 6; i++) if (ponto != null && no(i).contains(ponto)) return i;
        return -1;
    }
    public static int navegar(int atual, int dx, int dy) {
        Rectangle origem = no(atual);
        int escolhido = atual;
        double distancia = Double.MAX_VALUE;
        for (int i = 0; i < 6; i++) {
            Rectangle candidato = no(i);
            double x = candidato.getCenterX() - origem.getCenterX(), y = candidato.getCenterY() - origem.getCenterY();
            if (dx != 0 && (y != 0 || x * dx <= 0) || dy != 0 && y * dy <= 0 || i == atual) continue;
            double valor = dx != 0 ? Math.abs(x) : Math.abs(y) * 10 + Math.abs(x);
            if (valor < distancia) { distancia = valor; escolhido = i; }
        }
        return escolhido;
    }
}
