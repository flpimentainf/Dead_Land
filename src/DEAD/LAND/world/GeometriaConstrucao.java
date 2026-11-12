package DEAD.LAND.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Volumes compartilhados pelo desenho, pelas portas e pela colisão das fachadas. */
public final class GeometriaConstrucao {
    public enum Material { OCRE, BARRO, ARDOSIA, MUSGO }
    public record Volume(int x, int y, int largura, int altura, int telhado,
            boolean empena, Material material, int estilo, boolean vitrine) {
        public Rectangle parede() {
            int margem = empena ? 6 : 3;
            int topo = y + telhado - (empena ? 12 : 3);
            return new Rectangle(x + margem, topo, largura - margem * 2, y + altura - 6 - topo);
        }
        public Rectangle porta() {
            if (!empena) return null;
            int alturaParede = altura - 12 - telhado;
            int h = estilo == 4 ? 111 : Math.min(87, alturaParede - 9);
            int w = estilo == 4 ? 75 : Math.min(54, largura / 5);
            int esquerda = x + largura / 2 - w / 2 - (estilo == 5 ? 24 : 0);
            return new Rectangle(esquerda, y + altura - 12 - h, w, h);
        }
    }

    private static final Map<MapaVila.Construcao, GeometriaConstrucao> CACHE = new ConcurrentHashMap<>();
    private final List<Volume> volumes;
    private final List<Rectangle> portas;
    private final Area paredes = new Area();
    private final int baseY;

    private GeometriaConstrucao(MapaVila.Construcao casa) {
        int w = casa.largura() * 48, h = casa.altura() * 48;
        volumes = switch (casa.estilo()) {
            case 0 -> List.of(anexo(176, 90, w - 176, h - 90, Material.OCRE, false),
                    empena(0, 0, 210, h, 138, Material.OCRE, 0));
            case 1 -> List.of(anexo(0, 90, 161, h - 90, Material.BARRO, true),
                    anexo(w - 160, 72, 160, h - 72, Material.OCRE, true),
                    empena(126, 0, 228, h, 164, Material.OCRE, 1));
            case 2 -> List.of(empena(0, 78, 194, h - 78, 145, Material.ARDOSIA, 2),
                    empena(w - 194, 78, 194, h - 78, 145, Material.ARDOSIA, 3),
                    empena(162, 0, 252, h, 219, Material.OCRE, 4));
            case 3 -> List.of(anexo(244, 108, w - 244, h - 108, Material.BARRO, false),
                    empena(0, 0, 267, h, 189, Material.BARRO, 5));
            case 4 -> List.of(anexo(0, 39, 135, h - 39, Material.MUSGO, false),
                    anexo(w - 132, 48, 132, h - 48, Material.MUSGO, false),
                    empena(111, 0, 213, h, 108, Material.OCRE, 6));
            case 5 -> List.of(anexo(0, 87, 156, h - 87, Material.OCRE, false),
                    empena(123, 0, w - 123, h, 166, Material.BARRO, 7));
            default -> List.of(empena(3, 0, w - 6, h, 108, Material.MUSGO, 8));
        };
        Rectangle origem = casa.area();
        for (Volume volume : volumes) {
            Rectangle parede = volume.parede(); parede.translate(origem.x, origem.y);
            paredes.add(new Area(parede));
        }
        portas = volumes.stream().filter(Volume::empena).map(volume -> {
            Rectangle porta = volume.porta(); porta.translate(origem.x, origem.y); return porta;
        }).toList();
        baseY = origem.y + h - 6;
    }

    private static Volume empena(int x, int y, int w, int h, int teto, Material material, int estilo) {
        return new Volume(x, y, w, h, teto, true, material, estilo, false);
    }
    private static Volume anexo(int x, int y, int w, int h, Material material, boolean vitrine) {
        return new Volume(x, y, w, h, Math.min(90, h * 45 / 100), false, material, 1, vitrine);
    }
    public static GeometriaConstrucao de(MapaVila.Construcao casa) { return CACHE.computeIfAbsent(casa, GeometriaConstrucao::new); }
    public List<Volume> volumes() { return volumes; }
    public List<Rectangle> portas() { return portas.stream().map(Rectangle::new).toList(); }
    public Rectangle portaPrincipal() { return new Rectangle(portas.getLast()); }
    public boolean colide(Rectangle area) { return paredes.intersects(area); }
    public int baseY() { return baseY; }
    public List<Point> acessos() {
        return portas.stream().map(porta -> new Point(porta.x + porta.width / 2, porta.y + porta.height + 15)).toList();
    }
}
