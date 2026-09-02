package DEAD.LAND.core;

import java.util.LinkedHashSet;
import java.util.Set;

public class GameState {
    public int slot = 1;
    public int cenarioIndex;
    public int jogadorX;
    public int jogadorY;
    public int vida;
    public int flechas;
    public int capacidadeFlechas;
    public int fragmentosCura;
    public int vontade;
    public boolean bossDerrotado;
    public String estadoHistoria = "QUARTO";
    public String objetivoHistoria = "Deite-se na cama.";
    public String objetivoSecundario = "";
    public String checkpoint = "";
    public long tempoJogoMs;
    public long salvoEmMs;
    public Set<String> itens = new LinkedHashSet<String>();
    public Set<String> npcsConhecidos = new LinkedHashSet<String>();
    public Set<String> eventosHistoria = new LinkedHashSet<String>();
    public Set<String> memorias = new LinkedHashSet<String>();

    public String getResumoProgresso() {
        return "Cenario " + cenarioIndex + " - " + estadoHistoria;
    }
}
