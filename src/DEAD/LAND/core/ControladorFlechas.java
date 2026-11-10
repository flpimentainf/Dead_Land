package DEAD.LAND.core;

import DEAD.LAND.entity.Flecha;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

public class ControladorFlechas {
    private static final long COOLDOWN_TIRO_MS = 450;
    private static final long CARGA_MAXIMA_MS = 1200;
    private static final long CARGA_MINIMA_VISUAL_MS = 120;

    private final List<Flecha> flechas = new ArrayList<Flecha>();
    private final LongSupplier relogio;
    private boolean atirarPressionado;
    private boolean carregando;
    private long inicioCargaMs;
    private long ultimoDisparoMs = -COOLDOWN_TIRO_MS;
    private int cargaX;
    private int cargaY;

    public ControladorFlechas() { this(System::currentTimeMillis); }

    public ControladorFlechas(LongSupplier relogio) {
        this.relogio = java.util.Objects.requireNonNull(relogio);
    }

    public boolean dispararMouse(panel cenaDoJogo, Point2D alvo) {
        if (alvo == null || !Double.isFinite(alvo.getX()) || !Double.isFinite(alvo.getY())) return false;
        carregando = false;
        return efetuarDisparo(cenaDoJogo, CARGA_MINIMA_VISUAL_MS, alvo);
    }

    public void cancelarCarga() {
        carregando = false;
        atirarPressionado = true;
    }

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        if (!cenaDoJogo.getInventario().arcoEquipado()) {
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
        if (relogio.getAsLong() - ultimoDisparoMs < COOLDOWN_TIRO_MS) {
            return;
        }

        if (!cenaDoJogo.getInventario().temFlechas()) {
            cenaDoJogo.getFeedback().mostrarMensagemCentro("Sem flechas");
            AudioManager.getInstancia().tocarEfeito("sem_flechas");
            return;
        }

        carregando = true;
        inicioCargaMs = relogio.getAsLong();
        atualizarPosicaoCarga(cenaDoJogo.getJogador());
    }

    private void finalizarCarga(panel cenaDoJogo) {
        if (!carregando) {
            return;
        }

        carregando = false;
        long cargaMs = Math.min(CARGA_MAXIMA_MS, Math.max(CARGA_MINIMA_VISUAL_MS,
                relogio.getAsLong() - inicioCargaMs));

        efetuarDisparo(cenaDoJogo, cargaMs, cenaDoJogo.getMiraMundo());
    }

    private boolean efetuarDisparo(panel cenaDoJogo, long cargaMs, Point2D alvo) {
        if (!cenaDoJogo.podeUsarArco() || relogio.getAsLong() - ultimoDisparoMs < COOLDOWN_TIRO_MS) return false;
        player jogador = cenaDoJogo.getJogador();
        double dx = alvo == null ? 0 : alvo.getX() - (jogador.x + jogador.width / 2.0);
        double dy = alvo == null ? 0 : alvo.getY() - (jogador.y + jogador.height / 2.0);
        if (alvo != null && Math.hypot(dx, dy) < 0.001) return false;

        if (!cenaDoJogo.getInventario().consumirFlecha()) {
            cenaDoJogo.getFeedback().mostrarMensagemCentro("Sem flechas");
            AudioManager.getInstancia().tocarEfeito("sem_flechas");
            return false;
        }

        disparar(jogador, cargaMs, alvo);
        cenaDoJogo.atualizarInventario();
        AudioManager.getInstancia().tocarEfeito("arco_disparo");
        ultimoDisparoMs = relogio.getAsLong();
        return true;
    }

    private void disparar(player jogador, long cargaMs, Point2D alvo) {
        float carga = cargaMs / (float) CARGA_MAXIMA_MS;
        int centroX = jogador.x + jogador.width / 2;
        int centroY = jogador.y + jogador.height / 2;
        double dx;
        double dy;
        if (alvo != null) {
            dx = alvo.getX() - centroX;
            dy = alvo.getY() - centroY;
        } else {
            String direcao = jogador.getDirecao();
            dx = "esquerda".equals(direcao) ? -1 : "direita".equals(direcao) ? 1 : 0;
            dy = "cima".equals(direcao) ? -1 : dx == 0 ? 1 : 0;
        }
        double tamanho = Math.hypot(dx, dy);
        dx /= tamanho;
        dy /= tamanho;

        double velocidade = 7.0 + 5.0 * carga;
        int dano = Math.round(18 + 32 * carga);
        int knockback = Math.round(6 + 10 * carga);
        int alcance = Math.round(320 + 300 * carga);
        flechas.add(new Flecha(centroX + dx * 12, centroY + dy * 12, dx, dy,
                velocidade, dano, knockback, alcance));
    }

    private void atualizarFlechas(panel cenaDoJogo) {
        tileMap cenario = cenaDoJogo.getCenario();
        int tileSize = cenario.getTamanhoTile();

        for (Flecha f : flechas) {
            if (!f.isAtiva()) {
                continue;
            }

            f.atualizar();
            int col = Math.floorDiv(f.getPontaX(), tileSize);
            int lin = Math.floorDiv(f.getPontaY(), tileSize);
            if (cenario.tileTemColisao(lin, col)
                    || cenario.tileTemColisao(Math.floorDiv(f.getCentroY(), tileSize), Math.floorDiv(f.getCentroX(), tileSize))) {
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

        return Math.min(1f, (relogio.getAsLong() - inicioCargaMs) / (float) CARGA_MAXIMA_MS);
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
