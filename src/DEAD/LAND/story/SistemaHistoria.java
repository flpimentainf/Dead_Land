package DEAD.LAND.story;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SistemaHistoria {
    private enum TipoEscolha {
        NENHUMA,
        FINAL,
        NPC
    }

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
    private final Set<String> npcsConhecidos = new HashSet<String>();
    private TipoEscolha tipoEscolha = TipoEscolha.NENHUMA;
    private String npcEscolhaId;
    private String nomeNpcFalando;
    private String opcaoNpc1 = "";
    private String opcaoNpc2 = "";
    private final Consumer<Estado> aoFinalizar;
    private boolean finalNotificado;
    private boolean confrontoBossApresentado;

    public SistemaHistoria() {
        this(null);
    }

    public SistemaHistoria(Consumer<Estado> aoFinalizar) {
        this.aoFinalizar = aoFinalizar;
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
            if (tipoEscolha == TipoEscolha.NPC) {
                concluirEscolhaNpc(cenaDoJogo);
            } else {
                if (opcaoEscolhida == 0) {
                    iniciarFinalAcordar();
                } else {
                    iniciarFinalFicar(cenaDoJogo);
                }
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
            objetivoAtual = "Encontre a chave prata.";
            mostrarMensagem(RoteiroHistoria.missaoMemoria());
            return;
        }

        if (indiceCenario == 7) {
            if (cenaDoJogo.getInventario().temChaveBoss63()) {
                objetivoAtual = "Derrote o chefe.";
                if (!confrontoBossApresentado) {
                    confrontoBossApresentado = true;
                    mostrarMensagem(RoteiroHistoria.antesDoBoss());
                }
            } else {
                objetivoAtual = "Encontre a chave vermelha 63.";
                mostrarMensagem(RoteiroHistoria.portaoSemMemoria());
            }
        }
    }


    public void eventoFalarComNpc(String npcId, panel cenaDoJogo) {
        if (historiaFinalizada() || npcId == null) return;

        this.nomeNpcFalando = nomeExibicaoNpc(npcId);

        if (npcsConhecidos.contains(npcId)) {
            iniciarEscolhaNpc(npcId);
            return;
        }

        npcsConhecidos.add(npcId);

        if ("ari".equals(npcId)) {
            if (estado == Estado.FLORESTA) {
                estado = Estado.FALHA;
            }
            objetivoAtual = "Procure primeiro a chave prata.";
            mostrarMensagem(RoteiroHistoria.npcAri());
            return;
        }

        if ("mara".equals(npcId)) {
            if (cenaDoJogo.getInventario().temChaveBoss63()) {
                mostrarMensagem(RoteiroHistoria.npcMaraComMemoria());
                return;
            }

            if (cenaDoJogo.getInventario().temChave()) {
                estado = Estado.BUSCA_MEMORIA;
                objetivoAtual = "Use a chave prata na porta inferior.";
                mostrarMensagem(RoteiroHistoria.npcMaraComChavePrata());
                return;
            }

            if (estado != Estado.MEMORIAS) {
                estado = Estado.BUSCA_MEMORIA;
                objetivoAtual = "Encontre a chave prata na área à esquerda.";
            }
            mostrarMensagem(RoteiroHistoria.npcMara());
            return;
        }

        if ("guardiao_memoria".equals(npcId)) {
            if (cenaDoJogo.getInventario().temChaveBoss63()) {
                mostrarMensagem(RoteiroHistoria.npcGuardiaoComMemoria());
                return;
            }

            if (cenaDoJogo.getInventario().temChave()) {
                estado = Estado.BUSCA_MEMORIA;
                objetivoAtual = "Volte e abra a porta inferior com a chave prata.";
                mostrarMensagem(RoteiroHistoria.npcGuardiaoComChavePrata());
                return;
            }

            if (estado != Estado.MEMORIAS) {
                estado = Estado.BUSCA_MEMORIA;
                objetivoAtual = "Encontre a chave prata nesta área.";
            }
            mostrarMensagem(RoteiroHistoria.npcGuardiaoMemoria());
            return;
        }

        if ("porteiro_morto".equals(npcId)) {
            if (cenaDoJogo != null && cenaDoJogo.getInventario().temChaveBoss63()) {
                iniciarEscolhaFinal(RoteiroHistoria.npcPorteiroComChave());
            } else {
                objetivoAtual = "Encontre a chave vermelha 63.";
                mostrarMensagem(RoteiroHistoria.npcPorteiroSemChave());
            }
            return;
        }

        mostrarMensagem(RoteiroHistoria.npcDesconhecido());
    }

    private void iniciarEscolhaNpc(String npcId) {
        this.npcEscolhaId = npcId;
        this.tipoEscolha = TipoEscolha.NPC;
        this.exibindoEscolha = true;
        this.exibindoMensagem = false;
        this.opcaoEscolhida = 0;

        if ("ari".equals(npcId)) {
            this.opcaoNpc1 = "Quem é você?";
            this.opcaoNpc2 = "O que devo fazer?";
        } else if ("mara".equals(npcId)) {
            this.opcaoNpc1 = "O que você ouve?";
            this.opcaoNpc2 = "Onde estão as chaves?";
        } else if ("guardiao_memoria".equals(npcId)) {
            this.opcaoNpc1 = "O que você guarda?";
            this.opcaoNpc2 = "Qual é minha missão?";
        } else {
            this.opcaoNpc1 = "O que existe além?";
            this.opcaoNpc2 = "O que ainda falta?";
        }

        mostrarMensagemSemAbrirCaixa(
                nomeNpcFalando,
                "Você já falou comigo.",
                "O que deseja perguntar?"
        );
    }

    private void concluirEscolhaNpc(panel cenaDoJogo) {
        String[] resposta;
        boolean segundaOpcao = opcaoEscolhida == 1;

        if ("ari".equals(npcEscolhaId)) {
            resposta = segundaOpcao
                    ? RoteiroHistoria.respostaAriMissao(cenaDoJogo.getInventario().temChaveBoss63())
                    : RoteiroHistoria.respostaAriIdentidade();
        } else if ("mara".equals(npcEscolhaId)) {
            resposta = segundaOpcao
                    ? RoteiroHistoria.respostaMaraChaves(
                            cenaDoJogo.getInventario().temChave(),
                            cenaDoJogo.getInventario().temChaveBoss63())
                    : RoteiroHistoria.respostaMaraEscuta();
        } else if ("guardiao_memoria".equals(npcEscolhaId)) {
            resposta = segundaOpcao
                    ? RoteiroHistoria.respostaGuardiaoMissao(
                            cenaDoJogo.getInventario().temChave(),
                            cenaDoJogo.getInventario().temChaveBoss63())
                    : RoteiroHistoria.respostaGuardiaoFuncao();
        } else {
            resposta = segundaOpcao
                    ? RoteiroHistoria.respostaPorteiroFalta(
                            cenaDoJogo.getInventario().temChaveBoss63())
                    : RoteiroHistoria.respostaPorteiroAlem();
        }

        this.exibindoEscolha = false;
        this.tipoEscolha = TipoEscolha.NENHUMA;
        mostrarMensagem(resposta);
    }

    public void eventoItemColetado(String nome) {
        if (nome == null || nome.isBlank()) return;

        if ("chave".equals(nome)) {
            estado = Estado.BUSCA_MEMORIA;
            objetivoAtual = "Abra a porta inferior e encontre a chave vermelha 63.";
            mostrarMensagem(RoteiroHistoria.chaveColetada());
            return;
        }

        if ("arco".equals(nome)) {
            mostrarMensagem(RoteiroHistoria.arcoColetado());
            return;
        }

        if ("flecha".equals(nome)) {
            mostrarMensagem(RoteiroHistoria.flechaColetada());
            return;
        }

        if ("chave_boss_63".equals(nome)) {
            estado = Estado.MEMORIAS;
            objetivoAtual = "Abra a porta do confronto com a chave vermelha 63.";
            mostrarMensagem(RoteiroHistoria.chaveBossColetada());
        }
    }

    public void eventoPortaSemChave(boolean portaDoBoss) {
        if (portaDoBoss) {
            mostrarMensagem(RoteiroHistoria.portaBossTrancada());
        } else {
            mostrarMensagem(RoteiroHistoria.portaChaveNormalTrancada());
        }
    }

    public boolean eventoPorta(int cenarioAtual, panel cenaDoJogo) {
        if (cenarioAtual == 0) {
            mostrarMensagem(RoteiroHistoria.portaQuartoTrancada());
            return true;
        }

        if (cenarioAtual >= 7) {
            boolean bossDerrotado = cenaDoJogo.getControladorInimigos() != null
                    && cenaDoJogo.getControladorInimigos().bossFoiDerrotado();

            if (cenaDoJogo.getInventario().temChaveBoss63() && bossDerrotado) {
                iniciarEscolhaFinal();
            } else if (cenaDoJogo.getInventario().temChaveBoss63()) {
                mostrarMensagem(RoteiroHistoria.falaBoss());
            } else {
                mostrarMensagem(RoteiroHistoria.saidaTrancada());
            }
            return true;
        }

        return false;
    }

    private void iniciarEscolhaFinal() {
        iniciarEscolhaFinal(RoteiroHistoria.escolhaFinal());
    }

    private void iniciarEscolhaFinal(String[] linhas) {
        estado = Estado.ESCOLHA_FINAL;
        objetivoAtual = "Escolha o final.";
        tipoEscolha = TipoEscolha.FINAL;
        nomeNpcFalando = null;
        exibindoEscolha = true;
        exibindoMensagem = false;
        opcaoEscolhida = 0;
        mostrarMensagemSemAbrirCaixa(linhas);
    }

    private void iniciarFinalAcordar() {
        exibindoEscolha = false;
        tipoEscolha = TipoEscolha.NENHUMA;
        estado = Estado.FINAL_ACORDAR;
        objetivoAtual = "Fim.";
        mostrarMensagem(RoteiroHistoria.finalAcordar());
    }

    private void iniciarFinalFicar(panel cenaDoJogo) {
        exibindoEscolha = false;
        tipoEscolha = TipoEscolha.NENHUMA;
        estado = Estado.FINAL_FICAR;
        objetivoAtual = "Fim.";
        cenaDoJogo.irParaCenario(2, 745, 335);
        mostrarMensagem(RoteiroHistoria.finalFicar());
    }

    private void iniciarFinalSecreto() {
        exibindoEscolha = false;
        tipoEscolha = TipoEscolha.NENHUMA;
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
        this.nomeNpcFalando = null;

        if (historiaFinalizada() && !finalNotificado && aoFinalizar != null) {
            finalNotificado = true;
            aoFinalizar.accept(estado);
        }
    }

    public void continuarNaFlorestaAposFinal() {
        estado = Estado.FLORESTA;
        objetivoAtual = "Explore a floresta.";
        exibindoMensagem = false;
        exibindoEscolha = false;
        tipoEscolha = TipoEscolha.NENHUMA;
        mensagem.clear();
        telaEscura = false;
        finalNotificado = false;
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

    public boolean isEscolhaNpc() {
        return exibindoEscolha && tipoEscolha == TipoEscolha.NPC;
    }

    public String getOpcaoEscolha1() {
        return isEscolhaNpc() ? opcaoNpc1 : "Acordar";
    }

    public String getOpcaoEscolha2() {
        return isEscolhaNpc() ? opcaoNpc2 : "Ficar em Dead Land";
    }

    public String getNomeNpcFalando() {
        return nomeNpcFalando;
    }

    public boolean jaFalouComNpc(String npcId) {
        return npcsConhecidos.contains(npcId);
    }

    private String nomeExibicaoNpc(String npcId) {
        if ("ari".equals(npcId)) return "Ari";
        if ("mara".equals(npcId)) return "Mara";
        if ("guardiao_memoria".equals(npcId)) return "Guardião";
        if ("porteiro_morto".equals(npcId)) return "Porteiro";
        return "Desconhecido";
    }

    private void tocarSomEstranhoBasico() {
        //Adicionar so wav ou waw esqueci qual e o arquivo
        Toolkit.getDefaultToolkit().beep();
    }
}
