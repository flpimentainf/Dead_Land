package DEAD.LAND.ui;

import DEAD.LAND.core.ControladorFlechas;
import DEAD.LAND.core.ControladorInimigos;
import DEAD.LAND.core.ControladorItens;
import DEAD.LAND.core.ControladorNPCs;
import DEAD.LAND.core.AudioManager;
import DEAD.LAND.core.AudioManager.Canal;
import DEAD.LAND.core.Camera;
import DEAD.LAND.core.GameState;
import DEAD.LAND.core.MemoryManager;
import DEAD.LAND.core.SaveManager;
import DEAD.LAND.core.SistemaCheckpoint;
import DEAD.LAND.core.SistemaFeedback;
import DEAD.LAND.core.SistemaMorte;
import DEAD.LAND.core.SistemaVontade;
import DEAD.LAND.core.gameLoop;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.story.SistemaHistoria;
import DEAD.LAND.world.CenarioId;
import DEAD.LAND.world.tileMap;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JPanel;


public class panel extends JPanel{
    private static final int LARGURA_LOGICA = 768;
    private static final int ALTURA_LOGICA = 480;
    private static final String[] OPCOES_PAUSA = {
            "CONTINUAR",
            "SALVAR SLOT 1",
            "MEMORIAS",
            "CONFIGURACOES",
            "MENU PRINCIPAL"
    };
    private static final String[] OPCOES_CONFIGURACOES = {
            "MASTER",
            "MUSICA",
            "EFEITOS",
            "AMBIENTE",
            "VOLTAR"
    };

    private enum TelaPausa {
        PRINCIPAL,
        MEMORIAS,
        CONFIGURACOES
    }

	private String posicao;
	gameLoop GL;
	escutadorTeclado ET;
    private player jogador;
    private tileMap cenario;
    private IconeInteracao iconeInteracao;
    private Inventario inventario;
    private panel painelInventario;
    private ControladorFlechas controladorFlechas;
    private ControladorItens controladorItens;
    private ControladorInimigos controladorInimigos;
    private ControladorNPCs controladorNPCs;
    private BarraDeVida barradeVida;
    private SistemaHistoria historia;
    private InterfaceHistoria interfaceHistoria;
    private SistemaCheckpoint sistemaCheckpoint;
    private SistemaMorte sistemaMorte;
    private SistemaFeedback feedback;
    private MemoryManager memoryManager;
    private SistemaVontade sistemaVontade;
    private Camera camera;
    private BufferedImage quadroLogico;
    private SaveManager saveManager;
    private long inicioJogoMs;
    private long ultimoAutosaveMs;
    private int slotAtual = 1;
    private Runnable aoVoltarMenuPrincipal;
    private boolean menuPausaAberto;
    private TelaPausa telaPausa = TelaPausa.PRINCIPAL;
    private int opcaoMenuPausa;
    private int opcaoConfiguracao;
    private boolean teclaMenuPausaPressionada;
    private boolean teclaCimaPausaPressionada;
    private boolean teclaBaixoPausaPressionada;
    private boolean teclaEsquerdaPausaPressionada;
    private boolean teclaDireitaPausaPressionada;
    private boolean teclaConfirmarPausaPressionada;


    public panel(String posicao, Inventario inventario) {
        this(posicao, inventario, null, null);
    }

    public panel(
            String posicao,
            Inventario inventario,
            Consumer<SistemaHistoria.Estado> aoFinalizar
    ) {
        this(posicao, inventario, aoFinalizar, null);
    }

    public panel(
            String posicao,
            Inventario inventario,
            Consumer<SistemaHistoria.Estado> aoFinalizar,
            Runnable aoVoltarMenuPrincipal
    ) {
        this.posicao = posicao.toLowerCase();
        this.inventario = inventario;
        this.aoVoltarMenuPrincipal = aoVoltarMenuPrincipal;
        
        switch (this.posicao) {
            case "centro":
                this.setPreferredSize(new Dimension(LARGURA_LOGICA, ALTURA_LOGICA));
            	this.setBackground(Color.BLACK);
                
                setJogador(new player());
                this.cenario = new tileMap();
                this.iconeInteracao = new IconeInteracao();
                this.controladorFlechas = new ControladorFlechas();
                this.historia = new SistemaHistoria(aoFinalizar);
                this.interfaceHistoria = new InterfaceHistoria();
                this.sistemaCheckpoint = new SistemaCheckpoint();
                this.sistemaMorte = new SistemaMorte();
                this.feedback = new SistemaFeedback();
                this.memoryManager = new MemoryManager();
                this.sistemaVontade = new SistemaVontade();
                this.camera = new Camera(LARGURA_LOGICA, ALTURA_LOGICA);
                this.saveManager = new SaveManager();
                this.inicioJogoMs = System.currentTimeMillis();

                
                ET = new escutadorTeclado();
                GL = new gameLoop(this, ET);
                
                this.addKeyListener(ET);
                this.setFocusable(true); 
                
                this.controladorItens = GL.getControladorItens();
                this.controladorInimigos = GL.getControladorInimigos();
                this.controladorNPCs = GL.getControladorNPCs();
                this.barradeVida = new BarraDeVida();
                this.sistemaCheckpoint.registrarCheckpointInicial(this);
                sincronizarCameraImediatamente();
                GL.iniciar();

                break;

            case "sul":
            	this.setPreferredSize(new Dimension(768, 125));
            	this.setBackground(new Color(38, 44, 58));
            	
                break;

            default:
            	this.setPreferredSize(new Dimension(768, 125));
            	this.setBackground(Color.GRAY);
            	
                break;
        }
	}
    
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        configurarRenderizacaoPixelArt(g2);

        switch (posicao) {
            case "centro":
                desenharCentro(g2);
                break;

            case "sul":
                g2.setColor(new Color(38, 44, 58));
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (this.inventario != null) {
                    this.inventario.desenhar(g2, getWidth(), getHeight());
                }
                break;
        }
    }

    private void desenharCentro(Graphics2D g2Tela) {
        g2Tela.setColor(Color.BLACK);
        g2Tela.fillRect(0, 0, getWidth(), getHeight());

        if (this.cenario == null) {
            return;
        }

        BufferedImage quadro = getQuadroLogico();
        Graphics2D g2 = quadro.createGraphics();
        configurarRenderizacaoPixelArt(g2);
        try {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, LARGURA_LOGICA, ALTURA_LOGICA);
            atualizarCamera();

            AffineTransform transformacaoOriginal = g2.getTransform();
            g2.translate(-this.camera.getXRender(), -this.camera.getYRender());

            this.cenario.desenhar(g2);
            if (this.controladorNPCs != null) this.controladorNPCs.desenhar(g2, this);
            if (this.controladorInimigos != null) this.controladorInimigos.desenhar(g2, this.cenario.getCenarioAtualIndex());
            getJogador().desenharPlayer(g2);
            if (this.controladorFlechas != null) this.controladorFlechas.desenhar(g2);
            if (this.controladorItens != null) this.controladorItens.desenhar(g2, this);
            this.iconeInteracao.desenharArea(g2, this.cenario, getJogador());
            if (this.controladorNPCs != null) this.controladorNPCs.desenharIndicadorInteracao(g2, this);
            if (this.feedback != null) this.feedback.desenharMundo(g2);

            g2.setTransform(transformacaoOriginal);
            if (this.sistemaVontade != null) this.sistemaVontade.desenharEfeitoMental(g2, LARGURA_LOGICA, ALTURA_LOGICA);
            desenharFlashDano(g2, LARGURA_LOGICA, ALTURA_LOGICA);
            if (this.barradeVida != null) this.barradeVida.desenhar(g2, getJogador(), LARGURA_LOGICA, ALTURA_LOGICA);
            if (this.sistemaVontade != null) this.sistemaVontade.desenharBarra(g2, LARGURA_LOGICA, ALTURA_LOGICA);
            if (deveDesenharBarraBoss()) {
                this.controladorInimigos.getChefe().desenharBarraBoss(g2, LARGURA_LOGICA);
            }
            desenharHudCombate(g2);
            if (this.feedback != null) this.feedback.desenharHud(g2, LARGURA_LOGICA, ALTURA_LOGICA);
            if (this.interfaceHistoria != null) this.interfaceHistoria.desenhar(g2, LARGURA_LOGICA, ALTURA_LOGICA, this.historia);
            if (this.sistemaMorte != null) this.sistemaMorte.desenhar(g2, LARGURA_LOGICA, ALTURA_LOGICA);
            if (this.menuPausaAberto) desenharMenuPausa(g2);
        } finally {
            g2.dispose();
        }

        desenharQuadroLogicoNaTela(g2Tela, quadro);
    }

    private BufferedImage getQuadroLogico() {
        if (this.quadroLogico == null
                || this.quadroLogico.getWidth() != LARGURA_LOGICA
                || this.quadroLogico.getHeight() != ALTURA_LOGICA) {
            this.quadroLogico = new BufferedImage(
                    LARGURA_LOGICA,
                    ALTURA_LOGICA,
                    BufferedImage.TYPE_INT_RGB
            );
        }

        return this.quadroLogico;
    }

    private void atualizarCamera() {
        if (this.camera == null || getJogador() == null || this.cenario == null) {
            return;
        }

        this.camera.definirViewport(LARGURA_LOGICA, ALTURA_LOGICA);
        this.camera.seguirSuavemente(
                getJogador(),
                this.cenario.getLarguraTotal(),
                this.cenario.getAlturaTotal()
        );
    }

    private void desenharQuadroLogicoNaTela(Graphics2D g2Tela, BufferedImage quadro) {
        int larguraPainel = Math.max(1, getWidth());
        int alturaPainel = Math.max(1, getHeight());
        double escala = Math.min(
                larguraPainel / (double) LARGURA_LOGICA,
                alturaPainel / (double) ALTURA_LOGICA
        );

        int larguraDesenho = Math.max(1, (int) Math.round(LARGURA_LOGICA * escala));
        int alturaDesenho = Math.max(1, (int) Math.round(ALTURA_LOGICA * escala));
        int x = (larguraPainel - larguraDesenho) / 2;
        int y = (alturaPainel - alturaDesenho) / 2;

        configurarRenderizacaoPixelArt(g2Tela);
        g2Tela.drawImage(quadro, x, y, larguraDesenho, alturaDesenho, null);
    }

    private void configurarRenderizacaoPixelArt(Graphics2D g2) {
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED
        );
    }

    private void desenharFlashDano(Graphics2D g2, int largura, int altura) {
        if (getJogador() == null) {
            return;
        }

        float intensidade = getJogador().getIntensidadeFlashDano();
        if (intensidade <= 0f) {
            return;
        }

        Composite composicaoOriginal = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER,
                Math.min(0.18f, intensidade * 0.18f)
        ));
        g2.setColor(Color.RED);
        g2.fillRect(0, 0, largura, altura);
        g2.setComposite(composicaoOriginal);
    }

    private void desenharHudCombate(Graphics2D g2) {
        if (this.inventario == null) {
            return;
        }

        int x = 200;
        int y = ALTURA_LOGICA - 42;
        String flechas = "Flechas: " + inventario.getQuantidadeFlechas()
                + "/" + inventario.getCapacidadeMaximaFlechas();

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        int largura = Math.max(135, fm.stringWidth(flechas) + 18);
        g2.setColor(new Color(0, 0, 0, 165));
        g2.fillRoundRect(x, y - 18, largura, 24, 8, 8);
        g2.setColor(new Color(245, 225, 170));
        g2.drawString(flechas, x + 9, y);

        if (getJogador() != null && getJogador().getProgressoCooldownDash() < 1f) {
            int dashX = x + largura + 12;
            int dashY = y - 14;
            int dashW = 70;
            int dashH = 8;
            g2.setColor(new Color(0, 0, 0, 165));
            g2.fillRoundRect(dashX - 1, dashY - 1, dashW + 2, dashH + 2, 5, 5);
            g2.setColor(new Color(120, 210, 255));
            g2.fillRoundRect(dashX, dashY, Math.round(dashW * getJogador().getProgressoCooldownDash()), dashH, 4, 4);
            g2.setColor(Color.WHITE);
            g2.drawString("Dash", dashX, dashY - 3);
        }
    }

    private boolean deveDesenharBarraBoss() {
        return this.cenario != null
                && this.cenario.getCenarioAtualIndex() == CenarioId.ARENA_BOSS.indice()
                && this.controladorInimigos != null
                && this.controladorInimigos.getChefe() != null
                && this.controladorInimigos.getChefe().estaVivo();
    }

    public boolean atualizarMenuPausa(escutadorTeclado teclado) {
        if (!"centro".equals(this.posicao) || teclado == null) {
            return false;
        }

        boolean historiaEmEscolha = this.historia != null && this.historia.isExibindoEscolha();

        if (teclado.abrirMenu && !this.teclaMenuPausaPressionada) {
            if (this.menuPausaAberto) {
                voltarOuFecharMenuPausa();
            } else if (!historiaEmEscolha) {
                abrirMenuPausa();
            }
        }

        if (this.menuPausaAberto) {
            atualizarNavegacaoMenuPausa(teclado);
        }

        atualizarTeclasMenuPausa(teclado);
        return this.menuPausaAberto;
    }

    private void abrirMenuPausa() {
        this.menuPausaAberto = true;
        this.telaPausa = TelaPausa.PRINCIPAL;
        this.opcaoMenuPausa = 0;
        AudioManager.getInstancia().tocarEfeito("interface");
    }

    private void voltarOuFecharMenuPausa() {
        if (this.telaPausa == TelaPausa.PRINCIPAL) {
            this.menuPausaAberto = false;
        } else {
            this.telaPausa = TelaPausa.PRINCIPAL;
        }
        AudioManager.getInstancia().tocarEfeito("interface");
    }

    private void atualizarNavegacaoMenuPausa(escutadorTeclado teclado) {
        if (this.telaPausa == TelaPausa.CONFIGURACOES) {
            atualizarConfiguracoes(teclado);
            return;
        }

        if (this.telaPausa == TelaPausa.MEMORIAS) {
            if (teclado.confirmar && !this.teclaConfirmarPausaPressionada) {
                this.telaPausa = TelaPausa.PRINCIPAL;
                AudioManager.getInstancia().tocarEfeito("interface");
            }
            return;
        }

        if (teclado.movePraCima && !this.teclaCimaPausaPressionada) {
            this.opcaoMenuPausa = (this.opcaoMenuPausa - 1 + OPCOES_PAUSA.length) % OPCOES_PAUSA.length;
            AudioManager.getInstancia().tocarEfeito("interface");
        }

        if (teclado.movePraBaixo && !this.teclaBaixoPausaPressionada) {
            this.opcaoMenuPausa = (this.opcaoMenuPausa + 1) % OPCOES_PAUSA.length;
            AudioManager.getInstancia().tocarEfeito("interface");
        }

        if (teclado.confirmar && !this.teclaConfirmarPausaPressionada) {
            executarOpcaoPausa();
        }
    }

    private void atualizarConfiguracoes(escutadorTeclado teclado) {
        if (teclado.movePraCima && !this.teclaCimaPausaPressionada) {
            this.opcaoConfiguracao = (this.opcaoConfiguracao - 1 + OPCOES_CONFIGURACOES.length)
                    % OPCOES_CONFIGURACOES.length;
            AudioManager.getInstancia().tocarEfeito("interface");
        }

        if (teclado.movePraBaixo && !this.teclaBaixoPausaPressionada) {
            this.opcaoConfiguracao = (this.opcaoConfiguracao + 1) % OPCOES_CONFIGURACOES.length;
            AudioManager.getInstancia().tocarEfeito("interface");
        }

        if (this.opcaoConfiguracao < 4) {
            if (teclado.movePraEsq && !this.teclaEsquerdaPausaPressionada) {
                ajustarVolumeCanal(this.opcaoConfiguracao, -0.1f);
            }
            if (teclado.movePraDir && !this.teclaDireitaPausaPressionada) {
                ajustarVolumeCanal(this.opcaoConfiguracao, 0.1f);
            }
        }

        if (teclado.confirmar && !this.teclaConfirmarPausaPressionada
                && this.opcaoConfiguracao == OPCOES_CONFIGURACOES.length - 1) {
            this.telaPausa = TelaPausa.PRINCIPAL;
            AudioManager.getInstancia().tocarEfeito("interface");
        }
    }

    private void ajustarVolumeCanal(int indice, float delta) {
        Canal canal;
        if (indice == 0) canal = Canal.MASTER;
        else if (indice == 1) canal = Canal.MUSIC;
        else if (indice == 2) canal = Canal.SFX;
        else canal = Canal.AMBIENT;

        float atual = AudioManager.getInstancia().getVolume(canal);
        AudioManager.getInstancia().definirVolume(canal, atual + delta);
        AudioManager.getInstancia().tocarEfeito("interface");
    }

    private void executarOpcaoPausa() {
        AudioManager.getInstancia().tocarEfeito("interface");
        switch (this.opcaoMenuPausa) {
            case 0:
                this.menuPausaAberto = false;
                break;
            case 1:
                salvarSlot(1);
                break;
            case 2:
                this.telaPausa = TelaPausa.MEMORIAS;
                break;
            case 3:
                this.telaPausa = TelaPausa.CONFIGURACOES;
                this.opcaoConfiguracao = 0;
                break;
            case 4:
                salvarSlot(this.slotAtual);
                this.menuPausaAberto = false;
                if (this.aoVoltarMenuPrincipal != null) {
                    this.aoVoltarMenuPrincipal.run();
                }
                break;
            default:
                break;
        }
    }

    private void atualizarTeclasMenuPausa(escutadorTeclado teclado) {
        this.teclaMenuPausaPressionada = teclado.abrirMenu;
        this.teclaCimaPausaPressionada = teclado.movePraCima;
        this.teclaBaixoPausaPressionada = teclado.movePraBaixo;
        this.teclaEsquerdaPausaPressionada = teclado.movePraEsq;
        this.teclaDireitaPausaPressionada = teclado.movePraDir;
        this.teclaConfirmarPausaPressionada = teclado.confirmar;
    }

    private void desenharMenuPausa(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, LARGURA_LOGICA, ALTURA_LOGICA);

        if (this.telaPausa == TelaPausa.MEMORIAS) {
            desenharTelaMemorias(g2);
        } else if (this.telaPausa == TelaPausa.CONFIGURACOES) {
            desenharTelaConfiguracoes(g2);
        } else {
            desenharTelaPrincipalPausa(g2);
        }
    }

    private void desenharTelaPrincipalPausa(Graphics2D g2) {
        desenharTextoCentralizado(g2, "PAUSA", 96, 34, Font.BOLD, new Color(235, 235, 235));
        int y = 158;
        for (int i = 0; i < OPCOES_PAUSA.length; i++) {
            desenharOpcao(g2, OPCOES_PAUSA[i], y + i * 44, i == this.opcaoMenuPausa);
        }
    }

    private void desenharTelaMemorias(Graphics2D g2) {
        int encontradas = this.memoryManager == null ? 0 : this.memoryManager.getQuantidadeDescoberta();
        int total = this.memoryManager == null ? 0 : this.memoryManager.getTotal();

        desenharTextoCentralizado(g2, "MEMORIAS", 58, 28, Font.BOLD, Color.WHITE);
        desenharTextoCentralizado(g2, "Encontradas: " + encontradas + "/" + total,
                91, 15, Font.PLAIN, new Color(220, 220, 220));

        int x = 92;
        int y = 128;
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        if (this.memoryManager != null) {
            for (MemoryManager.Memoria memoria : this.memoryManager.getMemorias()) {
                boolean descoberta = this.memoryManager.foiDescoberta(memoria.getId());
                g2.setColor(descoberta ? new Color(245, 226, 166) : new Color(160, 160, 160));
                g2.drawString(descoberta ? memoria.getTitulo() : "???", x, y);
                g2.setColor(descoberta ? Color.WHITE : new Color(120, 120, 120));
                g2.drawString(descoberta ? memoria.getDescricao() : "Memoria ainda oculta.", x + 18, y + 19);
                y += 55;
            }
        }

        desenharTextoCentralizado(g2, "ENTER/E ou ESC para voltar", ALTURA_LOGICA - 34,
                12, Font.PLAIN, new Color(190, 190, 190));
    }

    private void desenharTelaConfiguracoes(Graphics2D g2) {
        desenharTextoCentralizado(g2, "CONFIGURACOES", 68, 28, Font.BOLD, Color.WHITE);
        int y = 135;
        for (int i = 0; i < OPCOES_CONFIGURACOES.length; i++) {
            boolean selecionado = i == this.opcaoConfiguracao;
            if (i == OPCOES_CONFIGURACOES.length - 1) {
                desenharOpcao(g2, OPCOES_CONFIGURACOES[i], y + i * 48, selecionado);
            } else {
                desenharVolume(g2, OPCOES_CONFIGURACOES[i], getVolumeCanal(i), y + i * 48, selecionado);
            }
        }
    }

    private float getVolumeCanal(int indice) {
        if (indice == 0) return AudioManager.getInstancia().getVolume(Canal.MASTER);
        if (indice == 1) return AudioManager.getInstancia().getVolume(Canal.MUSIC);
        if (indice == 2) return AudioManager.getInstancia().getVolume(Canal.SFX);
        return AudioManager.getInstancia().getVolume(Canal.AMBIENT);
    }

    private void desenharVolume(Graphics2D g2, String texto, float valor, int y, boolean selecionado) {
        int x = 220;
        int largura = 320;
        g2.setFont(new Font("Arial", Font.BOLD, 17));
        g2.setColor(selecionado ? new Color(245, 226, 166) : Color.WHITE);
        g2.drawString((selecionado ? "> " : "  ") + texto, x, y);

        int barraX = x + 125;
        int barraY = y - 14;
        int barraW = 150;
        int barraH = 10;
        g2.setColor(new Color(30, 30, 30, 220));
        g2.fillRoundRect(barraX, barraY, barraW, barraH, 8, 8);
        g2.setColor(new Color(120, 210, 255));
        g2.fillRoundRect(barraX, barraY, Math.round(barraW * valor), barraH, 8, 8);
        g2.setColor(new Color(220, 220, 220));
        g2.drawRoundRect(barraX, barraY, barraW, barraH, 8, 8);
        g2.drawString(Math.round(valor * 100) + "%", x + largura - 20, y);
    }

    private void desenharOpcao(Graphics2D g2, String texto, int y, boolean selecionado) {
        Color cor = selecionado ? new Color(245, 226, 166) : Color.WHITE;
        desenharTextoCentralizado(g2, (selecionado ? "> " : "  ") + texto, y,
                18, Font.BOLD, cor);
    }

    private void desenharTextoCentralizado(Graphics2D g2, String texto, int y,
            int tamanho, int estilo, Color cor) {
        g2.setFont(new Font("Arial", estilo, tamanho));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(cor);
        g2.drawString(texto, (LARGURA_LOGICA - fm.stringWidth(texto)) / 2, y);
    }

    public boolean isMenuPausaAberto() {
        return this.menuPausaAberto;
    }


	public player getJogador() {
		return jogador;
	}


	public void setJogador(player jogador) {
		this.jogador = jogador;
	}


    public ControladorFlechas getControladorFlechas() { return controladorFlechas; }

    public ControladorItens getControladorItens() { return controladorItens; }

    public ControladorNPCs getControladorNPCs() { return controladorNPCs; }

    public ControladorInimigos getControladorInimigos() { return controladorInimigos; }

    public SistemaHistoria getHistoria() { return historia; }

    public SistemaCheckpoint getSistemaCheckpoint() { return sistemaCheckpoint; }

    public SistemaMorte getSistemaMorte() { return sistemaMorte; }

    public SistemaFeedback getFeedback() { return feedback; }

    public MemoryManager getMemoryManager() { return memoryManager; }

    public SistemaVontade getSistemaVontade() { return sistemaVontade; }

    public Camera getCamera() { return camera; }

    public GameState criarGameState(int slot) {
        GameState state = new GameState();
        state.slot = Math.max(1, Math.min(3, slot));
        state.cenarioIndex = this.cenario == null ? 0 : this.cenario.getCenarioAtualIndex();
        state.jogadorX = getJogador() == null ? 745 : getJogador().x;
        state.jogadorY = getJogador() == null ? 335 : getJogador().y;
        state.vida = getJogador() == null ? player.VIDA_MAXIMA : getJogador().getVida();
        state.flechas = inventario == null ? 0 : inventario.getQuantidadeFlechas();
        state.capacidadeFlechas = inventario == null ? 20 : inventario.getCapacidadeMaximaFlechas();
        state.fragmentosCura = inventario == null ? 0 : inventario.getQuantidadeCura();
        state.vontade = sistemaVontade == null ? SistemaVontade.VONTADE_MAXIMA : sistemaVontade.getVontade();
        state.bossDerrotado = controladorInimigos != null && controladorInimigos.bossFoiDerrotado();
        state.estadoHistoria = historia == null ? "QUARTO" : historia.getEstado().name();
        state.objetivoHistoria = historia == null ? "" : historia.getObjetivoAtual();
        state.objetivoSecundario = historia == null ? "" : historia.getObjetivoSecundario();
        state.checkpoint = sistemaCheckpoint == null ? "" : sistemaCheckpoint.getNomeCheckpointAtual();
        state.tempoJogoMs = System.currentTimeMillis() - inicioJogoMs;
        state.itens = inventario == null ? new LinkedHashSet<String>() : inventario.getNomesItens();
        state.npcsConhecidos = historia == null ? new LinkedHashSet<String>() : historia.getNpcsConhecidos();
        state.eventosHistoria = historia == null ? new LinkedHashSet<String>() : historia.getEventosHistoria();
        state.memorias = memoryManager == null ? new LinkedHashSet<String>() : memoryManager.getIdsDescobertas();
        return state;
    }

    public void aplicarGameState(GameState state) {
        if (state == null || this.cenario == null || getJogador() == null) {
            return;
        }

        this.slotAtual = Math.max(1, Math.min(3, state.slot));
        this.inicioJogoMs = System.currentTimeMillis() - Math.max(0, state.tempoJogoMs);
        this.cenario.irParaCenario(state.cenarioIndex);
        getJogador().posicionarEm(state.jogadorX, state.jogadorY);
        getJogador().definirVida(Math.max(1, state.vida));

        if (this.memoryManager != null) {
            this.memoryManager.definirDescobertas(state.memorias);
        }

        Set<String> coletas = new LinkedHashSet<String>(state.itens);
        coletas.addAll(state.memorias);
        if (this.controladorItens != null) {
            this.controladorItens.restaurarColetas(coletas, this.cenario);
        }

        if (this.inventario != null && this.controladorItens != null) {
            this.inventario.definirCapacidadeMaximaFlechas(state.capacidadeFlechas);
            this.inventario.substituirPorItens(
                    this.controladorItens.getItensPorNomes(state.itens),
                    state.flechas,
                    state.fragmentosCura
            );
            this.inventario.equiparArcoSePossuir();
        }

        if (this.sistemaVontade != null) {
            this.sistemaVontade.definirVontade(state.vontade);
        }

        if (this.controladorInimigos != null) {
            this.controladorInimigos.definirBossDerrotado(state.bossDerrotado);
        }

        if (this.historia != null) {
            this.historia.restaurarEstadoPersistido(
                    state.estadoHistoria,
                    state.objetivoHistoria,
                    state.objetivoSecundario,
                    state.npcsConhecidos,
                    state.eventosHistoria,
                    state.bossDerrotado
            );
        }

        if (this.sistemaCheckpoint != null) {
            this.sistemaCheckpoint.registrarCheckpointInicial(this);
        }

        atualizarInventario();
        sincronizarCameraImediatamente();
        requestFocusInWindow();
    }

    public boolean salvarSlot(int slot) {
        if (this.saveManager == null) {
            this.saveManager = new SaveManager();
        }

        this.slotAtual = Math.max(1, Math.min(3, slot));
        boolean salvou = this.saveManager.salvar(criarGameState(this.slotAtual));
        if (salvou && this.feedback != null) {
            this.feedback.mostrarMensagemCentro("Jogo salvo no slot " + this.slotAtual);
        }
        return salvou;
    }

    public void salvarAuto() {
        salvarAuto(false);
    }

    public void salvarAutoForcado() {
        salvarAuto(true);
    }

    private void salvarAuto(boolean forcar) {
        long agora = System.currentTimeMillis();
        if (!forcar && agora - ultimoAutosaveMs < 5000) {
            return;
        }

        ultimoAutosaveMs = agora;
        salvarSlot(this.slotAtual);
    }

    public tileMap getCenario() {
        return cenario;
    }

    public Inventario getInventario() {
        return this.inventario;
    }

    public void setPainelInventario(panel painelInventario) {
        this.painelInventario = painelInventario;
    }

    public void atualizarInventario() {
        if (this.painelInventario != null) {
            this.painelInventario.repaint();
        }
    }

    public tileMap.InteracaoPerto getInteracaoPerto() {
        if (this.cenario == null || getJogador() == null) {
            return null;
        }

        getJogador().atualizarAreaColisao();
        return this.cenario.getInteracaoPerto(getJogador().AreaColisao);
    }

    public boolean jogadorPerto() {
        return getInteracaoPerto() != null;
    }

    public boolean existeCombateAtivo() {
        return this.controladorInimigos != null
                && this.controladorInimigos.existeCombateAtivo(this);
    }

    public void irParaCenario(int indexCenario, int jogadorX, int jogadorY) {
        cenario.irParaCenario(indexCenario);
        posicionarJogador(jogadorX, jogadorY);
        sincronizarCameraImediatamente();
    }

    public void irParaProximoCenario() {
        cenario.irParaProximoCenario();
        posicionarJogador(50, getJogador().y);
        sincronizarCameraImediatamente();
    }

    public void irParaCenarioAnterior() {
        cenario.irParaCenarioAnterior();
        posicionarJogador(cenario.getLarguraTotal() - getJogador().width - 10, getJogador().y);
        sincronizarCameraImediatamente();
    }

    private void posicionarJogador(int x, int y) {
        getJogador().posicionarEm(x, y);
    }

    private void sincronizarCameraImediatamente() {
        if (this.camera == null || this.cenario == null || getJogador() == null) {
            return;
        }

        this.camera.definirViewport(LARGURA_LOGICA, ALTURA_LOGICA);
        this.camera.centralizarImediatamente(
                getJogador(),
                this.cenario.getLarguraTotal(),
                this.cenario.getAlturaTotal()
        );
    }

    public void pausarJogo() {
        if (GL != null) {
            GL.parar();
        }
    }

    public void continuarAposFinalNaFloresta() {
        if (historia == null || GL == null) {
            return;
        }

        historia.continuarNaFlorestaAposFinal();
        irParaCenario(2, 745, 335);
        GL.iniciar();
        requestFocusInWindow();
        repaint();
    }
}
