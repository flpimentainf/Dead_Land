package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class EstiloHUD {
    public static final Color TEXTO = new Color(255, 200, 80);
    public static final int LARGURA_BARRA = 160;

    private EstiloHUD() {}

    public static void desenharBarra(Graphics2D destino, String titulo, int x, int y,
            int altura, float proporcao, Color cor) {
        Graphics2D g2 = (Graphics2D) destino.create();
        try {
            float preenchimento = Math.max(0f, Math.min(1f, proporcao));
            g2.setColor(new Color(20, 20, 20, 200));
            g2.fillRoundRect(x - 2, y - 2, LARGURA_BARRA + 4, altura + 4, 8, 8);
            g2.setColor(new Color(55, 55, 65));
            g2.fillRoundRect(x, y, LARGURA_BARRA, altura, 6, 6);
            if (preenchimento > 0f) {
                g2.setColor(cor);
                g2.fillRoundRect(x, y, Math.round(LARGURA_BARRA * preenchimento), altura, 6, 6);
            }
            g2.setColor(new Color(200, 200, 200, 180));
            g2.drawRoundRect(x, y, LARGURA_BARRA, altura, 6, 6);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(TEXTO);
            g2.drawString(titulo, x, y - 4);
        } finally {
            g2.dispose();
        }
    }
}
