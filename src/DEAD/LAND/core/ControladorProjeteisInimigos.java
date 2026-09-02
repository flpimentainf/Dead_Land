package DEAD.LAND.core;

import DEAD.LAND.entity.ProjetilInimigo;
import DEAD.LAND.entity.player;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ControladorProjeteisInimigos {
    private final List<ProjetilInimigo> projeteis = new ArrayList<ProjetilInimigo>();

    public void adicionarProjetil(int origemX, int origemY, int alvoX, int alvoY,
            double velocidade, int danoVida, int danoVontade, Color cor) {
        double dx = alvoX - origemX;
        double dy = alvoY - origemY;
        double distancia = Math.max(1, Math.sqrt(dx * dx + dy * dy));
        projeteis.add(new ProjetilInimigo(
                origemX,
                origemY,
                velocidade * dx / distancia,
                velocidade * dy / distancia,
                danoVida,
                danoVontade,
                520,
                cor
        ));
    }

    public void adicionarProjetilDirecional(int origemX, int origemY, double dirX, double dirY,
            double velocidade, int danoVida, int danoVontade, Color cor) {
        double distancia = Math.max(1, Math.sqrt(dirX * dirX + dirY * dirY));
        projeteis.add(new ProjetilInimigo(
                origemX,
                origemY,
                velocidade * dirX / distancia,
                velocidade * dirY / distancia,
                danoVida,
                danoVontade,
                520,
                cor
        ));
    }

    public void atualizar(panel cenaDoJogo) {
        tileMap cenario = cenaDoJogo.getCenario();
        player jogador = cenaDoJogo.getJogador();
        int tileSize = cenario.getTamanhoTile();

        for (ProjetilInimigo projetil : projeteis) {
            if (!projetil.isAtivo()) {
                continue;
            }

            projetil.atualizar();
            int col = projetil.getCentroX() / tileSize;
            int lin = projetil.getCentroY() / tileSize;
            if (cenario.tileTemColisao(lin, col)) {
                projetil.desativar();
                if (cenaDoJogo.getFeedback() != null) {
                    cenaDoJogo.getFeedback().impactoParede(projetil.getCentroX(), projetil.getCentroY());
                }
                continue;
            }

            if (jogador != null && projetil.intersects(jogador.AreaColisao)) {
                jogador.levarDano(projetil.getDanoVida(), projetil.getCentroX(), projetil.getCentroY());
                if (cenaDoJogo.getSistemaVontade() != null && projetil.getDanoVontade() > 0) {
                    cenaDoJogo.getSistemaVontade().reduzir(projetil.getDanoVontade());
                }
                projetil.desativar();
            }
        }

        projeteis.removeIf(p -> !p.isAtivo());
    }

    public void desenhar(Graphics2D g2) {
        for (ProjetilInimigo projetil : projeteis) {
            projetil.desenhar(g2);
        }
    }

    public void limpar() {
        projeteis.clear();
    }
}
