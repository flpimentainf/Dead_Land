package DEAD.LAND.ui;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

public final class TextoHUD {
    private TextoHUD() {}

    /** Quebra inclusive palavras maiores que a caixa, sem descartar conteúdo. */
    public static List<String> quebrar(String texto, FontMetrics fm, int largura) {
        List<String> linhas = new ArrayList<>();
        for (String paragrafo : (texto == null ? "" : texto).split("\\R", -1)) {
            String linha = "";
            for (String palavra : paragrafo.strip().split("\\s+")) {
                if (palavra.isEmpty()) continue;
                if (!linha.isEmpty() && fm.stringWidth(linha + " " + palavra) > largura) {
                    linhas.add(linha);
                    linha = "";
                }
                while (fm.stringWidth(palavra) > largura && palavra.length() > 1) {
                    int corte = 1;
                    while (corte < palavra.length() && fm.stringWidth(palavra.substring(0, corte + 1)) <= largura) corte++;
                    linhas.add(palavra.substring(0, corte));
                    palavra = palavra.substring(corte);
                }
                linha = linha.isEmpty() ? palavra : linha + " " + palavra;
            }
            linhas.add(linha);
        }
        return linhas;
    }
}
