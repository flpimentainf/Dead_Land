package DEAD.LAND.ui;

import DEAD.LAND.core.AudioManager;
import DEAD.LAND.core.AudioManager.Canal;
import DEAD.LAND.core.GameState;
import DEAD.LAND.core.SaveManager;
import DEAD.LAND.story.SistemaHistoria;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class moldura extends JFrame {
    private static final String TELA_MENU = "menu";
    private static final String TELA_CONTEXTO = "contexto";
    private static final String TELA_CARREGAR = "carregar";
    private static final String TELA_CONFIGURACOES = "configuracoes";
    private static final String TELA_JOGO = "jogo";
    private static final String TELA_FINAL = "final";

    private static final String[][] SLIDES_CONTEXTO = {
        {
            "Entre dois mundos",
            "Após um acidente em uma noite de chuva, você desperta em um lugar estranho."
        },
        {
            "Dead Land",
            "Esta floresta aprisiona pessoas que estão entre acordar e desistir."
        },
        {
            "Sua memória",
            "Encontre as chaves, recupere suas lembranças e escolha se deseja voltar."
        }
    };

    private final CardLayout navegacao = new CardLayout();
    private final JPanel telas = new JPanel(navegacao);
    private final SaveManager saveManager = new SaveManager();
    private JPanel telaMenu;
    private JPanel telaContexto;
    private JPanel telaCarregar;
    private JPanel telaConfiguracoes;
    private JPanel telaFinal;
    private JPanel telaJogo;
    private panel painelCentro;
    private int slideAtual;

    public moldura() {
        setTitle("DEAD LAND");
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(768, 600));

        telaMenu = criarTelaInicial();
        telas.add(telaMenu, TELA_MENU);
        telaContexto = criarTelaContexto();
        telas.add(telaContexto, TELA_CONTEXTO);
        telaCarregar = criarTelaCarregar();
        telas.add(telaCarregar, TELA_CARREGAR);
        telaConfiguracoes = criarTelaConfiguracoes();
        telas.add(telaConfiguracoes, TELA_CONFIGURACOES);
        telaFinal = new JPanel();
        telas.add(telaFinal, TELA_FINAL);

        setContentPane(telas);
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        mostrarMenu();
    }

    private JPanel criarTelaInicial() {
        JPanel tela = criarPainelBase();
        montarTelaInicial(tela);
        return tela;
    }

    private void montarTelaInicial(JPanel tela) {
        tela.removeAll();
        tela.setLayout(new GridBagLayout());

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));

        JLabel titulo = criarTitulo("DEAD LAND", 56);
        JLabel subtitulo = criarTextoCentralizado(
                "Uma jornada entre memória, sonho e realidade.",
                20
        );
        JButton novoJogo = criarBotao("NOVO JOGO");
        JButton continuar = criarBotao("CONTINUAR");
        JButton carregar = criarBotao("CARREGAR");
        JButton configuracoes = criarBotao("CONFIGURAÇÕES");
        JButton sair = criarBotao("SAIR");

        continuar.setEnabled(saveManager.existeAlgumSave());
        carregar.setEnabled(saveManager.existeAlgumSave());

        novoJogo.addActionListener(e -> iniciarNovoJogo());
        continuar.addActionListener(e -> continuarJogo());
        carregar.addActionListener(e -> mostrarTelaCarregar());
        configuracoes.addActionListener(e -> {
            atualizarTelaConfiguracoes();
            navegacao.show(telas, TELA_CONFIGURACOES);
        });
        sair.addActionListener(e -> System.exit(0));

        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(subtitulo);
        conteudo.add(Box.createVerticalStrut(42));
        conteudo.add(novoJogo);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(continuar);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(carregar);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(configuracoes);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(sair);

        tela.add(conteudo);
        tela.revalidate();
        tela.repaint();
    }

    private JPanel criarTelaContexto() {
        JPanel tela = criarPainelBase();
        tela.setLayout(new BorderLayout(30, 30));
        tela.setBorder(BorderFactory.createEmptyBorder(80, 100, 60, 100));
        atualizarSlide(tela);
        return tela;
    }

    private JPanel criarTelaCarregar() {
        JPanel tela = criarPainelBase();
        montarTelaCarregar(tela);
        return tela;
    }

    private void montarTelaCarregar(JPanel tela) {
        tela.removeAll();
        tela.setLayout(new GridBagLayout());

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.add(criarTitulo("CARREGAR", 42));
        conteudo.add(Box.createVerticalStrut(26));

        for (int slot = 1; slot <= 3; slot++) {
            JButton botaoSlot = criarBotao(saveManager.getResumoSlot(slot), 520);
            botaoSlot.setEnabled(saveManager.existeSave(slot));
            final int slotEscolhido = slot;
            botaoSlot.addActionListener(e -> carregarSlot(slotEscolhido));
            conteudo.add(botaoSlot);
            conteudo.add(Box.createVerticalStrut(12));
        }

        JButton voltar = criarBotao("VOLTAR");
        voltar.addActionListener(e -> mostrarMenu());
        conteudo.add(Box.createVerticalStrut(18));
        conteudo.add(voltar);

        tela.add(conteudo);
        tela.revalidate();
        tela.repaint();
    }

    private JPanel criarTelaConfiguracoes() {
        JPanel tela = criarPainelBase();
        atualizarTelaConfiguracoes(tela);
        return tela;
    }

    private void atualizarTelaConfiguracoes() {
        atualizarTelaConfiguracoes(telaConfiguracoes);
    }

    private void atualizarTelaConfiguracoes(JPanel tela) {
        tela.removeAll();
        tela.setLayout(new GridBagLayout());

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.add(criarTitulo("CONFIGURAÇÕES", 42));
        conteudo.add(Box.createVerticalStrut(28));
        conteudo.add(criarLinhaVolume("MASTER", Canal.MASTER));
        conteudo.add(criarLinhaVolume("MÚSICA", Canal.MUSIC));
        conteudo.add(criarLinhaVolume("EFEITOS", Canal.SFX));
        conteudo.add(criarLinhaVolume("AMBIENTE", Canal.AMBIENT));
        conteudo.add(Box.createVerticalStrut(26));

        JButton voltar = criarBotao("VOLTAR");
        voltar.addActionListener(e -> mostrarMenu());
        conteudo.add(voltar);

        tela.add(conteudo);
        tela.revalidate();
        tela.repaint();
    }

    private JPanel criarLinhaVolume(String texto, Canal canal) {
        JPanel linha = new JPanel();
        linha.setOpaque(false);
        linha.setLayout(new BoxLayout(linha, BoxLayout.X_AXIS));
        linha.setMaximumSize(new Dimension(430, 42));

        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setPreferredSize(new Dimension(110, 32));

        JSlider slider = new JSlider(
                0,
                100,
                Math.round(AudioManager.getInstancia().getVolume(canal) * 100)
        );
        slider.setOpaque(false);
        slider.setPreferredSize(new Dimension(300, 32));
        slider.addChangeListener(e -> AudioManager.getInstancia().definirVolume(
                canal,
                ((JSlider) e.getSource()).getValue() / 100f
        ));

        linha.add(label);
        linha.add(slider);
        return linha;
    }

    private void atualizarSlide(JPanel tela) {
        tela.removeAll();

        String[] slide = SLIDES_CONTEXTO[slideAtual];
        JLabel titulo = criarTitulo(slide[0], 42);
        JLabel texto = criarTextoCentralizado(slide[1], 24);
        JButton continuar = criarBotao(
                slideAtual == SLIDES_CONTEXTO.length - 1 ? "COMEÇAR" : "CONTINUAR"
        );

        continuar.addActionListener(e -> {
            if (slideAtual < SLIDES_CONTEXTO.length - 1) {
                slideAtual++;
                atualizarSlide(telaContexto);
            } else {
                iniciarJogo();
            }
        });

        tela.add(titulo, BorderLayout.NORTH);
        tela.add(texto, BorderLayout.CENTER);

        JPanel rodape = new JPanel();
        rodape.setOpaque(false);
        rodape.add(continuar);
        tela.add(rodape, BorderLayout.SOUTH);

        tela.revalidate();
        tela.repaint();
    }

    private void iniciarNovoJogo() {
        if (saveManager.existeAlgumSave()) {
            int escolha = JOptionPane.showConfirmDialog(
                    this,
                    "Iniciar um novo jogo apagará os saves existentes.",
                    "Novo jogo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (escolha != JOptionPane.YES_OPTION) {
                return;
            }
            saveManager.apagarTodos();
        }

        iniciarContexto();
    }

    private void continuarJogo() {
        Optional<Integer> slot = saveManager.getSlotMaisRecente();
        if (!slot.isPresent()) {
            JOptionPane.showMessageDialog(this, "Nenhum save encontrado.");
            mostrarMenu();
            return;
        }

        carregarSlot(slot.get());
    }

    private void carregarSlot(int slot) {
        Optional<GameState> state = saveManager.carregar(slot);
        if (!state.isPresent()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este save não pôde ser carregado.",
                    "Save inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            mostrarTelaCarregar();
            return;
        }

        iniciarJogo(state.get());
    }

    private void mostrarTelaCarregar() {
        montarTelaCarregar(telaCarregar);
        navegacao.show(telas, TELA_CARREGAR);
    }

    private void iniciarContexto() {
        slideAtual = 0;
        atualizarSlide(telaContexto);
        navegacao.show(telas, TELA_CONTEXTO);
    }

    private void iniciarJogo() {
        iniciarJogo(null);
    }

    private void iniciarJogo(GameState state) {
        if (telaJogo != null) {
            telas.remove(telaJogo);
        }

        Inventario inventario = new Inventario();
        painelCentro = new panel("centro", inventario, this::mostrarTelaFinal, this::mostrarMenu);
        panel painelSul = new panel("sul", inventario);
        painelCentro.setPainelInventario(painelSul);

        telaJogo = new JPanel(new BorderLayout());
        telaJogo.add(painelCentro, BorderLayout.CENTER);
        telaJogo.add(painelSul, BorderLayout.SOUTH);
        telas.add(telaJogo, TELA_JOGO);

        if (state != null) {
            painelCentro.aplicarGameState(state);
        }

        navegacao.show(telas, TELA_JOGO);
        painelCentro.requestFocusInWindow();
    }

    private void mostrarTelaFinal(SistemaHistoria.Estado finalEscolhido) {
        painelCentro.pausarJogo();
        telaFinal.removeAll();
        telaFinal.setLayout(new GridBagLayout());
        telaFinal.setBackground(new Color(12, 14, 20));

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));

        boolean voltouAoMundoReal = finalEscolhido == SistemaHistoria.Estado.FINAL_ACORDAR;
        String titulo = voltouAoMundoReal
                ? "VOCÊ ACORDOU"
                : finalEscolhido == SistemaHistoria.Estado.FINAL_SECRETO
                        ? "O CICLO CONTINUA"
                        : "VOCÊ FICOU EM DEAD LAND";
        String texto = voltouAoMundoReal
                ? "A história chegou ao fim."
                : "A floresta ainda se lembra de você.";

        conteudo.add(criarTitulo(titulo, 42));
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(criarTextoCentralizado(texto, 22));
        conteudo.add(Box.createVerticalStrut(36));
        conteudo.add(criarTextoCentralizado(
                "Obrigado por jogar Dead Land",
                18
        ));
        conteudo.add(Box.createVerticalStrut(8));
        conteudo.add(criarTextoCentralizado(
                "Criado por:<br>"
                        + "Francisco Lara Pimenta<br>"
                        + "Christian Leineker<br>"
                        + "Kauâ Lizarte<br>"
                        + "Guilherme Machado",
                16
        ));
        conteudo.add(Box.createVerticalStrut(40));

        JButton continuar = criarBotao(voltouAoMundoReal ? "MENU INICIAL" : "CONTINUAR");
        continuar.addActionListener(e -> {
            if (voltouAoMundoReal) {
                mostrarMenu();
            } else {
                navegacao.show(telas, TELA_JOGO);
                painelCentro.continuarAposFinalNaFloresta();
            }
        });
        conteudo.add(continuar);

        telaFinal.add(conteudo);
        telaFinal.revalidate();
        telaFinal.repaint();
        navegacao.show(telas, TELA_FINAL);
    }

    private void mostrarMenu() {
        if (painelCentro != null) {
            painelCentro.pausarJogo();
        }
        montarTelaInicial(telaMenu);
        navegacao.show(telas, TELA_MENU);
    }

    private JPanel criarPainelBase() {
        JPanel painel = new JPanel();
        painel.setBackground(new Color(12, 14, 20));
        return painel;
    }

    private JLabel criarTitulo(String texto, int tamanho) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setForeground(new Color(210, 55, 55));
        label.setFont(new Font("Serif", Font.BOLD, tamanho));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel criarTextoCentralizado(String texto, int tamanho) {
        JLabel label = new JLabel(
                "<html><div style='text-align:center; width:650px'>" + texto + "</div></html>",
                SwingConstants.CENTER
        );
        label.setForeground(new Color(225, 225, 225));
        label.setFont(new Font("SansSerif", Font.PLAIN, tamanho));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton criarBotao(String texto) {
        return criarBotao(texto, 240);
    }

    private JButton criarBotao(String texto, int largura) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 18));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(70, 30, 35));
        botao.setFocusPainted(false);
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setPreferredSize(new Dimension(largura, 48));
        botao.setMaximumSize(new Dimension(largura, 48));
        botao.setMargin(new Insets(10, 25, 10, 25));
        return botao;
    }
}
