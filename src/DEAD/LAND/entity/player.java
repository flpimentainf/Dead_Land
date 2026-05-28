package DEAD.LAND.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class player extends Rectangle{
    private Color CorFundo = Color.WHITE;
    public Rectangle AreaColisao;
    public int passo = 3;
    
    Image[]imgPlayerDown = new Image[3];
    Image[]imgPlayerRight = new Image[3];
    Image[]imgPlayerLeft = new Image[3];
    Image[]imgPlayerUp = new Image[3];
    Image imagemPlayer;
    private int frameJogador = 0;

    public player() {
        this.x = 200;
        this.y = 100;
        this.width = 48;
        this.height = 48;
        this.AreaColisao = new Rectangle();
        atualizarAreaColisao();
        //ImageIcon icon;
        //icon = new ImageIcon("res/PLAYERS/down1.png");
        //this.imagemPlayer = icon.getImage();
        for (int i = 0; i < 3; i++) {
            this.imgPlayerDown[i] = new ImageIcon("repos/PLAYERS/down" + (i+1) + ".png").getImage();
            this.imgPlayerRight[i] = new ImageIcon("repos/PLAYERS/right" + (i+1) + ".png").getImage();
            this.imgPlayerLeft[i] = new ImageIcon("repos/PLAYERS/left" + (i+1) + ".png").getImage();
            this.imgPlayerUp[i] = new ImageIcon("repos/PLAYERS/up" + (i+1) + ".png").getImage();
        }
        this.imagemPlayer = this.imgPlayerDown[frameJogador];

    }
    public void desenharPlayer(Graphics2D g) {
        g.setColor(this.CorFundo);
        // g.fillRect(this.x, this.y, this.width, this.height);
        // Para visualizar a area de colisao, descomente a linha abaixo.
        // g.fillRect(this.AreaColisao.x, this.AreaColisao.y, this.AreaColisao.width, this.AreaColisao.height);
        g.drawImage(imagemPlayer, x, y, width, height, null);
    }
    
    public void atualizarPosicaoJogador (boolean ME, boolean MC, boolean MD, boolean MB) {
    	if (ME)	this.x -= this.passo;
    	if (MD)	this.x += this.passo;
    	if (MC)	this.y -= this.passo;
    	if (MB)	this.y += this.passo;

        atualizarAreaColisao();
    }

    public void atualizarAreaColisao() {
        this.AreaColisao.x = this.x + 3;
        this.AreaColisao.y = this.y + this.height / 2;
        this.AreaColisao.width = this.width - 20;
        this.AreaColisao.height = this.height / 2;
    }
    
    public void atualizarSprite(boolean moveEsq, boolean moveCima,
            boolean moveDir, boolean moveBaixo) {
        this.frameJogador++;
        if (moveEsq) {
            if (frameJogador >= this.imgPlayerLeft.length)
                frameJogador = 0;
            
            this.imagemPlayer = this.imgPlayerLeft[frameJogador];
        }
        if (moveCima) {
            if (frameJogador >= this.imgPlayerUp.length)
                frameJogador = 0;
            
            this.imagemPlayer = this.imgPlayerUp[frameJogador];
        }
        if (moveDir) {
            if (frameJogador >= this.imgPlayerRight.length)
                frameJogador = 0;
            
            this.imagemPlayer = this.imgPlayerRight[frameJogador];
        }
        if (moveBaixo) {
            if (frameJogador >= this.imgPlayerDown.length)
                frameJogador = 0;
            
            this.imagemPlayer = this.imgPlayerDown[frameJogador];
        }
    }
}
