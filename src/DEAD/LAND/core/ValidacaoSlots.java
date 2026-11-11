package DEAD.LAND.core;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.moldura;
import DEAD.LAND.ui.panel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Exercita o fluxo real dos menus usando apenas saves temporários. */
public class ValidacaoSlots {
    public static void main(String[] args) throws Exception {
        File pasta = Files.createTempDirectory("dead-land-slots-").toFile();
        SaveManager saves = new SaveManager(pasta);
        try {
            GameState completo = new GameState();
            completo.slot = 1;
            completo.estadoHistoria = "FINAL_ACORDAR";
            completo.bossDerrotado = true;
            exigir(saves.salvar(completo), "preparar partida completa");
            byte[] original = ler(pasta, 1);
            SwingUtilities.invokeAndWait(() -> validarMenus(saves, pasta, original));
            exigir(!saves.apagarSlot(0) && !saves.apagarSlot(4), "rejeitar slots inválidos");
            // O arquivo escolhido determina o slot, mesmo em saves antigos inconsistentes.
            Files.writeString(new File(pasta, "slot3.properties").toPath(), "slot=1\n");
            exigir(saves.carregar(3).orElseThrow().slot == 3, "carregar no slot escolhido");
            System.out.println("Validacoes de slots OK");
        } finally {
            for (int slot = 1; slot <= SaveManager.TOTAL_SLOTS; slot++) {
                Files.deleteIfExists(new File(pasta, "slot" + slot + ".properties").toPath());
            }
            Files.deleteIfExists(pasta.toPath());
        }
    }

    private static void validarMenus(SaveManager saves, File pasta, byte[] original) {
        moldura janela = new moldura(saves);
        try {
            exigir(botoes(janela, "CONQUISTAS").isEmpty(), "painel de conquistas não deve estar disponível fora da partida");
            clicarBotao(janela, "NOVO JOGO", 0);
            exigir(botoes(janela, "NOVO JOGO").size() == 2, "dois slots livres disponíveis");
            clicarBotao(janela, "NOVO JOGO", 0); // Slot 2.
            clicarBotao(janela, "VOLTAR", 0); // Cancelar antes de começar.
            exigir(!saves.existeSave(2), "cancelar introdução não deve criar save");
            exigir(Arrays.equals(original, ler(pasta, 1)), "selecionar slot preserva partida completa");
            clicarBotao(janela, "NOVO JOGO", 0);
            comecar(janela);
            panel cena = cena(janela);
            cena.pausarJogo();
            exigir(saves.carregar(2).orElseThrow().slot == 2, "nova partida nasce no slot 2");
            cena.getJogador().posicionarEm(300, 200);
            abrirPausa(cena);
            clicarPausa(cena, 202); // Salvar manualmente.
            exigir(saves.carregar(2).orElseThrow().jogadorX == 300, "pausa salva no slot ativo");
            cena.getJogador().posicionarEm(350, 200);
            cena.salvarAutoForcado();
            exigir(saves.carregar(2).orElseThrow().jogadorX == 350, "autosave usa slot ativo");
            clicarPausa(cena, 334); // Menu principal, com save.
            clicarBotao(janela, "NOVO JOGO", 0);
            clicarBotao(janela, "NOVO JOGO", 0); // Único livre: slot 3.
            comecar(janela);
            cena = cena(janela);
            cena.pausarJogo();
            abrirPausa(cena);
            clicarPausa(cena, 334);
            exigir(saves.existeSave(1) && saves.existeSave(2) && saves.existeSave(3), "três partidas independentes");
            clicarBotao(janela, "PARTIDAS SALVAS", 0);
            exigir(botoes(janela, "REINICIAR").size() == 3, "slots cheios permitem gerenciamento");
            responderDialogo(janela, "REINICIAR", 0, JOptionPane.NO_OPTION);
            exigir(Arrays.equals(original, ler(pasta, 1)), "cancelar reinício preserva save");
            responderDialogo(janela, "REINICIAR", 1, JOptionPane.YES_OPTION);
            comecar(janela);
            cena = cena(janela);
            cena.pausarJogo();
            exigir(saves.carregar(2).orElseThrow().jogadorX != 350, "reinício limpa somente a partida escolhida");
            abrirPausa(cena);
            clicarPausa(cena, 334);
            clicarBotao(janela, "PARTIDAS SALVAS", 0);
            clicarBotao(janela, "CARREGAR", 2);
            cena = cena(janela);
            cena.pausarJogo();
            cena.getJogador().posicionarEm(450, 200);
            abrirPausa(cena);
            clicarPausa(cena, 202);
            exigir(saves.carregar(3).orElseThrow().jogadorX == 450, "carregar e salvar mantém slot 3");
            clicarPausa(cena, 334);
            clicarBotao(janela, "CONTINUAR", 0);
            cena = cena(janela);
            cena.pausarJogo();
            exigir(cena.getJogador().x == 450, "continuar abre partida mais recente");
            abrirPausa(cena);
            clicarPausa(cena, 334);
            clicarBotao(janela, "PARTIDAS SALVAS", 0);
            byte[] terceiro = ler(pasta, 3);
            responderDialogo(janela, "APAGAR", 1, JOptionPane.NO_OPTION);
            exigir(saves.existeSave(2), "cancelar exclusão preserva slot");
            responderDialogo(janela, "APAGAR", 1, JOptionPane.YES_OPTION);
            exigir(!saves.existeSave(2), "excluir libera somente slot 2");
            exigir(Arrays.equals(original, ler(pasta, 1)), "partida completa permanece byte a byte intacta");
            exigir(Arrays.equals(terceiro, ler(pasta, 3)), "exclusão preserva slot 3");
        } finally {
            for (Component componente : descendentes(janela)) {
                if (componente instanceof panel painel) painel.pausarJogo();
            }
            janela.dispose();
        }
    }

    private static void comecar(moldura janela) {
        clicarBotao(janela, "CONTINUAR", 0);
        clicarBotao(janela, "CONTINUAR", 0);
        clicarBotao(janela, "COMEÇAR", 0);
    }

    private static void abrirPausa(panel cena) {
        cena.setSize(768, 480);
        escutadorTeclado teclado = new escutadorTeclado();
        teclado.abrirMenu = true;
        cena.atualizarMenuPausa(teclado);
        teclado.abrirMenu = false;
        cena.atualizarMenuPausa(teclado);
    }

    private static void clicarPausa(panel cena, int y) {
        cena.dispatchEvent(new MouseEvent(cena, MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(), 0, 384, y, 1, false, MouseEvent.BUTTON1));
    }

    private static void responderDialogo(moldura janela, String texto, int indice, int resposta) {
        Timer timer = new Timer(100, e -> {
            for (Window window : Window.getWindows()) {
                if (!window.isShowing() || window == janela) continue;
                for (Component componente : descendentes(window)) {
                    if (componente instanceof JOptionPane opcao) opcao.setValue(resposta);
                }
            }
        });
        timer.start();
        try {
            clicarBotao(janela, texto, indice);
        } finally {
            timer.stop();
        }
    }

    private static panel cena(moldura janela) {
        return descendentes(janela).stream().filter(c -> c instanceof panel p && p.getCenario() != null)
                .map(c -> (panel) c).findFirst().orElseThrow();
    }

    private static List<JButton> botoes(moldura janela, String texto) {
        return descendentes(janela).stream().filter(c -> c.isShowing() && c instanceof JButton b
                && texto.equals(b.getText())).map(c -> (JButton) c).toList();
    }

    private static void clicarBotao(moldura janela, String texto, int indice) {
        JButton botao = botoes(janela, texto).get(indice);
        exigir(botao.isEnabled(), "botão disponível: " + texto);
        botao.doClick(0);
    }

    private static List<Component> descendentes(Container raiz) {
        List<Component> resultado = new ArrayList<>();
        for (Component componente : raiz.getComponents()) {
            resultado.add(componente);
            if (componente instanceof Container filho) resultado.addAll(descendentes(filho));
        }
        return resultado;
    }

    private static byte[] ler(File pasta, int slot) {
        try {
            return Files.readAllBytes(new File(pasta, "slot" + slot + ".properties").toPath());
        } catch (java.io.IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalStateException(mensagem);
    }
}
