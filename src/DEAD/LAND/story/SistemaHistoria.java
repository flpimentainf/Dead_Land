package DEAD.LAND.story;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Toolkit;

public class SistemaHistoria {
    public enum Estado {
        QUARTO,
        QUEDA,
        FLORESTA,
        FALHA,
        MEMORIAS,
        ESCOLHA_FINAL,
        FINAL_ACORDAR,
        FINAL_FICAR,
        FINAL_SECRETO
    }

    private Estado estado = Estado.QUARTO;
    private String objetivoAtual = "Deite-se na cama.";
    private List<String> mensagem = new ArrayList<String>();
    private boolean exibindoMensagem = true;
    private boolean exibindoEscolha;
    private int opcaoEscolhida;
    private int tentativasMenu;
    private boolean teclaConfirmarPressionada;
    private boolean teclaMenuPressionada;
    private boolean teclaCimaPressionada;
    private boolean teclaBaixoPressionada;
    private boolean telaEscura;

    public SistemaHistoria() {
        mostrarMensagem(
                "Você está no seu quarto.",
                "O silêncio parece mais pesado do que deveria.",
                "Seu corpo está cansado... mas sua mente continua inquieta.",
                "Talvez dormir seja a única coisa que ainda faça sentido."
        );
    }

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        tratarMenu(teclado);

        if (exibindoEscolha) {
            tratarEscolha(teclado, cenaDoJogo);
            return;
        }

        if (exibindoMensagem && teclado.confirmar && !teclaConfirmarPressionada) {
            exibindoMensagem = false;
            mensagem.clear();
        }

        teclaConfirmarPressionada = teclado.confirmar;
        teclaCimaPressionada = teclado.movePraCima;
        teclaBaixoPressionada = teclado.movePraBaixo;
    }

    private void tratarMenu(escutadorTeclado teclado) {
        if (teclado.abrirMenu && !teclaMenuPressionada) {
            tentativasMenu++;

            if (estado == Estado.ESCOLHA_FINAL && tentativasMenu >= 3) {
                iniciarFinalSecreto();
            } else if (estado != Estado.FINAL_ACORDAR && estado != Estado.FINAL_FICAR && estado != Estado.FINAL_SECRETO) {
                mostrarMensagem(
                        "Sistema",
                        "Menu indisponível.",
                        "Botão de logout não encontrado.",
                        "Complete as missões principais para retornar."
                );
            }
        }
        teclaMenuPressionada = teclado.abrirMenu;
    }

    private void tratarEscolha(escutadorTeclado teclado, panel cenaDoJogo) {
        if (teclado.movePraCima && !teclaCimaPressionada) {
            opcaoEscolhida = 0;
        }
        if (teclado.movePraBaixo && !teclaBaixoPressionada) {
            opcaoEscolhida = 1;
        }

        if (teclado.confirmar && !teclaConfirmarPressionada) {
            if (opcaoEscolhida == 0) {
                iniciarFinalAcordar();
            } else {
                iniciarFinalFicar(cenaDoJogo);
            }
        }

        teclaConfirmarPressionada = teclado.confirmar;
        teclaCimaPressionada = teclado.movePraCima;
        teclaBaixoPressionada = teclado.movePraBaixo;
    }

    public boolean bloqueiaControleDoJogador() {
        return exibindoMensagem || exibindoEscolha
                || estado == Estado.FINAL_ACORDAR
                || estado == Estado.FINAL_FICAR
                || estado == Estado.FINAL_SECRETO;
    }

    public void eventoDormir() {
        estado = Estado.QUEDA;
        objetivoAtual = "Sobreviva à queda.";
        telaEscura = true;
        tocarSomEstranhoBasico();

        mostrarMensagem(
                "Você fecha os olhos.",
                "Por um instante, tudo fica escuro.",
                "Mas então... você começa a cair."
        );
    }

    public void eventoQuedaConcluida() {
        if (estado != Estado.QUEDA) return;

        estado = Estado.FLORESTA;
        objetivoAtual = "Explore a floresta e procure uma saída.";
        telaEscura = false;

        mostrarMensagem(
                "Você acorda antes de tocar o chão.",
                "O céu está errado.",
                "A floresta respira como se estivesse viva.",
                "Uma mensagem aparece diante dos seus olhos:",
                "Complete as missões principais para retornar."
        );
    }

    public void eventoEntrouCenario(int indiceCenario, panel cenaDoJogo) {
        if (estado == Estado.FINAL_SECRETO || estado == Estado.FINAL_ACORDAR || estado == Estado.FINAL_FICAR) return;

        if (indiceCenario == 3 && estado == Estado.FLORESTA) {
            estado = Estado.FALHA;
            objetivoAtual = "Fale com alguém que ainda lembra.";
            mostrarMensagem(
                    "NPC",
                    "Você não deveria estar andando.",
                    "Você já tentou acordar?"
            );
            return;
        }

        if (indiceCenario == 6 && estado != Estado.MEMORIAS) {
            objetivoAtual = "Encontre a chave e recupere suas memórias.";
            mostrarMensagem(
                    "Sistema corrompido",
                    "Missão atualizada.",
                    "Recupere a CHAVE DA MEMÓRIA."
            );
            return;
        }

        if (indiceCenario == 7) {
            if (cenaDoJogo.getInventario().temItem("chave")) {
                iniciarEscolhaFinal();
            } else {
                objetivoAtual = "A saída está trancada. Procure a chave.";
                mostrarMensagem(
                        "Portão morto",
                        "A saída reconhece você...",
                        "mas falta uma memória para abrir."
                );
            }
        }
    }

    public void eventoItemColetado(String nome) {
        if (nome == null || nome.isBlank()) return;

        if ("chave".equals(nome)) {
            estado = Estado.MEMORIAS;
            objetivoAtual = "Vá até o fim da floresta.";
            mostrarMensagem(
                    "Memória recuperada",
                    "Vidro quebrado.",
                    "Sirene distante.",
                    "Uma voz gritando seu nome."
            );
            return;
        }

        if ("arco".equals(nome)) {
            mostrarMensagem(
                    "Item obtido",
                    "Arco encontrado.",
                    "Às vezes lutar é só uma forma de negar o medo."
            );
            return;
        }

        if ("flecha".equals(nome)) {
            mostrarMensagem(
                    "Item obtido",
                    "Flecha encontrada.",
                    "O caminho final está mais perto."
            );
        }
    }

    public boolean eventoPorta(int cenarioAtual, panel cenaDoJogo) {
        if (cenarioAtual == 0) {
            mostrarMensagem(
                    "Porta",
                    "A maçaneta está fria.",
                    "Você tenta sair, mas a porta não reconhece este mundo."
            );
            return true;
        }

        if (cenarioAtual >= 7) {
            if (cenaDoJogo.getInventario().temItem("chave")) {
                iniciarEscolhaFinal();
            } else {
                mostrarMensagem("Porta", "A saída está trancada.", "Falta a chave da memória.");
            }
            return true;
        }

        return false;
    }

    private void iniciarEscolhaFinal() {
        estado = Estado.ESCOLHA_FINAL;
        objetivoAtual = "Escolha o final.";
        exibindoEscolha = true;
        exibindoMensagem = false;
        mensagem = Arrays.asList(
                "Memória recuperada.",
                "Carro quebrado.",
                "Ambulância.",
                "Cama de hospital.",
                "Você está entre acordar e permanecer."
        );
    }

    private void iniciarFinalAcordar() {
        exibindoEscolha = false;
        estado = Estado.FINAL_ACORDAR;
        objetivoAtual = "Fim.";
        mostrarMensagem(
                "Final: ACORDAR",
                "A tela fica branca.",
                "Bip... bip... bip...",
                "Ele abriu os olhos.",
                "Alguém ao lado da cama sussurra: você voltou."
        );
    }

    private void iniciarFinalFicar(panel cenaDoJogo) {
        exibindoEscolha = false;
        estado = Estado.FINAL_FICAR;
        objetivoAtual = "Fim.";
        cenaDoJogo.irParaCenario(2, 745, 335);
        mostrarMensagem(
                "Final: FICAR",
                "A floresta volta a ficar bonita.",
                "Os NPCs sorriem como se nada tivesse acontecido.",
                "No mundo real, ele nunca acordou.",
                "Ele escolheu viver onde se sentia importante."
        );
    }

    private void iniciarFinalSecreto() {
        exibindoEscolha = false;
        estado = Estado.FINAL_SECRETO;
        objetivoAtual = "Loop reiniciado.";
        mostrarMensagem(
                "Erro...",
                "Consciência ainda presa.",
                "Você acorda de novo na floresta.",
                "Um NPC diz: você já tentou isso antes.",
                "DEAD LAND"
        );
    }

    private void mostrarMensagem(String... linhas) {
        this.mensagem = new ArrayList<String>(Arrays.asList(linhas));
        this.exibindoMensagem = true;
    }

    public void desenhar(Graphics2D g2, int largura, int altura) {
        if (telaEscura) {
            desenharEscurecimento(g2, largura, altura);
        }

        desenharObjetivo(g2, largura);

        if (exibindoEscolha) {
            desenharEscolha(g2, largura, altura);
            return;
        }

        if (exibindoMensagem) {
            desenharCaixaTexto(g2, largura, altura, mensagem, "ENTER/E para continuar");
        }
    }

    private void desenharEscurecimento(Graphics2D g2, int largura, int altura) {
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, largura, altura);
    }

    private void desenharObjetivo(Graphics2D g2, int largura) {
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(20, 18, Math.min(largura - 40, 560), 34, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawString("Missão: " + objetivoAtual, 34, 41);
    }

    private void desenharEscolha(Graphics2D g2, int largura, int altura) {
        List<String> linhas = new ArrayList<String>(mensagem);
        linhas.add("");
        linhas.add((opcaoEscolhida == 0 ? "> " : "  ") + "Acordar");
        linhas.add((opcaoEscolhida == 1 ? "> " : "  ") + "Ficar no jogo");
        desenharCaixaTexto(g2, largura, altura, linhas, "↑/↓ escolhe | ENTER/E confirma | ESC 3x: segredo");
    }

    private void desenharCaixaTexto(Graphics2D g2, int largura, int altura, List<String> linhas, String rodape) {
        int caixaX = 40;
        int caixaLargura = largura - 80;
        int caixaAltura = 190;
        int caixaY = altura - caixaAltura - 34;

        g2.setColor(new Color(0, 0, 0, 205));
        g2.fillRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 18, 18);
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 18, 18);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int y = caixaY + 32;
        for (String linha : linhas) {
            g2.drawString(linha, caixaX + 24, y);
            y += fm.getHeight() + 2;
        }

        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(rodape, caixaX + 24, caixaY + caixaAltura - 16);
    }

    private void tocarSomEstranhoBasico() {
    // Som simples para não depender de arquivo de áudio.
    // Depois você pode trocar por um efeito .wav.
    Toolkit.getDefaultToolkit().beep();
    }
}
