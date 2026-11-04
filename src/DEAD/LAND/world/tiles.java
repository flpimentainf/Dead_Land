package DEAD.LAND.world;

import DEAD.LAND.core.ResourceManager;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.HashSet;
import java.util.Set;

public class tiles {
    public static final int LARGURA = 48;
    public static final int ALTURA = 48;
    private static final int TILES_BASE = 64;
    public static final int TILE_CAMA_CABECEIRA = 64;
    public static final int TILE_CAMA_PESEIRA = 65;
    public static final int TOTAL_TILES = 66;

    private static final String PASTA_TILES = "repos/tiles/";

    private Image[] imagensTiles = new Image[TOTAL_TILES + 1];
    private Image imgAtual;
    private int valorAtual;
    private final RenderizadorTiles renderizador = new RenderizadorTiles();

    private static final int AJUSTE_ID_ARQUIVO = 1;

    private Set<Integer> tilesSolidos = new HashSet<Integer>();

    public tiles() {
        carregarTodasImagens();
        configurarTilesSolidos();
    }

    private void configurarTilesSolidos() {
        // Valores da matriz que devem bloquear o jogador.
        // Para adicionar/remover blocos solidos, edite esta lista.
        this.tilesSolidos.add(0);
        this.tilesSolidos.add(TILE_CAMA_CABECEIRA);
        this.tilesSolidos.add(TILE_CAMA_PESEIRA);
        this.tilesSolidos.add(53);
        this.tilesSolidos.add(23);
        this.tilesSolidos.add(24);
        this.tilesSolidos.add(31);
        this.tilesSolidos.add(32);
        this.tilesSolidos.add(26);
        this.tilesSolidos.add(56);
        this.tilesSolidos.add(11);
        this.tilesSolidos.add(12);
        this.tilesSolidos.add(14);
        this.tilesSolidos.add(13);
        this.tilesSolidos.add(15);
        this.tilesSolidos.add(16);
        this.tilesSolidos.add(18);
        this.tilesSolidos.add(17);
        this.tilesSolidos.add(19);
        this.tilesSolidos.add(20);
        this.tilesSolidos.add(27);
        this.tilesSolidos.add(22);
        this.tilesSolidos.add(51);
        this.tilesSolidos.add(52);
        this.tilesSolidos.add(43);
        this.tilesSolidos.add(44);
        this.tilesSolidos.add(36);
        this.tilesSolidos.add(60);
        this.tilesSolidos.add(33);
        this.tilesSolidos.add(34);
        this.tilesSolidos.add(35);
        this.tilesSolidos.add(40);
        this.tilesSolidos.add(42);
        this.tilesSolidos.add(48);
        this.tilesSolidos.add(49);
        this.tilesSolidos.add(50);
    }

    public void desenhar(Graphics2D g, int linha, int coluna) {
        int posX = coluna * LARGURA;
        int posY = linha * ALTURA;

        if (this.imgAtual != null) {
            g.drawImage(this.imgAtual, posX, posY, LARGURA, ALTURA, null);
        }
    }

    public void desenhar(Graphics2D g, int linha, int coluna, CenarioId cenario, boolean objeto) {
        if (!renderizador.desenhar(g, valorAtual, linha, coluna, cenario, objeto)) desenhar(g, linha, coluna);
    }

    private Image carregarImagem(String caminho) {
        return ResourceManager.getInstancia().carregarImagem(caminho);
    }

    private void carregarTodasImagens() {
        for (int i = 1; i <= TILES_BASE; i++) {
            if (!RenderizadorTiles.temNovoVisual(i - AJUSTE_ID_ARQUIVO)) {
                this.imagensTiles[i] = carregarImagem(PASTA_TILES + "tile (" + i + ").png");
            }
        }
        this.imagensTiles[TILE_CAMA_CABECEIRA + AJUSTE_ID_ARQUIVO] = carregarImagem("repos/quarto/cama_cima.png");
        this.imagensTiles[TILE_CAMA_PESEIRA + AJUSTE_ID_ARQUIVO] = carregarImagem("repos/quarto/cama_baixo.png");
    }

    public void carregaPecaDaMatriz(int valorDaPeca) {
        this.valorAtual = valorDaPeca;
        int idArquivo = valorDaPeca + AJUSTE_ID_ARQUIVO;

        if (idArquivo >= 1 && idArquivo <= TOTAL_TILES && this.imagensTiles[idArquivo] != null) {
            this.imgAtual = this.imagensTiles[idArquivo];
        } else {
            this.imgAtual = this.imagensTiles[1];
        }
    }


    public boolean isColisao() {
        return this.tilesSolidos.contains(this.valorAtual);
    }

    public boolean isTileSolido(int valorDaPeca) {
        return this.tilesSolidos.contains(valorDaPeca);
    }

    public int getLargura() {
        return LARGURA;
    }

    public int getAltura() {
        return ALTURA;
    }
}
