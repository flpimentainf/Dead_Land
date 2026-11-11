# Conquistas da partida

Disponíveis somente durante o jogo em **ESC > CONQUISTAS**. O menu inicial não
oferece acesso ao painel. A tela pausa o combate e mantém o visual escurecido do HUD.

| Trilha | Conquista | Requisito |
| --- | --- | --- |
| Memórias | Eco do passado | Coletar a primeira orbe de memória |
| Memórias | Memória fragmentada | Coletar três orbes diferentes |
| Memórias | Mestre das lembranças | Coletar todas as cinco orbes |
| Armas | Mira certeira | Coletar o arco |
| Armas | Lâmina desbravadora | Coletar a espada |
| Cura | Essência vital | Obter a primeira poção de cura |

Cada nó bloqueado mostra uma silhueta preta e seu requisito/progresso. Ao desbloquear,
o ícone recupera as cores e recebe a indicação `[OK]`. Os marcos de memória e os de
armas são ligados visualmente. A conquista da espada depende da coleta da espada;
a ordem em que as duas armas são encontradas não impede os desbloqueios.

**Mouse:** passar ou clicar em um nó revela seus detalhes; roda do mouse percorre
os nós. **WASD:** navegar pelas trilhas. **ENTER**, **ESC** ou **VOLTAR** retornam ao
menu de pausa. O jogo só retoma ao sair da pausa.

O desbloqueio apresenta um aviso curto no topo direito, usando o ícone colorido.
Diálogos, pausas e morte suspendem o tempo do aviso. A notificação de conquista tem
prioridade sobre a notificação de missão; a missão aguarda sem perder seu tempo.
Durante o chefe, os avisos ficam abaixo da barra de vida.

## Persistência

As conquistas pertencem ao slot e são gravadas junto ao autosave da coleta. Gastar
a poção, alternar a arma ou morrer não remove conquistas. Carregar outro slot limpa
o estado da partida anterior e os avisos pendentes.

Saves antigos são compatíveis: memórias e itens já registrados recuperam os marcos
correspondentes sem repetir notificações. A recuperação da primeira poção depende
do item ou quantidade ainda registrado no save antigo. As novas partidas guardam
os IDs permanentes na propriedade `conquistas` dos arquivos de save.

## Estrutura e verificação

- `SistemaConquistas`: catálogo, condições, progresso e histórico permanente.
- `InterfaceConquistas`, `LayoutConquistas` e `IconesConquistas`: árvore, navegação e ícones.
- `NotificacoesConquistas`: fila e duração dos avisos.
- `ValidacaoConquistas`: coleta real, marcos de 1/3/5 orbes, armas, cura, autosave,
  morte, isolamento dos slots e migração.
- `ValidacaoInterfaceConquistas`: acesso pela pausa, mouse/teclado, cores e silhuetas,
  duração dos avisos e capturas em 1280×720, 1366×768 e 1920×1080.

Executar `./rodar.ps1 -SomenteValidar`. As capturas ficam em `build/hud/conquistas-*`.
Os testes usam saves temporários. Para acrescentar conquistas, adicionar a definição
e condição no catálogo, posicionar o nó em `LayoutConquistas` e ajustar as ligações
e a navegação do painel.
