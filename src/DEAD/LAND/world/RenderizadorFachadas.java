package DEAD.LAND.world;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/** Arquitetura original em uma grade de três pixels, sem texturas fotográficas. */
public final class RenderizadorFachadas {
    private static final Color CONTORNO = new Color(43, 35, 30);
    private static final Color MADEIRA = new Color(91, 60, 39);
    private static final Color LUZ_MADEIRA = new Color(155, 105, 57);
    private static final Color REBOCO = new Color(192, 184, 148);
    private static final Color PEDRA = new Color(139, 145, 132);
    private static final Color VIDRO = new Color(40, 66, 72);
    private record Paleta(Color parede, Color teto, Color luz, Color sombra) {}
    private static final Paleta OCRE = new Paleta(REBOCO, new Color(155, 91, 49), new Color(191, 125, 64), new Color(113, 64, 39));
    private static final Paleta BARRO = new Paleta(new Color(181, 167, 129), new Color(128, 64, 44), new Color(169, 91, 55), new Color(91, 47, 37));
    private static final Paleta ARDOSIA = new Paleta(PEDRA, new Color(62, 92, 97), new Color(89, 124, 121), new Color(42, 65, 72));
    private static final Paleta MUSGO = new Paleta(new Color(174, 177, 149), new Color(90, 110, 64), new Color(128, 142, 76), new Color(60, 81, 49));

    public BufferedImage criar(MapaVila.Construcao casa) {
        int w = casa.largura() * 48, h = casa.altura() * 48;
        BufferedImage imagem = new BufferedImage((w + 24) / 3, (h + 24) / 3, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g.scale(1.0 / 3, 1.0 / 3); g.translate(12, 0);
            g.setColor(new Color(14, 24, 23, 95)); g.fillRect(12, h - 9, w - 12, 24);
            for (var volume : GeometriaConstrucao.de(casa).volumes()) {
                Paleta paleta = switch (volume.material()) {
                    case OCRE -> OCRE; case BARRO -> BARRO; case ARDOSIA -> ARDOSIA; case MUSGO -> MUSGO;
                };
                if (volume.empena()) empena(g, volume, paleta);
                else anexo(g, volume.x(), volume.y(), volume.largura(), volume.altura(), paleta, volume.vitrine());
            }
            switch (casa.estilo()) {
                case 0 -> janela(g, 217, h - 79, 36, 45, false);
                case 1 -> {
                    toldo(g, 15, h - 119, 123); toldo(g, w - 143, h - 125, 126);
                    letreiro(g, w / 2, h - 99, "ARMAZÉM");
                }
                case 2 -> {
                    bandeira(g, 129, h - 151); bandeira(g, w - 156, h - 151);
                    letreiro(g, w / 2, h - 130, "GUILDA DOS ERRANTES"); escudo(g, w / 2, 183);
                }
                case 3 -> janela(g, w - 128, h - 93, 57, 57, true);
                case 4 -> { janela(g, 30, h - 66, 39, 33, false); janela(g, w - 84, h - 66, 39, 33, true); }
                case 5 -> janela(g, 36, h - 91, 51, 54, true);
                default -> { }
            }
            floreira(g, 15, h - 12, casa.estilo());
            floreira(g, w - 42, h - 12, casa.estilo() + 1);
        } finally { g.dispose(); }
        return imagem;
    }

    private void empena(Graphics2D g, GeometriaConstrucao.Volume volume, Paleta p) {
        int x = volume.x(), y = volume.y(), w = volume.largura(), h = volume.altura();
        int telhado = volume.telhado(), estilo = volume.estilo();
        int base = y + h - 12, paredeY = y + telhado;
        parede(g, x + 9, paredeY - 9, w - 18, base - paredeY + 9, p, estilo);
        int cx = x + w / 2;
        Polygon esquerda = poligono(cx, y + 3, cx, y + telhado * 55 / 100,
                x, y + telhado, x, y + telhado * 45 / 100);
        Polygon direita = poligono(cx, y + 3, x + w, y + telhado * 45 / 100,
                x + w, y + telhado, cx, y + telhado * 55 / 100);
        planoTelhado(g, esquerda, p.luz(), p.teto(), x, cx, y, telhado, true);
        planoTelhado(g, direita, p.teto(), p.sombra(), cx, x + w, y, telhado, false);
        int topoFrente = y + telhado * 55 / 100;
        Polygon frente = poligono(cx, topoFrente, x + w - 8, paredeY - 1, x + 8, paredeY - 1);
        g.setColor(p.parede()); g.fillPolygon(frente);
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g.setColor(CONTORNO); g.drawPolygon(frente);
        g.setColor(MADEIRA); g.drawLine(cx, topoFrente + 9, cx, paredeY);
        g.drawLine(x + 18, paredeY - 8, cx, topoFrente + 12);
        g.drawLine(cx, topoFrente + 12, x + w - 18, paredeY - 8);
        g.drawLine(x + 9, paredeY, x + w - 9, paredeY);
        g.setColor(LUZ_MADEIRA); g.setStroke(new BasicStroke(3));
        g.drawLine(x + 18, paredeY - 12, cx, topoFrente + 9);
        int raio = Math.min(20, Math.max(11, telhado / 10));
        if (estilo != 4) oculo(g, cx, y + telhado * 83 / 100, raio);
        int alturaParede = base - paredeY;
        Rectangle entrada = volume.porta();
        int portaX = entrada.x, portaH = entrada.height;
        porta(g, entrada.x, entrada.y, entrada.width, entrada.height, estilo == 4);
        int janelaH = Math.min(57, alturaParede - 33);
        if (janelaH >= 27) {
            if (estilo != 0 && estilo != 8) janela(g, x + 28, base - janelaH - 21, 36, janelaH, estilo % 2 == 0);
            janela(g, x + w - 65, base - janelaH - 21, 36, janelaH, estilo % 2 != 0);
        }
        if (estilo == 4 || estilo == 0 || estilo == 5) lanterna(g, portaX - 18, base - portaH + 12);
        if (estilo != 6 && estilo != 8) chamine(g, x + w * 3 / 4, y + telhado / 5, p);
    }

    private void anexo(Graphics2D g, int x, int y, int w, int h, Paleta p, boolean vitrine) {
        int teto = Math.min(90, h * 45 / 100);
        parede(g, x + 6, y + teto, w - 12, h - teto - 12, p, 1);
        Polygon forma = poligono(x + 12, y + 3, x + w - 15, y + 9,
                x + w, y + teto + 6, x, y + teto);
        g.setColor(p.teto()); g.fillPolygon(forma);
        Shape clip = g.getClip(); g.clip(forma);
        for (int yy = y + 12; yy < y + teto; yy += 18) {
            g.setColor(p.sombra()); g.fillRect(x, yy, w, 3);
            g.setColor(p.luz()); g.fillRect(x + 3, yy - 3, w - 6, 3);
            for (int xx = x + (yy / 18 % 2) * 15; xx < x + w; xx += 30) {
                g.setColor(p.sombra()); g.fillRect(xx, yy - 15, 3, 12);
            }
        }
        g.setClip(clip); g.setColor(CONTORNO); g.setStroke(new BasicStroke(6)); g.drawPolygon(forma);
        if (vitrine) janela(g, x + 24, y + h - 81, Math.max(45, w - 48), 48, false);
    }

    private void parede(Graphics2D g, int x, int y, int w, int h, Paleta p, int estilo) {
        g.setColor(CONTORNO); g.fillRect(x - 3, y - 3, w + 6, h + 9);
        g.setColor(p.parede()); g.fillRect(x, y, w, h);
        g.setColor(p.parede().darker()); g.fillRect(x + w - 9, y, 9, h);
        g.setColor(MADEIRA); g.fillRect(x + 3, y, 6, h); g.fillRect(x + w - 15, y, 6, h);
        g.fillRect(x, y + 6, w, 6);
        if (estilo == 8) {
            for (int yy = y + 18; yy < y + h; yy += 18) {
                g.setColor(MADEIRA); g.fillRect(x, yy, w, 12);
                g.setColor(LUZ_MADEIRA); g.fillRect(x + 9, yy, w - 18, 3);
            }
        } else if (h > 120) {
            g.setColor(MADEIRA); g.fillRect(x, y + 30, w, 6);
            g.setStroke(new BasicStroke(6));
            g.drawLine(x + 12, y + 12, x + 42, y + 30);
            g.drawLine(x + w - 12, y + 12, x + w - 42, y + 30);
        }
        g.setColor(new Color(91, 101, 91)); g.fillRect(x - 3, y + h - 15, w + 6, 21);
        g.setColor(PEDRA); g.fillRect(x, y + h - 15, w, 6);
        for (int xx = x + 12; xx < x + w; xx += 33) {
            g.setColor(new Color(62, 74, 67)); g.fillRect(xx, y + h - 6, 3, 9);
        }
    }

    private void planoTelhado(Graphics2D g, Polygon forma, Color cor, Color sombra,
            int esquerda, int direita, int y, int h, boolean ladoEsquerdo) {
        g.setColor(cor); g.fillPolygon(forma);
        Shape clip = g.getClip(); g.clip(forma);
        int inicio = y + h * 45 / 100;
        for (int d = 6; d < h * 55 / 100; d += 15) {
            g.setColor(sombra); g.setStroke(new BasicStroke(3));
            if (ladoEsquerdo) g.drawLine(esquerda, inicio + d, direita, y + d);
            else g.drawLine(esquerda, y + d, direita, inicio + d);
        }
        // Poucos grupos de telhas, em vez de uma grade fina em toda a superfície.
        for (int x = esquerda + 12; x < direita; x += 27) {
            int inclinacao = ladoEsquerdo ? (direita - x) * (h * 45 / 100) / Math.max(1, direita - esquerda)
                    : (x - esquerda) * (h * 45 / 100) / Math.max(1, direita - esquerda);
            for (int d = (x / 27 % 2) * 15 + 9; d < h / 2; d += 30) {
                g.setColor(sombra); g.fillRect(x, y + inclinacao + d, 3, 9);
                g.setColor(cor.brighter()); g.fillRect(x + 3, y + inclinacao + d, 6, 3);
            }
        }
        g.setClip(clip); g.setColor(CONTORNO); g.setStroke(new BasicStroke(6)); g.drawPolygon(forma);
    }

    private static Polygon poligono(int... pontos) {
        Polygon p = new Polygon();
        for (int i = 0; i < pontos.length; i += 2) p.addPoint(pontos[i], pontos[i + 1]);
        return p;
    }

    private Shape arco(int x, int y, int w, int h) {
        Area forma = new Area(new Ellipse2D.Double(x, y, w, w));
        forma.add(new Area(new Rectangle(x, y + w / 2, w, h - w / 2)));
        return forma;
    }

    private void janela(Graphics2D g, int x, int y, int w, int h, boolean arqueada) {
        Shape forma = arqueada ? arco(x, y, w, h) : new Rectangle(x, y, w, h);
        g.setColor(CONTORNO); g.setStroke(new BasicStroke(12)); g.draw(forma);
        g.setColor(LUZ_MADEIRA); g.setStroke(new BasicStroke(6)); g.draw(forma);
        g.setColor(VIDRO); g.fill(forma);
        Shape clip = g.getClip(); g.clip(forma);
        g.setColor(new Color(73, 104, 104)); g.fillRect(x + 6, y + 6, w / 3, h - 12);
        g.setColor(new Color(184, 163, 103)); g.fillRect(x + w / 2 + 3, y + 9, w / 2 - 9, h / 3);
        g.setColor(MADEIRA); g.fillRect(x + w / 2 - 3, y, 6, h);
        g.fillRect(x, y + h / 2, w, 6); g.setClip(clip);
        g.setColor(LUZ_MADEIRA); g.fillRect(x - 9, y + h, w + 18, 6);
    }

    private void oculo(Graphics2D g, int x, int y, int r) {
        g.setColor(CONTORNO); g.fillOval(x - r - 6, y - r - 6, r * 2 + 12, r * 2 + 12);
        g.setColor(LUZ_MADEIRA); g.fillOval(x - r - 3, y - r - 3, r * 2 + 6, r * 2 + 6);
        g.setColor(VIDRO); g.fillOval(x - r, y - r, r * 2, r * 2);
        g.setColor(new Color(89, 116, 114)); g.fillRect(x - r + 6, y - r + 6, Math.max(3, r / 2), r);
        g.setColor(MADEIRA); g.fillRect(x - 2, y - r, 4, r * 2); g.fillRect(x - r, y - 2, r * 2, 4);
    }

    private void porta(Graphics2D g, int x, int y, int w, int h, boolean dupla) {
        Shape forma = arco(x, y, w, h);
        g.setColor(CONTORNO); g.setStroke(new BasicStroke(15)); g.draw(forma);
        g.setColor(new Color(151, 123, 82)); g.setStroke(new BasicStroke(9)); g.draw(forma);
        g.setColor(MADEIRA); g.fill(forma);
        Shape clip = g.getClip(); g.clip(forma);
        g.setColor(new Color(118, 77, 41));
        for (int xx = x + 6; xx < x + w; xx += 12) g.fillRect(xx, y + 3, 6, h);
        g.setColor(CONTORNO); g.fillRect(x, y + w / 2 + 3, w, 6);
        g.fillRect(x, y + h - 21, w, 6);
        if (dupla) g.fillRect(x + w / 2 - 3, y, 6, h);
        g.setClip(clip);
        int puxador = dupla ? x + w / 2 : x + w - 15;
        g.setColor(new Color(213, 170, 85)); g.setStroke(new BasicStroke(3));
        g.drawOval(puxador - 3, y + h * 2 / 3, 9, 12);
        g.setColor(new Color(159, 157, 133)); g.fillRect(x - 12, y + h + 3, w + 24, 6);
        g.setColor(new Color(87, 101, 92)); g.fillRect(x - 15, y + h + 9, w + 30, 6);
    }

    private void chamine(Graphics2D g, int x, int y, Paleta p) {
        g.setColor(CONTORNO); g.fillRect(x - 3, y - 18, 33, 63);
        g.setColor(new Color(119, 94, 76)); g.fillRect(x, y - 12, 24, 54);
        g.setColor(new Color(157, 126, 95)); g.fillRect(x, y - 12, 9, 51);
        g.setColor(new Color(75, 66, 59));
        g.fillRect(x, y + 9, 24, 3); g.fillRect(x + 9, y - 9, 3, 18);
        g.fillRect(x, y + 30, 24, 3); g.fillRect(x + 15, y + 12, 3, 18);
        g.setColor(CONTORNO); g.fillRect(x - 6, y - 21, 39, 12);
        g.setColor(new Color(152, 150, 127)); g.fillRect(x - 3, y - 21, 33, 3);
    }

    private void toldo(Graphics2D g, int x, int y, int w) {
        for (int xx = x; xx < x + w; xx += 21) {
            g.setColor((xx - x) / 21 % 2 == 0 ? new Color(137, 65, 49) : new Color(208, 185, 133));
            g.fillRect(xx, y, Math.min(21, x + w - xx), 24);
            g.fillRect(xx + 3, y + 24, Math.min(15, x + w - xx - 3), 6);
        }
        g.setColor(CONTORNO); g.setStroke(new BasicStroke(3)); g.drawLine(x, y, x + w, y);
    }

    private void bandeira(Graphics2D g, int x, int y) {
        g.setColor(CONTORNO); g.fillRect(x - 6, y - 6, 39, 6);
        g.setColor(new Color(46, 87, 89)); g.fillRect(x, y, 27, 69);
        g.fillPolygon(poligono(x, y + 69, x + 13, y + 81, x + 27, y + 69));
        g.setColor(new Color(197, 159, 75)); g.fillRect(x + 12, y + 15, 3, 36); g.fillRect(x + 6, y + 30, 15, 3);
    }

    private void escudo(Graphics2D g, int x, int y) {
        Polygon escudo = poligono(x - 24, y - 24, x + 24, y - 24, x + 21, y + 12, x, y + 30, x - 21, y + 12);
        g.setColor(CONTORNO); g.setStroke(new BasicStroke(9)); g.drawPolygon(escudo);
        g.setColor(new Color(47, 83, 83)); g.fillPolygon(escudo);
        g.setColor(new Color(215, 177, 88)); g.setStroke(new BasicStroke(3));
        g.drawLine(x - 12, y - 12, x + 12, y + 15); g.drawLine(x + 12, y - 12, x - 12, y + 15);
    }

    private void floreira(Graphics2D g, int x, int y, int estilo) {
        g.setColor(CONTORNO); g.fillRect(x, y - 9, 27, 21);
        g.setColor(new Color(137, 87, 48)); g.fillRect(x + 3, y - 3, 21, 12);
        g.setColor(new Color(49, 81, 42)); g.fillRect(x - 3, y - 15, 30, 9);
        g.setColor(estilo % 2 == 0 ? new Color(195, 145, 108) : new Color(187, 111, 93));
        g.fillRect(x + 3, y - 18, 6, 6); g.fillRect(x + 18, y - 12, 6, 6);
    }

    private void lanterna(Graphics2D g, int x, int y) {
        g.setColor(CONTORNO); g.fillRect(x, y - 6, 12, 27);
        g.fillRect(x + 3, y - 12, 6, 9);
        g.setColor(new Color(219, 180, 89)); g.fillRect(x + 3, y, 6, 12);
    }

    private void letreiro(Graphics2D g, int cx, int y, String texto) {
        int larguraTexto = texto.length() * 18 - 3, w = larguraTexto + 24;
        g.setColor(CONTORNO); g.fillRect(cx - w / 2 - 3, y - 30, w + 6, 39);
        g.setColor(MADEIRA); g.fillRect(cx - w / 2, y - 27, w, 33);
        g.setColor(new Color(224, 203, 148));
        for (int i = 0; i < texto.length(); i++) {
            char letra = texto.charAt(i);
            int x = cx - larguraTexto / 2 + i * 18;
            int[] linhas = glifo(letra);
            for (int l = 0; l < linhas.length; l++) for (int c = 0; c < 5; c++) {
                if ((linhas[l] & (1 << (4 - c))) != 0) g.fillRect(x + c * 3, y - 21 + l * 3, 3, 3);
            }
            if (letra == 'É') { g.fillRect(x + 9, y - 27, 3, 3); g.fillRect(x + 6, y - 24, 3, 3); }
        }
    }

    private int[] glifo(char letra) {
        return switch (letra) {
            case 'A' -> new int[]{14, 17, 17, 31, 17, 17, 17};
            case 'D' -> new int[]{30, 17, 17, 17, 17, 17, 30};
            case 'E', 'É' -> new int[]{31, 16, 16, 30, 16, 16, 31};
            case 'G' -> new int[]{14, 17, 16, 23, 17, 17, 14};
            case 'I' -> new int[]{14, 4, 4, 4, 4, 4, 14};
            case 'L' -> new int[]{16, 16, 16, 16, 16, 16, 31};
            case 'M' -> new int[]{17, 27, 21, 21, 17, 17, 17};
            case 'N' -> new int[]{17, 25, 25, 21, 19, 19, 17};
            case 'O' -> new int[]{14, 17, 17, 17, 17, 17, 14};
            case 'R' -> new int[]{30, 17, 17, 30, 20, 18, 17};
            case 'S' -> new int[]{15, 16, 16, 14, 1, 1, 30};
            case 'T' -> new int[]{31, 4, 4, 4, 4, 4, 4};
            case 'U' -> new int[]{17, 17, 17, 17, 17, 17, 14};
            case 'Z' -> new int[]{31, 1, 2, 4, 8, 16, 31};
            default -> new int[7];
        };
    }
}
