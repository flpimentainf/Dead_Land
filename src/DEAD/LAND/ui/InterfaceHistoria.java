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
        desenhar(g2, largura, altura, historia, true);
    }

    public void desenhar(Graphics2D g2, int largura, int altura, SistemaHistoria historia, boolean objetivos) {
        if (historia == null) return;
        mensagemOrigem = List.copyOf(historia.getMensagem());

        if (historia.isTelaEscura()) {
            desenharEscurecimento(g2, largura, altura);
        }

        if (objetivos) desenharObjetivo(g2, largura, historia);

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

    public static int alturaObjetivos(Graphics2D g, SistemaHistoria historia) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        int linhas = Math.min(4, TextoHUD.quebrar(historia.getObjetivoAtual(), g.getFontMetrics(), 276).size());
        return 54 + linhas * 18 + (historia.getObjetivoSecundario().isBlank() ? 0 : 20);
    }

    private void desenharObjetivo(Graphics2D g2, int largura, SistemaHistoria historia) {
        int w = Math.min(304, largura - 40), h = alturaObjetivos(g2, historia);
        g2.setColor(new Color(12, 14, 20, 210));
        g2.fillRect(20, 18, w, h);
        g2.setColor(new Color(116, 112, 102)); g2.drawRect(20, 18, w, h);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(new Color(220, 206, 164));
        g2.drawString(historia.isObjetivoPrincipalConcluido() ? "[OK] OBJETIVO CONCLUÍDO" : "OBJETIVO ATUAL", 34, 40);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(Color.WHITE);
        List<String> linhas = TextoHUD.quebrar(historia.getObjetivoAtual(), g2.getFontMetrics(), w - 28);
        int n = Math.min(4, linhas.size());
        for (int i = 0; i < n; i++) {
            String linha = linhas.get(i);
            if (i == n - 1 && linhas.size() > n) linha = TextoHUD.resumir(linha, g2.getFontMetrics(), w - 28);
            g2.drawString(linha, 34, 62 + i * 18);
        }
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(Color.LIGHT_GRAY);
        int y = 18 + h - 12;
        if (!historia.getObjetivoSecundario().isBlank()) {
            g2.drawString(historia.isObjetivoSecundarioConcluido() ? "[OK] Secundária concluída" : "+ Missão secundária disponível", 34, y - 18);
        }
        g2.drawString("ESC > OBJETIVOS: detalhes", 34, y);
    }
    private void desenharEscolha(Graphics2D g2, int largura, int altura, SistemaHistoria historia) {
        List<String> linhas = new ArrayList<String>(historia.getMensagem());
        linhas.add("");
        linhas.add((historia.getOpcaoEscolhida() == 0 ? "> " : "  ") + historia.getOpcaoEscolha1());
        linhas.add((historia.getOpcaoEscolhida() == 1 ? "> " : "  ") + historia.getOpcaoEscolha2());
        String rodape = historia.isEscolhaNpc()
                ? "W/S escolhe | ENTER/E confirma"
                : "W/S escolhe | ENTER/E confirma | ESC 3x: segredo";
        desenharCaixaTexto(g2, largura, altura, linhas, rodape, historia.getNomeNpcFalando());
    }

    private List<String> chaveDialogo = List.of();
    private int paginaDialogo;
    private int totalPaginas = 1;
    private List<String> mensagemOrigem = List.of();

    public boolean avancarPagina(SistemaHistoria historia) {
        if (!historia.getMensagem().equals(mensagemOrigem) || paginaDialogo + 1 >= totalPaginas) return false;
        paginaDialogo++;
        return true;
    }

    public boolean temProximaPagina(SistemaHistoria historia) {
        return historia.getMensagem().equals(mensagemOrigem) && paginaDialogo + 1 < totalPaginas;
    }

    private void desenharCaixaTexto(Graphics2D g2, int largura, int altura,
            List<String> linhas, String rodape, String nomeNpc) {
        int caixaX = 32, caixaLargura = largura - 64;
        int textoX = caixaX + (nomeNpc == null ? 20 : 98);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        FontMetrics fm = g2.getFontMetrics();
        List<String> quebradas = new ArrayList<>();
        for (String linha : linhas) quebradas.addAll(TextoHUD.quebrar(linha, fm, caixaX + caixaLargura - textoX - 20));
        List<String> chave = linhas.stream().map(l -> l.startsWith("> ") || l.startsWith("  ") ? l.substring(2) : l).toList();
        if (!chave.equals(chaveDialogo)) {
            chaveDialogo = chave;
            paginaDialogo = 0;
        }
        int passo = fm.getHeight() + 2;
        int capacidade = Math.max(1, (altura - 170) / passo);
        totalPaginas = Math.max(1, (quebradas.size() + capacidade - 1) / capacidade);
        paginaDialogo = Math.min(paginaDialogo, totalPaginas - 1);
        int inicio = paginaDialogo * capacidade;
        int quantidade = Math.min(capacidade, quebradas.size() - inicio);
        int caixaAltura = Math.max(190, 82 + quantidade * passo);
        int caixaY = altura - caixaAltura - 20;
        g2.setColor(new Color(12, 14, 20, 242));
        g2.fillRect(caixaX, caixaY, caixaLargura, caixaAltura);
        g2.setColor(new Color(200, 193, 170));
        g2.drawRect(caixaX, caixaY, caixaLargura, caixaAltura);
        if (nomeNpc != null) desenharRetratoNpc(g2, caixaX + 18, caixaY + 22, nomeNpc);
        g2.setColor(Color.WHITE);
        for (int i = 0; i < quantidade; i++) g2.drawString(quebradas.get(inicio + i), textoX, caixaY + 30 + i * passo);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(Color.LIGHT_GRAY);
        String dica = paginaDialogo + 1 < totalPaginas ? "ENTER/E: próxima página" : rodape;
        List<String> dicas = TextoHUD.quebrar(dica + (totalPaginas > 1 ? " | " + (paginaDialogo + 1) + "/" + totalPaginas : ""),
                g2.getFontMetrics(), caixaLargura - 40);
        for (int i = 0; i < dicas.size(); i++) g2.drawString(dicas.get(i), caixaX + 20, caixaY + caixaAltura - 28 + i * 15);
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
