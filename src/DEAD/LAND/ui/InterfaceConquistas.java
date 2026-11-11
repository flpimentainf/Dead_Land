package DEAD.LAND.ui;

import DEAD.LAND.core.ResourceManager;
import DEAD.LAND.core.SistemaConquistas;
import DEAD.LAND.core.SistemaConquistas.Conquista;
import java.awt.*;

public final class InterfaceConquistas {
    private static final Conquista[] LISTA = Conquista.values();
    private final Image pedra = ResourceManager.getInstancia().carregarImagem("repos/tileset/parede_musgo.png");

    public void desenhar(Graphics2D destino, SistemaConquistas sistema, int selecionada) {
        Graphics2D g = (Graphics2D) destino.create();
        try {
            g.setColor(new Color(15, 17, 22, 245)); g.fillRect(36, 58, 696, 310);
            Graphics2D fundo = (Graphics2D) g.create();
            try {
                fundo.clipRect(38, 60, 692, 306);
                for (int y = 60; y < 368; y += 48) for (int x = 38; x < 732; x += 48) fundo.drawImage(pedra, x, y, 48, 48, null);
                fundo.setColor(new Color(4, 6, 10, 226)); fundo.fillRect(38, 60, 692, 306);
            } finally { fundo.dispose(); }
            g.setColor(EstiloHUD.BORDA); g.drawRect(36, 58, 695, 309);
            g.setFont(EstiloHUD.TITULO.deriveFont(24f)); g.setColor(EstiloHUD.TEXTO);
            centralizar(g, "CONQUISTAS", 384, 36);
            g.setFont(EstiloHUD.DICA); g.setColor(EstiloHUD.DESTAQUE);
            g.drawString(sistema.getQuantidade() + "/" + LISTA.length + " desbloqueadas", 574, 38);
            g.drawString("MEMÓRIAS", 56, 88); g.drawString("ARMAS", 56, 204); g.drawString("CURA", 56, 290);
            g.drawString("Obtenha 1 poção", 56, 308);
            ligar(g, sistema, 0, 1); ligar(g, sistema, 1, 2); ligar(g, sistema, 3, 4);
            for (int i = 0; i < LISTA.length; i++) desenharNo(g, sistema, i, i == selecionada);

            Conquista conquista = LISTA[selecionada];
            Rectangle detalhes = LayoutConquistas.detalhes();
            g.setColor(new Color(10, 12, 17)); g.fillRect(detalhes.x, detalhes.y, detalhes.width, detalhes.height);
            g.setColor(EstiloHUD.BORDA); g.drawRect(detalhes.x, detalhes.y, detalhes.width, detalhes.height);
            g.setFont(EstiloHUD.TITULO); g.setColor(EstiloHUD.DESTAQUE);
            g.drawString(conquista.titulo, detalhes.x + 12, detalhes.y + 17);
            String estado = sistema.desbloqueada(conquista) ? "[OK] DESBLOQUEADA" : "[ ] NÃO CONQUISTADA";
            g.drawString(estado, detalhes.x + detalhes.width - g.getFontMetrics().stringWidth(estado) - 12, detalhes.y + 17);
            g.setFont(EstiloHUD.CORPO.deriveFont(12f)); g.setColor(EstiloHUD.TEXTO);
            g.drawString(conquista.requisito + "  (" + sistema.getProgresso(conquista) + "/" + sistema.getMeta(conquista) + ")", detalhes.x + 12, detalhes.y + 39);
            g.setFont(EstiloHUD.DICA); g.setColor(Color.LIGHT_GRAY);
            g.drawString("WASD ou mouse: selecionar  |  ENTER/ESC: voltar", 52, 458);
            Rectangle voltar = LayoutConquistas.voltar();
            g.setColor(new Color(55, 57, 65)); g.fillRect(voltar.x, voltar.y, voltar.width, voltar.height);
            g.setColor(EstiloHUD.BORDA); g.drawRect(voltar.x, voltar.y, voltar.width, voltar.height);
            g.setFont(EstiloHUD.TITULO); g.setColor(EstiloHUD.TEXTO); centralizar(g, "VOLTAR", (int) voltar.getCenterX(), voltar.y + 19);
        } finally { g.dispose(); }
    }

    private void ligar(Graphics2D g, SistemaConquistas sistema, int de, int para) {
        Rectangle a = LayoutConquistas.no(de), b = LayoutConquistas.no(para);
        g.setColor(new Color(4, 5, 7)); g.fillRect(a.x + 48, a.y + 20, b.x - a.x - 48, 8);
        g.setColor(sistema.desbloqueada(LISTA[de]) && sistema.desbloqueada(LISTA[para]) ? EstiloHUD.DESTAQUE : new Color(66, 69, 74));
        g.fillRect(a.x + 48, a.y + 23, b.x - a.x - 48, 2);
    }

    private void desenharNo(Graphics2D g, SistemaConquistas sistema, int indice, boolean selecionado) {
        Conquista conquista = LISTA[indice];
        Rectangle no = LayoutConquistas.no(indice);
        boolean conquistada = sistema.desbloqueada(conquista);
        g.setColor(new Color(2, 3, 5)); g.fillRect(no.x - 4, no.y - 4, 56, 56);
        g.setColor(selecionado ? new Color(244, 224, 156) : conquistada ? new Color(158, 135, 77) : new Color(85, 88, 94));
        g.drawRect(no.x - 3, no.y - 3, 53, 53); g.drawRect(no.x - 2, no.y - 2, 51, 51);
        g.setColor(new Color(22, 25, 33)); g.fillRect(no.x, no.y, 48, 48);
        IconesConquistas.desenhar(g, conquista, no, conquistada);
        g.setFont(EstiloHUD.TITULO.deriveFont(11f)); g.setColor(conquistada ? EstiloHUD.DESTAQUE : new Color(192, 194, 198));
        centralizar(g, (conquistada ? "[OK] " : "") + conquista.titulo, (int) no.getCenterX(), no.y + 66);
        // A trilha da poção fica próxima do painel de detalhes.
        if (indice != 5) {
            g.setFont(EstiloHUD.DICA.deriveFont(10f)); g.setColor(Color.LIGHT_GRAY);
            centralizar(g, conquista.resumo + " · " + sistema.getProgresso(conquista) + "/" + sistema.getMeta(conquista), (int) no.getCenterX(), no.y + 80);
        }
    }

    private void centralizar(Graphics2D g, String texto, int centro, int y) { g.drawString(texto, centro - g.getFontMetrics().stringWidth(texto) / 2, y); }
}
