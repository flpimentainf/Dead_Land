package DEAD.LAND.core;

import DEAD.LAND.ui.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

public final class ValidacaoHUD {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(ValidacaoHUD::validarPrioridades);
        validarTextoEMissoes();
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

    private static void validarTextoEMissoes() {
        BufferedImage imagem = new BufferedImage(768, 480, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            String extenso = "Encontre a chave na floresta e atravesse a passagem. ".repeat(12) + "X".repeat(100);
            for (String linha : TextoHUD.quebrar(extenso, g.getFontMetrics(), 276)) {
                exigir(g.getFontMetrics().stringWidth(linha) <= 276, "texto deve respeitar largura por FontMetrics");
            }
            var historia = new DEAD.LAND.story.SistemaHistoria();
            long[] agora = {0};
            NotificacoesObjetivos avisos = new NotificacoesObjetivos(() -> agora[0]);
            avisos.atualizar(historia, true);
            exigir("Novo objetivo".equals(avisos.getAtual().titulo()), "objetivo inicial deve notificar");
            agora[0] = 1000;
            avisos.atualizar(historia, false);
            exigir(avisos.getRestanteMs() == 3500, "diálogo não deve consumir aviso");
            avisos.limpar(historia);
            historia.eventoDormir();
            avisos.atualizar(historia, false);
            exigir("Objetivo concluído".equals(avisos.getAtual().titulo()), "concluir etapa notifica");
            for (int i = 0; i < 14; i++) { agora[0] += 250; avisos.atualizar(historia, true); }
            exigir("Novo objetivo".equals(avisos.getAtual().titulo()), "novo objetivo segue conclusão");
            historia.restaurarEstadoPersistido("MEMORIAS", extenso, "Ajuda do Cacador concluida.",
                    java.util.Set.of(), java.util.Set.of("cacador_recompensa"), false);
            exigir(historia.isObjetivoSecundarioConcluido(), "estado secundário usa evento existente");
            exigir(InterfaceHistoria.alturaObjetivos(g, historia) <= 146, "objetivo compacto tem altura máxima");
            var longa = new DEAD.LAND.story.SistemaHistoria() {
                @Override public java.util.List<String> getMensagem() { return java.util.List.of(extenso.repeat(4)); }
            };
            InterfaceHistoria interfaceLonga = new InterfaceHistoria();
            interfaceLonga.desenhar(g, 768, 480, longa, false);
            exigir(interfaceLonga.temProximaPagina(longa), "diálogo extenso deve ser paginado");
            int paginas = 1;
            while (interfaceLonga.avancarPagina(longa)) {
                interfaceLonga.desenhar(g, 768, 480, longa, false);
                exigir(++paginas < 100, "paginação deve terminar");
            }
            exigir(paginas > 1 && longa.isExibindoMensagem(), "páginas não encerram o diálogo");
        } finally { g.dispose(); }
    }
}
