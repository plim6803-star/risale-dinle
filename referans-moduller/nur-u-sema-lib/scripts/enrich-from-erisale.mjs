// eRisale.com'dan Sözler kitabının temiz metinlerini (Arapça ibareler ve ayetlerle birlikte) çeker
// ve PDF'ten çıkarılan sayfa kenarı lügatçeleriyle birleştirir.
import fs from "node:fs";

const BOOK = "sozler";
const bookFile = `public/data/kulliyat/${BOOK}.json`;
const bookData = JSON.parse(fs.readFileSync(bookFile, "utf8"));

const ERISALE_SECTIONS = [
  { id: 2, slug: "birinci-soz", sub: "birinci-soz-ondorduncu-lemanin-ikinci-makami" },
  { id: 3, slug: "ikinci-soz" },
  { id: 4, slug: "ucuncu-soz" },
  { id: 5, slug: "dorduncu-soz" },
  { id: 6, slug: "besinci-soz" },
  { id: 7, slug: "altinci-soz" },
  { id: 8, slug: "yedinci-soz" },
  { id: 9, slug: "sekizinci-soz" },
  { id: 10, slug: "dokuzuncu-soz" },
  { id: 11, slug: "onuncu-soz-hasir-bahsi" },
  { id: 12, slug: "onbirinci-soz" },
  { id: 13, slug: "onikinci-soz" },
  { id: 14, slug: "onucuncu-soz" },
  { id: 15, slug: "ondorduncu-soz" },
  { id: 16, slug: "onbesinci-soz" },
  { id: 17, slug: "onaltinci-soz" },
  { id: 18, slug: "onyedinci-soz" },
  { id: 19, slug: "onsekizinci-soz" },
  { id: 20, slug: "ondokuzuncu-soz" },
  { id: 21, slug: "yirminci-soz" },
  { id: 22, slug: "yirmibirinci-soz" },
  { id: 23, slug: "yirmiikinci-soz" },
  { id: 24, slug: "yirmiucuncu-soz" },
  { id: 25, slug: "yirmidorduncu-soz" },
  { id: 26, slug: "yirmibesinci-soz-mucizat-i-kuraniye-risalesi" },
  { id: 27, slug: "yirmialtinci-soz-kader-risalesi" },
  { id: 28, slug: "yirmiyedinci-soz-ictihad-risalesi" },
  { id: 29, slug: "yirmisekizinci-soz" },
  { id: 30, slug: "yirmidokuzuncu-soz" },
  { id: 31, slug: "otuzuncu-soz" },
  { id: 32, slug: "otuzbirinci-soz" },
  { id: 33, slug: "otuzikinci-soz" },
  { id: 34, slug: "otuzucuncu-soz-pencereler-risalesi" },
  { id: 35, slug: "lemeat" },
  { id: 36, slug: "konferans" },
  { id: 37, slug: "dua" },
];

async function fetchErisaleParagraphs(urlPath) {
  const url = `https://www.erisale.com/tr/sozler/${urlPath}`;
  const r = await fetch(url, { headers: { "User-Agent": "Mozilla/5.0" } });
  if (!r.ok) return [];
  const t = await r.text();
  const blocks = [...t.matchAll(/<div data-block-index="(\d+)"[^>]*>([\s\S]*?)(?=<div data-block-index="|<\/main>)/g)];
  const out = [];
  for (const b of blocks) {
    const inner = b[2].replace(/<button[\s\S]*?<\/button>/g, "").replace(/<svg[\s\S]*?<\/svg>/g, "");
    const idMatch = inner.match(/id="([^"]+)"/);
    const blockType = idMatch?.[1] || "";
    if (blockType.startsWith("ThematicBreak")) {
      out.push("* * *");
      continue;
    }
    const text = inner
      .replace(/<[^>]+>/g, " ")
      .replace(/&#x27;/g, "’")
      .replace(/&quot;/g, "“")
      .replace(/&amp;/g, "&")
      .replace(/\s+/g, " ")
      .trim();
    if (!text) continue;
    out.push(text);
  }
  return out;
}

for (const item of ERISALE_SECTIONS) {
  const sec = bookData.sections.find((s) => s.id === item.id);
  if (!sec) continue;
  const p1 = await fetchErisaleParagraphs(item.slug);
  let p2 = [];
  if (item.sub) {
    p2 = await fetchErisaleParagraphs(item.sub);
  }
  const combined = [...p1, ...(p2.length ? ["* * *", ...p2] : [])];
  if (combined.length > 5) {
    sec.paragraphs = combined;
    console.log(`Güncellendi: ${sec.title} -> ${combined.length} temiz paragraf (eRisale + PDF Lügat: ${sec.lugat.length} kelime)`);
  }
}

fs.writeFileSync(bookFile, JSON.stringify(bookData));
console.log("Kaydedildi:", bookFile);
