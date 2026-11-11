package DEAD.LAND.ui;

import DEAD.LAND.story.SistemaHistoria;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.function.LongSupplier;

/** Relógio de tempo visível: diálogos e pausas não consomem a notificação. */
public final class NotificacoesObjetivos {
    public static final long DURACAO_MS = 3500;
    private final ArrayDeque<SistemaHistoria.AvisoObjetivo> fila = new ArrayDeque<>();
    private final LongSupplier relogio;
    private long ultimo;
    private long restante;

    public NotificacoesObjetivos() { this(() -> System.nanoTime() / 1_000_000); }
    public NotificacoesObjetivos(LongSupplier relogio) { this.relogio = relogio; ultimo = relogio.getAsLong(); }

    public void atualizar(SistemaHistoria historia, boolean visivel) {
        long agora = relogio.getAsLong();
        long delta = Math.max(0, Math.min(250, agora - ultimo));
        ultimo = agora;
        if (visivel && !fila.isEmpty()) {
            restante -= delta;
            if (restante <= 0) { fila.removeFirst(); restante = DURACAO_MS; }
        }
        SistemaHistoria.AvisoObjetivo aviso;
        while ((aviso = historia.consumirAvisoObjetivo()) != null) {
            if (fila.isEmpty()) restante = DURACAO_MS;
            if (fila.size() == 32) fila.removeFirst();
            fila.addLast(aviso);
        }
    }

    public SistemaHistoria.AvisoObjetivo getAtual() { return fila.peekFirst(); }
    public long getRestanteMs() { return restante; }
    public void limpar(SistemaHistoria historia) { fila.clear(); historia.limparAvisosObjetivos(); ultimo = relogio.getAsLong(); }

    public void desenhar(Graphics2D destino, int largura, int altura) {
        desenhar(destino, largura, altura, false);
    }

    public void desenhar(Graphics2D destino, int largura, int altura, boolean chefe) {
        if (fila.isEmpty()) return;
        Graphics2D g = (Graphics2D) destino.create();
        try {
            float entrada = Math.min(1f, (DURACAO_MS - restante) / 150f);
            float saida = Math.min(1f, restante / 250f);
            g.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(entrada, saida))));
            Rectangle area = LayoutHUD.avisoMissao(largura, chefe);
            int w = area.width, x = area.x, y = area.y;
            g.setFont(EstiloHUD.CORPO.deriveFont(11f));
            java.util.List<String> linhas = TextoHUD.quebrar(fila.peekFirst().descricao(), g.getFontMetrics(), w - 46);
            int quantidade = Math.min(2, linhas.size());
            g.setColor(new Color(10, 12, 16, 245)); g.fillRect(x, y, w, area.height);
            g.setColor(new Color(75, 75, 72)); g.drawRect(x, y, w - 1, area.height - 1);
            g.setColor(new Color(145, 136, 110)); g.drawRect(x + 2, y + 2, w - 5, area.height - 5);
            // Pequeno pergaminho em pixels, sem ampliar o card.
            g.setColor(new Color(112, 86, 45)); g.fillRect(x + 9, y + 14, 20, 24);
            g.setColor(EstiloHUD.DESTAQUE); g.fillRect(x + 11, y + 12, 16, 24);
            g.setColor(new Color(75, 58, 37));
            for (int i = 0; i < 3; i++) g.fillRect(x + 14, y + 18 + i * 5, 10, 1);
            g.setFont(EstiloHUD.TITULO.deriveFont(10f)); g.setColor(EstiloHUD.DESTAQUE);
            g.drawString(fila.peekFirst().titulo(), x + 36, y + 15);
            g.setFont(EstiloHUD.CORPO.deriveFont(11f)); g.setColor(EstiloHUD.TEXTO);
            for (int i = 0; i < quantidade; i++) {
                String linha = linhas.get(i);
                if (i == quantidade - 1 && linhas.size() > quantidade) linha = TextoHUD.resumir(linha, g.getFontMetrics(), w - 46);
                g.drawString(linha, x + 36, y + 30 + i * 13);
            }
        } finally { g.dispose(); }
    }
}
