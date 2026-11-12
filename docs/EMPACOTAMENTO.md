# Executável Windows

As distribuições ficam somente em `dist/vX.Y.Z/`, com `Dead_Land.exe` diretamente
dentro de cada pasta. Os pacotes atual e anterior são `dist/v1.3.3/` e `dist/v1.3.2/`.
O Java está em `runtime/`, os recursos em `app/repos/` e os três slots em `save/`.
Mantenha essas pastas junto do executável. Para transportar o jogo, copie a pasta
da versão inteira. Não são gerados ZIPs.

A v1.2.1 incorpora a floresta enviada em `MapaFloresta.java`, com 50 × 25 tiles,
uma orbe e três poções. O nascimento após a queda continua em `(745, 200)` e o
retorno após o final em `(745, 335)`. As trilhas foram adaptadas para permanecerem
transitáveis, mantendo o visual de terra e as colisões dos demais cenários.

A v1.2.2 acrescenta os 16 inimigos definidos no novo arquivo da floresta:
seis esqueletos e dez slimes, com as posições, direções e raios de patrulha
enviados. O terreno, os itens e os pontos de nascimento permanecem os mesmos.

A v1.3.0 amplia a Vila de Ari para 50 × 25: casas variadas, armazém, guilda,
fonte central, caminhos curvos e fachadas na camada de objetos. A saída leste
mantém o caminho normal do castelo; a saída sudeste fica barricada. A vila é
segura, registra checkpoint e adapta posições de saves antigos quando necessário.
As árvores foram redesenhadas e os indicadores de interação ficam junto dos alvos.
Detalhes: [Mapa da vila](MAPA_VILA.md).

A v1.3.1 redesenha as fachadas com volumes medievais variados e pixel art mais
marcada: empenas, anexos laterais, portas e janelas arqueadas e letreiros pixelados.
Os caminhos usam cores de terra mais simples e pequenos degraus nas curvas.

A v1.3.2 aplica esse estilo às trilhas de todos os cenários e liga os acessos às
portas realmente desenhadas. As colisões da vila seguem as paredes de cada volume;
telhados e cantos vazios permitem passagem, com sobreposição visual correta.
A fonte central foi substituída por um lago de margem irregular e colisão da água.

A v1.3.3 padroniza as bordas dos lagos do caminho do castelo com o lago da primeira
vila: margem de grama e terra, água rasa e reflexos pixelados, sem emendas entre tiles.

## Próximas atualizações

Em Windows com JDK 21 ou superior, x64:

```powershell
.\rodar.ps1 -SomenteValidar
.\empacotar.ps1 -Versao 1.4.0
```

O empacotador aplica estas regras:

- A versão é obrigatória e deve ser maior que a última publicada.
- A pasta é criada automaticamente como `dist/v1.4.0/`.
- O pacote é montado em `build/` e só depois publicado em `dist/`.
- Cada slot leva a cópia mais recente encontrada nas versões mantidas, mesmo que
  a última jogatina tenha acontecido na versão anterior.
- Depois da publicação, permanecem somente as duas versões mais recentes,
  ordenadas numericamente. Ao publicar `v1.4.0`, ficam `v1.3.3` e `v1.4.0`.
- Não são criadas pastas `Atualizado`, `Versao-X.Y.Z`, pastas extras `Dead_Land`
  dentro da pasta da versão, nem arquivos ZIP.

A limpeza verifica o diretório, os marcadores do jogo e a versão do executável
antes de remover uma versão antiga. Versões já existentes não são sobrescritas.
Arquivos em uso são verificados antes da exclusão para evitar uma limpeza parcial.
Se uma versão antiga estiver aberta, salve e feche a partida para liberar a pasta.
Caso o pacote novo já tenha sido publicado, a conclusão pode ser retomada sem
recriá-lo, sincronizando novamente os slots antes da limpeza:

```powershell
Import-Module .\ferramentas\Distribuicao.psm1 -Force
$raizDistribuicao = Join-Path $PWD 'dist'
$versoesJogo = @(Get-VersoesJogo $raizDistribuicao)
Copy-SavesRecentes -Origens @($versoesJogo | ForEach-Object { Join-Path $_.Pasta 'save' }) -Destino (Join-Path $versoesJogo[0].Pasta 'save')
Remove-VersoesAntigasJogo $raizDistribuicao
```

Os saves usados ao executar os fontes continuam separados, em `save/` na raiz.
Na primeira distribuição, essa pasta serve como origem quando ainda não existem
versões anteriores.

Para indicar outra origem dos slots, use `-PastaSaves caminho\save`.
Para gerar deliberadamente um pacote sem progresso, use `-IncluirSaves:$false`.
`-Destino` muda a raiz de distribuição; o padrão permanece `dist`.

## Testar a organização

```powershell
.\ferramentas\validar-distribuicao.ps1
.\ferramentas\validar-distribuicao.ps1 -Empacotar
```

O primeiro comando testa a retenção numérica, a preservação dos slots e a recusa de
apagar pastas sem os marcadores do jogo. O segundo também gera um executável real
em uma distribuição temporária de teste, verifica que restaram apenas duas versões
e limpa o teste. Nenhum deles modifica as versões reais de `dist/`.

O launcher usa os caminhos relativos ao próprio pacote por meio de `$APPDIR`,
portanto renomear a pasta da versão não muda a localização dos recursos ou saves.
Referência: [jpackage no JDK 21](https://docs.oracle.com/en/java/javase/21/jpackage/support-application-features.html).
