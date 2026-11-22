import fs from "node:fs";
const t = fs.readFileSync("temp_erisale_1soz.html", "utf8");
const blocks = [...t.matchAll(/<div data-block-index="(\d+)"[^>]*>([\s\S]*?)(?=<div data-block-index="|<\/main>)/g)];
console.log("Total blocks:", blocks.length);
for (const b of blocks.slice(0, 15)) {
  const idx = b[1];
  const inner = b[2].replace(/<button[\s\S]*?<\/button>/g, "").replace(/<svg[\s\S]*?<\/svg>/g, "");
  const idMatch = inner.match(/id="([^"]+)"/);
  const text = inner.replace(/<[^>]+>/g, " ").replace(/&#x27;/g, "'").replace(/&quot;/g, '"').replace(/\s+/g, " ").trim();
  console.log(`[Block ${idx} - ${idMatch?.[1]}]`, text);
}
