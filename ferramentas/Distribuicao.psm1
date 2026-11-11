Set-StrictMode -Version Latest

function Get-VersoesJogo {
    param([Parameter(Mandatory)][string]$Raiz)
    @(Get-ChildItem -LiteralPath $Raiz -Directory | Where-Object { $_.Name -match '^v\d+\.\d+\.\d+$' } |
        ForEach-Object {
            [pscustomobject]@{ Numero = [version]$_.Name.Substring(1); Pasta = $_.FullName }
        } | Sort-Object Numero -Descending)
}

function Assert-PastaVersaoJogo {
    param([Parameter(Mandatory)][string]$Raiz, [Parameter(Mandatory)][string]$Pasta)
    $raizAbsoluta = [IO.Path]::GetFullPath((Resolve-Path -LiteralPath $Raiz).Path).TrimEnd('\')
    $alvoAbsoluto = [IO.Path]::GetFullPath((Resolve-Path -LiteralPath $Pasta).Path).TrimEnd('\')
    if (-not [IO.Path]::GetDirectoryName($alvoAbsoluto).Equals($raizAbsoluta, [StringComparison]::OrdinalIgnoreCase) -or
        [IO.Path]::GetFileName($alvoAbsoluto) -notmatch '^v\d+\.\d+\.\d+$') {
        throw 'A versao precisa ser uma subpasta direta da distribuicao.'
    }
    $itens = @((Get-Item -LiteralPath $raizAbsoluta -Force), (Get-Item -LiteralPath $alvoAbsoluto -Force)) +
        @(Get-ChildItem -LiteralPath $alvoAbsoluto -Recurse -Force)
    if (@($itens | Where-Object { $_.Attributes -band [IO.FileAttributes]::ReparsePoint }).Count) {
        throw 'Link inesperado na pasta da distribuicao.'
    }
    $cfg = Join-Path $alvoAbsoluto 'app\Dead_Land.cfg'
    if (-not (Test-Path -LiteralPath (Join-Path $alvoAbsoluto 'Dead_Land.exe') -PathType Leaf) -or
        -not (Test-Path -LiteralPath $cfg -PathType Leaf)) { throw 'A pasta nao e um pacote de Dead Land.' }
    $numero = [IO.Path]::GetFileName($alvoAbsoluto).Substring(1)
    if (-not (@(Get-Content -LiteralPath $cfg) -contains "java-options=-Djpackage.app-version=$numero")) {
        throw 'A versao do executavel nao corresponde ao nome da pasta.'
    }
}

function Copy-SavesRecentes {
    param([string[]]$Origens, [Parameter(Mandatory)][string]$Destino)
    foreach ($slot in 1, 2, 3) {
        $candidatos = @($Origens | ForEach-Object {
            $arquivo = Join-Path $_ "slot$slot.properties"
            if (Test-Path -LiteralPath $arquivo -PathType Leaf) { Get-Item -LiteralPath $arquivo }
        } | Sort-Object LastWriteTimeUtc -Descending)
        if ($candidatos.Count) {
            New-Item -ItemType Directory -Path $Destino -Force | Out-Null
            $alvo = Join-Path $Destino "slot$slot.properties"
            Copy-Item -LiteralPath $candidatos[0].FullName -Destination $alvo -Force
            if ((Get-FileHash -LiteralPath $alvo).Hash -ne (Get-FileHash -LiteralPath $candidatos[0].FullName).Hash) {
                throw "Falha ao copiar o slot $slot."
            }
        }
    }
}

function Remove-VersoesAntigasJogo {
    param([Parameter(Mandatory)][string]$Raiz)
    $versoes = @(Get-VersoesJogo -Raiz $Raiz)
    # Verifica todos os alvos antes de iniciar qualquer exclusao.
    foreach ($versao in $versoes) { Assert-PastaVersaoJogo -Raiz $Raiz -Pasta $versao.Pasta }
    foreach ($versao in @($versoes | Select-Object -Skip 2)) {
        Assert-PastaVersaoJogo -Raiz $Raiz -Pasta $versao.Pasta
        Remove-Item -LiteralPath $versao.Pasta -Recurse -Force -ErrorAction Stop
    }
}

Export-ModuleMember -Function Get-VersoesJogo, Assert-PastaVersaoJogo, Copy-SavesRecentes, Remove-VersoesAntigasJogo
