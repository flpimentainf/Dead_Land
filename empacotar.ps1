param(
    [string]$Destino = 'dist',
    [switch]$IncluirSaves = $true,
    [string]$PastaSaves = '',
    [Parameter(Mandatory)]
    [ValidatePattern('^\d+\.\d+\.\d+$')]
    [string]$Versao
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
Import-Module (Join-Path $PSScriptRoot 'ferramentas\Distribuicao.psm1') -Force
$raizProjeto = [IO.Path]::GetFullPath($PSScriptRoot)
$prefixoProjeto = $raizProjeto.TrimEnd('\') + '\'
$pastaDestino = [IO.Path]::GetFullPath((Join-Path $raizProjeto $Destino)).TrimEnd('\')
if (-not $pastaDestino.StartsWith($prefixoProjeto, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'O destino precisa ficar dentro do projeto.'
}
New-Item -ItemType Directory -Path $pastaDestino -Force | Out-Null
$pastaApp = Join-Path $pastaDestino "v$Versao"
$versoesExistentes = @(Get-VersoesJogo -Raiz $pastaDestino)
foreach ($existente in $versoesExistentes) { Assert-PastaVersaoJogo -Raiz $pastaDestino -Pasta $existente.Pasta }
if (Test-Path -LiteralPath $pastaApp) { throw "A versao v$Versao ja existe." }
if ($versoesExistentes.Count -and [version]$Versao -le $versoesExistentes[0].Numero) {
    throw 'A nova versao precisa ser maior que a mais recente.'
}
$origensSaves = @()
if ($IncluirSaves) {
    if ($PastaSaves) {
        $origem = [IO.Path]::GetFullPath((Join-Path $raizProjeto $PastaSaves))
        if (-not (Test-Path -LiteralPath $origem -PathType Container)) { throw "A pasta de saves nao existe: $origem" }
        $origensSaves = @($origem)
    } else {
        $origensSaves = @($versoesExistentes | ForEach-Object { Join-Path $_.Pasta 'save' })
        if (-not $origensSaves.Count) { $origensSaves = @((Join-Path $raizProjeto 'save')) }
    }
}
$jdkPasta = Split-Path -Parent (Get-Command javac.exe -CommandType Application | Select-Object -First 1).Source
foreach ($ferramenta in @('javac.exe', 'java.exe', 'jar.exe', 'jpackage.exe')) {
    if (-not (Test-Path -LiteralPath (Join-Path $jdkPasta $ferramenta))) { throw "Ferramenta ausente: $ferramenta" }
}
$versaoCompilador = (& (Join-Path $jdkPasta 'javac.exe') -version 2>&1 | Out-String).Trim()
if ($LASTEXITCODE -ne 0 -or $versaoCompilador -notmatch 'javac\s+(\d+)' -or [int]$Matches[1] -lt 21) {
    throw 'E necessario um JDK 21 ou superior.'
}
$arquitetura = & (Join-Path $jdkPasta 'java.exe') -XshowSettings:properties -version 2>&1 | Out-String
if ($arquitetura -notmatch 'os.arch\s*=\s*(amd64|x86_64)') { throw 'Use um JDK Windows x64 para gerar este pacote.' }

$pastaBuild = Join-Path $raizProjeto ('build\pacote-' + [Guid]::NewGuid().ToString('N'))
$classes = Join-Path $pastaBuild 'classes'
$entrada = Join-Path $pastaBuild 'entrada'
$saida = Join-Path $pastaBuild 'saida'
$pacotePronto = Join-Path $saida 'Dead_Land'
New-Item -ItemType Directory -Path $classes, $entrada, $pastaDestino -Force | Out-Null
Push-Location -LiteralPath $raizProjeto
try {
    Write-Host "Compilando com $versaoCompilador..."
    $fontes = @(Get-ChildItem -LiteralPath 'src' -Recurse -Filter '*.java' -File | Sort-Object FullName | ForEach-Object FullName)
    & (Join-Path $jdkPasta 'javac.exe') --release 21 -encoding UTF-8 -d $classes @fontes
    if ($LASTEXITCODE -ne 0) { throw 'A compilacao falhou.' }
    & (Join-Path $jdkPasta 'jar.exe') --create --file (Join-Path $entrada 'Dead_Land.jar') --main-class DEAD.LAND.main -C $classes .
    if ($LASTEXITCODE -ne 0) { throw 'Nao foi possivel criar o JAR.' }
    Copy-Item -LiteralPath (Join-Path $raizProjeto 'repos') -Destination $entrada -Recurse

    Write-Host 'Gerando Dead_Land.exe e o Java incluido...'
    & (Join-Path $jdkPasta 'jpackage.exe') --type app-image --name Dead_Land `
        --app-version $Versao --vendor 'Dead Land' --description 'Dead Land' `
        --input $entrada --dest $saida --main-jar Dead_Land.jar --main-class DEAD.LAND.main `
        --add-modules java.desktop --jlink-options '--strip-debug --no-header-files --no-man-pages --compress=2' `
        --java-options '-Ddeadland.recursos=$APPDIR' --java-options '-Ddeadland.save=$APPDIR/../save' `
        --java-options '-Dfile.encoding=UTF-8'
    if ($LASTEXITCODE -ne 0) { throw 'O empacotamento falhou.' }
    if ($IncluirSaves) {
        Copy-SavesRecentes -Origens $origensSaves -Destino (Join-Path $pacotePronto 'save')
    }
    @'
DEAD LAND - Windows x64

Abra Dead_Land.exe para jogar. O Java ja esta incluido.
Mantenha as pastas app e runtime junto do executavel.
Os tres slots ficam na pasta save, ao lado do executavel.
Para transportar o jogo, copie a pasta da versao inteira.

Controles: WASD mover | E interagir | ESC pausa | Q arma | H cura
Inventario: roda do mouse sobre o rodape ou botoes < e >.
'@ | Set-Content -LiteralPath (Join-Path $pacotePronto 'LEIA-ME.txt') -Encoding utf8
    $origemAbsoluta = [IO.Path]::GetFullPath((Resolve-Path -LiteralPath $pacotePronto).Path)
    $prefixoBuild = [IO.Path]::GetFullPath($pastaBuild).TrimEnd('\') + '\'
    if (-not $origemAbsoluta.StartsWith($prefixoBuild, [StringComparison]::OrdinalIgnoreCase) -or
        -not [IO.Path]::GetDirectoryName($pastaApp).Equals($pastaDestino, [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Caminho inesperado ao publicar o pacote.'
    }
    $itensPacote = @((Get-Item -LiteralPath $origemAbsoluta -Force)) + @(Get-ChildItem -LiteralPath $origemAbsoluta -Recurse -Force)
    if (@($itensPacote | Where-Object { $_.Attributes -band [IO.FileAttributes]::ReparsePoint }).Count) { throw 'Link inesperado no pacote.' }
    Move-Item -LiteralPath $origemAbsoluta -Destination $pastaApp
    Assert-PastaVersaoJogo -Raiz $pastaDestino -Pasta $pastaApp
    Remove-VersoesAntigasJogo -Raiz $pastaDestino
    Write-Host "Executavel: $(Join-Path $pastaApp 'Dead_Land.exe')"
    Write-Host 'Mantidas somente as duas versoes mais recentes, sem ZIP.'
} finally {
    Pop-Location
    $resolvido = [IO.Path]::GetFullPath((Resolve-Path -LiteralPath $pastaBuild).Path)
    $prefixoBuild = [IO.Path]::GetFullPath((Join-Path $raizProjeto 'build')).TrimEnd('\') + '\'
    if (-not $resolvido.StartsWith($prefixoBuild, [StringComparison]::OrdinalIgnoreCase)) { throw 'Diretorio temporario inesperado.' }
    $itens = @((Get-Item -LiteralPath $resolvido -Force)) + @(Get-ChildItem -LiteralPath $resolvido -Recurse -Force)
    if (@($itens | Where-Object { $_.Attributes -band [IO.FileAttributes]::ReparsePoint }).Count -gt 0) { throw 'Link inesperado na compilacao temporaria.' }
    Remove-Item -LiteralPath $resolvido -Recurse -Force
}
