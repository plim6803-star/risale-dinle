# Risale Dinle v11 — Saniye Eşleşmeli Risale-i Nur Dinleme Uygulaması & Tam Külliyat Veri Arşivi

**Risale Dinle (v11)**, Risale-i Nur Külliyatı'nı dikkat dağıtıcı hiçbir unsur olmadan, saniye-saniye metin-ses eşleşmesiyle, hikmetli konu fihristiyle, kişisel radyo yayın akışıyla ve 26 dahili fon müziğiyle dinlemek üzere tasarlanmış bağımsız bir Web (PWA) ve Android (`.apk`) uygulamasıdır.

Bu depo, uygulamanın hem **çalışan Web/PWA ve Android APK sürümlerini** hem de mevcut depodaki **tüm Risale-i Nur Külliyatı JSON metinlerini, ses-metin senkronizasyon verilerini, PDF fihristlerini, PDF külliyat kitaplarını, Osmanlıca lügat verilerini ve yardımcı referans modüllerini** tek bir çatı altında toplar.

---

## Doğrudan İndirme & Kurulum

- **İmzalı Android Kurulum Paketi (`.apk`):**
  - Depo kök dizininde: [`Risale-Dinle-v11.apk`](./Risale-Dinle-v11.apk) (`com.risaledinle.app`, ~5.5 MB, Android 7.0+ / API 24–34)
  - GitHub Releases: [`v11.0-android` Release İndirme Bağlantısı](https://github.com/plim6803-star/risale-dinle/releases/download/v11.0-android/Risale-Dinle-v11.apk)
- **Web / PWA Uygulaması:**
  - [`index.html`](./index.html) dosyasını doğrudan tarayıcıda açabilir veya herhangi bir statik sunucuyla (`python3 -m http.server 4173`) çalıştırabilirsiniz.

---

## Uygulama Özellikleri (v11)

1. **Sıfır Dikkat Dağıtıcı, 4 Sade Bölüm:**
   - **Konu Fihristi:** 117 temiz konu başlığı altında **6.637 Doğrudan** + **3.534 Dolaylı (Hikmetli Sıralanmış)** = **10.171** saniye-eşleşmeli mikro-kesit. Her kesitte neden o sırada yer aldığını açıklayan Türkçe **Künye** ve tek satırlık **Hikmetli Sıra | Sadece Doğrudan | Dolaylı** filtresi.
   - **Radyo Akışı:** Zamanlı yayın akışı (başlangıç–bitiş saatleri, gece yarısını aşan bloklar, gün seçimi) veya 7/24 kesintisiz akış. Her blok için konu, filtre, fon müziği, fon seviyesi, okuma hızı, okuyucu, **Kesitler Arası Tefekkür Sükûtu (`0–12 sn`)** ve **Otomatik Yayın Bitiş Saati** (15 sn yumuşak kısılma).
   - **AI Liste (Çoklu API Stüdyosu):** **Yerel Semantik AI (Çevrimdışı)** + **Google Gemini, OpenAI, Groq, DeepSeek, OpenRouter ve Özel Uç Nokta** API anahtarı desteği, canlı bağlantı testi, künyeli/hikmetli akış üretimi ve tek tıkla Radyoya ekleme.
   - **Tüm Dersler:** 261 tam ses kaydı (58.24 saat) ve 2.373 mikro-kesit.
2. **26 Dahili Fon Müziği + Kur'an-ı Kerim & Besmele Hürmet Susturması:**
   - 14 Makamlı Ney/Saz, 7 Tabiat/Rahmet, 5 Derin Odak fonu + Makam Döngüsü + Kendi MP3/WAV fonunu kalıcı yükleme (IndexedDB).
   - Arapça ayet, hadis ve Besmele tilavetlerinden **2.5 saniye önce** fon müziğini yumuşakça sıfıra indirir, tilavet bitiminde tekrar eski seviyesine döndürür.
3. **2 Katmanlı Ayarlar Stüdyosu:**
   - 4 tek-tık dinleme profili (*Klasik Medrese*, *Gece Tefekkürü*, *Süratli Mütalaa*, *Saf Metin & Ses*), 0–10 bağımsız ses mikseri, sarma adımı, uyku zamanlayıcısı, 3 tema (*Parşömen*, *Sepya*, *Gece*), yer imleri ve JSON yedekleme.

---

## Depo Dizin Yapısı ve Veri Arşivi

```text
risale-dinle/
├── index.html                  # Tek dosyalık bağımsız Risale Dinle v11 Web/PWA uygulaması
├── manifest.json               # PWA kurulum manifestosu
├── sw.js                       # Çevrimdışı önbellek Service Worker
├── icon.svg                    # Uygulama ikonu
├── Risale-Dinle-v11.apk        # İmzalı Android kurulum paketi (v11.0)
├── android-app/                # Yerel Android WebView kaynak kodu (com.risaledinle.app) & build_apk.sh
├── scripts/
│   ├── build_catalog.py        # Külliyat metin-ses hizalama, 117 konu tarayıcısı, Hikmetli sıra & Künye motoru
│   └── build_app.py            # Tek dosyalık PWA/HTML uygulama derleyicisi
├── data/
│   ├── mp3-durations.json      # 261 MP3 ses kaydının saniye cinsinden kesin süre haritası
│   ├── risale-catalog.json     # 261 ders ve alt başlık kataloğu
│   ├── segments-index.json     # 2.373 saniye-hizalı mikro-kesit indeksi
│   └── topic-playlists.json    # 117 konu başlığı ve 10.171 künyeli kesit listesi
├── kulliyat-verileri/
│   ├── kulliyat/               # 14 Risale-i Nur Külliyatı kitabının tam JSON metinleri (Sözler, Mektubat, Lem'alar, Şualar vb.)
│   ├── sync/                   # Saniye-saniye okuyucu ses-metin hizalama (koseoglu/sozler & ASR önbelleği)
│   ├── lugat/                  # Osmanlıca-Türkçe Risale lügat verileri (lugat-500.json, 3000 kelime vezin/kök aileleri, dualar)
│   ├── ses-listeleri/          # Sorularla Risale ses kayıt listeleri ve eşleştirmeleri
│   └── arsiv-verileri/         # Ek veri tabanı ve tefekkür arşiv JSON dosyaları
├── kaynak-pdfler/
│   ├── fihristler/             # Risale-i Nur Konu Fihristi, Fihrist Risalesi, İki Boyutlu Fihrist, Soru-Cevap Fihristi, Namaz Dersleri PDF'leri
│   ├── kulliyat/               # 14 Risale-i Nur Külliyatı kitabının orijinal PDF nüshaları ve Üstad Biyografisi
│   └── osmanlica/              # Ezberlenecek 500 Kelime ve Kolay Osmanlıca Rehberi (Bâblar) PDF'leri
└── referans-moduller/
    ├── nur-u-sema-lib/         # Külliyat ayrıştırıcıları, eRisale/SorularlaRisale ses araçları ve yardımcı TypeScript/JS betikleri
    └── benim-radyom-v6/        # Android radyo yayın akışı motoru ve bilgi kataloğu referans kaynakları
```

---

## Yeniden Derleme Komutları

Katalog verilerini ve `index.html` paketini sıfırdan derlemek için:

```bash
python3 scripts/build_catalog.py
python3 scripts/build_app.py
```

Android `.apk` paketini derlemek için (Android SDK kurulu ortamda veya GitHub Actions üzerinden):

```bash
./android-app/build_apk.sh
```
