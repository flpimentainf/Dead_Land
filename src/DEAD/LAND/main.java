package DEAD.LAND;
/*
 * CAP09 - GAME2D EM JAVA
 * 		   SPRITE & TILES
 * AUTOR:  FRANCISCO LARA PIMENTA
 * DATA:   29/04/2026
 */

import DEAD.LAND.ui.moldura;
import javax.swing.SwingUtilities;

public class main {

	public static void main(String[] args) {
		System.out.println("DEAD LAND");
		SwingUtilities.invokeLater(moldura::new);
	}

}
