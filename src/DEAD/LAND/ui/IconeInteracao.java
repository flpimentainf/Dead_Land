package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import DEAD.LAND.entity.player;
import DEAD.LAND.world.tileMap;

public class IconeInteracao {
    private static final int TAMANHO = 24;

    public void desenharCama(Graphics2D g2, tileMap cenario, player jogador) {
        jogador.atualizarAreaColisao();
        Rectangle areaCama = cenario.getAreaCamaPerto(jogador.AreaColisao);

        if (areaCama == null) {
            return;
        }

        int x = areaCama.x + areaCama.width / 2 - TAMANHO / 2;
        int y = areaCama.y - TAMANHO - 4;

        if (y < 4) {
            y = areaCama.y + 4;
        }

        desenharBotao(g2, x, y);
    }

    private void desenharBotao(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x, y, TAMANHO, TAMANHO, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, TAMANHO, TAMANHO, 8, 8);
        g2.setFont(new Font("Arial", Font.BOLD, 16));

        FontMetrics metricas = g2.getFontMetrics();
        int textoX = x + (TAMANHO - metricas.stringWidth("E")) / 2;
        int textoY = y + (TAMANHO + metricas.getAscent()) / 2 - 3;
        g2.drawString("E", textoX, textoY);
    }
}
