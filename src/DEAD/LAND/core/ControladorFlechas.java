package DEAD.LAND.core;

import DEAD.LAND.entity.Flecha;
import DEAD.LAND.entity.player;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ControladorFlechas {

    private List<Flecha> flechas = new ArrayList<>();
    private boolean atirarPressionado = false;

    public void atualizar(panel cenaDoJogo, escutadorTeclado teclado) {
        if (!cenaDoJogo.getInventario().temArco()) {
            return;
        }

        if (teclado.atirar && !atirarPressionado) {
            atirarPressionado = true;
            disparar(cenaDoJogo.getJogador());
        }
        if (!teclado.atirar) {
            atirarPressionado = false;
        }

        tileMap cenario = cenaDoJogo.getCenario();
        int tileSize = cenario.getTamanhoTile();

        for (Flecha f : flechas) {
            if (!f.isAtiva()) {
                continue;
            }
            f.atualizar();

            int col = f.x / tileSize;
            int lin = f.y / tileSize;
            if (cenario.tileTemColisao(lin, col)) {
                f.desativar();
            }
        }

        flechas.removeIf(f -> !f.isAtiva());
    }

    private void disparar(player jogador) {
        int cx = jogador.x + jogador.width / 2;
        int cy = jogador.y + jogador.height / 2;
        flechas.add(new Flecha(cx, cy, jogador.getDirecao()));
    }

    public List<Flecha> getFlechas() {
        return flechas;
    }

    public void limpar() {
        flechas.clear();
        atirarPressionado = false;
    }

    public void desenhar(Graphics2D g) {
        for (Flecha f : flechas) {
            f.desenhar(g);
        }
    }
}
