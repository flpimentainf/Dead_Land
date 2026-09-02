package DEAD.LAND.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MemoryManager {
    private final List<Memoria> memorias = new ArrayList<Memoria>();
    private final Set<String> descobertas = new LinkedHashSet<String>();

    public MemoryManager() {
        memorias.add(new Memoria("memoria_01", "A queda", "O primeiro som foi chuva. O segundo, seu proprio nome se desfazendo."));
        memorias.add(new Memoria("memoria_02", "A estrada", "Alguem chamou por voce antes da luz tomar a pista."));
        memorias.add(new Memoria("memoria_03", "A promessa", "Mara lembra de uma promessa que voce ainda nao decidiu cumprir."));
        memorias.add(new Memoria("memoria_04", "O Porteiro", "Nem toda saida devolve a mesma pessoa que entrou."));
        memorias.add(new Memoria("memoria_05", "O Esquecido", "O guardiao final ja tentou acordar. Falhou por medo de lembrar."));
    }

    public boolean coletar(String id) {
        if (!existe(id) || descobertas.contains(id)) {
            return false;
        }

        descobertas.add(id);
        return true;
    }

    public boolean foiDescoberta(String id) {
        return descobertas.contains(id);
    }

    public int getTotal() {
        return memorias.size();
    }

    public int getQuantidadeDescoberta() {
        return descobertas.size();
    }

    public int getPercentualConclusao() {
        if (memorias.isEmpty()) {
            return 0;
        }

        return Math.round(100f * descobertas.size() / memorias.size());
    }

    public List<Memoria> getMemorias() {
        return Collections.unmodifiableList(memorias);
    }

    public Set<String> getIdsDescobertas() {
        return new LinkedHashSet<String>(descobertas);
    }

    public void definirDescobertas(Set<String> ids) {
        descobertas.clear();
        if (ids == null) {
            return;
        }

        for (String id : ids) {
            if (existe(id)) {
                descobertas.add(id);
            }
        }
    }

    public Memoria getMemoria(String id) {
        for (Memoria memoria : memorias) {
            if (memoria.id.equals(id)) {
                return memoria;
            }
        }

        return null;
    }

    private boolean existe(String id) {
        return getMemoria(id) != null;
    }

    public static class Memoria {
        private final String id;
        private final String titulo;
        private final String descricao;

        private Memoria(String id, String titulo, String descricao) {
            this.id = id;
            this.titulo = titulo;
            this.descricao = descricao;
        }

        public String getId() {
            return id;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
