package DEAD.LAND.story;

public final class RoteiroHistoria {
    private RoteiroHistoria() {}

    public static String[] abertura() {
        return new String[] {
                "Você está no seu quarto.",
                "O silêncio parece mais pesado do que deveria.",
                "Seu corpo está cansado... mas sua mente continua inquieta.",
                "Talvez dormir seja a única coisa que ainda faça sentido."
        };
    }

    public static String[] menuBloqueado() {
        return new String[] {
                "Sistema",
                "Menu bloqueado.",
                "O botão de logout pisca, mas não responde.",
                "A floresta exige uma decisão antes de abrir a saída."
        };
    }

    public static String[] dormir() {
        return new String[] {
                "Você fecha os olhos e o quarto perde o contorno.",
                "O colchão afunda como água escura.",
                "Quando tenta respirar... o chão desaparece."
        };
    }

    public static String[] quedaConcluida() {
        return new String[] {
                "Você desperta antes do impacto.",
                "O céu tem uma cor que não existe no mundo real.",
                "As árvores se curvam, observando cada passo.",
                "Uma frase aparece, fria e perfeita:",
                "Complete as missões principais para retornar."
        };
    }

    public static String[] npcLembra() {
        return new String[] {
                "NPC",
                "Você caiu aqui de novo?",
                "Ninguém chega acordado a Dead Land.",
                "Procure a memória que o portão roubou de você."
        };
    }

    public static String[] missaoMemoria() {
        return new String[] {
                "Eco da floresta",
                "O ar fica pesado. Algo seu está enterrado aqui.",
                "Encontre a CHAVE DA MEMÓRIA.",
                "Sem ela, a saída só vai repetir o mesmo pesadelo."
        };
    }

    public static String[] portaoSemMemoria() {
        return new String[] {
                "Portão morto",
                "A madeira pulsa como um coração cansado.",
                "Ele reconhece seu medo...",
                "mas exige uma memória para abrir."
        };
    }

    public static String[] chaveColetada() {
        return new String[] {
                "Memória recuperada",
                "Vidro quebrando no asfalto.",
                "Faróis atravessando a chuva.",
                "Uma sirene engolindo seu nome.",
                "Agora você lembra: alguém ficou esperando você acordar."
        };
    }

    public static String[] arcoColetado() {
        return new String[] {
                "Item obtido",
                "Arco encontrado.",
                "A madeira é fria, mas sua mão parece lembrar como usá-lo.",
                "Lutar aqui não é coragem; é ganhar tempo."
        };
    }

    public static String[] flechaColetada() {
        return new String[] {
                "Item obtido",
                "Flecha encontrada.",
                "A ponta vibra como se apontasse para a saída.",
                "Cada flecha é uma chance a menos de desistir."
        };
    }

    public static String[] portaQuartoTrancada() {
        return new String[] {
                "Porta",
                "A maçaneta congela sob seus dedos.",
                "Do outro lado não há corredor... só silêncio.",
                "Ainda não é por aqui."
        };
    }

    public static String[] saidaTrancada() {
        return new String[] {
                "Porta",
                "A saída respira atrás da fechadura.",
                "Falta a CHAVE DA MEMÓRIA."
        };
    }

    public static String[] escolhaFinal() {
        return new String[] {
                "Memória completa.",
                "O carro retorcido.",
                "Chuva no vidro.",
                "Ambulância.",
                "Dead Land oferece descanso. O mundo real exige dor."
        };
    }

    public static String[] finalAcordar() {
        return new String[] {
                "Final: ACORDAR",
                "A floresta se desfaz em luz branca.",
                "Bip... bip... bip...",
                "Seu peito dói como se voltasse de muito longe.",
                "Alguém segura sua mão e sussurra: você voltou."
        };
    }

    public static String[] finalFicar() {
        return new String[] {
                "Final: FICAR",
                "A floresta fica bonita demais para ser verdade.",
                "Os NPCs sorriem sem mover os olhos.",
                "Lá fora, uma máquina continua apitando sozinha.",
                "Você escolheu o lugar onde a dor não podia alcançar."
        };
    }

    public static String[] finalSecreto() {
        return new String[] {
                "Erro do sistema",
                "A escolha foi recusada três vezes.",
                "A floresta entende: você percebeu o truque.",
                "O céu reinicia. As árvores voltam ao mesmo lugar.",
                "Um NPC sussurra: você já tentou isso antes.",
                "DEAD LAND"
        };
    }

    public static String[] npcAri() {
        return new String[] {
                "Ari, o que lembra",
                "Você não morreu... ainda não do jeito que importa.",
                "Dead Land é o lugar entre acordar e desistir.",
                "O portão tomou uma memória sua. Sem ela, você só anda em círculo."
        };
    }

    public static String[] npcMara() {
        return new String[] {
                "Mara, a escutadora",
                "Nem todo vulto aqui quer te ferir.",
                "Alguns de nós somos sobras de pessoas que quase acordaram.",
                "Se encontrar uma chave, não pense nela como metal.",
                "Pense nela como uma lembrança que ainda dói."
        };
    }

    public static String[] npcGuardiaoMemoria() {
        return new String[] {
                "Guardião da memória",
                "A chave está perto porque você também está perto da verdade.",
                "Quando pegá-la, a floresta vai devolver cenas que você tentou apagar.",
                "Depois disso, vá ao portão. Ele só abre para quem lembra."
        };
    }

    public static String[] npcPorteiroSemChave() {
        return new String[] {
                "Porteiro morto",
                "Você chegou até a saída sem trazer a parte que perdeu.",
                "Volte. Procure a CHAVE DA MEMÓRIA.",
                "O portão não quer força. Ele quer verdade."
        };
    }

    public static String[] npcPorteiroComChave() {
        return new String[] {
                "Porteiro morto",
                "Agora você lembra do impacto, da chuva e da sirene.",
                "Posso abrir o caminho, mas não posso escolher por você.",
                "Acordar dói. Ficar aqui também cobra um preço."
        };
    }

    public static String[] npcDesconhecido() {
        return new String[] {
                "NPC",
                "A figura observa você em silêncio.",
                "Por algum motivo, parece estar esperando outra versão sua."
        };
    }

}
