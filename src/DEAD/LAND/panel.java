package DEAD.LAND;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;


public class panel extends JPanel{
	private String posicao;
	gameLoop GL;
	escutadorTeclado ET;
	spriteLoop SL;
    player jogador;
    tileMap cenario;


	
    public panel(String posicao) {
        this.posicao = posicao.toLowerCase();
        
        switch (this.posicao) {
            case "centro":
            	this.setPreferredSize(new Dimension(768, 480));
                this.setBackground(Color.BLACK);
                
                jogador = new player();

                
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
            	
            	this.cenario.desenhar(g2);
            	jogador.desenharPlayer(g2);

                break;
                
            case "sul":
            	g2.setColor(Color.YELLOW);
            	g2.fillRect(0, 0, getWidth(), getHeight());

                break;
                
        }
    }
}
