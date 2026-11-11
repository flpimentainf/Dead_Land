package DEAD.LAND.ui;

import java.awt.Rectangle;

/** Coordenadas lógicas e prioridade dos componentes, independentes da janela. */
public final class LayoutHUD {
    public enum Estado { EXPLORACAO, DIALOGO, CHEFE, PAUSA, MORTE, ENCERRAMENTO }

    private LayoutHUD() {}

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
