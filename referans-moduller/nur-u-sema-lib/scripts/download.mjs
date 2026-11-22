// Kullanım: node --use-system-ca scripts/download.mjs <url> <hedefDosya>
import fs from "node:fs";
import path from "node:path";
import { Readable } from "node:stream";
import { pipeline } from "node:stream/promises";

const [url, dest] = process.argv.slice(2);
fs.mkdirSync(path.dirname(dest), { recursive: true });
const have = fs.existsSync(dest) ? fs.statSync(dest).size : 0;
const r = await fetch(url, { headers: have ? { Range: `bytes=${have}-` } : {} });
if (r.status === 416) { console.log("zaten tamam"); process.exit(0); }
if (!r.ok) throw new Error("HTTP " + r.status);
const total = Number(r.headers.get("content-length") || 0) + (r.status === 206 ? have : 0);
let got = r.status === 206 ? have : 0;
let last = 0;
const body = Readable.fromWeb(r.body);
body.on("data", (c) => {
  got += c.length;
  if (Date.now() - last > 5000) { last = Date.now(); console.log(`${(got / 1e6).toFixed(0)} / ${(total / 1e6).toFixed(0)} MB`); }
});
await pipeline(body, fs.createWriteStream(dest, { flags: r.status === 206 ? "a" : "w" }));
console.log("TAMAM", dest, (got / 1e6).toFixed(0), "MB");
