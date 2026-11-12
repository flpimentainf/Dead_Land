package DEAD.LAND.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/** Curvas amostradas em degraus pequenos, com uma paleta curta de terra. */
public final class RenderizadorCaminhos {
    private static final int PIXEL = 6;
    private static final Color TERRA = new Color(137, 103, 65);
    private static final Color CLARA = new Color(153, 117, 73);
    private static final Color SOMBRA = new Color(122, 91, 58);
    private static final Color BORDA = new Color(105, 88, 51);
    private static final Color GRAMA = new Color(69, 89, 44);

    private RenderizadorCaminhos() {}

    public static void desenhar(Graphics2D g, Shape caminhos) {
        desenhar(g, caminhos, 2400, 1200, 0);
    }

    public static void desenhar(Graphics2D g, Shape caminhos, int largura, int altura, int variacao) {
        int linhas = altura / PIXEL, colunas = largura / PIXEL;
        boolean[][] mascara = new boolean[linhas][colunas];
        for (int l = 0; l < linhas; l++) for (int c = 0; c < colunas; c++) {
            mascara[l][c] = caminhos.contains(c * PIXEL + 3, l * PIXEL + 3);
        }
        for (int l = 0; l < linhas; l++) for (int c = 0; c < colunas; c++) {
            int x = c * PIXEL, y = l * PIXEL;
            boolean cima = dentro(mascara, l - 1, c), baixo = dentro(mascara, l + 1, c);
            boolean esquerda = dentro(mascara, l, c - 1), direita = dentro(mascara, l, c + 1);
            int semente = Math.floorMod(c * 73856093 ^ l * 19349663 ^ variacao * 83492791, 101);
            if (!mascara[l][c]) {
                if (cima || baixo || esquerda || direita) {
                    g.setColor(GRAMA); g.fillRect(x, y, PIXEL, PIXEL);
                    if (semente % 7 == 0) {
                        g.setColor(new Color(92, 112, 52)); g.fillRect(x, y, 3, 6);
                    }
                }
                continue;
            }
            boolean borda = !cima || !baixo || !esquerda || !direita;
            g.setColor(borda ? BORDA : TERRA); g.fillRect(x, y, PIXEL, PIXEL);
            if (!borda && semente < 9) {
                g.setColor(semente < 5 ? CLARA : SOMBRA);
                g.fillRect(x, y, PIXEL, 3);
                if (semente < 3) g.fillRect(x + 3, y + 3, 6, 3);
            } else if (borda && semente % 5 == 0) {
                g.setColor(GRAMA); g.fillRect(x, y, 3, 3);
            }
        }
    }

    private static boolean dentro(boolean[][] mascara, int l, int c) {
        return l < 0 || l >= mascara.length || c < 0 || c >= mascara[0].length || mascara[l][c];
    }
}
