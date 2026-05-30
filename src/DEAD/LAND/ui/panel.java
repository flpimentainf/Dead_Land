package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

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


	
    public panel(String posicao) {
        this.posicao = posicao.toLowerCase();
        
        switch (this.posicao) {
            case "centro":
            	this.setPreferredSize(new Dimension(768, 480));
                this.setBackground(Color.BLACK);
                
                setJogador(new player());

                
                ET = new escutadorTeclado();
                GL = new gameLoop(this, ET);
                SL = new spriteLoop(this, ET);
                
                this.addKeyListener(ET);
                this.setFocusable(true); 
                
                GL.start();
                SL.start();
                
                this.cenario = new tileMap();

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

            	g2.scale((double) getWidth() / 768, (double) getHeight() / 480);

            	this.cenario.desenhar(g2);
            	getJogador().desenharPlayer(g2);

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
}
