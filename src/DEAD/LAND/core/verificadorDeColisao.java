package DEAD.LAND.core;

import DEAD.LAND.entity.player;
import DEAD.LAND.world.tileMap;
import java.awt.Rectangle;

public class verificadorDeColisao {
    public boolean ocorreuColisao(
            player jogador,
            tileMap cenario,
            int movimentoX,
            int movimentoY
    ) {
        if (movimentoX == 0 && movimentoY == 0) {
            return false;
        }

        jogador.atualizarAreaColisao();
        return ocorreuColisao(jogador.AreaColisao, cenario, movimentoX, movimentoY);
    }

    public boolean ocorreuColisao(
            Rectangle areaColisao,
            tileMap cenario,
            int movimentoX,
            int movimentoY
    ) {
        if (movimentoX == 0 && movimentoY == 0) {
            return false;
        }

        Rectangle areaFutura = new Rectangle(areaColisao);
        areaFutura.translate(movimentoX, movimentoY);

        return cenario.areaTemColisao(areaFutura);
    }
}
