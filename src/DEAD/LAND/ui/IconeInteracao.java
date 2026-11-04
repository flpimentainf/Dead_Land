package DEAD.LAND.ui;

import DEAD.LAND.entity.player;
import DEAD.LAND.world.tileMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class IconeInteracao {
    private static final int TAMANHO = 24;
    private static final int ESPACO = 8;

    public void desenharArea(Graphics2D g2, tileMap cenario, player jogador, Rectangle limites) {
        jogador.atualizarAreaColisao();
        tileMap.InteracaoPerto interacao = cenario.getInteracaoPerto(jogador.AreaColisao);

        if (interacao == null) {
            return;
        }

        if (interacao.tipo == tileMap.TIPO_CAMA) {
            desenharIndicador(g2, interacao.area, limites, jogador,
                    "E  DORMIR", new Color(24, 24, 32, 220));
        } else if (interacao.tipo == tileMap.TIPO_PORTA) {
            desenharIndicador(g2, interacao.area, limites, jogador,
                    "E", new Color(120, 70, 20, 180));
        }
    }

    public static void desenharIndicador(Graphics2D g2, Rectangle alvo, Rectangle limites,
            Rectangle jogador, String texto, Color corFundo) {
        int largura = "E".equals(texto) ? TAMANHO : 96;
        Rectangle caixa = posicionar(alvo, limites, jogador, largura);
        Graphics2D g = (Graphics2D) g2.create();
        try {
            g.setStroke(new java.awt.BasicStroke(1));
            g.setColor(corFundo);
            g.fillRoundRect(caixa.x, caixa.y, largura, TAMANHO, 8, 8);
            g.setColor(Color.WHITE);
            g.drawRoundRect(caixa.x, caixa.y, largura, TAMANHO, 8, 8);
            g.setFont(new Font("Arial", Font.BOLD, "E".equals(texto) ? 16 : 12));

            FontMetrics metricas = g.getFontMetrics();
            int textoX = caixa.x + (largura - metricas.stringWidth(texto)) / 2;
            int textoY = caixa.y + (TAMANHO - metricas.getHeight()) / 2 + metricas.getAscent();
            g.drawString(texto, textoX, textoY);
        } finally { g.dispose(); }
    }

    private static Rectangle posicionar(Rectangle alvo, Rectangle limites, Rectangle jogador, int largura) {
        int x = limitar(alvo.x + (alvo.width - largura) / 2, limites.x, limites.width - largura);
        int acima = alvo.y - TAMANHO - ESPACO;
        // Prioriza o centro acima do alvo e mantém a caixa fora do personagem.
        Rectangle[] candidatos = {
            new Rectangle(x, acima, largura, TAMANHO),
            new Rectangle(x, Math.min(alvo.y, jogador.y) - TAMANHO - ESPACO, largura, TAMANHO),
            new Rectangle(x, alvo.y + alvo.height + ESPACO, largura, TAMANHO),
            new Rectangle(x, Math.max(alvo.y + alvo.height, jogador.y + jogador.height) + ESPACO, largura, TAMANHO),
            new Rectangle(alvo.x - largura - ESPACO, alvo.y, largura, TAMANHO),
            new Rectangle(alvo.x + alvo.width + ESPACO, alvo.y, largura, TAMANHO)
        };
        for (Rectangle candidato : candidatos) {
            if (limites.contains(candidato) && !candidato.intersects(jogador)) return candidato;
        }
        return new Rectangle(x, limitar(acima, limites.y, limites.height - TAMANHO), largura, TAMANHO);
    }

    private static int limitar(int valor, int inicio, int espacoDisponivel) {
        return Math.max(inicio, Math.min(valor, inicio + Math.max(0, espacoDisponivel)));
    }
}
