param([Parameter(Mandatory=$true)][string]$Destination, [switch]$IncludeApk)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$nokiProject = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$nokiOutput = [IO.Path]::GetFullPath($Destination)
if ([IO.Path]::GetExtension($nokiOutput) -ne '.zip') { throw 'El destino debe ser un ZIP.' }
if (Test-Path -LiteralPath $nokiOutput) { throw 'El ZIP ya existe. Usa un nombre nuevo; no se sobrescribe.' }
$nokiParent = [IO.Path]::GetDirectoryName($nokiOutput)
if (-not (Test-Path -LiteralPath $nokiParent)) { New-Item -ItemType Directory -Path $nokiParent | Out-Null }

# Explicit source roots: no credentials, IDE state, build output, SDK or cached database metadata.
$nokiRootFiles = @('.gitignore','README.md','build.gradle.kts','settings.gradle.kts','gradle.properties','gradlew','gradlew.bat','app/build.gradle.kts','app/proguard-rules.pro','shared/build.gradle.kts')
$nokiSourceDirs = @('app/src','shared/src','gradle/wrapper','docs','licenses','supabase/migrations','tools')
$nokiFiles = [Collections.Generic.List[IO.FileInfo]]::new()
foreach ($nokiName in $nokiRootFiles) { $nokiFiles.Add((Get-Item -LiteralPath (Join-Path $nokiProject $nokiName))) }
foreach ($nokiDir in $nokiSourceDirs) {
    Get-ChildItem -LiteralPath (Join-Path $nokiProject $nokiDir) -Recurse -File | ForEach-Object { $nokiFiles.Add($_) }
}
# Relevant reproducible evidence; raw captures and old local outputs stay in the working folder.
$nokiEvidence = @('paquete-spec-4.3.md')
foreach ($nokiName in $nokiEvidence) {
    $nokiFile = Join-Path $nokiProject "verification/$nokiName"
    if (Test-Path -LiteralPath $nokiFile) { $nokiFiles.Add((Get-Item -LiteralPath $nokiFile)) }
}
$nokiApk = $null
if ($IncludeApk) {
    $nokiMeta = Get-Content -LiteralPath (Join-Path $nokiProject 'app/build/outputs/apk/debug/output-metadata.json') -Raw | ConvertFrom-Json
    $nokiBuild = Get-Content -LiteralPath (Join-Path $nokiProject 'app/build.gradle.kts') -Raw
    $nokiVersion = [regex]::Match($nokiBuild,'versionName\s*=\s*"([^"]+)"').Groups[1].Value
    $nokiVersionCode = [int][regex]::Match($nokiBuild,'versionCode\s*=\s*(\d+)').Groups[1].Value
    if ($nokiMeta.applicationId -ne 'co.noki.app' -or $nokiMeta.variantName -ne 'debug' -or $nokiMeta.elements.Count -ne 1 -or $nokiMeta.elements[0].versionName -ne $nokiVersion -or $nokiMeta.elements[0].versionCode -ne $nokiVersionCode) { throw 'APK desactualizado o variante incorrecta.' }
    $nokiApk = Join-Path $nokiProject 'app/build/outputs/apk/debug/app-debug.apk'
    if (-not (Test-Path -LiteralPath $nokiApk)) { throw 'Falta el APK compilado.' }
}
$nokiManifest = [Collections.Generic.List[string]]::new()
$nokiArchive = [IO.Compression.ZipFile]::Open($nokiOutput,[IO.Compression.ZipArchiveMode]::Create)
try {
    foreach ($nokiFile in ($nokiFiles | Sort-Object FullName -Unique)) {
        $nokiFull = [IO.Path]::GetFullPath($nokiFile.FullName)
        if (-not $nokiFull.StartsWith($nokiProject + [IO.Path]::DirectorySeparatorChar,[StringComparison]::OrdinalIgnoreCase)) { throw 'Archivo fuera del proyecto.' }
        if ($nokiFile.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'No se empaquetan enlaces de archivos.' }
        $nokiRelative = $nokiFull.Substring($nokiProject.Length + 1).Replace('\','/')
        if ($nokiRelative -match '(?i)(^|/)(\.env[^/]*|local\.properties|secrets[^/]*|key\.properties|google-services\.json)$|\.(jks|keystore|p12|pem|apk|aab)$') { throw "Archivo no permitido: $nokiRelative" }
        if ($nokiFile.Extension -in @('.kt','.kts','.json','.xml','.properties','.md','.txt','.sql','.ps1','.html')) {
            $nokiText = [IO.File]::ReadAllText($nokiFull)
            if ($nokiText -match 'sb_secret_[A-Za-z0-9_-]+|-----BEGIN [^-]*PRIVATE KEY-----|eyJ[A-Za-z0-9_-]{40,}\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+') { throw "Posible credencial en $nokiRelative. Revisión requerida." }
        }
        [IO.Compression.ZipFileExtensions]::CreateEntryFromFile($nokiArchive,$nokiFull,"Noki-Android/$nokiRelative",[IO.Compression.CompressionLevel]::Optimal) | Out-Null
        $nokiManifest.Add("$((Get-FileHash -LiteralPath $nokiFull -Algorithm SHA256).Hash)  Noki-Android/$nokiRelative")
    }
    foreach ($nokiDoc in @(@('docs/Noki-App-SPEC.md','SPEC.md'),@('docs/PLAN.md','PLAN.md'),@('docs/TASKS.md','TASKS.md'),@('docs/GUIA-ENTREGA.md','LEEME.md'),@('docs/PENDIENTES.md','PENDIENTES.md'),@('docs/VERIFICACION.md','VERIFICACION.md'))) {
        $nokiFull = Join-Path $nokiProject $nokiDoc[0]
        [IO.Compression.ZipFileExtensions]::CreateEntryFromFile($nokiArchive,$nokiFull,$nokiDoc[1],[IO.Compression.CompressionLevel]::Optimal) | Out-Null
        $nokiManifest.Add("$((Get-FileHash -LiteralPath $nokiFull -Algorithm SHA256).Hash)  $($nokiDoc[1])")
    }
    # Keep links usable in the root convenience copy; the authoritative matrix
    # inside docs links to its neighbouring source directories.
    $nokiMatrix = [IO.File]::ReadAllText((Join-Path $nokiProject 'docs/MATRIZ-SPEC-CODIGO.md')).Replace('](../','](Noki-Android/')
    $nokiMatrixBytes = [Text.UTF8Encoding]::new($false).GetBytes($nokiMatrix)
    $nokiMatrixEntry = $nokiArchive.CreateEntry('MATRIZ-SPEC-CODIGO.md')
    $nokiMatrixStream = $nokiMatrixEntry.Open()
    try { $nokiMatrixStream.Write($nokiMatrixBytes,0,$nokiMatrixBytes.Length) } finally { $nokiMatrixStream.Dispose() }
    $nokiMatrixSha = [Security.Cryptography.SHA256]::Create()
    try { $nokiMatrixHash = [BitConverter]::ToString($nokiMatrixSha.ComputeHash($nokiMatrixBytes)).Replace('-','') } finally { $nokiMatrixSha.Dispose() }
    $nokiManifest.Add("$nokiMatrixHash  MATRIZ-SPEC-CODIGO.md")
    if ($nokiApk) {
        $nokiApkName = "Instalador/Noki-$nokiVersion.apk"
        [IO.Compression.ZipFileExtensions]::CreateEntryFromFile($nokiArchive,$nokiApk,$nokiApkName,[IO.Compression.CompressionLevel]::Optimal) | Out-Null
        $nokiManifest.Add("$((Get-FileHash -LiteralPath $nokiApk -Algorithm SHA256).Hash)  $nokiApkName")
    }
    $nokiEntry = $nokiArchive.CreateEntry('CONTENIDO-SHA256.txt')
    $nokiWriter = [IO.StreamWriter]::new($nokiEntry.Open(),[Text.UTF8Encoding]::new($false))
    try { $nokiWriter.WriteLine(($nokiManifest -join "`n")) } finally { $nokiWriter.Dispose() }
} finally { $nokiArchive.Dispose() }
[pscustomobject]@{Path=$nokiOutput;Files=$nokiManifest.Count;Bytes=(Get-Item -LiteralPath $nokiOutput).Length;SHA256=(Get-FileHash -LiteralPath $nokiOutput -Algorithm SHA256).Hash} | ConvertTo-Json -Compress
