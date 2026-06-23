package DEAD.LAND.ui;

import DEAD.LAND.core.ControladorFlechas;
import DEAD.LAND.core.ControladorInimigos;
import DEAD.LAND.core.ControladorItens;
import DEAD.LAND.core.ControladorNPCs;
import DEAD.LAND.core.gameLoop;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.story.SistemaHistoria;
import DEAD.LAND.world.tileMap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.function.Consumer;
import javax.swing.JPanel;


public class panel extends JPanel{
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
            	this.setPreferredSize(new Dimension(768, 480));
            	this.setBackground(Color.BLACK);
                
                setJogador(new player());
                this.cenario = new tileMap();
                this.iconeInteracao = new IconeInteracao();
                this.controladorFlechas = new ControladorFlechas();
                this.historia = new SistemaHistoria(aoFinalizar);
                this.interfaceHistoria = new InterfaceHistoria();

                
                ET = new escutadorTeclado();
                GL = new gameLoop(this, ET);
                
                this.addKeyListener(ET);
                this.setFocusable(true); 
                
                this.controladorItens = GL.getControladorItens();
                this.controladorInimigos = GL.getControladorInimigos();
                this.controladorNPCs = GL.getControladorNPCs();
                this.barradeVida = new BarraDeVida();
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

        switch (posicao) {
            case "centro":
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (this.cenario == null) break;

                AffineTransform transformacaoOriginal = g2.getTransform();
                double escalaX = (double) getWidth()  / this.cenario.getLarguraTotal();
                double escalaY = (double) getHeight() / this.cenario.getAlturaTotal();
                g2.scale(escalaX, escalaY);

                this.cenario.desenhar(g2);
                if (this.controladorNPCs != null) this.controladorNPCs.desenhar(g2, this);
                if (this.controladorInimigos != null) this.controladorInimigos.desenhar(g2, this.cenario.getCenarioAtualIndex());
                getJogador().desenharPlayer(g2);
                if (this.controladorFlechas != null) this.controladorFlechas.desenhar(g2);
                if (this.controladorItens != null) this.controladorItens.desenhar(g2, this);
                this.iconeInteracao.desenharArea(g2, this.cenario, getJogador());
                if (this.controladorNPCs != null) this.controladorNPCs.desenharIndicadorInteracao(g2, this);
                g2.setTransform(transformacaoOriginal);
                if (this.barradeVida != null) this.barradeVida.desenhar(g2, getJogador(), getWidth(), getHeight());
                if (this.interfaceHistoria != null) this.interfaceHistoria.desenhar(g2, getWidth(), getHeight(), this.historia);
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


	public player getJogador() {
		return jogador;
	}


	public void setJogador(player jogador) {
		this.jogador = jogador;
	}


    public ControladorFlechas getControladorFlechas() { return controladorFlechas; }

    public ControladorNPCs getControladorNPCs() { return controladorNPCs; }

    public SistemaHistoria getHistoria() { return historia; }

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

    public void irParaCenario(int indexCenario, int jogadorX, int jogadorY) {
        cenario.irParaCenario(indexCenario);
        posicionarJogador(jogadorX, jogadorY);
    }

    public void irParaProximoCenario() {
        cenario.irParaProximoCenario();
        posicionarJogador(50, getJogador().y);
    }

    public void irParaCenarioAnterior() {
        cenario.irParaCenarioAnterior();
        posicionarJogador(cenario.getLarguraTotal() - getJogador().width - 10, getJogador().y);
    }

    private void posicionarJogador(int x, int y) {
        getJogador().x = x;
        getJogador().y = y;
        getJogador().atualizarAreaColisao();
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
