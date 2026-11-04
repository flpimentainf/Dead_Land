package DEAD.LAND.world;

import DEAD.LAND.core.ResourceManager;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/** Terraço aberto do castelo: chão, parapeitos e fachada abaixo da arena. */
final class RenderizadorTorre {
    private static final int TILE = tiles.LARGURA;
    private final Image pedra = ResourceManager.getInstancia().carregarImagem("repos/tileset/parede_pedra.png");
    private final Image piso = ResourceManager.getInstancia().carregarImagem("repos/tileset/piso_pedra.png");
    private BufferedImage fundo;
    private int[][] mapaAtual;

    void desenharFundo(Graphics2D g, int[][] mapa) {
        mapaAtual = mapa;
        int largura = mapa[0].length * TILE;
        int altura = mapa.length * TILE;
        if (fundo == null || fundo.getWidth() != largura || fundo.getHeight() != altura) {
            fundo = criarFundo(mapa, largura, altura);
        }
        g.drawImage(fundo, 0, 0, null);
    }

    int orientarParede(int id, int linha, int coluna) {
        if ((id == 40 || id == 42) && mapaAtual != null) {
            if (coluna + 1 < mapaAtual[linha].length && mapaAtual[linha][coluna + 1] == 41) return 40;
            if (coluna > 0 && mapaAtual[linha][coluna - 1] == 41) return 42;
        }
        return id;
    }

    boolean desenharTile(Graphics2D g, int id, int variacao) {
        switch (id) {
            case 38 -> { /* Transparente: deixa ver o céu e a torre abaixo do terraço. */ }
            case 41 -> desenharPiso(g, variacao);
            case 33 -> { muralha(g, 0); muralha(g, 3); }
            case 34 -> muralha(g, 0);
            case 35 -> { muralha(g, 0); muralha(g, 1); }
            case 40 -> muralha(g, 3);
            case 42 -> muralha(g, 1);
            case 48 -> { muralha(g, 2); muralha(g, 3); }
            case 49 -> muralha(g, 2);
            case 50 -> { muralha(g, 2); muralha(g, 1); }
            default -> { return false; }
        }
        return true;
    }

    private void desenharPiso(Graphics2D g, int variacao) {
        g.drawImage(piso, 0, 0, TILE, TILE, null);
        g.setColor(new Color(130, 134, 133, 170));
        g.fillRect(0, 0, TILE, TILE);
        g.setColor(new Color(77, 83, 86));
        g.fillRect(0, 0, TILE, 2);
        g.fillRect(0, 24, TILE, 2);
        g.fillRect(0, 0, 2, 24);
        g.fillRect(24, 24, 2, 24);
        g.setColor(new Color(173, 175, 162));
        g.fillRect(2, 2, 46, 2);
        g.fillRect(0, 26, 48, 2);
        if (variacao == 3 || variacao == 6) {
            g.setColor(new Color(92, 97, 95));
            g.drawPolyline(new int[]{12, 16, 16, 20}, new int[]{8, 12, 16, 20}, 4);
        }
    }

    private void muralha(Graphics2D g, int quartosDeVolta) {
        Graphics2D parede = (Graphics2D) g.create();
        try {
            parede.rotate(quartosDeVolta * Math.PI / 2, TILE / 2.0, TILE / 2.0);
            Area silhueta = new Area(new Rectangle(0, 14, TILE, 30));
            for (int x = 0; x < TILE; x += 16) silhueta.add(new Area(new Rectangle(x, 0, 10, 16)));
            Shape clip = parede.getClip();
            parede.clip(silhueta);
            parede.drawImage(pedra, 0, 0, TILE, TILE, null);
            parede.setColor(new Color(53, 64, 72, 90));
            parede.fillRect(0, 22, TILE, 22);
            parede.setClip(clip);
            parede.setColor(new Color(177, 182, 171));
            for (int x = 0; x < TILE; x += 16) parede.fillRect(x, 0, 10, 3);
            parede.fillRect(0, 14, TILE, 3);
            parede.setColor(new Color(41, 49, 55));
            parede.fillRect(0, 38, TILE, 4);
            parede.setColor(new Color(153, 161, 154));
            parede.fillRect(0, 36, TILE, 2);
            parede.setColor(new Color(21, 30, 38, 130));
            parede.fillRect(0, 42, TILE, 6);
        } finally { parede.dispose(); }
    }

    private BufferedImage criarFundo(int[][] mapa, int largura, int altura) {
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setPaint(new GradientPaint(0, 0, new Color(59, 83, 112), 0, altura, new Color(149, 166, 176)));
            g.fillRect(0, 0, largura, altura);
            desenharNuvens(g, largura, altura);
            g.setColor(new Color(77, 104, 124, 100));
            for (int x = -80; x < largura; x += 220) {
                g.fillPolygon(new Polygon(new int[]{x, x + 90, x + 220},
                        new int[]{altura, altura - 110 - Math.floorMod(x, 65), altura}, 3));
            }
            desenharFachada(g, mapa, altura);
        } finally { g.dispose(); }
        return imagem;
    }

    private void desenharNuvens(Graphics2D g, int largura, int altura) {
        for (int i = 0; i < 10; i++) {
            int x = Math.floorMod(i * 197 + 35, largura + 180) - 100;
            int y = 16 + Math.floorMod(i * 83, Math.max(1, altura - 64));
            g.setColor(new Color(216, 226, 229, 50));
            g.fillRect(x, y + 16, 140, 12);
            g.fillRect(x + 18, y + 8, 92, 8);
            g.fillRect(x + 38, y, 48, 8);
            g.setColor(new Color(224, 232, 233, 35));
            g.fillRect(x - 20, y + 28, 180, 6);
        }
    }

    private void desenharFachada(Graphics2D g, int[][] mapa, int altura) {
        int esquerda = mapa[0].length, direita = -1, linhaFrente = -1;
        for (int linha = 0; linha < mapa.length; linha++) {
            for (int col = 0; col < mapa[linha].length; col++) {
                int id = mapa[linha][col];
                if (id == 48 || id == 49 || id == 50) {
                    esquerda = Math.min(esquerda, col);
                    direita = Math.max(direita, col);
                    linhaFrente = linha;
                }
            }
        }
        if (linhaFrente < 0) return;
        int topo = (linhaFrente + 1) * TILE;
        for (int y = topo; y < altura; y += TILE) {
            for (int col = esquerda; col <= direita; col++) g.drawImage(pedra, col * TILE, y, TILE, TILE, null);
        }
        int x = esquerda * TILE, largura = (direita - esquerda + 1) * TILE;
        g.setPaint(new GradientPaint(0, topo, new Color(24, 35, 46, 110), 0, altura, new Color(20, 31, 42, 220)));
        g.fillRect(x, topo, largura, altura - topo);
        g.setColor(new Color(161, 170, 163));
        g.fillRect(x, topo, largura, 4);
        g.setColor(new Color(28, 40, 49));
        g.fillRect(x, topo + 4, largura, 8);
        for (int col = esquerda + 2; col < direita; col += 4) {
            int centro = col * TILE + 20;
            g.setColor(new Color(23, 32, 40));
            g.fillRect(centro, topo + 28, 8, 32);
            g.setColor(new Color(90, 104, 111));
            g.fillRect(centro - 2, topo + 26, 2, 34);
        }
    }
}
