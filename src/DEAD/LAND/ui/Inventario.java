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
    private static final int CURA_POCAO = 30;

    private List<item> itens = new ArrayList<item>();
    private int quantidadeFlechas;
    private int capacidadeMaximaFlechas = 20;
    private int fragmentosCura;
    private String armaEquipada = "";
    private final item iconeFlechas = new item(item.SPRITE_FLECHA, 0, 0, 0, -1, "flecha");
    private final item iconePocaoCura = new item(item.SPRITE_POCAO_CURA, 0, 0, 0, -1, "pocao_cura");

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

        if ("pocao_cura".equals(nome)) {
            fragmentosCura += itemColetado.getQuantidade();
            adicionarRepresentanteSeNecessario(itemColetado);
            return;
        }

        if (nome != null && !nome.trim().isEmpty() && temItem(nome)) {
            return;
        }

        this.itens.add(itemColetado);

        if (ehArma(nome)) {
            armaEquipada = nome;
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
        definirQuantidadeCura(curas);
    }

    private int paginaRapida;

    public List<item> getItensRapidos() {
        List<item> rapidos = new ArrayList<>();
        for (item i : itens) {
            if (ehArma(i.getNome()) || "flecha".equals(i.getNome()) && !arcoEquipado()) continue;
            rapidos.add(i);
        }
        if (arcoEquipado() && !temItem("flecha")) rapidos.add(iconeFlechas);
        return rapidos;
    }

    public int getTotalPaginas(LayoutHUD.Inferior layout) {
        int capacidade = Math.max(1, layout.capacidade() - (armaEquipada.isEmpty() ? 0 : 1));
        return Math.max(1, (getItensRapidos().size() + capacidade - 1) / capacidade);
    }

    public int getPaginaRapida() { return paginaRapida; }
    public List<item> getItensPagina(LayoutHUD.Inferior layout) {
        List<item> rapidos = getItensRapidos();
        int capacidade = Math.max(1, layout.capacidade() - (armaEquipada.isEmpty() ? 0 : 1));
        paginaRapida = Math.min(paginaRapida, getTotalPaginas(layout) - 1);
        int inicio = Math.min(rapidos.size(), paginaRapida * capacidade);
        return List.copyOf(rapidos.subList(inicio, Math.min(rapidos.size(), inicio + capacidade)));
    }
    public void mudarPagina(int delta, LayoutHUD.Inferior layout) {
        paginaRapida = Math.floorMod(paginaRapida + delta, getTotalPaginas(layout));
    }

    public void desenhar(Graphics2D g2, int largura, int altura) {
        desenhar(g2, LayoutHUD.inferior(largura, altura));
    }

    public void desenhar(Graphics2D g2, LayoutHUD.Inferior layout) {
        if (layout.capacidade() <= 0) return;
        int indice = 0;
        for (item arma : itens) {
            if (ehArma(arma.getNome()) && arma.getNome().equals(armaEquipada)) {
                desenharItem(g2, arma, layout.slot(indice++));
                break;
            }
        }
        int paginas = getTotalPaginas(layout);
        for (item rapido : getItensPagina(layout)) {
            if (indice >= layout.capacidade()) break;
            desenharItem(g2, rapido, layout.slot(indice++));
        }
        if (paginas > 1) {
            g2.setColor(new Color(220, 206, 164));
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            java.awt.Rectangle anterior = layout.anterior(), proxima = layout.proxima();
            g2.drawRect(anterior.x, anterior.y, anterior.width, anterior.height);
            g2.drawRect(proxima.x, proxima.y, proxima.width, proxima.height);
            g2.drawString("<", anterior.x + 8, anterior.y + 13);
            g2.drawString(">", proxima.x + 8, proxima.y + 13);
            String dica = (paginaRapida + 1) + "/" + paginas + " | roda do mouse: itens";
            if (g2.getFontMetrics().stringWidth(dica) > layout.inventario().width - 60) dica = (paginaRapida + 1) + "/" + paginas;
            int x = anterior.x + (proxima.x + proxima.width - anterior.x - g2.getFontMetrics().stringWidth(dica)) / 2;
            g2.drawString(dica, x, anterior.y + 13);
        }
    }

    private void desenharItem(Graphics2D destino, item itemColetado, java.awt.Rectangle slot) {
        Graphics2D g2 = (Graphics2D) destino.create();
        try {
            g2.translate(slot.x, slot.y);
            g2.scale(slot.width / 64.0, slot.height / 64.0);
            desenharSlot(g2, itemColetado, 0, 0);
            desenharQuantidade(g2, itemColetado, 0, 0);
            desenharDicaTecla(g2, itemColetado, 0, 0);
        } finally { g2.dispose(); }
    }
   public boolean temArco() {
        return temItem("arco");
    }

    public boolean temEspada() { return temItem("espada"); }
    public boolean arcoEquipado() { return temArco() && "arco".equals(armaEquipada); }
    public boolean espadaEquipada() { return temEspada() && "espada".equals(armaEquipada); }
    private static boolean ehArma(String nome) { return "arco".equals(nome) || "espada".equals(nome); }

    public boolean alternarArma() {
        if (!temArco() || !temEspada()) return false;
        armaEquipada = arcoEquipado() ? "espada" : "arco";
        return true;
    }

    public void restaurarArmaEquipada(String nome) {
        if (ehArma(nome) && temItem(nome)) armaEquipada = nome;
        else if (temArco()) armaEquipada = "arco";
        else if (temEspada()) armaEquipada = "espada";
        else armaEquipada = "";
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
        if (fragmentosCura > 0) {
            adicionarRepresentanteSeNecessario(iconePocaoCura);
        }
    }

    public boolean usarCura(player jogador) {
        if (jogador == null || fragmentosCura <= 0 || jogador.getVida() >= player.VIDA_MAXIMA) {
            return false;
        }

        fragmentosCura--;
        jogador.curar(CURA_POCAO);
        return true;
    }

    public String getMotivoCuraIndisponivel(player jogador) {
        if (fragmentosCura <= 0) return "Sem poções de cura.";
        if (jogador == null || jogador.getVida() >= player.VIDA_MAXIMA) return "Vida já está cheia.";
        return "";
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
        boolean arma = ehArma(itemColetado.getNome());
        if (arma) tecla = "Q";
        if ("pocao_cura".equals(itemColetado.getNome())) {
            tecla = "H";
        }
        if (tecla == null) return;

        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(tecla) + 8;
        int th = arma ? 18 : 14;
        int tx = arma ? x + TAMANHO_SLOT - tw - 3 : x + (TAMANHO_SLOT - tw) / 2;
        int ty = y + 3;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(tx, ty, tw, th, 5, 5);
        g2.setColor(new Color(255, 200, 50));
        g2.drawRoundRect(tx, ty, tw, th, 5, 5);
        g2.setColor(Color.WHITE);
        g2.drawString(tecla, tx + 4, ty + th - 3);
    }

    private void desenharSlot(Graphics2D g2, item itemColetado, int x, int y) {
        boolean equipada = ehArma(itemColetado.getNome()) && itemColetado.getNome().equals(armaEquipada);
        g2.setColor(EstiloHUD.FUNDO);
        g2.fillRect(x, y, TAMANHO_SLOT, TAMANHO_SLOT);
        g2.setColor(equipada ? EstiloHUD.DESTAQUE : EstiloHUD.BORDA);
        g2.drawRect(x, y, TAMANHO_SLOT - 1, TAMANHO_SLOT - 1);
        if (equipada) {
            g2.drawRect(x + 2, y + 2, TAMANHO_SLOT - 5, TAMANHO_SLOT - 5);
            g2.setFont(EstiloHUD.TITULO.deriveFont(9f));
            g2.drawString("ATIVA", x + 5, y + 13);
        }
        if (ehArma(itemColetado.getNome())) {
            g2.drawImage(itemColetado.getSprite(), x + 10, y + 16, 44, 44, null);
        } else {
            g2.drawImage(itemColetado.getSprite(), x + 6, y + 6, TAMANHO_SLOT - 12, TAMANHO_SLOT - 12, null);
        }
    }

    private void desenharQuantidade(Graphics2D g2, item itemColetado, int x, int y) {
        String texto = null;
        if ("flecha".equals(itemColetado.getNome())) {
            texto = String.valueOf(quantidadeFlechas);
        } else if ("pocao_cura".equals(itemColetado.getNome())) {
            texto = String.valueOf(fragmentosCura);
        }

        if (texto == null) {
            return;
        }

        if (texto.length() > 4) texto = "999+";
        g2.setFont(EstiloHUD.TITULO);
        FontMetrics fm = g2.getFontMetrics();
        int larguraTexto = fm.stringWidth(texto) + 10;
        int alturaTexto = 18;
        int tx = "flecha".equals(itemColetado.getNome())
                ? x + 4
                : x + TAMANHO_SLOT - larguraTexto - 4;
        int ty = y + TAMANHO_SLOT - alturaTexto - 4;

        g2.setColor(new Color(0, 0, 0, 175));
        g2.fillRoundRect(tx, ty, larguraTexto, alturaTexto, 6, 6);
        g2.setColor(Color.WHITE);
        g2.drawString(texto, tx + 5, ty + 13);
    }
}
