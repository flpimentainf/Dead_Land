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
        if (fila.isEmpty()) return;
        Graphics2D g = (Graphics2D) destino.create();
        try {
            int w = 320, x = largura - w - 20;
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            java.util.List<String> linhas = TextoHUD.quebrar(fila.peekFirst().descricao(), g.getFontMetrics(), w - 24);
            int quantidade = Math.min(3, linhas.size()), h = 46 + quantidade * 18, y = altura - h - 18;
            g.setColor(new Color(12, 14, 20, 235)); g.fillRect(x, y, w, h);
            g.setColor(new Color(182, 160, 100)); g.drawRect(x, y, w, h);
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.drawString(fila.peekFirst().titulo(), x + 12, y + 20);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13)); g.setColor(Color.WHITE);
            for (int i = 0; i < quantidade; i++) {
                String linha = linhas.get(i);
                if (i == quantidade - 1 && linhas.size() > quantidade) linha = TextoHUD.resumir(linha, g.getFontMetrics(), w - 24);
                g.drawString(linha, x + 12, y + 40 + i * 18);
            }
            if (linhas.size() > quantidade) { g.setColor(Color.LIGHT_GRAY); g.drawString("… ESC > OBJETIVOS", x + 12, y + h - 6); }
        } finally { g.dispose(); }
    }
}
