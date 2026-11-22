// ============================================================================
// Nûr-u Sema: Başka Bilgisayarda Ses Kayıtlarını ve Fon Müziklerini Otomatik
// İndirme & Kurma Aracı (scripts/setup-audio.mjs)
// ----------------------------------------------------------------------------
// Kullanım:
//   npm run setup-audio          -> Fon müziklerini üretir + İlk 10 Söz MP3'lerini indirir
//   npm run setup-audio -- --all -> Fon müziklerini üretir + Tüm Sözler MP3'lerini indirir
// ============================================================================

import fs from "node:fs";
import path from "node:path";
import { spawnSync } from "node:child_process";

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

const args = process.argv.slice(2);
const downloadAll = args.includes("--all");

console.log("====================================================================");
console.log(" Nûr-u Sema | Ses Kayıtları & Fon Müzikleri Otomatik Kurulum Aracı");
console.log("====================================================================\n");

// 1. ADIM: Fon Müziklerini (WAV) Yerel Olarak Üret
console.log("1/2 • 6 Makam Enstrümantal Fon Müzikleri (public/ses/fon/*.wav) üretiliyor...");
const genRes = spawnSync(process.execPath, ["scripts/generate-ambient-music.mjs"], {
  stdio: "inherit",
});
if (genRes.status === 0) {
  console.log("✓ Fon müzikleri başarıyla oluşturuldu.\n");
} else {
  console.warn("⚠ Fon müzikleri üretilirken bir uyarı oluştu, devam ediliyor...\n");
}

// 2. ADIM: Sesli Okuma MP3 Kayıtlarını İndir (public/data/sorularlarisale_sozler.json üzerinden)
const playlistPath = path.resolve("public/data/sorularlarisale_sozler.json");
if (!fs.existsSync(playlistPath)) {
  console.error("Hata: public/data/sorularlarisale_sozler.json bulunamadı!");
  process.exit(1);
}

const playlist = JSON.parse(fs.readFileSync(playlistPath, "utf-8"));

// Odaklanılan İlk 10 Söz (veya --all parametresiyle tümü)
const first10Patterns = [
  /1\.Söz/i,
  /14\.Lem/i,
  /2\.Söz/i,
  /3\.Söz/i,
  /4\.Söz/i,
  /5\.Söz/i,
  /6\.Söz/i,
  /7\.Söz/i,
  /8\.Söz/i,
  /9\.Söz/i,
  /10\.Söz/i,
];

const targetTracks = playlist.filter((item) => {
  if (!item.mp3) return false;
  if (downloadAll) return true;
  // Varsayılan olarak A. Köseoğlu'nun ilk 10 Söz okumalarını indir
  const isKoseoglu = (item.artist || "").toLowerCase().includes("köseoğlu");
  const isFirst10 = first10Patterns.some((re) => re.test(item.title || ""));
  return isKoseoglu && isFirst10;
});

console.log(
  `2/2 • Sesli Külliyat MP3 kayıtları indiriliyor (${targetTracks.length} kayıt seçildi)...`
);
console.log("     (Tüm okuyucuları indirmek isterseniz: npm run setup-audio -- --all)\n");

const outDir = path.resolve("public/ses/koseoglu/sozler");
fs.mkdirSync(outDir, { recursive: true });

for (let i = 0; i < targetTracks.length; i++) {
  const track = targetTracks[i];
  const url = track.mp3;
  const fileName = path.basename(new URL(url).pathname);
  const destPath = path.join(outDir, fileName);

  const cleanTitle = (track.title || "")
    .replace(/<[^>]+>/g, " ")
    .replace(/\s+/g, " ")
    .trim();

  if (fs.existsSync(destPath) && fs.statSync(destPath).size > 50000) {
    console.log(`[${i + 1}/${targetTracks.length}] Zaten mevcut: ${fileName} (${cleanTitle})`);
    continue;
  }

  console.log(`[${i + 1}/${targetTracks.length}] İndiriliyor: ${cleanTitle} -> ${fileName}`);
  try {
    const res = await fetch(url);
    if (!res.ok) {
      console.warn(`   ⚠ İndirilemedi (HTTP ${res.status}): ${url}`);
      continue;
    }
    const buf = Buffer.from(await res.arrayBuffer());
    fs.writeFileSync(destPath, buf);
    console.log(
      `   ✓ Tamamlandı (${(buf.length / (1024 * 1024)).toFixed(2)} MB)`
    );
  } catch (err) {
    console.warn(`   ⚠ Bağlantı hatası (${fileName}):`, err.message);
  }
}

console.log("\n====================================================================");
console.log(" ✓ Kurulum Tamamlandı! Uygulamayı başlatmak için: npm run dev");
console.log("====================================================================");
