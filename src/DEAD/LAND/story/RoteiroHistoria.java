package DEAD.LAND.story;

public final class RoteiroHistoria {
    private RoteiroHistoria() {}

    public static String[] abertura() {
        return new String[] {
                "Você está no seu quarto.",
                "O silêncio está estranho.",
                "A chuva bate na janela como se tentasse avisar algo.",
                "Sua cabeça dói. Suas mãos tremem.",
                "Talvez dormir seja a única forma de escapar disso."
        };
    }

    public static String[] menuBloqueado() {
        return new String[] {
                "Sistema",
                "Menu bloqueado.",
                "O botão de logout pisca, mas não responde.",
                "Dead Land não deixa ninguém sair no meio da escolha.",
                "A floresta exige uma decisão."
        };
    }

    public static String[] dormir() {
        return new String[] {
                "Você fecha os olhos.",
                "Por um segundo, tudo fica calmo.",
                "Então o chão desaparece.",
                "Você está caindo.",
                "Uma voz distante sussurra: você ainda não terminou."
        };
    }

    public static String[] quedaConcluida() {
        return new String[] {
                "Você desperta antes do impacto.",
                "O céu não tem sol.",
                "As árvores parecem observar você.",
                "Uma frase aparece em sua mente:",
                "Bem-vindo a Dead Land."
        };
    }

    public static String[] npcLembra() {
        return new String[] {
                "Ari, o que lembra",
                "Você caiu aqui de novo?",
                "Não se assuste. Quase ninguém lembra da primeira vez.",
                "Dead Land prende quem está entre acordar e desistir.",
                "Sua memória foi trancada atrás de duas fechaduras.",
                "Primeiro procure a chave prata."
        };
    }

    public static String[] missaoMemoria() {
        return new String[] {
                "Eco da floresta",
                "A Chave da Memória está selada em outra parte da ruína.",
                "Uma chave prata abre o caminho até ela.",
                "Explore esta área, encontre a CHAVE PRATA",
                "e volte para a porta inferior do salão."
        };
    }

    public static String[] portaoSemMemoria() {
        return new String[] {
                "Portão morto",
                "O portão respira lentamente.",
                "Ele sabe que você está incompleto.",
                "A chave prata não é suficiente.",
                "Encontre a chave vermelha",
                "e recupere sua memória antes de atravessar."
        };
    }

    public static String[] chaveColetada() {
        return new String[] {
                "Item obtido: Chave Prata",
                "O metal está frio e sem marcas.",
                "Ela não carrega nenhuma lembrança.",
                "Mas combina com a fechadura da porta inferior do salão.",
                "Atrás dela está a chave vermelha."
        };
    }

    public static String[] arcoColetado() {
        return new String[] {
                "Item obtido: Arco do Eco",
                "A madeira é fria.",
                "Mesmo assim, sua mão sabe como segurá-lo.",
                "Você já lutou antes.",
                "Só não lembra contra o quê."
        };
    }

    public static String[] flechaColetada() {
        return new String[] {
                "Item obtido: Flechas quebradas",
                "Cada flecha carrega uma pequena luz.",
                "Use bem.",
                "A floresta odeia luz."
        };
    }

    public static String[] chaveBossColetada() {
        return new String[] {
                "Memória recuperada: Chave Vermelha",
                "Chuva. Faróis. Um carro fora da estrada.",
                "Vidro quebrado. Alguém chamando seu nome.",
                "Agora você lembra: sofreu um acidente.",
                "Esta é a Chave da Memória.",
                "Leve-a até a porta do confronto final."
        };
    }

    public static String[] portaQuartoTrancada() {
        return new String[] {
                "Porta",
                "A maçaneta está gelada.",
                "Não há nada atrás desta porta.",
                "Hoje, a saída não é acordado.",
                "Volte para a cama."
        };
    }

    public static String[] saidaTrancada() {
        return new String[] {
                "Portão final",
                "A fechadura não se move.",
                "O portão sente que algo está faltando.",
                "Você precisa da CHAVE VERMELHA.",
                "a verdadeira Chave da Memória."
        };
    }

    public static String[] portaChaveNormalTrancada() {
        return new String[] {
                "Porta da câmara vermelha",
                "Uma fechadura prateada impede a passagem.",
                "A Chave da Memória está além desta porta.",
                "Encontre a CHAVE PRATA."
        };
    }

    public static String[] portaBossTrancada() {
        return new String[] {
                "Porta do confronto",
                "O selo pulsa em vermelho.",
                "Somente a CHAVE VERMELHA pode rompê-lo.",
                "Ela é a Chave da Memória."
        };
    }

    public static String[] escolhaFinal() {
        return new String[] {
                "O portão se abre.",
                "Do outro lado existe uma luz branca.",
                "Você ouve máquinas apitando.",
                "Você ouve uma voz chorando.",
                "Alguém está chamando seu nome.",
                "Dead Land oferece descanso.",
                "O mundo real oferece dor.",
                "Escolha."
        };
    }

    public static String[] finalAcordar() {
        return new String[] {
                "Final: ACORDAR",
                "Você atravessa a luz.",
                "A floresta se desfaz atrás de você.",
                "Bip... bip... bip...",
                "Seus olhos abrem devagar.",
                "O teto de um hospital aparece.",
                "Uma mão segura a sua.",
                "Alguém sussurra: você voltou."
        };
    }

    public static String[] finalFicar() {
        return new String[] {
                "Final: FICAR",
                "Você se afasta da luz.",
                "O portão se fecha.",
                "A floresta fica mais bonita.",
                "Os NPCs sorriem, mas nenhum deles pisca.",
                "Lá fora, uma máquina continua apitando sozinha.",
                "Você escolheu esquecer."
        };
    }

    public static String[] finalSecreto() {
        return new String[] {
                "Final secreto: LOOP",
                "A escolha desaparece.",
                "O céu começa a rachar.",
                "As árvores voltam para o mesmo lugar.",
                "Ari observa você de longe.",
                "Ele parece triste.",
                "Você pergunta se já esteve ali antes.",
                "Ari responde: mais vezes do que consegue lembrar."
        };
    }

    public static String[] npcAri() {
        return new String[] {
                "Ari, o que lembra",
                "Você caiu aqui de novo?",
                "Não se assuste. Quase ninguém lembra da primeira vez.",
                "Este lugar se chama Dead Land.",
                "Ele aparece para quem está preso entre dois mundos.",
                "Sua memória está presa na chave vermelha.",
                "Mas ela foi trancada atrás de uma porta prateada.",
                "Encontre primeiro a chave prata."
        };
    }

    public static String[] npcMara() {
        return new String[] {
                "Mara, a escutadora",
                "Eu ouvi sua queda antes de você chegar.",
                "A porta inferior deste salão protege uma câmara.",
                "Lá dentro está a chave vermelha.",
                "Não tente forçar a fechadura.",
                "Procure a chave prata na área à esquerda."
        };
    }

    public static String[] npcMaraComChavePrata() {
        return new String[] {
                "Mara, a escutadora",
                "Você encontrou a chave prata.",
                "Agora volte à porta inferior deste salão.",
                "Ela abre o caminho até a chave vermelha.",
                "Não confunda as duas: somente a vermelha guarda sua memória."
        };
    }

    public static String[] npcMaraComMemoria() {
        return new String[] {
                "Mara, a escutadora",
                "A chave vermelha está com você.",
                "Eu consigo ouvir a lembrança presa nela.",
                "O caminho do confronto final agora pode ser aberto."
        };
    }

    public static String[] npcGuardiaoMemoria() {
        return new String[] {
                "Guardião da memória",
                "Você encontrou o caminho da chave prata.",
                "Leve-a de volta ao salão de pedra.",
                "Ela abre a porta inferior.",
                "Depois dessa porta está a chave vermelha.",
                "Essa, sim, guarda sua memória."
        };
    }

    public static String[] npcGuardiaoComChavePrata() {
        return new String[] {
                "Guardião da memória",
                "A chave prata escolheu sua mão.",
                "Volte ao salão e abra a porta inferior.",
                "A chave vermelha espera do outro lado."
        };
    }

    public static String[] npcGuardiaoComMemoria() {
        return new String[] {
                "Guardião da memória",
                "Você encontrou a verdadeira chave.",
                "O vermelho dela carrega aquilo que tentou esquecer.",
                "Vá até a porta do confronto e termine o caminho."
        };
    }

    public static String[] npcPorteiroSemChave() {
        return new String[] {
                "Porteiro morto",
                "Você chegou cedo demais.",
                "A chave prata abre apenas o caminho.",
                "Ela não pode abrir o portão final.",
                "Volte e encontre a CHAVE VERMELHA.",
                "Traga a verdadeira Chave da Memória."
        };
    }

    public static String[] npcPorteiroComChave() {
        return new String[] {
                "Porteiro morto",
                "A chave vermelha reconheceu você.",
                "Agora você lembra.",
                "O acidente.",
                "A chuva.",
                "A sirene.",
                "A mão de alguém segurando a sua.",
                "Posso abrir o caminho, mas não posso escolher por você."
        };
    }

    public static String[] npcDesconhecido() {
        return new String[] {
                "NPC",
                "A figura observa você em silêncio.",
                "Por algum motivo, parece estar esperando outra versão sua."
        };
    }

    public static String[] respostaAriIdentidade() {
        return new String[] {
                "Ari",
                "Eu sou uma lembrança que aprendeu a caminhar.",
                "Ajudo quem ainda tem vontade de acordar."
        };
    }

    public static String[] respostaAriMissao(boolean temMemoria) {
        return temMemoria
                ? new String[] {"Ari", "Você já recuperou a memória.", "Siga para o confronto final."}
                : new String[] {"Ari", "Encontre a chave prata primeiro.", "Ela leva até a chave vermelha."};
    }

    public static String[] respostaMaraEscuta() {
        return new String[] {
                "Mara",
                "Eu ouço as memórias presas nas paredes.",
                "A sua faz o som de chuva sobre vidro."
        };
    }

    public static String[] respostaMaraChaves(boolean temPrata, boolean temMemoria) {
        if (temMemoria) {
            return new String[] {"Mara", "A chave vermelha está com você.", "A porta final vai reconhecê-la."};
        }
        if (temPrata) {
            return new String[] {"Mara", "Use a chave prata na porta inferior.", "A chave vermelha está além dela."};
        }
        return new String[] {"Mara", "A chave prata está na área à esquerda.", "Volte com ela para este salão."};
    }

    public static String[] respostaGuardiaoFuncao() {
        return new String[] {
                "Guardião",
                "Eu protejo o caminho entre a chave prata e a memória.",
                "Quando a chave vermelha for encontrada, minha tarefa termina."
        };
    }

    public static String[] respostaGuardiaoMissao(boolean temPrata, boolean temMemoria) {
        if (temMemoria) {
            return new String[] {"Guardião", "Sua memória voltou.", "Não há mais nada para eu guardar."};
        }
        if (temPrata) {
            return new String[] {"Guardião", "Leve a prata à porta inferior.", "Encontre a chave vermelha."};
        }
        return new String[] {"Guardião", "Explore esta área.", "A chave prata está próxima."};
    }

    public static String[] respostaPorteiroAlem() {
        return new String[] {
                "Porteiro",
                "Além do portão existe a escolha.",
                "Acordar e enfrentar a dor, ou permanecer em Dead Land."
        };
    }

    public static String[] respostaPorteiroFalta(boolean temMemoria) {
        return temMemoria
                ? new String[] {"Porteiro", "Nada mais falta.", "A chave vermelha completou sua memória."}
                : new String[] {"Porteiro", "Falta a chave vermelha.", "A chave prata não abre este caminho."};
    }

    // Métodos extras opcionais para usar depois em novos eventos.
    // Eles não quebram o projeto atual, mesmo que ainda não estejam sendo chamados.

    public static String[] dicaAri() {
        return new String[] {
                "Ari, o que lembra",
                "Primeiro encontre a chave prata.",
                "Ela abre a câmara da chave vermelha.",
                "A vermelha é a verdadeira Chave da Memória."
        };
    }

    public static String[] dicaMaraComArco() {
        return new String[] {
                "Mara, a escutadora",
                "Agora você pode lutar.",
                "Mas cuidado.",
                "As criaturas daqui não querem matar seu corpo.",
                "Elas querem cansar sua vontade."
        };
    }

    public static String[] inimigoAvistado() {
        return new String[] {
                "Algo se move entre as árvores.",
                "Não é vivo.",
                "Não é morto.",
                "É o que sobra quando alguém desiste de acordar."
        };
    }

    public static String[] inimigoDerrotado() {
        return new String[] {
                "A criatura cai em silêncio.",
                "Por um instante, você sente pena.",
                "Talvez ela também tenha procurado uma saída."
        };
    }

    public static String[] antesDoBoss() {
        return new String[] {
                "O selo se quebra.",
                "Do outro lado, uma figura aparece.",
                "Ela tem seus olhos.",
                "Sua postura.",
                "Sua sombra.",
                "O Esquecido sorri: você demorou."
        };
    }

    public static String[] falaBoss() {
        return new String[] {
                "O Esquecido",
                "Por que quer voltar?",
                "Lá fora existe dor.",
                "Aqui existe silêncio.",
                "Lá fora, você vai lembrar de tudo.",
                "Aqui, eu posso apagar para você.",
                "Fique. Pare de lutar."
        };
    }

    public static String[] bossDerrotado() {
        return new String[] {
                "O Esquecido cai de joelhos.",
                "Pela primeira vez, a floresta fica em silêncio de verdade.",
                "Ele olha para você e sussurra:",
                "Então você ainda quer acordar..."
        };
    }

    public static String[] placaFloresta1() {
        return new String[] {
                "Placa antiga",
                "Nem todo caminho leva para frente.",
                "A floresta lembra de você."
        };
    }

    public static String[] placaFloresta2() {
        return new String[] {
                "Placa antiga",
                "Quem esquece demais vira parte do lugar.",
                "Não siga as vozes depois da meia-noite."
        };
    }

    public static String[] placaPortaoFinal() {
        return new String[] {
                "Placa quebrada",
                "Dead Land não prende corpos.",
                "Prende vontades."
        };
    }
}
