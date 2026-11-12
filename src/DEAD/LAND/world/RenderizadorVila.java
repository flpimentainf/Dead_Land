package DEAD.LAND.world;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/** Fachadas montadas como objetos inteiros, sobre o terreno contínuo da vila. */
public final class RenderizadorVila {
    private BufferedImage terreno;
    private final Map<MapaVila.Construcao, BufferedImage> fachadas = new HashMap<>();
    private final Map<MapaVila.Construcao, Area> oclusoes = new HashMap<>();
    private final RenderizadorFachadas fachadasPixel = new RenderizadorFachadas();
    private final RenderizadorArvores arvores = new RenderizadorArvores();

    public void desenhar(Graphics2D g, int[][] objetos) {
        if (terreno == null) terreno = criarTerreno();
        g.drawImage(terreno, 0, 0, 2400, 1200, null);
        for (MapaVila.Construcao casa : MapaVila.CONSTRUCOES) {
            BufferedImage imagem = fachadas.computeIfAbsent(casa, fachadasPixel::criar);
            Rectangle area = casa.area();
            g.drawImage(imagem, area.x - 12, area.y, area.width + 24, area.height + 24, null);
        }
        desenharLago(g);
        for (int l = 0; l < objetos.length; l++) {
            for (int c = 0; c < objetos[l].length; c++) {
                if (objetos[l][c] == MapaVila.ARVORE) {
                    arvores.desenhar(g, c * 48, l * 48, c * 7 + l * 3,
                            l > 0 && l < 24 && c > 0 && c < 49);
                }
            }
        }
        desenharBarricada(g);
        placa(g, 46 * 48, 11 * 48, "CASTELO  >", false);
        placa(g, 2 * 48, 5 * 48, "VILA DE ARI", false);
    }

    private BufferedImage criarTerreno() {
        BufferedImage imagem = new BufferedImage(1200, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.scale(0.5, 0.5);
            RenderizadorTiles tiles = new RenderizadorTiles();
            for (int l = 0; l < 25; l++) for (int c = 0; c < 50; c++) {
                tiles.desenhar(g, 55, l, c, CenarioId.BOSQUE_ARI, false);
            }
            RenderizadorCaminhos.desenhar(g, MapaVila.caminhos());
        } finally { g.dispose(); }
        return imagem;
    }

    public void aplicarOclusao(Graphics2D g, Rectangle personagem) {
        Area visivel = null;
        for (var casa : MapaVila.CONSTRUCOES) {
            Rectangle desenho = casa.area(); desenho.grow(12, 12);
            if (personagem.y + personagem.height >= GeometriaConstrucao.de(casa).baseY() || !desenho.intersects(personagem)) continue;
            if (visivel == null) visivel = new Area(g.getClip() == null ? new Rectangle(0, 0, 2400, 1200) : g.getClip());
            visivel.subtract(oclusoes.computeIfAbsent(casa, this::criarOclusao));
        }
        if (visivel != null) g.clip(visivel);
    }

    private Area criarOclusao(MapaVila.Construcao casa) {
        BufferedImage imagem = fachadas.computeIfAbsent(casa, fachadasPixel::criar);
        Area opaca = new Area();
        for (int y = 0; y < imagem.getHeight(); y++) {
            int inicio = -1;
            for (int x = 0; x <= imagem.getWidth(); x++) {
                boolean dentro = x < imagem.getWidth() && (imagem.getRGB(x, y) >>> 24) >= 128;
                if (dentro && inicio < 0) inicio = x;
                if (!dentro && inicio >= 0) {
                    opaca.add(new Area(new Rectangle(inicio, y, x - inicio, 1))); inicio = -1;
                }
            }
        }
        Rectangle a = casa.area();
        AffineTransform transformacao = AffineTransform.getTranslateInstance(a.x - 12, a.y);
        transformacao.scale(3, 3); opaca.transform(transformacao);
        return opaca;
    }

    private void desenharLago(Graphics2D destino) {
        Graphics2D g = (Graphics2D) destino.create();
        try {
            Shape agua = MapaVila.LAGUINHO.agua();
            Rectangle a = MapaVila.AREA_LAGO;
            RenderizadorAgua.desenhar(g, agua, a);
            for (int[] pedra : new int[][]{{6, 51}, {45, 3}, {219, 180}, {63, 222}, {243, 90}}) {
                int x = a.x + pedra[0], y = a.y + pedra[1];
                g.setColor(new Color(54, 68, 58)); g.fillRect(x, y + 3, 21, 12);
                g.setColor(new Color(132, 139, 116)); g.fillRect(x + 3, y, 15, 12);
                g.setColor(new Color(175, 173, 137)); g.fillRect(x + 6, y, 9, 3);
            }
            for (int[] planta : new int[][]{{3, 117}, {186, 15}, {153, 228}}) {
                int x = a.x + planta[0], y = a.y + planta[1];
                g.setColor(new Color(45, 79, 38)); g.fillRect(x, y - 18, 3, 24); g.fillRect(x + 9, y - 27, 3, 33);
                g.setColor(new Color(108, 125, 59)); g.fillRect(x + 3, y - 12, 3, 18);
                g.setColor(new Color(100, 68, 35)); g.fillRect(x + 6, y - 33, 6, 12);
            }
        } finally { g.dispose(); }
    }

    private void desenharBarricada(Graphics2D g) {
        Rectangle a = MapaVila.AREA_BARRICADA;
        g.setColor(new Color(44, 38, 29));
        for (int x = a.x; x < a.x + a.width; x += 48) g.fillRect(x + 6, a.y - 19, 14, 64);
        g.setColor(new Color(122, 86, 46)); g.fillRect(a.x, a.y - 10, a.width, 13);
        g.fillRect(a.x, a.y + 15, a.width, 13);
        g.setColor(new Color(181, 142, 80)); g.fillRect(a.x, a.y - 10, a.width, 3);
        g.setColor(new Color(135, 53, 43)); g.fillRect(a.x + 103, a.y - 6, 29, 40);
        g.setColor(new Color(231, 191, 136)); g.fillRect(a.x + 116, a.y, 4, 17); g.fillRect(a.x + 116, a.y + 22, 4, 4);
        placa(g, a.x + 12, a.y - 100, "PASSAGEM FECHADA", true);
    }

    private void placa(Graphics2D g, int x, int y, String texto, boolean pequena) {
        int w = pequena ? 214 : 174;
        g.setColor(new Color(65, 47, 31)); g.fillRect(x + 9, y + 12, 8, 45);
        g.setColor(new Color(48, 39, 28)); g.fillRect(x, y, w, 34);
        g.setColor(new Color(123, 91, 49)); g.fillRect(x + 3, y + 3, w - 6, 27);
        g.setFont(new Font("Monospaced", Font.BOLD, pequena ? 16 : 18));
        g.setColor(new Color(225, 211, 169));
        g.drawString(texto, x + (w - g.getFontMetrics().stringWidth(texto)) / 2, y + 23);
    }
}
