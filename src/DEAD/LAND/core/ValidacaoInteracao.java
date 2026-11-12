package DEAD.LAND.core;

import DEAD.LAND.ui.IconeInteracao;
import java.awt.Rectangle;

final class ValidacaoInteracao {
    static void validar() {
        Rectangle tela = new Rectangle(8, 8, 752, 464);
        Rectangle missao = new Rectangle(20, 18, 280, 72);
        Rectangle alvo = new Rectangle(600, 40, 48, 72);
        Rectangle jogador = new Rectangle(560, 90, 48, 48);
        Rectangle indicador = IconeInteracao.posicionar(alvo, tela, jogador, 24, missao);
        exigir(indicador != null && indicador.x == alvo.x + alvo.width + 4,
                "missão do lado esquerdo não deve afastar a interação do lado direito");
        exigir(tela.contains(indicador) && !indicador.intersects(jogador), "E deve caber na tela e evitar o jogador");
        for (Rectangle borda : new Rectangle[]{new Rectangle(8, 8, 48, 72), new Rectangle(704, 8, 48, 72),
                new Rectangle(8, 400, 48, 72), new Rectangle(704, 400, 48, 72)}) {
            indicador = IconeInteracao.posicionar(borda, tela, new Rectangle(350, 240, 48, 48), 24);
            exigir(indicador != null && tela.contains(indicador), "E deve permanecer visível nos quatro cantos");
            Rectangle proximidade = new Rectangle(borda); proximidade.grow(29, 29);
            exigir(proximidade.intersects(indicador), "E precisa permanecer junto do alvo");
        }
        exigir(IconeInteracao.posicionar(alvo, tela, jogador, 24, tela) == null,
                "quando todos os lados estão cobertos, não deve surgir um E longe do objeto");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalStateException(mensagem);
    }
}
