package DEAD.LAND.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;


public class gameLoop extends Thread implements Runnable, ActionListener{
	private int FPS = 60;
	private Timer controleDoTempoDoJogo;
	private long contadorDeFPS;
	private ControladorMovimento controladorMovimento;
	private ControladorQueda controladorQueda;
	private ControladorInteracao controladorInteracao;
	private ControladorItens controladorItens;
    panel CenaDoJogo;
    escutadorTeclado ET;
	
    public gameLoop(panel P, escutadorTeclado ET) {
        System.out.println("GameLoop Instanciado");
        this.CenaDoJogo = P;
        this.ET = ET;
		this.controladorMovimento = new ControladorMovimento();
		this.controladorQueda = new ControladorQueda();
		this.controladorInteracao = new ControladorInteracao();
		this.controladorItens = new ControladorItens();
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

                if (CenaDoJogo.getCenario() != null) {

                    if (this.controladorQueda.atualizar(CenaDoJogo)) {
                        this.controladorInteracao.resetar();
                    } else {
                        this.controladorMovimento.atualizar(CenaDoJogo, ET);
                        this.controladorInteracao.atualizar(CenaDoJogo, ET);
                        this.controladorItens.atualizar(CenaDoJogo);
                    }
                }

        		CenaDoJogo.repaint();
        		this.contadorDeFPS++;
        		tempoDecorrido = 0;
        	}
        }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("FPS GameLoop: " + this.contadorDeFPS);
		this.contadorDeFPS = 0;
		
	}

}
