package DEAD.LAND.entity;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import DEAD.LAND.world.tiles;

public class item extends Rectangle {
    private Image sprite;
    private int cenarioIndex;
    private int linha;
    private int coluna;
    private int tileSubstituto;
    private boolean coletado;

    public item(String caminhoSprite, int cenarioIndex, int linha, int coluna, int tileSubstituto) {
        this.sprite = new ImageIcon(caminhoSprite).getImage();
        this.cenarioIndex = cenarioIndex;
        this.linha = linha;
        this.coluna = coluna;
        this.tileSubstituto = tileSubstituto;
        this.coletado = false;
        this.x = coluna * tiles.LARGURA;
        this.y = linha * tiles.ALTURA;
        this.width = tiles.LARGURA;
        this.height = tiles.ALTURA;
    }

    public Image getSprite() {
        return this.sprite;
    }

    public int getCenarioIndex() {
        return this.cenarioIndex;
    }

    public int getLinha() {
        return this.linha;
    }

    public int getColuna() {
        return this.coluna;
    }

    public int getTileSubstituto() {
        return this.tileSubstituto;
    }

    public boolean foiColetado() {
        return this.coletado;
    }

    public void coletar() {
        this.coletado = true;
    }
}