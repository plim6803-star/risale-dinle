const r = await fetch("https://www.erisale.com/tr/sozler/birinci-soz", { headers: { "User-Agent": "Mozilla/5.0" } });
const t = await r.text();
console.log("Status:", r.status, "Len:", t.length);
import fs from "node:fs";
fs.writeFileSync("temp_erisale_1soz.html", t);
// Extract visible text or RSC payload
const plain = t.replace(/<script[\s\S]*?<\/script>/gi, "").replace(/<style[\s\S]*?<\/style>/gi, "").replace(/<[^>]+>/g, "\n").replace(/\n+/g, "\n").trim();
console.log("Plain preview:\n", plain.slice(0, 2500));
