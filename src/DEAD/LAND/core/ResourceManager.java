package DEAD.LAND.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class ResourceManager {
    private static final ResourceManager INSTANCIA = new ResourceManager();

    private final Map<String, Image> cacheImagens = new HashMap<String, Image>();
    private final Image imagemFallback;

    private ResourceManager() {
        this.imagemFallback = criarImagemFallback();
    }

    public static ResourceManager getInstancia() {
        return INSTANCIA;
    }

    public Image carregarImagem(String caminho) {
        if (caminho == null || caminho.trim().isEmpty()) {
            return imagemFallback;
        }

        if (cacheImagens.containsKey(caminho)) {
            return cacheImagens.get(caminho);
        }

        Image imagem = imagemFallback;
        File arquivo = new File(caminho);
        if (arquivo.exists()) {
            ImageIcon icon = new ImageIcon(caminho);
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                imagem = icon.getImage();
            }
        }

        cacheImagens.put(caminho, imagem);
        return imagem;
    }

    private Image criarImagemFallback() {
        BufferedImage imagem = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            g2.setColor(new Color(35, 35, 45));
            g2.fillRect(0, 0, 48, 48);
            g2.setColor(new Color(190, 60, 80));
            g2.drawRect(1, 1, 45, 45);
            g2.drawLine(6, 6, 41, 41);
            g2.drawLine(41, 6, 6, 41);
        } finally {
            g2.dispose();
        }
        return imagem;
    }
}
