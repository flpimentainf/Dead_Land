package DEAD.LAND.ui;

import java.awt.Rectangle;

/** Coordenadas lógicas e prioridade dos componentes, independentes da janela. */
public final class LayoutHUD {
    public enum Estado { EXPLORACAO, DIALOGO, CHEFE, PAUSA, MORTE, ENCERRAMENTO }

    private LayoutHUD() {}

    public static final int LARGURA = 768, ALTURA_JOGO = 480, ALTURA_RODAPE = 104;
    public record Inferior(Rectangle status, Rectangle inventario, int tamanhoSlot, int capacidade,
            Rectangle anterior, Rectangle proxima) {
        public Rectangle slot(int indice) {
            return new Rectangle(inventario.x + inventario.width - tamanhoSlot - indice * (tamanhoSlot + 12),
                    inventario.y, tamanhoSlot, tamanhoSlot);
        }
    }

    public static Inferior inferior(int largura, int altura) {
        int margem = 16;
        int statusW = Math.min(160, Math.max(64, largura / 4));
        Rectangle status = new Rectangle(margem, Math.max(18, (altura - 64) / 2), statusW, 64);
        int inicio = status.x + status.width + 24;
        Rectangle inventario = new Rectangle(inicio, Math.max(18, (altura - 68) / 2),
                Math.max(0, largura - inicio - margem), 64);
        int tamanho = Math.min(64, Math.max(32, inventario.width / 2 - 6));
        int capacidade = (inventario.width + 12) / (tamanho + 12);
        return new Inferior(status, inventario, tamanho, capacidade,
                new Rectangle(inicio, altura - 20, 24, 18), new Rectangle(largura - margem - 24, altura - 20, 24, 18));
    }

    public static Rectangle partida(int largura, int altura) {
        return new ViewportJogo(LARGURA, ALTURA_JOGO + ALTURA_RODAPE).getArea(largura, altura);
    }

    public static Estado estado(boolean pausa, boolean morte, boolean encerramento,
            boolean dialogo, boolean chefe) {
        if (morte) return Estado.MORTE;
        if (pausa) return Estado.PAUSA;
        if (encerramento) return Estado.ENCERRAMENTO;
        if (dialogo) return Estado.DIALOGO;
        return chefe ? Estado.CHEFE : Estado.EXPLORACAO;
    }

    public static Rectangle chefe(int largura) {
        int w = Math.min(536, largura - 40);
        return new Rectangle((largura - w) / 2, 12, w, 58);
    }

    public static boolean mostrarObjetivos(Estado estado) { return estado == Estado.EXPLORACAO; }
    public static boolean mostrarAvisos(Estado estado) { return estado == Estado.EXPLORACAO || estado == Estado.CHEFE; }
}
