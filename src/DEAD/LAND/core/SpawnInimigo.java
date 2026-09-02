package DEAD.LAND.core;

import DEAD.LAND.entity.TipoInimigo;

public class SpawnInimigo {
    private final TipoInimigo tipo;
    private final int cenarioIndex;
    private final int x;
    private final int y;
    private final String direcaoInicial;
    private final int raioPatrulha;

    public SpawnInimigo(TipoInimigo tipo, int cenarioIndex, int x, int y,
            String direcaoInicial, int raioPatrulha) {
        this.tipo = tipo;
        this.cenarioIndex = cenarioIndex;
        this.x = x;
        this.y = y;
        this.direcaoInicial = direcaoInicial;
        this.raioPatrulha = raioPatrulha;
    }

    public TipoInimigo getTipo() {
        return tipo;
    }

    public int getCenarioIndex() {
        return cenarioIndex;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getDirecaoInicial() {
        return direcaoInicial;
    }

    public int getRaioPatrulha() {
        return raioPatrulha;
    }
}
