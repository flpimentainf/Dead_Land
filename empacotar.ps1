param(
    [string]$Destino = 'dist',
    [switch]$IncluirSaves
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$raizProjeto = [IO.Path]::GetFullPath($PSScriptRoot)
$prefixoProjeto = $raizProjeto.TrimEnd('\') + '\'
$pastaDestino = [IO.Path]::GetFullPath((Join-Path $raizProjeto $Destino))
if (-not $pastaDestino.StartsWith($prefixoProjeto, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'O destino precisa ficar dentro do projeto.'
}
$pastaApp = Join-Path $pastaDestino 'Dead_Land'
$arquivoZip = Join-Path $pastaDestino 'Dead_Land-Windows-x64.zip'
if ((Test-Path -LiteralPath $pastaApp) -or (Test-Path -LiteralPath $arquivoZip)) {
    throw 'O pacote ja existe. Use -Destino com outra pasta para preservar o pacote e seus saves.'
}
$jdkPasta = Split-Path -Parent (Get-Command javac.exe -CommandType Application | Select-Object -First 1).Source
foreach ($ferramenta in @('javac.exe', 'java.exe', 'jar.exe', 'jpackage.exe')) {
    if (-not (Test-Path -LiteralPath (Join-Path $jdkPasta $ferramenta))) { throw "Ferramenta ausente: $ferramenta" }
}
$versao = (& (Join-Path $jdkPasta 'javac.exe') -version 2>&1 | Out-String).Trim()
if ($LASTEXITCODE -ne 0 -or $versao -notmatch 'javac\s+(\d+)' -or [int]$Matches[1] -lt 21) {
    throw 'E necessario um JDK 21 ou superior.'
}
$arquitetura = & (Join-Path $jdkPasta 'java.exe') -XshowSettings:properties -version 2>&1 | Out-String
if ($arquitetura -notmatch 'os.arch\s*=\s*(amd64|x86_64)') { throw 'Use um JDK Windows x64 para gerar este pacote.' }

$pastaBuild = Join-Path $raizProjeto ('build\pacote-' + [Guid]::NewGuid().ToString('N'))
$classes = Join-Path $pastaBuild 'classes'
$entrada = Join-Path $pastaBuild 'entrada'
New-Item -ItemType Directory -Path $classes, $entrada, $pastaDestino -Force | Out-Null
Push-Location -LiteralPath $raizProjeto
try {
    Write-Host "Compilando com $versao..."
    $fontes = @(Get-ChildItem -LiteralPath 'src' -Recurse -Filter '*.java' -File | Sort-Object FullName | ForEach-Object FullName)
    & (Join-Path $jdkPasta 'javac.exe') --release 21 -encoding UTF-8 -d $classes @fontes
    if ($LASTEXITCODE -ne 0) { throw 'A compilacao falhou.' }
    & (Join-Path $jdkPasta 'jar.exe') --create --file (Join-Path $entrada 'Dead_Land.jar') --main-class DEAD.LAND.main -C $classes .
    if ($LASTEXITCODE -ne 0) { throw 'Nao foi possivel criar o JAR.' }
    Copy-Item -LiteralPath (Join-Path $raizProjeto 'repos') -Destination $entrada -Recurse

    Write-Host 'Gerando Dead_Land.exe e o Java incluido...'
    & (Join-Path $jdkPasta 'jpackage.exe') --type app-image --name Dead_Land `
        --app-version 1.0.0 --vendor 'Dead Land' --description 'Dead Land' `
        --input $entrada --dest $pastaDestino --main-jar Dead_Land.jar --main-class DEAD.LAND.main `
        --add-modules java.desktop --jlink-options '--strip-debug --no-header-files --no-man-pages --compress=2' `
        --java-options '-Ddeadland.recursos=$APPDIR' --java-options '-Ddeadland.save=$APPDIR/../save' `
        --java-options '-Dfile.encoding=UTF-8'
    if ($LASTEXITCODE -ne 0) { throw 'O empacotamento falhou.' }
    if ($IncluirSaves -and (Test-Path -LiteralPath (Join-Path $raizProjeto 'save'))) {
        Copy-Item -LiteralPath (Join-Path $raizProjeto 'save') -Destination $pastaApp -Recurse
    }
    @'
DEAD LAND - Windows x64

Abra Dead_Land.exe para jogar. O Java ja esta incluido.
Mantenha as pastas app e runtime junto do executavel.
Os tres slots ficam na pasta save, ao lado do executavel.
Para transportar o jogo, copie a pasta Dead_Land inteira ou extraia o ZIP inteiro.
Nao execute o jogo diretamente de dentro do ZIP.

Controles: WASD mover | E interagir | ESC pausa | Q arma | H cura
Inventario: roda do mouse sobre o rodape ou botoes < e >.
'@ | Set-Content -LiteralPath (Join-Path $pastaApp 'LEIA-ME.txt') -Encoding utf8
    Write-Host 'Criando ZIP...'
    Compress-Archive -LiteralPath $pastaApp -DestinationPath $arquivoZip -CompressionLevel Optimal
    Write-Host "Executavel: $(Join-Path $pastaApp 'Dead_Land.exe')"
    Write-Host "ZIP: $arquivoZip"
} finally {
    Pop-Location
    $resolvido = [IO.Path]::GetFullPath((Resolve-Path -LiteralPath $pastaBuild).Path)
    $prefixoBuild = [IO.Path]::GetFullPath((Join-Path $raizProjeto 'build')).TrimEnd('\') + '\'
    if (-not $resolvido.StartsWith($prefixoBuild, [StringComparison]::OrdinalIgnoreCase)) { throw 'Diretorio temporario inesperado.' }
    $itens = @((Get-Item -LiteralPath $resolvido -Force)) + @(Get-ChildItem -LiteralPath $resolvido -Recurse -Force)
    if (@($itens | Where-Object { $_.Attributes -band [IO.FileAttributes]::ReparsePoint }).Count -gt 0) { throw 'Link inesperado na compilacao temporaria.' }
    Remove-Item -LiteralPath $resolvido -Recurse -Force
}
