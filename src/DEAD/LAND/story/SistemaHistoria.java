package DEAD.LAND.story;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SistemaHistoria {
    public enum Estado {
        QUARTO,
        QUEDA,
        FLORESTA,
        FALHA,
        BUSCA_MEMORIA,
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
        mostrarMensagem(RoteiroHistoria.abertura());
    }

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        tratarMenu(teclado);

        if (exibindoEscolha) {
            tratarEscolha(teclado, cenaDoJogo);
            return;
        }

        if (exibindoMensagem && teclado.confirmar && !teclaConfirmarPressionada) {
            fecharMensagem();
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
            } else if (!historiaFinalizada()) {
                mostrarMensagem(RoteiroHistoria.menuBloqueado());
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
        return exibindoMensagem || exibindoEscolha || historiaFinalizada();
    }

    private boolean historiaFinalizada() {
        return estado == Estado.FINAL_ACORDAR
                || estado == Estado.FINAL_FICAR
                || estado == Estado.FINAL_SECRETO;
    }

    public void eventoDormir() {
        estado = Estado.QUEDA;
        objetivoAtual = "Sobreviva à queda.";
        telaEscura = true;
        tocarSomEstranhoBasico();
        mostrarMensagem(RoteiroHistoria.dormir());
    }

    public void eventoQuedaConcluida() {
        if (estado != Estado.QUEDA) return;

        estado = Estado.FLORESTA;
        objetivoAtual = "Explore a floresta e procure uma saída.";
        telaEscura = false;
        mostrarMensagem(RoteiroHistoria.quedaConcluida());
    }

    public void eventoEntrouCenario(int indiceCenario, panel cenaDoJogo) {
        if (historiaFinalizada()) return;

        if (indiceCenario == 3 && estado == Estado.FLORESTA) {
            estado = Estado.FALHA;
            objetivoAtual = "Fale com alguém que ainda lembra.";
            mostrarMensagem(RoteiroHistoria.npcLembra());
            return;
        }

        if (indiceCenario == 6 && estado != Estado.BUSCA_MEMORIA && estado != Estado.MEMORIAS) {
            estado = Estado.BUSCA_MEMORIA;
            objetivoAtual = "Encontre a chave e recupere suas memórias.";
            mostrarMensagem(RoteiroHistoria.missaoMemoria());
            return;
        }

        if (indiceCenario == 7) {
            if (cenaDoJogo.getInventario().temItem("chave")) {
                iniciarEscolhaFinal();
            } else {
                objetivoAtual = "A saída está trancada. Procure a chave.";
                mostrarMensagem(RoteiroHistoria.portaoSemMemoria());
            }
        }
    }

    public void eventoItemColetado(String nome) {
        if (nome == null || nome.isBlank()) return;

        if ("chave".equals(nome)) {
            estado = Estado.MEMORIAS;
            objetivoAtual = "Vá até o fim da floresta.";
            mostrarMensagem(RoteiroHistoria.chaveColetada());
            return;
        }

        if ("arco".equals(nome)) {
            mostrarMensagem(RoteiroHistoria.arcoColetado());
            return;
        }

        if ("flecha".equals(nome)) {
            mostrarMensagem(RoteiroHistoria.flechaColetada());
        }
    }

    public boolean eventoPorta(int cenarioAtual, panel cenaDoJogo) {
        if (cenarioAtual == 0) {
            mostrarMensagem(RoteiroHistoria.portaQuartoTrancada());
            return true;
        }

        if (cenarioAtual >= 7) {
            if (cenaDoJogo.getInventario().temItem("chave")) {
                iniciarEscolhaFinal();
            } else {
                mostrarMensagem(RoteiroHistoria.saidaTrancada());
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
        opcaoEscolhida = 0;
        mostrarMensagemSemAbrirCaixa(RoteiroHistoria.escolhaFinal());
    }

    private void iniciarFinalAcordar() {
        exibindoEscolha = false;
        estado = Estado.FINAL_ACORDAR;
        objetivoAtual = "Fim.";
        mostrarMensagem(RoteiroHistoria.finalAcordar());
    }

    private void iniciarFinalFicar(panel cenaDoJogo) {
        exibindoEscolha = false;
        estado = Estado.FINAL_FICAR;
        objetivoAtual = "Fim.";
        cenaDoJogo.irParaCenario(2, 745, 335);
        mostrarMensagem(RoteiroHistoria.finalFicar());
    }

    private void iniciarFinalSecreto() {
        exibindoEscolha = false;
        estado = Estado.FINAL_SECRETO;
        objetivoAtual = "Loop reiniciado.";
        mostrarMensagem(RoteiroHistoria.finalSecreto());
    }

    private void mostrarMensagem(String... linhas) {
        this.mensagem = new ArrayList<String>(Arrays.asList(linhas));
        this.exibindoMensagem = true;
    }

    private void mostrarMensagemSemAbrirCaixa(String... linhas) {
        this.mensagem = new ArrayList<String>(Arrays.asList(linhas));
    }

    private void fecharMensagem() {
        this.exibindoMensagem = false;
        this.mensagem.clear();
    }

    public Estado getEstado() {
        return estado;
    }

    public String getObjetivoAtual() {
        return objetivoAtual;
    }

    public List<String> getMensagem() {
        return Collections.unmodifiableList(mensagem);
    }

    public boolean isExibindoMensagem() {
        return exibindoMensagem;
    }

    public boolean isExibindoEscolha() {
        return exibindoEscolha;
    }

    public int getOpcaoEscolhida() {
        return opcaoEscolhida;
    }

    public boolean isTelaEscura() {
        return telaEscura;
    }

    private void tocarSomEstranhoBasico() {
        //Adicionar so wav ou waw esqueci qual e o arquivo
        Toolkit.getDefaultToolkit().beep();
    }
}
