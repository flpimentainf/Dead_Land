package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.entity.player;
import DEAD.LAND.ui.Inventario;
import DEAD.LAND.world.tileMap;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

public class ValidacaoTecnica {
    public static void main(String[] args) throws Exception {
        validarColisaoETransicao();
        validarInventario();
        validarDano();
        validarSave();
        System.out.println("Validacoes tecnicas OK");
    }

    private static void validarColisaoETransicao() {
        tileMap mapa = new tileMap();
        exigir(mapa.getLarguraTotal() > 0, "mapa deve ter largura");
        exigir(mapa.tileTemColisao(3, 19), "objeto interativo solido deve bloquear");

        mapa.irParaCenario(5);
        exigir(mapa.getCenarioAtualIndex() == 5, "transicao direta deve trocar cenario");
        exigir(mapa.getCenarioAtual() != null, "cenario atual deve ter metadados");
        mapa.irParaProximoCenario();
        exigir(mapa.getCenarioAtualIndex() == 6, "proximo cenario deve avancar");
    }

    private static void validarInventario() {
        Inventario inventario = new Inventario();
        inventario.adicionar(new item(
                "repos/tiles/tile (48).png",
                0,
                0,
                0,
                "flecha",
                item.Categoria.MUNICAO,
                8,
                true
        ));
        exigir(inventario.getQuantidadeFlechas() > 0, "flecha deve virar municao");

        int antes = inventario.getQuantidadeFlechas();
        exigir(inventario.consumirFlecha(), "deve consumir uma flecha");
        exigir(inventario.getQuantidadeFlechas() == antes - 1, "municao deve diminuir");

        inventario.definirQuantidadeCura(1);
        player jogador = new player();
        jogador.levarDano(40);
        inventario.usarCura(jogador);
        exigir(jogador.getVida() > 60, "cura deve restaurar vida");
    }

    private static void validarDano() {
        player jogador = new player();
        exigir(jogador.levarDano(25), "primeiro dano deve entrar");
        exigir(jogador.getVida() == 75, "vida deve reduzir");
        exigir(!jogador.levarDano(25), "invulnerabilidade deve bloquear dano repetido");
        jogador.definirVida(0);
        exigir(jogador.estaMorto(), "jogador deve morrer em zero HP");
        jogador.prepararRespawn();
        exigir(jogador.getVida() == player.VIDA_MAXIMA, "respawn deve restaurar vida");
    }

    private static void validarSave() throws Exception {
        File pasta = Files.createTempDirectory("dead-land-save").toFile();
        SaveManager saves = new SaveManager(pasta);

        GameState state = new GameState();
        state.slot = 1;
        state.cenarioIndex = 5;
        state.jogadorX = 100;
        state.jogadorY = 200;
        state.vida = 77;
        state.flechas = 4;
        state.capacidadeFlechas = 20;
        state.fragmentosCura = 1;
        state.checkpoint = "regiao_principal";
        state.itens.add("chave");
        state.memorias.add("memoria_01");

        exigir(saves.salvar(state), "save deve ser gravado");
        Optional<GameState> carregado = saves.carregar(1);
        exigir(carregado.isPresent(), "save deve carregar");
        exigir(carregado.get().cenarioIndex == 5, "cenario salvo deve voltar");
        exigir("regiao_principal".equals(carregado.get().checkpoint), "checkpoint deve persistir");
        exigir(carregado.get().itens.contains("chave"), "itens devem persistir");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
