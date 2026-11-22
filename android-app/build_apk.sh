#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-/usr/local/lib/android/sdk}}"

BUILD_TOOLS="$(ls -d "$SDK/build-tools/"* | sort -V | tail -n 1)"
PLATFORM_JAR="$(ls "$SDK/platforms/"android-*/android.jar | sort -V | tail -n 1)"

echo "Using BUILD_TOOLS: $BUILD_TOOLS"
echo "Using PLATFORM_JAR: $PLATFORM_JAR"

AAPT2="$BUILD_TOOLS/aapt2"
D8="$BUILD_TOOLS/d8"
ZIPALIGN="$BUILD_TOOLS/zipalign"
APKSIGNER="$BUILD_TOOLS/apksigner"

WORK="$DIR/build"
rm -rf "$WORK"
mkdir -p "$WORK/compiled_res" "$WORK/gen" "$WORK/classes" "$WORK/dex" "$DIR/assets"

# Copy latest single-file Risale Dinle v11 bundle into Android assets
cp "$DIR/../index.html" "$DIR/assets/index.html"
cp "$DIR/../manifest.json" "$DIR/assets/manifest.json"
cp "$DIR/../icon.svg" "$DIR/assets/icon.svg"

# 1. Compile resources
"$AAPT2" compile --dir "$DIR/res" -o "$WORK/compiled_res/res.zip"

# 2. Link resources + AndroidManifest.xml + assets (store .html/.json uncompressed for instant WebView load)
"$AAPT2" link \
  -I "$PLATFORM_JAR" \
  --manifest "$DIR/AndroidManifest.xml" \
  -A "$DIR/assets" \
  -0 html -0 json -0 svg \
  --java "$WORK/gen" \
  -o "$WORK/unaligned.apk" \
  "$WORK/compiled_res/res.zip"

# 3. Compile Java sources (R.java + MainActivity.java) targeting Java 8 bytecode
javac --release 8 \
  -cp "$PLATFORM_JAR" \
  -d "$WORK/classes" \
  "$WORK/gen/com/risaledinle/app/R.java" \
  "$DIR/src/com/risaledinle/app/MainActivity.java"

# 4. Convert .class files to classes.dex
CLASS_FILES=$(find "$WORK/classes" -name "*.class")
"$D8" --lib "$PLATFORM_JAR" --min-api 24 --output "$WORK/dex" $CLASS_FILES

# 5. Add classes.dex to the root of unaligned.apk
(cd "$WORK/dex" && zip -q -u "$WORK/unaligned.apk" classes.dex)

# 6. Align APK on 4-byte boundaries
"$ZIPALIGN" -f -p 4 "$WORK/unaligned.apk" "$WORK/aligned.apk"

# 7. Generate keystore if not present and sign APK (v1 + v2 + v3 schemes)
KEYSTORE="$WORK/release.keystore"
keytool -genkeypair -v \
  -keystore "$KEYSTORE" \
  -alias risaledinle \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass risale123 -keypass risale123 \
  -dname "CN=Risale Dinle, OU=Risale-i Nur, O=RisaleDinle, L=Istanbul, S=Istanbul, C=TR"

OUT_APK="$DIR/../Risale-Dinle-v11.apk"
"$APKSIGNER" sign \
  --ks "$KEYSTORE" \
  --ks-key-alias risaledinle \
  --ks-pass pass:risale123 \
  --key-pass pass:risale123 \
  --out "$OUT_APK" \
  "$WORK/aligned.apk"

"$APKSIGNER" verify --verbose "$OUT_APK"
ls -lh "$OUT_APK"
echo "Successfully built signed Android APK -> $OUT_APK"
