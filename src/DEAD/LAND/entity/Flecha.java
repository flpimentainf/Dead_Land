package DEAD.LAND.entity;

import DEAD.LAND.core.ResourceManager;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

public class Flecha extends Rectangle {
    private static final int COMPRIMENTO = 24;
    private static final int ESPESSURA = 8;
    // O sprite original aponta para cima e para a direita (-45 graus).
    private static final double ANGULO_ORIGINAL_SPRITE = -Math.PI / 4;
    private static final BasicStroke AREA_IMPACTO = new BasicStroke(ESPESSURA, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private final double direcaoX;
    private final double direcaoY;
    private final double angulo;
    private final double velocidade;
    private final int dano;
    private final int knockback;
    private final int alcanceMaximo;
    private final Image sprite;
    private final double origemX;
    private final double origemY;
    private boolean ativa = true;
    private double mundoX;
    private double mundoY;

    public Flecha(int x, int y, String direcao) {
        this(x, y, direcao, 7.0, 30, 8, 420);
    }

    public Flecha(int x, int y, String direcao, double velocidade, int dano, int knockback, int alcanceMaximo) {
        this(x, y, eixoX(direcao), eixoY(direcao), velocidade, dano, knockback, alcanceMaximo);
    }

    public Flecha(double x, double y, double dx, double dy, double velocidade, int dano, int knockback, int alcanceMaximo) {
        double tamanho = Math.hypot(dx, dy);
        if (!java.lang.Double.isFinite(tamanho) || tamanho < 0.000001) {
            dx = 0; dy = 1; tamanho = 1;
        }
        this.direcaoX = dx / tamanho;
        this.direcaoY = dy / tamanho;
        this.angulo = Math.atan2(direcaoY, direcaoX);
        this.velocidade = velocidade;
        this.dano = dano;
        this.knockback = knockback;
        this.alcanceMaximo = alcanceMaximo;
        this.origemX = this.mundoX = x;
        this.origemY = this.mundoY = y;
        this.sprite = ResourceManager.getInstancia().carregarImagem("repos/tiles/tile (48).png");
        atualizarArea();
    }

    public void atualizar() {
        if (!ativa) return;
        mundoX += direcaoX * velocidade;
        mundoY += direcaoY * velocidade;
        atualizarArea();
        if (Math.hypot(mundoX - origemX, mundoY - origemY) >= alcanceMaximo) desativar();
    }

    public void desenhar(Graphics2D destino) {
        if (!ativa) return;
        Graphics2D g = (Graphics2D) destino.create();
        try {
            g.translate(mundoX, mundoY);
            g.rotate(angulo - ANGULO_ORIGINAL_SPRITE);
            g.drawImage(sprite, -COMPRIMENTO / 2, -COMPRIMENTO / 2, COMPRIMENTO, COMPRIMENTO, null);
        } finally { g.dispose(); }
    }

    @Override
    public boolean intersects(Rectangle alvo) {
        if (!super.intersects(alvo)) return false;
        double metade = (COMPRIMENTO - ESPESSURA) / 2.0;
        Line2D corpo = new Line2D.Double(mundoX - direcaoX * metade, mundoY - direcaoY * metade,
                mundoX + direcaoX * metade, mundoY + direcaoY * metade);
        return AREA_IMPACTO.createStrokedShape(corpo).intersects(alvo);
    }

    public boolean isAtiva() { return ativa; }
    public void desativar() { ativa = false; }
    public int getDano() { return dano; }
    public int getKnockback() { return knockback; }
    public double getAngulo() { return angulo; }
    public int getCentroX() { return (int) Math.round(mundoX); }
    public int getCentroY() { return (int) Math.round(mundoY); }
    public int getPontaX() { return (int) Math.round(mundoX + direcaoX * COMPRIMENTO / 2); }
    public int getPontaY() { return (int) Math.round(mundoY + direcaoY * COMPRIMENTO / 2); }
    public String getDirecao() {
        return Math.abs(direcaoX) > Math.abs(direcaoY)
                ? (direcaoX < 0 ? "esquerda" : "direita") : (direcaoY < 0 ? "cima" : "baixo");
    }

    private void atualizarArea() {
        this.width = (int) Math.ceil(Math.abs(direcaoX) * COMPRIMENTO + Math.abs(direcaoY) * ESPESSURA);
        this.height = (int) Math.ceil(Math.abs(direcaoY) * COMPRIMENTO + Math.abs(direcaoX) * ESPESSURA);
        this.x = (int) Math.floor(mundoX - width / 2.0);
        this.y = (int) Math.floor(mundoY - height / 2.0);
    }

    private static int eixoX(String direcao) {
        return "esquerda".equals(direcao) ? -1 : "direita".equals(direcao) ? 1 : 0;
    }

    private static int eixoY(String direcao) {
        return "cima".equals(direcao) ? -1 : eixoX(direcao) == 0 ? 1 : 0;
    }
}
