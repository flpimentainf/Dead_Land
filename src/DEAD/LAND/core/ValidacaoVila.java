package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.Inventario;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.MapaVila;
import DEAD.LAND.world.tileMap;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.Set;
import javax.swing.SwingUtilities;

public final class ValidacaoVila {
    private ValidacaoVila() {}

    public static void validar() throws Exception {
        tileMap mapa = new tileMap(); mapa.irParaCenario(3);
        exigir(mapa.getLarguraTotal() == 2400 && mapa.getAlturaTotal() == 1200, "vila deve medir 50x25 tiles");
        for (int[] linha : mapa.getCenarioAtual().getMapaBase()) {
            for (int id : linha) exigir(id == 55 || id == 5, "construções não podem fazer parte do terreno");
        }
        boolean[][] acessivel = explorar(mapa);
        exigir(acessivel[14][49] && acessivel[4][0], "entrada e saída do castelo devem estar conectadas");
        for (var casa : MapaVila.CONSTRUCOES) {
            exigir(mapa.tileTemColisao(casa.linha(), casa.coluna()), "fachadas precisam bloquear passagem");
            exigir(acessivel[casa.linha() + casa.altura()][casa.coluna() + casa.largura() / 2],
                    "deve ser possível chegar à frente de " + casa.nome());
        }
        exigir(mapa.tileTemColisao(12, 22), "fonte precisa ter colisão");
        exigir(acessivel[MapaVila.ARI_LINHA][MapaVila.ARI_COLUNA], "Ari precisa continuar acessível");
        item orbe = new ControladorItens().getItensPorNomes(Set.of("memoria_02")).getFirst();
        exigir(acessivel[orbe.y / 48][orbe.x / 48], "a segunda memória precisa continuar acessível");
        for (int c = 0; c < 50; c++) exigir(mapa.tileTemColisao(24, c), "borda sul deve impedir saídas imprevistas");
        for (int c = 35; c < 40; c++) exigir(mapa.tileTemColisao(23, c), "saída 2 deve estar barricada");
        exigir(new ControladorInimigos().getInimigos().stream().noneMatch(i -> i.getCenarioIndex() == 3),
                "a primeira vila deve ser uma área segura sem inimigos");
        SwingUtilities.invokeAndWait(ValidacaoVila::validarPartida);
    }

    private static boolean[][] explorar(tileMap mapa) {
        boolean[][] vistos = new boolean[25][50];
        ArrayDeque<Point> fila = new ArrayDeque<>();
        fila.add(new Point(1, 4)); vistos[4][1] = true;
        int[][] passos = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!fila.isEmpty()) {
            Point atual = fila.remove();
            for (int[] passo : passos) {
                int c = atual.x + passo[0], l = atual.y + passo[1];
                if (c < 0 || c >= 50 || l < 0 || l >= 25 || vistos[l][c] || mapa.tileTemColisao(l, c)) continue;
                vistos[l][c] = true; fila.add(new Point(c, l));
            }
        }
        return vistos;
    }

    private static void validarPartida() {
        Inventario inventario = new Inventario();
        panel cena = new panel("centro", inventario) {
            @Override public void salvarAuto() {}
            @Override public void salvarAutoForcado() {}
        };
        try {
            cena.pausarJogo();
            atravessar(cena, 2, 2376, 240, true, 3);
            exigir(cena.getJogador().y == MapaVila.ENTRADA_Y, "a entrada deve respeitar a altura da vila nova");
            atravessar(cena, 3, 2340, MapaVila.SAIDA_CASTELO_Y, true, 4);
            exigir(cena.getJogador().y == 240, "a saída 1 deve manter a conexão normal com o castelo");
            atravessar(cena, 4, 50, 240, false, 3);
            exigir(cena.getJogador().y == MapaVila.SAIDA_CASTELO_Y, "retorno do castelo deve entrar pela saída 1");
            atravessar(cena, 3, 50, MapaVila.ENTRADA_Y, false, 2);
            cena.irParaCenario(3, 37 * 48, 22 * 48 - 24);
            escutadorTeclado teclado = new escutadorTeclado(); teclado.movePraBaixo = true;
            ControladorMovimento movimento = new ControladorMovimento();
            for (int i = 0; i < 100; i++) movimento.atualizar(cena, teclado);
            exigir(cena.getCenario().getCenarioAtualIndex() == 3 && cena.getJogador().y + 48 <= 23 * 48,
                    "o jogador não pode atravessar a barricada da saída 2");

            GameState antigo = cena.criarGameState(1);
            antigo.jogadorX = 10 * 48; antigo.jogadorY = 3 * 48;
            antigo.estadoHistoria = "FLORESTA";
            antigo.objetivoHistoria = "Encontre Ari na vila.";
            antigo.memorias = Set.of("memoria_01", "memoria_02");
            cena.aplicarGameState(antigo);
            exigir(MapaVila.posicaoLivre(cena.getCenario(), cena.getJogador().AreaColisao),
                    "saves antigos dentro de uma casa precisam ser reposicionados em área livre");
            exigir(cena.getMemoryManager().getQuantidadeDescoberta() == 2,
                    "a adaptação da vila não pode apagar as memórias coletadas");
            cena.getSistemaCheckpoint().avaliarCheckpointAutomatico(cena);
            exigir(cena.getSistemaCheckpoint().getNomeCheckpointAtual().equals("bosque_ari"),
                    "chegar à vila segura deve registrar um checkpoint");
            cena.irParaCenario(2, 745, 200);
            cena.getSistemaCheckpoint().restaurar(cena);
            exigir(cena.getCenario().getCenarioAtualIndex() == 3, "checkpoint deve retornar à vila");
            exigir(!cena.getControladorInimigos().existeCombateAtivo(cena), "não pode haver combate ativo na vila");
            int vida = cena.getJogador().getVida();
            for (int i = 0; i < 60; i++) cena.getControladorInimigos().atualizar(cena, cena.getControladorFlechas());
            exigir(cena.getJogador().getVida() == vida, "área segura não deve receber dano de inimigos");
        } finally { cena.pausarJogo(); }
    }

    private static void atravessar(panel cena, int origem, int x, int y, boolean direita, int destino) {
        cena.irParaCenario(origem, x, y);
        escutadorTeclado teclado = new escutadorTeclado();
        teclado.movePraDir = direita; teclado.movePraEsq = !direita;
        ControladorMovimento movimento = new ControladorMovimento();
        for (int i = 0; i < 160 && cena.getCenario().getCenarioAtualIndex() == origem; i++) movimento.atualizar(cena, teclado);
        exigir(cena.getCenario().getCenarioAtualIndex() == destino, "conexão deve funcionar de " + origem + " para " + destino);
        exigir(MapaVila.posicaoLivre(cena.getCenario(), cena.getJogador().AreaColisao), "transição não pode prender o personagem");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalStateException(mensagem);
    }
}
