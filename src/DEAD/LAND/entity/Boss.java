package DEAD.LAND.entity;

import DEAD.LAND.core.AudioManager;
import DEAD.LAND.core.ControladorProjeteisInimigos;
import DEAD.LAND.core.ResourceManager;
import DEAD.LAND.core.verificadorDeColisao;
import DEAD.LAND.world.tileMap;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Boss extends Inimigo {
    public static final int VIDA_MAXIMA_BOSS = 600;

    private enum EstadoBoss {
        INTRO,
        PHASE_1,
        TRANSITION_1,
        PHASE_2,
        TRANSITION_2,
        PHASE_3,
        ATTACK,
        DEATH
    }

    private final Rectangle areaDano = new Rectangle();
    private final Rectangle areaAviso = new Rectangle();
    private final int origemBossX;
    private final int origemBossY;

    private EstadoBoss estadoBoss = EstadoBoss.INTRO;
    private EstadoBoss faseAnterior = EstadoBoss.PHASE_1;
    private long inicioEstadoBossMs;
    private long proximoAtaqueMs;
    private long resolverAtaqueMs;
    private long fimAtaqueMs;
    private int tipoAtaqueAtual;
    private boolean ataqueResolvido;

    public Boss(int x, int y) {
        super(x, y, TipoInimigo.BOSS, "esquerda", 80);
        this.origemBossX = x;
        this.origemBossY = y;
        this.width = 128;
        this.height = 128;
        this.vidaMaxima = VIDA_MAXIMA_BOSS;
        this.vida = VIDA_MAXIMA_BOSS;
        carregarSpritesWizard();
        atualizarAreaColisao();
        mudarEstadoBoss(EstadoBoss.INTRO);
    }

    @Override
    public void atualizar(player jogador, tileMap cenario, verificadorDeColisao verificadorColisao,
            ControladorProjeteisInimigos controladorProjeteis) {
        if (!vivo || jogador == null || jogador.estaMorto()) {
            return;
        }

        atualizarAreaColisao();
        long agora = System.currentTimeMillis();

        if (vida <= 0 && estadoBoss != EstadoBoss.DEATH) {
            mudarEstadoBoss(EstadoBoss.DEATH);
            AudioManager.getInstancia().tocarEfeito("boss_morte");
        }

        if (estadoBoss == EstadoBoss.DEATH) {
            if (agora - inicioEstadoBossMs > 1400) {
                vivo = false;
            }
            return;
        }

        atualizarTransicoesDeFase(agora);
        if (estadoBoss == EstadoBoss.INTRO) {
            if (agora - inicioEstadoBossMs > 1200) {
                mudarEstadoBoss(EstadoBoss.PHASE_1);
            }
            return;
        }

        if (estadoBoss == EstadoBoss.TRANSITION_1 || estadoBoss == EstadoBoss.TRANSITION_2) {
            if (agora - inicioEstadoBossMs > 900) {
                mudarEstadoBoss(estadoBoss == EstadoBoss.TRANSITION_1
                        ? EstadoBoss.PHASE_2
                        : EstadoBoss.PHASE_3);
            }
            return;
        }

        if (estadoBoss == EstadoBoss.ATTACK) {
            atualizarAtaqueBoss(jogador, cenario, verificadorColisao, controladorProjeteis, agora);
            return;
        }

        olharPara(jogador.x, jogador.y);
        double distancia = distanciaAte(jogador.x + jogador.width / 2, jogador.y + jogador.height / 2);
        if (distancia > getAlcanceAtaque()) {
            moverEixoSePossivel(cenario, verificadorColisao,
                    calcularMovimentoX(jogador, distancia),
                    0);
            moverEixoSePossivel(cenario, verificadorColisao,
                    0,
                    calcularMovimentoY(jogador, distancia));
        }

        if (agora >= proximoAtaqueMs) {
            iniciarAtaqueBoss(jogador, agora);
        }
    }

    @Override
    public void desenhar(Graphics2D g2) {
        if (!vivo) return;

        Composite original = g2.getComposite();
        if (estadoBoss == EstadoBoss.PHASE_2) {
            desenharSombras(g2);
        }

        if (estadoBoss == EstadoBoss.DEATH) {
            float alpha = Math.max(0.05f, 1f - (System.currentTimeMillis() - inicioEstadoBossMs) / 1400f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        boolean esquerda = "esquerda".equals(direcao);
        g2.drawImage(esquerda ? spritesEsq[0] : spritesDir[0], x, y, width, height, null);
        g2.setComposite(original);

        if (estadoBoss == EstadoBoss.ATTACK || estadoBoss == EstadoBoss.TRANSITION_1
                || estadoBoss == EstadoBoss.TRANSITION_2) {
            desenharAviso(g2);
        }
    }

    public void desenharBarraBoss(Graphics2D g2, int larguraTela) {
        if (!vivo || estadoBoss == EstadoBoss.DEATH || getCenarioIndex() != 7) {
            return;
        }

        int largura = Math.min(520, larguraTela - 120);
        int altura = 18;
        int x = (larguraTela - largura) / 2;
        int y = 18;
        float prop = vida / (float) vidaMaxima;

        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRoundRect(x - 8, y - 16, largura + 16, 44, 10, 10);
        g2.setFont(new Font("Serif", Font.BOLD, 16));
        g2.setColor(new Color(240, 220, 190));
        g2.drawString("O ESQUECIDO", x, y - 3);
        g2.setColor(new Color(55, 35, 55));
        g2.fillRect(x, y + 4, largura, altura);
        g2.setColor(prop > 0.6f ? new Color(160, 50, 200)
                : prop > 0.25f ? new Color(210, 65, 120)
                        : new Color(245, 80, 70));
        g2.fillRect(x, y + 4, Math.round(largura * prop), altura);
        g2.setColor(Color.WHITE);
        g2.drawRect(x, y + 4, largura, altura);
    }

    @Override
    public void levarDano(int dano, int origemDanoX, int origemDanoY, int forcaKnockback) {
        if (!podeSerAtingido()) {
            return;
        }

        super.levarDano(dano, origemDanoX, origemDanoY, Math.max(2, forcaKnockback / 2));
        if (vida <= 0) {
            vivo = true;
            mudarEstadoBoss(EstadoBoss.DEATH);
        }
    }

    @Override
    public boolean podeSerAtingido() {
        return vivo && estadoBoss != EstadoBoss.INTRO && estadoBoss != EstadoBoss.DEATH;
    }

    @Override
    public boolean foiDerrotado() {
        return estadoBoss == EstadoBoss.DEATH || !vivo;
    }

    @Override
    public void resetarParaOrigem() {
        super.resetarParaOrigem();
        this.x = origemBossX;
        this.y = origemBossY;
        this.vida = VIDA_MAXIMA_BOSS;
        this.vivo = true;
        atualizarAreaColisao();
        mudarEstadoBoss(EstadoBoss.INTRO);
    }

    @Override
    protected void atualizarAreaColisao() {
        areaColisao.x = this.x + 19;
        areaColisao.y = this.y + 80;
        areaColisao.width = 90;
        areaColisao.height = 48;
        atualizarAreaDano();
    }

    private void atualizarAreaDano() {
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
        if (estadoBoss == EstadoBoss.PHASE_3) return 5;
        if (estadoBoss == EstadoBoss.PHASE_2) return 4;
        return 3;
    }

    @Override
    protected int getDanoNoJogador() {
        return estadoBoss == EstadoBoss.PHASE_3 ? 28 : 23;
    }

    @Override
    protected int getAlcanceDeteccao() {
        return 340;
    }

    @Override
    protected int getAlcanceAtaque() {
        return estadoBoss == EstadoBoss.PHASE_1 ? 95 : 115;
    }

    @Override
    public boolean consumirDropPendente() {
        return false;
    }

    private void atualizarTransicoesDeFase(long agora) {
        float prop = vida / (float) vidaMaxima;
        if (prop <= 0.25f && estadoBoss != EstadoBoss.PHASE_3
                && estadoBoss != EstadoBoss.TRANSITION_2
                && estadoBoss != EstadoBoss.DEATH) {
            mudarEstadoBoss(EstadoBoss.TRANSITION_2);
            proximoAtaqueMs = agora + 1000;
        } else if (prop <= 0.60f && estadoBoss == EstadoBoss.PHASE_1) {
            mudarEstadoBoss(EstadoBoss.TRANSITION_1);
            proximoAtaqueMs = agora + 1000;
        }
    }

    private void iniciarAtaqueBoss(player jogador, long agora) {
        faseAnterior = estadoBoss;
        tipoAtaqueAtual = escolherAtaque();
        ataqueResolvido = false;
        prepararAreaAviso(jogador);
        mudarEstadoBoss(EstadoBoss.ATTACK);
        resolverAtaqueMs = agora + getTelegraphMs();
        fimAtaqueMs = resolverAtaqueMs + 240;
    }

    private void atualizarAtaqueBoss(player jogador, tileMap cenario,
            verificadorDeColisao verificadorColisao,
            ControladorProjeteisInimigos controladorProjeteis, long agora) {
        olharPara(jogador.x, jogador.y);

        if (!ataqueResolvido && agora >= resolverAtaqueMs) {
            ataqueResolvido = true;
            resolverAtaque(jogador, cenario, verificadorColisao, controladorProjeteis);
        }

        if (agora >= fimAtaqueMs) {
            mudarEstadoBoss(faseAnterior);
            proximoAtaqueMs = agora + getIntervaloAtaqueMs();
        }
    }

    private int escolherAtaque() {
        if (estadoBoss == EstadoBoss.PHASE_3) {
            int escolha = (int) (System.currentTimeMillis() % 3);
            return escolha == 0 ? 3 : escolha == 1 ? 4 : 5;
        }

        if (estadoBoss == EstadoBoss.PHASE_2) {
            return System.currentTimeMillis() % 2 == 0 ? 2 : 5;
        }

        return System.currentTimeMillis() % 2 == 0 ? 0 : 1;
    }

    private void resolverAtaque(player jogador, tileMap cenario,
            verificadorDeColisao verificadorColisao,
            ControladorProjeteisInimigos controladorProjeteis) {
        if (tipoAtaqueAtual == 0) {
            if (areaAviso.intersects(jogador.AreaColisao)) {
                jogador.levarDano(getDanoNoJogador(), x + width / 2, y + height / 2);
            }
        } else if (tipoAtaqueAtual == 1 || tipoAtaqueAtual == 2) {
            int quantidade = tipoAtaqueAtual == 2 ? 3 : 1;
            for (int i = 0; i < quantidade; i++) {
                controladorProjeteis.adicionarProjetil(
                        x + width / 2,
                        y + height / 2,
                        jogador.x + jogador.width / 2 + (i - 1) * 35,
                        jogador.y + jogador.height / 2,
                        3.4,
                        18,
                        8,
                        new Color(190, 80, 235)
                );
            }
        } else if (tipoAtaqueAtual == 3) {
            if (areaAviso.intersects(jogador.AreaColisao)) {
                jogador.levarDano(24, x + width / 2, y + height / 2);
            }
        } else if (tipoAtaqueAtual == 4) {
            for (int i = 0; i < 8; i++) {
                double angulo = i * Math.PI / 4.0;
                controladorProjeteis.adicionarProjetilDirecional(
                        x + width / 2,
                        y + height / 2,
                        Math.cos(angulo),
                        Math.sin(angulo),
                        3.2,
                        16,
                        7,
                        new Color(230, 70, 145)
                );
            }
        } else if (tipoAtaqueAtual == 5) {
            teleportar(cenario);
        }
        AudioManager.getInstancia().tocarEfeito("boss_ataque");
    }

    private void prepararAreaAviso(player jogador) {
        if (tipoAtaqueAtual == 3) {
            areaAviso.setBounds(x + width / 2 - 110, y + height / 2 - 80, 220, 160);
        } else if (tipoAtaqueAtual == 0) {
            areaAviso.setBounds(x + 12, y + 48, width - 24, 80);
        } else {
            areaAviso.setBounds(jogador.x - 18, jogador.y - 18, jogador.width + 36, jogador.height + 36);
        }
    }

    private void teleportar(tileMap cenario) {
        int[][] pontos = {
            {8 * 48, 2 * 48},
            {15 * 48, 2 * 48},
            {22 * 48, 2 * 48},
            {10 * 48, 5 * 48},
            {20 * 48, 5 * 48}
        };

        for (int i = 0; i < pontos.length; i++) {
            int[] ponto = pontos[(i + (int) (System.currentTimeMillis() % pontos.length)) % pontos.length];
            int coluna = ponto[0] / cenario.getTamanhoTile();
            int linha = ponto[1] / cenario.getTamanhoTile();
            if (!cenario.tileTemColisao(linha, coluna)) {
                this.x = ponto[0];
                this.y = ponto[1];
                atualizarAreaColisao();
                return;
            }
        }
    }

    private int calcularMovimentoX(player jogador, double distancia) {
        return (int) Math.round(getVelocidade()
                * ((jogador.x + jogador.width / 2.0) - (x + width / 2.0))
                / Math.max(1, distancia));
    }

    private int calcularMovimentoY(player jogador, double distancia) {
        return (int) Math.round(getVelocidade()
                * ((jogador.y + jogador.height / 2.0) - (y + height / 2.0))
                / Math.max(1, distancia));
    }

    private long getTelegraphMs() {
        return estadoBoss == EstadoBoss.PHASE_3 ? 430 : 620;
    }

    private long getIntervaloAtaqueMs() {
        return estadoBoss == EstadoBoss.PHASE_3 ? 700 : estadoBoss == EstadoBoss.PHASE_2 ? 900 : 1150;
    }

    private void mudarEstadoBoss(EstadoBoss novoEstado) {
        this.estadoBoss = novoEstado;
        this.inicioEstadoBossMs = System.currentTimeMillis();
        if (novoEstado == EstadoBoss.PHASE_1
                || novoEstado == EstadoBoss.PHASE_2
                || novoEstado == EstadoBoss.PHASE_3) {
            this.proximoAtaqueMs = System.currentTimeMillis() + getIntervaloAtaqueMs();
        }
    }

    private void desenharAviso(Graphics2D g2) {
        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.32f));
        g2.setColor(new Color(230, 30, 80));
        g2.fillOval(areaAviso.x, areaAviso.y, areaAviso.width, areaAviso.height);
        g2.setComposite(original);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(255, 210, 230));
        g2.drawOval(areaAviso.x, areaAviso.y, areaAviso.width, areaAviso.height);
    }

    private void desenharSombras(Graphics2D g2) {
        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.28f));
        g2.drawImage(spritesEsq[0], x - 90, y + 18, width, height, null);
        g2.drawImage(spritesDir[0], x + 90, y + 18, width, height, null);
        g2.setComposite(original);
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
}
