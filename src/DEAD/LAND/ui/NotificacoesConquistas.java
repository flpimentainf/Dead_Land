package DEAD.LAND.ui;

import DEAD.LAND.core.SistemaConquistas;
import DEAD.LAND.core.SistemaConquistas.Conquista;
import java.awt.*;
import java.util.function.LongSupplier;

public final class NotificacoesConquistas {
    private final LongSupplier relogio;
    private long ultimo, restante;
    private Conquista atual;
    public NotificacoesConquistas() { this(() -> System.nanoTime() / 1_000_000); }
    public NotificacoesConquistas(LongSupplier relogio) { this.relogio = relogio; ultimo = relogio.getAsLong(); }
    public void atualizar(SistemaConquistas sistema, boolean visivel) {
        long agora = relogio.getAsLong(), delta = Math.max(0, Math.min(250, agora - ultimo));
        ultimo = agora;
        if (atual != null && visivel) {
            restante -= delta;
            if (restante <= 0) atual = null;
        }
        if (atual == null) { atual = sistema.consumirNova(); restante = 3500; }
    }
    public boolean visivel() { return atual != null; }
    public Conquista getAtual() { return atual; }
    public long getRestanteMs() { return restante; }
    public void limpar() { atual = null; restante = 0; ultimo = relogio.getAsLong(); }
    public void desenhar(Graphics2D destino, int largura, boolean chefe) {
        if (atual == null) return;
        Graphics2D g = (Graphics2D) destino.create();
        try {
            float alpha = Math.max(0, Math.min(Math.min(1f, (3500 - restante) / 150f), Math.min(1f, restante / 250f)));
            g.setComposite(AlphaComposite.SrcOver.derive(alpha));
            Rectangle area = LayoutHUD.avisoMissao(largura, chefe);
            g.setColor(new Color(10, 12, 16, 245)); g.fillRect(area.x, area.y, area.width, area.height);
            g.setColor(EstiloHUD.DESTAQUE); g.drawRect(area.x, area.y, area.width - 1, area.height - 1);
            IconesConquistas.desenhar(g, atual, new Rectangle(area.x + 7, area.y + 11, 30, 30), true);
            g.setFont(EstiloHUD.TITULO.deriveFont(10f)); g.drawString("Conquista desbloqueada", area.x + 42, area.y + 18);
            g.setFont(EstiloHUD.CORPO.deriveFont(11f)); g.setColor(EstiloHUD.TEXTO);
            g.drawString(atual.titulo, area.x + 42, area.y + 36);
        } finally { g.dispose(); }
    }
}
