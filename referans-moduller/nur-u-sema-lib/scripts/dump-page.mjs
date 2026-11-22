import { getDocument } from "pdfjs-dist/legacy/build/pdf.mjs";
import fs from "node:fs";
const [file, p] = [process.argv[2], +process.argv[3]];
const d = await getDocument({ data: new Uint8Array(fs.readFileSync(file)), verbosity: 0 }).promise;
const pg = await d.getPage(p);
console.log("viewport", pg.view);
const tc = await pg.getTextContent();
for (const i of tc.items) {
  if (!i.str.trim()) continue;
  console.log(Math.round(i.transform[4]), Math.round(i.transform[5]), Math.round(i.transform[0]), i.fontName.slice(-2), JSON.stringify(i.str.slice(0, 50)));
}
