package DEAD.LAND.core;

import DEAD.LAND.world.tileMap;
import java.awt.Rectangle;

final class ValidacaoColisao {
    static void validar() {
        tileMap mapa = new tileMap();
        mapa.irParaCenario(1); mapa.definirTile(4, 15, 60);
        if (!mapa.areaTemColisao(new Rectangle(14 * 48, 4 * 48 + 4, 3 * 48, 24))) {
            throw new IllegalStateException("Uma parede no meio de um corpo largo deve bloquear, mesmo com os quatro cantos livres");
        }
        Rectangle antigo = new Rectangle(698, 192, 28, 24);
        var deslocamento = mapa.deslocamentoLivreProximo(antigo);
        if (deslocamento == null || deslocamento.x != -6 || deslocamento.y != 0) {
            throw new IllegalStateException("Posições antigas devem ser ajustadas pelo menor deslocamento livre");
        }
    }
}
