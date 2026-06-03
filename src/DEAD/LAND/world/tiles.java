package DEAD.LAND.world;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

public class tiles {
    public static final int LARGURA = 48;
    public static final int ALTURA = 48;
    public static final int TOTAL_TILES = 60;

    private static final String PASTA_TILES = "repos/tiles/";

    private Image[] imagensTiles = new Image[TOTAL_TILES + 1];
    private Image imgAtual;
    private int valorAtual;

    private static final int AJUSTE_ID_ARQUIVO = 1;

    private Set<Integer> tilesSolidos = new HashSet<Integer>();

    public tiles() {
        carregarTodasImagens();
        configurarTilesSolidos();
    }

    private void configurarTilesSolidos() {
        // Valores da matriz que devem bloquear o jogador.
        // Para adicionar/remover blocos solidos, edite esta lista.
        this.tilesSolidos.add(1);
        this.tilesSolidos.add(53);
        this.tilesSolidos.add(104);
        this.tilesSolidos.add(117);
        this.tilesSolidos.add(118);
        this.tilesSolidos.add(106);
        this.tilesSolidos.add(32);
        this.tilesSolidos.add(75);
        this.tilesSolidos.add(76);
        this.tilesSolidos.add(77);
        this.tilesSolidos.add(78);
        this.tilesSolidos.add(75);
        this.tilesSolidos.add(75);
        this.tilesSolidos.add(75);
        this.tilesSolidos.add(75);
        this.tilesSolidos.add(75);
    }

    public void desenhar(Graphics2D g, int linha, int coluna) {
        int posX = coluna * LARGURA;
        int posY = linha * ALTURA;

        if (this.imgAtual != null) {
            g.drawImage(this.imgAtual, posX, posY, LARGURA, ALTURA, null);
        }
    }

    private Image carregarImagem(String caminho) {
        return new ImageIcon(caminho).getImage();
    }

    private void carregarTodasImagens() {
        for (int i = 1; i <= TOTAL_TILES; i++) {
            this.imagensTiles[i] = carregarImagem(PASTA_TILES + "tile (" + i + ").png");
        }
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
