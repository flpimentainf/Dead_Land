package DEAD.LAND.ui;

import DEAD.LAND.story.SistemaHistoria;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

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
            desenharCaixaTexto(
                    g2,
                    largura,
                    altura,
                    historia.getMensagem(),
                    "ENTER/E para continuar",
                    historia.getNomeNpcFalando()
            );
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
        linhas.add((historia.getOpcaoEscolhida() == 0 ? "> " : "  ") + historia.getOpcaoEscolha1());
        linhas.add((historia.getOpcaoEscolhida() == 1 ? "> " : "  ") + historia.getOpcaoEscolha2());
        String rodape = historia.isEscolhaNpc()
                ? "↑/↓ escolhe | ENTER/E confirma"
                : "↑/↓ escolhe | ENTER/E confirma | ESC 3x: segredo";
        desenharCaixaTexto(g2, largura, altura, linhas, rodape, historia.getNomeNpcFalando());
    }

    private void desenharCaixaTexto(Graphics2D g2, int largura, int altura,
            List<String> linhas, String rodape, String nomeNpc) {
        int caixaX = 40;
        int caixaLargura = largura - 80;
        int caixaAltura = Math.min(altura - 80, Math.max(190, 70 + linhas.size() * 24));
        int caixaY = altura - caixaAltura - 34;

        g2.setColor(new Color(0, 0, 0, 205));
        g2.fillRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 18, 18);
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 18, 18);

        int textoX = caixaX + 24;
        if (nomeNpc != null) {
            desenharRetratoNpc(g2, caixaX + 22, caixaY + 24, nomeNpc);
            textoX += 72;
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int y = caixaY + 32;
        for (String linha : linhas) {
            g2.drawString(linha, textoX, y);
            y += fm.getHeight() + 2;
        }

        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(rodape, caixaX + 24, caixaY + caixaAltura - 16);
    }

    private void desenharRetratoNpc(Graphics2D g2, int x, int y, String nomeNpc) {
        String arquivo = getArquivoRetrato(nomeNpc);
        ImageIcon icone = new ImageIcon(
                "repos/NPCs e Inimigos/NPC/retratos/" + arquivo
        );

        if (icone.getIconWidth() > 0) {
            Image retrato = icone.getImage();
            g2.drawImage(retrato, x, y, 64, 64, null);
            return;
        }

    }

    private String getArquivoRetrato(String nomeNpc) {
        if ("Ari".equals(nomeNpc)) return "ari.png";
        if ("Mara".equals(nomeNpc)) return "mara.png";
        if ("Guardião".equals(nomeNpc)) return "guardiao.png";
        if ("Porteiro".equals(nomeNpc)) return "porteiro.png";
        return nomeNpc.toLowerCase() + ".png";
    }
}
