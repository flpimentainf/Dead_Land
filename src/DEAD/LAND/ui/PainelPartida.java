package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;

/** Um único viewport para mundo e rodapé; as margens pertencem à partida. */
public final class PainelPartida extends JPanel {
    private final panel centro;
    private final panel rodape;

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
}
