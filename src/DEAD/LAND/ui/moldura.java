package DEAD.LAND.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;


public class moldura extends JFrame{
    public moldura() {
        this.setTitle("DEAD LAND");
        this.setAlwaysOnTop(true);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Inventario inventario = new Inventario();
        panel painelCentro = new panel("centro", inventario);
        panel painelSul = new panel("sul", inventario);
        painelCentro.setPainelInventario(painelSul);

        add(painelCentro, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
    }
}
