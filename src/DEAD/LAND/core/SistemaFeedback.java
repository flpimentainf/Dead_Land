package DEAD.LAND.core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SistemaFeedback {
    private final List<Particula> particulas = new ArrayList<Particula>();
    private final List<TextoFlutuante> textos = new ArrayList<TextoFlutuante>();
    private final Random random = new Random();

    private String mensagemCentro = "";
    private int framesMensagemCentro;

    public void atualizar() {
        for (Iterator<Particula> it = particulas.iterator(); it.hasNext();) {
            Particula particula = it.next();
            particula.atualizar();
            if (particula.framesVida <= 0) {
                it.remove();
            }
        }

        for (Iterator<TextoFlutuante> it = textos.iterator(); it.hasNext();) {
            TextoFlutuante texto = it.next();
            texto.atualizar();
            if (texto.framesVida <= 0) {
                it.remove();
            }
        }

        if (framesMensagemCentro > 0) {
            framesMensagemCentro--;
        }
    }

    public void impactoFlecha(int x, int y) {
        criarParticulas(x, y, new Color(230, 70, 60), 8, 22);
    }

    public void impactoParede(int x, int y) {
        criarParticulas(x, y, new Color(190, 170, 120), 5, 16);
    }

    public void morteInimigo(int x, int y) {
        criarParticulas(x, y, new Color(190, 190, 210), 14, 30);
    }

    public void brilhoItem(int x, int y) {
        criarParticulas(x, y, new Color(255, 230, 120), 10, 26);
    }

    public void textoDano(int x, int y, int dano) {
        textos.add(new TextoFlutuante(String.valueOf(dano), x, y, new Color(255, 230, 220)));
    }

    public void mostrarMensagemCentro(String mensagem) {
        this.mensagemCentro = mensagem == null ? "" : mensagem;
        this.framesMensagemCentro = 90;
    }

    public void desenharMundo(Graphics2D g2) {
        Composite composicaoOriginal = g2.getComposite();

        for (Particula particula : particulas) {
            float alpha = Math.max(0f, Math.min(1f, particula.framesVida / (float) particula.framesVidaInicial));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(particula.cor);
            g2.fillRect((int) particula.x, (int) particula.y, particula.tamanho, particula.tamanho);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        for (TextoFlutuante texto : textos) {
            float alpha = Math.max(0f, Math.min(1f, texto.framesVida / 40f));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(texto.cor);
            g2.drawString(texto.texto, (int) texto.x, (int) texto.y);
        }

        g2.setComposite(composicaoOriginal);
    }

    public void desenharHud(Graphics2D g2, int largura, int altura) {
        if (framesMensagemCentro <= 0 || mensagemCentro.trim().isEmpty()) {
            return;
        }

        Composite composicaoOriginal = g2.getComposite();
        float alpha = Math.min(1f, framesMensagemCentro / 20f);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setFont(new Font("Serif", Font.BOLD, 22));
        FontMetrics metricas = g2.getFontMetrics();
        int textoLargura = metricas.stringWidth(mensagemCentro);
        int x = (largura - textoLargura) / 2;
        int y = altura / 2 - 70;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x - 18, y - 28, textoLargura + 36, 40, 10, 10);
        g2.setColor(new Color(255, 235, 190));
        g2.drawString(mensagemCentro, x, y);
        g2.setComposite(composicaoOriginal);
    }

    private void criarParticulas(int x, int y, Color cor, int quantidade, int duracao) {
        for (int i = 0; i < quantidade; i++) {
            double vx = -1.8 + random.nextDouble() * 3.6;
            double vy = -1.8 + random.nextDouble() * 3.6;
            particulas.add(new Particula(x, y, vx, vy, cor, 2 + random.nextInt(3), duracao));
        }
    }

    private static class Particula {
        private double x;
        private double y;
        private final double vx;
        private final double vy;
        private final Color cor;
        private final int tamanho;
        private final int framesVidaInicial;
        private int framesVida;

        private Particula(int x, int y, double vx, double vy, Color cor, int tamanho, int framesVida) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.cor = cor;
            this.tamanho = tamanho;
            this.framesVidaInicial = framesVida;
            this.framesVida = framesVida;
        }

        private void atualizar() {
            x += vx;
            y += vy;
            framesVida--;
        }
    }

    private static class TextoFlutuante {
        private final String texto;
        private double x;
        private double y;
        private final Color cor;
        private int framesVida = 40;

        private TextoFlutuante(String texto, int x, int y, Color cor) {
            this.texto = texto;
            this.x = x;
            this.y = y;
            this.cor = cor;
        }

        private void atualizar() {
            y -= 0.55;
            framesVida--;
        }
    }
}
