package DEAD.LAND.ui;

import DEAD.LAND.entity.player;
import java.awt.Color;
import java.awt.Graphics2D;

public class BarraDeVida {
    public void desenhar(Graphics2D g2, player jogador, int x, int y) {
        desenhar(g2, jogador, x, y, EstiloHUD.LARGURA_BARRA);
    }

    public void desenhar(Graphics2D g2, player jogador, int x, int y, int largura) {
        float proporcao = jogador.getVida() / (float) player.VIDA_MAXIMA;
        EstiloHUD.desenharBarra(g2, proporcao <= 0.25f ? "VIDA !" : "VIDA", x, y, largura, 16,
                proporcao, EstiloHUD.VIDA, jogador.getVida() + "/" + player.VIDA_MAXIMA);
    }
}
