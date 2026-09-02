package DEAD.LAND.story;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.core.MemoryManager;
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
    private static final String EVENTO_CACADOR_RECOMPENSA = "cacador_recompensa";
    private static final String EVENTO_ECO_VONTADE = "eco_vontade";
    private static final int MEMORIAS_PARA_RECOMPENSA = 3;

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

    public static class ProgressoHistoria {
        private final Estado estado;
        private final String objetivoAtual;
        private final String objetivoSecundario;
        private final Set<String> npcsConhecidos;
        private final Set<String> eventosHistoria;
        private final int tentativasMenu;
        private final boolean confrontoBossApresentado;
        private final boolean finalNotificado;

        private ProgressoHistoria(
                Estado estado,
                String objetivoAtual,
                String objetivoSecundario,
                Set<String> npcsConhecidos,
                Set<String> eventosHistoria,
                int tentativasMenu,
                boolean confrontoBossApresentado,
                boolean finalNotificado
        ) {
            this.estado = estado;
            this.objetivoAtual = objetivoAtual;
            this.objetivoSecundario = objetivoSecundario;
            this.npcsConhecidos = new HashSet<String>(npcsConhecidos);
            this.eventosHistoria = new HashSet<String>(eventosHistoria);
            this.tentativasMenu = tentativasMenu;
            this.confrontoBossApresentado = confrontoBossApresentado;
            this.finalNotificado = finalNotificado;
        }
    }

    private Estado estado = Estado.QUARTO;
    private String objetivoAtual = "Deite-se na cama.";
    private String objetivoSecundario = "";
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
    private final Set<String> eventosHistoria = new HashSet<String>();
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
                    iniciarFinalAcordar(cenaDoJogo);
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
                objetivoAtual = "Encontre a chave vermelha.";
                mostrarMensagem(RoteiroHistoria.portaoSemMemoria());
            }
        }
    }


    public void eventoFalarComNpc(String npcId, panel cenaDoJogo) {
        if (historiaFinalizada() || npcId == null) return;

        this.nomeNpcFalando = nomeExibicaoNpc(npcId);

        if ("sobrevivente_perdido".equals(npcId)) {
            conversarComSobrevivente(npcId);
            return;
        }

        if ("eco".equals(npcId)) {
            conversarComEco(npcId, cenaDoJogo);
            return;
        }

        if ("cacador_memorias".equals(npcId)) {
            conversarComCacador(npcId, cenaDoJogo);
            return;
        }

        if ("alma_esquecida".equals(npcId)) {
            conversarComAlma(npcId);
            return;
        }

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
                objetivoAtual = "Encontre a chave vermelha.";
                mostrarMensagem(RoteiroHistoria.npcPorteiroSemChave());
            }
            return;
        }

        mostrarMensagem(RoteiroHistoria.npcDesconhecido());
    }

    private void conversarComSobrevivente(String npcId) {
        boolean primeiraConversa = npcsConhecidos.add(npcId);
        if (primeiraConversa && objetivoSecundario.trim().isEmpty()) {
            objetivoSecundario = "Encontre 3 fragmentos de memoria.";
        }

        mostrarMensagem(
                "Sobrevivente perdido",
                primeiraConversa
                        ? "Os esqueletos carregam flechas quebradas. As vezes ainda servem."
                        : "Se ficar sem flechas, recue e procure restos depois das lutas.",
                "Algumas criaturas podem deixar municao ou cura."
        );
    }

    private void conversarComEco(String npcId, panel cenaDoJogo) {
        npcsConhecidos.add(npcId);

        if (!eventosHistoria.contains(EVENTO_ECO_VONTADE)) {
            eventosHistoria.add(EVENTO_ECO_VONTADE);
            if (cenaDoJogo != null && cenaDoJogo.getSistemaVontade() != null) {
                cenaDoJogo.getSistemaVontade().recuperar(15);
            }
            if (cenaDoJogo != null && cenaDoJogo.getFeedback() != null) {
                cenaDoJogo.getFeedback().mostrarMensagemCentro("Vontade restaurada");
            }
            mostrarMensagem(
                    "Eco",
                    "Dead Land repete o medo ate voce acreditar que ele e seu.",
                    "Respire. Nem toda voz aqui e sua."
            );
            return;
        }

        mostrarMensagem(
                "Eco",
                "Quando a Vontade cai, o mundo mente melhor.",
                "Memorias e checkpoints ajudam a manter voce inteiro."
        );
    }

    private void conversarComCacador(String npcId, panel cenaDoJogo) {
        npcsConhecidos.add(npcId);
        int memorias = cenaDoJogo != null && cenaDoJogo.getMemoryManager() != null
                ? cenaDoJogo.getMemoryManager().getQuantidadeDescoberta()
                : 0;

        if (memorias >= MEMORIAS_PARA_RECOMPENSA
                && !eventosHistoria.contains(EVENTO_CACADOR_RECOMPENSA)) {
            eventosHistoria.add(EVENTO_CACADOR_RECOMPENSA);
            objetivoSecundario = "Ajuda do Cacador concluida.";
            if (cenaDoJogo != null && cenaDoJogo.getInventario() != null) {
                cenaDoJogo.getInventario().adicionarFlechas(6);
                cenaDoJogo.getInventario().definirQuantidadeCura(
                        cenaDoJogo.getInventario().getQuantidadeCura() + 1
                );
                cenaDoJogo.atualizarInventario();
            }
            if (cenaDoJogo != null && cenaDoJogo.getFeedback() != null) {
                cenaDoJogo.getFeedback().mostrarMensagemCentro("Recompensa recebida");
            }
            if (cenaDoJogo != null) {
                cenaDoJogo.salvarAutoForcado();
            }
            mostrarMensagem(
                    "Cacador",
                    "Tres lembrancas ja sao uma trilha. Pegue isto.",
                    "Voce recebeu flechas e um Fragmento de Memoria."
            );
            return;
        }

        if (!eventosHistoria.contains(EVENTO_CACADOR_RECOMPENSA)) {
            objetivoSecundario = "Encontre 3 fragmentos de memoria.";
            mostrarMensagem(
                    "Cacador",
                    "Traga tres fragmentos de memoria e eu divido meus suprimentos.",
                    "Memorias encontradas: " + memorias + "/" + MEMORIAS_PARA_RECOMPENSA
            );
            return;
        }

        mostrarMensagem(
                "Cacador",
                "Use o dash antes de atirar. O Esquecido odeia alvo que se move.",
                "Nao gaste suas ultimas flechas sem carga."
        );
    }

    private void conversarComAlma(String npcId) {
        boolean primeiraConversa = npcsConhecidos.add(npcId);
        if (primeiraConversa && objetivoSecundario.trim().isEmpty()) {
            objetivoSecundario = "Descubra por que o menu reage ao medo.";
        }

        mostrarMensagem(
                "Alma esquecida",
                primeiraConversa
                        ? "Algumas saidas so aparecem quando voce insiste no impossivel."
                        : "Tres tentativas diante da escolha final podem abrir outro caminho.",
                "A floresta observa ate o que voce tenta evitar."
        );
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
        if (nome == null || nome.trim().isEmpty()) return;

        if ("chave".equals(nome)) {
            estado = Estado.BUSCA_MEMORIA;
            objetivoAtual = "Abra a porta inferior e encontre a chave vermelha.";
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

        if (nome.startsWith("memoria_")) {
            return;
        }

        if ("chave_boss_63".equals(nome)) {
            estado = Estado.MEMORIAS;
            objetivoAtual = "Abra a porta do confronto com a chave vermelha.";
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

    private void iniciarFinalAcordar(panel cenaDoJogo) {
        exibindoEscolha = false;
        tipoEscolha = TipoEscolha.NENHUMA;
        estado = Estado.FINAL_ACORDAR;
        objetivoAtual = "Fim.";
        mostrarMensagem(comComplementoDeMemoria(
                RoteiroHistoria.finalAcordar(),
                complementoFinalPorMemorias(cenaDoJogo)
        ));
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

    private String complementoFinalPorMemorias(panel cenaDoJogo) {
        if (cenaDoJogo == null || cenaDoJogo.getMemoryManager() == null) {
            return "Algumas lembrancas ficaram presas na floresta.";
        }

        int percentual = cenaDoJogo.getMemoryManager().getPercentualConclusao();
        if (percentual >= 100) {
            return "Com todas as memorias, voce acorda lembrando quem precisou deixar para tras.";
        }
        if (percentual >= 30) {
            return "Voce acorda com partes suficientes de si para continuar.";
        }
        return "Voce acorda, mas Dead Land ainda guarda quase tudo que era seu.";
    }

    private String[] comComplementoDeMemoria(String[] base, String complemento) {
        String[] resultado = Arrays.copyOf(base, base.length + 1);
        resultado[base.length] = complemento;
        return resultado;
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

    public ProgressoHistoria copiarProgresso() {
        return new ProgressoHistoria(
                estado,
                objetivoAtual,
                objetivoSecundario,
                npcsConhecidos,
                eventosHistoria,
                tentativasMenu,
                confrontoBossApresentado,
                finalNotificado
        );
    }

    public void restaurarAposRespawn(ProgressoHistoria progresso,
            Set<String> itensImportantes, int cenarioAtual, boolean bossDerrotado) {
        if (progresso != null) {
            this.estado = progresso.estado;
            this.objetivoAtual = progresso.objetivoAtual;
            this.objetivoSecundario = progresso.objetivoSecundario;
            this.npcsConhecidos.clear();
            this.npcsConhecidos.addAll(progresso.npcsConhecidos);
            this.eventosHistoria.clear();
            this.eventosHistoria.addAll(progresso.eventosHistoria);
            this.tentativasMenu = progresso.tentativasMenu;
            this.confrontoBossApresentado = progresso.confrontoBossApresentado;
            this.finalNotificado = progresso.finalNotificado;
        }

        this.mensagem.clear();
        this.exibindoMensagem = false;
        this.exibindoEscolha = false;
        this.tipoEscolha = TipoEscolha.NENHUMA;
        this.npcEscolhaId = null;
        this.nomeNpcFalando = null;
        this.opcaoNpc1 = "";
        this.opcaoNpc2 = "";
        this.opcaoEscolhida = 0;
        this.teclaConfirmarPressionada = false;
        this.teclaMenuPressionada = false;
        this.teclaCimaPressionada = false;
        this.teclaBaixoPressionada = false;
        this.telaEscura = false;

        ajustarObjetivoAposRespawn(itensImportantes, cenarioAtual, bossDerrotado);
    }

    private void ajustarObjetivoAposRespawn(Set<String> itensImportantes,
            int cenarioAtual, boolean bossDerrotado) {
        if (historiaFinalizada()) {
            return;
        }

        boolean temChavePrata = itensImportantes != null && itensImportantes.contains("chave");
        boolean temChaveBoss = itensImportantes != null && itensImportantes.contains("chave_boss_63");

        if (bossDerrotado) {
            this.objetivoAtual = "Fale com o Porteiro.";
            return;
        }

        if (temChaveBoss) {
            if (this.estado.ordinal() < Estado.MEMORIAS.ordinal()) {
                this.estado = Estado.MEMORIAS;
            }
            this.objetivoAtual = cenarioAtual == 7
                    ? "Derrote o chefe."
                    : "Abra a porta do confronto com a chave vermelha.";
            this.confrontoBossApresentado = this.confrontoBossApresentado || cenarioAtual == 7;
            return;
        }

        if (temChavePrata) {
            if (this.estado.ordinal() < Estado.BUSCA_MEMORIA.ordinal()) {
                this.estado = Estado.BUSCA_MEMORIA;
            }
            this.objetivoAtual = "Abra a porta inferior e encontre a chave vermelha.";
            return;
        }

        if (cenarioAtual >= 2 && (this.estado == Estado.QUARTO || this.estado == Estado.QUEDA)) {
            this.estado = Estado.FLORESTA;
            this.objetivoAtual = "Explore a floresta e procure uma saída.";
        }
    }

    public void eventoBossDerrotado() {
        if (historiaFinalizada()) {
            return;
        }

        objetivoAtual = "Fale com o Porteiro.";
        mostrarMensagem(
                "O Esquecido se desfaz em silêncio.",
                "O Porteiro agora pode ouvir seus passos."
        );
    }

    public void eventoMemoriaColetada(MemoryManager.Memoria memoria, int total, int encontradas) {
        if (memoria == null || historiaFinalizada()) {
            return;
        }

        if (!eventosHistoria.contains(EVENTO_CACADOR_RECOMPENSA)) {
            objetivoSecundario = encontradas >= MEMORIAS_PARA_RECOMPENSA
                    ? "Volte ao Cacador."
                    : "Encontre 3 fragmentos de memoria.";
        }

        mostrarMensagem(
                memoria.getTitulo(),
                memoria.getDescricao(),
                "Memorias: " + encontradas + "/" + total
        );
    }

    public Estado getEstado() {
        return estado;
    }

    public String getObjetivoAtual() {
        return objetivoAtual;
    }

    public String getObjetivoSecundario() {
        return objetivoSecundario;
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

    public Set<String> getNpcsConhecidos() {
        return new HashSet<String>(npcsConhecidos);
    }

    public Set<String> getEventosHistoria() {
        return new HashSet<String>(eventosHistoria);
    }

    public boolean isConfrontoBossApresentado() {
        return confrontoBossApresentado;
    }

    public String getSimboloNpc(String npcId, panel cenaDoJogo) {
        if (npcId == null || npcId.trim().isEmpty()) {
            return "";
        }

        if ("cacador_memorias".equals(npcId)) {
            boolean recompensa = eventosHistoria.contains(EVENTO_CACADOR_RECOMPENSA);
            int memorias = cenaDoJogo != null && cenaDoJogo.getMemoryManager() != null
                    ? cenaDoJogo.getMemoryManager().getQuantidadeDescoberta()
                    : 0;
            if (recompensa) return "✓";
            if (memorias >= MEMORIAS_PARA_RECOMPENSA || !npcsConhecidos.contains(npcId)) return "!";
            return "?";
        }

        if ("porteiro_morto".equals(npcId)) {
            boolean bossDerrotado = cenaDoJogo != null
                    && cenaDoJogo.getControladorInimigos() != null
                    && cenaDoJogo.getControladorInimigos().bossFoiDerrotado();
            return bossDerrotado ? "!" : "?";
        }

        if ("sobrevivente_perdido".equals(npcId)
                || "eco".equals(npcId)
                || "alma_esquecida".equals(npcId)) {
            return npcsConhecidos.contains(npcId) ? "✓" : "!";
        }

        if (!npcsConhecidos.contains(npcId)) {
            return "!";
        }

        if ("guardiao_memoria".equals(npcId)
                && cenaDoJogo != null
                && cenaDoJogo.getInventario().temChaveBoss63()) {
            return "✓";
        }

        return "?";
    }

    public void restaurarEstadoPersistido(String estadoNome, String objetivo,
            Set<String> npcs, boolean bossApresentado) {
        restaurarEstadoPersistido(estadoNome, objetivo, "", npcs, new HashSet<String>(), bossApresentado);
    }

    public void restaurarEstadoPersistido(String estadoNome, String objetivo,
            String objetivoSecundario, Set<String> npcs, Set<String> eventos,
            boolean bossApresentado) {
        try {
            this.estado = Estado.valueOf(estadoNome);
        } catch (Exception e) {
            this.estado = Estado.QUARTO;
        }

        this.objetivoAtual = objetivo == null || objetivo.trim().isEmpty()
                ? "Explore Dead Land."
                : objetivo;
        this.objetivoSecundario = objetivoSecundario == null ? "" : objetivoSecundario;
        this.npcsConhecidos.clear();
        if (npcs != null) {
            this.npcsConhecidos.addAll(npcs);
        }
        this.eventosHistoria.clear();
        if (eventos != null) {
            this.eventosHistoria.addAll(eventos);
        }
        this.confrontoBossApresentado = bossApresentado;
        this.exibindoMensagem = false;
        this.exibindoEscolha = false;
        this.tipoEscolha = TipoEscolha.NENHUMA;
        this.mensagem.clear();
        this.telaEscura = false;
    }

    private String nomeExibicaoNpc(String npcId) {
        if ("ari".equals(npcId)) return "Ari";
        if ("mara".equals(npcId)) return "Mara";
        if ("guardiao_memoria".equals(npcId)) return "Guardião";
        if ("porteiro_morto".equals(npcId)) return "Porteiro";
        if ("sobrevivente_perdido".equals(npcId)) return "Sobrevivente perdido";
        if ("eco".equals(npcId)) return "Eco";
        if ("cacador_memorias".equals(npcId)) return "Cacador";
        if ("alma_esquecida".equals(npcId)) return "Alma esquecida";
        return "Desconhecido";
    }

    private void tocarSomEstranhoBasico() {
        //Adicionar so wav ou waw esqueci qual e o arquivo
        Toolkit.getDefaultToolkit().beep();
    }
}
