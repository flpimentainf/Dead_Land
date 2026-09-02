package DEAD.LAND.core;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class SistemaMorte {
    private static final int FRAMES_FADE = 90;
    private static final int FRAMES_PARA_MENSAGEM = 70;

    private enum EstadoMorte {
        VIVO,
        MORRENDO,
        AGUARDANDO_RESPAWN
    }

    private EstadoMorte estado = EstadoMorte.VIVO;
    private int framesMorte;
    private boolean confirmarPressionado;

    public boolean atualizar(panel cenaDoJogo, escutadorTeclado teclado,
            SistemaCheckpoint sistemaCheckpoint) {
        if (cenaDoJogo == null || cenaDoJogo.getJogador() == null) {
            return false;
        }

        if (estado == EstadoMorte.VIVO) {
            confirmarPressionado = teclado != null && teclado.confirmar;
            if (!cenaDoJogo.getJogador().estaMorto()) {
                return false;
            }

            estado = EstadoMorte.MORRENDO;
            framesMorte = 0;
        }

        if (estado == EstadoMorte.MORRENDO) {
            framesMorte++;
            if (framesMorte >= FRAMES_FADE) {
                estado = EstadoMorte.AGUARDANDO_RESPAWN;
            }
            confirmarPressionado = teclado != null && teclado.confirmar;
            return true;
        }

        if (estado == EstadoMorte.AGUARDANDO_RESPAWN) {
            boolean confirmou = teclado != null && teclado.confirmar && !confirmarPressionado;
            confirmarPressionado = teclado != null && teclado.confirmar;

            if (confirmou) {
                if (sistemaCheckpoint != null) {
                    sistemaCheckpoint.restaurar(cenaDoJogo);
                }
                if (teclado != null) {
                    teclado.resetar();
                }
                estado = EstadoMorte.VIVO;
                framesMorte = 0;
            }

            return true;
        }

        return false;
    }

    public boolean bloqueiaControle() {
        return estado != EstadoMorte.VIVO;
    }

    public void desenhar(Graphics2D g2, int largura, int altura) {
        if (estado == EstadoMorte.VIVO) {
            return;
        }

        Composite composicaoOriginal = g2.getComposite();
        int alpha = getAlphaFade();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, largura, altura);
        g2.setComposite(composicaoOriginal);

        if (framesMorte >= FRAMES_PARA_MENSAGEM || estado == EstadoMorte.AGUARDANDO_RESPAWN) {
            desenharMensagem(g2, largura, altura);
        }
    }

    private int getAlphaFade() {
        if (estado == EstadoMorte.AGUARDANDO_RESPAWN) {
            return 240;
        }

        return Math.min(240, (int) (240f * framesMorte / FRAMES_FADE));
    }

    private void desenharMensagem(Graphics2D g2, int largura, int altura) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Serif", Font.BOLD, 30));
        desenharTextoCentralizado(g2, "Você se perdeu em Dead Land...", largura, altura / 2 - 18);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(230, 230, 230));
        desenharTextoCentralizado(g2, "Pressione ENTER para tentar novamente", largura, altura / 2 + 26);
    }

    private void desenharTextoCentralizado(Graphics2D g2, String texto, int largura, int y) {
        FontMetrics metricas = g2.getFontMetrics();
        int x = (largura - metricas.stringWidth(texto)) / 2;
        g2.drawString(texto, x, y);
    }
}
