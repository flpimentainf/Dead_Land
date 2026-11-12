param([switch]$Empacotar)
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
Import-Module (Join-Path $PSScriptRoot 'Distribuicao.psm1') -Force
$raizProjeto = Split-Path -Parent $PSScriptRoot
$raizTeste = Join-Path $raizProjeto ('build\distribuicao-teste-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $raizTeste -Force | Out-Null

function Exigir($Condicao, [string]$Mensagem) { if (-not $Condicao) { throw $Mensagem } }
function Criar-VersaoTeste([string]$Raiz, [string]$Numero) {
    $pasta = Join-Path $Raiz "v$Numero"
    New-Item -ItemType Directory -Path (Join-Path $pasta 'app'), (Join-Path $pasta 'save') -Force | Out-Null
    Set-Content -LiteralPath (Join-Path $pasta 'Dead_Land.exe') -Value 'fixture'
    Set-Content -LiteralPath (Join-Path $pasta 'app\Dead_Land.cfg') -Value "java-options=-Djpackage.app-version=$Numero"
    return $pasta
}
try {
    $raizNumerica = Join-Path $raizTeste 'numerica'
    foreach ($numero in @('1.9.0', '1.10.0', '1.11.0')) { Criar-VersaoTeste $raizNumerica $numero | Out-Null }
    Remove-VersoesAntigasJogo $raizNumerica
    $mantidas = @(Get-VersoesJogo $raizNumerica)
    Exigir ($mantidas.Count -eq 2 -and $mantidas[0].Numero -eq [version]'1.11.0' -and $mantidas[1].Numero -eq [version]'1.10.0') 'Retencao deve ordenar versoes numericamente.'
    $desconhecida = Join-Path $raizNumerica 'v99.0.0'
    New-Item -ItemType Directory -Path $desconhecida -Force | Out-Null
    $recusou = $false
    try { Remove-VersoesAntigasJogo $raizNumerica } catch { $recusou = $true }
    Exigir $recusou 'Nao deve apagar uma pasta sem os marcadores do jogo.'
    Exigir (Test-Path -LiteralPath (Join-Path $raizNumerica 'v1.10.0')) 'Validacao deve ocorrer antes de qualquer exclusao.'

    $raizTrava = Join-Path $raizTeste 'arquivo-em-uso'
    foreach ($numero in @('1.0.0', '1.1.0', '1.2.0', '1.3.0')) { Criar-VersaoTeste $raizTrava $numero | Out-Null }
    $jarAberto = Join-Path $raizTrava 'v1.0.0\app\Dead_Land.jar'
    Set-Content -LiteralPath $jarAberto -Value 'fixture jar'
    $antes = @(Get-ChildItem -LiteralPath $raizTrava -Recurse -File | ForEach-Object FullName | Sort-Object)
    $trava = [IO.File]::Open($jarAberto, [IO.FileMode]::Open, [IO.FileAccess]::ReadWrite, [IO.FileShare]::None)
    try {
        $recusou = $false
        try { Remove-VersoesAntigasJogo $raizTrava } catch { $recusou = $_.Exception.Message -like '*em uso*' }
        Exigir $recusou 'Uma versao aberta deve impedir a limpeza.'
        $depois = @(Get-ChildItem -LiteralPath $raizTrava -Recurse -File | ForEach-Object FullName | Sort-Object)
        Exigir (($antes -join '|') -eq ($depois -join '|')) 'Um arquivo bloqueado nao pode causar exclusao parcial de nenhuma versao.'
    } finally { $trava.Dispose() }
    Remove-VersoesAntigasJogo $raizTrava
    Exigir (@(Get-VersoesJogo $raizTrava).Count -eq 2) 'Fechar o arquivo deve permitir concluir a limpeza.'

    $raizPacote = Join-Path $raizTeste 'pacotes'
    $antiga = Criar-VersaoTeste $raizPacote '1.0.0'
    $atual = Criar-VersaoTeste $raizPacote '1.1.0'
    Set-Content -LiteralPath (Join-Path $antiga 'save\slot2.properties') -Value 'progresso mais recente'
    Set-Content -LiteralPath (Join-Path $atual 'save\slot2.properties') -Value 'progresso antigo'
    (Get-Item -LiteralPath (Join-Path $antiga 'save\slot2.properties')).LastWriteTimeUtc = [datetime]'2026-11-11T18:00:00Z'
    (Get-Item -LiteralPath (Join-Path $atual 'save\slot2.properties')).LastWriteTimeUtc = [datetime]'2026-11-11T17:00:00Z'
    $hash = (Get-FileHash -LiteralPath (Join-Path $antiga 'save\slot2.properties')).Hash
    if ($Empacotar) {
        $relativo = [IO.Path]::GetRelativePath($raizProjeto, $raizPacote)
        & (Join-Path $raizProjeto 'empacotar.ps1') -Versao 1.2.0 -Destino $relativo
    } else {
        $nova = Criar-VersaoTeste $raizPacote '1.2.0'
        Copy-SavesRecentes -Origens @((Join-Path $atual 'save'), (Join-Path $antiga 'save')) -Destino (Join-Path $nova 'save')
        Remove-VersoesAntigasJogo $raizPacote
    }
    $versoes = @(Get-VersoesJogo $raizPacote)
    Exigir ($versoes.Count -eq 2 -and $versoes[0].Numero -eq [version]'1.2.0' -and $versoes[1].Numero -eq [version]'1.1.0') 'Deve manter somente v1.1.0 e v1.2.0.'
    Exigir ((Get-FileHash -LiteralPath (Join-Path $raizPacote 'v1.2.0\save\slot2.properties')).Hash -eq $hash) 'O slot mais recente da versao removida deve sobreviver.'
    Copy-SavesRecentes -Origens @((Join-Path $raizPacote 'v1.2.0\save'), (Join-Path $atual 'save')) -Destino (Join-Path $raizPacote 'v1.2.0\save')
    Exigir ((Get-FileHash -LiteralPath (Join-Path $raizPacote 'v1.2.0\save\slot2.properties')).Hash -eq $hash) 'Repetir a sincronizacao nao pode copiar um arquivo sobre si mesmo.'
    Exigir (@(Get-ChildItem -LiteralPath $raizPacote -File -Filter '*.zip').Count -eq 0) 'Nao deve gerar ZIP.'
    Write-Host 'Distribuicao OK: duas versoes, ordem numerica, saves preservados e nenhum ZIP.'
} finally {
    $resolvido = [IO.Path]::GetFullPath((Resolve-Path -LiteralPath $raizTeste).Path)
    $prefixoBuild = [IO.Path]::GetFullPath((Join-Path $raizProjeto 'build')).TrimEnd('\') + '\'
    if (-not $resolvido.StartsWith($prefixoBuild, [StringComparison]::OrdinalIgnoreCase)) { throw 'Limpeza fora do diretorio de testes.' }
    $itens = @((Get-Item -LiteralPath $resolvido -Force)) + @(Get-ChildItem -LiteralPath $resolvido -Recurse -Force)
    if (@($itens | Where-Object { $_.Attributes -band [IO.FileAttributes]::ReparsePoint }).Count) { throw 'Link inesperado no teste.' }
    Remove-Item -LiteralPath $resolvido -Recurse -Force
}
