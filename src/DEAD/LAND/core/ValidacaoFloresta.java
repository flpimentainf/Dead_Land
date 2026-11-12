package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.entity.player;
import DEAD.LAND.entity.TipoInimigo;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.Inventario;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tileMap;
import DEAD.LAND.world.MapaFloresta;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.Set;
import javax.swing.SwingUtilities;

/** Regressões da importação da floresta: nascimento, caminhos, itens e saída. */
public final class ValidacaoFloresta {
    private ValidacaoFloresta() {}

    public static void validar() throws Exception {
        tileMap mapa = new tileMap();
        mapa.irParaCenario(2);
        exigir(mapa.getLarguraTotal() == 2400 && mapa.getAlturaTotal() == 1200,
                "a floresta deve carregar as 50 colunas e 25 linhas do novo mapa");
        player jogador = new player();
        for (int y : new int[]{200, 335}) {
            jogador.posicionarEm(745, y);
            exigir(areaLivre(mapa, jogador.AreaColisao),
                    "o nascimento existente deve continuar livre em 745," + y);
        }

        boolean[][] acessivel = explorar(mapa, 4, 15);
        exigir(acessivel[7][15], "o retorno após o final deve se conectar à clareira inicial");
        exigir(acessivel[6][22], "o Eco deve continuar acessível");
        exigir(acessivel[5][49], "deve existir caminho até a saída para o Bosque de Ari");
        var itens = new ControladorItens().getItensPorNomes(Set.of("memoria_01", "pocao_cura"));
        int orbes = 0;
        int pocoes = 0;
        for (item coletavel : itens) {
            if (coletavel.getCenarioIndex() != 2) continue;
            exigir(acessivel[coletavel.y / 48][coletavel.x / 48],
                    "todo item importado deve ser alcançável: " + coletavel.getNome());
            if (coletavel.getNome().equals("memoria_01")) {
                orbes++;
                exigir(coletavel.x == 192 && coletavel.y == 144, "a orbe deve usar a posição enviada");
            } else pocoes++;
        }
        exigir(orbes == 1 && pocoes == 3, "o mapa deve conter uma orbe e três poções");
        validarInimigos(mapa, acessivel);

        // Cada partida precisa de suas próprias camadas editáveis.
        mapa.getCenarioAtual().getMapaBase()[4][15] = 0;
        mapa.getCenarioAtual().getCamadaObjetos()[4][15] = 56;
        tileMap outraPartida = new tileMap();
        outraPartida.irParaCenario(2);
        exigir(!outraPartida.tileTemColisao(4, 15), "uma partida não pode modificar outra floresta");
        SwingUtilities.invokeAndWait(ValidacaoFloresta::validarQuedaESaida);
    }

    private static void validarInimigos(tileMap mapa, boolean[][] acessivel) {
        ControladorInimigos controlador = new ControladorInimigos();
        var inimigos = controlador.getInimigos().stream()
                .filter(inimigo -> inimigo.getCenarioIndex() == 2).toList();
        exigir(inimigos.size() == 16, "a floresta deve carregar os 16 inimigos enviados");
        exigir(inimigos.stream().filter(i -> i.getTipo() == TipoInimigo.SKELETON).count() == 6
                        && inimigos.stream().filter(i -> i.getTipo() == TipoInimigo.SLIME).count() == 10,
                "a floresta deve conter seis esqueletos e dez slimes");
        for (SpawnInimigo spawn : MapaFloresta.criarSpawns()) {
            var inimigo = inimigos.stream().filter(i -> i.x == spawn.getX()
                    && i.y == spawn.getY() && i.getTipo() == spawn.getTipo()).findFirst().orElseThrow();
            exigir(areaLivre(mapa, inimigo.getAreaDano()),
                    "inimigo deve nascer fora dos obstáculos em " + inimigo.x + "," + inimigo.y);
            exigir(acessivel[inimigo.y / 48][inimigo.x / 48],
                    "a posição de cada inimigo deve ser acessível ao jogador");
            exigir(!inimigo.intersects(new Rectangle(745, 200, 48, 48))
                            && !inimigo.intersects(new Rectangle(745, 335, 48, 48)),
                    "inimigos não devem ocupar os pontos de nascimento existentes");
            inimigo.marcarDerrotado();
            inimigo.translate(12, 12);
        }
        controlador.resetarCenario(2, false);
        for (SpawnInimigo spawn : MapaFloresta.criarSpawns()) {
            exigir(inimigos.stream().anyMatch(i -> i.x == spawn.getX() && i.y == spawn.getY()
                            && i.estaVivo() && i.getVida() == i.getVidaMaxima()),
                    "retorno ao checkpoint deve restaurar cada inimigo na posição enviada");
        }
        exigir(controlador.getInimigos().size() == 25
                        && controlador.getInimigos().stream().filter(i -> i.getCenarioIndex() == 5).count() == 3
                        && controlador.getInimigos().stream().filter(i -> i.getCenarioIndex() == 6).count() == 5
                        && controlador.getChefe().getCenarioIndex() == 7,
                "a importação deve preservar os inimigos dos outros cenários e o chefe");
    }

    private static boolean[][] explorar(tileMap mapa, int linha, int coluna) {
        boolean[][] acessivel = new boolean[25][50];
        ArrayDeque<Point> fila = new ArrayDeque<>();
        fila.add(new Point(coluna, linha));
        acessivel[linha][coluna] = true;
        int[][] passos = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!fila.isEmpty()) {
            Point atual = fila.remove();
            for (int[] passo : passos) {
                int c = atual.x + passo[0];
                int l = atual.y + passo[1];
                if (l < 0 || l >= 25 || c < 0 || c >= 50 || acessivel[l][c]) continue;
                if (!areaLivre(mapa, new Rectangle(c * 48 + 8, l * 48 + 8, 28, 24))) continue;
                acessivel[l][c] = true;
                fila.add(new Point(c, l));
            }
        }
        return acessivel;
    }

    private static boolean areaLivre(tileMap mapa, Rectangle area) {
        for (int l = area.y / 48; l <= (area.y + area.height - 1) / 48; l++) {
            for (int c = area.x / 48; c <= (area.x + area.width - 1) / 48; c++) {
                if (mapa.tileTemColisao(l, c)) return false;
            }
        }
        return true;
    }

    private static void validarQuedaESaida() {
        panel cena = new panel("centro", new Inventario()) {
            @Override public void salvarAuto() {}
            @Override public void salvarAutoForcado() {}
        };
        try {
            cena.pausarJogo();
            cena.irParaCenario(1, 745, 0);
            ControladorQueda queda = new ControladorQueda();
            for (int i = 0; i < 300 && cena.getCenario().getCenarioAtualIndex() == 1; i++) {
                queda.atualizar(cena);
            }
            exigir(cena.getCenario().getCenarioAtualIndex() == 2
                            && cena.getJogador().x == 745 && cena.getJogador().y == 200,
                    "a queda deve manter exatamente o nascimento anterior em 745,200");
            exigir(areaLivre(cena.getCenario(), cena.getJogador().AreaColisao),
                    "a queda deve terminar fora dos obstáculos");
            cena.irParaCenario(2, 2376, 240);
            escutadorTeclado teclado = new escutadorTeclado();
            teclado.movePraDir = true;
            ControladorMovimento movimento = new ControladorMovimento();
            for (int i = 0; i < 100 && cena.getCenario().getCenarioAtualIndex() == 2; i++) {
                movimento.atualizar(cena, teclado);
            }
            exigir(cena.getCenario().getCenarioAtualIndex() == 3,
                    "a borda direita ampliada deve levar ao Bosque de Ari");
            exigir(areaLivre(cena.getCenario(), cena.getJogador().AreaColisao),
                    "a entrada no bosque deve ficar livre");
            teclado.movePraDir = false;
            teclado.movePraEsq = true;
            for (int i = 0; i < 100 && cena.getCenario().getCenarioAtualIndex() == 3; i++) {
                movimento.atualizar(cena, teclado);
            }
            exigir(cena.getCenario().getCenarioAtualIndex() == 2,
                    "deve ser possível retornar à floresta pela mesma passagem");
            exigir(areaLivre(cena.getCenario(), cena.getJogador().AreaColisao),
                    "o retorno deve usar a largura nova sem prender o personagem");
        } finally {
            cena.pausarJogo();
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalStateException(mensagem);
    }
}
