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
    private static final int ESPACO = 4;

    public void desenharArea(Graphics2D g2, tileMap cenario, player jogador, Rectangle limites, Rectangle... obstaculos) {
        jogador.atualizarAreaColisao();
        tileMap.InteracaoPerto interacao = cenario.getInteracaoPerto(jogador.AreaColisao);

        if (interacao == null) {
            return;
        }

        if (interacao.tipo == tileMap.TIPO_CAMA) {
            desenharIndicador(g2, interacao.area, limites, jogador,
                    "E  DORMIR", new Color(24, 24, 32, 220), obstaculos);
        } else if (interacao.tipo == tileMap.TIPO_PORTA) {
            Rectangle porta = interacao.area;
            Rectangle caixa = new Rectangle(porta.x + (porta.width - TAMANHO) / 2,
                    porta.y + (porta.height - TAMANHO) / 2, TAMANHO, TAMANHO);
            // A porta ancora o E; o jogador pode passar atrás da indicação.
            if (limites.contains(caixa) && livre(caixa, obstaculos)) {
                desenharBotao(g2, caixa, "E", new Color(120, 70, 20, 220));
            } else {
                desenharIndicador(g2, porta, limites, jogador, "E", new Color(120, 70, 20, 220), obstaculos);
            }
        }
    }

    public static void desenharIndicador(Graphics2D g2, Rectangle alvo, Rectangle limites,
            Rectangle jogador, String texto, Color corFundo, Rectangle... obstaculos) {
        int largura = "E".equals(texto) ? TAMANHO : 96;
        Rectangle caixa = posicionar(alvo, limites, jogador, largura, obstaculos);
        if (caixa != null) desenharBotao(g2, caixa, texto, corFundo);
    }

    private static void desenharBotao(Graphics2D g2, Rectangle caixa, String texto, Color corFundo) {
        int largura = caixa.width;
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

    public static Rectangle posicionar(Rectangle alvo, Rectangle limites, Rectangle jogador, int largura,
            Rectangle... obstaculos) {
        int x = limitar(alvo.x + (alvo.width - largura) / 2, limites.x, limites.width - largura);
        int acima = alvo.y - TAMANHO - ESPACO;
        // Cada opção permanece a 4px do alvo. Nunca desloca a indicação até o HUD.
        int lateralY = limitar(alvo.y + (alvo.height - TAMANHO) / 2, limites.y, limites.height - TAMANHO);
        Rectangle[] candidatos = {
            new Rectangle(alvo.x + alvo.width + ESPACO, lateralY, largura, TAMANHO),
            new Rectangle(alvo.x - largura - ESPACO, lateralY, largura, TAMANHO),
            new Rectangle(x, acima, largura, TAMANHO),
            new Rectangle(x, alvo.y + alvo.height + ESPACO, largura, TAMANHO)
        };
        for (Rectangle candidato : candidatos) {
            if (limites.contains(candidato) && !candidato.intersects(jogador) && livre(candidato, obstaculos)) return candidato;
        }
        return null;
    }

    private static boolean livre(Rectangle area, Rectangle[] obstaculos) {
        for (Rectangle obstaculo : obstaculos) if (obstaculo.intersects(area)) return false;
        return true;
    }

    private static int limitar(int valor, int inicio, int espacoDisponivel) {
        return Math.max(inicio, Math.min(valor, inicio + Math.max(0, espacoDisponivel)));
    }
}
