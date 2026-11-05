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

$temporaryRoot = [System.IO.Path]::GetFullPath([System.IO.Path]::GetTempPath())
$runName = 'DeadLand-' + [Guid]::NewGuid().ToString('N')
$runDirectory = [System.IO.Path]::GetFullPath((Join-Path $temporaryRoot $runName))
$temporaryPrefix = $temporaryRoot.TrimEnd([System.IO.Path]::DirectorySeparatorChar) + [System.IO.Path]::DirectorySeparatorChar
if (-not $runDirectory.StartsWith($temporaryPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
    throw 'O diretorio de compilacao precisa ficar na pasta temporaria do sistema.'
}
$classesDirectory = Join-Path $runDirectory 'classes'

Push-Location -LiteralPath $PSScriptRoot
try {
    New-Item -ItemType Directory -Path $classesDirectory -Force | Out-Null
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

    Write-Host 'Iniciando o jogo atualizado...'
    & $javaExecutable -cp $classesDirectory DEAD.LAND.main
    if ($LASTEXITCODE -ne 0) {
        throw "O jogo encerrou com o codigo $LASTEXITCODE."
    }
} finally {
    Pop-Location
    if (Test-Path -LiteralPath $runDirectory) {
        $resolvedRunDirectory = [System.IO.Path]::GetFullPath((Resolve-Path -LiteralPath $runDirectory).Path)
        if ($resolvedRunDirectory -ne $runDirectory -or -not $resolvedRunDirectory.StartsWith($temporaryPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
            throw 'Diretorio temporario inesperado durante a limpeza.'
        }
        $temporaryItems = @((Get-Item -LiteralPath $resolvedRunDirectory -Force)) + @(Get-ChildItem -LiteralPath $resolvedRunDirectory -Recurse -Force)
        if (@($temporaryItems | Where-Object { $_.Attributes -band [System.IO.FileAttributes]::ReparsePoint }).Count -gt 0) {
            throw 'A compilacao temporaria contem um link inesperado.'
        }
        Remove-Item -LiteralPath $resolvedRunDirectory -Recurse -Force -ErrorAction Stop
    }
}
