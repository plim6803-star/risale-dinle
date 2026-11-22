export interface KeyTerm {
  term: string;
  meaning: string;
}

export interface SeedBook {
  slug: string;
  title: string;
  ottomanTitle: string;
  subtitle: string;
  description: string;
  category: string;
  coverColor: string;
  accentGold: string;
  collectionOrder: number;
}

export interface SeedNarrator {
  slug: string;
  name: string;
  title: string;
  styleDescription: string;
  voicePitchHz: number;
  speechRateMultiplier: string;
  streamSourceLabel: string;
  externalArchiveUrl: string;
  avatarInitials: string;
}

export interface SeedChapter {
  bookSlug: string;
  chapterNumber: number;
  partLabel: string;
  title: string;
  sectionSubtitle: string;
  durationSeconds: number;
  excerptText: string;
  keyTerms: KeyTerm[];
  externalAudioUrl: string;
  externalDownloadUrl: string;
  archiveOrgUrl: string;
  fileSizeMb: string;
}

export const SEED_NARRATORS: SeedNarrator[] = [
  {
    slug: "abdullah-koseoglu",
    name: "Abdullah Köseoğlu",
    title: "Tam Külliyat Seslendirmesi (RNK)",
    styleDescription:
      "RNK Neşriyat ve Sorularla Risale arşivinin ana sesi; berrak, ölçülü ve tam külliyat kıraati.",
    voicePitchHz: 196,
    speechRateMultiplier: "1.0",
    streamSourceLabel: "RNK & Sorularla Risale Arşivi",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "AK",
  },
  {
    slug: "mehmet-ali-ozkan",
    name: "Mehmet Ali Özkan",
    title: "Sözler & Mektubat Okuması",
    styleDescription:
      "Tefekkür ikliminde akıcı ve ahenkli Risale-i Nur kıraati.",
    voicePitchHz: 210,
    speechRateMultiplier: "0.95",
    streamSourceLabel: "Sorularla Risale & Nurpenceresi",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "MÖ",
  },
  {
    slug: "nadir-kaya",
    name: "Nadir Kaya",
    title: "Vakarlı ve Dingin Ders Kıraati",
    styleDescription:
      "8, 9 ve 26. Sözler gibi derin bahislerde ağırbaşlı ve tefekküre sevk eden okuma.",
    voicePitchHz: 180,
    speechRateMultiplier: "0.90",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "NK",
  },
  {
    slug: "zeki-kaplan",
    name: "Zeki Kaplan",
    title: "Fasîh ve Teshirli Seslendirme",
    styleDescription:
      "11 ve 12. Sözler gibi muazzam hakikatlerde berrak diksiyon ve etkileyici tonlama.",
    voicePitchHz: 192,
    speechRateMultiplier: "0.95",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "ZK",
  },
  {
    slug: "enes-araci",
    name: "Enes Aracı",
    title: "Canlı ve Vurgulu İfade",
    styleDescription:
      "21. Söz (Namaz) ve 29. Söz (Ruh ve Melekler) bahislerinde kalbe tesir eden hitabet.",
    voicePitchHz: 205,
    speechRateMultiplier: "0.95",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "EA",
  },
  {
    slug: "osman-calik",
    name: "Osman Çalık",
    title: "Delil ve Bürhan Odaklı Okuma",
    styleDescription:
      "22. Söz (Tevhid Delilleri) gibi 34 farklı parçada mantıkî ve ilzam edici kıraat.",
    voicePitchHz: 185,
    speechRateMultiplier: "0.92",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "OÇ",
  },
  {
    slug: "sadik-gundogdu",
    name: "Sadık Gündoğdu",
    title: "Hikmet ve Hakikat Terennümü",
    styleDescription:
      "24. Söz (Esma-i Hüsna ve Dallar) derslerinde geniş ufuklu ve ahenkli okuma.",
    voicePitchHz: 198,
    speechRateMultiplier: "0.95",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "SG",
  },
  {
    slug: "mehmet-akcay",
    name: "Mehmet Akçay",
    title: "Edebî ve Manzum İklim Okuması",
    styleDescription:
      "17. Söz ve Fârisî Beyitler ile Lemaat manzumelerinde musiki ve mana ahengi.",
    voicePitchHz: 200,
    speechRateMultiplier: "0.92",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "MA",
  },
  {
    slug: "metin-bayburtlu",
    name: "Metin Bayburtlu",
    title: "Gençlik Rehberi & İkna Kıraati",
    styleDescription:
      "13. Söz Gençlik Rehberi derslerinde samimî, muhatap kılan ve tesirli seslendirme.",
    voicePitchHz: 188,
    speechRateMultiplier: "0.95",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "MB",
  },
  {
    slug: "hasan-erboga",
    name: "Hasan Erboğa",
    title: "Nübüvvet Delilleri Kıraati",
    styleDescription:
      "19. Söz Mu'cizat-ı Ahmediye ve Reşhalar bahsinde hürmetkâr ve berrak seslendirme.",
    voicePitchHz: 190,
    speechRateMultiplier: "0.95",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "HE",
  },
  {
    slug: "enver-seyitoglu",
    name: "Enver Seyitoğlu",
    title: "Tevhid ve Hüve Nüktesi Okuması",
    styleDescription:
      "13. Söz 2. Makam Hüve Nüktesi'nde hava zerrelerinin tevhidine dair berrak okuma.",
    voicePitchHz: 185,
    speechRateMultiplier: "0.90",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "ES",
  },
  {
    slug: "mehmet-yuksel",
    name: "Mehmet Yüksel",
    title: "Tefekkür ve İbret Kıraati",
    styleDescription:
      "17. Söz ve zeyillerinde dünya faniliğini ve hakikatini hissettiren okuma.",
    voicePitchHz: 178,
    speechRateMultiplier: "0.92",
    streamSourceLabel: "Sorularla Risale",
    externalArchiveUrl: "https://sorularlarisale.com/risale-dinle/sozler",
    avatarInitials: "MY",
  },
];

export const SEED_BOOKS: SeedBook[] = [
  {
    slug: "sozler",
    title: "Sözler",
    ottomanTitle: "سوزلر",
    subtitle: "İman ve Kur'an Hakikatleri • 33 Söz",
    description:
      "Risale-i Nur Külliyatı'nın ilk ve en temel eseri. Bismillâh sırrından Haşir hakikatine, namazın hikmetinden Tevhid burhanlarına kadar imanın rükünlerini akla ve kalbe berrak temsillerle ders verir.",
    category: "Ana Külliyat",
    coverColor: "#7C1D26",
    accentGold: "#D4A84F",
    collectionOrder: 1,
  },
  {
    slug: "mektubat",
    title: "Mektubat",
    ottomanTitle: "مكتوبات",
    subtitle: "İman, İslâmiyet ve Mu'cizât-ı Ahmediye • 33 Mektup",
    description:
      "Talebelere ve muhtelif suallere cevaben kaleme alınan otuz üç mektup. Mu'cizât-ı Ahmediye, Kelime-i Tevhid mertebeleri, uhuvvet ve velâyet yollarının hakikatlerini ihtiva eder.",
    category: "Ana Külliyat",
    coverColor: "#631720",
    accentGold: "#C89B3C",
    collectionOrder: 2,
  },
  {
    slug: "lemalar",
    title: "Lem'alar",
    ottomanTitle: "لمعه‌لر",
    subtitle: "İhlas, Hikmet, Şefkat ve Deva Nurları • 33 Lem'a",
    description:
      "Hz. Yunus ve Hz. Eyyüb kıssalarından İhlas ve Hastalar Risalesi'ne, İktisat ve Tabiat Risalesi'nden Nota'lara kadar kalp ve ruh hayatına şifa veren otuz üç nurlu lem'a.",
    category: "Ana Külliyat",
    coverColor: "#1E4D3B",
    accentGold: "#D4A84F",
    collectionOrder: 3,
  },
  {
    slug: "sualar",
    title: "Şualar",
    ottomanTitle: "شعاعلر",
    subtitle: "Âyetü'l-Kübrâ, Münâcât ve Meyve Risalesi • 15 Şua",
    description:
      "Kâinattan Hâlıkını soran bir seyyahın müşahedelerini anlatan Âyetü'l-Kübrâ, Münâcât Risalesi, Hasbünallah mertebeleri ve Denizli hapsinin meyvesi olan On Birinci Şua'yı cem eder.",
    category: "Ana Külliyat",
    coverColor: "#7A232B",
    accentGold: "#E0B65A",
    collectionOrder: 4,
  },
  {
    slug: "asa-yi-musa",
    title: "Asâ-yı Mûsa",
    ottomanTitle: "عصا موسی",
    subtitle: "Meyve Risalesi ve Hüccetü'l-Bâliğa • 22 Mesele",
    description:
      "Mûsa Aleyhisselâm'ın asâsı gibi dalâlet ve şüphe kayalarını parçalayıp hakikat ab-ı hayatını çıkaran, on bir mesele ve on bir hüccet-i imaniyeden müteşekkil muazzam eser.",
    category: "Ana Külliyat",
    coverColor: "#4A2531",
    accentGold: "#C89B3C",
    collectionOrder: 5,
  },
  {
    slug: "mesnevi-i-nuriye",
    title: "Mesnevî-i Nûriye",
    ottomanTitle: "مثنوی نوریه",
    subtitle: "Katre, Habbe, Şemme, Zerre ve Reşhalar",
    description:
      "Risale-i Nur'un bir nevi çekirdeği ve fidanlığı hükmünde olan, enfüsî tefekkür ve Tevhid zevkini beliğ bir üslupla ders veren on risaleden müteşekkil eser.",
    category: "Ana Külliyat",
    coverColor: "#253D34",
    accentGold: "#D4A84F",
    collectionOrder: 6,
  },
  {
    slug: "isaratul-icaz",
    title: "İşârâtü'l-İ'câz",
    ottomanTitle: "اشارات الاعجاز",
    subtitle: "Kur'an'ın Nazmındaki İ'câz Lem'aları",
    description:
      "Birinci Cihan Harbi'nde cephede telif edilen, Fâtiha Sûresi ve Bakara Sûresi'nin ilk âyetlerinin kelime ve cümle dizilişindeki mucizevî belâgatı beyan eden eşsiz tefsir.",
    category: "Tefsir & İlk Dönem",
    coverColor: "#5C2C16",
    accentGold: "#D4A84F",
    collectionOrder: 7,
  },
  {
    slug: "barla-lahikasi",
    title: "Barla Lâhikası",
    ottomanTitle: "بارلا لاحقه‌سی",
    subtitle: "İlk Nur Talebelerinin Hizmet ve İhlas Mektupları",
    description:
      "Risale-i Nur'un ilk telif merkezi olan Barla'da Hulusi Bey, Sabri Efendi, Hafız Ali ve Hüsrev Efendi gibi ilk talebelerin kaleme aldığı samimi tefekkür mektupları.",
    category: "Lâhikalar",
    coverColor: "#6B242B",
    accentGold: "#C89B3C",
    collectionOrder: 8,
  },
  {
    slug: "tarihce-i-hayat",
    title: "Tarihçe-i Hayat",
    ottomanTitle: "تاریخچهٔ حیات",
    subtitle: "Bediüzzaman Said Nursî'nin Hayatı ve Hizmet Safhaları",
    description:
      "Nurs köyünden İstanbul'a, Barla'dan Kastamonu, Denizli, Afyon ve Emirdağ yıllarına uzanan bir ömrün iman ve Kur'an hizmeti safhaları, mektupları ve müdafaaları.",
    category: "Hayat & Müdafaa",
    coverColor: "#3D1F28",
    accentGold: "#D4A84F",
    collectionOrder: 9,
  },
  {
    slug: "kastamonu-lahikasi",
    title: "Kastamonu Lâhikası",
    ottomanTitle: "قسطمونى لاحقه‌سی",
    subtitle: "Kastamonu Hayatındaki İrşad ve Hizmet Mektupları",
    description:
      "Sekiz yıllık Kastamonu ikameti esnasında İkinci Cihan Harbi fırtınaları içinde kalplere inşirah veren, ihlas, takva ve hizmet düsturlarını ders veren nurlu mektuplar.",
    category: "Lâhikalar",
    coverColor: "#4F242C",
    accentGold: "#D4A84F",
    collectionOrder: 10,
  },
  {
    slug: "emirdag-lahikasi",
    title: "Emirdağ Lâhikası",
    ottomanTitle: "اميراغ لاحقه‌سی",
    subtitle: "Emirdağ Yılları • Müsbet Hareket ve İçtimaî Düsturlar",
    description:
      "Denizli ve Afyon hapislerinden sonra Emirdağ'da kaleme alınan, Nur hizmetinin içtimaî düsturlarını, müsbet hareket ve asayişi muhafaza esaslarını beyan eden iki ciltlik lâhika.",
    category: "Lâhikalar",
    coverColor: "#264738",
    accentGold: "#C89B3C",
    collectionOrder: 11,
  },
  {
    slug: "sikke-i-tasdik-i-gaybi",
    title: "Sikke-i Tasdik-i Gaybî",
    ottomanTitle: "سكهٔ تصديق غيبى",
    subtitle: "Tevafukat, İşârât-ı Kur'aniye ve Gaybî Müjdeler",
    description:
      "Kur'an-ı Kerim'in lâfzî ve manevî tevafuk mucizesini, Risale-i Nur'un bu asırda iman hizmetindeki makbuliyetine dair gaybî işaretleri ve Celcelûtiye sırlarını cem eden eser.",
    category: "Külliyat Mecmuası",
    coverColor: "#631C24",
    accentGold: "#E0B65A",
    collectionOrder: 12,
  },
  {
    slug: "muhakemat",
    title: "Muhâkemat",
    ottomanTitle: "محاكمات",
    subtitle: "Unsuru'l-Hakikat, Unsuru'l-Belâgat ve Unsuru'l-Akîde",
    description:
      "Tefsir usulünü, İslâmiyet ile fünun-u müsbetenin barışıklığını, belâgat sırlarını ve akaid burhanlarını on iki mukaddeme ile tesis eden muazzam ilim ve mantık şaheseri.",
    category: "Tefsir & İlk Dönem",
    coverColor: "#542E1B",
    accentGold: "#D4A84F",
    collectionOrder: 13,
  },
  {
    slug: "iman-ve-kufur-muvazeneleri",
    title: "İman ve Küfür Muvazeneleri",
    ottomanTitle: "ايمان و كفر موازنه‌لری",
    subtitle: "Hidayet ve Dalâletin Akıl, Kalp ve Ruh Üzerindeki Mukayesesi",
    description:
      "İmanın dünyada dahi manevî bir Cennet çekirdeği, küfrün ve gafletin ise manevî bir Cehennem zakkumu olduğunu temsilî muvazenelerle ispat eden seçkin risaleler mecmuası.",
    category: "Külliyat Mecmuası",
    coverColor: "#2C3E35",
    accentGold: "#D4A84F",
    collectionOrder: 14,
  },
];

export const SEED_CHAPTERS: SeedChapter[] = [
  // ================= SÖZLER =================
  {
    bookSlug: "sozler",
    chapterNumber: 1,
    partLabel: "Birinci Söz",
    title: "Bismillâh Her Hayrın Başıdır",
    sectionSubtitle: "Besmele-i Şerife'nin Sırrı, Bereket ve Kudret Temsili",
    durationSeconds: 860,
    excerptText:
      "Bismillâh her hayrın başıdır. Biz dahi başta ona başlarız. Bil ey nefsim, şu mübarek kelime İslâm nişanı olduğu gibi, bütün mevcudatın lisan-ı hâliyle vird-i zebânıdır. Bismillâh ne büyük tükenmez bir kuvvet, ne çok bitmez bir bereket olduğunu anlamak istersen, şu temsilî hikâyeciğe bak, dinle: Bedevî Arap çöllerinde seyahat eden adama gerektir ki, bir kabile reisinin ismini alsın ve himayesine girsin; tâ şakîlerin şerrinden kurtulup hâcâtını tedarik edebilsin. Yoksa tek başıyla hadsiz düşman ve ihtiyacata karşı perişan olacaktır. İşte böyle bir seyahat için iki adam sahraya çıkıp gidiyorlar. Birisi mütevazı idi, diğeri mağrur. Mütevazıı bir reisin ismini aldı, mağrur almadı. Alanı her yerde selâmetle gezdi; bir yol kesiciye rast gelse der: Ben filân reisin ismiyle gezerim. Şakî defolur, ilişemez. Bir çadıra girse, o nam ile hürmet görür. Öteki mağrur, bütün seyahatinde öyle belâlar çeker ki tarif edilmez; daima titrer, daima dilencilik eder, hem zelil hem rezil oldu. İşte ey mağrur nefsim! Sen o seyyahsın. Şu dünya ise bir çöldür. Aczin ve fakrın hadsizdir, düşmanın ve hâcâtın nihayetsizdir. Madem öyledir; şu sahranın Mâlik-i Ebedîsi ve Hâkim-i Ezelîsinin ismini al, tâ bütün kâinatın dilenciliğinden ve her hadisâtın karşısında titremeden kurtulasın! Evet, bu kelâm öyle mübarek bir definedir ki, senin nihayetsiz aczin ve fakrın, seni nihayetsiz kudrete ve rahmete rabtedip Kadîr-i Rahîm'in dergâhında acz ve fakrı en makbul bir şefaatçi yapar. Her şey Bismillâh der; zerre gibi bir tek çekirdek Bismillâh der, koca bir çam ağacı gibi bir yükü omuzuna alır. Madem her şey manen Bismillâh der, Allah namına Allah'ın nimetlerini getirip bize veriyorlar; biz dahi Bismillâh demeliyiz. Allah namına vermeliyiz, Allah namına almalıyız.",
    keyTerms: [
      { term: "Lisan-ı hâl", meaning: "Hâl dili, varlıkların duruş ve yaratılışlarıyla konuşması" },
      { term: "Vird-i zebân", meaning: "Dilden düşmeyen zikir, sürekli tekrarlanan söz" },
      { term: "İntisap", meaning: "Bağlanma, bir zatın namına ve himayesine dayanma" },
      { term: "Mevcudat", meaning: "Yaratılmış bütün varlıklar, kâinat" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p001-1.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p001-1.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "5.2 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 2,
    partLabel: "İkinci Söz",
    title: "İman Bir İntisaptır • Saadet ve Tevekkül",
    sectionSubtitle: "Hodbin ve Hüdâbin İki Adamın Kâinata Bakışı",
    durationSeconds: 765,
    excerptText:
      "İmanda ne kadar büyük bir saadet ve nimet ve ne kadar büyük bir lezzet ve rahat bulunduğunu anlamak istersen, şu temsilî hikâyeciğe bak, dinle: Bir vakit iki adam hem keyif, hem ticaret için seyahate giderler. Biri hodbin, tâlihsiz bir tarafa; diğeri hüdâbin, bahtiyar diğer tarafa sülûk ederler. Hodbin adam hem hodgâm, hem hodendiş, hem bedbin olduğundan, bedbinlik cezası olarak nazarında pek fena bir memlekete düşer. Bakar ki, her yerde âciz bîçareler, zorba müthiş adamların ellerinden ve tahribatlarından واويلا ediyorlar. Bütün gezdiği yerlerde böyle حزين elîm bir hali görür. Memleket ise umumî bir matemhane şeklini almış. Kendisi dahi şu elîm ve muzlim hâletten kurtulmak için sarhoşluktan başka çare bulamaz. Diğeri ise hüdâbin, hüdâperest ve hak-endiş ve güzel ahlâklı idi ki, nazarında pek güzel bir memlekete düştü. İşte bu bahtiyar adam girdiği yerlerde umumî bir şehrâyin görüyor. Her tarafta bir sürur, bir şenlik, bir cezbe ve neş'e içinde zikirhaneler... İşte ey nefsim! Bil ki, evvelki adam kâfirdir veya fâsık gâfildir. İkinci adam ise mü'mindir; Cenâb-ı Hakk'ı tanır ve tasdik eder.",
    keyTerms: [
      { term: "Hodbin", meaning: "Yalnız kendini gören, bencil" },
      { term: "Hüdâbin", meaning: "Rabbini gören, her şeyde İlahî sanatı müşahede eden" },
      { term: "Tevekkül", meaning: "Sebeplere riayet ettikten sonra Allah'a güvenip dayanmak" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p003-2.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p003-2.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "4.8 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 3,
    partLabel: "Üçüncü Söz",
    title: "İbadetin Hikmeti ve Büyük Ticareti",
    sectionSubtitle: "Yâ Eyyühennâsu'budû • İki Askerin Yolculuğu",
    durationSeconds: 790,
    excerptText:
      "İbadet ne kadar büyük bir ticaret ve saadet; fısk ve sefahet ne kadar büyük bir hasâret ve helâket olduğunu anlamak istersen, şu temsilî hikâyeciğe bak, dinle: Bir vakit iki asker, uzak bir şehre gitmek için emir alıyorlar. İki yol ayrılıncaya kadar beraber giderler. Yol başında bir adam bulunur, onlara der: Şu sağdaki yolun hiç zararı olmamakla beraber, on yolcudan dokuzu büyük kâr ve rahat görür. Soldaki yol ise menfaati olmamakla beraber, on yolcusundan dokuzu zarar görür. Hem ikisi, kısalık ve uzunlukta birdirler. Yalnız bir fark var ki, nizamsız, hükümet-i askeriye altında olmayan sol yolun yolcusu çantasız, silâhsız gider; zahirî bir hafiflik, yalancı bir serbestiyet görür. İntizam-ı askerî altındaki sağ yolun yolcusu ise, mugaddî hülâsalarla dolu dört okkalık bir çanta ve her adüvvü alt ve mağlûp edecek iki okkalık bir mükemmel mirî tüfeği taşımaya mecburdur...",
    keyTerms: [
      { term: "Hasâret", meaning: "Büyük zarar ve ziyan" },
      { term: "Mükellefiyet", meaning: "İlahî emirlerle sorumlu olma" },
      { term: "Ubudiyet", meaning: "Kulluk, Allah'a itaat ve ibadet" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p004-3.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p004-3.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "5.6 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 4,
    partLabel: "Dördüncü Söz",
    title: "Namaz Dinin Direğidir ve Kıymeti",
    sectionSubtitle: "Yirmi Dört Saatlik Ömür Sermayesinin En Kârlı Sarfı",
    durationSeconds: 695,
    excerptText:
      "Essalâtü imâdü'd-dîn. Namaz ne kadar kıymetli ve mühim, hem ne kadar ucuz ve az bir masraf ile kazanılır, hem namazsız adam ne kadar divane ve zararlı olduğunu, iki kere iki dört eder derecede kat'î anlamak istersen, şu temsilî hikâyeciğe bak, gör: Bir zaman bir büyük hâkim, iki hizmetkârını, her birisine yirmi dört altın verip, iki ay uzaklıkta güzel bir çiftliğine ikamet etmek için gönderiyor. Ve onlara emreder ki: Şu para ile yol ve bilet masrafını yapınız. Hem oradaki meskeninize lâzım bazı şeyleri mübayaa ediniz. Bir günlük mesafede bir istasyon vardır. Hem araba, hem gemi, hem şimendifer, hem tayyare bulunur. Sermayeye göre binilir...",
    keyTerms: [
      { term: "İmâdü'd-dîn", meaning: "Dinin direği ve temel dayanağı" },
      { term: "Bilet ve Harçlık", meaning: "Kabir kapısından ebedî saadete geçiş vesilesi" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p005-4.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p005-4.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "4.4 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 5,
    partLabel: "Beşinci Söz",
    title: "Namazı Eda Etmek ve Kebâiri Terk Etmek",
    sectionSubtitle: "İnsanın Vazife-i Hakikiyesi • İki Askerin İmtihanı",
    durationSeconds: 740,
    excerptText:
      "İnnallâhe maallezînettekav vellezîne hüm muhsinûn. Namazı kılmak ve büyük günahlardan kaçınmak ne derece hakiki bir vazife-i insaniye ve ne kadar fıtrî bir netice-i hilkat-i beşeriye olduğunu görmek istersen, şu temsilî hikâyeciğe bak, dinle: Seferberlikte bir taburda biri muallem, vazifeperver, diğeri acemi, nefsini sever iki asker beraber bulunuyordu. Gayet adil ve şefkatli bir kumandanları vardı. Onların erzak ve tayinatını ve hoşlarına giden her şeylerini bizzat gönderir, onları beslerdi; yalnız onlardan kendine mahsus iki hafif vazife isterdi: Biri nöbet tutmak ve talim etmek, diğeri de padişaha isyan ve kanuna muhalefet etmemekti...",
    keyTerms: [
      { term: "Kebâir", meaning: "Büyük günahlar" },
      { term: "Tayinat", meaning: "Askerin yiyecek ve geçim tahsisatı; Cenâb-ı Hakk'ın verdiği rızık" },
      { term: "Takvâ", meaning: "Günahlardan sakınıp İlahî emirlere sarılmak" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p006-5.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p006-5.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "5.1 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 6,
    partLabel: "Altıncı Söz",
    title: "Nefis ve Malı Cenâb-ı Hakk'a Satmak",
    sectionSubtitle: "İnnallâheşterâ Mine'l-Mü'minîne Enfüsehüm • Beş Kâr",
    durationSeconds: 1120,
    excerptText:
      "Nefis ve malını Cenâb-ı Hakk'a satmak ve O'na abd olmak ve asker olmak ne kadar kârlı bir ticaret, ne kadar şerefli bir rütbe olduğunu anlamak istersen, şu temsilî hikâyeciğe dinle: Bir zaman bir padişah, raiyetinden iki adama, her birisine emniyet ve imtihan için emanet olarak birer çiftlik verir ki, içinde fabrika, makine, at, silâh gibi her şey var...",
    keyTerms: [
      { term: "Emanet", meaning: "Allah'ın insana geçici olarak tevdi ettiği hayat, akıl ve duygular" },
      { term: "Fâni", meaning: "Geçici, zevale mahkûm olan" },
      { term: "Bâki", meaning: "Sonsuz, kalıcı, ebedî" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p007-6.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p007-6.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "8.2 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 7,
    partLabel: "Yedinci Söz",
    title: "Âmentü Billâhi ve Bi'l-Yevmi'l-Âhir • İki Muazzam Tılsım",
    sectionSubtitle: "Kâinatın Muammasını Açan Allah'a ve Ahirete İman Anahtarı",
    durationSeconds: 1080,
    excerptText:
      "Şu kâinatın muammâ-yı مغلقını açan ve tılsım-ı müşkil-küşâsını keşfeden ruh-u beşer için saadet kapısını açan Âmentü billâhi ve bi'l-yevmi'l-âhir kelime-i mübarekesi ne kadar ruh-u beşere medâr-ı saadet ve ne derece kalb-i insana menba-ı inşirah olduğunu anlamak istersen, şu temsilî hikâyeciğe bak, dinle: Bir zaman bir nefer, bir meydan-ı harpte ve imtihan mevkii olan bir hengâmda pek müthiş bir vaziyete düşer. Sağ ve sol iki cihetinden dehşetli iki derin yara ile yaralanmış ve arkasında cesîm bir arslan, ona saldırmaya bekliyor gibi duruyor; ve gözü önünde bir darağacı dikilmiş, bütün sevdiklerini asıp mahvediyor, onu da bekliyor. Hem bu haliyle beraber uzun bir sefer var, nefyediliyor...",
    keyTerms: [
      { term: "Tılsım-ı Müşkil-Küşâ", meaning: "Çözülmesi en zor düğümleri ve sırları açan anahtar" },
      { term: "Acz ve Fakr", meaning: "İnsanın sonsuz güçsüzlüğü ve İlahî rahmete muhtaçlığı" },
      { term: "Terhis Tezkeresi", meaning: "Ölümün mü'min için dünya nöbetinden azat belgesi olması" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p008-7.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p008-7.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "7.9 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 8,
    partLabel: "Sekizinci Söz",
    title: "Dünya ve Dinin Mahiyeti • Kuyu ve Güzel Bahçe Temsili",
    sectionSubtitle: "Allâhü Veliyyüllezîne Âmenû Yuhricühüm Mine'z-Zulumâti İle'n-Nûr",
    durationSeconds: 1180,
    excerptText:
      "Şu dünyanın ve世界で insanın ruhunun ve insanda dinin mahiyet ve kıymetlerini ve eğer doğru din olmazsa dünya bir zindan olması ve dinsiz insan en bedbaht mahlûk olduğunu ve şu kâinatın tılsımını açan ve ruh-u beşeri zulümattan kurtaran Yâ Allah ve Lâ ilâhe illallah olduğunu anlamak istersen, şu temsilî hikâyeciğe bak, dinle: Eski zamanda iki kardeş uzun bir seyahate beraber gidiyorlar. Git gide, tâ yol ikileşti. O iki yol başında ciddî bir adamı gördüler. Ondan sordular: Hangi yol iyidir? O zat onlara dedi ki: Sağ yolda kanun ve nizama tebaiyet mecburiyeti vardır; fakat o külfet içinde bir emniyet ve saadet vardır. Sol yolda ise serbestiyet ve hürriyet vardır; fakat o serbestiyet içinde bir tehlike ve şekavet vardır...",
    keyTerms: [
      { term: "Zulümat ve Nur", meaning: "Küfür karanlıkları ve iman aydınlığı" },
      { term: "Tılsım", meaning: "Sır perdesi, hikmetli şifre" },
      { term: "Şekavet", meaning: "Bedbahtlık ve manevî felaket" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p009-8.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p009-8.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "8.8 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 9,
    partLabel: "Dokuzuncu Söz",
    title: "Beş Vakit Namazın Vakitlere Tahsis Hikmeti",
    sectionSubtitle: "Sabah, Öğle, İkindi, Akşam ve Yatsı İnkılapları",
    durationSeconds: 1330,
    excerptText:
      "Fe-sübhânallâhi hîne tümsûne ve hîne tusbihûn. Ey birader! Namazın neden şu muayyen beş vakte tahsis edildiğinin hikmetini soruyorsun. Pek çok hikmetlerinden yalnız birisine işaret ederiz. Evet, her bir namazın vakti, mühim bir inkılâb başı olduğu gibi, büyük bir tasarruf-u İlâhînin âyinesi ve o tasarruf içinde ihsanat-ı külliyenin birer ma'kesi olduğundan, Kudret-i Sonsuz'a daha ziyade tesbih ve tazim emredilmiştir...",
    keyTerms: [
      { term: "Tasarruf-u İlâhî", meaning: "Allah'ın kâinat üzerindeki kudret ve idaresi" },
      { term: "İnkılâb", meaning: "Vakitlerin gece ve gündüz arasındaki büyük değişimi" },
      { term: "Tesbih ve Tazim", meaning: "Allah'ı kusurlardan tenzih edip yüceltmek" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p010-9.soz-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p010-9.soz-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "10.2 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 10,
    partLabel: "Onuncu Söz",
    title: "Haşir Risalesi • On İki Suret ve On İki Kat'î Hakikat",
    sectionSubtitle: "Bahar Haşrinin Numuneleri ve Esmâ-i Hüsnâ'nın Ahiret Âlemini İktiza Etmesi",
    durationSeconds: 2660,
    excerptText:
      "Fenzur ilâ âsâri rahmetillâhi keyfe yuhyi'l-arda ba'de mevtihâ. Şu yüksek hakikati görmek istersen, gel, şimdi bahar mevsimidir; Kudret-i Fâtıra'nın binler haşir numunelerini gösterdiği bu çiçekli sahraya çıkalım. Bak, kışta ölmüş olan yeryüzü, üç yüz bin nevi nebatat ve hayvanatıyla nasıl bir anda diriltiliyor. Birinci Hakikat: Bâb-ı Rububiyet ve Saltanat olup İsm-i Rab cilvesidir. Hiç mümkün müdür ki, rububiyet ve saltanat-ı mutlaka sahibi, kendine iman ve itaat eden dostlarını mükâfatsız bıraksın?",
    keyTerms: [
      { term: "Haşir", meaning: "Öldükten sonra ahirette tekrar diriltilip toplanma" },
      { term: "Âsâr-ı Rahmet", meaning: "İlahî rahmetin yeryüzündeki eserleri ve nakışları" },
      { term: "Esmâ-i Hüsnâ", meaning: "Cenâb-ı Hakk'ın güzel isimleri" },
      { term: "Dâr-ı Bekâ", meaning: "Sonsuzluk yurdu olan ahiret âlemi" },
    ],
    externalAudioUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p011-10.soz-p1-koseoglu.mp3",
    externalDownloadUrl:
      "https://rnknesriyat.com/kulliyatseslisayfalar/risalesesyeni/sozler/sozler-p011-10.soz-p1-koseoglu.mp3",
    archiveOrgUrl:
      "https://sorularlarisale.com/risale-dinle/sozler",
    fileSizeMb: "36.4 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 11,
    partLabel: "Yirmi Birinci Söz",
    title: "Namaz ve Vesvese Risalesi",
    sectionSubtitle: "Birinci Makam: Namaz İkazları • İkinci Makam: Vesvesenin İlacı",
    durationSeconds: 1840,
    excerptText:
      "Bir zaman sinnen, cismen, rütbeten büyük bir adam bana dedi ki: Namaz iyidir; fakat her gün her gün beşer defa kılmak çoktur, bitmediğinden usanç veriyor. O zatın o sözünden hayli zaman geçtikten sonra nefsimi dinledim; işittim ki, aynı sözleri söylüyor. Baktım ki, o nefsim, tenbellik kulağıyla şeytandan aynı dersi alıyor. O vakit anladım ki, o zat o sözü bütün nüfûs-u emmârenin namına söylemiş...",
    keyTerms: [
      { term: "Nefs-i Emmâre", meaning: "Kötülüğü ve tenbelliği emreden nefis mertebesi" },
      { term: "Vesvese", meaning: "Kalbe atılan asılsız kuruntu ve şüphe, ehemmiyet verdikçe büyür" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Sozler_21_YirmiBirinci_Soz.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "28.9 MB",
  },
  {
    bookSlug: "sozler",
    chapterNumber: 12,
    partLabel: "Yirmi Üçüncü Söz",
    title: "İman İnsanı İnsan Eder • Ahsen-i Takvim Sırrı",
    sectionSubtitle: "Birinci Mebhas: İmanınBeş Noktası • İkinci Mebhas: İnsanın Mahiyeti",
    durationSeconds: 1920,
    excerptText:
      "Lekad halakne'l-insâne fî ahseni takvîm. İman insanı insan eder, belki insanı sultan eder. Öyle ise insanın vazife-i asliyesi iman ve duadır. Küfür, insanı gayet âciz bir canavar hayvan eder. Bu meselenin binler delillerinden yalnız hayvan ve insanın dünyaya gelmelerindeki farkları o hakikate vazıh bir burhandır...",
    keyTerms: [
      { term: "Ahsen-i Takvim", meaning: "En güzel kıvam ve en mükemmel yaratılış" },
      { term: "Vazife-i Asliye", meaning: "İnsanın yaratılışındaki en temel gaye: iman ve dua" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Sozler_23_YirmiUcuncu_Soz.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "30.4 MB",
  },

  // ================= MEKTUBAT =================
  {
    bookSlug: "mektubat",
    chapterNumber: 1,
    partLabel: "Birinci Mektup",
    title: "Hayat Tabakaları ve Dört Mühim Sualin Cevabı",
    sectionSubtitle: "Hz. Hızır'ın Hayatı, Cehennem ve Muhabbet-i İlâhiye",
    durationSeconds: 1165,
    excerptText:
      "Bismihi Sübhânehû. Ve in min şey'in illâ yüsebbihu bihamdihî. Kardeşim Hulusi Bey'in suallerine muhtasar ve nurlu cevaplardır. Birinci Sual: Hazret-i Hızır hayatta mıdır? Hayatta ise niçin bazı mühim ulema hayatını kabul etmiyorlar? Elcevap: Hayattadır, fakat merâtib-i hayat beştir. O, ikinci mertebededir...",
    keyTerms: [
      { term: "Merâtib-i Hayat", meaning: "Hayatın beş farklı tabakası ve mertebesi" },
      { term: "Kayd-ı Beşeriyet", meaning: "İnsanın maddi beden ve zaman kayıtlarıyla sınırlı oluşu" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Mektubat_01_Birinci_Mektup.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "18.4 MB",
  },
  {
    bookSlug: "mektubat",
    chapterNumber: 2,
    partLabel: "İkinci Mektup",
    title: "İstiğna Düsturu ve İlmî Hizmetin İhlası",
    sectionSubtitle: "Hediye Kabul Etmemenin Altı Nüktesi",
    durationSeconds: 1000,
    excerptText:
      "Senin samimî mektubun ve temiz hediyen geldi. Bilirsin ki, seksen küsur seneden beri devam eden bir düstur-u hayatım var ki, halkların hediyelerini kabul etmiyorum. Çünkü ilim ve iman hizmeti yalnız Allah rızası için yapılır; maddi mukabele ve minnet altına girmek ihlası zedeler...",
    keyTerms: [
      { term: "İstiğna", meaning: "Gönül tokluğu, kimseden maddi beklenti içinde olmamak" },
      { term: "İhlas", meaning: "Amelde yalnız Allah'ın rızasını gözetmek" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Mektubat_02_Ikinci_Mektup.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "15.9 MB",
  },
  {
    bookSlug: "mektubat",
    chapterNumber: 3,
    partLabel: "On Dokuzuncu Mektup",
    title: "Mu'cizât-ı Ahmediye Risalesi (a.s.m.)",
    sectionSubtitle: "Resûl-i Ekrem'in (a.s.m.) Üç Yüzden Fazla Mucizesi",
    durationSeconds: 2530,
    excerptText:
      "Şu risale, üç yüz küsur mucizâtı beyan eder. Risalet-i Ahmediyenin (a.s.m.) mucizesini beyan ettiği gibi, kendisi de o mucizenin bir kerametidir. Kâinatın medâr-ı fahri ve nev-i beşerin yüz akı olan Zât-ı Ahmediye'nin (a.s.m.) nübüvvet delillerini on dokuz nükteli işaretle ders verir...",
    keyTerms: [
      { term: "Risalet-i Ahmediye", meaning: "Hz. Muhammed'in (a.s.m.) peygamberlik vazifesi" },
      { term: "İrhasat", meaning: "Peygamberlikten önce zuhur eden harika hadiseler" },
      { term: "Şakk-ı Kamer", meaning: "Ayın ikiye yarılması mucizesi" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Mektubat_19_Mucizati_Ahmediye.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "39.7 MB",
  },
  {
    bookSlug: "mektubat",
    chapterNumber: 4,
    partLabel: "Yirminci Mektup • Birinci Makam",
    title: "Kelime-i Tevhid'in On Bir Kelimesi ve Müjdesi",
    sectionSubtitle: "Lâ İlâhe İllallâhu Vahdehû Lâ Şerîke Lehû Mertebeleri",
    durationSeconds: 2210,
    excerptText:
      "Namaz tesbihatının âhirinde tekrar edilen şu tevhid kelâmının on bir kelimesinden her birisinde hem birer müjde, hem birer mertebe-i tevhid-i rububiyet, hem bir İsm-i Azam cilvesi vardır. Birinci Kelime: Lâ ilâhe illallah. Bunda, bütün kâinatta Mâbud-u Bilhak yalnız O olduğunu ilan eden büyük bir saadet müjdesi vardır...",
    keyTerms: [
      { term: "Mâbud-u Bilhak", meaning: "İbadete layık olan tek gerçek İlah" },
      { term: "Tevhid-i Rububiyet", meaning: "Bütün âlemlerin terbiye ve idaresinde Allah'ı bir bilmek" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Mektubat_20_Yirminci_Mektup_1.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "34.6 MB",
  },
  {
    bookSlug: "mektubat",
    chapterNumber: 5,
    partLabel: "Yirmi İkinci Mektup",
    title: "Uhuvvet Risalesi • Kardeşlik ve Muhabbet Düsturları",
    sectionSubtitle: "Mü'minlerde İتفاق, Nifak ve İhtilafın İlacı",
    durationSeconds: 1890,
    excerptText:
      "İnneme'l-mü'minûne ihvetün fe-aslihû beyne ehaveyküm. Mü'minlerde nifak ve şikak, kin ve adavete sebebiyet veren tarafgirlik ve inat ve haset, hakikatçe ve hikmetçe ve insaniyet-i kübra olan İslâmiyetçe ve hayat-ı şahsiyece ve hayat-ı içtimaiyece ve hayat-ı maneviyece çirkin ve merduttur, مضردir ve zulümdür...",
    keyTerms: [
      { term: "Uhuvvet", meaning: "İman kardeşliği, kalbî bağlılık" },
      { term: "Adâvet", meaning: "Düşmanlık; mü'mine karşı adâvet etmek bütün faziletlerini yok saymak gibidir" },
      { term: "İnsaniyet-i Kübrâ", meaning: "Büyük insanlık olan İslâmiyet" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Mektubat_22_Uhuvvet_Risalesi.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "29.5 MB",
  },

  // ================= LEM'ALAR =================
  {
    bookSlug: "lemalar",
    chapterNumber: 1,
    partLabel: "Birinci Lem'a",
    title: "Hz. Yunus (a.s.) Münâcâtı • Lâ İlâhe İllâ Ente Sübhâneke",
    sectionSubtitle: "Gece, Deniz, Balık ve Nefis Karanlıklarından Kurtuluş Sırrı",
    durationSeconds: 1065,
    excerptText:
      "Hazret-i Yunus İbn-i Mettâ Alâ Nebiyyinâ ve Aleyhissalâtü Vesselâm'ın münâcâtı, en azîm bir münâcâttır ve en mühim bir vesile-i icabe-i duadır. Hazret-i Yunus Aleyhisselâm'ın kıssa-i meşhuresinin hülâsası: Denize atılmış, büyük bir balık onu yutmuş. Deniz fırtınalı ve gece dağdağalı ve karanlık ve her taraftan ümit kesik bir vaziyette, Lâ ilâhe illâ ente sübhâneke innî küntü mine'z-zâlimîn مünâcâtı ona sür'aten vasıta-i necat olmuştur...",
    keyTerms: [
      { term: "Münâcât", meaning: "Kalbin en derin yakarışıyla Cenâb-ı Hakk'a yönelişi" },
      { term: "Vasıta-i Necat", meaning: "Kurtuluş vesilesi ve gemisi" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Lemalar_01_Birinci_Lema.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "16.8 MB",
  },
  {
    bookSlug: "lemalar",
    chapterNumber: 2,
    partLabel: "İkinci Lem'a",
    title: "Hz. Eyyüb (a.s.) Kıssası ve Manevî Hastalıklarımız",
    sectionSubtitle: "Sabır Kahramanı Hz. Eyyüb'ün Dua Sırrı ve Beş Nükte",
    durationSeconds: 1450,
    excerptText:
      "Sabır kahramanı Hazret-i Eyyüb Aleyhisselâm'ın şu münâcâtı, hem mücerreb, hem tesirlidir. Hazret-i Eyyüb Aleyhisselâm çok يارا ve bere içinde epeyi müddet kaldığı halde, o hastalığın azîm mükâfatını düşünerek kemal-i sabırla tahammül edip kalmış. Sonra yaralarından tevellüd eden kurtlar kalbine ve lisanına iliştiği zaman, zikir ve marifet-i İlâhiyenin mahalleri olan kalb ve lisan خللدار olacağı düşüncesiyle dua etmiştir...",
    keyTerms: [
      { term: "Marifet-i İlâhiye", meaning: "Cenâb-ı Hakk'ı isim ve sıfatlarıyla tanıma ilmi" },
      { term: "Ubudiyetin Ruhu", meaning: "İhlaslı zikir ve kalbî tefekkür" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Lemalar_02_Ikinci_Lema.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "22.7 MB",
  },
  {
    bookSlug: "lemalar",
    chapterNumber: 3,
    partLabel: "On Dokuzuncu Lem'a",
    title: "İktisat Risalesi • Nimetlere Şükür ve Bereketin Sırrı",
    sectionSubtitle: "Külû veşrebû velâ tüsrifû • Yedi Nüktede İktisat ve Kanaat",
    durationSeconds: 1720,
    excerptText:
      "Külû veşrebû velâ tüsrifû. Yiyiniz, içiniz; fakat israf etmeyiniz. Şu âyet-i kerime, iktisada kat'î emir ve israftan nehy-i sarih suretinde gayet mühim bir düstur-u hikmeti ders veriyor. Birinci Nükte: Hâlık-ı Rahîm, nev-i beşere verdiği nimetlerin mukabilinde şükür istiyor. İsraf ise şükre zıttır, nimete karşı hasâretli bir استخفافtır; İktisat ise nimete karşı ticaretli bir ihtiramdır...",
    keyTerms: [
      { term: "İktisat", meaning: "Nimete hürmet ve ölçülü kullanım; bereketin mühim vesilesi" },
      { term: "Kanaat", meaning: "Helal rızka razı olup izzetini muhafaza etmek" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Lemalar_19_Iktisat_Risalesi.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "27.1 MB",
  },
  {
    bookSlug: "lemalar",
    chapterNumber: 4,
    partLabel: "Yirmi Birinci Lem'a",
    title: "İhlas Risalesi • İhlası Kazanmanın Dört Düsturu",
    sectionSubtitle: "En Ehemmiyetli Bir Esas ve En Büyük Bir Kuvvet",
    durationSeconds: 1875,
    excerptText:
      "Bu Lem'a en az on beş günde bir defa okunmalı. Ey ahiret kardeşlerim ve ey hizmet-i Kur'aniyede arkadaşlarım! Bilirsiniz ve biliniz ki: Bu dünyada, hususan uhrevî hizmetlerde en mühim bir esas, en büyük bir kuvvet, en makbul bir şefaatçi, en metin bir nokta-i istinad, en kısa bir tarîk-ı hakikat, en makbul bir dua-yı manevî, en kerametli bir vesile-i makasıd, en yüksek bir haslet, en safi bir ubudiyet: İhlastır...",
    keyTerms: [
      { term: "Nokta-i İstinad", meaning: "Dayanak noktası" },
      { term: "Tarîk-ı Hakikat", meaning: "Hakikate ulaştıran en doğru ve kısa yol" },
      { term: "Tefani", meaning: "Kardeşlerinde fâni olma, benliği bırakıp biz olma sırrı" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Lemalar_21_Ihlas_Risalesi.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "29.4 MB",
  },
  {
    bookSlug: "lemalar",
    chapterNumber: 5,
    partLabel: "Yirmi Beşinci Lem'a",
    title: "Hastalar Risalesi • Yirmi Beş Manevî Deva",
    sectionSubtitle: "Musibetzedelerin ve Hastaların Manevî Eczahanesi",
    durationSeconds: 2670,
    excerptText:
      "Nev-i beşerin on kısmından birisini teşkil eden musibetzedelerin ve hastaların hakikî bir tesellisi ve نافع bir merhemi olmak niyetiyle bu Yirmi Beşinci Lem'a kaleme alınmıştır. Birinci Deva: Ey biçare hasta! Merak etme, sabret. Senin hastalığın sana dert değil, belki bir nevi dermandır. Çünkü ömür bir sermayedir, gidiyor. Meyvesi bulunmazsa zayi olur. Hastalık senin o sermayeni büyük kârlarla meyvedar ediyor...",
    keyTerms: [
      { term: "Şâfî-i Hakikî", meaning: "Gerçek şifa verici olan Cenâb-ı Hak" },
      { term: "Kefaretü'z-Zünûb", meaning: "Günahlara kefaret ve arınma vesilesi" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Lemalar_25_Hastalar_Risalesi.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "42.0 MB",
  },

  // ================= ŞUALAR =================
  {
    bookSlug: "sualar",
    chapterNumber: 1,
    partLabel: "Üçüncü Şua",
    title: "Münâcât Risalesi • Kâinatın Şehadetiyle Niyaz",
    sectionSubtitle: "Semâvat, Küre-i Arz ve Zîhayatların Lisanıyla Yakarış",
    durationSeconds: 2295,
    excerptText:
      "Yâ İlahî ve yâ Rabbî! Ben imanın gözüyle ve Kur'an'ın talimiyle ve Resûl-i Ekrem Aleyhissalâtü Vesselâm'ın dersiyle ve İsm-i Hakîm'in göstermesiyle görüyorum ki: Semâvatta hiçbir deveran ve hareket yoktur ki, böyle intizamıyla Senin mevcudiyetine işaret ve delâlet etmesin...",
    keyTerms: [
      { term: "Deveran", meaning: "Yıldızların ve gezegenlerin yörüngelerindeki intizamlı dönüşü" },
      { term: "Şehadet", meaning: "Varlıkların Yaratıcının birliğine şahitlik etmesi" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Sualar_03_Munacat_Risalesi.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "35.8 MB",
  },
  {
    bookSlug: "sualar",
    chapterNumber: 2,
    partLabel: "Yedinci Şua • Birinci Makam",
    title: "Âyetü'l-Kübrâ • Kâinattan Hâlıkını Soran Seyyah",
    sectionSubtitle: "On Dokuz Mertebede Kâinatın Tevhid Şehadeti",
    durationSeconds: 3150,
    excerptText:
      "Tüsebbihu lehü's-semâvâtü's-seb'u ve'l-ardu ve men fîhin. Kâinattan Hâlıkını soran bir seyyahın müşahedatıdır. Pek büyük ve küllî olan Âyetü'l-Kübrâ-yı Tevhid'i, göklerin ve yerin ve bütün mevcudatın dilleriyle okur; akla ve kalbe İman-ı Billâh'ın en yüksek mertebesini açar...",
    keyTerms: [
      { term: "Âyetü'l-Kübrâ", meaning: "Kâinat kitabının en büyük Tevhid delili" },
      { term: "Seyyah-ı Alem", meaning: "Varlık âlemlerini tefekkürle gezen akıl ve kalp yolcusu" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Sualar_07_Ayetul_Kubra.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "49.1 MB",
  },
  {
    bookSlug: "sualar",
    chapterNumber: 3,
    partLabel: "On Birinci Şua",
    title: "Meyve Risalesi • Denizli Medrese-i Yusufiyesi Dersleri",
    sectionSubtitle: "On Bir Meselede İman, Namaz, Gençlik ve Ahiret Hakikati",
    durationSeconds: 2715,
    excerptText:
      "Gençlik hiç şüphe yok ki gidecek. Yaz güze ve kışa yer vermesi ve gündüz akşama ve geceye değişmesi kat'iyetinde, gençlik dahi ihtiyarlığa ve ölüme değişecek. Eğer o fâni ve geçici gençliğini iffetle hayrata, استقامت dairesinde sarf etse, onunla ebedî, bâki bir gençliği kazanacağını bütün semavî fermanlar müjde veriyorlar...",
    keyTerms: [
      { term: "Medrese-i Yusufiye", meaning: "Sabır ve ilim ocağına dönüşen hapishane" },
      { term: "İstikamet", meaning: "İman ve ahlak üzere dosdoğru yaşamak" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Sualar_11_Meyve_Risalesi.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "42.5 MB",
  },

  // ================= ASÂ-YI MÛSA =================
  {
    bookSlug: "asa-yi-musa",
    chapterNumber: 1,
    partLabel: "Birinci Kısım • Altıncı Mesele",
    title: "Bize Hâlıkımızı Tanıttır • Kâinatın Büyük Kitabı",
    sectionSubtitle: "Muallimlerimiz Allah'tan Bahsetmiyor Diyen Gençlere Cevap",
    durationSeconds: 1730,
    excerptText:
      "Kastamonu'da lise talebelerinden bir kısmı yanıma geldiler: Bize Hâlıkımızı tanıttır, muallimlerimiz Allah'tan bahsetmiyorlar dediler. Ben dedim: Sizin okuduğunuz fenlerden her fen, kendi lisan-ı mahsusuyla mütemadiyen Allah'tan bahsedip Hâlıkı tanıttırıyorlar. Muallimleri değil, onları dinleyiniz...",
    keyTerms: [
      { term: "Lisan-ı Mahsus", meaning: "Her ilim dalının ve sanatın kendine özgü şahitlik dili" },
      { term: "Eczahane-i Kübrâ", meaning: "Yeryüzünün muazzam ve ölçülü bir İlahî eczahane oluşu" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/AsayiMusa_06_Altinci_Mesele.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "27.3 MB",
  },
  {
    bookSlug: "asa-yi-musa",
    chapterNumber: 2,
    partLabel: "İkinci Kısım • Onuncu Hüccet",
    title: "Fâtiha-i Şerife'nin İman Hüccetleri ve Nurları",
    sectionSubtitle: "Elhamdülillâhi Rabbi'l-Âlemîn'den Sırat-ı Müstakîm'e",
    durationSeconds: 1960,
    excerptText:
      "Fâtiha-i Şerife, Kur'an-ı Mucizü'l-Beyan'ın hülâsası ve çekirdeği olduğu gibi, namazın da ruhu ve kalbidir. Her bir âyeti, kâinat çapında Tevhid, Haşir ve Risalet hakikatlerine pencereler açar...",
    keyTerms: [
      { term: "Sırat-ı Müstakîm", meaning: "Enbiya, sıddıkîn, şüheda ve salihlerin yürüdüğü dosdoğru yol" },
      { term: "Hüccet-i İmaniye", meaning: "Şüphe bırakmayan iman delili" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/AsayiMusa_20_Fatiha_Nurlari.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "30.9 MB",
  },

  // ================= MESNEVÎ-İ NÛRİYE =================
  {
    bookSlug: "mesnevi-i-nuriye",
    chapterNumber: 1,
    partLabel: "Reşhalar",
    title: "Zât-ı Risâlet'in (a.s.m.) Marifetullah Güneşi",
    sectionSubtitle: "Ondokuz Reşhada Peygamber Efendimizin Nuraniyet Delilleri",
    durationSeconds: 1905,
    excerptText:
      "Kâinatın Hâlıkını bize tarif eden üç büyük küllî muarrif var: Birisi şu kitab-ı kâinattır, birisi Kur'an-ı Azîmüşşan'dır, diğeri ise bu kitab-ı kebîrin âyet-i kübrâsı olan Zât-ı Ahmediye Aleyhissalâtü Vesselâm'dır...",
    keyTerms: [
      { term: "Reşha", meaning: "Hakikat pınarından sızan nurlu damla" },
      { term: "Muarrif", meaning: "Tanıttırıcı, tarif ve izah eden rehber" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/MesneviiNuriye_01_Reshalar.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "29.8 MB",
  },
  {
    bookSlug: "mesnevi-i-nuriye",
    chapterNumber: 2,
    partLabel: "Katre Risalesi",
    title: "Bahr-i Tevhid'den Bir Katre • Dört Mesele",
    sectionSubtitle: "Sebepler Perdesi Arkasında Kudret-i Ezeliyenin Tasarrufu",
    durationSeconds: 2240,
    excerptText:
      "Bil ki, kâinatta her şeyde, her zerrede Cenâb-ı Hakk'ın vücub-u vücuduna ve vahdetine açılan pencereler vardır. Sebepler ancak Kudret-i İlahiyenin izzet ve azametine birer perdedir...",
    keyTerms: [
      { term: "Vücub-u Vücud", meaning: "Allah'ın varlığının zatından olup zorunlu ve ezeli olması" },
      { term: "İzzet ve Azamet", meaning: "İlahî yücelik ve kusursuzluk" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/MesneviiNuriye_02_Katre.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "35.1 MB",
  },

  // ================= İŞÂRÂTÜ'L-İ'CÂZ =================
  {
    bookSlug: "isaratul-icaz",
    chapterNumber: 1,
    partLabel: "Fâtiha-i Şerife Tefsiri",
    title: "Kur'an'ın Tarifi ve Fâtiha Sûresi Nazım İ'câzı",
    sectionSubtitle: "Kelimelerin Dizilişindeki Mucizevî Nizam ve Belâgat",
    durationSeconds: 2140,
    excerptText:
      "Kur'an nedir, tarifi nasıldır? Kur'an, şu kitab-ı kebîr-i kâinatın bir tercüme-i ezeliyesi ve âyât-ı tekviniyeyi okuyan muhtelif dillerinin tercüman-ı ebedîsi ve şu âlem-i gayb ve âlem-i şehadet kitabının müfessiridir...",
    keyTerms: [
      { term: "Nazm-ı Kur'an", meaning: "Kur'an âyet ve kelimelerinin eşsiz diziliş ahengi" },
      { term: "Âyât-ı Tekviniye", meaning: "Kâinatta yaratılmış olan kevnî âyetler ve deliller" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/IsaratulIcaz_01_Fatiha.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "33.5 MB",
  },
  {
    bookSlug: "isaratul-icaz",
    chapterNumber: 2,
    partLabel: "Tevhid'in İspatı",
    title: "Yâ Eyyühennâsu'budû Rabbeküm • İbadet ve Tevhid",
    sectionSubtitle: "Bakara Sûresi 21-22. Âyetlerin İ'câz Nükteleri",
    durationSeconds: 2365,
    excerptText:
      "Ey insanlar! Sizi ve sizden evvelkileri yaratan Rabbinize ibadet ediniz ki takva mertebesine eresiniz. O Rab ki, yeryüzünü size bir döşek, gökyüzünü bir kubbe yaptı...",
    keyTerms: [
      { term: "Takva", meaning: "Allah'ın emirlerine sarılıp yasaklarından sakınarak korunma" },
      { term: "Belâgat", meaning: "Sözün mukteza-yı hâle yani makam ve muhataba tam uygunluğu" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/IsaratulIcaz_02_Tevhid.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "37.0 MB",
  },

  // ================= BARLA LÂHİKASI =================
  {
    bookSlug: "barla-lahikasi",
    chapterNumber: 1,
    partLabel: "Mukaddime & İlk Mektuplar",
    title: "Barla Medrese-i Nuriye İklimi ve Hulusi Bey'in Fıkraları",
    sectionSubtitle: "İlk Nur Talebelerinin İhlas, Sadakat ve Tefekkür Notları",
    durationSeconds: 1780,
    excerptText:
      "Aziz, sıddık kardeşlerim! Sözler ve Nurlar hakkında yazdığınız mektuplar, bu zamanda iman hakikatlerine ne derece şiddetli bir ihtiyaç olduğunu ve Kur'an eczahanesinden çıkan bu nurlu devaların kalplere nasıl şifa verdiğini gösteriyor...",
    keyTerms: [
      { term: "Lâhika", meaning: "Külliyata eklenen hizmet mektupları ve düsturlar mecmuası" },
      { term: "Sıddıkıyet", meaning: "Sarsılmaz sadakat ve doğruluk" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/BarlaLahikasi_01_Mukaddime.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "27.9 MB",
  },

  // ================= TARİHÇE-İ HAYAT =================
  {
    bookSlug: "tarihce-i-hayat",
    chapterNumber: 1,
    partLabel: "Birinci Kısım • İlk Hayat",
    title: "Nurs Köyünden Barla'ya • İlim ve Tefekkür Yılları",
    sectionSubtitle: "Medrese Tahsili, Zehra Medresesi Mefkûresi ve Darü'l-Hikmet",
    durationSeconds: 2210,
    excerptText:
      "Bediüzzaman Said Nursî, Hicrî 1293 (Milâdî 1877) tarihinde Bitlis vilâyetine bağlı Hizan kazasının İsparit nahiyesinin Nurs köyünde dünyaya gelmiştir. Küçük yaşta başladığı medrese tahsilini harika bir zekâ ve hafıza ile kısa sürede ikmal ederek, fen ilimleriyle din ilimlerinin birlikte okutulacağı Medresetü'z-Zehra mefkûresini hayatının gayesi edinmiştir...",
    keyTerms: [
      { term: "Medresetü'z-Zehra", meaning: "Din ilimleri ile fen ilimlerinin mezcedilerek okutulacağı ilim merkezi projesi" },
      { term: "Darü'l-Hikmeti'l-İslâmiye", meaning: "Osmanlı döneminde kurulan en yüksek İslâmî ilim akademisi" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/TarihceiHayat_01_Ilk_Hayat.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "34.6 MB",
  },
  {
    bookSlug: "tarihce-i-hayat",
    chapterNumber: 2,
    partLabel: "İkinci Kısım • Barla Hayatı",
    title: "Barla'da Çınar Ağacı ve İlk Risalelerin Telifi",
    sectionSubtitle: "Haşir Risalesi ve Sözler'in Kaleme Alındığı Nurlu Menzil",
    durationSeconds: 2480,
    excerptText:
      "Barla, Risale-i Nur'un ilk dershanesi ve Kur'an nurlarının neşredildiği mübarek bir menzildir. Çam Dağı'nda ve Barla bağlarında tefekkürle kaleme alınan Sözler ve Mektubat, elden ele yazılarak Anadolu'nun dört bir yanına iman meşalesi olarak yayılmıştır...",
    keyTerms: [
      { term: "Menzil", meaning: "Konaklama ve tefekkür yeri" },
      { term: "İntişar", meaning: "Hakikatlerin kalpten kalbe yayılması" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/TarihceiHayat_02_Barla_Hayati.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "38.9 MB",
  },

  // ================= KASTAMONU LÂHİKASI =================
  {
    bookSlug: "kastamonu-lahikasi",
    chapterNumber: 1,
    partLabel: "Kastamonu Mektupları • 1. Kısım",
    title: "Huzur-u Daimî, Takva ve İhlasla Hizmet Düsturları",
    sectionSubtitle: "Tahribat-ı Maneviyeye Karşı Takva Kalesi ve Salih Amel",
    durationSeconds: 1840,
    excerptText:
      "Aziz, sıddık kardeşlerim! Bu zamanda tahribat ve menfî cereyan dehşetlidir; buna karşı en büyük siper ve kale takvadır. Günahlardan çekinmek ve farzları eda etmekle beraber, iman hakikatlerini tahkîkî bir surette kalbe yerleştirmek, yüzlerce günahın hücumuna karşı ruhu muhafaza eder. Risale-i Nur şakirtleri, şahs-ı manevînin kuvvetiyle ve ihlas sırrıyla birbirinin fazilet ve amellerine hissedar olur.",
    keyTerms: [
      { term: "Takva", meaning: "Günahlardan sakınarak İlahî himayeye girme" },
      { term: "Şahs-ı Manevî", meaning: "İhlas ve kardeşlikle kenetlenen topluluğun ortak manevi ruhu" },
      { term: "Tahkîkî İman", meaning: "Delil ve tefekkürle sarsılmaz hale gelen iman" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/KastamonuLahikasi_01.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "28.4 MB",
  },

  // ================= EMİRDAĞ LÂHİKASI =================
  {
    bookSlug: "emirdag-lahikasi",
    chapterNumber: 1,
    partLabel: "Emirdağ Mektupları • Müsbet Hareket",
    title: "Müsbet Hareket, İhlas ve Vazife-i İlahiyeye Karışmamak",
    sectionSubtitle: "Nur Hizmetinin İçtimaî Düsturları ve Vefat Öncesi Son Ders",
    durationSeconds: 2120,
    excerptText:
      "Aziz, sıddık kardeşlerim! Bizim vazifemiz müsbet hareket etmektir; menfî hareket değildir. Rıza-yı İlahîye göre sırf hizmet-i imaniyeyi yapmaktır, vazife-i İlahiyeye karışmamaktır. Bizler asayişi muhafazayı netice veren müsbet iman hizmeti içinde her bir sıkıntıya karşı sabırla, şükürle mükellefiz.",
    keyTerms: [
      { term: "Müsbet Hareket", meaning: "Yıkıcı olmadan, kendi iman vazifesini ihlasla yapma düsturu" },
      { term: "Rıza-yı İlâhî", meaning: "Cenâb-ı Hakk'ın hoşnutluğu" },
      { term: "Teslimiyet", meaning: "İlahî takdire ve hikmete gönül huzuruyla razı olma" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/EmirdagLahikasi_01.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "32.1 MB",
  },

  // ================= SİKKE-İ TASDİK-İ GAYBÎ =================
  {
    bookSlug: "sikke-i-tasdik-i-gaybi",
    chapterNumber: 1,
    partLabel: "Birinci Şua & Tevafukat Sırları",
    title: "İşârât-ı Kur'aniye ve Risale-i Nur'un Gaybî Tasdikleri",
    sectionSubtitle: "Âyet-i Nur ve Evliya-i İzâmın İشارetleriyle Manevî Müjdeler",
    durationSeconds: 1950,
    excerptText:
      "Kur'an-ı Mucizü'l-Beyan, her asra ve her tabakaya hitap ettiği gibi, bu asırda iman hakikatlerine en ziyade muhtaç olan ehl-i imana dahi hususi işârât ve remizleriyle teselli ve kuvvet veriyor. Lafzullah ve Rab kelimelerindeki harika tevafuklar, tesadüf ihtimalini kökten kesen birer Sikke-i Tasdik-i Gaybîdir.",
    keyTerms: [
      { term: "Tevafuk", meaning: "Tesadüfe verilemeyecek hikmetli ve ince uygunluk" },
      { term: "Sikke-i Tasdik-i Gaybî", meaning: "Gaybî doğrulama ve kabul mührü" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/SikkeiTasdik_01.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "29.5 MB",
  },

  // ================= MUHÂKEMAT =================
  {
    bookSlug: "muhakemat",
    chapterNumber: 1,
    partLabel: "Birinci Makale • Unsuru'l-Hakikat",
    title: "On İki Mukaddeme ile Hakikat ve Tefsir Usulü",
    sectionSubtitle: "İslâmiyet ve Fünun-u Müsbetenin İmtizacı, Belâgat ve Akîde",
    durationSeconds: 2340,
    excerptText:
      "Bil ki, İslâmiyet fünun-u hakikiyenin seyyidi ve mürşidi ve ulûm-u âliyenin reis ve pederidir. Vicdanın ziyası ulûm-u diniyedir; aklın nuru fünun-u medeniyedir. İkisinin tecellisiyle hakikat tecelli eder. İsrâiliyat ve zahirperestlik perdelerini kaldırıp Kur'an'ın elmas hakikatlerini asrın idrakine göstermek elzemdir.",
    keyTerms: [
      { term: "Muhâkemat", meaning: "Akıl ve mantık ölçüleriyle yapılan muhakemeler" },
      { term: "Belâgat", meaning: "Sözün makam ve muhatabın hâline tam uygunluğu" },
      { term: "Mihenk", meaning: "Hakkı batıldan, altını bakırdan ayıran ölçü taşı" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Muhakemat_01.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "35.4 MB",
  },

  // ================= İMAN VE KÜFÜR MUVAZENELERİ =================
  {
    bookSlug: "iman-ve-kufur-muvazeneleri",
    chapterNumber: 1,
    partLabel: "Temsilî Muvazeneler • Hidayet ve Dalâlet",
    title: "İmanın Manevî Cennet Çekirdeği, Küfrün Manevî Cehennem Oluşu",
    sectionSubtitle: "İkinci, Üçüncü, Sekizinci Söz ve Gençlik Rehberi Muvazeneleri",
    durationSeconds: 1890,
    excerptText:
      "İman, yalnız ahirette değil, şu dünyada dahi insana manevî bir Cennet temin eder; küfür ve dalâlet ise şu dünya hayatını dahi elîm bir zindana çevirir. Çünkü iman, geçmiş zamanı ve gelecek zamanı nurlandırır; ölümü dostlara kavuşma vesilesi olan bir terhis tezkeresi yapar.",
    keyTerms: [
      { term: "Muvazene", meaning: "Karşılaştırma, iman ile küfrün akıl ve kalp terazisinde tartılması" },
      { term: "Saadet-i Dareyn", meaning: "Dünya ve ahiret mutluluğu" },
      { term: "Dalâlet", meaning: "Hak yoldan sapma ve manevî karanlık" },
    ],
    externalAudioUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224/Muvazeneler_01.mp3",
    externalDownloadUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    archiveOrgUrl:
      "https://archive.org/details/podcast_isaratl-icaz-risale-i-nur_1016441224",
    fileSizeMb: "28.9 MB",
  },
];

export function formatDuration(seconds: number): string {
  const safe = Math.max(0, Math.floor(seconds));
  const hrs = Math.floor(safe / 3600);
  const mins = Math.floor((safe % 3600) / 60);
  const secs = safe % 60;
  if (hrs > 0) {
    return `${hrs}:${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
  }
  return `${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
}

export function formatHoursMinutes(seconds: number): string {
  const safe = Math.max(0, Math.floor(seconds));
  const hrs = Math.floor(safe / 3600);
  const mins = Math.floor((safe % 3600) / 60);
  if (hrs > 0) {
    return `${hrs} sa ${mins} dk`;
  }
  return `${mins} dk`;
}
