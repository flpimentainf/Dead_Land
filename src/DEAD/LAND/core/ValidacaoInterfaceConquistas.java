package DEAD.LAND.core;

import DEAD.LAND.core.SistemaConquistas.Conquista;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import javax.imageio.ImageIO;

final class ValidacaoInterfaceConquistas {
    static void validar(Path pasta) {
        validarIconesEAvisos();
        panel cena = new panel("centro", new Inventario(), null, null, new SaveManager(pasta.toFile()), 1);
        panel rodape = new panel("sul", cena.getInventario());
        cena.setPainelInventario(rodape);
        PainelPartida partida = new PainelPartida(cena, rodape);
        try {
            cena.pausarJogo();
            partida.setSize(768, 584); partida.doLayout();
            exigir(!cena.isTelaConquistasAberta(), "conquistas não abrem fora do menu de pausa");
            GameState state = cena.criarGameState(1);
            state.cenarioIndex = 5; state.estadoHistoria = "FLORESTA";
            Path saida = Path.of("build/hud"); Files.createDirectories(saida);
            for (int fase = 0; fase < 3; fase++) {
                fecharPausa(cena);
                state.memorias = fase == 0 ? Set.of() : fase == 1 ? Set.of("memoria_01", "memoria_02", "memoria_03")
                        : Set.of("memoria_01", "memoria_02", "memoria_03", "memoria_04", "memoria_05");
                state.itens = fase == 0 ? Set.of() : fase == 1 ? Set.of("arco") : Set.of("arco", "espada", "pocao_cura");
                state.fragmentosCura = fase == 2 ? 1 : 0;
                cena.aplicarGameState(state);
                alternarPausa(cena);
                clicar(cena, new Point2D.Double(384, 422));
                exigir(cena.isTelaConquistasAberta(), "ESC e CONQUISTAS abrem o painel");
                exigir(cena.getConquistas().getQuantidade() == (fase == 0 ? 0 : fase == 1 ? 3 : 6), "quantidade exibida reflete a partida");
                for (Dimension resolucao : new Dimension[]{new Dimension(1280, 720), new Dimension(1366, 768), new Dimension(1920, 1080)}) {
                    partida.setSize(resolucao); partida.doLayout();
                    BufferedImage imagem = new BufferedImage(resolucao.width, resolucao.height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = imagem.createGraphics(); partida.paint(g); g.dispose();
                    String estado = fase == 0 ? "bloqueadas" : fase == 1 ? "parciais" : "completas";
                    ImageIO.write(imagem, "png", saida.resolve("conquistas-" + estado + "-" + resolucao.width + "x" + resolucao.height + ".png").toFile());
                    Rectangle alvo = LayoutConquistas.no(4);
                    clicar(cena, new Point2D.Double(alvo.getCenterX(), alvo.getCenterY()));
                    exigir(cena.getConquistaEmDestaque() == Conquista.LAMINA_DESBRAVADORA, "clique deve selecionar nó no viewport escalonado");
                    Rectangle no = LayoutConquistas.no(0);
                    Point2D centro = new Point2D.Double(no.getCenterX(), no.getCenterY());
                    mover(cena, centro);
                    exigir(cena.getConquistaEmDestaque() == Conquista.ECO_PASSADO, "mouse mostra informações da conquista bloqueada");
                }
                escutadorTeclado tecla = new escutadorTeclado();
                tecla.movePraDir = true; cena.atualizarMenuPausa(tecla);
                tecla.movePraDir = false; cena.atualizarMenuPausa(tecla);
                exigir(cena.getConquistaEmDestaque() == Conquista.MEMORIA_FRAGMENTADA, "D seleciona o próximo marco");
                clicar(cena, new Point2D.Double(LayoutConquistas.voltar().getCenterX(), LayoutConquistas.voltar().getCenterY()));
                exigir(!cena.isTelaConquistasAberta() && cena.isMenuPausaAberto(), "voltar retorna à pausa sem retomar o combate");
            }
            Set<Integer> alcancaveis = new java.util.HashSet<>(); alcancaveis.add(0);
            for (int rodada = 0; rodada < 6; rodada++) for (int i : Set.copyOf(alcancaveis)) {
                for (Point d : new Point[]{new Point(-1, 0), new Point(1, 0), new Point(0, -1), new Point(0, 1)}) alcancaveis.add(LayoutConquistas.navegar(i, d.x, d.y));
            }
            exigir(alcancaveis.size() == 6, "todas as conquistas acessíveis pelo teclado");
            for (int i = 0; i < 6; i++) exigir(new Rectangle(36, 58, 696, 310).contains(LayoutConquistas.no(i)), "nós cabem no painel");
        } catch (Exception e) { throw new IllegalStateException(e); }
        finally { cena.pausarJogo(); }
    }

    private static void validarIconesEAvisos() {
        for (Conquista conquista : Conquista.values()) {
            BufferedImage preto = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = preto.createGraphics(); IconesConquistas.desenhar(g, conquista, new Rectangle(0, 0, 48, 48), false); g.dispose();
            int pixels = 0;
            for (int y = 0; y < 48; y++) for (int x = 0; x < 48; x++) {
                int pixel = preto.getRGB(x, y);
                if ((pixel >>> 24) != 0) { pixels++; exigir((pixel & 0xFFFFFF) == 0, "ícone bloqueado deve ficar preto"); }
            }
            exigir(pixels > 0, "silhueta não pode ficar vazia");
            BufferedImage colorido = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            g = colorido.createGraphics(); IconesConquistas.desenhar(g, conquista, new Rectangle(0, 0, 48, 48), true); g.dispose();
            int cores = 0;
            for (int y = 0; y < 48; y++) for (int x = 0; x < 48; x++) if ((colorido.getRGB(x, y) & 0xFFFFFF) != 0) cores++;
            exigir(cores > 0, "ícone desbloqueado deve recuperar as cores");
        }
        long[] agora = {0};
        SistemaConquistas sistema = new SistemaConquistas(); MemoryManager memorias = new MemoryManager();
        memorias.coletar("memoria_01"); sistema.atualizar(new Inventario(), memorias, true);
        NotificacoesConquistas aviso = new NotificacoesConquistas(() -> agora[0]);
        aviso.atualizar(sistema, true); exigir(aviso.getAtual() == Conquista.ECO_PASSADO, "desbloqueio gera aviso");
        agora[0] += 1000; aviso.atualizar(sistema, false);
        exigir(aviso.getRestanteMs() == 3500, "pausa e diálogo não consomem aviso de conquista");
        for (int i = 0; i < 14; i++) { agora[0] += 250; aviso.atualizar(sistema, true); }
        exigir(!aviso.visivel(), "aviso expira sem repetir a conquista");
    }

    private static Point ponto(panel cena, Point2D logico) {
        Rectangle area = new ViewportJogo(768, 480).getArea(cena.getWidth(), cena.getHeight());
        return new Point(area.x + (int) Math.round(logico.getX() * area.width / 768), area.y + (int) Math.round(logico.getY() * area.height / 480));
    }
    private static void clicar(panel cena, Point2D logico) {
        Point p = ponto(cena, logico);
        cena.dispatchEvent(new MouseEvent(cena, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, p.x, p.y, 1, false, MouseEvent.BUTTON1));
    }
    private static void mover(panel cena, Point2D logico) {
        Point p = ponto(cena, logico);
        cena.dispatchEvent(new MouseEvent(cena, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, p.x, p.y, 0, false));
    }
    private static void alternarPausa(panel cena) {
        escutadorTeclado tecla = new escutadorTeclado(); tecla.abrirMenu = true; cena.atualizarMenuPausa(tecla);
        tecla.abrirMenu = false; cena.atualizarMenuPausa(tecla);
    }
    private static void fecharPausa(panel cena) { while (cena.isMenuPausaAberto()) alternarPausa(cena); }
    private static void exigir(boolean condicao, String texto) { ValidacaoConquistas.exigir(condicao, texto); }
}
