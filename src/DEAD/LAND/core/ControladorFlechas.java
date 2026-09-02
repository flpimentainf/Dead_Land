package DEAD.LAND.core;

import DEAD.LAND.entity.Flecha;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ControladorFlechas {
    private static final long COOLDOWN_TIRO_MS = 450;
    private static final long CARGA_MAXIMA_MS = 1200;
    private static final long CARGA_MINIMA_VISUAL_MS = 120;

    private final List<Flecha> flechas = new ArrayList<Flecha>();
    private boolean atirarPressionado;
    private boolean carregando;
    private long inicioCargaMs;
    private long ultimoDisparoMs = -COOLDOWN_TIRO_MS;
    private int cargaX;
    private int cargaY;

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        if (!cenaDoJogo.getInventario().temArco()) {
            carregando = false;
            atirarPressionado = teclado.atirar;
            atualizarFlechas(cenaDoJogo);
            return;
        }

        if (teclado.atirar && !atirarPressionado) {
            iniciarCarga(cenaDoJogo);
        }

        if (teclado.atirar && carregando) {
            atualizarPosicaoCarga(cenaDoJogo.getJogador());
        }

        if (!teclado.atirar && atirarPressionado) {
            finalizarCarga(cenaDoJogo);
        }

        atirarPressionado = teclado.atirar;
        atualizarFlechas(cenaDoJogo);
    }

    private void iniciarCarga(panel cenaDoJogo) {
        if (System.currentTimeMillis() - ultimoDisparoMs < COOLDOWN_TIRO_MS) {
            return;
        }

        if (!cenaDoJogo.getInventario().temFlechas()) {
            cenaDoJogo.getFeedback().mostrarMensagemCentro("Sem flechas");
            AudioManager.getInstancia().tocarEfeito("sem_flechas");
            return;
        }

        carregando = true;
        inicioCargaMs = System.currentTimeMillis();
        atualizarPosicaoCarga(cenaDoJogo.getJogador());
    }

    private void finalizarCarga(panel cenaDoJogo) {
        if (!carregando) {
            return;
        }

        carregando = false;
        long cargaMs = Math.min(CARGA_MAXIMA_MS, Math.max(CARGA_MINIMA_VISUAL_MS,
                System.currentTimeMillis() - inicioCargaMs));

        if (!cenaDoJogo.getInventario().consumirFlecha()) {
            cenaDoJogo.getFeedback().mostrarMensagemCentro("Sem flechas");
            return;
        }

        disparar(cenaDoJogo.getJogador(), cargaMs);
        cenaDoJogo.atualizarInventario();
        AudioManager.getInstancia().tocarEfeito("arco_disparo");
        ultimoDisparoMs = System.currentTimeMillis();
    }

    private void disparar(player jogador, long cargaMs) {
        float carga = cargaMs / (float) CARGA_MAXIMA_MS;
        int centroX = jogador.x + jogador.width / 2;
        int centroY = jogador.y + jogador.height / 2;
        String direcao = jogador.getDirecao();

        int offset = 24;
        if ("cima".equals(direcao)) centroY -= offset;
        else if ("baixo".equals(direcao)) centroY += offset;
        else if ("esquerda".equals(direcao)) centroX -= offset;
        else centroX += offset;

        double velocidade = 7.0 + 5.0 * carga;
        int dano = Math.round(18 + 32 * carga);
        int knockback = Math.round(6 + 10 * carga);
        int alcance = Math.round(320 + 300 * carga);
        flechas.add(new Flecha(centroX, centroY, direcao, velocidade, dano, knockback, alcance));
    }

    private void atualizarFlechas(panel cenaDoJogo) {
        tileMap cenario = cenaDoJogo.getCenario();
        int tileSize = cenario.getTamanhoTile();

        for (Flecha f : flechas) {
            if (!f.isAtiva()) {
                continue;
            }

            f.atualizar();
            int col = f.getCentroX() / tileSize;
            int lin = f.getCentroY() / tileSize;
            if (cenario.tileTemColisao(lin, col)) {
                f.desativar();
                cenaDoJogo.getFeedback().impactoParede(f.getCentroX(), f.getCentroY());
                AudioManager.getInstancia().tocarEfeito("flecha_parede");
            }
        }

        flechas.removeIf(f -> !f.isAtiva());
    }

    private void atualizarPosicaoCarga(player jogador) {
        cargaX = jogador.x + jogador.width / 2;
        cargaY = jogador.y - 12;
    }

    public List<Flecha> getFlechas() {
        return flechas;
    }

    public boolean estaCarregando() {
        return carregando;
    }

    public float getProgressoCarga() {
        if (!carregando) {
            return 0f;
        }

        return Math.min(1f, (System.currentTimeMillis() - inicioCargaMs) / (float) CARGA_MAXIMA_MS);
    }

    public void limpar() {
        flechas.clear();
        atirarPressionado = false;
        carregando = false;
    }

    public void desenhar(Graphics2D g) {
        for (Flecha f : flechas) {
            f.desenhar(g);
        }

        if (carregando) {
            desenharCarga(g);
        }
    }

    private void desenharCarga(Graphics2D g) {
        int largura = 38;
        int altura = 5;
        int x = cargaX - largura / 2;
        int y = cargaY;
        float carga = getProgressoCarga();

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(x - 1, y - 1, largura + 2, altura + 2);
        g.setColor(new Color(255, 230, 120));
        g.fillRect(x, y, Math.round(largura * carga), altura);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, largura, altura);
    }
}
