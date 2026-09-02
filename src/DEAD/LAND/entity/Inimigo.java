package DEAD.LAND.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import DEAD.LAND.core.verificadorDeColisao;
import DEAD.LAND.world.tileMap;

public class Inimigo extends Rectangle {
    public static final int VIDA_MAXIMA = 60;
    private static final int VELOCIDADE = 3;
    private static final int DANO_NO_JOGADOR = 15;
    private static final int FRAMES_ATAQUE = 90;
    private static final int FRAMES_SPRITE = 12;
    private static final int ALCANCE_DETECCAO = 120;

    protected int vida = VIDA_MAXIMA;
    protected int vidaMaxima = VIDA_MAXIMA;
    protected boolean vivo = true;
    private final int origemX;
    private final int origemY;
    private String direcao = "direita";
    private int frameAtual = 0;
    private int contadorSprite = 0;
    private int contadorAtaque = 0;
    private boolean atacando = false;

    protected Image[] spritesEsq = new Image[3];
    protected Image[] spritesDir = new Image[3];
    protected Image[] spritesAtaqueEsq = new Image[3];
    protected Image[] spritesAtaqueDir = new Image[3];

    public Rectangle areaColisao;

    public Inimigo(int x, int y) {
        this.origemX = x;
        this.origemY = y;
        this.x = x;
        this.y = y;
        this.width = 48;
        this.height = 48;
        this.areaColisao = new Rectangle(x + 6, y + 24, 36, 24);

        String base = "repos/NPCs e Inimigos/Inimigos/Skeleton/";
        for (int i = 0; i < 3; i++) {
            spritesEsq[i]      = new ImageIcon(base + "left"        + (i+1) + ".png").getImage();
            spritesDir[i]      = new ImageIcon(base + "right"       + (i+1) + ".png").getImage();
            spritesAtaqueEsq[i] = new ImageIcon(base + "attackleft" + (i+1) + ".png").getImage();
            spritesAtaqueDir[i] = new ImageIcon(base + "attackright"+ (i+1) + ".png").getImage();
        }
    }

    public void atualizar(
            player jogador,
            tileMap cenario,
            verificadorDeColisao verificadorColisao
    ) {
        if (!vivo || jogador == null || jogador.estaMorto()) return;

        atualizarAreaColisao();
        contadorAtaque = Math.max(0, contadorAtaque - 1);

        int dx = (jogador.x + jogador.width / 2) - (this.x + this.width / 2);
        int dy = (jogador.y + jogador.height / 2) - (this.y + this.height / 2);
        double dist = Math.sqrt(dx * dx + dy * dy);

        atacando = false;

        if (dist < getAlcanceDeteccao()) {
            // Perseguir
            if (Math.abs(dx) > Math.abs(dy)) {
                direcao = dx > 0 ? "direita" : "esquerda";
            } else {
                direcao = dy > 0 ? "baixo" : "cima";
            }

            if (dist > getAlcanceAtaque()) {
                int movimentoX = (int)(getVelocidade() * dx / dist);
                int movimentoY = (int)(getVelocidade() * dy / dist);
                moverEixoSePossivel(cenario, verificadorColisao, movimentoX, 0);
                moverEixoSePossivel(cenario, verificadorColisao, 0, movimentoY);
            } else {
                // Está perto — atacar
                atacando = true;
                if (contadorAtaque == 0) {
                    jogador.levarDano(
                            getDanoNoJogador(),
                            this.x + this.width / 2,
                            this.y + this.height / 2
                    );
                    contadorAtaque = getFramesAtaque();
                }
            }
        }

        // Atualizar sprite
        contadorSprite++;
        if (contadorSprite >= FRAMES_SPRITE) {
            contadorSprite = 0;
            frameAtual = (frameAtual + 1) % 3;
        }
    }

    private void moverEixoSePossivel(
            tileMap cenario,
            verificadorDeColisao verificadorColisao,
            int movimentoX,
            int movimentoY
    ) {
        if (movimentoX == 0 && movimentoY == 0) {
            return;
        }

        atualizarAreaColisao();
        if (!verificadorColisao.ocorreuColisao(
                areaColisao,
                cenario,
                movimentoX,
                movimentoY
        )) {
            this.x += movimentoX;
            this.y += movimentoY;
            atualizarAreaColisao();
        }
    }

    public void desenhar(Graphics2D g2) {
        if (!vivo) return;

        Image sprite;
        if (atacando) {
            sprite = direcao.equals("esquerda") ? spritesAtaqueEsq[frameAtual] : spritesAtaqueDir[frameAtual];
        } else {
            sprite = direcao.equals("esquerda") ? spritesEsq[frameAtual] : spritesDir[frameAtual];
        }
        g2.drawImage(sprite, x, y, width, height, null);

        // Barra de vida mini acima do inimigo
        int bw = 36;
        int bh = 5;
        int bx = x + (width - bw) / 2;
        int by = y - 8;
        g2.setColor(new Color(60, 60, 60, 200));
        g2.fillRect(bx, by, bw, bh);
        float prop = (float) vida / vidaMaxima;
        g2.setColor(prop > 0.5f ? new Color(60, 200, 60) : prop > 0.25f ? new Color(230, 180, 0) : new Color(220, 40, 40));
        g2.fillRect(bx, by, (int)(bw * prop), bh);
        g2.setColor(Color.WHITE);
        g2.drawRect(bx, by, bw, bh);
    }

    public void levarDano(int dano) {
        if (!vivo) return;
        vida = Math.max(0, vida - dano);
        if (vida == 0) vivo = false;
    }

    public void resetarParaOrigem() {
        this.x = origemX;
        this.y = origemY;
        this.vida = this.vidaMaxima;
        this.vivo = true;
        this.atacando = false;
        this.contadorAtaque = 0;
        this.contadorSprite = 0;
        this.frameAtual = 0;
        atualizarAreaColisao();
    }

    public void marcarDerrotado() {
        this.vida = 0;
        this.vivo = false;
        this.atacando = false;
        this.contadorAtaque = 0;
    }

    public boolean estaEmCombateCom(player jogador) {
        if (!vivo || jogador == null || jogador.estaMorto()) {
            return false;
        }

        int dx = (jogador.x + jogador.width / 2) - (this.x + this.width / 2);
        int dy = (jogador.y + jogador.height / 2) - (this.y + this.height / 2);
        double dist = Math.sqrt(dx * dx + dy * dy);

        return dist < getAlcanceDeteccao() + 40 || atacando || contadorAtaque > 0;
    }

    public boolean estaVivo() { return vivo; }
    public int getCenarioIndex() { return cenarioIndex; }
    public Rectangle getAreaDano() { return areaColisao; }

    protected int getVelocidade() { return VELOCIDADE; }
    protected int getDanoNoJogador() { return DANO_NO_JOGADOR; }
    protected int getFramesAtaque() { return FRAMES_ATAQUE; }
    protected int getAlcanceDeteccao() { return ALCANCE_DETECCAO; }
    protected int getAlcanceAtaque() { return 30; }

    private int cenarioIndex;
    public void setCenarioIndex(int i) { this.cenarioIndex = i; }
 
    protected void atualizarAreaColisao() {
        areaColisao.x = this.x + 6;
        areaColisao.y = this.y + 24;
        areaColisao.width = 36;
        areaColisao.height = 24;
    }
}
