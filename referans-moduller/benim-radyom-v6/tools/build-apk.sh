#!/usr/bin/env bash
# ----------------------------------------------------------------------------
# Benim Radyom - Gradle'siz APK uretimi
#
# Bu makinede Gradle calisamiyor (Cortex XDR, Java NIO loopback soketlerini
# engelliyor). Bu betik ayni isi Android SDK araclarini dogrudan cagirarak yapar:
#   aapt2 (kaynaklar) -> kotlinc (Kotlin) -> d8 (dex) -> zipalign + apksigner
# ----------------------------------------------------------------------------
set -u

ROOT="C:/Users/masallahresulozcan/Desktop/Benim Radiyom"
TOOLS="C:/Users/masallahresulozcan/DevTools"
SDK="$TOOLS/android-sdk"
BT="$SDK/build-tools/36.0.0"
ANDROID_JAR="$SDK/platforms/android-36/android.jar"
JAVA="$TOOLS/jdk-17.0.20.1+1/bin/java"
JAVAC="$TOOLS/jdk-17.0.20.1+1/bin/javac"
JAR="$TOOLS/jdk-17.0.20.1+1/bin/jar"
LIBS="$TOOLS/libs"
DEPS="$TOOLS/deps"
OUT="$TOOLS/apkbuild"

SRC="$ROOT/app/src/main/java"
RES="$ROOT/app/src/main/res"
MANIFEST="$ROOT/app/src/main/AndroidManifest.xml"
KEYSTORE="$ROOT/debug.keystore"

MIN_SDK=26
TARGET_SDK=36

step() { echo ""; echo "=========== $* ==========="; }
die()  { echo "HATA: $*" >&2; exit 1; }

# ----------------------------------------------------------------------------
# Kilit: ayni anda iki derleme ayni $OUT klasorune yazarsa biri digerinin ara
# dosyalarini siler ve IMZALI AMA ICI EKSIK bir APK cikar (acilista coker).
# build-apk.ps1 de ayni kilidi kullanir. mkdir atomiktir.
# ----------------------------------------------------------------------------
LOCK="$OUT.lock"
if ! mkdir "$LOCK" 2>/dev/null; then
    # 30 dakikadan eski kilit yarim kalmis bir derlemeden kalmistir.
    if [ -n "$(find "$LOCK" -maxdepth 0 -mmin +30 2>/dev/null)" ]; then
        rm -rf "$LOCK"; mkdir "$LOCK" || die "kilit alinamadi"
    else
        die "Baska bir derleme calisiyor ($LOCK). Bitmesini bekleyin."
    fi
fi
trap 'rm -rf "$LOCK"' EXIT

# Surum tek yerden gelir: app/build.gradle.kts
GRADLE="$ROOT/app/build.gradle.kts"
VERSION_CODE=$(grep -oE 'versionCode\s*=\s*[0-9]+' "$GRADLE" | grep -oE '[0-9]+$')
VERSION_NAME=$(grep -oE 'versionName\s*=\s*"[^"]+"' "$GRADLE" | sed -E 's/.*"([^"]+)"/\1/')
[ -n "$VERSION_CODE" ] && [ -n "$VERSION_NAME" ] || die "surum build.gradle.kts'ten okunamadi"
echo "Surum: $VERSION_NAME ($VERSION_CODE)"

# ----------------------------------------------------------------------------
step "1/8  AAR paketleri aciliyor"
# ----------------------------------------------------------------------------
mkdir -p "$OUT/exploded"
for aar in "$DEPS"/*.aar; do
    [ -e "$aar" ] || continue
    name=$(basename "$aar" .aar)
    dir="$OUT/exploded/$name"
    if [ ! -d "$dir" ]; then
        mkdir -p "$dir"
        unzip -q -o "$aar" -d "$dir" 2>/dev/null
    fi
done
echo "acilan AAR: $(ls -1 "$OUT/exploded" 2>/dev/null | wc -l)"

# Derleme sinif yolu: tum classes.jar + duz jar'lar + kotlin stdlib
CP="$ANDROID_JAR"
for j in "$OUT/exploded"/*/classes.jar; do [ -e "$j" ] && CP="$CP;$j"; done
for j in "$OUT/exploded"/*/libs/*.jar; do [ -e "$j" ] && CP="$CP;$j"; done
for j in "$DEPS"/*.jar; do [ -e "$j" ] && CP="$CP;$j"; done
CP="$CP;$LIBS/kotlin-stdlib.jar;$LIBS/kotlinx-coroutines-core-jvm.jar"

# ----------------------------------------------------------------------------
step "2/8  Kaynaklar derleniyor (aapt2 compile)"
# ----------------------------------------------------------------------------
mkdir -p "$OUT/flat"
rm -f "$OUT/flat"/*.zip

"$BT/aapt2.exe" compile --dir "$RES" -o "$OUT/flat/app.zip" || die "app kaynaklari derlenemedi"

i=0
EXTRA_PKGS=""
for dir in "$OUT/exploded"/*/; do
    name=$(basename "$dir")
    if [ -d "$dir/res" ] && [ -n "$(ls -A "$dir/res" 2>/dev/null)" ]; then
        i=$((i+1))
        "$BT/aapt2.exe" compile --dir "$dir/res" -o "$OUT/flat/lib_$i.zip" 2>/dev/null
    fi
    # Kutuphanenin kendi R sinifi icin paket adi
    if [ -f "$dir/AndroidManifest.xml" ]; then
        pkg=$(grep -o 'package="[^"]*"' "$dir/AndroidManifest.xml" | head -1 | sed 's/package="//;s/"//')
        [ -n "$pkg" ] && EXTRA_PKGS="$EXTRA_PKGS:$pkg"
    fi
done
EXTRA_PKGS="${EXTRA_PKGS#:}"
echo "derlenen kutuphane kaynagi: $i"

# ----------------------------------------------------------------------------
step "3/8  Kaynaklar baglaniyor (aapt2 link)"
# ----------------------------------------------------------------------------
mkdir -p "$OUT/gen"
rm -rf "$OUT/gen"; mkdir -p "$OUT/gen"

LINK_ARGS=()
for z in "$OUT/flat"/*.zip; do LINK_ARGS+=(-R "$z"); done

"$BT/aapt2.exe" link \
    -o "$OUT/base.apk" \
    -I "$ANDROID_JAR" \
    --manifest "$MANIFEST" \
    "${LINK_ARGS[@]}" \
    --java "$OUT/gen" \
    --min-sdk-version $MIN_SDK \
    --target-sdk-version $TARGET_SDK \
    --version-code "$VERSION_CODE" --version-name "$VERSION_NAME" \
    --auto-add-overlay \
    --no-version-vectors \
    ${EXTRA_PKGS:+--extra-packages "$EXTRA_PKGS"} \
    || die "aapt2 link basarisiz"
echo "uretilen R dosyalari: $(find "$OUT/gen" -name "R.java" | wc -l)"

# ----------------------------------------------------------------------------
step "4/8  R siniflari derleniyor (javac)"
# ----------------------------------------------------------------------------
mkdir -p "$OUT/rclasses"; rm -rf "$OUT/rclasses"; mkdir -p "$OUT/rclasses"
# Argüman dosyalarinda yollar tirnak icinde olmali: proje yolunda bosluk var.
find "$OUT/gen" -name "*.java" | sed 's/.*/"&"/' > "$OUT/rsources.txt"
if [ -s "$OUT/rsources.txt" ]; then
    # R siniflari sadece "public static final int" alanlari icerir; Android
    # API'sine dokunmaz, bu yuzden bootclasspath gerekmez.
    "$JAVAC" -nowarn -d "$OUT/rclasses" "@$OUT/rsources.txt" 2>&1 | head -5
fi
CP="$CP;$OUT/rclasses"

# ----------------------------------------------------------------------------
step "5/8  Kotlin derleniyor (kotlinc + Compose eklentisi)"
# ----------------------------------------------------------------------------
KCP="$LIBS/kotlin-compiler-embeddable.jar;$LIBS/kotlin-stdlib.jar;$LIBS/kotlin-reflect.jar"
KCP="$KCP;$LIBS/kotlin-script-runtime.jar;$LIBS/kotlin-daemon-embeddable.jar"
KCP="$KCP;$LIBS/annotations.jar;$LIBS/trove4j.jar;$LIBS/kotlinx-coroutines-core-jvm.jar"

mkdir -p "$OUT/classes"; rm -rf "$OUT/classes"; mkdir -p "$OUT/classes"
find "$SRC" -name "*.kt" | sed 's/.*/"&"/' > "$OUT/ksources.txt"
echo "Kotlin dosyasi: $(wc -l < "$OUT/ksources.txt")"

"$JAVA" -Xmx3g -cp "$KCP" org.jetbrains.kotlin.cli.jvm.K2JVMCompiler \
    @"$OUT/ksources.txt" \
    -d "$OUT/classes" \
    -classpath "$CP" \
    -no-jdk -no-stdlib -no-reflect \
    -jvm-target 17 \
    -Xplugin="$LIBS/compose-compiler-plugin.jar" \
    -nowarn \
    2>&1 | grep -viE "^warning:|is deprecated|never used" | head -60

if [ -z "$(find "$OUT/classes" -name '*.class' 2>/dev/null | head -1)" ]; then
    die "Kotlin derlemesi cikti uretmedi"
fi
echo "uretilen sinif: $(find "$OUT/classes" -name '*.class' | wc -l)"

# ----------------------------------------------------------------------------
step "6/8  Dex uretiliyor (d8)"
# ----------------------------------------------------------------------------
mkdir -p "$OUT/dex"; rm -rf "$OUT/dex"; mkdir -p "$OUT/dex"

# AndroidX'te bazi kutuphaneler (-ktx, java8) yeni surumlerde ana pakete
# tasindi; bagimlilik agaci hem eskisini hem yenisini getirebiliyor ve d8
# "ayni sinif iki kez tanimli" diyor. Hepsini TEK bir agaca aciyoruz:
#   once eski -ktx paketleri, sonra duz paketler, en son -android/-jvm
# Boylece yeni olan eskisinin uzerine yazar ve mukerrer kalmaz.
MERGED="$OUT/merged"
rm -rf "$MERGED"; mkdir -p "$MERGED"

unpack() {
    while read -r j; do
        [ -f "$j" ] || continue
        unzip -q -o "$j" -d "$MERGED" -x "META-INF/*" "module-info.class" "**/module-info.class" 2>/dev/null
    done
}

ALLJARS="$OUT/alljars.txt"
{
    ls -1 "$OUT"/exploded/*/classes.jar 2>/dev/null
    ls -1 "$OUT"/exploded/*/libs/*.jar 2>/dev/null
    ls -1 "$DEPS"/*.jar 2>/dev/null
    echo "$LIBS/kotlin-stdlib.jar"
    echo "$LIBS/kotlinx-coroutines-core-jvm.jar"
} > "$ALLJARS"

# NOT: desen tire ile basladiginda grep onu secenek sanar; -e ile veriyoruz.
grep -E -e "ktx|java8" "$ALLJARS" | sort -V | unpack
grep -vE -e "ktx|java8|-(android|jvm)-[0-9]" "$ALLJARS" | sort -V | unpack
grep -E -e "-(android|jvm)-[0-9]" "$ALLJARS" | sort -V | unpack

# Uygulamanin kendi siniflari en son: her zaman onlar gecerli.
cp -r "$OUT/rclasses/." "$MERGED/" 2>/dev/null
cp -r "$OUT/classes/." "$MERGED/" 2>/dev/null
rm -rf "$MERGED/META-INF"
find "$MERGED" -name "module-info.class" -delete 2>/dev/null
echo "birlestirilen sinif: $(find "$MERGED" -name '*.class' | wc -l)"

rm -f "$OUT/merged.jar"
(cd "$MERGED" && "$JAR" cf "$OUT/merged.jar" .) || die "birlesik jar olusturulamadi"

# Butunluk kontrolu: bunlardan biri eksikse APK acilista coker. Eksik paketi
# asla imzalama.
MISSING=""
for c in \
    "com/benimradyom/app/MainActivity.class" \
    "com/benimradyom/app/BenimRadyomApp.class" \
    "com/benimradyom/app/service/RadioService.class" \
    "com/benimradyom/app/engine/RadioEngine.class" \
    "androidx/compose/ui/platform/AndroidComposeView.class" \
    "androidx/compose/material/icons/filled/RadioKt.class" \
    "androidx/media3/exoplayer/ExoPlayer.class" \
    "kotlin/jvm/internal/Intrinsics.class"; do
    unzip -l "$OUT/merged.jar" "$c" 2>/dev/null | grep -q "$c" || MISSING="$MISSING $c"
done
[ -z "$MISSING" ] || die "birlesik pakette eksik sinif var:$MISSING"
echo "butunluk kontrolu: tamam"

"$JAVA" -Xmx3g -cp "$BT/lib/d8.jar" com.android.tools.r8.D8 \
    --release \
    --min-api $MIN_SDK \
    --lib "$ANDROID_JAR" \
    --output "$OUT/dex" \
    "$OUT/merged.jar" 2>&1 | head -30

ls -1 "$OUT/dex"/*.dex >/dev/null 2>&1 || die "dex uretilemedi"
echo "uretilen dex: $(ls -1 "$OUT/dex"/*.dex | wc -l)"

# ----------------------------------------------------------------------------
step "7/8  APK paketleniyor"
# ----------------------------------------------------------------------------
cp "$OUT/base.apk" "$OUT/unsigned.apk"
(cd "$OUT/dex" && "$JAR" uf "$OUT/unsigned.apk" classes*.dex) || die "dex apk'ya eklenemedi"

rm -f "$OUT/aligned.apk"
"$BT/zipalign.exe" -p -f 4 "$OUT/unsigned.apk" "$OUT/aligned.apk" || die "zipalign basarisiz"

# ----------------------------------------------------------------------------
step "8/8  Imzalaniyor"
# ----------------------------------------------------------------------------
# Once gecici dosyaya imzala; basariliysa yerine koy. Boylece basarisiz bir
# derleme elinizdeki calisan APK'yi silmez.
SIGNED="$OUT/signed.apk"
rm -f "$SIGNED" "$SIGNED.idsig"
"$JAVA" -cp "$BT/lib/apksigner.jar" com.android.apksigner.ApkSignerTool sign \
    --ks "$KEYSTORE" \
    --ks-pass pass:android \
    --key-pass pass:android \
    --ks-key-alias androiddebugkey \
    --min-sdk-version $MIN_SDK \
    --v4-signing-enabled false \
    --out "$SIGNED" \
    "$OUT/aligned.apk" || die "imzalama basarisiz"
"$JAVA" -cp "$BT/lib/apksigner.jar" com.android.apksigner.ApkSignerTool verify "$SIGNED" \
    || die "imza dogrulanamadi"
mv -f "$SIGNED" "$ROOT/BenimRadyom.apk"
rm -f "$ROOT/BenimRadyom.apk.idsig"

echo ""
echo "############################################"
ls -lh "$ROOT/BenimRadyom.apk"
echo "Surum $VERSION_NAME ($VERSION_CODE) - imza dogrulandi"
echo "############################################"
