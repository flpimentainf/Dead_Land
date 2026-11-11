package DEAD.LAND.ui;

import DEAD.LAND.core.ResourceManager;
import DEAD.LAND.core.SistemaConquistas.Conquista;
import DEAD.LAND.entity.item;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

public final class IconesConquistas {
    private static final EnumMap<Conquista, BufferedImage> COLORIDOS = new EnumMap<>(Conquista.class);
    private static final EnumMap<Conquista, BufferedImage> BLOQUEADOS = new EnumMap<>(Conquista.class);
    private IconesConquistas() {}

    public static void desenhar(Graphics2D destino, Conquista conquista, Rectangle area, boolean desbloqueada) {
        var cache = desbloqueada ? COLORIDOS : BLOQUEADOS;
        BufferedImage imagem = cache.computeIfAbsent(conquista, c -> criar(c, desbloqueada));
        destino.drawImage(imagem, area.x, area.y, area.width, area.height, null);
    }

    private static BufferedImage criar(Conquista conquista, boolean colorida) {
        BufferedImage imagem = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            String caminho = conquista.deMemoria() ? item.SPRITE_MEMORIA : switch (conquista) {
                case MIRA_CERTEIRA -> item.SPRITE_ARCO;
                case LAMINA_DESBRAVADORA -> item.SPRITE_ESPADA;
                default -> item.SPRITE_POCAO_CURA;
            };
            Image sprite = ResourceManager.getInstancia().carregarImagem(caminho);
            switch (conquista) {
                case MEMORIA_FRAGMENTADA -> {
                    g.drawImage(sprite, 14, 1, 22, 22, null);
                    g.drawImage(sprite, 2, 24, 22, 22, null);
                    g.drawImage(sprite, 25, 24, 22, 22, null);
                }
                case MESTRE_LEMBRANCAS -> {
                    for (Point p : new Point[]{new Point(15, 0), new Point(0, 14), new Point(29, 14), new Point(5, 30), new Point(24, 30)}) {
                        g.drawImage(sprite, p.x, p.y, 18, 18, null);
                    }
                }
                default -> g.drawImage(sprite, 4, 4, 40, 40, null);
            }
            if (!colorida) {
                g.setComposite(AlphaComposite.SrcIn);
                g.setColor(Color.BLACK); g.fillRect(0, 0, 48, 48);
            }
        } finally { g.dispose(); }
        return imagem;
    }
}
