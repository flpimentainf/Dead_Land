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
        Rectangle areaFutura = new Rectangle(jogador.AreaColisao);
        areaFutura.translate(movimentoX, movimentoY);

        int tamanhoTile = cenario.getTamanhoTile();
        int colunaEsquerda = Math.floorDiv(areaFutura.x, tamanhoTile);
        int colunaDireita = Math.floorDiv(areaFutura.x + areaFutura.width - 1, tamanhoTile);
        int linhaSuperior = Math.floorDiv(areaFutura.y, tamanhoTile);
        int linhaInferior = Math.floorDiv(areaFutura.y + areaFutura.height - 1, tamanhoTile);

        return cenario.tileTemColisao(linhaSuperior, colunaEsquerda)
                || cenario.tileTemColisao(linhaSuperior, colunaDireita)
                || cenario.tileTemColisao(linhaInferior, colunaEsquerda)
                || cenario.tileTemColisao(linhaInferior, colunaDireita);
    }
}
