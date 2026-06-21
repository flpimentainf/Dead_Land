package DEAD.LAND.core;

import DEAD.LAND.input.escutadorTeclado;
import DEAD.LAND.ui.panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class gameLoop implements ActionListener {
	private static final int FPS = 60;
	private static final int FPS_SPRITES = 15;
	private static final int INTERVALO_LOOP_MS = 1000 / FPS;
	private static final int ATUALIZACOES_POR_SPRITE = FPS / FPS_SPRITES;

	private final Timer temporizador;
	private final ControladorMovimento controladorMovimento;
	private final ControladorQueda controladorQueda;
	private final ControladorInteracao controladorInteracao;
	private final ControladorItens controladorItens;
	private final ControladorFlechas controladorFlechas;
	private final panel cenaDoJogo;
	private final escutadorTeclado teclado;

	private int atualizacoesDesdeUltimoSprite;

    public gameLoop(panel P, escutadorTeclado ET) {
		this.cenaDoJogo = P;
		this.teclado = ET;
		this.controladorMovimento = new ControladorMovimento();
		this.controladorQueda = new ControladorQueda();
		this.controladorInteracao = new ControladorInteracao();
		this.controladorItens = new ControladorItens();
		this.controladorFlechas = P.getControladorFlechas();
		this.temporizador = new Timer(INTERVALO_LOOP_MS, this);
		this.temporizador.setCoalesce(true);
    }

	public void iniciar() {
		if (!this.temporizador.isRunning()) {
			this.temporizador.start();
		}
	}

	public void parar() {
		this.temporizador.stop();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		atualizarJogo();
		atualizarSpriteQuandoNecessario();
		this.cenaDoJogo.repaint();
	}

	private void atualizarJogo() {
		if (this.cenaDoJogo.getCenario() == null) {
			return;
		}

		if (this.cenaDoJogo.getHistoria() != null) {
			this.cenaDoJogo.getHistoria().atualizar(this.cenaDoJogo, this.teclado);

			if (this.cenaDoJogo.getHistoria().bloqueiaControleDoJogador()) {
				this.controladorInteracao.resetar();
				return;
			}
		}

		if (this.controladorQueda.atualizar(this.cenaDoJogo)) {
			this.controladorInteracao.resetar();
			return;
		}

		this.controladorMovimento.atualizar(this.cenaDoJogo, this.teclado);
		this.controladorInteracao.atualizar(this.cenaDoJogo, this.teclado);
		this.controladorItens.atualizar(this.cenaDoJogo);

		if (this.controladorFlechas != null) {
			this.controladorFlechas.atualizar(this.cenaDoJogo, this.teclado);
		}
	}

	private void atualizarSpriteQuandoNecessario() {
		if (this.cenaDoJogo.getHistoria() != null
				&& this.cenaDoJogo.getHistoria().bloqueiaControleDoJogador()) {
			return;
		}

		this.atualizacoesDesdeUltimoSprite++;

		if (this.atualizacoesDesdeUltimoSprite < ATUALIZACOES_POR_SPRITE) {
			return;
		}

		this.atualizacoesDesdeUltimoSprite = 0;

		this.cenaDoJogo.getJogador().atualizarSprite(
				this.teclado.movePraEsq,
				this.teclado.movePraCima,
				this.teclado.movePraDir,
				this.teclado.movePraBaixo
		);
	}
}
