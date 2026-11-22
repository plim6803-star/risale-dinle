// Risale-i Nur PDF'lerinden metni ve sayfa kenarı lügatçesini çıkarıp uygulama içi JSON verisine dönüştürür.
// Kullanım: node scripts/extract-kulliyat.mjs [slug]
import { getDocument } from "pdfjs-dist/legacy/build/pdf.mjs";
import fs from "node:fs";
import path from "node:path";

const SRC = "public/kaynaklar/Risale-i Nur Külliyat";
const OUT = "public/data/kulliyat";
fs.mkdirSync(OUT, { recursive: true });

// Eski Türkçe font kodlamasını düzelt
function fixTr(s) {
  return s
    .replace(/(?<=[A-ZÇĞÖÜ‹])fi|fi(?=[A-ZÇĞÖÜ‹])/g, "Ş")
    .replace(/^fi(?=[a-zçğıöüâîû›¤])/g, "Ş")
    .replace(/(?<=[\s“"'(‘—-])fi(?=[aeıioöuüâ›][a-zçğıöüâîû›¤fl]*)/g, (m, off, str) => "Ş")
    .replace(/‹/g, "İ")
    .replace(/⁄/g, "Ğ")
    .replace(/›/g, "ı")
    .replace(/¤/g, "ğ")
    .replace(/Ð/g, "Ğ")
    .replace(/fl/g, "ş")
    .replace(/ﬂ/g, "ş")
    .replace(/ﬁ/g, "Ş")
    .replace(/Þ/g, "Ş")
    .replace(/þ/g, "ş")
    .replace(/ý/g, "ı")
    .replace(/Ý/g, "İ")
    .replace(/ð/g, "ğ");
}

const TR_OK = /[A-Za-zÇĞİÖŞÜçğıöşüÂÎÛâîû0-9\s.,;:'’‘"“”!?()\-–—\/«»…%\[\]•*]/;
function isReadable(line) {
  const t = line.replace(/\s/g, "");
  if (!t) return false;
  let ok = 0;
  for (const ch of t) if (TR_OK.test(ch)) ok++;
  return ok / t.length > 0.9;
}

function trUpperToTitle(s) {
  return s
    .toLocaleLowerCase("tr")
    .split(/\s+/)
    .map((w) => (w ? w[0].toLocaleUpperCase("tr") + w.slice(1) : w))
    .join(" ");
}

const HDR_RE = /\d\s*\||\|\s*\d/;

// Arapça fontla basılmış ibareleri temizle; Besmele'yi tanı
const BESMELE_RE = /p?\s?º«\/MsôdG p ø'ªrMsôdG \$G p º°rùpH/g;
function cleanLine(t) {
  t = t.replace(BESMELE_RE, " Bismillâhirrahmânirrahîm ");
  const words = t.split(/\s+/);
  const good = words.filter((w) => !w || isReadable(w));
  if (good.join("").length < words.join("").length * 0.6) return isReadable(t) ? t : "";
  return good.join(" ").replace(/\s+/g, " ").trim();
}

async function pageContent(page) {
  const tc = await page.getTextContent();
  const rows = [];
  const small = [];
  const hdrItem = tc.items.find((i) => HDR_RE.test(i.str));
  const hdrY = hdrItem ? Math.round(hdrItem.transform[5]) : null;
  for (const it of tc.items) {
    if (!it.str) continue;
    const size = Math.abs(it.transform[0]);
    const x = it.transform[4];
    const y = Math.round(it.transform[5]);
    const isHdr = hdrY !== null && Math.abs(y - hdrY) <= 2;
    if (!isHdr && size < 8) {
      if (it.str.trim()) small.push({ x, y, str: it.str, font: it.fontName });
      continue;
    }
    let row = rows.find((r) => Math.abs(r.y - y) <= 3);
    if (!row) rows.push((row = { y, items: [] }));
    row.items.push({ x, str: it.str, w: it.width, hdr: isHdr });
  }
  rows.sort((a, b) => b.y - a.y); // PDF koordinatları: aşağıdan yukarı
  let header = null;
  const lines = [];
  for (const r of rows) {
    r.items.sort((a, b) => a.x - b.x);
    const last = r.items[r.items.length - 1];
    const text = fixTr(r.items.map((i) => i.str).join("")).replace(/\s+/g, " ").trim();
    if (r.items.some((i) => i.hdr)) header = text;
    else if (text) lines.push({ x: r.items[0].x, end: last.x + last.w, text });
  }
  return { header, lines, glossary: parseGlossary(small) };
}

// Sayfa kenarındaki lügatçe: kalın font "terim:" + normal font anlam
function parseGlossary(items) {
  if (!items.length) return [];
  const fc = {};
  for (const i of items) if (/:\s*$/.test(i.str)) fc[i.font] = (fc[i.font] || 0) + 1;
  const termFont = Object.entries(fc).sort((a, b) => b[1] - a[1])[0]?.[0];
  if (!termFont) return [];
  const terms = items
    .filter((it) => it.font === termFont && /:\s*$/.test(it.str) && !/^\d+\.?:?$/.test(it.str.trim()))
    .map((it) => ({ x: it.x, y: it.y, term: it.str.replace(/:\s*$/, ""), parts: [] }));
  const colXs = [];
  for (const t of terms) if (!colXs.some((c) => Math.abs(c - t.x) < 12)) colXs.push(t.x);
  colXs.sort((a, b) => a - b);
  const colOf = (x) => colXs.filter((c) => c <= x + 2).sort((a, b) => b - a)[0];
  for (const it of items) {
    if (it.font === termFont) continue;
    const col = colOf(it.x);
    if (col === undefined) continue;
    let owner = terms
      .filter((t) => Math.abs(t.x - col) < 12 && t.y >= it.y - 3)
      .sort((a, b) => a.y - b.y)[0];
    if (!owner) {
      const prevCol = colXs[colXs.indexOf(col) - 1];
      if (prevCol !== undefined) {
        owner = terms.filter((t) => Math.abs(t.x - prevCol) < 12).sort((a, b) => a.y - b.y)[0];
        if (owner) it.y -= 10000;
      }
    }
    if (owner) owner.parts.push(it);
  }
  return terms
    .map((t) => {
      t.parts.sort((a, b) => b.y - a.y || a.x - b.x);
      let m = "";
      for (const p of t.parts) {
        if (m.endsWith("-")) m = m.slice(0, -1) + p.str;
        else m += (m && !m.endsWith(" ") && !p.str.startsWith(" ") ? " " : "") + p.str;
      }
      return { term: fixTr(t.term).trim(), meaning: fixTr(m).replace(/\s+/g, " ").replace(/\s+([.,;])/g, "$1").trim() };
    })
    .filter((e) => e.term && e.meaning && e.term.length < 60 && e.meaning.length < 400);
}

// Sayfa başlığı
function parseHeader(t, bookUpper) {
  let m = t.match(/^(.+?)\s*\|\s*(\d+)\s*(.*)$/);
  if (m && m[1].trim() === bookUpper) return { page: +m[2], section: m[3].trim() };
  m = t.match(/^(.*?)\s*(\d+)\s*\|\s*(.+)$/);
  if (m && m[3].trim() === bookUpper) return { page: +m[2], section: m[1].trim() };
  return null;
}

async function extractBook(file) {
  const slug = path.basename(file, ".pdf");
  const data = new Uint8Array(fs.readFileSync(path.join(SRC, file)));
  const doc = await getDocument({ data, verbosity: 0 }).promise;

  const counts = {};
  for (let p = 10; p <= Math.min(doc.numPages, 60); p++) {
    const { header } = await pageContent(await doc.getPage(p));
    if (!header) continue;
    for (const part of header.split("|").map((s) => s.replace(/^\d+\s*|\s*\d+$/g, "").trim())) if (part) counts[part] = (counts[part] || 0) + 1;
  }
  const bookUpper = Object.entries(counts).sort((a, b) => b[1] - a[1])[0]?.[0] ?? "";

  const sections = [];
  let cur = null;
  const newSection = (raw, bookPage, p) => {
    cur = { rawTitle: raw, title: trUpperToTitle(raw), startPage: bookPage, pdfPage: p, paragraphs: [], lugat: new Map(), buf: "" };
    sections.push(cur);
  };
  const flush = () => {
    if (cur && cur.buf.trim()) cur.paragraphs.push(cur.buf.trim());
    if (cur) cur.buf = "";
  };

  for (let p = 1; p <= doc.numPages; p++) {
    const page = await doc.getPage(p);
    const { header, lines, glossary } = await pageContent(page);
    page.cleanup();
    if (!lines.length) continue;

    const h = header ? parseHeader(header, bookUpper) : null;
    let sectionName = h?.section || null;
    if (h && !h.section) {
      const a = Math.floor((h.page - 1) / 20) * 20 + 1;
      sectionName = `SAYFA ${a}–${a + 19}`;
    }
    if (!sectionName) {
      if (p < doc.numPages) {
        const nx = await pageContent(await doc.getPage(p + 1));
        const nh = nx.header ? parseHeader(nx.header, bookUpper) : null;
        sectionName = nh?.section || null;
      }
    }
    if (!sectionName) sectionName = cur ? cur.rawTitle : "GİRİŞ";
    if (/İÇİNDEKİLER|FİHRİST/.test(sectionName)) continue;
    if (!cur || cur.rawTitle !== sectionName) {
      flush();
      newSection(sectionName, h?.page ?? p, p);
    }
    for (const g of glossary) {
      if (/(\S ){5,}/.test(g.meaning) || /\d \d/.test(g.meaning)) continue;
      const k = g.term.toLocaleLowerCase("tr");
      const prev = cur.lugat.get(k);
      if (!prev || g.meaning.length > prev.meaning.length) cur.lugat.set(k, g);
    }

    const bodyLines = lines;
    if (!bodyLines.length) continue;
    const minX = Math.min(...bodyLines.map((l) => l.x));
    const ends = bodyLines.map((l) => l.end).sort((a, b) => a - b);
    const maxEnd = ends[Math.floor(ends.length * 0.75)];
    for (const l0 of bodyLines) {
      const l = { ...l0, text: cleanLine(l0.text) };
      if (!l.text || l.text.length < 3 || /^(?:onr|osr|or|sr)$/i.test(l.text)) {
        continue;
      }
      if (/^\d+$/.test(l.text)) continue;
      if (!isReadable(l.text)) {
        flush();
        if (cur.paragraphs[cur.paragraphs.length - 1] !== "﴾…﴿") cur.paragraphs.push("﴾…﴿");
        continue;
      }
      // Paragraf girintisi (indented): yeni paragraf başlat
      if (l.x - minX > 18) {
        flush();
      }
      if (cur.buf.endsWith("-")) {
        cur.buf = cur.buf.slice(0, -1) + l.text;
      } else {
        cur.buf += (cur.buf ? " " : "") + l.text;
      }
      // Satır normal satır sonundan çok kısa kalmışsa ve tire ile bitmiyorsa paragraf sonudur
      if (maxEnd - l.end > 35 && !l.text.endsWith("-")) {
        flush();
      }
    }
  }
  flush();

  const merged = [];
  for (const s of sections) {
    const prev = merged[merged.length - 1];
    if (prev && prev.rawTitle === s.rawTitle) {
      prev.paragraphs.push(...s.paragraphs);
      for (const [k, v] of s.lugat) if (!prev.lugat.has(k)) prev.lugat.set(k, v);
    } else merged.push(s);
  }
  const out = merged
    .map((s) => ({ title: s.title, startPage: s.startPage, pdfPage: s.pdfPage, paragraphs: s.paragraphs.filter((x) => x.length > 1), lugat: [...s.lugat.values()] }))
    .filter((s) => s.paragraphs.some((x) => x.length > 40))
    .map((s, i) => ({ id: i + 1, ...s }));

  const wordCount = out.reduce((n, s) => n + s.paragraphs.join(" ").split(/\s+/).length, 0);
  const lugatCount = out.reduce((n, s) => n + s.lugat.length, 0);
  fs.writeFileSync(path.join(OUT, `${slug}.json`), JSON.stringify({ slug, bookTitle: trUpperToTitle(bookUpper), pageCount: doc.numPages, wordCount, sections: out }));
  console.log(`${slug}: ${doc.numPages} sayfa, ${out.length} bölüm, ${wordCount} kelime, ${lugatCount} lügat`);
  return {
    slug,
    bookTitle: trUpperToTitle(bookUpper),
    pageCount: doc.numPages,
    wordCount,
    lugatCount,
    sections: out.map((s) => ({ id: s.id, title: s.title, startPage: s.startPage, pdfPage: s.pdfPage, paragraphCount: s.paragraphs.length, lugatCount: s.lugat.length })),
  };
}

const only = process.argv[2];
const files = fs.readdirSync(SRC).filter((f) => f.endsWith(".pdf") && (!only || f.startsWith(only)));
const indexPath = path.join(OUT, "index.json");
const index = fs.existsSync(indexPath) ? JSON.parse(fs.readFileSync(indexPath, "utf8")) : {};
for (const f of files) {
  try {
    const r = await extractBook(f);
    index[r.slug] = r;
  } catch (e) {
    console.error(f, e.message);
  }
}
fs.writeFileSync(indexPath, JSON.stringify(index, null, 1));
