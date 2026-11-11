# Interface de Dead Land

O mundo continua com resolução lógica de 768 × 480. O rodapé usa 768 × 104.
`PainelPartida` calcula um viewport de 768 × 584 para os dois componentes, com margens
pretas quando a proporção da janela difere. Os buffers usam interpolação por vizinho
mais próximo; a janela não estica os eixos independentemente.

## Organização

- `LayoutHUD`: regiões do rodapé, limites do chefe, viewport e prioridade visual.
- `EstiloHUD`: cores, fontes e barras de status.
- `TextoHUD`: quebra de texto por `FontMetrics`, incluindo palavras extensas.
- `InterfaceHistoria`: card de missão de até 280 × 72, diálogos baixos e paginação.
- `NotificacoesObjetivos`: fila de avisos, 3,5 segundos de tempo visível e transições.
- `LayoutMenuPausa`: áreas de clique do menu, incluindo a consulta de objetivos.

Morte, pausa e encerramento têm prioridade sobre os componentes de exploração.
Diálogos ocultam objetivos e avisos; a arena reserva o topo para o chefe. Durante o
combate, as missões continuam disponíveis em **ESC > MISSÕES**. O painel retorna
após o confronto. As notificações compactas de 224 × 52 ficam no canto superior direito
(abaixo da barra durante o chefe), e o feedback de
ações no canto inferior esquerdo.

As informações e conclusões vêm de `SistemaHistoria`, seus estados e eventos
existentes. A fila de notificações é transitória e não altera o formato dos saves.
Carregar uma partida ou restaurar um checkpoint descarta avisos anteriores.

## Controles

- **ESC > MISSÕES**: consultar missão principal e secundária, com estado textual.
- **ESC > CONQUISTAS**: árvore de marcos da partida, com requisitos, progresso e ícones
  pretos/coloridos. Disponível exclusivamente no menu de pausa durante o jogo.
- **W/S** ou roda do mouse: rolar objetivos extensos; **ENTER/ESC** ou **VOLTAR**: sair.
- **Roda do mouse sobre o rodapé** ou botões **< / >**: mudar a página de itens.
- A arma equipada fica fixa à direita, com borda dupla e marcador **ATIVA**.
- **Q** continua alternando armas; **H** continua usando cura.
- **ENTER/E** avança as páginas de diálogos antes de encerrar ou confirmar a escolha.
- **W/S** seleciona a resposta em qualquer página em que as escolhas estejam visíveis;
  avançar o texto mantém a seleção. Segurar **ENTER/E** não confirma duas vezes.

Durante mensagens e escolhas, a caixa preserva o topo e a largura e se estende
104 pixels lógicos para baixo, aproveitando o rodapé. Vida, estamina e inventário
ficam ocultos durante a conversa e voltam automaticamente ao fechá-la. A câmera e
a área ocupada pelo mundo permanecem iguais. A altura extra comporta mais linhas
por página, mantendo W/S e ENTER/E.

## Validação

Executar com JDK 21 ou superior:

```powershell
.\rodar.ps1 -SomenteValidar
```

O comando compila, executa `ValidacaoTecnica`, `ValidacaoSlots` e `ValidacaoHUD` e
encerra sem abrir uma partida. `-Validar` executa as mesmas verificações e depois
abre o jogo. Os testes de slots usam diretórios temporários.

`ValidacaoHUD` cobre:

- Prioridade do chefe, retorno do objetivo e preenchimento da barra sem sobreposição.
- Quebra de texto por métricas, palavras extensas e paginação de diálogos.
- Conclusão de etapa, novo objetivo, duração e suspensão das notificações.
- Estado concluído da missão secundária a partir do evento existente.
- Consulta de textos extensos com rolagem real por eventos de mouse.
- Inventário de 30 itens: regiões separadas e acesso a todas as páginas.
- Mesma escala nos dois eixos e ausência de interseção entre mundo e rodapé.
- Escurecimento do HUD inferior durante a pausa.
- Feedback ao tentar usar uma poção inexistente sem alterar o inventário.

A validação visual renderiza os componentes reais em **1280×720**, **1366×768** e
**1920×1080**. São geradas 30 capturas em `build/hud/`: exploração, segunda área do
castelo, chefe, pausa, missões, diálogo narrativo, conversa com NPC, inventário cheio, interação com a cama e morte
em cada resolução, além de uma prévia do aviso compacto de missão.
Essa pasta é ignorada pelo Git. Os cenários são preparados sem gravar no diretório
de saves da partida.
