package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.Inventario;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.MapaVila;
import DEAD.LAND.world.GeometriaConstrucao;
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
            validarFachada(mapa, casa);
        }
        exigir(mapa.pontoTemColisao(23 * 48, 13 * 48), "água do lago precisa ter colisão");
        exigir(!mapa.pontoTemColisao(MapaVila.AREA_LAGO.x, MapaVila.AREA_LAGO.y),
                "margem seca fora da água não pode bloquear como um retângulo");
        exigir(acessivel[MapaVila.ARI_LINHA][MapaVila.ARI_COLUNA], "Ari precisa continuar acessível");
        item orbe = new ControladorItens().getItensPorNomes(Set.of("memoria_02")).getFirst();
        exigir(acessivel[orbe.y / 48][orbe.x / 48], "a segunda memória precisa continuar acessível");
        for (int c = 0; c < 50; c++) exigir(mapa.tileTemColisao(24, c), "borda sul deve impedir saídas imprevistas");
        for (int c = 35; c < 40; c++) exigir(mapa.tileTemColisao(23, c), "saída 2 deve estar barricada");
        exigir(new ControladorInimigos().getInimigos().stream().noneMatch(i -> i.getCenarioIndex() == 3),
                "a primeira vila deve ser uma área segura sem inimigos");
        SwingUtilities.invokeAndWait(ValidacaoVila::validarPartida);
    }

    private static void validarFachada(tileMap mapa, MapaVila.Construcao casa) {
        var geometria = GeometriaConstrucao.de(casa);
        Rectangle origem = casa.area();
        Rectangle livre = new Rectangle(origem.x + 12, origem.y + 6, 28, 24);
        exigir(!mapa.areaTemColisao(livre), "canto vazio junto do telhado deve ficar livre: " + casa.nome());
        verificadorDeColisao colisao = new verificadorDeColisao();
        for (var volume : geometria.volumes()) {
            Rectangle parede = volume.parede(); parede.translate(origem.x, origem.y);
            exigir(mapa.pontoTemColisao((int) parede.getCenterX(), (int) parede.getCenterY()),
                    "paredes precisam impedir passagem: " + casa.nome());
            Rectangle frente = new Rectangle((int) parede.getCenterX() - 14, parede.y + parede.height + 2, 28, 24);
            exigir(colisao.ocorreuColisao(frente, mapa, 0, -4), "andar contra a fachada deve parar na base desenhada");
            Rectangle lateral = new Rectangle(parede.x - 30, (int) parede.getCenterY() - 12, 28, 24);
            exigir(colisao.ocorreuColisao(lateral, mapa, 4, 0), "lateral da parede deve bloquear aproximação");
        }
        for (Point acesso : geometria.acessos()) {
            exigir(MapaVila.caminhos().contains(acesso.x, acesso.y + 6),
                    "rua precisa tocar a soleira de cada porta: " + casa.nome());
            exigir(!mapa.areaTemColisao(new Rectangle(acesso.x - 14, acesso.y - 3, 28, 24)),
                    "jogador precisa conseguir encostar na soleira: " + casa.nome());
        }
        // O teto visível pode cobrir o ator; os cantos transparentes não podem cobri-lo.
        var imagem = new java.awt.image.BufferedImage(2400, 1200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        var g = imagem.createGraphics();
        var telhado = geometria.volumes().stream().filter(GeometriaConstrucao.Volume::empena).findFirst().orElseThrow();
        Rectangle atras = new Rectangle(origem.x + telhado.x() + 18,
                origem.y + telhado.y() + telhado.telhado() * 45 / 100 - 18, 48, 48);
        try {
            mapa.aplicarOclusao(g, atras); g.setColor(java.awt.Color.MAGENTA); g.fill(atras);
        } finally { g.dispose(); }
        int visiveis = 0;
        for (int y = atras.y; y < atras.y + atras.height; y++) for (int x = atras.x; x < atras.x + atras.width; x++) {
            if (imagem.getRGB(x, y) == java.awt.Color.MAGENTA.getRGB()) visiveis++;
        }
        exigir(visiveis > 0 && visiveis < 48 * 48, "oclusão deve acompanhar o telhado e suas transparências: " + casa.nome());
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
            var casa = MapaVila.CONSTRUCOES.getFirst();
            var volume = GeometriaConstrucao.de(casa).volumes().getLast();
            Rectangle parede = volume.parede(); parede.translate(casa.area().x, casa.area().y);
            cena.irParaCenario(3, casa.portaX() - 24, casa.area().y + 6);
            int inicioY = cena.getJogador().y;
            escutadorTeclado aproximar = new escutadorTeclado(); aproximar.movePraBaixo = true;
            ControladorMovimento andar = new ControladorMovimento();
            for (int i = 0; i < 80; i++) andar.atualizar(cena, aproximar);
            exigir(cena.getJogador().y > inicioY && cena.getJogador().y + 48 <= parede.y,
                    "deve andar pelo espaço do teto e parar somente ao chegar na parede");
            exigir(cena.getJogador().AreaColisao.getCenterX() == cena.getJogador().getCenterX(),
                    "colisor do jogador deve ficar centralizado no personagem");
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
