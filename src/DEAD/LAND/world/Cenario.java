package DEAD.LAND.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cenario {
    private final CenarioId id;
    private final String nome;
    private final String checkpoint;
    private final String musica;
    private final int[][] mapaBase;
    private final int[][] camadaObjetos;
    private final List<Portal> portais = new ArrayList<Portal>();

    public Cenario(CenarioId id, String nome, String checkpoint, String musica,
            int[][] mapaBase, int[][] camadaObjetos) {
        this.id = id;
        this.nome = nome;
        this.checkpoint = checkpoint;
        this.musica = musica;
        this.mapaBase = mapaBase;
        this.camadaObjetos = camadaObjetos;
    }

    public Cenario adicionarPortal(Portal portal) {
        if (portal != null) {
            this.portais.add(portal);
        }
        return this;
    }

    public CenarioId getId() {
        return id;
    }

    public int getIndice() {
        return id.indice();
    }

    public String getNome() {
        return nome;
    }

    public String getCheckpoint() {
        return checkpoint;
    }

    public String getMusica() {
        return musica;
    }

    public int[][] getMapaBase() {
        return mapaBase;
    }

    public int[][] getCamadaObjetos() {
        return camadaObjetos;
    }

    public List<Portal> getPortais() {
        return Collections.unmodifiableList(portais);
    }
}
