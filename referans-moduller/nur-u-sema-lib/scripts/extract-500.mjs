// "Risale-i Nur'da geçen en lâzım 500 kelime" PDF'ini JSON'a dönüştürür.
import { getDocument } from "pdfjs-dist/legacy/build/pdf.mjs";
import fs from "node:fs";

const FILE = "public/kaynaklar/Osmanlıca/ezberlenecek-500-kelime.pdf";
const doc = await getDocument({ data: new Uint8Array(fs.readFileSync(FILE)), verbosity: 0 }).promise;
const out = [];

for (let p = 1; p <= doc.numPages; p++) {
  const page = await doc.getPage(p);
  const mid = page.view[2] / 2;
  const tc = await page.getTextContent();
  const cols = [[], []];
  for (const it of tc.items) {
    if (!it.str.trim()) continue;
    cols[it.transform[4] < mid ? 0 : 1].push({ x: it.transform[4], y: Math.round(it.transform[5]), str: it.str });
  }
  for (const [ci, items] of cols.entries()) {
    const rows = [];
    for (const it of items) {
      let r = rows.find((r) => Math.abs(r.y - it.y) <= 2);
      if (!r) rows.push((r = { y: it.y, items: [] }));
      r.items.push(it);
    }
    rows.sort((a, b) => b.y - a.y);
    let group = null;
    for (const r of rows) {
      r.items.sort((a, b) => a.x - b.x);
      const text = r.items.map((i) => i.str).join(" ").replace(/\s+/g, " ").replace(/\s+([,;.)])/g, "$1").replace(/([(])\s+/g, "$1").trim();
      const g = text.match(/^(Birinci|İkinci|Üçüncü|Dördüncü|Beşinci|Altıncı|Yedinci|Sekizinci|Dokuzuncu|Onuncu) 50 kelime/i);
      if (g) { group = g[0]; continue; }
      if (/^Risâle-i Nur/.test(text)) continue;
      const eq = text.indexOf(" = ");
      if (eq > 0) {
        const term = text.slice(0, eq).replace(/\.$/, "").trim();
        out.push({ term, meaning: text.slice(eq + 3).trim(), group: group ?? `Sayfa ${p}`, page: p, col: ci });
      } else if (out.length && out[out.length - 1].page === p && out[out.length - 1].col === ci) {
        out[out.length - 1].meaning += " " + text; // satır devamı
      }
    }
  }
}
const clean = out.map(({ col, ...e }) => ({ ...e, term: e.term.replace(/\s+/g, " "), meaning: e.meaning.replace(/\s+/g, " ").trim() }));
fs.mkdirSync("public/data", { recursive: true });
fs.writeFileSync("public/data/lugat-500.json", JSON.stringify(clean, null, 1));
console.log(clean.length, "kelime");
console.log(clean.slice(0, 5), clean.slice(-3));
