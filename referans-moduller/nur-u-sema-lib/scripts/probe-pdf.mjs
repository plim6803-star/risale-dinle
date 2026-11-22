import { getDocument } from "pdfjs-dist/legacy/build/pdf.mjs";
import fs from "node:fs";

const file = process.argv[2];
const pages = (process.argv[3] || "1,2,3").split(",").map(Number);
const data = new Uint8Array(fs.readFileSync(file));
const doc = await getDocument({ data, verbosity: 0 }).promise;
console.log("PAGES", doc.numPages);
for (const p of pages) {
  if (p > doc.numPages) continue;
  const page = await doc.getPage(p);
  const tc = await page.getTextContent();
  const txt = tc.items.map((i) => i.str + (i.hasEOL ? "\n" : "")).join("");
  console.log(`--- page ${p} ---\n` + txt.slice(0, 1200));
}
