package DEAD.LAND.entity;

import javax.swing.ImageIcon;

public class Boss extends Inimigo {

    public Boss(int x, int y) {
        super(x, y);

        this.width = 128;
        this.height = 128;

        this.vida = 300;
        atualizarAreaColisao();
        
        String base = "repos/NPCs e Inimigos/Inimigos/Wizard/";

        for (int i = 0; i < 3; i++) {

            spritesEsq[i] =
                new ImageIcon(base + "left" + (i + 1) + ".png")
                .getImage();

            spritesDir[i] =
                new ImageIcon(base + "right" + (i + 1) + ".png")
                .getImage();

            spritesAtaqueEsq[i] =
                new ImageIcon(base + "attackleft" + (i + 1) + ".png")
                .getImage();

            spritesAtaqueDir[i] =
                new ImageIcon(base + "attackright" + (i + 1) + ".png")
                .getImage();
        }
    }

    @Override
    protected void atualizarAreaColisao() {
        // O sprite é grande e pode aparecer por cima das paredes, mas somente
        // a região dos pés deve bloquear o movimento no mapa.
        areaColisao.x = this.x + 19;
        areaColisao.y = this.y + 80;
        areaColisao.width = 90;
        areaColisao.height = 48;
    }


}
