package DEAD.LAND.entity;

import DEAD.LAND.core.ResourceManager;
import DEAD.LAND.world.tiles;
import java.awt.Image;
import java.awt.Rectangle;

public class item extends Rectangle {
    public enum Categoria {
        ITEM_CHAVE,
        ARMA,
        MUNICAO,
        CONSUMIVEL,
        ITEM_HISTORIA
    }

    private Image sprite;
    private int cenarioIndex;
    private int linha;
    private int coluna;
    private int tileSubstituto;
    private boolean coletado;
    private Categoria categoria;
    private int quantidade;
    private boolean empilhavel;

    private String nome;

    public item(String caminhoSprite, int cenarioIndex, int linha, int coluna, int tileSubstituto) {
        this(caminhoSprite, cenarioIndex, linha, coluna, tileSubstituto, "");
    }

    public item(String caminhoSprite, int cenarioIndex, int linha, int coluna, int tileSubstituto, String nome) {
        this(
                caminhoSprite,
                cenarioIndex,
                linha,
                coluna,
                tileSubstituto,
                nome,
                categoriaPadrao(nome),
                quantidadePadrao(nome),
                ehEmpilhavel(nome)
        );
    }

    public item(String caminhoSprite, int cenarioIndex, int linha, int coluna, int tileSubstituto,
            String nome, Categoria categoria, int quantidade, boolean empilhavel) {
        this.sprite = ResourceManager.getInstancia().carregarImagem(caminhoSprite);
        this.nome = nome;
        this.cenarioIndex = cenarioIndex;
        this.linha = linha;
        this.coluna = coluna;
        this.tileSubstituto = tileSubstituto;
        this.coletado = false;
        this.categoria = categoria;
        this.quantidade = Math.max(1, quantidade);
        this.empilhavel = empilhavel;
        this.x = coluna * tiles.LARGURA;
        this.y = linha * tiles.ALTURA;
        this.width = tiles.LARGURA;
        this.height = tiles.ALTURA;
    }

    public item(String caminhoSprite, int cenarioIndex, int x, int y,
            String nome, Categoria categoria, int quantidade, boolean empilhavel) {
        this.sprite = ResourceManager.getInstancia().carregarImagem(caminhoSprite);
        this.nome = nome;
        this.cenarioIndex = cenarioIndex;
        this.linha = Math.max(0, y / tiles.ALTURA);
        this.coluna = Math.max(0, x / tiles.LARGURA);
        this.tileSubstituto = -1;
        this.coletado = false;
        this.categoria = categoria;
        this.quantidade = Math.max(1, quantidade);
        this.empilhavel = empilhavel;
        this.x = x;
        this.y = y;
        this.width = tiles.LARGURA;
        this.height = tiles.ALTURA;
    }

    public Image getSprite() {
        return this.sprite;
    }

    public int getCenarioIndex() {
        return this.cenarioIndex;
    }

    public int getLinha() {
        return this.linha;
    }

    public int getColuna() {
        return this.coluna;
    }

    public int getTileSubstituto() {
        return this.tileSubstituto;
    }

    public boolean foiColetado() {
        return this.coletado;
    }

    public void coletar() {
        this.coletado = true;
    }

    public String getNome() { return nome; }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public boolean isEmpilhavel() {
        return empilhavel;
    }

    public String getNomeExibicao() {
        if ("chave".equals(nome)) return "Chave Prata";
        if ("chave_boss_63".equals(nome)) return "Chave Vermelha";
        if ("arco".equals(nome)) return "Arco";
        if ("flecha".equals(nome)) return "Flecha";
        if ("fragmento_cura".equals(nome)) return "Fragmento de Memoria";
        if (nome != null && nome.startsWith("memoria_")) return "Memoria";
        return nome == null || nome.trim().isEmpty() ? "Item" : nome;
    }

    public static Categoria categoriaPadrao(String nome) {
        if ("arco".equals(nome)) return Categoria.ARMA;
        if ("flecha".equals(nome)) return Categoria.MUNICAO;
        if ("fragmento_cura".equals(nome)) return Categoria.CONSUMIVEL;
        if (nome != null && nome.startsWith("memoria_")) return Categoria.ITEM_HISTORIA;
        return Categoria.ITEM_CHAVE;
    }

    public static int quantidadePadrao(String nome) {
        if ("flecha".equals(nome)) return 8;
        if ("fragmento_cura".equals(nome)) return 1;
        return 1;
    }

    public static boolean ehEmpilhavel(String nome) {
        return "flecha".equals(nome) || "fragmento_cura".equals(nome);
    }
}
