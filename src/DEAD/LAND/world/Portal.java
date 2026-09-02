package DEAD.LAND.world;

import java.awt.Rectangle;

public class Portal {
    private final String id;
    private final CenarioId origem;
    private final Rectangle areaAtivacao;
    private final CenarioId destino;
    private final int destinoX;
    private final int destinoY;
    private final String itemNecessario;
    private final boolean portaDoBoss;

    public Portal(String id, CenarioId origem, Rectangle areaAtivacao,
            CenarioId destino, int destinoX, int destinoY,
            String itemNecessario, boolean portaDoBoss) {
        this.id = id;
        this.origem = origem;
        this.areaAtivacao = new Rectangle(areaAtivacao);
        this.destino = destino;
        this.destinoX = destinoX;
        this.destinoY = destinoY;
        this.itemNecessario = itemNecessario;
        this.portaDoBoss = portaDoBoss;
    }

    public static Portal porTiles(String id, CenarioId origem,
            int coluna, int linha, int larguraTiles, int alturaTiles,
            CenarioId destino, int destinoX, int destinoY,
            String itemNecessario, boolean portaDoBoss) {
        return new Portal(
                id,
                origem,
                new Rectangle(
                        coluna * tiles.LARGURA,
                        linha * tiles.ALTURA,
                        Math.max(1, larguraTiles) * tiles.LARGURA,
                        Math.max(1, alturaTiles) * tiles.ALTURA
                ),
                destino,
                destinoX,
                destinoY,
                itemNecessario,
                portaDoBoss
        );
    }

    public boolean intersecta(Rectangle area) {
        return area != null && areaAtivacao.intersects(area);
    }

    public boolean requerItem(String nomeItem) {
        return itemNecessario != null && itemNecessario.equals(nomeItem);
    }

    public String getId() {
        return id;
    }

    public CenarioId getOrigem() {
        return origem;
    }

    public Rectangle getAreaAtivacao() {
        return new Rectangle(areaAtivacao);
    }

    public CenarioId getDestino() {
        return destino;
    }

    public int getDestinoIndex() {
        return destino.indice();
    }

    public int getDestinoX() {
        return destinoX;
    }

    public int getDestinoY() {
        return destinoY;
    }

    public String getItemNecessario() {
        return itemNecessario;
    }

    public boolean isPortaDoBoss() {
        return portaDoBoss;
    }
}
