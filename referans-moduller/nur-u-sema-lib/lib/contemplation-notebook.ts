// Tefekkür Defteri, Cümle Notları ve Günlük Okuma/Dinleme Takibi

export interface ContemplationNote {
  id: string;
  chapterSlug: string;
  sentenceIndex?: number;
  excerpt?: string;
  noteText: string;
  createdAt: string;
  tags?: string[];
}

export interface UserStudyStreak {
  lastActiveDate: string; // YYYY-MM-DD
  streakDays: number;
  totalListeningMinutes: number;
  completedChapters: string[];
}

const NOTES_STORAGE_KEY = 'risale_tefekkur_notes_v1';
const STREAK_STORAGE_KEY = 'risale_study_streak_v1';

export function loadContemplationNotes(): ContemplationNote[] {
  if (typeof window === 'undefined') return [];
  try {
    const raw = localStorage.getItem(NOTES_STORAGE_KEY);
    if (!raw) return getDefaultNotes();
    return JSON.parse(raw);
  } catch {
    return getDefaultNotes();
  }
}

export function saveContemplationNotes(notes: ContemplationNote[]) {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(NOTES_STORAGE_KEY, JSON.stringify(notes));
  } catch (err) {
    console.error('Notes save error:', err);
  }
}

export function addContemplationNote(note: Omit<ContemplationNote, 'id' | 'createdAt'>): ContemplationNote {
  const notes = loadContemplationNotes();
  const newNote: ContemplationNote = {
    ...note,
    id: 'note_' + Date.now(),
    createdAt: new Date().toISOString()
  };
  notes.unshift(newNote);
  saveContemplationNotes(notes);
  return newNote;
}

export function deleteContemplationNote(id: string) {
  const notes = loadContemplationNotes().filter((n) => n.id !== id);
  saveContemplationNotes(notes);
}

export function loadUserStudyStreak(): UserStudyStreak {
  if (typeof window === 'undefined') {
    return {
      lastActiveDate: '',
      streakDays: 1,
      totalListeningMinutes: 25,
      completedChapters: ['birinci-soz']
    };
  }
  try {
    const raw = localStorage.getItem(STREAK_STORAGE_KEY);
    if (!raw) {
      const initial: UserStudyStreak = {
        lastActiveDate: new Date().toISOString().slice(0, 10),
        streakDays: 3,
        totalListeningMinutes: 48,
        completedChapters: ['birinci-soz']
      };
      saveUserStudyStreak(initial);
      return initial;
    }
    return JSON.parse(raw);
  } catch {
    return {
      lastActiveDate: new Date().toISOString().slice(0, 10),
      streakDays: 1,
      totalListeningMinutes: 15,
      completedChapters: []
    };
  }
}

export function saveUserStudyStreak(streak: UserStudyStreak) {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(STREAK_STORAGE_KEY, JSON.stringify(streak));
  } catch (err) {
    console.error('Streak save error:', err);
  }
}

export function logListeningSession(minutes: number, chapterSlug: string): UserStudyStreak {
  const streak = loadUserStudyStreak();
  const today = new Date().toISOString().slice(0, 10);

  if (streak.lastActiveDate !== today) {
    // yeni bir gün
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const yesterdayStr = yesterday.toISOString().slice(0, 10);

    if (streak.lastActiveDate === yesterdayStr) {
      streak.streakDays += 1;
    } else if (streak.lastActiveDate !== today) {
      streak.streakDays = 1;
    }
    streak.lastActiveDate = today;
  }

  streak.totalListeningMinutes += minutes;
  if (!streak.completedChapters.includes(chapterSlug)) {
    streak.completedChapters.push(chapterSlug);
  }

  saveUserStudyStreak(streak);
  return streak;
}

function getDefaultNotes(): ContemplationNote[] {
  return [
    {
      id: 'default_note_1',
      chapterSlug: 'birinci-soz',
      sentenceIndex: 0,
      excerpt: 'Bismillah her hayrın başıdır. Biz dahi başta ona başlarız.',
      noteText: 'Güne ve her amele Bismillah ile başlamak; nefsi aradan çıkarıp işi asıl Sahib-i Hakikîye intisap ettirmektir. Acz ve fakrımın idrakiyle huzur buldum.',
      createdAt: new Date().toISOString(),
      tags: ['Tevhid', 'Bismillah', 'İntisap']
    },
    {
      id: 'default_note_2',
      chapterSlug: 'birinci-soz',
      sentenceIndex: 5,
      excerpt: 'İpek gibi yumuşak damarlar ve kökler, sert taşları deler geçer.',
      noteText: 'Zahiren zayıf görünen tevekkül ve ubudiyet, fıtrattaki en katı engelleri rahmet-i İlahiye sayesinde eritir.',
      createdAt: new Date().toISOString(),
      tags: ['Tevekkül', 'Letafet', 'Burhan']
    }
  ];
}
