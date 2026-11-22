export interface LugatWord {
  term: string;
  ottoman?: string;
  meaning: string;
  rarity: "nadir" | "yaygin"; // nadir = Az Bilinen / Nadir, yaygin = Bölümde Geçen Temel Lügat
  category?: string;
  source?: string; // Sorularla Risale / RisaletinNur / 500 Kelime
}

export const CHAPTER_LUGAT_DATA: Record<string, LugatWord[]> = {
  // ================= SÖZLER =================
  "sozler-1": [
    { term: "Lisan-ı hâl", ottoman: "لسان حال", meaning: "Hâl dili; varlıkların duruş, nizam ve yaratılışlarıyla konuşması", rarity: "yaygin", category: "Terkip", source: "Sorularla Risale" },
    { term: "Vird-i zebân", ottoman: "ورد زبان", meaning: "Dilden düşmeyen zikir, sürekli tekrarlanan mübarek söz", rarity: "nadir", category: "Tasavvuf / Edebî", source: "RisaletinNur Lügat" },
    { term: "İntisap", ottoman: "انتساب", meaning: "Bağlanma, bir zatın namına ve himayesine dayanma, mensup olma", rarity: "yaygin", category: "Kelâm", source: "Sorularla Risale" },
    { term: "Mevcudat", ottoman: "موجودات", meaning: "Yaratılmış bütün varlıklar, mahlûkat, kâinat", rarity: "yaygin", category: "İman", source: "500 Kelime" },
    { term: "Şakî", ottoman: "شقى", meaning: "Haydut, yol kesici eşkıya; ruhen bedbaht ve asi kimse", rarity: "nadir", category: "Lügavî", source: "RisaletinNur Lügat" },
    { term: "Hâcât", ottoman: "حاجات", meaning: "İhtiyaçlar, lüzumlu ve zaruri istekler (Hâcet kelimesinin çoğulu)", rarity: "yaygin", category: "Lügavî", source: "500 Kelime" },
    { term: "Tedarik", ottoman: "تدارك", meaning: "Temin etme, hazırlama, elde etme", rarity: "yaygin", category: "Lügavî", source: "Sorularla Risale" },
    { term: "Sikke-i İlahiye", ottoman: "سكهٔ الهيه", meaning: "Her bir varlığın üzerindeki İlahî mühür ve birlik damgası", rarity: "nadir", category: "Tevhid", source: "Sorularla Risale" },
    { term: "Müstesna", ottoman: "مستثنا", meaning: "Ayrı tutulan, kural dışı, seçkin ve benzerlerinden üstün", rarity: "yaygin", category: "Lügavî", source: "500 Kelime" },
    { term: "Zelil", ottoman: "ذليل", meaning: "Hor, hakir, aşağılanmış ve izzetini kaybetmiş", rarity: "nadir", category: "Ahlâk", source: "500 Kelime" },
    { term: "İstinad", ottoman: "استناد", meaning: "Dayanma, güvenme, bir kuvveti kendine arka ve destek yapma", rarity: "yaygin", category: "Kelâm", source: "500 Kelime" },
    { term: "Tezyin", ottoman: "تزيين", meaning: "Süsleme, bezeme, nakışlarla güzelleştirme", rarity: "nadir", category: "Sanat-ı İlahiye", source: "500 Kelime" },
    { term: "Mukabil", ottoman: "مقابل", meaning: "Karşılık, bedel, bir şeyin karşısında bulunan", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
    { term: "Musahhar", ottoman: "مسخر", meaning: "Emir altına alınmış, boyun eğdirilmiş, hizmet ettirilen", rarity: "nadir", category: "Tevhid", source: "500 Kelime" }
  ],
  "sozler-2": [
    { term: "Hodbin", ottoman: "خودبين", meaning: "Yalnız kendini gören, bencil, enaniyetli ve kibirli kimse", rarity: "nadir", category: "Ahlâk", source: "500 Kelime" },
    { term: "Hüdâbin", ottoman: "خدابين", meaning: "Rabbini gören, her şeyde İlahî sanatı ve hikmeti müşahede eden", rarity: "nadir", category: "İman", source: "Sorularla Risale" },
    { term: "Hodgâm", ottoman: "خودكام", meaning: "Yalnız kendi çıkarını ve keyfini düşünen, egoist", rarity: "nadir", category: "Ahlâk", source: "RisaletinNur Lügat" },
    { term: "Hodendiş", ottoman: "خودانديش", meaning: "Sadece kendi nefsinin kaygısını çeken, başkalarını düşünmeyen", rarity: "nadir", category: "Ahlâk", source: "RisaletinNur Lügat" },
    { term: "Bedbin", ottoman: "بدبين", meaning: "Kötümser, her şeye karamsar ve negatif nazarla bakan", rarity: "yaygin", category: "Ahlâk", source: "Sorularla Risale" },
    { term: "Sülûk", ottoman: "سلوك", meaning: "Bir yola girme, manevî yolculuğa çıkma, tarz tutma", rarity: "nadir", category: "Tasavvuf", source: "Sorularla Risale" },
    { term: "Tevekkül", ottoman: "توكل", meaning: "Sebeplere riayet ettikten sonra neticeyi Allah'tan bekleyip O'na güvenmek", rarity: "yaygin", category: "İman", source: "Sorularla Risale" },
    { term: "Vâveylâ", ottoman: "واويلا", meaning: "Çığlık, feryat, acı ve ümitsizlik haykırışı", rarity: "nadir", category: "Edebî", source: "500 Kelime" },
    { term: "Mâtemsârâ", ottoman: "ماتمسرا", meaning: "Yas evi, hüzün ve ağlama yeri", rarity: "nadir", category: "Terkip", source: "RisaletinNur Lügat" },
    { term: "Şehrayin", ottoman: "شهرآيين", meaning: "Bayram şenliği, ışıklarla donatılmış sevinçli kutlama", rarity: "nadir", category: "Lügavî", source: "RisaletinNur Lügat" }
  ],
  "sozler-3": [
    { term: "Hasâret", ottoman: "خسارت", meaning: "Büyük zarar, ziyan ve hüsran", rarity: "nadir", category: "Lügavî", source: "RisaletinNur Lügat" },
    { term: "Helâket", ottoman: "هلاكت", meaning: "Mahvolma, yıkıma ve felakete sürüklenme", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Fısk", ottoman: "فسق", meaning: "Hak yoldan çıkma, İlahî emirlere itaatsizlik ve günaha dalma", rarity: "nadir", category: "Fıkıh / Ahlâk", source: "Sorularla Risale" },
    { term: "Sefahet", ottoman: "سفاهت", meaning: "Haram zevklere ve eğlencelere dalarak ömrü ve malı zayi etme", rarity: "yaygin", category: "Ahlâk", source: "500 Kelime" },
    { term: "Mükellefiyet", ottoman: "مكلفيت", meaning: "İlahî emir ve yasaklarla sorumlu tutulma, yükümlülük", rarity: "yaygin", category: "Fıkıh", source: "Sorularla Risale" },
    { term: "Ubudiyet", ottoman: "عبوديت", meaning: "Kulluk, Allah'a tam itaat, tevazu ve ibadet şuuru", rarity: "yaygin", category: "İman", source: "500 Kelime" },
    { term: "İstimal", ottoman: "استعمال", meaning: "Kullanma, sarf etme, değerlendirme", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Menzil", ottoman: "منزل", meaning: "Yolculukta konaklama yeri, varılacak durak veya makam", rarity: "yaygin", category: "Lügavî", source: "Sorularla Risale" }
  ],
  "sozler-4": [
    { term: "İmâdü'd-dîn", ottoman: "عماد الدين", meaning: "Dinin direği, ayakta tutan en temel dayanağı", rarity: "nadir", category: "Hadis Tabiri", source: "Sorularla Risale" },
    { term: "Maişet", ottoman: "معيشت", meaning: "Geçim, dünyevî yaşayış için gerekli rızık ve ihtiyaçlar", rarity: "yaygin", category: "Lügavî", source: "500 Kelime" },
    { term: "İstikraz", ottoman: "استقراض", meaning: "Borçlanma, ödünç alma", rarity: "nadir", category: "Lügavî", source: "RisaletinNur Lügat" },
    { term: "Hasis", ottoman: "خسيس", meaning: "Değersiz, bayağı, geçici ve kıymetsiz; cimri", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Zeval", ottoman: "زوال", meaning: "Sona erme, yok olup gitme, geçip kaybolma", rarity: "yaygin", category: "Kelâm", source: "500 Kelime" },
    { term: "Bâki", ottoman: "باقى", meaning: "Sonsuz, kalıcı, ebedî olarak devam eden", rarity: "yaygin", category: "İman", source: "500 Kelime" },
    { term: "Müzeyyen", ottoman: "مزين", meaning: "Süslenmiş, en güzel şekilde donatılmış", rarity: "nadir", category: "Edebî", source: "500 Kelime" },
    { term: "Divane", ottoman: "ديوانه", meaning: "Akılsızca hareket eden, hakikî kârını zarar ile değiştiren şaşkın", rarity: "yaygin", category: "Lügavî", source: "Sorularla Risale" }
  ],
  "sozler-5": [
    { term: "Raiyet", ottoman: "رعيت", meaning: "Bir hükümdarın idaresi altındaki halk, tebaa", rarity: "nadir", category: "Tarih / Temsil", source: "500 Kelime" },
    { term: "Emanet", ottoman: "امانت", meaning: "Allah'ın insana geçici olarak tevdi ettiği hayat, beden, akıl ve duygular", rarity: "yaygin", category: "İman", source: "Sorularla Risale" },
    { term: "Fâni", ottoman: "فانى", meaning: "Geçici, ölümlü, zevale mahkûm olan", rarity: "yaygin", category: "Kelâm", source: "500 Kelime" },
    { term: "Müflis", ottoman: "مفلس", meaning: "İflas etmiş, elindeki tüm sermayeyi kaybetmiş kimse", rarity: "nadir", category: "Lügavî", source: "RisaletinNur Lügat" },
    { term: "Cihâzât", ottoman: "جهازات", meaning: "Donanımlar, insana verilen maddi ve manevi duygular ve organlar", rarity: "nadir", category: "İman", source: "500 Kelime" },
    { term: "Galebe", ottoman: "غلبه", meaning: "Üstün gelme, zafer kazanma, baskın çıkma", rarity: "yaygin", category: "Lügavî", source: "500 Kelime" },
    { term: "Mübrem", ottoman: "مبرم", meaning: "Kaçınılmaz, kesin, vazgeçilmez derecede zorunlu", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Semerat", ottoman: "ثمرات", meaning: "Meyveler, neticeler, faydalı sonuçlar", rarity: "nadir", category: "Lügavî", source: "500 Kelime" }
  ],
  "sozler-6": [
    { term: "Tasarruf-u İlâhî", ottoman: "تصرف الهى", meaning: "Allah'ın kâinat üzerindeki mutlak kudret, irade ve idaresi", rarity: "yaygin", category: "Tevhid", source: "Sorularla Risale" },
    { term: "İnkılâb", ottoman: "انقلاب", meaning: "Bir hâlden başka bir hâle büyük dönüşüm (gece-gündüz, mevsimler)", rarity: "yaygin", category: "Kevnî", source: "500 Kelime" },
    { term: "Ma'kes", ottoman: "معكس", meaning: "Yansıma yeri, tecellilerin göründüğü ayna", rarity: "nadir", category: "Edebî / Kelâm", source: "500 Kelime" },
    { term: "İhsanat-ı Külliye", ottoman: "احسانات كليه", meaning: "Bütün varlıkları kuşatan büyük ve umumi İlahî ikramlar", rarity: "nadir", category: "Terkip", source: "Sorularla Risale" },
    { term: "Tesbih", ottoman: "تسبيح", meaning: "Allah'ı her türlü kusur, acz ve noksandan tenzih edip yüceltmek (Sübhanallah)", rarity: "yaygin", category: "İbadet", source: "Sorularla Risale" },
    { term: "Tazim", ottoman: "تعظيم", meaning: "Büyüklüğünü ve azametini ilan ederek hürmet göstermek (Allahu Ekber)", rarity: "yaygin", category: "İbadet", source: "500 Kelime" },
    { term: "Muayyen", ottoman: "معين", meaning: "Belirlenmiş, tayin ve tespit edilmiş vakit", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Berzah", ottoman: "برزخ", meaning: "İki âlem arasındaki geçit; kabir âlemi", rarity: "yaygin", category: "Ahiret", source: "500 Kelime" }
  ],
  "sozler-7": [
    { term: "Haşir", ottoman: "حشر", meaning: "Öldükten sonra ahirette tekrar diriltilip huzur-u İlahîde toplanma", rarity: "yaygin", category: "Ahiret", source: "500 Kelime" },
    { term: "Âsâr-ı Rahmet", ottoman: "آثار رحمت", meaning: "İlahî rahmetin yeryüzündeki eserleri, bahar çiçekleri ve meyveleri", rarity: "nadir", category: "Terkip", source: "Sorularla Risale" },
    { term: "Kudret-i Fâtıra", ottoman: "قدرت فاطره", meaning: "Yoktan ve benzersiz şekilde yaratan sonsuz İlahî kudret", rarity: "nadir", category: "Tevhid", source: "Sorularla Risale" },
    { term: "Sermedî", ottoman: "سرمدى", meaning: "Başlangıcı ve sonu olmayan, kesintisiz devam eden ebedîlik", rarity: "nadir", category: "Kelâm", source: "500 Kelime" },
    { term: "Nebatat", ottoman: "نباتات", meaning: "Bitkiler âlemi, yerden biten otlar ve ağaçlar", rarity: "yaygin", category: "Kevnî", source: "500 Kelime" },
    { term: "Mizan", ottoman: "ميزان", meaning: "Hassas ölçü, terazi, kâinattaki kusursuz denge", rarity: "yaygin", category: "Kelâm", source: "500 Kelime" },
    { term: "Neşir", ottoman: "نشر", meaning: "Tohumların ve ruhların diriltilerek etrafa yayılması ve dağılması", rarity: "nadir", category: "Ahiret", source: "500 Kelime" },
    { term: "Haşmet", ottoman: "حشمت", meaning: "Büyüklük, görkem, heybet ve saltanat parıltısı", rarity: "yaygin", category: "Lügavî", source: "500 Kelime" }
  ],
  "sozler-8": [
    { term: "Esmâ-i Hüsnâ", ottoman: "اسماء حسنى", meaning: "Cenâb-ı Hakk'ın sonsuz kemal ve güzellikteki isimleri", rarity: "yaygin", category: "Tevhid", source: "Sorularla Risale" },
    { term: "Rububiyet", ottoman: "ربوبيت", meaning: "Allah'ın her bir varlığı rızıklandırıp terbiye ederek kemale erdirmesi", rarity: "yaygin", category: "Tevhid", source: "500 Kelime" },
    { term: "Dâr-ı Bekâ", ottoman: "دار بقا", meaning: "Sonsuzluk ve kalıcılık yurdu olan ahiret âlemi", rarity: "nadir", category: "Ahiret", source: "Sorularla Risale" },
    { term: "İktiza", ottoman: "اقتضا", meaning: "Gerektirme, zorunlu kılma, isteme", rarity: "nadir", category: "Kelâm", source: "500 Kelime" },
    { term: "Cilve", ottoman: "جلوه", meaning: "İlahî isim ve sıfatların varlıklar üzerindeki tecellisi, görünüşü", rarity: "yaygin", category: "Tasavvuf", source: "500 Kelime" },
    { term: "Saltanat-ı Mutlaka", ottoman: "سلطنت مطلقه", meaning: "Hiçbir kayıt ve ortak kabul etmeyen sınırsız İlahî hâkimiyet", rarity: "nadir", category: "Terkip", source: "Sorularla Risale" },
    { term: "Bâb-ı Kerem", ottoman: "باب كرم", meaning: "Sonsuz cömertlik ve ikram kapısı", rarity: "nadir", category: "Terkip", source: "RisaletinNur Lügat" },
    { term: "Adalet-i Mahzâ", ottoman: "عدالت محضا", meaning: "Hiçbir haksızlığa yer bırakmayan saf ve tam İlahî adalet", rarity: "nadir", category: "Kelâm", source: "Sorularla Risale" }
  ],
  "sozler-9": [
    { term: "Nefs-i Emmâre", ottoman: "نفس اماره", meaning: "İnsanı daima kötülüğe, günaha ve tembelliğe sevk eden nefis mertebesi", rarity: "yaygin", category: "Ahlâk", source: "Sorularla Risale" },
    { term: "Vesvese", ottoman: "وسوسه", meaning: "Kalbe atılan asılsız şüphe ve kuruntu; ehemmiyet verdikçe şişer, önemsemeyince söner", rarity: "yaygin", category: "Fıkıh / Ruhiyat", source: "Sorularla Risale" },
    { term: "Sinnen", ottoman: "سناً", meaning: "Yaş itibarıyla, yaşça", rarity: "nadir", category: "Lügavî", source: "RisaletinNur Lügat" },
    { term: "Tevehhüm", ottoman: "توهم", meaning: "Olmayan bir şeyi var sanma, kuruntuya kapılma", rarity: "nadir", category: "Ruhiyat", source: "500 Kelime" },
    { term: "Tedai-i Efkâr", ottoman: "تداعى افكار", meaning: "Fikirlerin ve hayallerin birbirini çağrıştırması", rarity: "nadir", category: "Ruhiyat", source: "Sorularla Risale" },
    { term: "Tahayyül", ottoman: "تخيل", meaning: "Hayalde canlandırma; tek başına hüküm ifade etmez ve mesuliyet doğurmaz", rarity: "yaygin", category: "Kelâm", source: "500 Kelime" },
    { term: "Müzahrefat", ottoman: "مزخرفات", meaning: "Süslü görünen fakat hakikatte pis ve değersiz şeyler", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Yeis", ottoman: "يأس", meaning: "Ümitsizlik; manevî gelişimi kıran en tehlikeli ruh hastalığı", rarity: "yaygin", category: "Ahlâk", source: "500 Kelime" }
  ],
  "sozler-10": [
    { term: "Ahsen-i Takvim", ottoman: "احسن تقويم", meaning: "En güzel kıvam, maddi ve manevi cihazlar bakımından en mükemmel yaratılış", rarity: "yaygin", category: "Kur'ânî Tabir", source: "Sorularla Risale" },
    { term: "Vazife-i Asliye", ottoman: "وظيفهٔ اصليه", meaning: "İnsanın dünyaya gönderilişindeki en temel vazifesi: İman, marifet ve dua", rarity: "yaygin", category: "İman", source: "Sorularla Risale" },
    { term: "Vazıh", ottoman: "واضح", meaning: "Apaçık, net, şüpheye yer bırakmayacak kadar berrak", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Burhan", ottoman: "برهان", meaning: "Sarsılmaz ve kesin delil, hüccet", rarity: "yaygin", category: "Kelâm", source: "500 Kelime" },
    { term: "İstidat", ottoman: "استعداد", meaning: "Doğuştan gelen kabiliyet, gelişmeye elverişli manevi potansiyel", rarity: "yaygin", category: "Ruhiyat", source: "500 Kelime" },
    { term: "Esfel-i Sâfilîn", ottoman: "اسفل سافلين", meaning: "Aşağıların en aşağısı; imansızlık ve isyanla düşülen en alt derece", rarity: "nadir", category: "Kur'ânî Tabir", source: "Sorularla Risale" },
    { term: "Fıtrat", ottoman: "فطرت", meaning: "Yaratılış gayesi, bozulmamış ilk ve saf tabiat", rarity: "yaygin", category: "İman", source: "500 Kelime" },
    { term: "Nüsha-i Câmi", ottoman: "نسخهٔ جامع", meaning: "Bütün İlahî isimlerin tecellilerini kendinde toplayan özet nüsha (İnsan)", rarity: "nadir", category: "Tasavvuf", source: "Sorularla Risale" }
  ],
  "mektubat-1": [
    { term: "Merâtib-i Hayat", ottoman: "مراتب حيات", meaning: "Hayatın beş farklı derecesi ve tabakası (Bizim hayatımızdan Hz. Hızır ve meleklerin hayatına kadar)", rarity: "nadir", category: "Kelâm", source: "Sorularla Risale" },
    { term: "Kayd-ı Beşeriyet", ottoman: "قيد بشريت", meaning: "İnsanın yeme, içme, zaman ve mekân gibi maddi beden kayıtlarıyla sınırlı oluşu", rarity: "nadir", category: "Terkip", source: "RisaletinNur Lügat" },
    { term: "Muhtasar", ottoman: "مختصر", meaning: "Kısa, özlü, az sözle çok mana ifade eden", rarity: "yaygin", category: "Edebî", source: "500 Kelime" },
    { term: "Tevatür", ottoman: "تواتر", meaning: "Yalan üzere birleşmeleri imkânsız büyük bir topluluğun verdiği kesin haber", rarity: "nadir", category: "Usûl", source: "500 Kelime" },
    { term: "Keşif ve Şuhud", ottoman: "كشف و شهود", meaning: "Perdelerin açılmasıyla manevî hakikatleri bizzat görerek müşahede etme", rarity: "nadir", category: "Tasavvuf", source: "Sorularla Risale" },
    { term: "Muhabbet-i İlâhiye", ottoman: "محبت الهيه", meaning: "Allah sevgisi; bütün sevgi ve güzelliklerin asıl kaynağı", rarity: "yaygin", category: "İman", source: "Sorularla Risale" }
  ],
  "mektubat-2": [
    { term: "İstiğna", ottoman: "استغنا", meaning: "Gönül tokluğu; Allah'tan başkasına el açmama ve kimseden maddi beklentiye girmeme düsturu", rarity: "nadir", category: "Ahlâk", source: "500 Kelime" },
    { term: "İhlas", ottoman: "اخلاص", meaning: "İbadet ve hizmette yalnız Allah'ın rızasını gözetip dünyevî çıkar karıştırmamak", rarity: "yaygin", category: "Ahlâk", source: "Sorularla Risale" },
    { term: "Minnet", ottoman: "منت", meaning: "Yapılan iyilik sebebiyle başkasının manevi ağırlığı ve borcu altına girme", rarity: "yaygin", category: "Ahlâk", source: "500 Kelime" },
    { term: "Düstur-u Hayat", ottoman: "دستور حيات", meaning: "Hayat boyu taviz verilmeden uygulanan temel ilke ve prensip", rarity: "yaygin", category: "Terkip", source: "Sorularla Risale" },
    { term: "Mukabele", ottoman: "مقابله", meaning: "Karşılık verme, yapılan bir şeye denk bir cevapla karşılıkta bulunma", rarity: "yaygin", category: "Lügavî", source: "500 Kelime" },
    { term: "Zillet", ottoman: "ذلت", meaning: "Alçalma, onur ve izzetin zedelenmesi", rarity: "nadir", category: "Ahlâk", source: "500 Kelime" }
  ],
  "mektubat-3": [
    { term: "Risalet-i Ahmediye", ottoman: "رسالت احمديه", meaning: "Hz. Muhammed'in (a.s.m.) peygamberlik vazifesi ve elçiliği", rarity: "yaygin", category: "Siyer / Kelâm", source: "Sorularla Risale" },
    { term: "İrhasat", ottoman: "ارهاصات", meaning: "Peygamber Efendimizin (a.s.m.) nübüvvetinden önce meydana gelen olağanüstü hazırlık hadiseleri", rarity: "nadir", category: "Kelâm", source: "500 Kelime" },
    { term: "Şakk-ı Kamer", ottoman: "شق قمر", meaning: "Peygamberimizin (a.s.m.) mübarek parmağının işaretiyle Ay'ın ikiye yarılması mucizesi", rarity: "nadir", category: "Mucize", source: "Sorularla Risale" },
    { term: "Medâr-ı Fahir", ottoman: "مدار فخر", meaning: "Övünç kaynağı, varlık âleminin kendisiyle iftihar ettiği zat", rarity: "nadir", category: "Terkip", source: "RisaletinNur Lügat" },
    { term: "Nübüvvet", ottoman: "نبوت", meaning: "Peygamberlik makamı, İlahî vahyi insanlığa tebliğ etme vazifesi", rarity: "yaygin", category: "Kelâm", source: "500 Kelime" },
    { term: "Mu'cizât", ottoman: "معجزات", meaning: "Peygamberlerin davalarını doğrulamak için Allah'ın yarattığı harikulade olaylar", rarity: "yaygin", category: "Kelâm", source: "Sorularla Risale" }
  ],
  "mektubat-4": [
    { term: "Mâbud-u Bilhak", ottoman: "معبود بالحق", meaning: "İbadet edilmeye hakkıyla layık olan tek gerçek İlah", rarity: "nadir", category: "Tevhid", source: "Sorularla Risale" },
    { term: "Tevhid-i Rububiyet", ottoman: "توحيد ربوبيت", meaning: "Bütün âlemlerin terbiye ve idaresinde Allah'ı tek ve ortaksız bilmek", rarity: "yaygin", category: "Tevhid", source: "Sorularla Risale" },
    { term: "İsm-i Azam", ottoman: "اسم اعظم", meaning: "Cenâb-ı Hakk'ın bütün isimlerini kuşatan en büyük ve en kapsamlı ismi", rarity: "yaygin", category: "Tevhid", source: "Sorularla Risale" },
    { term: "Vahdet", ottoman: "وحدت", meaning: "Birlik, teklik; her şeyin tek bir Yaratıcının elinden çıkması", rarity: "yaygin", category: "Tevhid", source: "500 Kelime" },
    { term: "Şerik", ottoman: "شريك", meaning: "Ortak; Cenâb-ı Hak mülkünde ve icraatında her türlü şerikten münezzehtir", rarity: "nadir", category: "Kelâm", source: "500 Kelime" }
  ],
  "mektubat-5": [
    { term: "Uhuvvet", ottoman: "اخوت", meaning: "İman kardeşliği, müminler arasındaki sarsılmaz kalbî bağlılık", rarity: "yaygin", category: "Ahlâk", source: "500 Kelime" },
    { term: "Adâvet", ottoman: "عداوت", meaning: "Düşmanlık, kin ve nefret besleme", rarity: "yaygin", category: "Ahlâk", source: "500 Kelime" },
    { term: "Şikak", ottoman: "شقاق", meaning: "Ayrılık, bölünme, uyuşmazlık ve geçimsizlik", rarity: "nadir", category: "Ahlâk", source: "500 Kelime" },
    { term: "İnsaniyet-i Kübrâ", ottoman: "انسانيت كبرى", meaning: "İnsanlığın en yüksek ve kâmil mertebesi olan İslâmiyet", rarity: "nadir", category: "Terkip", source: "Sorularla Risale" },
    { term: "Merdut", ottoman: "مردود", meaning: "Reddedilmiş, İlahî rahmet ve hikmet tarafından kabul edilmeyen", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Tarafgirlik", ottoman: "طرفكيرلك", meaning: "Körü körüne taraftarlık yapıp karşı tarafı haksızca kötüleme", rarity: "yaygin", category: "Ahlâk", source: "Sorularla Risale" }
  ],
  "lemalar-1": [
    { term: "Münâcât", ottoman: "مناجات", meaning: "Kalbin en derin yakarışıyla Cenâb-ı Hakk'a yönelip dua etmesi", rarity: "yaygin", category: "İbadet", source: "Sorularla Risale" },
    { term: "Vasıta-i Necat", ottoman: "واسطهٔ نجات", meaning: "Kurtuluş vesilesi, selamete çıkaran manevi gemi", rarity: "nadir", category: "Terkip", source: "Sorularla Risale" },
    { term: "İcabe-i Dua", ottoman: "اجابهٔ دعا", meaning: "Duanın Cenâb-ı Hak katında kabul edilip karşılık bulması", rarity: "nadir", category: "Terkip", source: "RisaletinNur Lügat" },
    { term: "Dağdağalı", ottoman: "دغدغه لى", meaning: "Çalkantılı, gürültülü, sıkıntı ve fırtınalarla dolu", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Hülâsa", ottoman: "خلاصه", meaning: "Özet, bir meselenin en öz ve temel kısmı", rarity: "yaygin", category: "Lügavî", source: "500 Kelime" }
  ],
  "lemalar-2": [
    { term: "Marifet-i İlâhiye", ottoman: "معرفت الهيه", meaning: "Cenâb-ı Hakk'ı isim, sıfat ve tecellileriyle kalben tanıma ilmi", rarity: "yaygin", category: "İman", source: "Sorularla Risale" },
    { term: "Mücerreb", ottoman: "مجرب", meaning: "Defalarca denenmiş ve tesiri kesin olarak görülmüş", rarity: "nadir", category: "Lügavî", source: "RisaletinNur Lügat" },
    { term: "Tevellüd", ottoman: "تولد", meaning: "Doğma, meydana gelme, türeme", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Haleldar", ottoman: "خللدار", meaning: "Zarar görmüş, bozulmaya ve zedelenmeye uğramış", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Kemal-i Sabır", ottoman: "كمال صبر", meaning: "Hiç şikâyet etmeden gösterilen en olgun ve tam sabır", rarity: "yaygin", category: "Ahlâk", source: "Sorularla Risale" }
  ],
  "lemalar-3": [
    { term: "İktisat", ottoman: "اقتصاد", meaning: "Nimete hürmet ederek ölçülü kullanmak; bereketin en mühim anahtarı", rarity: "yaygin", category: "Ahlâk", source: "500 Kelime" },
    { term: "Kanaat", ottoman: "قناعت", meaning: "Allah'ın verdiği helal rızka razı olup hırs ve zillete düşmemek", rarity: "yaygin", category: "Ahlâk", source: "500 Kelime" },
    { term: "Nehy-i Sarih", ottoman: "نهى صريح", meaning: "Apaçık ve kesin İlahî yasaklama", rarity: "nadir", category: "Fıkıh", source: "Sorularla Risale" },
    { term: "İstihfaf", ottoman: "استخفاف", meaning: "Hafife alma, küçümseme, nimetin kıymetini bilmeme", rarity: "nadir", category: "Ahlâk", source: "500 Kelime" },
    { term: "İhtiram", ottoman: "احترام", meaning: "Hürmet gösterme, saygı ve edeple değer verme", rarity: "nadir", category: "Ahlâk", source: "500 Kelime" }
  ],
  "lemalar-4": [
    { term: "Nokta-i İstinad", ottoman: "نقطهٔ استناد", meaning: "Sarsılmaz dayanak noktası ve manevi kuvvet kaynağı", rarity: "yaygin", category: "Kelâm", source: "Sorularla Risale" },
    { term: "Tarîk-ı Hakikat", ottoman: "طريق حقيقت", meaning: "Hakikate ve rıza-yı İlahîye ulaştıran en doğru ve kısa yol", rarity: "nadir", category: "Tasavvuf", source: "Sorularla Risale" },
    { term: "Tefani", ottoman: "تفانى", meaning: "Kardeşlerinin şahs-ı manevisinde benliğini eritip 'biz' olma sırrı", rarity: "nadir", category: "Ahlâk / Tasavvuf", source: "Sorularla Risale" },
    { term: "Vesile-i Makasıd", ottoman: "وسيلهٔ مقاصد", meaning: "Ulvi gayelere ve maksatlara ulaştıran en feyizli vesile", rarity: "nadir", category: "Terkip", source: "RisaletinNur Lügat" },
    { term: "Haslet", ottoman: "خصلت", meaning: "Huy, yaratılıştan gelen veya kazanılan güzel ahlâk özelliği", rarity: "yaygin", category: "Ahlâk", source: "500 Kelime" }
  ],
  "lemalar-5": [
    { term: "Şâfî-i Hakikî", ottoman: "شافىٔ حقيقى", meaning: "Maddi ve manevi bütün dertlere gerçek şifa veren Cenâb-ı Hak", rarity: "yaygin", category: "Esmâ", source: "Sorularla Risale" },
    { term: "Kefaretü'z-Zünûb", ottoman: "كفارت الذنوب", meaning: "Hastalığın ve musibetlerin günahlara kefaret olup ruhu arındırması", rarity: "nadir", category: "Hadis Tabiri", source: "Sorularla Risale" },
    { term: "Nâfi", ottoman: "نافع", meaning: "Faydalı, şifalı ve tesirli olan", rarity: "nadir", category: "Lügavî", source: "500 Kelime" },
    { term: "Musibetzede", ottoman: "مصيبتزده", meaning: "Dert, hastalık veya sıkıntıya uğramış kimse", rarity: "yaygin", category: "Lügavî", source: "Sorularla Risale" },
    { term: "Meyvedar", ottoman: "ميوه دار", meaning: "Meyve veren, ahiret için bol sevap ve nur kazandıran", rarity: "yaygin", category: "Edebî", source: "500 Kelime" }
  ]
};

// Külliyat genelinde en sık karşılaşılan Nadir / Az Bilinen Osmanlıca Kelimeler + Ezberlenecek 500 Kelime Külliyat Sözlüğü
export const GLOBAL_RARE_LUGAT: LugatWord[] = [
  // Temel Istılahlar (Sorularla Risale & RisaletinNur)
  { term: "Sikke-i Tasdik-i Gaybî", ottoman: "سكهٔ تصديق غيبى", meaning: "Gaybî doğrulama mührü; Kur'an ve iman hakikatlerinin tevafuk ve işaretleri", rarity: "nadir", category: "Külliyat", source: "Sorularla Risale" },
  { term: "Tevafuk", ottoman: "توافق", meaning: "Tesadüfe verilemeyecek kadar hikmetli ve ince uygunluk, birbirine denk gelme", rarity: "nadir", category: "Kelâm", source: "500 Kelime" },
  { term: "İstidrâc", ottoman: "استدراج", meaning: "Hak yoldan sapanlara imtihan sırrıyla verilen geçici dünyevî parlaklık", rarity: "nadir", category: "Kelâm", source: "500 Kelime" },
  { term: "Tahkîkî İman", ottoman: "تحقيقى ايمان", meaning: "Taklitten kurtulup delil, burhan ve tefekkürle kazanılan sarsılmaz iman", rarity: "yaygin", category: "İman", source: "Sorularla Risale" },
  { term: "Enaniyet", ottoman: "انانيت", meaning: "Benlik; insana emanet verilen sıfatlarla İlahî sıfatları tanımak için verilen ölçü birimi", rarity: "yaygin", category: "Ruhiyat", source: "500 Kelime" },
  { term: "Mana-yı Harfî", ottoman: "معناى حرفى", meaning: "Varlıklara kendileri için değil, Sanatkâr ve Yaratıcısını gösteren birer harf olarak bakmak", rarity: "nadir", category: "Kelâm", source: "Sorularla Risale" },
  { term: "Mana-yı İsmî", ottoman: "معناى اسمى", meaning: "Varlığa Yaratıcısını unutarak yalnızca kendi zatı ve maddesi hesabına bakmak", rarity: "nadir", category: "Kelâm", source: "Sorularla Risale" },
  { term: "Şahs-ı Manevî", ottoman: "شخص معنوى", meaning: "Müminlerin ihlas ve uhuvvetle bir araya gelmesinden doğan ortak manevi ruh ve kuvvet", rarity: "nadir", category: "Ahlâk", source: "Sorularla Risale" },
  { term: "Ehadiyet", ottoman: "احديت", meaning: "Cenâb-ı Hakk'ın bütün isimlerinin her bir küçük varlıkta (bir çiçekte) bir arada tecelli etmesi", rarity: "nadir", category: "Tevhid", source: "Sorularla Risale" },
  { term: "Vâhidiyet", ottoman: "واحديت", meaning: "Cenâb-ı Hakk'ın bütün kâinatı kuşatan küllî ve umumi birlik tecellisi", rarity: "nadir", category: "Tevhid", source: "Sorularla Risale" },
  { term: "Müsbet Hareket", ottoman: "مثبت حركت", meaning: "Yıkıcı olmadan, kendi iman ve hakikat vazifesini ihlasla yapıp İlahî vazifeye karışmamak", rarity: "yaygin", category: "Ahlâk", source: "Sorularla Risale" },
  { term: "Şefkat-i İmaniye", ottoman: "شفقت ايمانيه", meaning: "İmandan gelen, hiçbir karşılık beklemeyen saf ve nezih merhamet", rarity: "nadir", category: "Ahlâk", source: "Sorularla Risale" },
  { term: "Huzur-u Daimî", ottoman: "حضور دائمى", meaning: "Her an Cenâb-ı Hakk'ın huzurunda bulunduğunu bilerek uyanık kalp ile yaşamak", rarity: "nadir", category: "Tasavvuf", source: "Sorularla Risale" },
  { term: "Acz ve Fakr", ottoman: "عجز و فقر", meaning: "Kulun kendi güçsüzlüğünü ve muhtaçlığını anlayıp Kudret ve Rahmet-i İlahiyeye tam dayanması", rarity: "yaygin", category: "Tasavvuf", source: "Sorularla Risale" },
  { term: "Şevk ve Şükür", ottoman: "شوق و شكر", meaning: "Kulluk vazifesinde manevi coşku ve İlahî nimetlere karşı daimi minnettarlık", rarity: "yaygin", category: "Ahlâk", source: "Sorularla Risale" },
  { term: "Adem", ottoman: "عدم", meaning: "Yokluk, hiçlik; bütün şerlerin ve çirkinliklerin kaynağı", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Vücud", ottoman: "وجود", meaning: "Varlık, var olma; bütün hayır ve güzelliklerin menbaı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "A'mâl", ottoman: "اعمال", meaning: "Ameller, yapılan işler ve ibadetler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Âb-ı Hayat", ottoman: "آب حيات", meaning: "Ebedî hayat suyu, kalplere can veren iman hakikati", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Âfâk", ottoman: "آفاق", meaning: "Ufuklar, insanın dışındaki büyük kâinat âlemi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Enfüs", ottoman: "انفس", meaning: "İnsanın kendi iç dünyası, kalp, ruh ve vicdan âlemi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ahval", ottoman: "احوال", meaning: "Hâller, vaziyetler, durumlar", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Akîm", ottoman: "عقيم", meaning: "Neticesiz, sonuçsuz, meyvesiz kalan", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Akvam", ottoman: "اقوام", meaning: "Kavimler, milletler, topluluklar", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Âlâm", ottoman: "آلام", meaning: "Elemler, acılar, kederler ve ızdıraplar", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Alâmet-i Farika", ottoman: "علامت فارقه", meaning: "Bir şeyi diğerlerinden ayıran en belirgin özellik, ayırıcı nişan", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Âlem-i Gayb", ottoman: "عالم غيب", meaning: "Duyu organlarıyla algılanamayan, iman ve basiretle bilinen görünmez âlem", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Âlem-i Şehadet", ottoman: "عالم شهادت", meaning: "Gözle görülen ve duyularla müşahede edilen maddi varlık âlemi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Âli", ottoman: "عالى", meaning: "Yüce, yüksek, ulvî ve pek kıymetli", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Âmâl", ottoman: "آمال", meaning: "Emeller, insanın uzanıp giden arzu ve ümitleri", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Anâsır", ottoman: "عناصر", meaning: "Unsurlar, kâinatı oluşturan temel elementler (toprak, su, hava, nur)", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Arz", ottoman: "ارض", meaning: "Yeryüzü, dünya küresi; sunma, bildirme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Âsâr", ottoman: "آثار", meaning: "Eserler, izler, sanatlı işaretler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Asfiya", ottoman: "اصفيا", meaning: "Kalpleri arınmış, Peygamber varisi olan büyük ve safi âlimler", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Evliya", ottoman: "اوليا", meaning: "Allah'ın dostları, velayet makamına ermiş mübarek zatlar", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Atâ", ottoman: "عطا", meaning: "İhsan, bağış, karşılıksız cömert ikram", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Avam", ottoman: "عوام", meaning: "Halk tabakası; ilimde derinleşmemiş sade müminler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Havas", ottoman: "خواص", meaning: "İlim, marifet ve takvada seçkinleşmiş zatlar", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "A'zam", ottoman: "اعظم", meaning: "En büyük, en yüce, en azametli", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Azîm", ottoman: "عظيم", meaning: "Büyük, haşmetli, pek mühim", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bâdî", ottoman: "بادى", meaning: "Sebep olan, yol açan, vesile", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bâhir", ottoman: "باهر", meaning: "Apaçık, parlak, göz kamaştırıcı derecede net", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bahr", ottoman: "بحر", meaning: "Deniz, umman, büyük su kütlesi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Basiret", ottoman: "بصيرت", meaning: "Kalp gözü, hakikati ve perde arkasını görebilme feraseti", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bâtıl", ottoman: "باطل", meaning: "Hakikati olmayan, çürük, asılsız ve boş", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bâtın", ottoman: "باطن", meaning: "İç yüz, gizli ve derin mânâ ciheti", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Zâhir", ottoman: "ظاهر", meaning: "Dış görünüş, açık ve meydanda olan", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bedâhet", ottoman: "بداهت", meaning: "Delile bile ihtiyaç bırakmayacak kadar apaçık olma", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bedî'", ottoman: "بديع", meaning: "Eşi ve benzeri olmadan, örneksiz ve harika şekilde yaratan", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Belâğat", ottoman: "بلاغت", meaning: "Sözün düzgün, etkili ve muhatabın hâline tam uygun söylenmesi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Beşer", ottoman: "بشر", meaning: "İnsanoğlu, insan nev'i", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Beyan", ottoman: "بيان", meaning: "Açıklama, izah etme, ortaya koyma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bîçare", ottoman: "بيچاره", meaning: "Çaresiz, elinden bir şey gelmeyen aciz kul", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bînihaye", ottoman: "بينهايه", meaning: "Sonsuz, sınırsız, uçsuz bucaksız", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bizzat", ottoman: "بالذات", meaning: "Kendi zatıyla, doğrudan doğruya, aracısız", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Câmi'", ottoman: "جامع", meaning: "Toplayan, bir araya getiren, kapsamlı", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Câzibe", ottoman: "جاذبه", meaning: "Çekim kuvveti, kendine çeken güzellik ve alımlılık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Cehalet", ottoman: "جهالت", meaning: "Bilgisizlik, hakikatten habersiz olma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Celâl", ottoman: "جلال", meaning: "Cenâb-ı Hakk'ın büyüklük, azamet ve haşmet sıfatlarının tecellisi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Cemâl", ottoman: "جمال", meaning: "Cenâb-ı Hakk'ın sonsuz güzellik, şefkat ve rahmet sıfatlarının tecellisi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Cemادات", ottoman: "جمادات", meaning: "Cansız varlıklar (taş, toprak, madenler)", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Cerh", ottoman: "جرح", meaning: "Çürütme, batıl bir fikri delille geçersiz kılma", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Cevher", ottoman: "جوهر", meaning: "Bir şeyin özü, mayası, çok kıymetli taş", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Cihet", ottoman: "جهت", meaning: "Yön, taraf, açı, bakım", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Cüz'î", ottoman: "جزئى", meaning: "Az, sınırlı, bir bütüne ait küçük parça", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Küllî", ottoman: "كلى", meaning: "Bütünü kuşatan, genel, geniş kapsamlı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Dalâlet", ottoman: "ضلالت", meaning: "Hak ve doğru yoldan sapma, inançsızlık ve şaşkınlık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hidayet", ottoman: "هدايت", meaning: "Doğru ve hak yol olan İslâm'a ve imana kavuşma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Dâr-ı İmtihan", ottoman: "دار امتحان", meaning: "Sınav ve deneme yurdu olan dünya hayatı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Defineler", ottoman: "دفينه لر", meaning: "Hazineler, İlahî isimlerin gizli manevi servetleri", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Dehşet", ottoman: "دهشت", meaning: "Büyük hayret ve korku uyandıran hâl", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Derc", ottoman: "درج", meaning: "İçine yerleştirme, şifreleme, kaydetme (çekirdeğe ağacın dercedilmesi)", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Deva", ottoman: "دوا", meaning: "İlaç, çare, kalp ve ruh yaralarını iyileştiren şifa", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Devam", ottoman: "دوام", meaning: "Süreklilik, kesilmeyip sürüp gitme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Dirhem", ottoman: "درهم", meaning: "Küçük ağırlık ölçüsü (yaklaşık üç gram); az miktar", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Düstur", ottoman: "دستور", meaning: "Genel kural, değişmez prensip ve kanun", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ebed", ottoman: "ابد", meaning: "Sonu olmayan gelecek zaman, sonsuzluk", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ezel", ottoman: "ازل", meaning: "Başlangıcı olmayan geçmiş zaman, öncesizlik", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ebleh", ottoman: "ابله", meaning: "Ahmak, akılsız, elmas kıymetindeki ahireti cam parçasına değişen", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ecza", ottoman: "اجزا", meaning: "Parçalar, kısımlar, birleşen unsurlar", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ef'âl", ottoman: "افعال", meaning: "Fiiller, işler ve İlahî icraatlar", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Efkâr", ottoman: "افكار", meaning: "Fikirler, düşünceler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Efrad", ottoman: "افراد", meaning: "Fertler, bireyler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ehemmiyet", ottoman: "اهميت", meaning: "Önem, kıymet ve değer derecesi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ekseriyet", ottoman: "اكثريت", meaning: "Çoğunluk, büyük kısım", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Elem", ottoman: "الم", meaning: "Acı, üzüntü, ruhî ve bedenî ızdırap", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Elîm", ottoman: "اليم", meaning: "Acıklı, çok ızdırap verici", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Emâre", ottoman: "اماره", meaning: "Belirti, iz, ipucu, işaret", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Emsal", ottoman: "امثال", meaning: "Benzerler, eşler, örnekler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Enbiya", ottoman: "انبيا", meaning: "Nebiler, Allah'ın gönderdiği peygamberler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Envâ'", ottoman: "انواع", meaning: "Türler, çeşitler, sınıflar", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Envar", ottoman: "انوار", meaning: "Nurlar, ışıklar, iman hakikatlerinin parıltıları", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Erkân", ottoman: "اركان", meaning: "Rükünler, bir binayı veya hakikati ayakta tutan temel direkler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Erzak", ottoman: "ارزاق", meaning: "Rızıklar, canlıların beslenmesi için gönderilen nimetler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Esbab", ottoman: "اسباب", meaning: "Sebepler; İlahî kudretin izzetine perde kılınan zahiri vasıtalar", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Esرار", ottoman: "اسرار", meaning: "Sırlar, ince hikmetler ve derin hakikatler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Eşya", ottoman: "اشيا", meaning: "Şeyler, yaratılmış varlıklar", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Evham", ottoman: "اوهام", meaning: "Vehimler, yersiz kuruntular ve hayalî korkular", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Evsaf", ottoman: "اوصاف", meaning: "Vasıflar, nitelikler, özellikler", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Fâil-i Muhtar", ottoman: "فاعل مختار", meaning: "Her işi kendi hür irade ve dilemesiyle yaratan Cenâb-ı Hak", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Fazilet", ottoman: "فضيلت", meaning: "Üstünlük, erdem, güzel ahlâk ve manevi değer", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Fekahet", ottoman: "فقاهت", meaning: "Derin anlayış, fıkıh ve incelik bilgisi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Fenâ", ottoman: "فنا", meaning: "Gelip geçicilik, fanilik, yok olmaya yüz tutma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Bekâ", ottoman: "بقا", meaning: "Kalıcılık, ölümsüzlük ve sonsuz devamlılık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ferah", ottoman: "فرح", meaning: "Gönül açıklığı, sevinç, iç huzuru", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Feyiz", ottoman: "فيض", meaning: "Manevî bolluk, bereket, kalbe akan ilim ve nur", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Firak", ottoman: "فراق", meaning: "Ayrılık, sevdiklerinden ayrı düşme acısı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Visal", ottoman: "وصال", meaning: "Kavuşma, ayrılığın sona erip dosta erişmesi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Fطرة", ottoman: "فطرت", meaning: "Yaratılış, ilk ve bozulmamış İlahî program", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Gaflet", ottoman: "غفلت", meaning: "Allah'ı ve ahireti unutarak dünyaya dalma uykusu", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Gaye-i Hayal", ottoman: "غايهٔ خيال", meaning: "İnsanın ulaşmayı en çok arzuladığı en yüksek hedef", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Gayr-ı Mütenahî", ottoman: "غير متناهى", meaning: "Sonu olmayan, sınırsız, nihayetsiz", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Gınâ", ottoman: "غنا", meaning: "Zenginlik, hiçbir şeye muhtaç olmama", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hâdim", ottoman: "خادم", meaning: "Hizmet eden, hizmetkâr (Kur'an hâdimi)", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hads", ottoman: "حدس", meaning: "Süratli intikal, kalbin delilleri bir anda kavrayan keskin sezgisi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hadsiz", ottoman: "حدسز", meaning: "Sınırsız, sayılamayacak kadar çok", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hafîz", ottoman: "حفيظ", meaning: "Her şeyi bütün incelikleriyle kaydedip muhafaza eden Cenâb-ı Hak", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hakîm", ottoman: "حكيم", meaning: "Her işini sayısız gaye, fayda ve hikmetle yaratan Allah", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hâkimiyet", ottoman: "حاكميت", meaning: "Hükümranlık, mutlak idare ve saltanat", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hakikat", ottoman: "حقيقت", meaning: "Gerçek, bir şeyin aslı ve değişmez özü", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Halâskar", ottoman: "خلاصكار", meaning: "Kurtarıcı, tehlikeden selamete çıkaran", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Halife-i Arz", ottoman: "خليفهٔ ارض", meaning: "Yeryüzünde İlahî isimlerin tecellilerine muhatap olan insan", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Harikulade", ottoman: "خارق العاده", meaning: "Alışılmışın ve sıradanlığın üstünde, olağanüstü", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hâşâ", ottoman: "حاشا", meaning: "Asla, kesinlikle öyle değildir, Allah korusun", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hâşiye", ottoman: "حاشيه", meaning: "Metnin altındaki açıklayıcı dipnot ve ince izah", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hâtime", ottoman: "خاتمه", meaning: "Son söz, bir eserin veya bölümün kapanış kısmı", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hayat-ı Bâkiye", ottoman: "حيات باقيه", meaning: "Hiç son bulmayacak olan ebedî ahiret hayatı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hayat-ı İçtimaiye", ottoman: "حيات اجتماعيه", meaning: "Toplum hayatı, sosyal yaşayış", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hikmet", ottoman: "حكمت", meaning: "Her şeyin belirli bir gaye, fayda ve ölçü ile yaratılması", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hilkat", ottoman: "خلقت", meaning: "Yaratılış, Cenâb-ı Hakk'ın varlıkları halk etmesi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Himmet", ottoman: "همت", meaning: "Ciddi gayret, yüksek çalışma azmi ve manevi yardım", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hüccet", ottoman: "حجت", meaning: "Kuvvetli delil, sarsılmaz senet ve ispat", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Hüsn-ü Zan", ottoman: "حسن ظن", meaning: "İyi niyet besleme, mümin kardeşi hakkında güzel düşünme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İ'câz", ottoman: "اعجاز", meaning: "Mucizelik; Kur'an'ın benzerini getirmekte bütün insanları aciz bırakması", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İcad", ottoman: "ايجاد", meaning: "Yoktan var etme, vücuda getirme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İcmal", ottoman: "اجمال", meaning: "Özetleme, bir hakikati ana hatlarıyla kısaca toplama", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tafsil", ottoman: "تفصيل", meaning: "Ayrıntılarıyla genişçe açıklama", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İfrat", ottoman: "افراط", meaning: "Ölçüyü aşarak aşırıya gitme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tefrit", ottoman: "تفريط", meaning: "Normal ölçünün altında kalma, gevşeklik ve ihmal", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İhatalı", ottoman: "احاطه لى", meaning: "Geniş kapsamlı, her tarafı kuşatan", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İhtiyar", ottoman: "اختيار", meaning: "Seçme hürriyeti, cüz'î irade; tercih etme", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İhtizaz", ottoman: "اهتزاز", meaning: "Titreşim, dalgalanma, manevi heyecanla titreme", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İnayet", ottoman: "عنايت", meaning: "Allah'ın özel yardımı, lütfu ve koruyucu ihsanı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İnkişaf", ottoman: "انكشاف", meaning: "Açılma, gelişme, tomurcuğun çiçek açması gibi ortaya çıkma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İnsibağ", ottoman: "انصباغ", meaning: "Boyanma, manevi bir rengi ve ahlâkı özümseme", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İntباه", ottoman: "انتباه", meaning: "Gafletten uyanma, kendine gelip hakikati fark etme", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İrşad", ottoman: "ارشاد", meaning: "Hak yolu gösterme, kalpleri uyandırıp aydınlatma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İstikbal", ottoman: "استقبال", meaning: "Gelecek zaman, önümüzdeki ömür ve ahiret menzilleri", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İstiğfar", ottoman: "استغفار", meaning: "Allah'tan günahların bağışlanmasını dileme (Estağfirullah)", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İstihale", ottoman: "استحاله", meaning: "Bir maddeden başka bir hâle dönüşüm (toprağın çiçeğe dönüşmesi)", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İstikra", ottoman: "استقرا", meaning: "Tek tek olayları inceleyerek genel bir hakikate ulaşma (tümevarım)", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İstila", ottoman: "استيلا", meaning: "Kaplama, yayılma, hâkimiyeti altına alma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İstinsah", ottoman: "استنساخ", meaning: "Nüsha çıkarma, Risale-i Nur eserlerini el yazısıyla çoğaltma", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "İttihad", ottoman: "اتحاد", meaning: "Birlik olma, gönül ve gaye birliği (İttihad-ı İslâm)", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İttifak", ottoman: "اتفاق", meaning: "Uyuşma, söz ve fikir birliği etme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "İz'an", ottoman: "اذعان", meaning: "Kalp ile kavrama, samimi kabul ve anlayış", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Kâffe", ottoman: "كافه", meaning: "Bütün, tamamı, hepsi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Kânun-u İlâhî", ottoman: "قانون الهى", meaning: "Cenâb-ı Hakk'ın kâinata koyduğu fıtrat ve yaratılış yasaları", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Kasem", ottoman: "قسم", meaning: "Yemin, Kur'an'da dikkat çekilen büyük yeminler", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Kat'î", ottoman: "قطعى", meaning: "Kesin, şüphe götürmeyen", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Kevnî", ottoman: "كونى", meaning: "Kâinata ve yaratılışa ait olan", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Kıyamet", ottoman: "قيامت", meaning: "Dünya nizamının bozulup ahiret âleminin başlaması", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Küfür", ottoman: "كفر", meaning: "İman hakikatlerini örtme, inkâr ve inançsızlık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Lâtife", ottoman: "لطيفه", meaning: "İnsandaki ince manevi duygu ve his cihazı; nükteli güzel söz", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Lem'a", ottoman: "لمعه", meaning: "Parıltı, hakikat güneşinden yansıyan nur pırıltısı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Letâfet", ottoman: "لطافت", meaning: "İncelik, hoşluk, manevi güzellik ve nezaket", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Levh-i Mahfuz", ottoman: "لوح محفوظ", meaning: "Bütün olmuş ve olacak şeylerin İlahî ilimde yazıldığı ana kader kitabı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Liyakat", ottoman: "لياقت", meaning: "Layık olma, ehliyet ve uygunluk", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mahiyet", ottoman: "ماهيت", meaning: "Bir şeyin iç yüzü, asıl niteliği ve öz yapısı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mahlûkat", ottoman: "مخلوقات", meaning: "Yaratılmışlar, bütün canlı ve cansız varlıklar", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mahzar", ottoman: "مظهر", meaning: "İlahî isim ve sıfatların üzerinde göründüğü, tecelli ettiği yer", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Maksat", ottoman: "مقصد", meaning: "Amaç, gaye, hedeflenen netice", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mâlik-ül Mülk", ottoman: "مالك الملك", meaning: "Bütün mülkün ve kâinatın gerçek sahibi olan Cenâb-ı Hak", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Masnuat", ottoman: "مصنوعات", meaning: "Sanatla yaratılmış varlıklar, İlahî sanat eserleri", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mazhar", ottoman: "مظهر", meaning: "Bir nimete veya tecelliye nail olan, aynalık eden", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mebde'", ottoman: "مبدأ", meaning: "Başlangıç noktası, ilk kaynak", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Münteha", ottoman: "منتها", meaning: "Varılacak en son nokta, nihayet", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mecaz", ottoman: "مجاز", meaning: "Bir kelimenin gerçek anlamı dışında benzetme yoluyla kullanılması", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Meşiet", ottoman: "مشيت", meaning: "Cenâb-ı Hakk'ın küllî dilemesi ve iradesi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Meşrep", ottoman: "مشرب", meaning: "Manevi hizmet tarzı, mizaç ve yol", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Meşru", ottoman: "مشروع", meaning: "Dinî hükümlere uygun, helal kılınmış", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mihenk", ottoman: "محك", meaning: "Altını bakırdan, hakkı batıldan ayıran ölçü taşı", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Misal", ottoman: "مثال", meaning: "Örnek, anlaşılması güç hakikatleri yaklaştıran temsil", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Muhabbet", ottoman: "محبت", meaning: "Sevgi, gönül bağlılığı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Muhاكemat", ottoman: "محاكمات", meaning: "Akıl ve mantık ölçüleriyle yapılan muhakemeler ve tefsir usulü", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Muhال", ottoman: "محال", meaning: "İmkânsız, gerçekleşmesi aklen mümkün olmayan", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Muhit", ottoman: "محيط", meaning: "Kuşatan, çevreleyen, ilmi ve kudreti her şeyi saran", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mukaddeme", ottoman: "مقدمه", meaning: "Giriş, önsöz, bir hakikate hazırlık basamağı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mukadderat", ottoman: "مقدرات", meaning: "Kaderde takdir edilmiş hadiseler", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mukni", ottoman: "مقنع", meaning: "İkna edici, akıl ve kalbi tatmin eden cevap", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Muntazam", ottoman: "منتظم", meaning: "Düzenli, intizamlı, ölçülü", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Murassa", ottoman: "مرصع", meaning: "Değerli mücevherlerle süslenmiş ve işlenmiş", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Musibet", ottoman: "مصيبت", meaning: "İnsanı uyarmak ve arındırmak için gelen bela, sıkıntı ve imtihan", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mutabık", ottoman: "مطابق", meaning: "Uygun, birbirine tam uyan", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Muvaffakiyet", ottoman: "موفقيت", meaning: "Allah'ın yardımıyla başarıya ulaşma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Muvakkat", ottoman: "موقت", meaning: "Geçici, kısa bir süre için belirlenmiş", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Muvazene", ottoman: "موازنه", meaning: "Karşılaştırma, denge, iman ile küfrün tartılması", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mübadele", ottoman: "مبادله", meaning: "Değiş tokuş, fani ömrü baki hayatla değiştirme", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mücahede", ottoman: "مجاهده", meaning: "Nefis ve şeytana karşı verilen manevi cihad ve gayret", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Müessir", ottoman: "مؤثر", meaning: "Tesir eden; hakikî müessir yalnız Cenâb-ı Hak'tır", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mükâfat", ottoman: "مكافات", meaning: "İyilik ve ibadete karşılık verilen İlahî ödül, sevap", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mükevvîn", ottoman: "مكون", meaning: "Kâinatı yaratan ve varlık âlemini idare eden Allah", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Münasebet", ottoman: "مناسبت", meaning: "İlgili olma, bağlantı, hikmetli alaka", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Münezzeh", ottoman: "منزه", meaning: "Her türlü kusur, acz, ortak ve noksandan uzak ve pak", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mürşid", ottoman: "مرشد", meaning: "Doğru yolu gösteren manevi rehber", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Müstakim", ottoman: "مستقيم", meaning: "Dosdoğru, istikamet sahibi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Müşahede", ottoman: "مشاهده", meaning: "Gözlemleme, hakikati yakînen görme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Müşkil", ottoman: "مشكل", meaning: "Çetin, anlaşılması ve çözülmesi zor mesele", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Müştak", ottoman: "مشتاق", meaning: "Çok arzulayan, hasret ve iştiyak duyan", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mütalaa", ottoman: "مطالعه", meaning: "Derinlemesine okuma, üzerinde düşünerek inceleme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mütefekkir", ottoman: "متفكر", meaning: "Tefekkür eden, kâinatı ibretle düşünen", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Mütevekkil", ottoman: "متوكل", meaning: "Allah'a güvenip tevekkül eden mümin", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Nazar", ottoman: "نظر", meaning: "Bakış, düşünce ve değerlendirme açısı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Nimet", ottoman: "نعمت", meaning: "Allah'ın kullarına ihsan ettiği maddi ve manevi her türlü iyilik", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Nizam", ottoman: "نظام", meaning: "Düzen, kâinattaki şaşmaz İlahî tertip", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Nükte", ottoman: "نكته", meaning: "İnce ve derin anlamlı hikmetli söz", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Râbıta-i Mevt", ottoman: "رابطهٔ موت", meaning: "Ölümü tefekkür ederek gafletten sıyrılma ve ahirete hazırlanma", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Rahmet", ottoman: "رحمت", meaning: "Bütün varlıkları kuşatan İlahî şefkat ve ikram", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Rıza-yı İlâhî", ottoman: "رضاى الهى", meaning: "Allah'ın hoşnutluğu; bütün ibadet ve hizmetlerin en yüksek gayesi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Riyakârlık", ottoman: "رياكارلق", meaning: "Gösteriş yapma, ihlasın zıddı olan manevi hastalık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Saadet-i Dareyn", ottoman: "سعادت دارين", meaning: "Dünya ve ahiret mutluluğu", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Saadet-i Ebediye", ottoman: "سعادت ابديه", meaning: "Cennetteki sonsuz mutluluk", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sadakat", ottoman: "صداقت", meaning: "Doğruluk, davasına ve dostuna içten bağlılık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sâfî", ottoman: "صافى", meaning: "Katıksız, berrak, arınmış", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sâni'", ottoman: "صانع", meaning: "Her şeyi eşsiz bir sanatla yaratan Allah", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sebat", ottoman: "ثبات", meaning: "Sözünde ve hizmetinde sarsılmadan durma, kararlılık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Seciye", ottoman: "سجيه", meaning: "Karakter, yerleşmiş güzel huy ve ahlâk", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sema", ottoman: "سما", meaning: "Gökyüzü, semavat âlemi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sena", ottoman: "ثنا", meaning: "Övgü, Cenâb-ı Hakk'ı güzel sıfatlarıyla medhetme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sıdk", ottoman: "صدق", meaning: "Doğruluk; peygamberlerin ve müminlerin en temel vasfı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sırr-ı İmtihan", ottoman: "سر امتحان", meaning: "Dünyada akla kapı açılıp iradenin elden alınmaması hikmeti", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sûret", ottoman: "صورت", meaning: "Biçim, şekil, dış görünüş", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Sünnet-i Seniyye", ottoman: "سنت سنيه", meaning: "Peygamber Efendimizin (a.s.m.) nurlu yolu, söz, fiil ve hâlleri", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Şeâir-i İslâmiye", ottoman: "شعائر اسلاميه", meaning: "İslâm'ın sembolü olan umumi ibadet ve işaretler (Ezan, namaz, cami)", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Şecere-i Hilkat", ottoman: "شجرهٔ خلقت", meaning: "Yaratılış ağacı; en mükemmel meyvesi insandır", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Şefaat", ottoman: "شفاعت", meaning: "Ahirette Peygamberimizin (a.s.m.) ve salih zatların müminler için bağışlanma dilemesi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Şua", ottoman: "شعاع", meaning: "Işık huzmesi, güneşten uzanan parlak nur", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Şuhud", ottoman: "شهود", meaning: "Gözle görür gibi kesin kanaat ve şahitlik", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Şuur", ottoman: "شعور", meaning: "Bilinç, idrak ve farkındalık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Şükür", ottoman: "شكر", meaning: "Nimeti vereni bilip dil, kalp ve azalarla O'na minnettar olmak", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Taalluk", ottoman: "تعلق", meaning: "İlgili olma, bağlanma, İlahî sıfatların varlıklara yönelmesi", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tabiat", ottoman: "طبيعت", meaning: "Yaratılış kanunları; kendisi yaratıcı değil, İlahî kudretin koyduğu bir sanattır", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tahattur", ottoman: "تخطر", meaning: "Hatırlama, zihinde canlandırma", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tahkik", ottoman: "تحقيق", meaning: "Bir meselenin doğruluğunu delilleriyle araştırıp sağlamlaştırma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Taklid", ottoman: "تقليد", meaning: "Delilini bilmeden başkasından gördüğü gibi uyma", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tazarru'", ottoman: "تضرع", meaning: "Tevazu ve acz içinde Cenâb-ı Hakk'a yalvarma", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tecelli", ottoman: "تجلى", meaning: "İlahî isim ve sıfatların eserler üzerinde görünmesi ve parlaması", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Teceddüd", ottoman: "تجدد", meaning: "Yenilenme, tazelenme", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tefekkür", ottoman: "تفكر", meaning: "Varlıkları İlahî sanat ve hikmet nazarıyla derinlemesine düşünme ibadeti", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tekemmül", ottoman: "تكمل", meaning: "Olgunlaşma, kemale ve mükemmelliğe doğru yükselme", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Temsil", ottoman: "تمثيل", meaning: "Derin akli hakikatleri dürbün gibi göze yaklaştıran benzetme ve hikâye", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tenezzüh", ottoman: "تنزه", meaning: "Kalp ve ruhun tefekkürle gezintiye çıkıp ferahlaması", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Terakki", ottoman: "ترقى", meaning: "İlerleme, maddi ve manevi yükseliş", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tedenni", ottoman: "تدنى", meaning: "Gerileme, alçalma ve manevi çöküş", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Terbiye-i İlahiye", ottoman: "تربيهٔ الهيه", meaning: "Cenâb-ı Hakk'ın her bir canlıyı şefkatle besleyip kemale erdirmesi", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tesadüf", ottoman: "تصادف", meaning: "Kör rastlantı; kâinatta tesadüfe asla yer yoktur, her şey kader ve hikmetledir", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tesاند", ottoman: "تساند", meaning: "Dayanışma, omuz omuza verip birbirine kuvvet olma", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Teslimiyet", ottoman: "تسليميت", meaning: "İlahî takdir ve emirlere gönül huzuruyla boyun eğme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tevazu", ottoman: "تواضع", meaning: "Alçakgönüllülük, büyüklüğü yalnız Allah'a verip nefsini küçük görme", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Tevhid", ottoman: "توحيد", meaning: "Allah'ı zatında, sıfatlarında ve icraatında bir ve eşsiz bilmek (Lâ ilâhe illallah)", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ulûhiyet", ottoman: "الوهيت", meaning: "İlahlık, yalnız Cenâb-ı Hakk'a mahsus olan mabudiyet makamı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ülfet", ottoman: "الفت", meaning: "Alışkanlık perdesi; harika mucizeleri sıradanmış gibi görme gafleti", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Ünsiyet", ottoman: "انسيت", meaning: "Dostluk, yakınlık, kalbin Allah'ı zikirle huzur bulması", rarity: "nadir", category: "500 Kelime", source: "500 Kelime" },
  { term: "Vecize", ottoman: "وجيزه", meaning: "Az kelimeyle çok büyük hakikatleri ifade eden hikmetli özdeyiş", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Vicdan", ottoman: "وجدان", meaning: "İnsanın fıtratına yerleştirilen, hakkı tanıyan ve Rabbine yönelen iç pusula", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Yakîn", ottoman: "يقين", meaning: "Şüphe barındırmayan kesin iman ve bilgi mertebesi (İlmelyakîn, Aynelyakîn, Hakkalyakîn)", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Zerre", ottoman: "ذره", meaning: "Atom ve atom altı en küçük madde parçacığı; kudret-i İlahiyenin memuru", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Zîhayat", ottoman: "ذى حيات", meaning: "Hayat sahibi olan canlı varlık", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Zîruh", ottoman: "ذى روح", meaning: "Ruh sahibi canlı", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" },
  { term: "Zîşuur", ottoman: "ذى شعور", meaning: "Şuur ve idrak sahibi varlık (insan, melek, cin)", rarity: "yaygin", category: "500 Kelime", source: "500 Kelime" }
];

// Metin içerisindeki herhangi bir kelimenin Lügat karşılığını bulan yardımcı fonksiyon
const NORMALIZED_LUGAT_INDEX = (() => {
  const map = new Map<string, LugatWord>();
  const normalize = (s: string) =>
    s
      .toLowerCase()
      .replace(/[.,;:!?"'“”‘’()[\]]/g, "")
      .replace(/â/g, "a")
      .replace(/î/g, "i")
      .replace(/û/g, "u")
      .trim();

  for (const list of Object.values(CHAPTER_LUGAT_DATA)) {
    for (const w of list) {
      map.set(w.term.toLowerCase(), w);
      map.set(normalize(w.term), w);
      const firstPart = normalize(w.term).split(/[\s-]/)[0];
      if (firstPart && firstPart.length >= 4 && !map.has(firstPart)) {
        map.set(firstPart, w);
      }
    }
  }
  for (const w of GLOBAL_RARE_LUGAT) {
    if (!map.has(w.term.toLowerCase())) {
      map.set(w.term.toLowerCase(), w);
    }
    const norm = normalize(w.term);
    if (!map.has(norm)) {
      map.set(norm, w);
    }
    const firstPart = norm.split(/[\s-]/)[0];
    if (firstPart && firstPart.length >= 4 && !map.has(firstPart)) {
      map.set(firstPart, w);
    }
  }
  return { map, normalize };
})();

export function lookupWordInLugat(rawToken: string): LugatWord | undefined {
  if (!rawToken || rawToken.length < 3) return undefined;
  const cleaned = rawToken.replace(/[.,;:!?"'“”‘’()[\]]/g, "").trim();
  if (!cleaned) return undefined;

  const exact = NORMALIZED_LUGAT_INDEX.map.get(cleaned.toLowerCase());
  if (exact) return exact;

  const norm = NORMALIZED_LUGAT_INDEX.normalize(cleaned);
  if (NORMALIZED_LUGAT_INDEX.map.has(norm)) {
    return NORMALIZED_LUGAT_INDEX.map.get(norm);
  }

  // Türkçe çekim eklerini (in, ın, un, ün, den, dan, de, da, le, la, i, ı, u, ü, ler, lar) soyarak kök arama
  for (let cut = 1; cut <= 4; cut++) {
    if (norm.length - cut >= 4) {
      const stem = norm.slice(0, norm.length - cut);
      const match = NORMALIZED_LUGAT_INDEX.map.get(stem);
      if (match) return match;
    }
  }

  return undefined;
}
