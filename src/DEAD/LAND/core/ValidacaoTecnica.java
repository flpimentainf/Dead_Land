package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.entity.player;
import DEAD.LAND.entity.Flecha;
import DEAD.LAND.ui.Inventario;
import DEAD.LAND.ui.ViewportJogo;
import DEAD.LAND.ui.LayoutMenuPausa;
import DEAD.LAND.ui.panel;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.world.tileMap;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

public class ValidacaoTecnica {
    public static void main(String[] args) throws Exception {
        validarColisaoETransicao();
        validarInventario();
        validarDano();
        validarEstamina();
        validarDashECorrida();
        validarViewport();
        validarFlechasDirecionais();
        SwingUtilities.invokeAndWait(ValidacaoTecnica::validarMouse);
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
        Files.writeString(new File(pasta, "slot1.properties").toPath(), "\nvontade=0\n",
                java.nio.file.StandardOpenOption.APPEND);
        Optional<GameState> carregado = saves.carregar(1);
        exigir(carregado.isPresent(), "save deve carregar");
        exigir(carregado.get().cenarioIndex == 5, "cenario salvo deve voltar");
        exigir("regiao_principal".equals(carregado.get().checkpoint), "checkpoint deve persistir");
        exigir(carregado.get().itens.contains("chave"), "itens devem persistir");
        exigir(carregado.get().itens.contains("pocao_cura"), "cura de save antigo deve virar pocao");
        exigir(!carregado.get().itens.contains("fragmento_cura"), "identificador antigo deve ser migrado");
        exigir(carregado.get().fragmentosCura == 1, "migracao deve preservar quantidade de pocoes");
        exigir(carregado.get().memorias.contains("memoria_01"), "save antigo deve preservar memorias coletadas");
        exigir(carregado.get().jogadorX == 100 && carregado.get().jogadorY == 200,
                "propriedade antiga removida nao deve alterar a posicao salva");
    }

    private static void validarEstamina() {
        SistemaEstamina estamina = new SistemaEstamina();
        exigir(estamina.consumirDash(), "dash deve aceitar estamina cheia");
        exigir(estamina.getValor() == 80f, "um dash deve gastar exatamente 20% da barra");
        estamina.atualizar(0.5, false);
        exigir(estamina.getValor() == 80f, "estamina deve aguardar antes de recarregar");
        estamina.atualizar(0.5, false);
        exigir(Math.abs(estamina.getValor() - 84f) < 0.01f, "estamina deve recarregar gradualmente");
        estamina.restaurarCompleta();
        for (int i = 0; i < 5; i++) exigir(estamina.consumirDash(), "barra cheia deve permitir cinco gastos de dash");
        exigir(!estamina.consumirDash(), "dash sem estamina deve ser bloqueado");
        exigir(estamina.getValor() == 0f, "estamina nao pode ficar negativa");
        estamina.atualizar(2, false);
        exigir(estamina.consumirDash(), "dash deve voltar a funcionar apos recarga suficiente");
        estamina.restaurarCompleta();
        exigir(estamina.atualizar(1, true), "corrida deve iniciar com estamina");
        exigir(estamina.getValor() == 90f, "corrida deve gastar dez pontos por segundo");
        estamina.atualizar(9, true);
        exigir(estamina.getValor() == 0f, "corrida deve esgotar a barra sem valor negativo");
        exigir(!estamina.atualizar(0.1, true), "corrida deve parar ao esgotar a estamina");
        estamina.atualizar(10, false);
        exigir(estamina.getValor() == SistemaEstamina.MAXIMA, "recarga deve respeitar o maximo");
    }

    private static void validarDashECorrida() {
        long[] agora = {0};
        player jogador = new player(() -> agora[0]);
        tileMap mapa = new tileMap();
        mapa.irParaCenario(1);
        ControladorMovimento movimento = new ControladorMovimento();
        escutadorTeclado teclado = new escutadorTeclado();
        teclado.movePraDir = true;
        int inicioX = jogador.x;
        movimento.atualizarMovimento(jogador, mapa, teclado, 0.016);
        exigir(jogador.x - inicioX == jogador.passo, "WASD sem Shift deve manter velocidade de caminhada");
        teclado.dash = true;
        movimento.atualizarMovimento(jogador, mapa, teclado, 0.016);
        exigir(jogador.estaEmDash(), "primeiro toque de Shift deve iniciar dash");
        exigir(jogador.getEstamina() == 80f, "dash deve deixar 80% de estamina");
        agora[0] = 240;
        inicioX = jogador.x;
        movimento.atualizarMovimento(jogador, mapa, teclado, 0.016);
        exigir(!jogador.estaEmDash() && jogador.estaCorrendo(), "segurar Shift apos dash deve manter corrida");
        exigir(jogador.x - inicioX > jogador.passo, "corrida deve ser mais rapida que caminhada");
        exigir(!jogador.iniciarDash(false, false, true, false), "cooldown deve impedir dash antes de um segundo");
        agora[0] = 1000;
        movimento.atualizarMovimento(jogador, mapa, teclado, 0.016);
        exigir(!jogador.estaEmDash(), "Shift segurado nao deve disparar novos dashes automaticamente");
        teclado.dash = false;
        movimento.atualizarMovimento(jogador, mapa, teclado, 0.016);
        exigir(!jogador.estaCorrendo(), "soltar Shift deve encerrar corrida");
        float antesDash = jogador.getEstamina();
        teclado.dash = true;
        movimento.atualizarMovimento(jogador, mapa, teclado, 0.016);
        exigir(jogador.estaEmDash(), "novo toque deve permitir dash apos exatamente um segundo");
        exigir(Math.abs(antesDash - jogador.getEstamina() - 20f) < 0.01f, "segundo dash tambem deve gastar somente 20 pontos");
        jogador.prepararRespawn();
        exigir(jogador.getEstamina() == 100f && !jogador.estaCorrendo(), "respawn deve restaurar estamina e interromper corrida");
        jogador.definirVida(0);
        exigir(!jogador.iniciarDash(false, false, true, false), "personagem morto nao pode usar dash");

        player pertoDaParede = new player(() -> 0);
        mapa.definirTile(4, 18, 60);
        pertoDaParede.posicionarEm(830, 168);
        exigir(pertoDaParede.iniciarDash(false, false, true, false), "dash deve iniciar perto da parede");
        pertoDaParede.atualizarDash(mapa, new verificadorDeColisao());
        exigir(pertoDaParede.AreaColisao.x + pertoDaParede.AreaColisao.width <= 18 * 48,
                "dash nao pode atravessar parede");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void validarViewport() {
        ViewportJogo viewport = new ViewportJogo(768, 480);
        Point2D centro = viewport.paraLogico(new Point(960, 540), 1920, 1080);
        exigir(centro.getX() == 384 && centro.getY() == 240, "mira deve compensar escala e margens laterais");
        exigir(viewport.paraLogico(new Point(30, 540), 1920, 1080) == null, "margem preta nao deve aceitar disparos");
        centro = viewport.paraLogico(new Point(400, 400), 800, 800);
        exigir(centro.getX() == 384 && centro.getY() == 240, "mira deve compensar margens verticais");
        exigir(viewport.paraLogico(new Point(400, 20), 800, 800) == null, "margem vertical deve ignorar cliques");
        for (int i = 0; i < 5; i++) {
            Rectangle opcao = LayoutMenuPausa.opcaoPrincipal(i);
            exigir(LayoutMenuPausa.indicePrincipal(new Point(opcao.x + 10, opcao.y + 10)) == i,
                    "cada opcao deve ter area de clique independente");
        }
        exigir(LayoutMenuPausa.volumeEmX(-100) == 0f && LayoutMenuPausa.volumeEmX(1000) == 1f,
                "arraste de volume deve ser limitado entre zero e cem por cento");
    }

    private static void validarFlechasDirecionais() {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Flecha flecha = new Flecha(100, 100, dx, dy, 10, 30, 8, 400);
                for (int i = 0; i < 10; i++) flecha.atualizar();
                double tamanho = Math.hypot(dx, dy);
                exigir(Math.abs(flecha.getCentroX() - (100 + 100 * dx / tamanho)) <= 1,
                        "flecha deve viajar no eixo X da mira");
                exigir(Math.abs(flecha.getCentroY() - (100 + 100 * dy / tamanho)) <= 1,
                        "flecha deve viajar no eixo Y da mira");
                exigir(Math.abs(flecha.getAngulo() - Math.atan2(dy, dx)) < 0.0001,
                        "ponta da flecha deve acompanhar o angulo de movimento");
            }
        }
        Flecha livre = new Flecha(100, 100, 3, 4, 10, 30, 8, 100);
        for (int i = 0; i < 10; i++) livre.atualizar();
        exigir(livre.getCentroX() == 160 && livre.getCentroY() == 180, "mira deve permitir angulos alem de oito direcoes");
        exigir(!livre.isAtiva(), "alcance deve ser igual para tiros diagonais");
        Flecha diagonal = new Flecha(100, 100, 1, 1, 10, 30, 8, 400);
        exigir(diagonal.intersects(new Rectangle(105, 105, 3, 3)), "corpo diagonal deve acertar alvos");
        exigir(!diagonal.intersects(new Rectangle(89, 109, 2, 2)), "canto vazio da caixa diagonal nao deve acertar alvos");
    }

    private static void validarMouse() {
        Inventario inventario = new Inventario();
        inventario.adicionar(new item("repos/tiles/tile (47).png", 0, 0, 0, -1, "arco"));
        inventario.adicionar(new item("repos/tiles/tile (48).png", 0, 0, 0, -1, "flecha"));
        panel cena = new panel("centro", inventario);
        float volumeAnterior = AudioManager.getInstancia().getVolume(AudioManager.Canal.MASTER);
        try {
            cena.pausarJogo();
            cena.setSize(1280, 960);
            cena.getCenario().irParaCenario(1);
            cena.getHistoria().restaurarAposRespawn(null, inventario.getNomesItens(), 1, false);
            int municao = inventario.getQuantidadeFlechas();
            clicar(cena, new Point2D.Double(500, 100));
            exigir(inventario.getQuantidadeFlechas() == municao - 1, "clique esquerdo deve consumir uma flecha");
            exigir(cena.getControladorFlechas().getFlechas().size() == 1, "clique deve gerar somente um projetil");
            Point2D mira = cena.getMiraMundo();
            player jogador = cena.getJogador();
            double angulo = Math.atan2(mira.getY() - jogador.y - jogador.height / 2.0,
                    mira.getX() - jogador.x - jogador.width / 2.0);
            exigir(Math.abs(cena.getControladorFlechas().getFlechas().get(0).getAngulo() - angulo) < 0.0001,
                    "disparo deve compensar o deslocamento da camera");
            clicar(cena, new Point2D.Double(300, 100));
            exigir(inventario.getQuantidadeFlechas() == municao - 1, "cliques durante cooldown nao devem consumir municao");

            escutadorTeclado teclado = new escutadorTeclado();
            teclado.abrirMenu = true;
            cena.atualizarMenuPausa(teclado);
            teclado.abrirMenu = false;
            cena.atualizarMenuPausa(teclado);
            clicar(cena, new Point2D.Double(384, 290));
            eventoMouse(cena, MouseEvent.MOUSE_MOVED, new Point2D.Double(400, 127));
            exigir(cena.getCursor().getType() == Cursor.HAND_CURSOR, "opcoes do menu devem indicar clique com o cursor");
            clicar(cena, new Point2D.Double(382.5, 127));
            exigir(Math.abs(AudioManager.getInstancia().getVolume(AudioManager.Canal.MASTER) - 0.25f) < 0.02f,
                    "clique na barra deve ajustar o volume");
            eventoMouse(cena, MouseEvent.MOUSE_PRESSED, new Point2D.Double(400, 127));
            eventoMouse(cena, MouseEvent.MOUSE_DRAGGED, new Point2D.Double(495, 127));
            eventoMouse(cena, MouseEvent.MOUSE_RELEASED, new Point2D.Double(495, 127));
            exigir(AudioManager.getInstancia().getVolume(AudioManager.Canal.MASTER) == 1f, "arrastar deve ajustar o volume ate o fim");
            clicar(cena, new Point2D.Double(384, 327));
            clicar(cena, new Point2D.Double(384, 246));
            clicar(cena, new Point2D.Double(384, 440));
            clicar(cena, new Point2D.Double(384, 158));
            exigir(!cena.isMenuPausaAberto(), "botoes de voltar e continuar devem funcionar com mouse");
            exigir(cena.getControladorFlechas().getFlechas().size() == 1, "cliques no menu nao podem disparar flechas");

            long[] agora = {0};
            ControladorFlechas tiros = new ControladorFlechas(() -> agora[0]);
            exigir(tiros.dispararMouse(cena, new Point2D.Double(1000, 100)), "tiro valido deve ser permitido");
            agora[0] = 449;
            exigir(!tiros.dispararMouse(cena, new Point2D.Double(1000, 100)), "cooldown de tiro deve ser preservado");
            agora[0] = 450;
            exigir(tiros.dispararMouse(cena, new Point2D.Double(1000, 100)), "novo tiro deve ser permitido apos cooldown");
            inventario.definirFlechas(0);
            agora[0] = 900;
            exigir(!tiros.dispararMouse(cena, new Point2D.Double(1000, 100)), "arco sem municao nao deve disparar");
        } finally {
            cena.pausarJogo();
            AudioManager.getInstancia().definirVolume(AudioManager.Canal.MASTER, volumeAnterior);
        }
    }

    private static void clicar(panel cena, Point2D ponto) {
        eventoMouse(cena, MouseEvent.MOUSE_PRESSED, ponto);
        eventoMouse(cena, MouseEvent.MOUSE_RELEASED, ponto);
    }

    private static void eventoMouse(panel cena, int tipo, Point2D logico) {
        Rectangle area = new ViewportJogo(768, 480).getArea(cena.getWidth(), cena.getHeight());
        int x = (int) Math.round(area.x + logico.getX() * area.width / 768.0);
        int y = (int) Math.round(area.y + logico.getY() * area.height / 480.0);
        boolean movimento = tipo == MouseEvent.MOUSE_MOVED || tipo == MouseEvent.MOUSE_DRAGGED;
        cena.dispatchEvent(new MouseEvent(cena, tipo, System.currentTimeMillis(),
                tipo == MouseEvent.MOUSE_DRAGGED ? MouseEvent.BUTTON1_DOWN_MASK : 0,
                x, y, movimento ? 0 : 1, false, movimento ? MouseEvent.NOBUTTON : MouseEvent.BUTTON1));
    }
}
