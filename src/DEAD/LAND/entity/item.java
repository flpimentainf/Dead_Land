package DEAD.LAND.entity;

import DEAD.LAND.world.tiles;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class item extends Rectangle {
    private Image sprite;
    private int cenarioIndex;
    private int linha;
    private int coluna;
    private int tileSubstituto;
    private boolean coletado;

    private String nome;

    public item(String caminhoSprite, int cenarioIndex, int linha, int coluna, int tileSubstituto) {
        this(caminhoSprite, cenarioIndex, linha, coluna, tileSubstituto, "");
    }

    public item(String caminhoSprite, int cenarioIndex, int linha, int coluna, int tileSubstituto, String nome) {
        this.sprite = new ImageIcon(caminhoSprite).getImage();
        this.nome = nome;
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

    public String getNome() { return nome; }
}