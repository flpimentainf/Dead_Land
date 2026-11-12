package DEAD.LAND.world;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Area;
import java.util.Arrays;
import java.util.List;

/** Vila segura de Ari. Terreno e objetos são camadas independentes. */
public final class MapaVila {
    public static final int COLUNAS = 50, LINHAS = 25, TILE = 48;
    public static final int CONSTRUCAO = 66, LAGO = 67, BARRICADA = 68, ARVORE = 69;
    public static final int ENTRADA_X = 50, ENTRADA_Y = 168;
    public static final int SAIDA_CASTELO_Y = 672;
    public static final int ARI_COLUNA = 19, ARI_LINHA = 14;
    public enum Tipo { CASA, LOJA, GUILDA }
    public record Construcao(String nome, int coluna, int linha, int largura, int altura,
            Tipo tipo, int estilo) {
        public Rectangle area() { return new Rectangle(coluna * TILE, linha * TILE, largura * TILE, altura * TILE); }
        public int portaX() { Rectangle porta = GeometriaConstrucao.de(this).portaPrincipal(); return porta.x + porta.width / 2; }
        public int frenteY() { return (linha + altura) * TILE; }
    }

    public static final List<Construcao> CONSTRUCOES = List.of(
            new Construcao("Casa do viajante", 7, 1, 6, 5, Tipo.CASA, 0),
            new Construcao("Armazém", 19, 1, 10, 6, Tipo.LOJA, 1),
            new Construcao("Guilda dos Errantes", 34, 1, 12, 8, Tipo.GUILDA, 2),
            new Construcao("Casa de pedra", 3, 11, 9, 7, Tipo.CASA, 3),
            new Construcao("Casa do jardim", 18, 18, 9, 4, Tipo.CASA, 4),
            new Construcao("Casa da praça", 34, 12, 8, 6, Tipo.CASA, 5),
            new Construcao("Cabana", 43, 18, 5, 4, Tipo.CASA, 6));
    public static final Rectangle AREA_LAGO = new Rectangle(20 * TILE + 12, 10 * TILE + 24, 6 * TILE - 24, 5 * TILE);
    public static final GeometriaLago LAGUINHO = new GeometriaLago(AREA_LAGO);
    public static final Rectangle AREA_BARRICADA = new Rectangle(35 * TILE, 23 * TILE, 5 * TILE, TILE);
    private static final Shape CAMINHOS = criarCaminhos();

    private MapaVila() {}

    public static Shape caminhos() { return new Area(CAMINHOS); }

    private static Shape criarCaminhos() {
        Area area = new Area(new BasicStroke(2.5f * TILE, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND).createStrokedShape(new Ellipse2D.Double(15.6 * TILE,
                        8.5 * TILE, 14.8 * TILE, 8.8 * TILE)));
        Path2D.Double trilha = new Path2D.Double();
        mover(trilha, -2, 4); curva(trilha, 4, 3.6, 7, 6, 11, 9.2);
        curva(trilha, 14, 11.5, 15, 10.5, 16.3, 12);
        mover(trilha, 10, 8.5); curva(trilha, 5, 9.2, 1.4, 10, 1.5, 15);
        curva(trilha, 1.3, 19.5, 2, 22.7, 6, 23);
        curva(trilha, 10, 23, 12, 21.5, 14.2, 18.2);
        curva(trilha, 15, 17, 16, 16, 17.7, 15.9);
        mover(trilha, 29, 10.3); curva(trilha, 32.2, 9, 34, 6.9, 36, 9.8);
        curva(trilha, 40, 9.5, 42.5, 11.5, 44.3, 13.2);
        curva(trilha, 46.4, 14.5, 48, 14.5, 52, 14.5);
        mover(trilha, 29, 16); curva(trilha, 31.7, 19.5, 35.4, 21, 40, 20.6);
        curva(trilha, 43, 20.1, 44.8, 17.8, 45.6, 14.2);
        mover(trilha, 13, 20); curva(trilha, 14, 23.5, 18.6, 23.4, 23, 23.5);
        mover(trilha, 41, 21); curva(trilha, 41.8, 23.5, 43.5, 23.5, 46, 23.5);
        mover(trilha, 32, 19); curva(trilha, 34.4, 21, 36.7, 22.2, 37.5, 25);
        area.add(new Area(new BasicStroke(2.3f * TILE, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND).createStrokedShape(trilha)));
        conectarPortas(area);
        return area;
    }

    private static void mover(Path2D p, double x, double y) { p.moveTo(x * TILE, y * TILE); }

    private static void conectarPortas(Area caminhos) {
        for (Construcao casa : CONSTRUCOES) {
            var geometria = GeometriaConstrucao.de(casa);
            var portas = geometria.portas();
            var acessos = geometria.acessos();
            for (int i = 0; i < acessos.size(); i++) {
                Point acesso = acessos.get(i), destino = null;
                double distancia = Double.MAX_VALUE;
                for (int y = acesso.y; y <= Math.min(LINHAS * TILE - 24, acesso.y + 360); y += 6) {
                    for (int x = Math.max(24, acesso.x - 360); x <= Math.min(COLUNAS * TILE - 24, acesso.x + 360); x += 6) {
                        double d = acesso.distanceSq(x, y);
                        if (d >= distancia || !caminhos.contains(x, y)) continue;
                        Rectangle passagem = new Rectangle(x - 14, y - 24, 28, 24);
                        if (LAGUINHO.colide(passagem) || CONSTRUCOES.stream().anyMatch(c -> GeometriaConstrucao.de(c).colide(passagem))) continue;
                        destino = new Point(x, y); distancia = d;
                    }
                }
                if (destino == null) throw new IllegalStateException("Porta sem acesso em " + casa.nome());
                Path2D ramo = new Path2D.Double();
                ramo.moveTo(acesso.x, acesso.y - 6);
                ramo.quadTo(acesso.x, destino.y, destino.x, destino.y);
                float largura = Math.max(54, Math.min(90, portas.get(i).width + 18));
                caminhos.add(new Area(new BasicStroke(largura, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND).createStrokedShape(ramo)));
            }
        }
    }
    private static void curva(Path2D p, double x1, double y1, double x2, double y2, double x, double y) {
        p.curveTo(x1 * TILE, y1 * TILE, x2 * TILE, y2 * TILE, x * TILE, y * TILE);
    }

    public static int[][] criarTerreno() {
        int[][] mapa = new int[LINHAS][COLUNAS];
        for (int l = 0; l < LINHAS; l++) {
            for (int c = 0; c < COLUNAS; c++) mapa[l][c] = CAMINHOS.contains(c * TILE + 24, l * TILE + 24) ? 5 : 55;
        }
        return mapa;
    }

    public static int[][] criarObjetos() {
        int[][] objetos = new int[LINHAS][COLUNAS];
        for (int[] linha : objetos) Arrays.fill(linha, -1);
        for (Construcao casa : CONSTRUCOES) {
            for (int l = casa.linha(); l < casa.linha() + casa.altura(); l++) {
                for (int c = casa.coluna(); c < casa.coluna() + casa.largura(); c++) {
                    if (GeometriaConstrucao.de(casa).colide(new Rectangle(c * TILE, l * TILE, TILE, TILE))) objetos[l][c] = CONSTRUCAO;
                }
            }
        }
        for (int l = 0; l < LINHAS; l++) for (int c = 0; c < COLUNAS; c++) {
            if (LAGUINHO.colide(new Rectangle(c * TILE, l * TILE, TILE, TILE))) objetos[l][c] = LAGO;
        }
        preencher(objetos, AREA_BARRICADA, BARRICADA);
        // Limite arborizado; somente a entrada e a saída para o castelo ficam abertas.
        for (int c = 0; c < COLUNAS; c++) { objetos[0][c] = ARVORE; objetos[24][c] = ARVORE; }
        for (int l = 1; l < LINHAS - 1; l++) {
            objetos[l][0] = l >= 2 && l <= 5 ? -1 : ARVORE;
            objetos[l][49] = l >= 12 && l <= 15 ? -1 : ARVORE;
        }
        // Árvores espaçadas: um tronco por objeto, copas maiores que o tile.
        int[][] arvores = {{3, 1}, {15, 2}, {16, 5}, {30, 2}, {31, 5}, {47, 2},
                {3, 7}, {6, 7}, {15, 7}, {30, 6}, {32, 11}, {47, 10}, {2, 9},
                {13, 12}, {14, 15}, {17, 10}, {28, 12}, {30, 14}, {45, 17},
                {2, 21}, {12, 24}, {15, 22}, {29, 22}, {32, 23}, {41, 24}};
        for (int[] arvore : arvores) {
            int c = arvore[0], l = arvore[1];
            if (objetos[l][c] == -1 && CONSTRUCOES.stream().noneMatch(casa -> casa.area().contains(c * TILE + 24, l * TILE + 24)) && !CAMINHOS.intersects(c * TILE - 12, l * TILE - 12, 72, 72)) {
                objetos[l][c] = ARVORE;
            }
        }
        return objetos;
    }

    private static void preencher(int[][] objetos, Rectangle area, int id) {
        for (int l = area.y / TILE; l < (area.y + area.height) / TILE; l++) {
            for (int c = area.x / TILE; c < (area.x + area.width) / TILE; c++) objetos[l][c] = id;
        }
    }

    public static boolean posicaoLivre(tileMap mapa, Rectangle area) {
        if (area.x < 0 || area.y < 0 || area.x + area.width > mapa.getLarguraTotal()
                || area.y + area.height > mapa.getAlturaTotal()) return false;
        return !mapa.areaTemColisao(area);
    }
}
