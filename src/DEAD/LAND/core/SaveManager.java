package DEAD.LAND.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public class SaveManager {
    private static final String PASTA_SAVE = "save";
    private static final int TOTAL_SLOTS = 3;
    private static final DateTimeFormatter DATA_FORMATO = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private final File pastaSave;

    public SaveManager() {
        this(new File(PASTA_SAVE));
    }

    public SaveManager(File pastaSave) {
        this.pastaSave = pastaSave == null ? new File(PASTA_SAVE) : pastaSave;
    }

    public boolean salvar(GameState state) {
        if (state == null || state.slot < 1 || state.slot > TOTAL_SLOTS) {
            return false;
        }

        if (!pastaSave.exists() && !pastaSave.mkdirs()) {
            return false;
        }

        state.salvoEmMs = System.currentTimeMillis();
        Properties props = new Properties();
        props.setProperty("slot", String.valueOf(state.slot));
        props.setProperty("cenarioIndex", String.valueOf(state.cenarioIndex));
        props.setProperty("jogadorX", String.valueOf(state.jogadorX));
        props.setProperty("jogadorY", String.valueOf(state.jogadorY));
        props.setProperty("vida", String.valueOf(state.vida));
        props.setProperty("flechas", String.valueOf(state.flechas));
        props.setProperty("capacidadeFlechas", String.valueOf(state.capacidadeFlechas));
        props.setProperty("fragmentosCura", String.valueOf(state.fragmentosCura));
        props.setProperty("vontade", String.valueOf(state.vontade));
        props.setProperty("bossDerrotado", String.valueOf(state.bossDerrotado));
        props.setProperty("estadoHistoria", state.estadoHistoria);
        props.setProperty("objetivoHistoria", state.objetivoHistoria);
        props.setProperty("objetivoSecundario", state.objetivoSecundario);
        props.setProperty("checkpoint", state.checkpoint);
        props.setProperty("tempoJogoMs", String.valueOf(state.tempoJogoMs));
        props.setProperty("salvoEmMs", String.valueOf(state.salvoEmMs));
        props.setProperty("itens", juntar(state.itens));
        props.setProperty("npcsConhecidos", juntar(state.npcsConhecidos));
        props.setProperty("eventosHistoria", juntar(state.eventosHistoria));
        props.setProperty("memorias", juntar(state.memorias));

        try (FileOutputStream out = new FileOutputStream(arquivoSlot(state.slot))) {
            props.store(out, "Dead Land save");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<GameState> carregar(int slot) {
        if (slot < 1 || slot > TOTAL_SLOTS || !existeSave(slot)) {
            return Optional.empty();
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(arquivoSlot(slot))) {
            props.load(in);
            GameState state = new GameState();
            state.slot = lerInt(props, "slot", slot);
            state.cenarioIndex = lerInt(props, "cenarioIndex", 0);
            state.jogadorX = lerInt(props, "jogadorX", 745);
            state.jogadorY = lerInt(props, "jogadorY", 335);
            state.vida = lerInt(props, "vida", 100);
            state.flechas = lerInt(props, "flechas", 0);
            state.capacidadeFlechas = lerInt(props, "capacidadeFlechas", 20);
            state.fragmentosCura = lerInt(props, "fragmentosCura", 0);
            state.vontade = lerInt(props, "vontade", SistemaVontade.VONTADE_MAXIMA);
            state.bossDerrotado = Boolean.parseBoolean(props.getProperty("bossDerrotado", "false"));
            state.estadoHistoria = props.getProperty("estadoHistoria", "QUARTO");
            state.objetivoHistoria = props.getProperty("objetivoHistoria", "Deite-se na cama.");
            state.objetivoSecundario = props.getProperty("objetivoSecundario", "");
            state.checkpoint = props.getProperty("checkpoint", "");
            state.tempoJogoMs = lerLong(props, "tempoJogoMs", 0);
            state.salvoEmMs = lerLong(props, "salvoEmMs", 0);
            state.itens = separar(props.getProperty("itens", ""));
            state.npcsConhecidos = separar(props.getProperty("npcsConhecidos", ""));
            state.eventosHistoria = separar(props.getProperty("eventosHistoria", ""));
            state.memorias = separar(props.getProperty("memorias", ""));
            return Optional.of(state);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean existeSave(int slot) {
        return arquivoSlot(slot).exists();
    }

    public boolean existeAlgumSave() {
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            if (existeSave(i)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Integer> getSlotMaisRecente() {
        long melhorData = -1;
        int melhorSlot = -1;
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            Optional<GameState> state = carregar(i);
            if (state.isPresent() && state.get().salvoEmMs > melhorData) {
                melhorData = state.get().salvoEmMs;
                melhorSlot = i;
            }
        }
        return melhorSlot == -1 ? Optional.empty() : Optional.of(melhorSlot);
    }

    public String getResumoSlot(int slot) {
        Optional<GameState> state = carregar(slot);
        if (!state.isPresent()) {
            return "SLOT " + slot + " - vazio";
        }

        GameState s = state.get();
        String data = s.salvoEmMs <= 0
                ? "sem data"
                : DATA_FORMATO.format(Instant.ofEpochMilli(s.salvoEmMs));
        return "SLOT " + slot + " - " + s.getResumoProgresso() + " - " + data;
    }

    public void apagarTodos() {
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            File arquivo = arquivoSlot(i);
            if (arquivo.exists()) {
                arquivo.delete();
            }
        }
    }

    private File arquivoSlot(int slot) {
        return new File(pastaSave, "slot" + slot + ".properties");
    }

    private String juntar(Set<String> valores) {
        if (valores == null || valores.isEmpty()) {
            return "";
        }
        return String.join(",", valores);
    }

    private Set<String> separar(String valor) {
        Set<String> itens = new LinkedHashSet<String>();
        if (valor == null || valor.trim().isEmpty()) {
            return itens;
        }
        for (String parte : valor.split(",")) {
            String item = parte.trim();
            if (!item.trim().isEmpty()) {
                itens.add(item);
            }
        }
        return itens;
    }

    private int lerInt(Properties props, String chave, int padrao) {
        try {
            return Integer.parseInt(props.getProperty(chave, String.valueOf(padrao)));
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    private long lerLong(Properties props, String chave, long padrao) {
        try {
            return Long.parseLong(props.getProperty(chave, String.valueOf(padrao)));
        } catch (NumberFormatException e) {
            return padrao;
        }
    }
}
