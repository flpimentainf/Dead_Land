package DEAD.LAND.core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;

public class SistemaVontade {
    public static final int VONTADE_MAXIMA = 100;

    private int vontade = VONTADE_MAXIMA;
    private boolean desistenciaPendente;

    public void atualizar() {
        if (vontade <= 0) {
            vontade = 35;
            desistenciaPendente = true;
        }
    }

    public void reduzir(int quantidade) {
        if (quantidade <= 0) {
            return;
        }

        vontade = Math.max(0, vontade - quantidade);
    }

    public void recuperar(int quantidade) {
        if (quantidade <= 0) {
            return;
        }

        vontade = Math.min(VONTADE_MAXIMA, vontade + quantidade);
    }

    public void restaurarCompleta() {
        vontade = VONTADE_MAXIMA;
        desistenciaPendente = false;
    }

    public boolean consumirDesistenciaPendente() {
        if (!desistenciaPendente) {
            return false;
        }

        desistenciaPendente = false;
        return true;
    }

    public int getVontade() {
        return vontade;
    }

    public void definirVontade(int vontade) {
        this.vontade = Math.max(0, Math.min(VONTADE_MAXIMA, vontade));
    }

    public NivelVontade getNivel() {
        if (vontade >= 70) return NivelVontade.NORMAL;
        if (vontade >= 40) return NivelVontade.LEVE;
        if (vontade >= 20) return NivelVontade.MODERADO;
        if (vontade > 0) return NivelVontade.CRITICO;
        return NivelVontade.DESISTENCIA;
    }

    public void desenharBarra(Graphics2D g2, int larguraTela, int alturaTela) {
        int largura = 160;
        int altura = 12;
        int x = 16;
        int y = alturaTela - 22;
        float prop = vontade / (float) VONTADE_MAXIMA;

        g2.setColor(new Color(20, 20, 20, 200));
        g2.fillRoundRect(x - 2, y - 2, largura + 4, altura + 4, 6, 6);
        g2.setColor(new Color(55, 55, 70, 220));
        g2.fillRoundRect(x, y, largura, altura, 5, 5);
        g2.setColor(prop > 0.5f ? new Color(95, 170, 240)
                : prop > 0.25f ? new Color(170, 120, 225)
                        : new Color(210, 55, 120));
        g2.fillRoundRect(x, y, Math.round(largura * prop), altura, 5, 5);
        g2.setColor(new Color(220, 220, 235, 180));
        g2.drawRoundRect(x, y, largura, altura, 5, 5);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.setColor(new Color(210, 225, 255));
        g2.drawString("VONTADE", x, y - 3);
    }

    public void desenharEfeitoMental(Graphics2D g2, int largura, int altura) {
        NivelVontade nivel = getNivel();
        if (nivel == NivelVontade.NORMAL) {
            return;
        }

        Composite original = g2.getComposite();
        int alpha = nivel == NivelVontade.LEVE ? 30
                : nivel == NivelVontade.MODERADO ? 65
                        : 100;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
        g2.setColor(new Color(80, 75, 95));
        g2.fillRect(0, 0, largura, altura);

        if (nivel == NivelVontade.CRITICO) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
            g2.setColor(new Color(20, 0, 15));
            g2.fillRect(0, 0, largura, altura);
        }

        g2.setComposite(original);
    }

    public enum NivelVontade {
        NORMAL,
        LEVE,
        MODERADO,
        CRITICO,
        DESISTENCIA
    }
}
