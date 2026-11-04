package DEAD.LAND.ui;

import DEAD.LAND.entity.player;
import java.awt.Color;
import java.awt.Graphics2D;

public class BarraDeVida {
    public void desenhar(Graphics2D g2, player jogador, int x, int y) {
        float proporcao = jogador.getVida() / (float) player.VIDA_MAXIMA;
        Color corVida = proporcao > 0.5f
                ? new Color(60, 200, 60)
                : proporcao > 0.25f
                    ? new Color(230, 180, 0)
                    : new Color(220, 40, 40);
        EstiloHUD.desenharBarra(g2, "VIDA", x, y, 16, proporcao, corVida);
    }
}
