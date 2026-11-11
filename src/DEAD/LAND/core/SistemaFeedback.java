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

    public String getMensagemCentro() { return mensagemCentro; }
    public boolean isMensagemVisivel() { return framesMensagemCentro > 0 && !mensagemCentro.isBlank(); }

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
        g2.setFont(DEAD.LAND.ui.EstiloHUD.CORPO);
        int w = Math.min(320, largura - 40);
        java.util.List<String> linhas = DEAD.LAND.ui.TextoHUD.quebrar(mensagemCentro, g2.getFontMetrics(), w - 24);
        int n = Math.min(4, linhas.size()), h = 20 + n * 19, x = 20, y = altura - h - 18;
        g2.setColor(DEAD.LAND.ui.EstiloHUD.FUNDO);
        g2.fillRect(x, y, w, h);
        g2.setColor(DEAD.LAND.ui.EstiloHUD.BORDA); g2.drawRect(x, y, w, h);
        g2.setColor(DEAD.LAND.ui.EstiloHUD.TEXTO);
        for (int i = 0; i < n; i++) {
            String linha = linhas.get(i);
            if (i == n - 1 && linhas.size() > n) linha = DEAD.LAND.ui.TextoHUD.resumir(linha, g2.getFontMetrics(), w - 24);
            g2.drawString(linha, x + 12, y + 23 + i * 19);
        }
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
