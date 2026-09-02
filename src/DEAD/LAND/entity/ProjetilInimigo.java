package DEAD.LAND.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ProjetilInimigo extends Rectangle {
    private final double velocidadeX;
    private final double velocidadeY;
    private final int danoVida;
    private final int danoVontade;
    private final int alcanceMaximo;
    private final Color cor;
    private final int origemX;
    private final int origemY;

    private double mundoX;
    private double mundoY;
    private boolean ativo = true;

    public ProjetilInimigo(int x, int y, double velocidadeX, double velocidadeY,
            int danoVida, int danoVontade, int alcanceMaximo, Color cor) {
        this.mundoX = x;
        this.mundoY = y;
        this.velocidadeX = velocidadeX;
        this.velocidadeY = velocidadeY;
        this.danoVida = danoVida;
        this.danoVontade = danoVontade;
        this.alcanceMaximo = alcanceMaximo;
        this.cor = cor;
        this.origemX = x;
        this.origemY = y;
        this.width = 14;
        this.height = 14;
        atualizarArea();
    }

    public void atualizar() {
        mundoX += velocidadeX;
        mundoY += velocidadeY;
        atualizarArea();
        if (getDistanciaPercorrida() > alcanceMaximo) {
            ativo = false;
        }
    }

    public void desenhar(Graphics2D g2) {
        if (!ativo) {
            return;
        }

        g2.setColor(new Color(0, 0, 0, 130));
        g2.fillOval(x - 2, y - 2, width + 4, height + 4);
        g2.setColor(cor);
        g2.fillOval(x, y, width, height);
        g2.setColor(Color.WHITE);
        g2.drawOval(x + 3, y + 3, width - 6, height - 6);
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void desativar() {
        ativo = false;
    }

    public int getDanoVida() {
        return danoVida;
    }

    public int getDanoVontade() {
        return danoVontade;
    }

    public int getCentroX() {
        return x + width / 2;
    }

    public int getCentroY() {
        return y + height / 2;
    }

    private void atualizarArea() {
        this.x = (int) Math.round(mundoX - width / 2.0);
        this.y = (int) Math.round(mundoY - height / 2.0);
    }

    private double getDistanciaPercorrida() {
        double dx = mundoX - origemX;
        double dy = mundoY - origemY;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
