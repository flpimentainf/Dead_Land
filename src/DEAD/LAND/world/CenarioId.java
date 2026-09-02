package DEAD.LAND.world;

public enum CenarioId {
    QUARTO(0),
    QUEDA(1),
    FLORESTA(2),
    BOSQUE_ARI(3),
    RUINAS(4),
    REGIAO_PRINCIPAL(5),
    AREA_MEMORIA(6),
    ARENA_BOSS(7),
    TRILHA_LATERAL(8);

    private final int indice;

    CenarioId(int indice) {
        this.indice = indice;
    }

    public int indice() {
        return indice;
    }

    public static CenarioId porIndice(int indice) {
        for (CenarioId id : values()) {
            if (id.indice == indice) {
                return id;
            }
        }
        return QUARTO;
    }
}
