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

    public void desenharArea(Graphics2D g2, tileMap cenario, player jogador) {
        jogador.atualizarAreaColisao();
        tileMap.InteracaoPerto interacao = cenario.getInteracaoPerto(jogador.AreaColisao);

        if (interacao == null) {
            return;
        }

        Rectangle area = interacao.area;
        int x = area.x + area.width - TAMANHO / 2;
        int y = area.y - TAMANHO + 20;

        if (y < 4) {
            y = area.y + 4;
        }

        if (interacao.tipo == tileMap.TIPO_CAMA) {
            desenharBotao(g2, x, y, "Z", new Color(30, 80, 180, 180));
        } else if (interacao.tipo == tileMap.TIPO_PORTA) {
            desenharBotao(g2, x, y, "P", new Color(120, 70, 20, 180));
        }
    }

    private void desenharBotao(Graphics2D g2, int x, int y, String texto, Color corFundo) {
        g2.setColor(corFundo);
        g2.fillRoundRect(x, y, TAMANHO, TAMANHO, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, TAMANHO, TAMANHO, 8, 8);
        g2.setFont(new Font("Arial", Font.BOLD, 16));

        FontMetrics metricas = g2.getFontMetrics();
        int textoX = x + (TAMANHO - metricas.stringWidth(texto)) / 2;
        int textoY = y + (TAMANHO + metricas.getAscent()) / 2 - 3;
        g2.drawString(texto, textoX, textoY);
    }
}
