package DEAD.LAND.core;

import DEAD.LAND.entity.item;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import javax.imageio.ImageIO;

/** Renderiza os componentes reais em três resoluções, sem tocar nos saves da partida. */
final class ValidacaoVisualHUD {
    static void validar() {
        try {
            File pasta = Files.createTempDirectory("dead-land-hud-").toFile();
            File saida = new File("build/hud");
            Files.createDirectories(saida.toPath());
            Inventario inventario = new Inventario();
            panel cena = new panel("centro", inventario, null, null, new SaveManager(pasta), 2);
            panel rodape = new panel("sul", inventario);
            cena.setPainelInventario(rodape);
            PainelPartida partida = new PainelPartida(cena, rodape);
            try {
                cena.pausarJogo();
                GameState state = cena.criarGameState(2);
                state.cenarioIndex = 5;
                state.estadoHistoria = "FLORESTA";
                state.vida = 75;
                state.itens = java.util.Set.of("arco", "espada", "chave", "chave_boss_63", "pocao_cura");
                state.fragmentosCura = 3;
                state.flechas = 12;
                state.armaEquipada = "arco";
                state.objetivoHistoria = "Explore a floresta, encontre a chave prata e descubra o caminho que leva à porta inferior sem perder suas lembranças.";
                state.objetivoSecundario = "Encontre os fragmentos de memória e retorne ao Caçador para receber sua recompensa.";
                cena.aplicarGameState(state);
                capturarAviso(cena, saida);
                for (String estado : new String[]{"exploracao", "vila", "vila-guilda", "vila-saida", "vila-barricada", "castelo2", "chefe", "pausa", "objetivos", "dialogo", "dialogo-npc", "inventario", "interacao", "morte"}) {
                    cena.aplicarGameState(state);
                    while (cena.isMenuPausaAberto()) alternarPausa(cena);
                    switch (estado) {
                        case "vila" -> cena.irParaCenario(3, 900, 690);
                        case "vila-guilda" -> cena.irParaCenario(3, 1900, 420);
                        case "vila-saida" -> cena.irParaCenario(3, 2336, DEAD.LAND.world.MapaVila.SAIDA_CASTELO_Y);
                        case "vila-barricada" -> cena.irParaCenario(3, 1750, 1056);
                        case "castelo2" -> cena.irParaCenario(6, 745, 335);
                        case "chefe" -> {
                            cena.irParaCenario(7, 745, 335);
                        }
                        case "pausa" -> alternarPausa(cena);
                        case "objetivos" -> { alternarPausa(cena); clicar(cena, 384, 378); }
                        case "dialogo" -> cena.getHistoria().eventoDormir();
                        case "dialogo-npc" -> cena.getHistoria().eventoFalarComNpc("mara", cena);
                        case "inventario" -> {
                            for (int i = 0; i < 30; i++) inventario.adicionar(new item(item.SPRITE_POCAO_CURA, 0, 0, 0, -1, "teste_" + i));
                        }
                        case "interacao" -> {
                            cena.irParaCenario(0, 18 * 48, 168);
                        }
                        case "morte" -> {
                            cena.getJogador().levarDano(9999);
                            for (int i = 0; i < 90; i++) cena.getSistemaMorte().atualizar(cena, new escutadorTeclado(), cena.getSistemaCheckpoint());
                        }
                    }
                    LayoutHUD.Estado esperado = switch (estado) {
                        case "chefe" -> LayoutHUD.Estado.CHEFE;
                        case "pausa", "objetivos" -> LayoutHUD.Estado.PAUSA;
                        case "dialogo", "dialogo-npc" -> LayoutHUD.Estado.DIALOGO;
                        case "morte" -> LayoutHUD.Estado.MORTE;
                        default -> LayoutHUD.Estado.EXPLORACAO;
                    };
                    ValidacaoHUD.exigir(cena.getEstadoHUD() == esperado, "captura no estado correto: " + estado);
                    for (Dimension resolucao : new Dimension[]{new Dimension(1280, 720), new Dimension(1366, 768), new Dimension(1920, 1080)}) {
                        partida.setSize(resolucao);
                        partida.doLayout();
                        ValidacaoHUD.exigir(!cena.getBounds().intersects(rodape.getBounds()), "rodapé separado do mundo");
                        ValidacaoHUD.exigir(new Rectangle(0, 0, resolucao.width, resolucao.height).contains(rodape.getBounds()), "HUD cabe na janela");
                        BufferedImage imagem = new BufferedImage(resolucao.width, resolucao.height, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = imagem.createGraphics(); partida.paint(g); g.dispose();
                        if (estado.startsWith("dialogo")) {
                            Rectangle area = LayoutHUD.partida(resolucao.width, resolucao.height);
                            int x = area.x + (int) Math.round(20 * area.width / 768.0);
                            int y = area.y + (int) Math.round(520 * area.height / 584.0);
                            ValidacaoHUD.exigir(imagem.getRGB(x, y) == new Color(21, 24, 32).getRGB(),
                                    "dialogo deve cobrir barras e itens do rodape");
                        }
                        ImageIO.write(imagem, "png", new File(saida, estado + "-" + resolucao.width + "x" + resolucao.height + ".png"));
                        if (estado.equals("chefe")) validarBarraChefe(cena);
                    }
                    if (estado.equals("inventario")) {
                        int pagina = inventario.getPaginaRapida();
                        rodape.dispatchEvent(new MouseWheelEvent(rodape, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                                0, rodape.getWidth() / 2, rodape.getHeight() / 2, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1));
                        ValidacaoHUD.exigir(inventario.getPaginaRapida() != pagina, "mouse acessa páginas do HUD escalonado");
                    }
                }
            } finally {
                cena.pausarJogo();
                Files.delete(pasta.toPath());
            }
        } catch (Exception e) { throw new IllegalStateException("Falha nas capturas do HUD", e); }
    }

    private static void validarBarraChefe(panel cena) {
        BufferedImage real = new BufferedImage(768, 480, BufferedImage.TYPE_INT_RGB);
        BufferedImage esperado = new BufferedImage(768, 480, BufferedImage.TYPE_INT_RGB);
        cena.setSize(768, 480);
        Graphics2D g = real.createGraphics(); cena.paint(g); g.dispose();
        // Usa o mesmo fundo da cena para comparar exatamente a composição final da barra.
        g = esperado.createGraphics(); g.drawImage(real, 0, 0, null);
        cena.getControladorInimigos().getChefe().desenharBarraBoss(g, 768); g.dispose();
        Rectangle area = LayoutHUD.chefe(768);
        for (int y = area.y; y < area.y + area.height; y++) {
            for (int x = area.x; x < area.x + area.width; x++) {
                ValidacaoHUD.exigir(real.getRGB(x, y) == esperado.getRGB(x, y), "barra do chefe não pode ter elementos sobrepostos");
            }
        }
    }

    private static void capturarAviso(panel cena, File saida) throws Exception {
        long[] agora = {0};
        var historia = new DEAD.LAND.story.SistemaHistoria();
        var aviso = new NotificacoesObjetivos(() -> agora[0]);
        aviso.atualizar(historia, true);
        agora[0] = 200; aviso.atualizar(historia, true);
        BufferedImage imagem = new BufferedImage(768, 480, BufferedImage.TYPE_INT_RGB);
        cena.setSize(768, 480);
        Graphics2D g = imagem.createGraphics(); cena.paint(g);
        aviso.desenhar(g, 768, 480); g.dispose();
        ImageIO.write(imagem, "png", new File(saida, "aviso-missao-768x480.png"));
    }

    private static void alternarPausa(panel cena) {
        escutadorTeclado tecla = new escutadorTeclado();
        tecla.abrirMenu = true; cena.atualizarMenuPausa(tecla);
        tecla.abrirMenu = false; cena.atualizarMenuPausa(tecla);
    }

    private static void clicar(panel cena, int x, int y) {
        // O menu usa o viewport real, inclusive quando há margens.
        Rectangle area = new ViewportJogo(768, 480).getArea(cena.getWidth(), cena.getHeight());
        int sx = area.x + (int) Math.round(x * area.width / 768.0);
        int sy = area.y + (int) Math.round(y * area.height / 480.0);
        cena.dispatchEvent(new MouseEvent(cena, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                0, sx, sy, 1, false, MouseEvent.BUTTON1));
    }
}
