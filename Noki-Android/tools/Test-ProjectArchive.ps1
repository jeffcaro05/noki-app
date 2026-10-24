param([Parameter(Mandatory=$true)][string]$Archive)
$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$nokiZip=[IO.Compression.ZipFile]::OpenRead([IO.Path]::GetFullPath($Archive))
try {
    $nokiNames=@($nokiZip.Entries | ForEach-Object {$_.FullName})
    if(($nokiNames | Sort-Object -Unique).Count -ne $nokiNames.Count){throw 'Hay entradas duplicadas.'}
    foreach($nokiName in $nokiNames){
        if($nokiName -match '(^/|\\|(^|/)\.\.(/|$)|:|(^|/)(\.gradle|\.idea|\.kotlin|\.temp|build)(/|$)|(^|/)(local\.properties|\.env[^/]*|secrets[^/]*|key\.properties)$|\.(jks|keystore|p12|pem)$)'){throw "Entrada no permitida: $nokiName"}
    }
    foreach($nokiRequired in @('SPEC.md','PLAN.md','TASKS.md','LEEME.md','MATRIZ-SPEC-CODIGO.md','PENDIENTES.md','VERIFICACION.md','CONTENIDO-SHA256.txt','Noki-Android/settings.gradle.kts','Noki-Android/gradlew','Noki-Android/gradlew.bat','Noki-Android/gradle/wrapper/gradle-wrapper.jar','Noki-Android/app/build.gradle.kts','Noki-Android/shared/build.gradle.kts')){
        if($nokiRequired -notin $nokiNames){throw "Falta $nokiRequired"}
    }
    $nokiReader=[IO.StreamReader]::new($nokiZip.GetEntry('CONTENIDO-SHA256.txt').Open())
    try{$nokiLines=$nokiReader.ReadToEnd().TrimEnd() -split '\r?\n'}finally{$nokiReader.Dispose()}
    $nokiSeen=[Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
    foreach($nokiLine in $nokiLines){
        if($nokiLine -notmatch '^([A-F0-9]{64})  (.+)$'){throw 'Manifiesto inválido.'}
        $nokiExpected=$Matches[1];$nokiName=$Matches[2]
        if(-not $nokiSeen.Add($nokiName)){throw 'Entrada duplicada en el manifiesto.'}
        $nokiEntry=$nokiZip.GetEntry($nokiName);if($null -eq $nokiEntry){throw "Falta $nokiName"}
        $nokiStream=$nokiEntry.Open();$nokiSha=[Security.Cryptography.SHA256]::Create()
        try{$nokiActual=[BitConverter]::ToString($nokiSha.ComputeHash($nokiStream)).Replace('-','')}finally{$nokiStream.Dispose();$nokiSha.Dispose()}
        if($nokiActual -cne $nokiExpected){throw "Integridad incorrecta: $nokiName"}
    }
    if($nokiSeen.Count -ne $nokiZip.Entries.Count-1){throw 'Hay archivos sin manifiesto.'}
    foreach($nokiPair in @(@('SPEC.md','Noki-Android/docs/Noki-App-SPEC.md'),@('PLAN.md','Noki-Android/docs/PLAN.md'),@('TASKS.md','Noki-Android/docs/TASKS.md'),@('LEEME.md','Noki-Android/docs/GUIA-ENTREGA.md'),@('PENDIENTES.md','Noki-Android/docs/PENDIENTES.md'),@('VERIFICACION.md','Noki-Android/docs/VERIFICACION.md'))){
        $nokiTexts=@();foreach($nokiName in $nokiPair){$nokiReader=[IO.StreamReader]::new($nokiZip.GetEntry($nokiName).Open());try{$nokiTexts += $nokiReader.ReadToEnd()}finally{$nokiReader.Dispose()}}
        if($nokiTexts[0] -cne $nokiTexts[1]){throw 'Copias de documentación divergentes.'}
    }
    $nokiMatrixTexts=@()
    foreach($nokiName in @('MATRIZ-SPEC-CODIGO.md','Noki-Android/docs/MATRIZ-SPEC-CODIGO.md')) {
        $nokiReader=[IO.StreamReader]::new($nokiZip.GetEntry($nokiName).Open())
        try {$nokiMatrixTexts += $nokiReader.ReadToEnd()} finally {$nokiReader.Dispose()}
    }
    if($nokiMatrixTexts[0] -cne $nokiMatrixTexts[1].Replace('](../','](Noki-Android/')){throw 'Matriz divergente; solo deben cambiar las rutas de navegación de la copia raíz.'}
    [pscustomobject]@{Valid=$true;VerifiedFiles=$nokiSeen.Count;DocumentCopiesIdentical=$true} | ConvertTo-Json -Compress
}finally{$nokiZip.Dispose()}
