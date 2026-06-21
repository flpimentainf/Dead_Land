package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import DEAD.LAND.entity.item;

public class Inventario {
    private static final int TAMANHO_SLOT = 64;
    private static final int ESPACO = 12;
    private static final int MARGEM = 20;

    private List<item> itens = new ArrayList<item>();

    public void adicionar(item itemColetado) {
        if (!this.itens.contains(itemColetado)) {
            this.itens.add(itemColetado);
        }
    }

    public void desenhar(Graphics2D g2, int largura, int altura) {
        int y = (altura - TAMANHO_SLOT) / 2;

        for (int i = 0; i < this.itens.size(); i++) {
            int x = largura - MARGEM - (i + 1) * TAMANHO_SLOT - i * ESPACO;
            desenharSlot(g2, this.itens.get(i), x, y);
        }
    }

    public boolean temArco() {
        return temItem("arco");
    }

    public boolean temItem(String nome) {
        for (item i : itens) {
            if (nome.equals(i.getNome())) return true;
        }
        return false;
    }

    private void desenharSlot(Graphics2D g2, item itemColetado, int x, int y) {
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(x, y, TAMANHO_SLOT, TAMANHO_SLOT, 10, 10);
        g2.setColor(new Color(90, 60, 20));
        g2.drawRoundRect(x, y, TAMANHO_SLOT, TAMANHO_SLOT, 10, 10);
        g2.drawImage(itemColetado.getSprite(), x + 6, y + 6, TAMANHO_SLOT - 12, TAMANHO_SLOT - 12, null);
    }
}