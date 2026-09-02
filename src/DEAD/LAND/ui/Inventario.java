package DEAD.LAND.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import DEAD.LAND.entity.item;
import DEAD.LAND.entity.player;

public class Inventario {
    private static final int TAMANHO_SLOT = 64;
    private static final int ESPACO = 12;
    private static final int MARGEM = 20;
    private static final int FLECHAS_INICIAIS = 8;
    private static final int CURA_FRAGMENTO = 30;

    private List<item> itens = new ArrayList<item>();
    private int quantidadeFlechas;
    private int capacidadeMaximaFlechas = 20;
    private int fragmentosCura;
    private String armaEquipada = "";

    public void adicionar(item itemColetado) {
        if (itemColetado == null) {
            return;
        }

        String nome = itemColetado.getNome();
        if ("flecha".equals(nome)) {
            adicionarFlechas(itemColetado.getQuantidade());
            adicionarRepresentanteSeNecessario(itemColetado);
            return;
        }

        if ("fragmento_cura".equals(nome)) {
            fragmentosCura += itemColetado.getQuantidade();
            adicionarRepresentanteSeNecessario(itemColetado);
            return;
        }

        if (nome != null && !nome.trim().isEmpty() && temItem(nome)) {
            return;
        }

        if (!this.itens.contains(itemColetado)) {
            this.itens.add(itemColetado);
        }

        if ("arco".equals(nome)) {
            armaEquipada = "arco";
        }
    }

    public void substituirPorItens(Collection<item> itensRestaurados) {
        this.itens.clear();
        this.quantidadeFlechas = 0;
        this.fragmentosCura = 0;
        this.armaEquipada = "";
        if (itensRestaurados == null) {
            return;
        }

        for (item itemRestaurado : itensRestaurados) {
            adicionar(itemRestaurado);
        }
    }

    public void substituirPorItens(Collection<item> itensRestaurados, int flechas, int curas) {
        substituirPorItens(itensRestaurados);
        this.quantidadeFlechas = Math.max(0, Math.min(capacidadeMaximaFlechas, flechas));
        this.fragmentosCura = Math.max(0, curas);
    }

    public void desenhar(Graphics2D g2, int largura, int altura) {
        int y = (altura - TAMANHO_SLOT) / 2;

        for (int i = 0; i < this.itens.size(); i++) {
            int x = largura - MARGEM - (i + 1) * TAMANHO_SLOT - i * ESPACO;
            desenharSlot(g2, this.itens.get(i), x, y);
            desenharQuantidade(g2, this.itens.get(i), x, y);
            desenharDicaTecla(g2, this.itens.get(i), x, y);
        }
    }

   public boolean temArco() {
        return temItem("arco") || "arco".equals(armaEquipada);
    }

    public boolean temChave() {
        return temItem("chave");
    }

    public boolean temChaveBoss63() {
        return temItem("chave_boss_63");
    }

    public boolean temItem(String nome) {
        for (item i : itens) {
            if (nome.equals(i.getNome())) return true;
        }
        return false;
    }

    public List<item> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public Set<String> getNomesItens() {
        Set<String> nomes = new LinkedHashSet<String>();
        for (item i : itens) {
            if (i.getNome() != null && !i.getNome().trim().isEmpty()) {
                nomes.add(i.getNome());
            }
        }
        return nomes;
    }

    public void adicionarFlechas(int quantidade) {
        if (quantidade <= 0) {
            return;
        }

        quantidadeFlechas = Math.min(capacidadeMaximaFlechas, quantidadeFlechas + quantidade);
    }

    public boolean consumirFlecha() {
        if (!temFlechas()) {
            return false;
        }

        quantidadeFlechas--;
        return true;
    }

    public boolean temFlechas() {
        return quantidadeFlechas > 0;
    }

    public int getQuantidadeFlechas() {
        return quantidadeFlechas;
    }

    public int getCapacidadeMaximaFlechas() {
        return capacidadeMaximaFlechas;
    }

    public void definirCapacidadeMaximaFlechas(int capacidade) {
        capacidadeMaximaFlechas = Math.max(1, capacidade);
        quantidadeFlechas = Math.min(quantidadeFlechas, capacidadeMaximaFlechas);
    }

    public void definirFlechas(int quantidade) {
        quantidadeFlechas = Math.max(0, Math.min(capacidadeMaximaFlechas, quantidade));
    }

    public void aumentarCapacidadeFlechas(int incremento) {
        if (incremento <= 0) {
            return;
        }

        capacidadeMaximaFlechas += incremento;
        quantidadeFlechas = Math.min(quantidadeFlechas, capacidadeMaximaFlechas);
    }

    public int getQuantidadeCura() {
        return fragmentosCura;
    }

    public void definirQuantidadeCura(int quantidade) {
        fragmentosCura = Math.max(0, quantidade);
    }

    public boolean usarCura(player jogador) {
        if (jogador == null || fragmentosCura <= 0 || jogador.getVida() >= player.VIDA_MAXIMA) {
            return false;
        }

        fragmentosCura--;
        jogador.curar(CURA_FRAGMENTO);
        return true;
    }

    public String getArmaEquipada() {
        return armaEquipada;
    }

    public void equiparArcoSePossuir() {
        if (temItem("arco")) {
            armaEquipada = "arco";
        }
    }

    private void adicionarRepresentanteSeNecessario(item itemColetado) {
        if (!temItem(itemColetado.getNome())) {
            this.itens.add(itemColetado);
        }
    }

    private void desenharDicaTecla(Graphics2D g2, item itemColetado, int x, int y) {
        String tecla = null;
        if ("arco".equals(itemColetado.getNome())) {
            tecla = "SPACE";
        }
        if ("fragmento_cura".equals(itemColetado.getNome())) {
            tecla = "Q";
        }
        if (tecla == null) return;

        g2.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(tecla) + 8;
        int th = 14;
        int tx = x + (TAMANHO_SLOT - tw) / 2;
        int ty = y - th - 3;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(tx, ty, tw, th, 5, 5);
        g2.setColor(new Color(255, 200, 50));
        g2.drawRoundRect(tx, ty, tw, th, 5, 5);
        g2.setColor(Color.WHITE);
        g2.drawString(tecla, tx + 4, ty + th - 3);
    }

    private void desenharSlot(Graphics2D g2, item itemColetado, int x, int y) {
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(x, y, TAMANHO_SLOT, TAMANHO_SLOT, 10, 10);
        g2.setColor("arco".equals(itemColetado.getNome()) && "arco".equals(armaEquipada)
                ? new Color(255, 210, 80)
                : new Color(90, 60, 20));
        g2.drawRoundRect(x, y, TAMANHO_SLOT, TAMANHO_SLOT, 10, 10);
        g2.drawImage(itemColetado.getSprite(), x + 6, y + 6, TAMANHO_SLOT - 12, TAMANHO_SLOT - 12, null);
    }

    private void desenharQuantidade(Graphics2D g2, item itemColetado, int x, int y) {
        String texto = null;
        if ("flecha".equals(itemColetado.getNome())) {
            texto = String.valueOf(quantidadeFlechas);
        } else if ("fragmento_cura".equals(itemColetado.getNome())) {
            texto = String.valueOf(fragmentosCura);
        }

        if (texto == null) {
            return;
        }

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(texto) + 10;
        int alturaTexto = 18;
        int tx = x + TAMANHO_SLOT - larguraTexto - 4;
        int ty = y + TAMANHO_SLOT - alturaTexto - 4;

        g2.setColor(new Color(0, 0, 0, 175));
        g2.fillRoundRect(tx, ty, larguraTexto, alturaTexto, 6, 6);
        g2.setColor(Color.WHITE);
        g2.drawString(texto, tx + 5, ty + 13);
    }
}
