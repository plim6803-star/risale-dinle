# Benim Radyom — Kurulum ve Derleme

## 1. APK hazır

**`BenimRadyom.apk`** (15,3 MB, imzalı) bu klasörde. Telefona kopyalayın, dosyaya
dokunun, "bilinmeyen kaynaktan kuruluma izin ver" uyarısını onaylayın.

| | |
|---|---|
| Paket | `com.benimradyom.app` |
| Uygulama adı | Benim Radyom |
| Gereken Android | 8.0 ve üzeri (API 26+) |
| İmza | v2 + v3 (doğrulandı) |
| İçerik | 4 dex, imzalı Android paketi |
| Sürüm | v6 — v5'in düzeltilmiş, tutarlı hâli (aşağıya bakın) |

### v6'da ne değişti

**Hatalar**
- **Uzun dersler artık yazıya dökülebiliyor.** Transkripsiyon servisinin 25 MB
  sınırı vardı; 45 dakikalık bir Risale dersi (~40 MB) bu yüzden işlenemiyordu.
  Şimdi kayıt kalite kaybı olmadan parçalara bölünüyor, her parça ayrı gönderiliyor
  ve zaman kodları birleştiriliyor. MP3 ve M4A desteklenir.
- **Akıllı yayında ayet ve hadisler artık araya giriyor.** Yerel planda bir indeks
  hatası yüzünden hep önce tüm Risale'ler geliyordu (`R R R R…`); şimdi
  `R A R H R A R H…` sırasıyla dönüşüyor.
- **"45 dakikalık yayın" artık gerçekten ~45 dakika.** Hedef süre, eşleşen ses
  kayıtlarının gerçek süresine ve metinlerin tahmini okuma süresine göre
  hesaplanıyor. Önceden kart sayısına kaba çevriliyordu.
- **Elle düzelttiğiniz ayet bölgeleri korunuyor.** Önceden aynı kaydı yeniden
  yazıya dökmek ya da ayet analizini yeniden çalıştırmak düzeltmelerinizi
  uyarısız siliyordu. Şimdi yalnızca otomatik bölgeler yenilenir; çakışmada
  elle olan kazanır.
- **Silinmiş/bozuk bir dosyada radyo artık donmuyor.** Fon müziği açık bir ders
  çalarken dosya açılamazsa hata kaybolabiliyor, radyo 0:00'da takılıyordu.
- Kurallardaki "fon müziği aç" eylemi akıllı akışın fon ayarını yok sayıyordu.
- Türkçe karakterli dosya adları (ş, ğ, ı) yüklemede bozuluyordu.

**Güvenilirlik**
- İki derleme aynı anda çalışırsa **imzalı ama içi eksik** bir APK çıkabiliyordu
  (açılışta çöker). Derleme betikleri artık kilit kullanıyor ve imzalamadan önce
  paketin tam olduğunu doğruluyor.
- Başarısız bir derleme artık elinizdeki çalışan APK'yı silmiyor.
- Her yapay zekâ isteği HTTPS'e zorlanıyor (önceden dört istemcinin ikisinde
  kontrol yoktu).

**Tutarlılık**
- Dört ayrı yerde kopyalanmış API bağlantı kodu tek yardımcıda (`AiHttp`).
- Beş yerde tekrarlanan fon müziği ayarı ve üç kopya "metni seslendir" akışı
  motorda tek fonksiyona indi.
- İçeriğiyle uyuşmayan dosya adları düzeltildi (`RadioScreenV2.kt` →
  `RadioScreen.kt` vb.).
- Sürüm numarası artık tek yerde: `app/build.gradle.kts`. Derleme betikleri
  oradan okur.

### İlk kullanım

1. Uygulamayı açın. İki izin istenir: **bildirim** ve **müzik ve ses**.
   İkisine de izin verin — müzik izni olmadan cihazdaki mp3'ler görünmez.
2. İlk açılışta altı liste otomatik oluşur:
   Ezgi/İlahi · Ders/Sohbet · Kur'an · Motivasyon · Fon Müzik · Ezan
   ve dört program: Sabah · Gündüz · Akşam · Gece.
3. **Kütüphane → Klasörler**: telefonunuzdaki tüm ses klasörleri dosya sayısıyla
   listelenir. Bir klasörün **Ekle** tuşuna basın, hedef listeyi seçin.
   *"Klasörü bağlı tut"* işaretliyse o klasöre sonradan attığınız dosyalar
   otomatik listeye düşer — bir daha ekleme yapmazsınız.
4. **Radyo** sekmesine dönüp oynat tuşuna basın.

### Akıllı yayın oluşturmak

**Yayın → Akıllı Akış** bölümüne isteğinizi normal cümleyle yazın:

> Risale-i Nur'dan melekler hakkında, aralarda ilgili ayet ve hadis bulunan
> 45 dakikalık bir yayın hazırla.

Uygulama önce kendi konu fihristinde arar ve düzenlenebilir bir taslak kaydeder.
API anahtarı olmasa da yerel planlayıcı çalışır. Taslağın içinde:

- Her öğeyi yukarı/aşağı taşıyabilir, açıp kapatabilir veya silebilirsiniz.
- Risale kartını cihazdaki ilgili MP3 kaydıyla eşleştirebilirsiniz.
- Ayet ve hadis metnini düzenleyip **Seslendir** ile dinleyebilirsiniz.
- Cihaz seslerini ve mevcut anonsları öneri panelinden doğrudan ekleyebilirsiniz.
- Fon müziğini, ses oranını ve yayın sonunda başa dönmeyi ayarlayabilirsiniz.
- **Kaydet** taslağı saklar; **Yayına al** kesin sırayı hemen çalmaya başlar.

#### Risale seslerini eşleştirmek

Ses dosyalarını kitap ve bölüm adıyla adlandırın; örneğin `29. Söz - Birinci
Esas.mp3`. Dosyayı Kütüphane'den eklediğinizde uygulama güvenli bir eşleşme
bulursa katalog kartına otomatik bağlar. Gerekirse **Yayın → Akıllı Akış →
Kaynak kataloğu** içinden kartı açıp doğru sesi elle seçebilirsiniz.

#### Okunan yeri takip ve Tefekkür Modu (v4)

Bir Risale, sohbet veya başka konuşma kaydı çalarken **Radyo → Okunan yeri
takip** kartı görünür. Kayıtla metni üç şekilde eşleştirebilirsiniz:

- **SRT/VTT:** Zaman kodları hazırsa okunan cümle tam vaktinde gösterilir.
- **TXT:** Düz metin paragraf ve kelime uzunluğuna göre kayıt süresine yaklaşık
  dağıtılır. Kesin takip gerektiğinde SRT/VTT tercih edin.
- **AI zaman kodu:** Açık onayınızdan sonra ses OpenAI transkripsiyon hizmetine
  gönderilir. 24 MB'ı aşan kayıtlar otomatik parçalara bölünür (MP3/M4A); API
  kullanım ücreti doğabilir.

Metin bağlandıktan sonra **Tefekkür modu** tam ekranda okunan bölümü takip eder.
İsterseniz 3 veya 6 temsilî sahneyi önceden ürettirirsiniz; uygulama bunları
özel klasöründe saklar ve yayın sırasında internet isteği yapmadan uygun zaman
aralığında değiştirir. Görseller gerçek dinî tasvir iddiası taşımaz: Allah,
peygamberler, sahabeler, melekler, cinler, cennet veya cehennem insan/varlık
şeklinde resmedilmez; yüz, vahşet, okunabilir dinî metin ve uydurma sembol
kullanılmaz. Doğa, ışık, su, gökyüzü, mimari ve geometrik motifler kullanılır.

Uygulamanın içine telifli tam Risale metni gömülmez. Elinizde bulunan veya
kullanma izniniz olan SRT/VTT/TXT metnini ilgili ses kaydına bağlayabilirsiniz.
Zaman kodlu metinler JSON yedeğine dâhildir; üretilmiş büyük JPEG dosyaları
yedeğe eklenmez.

#### Ayet okunurken fonu otomatik susturma (v5)

Bu özellik özellikle Risale-i Nur ses kayıtları için hazırlanmıştır fakat tüm
konuşma kayıtlarında kullanılabilir:

1. Risale kaydını çalın ve **Radyo → Okunan yeri takip** kartını açın.
2. **Ayet geçişlerini bul** seçeneğine dokunun.
3. Zaman kodlu metin yoksa açık onayınızla ses önce çözümlenir; varsa yalnızca
   metin segmentleri analiz edilir.
4. Uygulama gerçek Arapça ayet tilaveti olabilecek bölgeleri işaretler. Fon,
   bulunan başlangıçtan yaklaşık **2,5 saniye önce** 1 saniyelik yumuşak geçişle
   sıfıra iner. Okuma bittikten 1,2 saniye sonra 1,5 saniyede geri açılır.
5. **Ayet sessizlikleri** editöründe her bölgeyi açıp kapatabilir, başlangıç ve
   bitişini birer saniye kaydırabilir, silebilir veya o anki konuma elle yeni
   bölge ekleyebilirsiniz.

AI sonucu tahmindir; ayet başlangıçlarını mutlaka dinleyerek kontrol edin.
Elle düzelttiğiniz bölgeler, analizi yeniden çalıştırsanız da korunur.

Servisin istek başına 25 MB sınırı vardır; daha büyük MP3/M4A kayıtları uygulama
yeniden kodlamadan parçalara böler ve zaman kodlarını birleştirir. Uzun bir derste
ilerleme "3 parçaya bölündü · 1 / 3" gibi gösterilir. Parça sınırına denk gelen
bir kelime bölünebilir. WAV/OGG/FLAC gibi biçimler bölünemez; bunları MP3/M4A'ya
dönüştürün veya SRT/VTT aktarın. Segment zamanları için `whisper-1` kullanılır.
Ayrıntı: [OpenAI ses transkripsiyonu](https://developers.openai.com/api/docs/guides/speech-to-text).

#### Fon müziği ve medya bağlantısı eklemek

- Cihazdaki müzik: **Kütüphane → Klasörler/Sesler → Ekle → Fon Müzik**.
- İnternet bağlantısı: **Kütüphane → Listelerim → Fon Müzik → Fon müziği
  bağlantısı ekle**. Doğrudan çalınabilir bir HTTPS MP3/M4A/AAC/OGG veya yayın
  akışı adresi girin. YouTube ya da normal web sayfası bağlantısı çalışmaz.
- Kullanım: **Yayın → Programlar → Ses → Fon müzik** veya akıllı akış
  editöründeki **Konuşmaların altında fon çal** seçeneğinden listeyi ve ses
  oranını belirleyin.

İnternet bağlantılı fonlar çevrim dışıyken çalmaz; cihazdaki fon dosyaları ise
internetsiz çalışır. Ayet sessizlik zamanları önceden kaydedildiği için yayın
sırasında AI çağrısı yapılmaz.

#### Ayet ve hadis güvenlik kuralı

Başlangıç kataloğu konu, sure/bölüm, referans ve kaynak bağlantısı içerir; tam
metinler topluca kopyalanmaz. Bir ayet meali veya hadis yayında okunmadan önce:

1. Karttaki **Kaynağı aç ve kontrol et** bağlantısına dokunun.
2. Kullanmak istediğiniz meal/hadis metnini karta ekleyin.
3. **Kaynakla karşılaştırdım** seçeneğini açın.

Bu onay yoksa kart taslakta görünebilir ama kapalı kalır ve TTS onu ayet/hadis
diye okumaz. Yapay zekâ da metin yazamaz; yalnızca kayıtlı katalog kimliklerini
sıralayabilir.

Başlangıç referansları: [eRisale](https://www.erisale.com/),
[iki boyutlu Risale fihristi](https://konularlarisale.net/wp-content/uploads/2015/07/%C4%B0ki-boyutlu-Risale-i-Nur-fihristi..pdf),
[Diyanet Kur'an](https://kuran.diyanet.gov.tr/) ve
[Hadislerle İslam fihristi](https://hadislerleislam.diyanet.gov.tr/icindekiler.php).

#### İsteğe bağlı API anahtarı

**Yayın → Ayarlar → Akıllı yayın ve kaynaklar**:

1. “Yapay zekâ ile sıralama”yı açın.
2. API anahtarınızı girin. Model ve HTTPS adresi gelişmiş bölümden değiştirilebilir.
3. Ayarları kaydedin.

Anahtar Android Keystore ile şifrelenir ve uygulamanın JSON yedeğine yazılmaz.
Akıllı yayın sıralamasında ses dosyaları veya ayet/hadis tam metinleri
gönderilmez; yalnızca yayın isteği ile aday kartların başlık, referans, konu ve
kimlikleri gönderilir. Ses yalnızca Radyo ekranındaki **AI zaman kodu** veya
zaman kodlu metin bulunmayan **Ayet geçişlerini bul** işlemini ayrıca
onayladığınızda transkripsiyon için gönderilir. Tefekkür görseli
üretirken de seçili metin bölümü görsel API'sine gönderilir.

### Anons eklemek

**Yayın → Anonslar → Yeni anons**, üç yol:
- **Kaydet** — mikrofonla kendi sesinizi anında kaydedin
- **Dosya seç** — hazır bir ses kaydı seçin
- **Metin yazın** — yazdığınız metin seslendirilir (hız/perde ayarlanır)

Araya hangi sıklıkta gireceğini **Yayın → Programlar → (bir program) → Anons** belirler.

Ayrıca telefondaki kısa MP3/ses kayıtlarını **Kütüphane** içinden doğrudan
**Anons** türündeki bir listeye (varsayılan: Motivasyon) ekleyebilirsiniz.
Uygulama bunları da belirlediğiniz sıklıkta otomatik araya koyar.

### Programlar — asıl özelleştirme burada

Eski "reçete" ve "zamanlayıcı" tek kavramda birleşti: **Program**. Bir program
ne zaman, ne çalacağını, hangi seste ve hangi kurallarla çalacağını bir arada
tutar. **Yayın → Programlar → bir programa dokunun**, beş katman açılır:

| Katman | İçinde ne var |
|---|---|
| **Zaman** | Hangi günler · başlangıç/bitiş saati · saati gelince kendiliğinden başlasın mı · sessiz saatler |
| **İçerik** | Her listenin bu programdaki ağırlığı (%0 = hiç çalmaz) · çalma sırası (sıralı, isme göre, karışık, en az çalan…) |
| **Ses** | Ana ses · **liste başına ses** · fon müzik açık/kapalı, listesi ve oranı · geçiş süresi · çapraz geçiş · ses dengeleme |
| **Anons** | Kaç parçada bir · saat anonsu ("Saat 14") · saat başı çalacak liste · **sabit saatli anonslar** (örn. her gün 08:00) |
| **Kurallar** | Şartlı görevler |

Ağırlıklar artık programa özel: "Sabah"ta %40 ilahi, "Gece"de %20 ilahi
olabilir. Dört program hazır gelir (Sabah 06–11, Gündüz 11–17, Akşam 17–23,
Gece 23–06) ve saatleri geldiğinde kendiliğinden devreye girerler.

### Şartlı görevler (Kurallar katmanı)

"EĞER şu olursa → İSE şunu yap" biçiminde kurulur.

**Tetikleyiciler:** program başlayınca · şu listeden bir parça çalınca · her N
parçada bir · saat gelince

**Eylemler:** şu listeden bir parça çal · şu anonsu çal · sesi şu seviyeye getir
· fon müziği aç · fon müziği kapat · şu kadar dakika duraklat · şu programa geç

Örnek: *"Ders/Sohbet listesinden bir bölüm çalınca → Motivasyon anonsunu çal"*
veya *"Saat 08:30 olunca → Sabah programına geç"*.

### Namaz vakitleri

**Yayın → Ayarlar → Namaz vakitleri**. Vakitler telefonda hesaplanır, internet
gerekmez. Şehir listesinden seçim yapın; hangi vakitlerde uygulanacağını, vakitten
kaç dakika önce susup kaç dakika sonra devam edeceğini ayarlayın. Ezan kaydınızın
bulunduğu listeyi seçerseniz vakit girince ezanı çalar, sonra kaldığı yerden devam eder.

### Yedekleme

**Yayın → Ayarlar → Yedek al** tüm listeleri, anonsları, programları, kaynak
kartlarını, akıllı akışları, okuma metinlerini, ayet sessizlik bölgelerini ve ayarları
tek bir JSON dosyasına yazar. **Geri yükle** ile aynı dosyadan döner. Ses
dosyaları ve API anahtarı yedeğe dahil değildir — sesler telefonda durdukları
sürece çalışır.

### Uyku zamanlayıcısı

Radyo ekranında oynat tuşunun altında: 15 / 30 / 45 / 60 / 90 dakika.

### Listelerde sıra

Liste başlığındaki sıralama düğmesinden isme göre / eskiden yeniye / en az çalan
gibi ölçütlerle tek dokunuşla sıralayabilir, her parçanın yanındaki oklarla elle
yukarı-aşağı taşıyabilirsiniz. Dosyalar eklenirken **doğal sıralama** uygulanır:
"Bölüm 2" artık "Bölüm 10"dan önce gelir. Ders ve Kur'an listelerinde sıra asla
karıştırılmaz.

---

## 2. APK burada nasıl üretildi

Bu bilgisayarda **Gradle çalışamıyor**. Makinede Palo Alto Cortex XDR var ve
Java'nın `Selector.open()` çağrısının kullandığı yerel loopback soket çiftini
engelliyor; Gradle bunu derleme süreçleri arası haberleşme için zorunlu
kullanıyor:

```
java.io.IOException: Unable to establish loopback connection
Caused by: java.net.SocketException: Invalid argument: connect
```

JDK 17 ve 21'de `Selector.open()` 10/10 başarısız. `--no-daemon`, JVM
argümanlarını eşitleme, `preferIPv4Stack` ve benzeri ağ bayrakları,
JDK 11/17/21/25, WSL, Docker — hiçbiri sonuç vermedi.

Bu yüzden Gradle atlandı ve APK doğrudan Android SDK araçlarıyla üretildi.
`tools/build-apk.ps1` bu hattı çalıştırır:

```
1. AAR paketlerini aç                  90 kütüphane
2. aapt2 compile                       37 kütüphane kaynağı + uygulama kaynakları
3. aapt2 link                          base.apk + 90 paket için R.java
4. javac                               R sınıfları
5. kotlinc + Compose eklentisi         28 Kotlin dosyası -> 389 uygulama sınıfı
6. d8                                  uygulama + bağımlılıklar -> 4 dex
7. jar + zipalign                      paketleme
8. apksigner                           imzalama
```

Bağımlılıklar için `tools/Resolve.java` yazıldı: POM'ları indirip bağımlılık
ağacını kendisi yürüyen küçük bir Maven çözümleyicisi. 114 paket çözdü
(Compose 1.12.1, Material3 1.4.0, Media3 1.11.1, lifecycle 2.11.0).

### Yeniden derlemek

Kodda değişiklik yaptıktan sonra PowerShell'de:

```powershell
& "C:\Users\masallahresulozcan\Desktop\Benim Radiyom\tools\build-apk.ps1"
```

Git Bash kullananlar için `tools/build-apk.sh` aynı işi yapar.

İki betik de aynı kilidi (`DevTools\apkbuild.lock`) kullanır: biri çalışırken
diğeri "Başka bir derleme çalışıyor" diyerek durur. Bu önemli — aynı anda iki
derleme (örneğin iki ayrı yapay zekâ aracından) imzalı ama içi eksik bir APK
üretebiliyordu. Betikler imzalamadan önce paketin tam olduğunu da doğrular;
eksikse APK üretmez ve eski APK'nıza dokunmaz.

Sürüm numarasını değiştirmek için yalnızca `app/build.gradle.kts` içindeki
`versionCode` ve `versionName` değerlerini değiştirin; betikler oradan okur.

Kurulu araçlar (`C:\Users\masallahresulozcan\DevTools`):

| Araç | Klasör |
|---|---|
| JDK 17 / JDK 21 (Microsoft OpenJDK) | `jdk-17.0.20.1+1`, `jdk-21.0.12.1+1` |
| Android SDK (platform 36, build-tools 36) | `android-sdk` |
| Kotlin derleyici + Compose eklentisi | `libs` |
| Çözülmüş bağımlılıklar | `deps` (114 paket, 69 MB) |
| Ara çıktılar | `apkbuild` |

> Ağ notu: kurumsal ağ `github.com`'u engelliyor (Gradle ve Temurin JDK oradan
> iner). JDK Microsoft'un sunucusundan, Gradle aynadan alınıp resmî SHA-256
> özetiyle doğrulandı. `dl.google.com` ve `repo1.maven.org` açık.

---

## 3. Gradle ile derlemek (başka bilgisayarda)

Kişisel bilgisayarınızda Android Studio ile de derlenebilir — proje dosyaları
(`build.gradle.kts`, `settings.gradle.kts`, `gradlew`) duruyor. Tek fark:
Gradle sürümünde Room kullanılmıyor, `app/build.gradle.kts` içindeki Room ve
kapt satırları gereksiz; silinebilir.

1. Android Studio kurun, bu klasörü **Open** ile açın
2. **Build → Build Bundle(s)/APK(s) → Build APK(s)**
3. Çıktı: `app/build/outputs/apk/debug/app-debug.apk`

---

## 4. Proje yapısı

```
app/src/main/java/com/benimradyom/app/
  MainActivity.kt            üç sekme, izinler, dosya seçici
  BenimRadyomApp.kt          açılışta zamanlayıcıları tazeler
  data/
    model/Models.kt          Playlist(+tür,ağırlık) Track(+kaldığı yer)
                             Announcement(+ses kaydı) LinkedFolder
    model/Program.kt         Program · kural · sabit saatli anons · namaz ayarı
    model/Knowledge.kt       Risale/ayet/hadis kartı · akıllı akış · API ayarı
    ai/ApiKeyStore.kt        Android Keystore ile şifreli API anahtarı
    ai/SmartFlowPlanner.kt   yerel arama + doğrulanan API planı
    db/RadioDb.kt            SQLite katmanı (Room yerine, kod üretimi gerektirmez)
    media/MediaStoreScanner  cihazdaki sesleri okur (klasör/sanatçı/süre)
    repo/RadioRepository     veri erişimi + bağlı klasör senkronu
  engine/
    RadioEngine.kt           radyonun beyni (servis içinde yaşar)
    QueueBuilder.kt          programdan sonsuz kuyruk üretir
    PlaybackHost.kt          iki ana çalar + fon (ducking, çapraz geçiş)
    PrayerTimes.kt           internet gerektirmeyen namaz vakti hesabı
    TtsSpeaker.kt            metin anonslarını seslendirir
    VoiceRecorder.kt         uygulama içi ses kaydı
    RadioEngineHolder.kt     motorun tek örneği
  service/
    RadioService.kt          ön plan servisi, bildirim, medya oturumu
    AlarmScheduler.kt        program zamanlayıcısı + açılış alıcısı
  ui/screens/                RadioScreen · LibraryScreen · BroadcastScreen
                             ProgramEditor · SmartFlowScreen · ContemplationMode
  data/media/                MediaStoreScanner · TranscriptParser
  data/ai/                   AiHttp (ortak, HTTPS zorunlu bağlantı katmanı)
                             SmartFlowPlanner · MediaAiClients · AudioChunker
  viewmodel/RadioViewModel   arayüz ile motor arasındaki köprü
  widget/RadioWidget.kt      ana ekran widget'ı
tools/
  build-apk.ps1              Windows/PowerShell Gradle'sız derleme hattı
  build-apk.sh               Git Bash için aynı derleme hattı
  Resolve.java               bağımlılık çözümleyici
```

---

## 5. Eski sürümdeki hangi hatalar düzeltildi

| Eski sorun | Durum |
|---|---|
| `AndroidManifest.xml`'de servis, alıcılar ve tüm izinler eksikti → hiç çalmıyordu | Düzeltildi |
| SAF URI izni kalıcı alınmıyordu → eklenen sesler bir süre sonra susuyordu | MediaStore'a geçildi; anons dosyalarında `takePersistableUriPermission` |
| Cihaz kütüphanesi yoktu, her ses dosya seçiciden tek tek ekleniyordu | Klasör/şarkı/arama ekranı + klasör bağlama |
| Anons için 10 adımlık akış kurmak gerekiyordu | Yayın reçetesi: birkaç kaydırma çubuğu |
| Anonslar sadece robot TTS'ti | Kendi ses kaydınız + uygulama içi kayıt |
| Motor ViewModel'deydi, ekran kapanınca radyo susuyordu | Motor servise taşındı |
| `selectPlaylist()` her çağrıda yeni akış başlatıyordu (sızıntı) | Önceki iş iptal ediliyor |
| `repeatDays` hiç okunmuyordu, alarm tek seferlikti | Günler uygulanıyor, alarm kendini yeniden kuruyor |
| Yeniden başlatmada zamanlayıcılar kayboluyordu | `BOOT_COMPLETED` alıcısı eklendi |
| Ses odağı yoktu (telefon çalınca susmuyordu) | ExoPlayer ses odağı yönetimi açık |
| Uzun kayıtlar hep baştan başlıyordu | `lastPositionMs` ile kaldığı yerden devam |

---

## 6. Doğrulananlar ve doğrulanamayan

**Doğrulandı** (APK üzerinde ölçüldü):
- İmza geçerli (v2 + v3 şeması)
- Paket adı, uygulama adı, 11 izin, hedef SDK 36, sürüm 5.0
- Başlatılabilir aktivite: `com.benimradyom.app.MainActivity`
- Bileşenler: servis, 2 alıcı, widget alıcısı, başlatma sağlayıcısı
- Kritik sınıflar dex içinde: MainActivity, RadioService, RadioEngine, RadioDb,
  SmartFlowPlanner, TranscriptParser, AudioTranscriptionClient, QuranCueAnalyzer,
  ContemplationImageClient, ApiKeyStore, Compose (AndroidComposeView, Material3),
  Media3 (ExoPlayer, MediaSessionService)
- Kaynaklar çözülüyor: tema, 5 yoğunlukta uygulama ikonu, widget düzeni,
  bildirim ikonu, metinler
- Manifeste elle eklenen üç başlatıcı sınıfın hepsi pakette

**Doğrulanamadı:** Uygulama gerçek bir cihazda **çalıştırılmadı**. Bu makinede
emülatör yok. Derleme ve paket yapısı sağlam, ama çalışma anındaki davranış
ancak telefonda görülür. Bir sorun çıkarsa (açılışta kapanma, bir ekranın boş
gelmesi, ses gelmemesi) ekran görüntüsünü veya hata mesajını iletin, düzeltip
yeni APK üretirim.
