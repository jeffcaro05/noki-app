# Interactive, user-operated authorization. Never saves the authorization code in this project.
$ErrorActionPreference = 'Stop'
$nodeCommand = Get-Command node -ErrorAction SilentlyContinue
if (-not $nodeCommand) { throw 'Instala Node.js 24 y vuelve a abrir esta ventana.' }
$nodePath = $nodeCommand.Source
$bundledPnpm = Join-Path (Split-Path $nodePath -Parent) '../node_modules/pnpm/bin/pnpm.cjs'
if (-not (Test-Path -LiteralPath $bundledPnpm)) {
    $bundledPnpm = 'C:/Users/hp/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/pnpm/bin/pnpm.cjs'
}
if (-not (Test-Path -LiteralPath $bundledPnpm)) { throw 'No se encontró pnpm. Instala pnpm 11 y usa: pnpm dlx firebase-tools@15.34.0 login' }
Write-Host 'Conecta la cuenta de Google que tiene acceso al proyecto Firebase noki-89c7b.'
Write-Host 'Abre el enlace que aparece a continuación y autoriza el acceso personalmente.'
Write-Host 'No envíes el código al chat. Pégalo solo cuando esta ventana lo solicite.'
& $nodePath $bundledPnpm dlx firebase-tools@15.34.0 login
if ($LASTEXITCODE -ne 0) { throw 'No se pudo iniciar la autorización de Firebase.' }
$secureAuthorization = Read-Host 'Pega aquí el código de autorización de Firebase (no se mostrará)' -AsSecureString
$plainAuthorization = [System.Net.NetworkCredential]::new('', $secureAuthorization).Password
try {
    if ([string]::IsNullOrWhiteSpace($plainAuthorization)) { throw 'No se ingresó ningún código.' }
    & $nodePath $bundledPnpm dlx firebase-tools@15.34.0 login $plainAuthorization
    if ($LASTEXITCODE -ne 0) { throw 'Firebase no aceptó la autorización. Solicita un código nuevo desde este asistente.' }
    Write-Host 'Cuenta conectada. Puedes volver a Noki y avisar que terminaste.'
} finally {
    $plainAuthorization = $null
    $secureAuthorization.Dispose()
}
