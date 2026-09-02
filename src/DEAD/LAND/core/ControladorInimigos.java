package DEAD.LAND.core;

import DEAD.LAND.entity.Boss;
import DEAD.LAND.entity.Flecha;
import DEAD.LAND.entity.Inimigo;
import DEAD.LAND.entity.TipoInimigo;
import DEAD.LAND.entity.player;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tiles;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControladorInimigos {
    private final List<Inimigo> inimigos = new ArrayList<Inimigo>();
    private final ControladorProjeteisInimigos controladorProjeteis = new ControladorProjeteisInimigos();
    private final verificadorDeColisao verificadorColisao = new verificadorDeColisao();
    private final Random random = new Random();
    private Boss chefe;
    private boolean bossDerrotadoNotificado;

    public ControladorInimigos() {
        carregarSpawns();
    }

    private void carregarSpawns() {
        List<SpawnInimigo> spawns = new ArrayList<SpawnInimigo>();
        spawns.add(new SpawnInimigo(TipoInimigo.SKELETON, 5, 22 * tiles.LARGURA, 3 * tiles.ALTURA, "esquerda", 90));
        spawns.add(new SpawnInimigo(TipoInimigo.SLIME, 5, 25 * tiles.LARGURA, 4 * tiles.ALTURA, "esquerda", 80));
        spawns.add(new SpawnInimigo(TipoInimigo.RANGED, 5, 29 * tiles.LARGURA, 2 * tiles.ALTURA, "esquerda", 70));
        spawns.add(new SpawnInimigo(TipoInimigo.SKELETON, 6, 6 * tiles.LARGURA, 6 * tiles.ALTURA, "direita", 100));
        spawns.add(new SpawnInimigo(TipoInimigo.SLIME, 6, 10 * tiles.LARGURA, 4 * tiles.ALTURA, "direita", 90));
        spawns.add(new SpawnInimigo(TipoInimigo.SKELETON, 6, 14 * tiles.LARGURA, 5 * tiles.ALTURA, "esquerda", 100));
        spawns.add(new SpawnInimigo(TipoInimigo.SLIME, 6, 17 * tiles.LARGURA, 4 * tiles.ALTURA, "esquerda", 90));
        spawns.add(new SpawnInimigo(TipoInimigo.RANGED, 6, 20 * tiles.LARGURA, 3 * tiles.ALTURA, "esquerda", 80));

        for (SpawnInimigo spawn : spawns) {
            adicionarInimigo(spawn);
        }

        adicionarChefe(15 * tiles.LARGURA, 2 * tiles.ALTURA, 7);
    }

    private void adicionarInimigo(SpawnInimigo spawn) {
        Inimigo inimigo = new Inimigo(
                spawn.getX(),
                spawn.getY(),
                spawn.getTipo(),
                spawn.getDirecaoInicial(),
                spawn.getRaioPatrulha()
        );
        inimigo.setCenarioIndex(spawn.getCenarioIndex());
        inimigos.add(inimigo);
    }

    private void adicionarChefe(int x, int y, int cenarioIndex) {
        chefe = new Boss(x, y);
        chefe.setCenarioIndex(cenarioIndex);
        inimigos.add(chefe);
    }

    public void atualizar(panel cenaDoJogo, ControladorFlechas controladorFlechas) {
        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();
        player jogador = cenaDoJogo.getJogador();

        for (Inimigo inimigo : inimigos) {
            if (!inimigo.estaVivo() || inimigo.getCenarioIndex() != cenarioAtual) {
                continue;
            }

            inimigo.atualizar(jogador, cenaDoJogo.getCenario(), verificadorColisao, controladorProjeteis);
            verificarColisaoComFlechas(cenaDoJogo, controladorFlechas, inimigo);
            processarDropSeNecessario(cenaDoJogo, inimigo, cenarioAtual);
        }

        separarInimigosDoCenario(cenaDoJogo, cenarioAtual);
        controladorProjeteis.atualizar(cenaDoJogo);
        notificarBossDerrotado(cenaDoJogo);
    }

    private void verificarColisaoComFlechas(panel cenaDoJogo,
            ControladorFlechas controladorFlechas, Inimigo inimigo) {
        if (controladorFlechas == null || !inimigo.podeSerAtingido()) {
            return;
        }

        for (Flecha flecha : controladorFlechas.getFlechas()) {
            if (flecha.isAtiva() && flecha.intersects(inimigo.getAreaDano())) {
                inimigo.levarDano(
                        flecha.getDano(),
                        flecha.getCentroX(),
                        flecha.getCentroY(),
                        flecha.getKnockback()
                );
                flecha.desativar();
                if (cenaDoJogo.getFeedback() != null) {
                    cenaDoJogo.getFeedback().impactoFlecha(flecha.getCentroX(), flecha.getCentroY());
                    cenaDoJogo.getFeedback().textoDano(inimigo.x + inimigo.width / 2, inimigo.y, flecha.getDano());
                }
                AudioManager.getInstancia().tocarEfeito("flecha_inimigo");
            }
        }
    }

    private void processarDropSeNecessario(panel cenaDoJogo, Inimigo inimigo, int cenarioAtual) {
        if (!inimigo.consumirDropPendente()) {
            return;
        }

        if (cenaDoJogo.getFeedback() != null) {
            cenaDoJogo.getFeedback().morteInimigo(inimigo.x + inimigo.width / 2, inimigo.y + inimigo.height / 2);
        }

        if (inimigo instanceof Boss || cenaDoJogo.getControladorItens() == null) {
            return;
        }

        int rolagem = random.nextInt(100);
        if (rolagem < 30) {
            cenaDoJogo.getControladorItens().criarDrop(
                    "flecha",
                    cenarioAtual,
                    inimigo.x + inimigo.width / 2,
                    inimigo.y + inimigo.height / 2,
                    1 + random.nextInt(3)
            );
        } else if (rolagem < 42) {
            cenaDoJogo.getControladorItens().criarDrop(
                    "fragmento_cura",
                    cenarioAtual,
                    inimigo.x + inimigo.width / 2,
                    inimigo.y + inimigo.height / 2,
                    1
            );
        }
    }

    private void separarInimigosDoCenario(panel cenaDoJogo, int cenarioAtual) {
        for (int i = 0; i < inimigos.size(); i++) {
            Inimigo a = inimigos.get(i);
            if (!a.estaVivo() || a.getCenarioIndex() != cenarioAtual) {
                continue;
            }
            for (int j = i + 1; j < inimigos.size(); j++) {
                Inimigo b = inimigos.get(j);
                if (b.estaVivo() && b.getCenarioIndex() == cenarioAtual) {
                    a.separarDe(b, cenaDoJogo.getCenario(), verificadorColisao);
                    b.separarDe(a, cenaDoJogo.getCenario(), verificadorColisao);
                }
            }
        }
    }

    private void notificarBossDerrotado(panel cenaDoJogo) {
        if (bossDerrotadoNotificado || chefe == null || !chefe.foiDerrotado()) {
            return;
        }

        bossDerrotadoNotificado = true;
        controladorProjeteis.limpar();
        if (cenaDoJogo.getHistoria() != null) {
            cenaDoJogo.getHistoria().eventoBossDerrotado();
        }
        if (cenaDoJogo.getFeedback() != null) {
            cenaDoJogo.getFeedback().mostrarMensagemCentro("O Esquecido caiu");
        }
        AudioManager.getInstancia().tocarEfeito("boss_morte");
        cenaDoJogo.salvarAutoForcado();
    }

    public void desenhar(Graphics2D g2, int cenarioAtual) {
        for (Inimigo ini : inimigos) {
            if (ini.getCenarioIndex() == cenarioAtual) {
                ini.desenhar(g2);
            }
        }
        controladorProjeteis.desenhar(g2);
    }

    public boolean bossFoiDerrotado() {
        return chefe != null && chefe.foiDerrotado();
    }

    public Boss getChefe() {
        return chefe;
    }

    public void definirBossDerrotado(boolean derrotado) {
        if (chefe == null || !derrotado) {
            return;
        }

        chefe.marcarDerrotado();
        bossDerrotadoNotificado = true;
        controladorProjeteis.limpar();
    }

    public boolean existeCombateAtivo(panel cenaDoJogo) {
        if (cenaDoJogo == null || cenaDoJogo.getCenario() == null) {
            return false;
        }

        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();
        player jogador = cenaDoJogo.getJogador();

        for (Inimigo ini : inimigos) {
            if (ini.getCenarioIndex() == cenarioAtual && ini.estaEmCombateCom(jogador)) {
                return true;
            }
        }

        return false;
    }

    public void resetarCenario(int cenarioIndex, boolean bossDerrotadoNoCheckpoint) {
        controladorProjeteis.limpar();
        for (Inimigo ini : inimigos) {
            if (ini.getCenarioIndex() != cenarioIndex) {
                continue;
            }

            if (ini == chefe && bossDerrotadoNoCheckpoint) {
                ini.marcarDerrotado();
            } else {
                ini.resetarParaOrigem();
            }
        }
    }
}
