package DEAD.LAND.ui;

import DEAD.LAND.story.SistemaHistoria;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class InterfaceHistoria {
    public void desenhar(Graphics2D g2, int largura, int altura, SistemaHistoria historia) {
        if (historia == null) return;

        if (historia.isTelaEscura()) {
            desenharEscurecimento(g2, largura, altura);
        }

        desenharObjetivo(g2, largura, historia.getObjetivoAtual());

        if (historia.isExibindoEscolha()) {
            desenharEscolha(g2, largura, altura, historia);
            return;
        }

        if (historia.isExibindoMensagem()) {
            desenharCaixaTexto(g2, largura, altura, historia.getMensagem(), "ENTER/E para continuar");
        }
    }

    private void desenharEscurecimento(Graphics2D g2, int largura, int altura) {
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, largura, altura);
    }

    private void desenharObjetivo(Graphics2D g2, int largura, String objetivoAtual) {
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(20, 18, Math.min(largura - 40, 560), 34, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawString("Missão: " + objetivoAtual, 34, 41);
    }

    private void desenharEscolha(Graphics2D g2, int largura, int altura, SistemaHistoria historia) {
        List<String> linhas = new ArrayList<String>(historia.getMensagem());
        linhas.add("");
        linhas.add((historia.getOpcaoEscolhida() == 0 ? "> " : "  ") + "Acordar");
        linhas.add((historia.getOpcaoEscolhida() == 1 ? "> " : "  ") + "Ficar em Dead Land");
        desenharCaixaTexto(g2, largura, altura, linhas, "↑/↓ escolhe | ENTER/E confirma | ESC 3x: segredo");
    }

    private void desenharCaixaTexto(Graphics2D g2, int largura, int altura, List<String> linhas, String rodape) {
        int caixaX = 40;
        int caixaLargura = largura - 80;
        int caixaAltura = Math.min(altura - 80, Math.max(190, 70 + linhas.size() * 24));
        int caixaY = altura - caixaAltura - 34;

        g2.setColor(new Color(0, 0, 0, 205));
        g2.fillRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 18, 18);
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 18, 18);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int y = caixaY + 32;
        for (String linha : linhas) {
            g2.drawString(linha, caixaX + 24, y);
            y += fm.getHeight() + 2;
        }

        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(rodape, caixaX + 24, caixaY + caixaAltura - 16);
    }
}
