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

        inventario.adicionar(new item(
                item.SPRITE_POCAO_CURA, 0, 0, 0,
                "pocao_cura", item.Categoria.CONSUMIVEL, 2, true
        ));
        exigir(inventario.temItem("pocao_cura"), "pocao coletada deve aparecer no inventario");
        exigir(inventario.getQuantidadeCura() == 2, "pocoes devem empilhar");
        player jogador = new player();
        exigir(!inventario.usarCura(jogador), "vida cheia nao deve consumir pocao");
        exigir(inventario.getQuantidadeCura() == 2, "pocoes devem ser preservadas com vida cheia");
        jogador.levarDano(40);
        exigir(inventario.usarCura(jogador), "pocao deve poder ser usada apos dano");
        exigir(jogador.getVida() == 90, "pocao deve restaurar 30 HP");
        exigir(inventario.getQuantidadeCura() == 1, "cura deve consumir uma pocao");

        Inventario recompensa = new Inventario();
        recompensa.definirQuantidadeCura(1);
        exigir(recompensa.temItem("pocao_cura"), "pocao de recompensa deve aparecer no inventario");
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
        state.itens.add("fragmento_cura");
        state.memorias.add("memoria_01");

        exigir(saves.salvar(state), "save deve ser gravado");
        Optional<GameState> carregado = saves.carregar(1);
        exigir(carregado.isPresent(), "save deve carregar");
        exigir(carregado.get().cenarioIndex == 5, "cenario salvo deve voltar");
        exigir("regiao_principal".equals(carregado.get().checkpoint), "checkpoint deve persistir");
        exigir(carregado.get().itens.contains("chave"), "itens devem persistir");
        exigir(carregado.get().itens.contains("pocao_cura"), "cura de save antigo deve virar pocao");
        exigir(!carregado.get().itens.contains("fragmento_cura"), "identificador antigo deve ser migrado");
        exigir(carregado.get().fragmentosCura == 1, "migracao deve preservar quantidade de pocoes");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
