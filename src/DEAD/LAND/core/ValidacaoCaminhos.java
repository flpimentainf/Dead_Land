package DEAD.LAND.core;

import DEAD.LAND.world.CenarioId;
import DEAD.LAND.world.RenderizadorTiles;
import DEAD.LAND.world.tileMap;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.util.HashSet;

final class ValidacaoCaminhos {
    static void validar() {
        tileMap mapa = new tileMap();
        for (CenarioId cenario : CenarioId.values()) {
            mapa.irParaCenario(cenario.indice());
            int[][] base = mapa.getCenarioAtual().getMapaBase();
            HashSet<Integer> verificados = new HashSet<>();
            RenderizadorTiles renderizador = new RenderizadorTiles();
            for (int l = 0; l < base.length; l++) for (int c = 0; c < base[l].length; c++) {
                int id = base[l][c];
                if (!(RenderizadorTiles.ehTrilha(id) || (cenario == CenarioId.TRILHA_LATERAL && (id == 41 || id == 61)))
                        || !verificados.add(id)) continue;
                BufferedImage imagem = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
                var g = imagem.createGraphics();
                try {
                    renderizador.desenharFundo(g, cenario, base);
                    g.setComposite(AlphaComposite.Clear); g.fillRect(0, 0, 48, 48); g.setComposite(AlphaComposite.SrcOver);
                    g.translate(-c * 48, -l * 48); renderizador.desenhar(g, id, l, c, cenario, false);
                } finally { g.dispose(); }
                boolean terraPixelada = false;
                for (int y = 0; y < 48; y++) for (int x = 0; x < 48; x++) {
                    if (imagem.getRGB(x, y) == 0xff896741) terraPixelada = true;
                }
                if (!terraPixelada) throw new IllegalStateException("Trilha deve compartilhar a paleta pixelada em " + cenario + ", ID " + id);
            }
        }

    }
}
