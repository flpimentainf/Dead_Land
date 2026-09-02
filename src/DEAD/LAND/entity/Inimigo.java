package DEAD.LAND.entity;

import DEAD.LAND.core.AudioManager;
import DEAD.LAND.core.ControladorProjeteisInimigos;
import DEAD.LAND.core.ResourceManager;
import DEAD.LAND.core.verificadorDeColisao;
import DEAD.LAND.world.tileMap;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Random;

public class Inimigo extends Rectangle {
    public static final int VIDA_MAXIMA = 60;

    protected int vida = VIDA_MAXIMA;
    protected int vidaMaxima = VIDA_MAXIMA;
    protected boolean vivo = true;
    protected TipoInimigo tipo = TipoInimigo.SKELETON;
    protected EstadoInimigo estado = EstadoInimigo.IDLE;
    protected String direcao = "direita";
    protected Image[] spritesEsq = new Image[3];
    protected Image[] spritesDir = new Image[3];
    protected Image[] spritesAtaqueEsq = new Image[3];
    protected Image[] spritesAtaqueDir = new Image[3];
    public Rectangle areaColisao;

    private final Random random = new Random();
    private final int origemX;
    private final int origemY;
    private int cenarioIndex;
    private int raioPatrulha = 96;
    private int frameAtual;
    private int contadorSprite;
    private int framesFlashDano;
    private int framesKnockback;
    private int knockbackX;
    private int knockbackY;
    private int velocidade = 3;
    private int danoNoJogador = 15;
    private int alcanceDeteccao = 140;
    private int alcanceAtaque = 34;
    private int alcancePreferido = 170;
    private long cooldownAtaqueMs = 1000;
    private long estadoInicioMs;
    private long ultimoAtaqueMs = -2000;
    private long resolverAtaqueMs;
    private long fimAtaqueMs;
    private long fimMorteMs;
    private long proximaMudancaPatrulhaMs;
    private int patrulhaX;
    private int patrulhaY;
    private int alvoAtaqueX;
    private int alvoAtaqueY;
    private boolean ataqueResolvido;
    private boolean dropPendente;

    public Inimigo(int x, int y) {
        this(x, y, TipoInimigo.SKELETON, "direita", 96);
    }

    public Inimigo(int x, int y, TipoInimigo tipo, String direcaoInicial, int raioPatrulha) {
        this.origemX = x;
        this.origemY = y;
        this.x = x;
        this.y = y;
        this.width = 48;
        this.height = 48;
        this.areaColisao = new Rectangle(x + 6, y + 24, 36, 24);
        this.raioPatrulha = Math.max(32, raioPatrulha);
        this.direcao = direcaoInicial == null ? "direita" : direcaoInicial;
        configurarTipo(tipo);
        escolherPontoPatrulha();
        mudarEstado(EstadoInimigo.PATROL);
    }

    public void atualizar(player jogador, tileMap cenario, verificadorDeColisao verificadorColisao) {
        atualizar(jogador, cenario, verificadorColisao, null);
    }

    public void atualizar(player jogador, tileMap cenario, verificadorDeColisao verificadorColisao,
            ControladorProjeteisInimigos controladorProjeteis) {
        if (!vivo || jogador == null || jogador.estaMorto()) {
            return;
        }

        atualizarAreaColisao();
        atualizarAnimacao();
        atualizarKnockback(cenario, verificadorColisao);

        long agora = System.currentTimeMillis();
        if (estado == EstadoInimigo.DEAD) {
            if (agora >= fimMorteMs) {
                vivo = false;
            }
            return;
        }

        if (estado == EstadoInimigo.ATTACK) {
            atualizarAtaque(jogador, cenario, verificadorColisao, controladorProjeteis, agora);
            return;
        }

        if ((estado == EstadoInimigo.HURT || estado == EstadoInimigo.STUN)
                && agora - estadoInicioMs < 220) {
            return;
        }

        double distanciaJogador = distanciaAte(jogador.x + jogador.width / 2, jogador.y + jogador.height / 2);
        if (distanciaJogador <= getAlcanceDeteccao()) {
            if (estado != EstadoInimigo.ALERT && estado != EstadoInimigo.CHASE) {
                mudarEstado(EstadoInimigo.ALERT);
                return;
            }

            if (estado == EstadoInimigo.ALERT && agora - estadoInicioMs < 280) {
                olharPara(jogador.x, jogador.y);
                return;
            }

            atualizarComportamentoDeCombate(jogador, cenario, verificadorColisao, controladorProjeteis, agora);
            return;
        }

        if (distanciaAte(origemX, origemY) > raioPatrulha + 30) {
            mudarEstadoSeNecessario(EstadoInimigo.RETURN);
            moverNaDirecao(cenario, verificadorColisao, origemX, origemY, getVelocidade());
            return;
        }

        atualizarPatrulha(cenario, verificadorColisao, agora);
    }

    public void desenhar(Graphics2D g2) {
        if (!vivo) return;

        Composite composicaoOriginal = g2.getComposite();
        if (estado == EstadoInimigo.DEAD) {
            float alpha = Math.max(0.1f, (fimMorteMs - System.currentTimeMillis()) / 700f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, alpha)));
        }

        Image sprite = getSpriteAtual();
        g2.drawImage(sprite, x, y, width, height, null);

        if (framesFlashDano > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, width, height);
        }

        g2.setComposite(composicaoOriginal);

        if (estado == EstadoInimigo.ALERT) {
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(new Color(255, 230, 90));
            g2.drawString("!", x + width / 2 - 3, y - 12);
        }

        if (estado != EstadoInimigo.DEAD) {
            desenharBarraVida(g2);
        }
    }

    public void levarDano(int dano) {
        levarDano(dano, x + width / 2, y + height / 2, 7);
    }

    public void levarDano(int dano, int origemDanoX, int origemDanoY, int forcaKnockback) {
        if (!podeSerAtingido() || dano <= 0) return;

        vida = Math.max(0, vida - dano);
        framesFlashDano = 10;
        prepararKnockback(origemDanoX, origemDanoY, forcaKnockback);
        AudioManager.getInstancia().tocarEfeito("inimigo_dano");

        if (vida == 0) {
            iniciarMorte();
        } else {
            mudarEstado(EstadoInimigo.HURT);
        }
    }

    public void resetarParaOrigem() {
        this.x = origemX;
        this.y = origemY;
        this.vida = this.vidaMaxima;
        this.vivo = true;
        this.dropPendente = false;
        this.framesFlashDano = 0;
        this.framesKnockback = 0;
        this.knockbackX = 0;
        this.knockbackY = 0;
        escolherPontoPatrulha();
        mudarEstado(EstadoInimigo.PATROL);
        atualizarAreaColisao();
    }

    public void marcarDerrotado() {
        this.vida = 0;
        this.vivo = false;
        this.dropPendente = false;
        this.estado = EstadoInimigo.DEAD;
    }

    public boolean estaEmCombateCom(player jogador) {
        if (!vivo || jogador == null || jogador.estaMorto()) {
            return false;
        }

        double dist = distanciaAte(jogador.x + jogador.width / 2, jogador.y + jogador.height / 2);
        return dist < getAlcanceDeteccao() + 40
                || estado == EstadoInimigo.ALERT
                || estado == EstadoInimigo.CHASE
                || estado == EstadoInimigo.ATTACK;
    }

    public void separarDe(Inimigo outro, tileMap cenario, verificadorDeColisao verificadorColisao) {
        if (outro == null || outro == this || !vivo || !outro.vivo
                || this.estado == EstadoInimigo.DEAD || outro.estado == EstadoInimigo.DEAD
                || this.cenarioIndex != outro.cenarioIndex) {
            return;
        }

        if (!this.areaColisao.intersects(outro.areaColisao)) {
            return;
        }

        int dx = (this.x + this.width / 2) - (outro.x + outro.width / 2);
        int dy = (this.y + this.height / 2) - (outro.y + outro.height / 2);
        if (dx == 0 && dy == 0) {
            dx = 1;
        }

        int movimentoX = Integer.signum(dx) * 2;
        int movimentoY = Integer.signum(dy) * 2;
        moverEixoSePossivel(cenario, verificadorColisao, movimentoX, 0);
        moverEixoSePossivel(cenario, verificadorColisao, 0, movimentoY);
    }

    public boolean consumirDropPendente() {
        if (!dropPendente) {
            return false;
        }

        dropPendente = false;
        return true;
    }

    public boolean podeSerAtingido() {
        return vivo && estado != EstadoInimigo.DEAD;
    }

    public boolean foiDerrotado() {
        return estado == EstadoInimigo.DEAD || !vivo;
    }

    public boolean estaVivo() { return vivo; }
    public int getCenarioIndex() { return cenarioIndex; }
    public void setCenarioIndex(int i) { this.cenarioIndex = i; }
    public Rectangle getAreaDano() { atualizarAreaColisao(); return areaColisao; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public TipoInimigo getTipo() { return tipo; }
    public EstadoInimigo getEstado() { return estado; }

    protected int getVelocidade() { return velocidade; }
    protected int getDanoNoJogador() { return danoNoJogador; }
    protected int getAlcanceDeteccao() { return alcanceDeteccao; }
    protected int getAlcanceAtaque() { return alcanceAtaque; }
    protected long getCooldownAtaqueMs() { return cooldownAtaqueMs; }

    protected void atualizarAreaColisao() {
        areaColisao.x = this.x + 6;
        areaColisao.y = this.y + 24;
        areaColisao.width = this.width - 12;
        areaColisao.height = Math.max(18, this.height / 2);
    }

    protected void moverEixoSePossivel(tileMap cenario, verificadorDeColisao verificadorColisao,
            int movimentoX, int movimentoY) {
        if (movimentoX == 0 && movimentoY == 0) {
            return;
        }

        atualizarAreaColisao();
        if (verificadorColisao == null || cenario == null
                || !verificadorColisao.ocorreuColisao(areaColisao, cenario, movimentoX, movimentoY)) {
            this.x += movimentoX;
            this.y += movimentoY;
            atualizarAreaColisao();
        }
    }

    protected void mudarEstado(EstadoInimigo novoEstado) {
        this.estado = novoEstado;
        this.estadoInicioMs = System.currentTimeMillis();
    }

    protected double distanciaAte(int alvoX, int alvoY) {
        int centroX = this.x + this.width / 2;
        int centroY = this.y + this.height / 2;
        return Math.hypot(alvoX - centroX, alvoY - centroY);
    }

    protected void olharPara(int alvoX, int alvoY) {
        int dx = alvoX - this.x;
        int dy = alvoY - this.y;
        if (Math.abs(dx) >= Math.abs(dy)) {
            direcao = dx >= 0 ? "direita" : "esquerda";
        } else {
            direcao = dy >= 0 ? "baixo" : "cima";
        }
    }

    private void configurarTipo(TipoInimigo novoTipo) {
        this.tipo = novoTipo == null ? TipoInimigo.SKELETON : novoTipo;

        if (this.tipo == TipoInimigo.SLIME) {
            vidaMaxima = 45;
            vida = vidaMaxima;
            velocidade = 2;
            danoNoJogador = 12;
            alcanceDeteccao = 115;
            alcanceAtaque = 70;
            cooldownAtaqueMs = 1400;
            carregarSpritesSlime();
            return;
        }

        if (this.tipo == TipoInimigo.RANGED) {
            vidaMaxima = 50;
            vida = vidaMaxima;
            velocidade = 2;
            danoNoJogador = 8;
            alcanceDeteccao = 280;
            alcanceAtaque = 220;
            alcancePreferido = 180;
            cooldownAtaqueMs = 1550;
            carregarSpritesWizard();
            return;
        }

        carregarSpritesSkeleton();
    }

    private void atualizarComportamentoDeCombate(player jogador, tileMap cenario,
            verificadorDeColisao verificadorColisao,
            ControladorProjeteisInimigos controladorProjeteis, long agora) {
        mudarEstadoSeNecessario(EstadoInimigo.CHASE);
        olharPara(jogador.x, jogador.y);

        if (tipo == TipoInimigo.RANGED) {
            atualizarRanged(jogador, cenario, verificadorColisao, controladorProjeteis, agora);
            return;
        }

        double distancia = distanciaAte(jogador.x + jogador.width / 2, jogador.y + jogador.height / 2);
        if (distancia <= getAlcanceAtaque() && agora - ultimoAtaqueMs >= getCooldownAtaqueMs()) {
            iniciarAtaque(jogador, agora);
            return;
        }

        moverNaDirecao(cenario, verificadorColisao,
                jogador.x + jogador.width / 2,
                jogador.y + jogador.height / 2,
                getVelocidade());
    }

    private void atualizarRanged(player jogador, tileMap cenario,
            verificadorDeColisao verificadorColisao,
            ControladorProjeteisInimigos controladorProjeteis, long agora) {
        double distancia = distanciaAte(jogador.x + jogador.width / 2, jogador.y + jogador.height / 2);
        if (distancia < 110) {
            moverParaLonge(cenario, verificadorColisao, jogador);
        } else if (distancia > alcancePreferido) {
            moverNaDirecao(cenario, verificadorColisao,
                    jogador.x + jogador.width / 2,
                    jogador.y + jogador.height / 2,
                    getVelocidade());
        }

        if (controladorProjeteis != null && agora - ultimoAtaqueMs >= getCooldownAtaqueMs()) {
            iniciarAtaque(jogador, agora);
        }
    }

    private void atualizarAtaque(player jogador, tileMap cenario,
            verificadorDeColisao verificadorColisao,
            ControladorProjeteisInimigos controladorProjeteis, long agora) {
        if (tipo == TipoInimigo.SLIME && agora < fimAtaqueMs) {
            moverEixoSePossivel(cenario, verificadorColisao, alvoAtaqueX, 0);
            moverEixoSePossivel(cenario, verificadorColisao, 0, alvoAtaqueY);
        }

        if (!ataqueResolvido && agora >= resolverAtaqueMs) {
            ataqueResolvido = true;
            if (tipo == TipoInimigo.RANGED && controladorProjeteis != null) {
                controladorProjeteis.adicionarProjetil(
                        x + width / 2,
                        y + height / 2,
                        jogador.x + jogador.width / 2,
                        jogador.y + jogador.height / 2,
                        4.0,
                        getDanoNoJogador(),
                        5,
                        new Color(135, 90, 230)
                );
                AudioManager.getInstancia().tocarEfeito("inimigo_projetil");
            } else if (this.areaColisao.intersects(jogador.AreaColisao)
                    || distanciaAte(jogador.x + jogador.width / 2, jogador.y + jogador.height / 2) <= getAlcanceAtaque() + 10) {
                jogador.levarDano(getDanoNoJogador(), x + width / 2, y + height / 2);
            }
        }

        if (agora >= fimAtaqueMs) {
            mudarEstado(EstadoInimigo.CHASE);
        }
    }

    private void iniciarAtaque(player jogador, long agora) {
        ultimoAtaqueMs = agora;
        ataqueResolvido = false;
        olharPara(jogador.x, jogador.y);
        mudarEstado(EstadoInimigo.ATTACK);

        if (tipo == TipoInimigo.SLIME) {
            double dx = (jogador.x + jogador.width / 2.0) - (x + width / 2.0);
            double dy = (jogador.y + jogador.height / 2.0) - (y + height / 2.0);
            double dist = Math.max(1, Math.hypot(dx, dy));
            alvoAtaqueX = (int) Math.round(7 * dx / dist);
            alvoAtaqueY = (int) Math.round(7 * dy / dist);
            resolverAtaqueMs = agora + 120;
            fimAtaqueMs = agora + 320;
        } else if (tipo == TipoInimigo.RANGED) {
            resolverAtaqueMs = agora + 420;
            fimAtaqueMs = agora + 600;
        } else {
            resolverAtaqueMs = agora + 220;
            fimAtaqueMs = agora + 380;
        }
    }

    private void atualizarPatrulha(tileMap cenario, verificadorDeColisao verificadorColisao, long agora) {
        mudarEstadoSeNecessario(EstadoInimigo.PATROL);
        if (agora >= proximaMudancaPatrulhaMs
                || distanciaAte(patrulhaX, patrulhaY) < 12) {
            escolherPontoPatrulha();
        }
        moverNaDirecao(cenario, verificadorColisao, patrulhaX, patrulhaY, Math.max(1, getVelocidade() - 1));
    }

    private void moverNaDirecao(tileMap cenario, verificadorDeColisao verificadorColisao,
            int alvoX, int alvoY, int velocidadeMovimento) {
        olharPara(alvoX, alvoY);
        double dx = alvoX - (x + width / 2.0);
        double dy = alvoY - (y + height / 2.0);
        double distancia = Math.max(1, Math.hypot(dx, dy));
        int movimentoX = (int) Math.round(velocidadeMovimento * dx / distancia);
        int movimentoY = (int) Math.round(velocidadeMovimento * dy / distancia);
        moverEixoSePossivel(cenario, verificadorColisao, movimentoX, 0);
        moverEixoSePossivel(cenario, verificadorColisao, 0, movimentoY);
    }

    private void moverParaLonge(tileMap cenario, verificadorDeColisao verificadorColisao, player jogador) {
        int alvoX = x + width / 2 + ((x + width / 2) - (jogador.x + jogador.width / 2));
        int alvoY = y + height / 2 + ((y + height / 2) - (jogador.y + jogador.height / 2));
        moverNaDirecao(cenario, verificadorColisao, alvoX, alvoY, getVelocidade());
    }

    private void escolherPontoPatrulha() {
        patrulhaX = origemX + random.nextInt(raioPatrulha * 2 + 1) - raioPatrulha;
        patrulhaY = origemY + random.nextInt(raioPatrulha * 2 + 1) - raioPatrulha;
        proximaMudancaPatrulhaMs = System.currentTimeMillis() + 900 + random.nextInt(1300);
    }

    private void iniciarMorte() {
        mudarEstado(EstadoInimigo.DEAD);
        fimMorteMs = System.currentTimeMillis() + 700;
        dropPendente = true;
        AudioManager.getInstancia().tocarEfeito("inimigo_morre");
    }

    private void prepararKnockback(int origemDanoX, int origemDanoY, int forca) {
        int centroX = this.x + this.width / 2;
        int centroY = this.y + this.height / 2;
        int dx = centroX - origemDanoX;
        int dy = centroY - origemDanoY;
        if (dx == 0 && dy == 0) {
            dx = "direita".equals(direcao) ? -1 : 1;
        }
        double distancia = Math.max(1, Math.hypot(dx, dy));
        knockbackX = (int) Math.round(forca * dx / distancia);
        knockbackY = (int) Math.round(forca * dy / distancia);
        framesKnockback = 6;
    }

    private void atualizarKnockback(tileMap cenario, verificadorDeColisao verificadorColisao) {
        if (framesKnockback <= 0) {
            return;
        }

        moverEixoSePossivel(cenario, verificadorColisao, knockbackX, 0);
        moverEixoSePossivel(cenario, verificadorColisao, 0, knockbackY);
        framesKnockback--;
    }

    private void atualizarAnimacao() {
        if (framesFlashDano > 0) {
            framesFlashDano--;
        }

        contadorSprite++;
        if (contadorSprite >= 12) {
            contadorSprite = 0;
            frameAtual = (frameAtual + 1) % 3;
        }
    }

    private void mudarEstadoSeNecessario(EstadoInimigo novoEstado) {
        if (estado != novoEstado) {
            mudarEstado(novoEstado);
        }
    }

    private Image getSpriteAtual() {
        boolean ataque = estado == EstadoInimigo.ATTACK;
        boolean esquerda = "esquerda".equals(direcao);
        if (ataque) {
            return esquerda ? spritesAtaqueEsq[frameAtual] : spritesAtaqueDir[frameAtual];
        }
        return esquerda ? spritesEsq[frameAtual] : spritesDir[frameAtual];
    }

    private void desenharBarraVida(Graphics2D g2) {
        int bw = Math.min(52, Math.max(36, width - 12));
        int bh = 5;
        int bx = x + (width - bw) / 2;
        int by = y - 8;
        g2.setColor(new Color(60, 60, 60, 200));
        g2.fillRect(bx, by, bw, bh);
        float prop = vida / (float) vidaMaxima;
        g2.setColor(prop > 0.5f ? new Color(60, 200, 60)
                : prop > 0.25f ? new Color(230, 180, 0)
                        : new Color(220, 40, 40));
        g2.fillRect(bx, by, (int) (bw * prop), bh);
        g2.setColor(Color.WHITE);
        g2.drawRect(bx, by, bw, bh);
    }

    private void carregarSpritesSkeleton() {
        String base = "repos/NPCs e Inimigos/Inimigos/Skeleton/";
        for (int i = 0; i < 3; i++) {
            spritesEsq[i] = ResourceManager.getInstancia().carregarImagem(base + "left" + (i + 1) + ".png");
            spritesDir[i] = ResourceManager.getInstancia().carregarImagem(base + "right" + (i + 1) + ".png");
            spritesAtaqueEsq[i] = ResourceManager.getInstancia().carregarImagem(base + "attackleft" + (i + 1) + ".png");
            spritesAtaqueDir[i] = ResourceManager.getInstancia().carregarImagem(base + "attackright" + (i + 1) + ".png");
        }
    }

    private void carregarSpritesWizard() {
        String base = "repos/NPCs e Inimigos/Inimigos/Wizard/";
        for (int i = 0; i < 3; i++) {
            spritesEsq[i] = ResourceManager.getInstancia().carregarImagem(base + "left" + (i + 1) + ".png");
            spritesDir[i] = ResourceManager.getInstancia().carregarImagem(base + "right" + (i + 1) + ".png");
            spritesAtaqueEsq[i] = ResourceManager.getInstancia().carregarImagem(base + "attackleft" + (i + 1) + ".png");
            spritesAtaqueDir[i] = ResourceManager.getInstancia().carregarImagem(base + "attackright" + (i + 1) + ".png");
        }
    }

    private void carregarSpritesSlime() {
        String base = "repos/NPCs e Inimigos/Inimigos/Slime/";
        for (int i = 0; i < 3; i++) {
            Image walk = ResourceManager.getInstancia().carregarImagem(base + "walk" + (i + 1) + ".png");
            Image attack = ResourceManager.getInstancia().carregarImagem(base + "attack" + (i + 1) + ".png");
            spritesEsq[i] = walk;
            spritesDir[i] = walk;
            spritesAtaqueEsq[i] = attack;
            spritesAtaqueDir[i] = attack;
        }
    }
}
