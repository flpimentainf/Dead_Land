param([switch]$Validar)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$compilerCommand = Get-Command javac.exe -CommandType Application -ErrorAction Stop | Select-Object -First 1
$jdkDirectory = Split-Path -Parent $compilerCommand.Source
$javaExecutable = Join-Path $jdkDirectory 'java.exe'
if (-not (Test-Path -LiteralPath $javaExecutable)) {
    throw 'O JDK encontrado precisa conter javac.exe e java.exe na mesma pasta.'
}

$compilerVersion = (& $compilerCommand.Source -version 2>&1 | Out-String).Trim()
if ($LASTEXITCODE -ne 0 -or $compilerVersion -notmatch 'javac\s+(\d+)' -or [int]$Matches[1] -lt 21) {
    throw "Instale um JDK 21 ou superior. Compilador encontrado: $compilerVersion"
}

$buildDirectory = Join-Path $PSScriptRoot 'build'
$runName = 'run-' + [Guid]::NewGuid().ToString('N')
$classesDirectory = Join-Path (Join-Path $buildDirectory $runName) 'classes'
New-Item -ItemType Directory -Path $classesDirectory -Force | Out-Null

Push-Location -LiteralPath $PSScriptRoot
try {
    $sourceFiles = @(Get-ChildItem -LiteralPath 'src' -Recurse -File -Filter '*.java' | Sort-Object FullName | ForEach-Object FullName)
    if ($sourceFiles.Count -eq 0) {
        throw 'Nenhum arquivo Java encontrado na pasta src.'
    }

    Write-Host "Compilando fontes atuais com $compilerVersion..."
    & $compilerCommand.Source --release 21 -encoding UTF-8 -d $classesDirectory @sourceFiles
    if ($LASTEXITCODE -ne 0) {
        throw 'A compilacao falhou. O jogo nao foi iniciado.'
    }

    if ($Validar) {
        & $javaExecutable -cp $classesDirectory DEAD.LAND.core.ValidacaoTecnica
        if ($LASTEXITCODE -ne 0) {
            throw 'As validacoes tecnicas falharam. O jogo nao foi iniciado.'
        }
    }

    Set-Content -LiteralPath (Join-Path $buildDirectory 'ultima-compilacao.txt') -Value $classesDirectory -Encoding UTF8
    Write-Host "Iniciando compilacao atualizada: $runName"
    & $javaExecutable -cp $classesDirectory DEAD.LAND.main
    if ($LASTEXITCODE -ne 0) {
        throw "O jogo encerrou com o codigo $LASTEXITCODE."
    }
} finally {
    Pop-Location
}
