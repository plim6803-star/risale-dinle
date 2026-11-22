export interface KonuFihristiItem {
  id: string;
  topic: string;
  category: string;
  summary: string;
  references: {
    book: string;
    section: string;
    note: string;
  }[];
}

export interface MuskilSualItem {
  number: number;
  question: string;
  sourceRef: string;
  shortAnswer: string;
  fullExcerpt: string;
  bookSlug: string;
}

export interface NamazDersiItem {
  id: string;
  title: string;
  source: string;
  coreMessage: string;
  excerpt: string;
  keyPoints: string[];
}

export interface PdfResourceItem {
  id: string;
  title: string;
  subtitle: string;
  category: "kulliyat" | "fihrist" | "osmanlica" | "biyografi";
  pdfUrl: string;
  pageInfo: string;
}

export const BOOK_PDF_MAP: Record<string, string> = {
  "sozler": "/kaynaklar/Risale-i Nur Külliyat/sozler.pdf",
  "mektubat": "/kaynaklar/Risale-i Nur Külliyat/mektubat.pdf",
  "lemalar": "/kaynaklar/Risale-i Nur Külliyat/lemalar.pdf",
  "sualar": "/kaynaklar/Risale-i Nur Külliyat/sualar.pdf",
  "asa-yi-musa": "/kaynaklar/Risale-i Nur Külliyat/asa-yi-musa.pdf",
  "mesnevi-i-nuriye": "/kaynaklar/Risale-i Nur Külliyat/mesnevi-i-nuriye.pdf",
  "isaratul-icaz": "/kaynaklar/Risale-i Nur Külliyat/isaratul-icaz.pdf",
  "barla-lahikasi": "/kaynaklar/Risale-i Nur Külliyat/barla-lahikasi.pdf",
  "kastamonu-lahikasi": "/kaynaklar/Risale-i Nur Külliyat/kastamonu-lahikasi.pdf",
  "emirdag-lahikasi": "/kaynaklar/Risale-i Nur Külliyat/emirdag-lahikasi.pdf",
  "tarihce-i-hayat": "/kaynaklar/Risale-i Nur Külliyat/tarihce-i-hayat.pdf",
  "sikke-i-tasdik-i-gaybi": "/kaynaklar/Risale-i Nur Külliyat/sikke-i-tasdik-i-gaybi.pdf",
  "muhakemat": "/kaynaklar/Risale-i Nur Külliyat/muhakemat.pdf",
  "iman-ve-kufur-muvazeneleri": "/kaynaklar/Risale-i Nur Külliyat/iman-ve-kufur-muvazeneleri.pdf",
};

export const KONU_FIHRISTI_DATA: KonuFihristiItem[] = [
  {
    id: "allah-iman",
    topic: "Allah'a İman, Tevhid ve Marifetullah",
    category: "İman Hakikatleri",
    summary: "Cenâb-ı Hakk'ın varlığı, birliği, Esmâ-i Hüsnâ tecellileri ve kâinat kitabındaki Tevhid burhanları.",
    references: [
      { book: "Sözler", section: "22. Söz (İki Makam)", note: "Her şeyde Tevhid sikkesini gösteren 12 burhan ve Lem'a" },
      { book: "Şualar", section: "7. Şua (Âyetü'l-Kübrâ)", note: "Kâinattan Hâlıkını soran seyyahın 19 mertebede müşahedeleri" },
      { book: "Sözler", section: "32. Söz (Üç Mevkıf)", note: "Zerrelerden yıldızlara kadar şirkin imkânsızlığı ve muhabbetullah" },
      { book: "Lem'alar", section: "23. Lem'a (Tabiat Risalesi)", note: "Tabiat ve tesadüf fikrinin 9 muhal ile çürütülmesi" },
      { book: "Mektubat", section: "20. Mektup", note: "Kelime-i Tevhid'in 11 kelimesinde 11 müjde ve mertebe" }
    ]
  },
  {
    id: "hasir-ahiret",
    topic: "Haşir, Ahirete İman ve Beka-i Ruh",
    category: "İman Hakikatleri",
    summary: "Öldükten sonra dirilmenin akli, nakli ve kevnî delilleri, bahar haşrinin binler nümunesi.",
    references: [
      { book: "Sözler", section: "10. Söz (Haşir Risalesi)", note: "12 Suret ve 12 Hakikat ile Esmâ-i İlahiyeden Haşrin ispatı" },
      { book: "Sözler", section: "29. Söz", note: "Beka-i Ruh, Melâike ve Haşir-i Cismanînin kat'î ispatı" },
      { book: "Şualar", section: "9. Şua & 11. Şua (Meyve Risalesi)", note: "Ölümü idam-ı ebediden terhis tezkeresine çeviren hakikatler" },
      { book: "Sözler", section: "28. Söz", note: "Cennet ve ebedî saadet menzillerinin hakikatleri" }
    ]
  },
  {
    id: "namaz-ibadet",
    topic: "Namaz, İbadet ve Ubudiyetin Hikmeti",
    category: "İbadet & Namaz",
    summary: "Beş vakit namazın vakitlerindeki hikmetler, ibadetin ruhun gıdası ve en büyük ticaret oluşu.",
    references: [
      { book: "Sözler", section: "4. Söz", note: "24 saatlik ömür sermayesinden 1 saati namaza vermenin kârı" },
      { book: "Sözler", section: "9. Söz", note: "Namazın 5 vakte tahsis edilmesinin 5 nüktede izahı" },
      { book: "Sözler", section: "21. Söz (Birinci Makam)", note: "Namaza gelen usanç ve gaflete karşı 5 nefis ikazı" },
      { book: "Sözler", section: "11. Söz", note: "Kâinat sarayında insanın ubudiyet ve ilan-ı sanat vazifesi" }
    ]
  },
  {
    id: "peygamberlik-kur-an",
    topic: "Nübüvvet, Mu'cizât-ı Ahmediye ve İ'câz-ı Kur'an",
    category: "Kur'an & Risalet",
    summary: "Hz. Muhammed'in (a.s.m.) risalet delilleri ve Kur'an-ı Kerim'in kırk vecihle mucize oluşu.",
    references: [
      { book: "Mektubat", section: "19. Mektup (Mu'cizât-ı Ahmediye)", note: "300'den fazla mucizenin tevatürle beyanı" },
      { book: "Sözler", section: "25. Söz (Mu'cizât-ı Kur'aniye)", note: "Kur'an'ın belâgat, gaybî ihbar ve gençliğini koruma mucizesi" },
      { book: "İşârâtü'l-İ'câz", section: "Fâtiha ve Bakara Tefsiri", note: "Nazm-ı Kur'an'daki kelime ve harf diziliş mucizesi" },
      { book: "Sözler", section: "19. Söz", note: "Reşhalar ile Zât-ı Ahmediye'nin (a.s.m.) hakkaniyeti" }
    ]
  },
  {
    id: "kader-cuzi-irade",
    topic: "Kader, Kaza ve Cüz'î İrade",
    category: "İman Hakikatleri",
    summary: "Kader ile cüz'î iradenin telifi, hayır ve şerrin yaratılışı ve kadere imanın ruhu kederden kurtarması.",
    references: [
      { book: "Sözler", section: "26. Söz (Kader Risalesi)", note: "Kader ve cüz'î ihtiyariyenin iki makamda benzersiz izahı" },
      { book: "Mektubat", section: "12. Mektup", note: "Hz. Âdem'in Cennetten ihracı ve şerrin yaratılmasındaki küllî hayırlar" },
      { book: "Lem'alar", section: "2. Lem'a & 25. Lem'a", note: "Musibet ve hastalıklarda kaderin rahmet ve adalet yüzü" }
    ]
  },
  {
    id: "ihlas-uhuvvet",
    topic: "İhlas, Uhuvvet, İktisat ve Hizmet Düsturları",
    category: "Ahlâk & İçtimaiyat",
    summary: "Amellerin ruhu olan ihlası kazanma yolları, müminler arası kardeşlik ve kanaat düsturları.",
    references: [
      { book: "Lem'alar", section: "20. ve 21. Lem'a (İhlas Risaleleri)", note: "İhlası kıran engeller ve ihlasın 4 temel düsturu" },
      { book: "Mektubat", section: "22. Mektup (Uhuvvet Risalesi)", note: "Adavet ve tarafgirliği bitiren 6 vecih kardeşlik düsturu" },
      { book: "Lem'alar", section: "19. Lem'a (İktisat Risalesi)", note: "İktisadın bereket ve izzet sebebi oluşu (7 Nükte)" },
      { book: "Kastamonu & Emirdağ Lâhikaları", section: "Hizmet Rehberi", note: "Müsbet hareket, şahs-ı manevî ve tefani sırrı" }
    ]
  },
  {
    id: "hastalik-vesvese-dua",
    topic: "Hastalık, Musibet, Vesvese ve Dua",
    category: "Ruhiyat & Şifa",
    summary: "Hastalara yirmi beş deva, kalbe gelen vesvesenin mahiyeti ve duanın ubudiyet sırrı.",
    references: [
      { book: "Lem'alar", section: "25. Lem'a (Hastalar Risalesi)", note: "Hastalığın günahlara kefaret ve manevi terakki oluşu (25 Deva)" },
      { book: "Sözler", section: "21. Söz (İkinci Makam)", note: "Vesvese hastalığının 5 yarasını iyileştiren 5 merhem" },
      { book: "Sözler", section: "23. Söz", note: "İmanın insanı sultan kılması ve duanın kabul şartları" },
      { book: "Mektubat", section: "24. Mektup (Birinci Zeyl)", note: "Beş noktada duanın çeşitleri ve hikmeti" }
    ]
  }
];

export const MUSKIL_SUALLER_DATA: MuskilSualItem[] = [
  {
    number: 1,
    question: "Neden kâinattaki bütün varlıklar 'Bismillâh' der ve Besmele nasıl tükenmez bir kuvvet olur?",
    sourceRef: "Sözler • Birinci Söz",
    shortAnswer: "Her varlık kendi aczini ve küçüklüğünü bilip Sonsuz Kudret Sahibinin namıyla hareket ettiği için, zerre kadar bir tohum koca bir ağacı taşır.",
    fullExcerpt: "Evet, bu kelâmı söyleyen adam, bütün mevcudata karşı bir intisap peyda eder. Her şey, Cenab-ı Hakk'ın namına hareket ettiğini görür. Bir tek çekirdek, Bismillah der; koca bir çam ağacı gibi bir yükü omuzuna alır. İncecik bir kök, Bismillah der; sert taşları ve kayaları deler geçer.",
    bookSlug: "sozler"
  },
  {
    number: 2,
    question: "İbadet neden bir mükâfatın mukaddemesi değil, geçmiş nimetlerin şükrüdür?",
    sourceRef: "Sözler • Yirmi Dördüncü Söz & İkinci Lem'a",
    shortAnswer: "Bize hayat, akıl, göz, kulak ve iman gibi paha biçilmez nimetler peşin verilmiştir; ibadet bu verilmiş nimetlerin şükrüdür.",
    fullExcerpt: "Ey nefis! İbadet, mukaddeme-i mükâfat-ı lâhika değil, belki netice-i nimet-i sâbıkadır. Evet, biz ücretimizi almışız; ona göre hizmetle ve ubudiyetle mükellefiz. Çünkü Cenab-ı Hak, sana adem-i sırftan vücud giydirdi, taş yapmadı, hayvan yapmadı, insan yaptı ve Müslüman kıldı.",
    bookSlug: "lemalar"
  },
  {
    number: 3,
    question: "Dualarımız neden bazen aynen istediğimiz gibi kabul olmuyor? 'Dua ettim kabul olmadı' denilebilir mi?",
    sourceRef: "Sözler • Yirmi Üçüncü Söz (Birinci Mebhas)",
    shortAnswer: "İcabat başka, kabul başkadır. Her dua için işitmek ve cevap vermek vardır; fakat Hakîm-i Mutlak kulun istediğini ya aynen verir ya da ahireti için daha hayırlısını ihsan eder.",
    fullExcerpt: "Cevap vermek ayrıdır, kabul etmek ayrıdır. Her dua için cevap vermek var; fakat kabul etmek, hem ayn-ı matlûbu vermek Cenab-ı Hakk'ın hikmetine tâbidir. Meselâ, hasta bir çocuk çağırır: 'Ya hekim, bana bak!' Hekim 'Lebbeyk' der, cevap verir. Çocuk 'Şu ilacı bana ver' der; hekim ise çocuğun maslahatına binaen ya istediğini verir yahut daha iyisini verir.",
    bookSlug: "sozler"
  },
  {
    number: 4,
    question: "Kalbe gelen çirkin hayaller ve şüpheler (vesvese) imana ve ibadete zarar verir mi?",
    sourceRef: "Sözler • Yirmi Birinci Söz (İkinci Makam)",
    shortAnswer: "Tahayyül-ü küfür küfür olmadığı gibi, tedai-i efkâr (çağrışım) ile kalbe gelen vesveseler de kalbin malı değildir; üzülmek bizzat imanın kuvvetine delildir.",
    fullExcerpt: "Ey maraz-ı vesvese ile mübtela! Bilir misin vesvesen neye benzer? Musibete benzer. Ehemmiyet verdikçe şişer, ehemmiyet vermezsen söner. Ona büyük nazarıyla baksan büyür; küçük görsen küçülür. Korksan ağırlaşır, hasta eder; havf etmezsen hafifleşir, mahfî kalır.",
    bookSlug: "sozler"
  },
  {
    number: 5,
    question: "Beş vakit namaz neden özellikle bu beş vakitte farz kılınmıştır?",
    sourceRef: "Sözler • Dokuzuncu Söz",
    shortAnswer: "Her bir namaz vakti, gece-gündüz ve ömür-kıyamet gibi büyük bir inkılâbın başıdır ve Kudret-i İlahiyenin küllî tasarrufunu hatırlatır.",
    fullExcerpt: "Her bir namazın vakti, mühim bir inkılâb başı olduğu gibi, azîm bir tasarruf-u İlahînin âyinesi ve o tasarruf içinde ihsanat-ı külliyenin birer ma'kesi olduğundan, Kudret-i Samedaniyeye daha ziyade tesbih ve tazim ve o vakitler arasında birikmiş nimetlere şükür ve hamd emredilmiştir.",
    bookSlug: "sozler"
  },
  {
    number: 6,
    question: "Kâinatta hastalıklar, musibetler ve çirkin görünen şeyler neden yaratılmıştır?",
    sourceRef: "Lem'alar • Yirmi Beşinci Lem'a & Mektubat • On İkinci Mektup",
    shortAnswer: "Her şey zıddıyla bilinir; hastalık olmasa sıhhatin lezzeti ve Şâfî isminin tecellisi anlaşılmaz, musibetler ise ruhun madenini saflaştırır.",
    fullExcerpt: "Yeknesak istirahat döşeğindeki hayat, hayr-ı mahz olan vücuddan ziyade, şerr-i mahz olan ademe يقينdir ve ona gider. Hastalık ise, o mühim sermayen olan ömrünü büyük bir kâr ile meyvedar ediyor; her bir dakikası bir gün ibadet hükmüne geçebiliyor.",
    bookSlug: "lemalar"
  }
];

export const NAMAZ_DERSLERI_DATA: NamazDersiItem[] = [
  {
    id: "ders-1",
    title: "1. Ders: İmanın ve İbadetin Özü (17. Lem'a • 12. Nota)",
    source: "Lem'alar • On Yedinci Lem'a",
    coreMessage: "Kendi nefsini muhatap alarak marifetullah, muhabbetullah ve ubudiyetin bir ömür boyu süren hakikatini tefekkür etmek.",
    excerpt: "Ey bu notaları dinleyen dostlarım! Biliniz ki, ben dünyaya aldanıp ahireti unutan nefsimi uyandırmak için bu hakikatleri kaleme aldım. Kimin nefsi bu derse muhtaç ise, o da benimle beraber dinlesin.",
    keyPoints: [
      "Her varlık Mana-yı Harfî ile Sanatkârını tanıtır.",
      "Fâni lezzetler ancak Bâki-i Hakikî'nin namıyla ebedîleşir.",
      "İnsanın en büyük sermayesi acz ve fakrını şefaatçi yapmaktır."
    ]
  },
  {
    id: "ders-2",
    title: "2. Ders: Namazsızlığın Divaneliği ve 24 Altın Temsili",
    source: "Sözler • Dördüncü Söz",
    coreMessage: "Günlük 24 saatlik ömür sermayesinden yalnız 1 saatini beş vakit namaza ayırmanın ebedî hayatı kazandıran elmas anahtar oluşu.",
    excerpt: "Yirmi dört saatlik bir ömür sermayesinden yalnız bir saatini beş vakit namaza sarf etmek; bâki bir hayat-ı ebediyeyi kazandırmakla beraber, diğer yirmi üç dünyevî saati de sadaka ve ibadet hükmüne geçirir.",
    keyPoints: [
      "24 altın = 24 saatlik günlük ömür sermayesi.",
      "1 altın masraf = Abdestle beraber 5 vakit namaza giden 1 saat.",
      "İyi niyetle yapılan dünyevî işler namaz sayesinde ibadete dönüşür."
    ]
  },
  {
    id: "ders-3",
    title: "3. Ders: İhlasın Dört Esası ve Uhuvvet Sırrı",
    source: "Lem'alar • Yirmi Birinci Lem'a",
    coreMessage: "Amellerde yalnız Rıza-yı İlahî'yi esas tutmak ve kardeşlerinin faziletleriyle iftihar etmek.",
    excerpt: "Amelinizde rıza-yı İlahî olmalı. Eğer O razı olsa, bütün dünya küsse ehemmiyeti yok. Eğer O kabul etse, bütün halk reddetse tesiri yok.",
    keyPoints: [
      "1. Düstur: Amelde yalnız Rıza-yı İlahî'yi gözetmek.",
      "2. Düstur: Kardeşlerini tenkit etmemek ve gıpta damarını tahrik etmemek.",
      "3. Düstur: Bütün kuvvetini ihlasta ve hakta bilmek.",
      "4. Düstur: Kardeşlerinin meziyetlerini kendi şahsında gibi sevmek (Tefani)."
    ]
  },
  {
    id: "ders-4",
    title: "4. Ders: Fâtiha-i Şerife ve Teşehhüddeki Küllî İbadet",
    source: "Şualar • Altıncı Şua & Asâ-yı Mûsa",
    coreMessage: "Tahiyyat'ta bütün mahlûkatın ibadetlerini, hayat hediyelerini ve tesbihatlarını Cenâb-ı Hakk'a takdim etme şuuru.",
    excerpt: "Ettehiyyâtü lillâhi... Namazda teşehhüde oturan mümin, bütün zîhayatın hayatlarıyla gösterdikleri fıtrî ibadetleri kendi namına Rabbine takdim eder.",
    keyPoints: [
      "Tahiyyat: Bütün canlıların hayat hediyeleri.",
      "Salâvat: Bütün zîruhların fıtrî duaları.",
      "Tayyibat: Bütün kâmil insanların nurlu tesbihatları."
    ]
  }
];

export const PDF_LIBRARY_RESOURCES: PdfResourceItem[] = [
  { id: "pdf-sozler", title: "Sözler (Tam Kitap)", subtitle: "Risale-i Nur Külliyatı • 1. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/sozler.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-mektubat", title: "Mektubat (Tam Kitap)", subtitle: "Risale-i Nur Külliyatı • 2. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/mektubat.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-lemalar", title: "Lem'alar (Tam Kitap)", subtitle: "Risale-i Nur Külliyatı • 3. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/lemalar.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-sualar", title: "Şualar (Tam Kitap)", subtitle: "Risale-i Nur Külliyatı • 4. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/sualar.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-asayimusa", title: "Asâ-yı Mûsa (Tam Kitap)", subtitle: "Risale-i Nur Külliyatı • 5. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/asa-yi-musa.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-mesnevi", title: "Mesnevî-i Nûriye", subtitle: "Risale-i Nur Külliyatı • 6. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/mesnevi-i-nuriye.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-isarat", title: "İşârâtü'l-İ'câz", subtitle: "Risale-i Nur Külliyatı • 7. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/isaratul-icaz.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-barla", title: "Barla Lâhikası", subtitle: "Risale-i Nur Külliyatı • 8. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/barla-lahikasi.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-kastamonu", title: "Kastamonu Lâhikası", subtitle: "Risale-i Nur Külliyatı • 9. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/kastamonu-lahikasi.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-emirdag", title: "Emirdağ Lâhikası", subtitle: "Risale-i Nur Külliyatı • 10. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/emirdag-lahikasi.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-tarihce", title: "Tarihçe-i Hayat", subtitle: "Risale-i Nur Külliyatı • 11. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/tarihce-i-hayat.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-sikke", title: "Sikke-i Tasdik-i Gaybî", subtitle: "Risale-i Nur Külliyatı • 12. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/sikke-i-tasdik-i-gaybi.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-muhakemat", title: "Muhâkemat", subtitle: "Risale-i Nur Külliyatı • 13. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/muhakemat.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-muvazeneler", title: "İman ve Küfür Muvazeneleri", subtitle: "Risale-i Nur Külliyatı • 14. Eser", category: "kulliyat", pdfUrl: "/kaynaklar/Risale-i Nur Külliyat/iman-ve-kufur-muvazeneleri.pdf", pageInfo: "Tam Külliyat Nüshası" },
  { id: "pdf-konu-fihristi", title: "Risale-i Nur Konu Fihristi", subtitle: "Alfabetik Konu ve Meseleler Rehberi", category: "fihrist", pdfUrl: "/kaynaklar/Risale-i Nur İçindekiler/Risale-i Nur Konu Fihristi .PDF", pageInfo: "28 Sayfa • Konu Fihristi" },
  { id: "pdf-soru-cevap", title: "100 Müşkil Suale 100 Mukni Cevap", subtitle: "Soru-Cevap Fihristi ve Külliyat Metinleri", category: "fihrist", pdfUrl: "/kaynaklar/Risale-i Nur İçindekiler/Soru-Cevap Fihrist.pdf", pageInfo: "99 Sayfa • Soru-Cevap" },
  { id: "pdf-iki-boyutlu", title: "İki Boyutlu Risale-i Nur Fihristi", subtitle: "Mevzular, Vecizeler ve Hizmet Düsturları", category: "fihrist", pdfUrl: "/kaynaklar/Risale-i Nur İçindekiler/İki boyutlu Risale-i Nur fihristi .pdf", pageInfo: "17 Sayfa • Rehber" },
  { id: "pdf-fihrist-risalesi", title: "Fihrist Risalesi", subtitle: "Külliyattaki Tüm Söz, Mektup ve Lem'aların Hülâsası", category: "fihrist", pdfUrl: "/kaynaklar/Risale-i Nur İçindekiler/_dosyalar_dosya2_Fihrist_Risalesi.pdf", pageInfo: "Külliyat Fihristi" },
  { id: "pdf-hangi-risale", title: "Hangi Risale Ne Anlatıyor?", subtitle: "Külliyat Kitaplarının Özeti", category: "fihrist", pdfUrl: "/kaynaklar/Risale-i Nur İçindekiler/_dosyalar_dosya2_hangi-risale-ne-anlatiyo.pdf", pageInfo: "1 Sayfa • Özet Tablo" },
  { id: "pdf-namaz-dersleri", title: "Kitap Kitap Namaz Dersleri", subtitle: "Birer Sayfalık Kısa Ders Rehberi", category: "fihrist", pdfUrl: "/kaynaklar/Risale-i Nur İçindekiler/Kitap-Kitap-Namaz-Dersleri.pdf", pageInfo: "4 Sayfa • Ders Rehberi" },
  { id: "pdf-500-kelime", title: "Ezberlenecek 500 Osmanlıca Kelime", subtitle: "Risale-i Nur Temel Kelime Hazinesi", category: "osmanlica", pdfUrl: "/kaynaklar/Osmanlıca/ezberlenecek-500-kelime.pdf", pageInfo: "5 Sayfa • 500 Kelime" },
  { id: "pdf-kolay-osmanlica", title: "Kolay Osmanlıca Rehberi (Bâblar)", subtitle: "Arapça ve Farsça Vezinler / Kelime Kalıpları", category: "osmanlica", pdfUrl: "/kaynaklar/Osmanlıca/_dosyalar_dosya_Kolay_Osmanlica_Rehberi_Bablar.pdf", pageInfo: "Osmanlıca Gramer Rehberi" },
  { id: "pdf-ustad-biyo", title: "Bediüzzaman Said Nursî Biyografisi", subtitle: "Kronolojik Hayat Panosu (1877-1960)", category: "biyografi", pdfUrl: "/kaynaklar/NP-Ustad-Biyografisi200x70cm.pdf", pageInfo: "Panoramik Biyografi" }
];
