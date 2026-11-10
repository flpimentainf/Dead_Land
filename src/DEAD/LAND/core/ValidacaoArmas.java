package DEAD.LAND.core;

import DEAD.LAND.entity.Inimigo;
import DEAD.LAND.entity.item;
import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.Inventario;
import DEAD.LAND.ui.panel;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.Set;

final class ValidacaoArmas {
    static void validar() {
        Inventario inventario = new Inventario();
        panel cena = new panel("centro", inventario) {
            @Override public void salvarAuto() {}
            @Override public void salvarAutoForcado() {}
        };
        try {
            cena.pausarJogo();
            var itens = cena.getControladorItens().getItensPorNomes(Set.of("espada", "chave_boss_63"));
            item espada = itens.stream().filter(i -> "espada".equals(i.getNome())).findFirst().orElseThrow();
            item chave = itens.stream().filter(i -> "chave_boss_63".equals(i.getNome())).findFirst().orElseThrow();
            exigir(espada.getCategoria() == item.Categoria.ARMA && espada.getCenarioIndex() == chave.getCenarioIndex(),
                    "espada deve ser uma arma na sala da chave do boss");
            cena.irParaCenario(espada.getCenarioIndex(), espada.x, espada.y);
            exigir(!cena.getCenario().tileTemColisao(espada.getLinha(), espada.getColuna()), "espada deve estar acessivel");
            cena.getHistoria().restaurarAposRespawn(null, Set.of(), 5, false);
            escutadorTeclado teclado = new escutadorTeclado();
            teclado.interagir = true;
            cena.getControladorItens().atualizar(cena, teclado);
            exigir(espada.foiColetado() && inventario.espadaEquipada(), "E deve coletar e equipar a espada");
            exigir(!inventario.alternarArma(), "Q com apenas uma arma deve manter a selecao");
            inventario.adicionar(new item(item.SPRITE_ARCO, 0, 0, 0, -1, "arco"));
            inventario.adicionar(new item(item.SPRITE_FLECHA, 0, 0, 0, -1, "flecha"));
            exigir(inventario.arcoEquipado() && inventario.alternarArma() && inventario.espadaEquipada(),
                    "Q deve alternar somente armas coletadas");
            GameState salvo = cena.criarGameState(1);
            inventario.alternarArma();
            cena.aplicarGameState(salvo);
            exigir(inventario.espadaEquipada() && inventario.temArco(), "carregar deve preservar espada e arma selecionada");
            cena.getSistemaCheckpoint().restaurar(cena);
            exigir(inventario.espadaEquipada(), "respawn deve preservar a arma selecionada");

            cena.irParaCenario(2, 500, 200);
            cena.getHistoria().restaurarAposRespawn(null, inventario.getNomesItens(), 2, false);
            teclado = new escutadorTeclado();
            long[] tempo = {0};
            ControladorEspada golpes = new ControladorEspada(() -> tempo[0]);
            Point2D mira = new Point2D.Double(800, 230);
            Inimigo perto = inimigo(562, 200, 2), longe = inimigo(700, 200, 2), atras = inimigo(450, 200, 2);
            int vida = perto.getVida(), flechas = inventario.getQuantidadeFlechas();
            exigir(golpes.atacar(cena, mira), "espada equipada deve atacar com mira");
            exigir(!golpes.atacar(cena, mira), "preparacao nao pode ser reiniciada por spam de cliques");
            golpes.atualizar(cena, teclado);
            exigir(!golpes.tentarAtingir(cena, perto), "preparacao ainda nao deve causar dano");
            tempo[0] = 180;
            golpes.atualizar(cena, teclado);
            exigir(golpes.tentarAtingir(cena, perto) && perto.getVida() == vida - 26,
                    "corte deve causar dano a inimigo dentro do alcance");
            exigir(!golpes.tentarAtingir(cena, perto) && perto.getVida() == vida - 26,
                    "mesmo golpe nao pode causar dano repetido a cada frame");
            exigir(!golpes.tentarAtingir(cena, longe) && !golpes.tentarAtingir(cena, atras),
                    "espada deve respeitar alcance e direcao");
            exigir(inventario.getQuantidadeFlechas() == flechas, "espada nao pode consumir flechas");
            exigir(!cena.getControladorFlechas().dispararMouse(cena, mira), "arco guardado nao pode disparar");

            golpes.cancelar();
            cena.getCenario().definirTile(4, 11, 60);
            exigir(golpes.atacar(cena, mira), "novo golpe deve iniciar");
            tempo[0] += 180;
            golpes.atualizar(cena, teclado);
            exigir(!golpes.tentarAtingir(cena, inimigo(562, 200, 2)), "golpe nao pode atravessar uma parede");
            cena.getCenario().definirTile(4, 11, 55);

            for (double angulo : new double[]{0, Math.PI / 2, Math.PI, -Math.PI / 2}) {
                golpes.cancelar();
                double x = cena.getJogador().getCenterX(), y = cena.getJogador().y + 30;
                Inimigo alvo = inimigo((int) (x + Math.cos(angulo) * 48 - 24),
                        (int) (y + Math.sin(angulo) * 48 - 36), 2);
                exigir(golpes.atacar(cena, new Point2D.Double(x + Math.cos(angulo) * 300,
                        y + Math.sin(angulo) * 300)), "golpe direcional deve iniciar");
                tempo[0] += 180;
                golpes.atualizar(cena, teclado);
                exigir(golpes.tentarAtingir(cena, alvo), "golpe deve acertar na direcao escolhida: " + angulo);
            }

            validarTeclasETroca(cena, inventario);
            exigir(cena.getControladorEspada().atacar(cena, mira), "M1 deve aceitar espada equipada");
            teclado = new escutadorTeclado(); teclado.abrirMenu = true;
            cena.atualizarMenuPausa(teclado);
            exigir(!cena.getControladorEspada().estaAtacando() && !cena.getControladorEspada().atacar(cena, mira),
                    "pausa deve cancelar e bloquear ataques");
        } finally { cena.pausarJogo(); }
    }

    private static void validarTeclasETroca(panel cena, Inventario inventario) {
        escutadorTeclado teclado = new escutadorTeclado();
        teclado.keyPressed(new KeyEvent(cena, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_Q, 'q'));
        exigir(teclado.trocarArma && !teclado.usarItem, "Q deve trocar arma sem beber pocao");
        teclado.keyPressed(new KeyEvent(cena, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_H, 'h'));
        exigir(teclado.usarItem, "H deve continuar permitindo usar pocao");
        teclado.resetar();
        gameLoop loop = new gameLoop(cena, teclado);
        teclado.trocarArma = true;
        loop.actionPerformed(null);
        exigir(inventario.arcoEquipado(), "primeiro toque de Q deve trocar para arco");
        loop.actionPerformed(null);
        exigir(inventario.arcoEquipado(), "segurar Q nao deve alternar a cada frame");
        teclado.trocarArma = false; loop.actionPerformed(null);
        teclado.atirar = true; loop.actionPerformed(null);
        int flechas = inventario.getQuantidadeFlechas();
        teclado.trocarArma = true; loop.actionPerformed(null);
        teclado.atirar = false; loop.actionPerformed(null);
        exigir(inventario.espadaEquipada() && inventario.getQuantidadeFlechas() == flechas
                        && !cena.getControladorFlechas().estaCarregando(),
                "trocar arma deve cancelar carga sem disparar nem gastar flecha");
        loop.parar();
    }

    private static Inimigo inimigo(int x, int y, int cenario) {
        Inimigo inimigo = new Inimigo(x, y);
        inimigo.setCenarioIndex(cenario);
        return inimigo;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalStateException(mensagem);
    }
}
