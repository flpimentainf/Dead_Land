package DEAD.LAND.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class escutadorTeclado implements KeyListener{
	public boolean movePraBaixo, movePraCima, movePraEsq, movePraDir;
	public boolean interagir;
	public boolean atirar;
	public boolean dash;
	public boolean usarItem;
	public boolean confirmar;
	public boolean abrirMenu;

	public void resetar() {
		movePraBaixo = false;
		movePraCima = false;
		movePraEsq = false;
		movePraDir = false;
		interagir = false;
		atirar = false;
		dash = false;
		usarItem = false;
		confirmar = false;
		abrirMenu = false;
	}


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
			this.confirmar = true;
			break;
		case KeyEvent.VK_ENTER:
			this.confirmar = true;
			break;
		case KeyEvent.VK_ESCAPE:
			this.abrirMenu = true;
			break;
		case KeyEvent.VK_SPACE:
			this.atirar = true;
			break;
		case KeyEvent.VK_SHIFT:
			this.dash = true;
			break;
		case KeyEvent.VK_Q:
			this.usarItem = true;
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
			this.confirmar = false;
			break;
		case KeyEvent.VK_ENTER:
			this.confirmar = false;
			break;
		case KeyEvent.VK_ESCAPE:
			this.abrirMenu = false;
			break;
		case KeyEvent.VK_SPACE:
			this.atirar = false;
			break;
		case KeyEvent.VK_SHIFT:
			this.dash = false;
			break;
		case KeyEvent.VK_Q:
			this.usarItem = false;
			break;
		default:
			System.out.println("Tecla sem Efeito");
			break;
		}
	}

}
