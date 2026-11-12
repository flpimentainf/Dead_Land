# Vila de Ari — 50 × 25

Primeira área segura depois da floresta. O cenário continua usando o índice 3
(`BOSQUE_ARI`) para preservar saves, missões, Ari e a segunda memória.

O desenho segue o esboço: entrada a noroeste, casa pequena próxima da entrada,
armazém ao norte, guilda maior a nordeste, casas a oeste, sul, leste e sudeste.
A praça central tem um lago de margem irregular; os caminhos usam curvas pixeladas.
As construções são somente fachadas, sem interiores ou menus de loja nesta versão.

## Camadas

- `MapaVila.criarTerreno()`: grama e terra transitáveis, IDs 55 e 5.
- `MapaVila.criarObjetos()`: construções (66), lago (67), barricada (68) e árvores (69).
- `CONSTRUCOES`: sete objetos, cada um com posição, dimensões, tipo e estilo.
- `RenderizadorVila`: compõe terreno, fachadas, lago, árvores e placas.
- `RenderizadorFachadas`: desenhos originais na grade de 3 pixels, com telhados
  em dois planos, empenas, anexos, portas arqueadas, óculos, floreiras e toldos.
  Os letreiros usam letras de 5 × 7 pixels para permanecerem legíveis.
- `RenderizadorCaminhos`: curvas em degraus de 6 pixels e uma paleta curta,
  compartilhada por todas as trilhas dos cenários. Pisos internos e tapetes são preservados.
- `GeometriaConstrucao`: define os mesmos volumes, paredes e portas usados pelo
  desenho. Os acessos são ligados automaticamente às soleiras, inclusive nas
  entradas laterais da guilda. Telhados e cantos transparentes não bloqueiam passos.
- `GeometriaLago`: a mesma margem pixelada define a água visível e a área que bloqueia.
- `RenderizadorArvores`: troncos, galhos e copas maiores que um tile, reutilizados
  na vila e nos antigos IDs de vegetação. Não modifica as colisões da floresta.

As antigas casas repetidas foram retiradas da matriz do terreno. As fachadas
compartilham elementos de desenho; não são necessárias cópias de PNG para cada
janela, cor ou pedaço de parede. A pasta `repos/tiles` já contém apenas os quatro
arquivos legados ainda usados pelo jogo (1, 46, 63 e 64), sem duplicatas idênticas.

## Conexões e segurança

| Caminho | Destino / comportamento |
| --- | --- |
| Entrada a noroeste | Floresta; chegada à vila em `(50, 168)` |
| Saída 1, leste | Caminho normal até a entrada do castelo, cenário 4, em `(50, 240)` |
| Retorno do castelo | Borda leste da vila, altura `672` |
| Saída 2, sudeste | Barricada física na linha 23, colunas 35–39; sem transição |

A vila não possui inimigos e limpa projéteis hostis ao ser atualizada. Entrar
registra o checkpoint `bosque_ari`. Saves e checkpoints anteriores que coloquem o
jogador dentro de uma nova construção são ajustados para a entrada livre, mantendo
itens, lembranças, conquistas e estado narrativo.

A colisão usa o corpo completo em coordenadas de mundo, não apenas os quatro
tiles dos cantos. O corpo do jogador fica centralizado; movimentos, projéteis e
golpes consultam as mesmas paredes. Ao passar atrás de uma casa, jogador e NPCs
são recortados pela parte opaca da fachada. A região transparente continua visível.
Posições antigas encostadas em paredes nos outros cenários recebem o menor ajuste
livre próximo, quando necessário.

## Validação

`rodar.ps1 -SomenteValidar` inclui verificações de circulação até as fachadas,
acesso às soleiras, colisão das paredes, transparências dos telhados, margem seca
do lago, acesso a Ari e à orbe, conexões de ida e volta, bloqueio da saída 2, segurança,
checkpoint e compatibilidade de saves. As capturas de vila, guilda e saídas são
geradas em `build/hud` nas resoluções 1280×720, 1366×768 e 1920×1080.

Os indicadores de interação respeitam as regiões realmente ocupadas pelo HUD e
ficam a quatro pixels do alvo. A coleta usa o mesmo alcance da ação real.
