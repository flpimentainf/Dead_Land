package DEAD.LAND.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class player extends Rectangle{
    private static final int VELOCIDADE_QUEDA_INICIAL = 1;
    private static final int VELOCIDADE_QUEDA_MAXIMA = 6;
    private static final int GRAVIDADE_QUEDA = 1;
    private static final int FRAMES_PARA_TROCAR_SPRITE_QUEDA = 8;

    private Color CorFundo = Color.WHITE;
    public Rectangle AreaColisao;
    public int passo = 3;
    private boolean caindo;
    private int velocidadeQueda;
    private int contadorFramesQueda;
    
    Image[]imgPlayerDown = new Image[3];
    Image[]imgPlayerRight = new Image[3];
    Image[]imgPlayerLeft = new Image[3];
    Image[]imgPlayerUp = new Image[3];
    Image imagemPlayer;
    private int frameJogador = 0;

    public player() {
        this.x = 745;
        this.y = 335;
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

    public void iniciarQueda() {
        this.caindo = true;
        this.velocidadeQueda = VELOCIDADE_QUEDA_INICIAL;
        this.contadorFramesQueda = 0;
        this.frameJogador = 0;
        this.imagemPlayer = this.imgPlayerDown[this.frameJogador];
        atualizarAreaColisao();
    }

    public void pararQueda() {
        this.caindo = false;
        this.velocidadeQueda = 0;
        atualizarAreaColisao();
    }

    public boolean estaCaindo() {
        return this.caindo;
    }

    public void atualizarQueda() {
        this.y += this.velocidadeQueda;

        if (this.velocidadeQueda < VELOCIDADE_QUEDA_MAXIMA) {
            this.velocidadeQueda += GRAVIDADE_QUEDA;
        }

        atualizarSpriteQueda();
        atualizarAreaColisao();
    }

    private void atualizarSpriteQueda() {
        this.contadorFramesQueda++;

        if (this.contadorFramesQueda < FRAMES_PARA_TROCAR_SPRITE_QUEDA) {
            return;
        }

        this.contadorFramesQueda = 0;
        this.frameJogador++;

        if (this.frameJogador >= this.imgPlayerDown.length) {
            this.frameJogador = 0;
        }

        this.imagemPlayer = this.imgPlayerDown[this.frameJogador];
    }
    
    public void atualizarSprite(boolean moveEsq, boolean moveCima,
            boolean moveDir, boolean moveBaixo) {
        if (this.caindo) {
            return;
        }

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
