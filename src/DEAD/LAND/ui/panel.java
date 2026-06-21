package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import DEAD.LAND.core.ControladorFlechas;
import DEAD.LAND.core.gameLoop;
import DEAD.LAND.core.spriteLoop;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.world.tileMap;


public class panel extends JPanel{
	private String posicao;
	gameLoop GL;
	escutadorTeclado ET;
	spriteLoop SL;
    private player jogador;
    private tileMap cenario;
    private IconeInteracao iconeInteracao;
    private Inventario inventario;
    private panel painelInventario;
    private ControladorFlechas controladorFlechas;


    public panel(String posicao, Inventario inventario) {
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

                
                ET = new escutadorTeclado();
                GL = new gameLoop(this, ET);
                SL = new spriteLoop(this, ET);
                
                this.addKeyListener(ET);
                this.setFocusable(true); 
                
                GL.start();
                SL.start();

                break;

            case "sul":
            	this.setPreferredSize(new Dimension(768, 100));
            	this.setBackground(new Color(38, 44, 58));
            	
                break;

            default:
            	this.setPreferredSize(new Dimension(768, 100));
            	this.setBackground(Color.GRAY);
            	
                break;
        }
	}
    
    
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        switch (posicao) {
            case "centro":
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (this.cenario == null) break;

                double escalaX = (double) getWidth()  / this.cenario.getLarguraTotal();
                double escalaY = (double) getHeight() / this.cenario.getAlturaTotal();
                g2.scale(escalaX, escalaY);

                this.cenario.desenhar(g2);
                getJogador().desenharPlayer(g2);
                if (this.controladorFlechas != null) this.controladorFlechas.desenhar(g2);
                this.iconeInteracao.desenharArea(g2, this.cenario, getJogador());
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
}
