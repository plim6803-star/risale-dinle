// ============================================================================
// RESUL KNOWLEDGE HUB DATA:
// 1. Risale-i Nur Lügatçesi & Kök Aileleriyle 3000 Kelime (Vezinler + Kök Aileleri + Sözlük)
// 2. Marifetullah Kitabı — 1. Aşama: Bütün Vücudu Tanımak (Merdiven, Rozetler, I.1 Tam Metin)
// 3. Abdullah'ın Yolculuğu — 1. Bölüm: Allah'a İman (7 Sahnelik Oynanabilir 2D Hikâye & Deliller)
// 4. Bilim ve Tefekkür Belgesel Arşivi (1745 Kayıt • 45 Kategori • Önce İzle & YouTube Tam Bölümler)
// ============================================================================

export const RISALE_LUGAT_VEZINLER = [
  { pattern: 'fâil (فاعل)', role: 'İsm-i fâil — işi yapan', examples: 'kâtib, âlim, hâlık, şâhid, sâni, fâtır' },
  { pattern: 'mef\'ûl (مفعول)', role: 'İsm-i mef\'ûl — işten etkilenen', examples: 'mektûb, ma\'lûm, mahlûk, mevcûd, masnû, meşhûd' },
  { pattern: 'faîl (فعيل)', role: 'Sıfat-ı müşebbehe — kalıcı nitelik', examples: 'kerîm, rahîm, hakîm, alîm, kadîr, latîf, cemîl' },
  { pattern: 'fa\'\'âl (فعّال)', role: 'Mübalağalı ism-i fâil — çok/sürekli yapan', examples: 'hallâk, rezzâk, ğaffâr, settâr, kahhâr, feyyâz' },
  { pattern: 'ef\'al (أفعل)', role: 'İsm-i tafdîl — en / daha çok', examples: 'ekber, a\'zam, ahsen, efdal, akreb, eltaf' },
  { pattern: 'mef\'al / mef\'il (مفعل)', role: 'İsm-i mekân / zaman — yer veya vakit', examples: 'mescid, mekteb, medrese, mahşer, menba, mecma' },
  { pattern: 'mif\'âl / mif\'al (مفعال)', role: 'İsm-i âlet — araç, ölçü', examples: 'mîzân, miftâh, mir\'ât, mikyâs, mi\'yâr' },
  { pattern: 'fi\'âl / fu\'ûl / fa\'l', role: 'Sülâsî mastarlar — temel eylem adı', examples: 'ilm, şükr, hamd, hikmet, kudret, rahmet, ibâdet' },
  { pattern: 'if\'âl (إفعال)', role: 'IV. bâb mastar — geçişlilik / oldurma', examples: 'îmân, ihsân, îcâd, ikrâm, irşâd, ibdâ, ihâtâ' },
  { pattern: 'muf\'il / muf\'al', role: 'IV. bâb fâil / mef\'ûl', examples: 'mü\'min, muhsin, mûcid, mümkin, mün\'im, mürşid' },
  { pattern: 'tef\'îl (تفعيل)', role: 'II. bâb mastar — pekiştirme, ettirgen', examples: 'tevhîd, tesbîh, takdîs, ta\'zîm, tahkîk, tefekkür (kök)' },
  { pattern: 'müfa\'\'il / müfa\'\'al', role: 'II. bâb fâil / mef\'ûl', examples: 'müfessir, müceddid, mukaddes, münevver, musavvir' },
  { pattern: 'müfâale (مفاعلة)', role: 'III. bâb mastar — karşılıklı eylem', examples: 'müşâhede, muhâkeme, mukâbele, münâcât, münâzara' },
  { pattern: 'tefa\'\'ul (تفعّل)', role: 'V. bâb mastar — dönüşlülük, içselleştirme', examples: 'tefekkür, tevekkül, tezekkür, tedebbür, tecellî, tahakkuk' },
  { pattern: 'mütefa\'\'il', role: 'V. bâb fâil', examples: 'mütefekkir, mütevekkil, mütecellî, müteessir' },
  { pattern: 'tefâul (تفاعل)', role: 'VI. bâb mastar — işteşlik, görünüm', examples: 'tezâhür, tekâmül, tesânüd, tevâfuk, tedâhül' },
  { pattern: 'infi\'âl (انفعال)', role: 'VII. bâb mastar — edilgenlik / kendi kendine', examples: 'inkılâb, inkişâf, intizâm, in\'ikâs, inkıyâd' },
  { pattern: 'ifti\'âl (افتعال)', role: 'VIII. bâb mastar — özenle yapma', examples: 'ihtiyâr, iktidâr, i\'tikâd, intibâh, irtibât, istinâd' },
  { pattern: 'istif\'âl (استفعال)', role: 'X. bâb mastar — isteme, sayma, bulma', examples: 'istiğfâr, istimdâd, istifâde, istikbâl, isti\'dâd, istihdam' },
  { pattern: 'müstef\'il / müstef\'al', role: 'X. bâb fâil / mef\'ûl', examples: 'müstakîm, müstaid, müstefîd, müstakil, müstehak' },
  { pattern: 'fu\'ûliyet / fa\'âliyet (-iyet)', role: 'Yapma mastar — soyut kavram, mahiyet', examples: 'ubûdiyet, rubûbiyet, ulûhiyet, mâhiyet, hüviyet, ehadiyet' },
  { pattern: 'fu\'lân / fa\'lân', role: 'Coşkunluk / doluluk bildiren sıfat ve mastar', examples: 'رحمان (rahmân), ğufrân, şükrân, hüsrân, burhân' }
];

export const RISALE_PERSIAN_AFFIXES = [
  { affix: '-dâr (دار)', role: 'Tutan, sahip olan', examples: 'hissedâr, alakadâr, minnetdâr, cilvedâr' },
  { affix: '-kâr (كار)', role: 'Yapan, işleyen', examples: 'san\'atkâr, şükürkâr, günahkâr, hizmetkâr' },
  { affix: '-mend / -vend (مند)', role: 'Sahip, nitelikli', examples: 'hünermend, derdmend, danişmend' },
  { affix: '-gâh (گاه)', role: 'Yer, mekân, makam', examples: 'dergâh, nazargâh, seyrangâh, karargâh' },
  { affix: '-âne (انه)', role: '-ca / -ce zarf ve sıfat eki', examples: 'ârifâne, âcizâne, şefkatkârâne, hâkimâne, müdebbirâne' },
  { affix: '-nümâ (نما)', role: 'Gösteren', examples: 'kıblenümâ, cihânnümâ, rehnümâ' },
  { affix: '-perver / -perest', role: 'Besleyen, seven / tapan', examples: 'vatanperver, hakperest, nefisperest' },
  { affix: '-engîz / -âmîz', role: 'Koparan, uyandıran / karışık', examples: 'hayretengîz, ibretâmîz, hikmetâmîz' },
  { affix: 'bî- (بی) / nâ- (نا)', role: '-sız, olumsuzluk ön eki', examples: 'bîçâre, bînihâye, bîhad, nâmahdûd, nâkıs, nâmütenâhî' },
  { affix: 'hem- (هم)', role: 'Aynı, ortak, birlikte', examples: 'hemrah, hemfikir, hemnev\', hemzaman' }
];

export const RISALE_ROOT_FAMILIES = [
  {
    root: 'ع ب د',
    code: 'A-B-D',
    coreMeaning: 'Kulluk, boyun eğme, ibadet ve ubudiyet',
    words: [
      { word: 'abd', ottoman: 'عبد', origin: 'A', pattern: 'fa\'l', meaning: 'kul, köle, Allah\'ın mahlûku ve muhatabı' },
      { word: 'ibâdet', ottoman: 'عبادت', origin: 'A', pattern: 'fi\'âlet', meaning: 'kulluk etme, Allah\'ın emirlerini yerine getirme' },
      { word: 'ubûdiyet', ottoman: 'عبوديت', origin: 'A', pattern: 'fu\'ûliyet', meaning: 'kulluk şuuru; acz, fakr ve şükürle Yaratıcıya yöneliş' },
      { word: 'âbid', ottoman: 'عابد', origin: 'A', pattern: 'fâil', meaning: 'ibadet eden, kulluk vazifesini yerine getiren' },
      { word: 'ma\'bûd', ottoman: 'معبود', origin: 'A', pattern: 'mef\'ûl', meaning: 'kendisine ibadet edilen; hakiki Ma\'bûd yalnız Allah\'tır' },
      { word: 'ma\'bed', ottoman: 'معبد', origin: 'A', pattern: 'mef\'al', meaning: 'ibadet edilen yer, mescid' },
      { word: 'taabbüd', ottoman: 'تعبّد', origin: 'A', pattern: 'tefa\'\'ul', meaning: 'kendini ibadete verme, hikmeti tam bilinmese de emre uyma' }
    ]
  },
  {
    root: 'ع ل م',
    code: 'A-L-M',
    coreMeaning: 'Bilme, ilim, iz ve nişan olma (âlem = Yaratıcıyı bildiren nişan)',
    words: [
      { word: 'ilm / ilim', ottoman: 'علم', origin: 'A', pattern: 'fi\'l', meaning: 'hakikati olduğu gibi bilme, bilgi' },
      { word: 'Alîm', ottoman: 'عليم', origin: 'A', pattern: 'faîl', meaning: 'her şeyi en ince sırrına kadar hakkıyla bilen (Allah)' },
      { word: 'âlim', ottoman: 'عالم', origin: 'A', pattern: 'fâil', meaning: 'bilen, ilim sahibi' },
      { word: 'âlem', ottoman: 'عالم', origin: 'A', pattern: 'fâ\'al', meaning: 'kâinat, yaratılmışlar bütünü (Sâni\'ine alâmet ve nişan olan)' },
      { word: 'ma\'lûm', ottoman: 'معلوم', origin: 'A', pattern: 'mef\'ûl', meaning: 'bilinen şey, ilme konu olan' },
      { word: 'alâmet', ottoman: 'علامت', origin: 'A', pattern: 'fa\'âlet', meaning: 'işaret, iz, belirti, delil' },
      { word: 'ta\'lîm', ottoman: 'تعليم', origin: 'A', pattern: 'tef\'îl', meaning: 'öğretme, esmâyı ve hakikatleri bildirme' },
      { word: 'taallüm', ottoman: 'تعلّم', origin: 'A', pattern: 'tefa\'\'ul', meaning: 'ilim öğrenme, tefekkürle ders alma' }
    ]
  },
  {
    root: 'ح ك م',
    code: 'H-K-M',
    coreMeaning: 'Hikmet, sağlamlık, abes olmamak, gaye ve hüküm',
    words: [
      { word: 'hikmet', ottoman: 'حكمت', origin: 'A', pattern: 'fi\'let', meaning: 'her şeyin bir gaye, fayda ve maslahatla yaratılması; abesiyetsizlik' },
      { word: 'Hakîm', ottoman: 'حكيم', origin: 'A', pattern: 'faîl', meaning: 'her fiili ve sanatı tam hikmetli ve gayeli olan (Allah)' },
      { word: 'Hâkim', ottoman: 'حاكم', origin: 'A', pattern: 'fâil', meaning: 'hükmeden, kâinatta emri geçerli olan' },
      { word: 'hüküm', ottoman: 'حكم', origin: 'A', pattern: 'fu\'l', meaning: 'karar, kanun, ilâhî ferman' },
      { word: 'muhkem', ottoman: 'محكم', origin: 'A', pattern: 'muf\'al', meaning: 'sağlam kılınmış, manası açık ve sarsılmaz' },
      { word: 'muhâkeme', ottoman: 'محاكمه', origin: 'A', pattern: 'müfâale', meaning: 'akıl ve vicdanla delilleri tartıp hükme varma' },
      { word: 'tahakküm', ottoman: 'تحكّم', origin: 'A', pattern: 'tefa\'\'ul', meaning: 'zorla hükmetmeye kalkışma, baskı' }
    ]
  },
  {
    root: 'ق د ر',
    code: 'K-D-R',
    coreMeaning: 'Ölçü, miktar, güç yetirme ve kader',
    words: [
      { word: 'kudret', ottoman: 'قدرت', origin: 'A', pattern: 'fu\'let', meaning: 'sonsuz güç ve yaratma iktidarı' },
      { word: 'Kadîr', ottoman: 'قدير', origin: 'A', pattern: 'faîl', meaning: 'her şeye gücü tam yeten, acziyetten münezzeh (Allah)' },
      { word: 'kader', ottoman: 'قدر', origin: 'A', pattern: 'fa\'al', meaning: 'ilâhî ilmin her şeyin planını, ölçüsünü ve vaktini takdir etmesi' },
      { word: 'mikdâr', ottoman: 'مقدار', origin: 'A', pattern: 'mif\'âl', meaning: 'ölçü, belirlenmiş sınır ve hacim' },
      { word: 'takdîr', ottoman: 'تقدير', origin: 'A', pattern: 'tef\'îl', meaning: 'ölçülü ve hikmetli planlama; kıymetini bilme' },
      { word: 'mukadder', ottoman: 'مقدّر', origin: 'A', pattern: 'müfa\'\'al', meaning: 'kaderde yazılmış, takdir edilmiş olan' },
      { word: 'iktidâr', ottoman: 'اقتدار', origin: 'A', pattern: 'ifti\'âl', meaning: 'güç yetirme, kabiliyet' }
    ]
  },
  {
    root: 'ر ح م',
    code: 'R-H-M',
    coreMeaning: 'Şefkat, merhamet, ihsan ve rahmet',
    words: [
      { word: 'rahmet', ottoman: 'رحمت', origin: 'A', pattern: 'fa\'let', meaning: 'kâinatı kuşatan ilâhî şefkat, ikram ve inâyet' },
      { word: 'Rahmân', ottoman: 'رحمان', origin: 'A', pattern: 'fa\'lân', meaning: 'dünyada bütün mahlûkata ayrım gözetmeden rızık ve hayat veren' },
      { word: 'Rahîm', ottoman: 'رحيم', origin: 'A', pattern: 'faîl', meaning: 'hususan şuurlu ve mü\'min kullarına ebedî şefkat gösteren' },
      { word: 'merhamet', ottoman: 'مرحمت', origin: 'A', pattern: 'mef\'alet', meaning: 'şefkatle acıma ve iyilik etme' },
      { word: 'Erhamürrâhimîn', ottoman: 'ارحم الراحمين', origin: 'A', pattern: 'ef\'al', meaning: 'merhametlilerin en merhametlisi olan Allah' }
    ]
  },
  {
    root: 'و ح د / أ ح د',
    code: 'V-H-D',
    coreMeaning: 'Birlik, teklik, Vâhidiyet ve Ehadiyet',
    words: [
      { word: 'tevhîd', ottoman: 'توحيد', origin: 'A', pattern: 'tef\'îl', meaning: 'Allah\'ın birliğini tasdik ve kâinattaki bütün eserleri O\'na nispet etme' },
      { word: 'Vâhid', ottoman: 'واحد', origin: 'A', pattern: 'fâil', meaning: 'Zâtında, sıfatlarında ve rubûbiyetinde tek olan' },
      { word: 'Ehad', ottoman: 'احد', origin: 'A', pattern: 'fa\'al', meaning: 'ortağı, benzeri ve cüz\'ü olmayan mutlak tek' },
      { word: 'vâhidiyet', ottoman: 'واحديت', origin: 'A', pattern: 'fâiliyet', meaning: 'ilâhî isimlerin bütün kâinatı küllî bir kanunla kuşatması (genel tecellî)' },
      { word: 'ehadiyet', ottoman: 'احديت', origin: 'A', pattern: 'fa\'aliyet', meaning: 'her bir cüz\'î eserde (bir çiçekte, bir insanda) bütün esmânın mührünün görünmesi' },
      { word: 'vahdet', ottoman: 'وحدت', origin: 'A', pattern: 'fa\'let', meaning: 'birlik; kesretin (çokluğun) arkasındaki tek Yaratıcı birliği' }
    ]
  },
  {
    root: 'ش ه د',
    code: 'Ş-H-D',
    coreMeaning: 'Hazır bulunma, gözle görme ve şahitlik etme',
    words: [
      { word: 'şâhid', ottoman: 'شاهد', origin: 'A', pattern: 'fâil', meaning: 'gören ve delil olarak tanıklık eden' },
      { word: 'şehâdet', ottoman: 'شهادت', origin: 'A', pattern: 'fa\'âlet', meaning: 'kesin şahitlik; âlem-i şehâdet (görünen maddi âlem)' },
      { word: 'müşâhede', ottoman: 'مشاهده', origin: 'A', pattern: 'müfâale', meaning: 'hakikati bizzat gözlemleme, eserden Esmâ\'yı okuma' },
      { word: 'meşhûd', ottoman: 'مشهود', origin: 'A', pattern: 'mef\'ûl', meaning: 'gözle görülen, açıkça müşahede edilen' },
      { word: 'meşhed', ottoman: 'مشهد', origin: 'A', pattern: 'mef\'al', meaning: 'tecellîlerin seyredildiği sahne' }
    ]
  },
  {
    root: 'ف ك ر',
    code: 'F-K-R',
    coreMeaning: 'Düşünme, tefekkür ve ibretle zihin yürütme',
    words: [
      { word: 'fikir', ottoman: 'فكر', origin: 'A', pattern: 'fi\'l', meaning: 'düşünce, zihinsel kavrayış' },
      { word: 'tefekkür', ottoman: 'تفكّر', origin: 'A', pattern: 'tefa\'\'ul', meaning: 'kâinattaki sanattan Sâni\'e, eserden Esmâ\'ya intikal eden ibadet hükmünde düşünce' },
      { word: 'mütefekkir', ottoman: 'متفكّر', origin: 'A', pattern: 'mütefa\'\'il', meaning: 'derin tefekkür eden' }
    ]
  },
  {
    root: 'ع ج ز',
    code: 'A-C-Z',
    coreMeaning: 'Güç yetirememe (kul için şefaatçi, Kur\'an için mu\'cize)',
    words: [
      { word: 'acz', ottoman: 'عجز', origin: 'A', pattern: 'fa\'l', meaning: 'kendi kendine güç yetirememe; Kadîr-i Zülcelâl\'e dayanmanın anahtarı' },
      { word: 'âciz', ottoman: 'عاجز', origin: 'A', pattern: 'fâil', meaning: 'gücü yetmeyen, muhtaç' },
      { word: 'mu\'cize', ottoman: 'معجزه', origin: 'A', pattern: 'muf\'ile', meaning: 'insanları benzerini getirmekten âciz bırakan peygamberlik harikası' },
      { word: 'i\'câz', ottoman: 'اعجاز', origin: 'A', pattern: 'if\'âl', meaning: 'Kur\'ân\'ın taklit edilemez belâgat ve hakikat üstünlüğü' }
    ]
  },
  {
    root: 'ن و ر',
    code: 'N-V-R',
    coreMeaning: 'Işık, aydınlık, hidayet ve marifet nuru',
    words: [
      { word: 'nûr', ottoman: 'نور', origin: 'A', pattern: 'fu\'l', meaning: 'hem kendi görünen hem başkasını gösteren ilâhî ışık, iman aydınlığı' },
      { word: 'münevver', ottoman: 'منوّر', origin: 'A', pattern: 'müfa\'\'al', meaning: 'nurlanmış, iman ve ilimle aydınlanmış' },
      { word: 'tenvîr', ottoman: 'تنوير', origin: 'A', pattern: 'tef\'îl', meaning: 'aydınlatma, kalbi ve aklı hakikatle nurlandırma' },
      { word: 'nurânî', ottoman: 'نورانى', origin: 'A', pattern: 'nisbet', meaning: 'nurla ilgili, şeffaf ve latif' }
    ]
  }
];

export const RISALE_CORE_DICTIONARY = [
  { id: 'w-1', word: 'âb-ı hayat', ottoman: 'آب حيات', origin: 'F+A', root: 'ح ي ي', meaning: 'Dirilik suyu, ebedî hayat pınarı; iman ve Kur\'an hakikatleri.' },
  { id: 'w-2', word: 'abesiyet', ottoman: 'عبثيت', origin: 'A', root: 'ع ب ث', meaning: 'Boş, gayesiz ve hikmetsiz olma hali (kâinatta hiçbir şeyde abesiyet yoktur).' },
  { id: 'w-3', word: 'acz-i mutlak', ottoman: 'عجز مطلق', origin: 'A', root: 'ع ج ز', meaning: 'Tam güçsüzlük; kulun kendi kudretiyle hiçbir şeye malik olmadığını bilip Kadîr\'e sığınması.' },
  { id: 'w-4', word: 'âfâkî tefekkür', ottoman: 'آفاقى تفكّر', origin: 'A', root: 'أ ف ق', meaning: 'Dış âlemdeki (gökler, arz, bitkiler, canlılar) ilâhî sanat ve nizamı tefekkür etme.' },
  { id: 'w-5', word: 'enfüsî tefekkür', ottoman: 'انفسى تفكّر', origin: 'A', root: 'ن ف س', meaning: 'İnsanın kendi iç dünyasındaki, vicdan, ruh ve bedenindeki ilâhî isimleri tefekkür etmesi.' },
  { id: 'w-6', word: 'âyât-ı tekvîniye', ottoman: 'آيات تكوينيه', origin: 'A', root: 'ك و ن', meaning: 'Kâinat kitabında yaratılışla yazılmış ilâhî âyetler ve deliller.' },
  { id: 'w-7', word: 'burhân-ı قâti\'', ottoman: 'برهان قاطع', origin: 'A', root: 'ب ر ه', meaning: 'Şüpheyi kökten kesen kesin ve sarsılmaz delil.' },
  { id: 'w-8', word: 'cemâl ve celâl', ottoman: 'جمال و جلال', origin: 'A', root: 'ج م ل / ج ل ل', meaning: 'Allah\'ın lütuf, güzellik ve şefkat tecellisi (cemâl) ile azamet ve heybet tecellisi (celâl).' },
  { id: 'w-9', word: 'cilve-i esmâ', ottoman: 'جلوه اسما', origin: 'A', root: 'ج ل و', meaning: 'İlâhî isimlerin mahlûkat aynalarında yansıyan tecellileri.' },
  { id: 'w-10', word: 'delil-i ihtirâ', ottoman: 'دليل اختراع', origin: 'A', root: 'خ ر ع', meaning: 'Her varlık türüne özgü yoktan ve benzersiz yaratılış (ihtirâ) üzerinden Sâni\'i ispat eden delil.' },
  { id: 'w-11', word: 'delil-i inâyet', ottoman: 'دليل عنايت', origin: 'A', root: 'ع ن ي', meaning: 'Varlıklardaki hikmet, fayda, maslahat ve şefkatli rızıklandırma üzerinden Hakîm ve Rahîm Yaratıcıyı gösteren delil.' },
  { id: 'w-12', word: 'ene (vâhid-i kıyâsî)', ottoman: 'انا (واحد قياسى)', origin: 'A', root: 'ق ي س', meaning: 'İnsana kendi cüz\'î ilim, kudret ve iradesiyle Allah\'ın mutlak sıfatlarını kıyaslayıp tanıması için verilen ölçü birimi.' },
  { id: 'w-13', word: 'esmâ-i hüsnâ', ottoman: 'اسماء حسنى', origin: 'A', root: 'س م و / ح س ن', meaning: 'Allah\'ın en güzel isimleri; kâinattaki bütün hakikatlerin ve sanatların kaynağı.' },
  { id: 'w-14', word: 'fakr-ı mutlak', ottoman: 'فقر مطلق', origin: 'A', root: 'ف ق ر', meaning: 'Her şeyde Ganiyy-i Mutlak ve Rahmân olan Allah\'a muhtaç olduğunu bilme şuuru.' },
  { id: 'w-15', word: 'hâtemü\'l-enbiyâ', ottoman: 'خاتم الانبيا', origin: 'A', root: 'خ ت م', meaning: 'Peygamberler silsilesinin sonuncusu ve mührü olan Hz. Muhammed (s.a.v.).' },
  { id: 'w-16', word: 'hudûs delili', ottoman: 'حدوث دليلى', origin: 'A', root: 'ح د ث', meaning: 'Sonradan var olan (hâdis) her şeyin ezelî bir Muhdis\'e (Yaratıcıya) muhtaç olduğunu gösteren kelâm delili.' },
  { id: 'w-17', word: 'imkân delili', ottoman: 'امكان دليلى', origin: 'A', root: 'م ك ن', meaning: 'Varlığı ile yokluğu eşit (mümkin) olan kâinatın, varlığını Vâcibü\'l-Vücûd olan Allah\'ın tercihine borçlu olduğunu gösteren delil.' },
  { id: 'w-18', word: 'mana-yı harfî', ottoman: 'معناى حرفى', origin: 'A', root: 'ح ر ف', meaning: 'Varlıklara kendileri hesabına değil, Sâni ve Yaratıcılarını bildiren birer harf ve ayna olarak bakma.' },
  { id: 'w-19', word: 'mana-yı ismî', ottoman: 'معناى اسمى', origin: 'A', root: 'س م و', meaning: 'Varlığa Yaratıcısını unutarak yalnızca kendi zatı ve maddesi hesabına bakma.' },
  { id: 'w-20', word: 'marifetullah', ottoman: 'معرفت الله', origin: 'A', root: 'ع ر ف', meaning: 'Allah\'ı eserleri, fiilleri, isimleri, sıfatları ve şuûnâtıyla delile dayalı ve kalbî olarak tanıma ilmi.' },
  { id: 'w-21', word: 'mektûbât-ı Samedâniye', ottoman: 'مكتوبات صمدانيه', origin: 'A', root: 'ك ت ب / ص م د', meaning: 'Hiçbir şeye muhtaç olmayan ve her şeyin O\'na muhtaç olduğu Samed\'in kâinat sayfasına yazdığı canlı mektuplar.' },
  { id: 'w-22', word: 'muarrif', ottoman: 'معرّف', origin: 'A', root: 'ع ر ف', meaning: 'Tanıtan, tarif eden (Rabbimizi bize tanıtan üç büyük küllî muarrif: Kâinat Kitabı, Hz. Peygamber, Kur\'an-ı Azîmüşşan).' },
  { id: 'w-23', word: 'nizam ve mîzan', ottoman: 'نظام و ميزان', origin: 'A', root: 'ن ظ م / و ز ن', meaning: 'Kâinatın her zerresinde işleyen şaşmaz düzen (nizam) ve hassas ölçü/denge (mîzan).' },
  { id: 'w-24', word: 'rubûbiyet', ottoman: 'ربوبيت', origin: 'A', root: 'ر ب ب', meaning: 'Allah\'ın her bir varlığı terbiye edip rızıklandırması, yönetmesi ve kemâline ulaştırması.' },
  { id: 'w-25', word: 'şرك-i hafî / esbâbperestlik', ottoman: 'اسباب پرستلك', origin: 'A+F', root: 'س ب ب', meaning: 'Aciz ve şuursuz sebeplere yaratıcılık veya müstakil tesir verme yanılgısı.' },
  { id: 'w-26', word: 'şuûnât-ı İlâhiye', ottoman: 'شئونات الهيه', origin: 'A', root: 'ش أ ن', meaning: 'Esmâ ve sıfatların kaynağı olan, mahlûkatın hislerine benzemeyen mukaddes ilâhî şe\'nler (mukaddes muhabbet, rahmet, memnuniyet).' },
  { id: 'w-27', word: 'tevâfuk', ottoman: 'توافق', origin: 'A', root: 'و ف ق', meaning: 'Tesadüfe verilemeyecek şekilde birbirine uygunluk, kasıt ve iradeyi gösteren ahenk.' },
  { id: 'w-28', word: 'vâcibü\'l-vücûd', ottoman: 'واجب الوجود', origin: 'A', root: 'و ج ب / و ج د', meaning: 'Varlığı zatından olup var olmaması asla düşünülemeyen ezelî ve ebedî Yaratıcı (Allah).' }
];

// ============================================================================
// 2. MARİFETULLAH KİTABI — 1. AŞAMA: BÜTÜN VÜCUDU TANIMAK (REVİZE)
// ============================================================================

export const MARIFETULLAH_BADGE_LEGEND = [
  { badge: '📗 Ayet', color: 'bg-emerald-950/80 border-emerald-500/50 text-emerald-200', desc: 'Kur\'ân-ı Kerîm ayeti, meali ve tefekkür bağlamı' },
  { badge: '📘 Sahih Hadis', color: 'bg-sky-950/80 border-sky-500/50 text-sky-200', desc: 'Sahih/hasen hadis metni, kaynağı ve sıhhat derecesi' },
  { badge: '⚖️ Akaid', color: 'bg-indigo-950/80 border-indigo-400/50 text-indigo-200', desc: 'Ehl-i Sünnet ortak akaid zemini (Mâtürîdî & Eş\'arî)' },
  { badge: '⚠️↔️ Görüş Farkı', color: 'bg-amber-950/80 border-amber-400/50 text-amber-200', desc: 'Mâtürîdî – Eş\'arî yan yana iki sütun karşılaştırma' },
  { badge: '🔆 Risale-i Nur Derinliği', color: 'bg-orange-950/80 border-orange-500/60 text-orange-200', desc: 'Risale-i Nur\'dan ana açıklayıcı omurga ve YA sayfa referansı' },
  { badge: '🔆🪔 Risale Temsili', color: 'bg-amber-900/70 border-amber-300/60 text-amber-100', desc: 'Risale-i Nur\'daki dürbün/temsil hikâyesi ve hakikat karşılığı' },
  { badge: '🌙 Tasavvuf', color: 'bg-purple-950/80 border-purple-400/50 text-purple-200', desc: 'Kalp ve zevk boyutu (akaid hükmü yerine geçmez, derinlik katar)' },
  { badge: '🌱 Tefekkür Notu', color: 'bg-teal-950/80 border-teal-400/50 text-teal-200', desc: 'Okuyucunun kendi hayatına ve secdesine taşıyacağı iç muhasebe' },
  { badge: '⛔ Tartışmalı Rivayet', color: 'bg-rose-950/80 border-rose-500/50 text-rose-200', desc: 'Halk arasında yaygın ama senedi zayıf/mevzû söz uyarısı' }
];

export const MARIFET_MERDIVENI = [
  {
    step: 1,
    name: 'Eser (Sanat & Masnû)',
    subtitle: 'Gözün gördüğü çiçek, yıldız, hücre, insan',
    question: 'Bu muazzam nizam, ölçü ve güzellik kendi kendine olabilir mi?',
    risaleKey: 'Mana-yı harfî ile bakış: Eser, ustasını gösteren bir mektuptur.',
    example: 'Bir elmanın rengi, kokusu, vitamini ve çekirdeğindeki genetik şifre.'
  },
  {
    step: 2,
    name: 'Fiil (İcraat & Tasarruf)',
    subtitle: 'Eserin arkasında anbean işleyen yaratma, rızıklandırma, diriltme fiilleri',
    question: 'Bu eser hangi fiillerle (tasvir, terbiye, rızık verme, ihya) meydana geliyor?',
    risaleKey: 'Fiilsiz eser olmaz; her intizamlı fiil şuurlu bir fâili gösterir.',
    example: 'Baharda milyonlarca tohumun aynı anda hatasız açılıp rızıklandırılması.'
  },
  {
    step: 3,
    name: 'İsim (Esmâ-i Hüsnâ)',
    subtitle: 'Fiilin kaynağındaki ilâhî unvan (Rezzâk, Musavvir, Hakîm, Şâfî)',
    question: 'Rızık verme fiili hangi İsm-i Şerif\'ten, şekil verme hangi İsim\'den geliyor?',
    risaleKey: 'Fiil, فâilin ismine dayanır. Rızıklandırma → Rezzâk; suret verme → Musavvir.',
    example: 'Elmadaki şifa → Şâfî; rızık → Rezzâk; eşsiz nakış → Sâni ve Musavvir.'
  },
  {
    step: 4,
    name: 'Sıfat (Yedi Sıfat-ı Sübûtiye)',
    subtitle: 'Esmânın dayandığı Hayat, İlim, İrade, Kudret, Sem\', Basar, Kelâm, Tekvîn',
    question: 'Musavvir ve Hakîm isimleri hangi sonsuz İlim, İrade ve Kudret sıfatlarıyla tecellî ediyor?',
    risaleKey: 'İsimler, yedi küllî ilâhî sıfata (Hayat, İlim, Kudret, İrade...) istinad eder.',
    example: 'Bir çiçeği yapmak için bütün kâinatı bilen bir İlim ve her şeye yeten bir Kudret gerekir.'
  },
  {
    step: 5,
    name: 'Şe\'n (Şuûnât-ı İlâhiye)',
    subtitle: 'Sıfatların arkasındaki mukaddes ilâhî rahmet, muhabbet ve cemâl',
    question: 'Sonsuz Kudret ve İlim neden bu kadar şefkatle ikram ediyor?',
    risaleKey: 'Otuz İkinci Söz: Mahlûkatın hislerine benzemeyen mukaddes şefkat, mukaddes muhabbet ve memnuniyet-i mukaddese.',
    example: 'Bir annenin yavrusuna şefkati, Rahmân\'ın mukaddes rahmet şe\'ninin tek bir parıltısıdır.'
  },
  {
    step: 6,
    name: 'Zât-ı Akdes (Allah\'ın Mukaddes Zâtı)',
    subtitle: 'Bütün şe\'n, sıfat, isim ve fiillerin sahibi Vâcibü\'l-Vücûd; künhü idrak edilemez, eserleriyle tanınır',
    question: 'Bu merdivenin zirvesinde kulun varacağı makam nedir?',
    risaleKey: '"Mâ عرفناك حقّ معرفتك" (Seni hakkıyla tanıyamadık) hayreti, tazimi ve secdesi.',
    example: 'Aklın delille yakîn bulup kalbin muhabbet, haşyet ve ubûdiyetle secdeye kapanması.'
  }
];

export const MARIFETULLAH_BOOK_PARTS = [
  { id: 'KISIM-I', title: 'Kısım I: Giriş ve Yöntem', units: 'I.1 – I.5', status: 'I.1 Tam Metin Hazır', summary: 'Marifetullah nedir, bilgi kaynakları (selim duyular, sadık haber, selim akıl), tenzih-teşbih dengesi, Risale-i Nur\'un Mana-yı Harfî, Ene ve Vâhidiyet-Ehadiyet metodu.' },
  { id: 'KISIM-II', title: 'Kısım II: Vücûd ve Tevhid Delilleri', units: 'II.1 – II.6', status: 'Mimari Hazır', summary: 'Hudûs, İmkân, Nizam-Gaye, İhtirâ ve İnâyet, Fıtrat-Vicdan delilleri ve Sebeplerin perde oluşu (Tabiat Risalesi).' },
  { id: 'KISIM-III', title: 'Kısım III: Zât-ı İlâhî ve Sıfatlar', units: 'III.1 – III.6', status: 'Mimari Hazır', summary: 'Sıfât-ı Nefsiyye (Vücûd), Sıfât-ı Selbiyye (Kıdem, Bekâ, Vahdâniyet, Muhâlefetün li\'l-havâdis, Kıyâm bi-nefsihî), Sıfât-ı Sübûtiyye ve Haberi Sıfatlar.' },
  { id: 'KISIM-IV', title: 'Kısım IV: Esmâ-i Hüsnâ (105 İsim Kartı)', units: 'IV.1 – IV.12', status: 'Mimari Hazır', summary: 'Lafza-i Celâl, Rahmân-Rahîm, 99 İsim + Kur\'ân ve Risale\'de öne çıkan Sâni, Fâtır, Mün\'im, Müdebbir, Şâfî, Kâfî isimlerinin 11-alanlı kartları.' },
  { id: 'KISIM-V', title: 'Kısım V: Şuûnât-ı İlâhiyye', units: 'V.1 – V.3', status: 'Mimari Hazır', summary: 'Otuz İkinci Söz ve Yirmi Dördüncü Mektup ışığında mukaddes muhabbet, mukaddes şefkat ve esmâdan şuûnâta yükseliş.' },
  { id: 'KISIM-VI', title: 'Kısım VI: Fiiller, Tecellî ve Kâinat', units: 'VI.1 – VI.6', status: 'Mimari Hazır', summary: 'Rubûbiyet, İbda\'-İnşâ, Kader ve Cüz\'î İrade (Yirmi Altıncı Söz), Hikmet ve Şer Meselesi, Haşir ve Esmâ İlişkisi (Onuncu Söz).' },
  { id: 'KISIM-VII', title: 'Kısım VII: Marifetin Meyvesi — Ubûdiyet', units: 'VII.1 – VII.6', status: 'Mimari Hazır', summary: 'İman-Marifet-Muhabbet-Müşahede, Acz-Fakr-Şefkat-Tefekkür yolu, Dua ve Esmâ ile Tevessül, Kalp mertebeleri.' }
];

export const MARIFETULLAH_UNIT_I_1 = {
  code: 'I.1',
  title: 'Marifetullah Nedir? — İman, İlim, Marifet, Muhabbet',
  keySentence: 'Marifetullah, Allah\'ı Zât\'ının künhüyle kuşatmak değil; O\'nu eserleri, fiilleri, isimleri, sıfatları ve şuûnâtıyla delile dayalı olarak tanıyıp bu tanıyışı kalp, muhabbet ve ubûdiyete dönüştürmektir.',
  sections: [
    {
      step: 1,
      badge: '🌱 Tefekkür Notu',
      title: '1. Açılış — Neden Bu Soruyla Başlıyoruz?',
      content: 'İnsan, içinde yaşadığı sarayın taşlarını sayıp mimarını hiç sormayan bir misafir gibi yaşayamaz. Gözünü açtığı andan itibaren nefes aldığı hava, damarlarında dolaşan kan, gökyüzünde şaşmaz bir ölçüyle dönen güneş ve kalbindeki sonsuzluk arzusu ona tek bir soruyu fısıldar: Bütün bunları kim yapıyor, beni kim var etti ve benden ne istiyor? İşte bu sorunun cevabı, insanın yaratılış gayesi olan marifetullahtır.'
    },
    {
      step: 2,
      badge: '⚖️ Akaid',
      title: '2. Tanım — İlim, İman, Marifet ve Muhabbet Farkı',
      content: '• İlim (العلم): Bir şeyi deliliyle ve gerçeğe uygun biçimde bilmektir.\n• İman (الإيمان): Hz. Peygamber\'in (s.a.v.) Allah katından getirdiği kesin olarak bilinen hakikatleri kalp ile tasdik, dil ile ikrar etmektir. İmanın zıddı küfürdür.\n• Marifet (المعرفة): İman edilen Allah\'ı, kâinattaki eserlerinden hareketle fiilleri, isimleri ve sıfatlarıyla ayrıntılı, derin ve kalpte iz bırakan bir şuurla tanımaktır. İman temeldir; marifet o temelin üzerinde yükselen nurânî saraydır.\n• Muhabbet (المحبة): Tanınan Cemîl ve Kemâl sahibini sevmektir. İnsanın fıtratında جمال (güzellik), kemâl (kusursuzluk) ve ihsan (iyilik) sevilir; marifet arttıkça bütün güzellik ve ihsanların gerçek sahibi olan Allah\'a muhabbet ve hürmet artar.'
    },
    {
      step: 3,
      badge: '📗 Ayet',
      title: '3. Kur\'ânî Temel — Yaratılışın Gayesi ve Âlimlerin Haşyeti',
      content: '• Zâriyât 51/56: «وَمَا خَلَقْتُ الْجِنَّ وَالْإِنسَ إِلَّا لِيَعْبُدُونِ» — "Ben cinleri ve insanları ancak bana kulluk (ibadet) etsinler diye yarattım." İbn Abbâs (r.a.) başta olmak üzere birçok müfessir buradaki "li-ya\'budûn" (bana kulluk etsinler) ifadesinin hakikatini "li-ya\'rifûn" (beni tanısınlar) olarak açıklamıştır; çünkü tanımadan gerçek kulluk yapılamaz.\n• Muhammed 47/19: «فَاعْلَمْ أَنَّهُ لَا إِلَٰهَ إِلَّا اللَّهُ» — "Bil ki, Allah\'tan başka ilâh yoktur." Ayet imanın ilim ve marifet üzerine kurulmasını emreder.\n• Fâtır 35/28: «إِنَّمَا يَخْشَى اللَّهَ مِنْ عِبَادِهِ الْعُلَمَاءُ» — "Kulları içinden ancak âlim olanlar Allah\'tan hakkıyla huşû ve haşyet duyar."'
    },
    {
      step: 4,
      badge: '📘 Sahih Hadis',
      title: '4. Nebevî Ölçü — İhsan Makamı ve Zât\'ı Değil Nimetleri Tefekkür',
      content: '• Cibrîl Hadisi (Buhârî, Îmân 37; Müslim, Îmân 1 — Sahih): İhsan, "Allah\'ı görüyormuşçasına O\'na kulluk etmendir; sen O\'nu görmesen de O seni görmektedir."\n• Allah\'ı en iyi tanıyanın Hz. Peygamber (s.a.v.) olması (Buhârî, Îmân 13 — Sahih): "Allah\'ı en çok tanıyanınız ve O\'ndan en çok korkanınız benim."\n• Tefekkürün sınırı (Beyhakî, Şuabü\'l-Îmân; Elbânî, Silsiletü\'s-Sahîha, no. 1788 — Hasen): "Allah\'ın nimetleri ve yarattıkları hakkında tefekkür edin; Allah\'ın Zât\'ı hakkında (künhünü kuşatmaya kalkışarak) tefekkür etmeyin."\n• ⛔ Tartışmalı Rivayet Uyarısı: Halk arasında çok tekrarlanan "Ben gizli bir hazineydim, bilinmeyi sevdim..." sözü sahih bir hadis değildir (İbn Teymiyye, Sehâvî, Aliyyü\'l-Kârî senedinin sabit olmadığını belirtmiştir). Ancak taşıdığı "Allah\'ın kâinatı isim ve kemâlâtını izhar etmek için yaratması" manası Zâriyât 56 ve Esmâ-i Hüsnâ ayetleriyle sabittir.'
    },
    {
      step: 5,
      badge: '⚠️↔️ Görüş Farkı',
      title: '5. Ehl-i Sünnet Akaidi — Mâtürîdî ve Eş\'arî Karşılaştırması',
      content: '• Ortak Zemin: Her iki Ehl-i Sünnet ekolüne göre Allah\'ı tanımak (marifetullah) mükellef olan her akıl sahibine farzdır ve Allah\'ın Zât\'ının hakikati (künhü) dünyada da ahirette de beşer aklıyla kuşatılamaz; O, sıfatları, isimleri ve fiilleriyle tanınır.\n• Mâtürîdî Yaklaşımı (İmam Mâtürîdî, Kitâbü\'t-Tevhîd): Kendisine peygamber tebliği ulaşmamış olsa bile akıl sahibi insan, kâinattaki nizam ve yaratılışa bakarak Yaratıcının varlığını ve birliğini aklen bilmekle yükümlüdür.\n• Eş\'arî Yaklaşımı (İmam Eş\'arî, Gazzâlî, Râzî): Akıl, Allah\'ı tanımanın âleti ve delilleri kavrama gücüdür; ancak sorumluluk (vücûb) akılla değil, ilâhî vahiy ve peygamber daveti ulaştıktan sonra başlar.'
    },
    {
      step: 6,
      badge: '🔆 Risale-i Nur Derinliği',
      title: '6. Risale-i Nur\'un Üç Büyük Muarrifi ve Marifet Merdiveni',
      content: 'Bediüzzaman Said Nursî, Ondokuzuncu Söz ve Şualar (Yedinci Şua — Âyetü\'l-Kübrâ, YA s. 964) başta olmak üzere külliyat boyunca Rabbimizi bize tanıtan üç büyük küllî muarrifi (tanıtıcıyı) şöyle sıralar:\n1. Kitâb-ı Kebîr-i Kâinat (Şehâdet âleminin her sayfası ve her canlısı),\n2. Hâtemü\'l-Enbiyâ Hz. Muhammed Aleyhissalâtü Vesselâm (Kâinat kitabının en büyük âyeti ve muallimi),\n3. Kur\'ân-ı Azîmüşşân (Kâinat kitabının ezelî tercümesi).\n\nYirminci Mektup (YA s. 628) ise marifeti "taklidî imandan tahkikî imana, tahkikî imandan marifetullaha, marifetullahtan muhabbetullaha ve lezzet-i ruhaniyeye" yükselen kesintisiz bir nur yolculuğu olarak kurar.'
    },
    {
      step: 7,
      badge: '🔆🪔 Risale Temsili',
      title: '7. Temsil Dürbünü — Eşsiz Saray ve Ustası',
      content: 'Muhteşem nakışlarla süslenmiş, her odasında binlerce hikmetli cihaz ve ziyafet sofrası bulunan bir saraya giren iki kişiden biri yalnız taşların rengine ve yemeklerin tadına bakar, sarayın sahibini hiç düşünmez (mana-yı ismî). Diğeri ise her nakışta ustanın sanatını, her ikramda saray sahibinin cömertliğini ve şefkatini okur; eserden fiile, fiilden ustanın unvanlarına ve sevgisine yükselir (mana-yı harfî). İşte kâinat o saraydır; marifetullah ise sarayın her nakşından Sultan-ı Ezelî\'nin isimlerini okumaktır.'
    },
    {
      step: 8,
      badge: '🌱 Tefekkür Notu',
      title: '8. Kalp ve Secde Uygulaması (3 Dakikalık Tefekkür)',
      content: 'Şimdi eline, gözüne veya pencereden gördüğün bir ağaca bak ve Marifet Merdiveni\'nin 6 basamağını uygula:\n1. Eser: Bu canlı ve ölçülü varlık.\n2. Fiil: Anbean yapılan besleme, koruma, şekil verme.\n3. İsim: Musavvir, Rezzâk, Hakîm, الحفيظ.\n4. Sıfat: Sonsuz İlim, İrade ve Kudret.\n5. Şe\'n: Mukaddes rahmet ve şefkat.\n6. Secde: "Sübhânallâhi ve bi-hamdihî — Rabbim, Seni bütün noksanlardan tenzih eder, Bize Kendini tanıttığın için Sana hamd ederim."'
    }
  ]
};

// ============================================================================
// 3. ABDULLAH'IN YOLCULUĞU — OYUN KONSEPTİ (1. BÖLÜM: ALLAH'A İMAN — 7 SAHNE)
// ============================================================================

export const ABDULLAH_GAME_CHAPTER_1 = {
  title: 'Abdullah\'ın Yolculuğu — 1. Bölüm: Allah\'a İman',
  subtitle: 'Sıradan bir günde başlayan büyük uyanış: Düzen, Fıtrat, Gaye, Hudûs ve İnâyet delilleriyle Üç Büyük Kapıya yürüyüş',
  controlsHelp: 'Klavyeden WASD / Yön Tuşları ile veya ekrandaki yön butonlarıyla Abdullah\'ı sahnede yürütün, nesnelere yaklaşarak düşünce balonlarını keşfedin.',
  longTermCurriculum: [
    { stage: 1, title: '1. Aşama: Varlık ve Yaratıcı', detail: 'Kâinattaki düzeni fark etme, her eserin bir ustası olduğunu anlama ve Üç Büyük Kapıya ulaşma.' },
    { stage: 2, title: '2. Aşama: Üç Büyük Kapı', detail: 'Kâinat Kitabı, Kur\'an-ı Kerim ve Peygamber Efendimiz (s.a.v.) rehberliğinde Yaratıcıyı tanıma.' },
    { stage: 3, title: '3. Aşama: İman Esasları', detail: 'Allah\'a, meleklere, kitaplara, peygamberlere, ahirete ve kadere imanın hakikatlerini keşfetme.' },
    { stage: 4, title: '4. Aşama: İbadet ve Kulluk Bilinci', detail: 'Namaz, dua ve ibadetlerin ruhunu, hikmetini ve günlük hayata verdiği huzuru yaşama.' },
    { stage: 5, title: '5. Aşama: İslam Ahlakı ve Güzel Yaşayış', detail: 'Doğruluk, emanet, adalet, merhamet, sabır, şükür ve insanlarla güzel muamele.' },
    { stage: 6, title: '6. Aşama: İnsanlığa Faydalı ve Bilinçli Kul Olma', detail: 'İlim, tefekkür, hikmet ve güzel ahlakı hayatın merkezine taşıma.' }
  ],
  scenes: [
    {
      id: 1,
      code: 'SAHNE 1',
      title: 'Açılış — Huzurlu Kasaba ve Sıradan Hayat',
      delilBadge: 'Giriş & Farkındalık Öncesi',
      environment: 'Sabah güneşinin aydınlattığı huzurlu kasaba meydanı, ağaçlar, kuş sesleri ve evler',
      bgGradient: 'from-amber-900/40 via-emerald-950 to-slate-950',
      abdullahStart: { x: 22, y: 65 },
      hotspot: { x: 75, y: 58, icon: '🏘️', label: 'Kasaba Meydanı & Sabah Güneşi' },
      speechBubbles: [
        'Her sabah aynı güneş doğuyor, kuşlar ötüyor, rüzgar esiyor...',
        'Bugün de her zamanki gibi sıradan bir gün... Acaba gerçekten sıradan mı?'
      ],
      innerReflection: 'Abdullah henüz büyük soruları sormuyor; ancak çevresindeki kusursuz ahenk birazdan kalbinde bir pencere açacak.',
      xpReward: 40
    },
    {
      id: 2,
      code: 'SAHNE 2',
      title: 'Kırılma Anı — Odadaki Düzen ve İlk Soru',
      delilBadge: 'Nizam & Düzenleyici İlkesi',
      environment: 'Abdullah\'ın çalışma odası: Kitaplık, masa, pencereden giren güneş ışığı',
      bgGradient: 'from-indigo-950 via-slate-900 to-amber-950/50',
      abdullahStart: { x: 25, y: 64 },
      hotspot: { x: 72, y: 54, icon: '📚', label: 'Düzenlenen Kitaplık ve Masa' },
      speechBubbles: [
        'Şu odamdaki kitaplar ve masa bile ben dizmeden, emek vermeden kendi kendine sıraya girmiyor...',
        'Küçücük bir oda bile kendi kendine düzenli olmuyorsa... Pencereden gördüğüm şu uçsuz bucaksız dünya nasıl bu kadar düzenli olabilir?'
      ],
      innerReflection: 'Kırılma Anı: Her düzen bir düzenleyiciyi gösterir. Bir harf kâtipsiz, bir iğne ustasız olmazsa bu kâinat nasıl sahipsiz olur?',
      xpReward: 50
    },
    {
      id: 3,
      code: 'SAHNE 3',
      title: 'Fıtrat Delili — Kalbin ve Vicdanın Sesi',
      delilBadge: 'Fıtrat & Vicdan Delili',
      environment: 'Sessiz bahçe köşesi ve tefekkür çardak yolu',
      bgGradient: 'from-emerald-950 via-teal-950 to-slate-950',
      abdullahStart: { x: 20, y: 66 },
      hotspot: { x: 74, y: 55, icon: '💚', label: 'Vicdan ve Fıtrat Aynası' },
      speechBubbles: [
        'İnsan ne kadar yalnız kalırsa kalsın, içinde kendisini aşan sonsuz bir kudrete yönelme ihtiyacı duyuyor.',
        'Sıkıştığımda, şükretmek istediğimde kalbim fıtrî olarak bir Yaratıcıyı arıyor. Bu his içime boşuna konulmuş olamaz!'
      ],
      innerReflection: 'Fıtrat Delili: İnsanın yaratılışında ve bozulmamış vicdanında Allah\'ı tanıma ve O\'na sığınma meyli vardır; vicdan asla yalan söylemez.',
      xpReward: 50
    },
    {
      id: 4,
      code: 'SAHNE 4',
      title: 'Gaye ve Nizam Delili — Doğada Hiçbir Şey Boşuna Değil',
      delilBadge: 'Gaye ve Nizam Delili',
      environment: 'Kasaba dışındaki çiçekli vadi, meyve ağaçları, arılar ve berrak akarsu',
      bgGradient: 'from-teal-900/60 via-emerald-950 to-slate-950',
      abdullahStart: { x: 18, y: 65 },
      hotspot: { x: 78, y: 52, icon: '🌸', label: 'Çiçek, Arı ve Güneş Uyumu' },
      speechBubbles: [
        'Güneş tam gereken mesafede ısıtıyor, yağmur toprağı diriltiyor, arı ile çiçek birbirini tanıyormuş gibi yardımlaşıyor!',
        'Tabiatta ne bir israf var ne de anlamsız bir tesadüf... Her şey bir gaye ve hikmet için birbirine bağlanmış.'
      ],
      innerReflection: 'Gaye ve Nizam Delili: Kâinattaki her varlık binlerce hikmetli gayeye hizmet eder; şuursuz sebepler bu küllî gayeyi bilemez.',
      xpReward: 60
    },
    {
      id: 5,
      code: 'SAHNE 5',
      title: 'Hudûs ve İmkân Delili — Sonradan Gelenin Var Edicisi',
      delilBadge: 'Hudûs ve İmkân Delili',
      environment: 'Mevsimlerin değiştiği tepe: Filizlenen tohumlar, doğan ve batan güneş',
      bgGradient: 'from-violet-950 via-indigo-950 to-slate-950',
      abdullahStart: { x: 22, y: 64 },
      hotspot: { x: 75, y: 54, icon: '🌱', label: 'Tohumdan Ağaca Oluşum Sırrı' },
      speechBubbles: [
        'Dün yok olan bu çiçek bugün var oldu. Gece gidiyor, gündüz geliyor; her şey sonradan var oluyor ve değişiyor.',
        'Yok iken var olan hiçbir şey kendi kendini yaratamaz. Öyleyse başlangıcı olmayan, varlığı zorunlu (Vâcibü\'l-Vücûd) bir Yaratıcı var!'
      ],
      innerReflection: 'Hudûs ve İmkân Delili: Sonradan var olan (hâdis) ve varlığıyla yokluğu eşit olan (mümkin) her şey, Ezelî ve Kadîr bir Yaratıcının tercihine muhtaçtır.',
      xpReward: 60
    },
    {
      id: 6,
      code: 'SAHNE 6',
      title: 'İnâyet Delili — Şefkatle Karşılanan İhtiyaçlar',
      delilBadge: 'İnâyet & Rahmet Delili',
      environment: 'Akarsu kıyısında kuş yavruları, meyve dalları ve şefkatli tabiat sahnesi',
      bgGradient: 'from-amber-900/50 via-teal-950 to-slate-950',
      abdullahStart: { x: 20, y: 64 },
      hotspot: { x: 76, y: 56, icon: '🕊️', label: 'Rızıklandırılan Canlılar & Rahmet' },
      speechBubbles: [
        'Sadece kuru bir düzen yok burada; aciz yavrulara en tatlı sütü, susamış toprağa yağmuru, insana binbir çeşit meyveyi gönderen bir şefkat var!',
        'Bizi ve bütün canlıları tanıyan, ihtiyacımızı bilen, koruyan ve ikram eden Rahmân ve Rahîm bir Rabbimiz var.'
      ],
      innerReflection: 'İnâyet Delili: Kâinattaki düzen yalnızca mekanik bir çark değil; arkasında şefkat, ikram ve rahmet bulunan bir Rubûbiyet sofrasıdır.',
      xpReward: 70
    },
    {
      id: 7,
      code: 'SAHNE 7',
      title: 'Kapanış ve Yeni Başlangıç — Üç Büyük Kapı',
      delilBadge: '3 Büyük Muarrif Kapısı',
      environment: 'Ufukta parlayan Üç Nurânî Kemer Kapı: Kâinat Kitabı, Kur\'an-ı Kerim, Hz. Peygamber (s.a.v.)',
      bgGradient: 'from-amber-700/40 via-emerald-900/70 to-slate-950',
      abdullahStart: { x: 24, y: 65 },
      hotspot: { x: 75, y: 50, icon: '🚪', label: 'Üç Büyük Hakikat Kapısı' },
      speechBubbles: [
        'Artık biliyorum: Bu muazzam kâinat sahipsiz değil; her zerrede kudreti, ilmi ve rahmeti görünen Allah\'ın eseri!',
        'Şimdi önümde O\'nu daha yakından tanımam için Üç Büyük Kapı açılıyor: Kâinat Kitabı, Kur\'an-ı Kerim ve Peygamber Efendimiz (s.a.v.)!'
      ],
      innerReflection: '1. Bölüm Tamamlandı! Abdullah artık "Bir Yaratıcı var" hakikatinden, "Rabbimi nasıl daha derin tanırım?" (Marifetullah) yolculuğuna adım atıyor.',
      xpReward: 100
    }
  ]
};

// ============================================================================
// 4. BİLİM VE TEFEKKÜR BELGESEL ARŞİVİ (1.745 KAYIT • 45 KATEGORİ ÖZETİ & SEÇKİ)
// ============================================================================

export const DOCUMENTARY_CATEGORY_MAP = [
  { group: 'Evren, Kozmoloji & Fizik', count: 294, categories: ['Kozmoloji ve Büyük Ölçekli Evren (83)', 'Güneş Sistemi ve Gezegen Bilimi (61)', 'Yıldızlar ve Galaksiler (37)', 'Uzay Keşfi ve Astronomi (56)', 'Temel Fizik ve Kuantum (57)'] },
  { group: 'Canlılar, Biyoloji & Ekosistem', count: 472, categories: ['Biyoloji ve Hücre (56)', 'Hayvanlar Alemi (91)', 'Bitkiler ve Mantarlar (37)', 'Deniz ve Okyanus (51)', 'Ekosistemler ve Biyomlar (54)', 'Doğa Tarihi ve Paleontoloji (52)'] },
  { group: 'İnsan, Zihin & Tıp (PDR Bağlantılı)', count: 162, categories: ['İnsan Bedeni ve Tıp (52)', 'Beyin, Zihin ve Psikoloji (52)', 'Beslenme, Uyku ve Uzun Yaşam (29)', 'Genetik ve Biyoteknoloji (29)'] },
  { group: 'Yerküre, İklim & Kimya', count: 173, categories: ['Yerküre ve Jeoloji (65)', 'İklim, Hava ve Atmosfer (43)', 'Su Dünyası ve Hidroloji (30)', 'Kimya ve Elementler (35)'] },
  { group: 'Tarih, Medeniyet & Bilim Tarihi', count: 318, categories: ['İslam Medeniyeti ve Bilim Tarihi (32)', 'Bilim Tarihi ve Bilim İnsanları (42)', 'Antik Medeniyetler (63)', 'Orta Çağ ve Yeni Çağ (40)', 'Modern Tarih (50)', 'Felsefe, Ahlak ve Tefekkür (30)'] },
  { group: 'Görsel Keşif, IMAX & Doğa Sinematografisi', count: 118, categories: ['Sinematik Doğa ve IMAX (48)', 'Makro, Mikro ve Time-Lapse (20)', 'Kültürel ve Coğrafi Yolculuk (50)'] }
];

export const CURATED_DOCUMENTARIES = [
  {
    id: 'BEL-0001',
    title: 'Cosmos: A Spacetime Odyssey',
    trTitle: 'Kozmos: Bir Uzay Serüveni',
    year: 2014,
    type: 'Mini Dizi (13 Bölüm)',
    category: 'Kozmoloji ve Büyük Ölçekli Evren',
    subField: 'Bilim tarihi ve kozmos',
    priority: 'Önce İzle',
    visualStyle: 'Sinematik / IMAX',
    platform: 'Disney+ / National Geographic',
    youtubeChannel: 'National Geographic',
    watchUrl: 'https://www.youtube.com/results?search_query=Cosmos+A+Spacetime+Odyssey+National+Geographic+belgesel',
    tefekkurQuestion: 'Ölçek, zaman ve fizik yasaları atomdan galaksilere kadar farklı gök cisimlerini nasıl tek bir nizam içinde birbirine bağlar?',
    marifetLink: 'Vâhidiyet & Nizam Delili — Göklerin ve yerin tek bir kanun ve kudret kalemiyle yazılması (Mülk 67/3-4).'
  },
  {
    id: 'BEL-0136',
    title: 'One Strange Rock',
    trTitle: 'Sıradışı Bir Kaya (Dünya\'nın İnce Ayarlı Dengesi)',
    year: 2018,
    type: 'Mini Dizi (10 Bölüm)',
    category: 'Güneş Sistemi ve Gezegen Bilimi',
    subField: 'Dünya\'nın uzaydan sistemik görünümü',
    priority: 'Önce İzle',
    visualStyle: 'Sinematik / IMAX',
    platform: 'Disney+ / National Geographic',
    youtubeChannel: 'National Geographic',
    watchUrl: 'https://www.youtube.com/results?search_query=One+Strange+Rock+National+Geographic+documentary',
    tefekkurQuestion: 'Atmosfer, manyetik kalkan, su döngüsü ve oksijen dengesi insan hayatı için nasıl bir inâyet ve koruma kubbesi oluşturur?',
    marifetLink: 'İnâyet & Hafîz İsm-i Şerifi — Yeryüzünün insan için beşik ve korunmuş bir saray kılınması.'
  },
  {
    id: 'BEL-0238',
    title: 'Particle Fever',
    trTitle: 'Parçacık Ateşi (CERN & Higgs Bozonu)',
    year: 2013,
    type: 'Tek Film (99 dk)',
    category: 'Temel Fizik, Kuantum ve Görelilik',
    subField: 'Higgs bozonu ve Büyük Hadron Çarpıştırıcısı',
    priority: 'Önce İzle',
    visualStyle: 'Standart',
    platform: 'Prime Video / Apple TV',
    youtubeChannel: 'Real Science / CERN',
    watchUrl: 'https://www.youtube.com/results?search_query=Particle+Fever+Higgs+Boson+documentary',
    tefekkurQuestion: 'Maddenin en temel yapı taşlarında işleyen matematiksel simetri ve ince ayar neyi gösterir?',
    marifetLink: 'İlm-i Muhît & Mikdâr — "O\'nun katında her şey bir ölçü (mikdâr) iledir" (Ra\'d 13/8).'
  },
  {
    id: 'BEL-0288',
    title: 'The Secret Life of Chaos',
    trTitle: 'Kaosun Gizli Yaşamı (Kendi Kendine Örgütlenme ve Düzen)',
    year: 2010,
    type: 'Tek Film (60 dk) • BBC / Jim Al-Khalili',
    category: 'Matematik, Mantık ve Bilgi Kuramı',
    subField: 'Kaos, fraktallar ve doğadaki matematiksel desenler',
    priority: 'Önce İzle',
    visualStyle: 'Standart',
    platform: 'BBC / YouTube',
    youtubeChannel: 'Spark / BBC Science',
    watchUrl: 'https://www.youtube.com/results?search_query=The+Secret+Life+of+Chaos+Jim+Al+Khalili+full+documentary',
    tefekkurQuestion: 'Karmaşık görünen doğa olaylarının arkasındaki fraktal geometri ve matematiksel yasalar nasıl şaşmaz bir intizam üretir?',
    marifetLink: 'Hakîm ve Mukaddir — Tesadüf zannedilen hadiselerin altında işleyen kader ve hikmet pergeli.'
  },
  {
    id: 'BEL-0327',
    title: 'Our Universe',
    trTitle: 'Evrenimiz (Kozmik Tarih ile Canlı Yaşamın Bağlantısı)',
    year: 2022,
    type: 'Mini Dizi (6 Bölüm • Morgan Freeman)',
    category: 'Biyoloji ve Yaşamın Temelleri',
    subField: 'Güneş\'teki nükleer füzyondan yeryüzündeki bir kaplumbağanın hayatına uzanan bağ',
    priority: 'Önce İzle',
    visualStyle: 'Sinematik / IMAX',
    platform: 'Netflix',
    youtubeChannel: 'Netflix / BBC Earth',
    watchUrl: 'https://www.youtube.com/results?search_query=Our+Universe+Netflix+documentary+trailer+episode',
    tefekkurQuestion: 'Milyarlarca ışık yılı uzaktaki yıldız süreçleri ile yeryüzündeki tek bir canlının rızkı nasıl birbirine bağlanmıştır?',
    marifetLink: 'Vâhidiyet içinde Ehadiyet — Bir çiçeği ve bir canlıyı beslemek için bütün kâinat çarklarını elinde tutan Rubûbiyet.'
  },
  {
    id: 'BEL-0332',
    title: 'The Hidden Life of the Cell',
    trTitle: 'Hücrenin Gizli Yaşamı (Moleküler Makineler)',
    year: 2012,
    type: 'Tek Film (60 dk) • BBC',
    category: 'Biyoloji ve Yaşamın Temelleri',
    subField: 'Hücre içi protein motorları, DNA onarımı ve savunma sistemi',
    priority: 'Önce İzle',
    visualStyle: 'Makro / Mikro / CGI',
    platform: 'BBC / YouTube',
    youtubeChannel: 'BBC Earth Science',
    watchUrl: 'https://www.youtube.com/results?search_query=Our+Secret+Universe+The+Hidden+Life+of+the+Cell+BBC',
    tefekkurQuestion: 'Gözle görülmeyen tek bir hücrede saniyede binlerce işlemi hatasız yürüten moleküler kuryeler ve şifre okuyucular nasıl çalışır?',
    marifetLink: 'San\'at-ı İlâhiye & Hayy-ı Kayyûm — En küçük zerrede en büyük fabrika nizamını kuran Kudret.'
  },
  {
    id: 'BEL-0500',
    title: 'Planet Earth II & III',
    trTitle: 'Gezegenimiz (Yeryüzü Biyomları ve Hayatta Kalma Hikmetleri)',
    year: 2016,
    type: 'Dizi (BBC • David Attenborough)',
    category: 'Ekosistemler ve Biyomlar',
    subField: 'Adalar, dağlar, ormanlar, çöller ve şehirlerde yaşam',
    priority: 'Önce İzle',
    visualStyle: 'Sinematik / IMAX',
    platform: 'BBC Earth / BluTV / YouTube',
    youtubeChannel: 'BBC Earth',
    watchUrl: 'https://www.youtube.com/results?search_query=Planet+Earth+II+BBC+Earth+full+episode',
    tefekkurQuestion: 'En çetin çöl ve kutup şartlarında bile her canlıya tam ihtiyacına uygun cihaz ve rızkın verilmesi hangi hakikati gösterir?',
    marifetLink: 'Rezzâk, Муdebbir ve Rahîm — Yeryüzü sofrasında hiçbir canlının unutulmaması (Hûd 11/6).'
  },
  {
    id: 'BEL-0525',
    title: 'Fantastic Fungi',
    trTitle: 'Muhteşem Mantarlar (Toprak Altı İletişim Ağı)',
    year: 2019,
    type: 'Tek Film (81 dk • Louie Schwartzberg)',
    category: 'Bitkiler, Mantarlar ve Toprak',
    subField: 'Miselyum ağı ve orman ekosistemindeki yardımlaşma',
    priority: 'Önce İzle',
    visualStyle: 'Time-Lapse / Makro',
    platform: 'Netflix / Apple TV',
    youtubeChannel: 'Moving Art / Netflix',
    watchUrl: 'https://www.youtube.com/results?search_query=Fantastic+Fungi+documentary+time+lapse',
    tefekkurQuestion: 'Toprağın altında ağaçların birbirine besin ve uyarı göndermesini sağlayan miselyum ağı, doğadaki yardımlaşma (tesânüd) yasasını nasıl ortaya koyar?',
    marifetLink: 'Teâvün (Yardımlaşma) Kanunu — Şuursuz varlıkların birbirinin imdadına koşması, tek bir Müdebbir\'in emriyle hareket ettiklerinin delilidir.'
  },
  {
    id: 'BEL-1210',
    title: 'Science and Islam',
    trTitle: 'Bilim ve İslam (İslam Medeniyetinin Bilim Tarihi)',
    year: 2009,
    type: 'Mini Dizi (3 Bölüm • BBC • Jim Al-Khalili)',
    category: 'İslam Medeniyeti ve Bilim Tarihi',
    subField: 'İbnü\'l-Heysem, Hârizmî, İbn Sînâ, Bîrûnî ve deneysel bilimin doğuşu',
    priority: 'Önce İzle',
    visualStyle: 'Tarihî / Bilimsel',
    platform: 'BBC / YouTube Tam Bölüm',
    youtubeChannel: 'Spark / Al Jazeera Science',
    watchUrl: 'https://www.youtube.com/results?search_query=Science+and+Islam+Jim+Al+Khalili+full+documentary',
    tefekkurQuestion: 'Kur\'ân\'ın kâinatı inceleme, ölçme ve tefekkür etme emri Müslüman âlimleri cebir, optik, tıp ve astronomiyi kurmaya nasıl sevk etmiştir?',
    marifetLink: 'Kitâb-ı Kebîr-i Kâinat — Vahiy ile kâinat ayetlerini birlikte okuyan medeniyet ufku.'
  },
  {
    id: 'BEL-1658',
    title: 'The Mind, Explained',
    trTitle: 'Zihnimizi Açıklıyoruz (Hafıza, Rüyalar, Kaygı, Farkındalık)',
    year: 2019,
    type: 'Mini Dizi (10 Bölüm • 20 dk)',
    category: 'Beyin, Zihin ve Psikoloji (PDR Özel)',
    subField: 'Bellek kodlama, Ebbinghaus unutma mekanizması, kaygı ve odaklanma',
    priority: 'Önce İzle',
    visualStyle: 'İnfografik / PDR',
    platform: 'Netflix / YouTube Vox',
    youtubeChannel: 'Vox / Netflix',
    watchUrl: 'https://www.youtube.com/results?search_query=The+Mind+Explained+Memory+Netflix+Vox',
    tefekkurQuestion: 'İnsan hafızası, duyguları ve bilinci etten bir beyin dokusunun içinde nasıl muhafaza edilir ve anlam üretir?',
    marifetLink: 'Enfüsî Tefekkür & Latîfe-i Rabbâniye — İnsanın iç âlemine yerleştirilen hafıza, hayal ve akıl hazineleri.'
  },
  {
    id: 'BEL-1688',
    title: 'NOVA: Decoding the Universe — Cosmos',
    trTitle: 'NOVA PBS: Evrenin Şifresini Çözmek (Ücretsiz YouTube Tam Bölüm)',
    year: 2024,
    type: 'YouTube Tam Bölüm (53 dk)',
    category: 'YouTube\'da Ücretsiz Tam Bölümler',
    subField: 'NOVA PBS Resmi Kanalı',
    priority: 'Önce İzle',
    visualStyle: 'Sinematik / Bilimsel',
    platform: 'YouTube (NOVA PBS)',
    youtubeChannel: 'NOVA PBS Official',
    watchUrl: 'https://www.youtube.com/watch?v=d44TQRew1c8',
    tefekkurQuestion: 'Evrenin başlangıcından bugüne uzanan kozmik sabitler hangi hassas ölçüyle korunmaktadır?',
    marifetLink: 'Fâtır-ı Hakîm — Gökleri ve yeri benzersiz bir nizamla yoktan var eden kudret.'
  },
  {
    id: 'BEL-1702',
    title: 'TRT Belgesel: İslam Bilim Tarihi & Su Savaşları / Doğa Seçkisi',
    trTitle: 'TRT Belgesel & TRT 2: Bilim, Doğa ve Medeniyet Seçkisi',
    year: 2023,
    type: 'TV & YouTube Resmi Arşiv',
    category: 'TRT Belgesel & Türkçe Kaynaklar',
    subField: 'Anadolu ekosistemi, su döngüsü ve İslam bilim mirası',
    priority: 'Önce İzle',
    visualStyle: 'Sinematik / Türkçe',
    platform: 'TRT Belgesel / tabii / YouTube',
    youtubeChannel: 'TRT Belgesel',
    watchUrl: 'https://www.youtube.com/@trtbelgesel/videos',
    tefekkurQuestion: 'Yaşadığımız coğrafyadaki su kaynakları, göçmen kuş rotaları ve biyoçeşitlilik nasıl bir rahmet takvimiyle işler?',
    marifetLink: 'Âfâkî Tefekkür — Kendi coğrafyamızdaki ilâhî sanat ve rızık döngüsünü müşahede etmek.'
  }
];
