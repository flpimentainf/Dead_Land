package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class EstiloHUD {
    public static final Color TEXTO = new Color(236, 232, 218);
    public static final Color DESTAQUE = new Color(220, 206, 164);
    public static final Color FUNDO = new Color(12, 14, 20, 235);
    public static final Color BORDA = new Color(116, 112, 102);
    public static final Color VIDA = new Color(66, 180, 100);
    public static final Color ESTAMINA = new Color(230, 188, 64);
    public static final Color CHEFE = new Color(170, 66, 190);
    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 12);
    public static final Font CORPO = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font DICA = new Font("SansSerif", Font.PLAIN, 11);
    public static final int LARGURA_BARRA = 160;

    private EstiloHUD() {}

    public static void desenharBarra(Graphics2D destino, String titulo, int x, int y,
            int altura, float proporcao, Color cor) {
        desenharBarra(destino, titulo, x, y, LARGURA_BARRA, altura, proporcao, cor);
    }

    public static void desenharBarra(Graphics2D destino, String titulo, int x, int y,
            int largura, int altura, float proporcao, Color cor) {
        desenharBarra(destino, titulo, x, y, largura, altura, proporcao, cor, "");
    }

    public static void desenharBarra(Graphics2D destino, String titulo, int x, int y,
            int largura, int altura, float proporcao, Color cor, String valor) {
        Graphics2D g2 = (Graphics2D) destino.create();
        try {
            float preenchimento = Math.max(0f, Math.min(1f, proporcao));
            g2.setColor(new Color(20, 20, 20, 200));
            g2.fillRect(x - 2, y - 2, largura + 4, altura + 4);
            g2.setColor(new Color(55, 55, 65));
            g2.fillRect(x, y, largura, altura);
            if (preenchimento > 0f) {
                g2.setColor(cor);
                g2.fillRect(x, y, Math.round(largura * preenchimento), altura);
            }
            g2.setColor(new Color(200, 200, 200, 180));
            g2.drawRect(x, y, largura, altura);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(TITULO.deriveFont(11f));
            g2.setColor(TEXTO);
            g2.drawString(titulo, x, y - 4);
            g2.drawString(valor, x + largura - g2.getFontMetrics().stringWidth(valor), y - 4);
        } finally {
            g2.dispose();
        }
    }
}
