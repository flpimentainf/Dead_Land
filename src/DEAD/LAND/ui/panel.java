package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

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


	
    public panel(String posicao) {
        this.posicao = posicao.toLowerCase();
        
        switch (this.posicao) {
            case "centro":
            	this.setPreferredSize(new Dimension(768, 480));
            	this.setBackground(Color.BLACK);
                
                setJogador(new player());
                this.cenario = new tileMap();
                this.iconeInteracao = new IconeInteracao();

                
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
            	this.setBackground(Color.YELLOW);
            	
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
                this.iconeInteracao.desenharCama(g2, this.cenario, getJogador());
                break;

            case "sul":
                g2.setColor(Color.YELLOW);
                g2.fillRect(0, 0, getWidth(), getHeight());
                break;
        }
    }


	public player getJogador() {
		return jogador;
	}


	public void setJogador(player jogador) {
		this.jogador = jogador;
	}


    public tileMap getCenario() {
        return cenario;
    }

    public boolean jogadorPertoDaCama() {
        if (this.cenario == null || getJogador() == null) {
            return false;
        }

        getJogador().atualizarAreaColisao();
        return this.cenario.getAreaCamaPerto(getJogador().AreaColisao) != null;
    }

    public void irParaCenario(int indexCenario, int jogadorX, int jogadorY) {
        cenario.irParaCenario(indexCenario);
        posicionarJogador(jogadorX, jogadorY);
    }

    public void irParaProximoCenario() {
        cenario.irParaProximoCenario();
        posicionarJogador(10, getJogador().y);
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
