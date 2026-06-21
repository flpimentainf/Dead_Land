package DEAD.LAND.ui;

import DEAD.LAND.entity.player;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class BarraDeVida {
    private static final int LARGURA = 160;
    private static final int ALTURA = 16;
    private static final int MARGEM_X = 16;
    private static final int MARGEM_Y = 16;
    private static final int RAIO = 6;

    public void desenhar(Graphics2D g2, player jogador, int larguraTela, int alturaTela) {
        int vida = jogador.getVida();
        int vidaMax = player.VIDA_MAXIMA;
        float proporcao = (float) vida / vidaMax;

        int x = MARGEM_X;
        int y = alturaTela - MARGEM_Y - ALTURA - 24;

        // Fundo escuro
        g2.setColor(new Color(20, 20, 20, 200));
        g2.fillRoundRect(x - 2, y - 2, LARGURA + 4, ALTURA + 4, RAIO + 2, RAIO + 2);

        // Barra cinza (fundo)
        g2.setColor(new Color(60, 60, 60, 200));
        g2.fillRoundRect(x, y, LARGURA, ALTURA, RAIO, RAIO);

        // Barra de vida colorida
        if (proporcao > 0) {
            Color corVida = proporcao > 0.5f
                ? new Color(60, 200, 60)
                : proporcao > 0.25f
                    ? new Color(230, 180, 0)
                    : new Color(220, 40, 40);
            g2.setColor(corVida);
            g2.fillRoundRect(x, y, (int)(LARGURA * proporcao), ALTURA, RAIO, RAIO);
        }

        // Borda
        g2.setColor(new Color(200, 200, 200, 180));
        g2.drawRoundRect(x, y, LARGURA, ALTURA, RAIO, RAIO);

        // Texto HP
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        String txt = vida + " / " + vidaMax;
        int tw = g2.getFontMetrics().stringWidth(txt);
        g2.drawString(txt, x + (LARGURA - tw) / 2, y + ALTURA - 3);

        // Label "HP"
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setColor(new Color(255, 200, 80));
        g2.drawString("HP", x, y - 4);
    }
}
