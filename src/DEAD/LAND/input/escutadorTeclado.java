package DEAD.LAND.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class escutadorTeclado implements KeyListener{
	public boolean movePraBaixo, movePraCima, movePraEsq, movePraDir;
	public boolean interagir;
	public boolean atirar;


	@Override
	public void keyTyped(KeyEvent e) {
		// NÃO SERÁ UTILIZADA, MAS NÃO PODE SER APAGADA
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int numTecla = e.getKeyCode();
		switch (numTecla) {
		case 37: //esquerda
			this.movePraEsq = true;
			break;
		case 38: //cima
			this.movePraCima = true;
			break;
		case 39: //direita
			this.movePraDir = true;
			break;
		case 40: //baixo
			this.movePraBaixo = true;
			break;
		case KeyEvent.VK_E:
			this.interagir = true;
			break;
		case KeyEvent.VK_SPACE:
			this.atirar = true;
			break;
		default:
			System.out.println("Tecla sem Efeito");
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int numTecla = e.getKeyCode();
		switch (numTecla) {
		case 37: //esquerda
			this.movePraEsq = false;
			break;
		case 38: //cima
			this.movePraCima = false;
			break;
		case 39: //direita
			this.movePraDir = false;
			break;
		case 40: //baixo
			this.movePraBaixo = false;
			break;
		case KeyEvent.VK_E:
			this.interagir = false;
			break;
		case KeyEvent.VK_SPACE:
			this.atirar = false;
			break;
		default:
			System.out.println("Tecla sem Efeito");
			break;
		}
	}

}
