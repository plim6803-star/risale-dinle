// ============================================================================
// Nûr-u Sema: Mantık & Burhan Ağacı (Mind-Map) ve Çapraz Külliyat Referansları
// İlk 10 Söz'ün her biri için mantık silsilesi, temsil-hakikat köprüleri ve
// diğer Risalelerle (Mektubat, Lem'alar, Şualar) irtibatlı çapraz referans haritası
// ============================================================================

export interface MindMapNode {
  id: string;
  label: string;
  type: "root" | "temsil" | "hakikat" | "burhan" | "netice";
  description: string;
  sourceRef?: string;
  sentenceIndex?: number;
  children?: MindMapNode[];
}

export interface CrossReference {
  id: string;
  targetBook: string;
  targetSection: string;
  topic: string;
  relationType: "ayni_mesele" | "derinlik" | "delil" | "temsil_aciklamasi";
  summary: string;
  keyQuote: string;
}

export interface ChapterConceptMap {
  chapterNumber: number;
  partLabel: string;
  title: string;
  centralTheme: string;
  mindMapTree: MindMapNode;
  crossReferences: CrossReference[];
}

export const CHAPTER_CONCEPT_MAPS: Record<number, ChapterConceptMap> = {
  1: {
    chapterNumber: 1,
    partLabel: "Birinci Söz",
    title: "Bismillâh Her Hayrın Başıdır",
    centralTheme: "Kâinattaki bütün mevcudatın lisan-ı hâl ile Bismillâh demesi ve tevhid sikkesi",
    mindMapTree: {
      id: "1_root",
      label: "BİSMİLLÂHİRRAHMÂNİRRAHÎM",
      type: "root",
      description: "Her hayrın başı, kâinatın vird-i zebânı ve İslâm nişanı",
      sentenceIndex: 0,
      children: [
        {
          id: "1_temsil_col",
          label: "Temsilî Hikâye: Sahra Yolculuğu",
          type: "temsil",
          description: "Arap çöllerinde seyahat eden iki adam: Biri mütevazı reisin ismini alır, diğeri mağrurdur",
          sentenceIndex: 3,
          children: [
            {
              id: "1_temsil_mutavazi",
              label: "Mütevazı Yolcu (Emniyet)",
              type: "burhan",
              description: "Her kabilenin çadırında hürmet görür, eşkıyadan korkmaz, selametle gezer",
              sentenceIndex: 7,
            },
            {
              id: "1_temsil_magrur",
              label: "Mağrur Yolcu (Helâket)",
              type: "burhan",
              description: "Kendi nefsine güvendiği için her şeyden titrer, dilenci ve zelil kalır",
              sentenceIndex: 8,
            },
          ],
        },
        {
          id: "1_hakikat_dunya",
          label: "Hakikat: Dünya Çölü ve İnsan",
          type: "hakikat",
          description: "İnsan nihayetsiz âciz ve fakirdir; Mâlik-i Ebedî'nin namına hareket ederse kainat emrine girer",
          sentenceIndex: 12,
          children: [
            {
              id: "1_burhan_tohum",
              label: "Kök ve Tohumların Kudreti",
              type: "burhan",
              description: "İpek gibi yumuşak kökler 'Bismillah' diyerek sert taşları delip geçer",
              sentenceIndex: 18,
            },
            {
              id: "1_burhan_agac",
              label: "Ağaç ve Hayvanların Vazifesi",
              type: "burhan",
              description: "Bütün ağaçlar birer Rahmânî mutfak, inek ve develer birer süt çeşmesidir",
              sentenceIndex: 22,
            },
            {
              id: "1_burhan_gunes",
              label: "Güneş ve Zerreler",
              type: "burhan",
              description: "Güneş koca kâinat sarayının lambası ve sobası olarak Allah namına döner",
              sentenceIndex: 26,
            },
          ],
        },
        {
          id: "1_netice_fiyat",
          label: "Netice: Nimetlerin Fiyatı",
          type: "netice",
          description: "Mün'im-i Hakikî bizden o kıymettar nimetlere bedel üç şey ister: ZİKİR, FİKİR, ŞÜKÜR",
          sentenceIndex: 32,
        },
      ],
    },
    crossReferences: [
      {
        id: "ref_14_lema",
        targetBook: "Lem'alar",
        targetSection: "14. Lem'a (2. Makam)",
        topic: "Besmele'nin Altı Sırrı ve Âlemlerin Yaratılışı",
        relationType: "derinlik",
        summary: "1. Söz'deki temsilin en derin felsefî ve kelâmî burhanı: Besmele'deki üç ismin (Allah, Rahmân, Rahîm) arş, ferş ve insan kalbindeki tecellileri izah edilir.",
        keyQuote: "Besmele öyle kutlu bir anahtardır ki, kâinatın tılsımını açar ve Rububiyetin hazinelerini gösterir.",
      },
      {
        id: "ref_isarat",
        targetBook: "İşârâtü'l-İ'câz",
        targetSection: "Fatiha Suresi Tefsiri (Besmele Bahsi)",
        topic: "Lafz-ı Celâl ve Esmâ-i Hüsnâ",
        relationType: "ayni_mesele",
        summary: "Besmele'deki harflerin ve kelimelerin nahiv, belâgat ve kelâm açısından harika i'câzı açıklanır.",
        keyQuote: "Bismillah bir sikke-i tevhiddir ki, kâinat sahifesine nakşedilmiştir.",
      },
      {
        id: "ref_24_soz",
        targetBook: "Sözler",
        targetSection: "24. Söz (1. Dal)",
        topic: "Kâinattaki Umumî İbadet ve Zikirler",
        relationType: "delil",
        summary: "Mahlûkatın hâl diliyle yaptığı zikirlerin melekler vasıtasıyla dergâh-ı İlâhîye takdim edilişi.",
        keyQuote: "Her bir masnu, kendi hususi lisanıyla Sâni-i Zülcelâl'i tesbih eder.",
      },
    ],
  },
  2: {
    chapterNumber: 2,
    partLabel: "İkinci Söz",
    title: "İmanın Saadet-i Dâreyn Anahtarı Oluşu",
    centralTheme: "Hüsn-ü zan ile imanın insanı sultan yapması; su-i zan ile küfrün insanı bedbaht hayvan etmesi",
    mindMapTree: {
      id: "2_root",
      label: "İMAN BİR İNTİSAPTIR",
      type: "root",
      description: "İman insanı insan eder, belki sultan eder; küfür ise canavar hayvan eder",
      sentenceIndex: 0,
      children: [
        {
          id: "2_temsil",
          label: "Temsil: İki Seyyahın Seyahati",
          type: "temsil",
          description: "Biri hodbin ve bedbin, diğeri hudabin ve hüsn-ü zan sahibi iki yolcu",
          sentenceIndex: 2,
          children: [
            {
              id: "2_temsil_hodbin",
              label: "Hodbin Seyyahın Kâbusu",
              type: "burhan",
              description: "Güzel bahçeyi canavarlar yuvası, askerleri düşman zannederek azap çeker",
              sentenceIndex: 5,
            },
            {
              id: "2_temsil_hudabin",
              label: "Hudabin Seyyahın Huzuru",
              type: "burhan",
              description: "Hüsn-ü ahlakı sebebiyle kışlayı intizam, bahçeyi ziyafetgâh görür",
              sentenceIndex: 9,
            },
          ],
        },
        {
          id: "2_hakikat",
          label: "Hakikat: İman Gözlüğü",
          type: "hakikat",
          description: "İman bir nurdur, hem kuvvettir. Hakiki imanı elde eden adam kâinata meydan okuyabilir",
          sentenceIndex: 14,
        },
        {
          id: "2_netice",
          label: "Netice: Tevekkül ve Saadet",
          type: "netice",
          description: "Kadere iman eden kederden emin olur; tevekkül eden dünyayı omuzundan atar",
          sentenceIndex: 20,
        },
      ],
    },
    crossReferences: [
      {
        id: "ref_23_soz",
        targetBook: "Sözler",
        targetSection: "23. Söz (İnsan Risalesi)",
        topic: "İmanın İnsanı Ahsen-i Takvime Çıkarması",
        relationType: "derinlik",
        summary: "2. Söz'deki intisap hakikatinin 5 nükte halinde şaheser mertebesinde açılımı.",
        keyQuote: "İman, insanı Sâni-i Zülcelâl'ine nisbet eder; insan intisap ile kıymet alır.",
      },
      {
        id: "ref_26_soz",
        targetBook: "Sözler",
        targetSection: "26. Söz (Kader Risalesi)",
        topic: "Tevekkül ve Ye'sin Reddi",
        relationType: "delil",
        summary: "İnsanın cüz'i iradesi ile İlahi kaderin ahengi ve tevekkülün ruhi ferahlığı.",
        keyQuote: "Men âmene bi'l-kaderi emine mine'l-keder.",
      },
    ],
  },
  3: {
    chapterNumber: 3,
    partLabel: "Üçüncü Söz",
    title: "İbadetin Menfaati ve Hayatın Gayesi",
    centralTheme: "İbadet büyük bir ticaret ve saadettir; fısk ve sefahat ise büyük bir hasarettir",
    mindMapTree: {
      id: "3_root",
      label: "İBADETİN TİCARET-İ UZMASI",
      type: "root",
      description: "Namaz ve ubudiyet kulun halıkına karşı en karlı şükür ticaretidir",
      sentenceIndex: 0,
      children: [
        {
          id: "3_temsil",
          label: "Temsil: Asker ve Tüfek İmtihanı",
          type: "temsil",
          description: "Padişahın iki neferine verdiği sermaye, silah ve talim emri",
          sentenceIndex: 3,
        },
        {
          id: "3_hakikat",
          label: "Hakikat: İnsana Verilen Cihazat",
          type: "hakikat",
          description: "Göz, kulak, akıl ve kalb nimetleri fâni dünyaya değil, ebedi ibadete bakar",
          sentenceIndex: 10,
        },
      ],
    },
    crossReferences: [
      {
        id: "ref_9_soz",
        targetBook: "Sözler",
        targetSection: "9. Söz (Namazın Beş Vakti)",
        topic: "Namazın Kâinattaki Saati ve İbadetin Ruhu",
        relationType: "derinlik",
        summary: "3. Söz'deki asker temsilinin 5 vakit namaz hikmetleriyle tamamlandığı makam.",
        keyQuote: "Namaz kâinatın fihristesi ve mahlûkatın ibadetlerinin hülâsasıdır.",
      },
    ],
  },
};

export function getConceptMapForChapter(chapterNumber: number): ChapterConceptMap {
  return (
    CHAPTER_CONCEPT_MAPS[chapterNumber] || {
      chapterNumber,
      partLabel: `${chapterNumber}. Söz`,
      title: `${chapterNumber}. Söz Tefekkür Haritası`,
      centralTheme: "Tevhid ve İman Hakikatleri",
      mindMapTree: {
        id: "default_root",
        label: `${chapterNumber}. Söz Esasları`,
        type: "root",
        description: "Bölümün temel burhan ve temsil silsilesi",
        children: [
          {
            id: "def_temsil",
            label: "Temsilî Hakikat",
            type: "temsil",
            description: "Meseleyi akla yakınlaştıran dürbün",
          },
          {
            id: "def_burhan",
            label: "Aklî ve Kelâmî Burhan",
            type: "burhan",
            description: "Kâinattan ve âyetlerden getirilen deliller",
          },
          {
            id: "def_netice",
            label: "Nefis Muhasebesi ve Netice",
            type: "netice",
            description: "Asrımıza ve kalbimize bakan hayat dersi",
          },
        ],
      },
      crossReferences: [
        {
          id: "genel_ref",
          targetBook: "Sözler",
          targetSection: "Külliyat İçi İrtibat",
          topic: "Tevhid ve Esmâ Tecellileri",
          relationType: "derinlik",
          summary: "Risale-i Nur'un bütün risaleleri birbirini tefsir ve teyit eden bir vahdet arz eder.",
          keyQuote: "Risale-i Nur bir daire-i nuraniyedir; her risale onun bir penceresidir.",
        },
      ],
    }
  );
}
