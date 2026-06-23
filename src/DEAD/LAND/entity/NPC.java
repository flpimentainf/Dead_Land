package DEAD.LAND.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class NPC extends Rectangle {

    private static final int LARGURA_NPC = 48;
    private static final int ALTURA_NPC = 72;

    private static final int ALCANCE_INTERACAO = 70;

    private static final int TOTAL_FRAMES = 3;
    private static final int FRAMES_SPRITE = 12;
    private static final int FRAMES_MOVIMENTO = 8;

    private static final int DISTANCIA_PATRULHA = 48;
    private static final int VELOCIDADE_NPC = 2;

    private static final int DOWN = 0;
    private static final int UP = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    private static final String[] DIRECOES = {
        "down", "up", "left", "right"
    };

    private final String id;
    private final String nome;
    private final int cenarioIndex;

    private int direcaoAtual = DOWN;
    private int frameAtual = 0;
    private int contadorSprite = 0;
    private int contadorMovimento = 0;

    private final int xInicial;
    private boolean andandoParaDireita = true;
    private boolean podeCaminhar;

    private final Image[][] sprites = new Image[4][TOTAL_FRAMES];

    private final Rectangle areaInteracao = new Rectangle();

    public NPC(String id, String nome, int numeroNpc, int cenarioIndex, int x, int y) {
        this.id = id;
        this.nome = nome;
        this.cenarioIndex = cenarioIndex;

        this.x = x;
        this.y = y;
        this.xInicial = x;

        this.width = LARGURA_NPC;
        this.height = ALTURA_NPC;

        carregarSprites(numeroNpc);
        atualizarAreaInteracao();
    }

    private void carregarSprites(int numeroNpc) {
        String npcFormatado = String.format("NPC_%02d", numeroNpc);
        String base = "repos/NPCs e Inimigos/NPC/" + npcFormatado + "/";
        String prefixoArquivo = String.format("npc_%02d_", numeroNpc);

        for (int d = 0; d < DIRECOES.length; d++) {
            String direcao = DIRECOES[d];

            for (int i = 0; i < TOTAL_FRAMES; i++) {
                int indice = i + 1;
                String caminho = base
                        + direcao + "/"
                        + prefixoArquivo
                        + direcao
                        + "_0"
                        + indice
                        + ".png";

                sprites[d][i] = new ImageIcon(caminho).getImage();
            }
        }
    }

    public void atualizar(player jogador) {
        if (estaPerto(jogador)) {
            olharParaJogador(jogador);
        } else {
            patrulhar();
        }

        atualizarAnimacao();
        atualizarAreaInteracao();
    }

    private void atualizarAnimacao() {
        contadorSprite++;

        if (contadorSprite >= FRAMES_SPRITE) {
            contadorSprite = 0;
            frameAtual = (frameAtual + 1) % TOTAL_FRAMES;
        }
    }

    private void olharParaJogador(player jogador) {
        int centroNpcX = this.x + this.width / 2;
        int centroNpcY = this.y + this.height / 2;

        int centroJogadorX = jogador.x + jogador.width / 2;
        int centroJogadorY = jogador.y + jogador.height / 2;

        int dx = centroJogadorX - centroNpcX;
        int dy = centroJogadorY - centroNpcY;

        if (Math.abs(dx) > Math.abs(dy)) {
            direcaoAtual = dx > 0 ? RIGHT : LEFT;
        } else {
            direcaoAtual = dy > 0 ? DOWN : UP;
        }
    }

    private void patrulhar() {
        if (!podeCaminhar) {
            return;
        }

        contadorMovimento++;

        if (contadorMovimento < FRAMES_MOVIMENTO) {
            return;
        }

        contadorMovimento = 0;

        int limiteDireita = xInicial + DISTANCIA_PATRULHA;
        int limiteEsquerda = xInicial - DISTANCIA_PATRULHA;

        if (andandoParaDireita) {
            x += VELOCIDADE_NPC;
            direcaoAtual = RIGHT;

            if (x >= limiteDireita) {
                x = limiteDireita;
                andandoParaDireita = false;
            }
        } else {
            x -= VELOCIDADE_NPC;
            direcaoAtual = LEFT;

            if (x <= limiteEsquerda) {
                x = limiteEsquerda;
                andandoParaDireita = true;
            }
        }
    }

    public void desenhar(Graphics2D g2, String simboloEstado, boolean memoriaRecuperada) {
        if (memoriaRecuperada) {
            g2.setColor(new Color(180, 220, 255, 70));
            g2.fillOval(x - 4, y + 8, width + 8, height - 4);
        }

        g2.drawImage(getSpriteAtual(), x, y, width, height, null);
        desenharNome(g2, simboloEstado);
    }

    private void desenharNome(Graphics2D g2, String simboloEstado) {
        g2.setFont(new Font("Arial", Font.BOLD, 11));

        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(nome) + 14;
        int caixaX = x + width / 2 - largura / 2;
        int caixaY = y - 19;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(caixaX, caixaY, largura, 16, 6, 6);

        g2.setColor(Color.WHITE);
        g2.drawString(nome, caixaX + 7, caixaY + 12);

        if (simboloEstado != null && !simboloEstado.isBlank()) {
            g2.setFont(new Font("Arial", Font.BOLD, 16));

            if ("✓".equals(simboloEstado)) {
                g2.setColor(new Color(100, 255, 140));
            } else {
                g2.setColor(new Color(255, 220, 70));
            }

            g2.drawString(simboloEstado, x + width + 3, y - 5);
        }
    }

    public void desenharIndicadorInteracao(Graphics2D g2) {
        int caixaLargura = 86;
        int caixaAltura = 22;

        int caixaX = x + width / 2 - caixaLargura / 2;
        int caixaY = y - caixaAltura - 24;

        if (caixaY < 4) {
            caixaY = y + 4;
        }

        g2.setColor(new Color(30, 30, 30, 190));
        g2.fillRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 8, 8);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 8, 8);

        g2.setFont(new Font("Arial", Font.BOLD, 12));

        String texto = "E  Falar";
        FontMetrics fm = g2.getFontMetrics();

        int textoX = caixaX + (caixaLargura - fm.stringWidth(texto)) / 2;
        int textoY = caixaY + (caixaAltura + fm.getAscent()) / 2 - 3;

        g2.drawString(texto, textoX, textoY);
    }

    private Image getSpriteAtual() {
        return sprites[direcaoAtual][frameAtual];
    }

    public boolean estaPerto(player jogador) {
        jogador.atualizarAreaColisao();
        return areaInteracao.intersects(jogador.AreaColisao);
    }

    private void atualizarAreaInteracao() {
        areaInteracao.x = this.x - ALCANCE_INTERACAO / 2;
        areaInteracao.y = this.y + this.height / 2 - ALCANCE_INTERACAO / 2;
        areaInteracao.width = this.width + ALCANCE_INTERACAO;
        areaInteracao.height = this.height / 2 + ALCANCE_INTERACAO;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getCenarioIndex() {
        return cenarioIndex;
    }

    public void setPodeCaminhar(boolean podeCaminhar) {
        this.podeCaminhar = podeCaminhar;
    }
}