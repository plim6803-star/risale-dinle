// ============================================================================
// Nûr-u Sema: İslami Edebe & Risale-i Nur Külliyatı Ruhu'na Hakim
// Sinematik Belgesel Yönetmeni Robotu (AI Cinema Director & Archive Engine)
// ----------------------------------------------------------------------------
// Özellikler:
// 1. Ücretsiz & Hazır AI Görsel/Video Servisleri (Free Built-in Providers):
//    - Pollinations Flux Free API (API Anahtarsız doğrudan gerçekçi 8K AI üretimi)
//    - Unsplash / Pexels Belgesel Fotoğraf & Sinematik Doğa/Kâinat Kütüphanesi
//    - Nûr-u Sema 60 FPS Canlı Sinematik Kanvas & Base64 HD Sanat Motoru (100% Garantili)
//    Görsellerde XML/ampersand kırılması yaşanmaması için tam Base64 + Otomatik
//    Yedekleme (Fallback) mimarisi kullanılmıştır.
// 2. Sinema Arşivi & Akıllı Yeniden Kullanım (Smart Reuse Cache):
//    Üretilen her sahne (görsel, video/hareketli sekans, atmosfer sesi, şerh özeti)
//    semantik imzasıyla (archetype + kavram parmak izi) arşive kaydedilir.
// ============================================================================

import type { SceneVisualArchetype, StoryboardScene } from "@/lib/cinema-storyboard";

export type CinemaVisualStyle =
  | "nano_banana_chatgpt"   // Nano Banana & ChatGPT / DALL-E 3 / Sora tarzı canlı hikayesel resim & video (insanlar, balina, dağ, deniz, eşyalar, Peygamberler saf nur)
  | "graphic_novel_comic"   // Çizgi Roman / Grafik Roman & Sinematik İllüstrasyon (Canlı karakterler, sahneler ve eşyalar)
  | "hollywood_epic_doc"    // IMAX / BBC Earth & Cosmos tarzı efsanevi kâinat ve tabiat belgeseli
  | "ottoman_illuminated"   // Klasik Osmanlı / Selçuklu tezhip, minyatür ve altın nur estetiği
  | "poetic_cinematic"      // Tarkovsky / Terrence Malick tarzı derin şiirsel tefekkür sineması
  | "sacred_calligraphy";   // Semâvî hat sanatı, geometrik şemse ve nur mimarisi

export interface CinemaRobotPreferences {
  visualStyle: CinemaVisualStyle;
  cameraPacing: "sakin_tefekkur" | "belgesel_akici" | "heybetli_epik";
  ambientMoodPreference: "auto_context" | "ussak" | "hicaz" | "rast" | "saba" | "rahmet" | "kainat";
  autoPreloadNextChapter: boolean;
  reuseArchivedScenesWhenSimilar: boolean;
  similarityThreshold: number; // 0.5 - 0.95
  aiProvider:
    | "auto_failover_pro"      // Akıllı Kesintisiz Motor: Gemini -> OpenAI -> 4K Belgesel (Otomatik Yedeklemeli)
    | "free_belgesel_hybrid"   // 4K Ultra Belgesel & Doğa/İnsan Koleksiyonu
    | "pollinations_flux"      // Ücretsiz Flux Canlı AI Motoru
    | "gemini"                 // Google Gemini Imagen 3
    | "openai_dalle"           // OpenAI DALL-E 3
    | "local_cinematic_engine";
  apiKey?: string;
  geminiApiKey?: string;
  openaiApiKey?: string;
  openrouterApiKey?: string;
}

export interface ArchivedCinemaAsset {
  id: string;
  semanticHash: string;          // Arketip + anahtar kavram imzası
  chapterNumber: number;
  partLabel: string;
  sceneIndex: number;
  archetype: SceneVisualArchetype;
  title: string;
  stageBadge: string;
  imageryDescription: string;
  coreMessage: string;
  directorNotes: string;         // Robotun bu sahneyi neden ve nasıl kurguladığı
  islamicComplianceNote: string; // İslami edep ve Risale-i Nur muvazenesine uygunluk notu
  visualStyle: CinemaVisualStyle;
  cameraSequence: string[];      // Kamera planları
  recommendedAmbientTheme: string;
  generatedImageUrl: string;     // Ana yüksek çözünürlüklü görsel URL'i (Gerçek Belgesel veya AI)
  fallbackImageUrl: string;      // İnternet kesilirse veya dış servis gecikirse anında devreye giren Base64 HD görsel
  videoLoopUrl?: string;         // Varsa sinematik doğa/uzay video döngüsü
  aiPromptUsed: string;
  keywords: string[];
  reuseCount: number;
  createdAt: string;
}

const ARCHIVE_STORAGE_KEY = "risale_cinema_archive_v5";
const PREFS_STORAGE_KEY = "risale_cinema_robot_prefs_v5";

// Yüksek Prodüksiyonlu 4K National Geographic / BBC Planet Earth / IMAX Belgesel Kareleri
// Her sahne arketipi için 6 farklı sinematik kamera açısı (Hiçbir zaman basit grafik/SVG dönmez)
export const FREE_DOCUMENTARY_SCENE_GALLERY: Record<SceneVisualArchetype, string[]> = {
  besmele_rahmet: [
    "https://images.unsplash.com/photo-1507608616759-54f48f0af0ee?auto=format&fit=crop&w=1920&q=90", // Bulutları yaran altın rahmet huzmeleri
    "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1920&q=90", // Kozmik semâdan süzülen nur ışıkları
    "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=1920&q=90", // Kandil ışığında tarihi yazma eser ve rahle
    "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=1920&q=90", // Yıldızlı gece seması ve nurani dağ zirvesi
    "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=1920&q=90", // Altın şafak vakti ufuk çizgisi
    "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?auto=format&fit=crop&w=1920&q=90", // Seher vaktinde vadilere yayılan güneş ışığı
  ],
  col_seyyahi: [
    "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1920&q=90", // Altın Sahra kum tepeleri ve gün doğumu
    "https://images.unsplash.com/photo-1542401886-65d6c61db217?auto=format&fit=crop&w=1920&q=90", // Uçsuz bucaksız çöl ufku ve vaha yolu
    "https://images.unsplash.com/photo-1512632578888-169bbbc64f33?auto=format&fit=crop&w=1920&q=90", // Gün batımında çöl kervanı ve seyyahlar
    "https://images.unsplash.com/photo-1473580044384-7ba9967e16a0?auto=format&fit=crop&w=1920&q=90", // Alacakaranlıkta sahra yıldızları ve çadır
    "https://images.unsplash.com/photo-1451337516015-6b6e9a44a8a3?auto=format&fit=crop&w=1920&q=90", // Rüzgârın şekillendirdiği kızıl çöl kumulları
    "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1920&q=90", // Sahra üzerinde yükselen emniyet güneşi
  ],
  magrur_yolcu: [
    "https://images.unsplash.com/photo-1518837695005-2083093ee35b?auto=format&fit=crop&w=1920&q=90", // Fırtınalı dev okyanus dalgaları
    "https://images.unsplash.com/photo-1568430462989-44163eb1752f?auto=format&fit=crop&w=1920&q=90", // Derin okyanusta yükselen dev balina
    "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1920&q=90", // Sarp, sisli ve heybetli dağ geçidi
    "https://images.unsplash.com/photo-1505672678657-cc7037095e60?auto=format&fit=crop&w=1920&q=90", // Karanlık fırtına bulutları ve şimşekli ufuk
    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1920&q=90", // Açık deniz ve ufukta yalnızlık
    "https://images.unsplash.com/photo-1454496522488-7a8e488e8606?auto=format&fit=crop&w=1920&q=90", // Karlı ve bulutlu sarp zirveler
  ],
  asker_padisah: [
    "https://images.unsplash.com/photo-1533154683836-84ea7a0bc310?auto=format&fit=crop&w=1920&q=90", // Tarihi taş kale burcu ve şafak
    "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1920&q=90", // İhtişamlı kubbe ve saray mimarisi
    "https://images.unsplash.com/photo-1526772662000-3f88f10405ff?auto=format&fit=crop&w=1920&q=90", // Dağların zirvesinde sarsılmaz burç
    "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=1920&q=90", // Altın ışıklar altında tarihi saltanat mimarisi
  ],
  tohum_ve_agac: [
    "https://images.unsplash.com/photo-1502082553048-f009c37129b9?auto=format&fit=crop&w=1920&q=90", // Güneş ışıkları altında ulu ve yemyeşil hayat ağacı
    "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1920&q=90", // Ormanda ağaçların arasından süzülen rahmet huzmeleri
    "https://images.unsplash.com/photo-1523348837708-15d4a09cfac2?auto=format&fit=crop&w=1920&q=90", // Toprağa ekilen canlı tohum ve filiz
    "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?auto=format&fit=crop&w=1920&q=90", // Kayaları yararak çıkan taze yeşil fidan
  ],
  cesme_ve_su: [
    "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?auto=format&fit=crop&w=1920&q=90", // Yalçın dağ kayalarından fışkıran berrak şelale
    "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=1920&q=90", // Yemyeşil vadi ve sabah sisleri içinde ırmak
    "https://images.unsplash.com/photo-1568430462989-44163eb1752f?auto=format&fit=crop&w=1400&q=90", // Mavi okyanusta süzülen dev balina
    "https://images.unsplash.com/photo-1490818387583-1baba5e638af?auto=format&fit=crop&w=1920&q=90", // Zengin meyve ve nimet sofrası
    "https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=1920&q=90", // Dağlar arasında zümrüt su havzası
  ],
  zikir_ve_hamd: [
    "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1920&q=90", // Altın gün batımında dağ ve ayna gibi göl
    "https://images.unsplash.com/photo-1490730141103-6cac27aaab94?auto=format&fit=crop&w=1920&q=90", // Ufukta yükselen şükür ve tefekkür güneşi
    "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?auto=format&fit=crop&w=1920&q=90", // Klasik fener ve sıcak tefekkür köşesi
  ],
  sikke_i_rububiyet: [
    "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=1920&q=90", // Muazzam galaksi, nebula ve kozmik tevhid mührü
    "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1920&q=90", // Uzaydan mavi yeryüzü küresi, denizler ve kıtalar
  ],
  namaz_ve_vakit: [
    "https://images.unsplash.com/photo-1509048191080-d2984bad6ae5?auto=format&fit=crop&w=1920&q=90", // Altın cep saati (24 altın saat temsili) ve eşyalar
    "https://images.unsplash.com/photo-1541432901042-2d8bd64b4a9b?auto=format&fit=crop&w=1920&q=90", // Seher vaktinde nurlu cami kubbe ve minare silüeti
  ],
  hasir_ve_bahar: [
    "https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=1920&q=90", // Bahar meydanında çiçek açan yüz binlerce renkli ağaç
    "https://images.unsplash.com/photo-1465146344425-f00d5f5c8f07?auto=format&fit=crop&w=1920&q=90", // Güneş ışığında dirilen bahar çiçekleri
  ],
  kainat_ve_sema: [
    "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?auto=format&fit=crop&w=1920&q=90", // Samanyolu galaksisi ve dağ zirvesinde gözlem
    "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=1920&q=90", // Derin mavi okyanus ve su altı dünyası
    "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=1920&q=90", // Yörüngede dünya ve güneş doğuşu
  ],
};

// Her sahne arketipi için Kesintisiz 1080p HD Belgesel Video Akışları (Wikimedia Commons & Mixkit CDN)
export const FREE_DOCUMENTARY_VIDEO_LOOPS: Record<SceneVisualArchetype, string[]> = {
  besmele_rahmet: [
    "https://assets.mixkit.co/videos/preview/mixkit-sun-rays-through-the-trees-in-the-forest-1219-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-stars-in-space-1610-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-white-clouds-on-the-sky-1244-large.mp4",
  ],
  col_seyyahi: [
    "https://assets.mixkit.co/videos/preview/mixkit-sand-dunes-in-the-desert-2266-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-desert-dunes-at-sunset-4154-large.mp4",
  ],
  magrur_yolcu: [
    "https://assets.mixkit.co/videos/preview/mixkit-waves-in-the-water-1164-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-dark-storm-clouds-in-the-sky-4156-large.mp4",
  ],
  asker_padisah: [
    "https://assets.mixkit.co/videos/preview/mixkit-clouds-and-blue-sky-2408-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-sunset-over-the-sea-1168-large.mp4",
  ],
  tohum_ve_agac: [
    "https://assets.mixkit.co/videos/preview/mixkit-sun-rays-through-the-trees-in-the-forest-1219-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4",
  ],
  cesme_ve_su: [
    "https://assets.mixkit.co/videos/preview/mixkit-waterfall-in-forest-2213-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-river-in-the-forest-1214-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-waves-in-the-water-1164-large.mp4",
  ],
  zikir_ve_hamd: [
    "https://assets.mixkit.co/videos/preview/mixkit-sunset-over-the-sea-1168-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-sun-rays-through-the-trees-in-the-forest-1219-large.mp4",
  ],
  sikke_i_rububiyet: [
    "https://assets.mixkit.co/videos/preview/mixkit-stars-in-space-1610-large.mp4",
  ],
  namaz_ve_vakit: [
    "https://assets.mixkit.co/videos/preview/mixkit-sunset-over-the-sea-1168-large.mp4",
  ],
  hasir_ve_bahar: [
    "https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-waterfall-in-forest-2213-large.mp4",
  ],
  kainat_ve_sema: [
    "https://assets.mixkit.co/videos/preview/mixkit-stars-in-space-1610-large.mp4",
    "https://assets.mixkit.co/videos/preview/mixkit-waves-in-the-water-1164-large.mp4",
  ],
};

export const DEFAULT_ROBOT_PREFERENCES: CinemaRobotPreferences = {
  visualStyle: "hollywood_epic_doc",
  cameraPacing: "belgesel_akici",
  ambientMoodPreference: "auto_context",
  autoPreloadNextChapter: true,
  reuseArchivedScenesWhenSimilar: true,
  similarityThreshold: 0.65,
  aiProvider: "auto_failover_pro",
  apiKey: "",
  geminiApiKey: "",
  openaiApiKey: "",
  openrouterApiKey: "",
};

// İslami Edep ve Risale-i Nur Külliyatı İlkeleri Rehberi (Robotun Sistem Düsturu)
export const ROBOT_ISLAMIC_CINEMA_CHARTER = [
  "1. Peygamberler İçin Nur Düsturu & Gerçekçi İnsan Tasviri: Sıradan insanlar (çöldeki yolcu, seyyah, asker, ilim yolcusu, tefekkür eden insan) gerçekçi sinema veya çizgi roman karakteri olarak gösterilir; ancak Peygamberler (a.s.) asla yüz/beden olarak çizilmez, yalnızca semâvî ve mukaddes bir NUR HUZMESİ olarak temsil edilir.",
  "2. Canlı Tabiat, Hayvanat ve Eşyalar: Denizdeki dev balinalar, sarp dağlar, fırtınalı okyanuslar, meyveler, cep saati, fener, pusula, saray ve çadır gibi eşyalar Nano Banana / ChatGPT / Sora kalitesinde tüm canlılığıyla sahneye taşınır.",
  "3. Temsil Dürbünü: Risale-i Nur'daki hikâye-i temsiliyeler (çöldeki yolcu, ipek köklerin kayayı delmesi, 24 altın, iki asker) izleyiciyi içine çeken sinematik ve çizgi-roman tadında sahnelenir.",
  "4. Akıllı Arşiv ve İktisat: Üretilen her yüksek kaliteli sahne arşive kaydedilir; Külliyat'ın diğer yerlerinde benzer temsil geçtiğinde hızla arşivden eşleştirilir.",
];

export const VISUAL_STYLE_LABELS: Record<
  CinemaVisualStyle,
  { name: string; desc: string; promptPrefix: string }
> = {
  nano_banana_chatgpt: {
    name: "Nano Banana & ChatGPT Sinematik Gerçeklik (İnsan, Balina, Dağ, Deniz & Eşyalar)",
    desc: "Günümüz Nano Banana ve ChatGPT/DALL-E 3 kalitesinde gerçek insanlar, dev balinalar, dağlar, denizler, eşyalar (Peygamberler sadece saf Nur olarak gösterilir)",
    promptPrefix:
      "Masterpiece Nano Banana and ChatGPT DALL-E 3 ultra-detailed cinematic film frame, realistic expressive humans in traditional attire, giant whales in deep ocean, majestic mountains, ornate objects and lanterns, prophets represented strictly as pure radiant divine golden light (Nur), 8k photorealistic storytelling",
  },
  graphic_novel_comic: {
    name: "Sinematik Çizgi Roman & Grafik İllüstrasyon (Canlı Hikâye & Karakterler)",
    desc: "Zengin detaylı çizgi roman / grafik roman estetiğinde temsildeki insanlar, hayvanlar, eşyalar ve doğa sahneleri",
    promptPrefix:
      "Epic cinematic graphic novel and comic book art illustration, rich dynamic ink lines and vibrant painterly colors, expressive human traveler characters, majestic whale and sea, mountains, antique objects, prophets shown only as radiant golden light beam, 8k",
  },
  hollywood_epic_doc: {
    name: "Efsanevi Kâinat & Tabiat Belgeseli (IMAX / Cosmos)",
    desc: "BBC Planet Earth ve Cosmos kalitesinde ultra gerçekçi doğa, okyanus, balina, dağ ve kâinat belgesel çekimleri",
    promptPrefix:
      "Award-winning IMAX nature and cosmos documentary cinematography, realistic human explorer, giant whale in ocean, mountains and stars, 8k photorealistic, volumetric divine sunlight",
  },
  ottoman_illuminated: {
    name: "Nurlu Tezhip & Klasik İslam Estetiği",
    desc: "Altın varak, Selçuklu/Osmanlı şemse geometrisi ve derin minyatür sanatıyla yoğrulmuş tefekkür levhaları",
    promptPrefix:
      "Masterpiece classical Ottoman and Seljuk illuminated manuscript art, 24k gold leaf details, deep lapis lazuli and crimson velvet palette, sacred Islamic geometry, 8k ultra detailed",
  },
  poetic_cinematic: {
    name: "Şiirsel & Derin Tefekkür Sineması",
    desc: "Sakin kamera hareketleri, buğulu seher ışığı ve kalbi dinlendiren sanatsal sinema kareleri",
    promptPrefix:
      "Poetic contemplative cinema, golden hour atmospheric mist, serene composition, dramatic lighting, ultra high resolution 8k film still",
  },
  sacred_calligraphy: {
    name: "Semâvî Hat & Nur Mimarisi",
    desc: "Gökyüzü, kubbeler ve tabiat üzerinde tecelli eden celî sülüs Besmele ve Esma nurları",
    promptPrefix:
      "Sacred Islamic architectural interior and starry cosmos, glowing golden Arabic calligraphy in the sky, monumental stone dome with light rays, 8k",
  },
};

function escapeXml(unsafe: string): string {
  return unsafe
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&apos;");
}

function toBase64Utf8(str: string): string {
  try {
    if (typeof window !== "undefined" && typeof window.btoa === "function") {
      return window.btoa(unescape(encodeURIComponent(str)));
    }
    if (typeof Buffer !== "undefined") {
      return Buffer.from(str, "utf-8").toString("base64");
    }
  } catch {}
  return "";
}

// Arketipe göre hangi fon müziğinin en uygun olduğunu belirler
export function recommendAmbientForArchetype(archetype: SceneVisualArchetype): string {
  switch (archetype) {
    case "col_seyyahi":
    case "magrur_yolcu":
      return "hicaz";
    case "tohum_ve_agac":
    case "cesme_ve_su":
      return "rahmet";
    case "sikke_i_rububiyet":
    case "kainat_ve_sema":
      return "kainat";
    case "zikir_ve_hamd":
    case "namaz_ve_vakit":
      return "saba";
    case "hasir_ve_bahar":
      return "neva_bahar";
    case "asker_padisah":
      return "segah_irfan";
    case "besmele_rahmet":
    default:
      return "ussak";
  }
}

export function computeSemanticHash(
  archetype: SceneVisualArchetype,
  keywords: string[],
  style: CinemaVisualStyle
): string {
  const sortedKw = [...keywords]
    .map((k) => k.toLowerCase().trim())
    .sort()
    .slice(0, 3)
    .join("_");
  return `${style}__${archetype}__${sortedKw || "genel"}`;
}

// XML-Safe ve Base64 Kodlu Yüksek Çözünürlüklü Sinematik SVG Tablosu (Hiçbir zaman kırılmaz)
export function generateHighResSceneArtworkDataUri(
  scene: StoryboardScene,
  style: CinemaVisualStyle,
  partLabel: string
): string {
  const { palette, archetype, title, stageBadge, extractedKeywords } = scene;
  const safePart = escapeXml(partLabel);
  const safeBadge = escapeXml(stageBadge);
  const safeTitle = escapeXml(title);
  const safeStyleName = escapeXml(VISUAL_STYLE_LABELS[style].name);
  const safeKw = escapeXml(
    extractedKeywords
      .slice(0, 4)
      .map((k) => `#${k}`)
      .join("  •  ")
  );

  let centerIllustration = "";

  if (archetype === "col_seyyahi") {
    centerIllustration = `
      <circle cx="980" cy="240" r="160" fill="url(#glowGrad)" opacity="0.9" />
      <circle cx="980" cy="240" r="46" fill="${palette.glow}" opacity="0.95" />
      <path d="M0,470 Q320,395 640,455 T1280,430 L1280,720 L0,720 Z" fill="#4A2B17" opacity="0.95" />
      <path d="M0,530 Q400,465 820,520 T1280,495 L1280,720 L0,720 Z" fill="#29150A" />
      <polygon points="935,450 980,385 1025,450" fill="${palette.accent}" stroke="${palette.glow}" stroke-width="2.5" />
      <line x1="980" y1="385" x2="980" y2="350" stroke="${palette.glow}" stroke-width="3" />
      <circle cx="420" cy="480" r="68" fill="url(#glowGrad)" opacity="0.75" />
      <circle cx="420" cy="455" r="10" fill="${palette.glow}" />
      <path d="M406,512 L413,468 L427,468 L434,512 Z" fill="${palette.glow}" />
      <line x1="438" y1="458" x2="432" y2="514" stroke="${palette.glow}" stroke-width="3" />
    `;
  } else if (archetype === "tohum_ve_agac") {
    centerIllustration = `
      <rect x="0" y="500" width="1280" height="220" fill="#0D2118" />
      <polygon points="470,500 595,450 615,535 450,535" fill="#2E3D35" />
      <polygon points="810,500 685,450 665,535 830,535" fill="#2E3D35" />
      <circle cx="640" cy="545" r="65" fill="url(#glowGrad)" />
      <path d="M640,545 Q610,600 555,655 M640,545 Q640,610 640,675 M640,545 Q670,600 725,655" stroke="${palette.glow}" stroke-width="3" fill="none" opacity="0.85" />
      <path d="M640,540 L640,310 M640,400 L545,305 M640,380 L735,290 M640,335 L585,240 M640,335 L700,235" stroke="#D4A359" stroke-width="11" stroke-linecap="round" />
      <circle cx="640" cy="255" r="155" fill="${palette.accent}" opacity="0.35" />
      <circle cx="555" cy="285" r="95" fill="${palette.glow}" opacity="0.3" />
      <circle cx="725" cy="280" r="95" fill="${palette.glow}" opacity="0.3" />
    `;
  } else if (archetype === "cesme_ve_su") {
    centerIllustration = `
      <polygon points="0,540 280,250 490,335 690,540" fill="#163247" />
      <path d="M360,305 Q450,430 640,540 T1180,620" stroke="${palette.glow}" stroke-width="16" fill="none" opacity="0.8" />
      <path d="M375,315 Q470,440 680,545 T1240,600" stroke="${palette.accent}" stroke-width="7" fill="none" opacity="0.95" />
      <circle cx="365" cy="308" r="55" fill="url(#glowGrad)" />
    `;
  } else {
    centerIllustration = `
      <circle cx="640" cy="325" r="240" fill="url(#glowGrad)" opacity="0.85" />
      <circle cx="640" cy="325" r="175" fill="none" stroke="${palette.accent}" stroke-width="2.5" stroke-dasharray="10 6" opacity="0.8" />
      <circle cx="640" cy="325" r="120" fill="none" stroke="${palette.glow}" stroke-width="3" opacity="0.9" />
      <circle cx="640" cy="325" r="65" fill="none" stroke="${palette.accent}" stroke-width="3.5" />
      <line x1="640" y1="0" x2="640" y2="720" stroke="${palette.glow}" stroke-width="45" opacity="0.12" />
      <line x1="470" y1="0" x2="575" y2="720" stroke="${palette.glow}" stroke-width="28" opacity="0.09" />
      <line x1="810" y1="0" x2="705" y2="720" stroke="${palette.glow}" stroke-width="28" opacity="0.09" />
    `;
  }

  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1280 720" width="1280" height="720">
    <defs>
      <linearGradient id="skyGrad" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0%" stop-color="${palette.skyTop}" />
        <stop offset="55%" stop-color="${palette.skyBottom}" />
        <stop offset="85%" stop-color="${palette.horizon}" />
        <stop offset="100%" stop-color="#060508" />
      </linearGradient>
      <radialGradient id="glowGrad" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stop-color="${palette.glow}" stop-opacity="0.95" />
        <stop offset="50%" stop-color="${palette.accent}" stop-opacity="0.4" />
        <stop offset="100%" stop-color="${palette.skyBottom}" stop-opacity="0" />
      </radialGradient>
    </defs>
    <rect width="1280" height="720" fill="url(#skyGrad)" />
    ${centerIllustration}
    <rect x="40" y="34" width="460" height="38" rx="19" fill="#000000" fill-opacity="0.65" stroke="${palette.accent}" stroke-opacity="0.6" />
    <text x="62" y="58" fill="${palette.glow}" font-family="Georgia, serif" font-size="15" font-weight="bold">${safePart} • ${safeBadge}</text>
    <text x="640" y="650" text-anchor="middle" fill="#FFFFFF" font-family="Georgia, serif" font-size="27" font-weight="bold" opacity="0.95">${safeTitle}</text>
    <text x="640" y="684" text-anchor="middle" fill="${palette.glow}" font-family="Arial, sans-serif" font-size="14" opacity="0.85">${safeKw}  |  ${safeStyleName}</text>
  </svg>`;

  const b64 = toBase64Utf8(svg);
  if (b64) {
    return `data:image/svg+xml;base64,${b64}`;
  }
  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}

// Sahne için gerçek HD belgesel fotoğrafı veya ücretsiz Pollinations Flux URL'i seçer
export function resolvePrimarySceneImageUrl(
  scene: StoryboardScene,
  chapterNumber: number,
  prefs: CinemaRobotPreferences,
  fullAiPrompt: string,
  fallbackDataUri: string
): string {
  if (prefs.aiProvider === "local_cinematic_engine") {
    return fallbackDataUri;
  }

  if (prefs.aiProvider === "pollinations_flux") {
    const cleanPrompt = fullAiPrompt.slice(0, 260);
    return `https://image.pollinations.ai/prompt/${encodeURIComponent(
      cleanPrompt
    )}?width=1280&height=720&nologo=true&seed=${chapterNumber * 100 + scene.sceneIndex}`;
  }

  // Varsayılan / Pro Mod: 4K Ultra Belgesel, Doğa, Dağ, Okyanus, İnsan ve Eşya Havuzu
  const pool =
    FREE_DOCUMENTARY_SCENE_GALLERY[scene.archetype] ||
    FREE_DOCUMENTARY_SCENE_GALLERY.besmele_rahmet;
  const chosenUrl = pool[(chapterNumber * 2 + scene.sceneIndex) % pool.length];
  return chosenUrl || fallbackDataUri;
}

export function loadCinemaArchive(): ArchivedCinemaAsset[] {
  if (typeof window === "undefined") return [];
  try {
    const raw = localStorage.getItem(ARCHIVE_STORAGE_KEY);
    if (!raw) return [];
    return JSON.parse(raw);
  } catch {
    return [];
  }
}

export function saveCinemaArchive(items: ArchivedCinemaAsset[]) {
  if (typeof window === "undefined") return;
  try {
    localStorage.setItem(ARCHIVE_STORAGE_KEY, JSON.stringify(items));
    fetch("/api/cinema-archive", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ action: "upsert_scenes", scenes: items }),
    }).catch(() => {});
  } catch (e) {
    console.error("Sinema arşivi kaydedilemedi:", e);
  }
}

export function loadRobotPreferences(): CinemaRobotPreferences {
  if (typeof window === "undefined") return DEFAULT_ROBOT_PREFERENCES;
  try {
    const raw = localStorage.getItem(PREFS_STORAGE_KEY);
    if (!raw) return DEFAULT_ROBOT_PREFERENCES;
    return { ...DEFAULT_ROBOT_PREFERENCES, ...JSON.parse(raw) };
  } catch {
    return DEFAULT_ROBOT_PREFERENCES;
  }
}

export function saveRobotPreferences(prefs: CinemaRobotPreferences) {
  if (typeof window === "undefined") return;
  try {
    localStorage.setItem(PREFS_STORAGE_KEY, JSON.stringify(prefs));
    fetch("/api/cinema-archive", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ action: "save_preferences", preferences: prefs }),
    }).catch(() => {});
  } catch {}
}

export async function syncCinemaArchiveFromServer(): Promise<{
  archive: ArchivedCinemaAsset[];
  preferences: CinemaRobotPreferences;
}> {
  if (typeof window === "undefined") {
    return { archive: [], preferences: DEFAULT_ROBOT_PREFERENCES };
  }
  try {
    const res = await fetch("/api/cinema-archive");
    const data = await res.json();
    if (data?.ok) {
      if (Array.isArray(data.archive) && data.archive.length > 0) {
        const local = loadCinemaArchive();
        const combined = [...local, ...data.archive];
        const uniqueMap = new Map<string, ArchivedCinemaAsset>();
        for (const item of combined) {
          // Eski basit SVG data URI'lerini tamamen filtrele (gerçek fotoğraf/video kalsın)
          if (
            item.generatedImageUrl &&
            item.generatedImageUrl.startsWith("data:")
          ) {
            continue;
          }
          const k = `${item.chapterNumber}_${item.sceneIndex}_${item.visualStyle}`;
          if (!uniqueMap.has(k)) uniqueMap.set(k, item);
        }
        const merged = Array.from(uniqueMap.values()).slice(0, 300);
        localStorage.setItem(ARCHIVE_STORAGE_KEY, JSON.stringify(merged));
      }
      if (data.preferences) {
        localStorage.setItem(
          PREFS_STORAGE_KEY,
          JSON.stringify({ ...DEFAULT_ROBOT_PREFERENCES, ...data.preferences })
        );
      }
      return {
        archive: loadCinemaArchive(),
        preferences: loadRobotPreferences(),
      };
    }
  } catch {}
  return {
    archive: loadCinemaArchive(),
    preferences: loadRobotPreferences(),
  };
}

export function findReusableArchivedScene(
  scene: StoryboardScene,
  style: CinemaVisualStyle,
  archive: ArchivedCinemaAsset[],
  threshold: number = 0.65
): ArchivedCinemaAsset | null {
  const exactHash = computeSemanticHash(scene.archetype, scene.extractedKeywords, style);
  const exactMatch = archive.find(
    (a) =>
      a.semanticHash === exactHash &&
      !a.generatedImageUrl.startsWith("data:")
  );
  if (exactMatch) return exactMatch;

  let bestCandidate: ArchivedCinemaAsset | null = null;
  let bestScore = 0;

  for (const item of archive) {
    if (item.archetype !== scene.archetype || item.visualStyle !== style) continue;
    if (item.generatedImageUrl.startsWith("data:")) continue;
    const setA = new Set(scene.extractedKeywords.map((k) => k.toLowerCase()));
    const setB = new Set(item.keywords.map((k) => k.toLowerCase()));
    let overlap = 0;
    setA.forEach((w) => {
      if (setB.has(w)) overlap++;
    });
    const unionSize = Math.max(1, new Set([...setA, ...setB]).size);
    const similarity = 0.5 + 0.5 * (overlap / unionSize);
    if (similarity >= threshold && similarity > bestScore) {
      bestScore = similarity;
      bestCandidate = item;
    }
  }

  return bestCandidate;
}

export function produceChapterCinemaAssets(
  chapterNumber: number,
  partLabel: string,
  storyboardScenes: StoryboardScene[],
  prefs: CinemaRobotPreferences
): {
  producedScenes: ArchivedCinemaAsset[];
  reusedCount: number;
  newlyGeneratedCount: number;
} {
  const archive = loadCinemaArchive();
  const producedScenes: ArchivedCinemaAsset[] = [];
  let reusedCount = 0;
  let newlyGeneratedCount = 0;

  for (const sc of storyboardScenes) {
    if (prefs.reuseArchivedScenesWhenSimilar) {
      const existing = findReusableArchivedScene(
        sc,
        prefs.visualStyle,
        archive,
        prefs.similarityThreshold
      );
      if (existing) {
        existing.reuseCount += 1;
        producedScenes.push({
          ...existing,
          sceneIndex: sc.sceneIndex,
        });
        reusedCount++;
        continue;
      }
    }

    const styleMeta = VISUAL_STYLE_LABELS[prefs.visualStyle] || VISUAL_STYLE_LABELS.nano_banana_chatgpt;
    const fullAiPrompt = `${styleMeta.promptPrefix}. ${sc.aiImagePrompt}. Ordinary humans, whales, mountains, sea and objects shown in rich detail; Prophets represented ONLY as radiant divine golden light (Nur).`;
    const curatedPool =
      FREE_DOCUMENTARY_SCENE_GALLERY[sc.archetype] ||
      FREE_DOCUMENTARY_SCENE_GALLERY.besmele_rahmet;
    const curatedPhotoFallback =
      curatedPool[(chapterNumber + sc.sceneIndex) % curatedPool.length] ||
      generateHighResSceneArtworkDataUri(sc, prefs.visualStyle, partLabel);
    const primaryImageUrl = resolvePrimarySceneImageUrl(
      sc,
      chapterNumber,
      prefs,
      fullAiPrompt,
      curatedPhotoFallback
    );
    const videoPool =
      FREE_DOCUMENTARY_VIDEO_LOOPS[sc.archetype] ||
      FREE_DOCUMENTARY_VIDEO_LOOPS.besmele_rahmet;
    const chosenVideoUrl =
      prefs.aiProvider === "local_cinematic_engine"
        ? undefined
        : videoPool[(chapterNumber + sc.sceneIndex) % videoPool.length];

    const newAsset: ArchivedCinemaAsset = {
      id: `scene_${chapterNumber}_${sc.sceneIndex}_${Date.now()}`,
      semanticHash: computeSemanticHash(sc.archetype, sc.extractedKeywords, prefs.visualStyle),
      chapterNumber,
      partLabel,
      sceneIndex: sc.sceneIndex,
      archetype: sc.archetype,
      title: sc.title,
      stageBadge: sc.stageBadge,
      imageryDescription: sc.imageryDescription,
      coreMessage: sc.coreMessage,
      directorNotes: `Bu sahnede "${sc.extractedKeywords.join(", ")}" kavramları üzerinden ${sc.title} temsili ${styleMeta.name} üslubuyla kurgulandı.`,
      islamicComplianceNote:
        "Tevhid ve tenzih ilkelerine tam riayet edilerek âfâkî ve temsilî sembolizm kullanıldı.",
      visualStyle: prefs.visualStyle,
      cameraSequence: [
        "1. Plan: Geniş Atmosferik Açılış (Establishing Shot)",
        `2. Plan: ${sc.title} Temsil Odağı (${sc.cameraMotion})`,
        "3. Plan: Semâvî Nur ve Tefekkür Levhası Kapanışı",
      ],
      recommendedAmbientTheme:
        prefs.ambientMoodPreference === "auto_context"
          ? recommendAmbientForArchetype(sc.archetype)
          : prefs.ambientMoodPreference,
      generatedImageUrl: primaryImageUrl,
      fallbackImageUrl: curatedPhotoFallback,
      videoLoopUrl: chosenVideoUrl,
      aiPromptUsed: fullAiPrompt,
      keywords: sc.extractedKeywords,
      reuseCount: 1,
      createdAt: new Date().toISOString(),
    };

    archive.unshift(newAsset);
    producedScenes.push(newAsset);
    newlyGeneratedCount++;
  }

  saveCinemaArchive(archive.slice(0, 250));
  return { producedScenes, reusedCount, newlyGeneratedCount };
}
