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
	private final ControladorInimigos controladorInimigos;
	private final ControladorNPCs controladorNPCs;
	private final verificadorDeColisao verificadorEfeitos;
	private final panel cenaDoJogo;
	private final escutadorTeclado teclado;

	private int atualizacoesDesdeUltimoSprite;
	private boolean usarItemPressionado;
	private int vidaJogadorAnterior = -1;
	private String musicaAtual = "";

    public gameLoop(panel P, escutadorTeclado ET) {
		this.cenaDoJogo = P;
		this.teclado = ET;
		this.controladorMovimento = new ControladorMovimento();
		this.controladorQueda = new ControladorQueda();
		this.controladorInteracao = new ControladorInteracao();
		this.controladorItens = new ControladorItens();
		this.controladorFlechas = P.getControladorFlechas();
		this.controladorInimigos = new ControladorInimigos();
		this.controladorNPCs = new ControladorNPCs();
		this.verificadorEfeitos = new verificadorDeColisao();
		this.temporizador = new Timer(INTERVALO_LOOP_MS, this);
		this.temporizador.setCoalesce(true);
    }

	public ControladorItens getControladorItens() { return controladorItens; }
	public ControladorInimigos getControladorInimigos() { return controladorInimigos; }
	public ControladorNPCs getControladorNPCs() { return controladorNPCs; }

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
		registrarFeedbackDanoJogador();
		atualizarSpriteQuandoNecessario();
		this.cenaDoJogo.repaint();
	}

	private void atualizarJogo() {
		if (this.cenaDoJogo.getCenario() == null) {
			return;
		}

		if (this.cenaDoJogo.getSistemaMorte() != null
				&& this.cenaDoJogo.getSistemaMorte().atualizar(
						this.cenaDoJogo,
						this.teclado,
						this.cenaDoJogo.getSistemaCheckpoint())) {
			this.controladorInteracao.resetar();
			return;
		}

		this.cenaDoJogo.getJogador().atualizarEfeitos(
				this.cenaDoJogo.getCenario(),
				this.verificadorEfeitos
		);
		atualizarSistemasDeFeedback();

		if (this.cenaDoJogo.atualizarMenuPausa(this.teclado)) {
			this.controladorInteracao.resetar();
			return;
		}

		if (this.cenaDoJogo.getHistoria() != null) {
			this.cenaDoJogo.getHistoria().atualizar(this.cenaDoJogo, this.teclado);
			atualizarCheckpointsAutomaticos();

			if (this.cenaDoJogo.getHistoria().bloqueiaControleDoJogador()) {
				this.controladorInteracao.resetar();
				return;
			}
		}

		if (this.controladorQueda.atualizar(this.cenaDoJogo)) {
			this.controladorInteracao.resetar();
			atualizarCheckpointsAutomaticos();
			return;
		}

		this.controladorMovimento.atualizar(this.cenaDoJogo, this.teclado);
		this.controladorNPCs.atualizar(this.cenaDoJogo);
		this.controladorInteracao.atualizar(this.cenaDoJogo, this.teclado);
		atualizarUsoDeConsumivel();
		this.controladorItens.atualizar(this.cenaDoJogo, this.teclado);

		if (this.controladorFlechas != null) {
			this.controladorFlechas.atualizar(this.cenaDoJogo, this.teclado);
		}
		this.controladorInimigos.atualizar(this.cenaDoJogo, this.controladorFlechas);
		atualizarAudioContextual();
		atualizarCheckpointsAutomaticos();
	}

	private void atualizarCheckpointsAutomaticos() {
		if (this.cenaDoJogo.getSistemaCheckpoint() != null) {
			this.cenaDoJogo.getSistemaCheckpoint().avaliarCheckpointAutomatico(this.cenaDoJogo);
		}
	}

	private void atualizarSistemasDeFeedback() {
		if (this.cenaDoJogo.getFeedback() != null) {
			this.cenaDoJogo.getFeedback().atualizar();
		}

		if (this.cenaDoJogo.getSistemaVontade() == null) {
			return;
		}

		this.cenaDoJogo.getSistemaVontade().atualizar();
		if (this.cenaDoJogo.getSistemaVontade().consumirDesistenciaPendente()
				&& this.cenaDoJogo.getSistemaCheckpoint() != null) {
			this.cenaDoJogo.getSistemaCheckpoint().restaurar(this.cenaDoJogo);
			this.cenaDoJogo.getSistemaVontade().recuperar(20);
			if (this.cenaDoJogo.getFeedback() != null) {
				this.cenaDoJogo.getFeedback().mostrarMensagemCentro("Sua vontade quase se perdeu");
			}
		}
	}

	private void atualizarUsoDeConsumivel() {
		if (!this.teclado.usarItem) {
			this.usarItemPressionado = false;
			return;
		}

		if (this.usarItemPressionado) {
			return;
		}

		this.usarItemPressionado = true;
		if (this.cenaDoJogo.getInventario().usarCura(this.cenaDoJogo.getJogador())) {
			this.cenaDoJogo.atualizarInventario();
			if (this.cenaDoJogo.getFeedback() != null) {
				this.cenaDoJogo.getFeedback().mostrarMensagemCentro("Fragmento de Memoria usado");
			}
			AudioManager.getInstancia().tocarEfeito("cura");
		}
	}

	private void registrarFeedbackDanoJogador() {
		if (this.cenaDoJogo.getJogador() == null) {
			return;
		}

		int vidaAtual = this.cenaDoJogo.getJogador().getVida();
		if (this.vidaJogadorAnterior >= 0 && vidaAtual < this.vidaJogadorAnterior) {
			AudioManager.getInstancia().tocarEfeito("jogador_dano");
			if (this.cenaDoJogo.getCamera() != null) {
				this.cenaDoJogo.getCamera().iniciarTremor(3, 8);
			}
		}
		this.vidaJogadorAnterior = vidaAtual;
	}

	private void atualizarAudioContextual() {
		String novaMusica;
		if (this.cenaDoJogo.getCenario().getCenarioAtualIndex() == 7) {
			novaMusica = "boss";
		} else if (this.cenaDoJogo.existeCombateAtivo()) {
			novaMusica = "tensao";
		} else if (this.cenaDoJogo.getCenario().getCenarioAtualIndex() <= 1) {
			novaMusica = "sonho";
		} else {
			novaMusica = "floresta";
		}

		if (!novaMusica.equals(this.musicaAtual)) {
			this.musicaAtual = novaMusica;
			AudioManager.getInstancia().trocarMusica(novaMusica);
		}
	}

	private void atualizarSpriteQuandoNecessario() {
		if (this.cenaDoJogo.isMenuPausaAberto()) {
			return;
		}

		if (this.cenaDoJogo.getSistemaMorte() != null
				&& this.cenaDoJogo.getSistemaMorte().bloqueiaControle()) {
			return;
		}

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
