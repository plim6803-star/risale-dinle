// Uzak zip dosyasının içindekiler listesini (central directory) sadece sonunu indirerek okur.
const url = process.argv[2];
const head = await fetch(url, { method: "HEAD" });
const size = Number(head.headers.get("content-length"));
const tail = 4 * 1024 * 1024;
const r = await fetch(url, { headers: { Range: `bytes=${size - tail}-${size - 1}` } });
const buf = Buffer.from(await r.arrayBuffer());
let eocd = buf.lastIndexOf(Buffer.from([0x50, 0x4b, 0x05, 0x06]));
let cdOffset = buf.readUInt32LE(eocd + 16);
let count = buf.readUInt16LE(eocd + 10);
// ZIP64
const z64 = buf.lastIndexOf(Buffer.from([0x50, 0x4b, 0x06, 0x06]));
if (z64 >= 0) { count = Number(buf.readBigUInt64LE(z64 + 32)); cdOffset = Number(buf.readBigUInt64LE(z64 + 48)); }
let p = cdOffset - (size - tail);
const names = [];
for (let i = 0; i < count && p >= 0 && p < buf.length; i++) {
  if (buf.readUInt32LE(p) !== 0x02014b50) break;
  const csize = buf.readUInt32LE(p + 20), usize = buf.readUInt32LE(p + 24);
  const nl = buf.readUInt16LE(p + 28), el = buf.readUInt16LE(p + 30), cl = buf.readUInt16LE(p + 32);
  names.push({ name: buf.slice(p + 46, p + 46 + nl).toString("utf8"), size: usize });
  p += 46 + nl + el + cl;
}
console.log("toplam", count, "okunan", names.length);
console.log(names.slice(0, 15).map((n) => `${n.name}  ${(n.size / 1e6).toFixed(2)}MB`).join("\n"));
console.log("...");
console.log(names.slice(-5).map((n) => `${n.name}  ${(n.size / 1e6).toFixed(2)}MB`).join("\n"));
