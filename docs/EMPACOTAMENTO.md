# Executável Windows

O pacote portátil fica em `dist/Dead_Land/`. Abra `Dead_Land.exe`.
O Java x64 necessário já está em `runtime/`, e os recursos ficam em `app/repos/`.
Mantenha essas duas pastas junto do executável. O ZIP deve ser extraído por inteiro.

Os slots do executável ficam em `dist/Dead_Land/save/`. Eles são independentes dos
slots usados ao executar os fontes. O pacote pessoal foi gerado com uma cópia dos
saves existentes, sem modificar os originais.

## Gerar novamente

Em Windows com JDK 21 ou superior, x64:

```powershell
.\rodar.ps1 -SomenteValidar
.\empacotar.ps1 -Destino dist\nova-versao -IncluirSaves
```

Omita `-IncluirSaves` para gerar uma distribuição sem progresso pessoal.
Para levar os slots de um executável anterior, use `-PastaSaves dist\Dead_Land\save`.
`-Versao 1.1.0` define a versão do novo executável. A versão 1.1.0 foi gerada em
`dist/Versao-1.1.0/`, incluindo os slots do pacote anterior.
O script recusa substituir uma pasta de jogo ou ZIP já existente para preservar os
slots do pacote anterior. Ao atualizar, copie a pasta `save` do pacote que estava
usando para o novo pacote.

O empacotamento compila os fontes, cria o JAR, inclui `repos`, gera um launcher
nativo com `jpackage --type app-image` e um runtime com `java.desktop`, e compacta
a pasta inteira em `Dead_Land-Windows-x64.zip`. Não exige instalação nem Java no
computador de destino. A arquitetura de destino é Windows x64.

`CaminhosJogo` resolve os recursos e os saves pelas propriedades configuradas no
launcher, usando o `$APPDIR` do pacote. Isso permite abrir por atalho ou com outra
pasta de trabalho. Executar pelos fontes mantém os diretórios locais `repos` e
`save`.

Referência do empacotador e do `$APPDIR`:
[Documentação do jpackage no JDK 21](https://docs.oracle.com/en/java/javase/21/jpackage/support-application-features.html).

## Verificação realizada

- Suítes `ValidacaoTecnica`, `ValidacaoSlots` e `ValidacaoHUD` aprovadas.
- Java incluído carregou as 170 imagens do pacote, com verificação dos pixels.
- Renderização e gravação/leitura de save temporário usando o runtime incluído.
- Teste em uma pasta de trabalho externa com espaços no caminho.
- Launcher nativo abriu a janela `DEAD LAND`, respondeu e encerrou normalmente.
- Hash do save original e da cópia preservado após os testes.
