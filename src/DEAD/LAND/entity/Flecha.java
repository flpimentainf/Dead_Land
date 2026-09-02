package DEAD.LAND.entity;

import DEAD.LAND.core.ResourceManager;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Flecha extends Rectangle {
    private static final int COMPRIMENTO = 24;
    private static final int ESPESSURA = 8;

    private final String direcao;
    private final double velocidade;
    private final int dano;
    private final int knockback;
    private final int alcanceMaximo;
    private final Image sprite;
    private final int origemX;
    private final int origemY;

    private boolean ativa;
    private double mundoX;
    private double mundoY;

    public Flecha(int x, int y, String direcao) {
        this(x, y, direcao, 7.0, 30, 8, 420);
    }

    public Flecha(int x, int y, String direcao, double velocidade, int dano,
            int knockback, int alcanceMaximo) {
        this.direcao = direcao == null ? "baixo" : direcao;
        this.velocidade = velocidade;
        this.dano = dano;
        this.knockback = knockback;
        this.alcanceMaximo = alcanceMaximo;
        this.origemX = x;
        this.origemY = y;
        this.mundoX = x;
        this.mundoY = y;
        this.ativa = true;
        this.sprite = ResourceManager.getInstancia().carregarImagem("repos/tiles/tile (48).png");
        atualizarArea();
    }

    public void atualizar() {
        switch (direcao) {
            case "cima": mundoY -= velocidade; break;
            case "baixo": mundoY += velocidade; break;
            case "esquerda": mundoX -= velocidade; break;
            case "direita": mundoX += velocidade; break;
            default: mundoY += velocidade; break;
        }

        atualizarArea();
        if (getDistanciaPercorrida() >= alcanceMaximo) {
            desativar();
        }
    }

    public void desenhar(Graphics2D g) {
        if (!ativa) return;

        AffineTransform original = g.getTransform();
        double centroX = x + width / 2.0;
        double centroY = y + height / 2.0;

        g.rotate(getAngulo(), centroX, centroY);
        g.drawImage(
                sprite,
                (int) Math.round(centroX - COMPRIMENTO / 2.0),
                (int) Math.round(centroY - ESPESSURA / 2.0),
                COMPRIMENTO,
                ESPESSURA,
                null
        );
        g.setTransform(original);
    }

    public boolean isAtiva() { return ativa; }
    public void desativar() { ativa = false; }
    public int getDano() { return dano; }
    public int getKnockback() { return knockback; }
    public String getDirecao() { return direcao; }
    public int getCentroX() { return x + width / 2; }
    public int getCentroY() { return y + height / 2; }

    private void atualizarArea() {
        boolean horizontal = "esquerda".equals(direcao) || "direita".equals(direcao);
        this.width = horizontal ? COMPRIMENTO : ESPESSURA;
        this.height = horizontal ? ESPESSURA : COMPRIMENTO;
        this.x = (int) Math.round(mundoX - width / 2.0);
        this.y = (int) Math.round(mundoY - height / 2.0);
    }

    private double getAngulo() {
        if ("cima".equals(direcao)) return -Math.PI / 2.0;
        if ("baixo".equals(direcao)) return Math.PI / 2.0;
        if ("esquerda".equals(direcao)) return Math.PI;
        return 0;
    }

    private double getDistanciaPercorrida() {
        double dx = mundoX - origemX;
        double dy = mundoY - origemY;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
