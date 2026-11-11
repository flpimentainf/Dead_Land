package DEAD.LAND.core;

import DEAD.LAND.core.SistemaConquistas.Conquista;
import DEAD.LAND.entity.item;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.Inventario;
import DEAD.LAND.ui.panel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;

public final class ValidacaoConquistas {
    public static void main(String[] args) throws Exception {
        validarMarcos();
        Path pasta = Files.createTempDirectory("deadland-conquistas-");
        try {
            SwingUtilities.invokeAndWait(() -> validarColetaESave(pasta));
            SwingUtilities.invokeAndWait(() -> ValidacaoInterfaceConquistas.validar(pasta));
        } finally {
            for (int slot = 1; slot <= 3; slot++) Files.deleteIfExists(pasta.resolve("slot" + slot + ".properties"));
            Files.delete(pasta);
        }
        System.out.println("Validacoes de conquistas OK");
    }

    private static void validarMarcos() {
        SistemaConquistas conquistas = new SistemaConquistas();
        Inventario inventario = new Inventario();
        MemoryManager memorias = new MemoryManager();
        conquistas.atualizar(inventario, memorias, true);
        exigir(conquistas.getQuantidade() == 0, "partida nova começa sem conquistas");
        memorias.coletar("memoria_01"); conquistas.atualizar(inventario, memorias, true);
        exigir(conquistas.desbloqueada(Conquista.ECO_PASSADO), "primeira orbe");
        memorias.coletar("memoria_01"); conquistas.atualizar(inventario, memorias, true);
        exigir(conquistas.getQuantidade() == 1, "mesma orbe não conta duas vezes");
        memorias.coletar("memoria_02"); conquistas.atualizar(inventario, memorias, true);
        exigir(!conquistas.desbloqueada(Conquista.MEMORIA_FRAGMENTADA), "duas orbes não concluem três");
        memorias.coletar("memoria_03"); conquistas.atualizar(inventario, memorias, true);
        exigir(conquistas.desbloqueada(Conquista.MEMORIA_FRAGMENTADA), "terceira orbe");
        memorias.coletar("memoria_04"); conquistas.atualizar(inventario, memorias, true);
        exigir(!conquistas.desbloqueada(Conquista.MESTRE_LEMBRANCAS), "quatro não são todas as orbes");
        memorias.coletar("memoria_05"); conquistas.atualizar(inventario, memorias, true);
        exigir(conquistas.desbloqueada(Conquista.MESTRE_LEMBRANCAS), "todas as orbes");
        inventario.adicionar(new item(item.SPRITE_ESPADA, 0, 0, 0, -1, "espada"));
        conquistas.atualizar(inventario, memorias, true);
        exigir(conquistas.desbloqueada(Conquista.LAMINA_DESBRAVADORA) && !conquistas.desbloqueada(Conquista.MIRA_CERTEIRA), "cada arma tem condição própria");
        inventario.adicionar(new item(item.SPRITE_ARCO, 0, 0, 0, -1, "arco"));
        inventario.definirQuantidadeCura(1); conquistas.atualizar(inventario, memorias, true);
        exigir(conquistas.getQuantidade() == 6, "seis conquistas disponíveis");
        inventario.substituirPorItens(List.of()); conquistas.atualizar(inventario, memorias, true);
        exigir(conquistas.getQuantidade() == 6, "remover itens não apaga o histórico");
        int avisos = 0; while (conquistas.consumirNova() != null) avisos++;
        exigir(avisos == 6, "um único aviso por conquista");
        conquistas.restaurar(Set.of("desconhecida"));
        exigir(conquistas.getQuantidade() == 0, "restaurar outro slot limpa conquistas e ignora IDs desconhecidos");
    }

    private static void validarColetaESave(Path pasta) {
        SaveManager saves = new SaveManager(pasta.toFile());
        panel cena = new panel("centro", new Inventario(), null, null, saves, 2);
        try {
            cena.pausarJogo();
            escutadorTeclado teclado = new escutadorTeclado();
            for (String nome : List.of("memoria_01", "memoria_02", "memoria_03", "memoria_04", "memoria_05", "arco", "espada", "pocao_cura")) {
                item alvo = cena.getControladorItens().getItensPorNomes(Set.of(nome)).getFirst();
                cena.irParaCenario(alvo.getCenarioIndex(), alvo.x, alvo.y);
                teclado.interagir = true; cena.getControladorItens().atualizar(cena, teclado);
                teclado.interagir = false; cena.getControladorItens().atualizar(cena, teclado);
                exigir(alvo.foiColetado(), "coleta real deve funcionar: " + nome);
            }
            exigir(cena.getConquistas().getQuantidade() == 6, "coletas reais desbloqueiam conquistas");
            exigir(saves.carregar(2).orElseThrow().conquistas.size() == 6, "autosave da coleta persiste as conquistas no slot ativo");
            cena.getJogador().definirVida(50);
            exigir(cena.getInventario().usarCura(cena.getJogador()), "poção pode ser consumida");
            exigir(cena.salvarSlot(2), "save após consumir poção");
            cena.aplicarGameState(saves.carregar(2).orElseThrow());
            exigir(cena.getConquistas().getQuantidade() == 6 && cena.getConquistas().consumirNova() == null, "carregar preserva conquista sem repetir avisos");
            cena.getSistemaCheckpoint().restaurar(cena);
            exigir(cena.getConquistas().getQuantidade() == 6, "morte não desfaz conquistas");
            GameState novo = new GameState(); novo.slot = 1; novo.vida = 100;
            cena.aplicarGameState(novo);
            exigir(cena.getConquistas().getQuantidade() == 0, "novo slot independente");
            exigir(saves.carregar(2).orElseThrow().conquistas.size() == 6, "outro slot permanece intacto");
            String antigo = Files.readString(pasta.resolve("slot2.properties")).replaceAll("(?m)^conquistas=.*\\R?", "");
            Files.writeString(pasta.resolve("slot2.properties"), antigo);
            cena.aplicarGameState(saves.carregar(2).orElseThrow());
            exigir(cena.getConquistas().getQuantidade() == 6, "save antigo recupera conquistas pelos itens e memórias");
            exigir(cena.getConquistas().consumirNova() == null, "migração não repete avisos");
        } catch (java.io.IOException e) { throw new IllegalStateException(e); }
        finally { cena.pausarJogo(); }
    }

    static void exigir(boolean condicao, String mensagem) { if (!condicao) throw new IllegalStateException(mensagem); }
}
