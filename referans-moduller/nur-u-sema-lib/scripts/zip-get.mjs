// Uzak zip'ten sadece istenen dosyaları (range isteğiyle) indirir.
// Kullanım: node --use-system-ca scripts/zip-get.mjs <zipUrl> <hedefKlasör> <dosyaAdıRegex>
import fs from "node:fs";
import path from "node:path";
import zlib from "node:zlib";

const [url, outDir, pattern] = process.argv.slice(2);
const head = await fetch(url, { method: "HEAD" });
const size = Number(head.headers.get("content-length"));
const tail = 4 * 1024 * 1024;
const range = async (a, b) => Buffer.from(await (await fetch(url, { headers: { Range: `bytes=${a}-${b}` } })).arrayBuffer());
const buf = await range(size - tail, size - 1);
const eocd = buf.lastIndexOf(Buffer.from([0x50, 0x4b, 0x05, 0x06]));
let cdOffset = buf.readUInt32LE(eocd + 16), count = buf.readUInt16LE(eocd + 10);
const z64 = buf.lastIndexOf(Buffer.from([0x50, 0x4b, 0x06, 0x06]));
if (z64 >= 0) { count = Number(buf.readBigUInt64LE(z64 + 32)); cdOffset = Number(buf.readBigUInt64LE(z64 + 48)); }
let p = cdOffset - (size - tail);
const entries = [];
for (let i = 0; i < count; i++) {
  const method = buf.readUInt16LE(p + 10);
  const csize = buf.readUInt32LE(p + 20), usize = buf.readUInt32LE(p + 24);
  const nl = buf.readUInt16LE(p + 28), el = buf.readUInt16LE(p + 30), cl = buf.readUInt16LE(p + 32);
  const off = buf.readUInt32LE(p + 42);
  const name = buf.slice(p + 46, p + 46 + nl).toString("latin1");
  entries.push({ name, method, csize, usize, off });
  p += 46 + nl + el + cl;
}
const re = new RegExp(pattern);
fs.mkdirSync(outDir, { recursive: true });
for (const e of entries.filter((e) => re.test(e.name) && e.usize > 0)) {
  const base = path.basename(e.name);
  const dest = path.join(outDir, base);
  if (fs.existsSync(dest) && fs.statSync(dest).size === e.usize) { console.log("var:", base); continue; }
  const lh = await range(e.off, e.off + 29);
  const dataStart = e.off + 30 + lh.readUInt16LE(26) + lh.readUInt16LE(28);
  const data = await range(dataStart, dataStart + e.csize - 1);
  const out = e.method === 8 ? zlib.inflateRawSync(data) : data;
  fs.writeFileSync(dest, out);
  console.log("indirildi:", base, (out.length / 1e6).toFixed(2), "MB");
}
