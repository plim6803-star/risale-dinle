import fs from "fs";
import path from "path";
import {
  SEED_BOOKS,
  SEED_NARRATORS,
  SEED_CHAPTERS,
} from "@/lib/kulliyat-data";

export interface Book {
  id: number;
  slug: string;
  title: string;
  ottomanTitle: string;
  subtitle: string;
  description: string;
  category: string;
  totalChapters: number;
  totalDurationSeconds: number;
  coverColor: string;
  accentGold: string;
  collectionOrder: number;
}

export interface Narrator {
  id: number;
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

export interface Chapter {
  id: number;
  bookId: number;
  chapterNumber: number;
  partLabel: string;
  title: string;
  sectionSubtitle: string;
  durationSeconds: number;
  excerptText: string;
  keyTermsJson: string;
  externalAudioUrl: string;
  externalDownloadUrl: string;
  archiveOrgUrl: string;
  fileSizeMb: string;
}

export interface ListeningProgress {
  id: number;
  chapterId: number;
  narratorId: number;
  currentPositionSeconds: number;
  completed: boolean;
  isBookmarked: boolean;
  bookmarkNote: string | null;
  lastListenedAt: string;
}

export interface SavedSerhItem {
  id: string;
  bookSlug: string;
  bookTitle: string;
  sectionId?: number;
  partLabel: string;
  chapterTitle: string;
  targetText: string;
  title: string;
  hulasa: string;
  temsilAnalizi: string;
  asrimizaDers: string;
  vecize: string;
  customAnswer?: string;
  providerUsed: string;
  createdAt: string;
}

export interface UserSettings {
  id: number;
  theme: string;
  playbackSpeed: string;
  defaultNarratorId: number;
  autoPlayNext: boolean;
  rememberPosition: boolean;
  skipForwardSeconds: number;
  skipBackwardSeconds: number;
  sleepTimerMinutes: number;
  audioQuality: string;
  streamSourceMode: string;
  customStreamBaseUrl: string;
  voiceSynthesisEnabled: boolean;
  ambientBackgroundSound: string;
  ambientVolume: number;
  showTextFollow: boolean;
  // Yeni Özelleştirme, Takip, AI API ve Panel Yerleşimi Ayarları
  autoScrollFollow?: boolean;
  followIndicatorStyle?: "line" | "arrow" | "both" | "highlight_only";
  lugatWordColorMode?: "erisale_crimson" | "gold" | "emerald";
  showLugatTooltipOnHover?: boolean;
  aiProvider?: "local_kulliyat" | "gemini" | "openai" | "anthropic" | "openrouter";
  geminiApiKey?: string;
  openaiApiKey?: string;
  anthropicApiKey?: string;
  openrouterApiKey?: string;
  panelLayoutOrder?: string[]; // Örn: ["left_nav", "center_stage", "right_companion"]
  studioPanelOrder?: string[]; // /oku stüdyosu için: ["toc", "cinema", "reader", "lugat", "serh_notebook"]
  activeLayer?: "v1_okuma" | "v2_lugat" | "v3_serh" | "v4_sinema";
  cinemaOverlayOptions?: {
    showFloatingFlowText?: boolean;
    showLugatTooltipInCinema?: boolean;
    showQuickSerhInCinema?: boolean;
    showStoryboardStrip?: boolean;
    floatingTextOpacity?: number;
  };
  visiblePanels?: {
    narratorsStrip?: boolean;
    textFollowPanel?: boolean;
    chaptersList?: boolean;
    rightCompanionPanel?: boolean;
  };
  visibleButtons?: {
    serhButton?: boolean;
    bookmarkButton?: boolean;
    lugatBadgeButton?: boolean;
    cinemaButton?: boolean;
    pronounceButton?: boolean;
  };
}

export interface DatabaseState {
  books: Book[];
  narrators: Narrator[];
  chapters: Chapter[];
  progress: ListeningProgress[];
  settings: UserSettings;
  savedSerhler?: SavedSerhItem[];
}

const DATA_DIR = path.join(process.cwd(), ".data");
const DATA_FILE = path.join(DATA_DIR, "db_v2.json");

function getDefaultState(): DatabaseState {
  const books: Book[] = SEED_BOOKS.map((b, idx) => ({
    id: idx + 1,
    slug: b.slug,
    title: b.title,
    ottomanTitle: b.ottomanTitle,
    subtitle: b.subtitle,
    description: b.description,
    category: b.category,
    totalChapters: 0,
    totalDurationSeconds: 0,
    coverColor: b.coverColor,
    accentGold: b.accentGold,
    collectionOrder: b.collectionOrder,
  }));

  const bookIdMap = new Map<string, number>();
  books.forEach((b) => bookIdMap.set(b.slug, b.id));

  const narrators: Narrator[] = SEED_NARRATORS.map((n, idx) => ({
    id: idx + 1,
    slug: n.slug,
    name: n.name,
    title: n.title,
    styleDescription: n.styleDescription,
    voicePitchHz: n.voicePitchHz,
    speechRateMultiplier: n.speechRateMultiplier,
    streamSourceLabel: n.streamSourceLabel,
    externalArchiveUrl: n.externalArchiveUrl,
    avatarInitials: n.avatarInitials,
  }));

  const chapters: Chapter[] = SEED_CHAPTERS.map((c, idx) => {
    const bookId = bookIdMap.get(c.bookSlug) || 1;
    return {
      id: idx + 1,
      bookId,
      chapterNumber: c.chapterNumber,
      partLabel: c.partLabel,
      title: c.title,
      sectionSubtitle: c.sectionSubtitle,
      durationSeconds: c.durationSeconds,
      excerptText: c.excerptText,
      keyTermsJson: JSON.stringify(c.keyTerms),
      externalAudioUrl: c.externalAudioUrl,
      externalDownloadUrl: c.externalDownloadUrl,
      archiveOrgUrl: c.archiveOrgUrl,
      fileSizeMb: c.fileSizeMb,
    };
  });

  // Calculate totals per book
  for (const b of books) {
    const bChapters = chapters.filter((c) => c.bookId === b.id);
    b.totalChapters = bChapters.length;
    b.totalDurationSeconds = bChapters.reduce((sum, c) => sum + c.durationSeconds, 0);
  }

  const settings: UserSettings = {
    id: 1,
    theme: "parchment",
    playbackSpeed: "1.0",
    defaultNarratorId: narrators[0]?.id ?? 1,
    autoPlayNext: true,
    rememberPosition: true,
    skipForwardSeconds: 15,
    skipBackwardSeconds: 15,
    sleepTimerMinutes: 0,
    audioQuality: "320kbps",
    streamSourceMode: "hybrid",
    customStreamBaseUrl:
      "https://archive.org/download/podcast_isaratl-icaz-risale-i-nur_1016441224",
    voiceSynthesisEnabled: false,
    ambientBackgroundSound: "ney_huzur",
    ambientVolume: 35,
    showTextFollow: true,
    autoScrollFollow: true,
    followIndicatorStyle: "both",
    lugatWordColorMode: "erisale_crimson",
    showLugatTooltipOnHover: true,
    aiProvider: "local_kulliyat",
    geminiApiKey: "",
    openaiApiKey: "",
    anthropicApiKey: "",
    openrouterApiKey: "",
    panelLayoutOrder: ["left_nav", "center_stage", "right_companion"],
    studioPanelOrder: ["toc", "cinema", "reader", "lugat", "serh_notebook"],
    activeLayer: "v1_okuma",
    cinemaOverlayOptions: {
      showFloatingFlowText: true,
      showLugatTooltipInCinema: true,
      showQuickSerhInCinema: true,
      showStoryboardStrip: true,
      floatingTextOpacity: 85,
    },
    visiblePanels: {
      narratorsStrip: true,
      textFollowPanel: true,
      chaptersList: true,
      rightCompanionPanel: true,
    },
    visibleButtons: {
      serhButton: true,
      bookmarkButton: true,
      lugatBadgeButton: true,
      cinemaButton: true,
      pronounceButton: true,
    },
  };

  return {
    books,
    narrators,
    chapters,
    progress: [],
    settings,
    savedSerhler: [],
  };
}

export function getDb(): DatabaseState {
  try {
    if (!fs.existsSync(DATA_DIR)) {
      fs.mkdirSync(DATA_DIR, { recursive: true });
    }
    if (!fs.existsSync(DATA_FILE)) {
      const initial = getDefaultState();
      fs.writeFileSync(DATA_FILE, JSON.stringify(initial, null, 2), "utf8");
      return initial;
    }
    const raw = fs.readFileSync(DATA_FILE, "utf8");
    return JSON.parse(raw);
  } catch (err) {
    console.error("Error reading db file, using default:", err);
    return getDefaultState();
  }
}

export function saveDb(state: DatabaseState): void {
  try {
    if (!fs.existsSync(DATA_DIR)) {
      fs.mkdirSync(DATA_DIR, { recursive: true });
    }
    fs.writeFileSync(DATA_FILE, JSON.stringify(state, null, 2), "utf8");
  } catch (err) {
    console.error("Error saving db file:", err);
  }
}

export function ensureSeeded(): void {
  getDb();
}
