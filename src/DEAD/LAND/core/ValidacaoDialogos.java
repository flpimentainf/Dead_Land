package DEAD.LAND.core;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.Inventario;
import DEAD.LAND.ui.panel;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Set;

final class ValidacaoDialogos {
    static void validar() {
        for (String npc : new String[]{"ari", "mara", "guardiao_memoria", "porteiro_morto"}) {
            panel cena = new panel("centro", new Inventario());
            try {
                cena.pausarJogo();
                cena.setSize(768, 480);
                var historia = cena.getHistoria();
                historia.restaurarEstadoPersistido("FLORESTA", "Explore.", Set.of(npc), false);
                historia.eventoFalarComNpc(npc, cena);
                pintar(cena);
                ValidacaoHUD.exigir(historia.isExibindoEscolha(), "NPC conhecido deve oferecer escolhas: " + npc);
                escutadorTeclado teclado = new escutadorTeclado();
                pressionar(cena, teclado, KeyEvent.VK_S);
                ValidacaoHUD.exigir(historia.getOpcaoEscolhida() == 1, "S deve escolher a segunda resposta, inclusive antes de avancar paginas: " + npc);
                soltar(cena, teclado, KeyEvent.VK_S);
                pressionar(cena, teclado, KeyEvent.VK_W);
                ValidacaoHUD.exigir(historia.getOpcaoEscolhida() == 0, "W deve retornar para a primeira resposta: " + npc);
                soltar(cena, teclado, KeyEvent.VK_W);
                pressionar(cena, teclado, KeyEvent.VK_S);
                soltar(cena, teclado, KeyEvent.VK_S);
                int paginas = 0;
                while (cena.temProximaPaginaDialogo()) {
                    ValidacaoHUD.exigir(++paginas < 20, "paginacao da escolha deve terminar");
                    pressionar(cena, teclado, KeyEvent.VK_ENTER);
                    ValidacaoHUD.exigir(historia.isExibindoEscolha(), "ENTER deve avancar sem confirmar antes da ultima pagina");
                    ValidacaoHUD.exigir(historia.getOpcaoEscolhida() == 1, "trocar pagina preserva a resposta selecionada");
                    // Segurar ENTER nao deve confirmar ao chegar na ultima pagina.
                    historia.atualizar(cena, teclado);
                    ValidacaoHUD.exigir(historia.isExibindoEscolha(), "ENTER segurado nao confirma duas vezes");
                    soltar(cena, teclado, KeyEvent.VK_ENTER);
                }
                pressionar(cena, teclado, KeyEvent.VK_ENTER);
                ValidacaoHUD.exigir(!historia.isExibindoEscolha() && historia.isExibindoMensagem(), "ENTER deve abrir a resposta selecionada");
                String[] esperado = switch (npc) {
                    case "ari" -> DEAD.LAND.story.RoteiroHistoria.respostaAriMissao(false);
                    case "mara" -> DEAD.LAND.story.RoteiroHistoria.respostaMaraChaves(false, false);
                    case "guardiao_memoria" -> DEAD.LAND.story.RoteiroHistoria.respostaGuardiaoMissao(false, false);
                    default -> DEAD.LAND.story.RoteiroHistoria.respostaPorteiroFalta(false);
                };
                ValidacaoHUD.exigir(historia.getMensagem().equals(java.util.List.of(esperado)), "deve executar a segunda resposta de " + npc);
            } finally { cena.pausarJogo(); }
        }
    }

    private static void pressionar(panel cena, escutadorTeclado teclado, int tecla) {
        teclado.keyPressed(new KeyEvent(cena, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, tecla, KeyEvent.CHAR_UNDEFINED));
        cena.getHistoria().atualizar(cena, teclado);
        pintar(cena);
    }

    private static void soltar(panel cena, escutadorTeclado teclado, int tecla) {
        teclado.keyReleased(new KeyEvent(cena, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, tecla, KeyEvent.CHAR_UNDEFINED));
        cena.getHistoria().atualizar(cena, teclado);
        pintar(cena);
    }

    private static void pintar(panel cena) {
        BufferedImage imagem = new BufferedImage(768, 480, BufferedImage.TYPE_INT_RGB);
        var g = imagem.createGraphics();
        try { cena.paint(g); } finally { g.dispose(); }
    }
}
