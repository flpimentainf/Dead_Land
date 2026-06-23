package DEAD.LAND.core;

import DEAD.LAND.entity.Boss;
import DEAD.LAND.entity.Flecha;
import DEAD.LAND.entity.Inimigo;
import DEAD.LAND.entity.player;
import DEAD.LAND.ui.panel;
import DEAD.LAND.world.tiles;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ControladorInimigos {

    private static final int DANO_FLECHA = 30;

    private List<Inimigo> inimigos = new ArrayList<>();
    private final verificadorDeColisao verificadorColisao = new verificadorDeColisao();
    private Boss chefe;

    public ControladorInimigos() {
        adicionarInimigo(22* tiles.LARGURA, 3 * tiles.ALTURA, 5);
        adicionarInimigo(25 * tiles.LARGURA, 4 * tiles.ALTURA, 5);
        adicionarInimigo(29 * tiles.LARGURA, 2 * tiles.ALTURA, 5);
        adicionarInimigo(6* tiles.LARGURA, 6* tiles.ALTURA, 6);
        adicionarInimigo(10* tiles.LARGURA, 4* tiles.ALTURA, 6);
        adicionarInimigo(14* tiles.LARGURA, 5* tiles.ALTURA, 6);
        adicionarInimigo(17* tiles.LARGURA, 4* tiles.ALTURA, 6);
        adicionarInimigo(20* tiles.LARGURA, 3* tiles.ALTURA, 6);
        adicionarChefe( 15 * tiles.LARGURA, 2 * tiles.ALTURA, 7);
    }

    private void adicionarInimigo(int x, int y, int cenarioIndex) {
        Inimigo ini = new Inimigo(x, y);
        ini.setCenarioIndex(cenarioIndex);
        inimigos.add(ini);
        
    } 
    
    private void adicionarChefe(int x, int y, int cenarioIndex) {
        chefe = new Boss(x, y);
        chefe.setCenarioIndex(cenarioIndex);
        inimigos.add(chefe);
    }

    public void atualizar(panel cenaDoJogo, ControladorFlechas controladorFlechas) {
        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();
        player jogador = cenaDoJogo.getJogador();

        jogador.atualizarInvencibilidade();

        for (Inimigo ini : inimigos) {
            if (!ini.estaVivo() || ini.getCenarioIndex() != cenarioAtual) {
                continue;
            }
            ini.atualizar(jogador, cenaDoJogo.getCenario(), verificadorColisao);

            // Verificar colisão de flechas com o inimigo
            if (controladorFlechas != null) {
                for (Flecha f : controladorFlechas.getFlechas()) {
                    if (f.isAtiva() && f.intersects(ini.getAreaDano())) {
                        ini.levarDano(DANO_FLECHA);
                        f.desativar();
                    }
                }
            }
        }
    }

    public void desenhar(Graphics2D g2, int cenarioAtual) {
        for (Inimigo ini : inimigos) {
            if (ini.getCenarioIndex() == cenarioAtual) {
                ini.desenhar(g2);
            }
        }
    }

    public boolean bossFoiDerrotado() {
        return chefe != null && !chefe.estaVivo();
    }
}
