package DEAD.LAND.core;

import DEAD.LAND.ui.Inventario;
import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/** Histórico permanente da partida; consumir itens não desfaz uma conquista. */
public final class SistemaConquistas {
    public enum Conquista {
        ECO_PASSADO("orbe_1", "Eco do passado", "Colete sua primeira orbe de memória.", "1 orbe", 1),
        MEMORIA_FRAGMENTADA("orbe_3", "Memória fragmentada", "Colete três orbes de memória diferentes.", "3 orbes", 3),
        MESTRE_LEMBRANCAS("orbes_todas", "Mestre das lembranças", "Colete todas as orbes de memória da floresta.", "Todas as orbes", 0),
        MIRA_CERTEIRA("arma_arco", "Mira certeira", "Encontre e colete o arco.", "Colete o arco", -1),
        LAMINA_DESBRAVADORA("arma_espada", "Lâmina desbravadora", "Encontre e colete a espada.", "Colete a espada", -1),
        ESSENCIA_VITAL("pocao_1", "Essência vital", "Obtenha sua primeira poção de cura.", "1 poção de cura", -1);

        public final String id, titulo, requisito, resumo;
        private final int orbes;
        Conquista(String id, String titulo, String requisito, String resumo, int orbes) {
            this.id = id; this.titulo = titulo; this.requisito = requisito; this.resumo = resumo; this.orbes = orbes;
        }
        public boolean deMemoria() { return orbes >= 0; }
    }

    private final EnumSet<Conquista> desbloqueadas = EnumSet.noneOf(Conquista.class);
    private final ArrayDeque<Conquista> novas = new ArrayDeque<>();
    private int orbes, totalOrbes = 5;

    public void atualizar(Inventario inventario, MemoryManager memorias, boolean notificar) {
        if (inventario == null || memorias == null) return;
        orbes = memorias.getQuantidadeDescoberta();
        totalOrbes = memorias.getTotal();
        for (Conquista conquista : Conquista.values()) {
            boolean cumpriu = conquista.deMemoria() ? totalOrbes > 0 && orbes >= getMeta(conquista) : switch (conquista) {
                case MIRA_CERTEIRA -> inventario.temArco();
                case LAMINA_DESBRAVADORA -> inventario.temEspada();
                case ESSENCIA_VITAL -> inventario.temItem("pocao_cura") || inventario.getQuantidadeCura() > 0;
                default -> false;
            };
            if (cumpriu && desbloqueadas.add(conquista) && notificar) novas.addLast(conquista);
        }
    }

    public boolean desbloqueada(Conquista conquista) { return desbloqueadas.contains(conquista); }
    public int getQuantidade() { return desbloqueadas.size(); }
    public int getMeta(Conquista conquista) { return conquista.deMemoria() ? conquista.orbes == 0 ? totalOrbes : conquista.orbes : 1; }
    public int getProgresso(Conquista conquista) {
        return desbloqueada(conquista) ? getMeta(conquista) : conquista.deMemoria() ? Math.min(orbes, getMeta(conquista)) : 0;
    }
    public Conquista consumirNova() { return novas.pollFirst(); }
    public void limparAvisos() { novas.clear(); }

    public Set<String> getIds() {
        Set<String> ids = new LinkedHashSet<>();
        for (Conquista conquista : desbloqueadas) ids.add(conquista.id);
        return ids;
    }

    public void restaurar(Set<String> ids) {
        desbloqueadas.clear(); novas.clear(); orbes = 0;
        if (ids == null) return;
        for (Conquista conquista : Conquista.values()) if (ids.contains(conquista.id)) desbloqueadas.add(conquista);
    }
}
