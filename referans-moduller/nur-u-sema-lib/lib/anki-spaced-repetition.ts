// Anki Spaced Repetition (SM-2 Algoritması) Motoru & Kelime Hafıza Sistemi
// Lügat kelimelerini aralıklı tekrar yöntemiyle kalıcı hafızaya aktarır.

export interface FlashcardProgress {
  word: string;
  meaning: string;
  contextSentence?: string;
  repetitionCount: number; // kaç kez tekrarlandı
  intervalDays: number; // bir sonraki tekrara kadar gün sayısı
  easeFactor: number; // 1.3 - 2.5 arası kolaylık katsayısı
  nextReviewDate: string; // ISO date string
  lastReviewedDate?: string;
  masteryLevel: 'yeni' | 'ogreniliyor' | 'pekisti' | 'ezberlendi';
}

const STORAGE_KEY = 'risale_lugat_flashcards_v1';

export function loadFlashcards(): Record<string, FlashcardProgress> {
  if (typeof window === 'undefined') return {};
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return getDefaultStarterCards();
    return JSON.parse(raw);
  } catch {
    return getDefaultStarterCards();
  }
}

export function saveFlashcards(cards: Record<string, FlashcardProgress>) {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(cards));
  } catch (err) {
    console.error('Flashcard save error:', err);
  }
}

// SM-2 Algoritması Hesaplaması
// rating: 1 = Çok Zor / Hatırlayamadım, 3 = İyi / Biraz Zorlandım, 5 = Kolay / Çok İyi Bildim
export function calculateSM2(
  card: FlashcardProgress,
  quality: 1 | 3 | 5
): FlashcardProgress {
  let { repetitionCount, intervalDays, easeFactor } = card;

  // Kaliteye göre ease factor güncellemesi
  // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
  const q = quality;
  easeFactor = easeFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02));
  if (easeFactor < 1.3) easeFactor = 1.3;
  if (easeFactor > 2.7) easeFactor = 2.7;

  if (quality < 3) {
    // Hatırlanamadı, sıfırdan başla
    repetitionCount = 0;
    intervalDays = 1;
  } else {
    // Hatırlandı
    if (repetitionCount === 0) {
      intervalDays = 1;
    } else if (repetitionCount === 1) {
      intervalDays = 3;
    } else {
      intervalDays = Math.round(intervalDays * easeFactor);
    }
    repetitionCount += 1;
  }

  const nextDate = new Date();
  nextDate.setDate(nextDate.getDate() + intervalDays);

  let masteryLevel: FlashcardProgress['masteryLevel'] = 'ogreniliyor';
  if (repetitionCount >= 5 && intervalDays > 21) {
    masteryLevel = 'ezberlendi';
  } else if (repetitionCount >= 3) {
    masteryLevel = 'pekisti';
  } else if (repetitionCount === 0) {
    masteryLevel = 'yeni';
  }

  return {
    ...card,
    repetitionCount,
    intervalDays,
    easeFactor,
    nextReviewDate: nextDate.toISOString(),
    lastReviewedDate: new Date().toISOString(),
    masteryLevel
  };
}

export function addWordToFlashcards(
  word: string,
  meaning: string,
  contextSentence?: string
): FlashcardProgress {
  const cards = loadFlashcards();
  const existing = cards[word];
  if (existing) return existing;

  const newCard: FlashcardProgress = {
    word,
    meaning,
    contextSentence,
    repetitionCount: 0,
    intervalDays: 0,
    easeFactor: 2.5,
    nextReviewDate: new Date().toISOString(),
    masteryLevel: 'yeni'
  };

  cards[word] = newCard;
  saveFlashcards(cards);
  return newCard;
}

export function getDueFlashcards(): FlashcardProgress[] {
  const cards = loadFlashcards();
  const now = new Date().toISOString();
  return Object.values(cards).filter((c) => c.nextReviewDate <= now);
}

function getDefaultStarterCards(): Record<string, FlashcardProgress> {
  const starterWords: Array<{ word: string; meaning: string; context: string }> = [
    {
      word: 'Bismillah',
      meaning: 'Allah’ın adıyla. Her hayırlı işin başı ve nihayetsiz tükenmez bir kuvvet ve bereket menbaı.',
      context: 'Bismillah her hayrın başıdır. Biz dahi başta ona başlarız.'
    },
    {
      word: 'Temsil',
      meaning: 'Benzerini getirerek anlatma, kıyas, derin ve soyut hakikatleri akla yaklaştırmak için hikâye tarzında dürbün kılma.',
      context: 'Şu mübarek kelime İslâm nişanı olduğu gibi, bütün mevcudatın lisan-ı haliyle vird-i zebanıdır. Bir temsil ile şu hakikati dinle.'
    },
    {
      word: 'Mütevekkil',
      meaning: 'İşlerini ve hayatını Allah’a havale eden, acz ve fakrını bilip Kadîr-i Zülcelal’e dayanan kimse.',
      context: 'Biri mütevazı idi, diğeri mağrur... Mütevazı olan bir reisin namını aldı, seyahatinde selâmetle gezdi.'
    },
    {
      word: 'Nokta-i İstinad',
      meaning: 'Dayanak noktası. İnsanın hadsiz düşmanlarına karşı iltifat ve kuvvet bulduğu sığınak.',
      context: 'Evet, her insan nihayetsiz acziyle hadsiz düşmanlarına karşı bir nokta-i istinada muhtaçtır.'
    },
    {
      word: 'Nokta-i İstimdad',
      meaning: 'Yardım dileme ve imdat isteme noktası. Hadsiz ihtiyaçları karşısında rahmet ve ikram kapısı.',
      context: 'Ve nihayetsiz fakrıyla hadsiz hâcâtına karşı bir nokta-i istimdada muhtaçtır.'
    },
    {
      word: 'Kenz-i Lâyefnâ',
      meaning: 'Tükenmez, son bulmaz hazine. Cenab-ı Hakk’ın bitmeyen rahmet ve kudret hazineleri.',
      context: 'Bismillah öyle tükenmez bir hazinedir ki; senin nihayetsiz fakrını nihayetsiz bir gınaya inkılâb ettirir.'
    },
    {
      word: 'Şecere',
      meaning: 'Ağaç. Kâinat ağacı veya meyve veren her bir nebat.',
      context: 'Bütün eşya lisan-ı hal ile Bismillah der. Evet, bir tek çekirdekten dağ gibi bir şecereyi halk eden kudret...'
    },
    {
      word: 'Lisan-ı Hal',
      meaning: 'Hâl ve durum dili; konuşmadan, varlık tarzı ve işleyişiyle bir hakikati beyan etme.',
      context: 'Bütün mevcudat lisan-ı hal ile Bismillah der, Allah namına hareket eder.'
    }
  ];

  const now = new Date().toISOString();
  const result: Record<string, FlashcardProgress> = {};
  for (const item of starterWords) {
    result[item.word] = {
      word: item.word,
      meaning: item.meaning,
      contextSentence: item.context,
      repetitionCount: 0,
      intervalDays: 0,
      easeFactor: 2.5,
      nextReviewDate: now,
      masteryLevel: 'yeni'
    };
  }
  return result;
}
