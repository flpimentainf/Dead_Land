package DEAD.LAND.core;

import DEAD.LAND.story.SistemaHistoria;
import DEAD.LAND.ui.panel;
import java.util.LinkedHashSet;
import java.util.Set;

public class SistemaCheckpoint {
    private static final String CHECKPOINT_INICIAL = "inicio";
    private static final String CHECKPOINT_QUEDA = "queda_concluida";
    private static final String CHECKPOINT_REGIAO_PRINCIPAL = "regiao_principal";
    private static final String CHECKPOINT_CHAVE_PRATA = "chave_prata";
    private static final String CHECKPOINT_ANTES_BOSS = "antes_boss";

    private final Set<String> checkpointsRegistrados = new LinkedHashSet<String>();
    private Checkpoint checkpointAtual;

    public void registrarCheckpointInicial(panel cenaDoJogo) {
        registrar(CHECKPOINT_INICIAL, cenaDoJogo);
    }

    public void avaliarCheckpointAutomatico(panel cenaDoJogo) {
        if (cenaDoJogo == null
                || cenaDoJogo.getCenario() == null
                || cenaDoJogo.getJogador() == null
                || cenaDoJogo.getJogador().estaMorto()) {
            return;
        }

        int cenarioAtual = cenaDoJogo.getCenario().getCenarioAtualIndex();

        if (cenarioAtual == 2 && cenaDoJogo.getHistoria() != null
                && cenaDoJogo.getHistoria().getEstado() != SistemaHistoria.Estado.QUEDA) {
            tentarRegistrar(CHECKPOINT_QUEDA, cenaDoJogo);
        }

        if (cenarioAtual == 5) {
            tentarRegistrar(CHECKPOINT_REGIAO_PRINCIPAL, cenaDoJogo);
        }

        if (cenaDoJogo.getInventario() != null && cenaDoJogo.getInventario().temChave()) {
            tentarRegistrar(CHECKPOINT_CHAVE_PRATA, cenaDoJogo);
        }

        if (cenarioAtual == 7 && cenaDoJogo.getInventario() != null
                && cenaDoJogo.getInventario().temChaveBoss63()) {
            tentarRegistrar(CHECKPOINT_ANTES_BOSS, cenaDoJogo);
        }
    }

    public void restaurar(panel cenaDoJogo) {
        if (cenaDoJogo == null) {
            return;
        }

        if (checkpointAtual == null) {
            registrarCheckpointInicial(cenaDoJogo);
        }

        if (checkpointAtual == null) {
            return;
        }

        Set<String> itensPreservados = new LinkedHashSet<String>(checkpointAtual.itensImportantes);
        if (cenaDoJogo.getInventario() != null) {
            itensPreservados.addAll(cenaDoJogo.getInventario().getNomesItens());
        }

        boolean bossDerrotado = checkpointAtual.bossDerrotado
                || (cenaDoJogo.getControladorInimigos() != null
                        && cenaDoJogo.getControladorInimigos().bossFoiDerrotado());

        cenaDoJogo.irParaCenario(
                checkpointAtual.cenarioIndex,
                checkpointAtual.jogadorX,
                checkpointAtual.jogadorY
        );

        cenaDoJogo.getJogador().prepararRespawn();

        if (cenaDoJogo.getControladorFlechas() != null) {
            cenaDoJogo.getControladorFlechas().limpar();
        }

        if (cenaDoJogo.getControladorItens() != null && cenaDoJogo.getInventario() != null) {
            cenaDoJogo.getControladorItens().restaurarColetas(
                    itensPreservados,
                    cenaDoJogo.getCenario()
            );
            cenaDoJogo.getInventario().substituirPorItens(
                    cenaDoJogo.getControladorItens().getItensPorNomes(itensPreservados)
            );
            cenaDoJogo.atualizarInventario();
        }

        if (cenaDoJogo.getControladorInimigos() != null) {
            cenaDoJogo.getControladorInimigos().resetarCenario(
                    checkpointAtual.cenarioIndex,
                    bossDerrotado
            );
        }

        if (cenaDoJogo.getHistoria() != null) {
            cenaDoJogo.getHistoria().restaurarAposRespawn(
                    checkpointAtual.progressoHistoria,
                    itensPreservados,
                    checkpointAtual.cenarioIndex,
                    bossDerrotado
            );
        }
    }

    public String getNomeCheckpointAtual() {
        return checkpointAtual == null ? "" : checkpointAtual.id;
    }

    private void tentarRegistrar(String id, panel cenaDoJogo) {
        if (checkpointsRegistrados.contains(id) || cenaDoJogo.existeCombateAtivo()) {
            return;
        }

        registrar(id, cenaDoJogo);
    }

    private void registrar(String id, panel cenaDoJogo) {
        if (cenaDoJogo == null
                || cenaDoJogo.getCenario() == null
                || cenaDoJogo.getJogador() == null) {
            return;
        }

        Set<String> itens = cenaDoJogo.getInventario() == null
                ? new LinkedHashSet<String>()
                : cenaDoJogo.getInventario().getNomesItens();

        SistemaHistoria.ProgressoHistoria progresso = cenaDoJogo.getHistoria() == null
                ? null
                : cenaDoJogo.getHistoria().copiarProgresso();

        boolean bossDerrotado = cenaDoJogo.getControladorInimigos() != null
                && cenaDoJogo.getControladorInimigos().bossFoiDerrotado();

        this.checkpointAtual = new Checkpoint(
                id,
                cenaDoJogo.getCenario().getCenarioAtualIndex(),
                cenaDoJogo.getJogador().x,
                cenaDoJogo.getJogador().y,
                cenaDoJogo.getJogador().getVida(),
                itens,
                progresso,
                bossDerrotado
        );
        this.checkpointsRegistrados.add(id);
    }

    private static class Checkpoint {
        private final String id;
        private final int cenarioIndex;
        private final int jogadorX;
        private final int jogadorY;
        private final int vida;
        private final Set<String> itensImportantes;
        private final SistemaHistoria.ProgressoHistoria progressoHistoria;
        private final boolean bossDerrotado;

        private Checkpoint(
                String id,
                int cenarioIndex,
                int jogadorX,
                int jogadorY,
                int vida,
                Set<String> itensImportantes,
                SistemaHistoria.ProgressoHistoria progressoHistoria,
                boolean bossDerrotado
        ) {
            this.id = id;
            this.cenarioIndex = cenarioIndex;
            this.jogadorX = jogadorX;
            this.jogadorY = jogadorY;
            this.vida = vida;
            this.itensImportantes = new LinkedHashSet<String>(itensImportantes);
            this.progressoHistoria = progressoHistoria;
            this.bossDerrotado = bossDerrotado;
        }
    }
}
