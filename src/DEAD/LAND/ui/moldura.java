package DEAD.LAND.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;


public class moldura extends JFrame{
    public moldura() {
        this.setTitle("DEAD LAND");
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        panel painelCentro = new panel("centro");
        panel painelSul = new panel("sul");

        add(painelCentro, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
    }
}
