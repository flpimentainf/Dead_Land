package DEAD.LAND;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class spriteLoop extends Thread implements Runnable, ActionListener{
	private int FPS = 15;
	private Timer controleDoTempoDoJogo;
	private long contadorDeFPS;
    panel CenaDoJogo;
    escutadorTeclado ET;
    
    public spriteLoop(panel P, escutadorTeclado ET) {
        System.out.println("SpriteLoop Instanciado");
        this.CenaDoJogo = P;
        this.ET = ET;
    }
    		
	@Override
	public void run() {
		this.contadorDeFPS = 0;
        this.controleDoTempoDoJogo = new Timer(1000, this);
        this.controleDoTempoDoJogo.start();
        //------------------------------------------------
        double frameRate = 1000000000/this.FPS;
        double tempoDecorrido = 0;
        long tempoUltimaMedidaDoLoop = System.nanoTime();
        long tempoAtualDoLoop;
        //------------------------------------------------
        while (this.isAlive()) {
        	tempoAtualDoLoop = System.nanoTime();
        	tempoDecorrido = tempoDecorrido +
        			(tempoAtualDoLoop - tempoUltimaMedidaDoLoop)/frameRate;
        	tempoUltimaMedidaDoLoop = tempoAtualDoLoop;
        	
        	if (tempoDecorrido >=1) {
        		CenaDoJogo.jogador.atualizarSprite(ET.movePraEsq, ET.movePraCima, 
						ET.movePraDir, ET.movePraBaixo);
        		CenaDoJogo.repaint();
        		this.contadorDeFPS++;
        		tempoDecorrido = 0;
        	}
        }
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("FPS Sprite: " + this.contadorDeFPS);
		this.contadorDeFPS = 0;
	}
	
}
