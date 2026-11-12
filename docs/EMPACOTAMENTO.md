# Executável Windows

As distribuições ficam somente em `dist/vX.Y.Z/`, com `Dead_Land.exe` diretamente
dentro de cada pasta. Hoje estão mantidas `dist/v1.2.1/` e `dist/v1.2.2/`.
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

## Próximas atualizações

Em Windows com JDK 21 ou superior, x64:

```powershell
.\rodar.ps1 -SomenteValidar
.\empacotar.ps1 -Versao 1.3.0
```

O empacotador aplica estas regras:

- A versão é obrigatória e deve ser maior que a última publicada.
- A pasta é criada automaticamente como `dist/v1.3.0/`.
- O pacote é montado em `build/` e só depois publicado em `dist/`.
- Cada slot leva a cópia mais recente encontrada nas versões mantidas, mesmo que
  a última jogatina tenha acontecido na versão anterior.
- Depois da publicação, permanecem somente as duas versões mais recentes,
  ordenadas numericamente. Ao publicar `v1.3.0`, ficam `v1.2.2` e `v1.3.0`.
- Não são criadas pastas `Atualizado`, `Versao-X.Y.Z`, pastas extras `Dead_Land`
  dentro da pasta da versão, nem arquivos ZIP.

A limpeza verifica o diretório, os marcadores do jogo e a versão do executável
antes de remover uma versão antiga. Versões já existentes não são sobrescritas.
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
