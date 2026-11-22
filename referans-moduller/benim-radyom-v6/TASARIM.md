# Benim Radyom — Mevcut Durum Analizi ve Yeni Tasarım Taslağı

Tarih: 2026-09-28
İncelenen kod: `agon-agent_2-43979e00.zip` (com.agon.app, ~2700 satır Kotlin/Compose)

> **Uygulama durumu (v5):** Bu belgedeki eski sürüm sorunları giderildi ve
> taslak Android uygulamasına dönüştürüldü. Cihaz içi müzik tarama, bağlı klasör,
> katmanlı program editörü, MP3/TTS/kayıt anonsları, fon müziği, çapraz geçiş,
> namaz vakti, kurallar, yedekleme ve uyku zamanlayıcısına ek olarak Risale-i Nur
> konu fihristi, ayet/hadis kaynak kataloğu, yerel/API destekli akıllı yayın
> üretimi ve karma akış editörü çalışan kaynakta yer alır. v4 ayrıca SRT/VTT
> ile kesin, TXT ile yaklaşık ve isteğe bağlı AI ile otomatik okuma takibi;
> yerel önbellekli, okunan temaya göre ilerleyen Tefekkür Modu içerir. v5'te
> doğrudan HTTPS medya bağlantıları fon listesine eklenebilir; Risale kaydındaki
> Arapça ayet tilaveti bölgeleri önceden analiz edilip fon 2,5 saniye önceden
> yumuşakça susturulur ve tilavet sonrasında geri açılır. Zamanlar elle
> düzenlenebilir ve yayın sırasında yeniden AI çağrısı gerekmez.
> Güncel kullanım bilgisi `KURULUM.md` dosyasındadır.

> Aşağıdaki “mevcut sorun” bölümü ilk taslağın tarihsel analizidir; v5'in güncel
> eksik listesi değildir.

---

## BÖLÜM 1 — Mevcut uygulamanın durumu

### 1.1 Uygulama şu an çalışmıyor (kritik)

`app/src/main/AndroidManifest.xml` içinde **sadece MainActivity** tanımlı. Eksikler:

| Eksik | Sonuç |
|---|---|
| `<service RadioService>` tanımı yok | `startForegroundService()` çağrısı **çöker** → hiçbir şey çalmaz |
| `<receiver RadioAlarmReceiver>` yok | Zamanlayıcılar hiç tetiklenmez |
| `<receiver RadioWidget>` yok | Ana ekran widget'ı hiç görünmez |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK` izni yok | Servis başlatılamaz |
| `POST_NOTIFICATIONS` izni yok | `MainActivity` izin istiyor ama manifestte yok → istek sessizce reddedilir |
| `READ_MEDIA_AUDIO` izni yok | Cihazdaki müziklere hiç erişilemez |
| `SCHEDULE_EXACT_ALARM`, `WAKE_LOCK`, `RECEIVE_BOOT_COMPLETED` yok | Zamanlama güvenilmez |

Yani "uygulamayı kurdum ama çalmıyor / garip davranıyor" hissinin ana sebebi bu.

### 1.2 Eklenen müzikler bir süre sonra kayboluyor (kritik)

`RadioViewModel.addTrackFromUri()` SAF (dosya seçici) URI'sini kaydediyor ama
**`contentResolver.takePersistableUriPermission()` çağrılmıyor.**

Sonuç: uygulamayı kapatıp açınca ya da telefonu yeniden başlatınca kaydedilmiş
parçaların URI izni düşer, çalmaya çalışınca sessizlik olur. Liste dolu görünür,
müzik gelmez.

### 1.3 Müzik eklemek neden bu kadar yorucu — sizin asıl şikâyetiniz

Şu anki akış:

```
Listeler sekmesi → liste seç → "Ses Ekle" → Android dosya seçici açılır
→ Dosya yöneticisinde klasör klasör gez → dosyaları seç → onayla → geri dön
→ bir sonraki liste için baştan tekrarla
```

Sorunlar:
- Uygulamanın **kendi içinde cihazdaki müzikleri gösteren bir ekran yok**.
- Klasörün tamamını tek dokunuşla ekleme yok.
- Sonradan o klasöre eklediğiniz yeni mp3'ler otomatik gelmiyor, elle tekrar eklemek gerekiyor.
- Arama yok, sanatçı/albüm/klasör gruplaması yok.
- Önce liste seçmeden "Ses Ekle" çalışmıyor (sessizce hiçbir şey olmuyor).

**Cevap: Evet, mümkün.** Android'in `MediaStore` API'si ile uygulama, cihazdaki
tüm ses dosyalarını (mp3, m4a, wav, ogg, opus) klasör/sanatçı/albüm/süre
bilgisiyle beraber **kendi arayüzünde** listeleyebilir. Dosya seçiciye hiç
çıkmadan, çoklu seçim kutucuklarıyla, "bu klasörün tamamını ekle" diyerek.
Ayrıca `MediaStore` URI'leri kalıcıdır — 1.2'deki kaybolma sorunu da otomatik çözülür.

### 1.4 Radyoyu "sadece başlat" demek şu an mümkün değil

Anons araya girsin istiyorsanız yapmanız gerekenler:

```
Otomasyon → Anons sekmesi → anons oluştur → metin yaz → hız/perde ayarla → kaydet
→ Akış sekmesi → yeni akış → adım ekle (playlist) → tekrar sayısı → adım ekle (anons)
→ ... her adım için tekrarla → kaydet → akışı aktifleştir → Radyo sekmesi → Başlat
```

~10 adım. Üstelik akış aktif değilse `buildRoundRobin()` devreye giriyor ve bu
fonksiyon **her listeden sadece 1 parça** alıyor — 5 listeniz varsa kuyruk 5
parçalık oluyor, sürekli yeniden kuruluyor. Davranış öngörülemez oluyor.

### 1.5 Anonslar sadece TTS (robot ses)

`Announcement` modelinde yalnızca `text` var. **Kendi ses kaydınızı anons olarak
kullanamıyorsunuz.** Sizin hedefiniz "kendi ses kayıtlarımdan anons" olduğu için
bu doğrudan engel.

### 1.6 Diğer tespitler

- **Zamanlayıcı günleri çalışmıyor:** `Schedule.repeatDays` ("1111111") veritabanına yazılıyor ama `RadioScheduler` bunu hiç okumuyor. Alarm tek seferlik kuruluyor, tetiklendikten sonra bir daha kurulmuyor → zamanlayıcı bir kez çalışıp ölüyor.
- **Yeniden başlatmada her şey kayboluyor:** `BOOT_COMPLETED` alıcısı yok, alarmlar silinir.
- **Mimari:** `RadioViewModel` her oluşturulduğunda **yeni bir** `RadioPlayerController` yaratıyor ve singleton `instance`'ı eziyor. Controller servisin içinde yaşamalı, ekranın içinde değil. Ekran kapanınca (`onCleared` → `ctrl.release()`) çalan radyo susar.
- **Coroutine sızıntısı:** `selectPlaylist()` her çağrıldığında yeni bir `collect` başlatıyor, eskisini iptal etmiyor. 10 liste gezerseniz 10 aktif akış olur, listeler birbirine karışır.
- **Ses odağı (audio focus) yok:** telefon çalınca, bildirim gelince radyo susmuyor/kısılmıyor.
- **Geçiş yok:** parçalar arasında crossfade/gapless yok, konuşma ve müzik arasında ses seviyesi zıplaması olur.
- **Uzun kayıtlarda kaldığı yer hatırlanmıyor:** 45 dakikalık bir Risale dersi her seferinde baştan başlar.
- **İsim karmaşası:** paket `com.agon.app`, uygulama adı "Phase One", proje adı "AgonApp".

---

## BÖLÜM 2 — Hayalinizdeki uygulamanın taslağı

### 2.1 Temel fikir: "Liste" değil, "İçerik Türü"

En büyük basitleştirme bu. Kullanıcı elle akış kurmak yerine, her listeye bir
**tür** verir ve uygulama radyoyu kendi kurar.

| Tür | Ne için | Davranış |
|---|---|---|
| 🎵 **Ezgi / İlahi** (MUSIC) | ilahi, ezgi, enstrümantal | Normal çalar, karıştırılabilir |
| 🎙 **Ders / Sohbet** (SPEECH) | Risale-i Nur, Kur'an, hadis, sohbet | Uzun; kaldığı yerden devam eder, karıştırılmaz, sıra korunur |
| 📢 **Anons** (SPOT) | motivasyon sözü, duyuru, hatırlatma | Kısa; araya serpiştirilir |
| 🕌 **Ezan / Vakit** (PRAYER) | ezan ve vakit kayıtları | Normal karışıma girmez; yalnız seçilen namaz vaktinde çalar |
| 🌙 **Fon Müzik** (BED) | enstrümantal fon | Konuşmanın **altında** kısık sesle döner |

Bu dört tür genel amaçlıdır — bir spor radyosu da, bir çocuk radyosu da aynı
yapıyla kurulabilir. Sizin kullanımınız için varsayılan hazır gelir.

### 2.2 Elle akış kurma yerine: "Yayın Reçetesi"

`FlowStep` listesini elle dizmek yerine tek ekranda birkaç kaydırma çubuğu:

```
┌─ Yayın Reçetesi ───────────────────────────┐
│                                            │
│  Karışım                                   │
│  🎵 Ezgi / İlahi      ████████░░  %40      │
│  🎙 Ders / Sohbet     ██████░░░░  %30      │
│  📢 Motivasyon        ████░░░░░░  %20      │
│  🕌 Kur'an            ██░░░░░░░░  %10      │
│                                            │
│  Araya anons          [ 3 ] parçada bir    │
│  Saat başı            [✓] Kur'an tilaveti  │
│  Fon müzik            [✓] konuşma altında  │
│                                            │
│  Hazır şablonlar:                          │
│  [Sabah] [Gündüz] [Akşam] [Gece] [+]       │
└────────────────────────────────────────────┘
```

Kuyruk motoru bu ağırlıklara göre sonsuz üretir — kullanıcı adım dizmez.
Yine de "elle sıra kurmak istiyorum" diyen için **Gelişmiş** sekmesinde eski
adım editörü durur (genel kapsamlı kalması için).

### 2.3 Kütüphane ekranı — cihazdaki müzikler doğrudan içeride

```
┌─ Kütüphane ────────────────────────────────┐
│  [ 🔍 Ara...                             ] │
│  ( Klasörler )( Şarkılar )( Sanatçı )( Listelerim ) │
│                                            │
│  📁 Risale Dersleri          142 dosya  [+]│
│  📁 İlahiler                  89 dosya  [+]│
│  📁 Kuran-ı Kerim             114 dosya [+]│
│  📁 Kayıtlarım                 12 dosya [+]│
│  📁 Download                   47 dosya [+]│
│                                            │
│  ── klasöre girince ──                     │
│  [✓] 01 - Birinci Söz.mp3          12:04   │
│  [✓] 02 - İkinci Söz.mp3           08:41   │
│  [ ] 03 - Üçüncü Söz.mp3           09:15   │
│                                            │
│  [ 3 seçili → Listeye Ekle ▾ ]  [Tümünü Seç]│
└────────────────────────────────────────────┘
```

Önemli özellik — **Klasör Bağlama:** bir klasörü bir listeye "bağlarsanız",
o klasöre telefonla sonradan attığınız her yeni mp3 uygulama açılınca otomatik
listeye düşer. Bir daha hiç dosya eklemekle uğraşmazsınız.

Teknik karşılığı:
- İzin: `READ_MEDIA_AUDIO` (Android 13+) / `READ_EXTERNAL_STORAGE` (12 ve altı)
- Sorgu: `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI`
- Alanlar: `_ID, TITLE, ARTIST, ALBUM, DURATION, BUCKET_DISPLAY_NAME, RELATIVE_PATH, DATE_ADDED`
- Klasörler = `BUCKET_DISPLAY_NAME` ile gruplama
- URI'ler kalıcı — SAF izin sorunu ortadan kalkar

### 2.4 Anonslar: kendi sesiniz + TTS

`Announcement` modeli genişler:

```kotlin
data class Announcement(
    val id: Long = 0,
    val label: String = "",          // ekranda görünür, okunmaz
    val text: String = "",           // TTS ile okunacak (audioUri yoksa)
    val audioUri: String? = null,    // YENİ: kendi ses kaydınız
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val isEnabled: Boolean = true,
    val category: String = "genel",  // YENİ: motivasyon / duyuru / hatırlatma
    val bedVolume: Float = 0.25f     // YENİ: altında çalacak fon sesi
)
```

Anons ekleme ekranında üç seçenek yan yana:

```
[ ✍️ Metin yaz (TTS) ]  [ 📁 Ses dosyası seç ]  [ 🔴 Şimdi kaydet ]
```

Üçüncüsü uygulama içi kayıt (`MediaRecorder`) — motivasyon sözünü kendi sesinizle
30 saniyede kaydedip anons havuzuna atarsınız.

### 2.5 Sizin kullanımınıza özel, ama genel kalan özellikler

| Özellik | Neden sizin için | Neden genel |
|---|---|---|
| **Kaldığı yerden devam** (`Track.lastPositionMs`) | 45 dk'lık Risale dersi baştan başlamasın | Podcast/sesli kitap radyosu için de şart |
| **Konuşma altında fon müzik (ducking)** | Risale/hadis okunurken hafif enstrümantal | Her konuşma radyosunda kullanılır |
| **Ses seviyesi eşitleme** | Kayıtlar farklı seviyede, kulak tırmalamasın | Genel kalite |
| **Saat başı sabit içerik** | Saat başı Kur'an | Saat başı haber/jingle |
| **Sessiz saatler** | Gece 00:00–05:00 sus | Genel |
| **Namaz vakti duraklat** (opsiyonel) | Ezan vaktinde otomatik sustur | Opsiyonel modül, kapatılabilir |

### 2.6 Üç ekran — sade arayüz

```
┌──────────────┬──────────────┬──────────────┐
│   📻 RADYO   │  📚 KÜTÜPHANE │  ⚙️ YAYIN    │
└──────────────┴──────────────┴──────────────┘
```

**1) RADYO** — açılışta gelen ekran, tek amacı var: dinlemek.

```
┌────────────────────────────────────┐
│           ● CANLI                  │
│                                    │
│         [  büyük kapak  ]          │
│                                    │
│   Birinci Söz                      │
│   Risale-i Nur Dersleri            │
│   ━━━━━━━━━━●─────────  04:12/12:04│
│                                    │
│   ⏮    ( ▶ )    ⏭    ⏹            │
│                                    │
│   Sırada: İlahi — Ya Rabbi         │
│                                    │
│   Reçete:  [Sabah] [Gündüz] [Gece] │
└────────────────────────────────────┘
```

Tek dokunuşla başlar. Hiçbir kurulum yapmamış olsanız bile: uygulama
kütüphaneyi tarar, türleri tahmin eder, varsayılan reçeteyle çalmaya başlar.

**2) KÜTÜPHANE** — 2.3'teki ekran. Cihaz + listeleriniz tek yerde.

**3) YAYIN** — reçete, anonslar, zamanlayıcı. Üç sekme, her biri tek ekran.

### 2.7 Düzeltilecek mimari

```
RadioService (foreground, MediaSessionService)
   └── RadioEngine          ← controller buraya taşınır, ekrandan bağımsız yaşar
         ├── QueueBuilder   ← reçeteden sonsuz kuyruk üretir
         ├── PlaybackHost   ← 2 ExoPlayer: ana + fon (crossfade & ducking için)
         ├── TtsEngine
         └── AudioFocus
UI (Compose) → ViewModel → Repository ─┐
                                       ├→ Room (v6)
MediaStoreScanner ─────────────────────┘
```

Ana değişiklik: **motor servisin içinde yaşar**, arayüz sadece ona bakar.
Uygulamayı kapatınca radyo susmaz.

---

## BÖLÜM 3 — Uygulama planı (fazlar)

### Faz 0 — Kırıkları onar (yarım gün) ⚠️ ÖNCE BU
1. `AndroidManifest.xml`: servis, alıcılar, widget, tüm izinler
2. `takePersistableUriPermission` (mevcut SAF akışı için)
3. Motoru servise taşı, ViewModel'deki coroutine sızıntısını kapat
4. Audio focus
> Sonuç: uygulama ilk kez gerçekten çalar.

### Faz 1 — Kütüphane tarayıcısı (sizin asıl isteğiniz) ⭐
5. `MediaStoreScanner` + `READ_MEDIA_AUDIO` izin akışı
6. Klasör / Şarkı / Sanatçı sekmeleri, arama, çoklu seçim
7. "Klasörün tamamını ekle" + "Klasör bağla" (otomatik senkron)
> Sonuç: dosya seçiciye bir daha çıkmazsınız.

### Faz 2 — Tür + Yayın Reçetesi
8. `Playlist.kind` alanı (MUSIC/SPEECH/SPOT/BED), Room v6 migration
9. `QueueBuilder`: ağırlıklı sonsuz kuyruk
10. Reçete ekranı + hazır şablonlar (Sabah/Gündüz/Akşam/Gece)
11. Kurulum yapılmamışsa otomatik başlangıç
> Sonuç: liste yap → başlat. Arada akış kurma yok.

### Faz 3 — Anons ve ses kalitesi
12. `Announcement.audioUri` + dosya seçme + uygulama içi kayıt
13. Ducking (konuşma altında fon müzik)
14. Crossfade + ses eşitleme
15. `Track.lastPositionMs` — kaldığı yerden devam

### Faz 4 — Zamanlama ve cila
16. `repeatDays` gerçekten çalışsın, alarm kendini yeniden kursun
17. `BOOT_COMPLETED` alıcısı
18. Saat başı sabit içerik, sessiz saatler
19. Widget + kilit ekranı kontrolleri
20. İsimlendirme: paket, uygulama adı, ikon

---

## Özet

Uygulamanın fikri ve iskeleti sağlam; Compose ekranları, Room şeması ve TTS
altyapısı yerinde. Ama üç şey onu kullanılamaz kılıyor:

1. **Manifest eksik** → hiç çalmıyor
2. **Cihaz kütüphanesi yok** → müzik eklemek işkence
3. **Elle akış kurma zorunlu** → "listeyi yap, başlat" diyemiyorsunuz

Taslaktaki üç karşılık: **manifest onarımı**, **MediaStore kütüphane tarayıcısı**,
**tür + yayın reçetesi**. Bu üçü yapıldığında hayalinizdeki uygulamanın %80'i
hazır olur.
