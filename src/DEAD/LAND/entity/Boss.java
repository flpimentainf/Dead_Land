package DEAD.LAND.entity;

import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Boss extends Inimigo {
    public static final int VIDA_MAXIMA_BOSS = 600;

    private final Rectangle areaDano = new Rectangle();

    public Boss(int x, int y) {
        super(x, y);

        this.width = 128;
        this.height = 128;

        this.vidaMaxima = VIDA_MAXIMA_BOSS;
        this.vida = VIDA_MAXIMA_BOSS;
        atualizarAreaColisao();
        atualizarAreaDano();
        
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
        atualizarAreaDano();
    }

    private void atualizarAreaDano() {
        // Flechas podem atingir o corpo do chefe, não apenas a região dos pés.
        areaDano.x = this.x + 24;
        areaDano.y = this.y + 16;
        areaDano.width = 80;
        areaDano.height = 108;
    }

    @Override
    public Rectangle getAreaDano() {
        atualizarAreaDano();
        return areaDano;
    }

    @Override
    protected int getVelocidade() {
        return 4;
    }

    @Override
    protected int getDanoNoJogador() {
        return 25;
    }

    @Override
    protected int getFramesAtaque() {
        return 60;
    }

    @Override
    protected int getAlcanceDeteccao() {
        return 260;
    }

    @Override
    protected int getAlcanceAtaque() {
        return 82;
    }


}
