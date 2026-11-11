package DEAD.LAND.core;

import DEAD.LAND.ui.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

public final class ValidacaoHUD {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(ValidacaoHUD::validarPrioridades);
        System.out.println("Validacoes do HUD OK");
    }

    private static void validarPrioridades() {
        panel cena = new panel("centro", new Inventario());
        try {
            cena.pausarJogo();
            GameState state = cena.criarGameState(1);
            state.cenarioIndex = 7;
            state.estadoHistoria = "MEMORIAS";
            cena.aplicarGameState(state);
            exigir(cena.getEstadoHUD() == LayoutHUD.Estado.CHEFE, "arena deve priorizar chefe");
            exigir(!LayoutHUD.mostrarObjetivos(cena.getEstadoHUD()), "objetivos ocultos no combate");
            cena.getControladorInimigos().definirBossDerrotado(true);
            exigir(cena.getEstadoHUD() == LayoutHUD.Estado.EXPLORACAO, "objetivos retornam após derrota");
            exigir(LayoutHUD.mostrarObjetivos(cena.getEstadoHUD()), "exploração mostra objetivo");
            exigir(LayoutHUD.estado(true, false, false, false, true) == LayoutHUD.Estado.PAUSA, "pausa tem prioridade");
            exigir(LayoutHUD.estado(false, true, false, true, true) == LayoutHUD.Estado.MORTE, "morte tem prioridade");
            Rectangle boss = LayoutHUD.chefe(768);
            exigir(new Rectangle(0, 0, 768, 480).contains(boss), "barra do chefe dentro da tela");
        } finally { cena.pausarJogo(); }
    }

    static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalStateException(mensagem);
    }
}
