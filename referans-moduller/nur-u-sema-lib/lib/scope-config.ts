// ============================================================================
// Risale-i Nur Külliyatı Çalışma Kapsamı Ayarları (Scope Configuration)
// ----------------------------------------------------------------------------
// Tüm 14 kitabın PDF'leri, JSON verileri, kenar lügatçeleri ve 33 Söz'ün
// tamamı proje içinde (`public/data/kulliyat/`, `src/lib/kulliyat-data.ts`)
// eksiksiz olarak korunmaktadır.
//
// Odaklı ve düzenli geliştirme aşamasında yalnızca Sözler kitabının
// İlk 10 Söz'ü (Birinci Söz -> Onuncu Söz) arayüzde aktif olarak gösterilir.
// İleride tüm külliyatı tekrar açmak için `ENABLE_FULL_KULLIYAT = true`
// yapmanız veya arayüzdeki "Tüm Külliyatı Göster" anahtarını açmanız yeterlidir.
// ============================================================================

export const ENABLE_FULL_KULLIYAT = false;

// Odaklı çalışma modunda aktif olan kitap(lar)
export const FOCUSED_BOOK_SLUGS = ["sozler"] as const;

// `public/data/kulliyat/sozler.json` içindeki İlk 10 Söz'ün section ID'leri:
// 2: Birinci Söz, 3: İkinci Söz, 4: Üçüncü Söz, 5: Dördüncü Söz, 6: Beşinci Söz,
// 7: Altıncı Söz, 8: Yedinci Söz, 9: Sekizinci Söz, 10: Dokuzuncu Söz, 11: Onuncu Söz
export const FOCUSED_SOZLER_SECTION_IDS = [2, 3, 4, 5, 6, 7, 8, 9, 10, 11] as const;

// İlk 10 Söz başlıkları (kontrol ve filtreleme için)
export const FOCUSED_TEN_WORDS_TITLES = [
  "Birinci Söz",
  "İkinci Söz",
  "Üçüncü Söz",
  "Dördüncü Söz",
  "Beşinci Söz",
  "Altıncı Söz",
  "Yedinci Söz",
  "Sekizinci Söz",
  "Dokuzuncu Söz",
  "Onuncu Söz",
];

export function isSectionInFocusedScope(
  bookSlug: string,
  sectionId: number,
  showAllOverride = false
): boolean {
  if (ENABLE_FULL_KULLIYAT || showAllOverride) return true;
  if (!FOCUSED_BOOK_SLUGS.includes(bookSlug as "sozler")) return false;
  return (FOCUSED_SOZLER_SECTION_IDS as readonly number[]).includes(sectionId);
}
