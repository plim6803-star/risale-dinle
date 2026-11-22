// ============================================================================
// Risale-i Nur Sinematik Tefekkür & Storyboard Motoru
// 1. Ön-Analiz (Pre-Analysis): Dinlenecek bölümün cümlelerini, kelimelerini,
//    temsillerini ve hakikat mesajını analiz ederek anlamlı sahnelere (Storyboard) ayırır.
// 2. Görsel Kompozitör (Illustrative Canvas Renderer): Her sahne için sadece soyut
//    parçacık değil; metindeki temsili (çöl seyyahı, çadır, eşkıya, mağrur/mütevazı yolcu,
//    tohumun kayayı yarıp ağaç olması, kayadan akan çeşme, sema/yıldızlar, güneş,
//    namaz/saat, bahar haşri vb.) hayal ettiren zengin animasyonlu tablo çizer.
// ============================================================================

export type SceneVisualArchetype =
  | "besmele_rahmet"      // Nur kubbesi, kalem/kitap, yükselen altın nurlar ve Besmele
  | "col_seyyahi"         // Çöl tepeleri, deve kervanı/seyyah, kabile çadırı ve emniyet yıldızı
  | "magrur_yolcu"        // Karanlık fırtına, yalnız titreyen yolcu, korkulu gölgeler
  | "asker_padisah"       // Kale burcu, sancak, devlet/kanun namına hareket eden nefer
  | "tohum_ve_agac"       // Toprak altındaki minik çekirdeğin sert kayayı yarıp ulu çam/meyve ağacı olması
  | "cesme_ve_su"         // Sert kayalardan ve kuru kumdan fışkıran berrak ab-ı hayat ırmakları ve koyun/süt/üzüm
  | "zikir_ve_hamd"       // Bütün nimetlerin "Bismillah" ile sunulması, şükür ve zikir halkası
  | "sikke_i_rububiyet"   // Arz, insan ve kâinat simasında birbirinin içinde üç birlik mührü
  | "namaz_ve_vakit"      // 24 altın saat, cami silüeti, seher/vakit nurları
  | "hasir_ve_bahar"      // Kıştan sonra dirilen bahar, çiçeklenen arz ve ebediyet ufku
  | "kainat_ve_sema";     // Güneş, yıldızlar, yörüngeler ve kozmik intizam

export interface StoryboardScene {
  sceneIndex: number;
  startSentIdx: number;
  endSentIdx: number;
  archetype: SceneVisualArchetype;
  stageBadge: string;          // Örn: "1. Sahne • Mukaddime & Sır" / "2. Sahne • Temsilî Hikâye"
  title: string;               // Sahne başlığı
  imageryDescription: string;  // Zihinde canlandırılacak hayal / tablo tarifi
  coreMessage: string;         // Tefekkür edilecek ana hakikat mesajı
  extractedKeywords: string[]; // Bu sahnedeki cümlelerden çıkarılan anahtar kelimeler
  aiImagePrompt: string;       // Harici/AI görsel üretimi için detaylı sahne istemi
  palette: {
    skyTop: string;
    skyBottom: string;
    horizon: string;
    accent: string;
    glow: string;
  };
  cameraMotion: "pan_right" | "zoom_in" | "rise_up" | "gentle_float";
}

interface RawSentence {
  para: number;
  text: string;
  isQuran: boolean;
}

const ARCHETYPE_CATALOG: Record<
  SceneVisualArchetype,
  {
    stageBadge: string;
    defaultTitle: string;
    imageryDescription: string;
    coreMessage: string;
    aiPromptBase: string;
    palette: StoryboardScene["palette"];
    cameraMotion: StoryboardScene["cameraMotion"];
  }
> = {
  besmele_rahmet: {
    stageBadge: "Mukaddime & Nur-u Kur'ân",
    defaultTitle: "Besmele-i Şerîf: Kâinatın Vird-i Zebânı",
    imageryDescription:
      "Gece karanlığını yaran altın bir rahmet kubbesi; kandil ışığında rahle üzerinde açık Kur'ân, kalem ve semâdan yeryüzüne inen saf Peygamberî/Kur'ânî nur huzmesi.",
    coreMessage:
      "Bismillâh her hayrın başıdır; bütün mevcudat hâl diliyle Allah namına hareket ettiğini ilân eder.",
    aiPromptBase:
      "Nano Banana and ChatGPT cinematic masterpiece, an elderly wise scholar in traditional robe reading an illuminated manuscript by warm lantern light on a wooden desk with brass inkwell, while a radiant golden divine light beam (Nur) descends from starry sky over mountains and sea",
    palette: {
      skyTop: "#070B16",
      skyBottom: "#1E142B",
      horizon: "#2D1B38",
      accent: "#E5B85B",
      glow: "#F7DC8A",
    },
    cameraMotion: "zoom_in",
  },
  col_seyyahi: {
    stageBadge: "Temsilî Hikâye • Emniyet",
    defaultTitle: "Sahra Yolculuğu ve Kabile Reisinin Himâyesi",
    imageryDescription:
      "Uçsuz bucaksız altın kum tepelerinde asası ve feneriyle yürüyen gerçek bir çöl seyyahı, deve kervanı, ufukta parlayan emniyetli kabile çadırı ve dağlar.",
    coreMessage:
      "Dünya çölünde bir kabile reisinin (Mâlik-i Ebedî'nin) ismini alan mütevazı yolcu, her yerde emniyet ve hürmetle gezer.",
    aiPromptBase:
      "Cinematic realistic storytelling scene, a humble human desert traveler in flowing cloak with wooden staff and brass lantern walking peacefully beside a camel caravan towards a warm glowing Bedouin tent on desert dunes at twilight",
    palette: {
      skyTop: "#12101B",
      skyBottom: "#3D2419",
      horizon: "#6E4223",
      accent: "#E8B059",
      glow: "#FAD48B",
    },
    cameraMotion: "pan_right",
  },
  magrur_yolcu: {
    stageBadge: "Temsilî Hikâye • İbret",
    defaultTitle: "Mağrur Yolcunun Fırtınalı Deniz, Balina ve Dağlar Karşısındaki Aczi",
    imageryDescription:
      "Karanlık fırtınalı okyanus, derinlikte süzülen dev bir balina, sarp dağlar ve bu haşmetli mevcudat karşısında himâyesiz kalmış yalnız yolcu.",
    coreMessage:
      "Hadsiz acz ve fakr içinde kendi nefsine güvenen mağrur insan, bütün kâinat karşısında titremeye ve dilenciliğe mahkûm olur.",
    aiPromptBase:
      "Dramatic Nano Banana and ChatGPT cinematic scene, a lone human traveler standing on a stormy rocky cliff above giant crashing ocean waves where a massive humpback whale emerges from the deep dark sea, lightning over mountains",
    palette: {
      skyTop: "#090C12",
      skyBottom: "#1C2430",
      horizon: "#2B2124",
      accent: "#C98A4B",
      glow: "#94A3B8",
    },
    cameraMotion: "gentle_float",
  },
  asker_padisah: {
    stageBadge: "Temsil & Hakikat • İstinad",
    defaultTitle: "Sultan-ı Ezelî Nâmına Hareket Eden Nefer",
    imageryDescription:
      "Muhteşem bir taş kale burcu üzerinde dalgalanan sancak; devlet ve kanun namına vakurla duran gerçek bir asker ve arkasındaki dağlar.",
    coreMessage:
      "İnsan kendi kuvvetiyle bir zerreye bile söz geçiremezken, 'Allah namına' dediğinde nihayetsiz Kudret ve Rahmet hazinesine bağlanır.",
    aiPromptBase:
      "Cinematic realistic historical film scene, a noble Ottoman soldier in ornate armor standing on a grand stone fortress balcony overlooking majestic mountains and sea at sunrise, golden banner waving, brass telescope and sword",
    palette: {
      skyTop: "#0D1322",
      skyBottom: "#351A24",
      horizon: "#4F232D",
      accent: "#E0B252",
      glow: "#F5D284",
    },
    cameraMotion: "zoom_in",
  },
  tohum_ve_agac: {
    stageBadge: "Kevnî Delil • Kudret Mucizesi",
    defaultTitle: "Çekirdeğin ve İncecik Köklerin 'Bismillâh'ı",
    imageryDescription:
      "İnsan avucundaki küçücük bir tohumun toprağa ekilip sert kayaları yumuşakça yararak göğe uzanan yemyeşil ve meyveli koca bir ağaca dönüşmesi.",
    coreMessage:
      "İpek gibi yumuşak kökler sert taşları kendi kuvvetiyle değil, 'Bismillâh' diyerek Rahmân ve Rahîm'in kudretiyle deler ve koca ağacı omuzunda taşır.",
    aiPromptBase:
      "Cinematic Nano Banana style close-up and wide shot, a human hand gently planting a glowing seed in rocky soil, delicate roots splitting hard stone and growing into a gigantic fruit tree with pomegranates and figs against mountain peaks",
    palette: {
      skyTop: "#071712",
      skyBottom: "#133628",
      horizon: "#1E4D38",
      accent: "#7CE395",
      glow: "#D4F7B5",
    },
    cameraMotion: "rise_up",
  },
  cesme_ve_su: {
    stageBadge: "Hazine-i Rahmet • Rezzâkıyet",
    defaultTitle: "Kayadan Akan Âb-ı Hayat, Deniz, Balina ve Rahmet Sofrası",
    imageryDescription:
      "Yalçın dağ kayalarından fışkıran berrak şelaleler, derin mavi denizde yüzen balina, yemyeşil vadide koyunlar ve ahşap sofrada dizili meyve, süt ve eşyalar.",
    coreMessage:
      "Toprak, taş, deniz, ağaç ve hayvanat birer mülk sahibi değil; Hazine-i Rahmetin tatlı sularını, sütlerini ve rızıklarını bize sunan birer rahmet çeşmesidir.",
    aiPromptBase:
      "Cinematic realistic painting and film frame, crystal waterfall flowing from rugged mountain into a turquoise sea where a gentle whale swims, while on the shore a wooden table holds a silver milk pitcher, pomegranates, grapes, and bread",
    palette: {
      skyTop: "#071826",
      skyBottom: "#123854",
      horizon: "#1A5270",
      accent: "#6BD5E1",
      glow: "#BAE6FD",
    },
    cameraMotion: "pan_right",
  },
  zikir_ve_hamd: {
    stageBadge: "Netice-i Meram • Ubûdiyet",
    defaultTitle: "Zikir: Başta Bismillâh, Ortada Fikir, Sonda Elhamdülillâh",
    imageryDescription:
      "Dağ ve deniz manzaralı bir terasta tefekkür eden insan; önünde rahle, fener, tespih ve meyveler; semâya yükselen üç nurlu şükür halkası.",
    coreMessage:
      "Nimetteki İlahî rahmet ayağını görüp Mun'im-i Hakikî'yi düşünmek, nimetin kendisinden çok daha yüksek ve bâki bir lezzettir.",
    aiPromptBase:
      "Warm ChatGPT and Nano Banana cinematic illustration, a grateful human sitting on a stone terrace at sunset overlooking mountains and ocean, antique lantern, prayer beads and bowl of apples on a carved table, golden light rays ascending",
    palette: {
      skyTop: "#140E1C",
      skyBottom: "#381B28",
      horizon: "#542834",
      accent: "#E5B95C",
      glow: "#FDE68A",
    },
    cameraMotion: "zoom_in",
  },
  sikke_i_rububiyet: {
    stageBadge: "14. Lem'a 2. Makam • Tevhid Sırları",
    defaultTitle: "Kâinat, Arz ve İnsan Sîmasında Üç Sikke-i Rubûbiyet",
    imageryDescription:
      "Kozmik kâinat küresi, mavi okyanuslarıyla yeryüzü ve tefekkür eden insan arasında parlayan iç içe geçmiş üç muazzam birlik mührü.",
    coreMessage:
      "Besmele'deki Allah, Rahmân ve Rahîm isimleri; kâinat, yeryüzü ve insan simasında birbirini gösteren üç mukaddes Tevhid mührüdür.",
    aiPromptBase:
      "Epic cinematic visual, a contemplative human standing on a mountain peak looking up at planet Earth, deep oceans with whales, and a glowing golden galaxy united by three concentric sacred light seals",
    palette: {
      skyTop: "#050814",
      skyBottom: "#161F42",
      horizon: "#282654",
      accent: "#F0C465",
      glow: "#FEF08A",
    },
    cameraMotion: "zoom_in",
  },
  namaz_ve_vakit: {
    stageBadge: "Ubûdiyet & Vakit • Mirac-ı Mü'min",
    defaultTitle: "Yirmi Dört Altın Saat, Eşyalar ve Beş Vakit İbadet",
    imageryDescription:
      "Ahşap masa üzerinde parlayan antika altın cep saati, 24 altın sikke, fener ve pencereden görünen seher vakti cami silüeti ile saf Peygamberî nur.",
    coreMessage:
      "Her gün verilen 24 saatlik ömür sermayesinden 1 saatini namaza ayırmak, diğer 23 saati de ibadet ve ebedî kazanca dönüştürür.",
    aiPromptBase:
      "Cinematic realistic scene, an ornate antique golden pocket watch and 24 gold coins on a velvet table beside a glowing lantern, looking out a window at a serene mosque silhouette at dawn with a pure divine golden beam of light (Nur) in the sky",
    palette: {
      skyTop: "#0F1026",
      skyBottom: "#2E1F47",
      horizon: "#52325C",
      accent: "#EAB308",
      glow: "#FEF08A",
    },
    cameraMotion: "rise_up",
  },
  hasir_ve_bahar: {
    stageBadge: "Haşir & Ahiret • Diriliş",
    defaultTitle: "Bahar Meydanında Üç Yüz Bin Haşir Nümunesi",
    imageryDescription:
      "Karlı dağların eteğinde bir anda çiçek açan yüz binlerce renkli ağaç, kelebekler, kuşlar ve bu dirilişi hayretle seyreden insan.",
    coreMessage:
      "Her bahar ölmüş arzı bir emirle dirilten Kudret-i Ezelîye için, insanları Haşr-i Ekber'de diriltmek bir bahar çiçeği kadar kolaydır.",
    aiPromptBase:
      "Breathtaking Nano Banana cinematic landscape, a human traveler watching in awe as a winter valley bursts into vibrant spring blossoms, cherry trees, birds, butterflies, crystal river and snow-capped mountains",
    palette: {
      skyTop: "#0B1D26",
      skyBottom: "#1B4332",
      horizon: "#2D6A4F",
      accent: "#F4A261",
      glow: "#E9C46A",
    },
    cameraMotion: "pan_right",
  },
  kainat_ve_sema: {
    stageBadge: "Âfâkî Tefekkür • İntizam",
    defaultTitle: "Güneş, Yıldızlar, Denizdeki Balina ve Kâinat Sarayı",
    imageryDescription:
      "Yüksek bir dağ tepesinde teleskop ve usturlapla yıldızlı semâyı inceleyen insan; aşağıda derin okyanusta yüzen dev balina ve yukarıda galaksiler.",
    coreMessage:
      "Denizdeki balıktan semâdaki yıldızlara kadar her şeyin kusursuz bir ölçü içinde hareket etmesi, Vâhid-i Ehad'in apaçık delilidir.",
    aiPromptBase:
      "Epic cinematic dual-world masterpiece, above: an astronomer human on a mountain cliff with a brass telescope looking at glowing golden galaxies; below: a cross-section of the deep blue ocean where a majestic whale swims among bioluminescent light",
    palette: {
      skyTop: "#040711",
      skyBottom: "#101B3B",
      horizon: "#1D2D5C",
      accent: "#F3C663",
      glow: "#FDE047",
    },
    cameraMotion: "gentle_float",
  },
};

// Tek bir cümlenin hangi görsel sahne arketipine ait olduğunu puanlayarak tespit eder
function classifySentenceArchetype(text: string): {
  archetype: SceneVisualArchetype;
  keywords: string[];
} {
  const s = text.toLocaleLowerCase("tr");
  const matchedKeywords: string[] = [];

  const rules: {
    archetype: SceneVisualArchetype;
    weight: number;
    terms: string[];
  }[] = [
    {
      archetype: "magrur_yolcu",
      weight: 3,
      terms: ["mağrur", "titrer", "dilencilik", "zelîl", "rezîl", "belâlar", "perişan", "hodbin", "bedbin"],
    },
    {
      archetype: "col_seyyahi",
      weight: 2.5,
      terms: ["çöl", "bedevî", "sahrâ", "sahra", "kabile", "seyyah", "çadır", "yol kesici", "şakî", "mütevâzı", "reis"],
    },
    {
      archetype: "asker_padisah",
      weight: 2.5,
      terms: ["asker", "devlet", "padişah", "kanun", "ordu", "kumandan", "hükümet", "ahali", "şah"],
    },
    {
      archetype: "tohum_ve_agac",
      weight: 3,
      terms: ["tohum", "çekirdek", "ağaç", "yaprak", "dal", "budak", "kök", "çam", "nebat", "ot", "taş", "kaya", "filiz"],
    },
    {
      archetype: "cesme_ve_su",
      weight: 2.8,
      terms: ["çeşme", "su", "süt", "koyun", "keçi", "inek", "üzüm", "hurma", "nar", "kavun", "tablacı", "mutfak", "konserve"],
    },
    {
      archetype: "zikir_ve_hamd",
      weight: 2.6,
      terms: ["elhamdülillâh", "elhamdülillah", "zikir", "fikir", "şükür", "nimet", "fiyat", "padişahın hediyesi", "elma"],
    },
    {
      archetype: "sikke_i_rububiyet",
      weight: 2.8,
      terms: ["sikke", "mühür", "üç sikke", "ehadiyet", "vâhidiyet", "rahmâniyet", "rahîmiyet", "on dördüncü lem'a", "makam"],
    },
    {
      archetype: "namaz_ve_vakit",
      weight: 2.5,
      terms: ["namaz", "yirmi dört", "altın", "vakit", "abdest", "ibadet", "ezan", "secde"],
    },
    {
      archetype: "hasir_ve_bahar",
      weight: 2.5,
      terms: ["haşir", "bahar", "diril", "kıyamet", "ahiret", "cennet", "bekâ", "terhis"],
    },
    {
      archetype: "kainat_ve_sema",
      weight: 2.2,
      terms: ["güneş", "yıldız", "semâ", "zemîn", "arz", "feza", "seyyare", "kozmik"],
    },
    {
      archetype: "besmele_rahmet",
      weight: 1.8,
      terms: ["bismillâh", "besmele", "hayrın başı", "vird-i zebân", "islâm nişanı", "bereket"],
    },
  ];

  let bestArchetype: SceneVisualArchetype = "besmele_rahmet";
  let bestScore = 0;

  for (const rule of rules) {
    let score = 0;
    for (const term of rule.terms) {
      if (s.includes(term)) {
        score += rule.weight;
        matchedKeywords.push(term);
      }
    }
    if (score > bestScore) {
      bestScore = score;
      bestArchetype = rule.archetype;
    }
  }

  return {
    archetype: bestArchetype,
    keywords: Array.from(new Set(matchedKeywords)).slice(0, 5),
  };
}

// Bölümün tamamını dinleme öncesinde analiz ederek sıralı bir Sinematik Storyboard (Sahne Akışı) üretir
export function buildChapterStoryboard(
  sentences: RawSentence[],
  sectionTitle: string
): {
  scenes: StoryboardScene[];
  sentenceToSceneMap: number[];
} {
  if (!sentences.length) {
    return { scenes: [], sentenceToSceneMap: [] };
  }

  // 1. Her cümlenin arketipini belirle
  const rawClassifications = sentences.map((sent, idx) => {
    const res = classifySentenceArchetype(sent.text);
    // İlk 3 cümle mukaddime/besmele ise besmele_rahmet olarak koru
    if (idx <= 2 && (sent.isQuran || sent.text.toLocaleLowerCase("tr").includes("bismill"))) {
      return { archetype: "besmele_rahmet" as SceneVisualArchetype, keywords: ["bismillâh", "mukaddime"] };
    }
    return res;
  });

  // 2. Çok kısa (1-2 cümlelik) kopuk geçişleri komşularıyla yumuşat (Window Smoothing)
  // ve anlamlı sahne blokları (Storyboard Scenes) oluştur (her sahne ortalama 4-10 cümle)
  const scenes: StoryboardScene[] = [];
  const sentenceToSceneMap: number[] = new Array(sentences.length).fill(0);

  let currentStart = 0;
  let currentArch = rawClassifications[0].archetype;
  let currentKeywords = new Set<string>(rawClassifications[0].keywords);

  const flushScene = (endIdx: number) => {
    const cat = ARCHETYPE_CATALOG[currentArch];
    const sampleSentence =
      sentences
        .slice(currentStart, endIdx + 1)
        .find((st) => st.text.length > 35 && !st.isQuran)?.text ||
      sentences[currentStart]?.text ||
      sectionTitle;

    const kwList = Array.from(currentKeywords).slice(0, 6);
    const sceneIdx = scenes.length;

    const customSubtitleSnippet =
      sampleSentence.length > 125 ? sampleSentence.slice(0, 122) + "…" : sampleSentence;

    scenes.push({
      sceneIndex: sceneIdx,
      startSentIdx: currentStart,
      endSentIdx: endIdx,
      archetype: currentArch,
      stageBadge: `${sceneIdx + 1}. Sahne • ${cat.stageBadge}`,
      title: cat.defaultTitle,
      imageryDescription: cat.imageryDescription,
      coreMessage: `${cat.coreMessage} (“${customSubtitleSnippet}”)`,
      extractedKeywords: kwList.length ? kwList : ["tefekkür", "hakikat", "iman"],
      aiImagePrompt: `${cat.aiPromptBase} — Context: ${sectionTitle}, Keywords: ${kwList.join(", ")}`,
      palette: cat.palette,
      cameraMotion: cat.cameraMotion,
    });

    for (let i = currentStart; i <= endIdx; i++) {
      sentenceToSceneMap[i] = sceneIdx;
    }
  };

  for (let i = 1; i < sentences.length; i++) {
    const nextArch = rawClassifications[i].archetype;
    const spanLen = i - currentStart;

    // Yeni bir arketip başladıysa ve mevcut sahne en az 3 cümle sürdüyse (veya çok güçlü bir sahne değişimi varsa)
    const lookaheadSame =
      i + 1 < sentences.length && rawClassifications[i + 1].archetype === nextArch;

    if (
      (nextArch !== currentArch && spanLen >= 3 && (lookaheadSame || rawClassifications[i].keywords.length >= 2)) ||
      spanLen >= 10
    ) {
      flushScene(i - 1);
      currentStart = i;
      currentArch = nextArch;
      currentKeywords = new Set<string>(rawClassifications[i].keywords);
    } else {
      rawClassifications[i].keywords.forEach((k) => currentKeywords.add(k));
    }
  }

  flushScene(sentences.length - 1);

  return { scenes, sentenceToSceneMap };
}

// ============================================================================
// SİNEMATİK KANVAS RESSAMI (Hikâyedeki Temsilleri Canlandıran Görsel Motor)
// overlayOnly === true olduğunda: Gerçek IMAX belgesel videosu veya AI fotoğrafı
// üzerinde sadece ince sinematik altın zerreler, hacimsel ışık huzmeleri (God rays)
// ve zarif vinyet çizerek amatör çizgi film hissini ortadan kaldırır.
// overlayOnly === false olduğunda: Tam vektör/kanvas illüstrasyon modunu korur.
// ============================================================================
export function renderStoryboardSceneToCanvas(
  ctx: CanvasRenderingContext2D,
  w: number,
  h: number,
  scene: StoryboardScene,
  t: number,
  sentenceProgressRatio: number, // 0.0 -> 1.0 (sahnenin kaçta kaçındayız)
  overlayOnly: boolean = false
) {
  const { palette, archetype, cameraMotion } = scene;

  if (overlayOnly) {
    ctx.clearRect(0, 0, w, h);
    ctx.save();

    // 1. Sinematik Anamorfik Vinyet (Kenarları film karesi gibi derinleştirir)
    const vig = ctx.createRadialGradient(
      w * 0.5,
      h * 0.45,
      Math.min(w, h) * 0.28,
      w * 0.5,
      h * 0.5,
      Math.max(w, h) * 0.72
    );
    vig.addColorStop(0, "rgba(0,0,0,0)");
    vig.addColorStop(0.75, "rgba(5,6,10,0.22)");
    vig.addColorStop(1, "rgba(4,5,8,0.68)");
    ctx.fillStyle = vig;
    ctx.fillRect(0, 0, w, h);

    // 2. İnce Hacimsel Nur Huzmeleri (Volumetric God Rays)
    const cx = w * 0.55;
    for (let r = -2; r <= 2; r++) {
      const sway = Math.sin(t * 0.35 + r) * 28;
      const rayGrad = ctx.createLinearGradient(cx + r * 70, 0, cx + r * 140 + sway, h * 0.85);
      rayGrad.addColorStop(0, palette.glow + "18");
      rayGrad.addColorStop(0.5, palette.accent + "0C");
      rayGrad.addColorStop(1, "rgba(0,0,0,0)");
      ctx.fillStyle = rayGrad;
      ctx.beginPath();
      ctx.moveTo(cx + r * 60 - 25, 0);
      ctx.lineTo(cx + r * 60 + 25, 0);
      ctx.lineTo(cx + r * 150 + 80 + sway, h);
      ctx.lineTo(cx + r * 150 - 80 + sway, h);
      ctx.closePath();
      ctx.fill();
    }

    // 3. Havada Süzülen Altın Tefekkür Zerreleri (Bokeh & Dust Motes)
    for (let i = 0; i < 36; i++) {
      const px = (((i * 137 + t * (8 + (i % 5) * 3)) % (w + 80)) - 40);
      const py =
        ((i * 89 - t * (5 + (i % 4) * 2)) % h + h) % h;
      const alpha = 0.2 + 0.55 * Math.abs(Math.sin(t * 0.9 + i));
      const radius = (i % 3 === 0 ? 2.4 : 1.3);
      ctx.fillStyle =
        palette.glow +
        Math.floor(alpha * 190)
          .toString(16)
          .padStart(2, "0");
      ctx.beginPath();
      ctx.arc(px, py, radius, 0, Math.PI * 2);
      ctx.fill();
    }

    ctx.restore();
    return;
  }

  // Kamera hareketi (hafif sinematik pan / zoom)
  ctx.save();
  const camOffsetX =
    cameraMotion === "pan_right" ? Math.sin(t * 0.25) * 18 : 0;
  const camOffsetY =
    cameraMotion === "rise_up" ? -Math.sin(t * 0.2) * 12 : 0;
  const camScale =
    cameraMotion === "zoom_in" ? 1 + 0.04 * (0.5 + 0.5 * Math.sin(t * 0.22)) : 1.02;

  ctx.translate(w / 2 + camOffsetX, h / 2 + camOffsetY);
  ctx.scale(camScale, camScale);
  ctx.translate(-w / 2, -h / 2);

  // 1. Gökyüzü / Atmosfer Gradyanı
  const skyGrad = ctx.createLinearGradient(0, 0, 0, h);
  skyGrad.addColorStop(0, palette.skyTop);
  skyGrad.addColorStop(0.58, palette.skyBottom);
  skyGrad.addColorStop(0.85, palette.horizon);
  skyGrad.addColorStop(1, "#070609");
  ctx.fillStyle = skyGrad;
  ctx.fillRect(0, 0, w, h);

  // 2. Yıldızlar ve Nur Zerreleri (Bütün sahnelerde derinlik katar)
  for (let i = 0; i < 48; i++) {
    const sx = ((i * 97 + Math.floor(t * 4)) % 1000) / 1000 * w;
    const sy = ((i * 53) % 650) / 1000 * h;
    const twinkle = 0.35 + 0.65 * Math.abs(Math.sin(t * 1.4 + i));
    ctx.fillStyle = palette.glow + Math.floor(twinkle * 180).toString(16).padStart(2, "0");
    ctx.beginPath();
    ctx.arc(sx, sy, (i % 3) * 0.7 + 0.8, 0, Math.PI * 2);
    ctx.fill();
  }

  const cx = w * 0.5;
  const cy = h * 0.42;

  // 3. Sahne Arketipine Özel İllüstrasyon Çizimi
  if (archetype === "col_seyyahi") {
    // Ufukta Rahmet Güneşi / Emniyet Yıldızı
    const sunGrad = ctx.createRadialGradient(w * 0.72, h * 0.32, 8, w * 0.72, h * 0.32, h * 0.35);
    sunGrad.addColorStop(0, palette.glow + "AA");
    sunGrad.addColorStop(0.4, palette.accent + "33");
    sunGrad.addColorStop(1, "transparent");
    ctx.fillStyle = sunGrad;
    ctx.fillRect(0, 0, w, h);

    // Arka ve Ön Kum Tepeleri (Dunes)
    ctx.fillStyle = "#3E2414";
    ctx.beginPath();
    ctx.moveTo(0, h * 0.68);
    for (let x = 0; x <= w; x += 20) {
      ctx.lineTo(x, h * 0.66 + Math.sin(x * 0.004 + 1.2) * 24);
    }
    ctx.lineTo(w, h);
    ctx.lineTo(0, h);
    ctx.fill();

    // Kabile Reisinin Işıklı Çadırı (Sağ Ufukta)
    const tentX = w * 0.75;
    const tentY = h * 0.64;
    ctx.fillStyle = palette.accent + "EE";
    ctx.beginPath();
    ctx.moveTo(tentX - 38, tentY + 14);
    ctx.lineTo(tentX, tentY - 22);
    ctx.lineTo(tentX + 38, tentY + 14);
    ctx.closePath();
    ctx.fill();
    // Çadırın Sancağı
    ctx.strokeStyle = palette.glow;
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(tentX, tentY - 22);
    ctx.lineTo(tentX, tentY - 38);
    ctx.stroke();

    // Ön Kumul
    ctx.fillStyle = "#26150B";
    ctx.beginPath();
    ctx.moveTo(0, h * 0.76);
    for (let x = 0; x <= w; x += 20) {
      ctx.lineTo(x, h * 0.74 + Math.cos(x * 0.005 + t * 0.3) * 18);
    }
    ctx.lineTo(w, h);
    ctx.lineTo(0, h);
    ctx.fill();

    // Mütevazı Seyyah (Elinde Asâ ve Üzerinde Himâye Nuru ile Yürüyen Yolcu)
    const walkProgress = 0.25 + sentenceProgressRatio * 0.35;
    const tx = w * walkProgress;
    const ty = h * 0.73 + Math.cos(tx * 0.005 + t * 0.3) * 18;

    // Himâye Halesi ("Bismillâh" Emniyet Kalkanı)
    const shieldGrad = ctx.createRadialGradient(tx, ty - 22, 4, tx, ty - 22, 46);
    shieldGrad.addColorStop(0, palette.glow + "77");
    shieldGrad.addColorStop(1, "transparent");
    ctx.fillStyle = shieldGrad;
    ctx.beginPath();
    ctx.arc(tx, ty - 22, 46, 0, Math.PI * 2);
    ctx.fill();

    // Seyyah Silüeti
    ctx.fillStyle = "#F7DC8A";
    ctx.beginPath();
    ctx.arc(tx, ty - 34, 6, 0, Math.PI * 2); // Baş
    ctx.fill();
    ctx.beginPath();
    ctx.moveTo(tx - 8, ty);
    ctx.lineTo(tx - 4, ty - 26);
    ctx.lineTo(tx + 5, ty - 26);
    ctx.lineTo(tx + 9, ty);
    ctx.closePath();
    ctx.fill();
    // Asâ
    ctx.strokeStyle = "#F7DC8A";
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(tx + 11, ty - 32);
    ctx.lineTo(tx + 7, ty + 2);
    ctx.stroke();
  } else if (archetype === "magrur_yolcu") {
    // Karanlık Fırtına Bulutları ve Şimşek/Rüzgâr Çizgileri
    ctx.strokeStyle = "#64748B55";
    ctx.lineWidth = 1.5;
    for (let r = 0; r < 14; r++) {
      const rx = ((r * 130 + t * 180) % (w + 200)) - 100;
      const ry = h * 0.25 + (r % 5) * 45;
      ctx.beginPath();
      ctx.moveTo(rx, ry);
      ctx.lineTo(rx - 65, ry + 18);
      ctx.stroke();
    }

    // Sarp Karanlık Tepeler
    ctx.fillStyle = "#141821";
    ctx.beginPath();
    ctx.moveTo(0, h * 0.72);
    for (let x = 0; x <= w; x += 25) {
      ctx.lineTo(x, h * 0.72 + Math.sin(x * 0.008) * 26);
    }
    ctx.lineTo(w, h);
    ctx.lineTo(0, h);
    ctx.fill();

    // Yalnız ve Titreyen Yolcu (Kayalık Uçurum Üzerinde İnsan Silüeti)
    const lx = w * 0.25 + Math.sin(t * 10) * 1.2;
    const ly = h * 0.71;
    ctx.fillStyle = "#CBD5E1";
    ctx.beginPath();
    ctx.arc(lx, ly - 28, 6, 0, Math.PI * 2);
    ctx.fill();
    ctx.beginPath();
    ctx.moveTo(lx - 8, ly);
    ctx.lineTo(lx - 4, ly - 22);
    ctx.lineTo(lx + 5, ly - 21);
    ctx.lineTo(lx + 7, ly);
    ctx.closePath();
    ctx.fill();

    // Fırtınalı Okyanus ve Derin Denizden Yükselen Dev Balina (Kudret-i İlahiye Tecellisi)
    const whaleX = w * 0.65 + Math.sin(t * 0.5) * 20;
    const whaleY = h * 0.72 + Math.cos(t * 0.7) * 6;
    ctx.fillStyle = "#1E3A5F";
    ctx.beginPath();
    ctx.ellipse(whaleX, whaleY, 68, 22, -0.15, 0, Math.PI * 2);
    ctx.fill();
    // Balina Kuyruğu
    ctx.beginPath();
    ctx.moveTo(whaleX + 58, whaleY - 6);
    ctx.lineTo(whaleX + 86, whaleY - 24);
    ctx.lineTo(whaleX + 76, whaleY - 2);
    ctx.lineTo(whaleX + 88, whaleY + 14);
    ctx.closePath();
    ctx.fill();
  } else if (archetype === "tohum_ve_agac") {
    // Toprak Katmanı ve Yarık Kaya
    const groundY = h * 0.72;
    ctx.fillStyle = "#12241B";
    ctx.fillRect(0, groundY, w, h - groundY);

    // Ortada Yarılan Sert Kaya Blokları
    ctx.fillStyle = "#334139";
    ctx.beginPath();
    ctx.moveTo(cx - 85, groundY);
    ctx.lineTo(cx - 25, groundY - 22);
    ctx.lineTo(cx - 12, groundY + 18);
    ctx.lineTo(cx - 90, groundY + 18);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(cx + 85, groundY);
    ctx.lineTo(cx + 25, groundY - 20);
    ctx.lineTo(cx + 12, groundY + 18);
    ctx.lineTo(cx + 90, groundY + 18);
    ctx.closePath();
    ctx.fill();

    // Toprak Altındaki Nurlu Çekirdek
    const seedY = groundY + 26;
    const seedGlow = ctx.createRadialGradient(cx, seedY, 2, cx, seedY, 36);
    seedGlow.addColorStop(0, palette.glow);
    seedGlow.addColorStop(1, "transparent");
    ctx.fillStyle = seedGlow;
    ctx.beginPath();
    ctx.arc(cx, seedY, 36, 0, Math.PI * 2);
    ctx.fill();

    // İpek Gibi Yumuşak Kökler (Aşağı Doğru)
    ctx.strokeStyle = palette.glow + "AA";
    ctx.lineWidth = 1.6;
    for (let r = -2; r <= 2; r++) {
      ctx.beginPath();
      ctx.moveTo(cx, seedY);
      ctx.quadraticCurveTo(cx + r * 22, seedY + 25, cx + r * 38, seedY + 48);
      ctx.stroke();
    }

    // Göğe Yükselen Muhteşem Ağaç Dalları ve Nurlu Yapraklar (Fraktal Ağaç)
    const drawTree = (x: number, y: number, len: number, angle: number, depth: number) => {
      if (depth === 0) {
        ctx.fillStyle = palette.accent + "CC";
        ctx.beginPath();
        ctx.arc(x, y, 4.5, 0, Math.PI * 2);
        ctx.fill();
        return;
      }
      const nx = x + Math.cos(angle) * len;
      const ny = y + Math.sin(angle) * len;
      ctx.strokeStyle = depth > 3 ? "#D4A359" : palette.accent;
      ctx.lineWidth = Math.max(1.2, depth * 1.1);
      ctx.beginPath();
      ctx.moveTo(x, y);
      ctx.lineTo(nx, ny);
      ctx.stroke();

      const sway = Math.sin(t * 0.9 + depth) * 0.035;
      drawTree(nx, ny, len * 0.74, angle - 0.34 + sway, depth - 1);
      drawTree(nx, ny, len * 0.74, angle + 0.34 + sway, depth - 1);
    };
    drawTree(cx, groundY + 10, Math.min(h * 0.16, 72), -Math.PI / 2, 6);
  } else if (archetype === "cesme_ve_su") {
    // Kayalardan Fışkıran Âb-ı Hayat Şelalesi ve Rahmet Irmakları
    const rockX = w * 0.3;
    const rockY = h * 0.42;

    // Dağ / Kaya Silüeti
    ctx.fillStyle = "#162E40";
    ctx.beginPath();
    ctx.moveTo(0, h * 0.75);
    ctx.lineTo(rockX - 40, rockY);
    ctx.lineTo(rockX + 60, rockY + 20);
    ctx.lineTo(w * 0.55, h * 0.75);
    ctx.closePath();
    ctx.fill();

    // Kayadan Akan Nurlu Su Şelalesi ve Dalgalar
    ctx.strokeStyle = palette.glow + "CC";
    ctx.lineWidth = 2.4;
    for (let s = 0; s < 5; s++) {
      ctx.beginPath();
      ctx.moveTo(rockX + s * 6, rockY + 25);
      ctx.quadraticCurveTo(
        rockX + 40 + s * 12,
        h * 0.6,
        w * 0.55 + s * 35,
        h * 0.74
      );
      ctx.stroke();
    }

    // Vadideki Rahmet Nehri / Deniz Dalgaları
    for (let wave = 0; wave < 5; wave++) {
      ctx.strokeStyle = palette.accent + (90 - wave * 12).toString(16);
      ctx.lineWidth = 2;
      ctx.beginPath();
      for (let x = 0; x <= w; x += 18) {
        const y = h * 0.72 + wave * 16 + Math.sin(x * 0.015 - t * 2 + wave) * 8;
        if (x === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.stroke();
    }

    // Denizde Süzülen Mübarek Balina Silüeti (Sağ Deniz Kıyısında)
    const wx = w * 0.72 + Math.cos(t * 0.4) * 18;
    const wy = h * 0.8 + Math.sin(t * 0.6) * 5;
    ctx.fillStyle = palette.accent + "AA";
    ctx.beginPath();
    ctx.ellipse(wx, wy, 52, 16, -0.08, 0, Math.PI * 2);
    ctx.fill();
    ctx.beginPath();
    ctx.moveTo(wx + 46, wy - 2);
    ctx.lineTo(wx + 68, wy - 16);
    ctx.lineTo(wx + 60, wy);
    ctx.lineTo(wx + 68, wy + 12);
    ctx.closePath();
    ctx.fill();
  } else if (archetype === "asker_padisah") {
    // Kale Burcu, Sancak ve Nurlu Kalkan
    const fortY = h * 0.68;
    ctx.fillStyle = "#23141C";
    ctx.fillRect(0, fortY, w, h - fortY);
    for (let bx = 0; bx < w; bx += 48) {
      ctx.fillRect(bx, fortY - 14, 28, 14);
    }

    // Merkezde Devlet/Sultan Sancağı ve Nurlu Arma
    const glow = ctx.createRadialGradient(cx, cy, 10, cx, cy, 140);
    glow.addColorStop(0, palette.glow + "88");
    glow.addColorStop(1, "transparent");
    ctx.fillStyle = glow;
    ctx.beginPath();
    ctx.arc(cx, cy, 140, 0, Math.PI * 2);
    ctx.fill();

    // Kalkan / Mühr-ü Sultanî Çizimi
    ctx.strokeStyle = palette.accent;
    ctx.lineWidth = 2.5;
    ctx.beginPath();
    ctx.moveTo(cx, cy - 55);
    ctx.lineTo(cx + 48, cy - 25);
    ctx.lineTo(cx + 36, cy + 35);
    ctx.lineTo(cx, cy + 62);
    ctx.lineTo(cx - 36, cy + 35);
    ctx.lineTo(cx - 48, cy - 25);
    ctx.closePath();
    ctx.stroke();
  } else if (archetype === "sikke_i_rububiyet" || archetype === "zikir_ve_hamd") {
    // Üç Sikke-i Rubûbiyet (İç İçe Üç Nurlu Daire: Kâinat, Arz ve İnsan)
    const radGrad = ctx.createRadialGradient(cx, cy, 8, cx, cy, Math.min(w, h) * 0.42);
    radGrad.addColorStop(0, palette.glow + "77");
    radGrad.addColorStop(0.5, palette.accent + "22");
    radGrad.addColorStop(1, "transparent");
    ctx.fillStyle = radGrad;
    ctx.fillRect(0, 0, w, h);

    const labels = ["İnsan (Rahîm)", "Arz (Rahmân)", "Kâinat (Allah)"];
    for (let ring = 1; ring <= 3; ring++) {
      const r = ring * 48 + Math.sin(t * 1.2 + ring) * 4;
      ctx.strokeStyle = palette.accent + (ring === 1 ? "EE" : ring === 2 ? "BB" : "88");
      ctx.lineWidth = 2.2 - ring * 0.4;
      ctx.beginPath();
      ctx.arc(cx, cy, r, 0, Math.PI * 2);
      ctx.stroke();

      // Yörünge üzerindeki nur noktası
      const angle = t * (0.6 / ring) + ring * 2;
      const ox = cx + Math.cos(angle) * r;
      const oy = cy + Math.sin(angle) * r;
      ctx.fillStyle = palette.glow;
      ctx.beginPath();
      ctx.arc(ox, oy, 4.5, 0, Math.PI * 2);
      ctx.fill();

      ctx.fillStyle = palette.glow + "CC";
      ctx.font = "11px serif";
      ctx.textAlign = "center";
      ctx.fillText(labels[ring - 1], cx, cy - r - 6);
    }
  } else {
    // Besmele Rahmet Kubbesi / Kâinat / Namaz / Haşir Genel Sinematik Sahnesi
    const radGrad = ctx.createRadialGradient(cx, cy, 10, cx, cy, Math.min(w, h) * 0.45);
    radGrad.addColorStop(0, palette.glow + "88");
    radGrad.addColorStop(0.5, palette.accent + "28");
    radGrad.addColorStop(1, "transparent");
    ctx.fillStyle = radGrad;
    ctx.fillRect(0, 0, w, h);

    // Semâдан İnen Rahmet Sütunları (Light Rays)
    for (let ray = -3; ray <= 3; ray++) {
      ctx.strokeStyle = palette.glow + "22";
      ctx.lineWidth = 18;
      ctx.beginPath();
      ctx.moveTo(cx + ray * 35, 0);
      ctx.lineTo(cx + ray * 95 + Math.sin(t + ray) * 15, h);
      ctx.stroke();
    }

    // Geometrik Şemse (Selçuklu/Osmanlı SekizKöşeli Yıldız Motifi)
    ctx.save();
    ctx.translate(cx, cy);
    ctx.rotate(t * 0.12);
    ctx.strokeStyle = palette.accent + "AA";
    ctx.lineWidth = 1.8;
    for (let k = 0; k < 2; k++) {
      ctx.rotate(Math.PI / 4);
      ctx.strokeRect(-52, -52, 104, 104);
    }
    ctx.restore();
  }

  ctx.restore();
}
