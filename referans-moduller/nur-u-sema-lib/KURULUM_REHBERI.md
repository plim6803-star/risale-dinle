# Nûr-u Sema | Başka Bilgisayarda Kurulum, Ses İndirme ve Android APK Rehberi

Bu paket, **Nûr-u Sema (Risale-i Nur Sesli Külliyat, Lügat, AI Şerh ve Sinema Modu)** uygulamasının tüm kaynak kodlarını (`src/`), veri tabanını (`public/data/`), tüm PDF Külliyat ve Fihrist kaynaklarını (`public/kaynaklar/`) ve otomasyon araçlarını (`scripts/`) içerir.

Taşıma kolaylığı için büyük boyutlu ses dosyaları (`public/ses/*.mp3`, `*.wav`) ve `node_modules` pakete dahil edilmemiştir; aşağıdaki tek komutla yeni bilgisayarda otomatik olarak indirilir ve üretilir.

---

## 1. Yeni Bilgisayarda Hızlı Kurulum (Windows)

1. Bilgisayarınızda **Node.js (v20 veya üzeri)** kurulu olduğundan emin olun (`https://nodejs.org`).
2. ZIP dosyasını bir klasöre çıkartın.
3. Klasörün içindeki **`KURULUM_VE_SES_INDIR.bat`** dosyasına çift tıklayın.
   - Bu dosya sırasıyla:
     1. `npm install` ile gerekli yazılım kütüphanelerini kurar,
     2. `node scripts/setup-audio.mjs` ile **6 makam fon müziğini** yerel olarak üretir ve **Sözler MP3 ses kayıtlarını** otomatik indirir,
     3. Uygulamayı `http://localhost:3000` adresinde başlatır.

---

## 2. Terminal Üzerinden Manuel Komutlar

İsterseniz PowerShell veya CMD açıp şu komutları da çalıştırabilirsiniz:

```bash
# 1. Paketleri kur
npm install

# 2. Fon müziklerini üret + İlk 10 Söz MP3 kayıtlarını indir
npm run setup-audio

# (İsteğe bağlı) Tüm Sözler ve tüm okuyucuların MP3 kayıtlarını indirmek için:
npm run setup-audio -- --all

# 3. Uygulamayı başlat
npm run dev
```

---

## 3. Android APK / Mobil Uygulama Olarak Kurulum

Uygulamayı Android telefon veya tablette iki şekilde kullanabilirsiniz:

### Yöntem A: PWA (Doğrudan Ana Ekrana Ekleme - APK Gerektirmez)
Uygulama `public/manifest.json` ile tam PWA uyumludur. Bilgisayarınızla aynı Wi-Fi ağındayken telefonunuzun Chrome tarayıcısından bilgisayarın yerel IP adresine (ör. `http://192.168.1.X:3000`) girip **"Ana Ekrana Ekle / Uygulamayı Yükle"** diyerek tam ekran mobil uygulama olarak kullanabilirsiniz.

### Yöntem B: Capacitor ile Yerel Android `.apk` Üretme
Proje içerisinde hazır `capacitor.config.json` mevcuttur. Android Studio kurulu bir bilgisayarda `.apk` dosyası üretmek için:

```bash
npm install @capacitor/core @capacitor/cli @capacitor/android
npx cap add android
npx cap sync
npx cap open android
```
Android Studio açıldığında **Build > Build Bundle(s) / APK(s) > Build APK(s)** menüsünden kurulum `.apk` dosyanızı alabilirsiniz.
