package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/** Um único viewport para mundo e rodapé; as margens pertencem à partida. */
public final class PainelPartida extends JPanel {
    private final panel centro;
    private final panel rodape;
    private BufferedImage camadaDialogo;

    public PainelPartida(panel centro, panel rodape) {
        super(null);
        this.centro = centro;
        this.rodape = rodape;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(LayoutHUD.LARGURA, LayoutHUD.ALTURA_JOGO + LayoutHUD.ALTURA_RODAPE));
        add(centro);
        add(rodape);
    }

    @Override public void doLayout() {
        Rectangle area = LayoutHUD.partida(getWidth(), getHeight());
        int alturaJogo = (int) Math.round(area.height * LayoutHUD.ALTURA_JOGO
                / (double) (LayoutHUD.ALTURA_JOGO + LayoutHUD.ALTURA_RODAPE));
        centro.setBounds(area.x, area.y, area.width, alturaJogo);
        rodape.setBounds(area.x, area.y + alturaJogo, area.width, area.height - alturaJogo);
    }

    @Override protected void paintChildren(Graphics destino) {
        super.paintChildren(destino);
        if (!centro.dialogoSobreHUD()) return;
        if (camadaDialogo == null) camadaDialogo = new BufferedImage(LayoutHUD.LARGURA,
                LayoutHUD.ALTURA_JOGO + LayoutHUD.ALTURA_RODAPE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = camadaDialogo.createGraphics();
        try {
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, camadaDialogo.getWidth(), camadaDialogo.getHeight());
            g.setComposite(AlphaComposite.SrcOver);
            g.setColor(new Color(21, 24, 32));
            g.fillRect(0, LayoutHUD.ALTURA_JOGO, LayoutHUD.LARGURA, LayoutHUD.ALTURA_RODAPE);
            centro.desenharDialogoSobreHUD(g);
        } finally { g.dispose(); }
        Rectangle area = LayoutHUD.partida(getWidth(), getHeight());
        Graphics2D tela = (Graphics2D) destino.create();
        try {
            tela.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            tela.drawImage(camadaDialogo, area.x, area.y, area.width, area.height, null);
        } finally { tela.dispose(); }
    }
}
