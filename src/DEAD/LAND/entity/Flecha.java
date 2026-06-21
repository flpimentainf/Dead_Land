package DEAD.LAND.entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Flecha extends Rectangle {
    private static final int VELOCIDADE = 7;
    private static final int TAMANHO = 16;

    private String direcao;
    private boolean ativa;
    private Image sprite;

    public Flecha(int x, int y, String direcao) {
        this.x = x - TAMANHO / 2;
        this.y = y - TAMANHO / 2;
        this.width = TAMANHO;
        this.height = TAMANHO;
        this.direcao = direcao;
        this.ativa = true;
        this.sprite = new ImageIcon("repos/tiles/tile (48).png").getImage();
    }

    public void atualizar() {
        switch (direcao) {
            case "cima":     y -= VELOCIDADE; break;
            case "baixo":    y += VELOCIDADE; break;
            case "esquerda": x -= VELOCIDADE; break;
            case "direita":  x += VELOCIDADE; break;
        }
    }

    public void desenhar(Graphics2D g) {
        if (!ativa) return;
        g.drawImage(sprite, x, y, width, height, null);
    }

    public boolean isAtiva() { return ativa; }
    public void desativar() { ativa = false; }
}
