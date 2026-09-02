package DEAD.LAND.entity;

import DEAD.LAND.core.verificadorDeColisao;
import DEAD.LAND.core.ResourceManager;
import DEAD.LAND.world.tileMap;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class player extends Rectangle{
    private static final int VELOCIDADE_QUEDA_INICIAL = 1;
    private static final int VELOCIDADE_QUEDA_MAXIMA = 6;
    private static final int GRAVIDADE_QUEDA = 1;
    private static final int FRAMES_PARA_TROCAR_SPRITE_QUEDA = 8;

    public static final int VIDA_MAXIMA = 100;
    private static final int FRAMES_INVENCIVEL = 60;
    private static final int FRAMES_FLASH_DANO = 10;
    private static final int FRAMES_KNOCKBACK = 5;
    private static final int FORCA_KNOCKBACK = 5;
    private static final int VELOCIDADE_DASH = 12;
    private static final long DURACAO_DASH_MS = 240;
    private static final long INVENCIBILIDADE_DASH_MS = 170;
    private static final long COOLDOWN_DASH_MS = 1000;

    private Color CorFundo = Color.WHITE;
    public Rectangle AreaColisao;
    public int passo = 4;
    private boolean caindo;
    private int vida = VIDA_MAXIMA;
    private int framesInvencivel = 0;
    private int framesFlashDano = 0;
    private int framesKnockback = 0;
    private int knockbackX = 0;
    private int knockbackY = 0;
    private boolean dashAtivo;
    private int direcaoDashX;
    private int direcaoDashY;
    private long fimDashMs;
    private long invulneravelDashAteMs;
    private long ultimoDashMs = -COOLDOWN_DASH_MS;
    private int velocidadeQueda;
    private int contadorFramesQueda;
    private String direcao = "baixo";
    
    Image[]imgPlayerDown = new Image[3];
    Image[]imgPlayerRight = new Image[3];
    Image[]imgPlayerLeft = new Image[3];
    Image[]imgPlayerUp = new Image[3];
    Image[]imgPlayerFall = new Image[3];
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
            this.imgPlayerDown[i] = ResourceManager.getInstancia().carregarImagem("repos/PLAYERS/down" + (i+1) + ".png");
            this.imgPlayerRight[i] = ResourceManager.getInstancia().carregarImagem("repos/PLAYERS/right" + (i+1) + ".png");
            this.imgPlayerLeft[i] = ResourceManager.getInstancia().carregarImagem("repos/PLAYERS/left" + (i+1) + ".png");
            this.imgPlayerUp[i] = ResourceManager.getInstancia().carregarImagem("repos/PLAYERS/up" + (i+1) + ".png");
            this.imgPlayerFall[i] = ResourceManager.getInstancia().carregarImagem("repos/PLAYERS/fall" + (i+1) + ".png");
        } 
        this.imagemPlayer = this.imgPlayerDown[frameJogador];

    }
    public void desenharPlayer(Graphics2D g) {
        g.setColor(this.CorFundo);
        // g.fillRect(this.x, this.y, this.width, this.height);
        // Para visualizar a area de colisao, descomente a linha abaixo.
        // g.fillRect(this.AreaColisao.x, this.AreaColisao.y, this.AreaColisao.width, this.AreaColisao.height);

        Composite composicaoOriginal = g.getComposite();
        if (estaInvencivel() && !estaMorto()) {
            float alpha = (framesInvencivel / 6) % 2 == 0 ? 0.45f : 1.0f;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        g.drawImage(imagemPlayer, x, y, width, height, null);
        g.setComposite(composicaoOriginal);

        if (framesFlashDano > 0) {
            float intensidade = getIntensidadeFlashDano();
            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    Math.min(0.45f, intensidade * 0.45f)
            ));
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
            g.setComposite(composicaoOriginal);
        }

        if (estaEmDash()) {
            g.setColor(new Color(120, 210, 255, 150));
            g.drawRect(x - 2, y - 2, width + 4, height + 4);
        }
    }
    
    public void mover(int movimentoX, int movimentoY) {
        this.x += movimentoX;
        this.y += movimentoY;
        atualizarAreaColisao();
    }

    public void posicionarEm(int x, int y) {
        this.x = x;
        this.y = y;
        atualizarAreaColisao();
    }

    public String getDirecao() { return direcao; }

    public int getVida() { return vida; }
    public boolean estaVivo() { return vida > 0; }
    public boolean estaMorto() { return vida <= 0; }

    public void definirVida(int vida) {
        this.vida = Math.max(0, Math.min(VIDA_MAXIMA, vida));
    }

    public boolean levarDano(int dano) {
        return levarDano(dano, this.x + this.width / 2, this.y + this.height / 2);
    }

    public boolean levarDano(int dano, int origemX, int origemY) {
        if (dano <= 0 || estaMorto() || estaInvencivel()) return false;
        vida = Math.max(0, vida - dano);
        framesInvencivel = FRAMES_INVENCIVEL;
        framesFlashDano = FRAMES_FLASH_DANO;
        prepararKnockback(origemX, origemY);

        if (vida == 0) {
            interromperMovimentoTemporario();
        }

        return true;
    }

    public void curar(int quantidade) {
        if (quantidade <= 0 || estaMorto()) return;
        vida = Math.min(VIDA_MAXIMA, vida + quantidade);
    }

    public void restaurarVidaCompleta() {
        vida = VIDA_MAXIMA;
    }

    public void prepararRespawn() {
        restaurarVidaCompleta();
        interromperMovimentoTemporario();
        framesFlashDano = 0;
        framesInvencivel = FRAMES_INVENCIVEL;
    }

    public void atualizarInvencibilidade() {
        if (framesInvencivel > 0) framesInvencivel--;
        if (framesFlashDano > 0) framesFlashDano--;
    }

    public boolean estaInvencivel() {
        return framesInvencivel > 0 || System.currentTimeMillis() < invulneravelDashAteMs;
    }

    public float getIntensidadeFlashDano() {
        if (framesFlashDano <= 0) return 0f;
        return framesFlashDano / (float) FRAMES_FLASH_DANO;
    }

    public void atualizarEfeitos(tileMap cenario, verificadorDeColisao verificador) {
        atualizarInvencibilidade();
        atualizarKnockback(cenario, verificador);
    }

    public boolean iniciarDash(boolean moveEsq, boolean moveCima, boolean moveDir, boolean moveBaixo) {
        long agora = System.currentTimeMillis();
        if (estaMorto() || caindo || dashAtivo || agora - ultimoDashMs < COOLDOWN_DASH_MS) {
            return false;
        }

        int dx = 0;
        int dy = 0;
        if (moveEsq) dx--;
        if (moveDir) dx++;
        if (moveCima) dy--;
        if (moveBaixo) dy++;

        if (dx == 0 && dy == 0) {
            if ("esquerda".equals(direcao)) dx = -1;
            else if ("direita".equals(direcao)) dx = 1;
            else if ("cima".equals(direcao)) dy = -1;
            else dy = 1;
        }

        this.direcaoDashX = dx;
        this.direcaoDashY = dy;
        this.dashAtivo = true;
        this.ultimoDashMs = agora;
        this.fimDashMs = agora + DURACAO_DASH_MS;
        this.invulneravelDashAteMs = agora + INVENCIBILIDADE_DASH_MS;
        return true;
    }

    public boolean atualizarDash(tileMap cenario, verificadorDeColisao verificador) {
        if (!dashAtivo) {
            return false;
        }

        if (System.currentTimeMillis() >= fimDashMs || estaMorto()) {
            dashAtivo = false;
            return false;
        }

        int movimentoX = direcaoDashX * VELOCIDADE_DASH;
        int movimentoY = direcaoDashY * VELOCIDADE_DASH;
        if (direcaoDashX != 0 && direcaoDashY != 0) {
            int diagonal = Math.max(1, (int) Math.round(VELOCIDADE_DASH / Math.sqrt(2)));
            movimentoX = direcaoDashX * diagonal;
            movimentoY = direcaoDashY * diagonal;
        }

        moverComColisao(cenario, verificador, movimentoX, 0);
        moverComColisao(cenario, verificador, 0, movimentoY);
        return true;
    }

    public boolean estaEmDash() {
        return dashAtivo;
    }

    public float getProgressoCooldownDash() {
        long decorrido = System.currentTimeMillis() - ultimoDashMs;
        return Math.max(0f, Math.min(1f, decorrido / (float) COOLDOWN_DASH_MS));
    }

    private void prepararKnockback(int origemX, int origemY) {
        int centroX = this.x + this.width / 2;
        int centroY = this.y + this.height / 2;
        int deltaX = centroX - origemX;
        int deltaY = centroY - origemY;

        if (deltaX == 0 && deltaY == 0) {
            deltaY = "cima".equals(direcao) ? 1 : -1;
        }

        double distancia = Math.max(1, Math.sqrt(deltaX * deltaX + deltaY * deltaY));
        this.knockbackX = (int) Math.round(FORCA_KNOCKBACK * deltaX / distancia);
        this.knockbackY = (int) Math.round(FORCA_KNOCKBACK * deltaY / distancia);
        this.framesKnockback = FRAMES_KNOCKBACK;
    }

    private void atualizarKnockback(tileMap cenario, verificadorDeColisao verificador) {
        if (framesKnockback <= 0 || (knockbackX == 0 && knockbackY == 0)) {
            return;
        }

        moverComColisao(cenario, verificador, knockbackX, 0);
        moverComColisao(cenario, verificador, 0, knockbackY);
        framesKnockback--;

        if (framesKnockback <= 0) {
            knockbackX = 0;
            knockbackY = 0;
        }
    }

    private void moverComColisao(tileMap cenario, verificadorDeColisao verificador,
            int movimentoX, int movimentoY) {
        if (movimentoX == 0 && movimentoY == 0) {
            return;
        }

        if (cenario != null && verificador != null
                && verificador.ocorreuColisao(this, cenario, movimentoX, movimentoY)) {
            return;
        }

        mover(movimentoX, movimentoY);
    }

    private void interromperMovimentoTemporario() {
        this.caindo = false;
        this.dashAtivo = false;
        this.invulneravelDashAteMs = 0;
        this.velocidadeQueda = 0;
        this.framesKnockback = 0;
        this.knockbackX = 0;
        this.knockbackY = 0;
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
        this.imagemPlayer = this.imgPlayerFall[this.frameJogador];
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

        if (this.frameJogador >= this.imgPlayerFall.length) {
            this.frameJogador = 0;
        }

        this.imagemPlayer = this.imgPlayerFall[this.frameJogador];
    }
    
    public void atualizarSprite(boolean moveEsq, boolean moveCima,
            boolean moveDir, boolean moveBaixo) {
        if (this.caindo || estaMorto()) {
            return;
        }

        this.frameJogador++;
        if (moveEsq) {
            if (frameJogador >= this.imgPlayerLeft.length)
                frameJogador = 0;
            this.direcao = "esquerda";
            this.imagemPlayer = this.imgPlayerLeft[frameJogador];
        }
        if (moveCima) {
            if (frameJogador >= this.imgPlayerUp.length)
                frameJogador = 0;
            this.direcao = "cima";
            this.imagemPlayer = this.imgPlayerUp[frameJogador];
        }
        if (moveDir) {
            if (frameJogador >= this.imgPlayerRight.length)
                frameJogador = 0;
            this.direcao = "direita";
            this.imagemPlayer = this.imgPlayerRight[frameJogador];
        }
        if (moveBaixo) {
            if (frameJogador >= this.imgPlayerDown.length)
                frameJogador = 0;
            this.direcao = "baixo";
            this.imagemPlayer = this.imgPlayerDown[frameJogador];
        }
    }
}
