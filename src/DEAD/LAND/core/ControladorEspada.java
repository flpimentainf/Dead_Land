package DEAD.LAND.core;

import DEAD.LAND.entity.Inimigo;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.AnimacaoEspada;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import java.util.function.LongSupplier;

public final class ControladorEspada {
    private static final double INICIO_CORTE = 0.22;
    private static final double FIM_CORTE = 0.72;
    private static final int ALCANCE = 66;
    private final LongSupplier relogio;
    private final AnimacaoEspada animacao = new AnimacaoEspada();
    private final Set<Inimigo> atingidos = new HashSet<>();
    private boolean atacando;
    private boolean teclaAtaqueAnterior;
    private boolean golpeEnfileirado;
    private Point2D alvoEnfileirado;
    private long inicioMs;
    private long ultimoInicioMs = -2000;
    private double angulo;
    private double progressoAnterior;
    private double progresso;
    private int combo;
    private Path2D areaCorte;

    public ControladorEspada() { this(System::currentTimeMillis); }
    public ControladorEspada(LongSupplier relogio) { this.relogio = java.util.Objects.requireNonNull(relogio); }

    public boolean atacar(panel cena, Point2D alvo) {
        if (!cena.podeUsarEspada()) return false;
        if (alvo != null && (!Double.isFinite(alvo.getX()) || !Double.isFinite(alvo.getY()))) return false;
        if (atacando) {
            if (progresso < 0.2) return false;
            golpeEnfileirado = true;
            alvoEnfileirado = alvo == null ? null : new Point2D.Double(alvo.getX(), alvo.getY());
            return true;
        }
        long agora = relogio.getAsLong();
        combo = agora - ultimoInicioMs <= 1000 ? (combo + 1) % 3 : 0;
        inicioMs = ultimoInicioMs = agora;
        player jogador = cena.getJogador();
        angulo = anguloAlvo(jogador, alvo);
        jogador.olharPara(angulo);
        progresso = progressoAnterior = 0;
        atingidos.clear();
        areaCorte = null;
        atacando = true;
        AudioManager.getInstancia().tocarEfeito("espada_golpe");
        return true;
    }

    public void atualizar(panel cena, escutadorTeclado teclado) {
        if (!cena.podeUsarEspada()) { suspender(teclado.atirar); return; }
        if (teclado.atirar && !teclaAtaqueAnterior) atacar(cena, cena.getMiraMundo());
        teclaAtaqueAnterior = teclado.atirar;
        areaCorte = null;
        if (!atacando) return;
        progressoAnterior = progresso;
        progresso = Math.min(1, Math.max(0, (relogio.getAsLong() - inicioMs) / (double) getDuracaoMs()));
        cena.getJogador().olharPara(angulo);
        if (progresso >= INICIO_CORTE && progressoAnterior < FIM_CORTE) {
            areaCorte = criarAreaCorte(cena.getJogador());
        }
        if (progresso >= 1 && areaCorte == null) {
            atacando = false;
            if (golpeEnfileirado) {
                Point2D alvo = alvoEnfileirado;
                golpeEnfileirado = false;
                atacar(cena, alvo);
            }
        }
    }

    public boolean tentarAtingir(panel cena, Inimigo inimigo) {
        if (!atacando || areaCorte == null || !cena.podeUsarEspada()
                || inimigo.getCenarioIndex() != cena.getCenario().getCenarioAtualIndex()
                || !inimigo.podeSerAtingido() || atingidos.contains(inimigo)) return false;
        Rectangle alvo = inimigo.getAreaDano();
        if (!areaCorte.intersects(alvo)) return false;
        player jogador = cena.getJogador();
        double x = jogador.x + jogador.width / 2.0, y = jogador.y + 30;
        double contatoX = Math.max(alvo.x, Math.min(x, alvo.getMaxX() - 1));
        double contatoY = Math.max(alvo.y, Math.min(y, alvo.getMaxY() - 1));
        if (!caminhoLivre(cena.getCenario(), x, y, contatoX, contatoY)) return false;
        atingidos.add(inimigo);
        int vidaAnterior = inimigo.getVida();
        inimigo.levarDano(getDano(), (int) x, (int) y, combo == 2 ? 14 : 10);
        int danoReal = vidaAnterior - inimigo.getVida();
        if (danoReal > 0 && cena.getFeedback() != null) {
            cena.getFeedback().impactoFlecha((int) contatoX, (int) contatoY);
            cena.getFeedback().textoDano(inimigo.x + inimigo.width / 2, inimigo.y, danoReal);
        }
        AudioManager.getInstancia().tocarEfeito("espada_impacto");
        return danoReal > 0;
    }

    private Path2D criarAreaCorte(player jogador) {
        double de = Math.max(INICIO_CORTE, progressoAnterior);
        double ate = Math.min(FIM_CORTE, progresso);
        double a = anguloLamina(de), b = anguloLamina(ate);
        double inicio = Math.min(a, b) - Math.toRadians(14);
        double fim = Math.max(a, b) + Math.toRadians(14);
        double x = jogador.x + jogador.width / 2.0, y = jogador.y + 30;
        Path2D area = new Path2D.Double();
        area.moveTo(x, y);
        for (int i = 0; i <= 20; i++) {
            double t = inicio + (fim - inicio) * i / 20;
            area.lineTo(x + Math.cos(t) * ALCANCE, y + Math.sin(t) * ALCANCE);
        }
        area.closePath();
        return area;
    }

    private static boolean caminhoLivre(tileMap mapa, double x, double y, double fimX, double fimY) {
        int passos = Math.max(1, (int) Math.ceil(Point2D.distance(x, y, fimX, fimY) / 3));
        for (int i = 0; i <= passos; i++) {
            double t = i / (double) passos;
            int col = (int) Math.floor((x + (fimX - x) * t) / mapa.getTamanhoTile());
            int lin = (int) Math.floor((y + (fimY - y) * t) / mapa.getTamanhoTile());
            if (mapa.tileTemColisao(lin, col)) return false;
        }
        return true;
    }

    private static double anguloAlvo(player jogador, Point2D alvo) {
        if (alvo != null) {
            double dx = alvo.getX() - jogador.getCenterX(), dy = alvo.getY() - (jogador.y + 30);
            if (Math.hypot(dx, dy) > 0.01) return Math.atan2(dy, dx);
        }
        return switch (jogador.getDirecao()) {
            case "cima" -> -Math.PI / 2;
            case "esquerda" -> Math.PI;
            case "direita" -> 0;
            default -> Math.PI / 2;
        };
    }

    public double anguloLamina(double p) {
        double t = Math.max(0, Math.min(1, (p - INICIO_CORTE) / (FIM_CORTE - INICIO_CORTE)));
        double sentido = combo == 1 ? -1 : 1;
        double amplitude = Math.toRadians(combo == 2 ? 125 : 95);
        return angulo + sentido * (-amplitude + 2 * amplitude * t);
    }

    public int getDano() { return combo == 2 ? 38 : 26; }
    public int getDuracaoMs() { return combo == 2 ? 480 : 360; }
    public int getCombo() { return combo; }
    public boolean estaAtacando() { return atacando; }
    public double getProgresso() { return progresso; }
    public double getAngulo() { return angulo; }

    public void suspender(boolean atirarPressionado) {
        cancelar();
        teclaAtaqueAnterior = atirarPressionado;
    }

    public void cancelar() {
        atacando = golpeEnfileirado = false;
        areaCorte = null;
        alvoEnfileirado = null;
        atingidos.clear();
        ultimoInicioMs = -2000;
    }

    public void desenharJogador(Graphics2D g, player jogador) {
        animacao.desenhar(g, jogador, this);
    }
}
