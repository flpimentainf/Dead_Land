package DEAD.LAND.ui;

import DEAD.LAND.core.ControladorFlechas;
import DEAD.LAND.core.ControladorInimigos;
import DEAD.LAND.core.ControladorItens;
import DEAD.LAND.core.ControladorNPCs;
import DEAD.LAND.core.Camera;
import DEAD.LAND.core.SistemaCheckpoint;
import DEAD.LAND.core.SistemaMorte;
import DEAD.LAND.core.gameLoop;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.story.SistemaHistoria;
import DEAD.LAND.world.tileMap;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.util.function.Consumer;
import javax.swing.JPanel;


public class panel extends JPanel{
    private static final int LARGURA_LOGICA = 768;
    private static final int ALTURA_LOGICA = 480;

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
    private Camera camera;
    private BufferedImage quadroLogico;


    public panel(String posicao, Inventario inventario) {
        this(posicao, inventario, null);
    }

    public panel(
            String posicao,
            Inventario inventario,
            Consumer<SistemaHistoria.Estado> aoFinalizar
    ) {
        this.posicao = posicao.toLowerCase();
        this.inventario = inventario;
        
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
                this.camera = new Camera(LARGURA_LOGICA, ALTURA_LOGICA);

                
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

            g2.setTransform(transformacaoOriginal);
            desenharFlashDano(g2, LARGURA_LOGICA, ALTURA_LOGICA);
            if (this.barradeVida != null) this.barradeVida.desenhar(g2, getJogador(), LARGURA_LOGICA, ALTURA_LOGICA);
            if (this.interfaceHistoria != null) this.interfaceHistoria.desenhar(g2, LARGURA_LOGICA, ALTURA_LOGICA, this.historia);
            if (this.sistemaMorte != null) this.sistemaMorte.desenhar(g2, LARGURA_LOGICA, ALTURA_LOGICA);
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

    public Camera getCamera() { return camera; }

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
