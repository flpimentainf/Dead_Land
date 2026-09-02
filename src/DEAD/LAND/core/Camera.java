package DEAD.LAND.core;

import DEAD.LAND.entity.player;
import java.util.Random;

public class Camera {
    private static final double SUAVIDADE_PADRAO = 0.14;

    private final Random random = new Random();
    private double cameraX;
    private double cameraY;
    private int larguraViewport;
    private int alturaViewport;
    private int framesTremor;
    private int intensidadeTremor;
    private int deslocamentoTremorX;
    private int deslocamentoTremorY;

    public Camera(int larguraViewport, int alturaViewport) {
        definirViewport(larguraViewport, alturaViewport);
    }

    public void definirViewport(int larguraViewport, int alturaViewport) {
        this.larguraViewport = Math.max(1, larguraViewport);
        this.alturaViewport = Math.max(1, alturaViewport);
    }

    public void seguirSuavemente(player jogador, int larguraMapa, int alturaMapa) {
        if (jogador == null) {
            return;
        }

        double alvoX = jogador.x + jogador.width / 2.0 - larguraViewport / 2.0;
        double alvoY = jogador.y + jogador.height / 2.0 - alturaViewport / 2.0;

        alvoX = limitar(alvoX, larguraMapa - larguraViewport);
        alvoY = limitar(alvoY, alturaMapa - alturaViewport);

        this.cameraX += (alvoX - this.cameraX) * SUAVIDADE_PADRAO;
        this.cameraY += (alvoY - this.cameraY) * SUAVIDADE_PADRAO;

        this.cameraX = limitar(this.cameraX, larguraMapa - larguraViewport);
        this.cameraY = limitar(this.cameraY, alturaMapa - alturaViewport);
        atualizarTremor();
    }

    public void centralizarImediatamente(player jogador, int larguraMapa, int alturaMapa) {
        if (jogador == null) {
            return;
        }

        this.cameraX = limitar(
                jogador.x + jogador.width / 2.0 - larguraViewport / 2.0,
                larguraMapa - larguraViewport
        );
        this.cameraY = limitar(
                jogador.y + jogador.height / 2.0 - alturaViewport / 2.0,
                alturaMapa - alturaViewport
        );
    }

    public void iniciarTremor(int intensidade, int duracaoFrames) {
        this.intensidadeTremor = Math.max(0, intensidade);
        this.framesTremor = Math.max(0, duracaoFrames);
        if (this.framesTremor == 0) {
            this.deslocamentoTremorX = 0;
            this.deslocamentoTremorY = 0;
        }
    }

    public int getXRender() {
        return (int) Math.round(cameraX) + deslocamentoTremorX;
    }

    public int getYRender() {
        return (int) Math.round(cameraY) + deslocamentoTremorY;
    }

    public double getCameraX() {
        return cameraX;
    }

    public double getCameraY() {
        return cameraY;
    }

    public int getLarguraViewport() {
        return larguraViewport;
    }

    public int getAlturaViewport() {
        return alturaViewport;
    }

    private void atualizarTremor() {
        if (framesTremor <= 0 || intensidadeTremor <= 0) {
            deslocamentoTremorX = 0;
            deslocamentoTremorY = 0;
            return;
        }

        framesTremor--;
        deslocamentoTremorX = random.nextInt(intensidadeTremor * 2 + 1) - intensidadeTremor;
        deslocamentoTremorY = random.nextInt(intensidadeTremor * 2 + 1) - intensidadeTremor;
    }

    private double limitar(double valor, int maximo) {
        if (maximo <= 0) {
            return 0;
        }

        return Math.max(0, Math.min(valor, maximo));
    }
}
