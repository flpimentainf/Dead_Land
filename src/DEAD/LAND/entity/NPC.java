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
    private static final int FRAMES_SPRITE = 16;
    private static final int FRAMES_MOVIMENTO = 8;
    private static final int DISTANCIA_PATRULHA = 48;

    private final String id;
    private final String nome;
    private final int cenarioIndex;

    private String direcao = "down";
    private int frameAtual = 0;
    private int contadorSprite = 0;
    private int contadorMovimento = 0;
    private final int xInicial;
    private boolean andandoParaDireita = true;
    private boolean podeCaminhar;

    private Image[] spritesDown = new Image[3];
    private Image[] spritesUp = new Image[3];
    private Image[] spritesLeft = new Image[3];
    private Image[] spritesRight = new Image[3];

    private Rectangle areaInteracao = new Rectangle();

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

        for (int i = 0; i < 3; i++) {
            int indice = i + 1;
            spritesDown[i] = new ImageIcon(base + "down/" + prefixoArquivo + "down_0" + indice + ".png").getImage();
            spritesUp[i] = new ImageIcon(base + "up/" + prefixoArquivo + "up_0" + indice + ".png").getImage();
            spritesLeft[i] = new ImageIcon(base + "left/" + prefixoArquivo + "left_0" + indice + ".png").getImage();
            spritesRight[i] = new ImageIcon(base + "right/" + prefixoArquivo + "right_0" + indice + ".png").getImage();
        }
    }

    public void atualizar(player jogador) {
        boolean jogadorPerto = estaPerto(jogador);

        if (jogadorPerto) {
            olharParaJogador(jogador);
        } else {
            patrulhar();
        }

        contadorSprite++;
        if (contadorSprite >= FRAMES_SPRITE) {
            contadorSprite = 0;
            frameAtual = (frameAtual + 1) % 3;
        }

        atualizarAreaInteracao();
    }

    private void olharParaJogador(player jogador) {
        int dx = (jogador.x + jogador.width / 2) - (this.x + this.width / 2);
        int dy = (jogador.y + jogador.height / 2) - (this.y + this.height / 2);

        if (Math.abs(dx) > Math.abs(dy)) {
            direcao = dx > 0 ? "right" : "left";
        } else {
            direcao = dy > 0 ? "down" : "up";
        }
    }

    private void patrulhar() {
        if (!podeCaminhar) return;

        contadorMovimento++;
        if (contadorMovimento < FRAMES_MOVIMENTO) return;
        contadorMovimento = 0;

        if (andandoParaDireita) {
            this.x++;
            direcao = "right";
            if (this.x >= xInicial + DISTANCIA_PATRULHA) {
                andandoParaDireita = false;
            }
        } else {
            this.x--;
            direcao = "left";
            if (this.x <= xInicial - DISTANCIA_PATRULHA) {
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
            g2.setColor("✓".equals(simboloEstado)
                    ? new Color(100, 255, 140)
                    : new Color(255, 220, 70));
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
        if ("up".equals(direcao)) return spritesUp[frameAtual];
        if ("left".equals(direcao)) return spritesLeft[frameAtual];
        if ("right".equals(direcao)) return spritesRight[frameAtual];
        return spritesDown[frameAtual];
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

    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getCenarioIndex() { return cenarioIndex; }
    public void setPodeCaminhar(boolean podeCaminhar) { this.podeCaminhar = podeCaminhar; }
}
