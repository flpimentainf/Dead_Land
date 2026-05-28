package DEAD.LAND.core;

import DEAD.LAND.entity.player;
import DEAD.LAND.world.tileMap;

public class verificadorDeColisao {
    private boolean colidiu;

    public boolean ocorreuColisao(player jogador, tileMap cenario, String direcao) {
        this.colidiu = false;

        if (direcao == null || direcao.isEmpty()) {
            return false;
        }

        jogador.atualizarAreaColisao();

        int tamanhoTile = cenario.getTamanhoTile();

        int bordaEsqX = jogador.AreaColisao.x;
        int bordaDirX = jogador.AreaColisao.x + jogador.AreaColisao.width;
        int bordaTopoY = jogador.AreaColisao.y;
        int bordaBaseY = jogador.AreaColisao.y + jogador.AreaColisao.height;

        int colEsqX = bordaEsqX / tamanhoTile;
        int colDirX = bordaDirX / tamanhoTile;
        int rowTopoY = bordaTopoY / tamanhoTile;
        int rowBaseY = bordaBaseY / tamanhoTile;

        if (direcao.equals("cima")) {
            int proxRowTopoY = (bordaTopoY - jogador.passo) / tamanhoTile;
            colidiu = cenario.tileTemColisao(proxRowTopoY, colEsqX)
                    || cenario.tileTemColisao(proxRowTopoY, colDirX);
        } else if (direcao.equals("baixo")) {
            int proxRowBaseY = (bordaBaseY + jogador.passo) / tamanhoTile;
            colidiu = cenario.tileTemColisao(proxRowBaseY, colEsqX)
                    || cenario.tileTemColisao(proxRowBaseY, colDirX);
        } else if (direcao.equals("direita")) {
            int proxColDirX = (bordaDirX + jogador.passo) / tamanhoTile;
            colidiu = cenario.tileTemColisao(rowTopoY, proxColDirX)
                    || cenario.tileTemColisao(rowBaseY, proxColDirX);
        } else if (direcao.equals("esquerda")) {
            int proxColEsqX = (bordaEsqX - jogador.passo) / tamanhoTile;
            colidiu = cenario.tileTemColisao(rowTopoY, proxColEsqX)
                    || cenario.tileTemColisao(rowBaseY, proxColEsqX);
        }

        return colidiu;
    }
}
