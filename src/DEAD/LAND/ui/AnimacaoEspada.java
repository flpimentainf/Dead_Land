package DEAD.LAND.ui;

import DEAD.LAND.core.ControladorEspada;
import DEAD.LAND.entity.player;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/** Anima os sprites originais do jogador com preparação, corte e recuperação. */
public final class AnimacaoEspada {
    public void desenhar(Graphics2D g, player jogador, ControladorEspada golpe) {
        if (!golpe.estaAtacando()) {
            jogador.desenharPlayer(g);
            return;
        }
        Graphics2D cena = (Graphics2D) g.create();
        try {
            cena.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            cena.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            double p = golpe.getProgresso();
            double direcao = golpe.getAngulo();
            double impulso = p < 0.22 ? -2 * p / 0.22
                    : p < 0.72 ? 4 * Math.sin((p - 0.22) / 0.5 * Math.PI) : 0;
            double inclinacao = Math.sin(p * Math.PI * 2) * 0.1 * (golpe.getCombo() == 1 ? -1 : 1);
            cena.translate(Math.rint(Math.cos(direcao) * impulso), Math.rint(Math.sin(direcao) * impulso));
            double x = jogador.getCenterX(), y = jogador.y + 30;
            jogador.desenharPlayer(cena, inclinacao);
            // O corte aparece em primeiro plano; a arma fica apenas no inventário e no chão.
            if (p >= 0.22 && p < 0.88) desenharRastro(cena, x, y, golpe, p);
        } finally { cena.dispose(); }
    }

    private void desenharRastro(Graphics2D g, double x, double y, ControladorEspada golpe, double p) {
        double t = Math.min(0.72, p);
        double ponta = golpe.anguloLamina(t);
        double sentido = golpe.getCombo() == 1 ? -1 : 1;
        double largura = Math.toRadians(golpe.getCombo() == 2 ? 105 : 80);
        int alpha = p <= 0.72 ? 175 : (int) (175 * (0.88 - p) / 0.16);
        Path2D rastro = faixa(x, y, ponta - sentido * largura, ponta, 38, 64);
        g.setColor(new Color(127, 228, 240, Math.max(0, alpha)));
        g.fill(rastro);
        g.setColor(new Color(226, 255, 255, Math.max(0, alpha)));
        g.fill(faixa(x, y, ponta - sentido * largura * 0.65, ponta, 60, 64));
    }

    private static Path2D faixa(double x, double y, double inicio, double fim, int interno, int externo) {
        Path2D faixa = new Path2D.Double();
        for (int i = 0; i <= 16; i++) {
            double a = inicio + (fim - inicio) * i / 16;
            double px = Math.rint((x + Math.cos(a) * externo) / 2) * 2;
            double py = Math.rint((y + Math.sin(a) * externo) / 2) * 2;
            if (i == 0) faixa.moveTo(px, py); else faixa.lineTo(px, py);
        }
        for (int i = 16; i >= 0; i--) {
            double a = inicio + (fim - inicio) * i / 16;
            faixa.lineTo(Math.rint((x + Math.cos(a) * interno) / 2) * 2,
                    Math.rint((y + Math.sin(a) * interno) / 2) * 2);
        }
        faixa.closePath();
        return faixa;
    }

}
