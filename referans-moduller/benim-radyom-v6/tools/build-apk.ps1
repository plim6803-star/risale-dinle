param(
    [string]$ProjectRoot = "C:\Users\masallahresulozcan\Desktop\Benim Radiyom"
)

$ErrorActionPreference = "Stop"

$devTools = "C:\Users\masallahresulozcan\DevTools"
$sdk = Join-Path $devTools "android-sdk"
$buildTools = Join-Path $sdk "build-tools\36.0.0"
$androidJar = Join-Path $sdk "platforms\android-36\android.jar"
$javaHome = Join-Path $devTools "jdk-17.0.20.1+1"
$java = Join-Path $javaHome "bin\java.exe"
$javac = Join-Path $javaHome "bin\javac.exe"
$jar = Join-Path $javaHome "bin\jar.exe"
$libs = Join-Path $devTools "libs"
$deps = Join-Path $devTools "deps"
$out = Join-Path $devTools "apkbuild"

$src = Join-Path $ProjectRoot "app\src\main\java"
$res = Join-Path $ProjectRoot "app\src\main\res"
$manifest = Join-Path $ProjectRoot "app\src\main\AndroidManifest.xml"
$keystore = Join-Path $ProjectRoot "debug.keystore"
$apk = Join-Path $ProjectRoot "BenimRadyom.apk"

$minSdk = 26
$targetSdk = 36

function Step([string]$text) {
    Write-Host ""
    Write-Host "=========== $text ==========="
}

function Ensure-Success([string]$message) {
    if ($LASTEXITCODE -ne 0) { throw "$message (cikis kodu: $LASTEXITCODE)" }
}

function Reset-Directory([string]$path) {
    $resolvedOut = [System.IO.Path]::GetFullPath($out)
    $resolvedPath = [System.IO.Path]::GetFullPath($path)
    if (-not $resolvedPath.StartsWith($resolvedOut, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Guvenlik denetimi: derleme klasoru disindaki yol temizlenemez: $resolvedPath"
    }
    if (Test-Path -LiteralPath $resolvedPath) {
        # Virüs tarayicisi binlerce sinif dosyasindan birini cok kisa sure
        # tutabiliyor. Temiz derlemeyi rastgele bozmamak icin sinirli yeniden
        # deneme yap; hedef yine yalnizca dogrulanmis apkbuild altindadir.
        for ($attempt = 1; $attempt -le 8; $attempt++) {
            try {
                Remove-Item -LiteralPath $resolvedPath -Recurse -Force -ErrorAction Stop
                break
            } catch {
                if ($attempt -eq 8) { throw }
                Start-Sleep -Milliseconds 250
            }
        }
    }
    New-Item -ItemType Directory -Path $resolvedPath -Force | Out-Null
}

foreach ($required in @($java, $javac, $jar, $androidJar, $manifest, $keystore)) {
    if (-not (Test-Path -LiteralPath $required)) { throw "Gerekli dosya bulunamadi: $required" }
}

# Kilit: build-apk.sh ile AYNI kilit. Iki derleme ayni anda $out'a yazarsa biri
# digerinin ara dosyalarini siler ve imzali ama ici eksik bir APK cikar.
$lock = "$out.lock"
if (Test-Path -LiteralPath $lock) {
    $age = (Get-Date) - (Get-Item -LiteralPath $lock).LastWriteTime
    if ($age.TotalMinutes -gt 30) {
        Remove-Item -LiteralPath $lock -Recurse -Force
    } else {
        throw "Baska bir derleme calisiyor ($lock). Bitmesini bekleyin."
    }
}
New-Item -ItemType Directory -Path $lock | Out-Null
# Hata olursa da kilit birakilir; basarida sonda birakilir.
trap {
    Remove-Item -LiteralPath $lock -Recurse -Force -ErrorAction SilentlyContinue
    break
}

# Surum tek yerden gelir: app/build.gradle.kts
$gradleText = Get-Content -Raw -LiteralPath (Join-Path $ProjectRoot "app\build.gradle.kts")
$versionCode = [regex]::Match($gradleText, 'versionCode\s*=\s*(\d+)').Groups[1].Value
$versionName = [regex]::Match($gradleText, 'versionName\s*=\s*"([^"]+)"').Groups[1].Value
if (-not $versionCode -or -not $versionName) { throw "Surum build.gradle.kts'ten okunamadi" }
Write-Host "Surum: $versionName ($versionCode)"

New-Item -ItemType Directory -Path $out -Force | Out-Null

Step "1/8 AAR paketleri aciliyor"
$exploded = Join-Path $out "exploded"
New-Item -ItemType Directory -Path $exploded -Force | Out-Null
$aars = Get-ChildItem -LiteralPath $deps -Filter *.aar -File
foreach ($aar in $aars) {
    $dir = Join-Path $exploded $aar.BaseName
    if (-not (Test-Path -LiteralPath $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Push-Location $dir
        try {
            & $jar xf $aar.FullName
            Ensure-Success "AAR acilamadi: $($aar.Name)"
        } finally { Pop-Location }
    }
}
Write-Host "acilan AAR: $($aars.Count)"

$compileClasspath = [System.Collections.Generic.List[string]]::new()
$compileClasspath.Add($androidJar)
Get-ChildItem -LiteralPath $exploded -Recurse -Filter classes.jar -File | ForEach-Object {
    $compileClasspath.Add($_.FullName)
}
Get-ChildItem -LiteralPath $exploded -Recurse -Filter *.jar -File | Where-Object {
    $_.FullName -match "\\libs\\"
} | ForEach-Object { $compileClasspath.Add($_.FullName) }
Get-ChildItem -LiteralPath $deps -Filter *.jar -File | ForEach-Object {
    $compileClasspath.Add($_.FullName)
}
$compileClasspath.Add((Join-Path $libs "kotlin-stdlib.jar"))
$compileClasspath.Add((Join-Path $libs "kotlinx-coroutines-core-jvm.jar"))

Step "2/8 Kaynaklar derleniyor"
$flat = Join-Path $out "flat"
Reset-Directory $flat
$appFlat = Join-Path $flat "app.zip"
& (Join-Path $buildTools "aapt2.exe") compile --dir $res -o $appFlat
Ensure-Success "Uygulama kaynaklari derlenemedi"

$extraPackages = [System.Collections.Generic.List[string]]::new()
$libraryResourceCount = 0
Get-ChildItem -LiteralPath $exploded -Directory | ForEach-Object {
    $libraryDir = $_.FullName
    $libraryRes = Join-Path $libraryDir "res"
    if ((Test-Path -LiteralPath $libraryRes) -and (Get-ChildItem -LiteralPath $libraryRes -Force | Select-Object -First 1)) {
        $libraryResourceCount++
        $libFlat = Join-Path $flat ("lib_{0}.zip" -f $libraryResourceCount)
        & (Join-Path $buildTools "aapt2.exe") compile --dir $libraryRes -o $libFlat 2>$null
        if ($LASTEXITCODE -ne 0) { Write-Host "uyari: kaynaklari atlanan kutuphane: $($_.Name)" }
    }
    $libManifest = Join-Path $libraryDir "AndroidManifest.xml"
    if (Test-Path -LiteralPath $libManifest) {
        $match = [regex]::Match((Get-Content -Raw -LiteralPath $libManifest), 'package="([^"]+)"')
        if ($match.Success) { $extraPackages.Add($match.Groups[1].Value) }
    }
}
Write-Host "derlenen kutuphane kaynagi: $libraryResourceCount"

Step "3/8 Kaynaklar baglaniyor"
$gen = Join-Path $out "gen"
Reset-Directory $gen
$baseApk = Join-Path $out "base.apk"
$linkArgs = [System.Collections.Generic.List[string]]::new()
$linkArgs.AddRange([string[]]@(
    "link", "-o", $baseApk, "-I", $androidJar,
    "--manifest", $manifest, "--java", $gen,
    "--min-sdk-version", "$minSdk", "--target-sdk-version", "$targetSdk",
    "--version-code", $versionCode, "--version-name", $versionName,
    "--auto-add-overlay", "--no-version-vectors"
))
Get-ChildItem -LiteralPath $flat -Filter *.zip -File | ForEach-Object {
    $linkArgs.Add("-R"); $linkArgs.Add($_.FullName)
}
if ($extraPackages.Count -gt 0) {
    $linkArgs.Add("--extra-packages")
    $linkArgs.Add(($extraPackages | Select-Object -Unique) -join ":")
}
& (Join-Path $buildTools "aapt2.exe") @linkArgs
Ensure-Success "aapt2 link basarisiz"

Step "4/8 R siniflari derleniyor"
$rclasses = Join-Path $out "rclasses"
Reset-Directory $rclasses
$rSources = Get-ChildItem -LiteralPath $gen -Recurse -Filter *.java -File
if ($rSources.Count -gt 0) {
    $rArgFile = Join-Path $out "rsources.txt"
    $rSources | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' } | Set-Content -LiteralPath $rArgFile -Encoding ascii
    & $javac -nowarn -d $rclasses "@$rArgFile"
    Ensure-Success "R siniflari derlenemedi"
}
$compileClasspath.Add($rclasses)

Step "5/8 Kotlin derleniyor"
$classes = Join-Path $out "classes"
Reset-Directory $classes
$kotlinSources = Get-ChildItem -LiteralPath $src -Recurse -Filter *.kt -File
$kArgFile = Join-Path $out "ksources.txt"
$kotlinSources | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' } | Set-Content -LiteralPath $kArgFile -Encoding ascii
$compilerCp = @(
    "kotlin-compiler-embeddable.jar", "kotlin-stdlib.jar", "kotlin-reflect.jar",
    "kotlin-script-runtime.jar", "kotlin-daemon-embeddable.jar", "annotations.jar",
    "trove4j.jar", "kotlinx-coroutines-core-jvm.jar"
) | ForEach-Object { Join-Path $libs $_ }
$compilerLog = Join-Path $out "kotlinc.log"
$kotlinArgs = @(
    "-Xmx3g", "-cp", ($compilerCp -join ";"),
    "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler", "@$kArgFile",
    "-d", $classes, "-classpath", ($compileClasspath -join ";"),
    "-no-jdk", "-no-stdlib", "-no-reflect", "-jvm-target", "17",
    ("-Xplugin=" + (Join-Path $libs "compose-compiler-plugin.jar")), "-nowarn"
)
& $java @kotlinArgs *> $compilerLog
$compilerExit = $LASTEXITCODE
Get-Content -LiteralPath $compilerLog | Where-Object {
    $_ -notmatch '^warning:' -and $_ -notmatch 'is deprecated' -and $_ -notmatch 'never used'
}
if ($compilerExit -ne 0) { throw "Kotlin derlemesi basarisiz (cikis kodu: $compilerExit)" }
$classCount = (Get-ChildItem -LiteralPath $classes -Recurse -Filter *.class -File).Count
if ($classCount -eq 0) { throw "Kotlin derlemesi sinif uretmedi" }
Write-Host "Kotlin dosyasi: $($kotlinSources.Count), uretilen sinif: $classCount"

Step "6/8 Dex uretiliyor"
$dex = Join-Path $out "dex"
Reset-Directory $dex
$merged = Join-Path $out "merged"
Reset-Directory $merged

$allJars = [System.Collections.Generic.List[string]]::new()
Get-ChildItem -LiteralPath $exploded -Recurse -Filter classes.jar -File | ForEach-Object { $allJars.Add($_.FullName) }
Get-ChildItem -LiteralPath $exploded -Recurse -Filter *.jar -File | Where-Object { $_.FullName -match "\\libs\\" } | ForEach-Object { $allJars.Add($_.FullName) }
Get-ChildItem -LiteralPath $deps -Filter *.jar -File | ForEach-Object { $allJars.Add($_.FullName) }
$allJars.Add((Join-Path $libs "kotlin-stdlib.jar"))
$allJars.Add((Join-Path $libs "kotlinx-coroutines-core-jvm.jar"))

$orderedJars = @(
    $allJars | Where-Object { $_ -match 'ktx|java8' } | Sort-Object
    $allJars | Where-Object { $_ -notmatch 'ktx|java8|-(android|jvm)-[0-9]' } | Sort-Object
    $allJars | Where-Object { $_ -match '-(android|jvm)-[0-9]' } | Sort-Object
)
Push-Location $merged
try {
    foreach ($dependencyJar in $orderedJars) {
        if (Test-Path -LiteralPath $dependencyJar) {
            & $jar xf $dependencyJar
            Ensure-Success "Kutuphane siniflari acilamadi: $dependencyJar"
        }
    }
} finally { Pop-Location }

Copy-Item -Path (Join-Path $rclasses '*') -Destination $merged -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item -Path (Join-Path $classes '*') -Destination $merged -Recurse -Force
$metaInf = Join-Path $merged "META-INF"
if (Test-Path -LiteralPath $metaInf) { Remove-Item -LiteralPath $metaInf -Recurse -Force }
Get-ChildItem -LiteralPath $merged -Recurse -Filter module-info.class -File | Remove-Item -Force

$mergedJar = Join-Path $out "merged.jar"
if (Test-Path -LiteralPath $mergedJar) { Remove-Item -LiteralPath $mergedJar -Force }
Push-Location $merged
try {
    & $jar cf $mergedJar .
    Ensure-Success "Birlesik jar olusturulamadi"
} finally { Pop-Location }

# Butunluk kontrolu: bunlardan biri eksikse APK acilista coker. Eksik paketi
# asla imzalama.
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($mergedJar)
try {
    $names = [System.Collections.Generic.HashSet[string]]::new()
    foreach ($entry in $zip.Entries) { [void]$names.Add($entry.FullName) }
} finally { $zip.Dispose() }
$missing = @(
    "com/benimradyom/app/MainActivity.class",
    "com/benimradyom/app/BenimRadyomApp.class",
    "com/benimradyom/app/service/RadioService.class",
    "com/benimradyom/app/engine/RadioEngine.class",
    "androidx/compose/ui/platform/AndroidComposeView.class",
    "androidx/compose/material/icons/filled/RadioKt.class",
    "androidx/media3/exoplayer/ExoPlayer.class",
    "kotlin/jvm/internal/Intrinsics.class"
) | Where-Object { -not $names.Contains($_) }
if ($missing.Count -gt 0) { throw "Birlesik pakette eksik sinif var: $($missing -join ', ')" }
Write-Host "butunluk kontrolu: tamam"

$d8Args = @(
    "-Xmx3g", "-cp", (Join-Path $buildTools "lib\d8.jar"), "com.android.tools.r8.D8",
    "--release", "--min-api", "$minSdk", "--lib", $androidJar,
    "--output", $dex, $mergedJar
)
& $java @d8Args
Ensure-Success "Dex uretilemedi"
$dexFiles = Get-ChildItem -LiteralPath $dex -Filter *.dex -File
if ($dexFiles.Count -eq 0) { throw "Dex dosyasi bulunamadi" }
Write-Host "uretilen dex: $($dexFiles.Count)"

Step "7/8 APK paketleniyor"
$unsigned = Join-Path $out "unsigned.apk"
$aligned = Join-Path $out "aligned.apk"
Copy-Item -LiteralPath $baseApk -Destination $unsigned -Force
Push-Location $dex
try {
    & $jar uf $unsigned @($dexFiles.Name)
    Ensure-Success "Dex APK'ya eklenemedi"
} finally { Pop-Location }
& (Join-Path $buildTools "zipalign.exe") -p -f 4 $unsigned $aligned
Ensure-Success "zipalign basarisiz"

Step "8/8 APK imzalaniyor ve dogrulaniyor"
# Once gecici dosyaya imzala; basariliysa yerine koy. Boylece basarisiz bir
# derleme elinizdeki calisan APK'yi silmez.
$signed = Join-Path $out "signed.apk"
if (Test-Path -LiteralPath $signed) { Remove-Item -LiteralPath $signed -Force }
$signer = Join-Path $buildTools "lib\apksigner.jar"
$signArgs = @(
    "-cp", $signer, "com.android.apksigner.ApkSignerTool", "sign",
    "--ks", $keystore, "--ks-pass", "pass:android", "--key-pass", "pass:android",
    "--ks-key-alias", "androiddebugkey", "--min-sdk-version", "$minSdk",
    "--v4-signing-enabled", "false",
    "--out", $signed, $aligned
)
& $java @signArgs
Ensure-Success "APK imzalanamadi"
& $java -cp $signer com.android.apksigner.ApkSignerTool verify $signed
Ensure-Success "APK imzasi dogrulanamadi"
Move-Item -LiteralPath $signed -Destination $apk -Force
$idsig = "$apk.idsig"
if (Test-Path -LiteralPath $idsig) { Remove-Item -LiteralPath $idsig -Force }

Remove-Item -LiteralPath $lock -Recurse -Force -ErrorAction SilentlyContinue

$sizeMb = [math]::Round((Get-Item -LiteralPath $apk).Length / 1MB, 1)
Write-Host ""
Write-Host "APK HAZIR: $apk ($sizeMb MB) - surum $versionName ($versionCode), imza dogrulandi"
