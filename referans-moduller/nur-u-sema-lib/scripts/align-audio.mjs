// Gerçek okuyucu kaydını kitap metniyle hizalar (cümle + kelime zamanları).
// Kullanım: node --use-system-ca scripts/align-audio.mjs <kitapSlug> <bölümId> <mp3_1> [mp3_2 ...]
// Çıktı: public/data/sync/koseoglu/<slug>/<bölümId>.json
import fs from "node:fs";
import path from "node:path";
import { MPEGDecoder } from "mpg123-decoder";
import { pipeline } from "@huggingface/transformers";

const [slug, sectionIdArg, ...mp3s] = process.argv.slice(2);
const sectionId = Number(sectionIdArg);
const NARRATOR = process.env.NARRATOR || "koseoglu";
const MODEL = process.env.WHISPER_MODEL || "onnx-community/whisper-tiny";

// ---------- Metin: arayüzle aynı cümle/kelime bölmesi ----------
const book = JSON.parse(fs.readFileSync(`public/data/kulliyat/${slug}.json`, "utf8"));
const section = book.sections.find((s) => s.id === sectionId);
if (!section) throw new Error("bölüm yok");
const sentences = [];
for (const p of section.paragraphs) {
  if (p === "﴾…﴿" || p === "* * *") continue;
  const parts = p.match(/[^.!?…]+(?:[.!?…]+[”"’)]*|$)/g) ?? [p];
  for (const s of parts) if (s.trim()) sentences.push(s.trim());
}
const bookWords = []; // {si, wi, n}
sentences.forEach((s, si) => s.split(/\s+/).forEach((w, wi) => bookWords.push({ si, wi, n: norm(w) })));

function norm(w) {
  return w
    .toLocaleLowerCase("tr")
    .replace(/[âà]/g, "a").replace(/[îì]/g, "i").replace(/[ûù]/g, "u")
    .replace(/[^a-zçğıöşü0-9]/g, "");
}

// ---------- Ses çözme (16 kHz mono) ----------
async function decode(file) {
  const dec = new MPEGDecoder();
  await dec.ready;
  const { channelData, sampleRate } = dec.decode(new Uint8Array(fs.readFileSync(file)));
  dec.free();
  const mono = channelData.length > 1 ? channelData[0].map((v, i) => (v + channelData[1][i]) / 2) : channelData[0];
  const ratio = sampleRate / 16000;
  const out = new Float32Array(Math.floor(mono.length / ratio));
  for (let i = 0; i < out.length; i++) {
    const a = Math.floor(i * ratio), b = Math.min(mono.length, Math.floor((i + 1) * ratio));
    let s = 0;
    for (let j = a; j < b; j++) s += mono[j];
    out[i] = s / Math.max(1, b - a);
  }
  return { audio: out, duration: mono.length / sampleRate };
}

// ---------- Konuşma tanıma ----------
console.log("model yükleniyor:", MODEL);
let asr = null;
const asrWords = []; // {t (track), s, e, n}
const tracks = [];
for (const [ti, f] of mp3s.entries()) {
  const t0 = Date.now();
  const { audio, duration } = await decode(f);
  tracks.push({ src: "/" + path.relative("public", f).replace(/\\/g, "/"), duration });
  const cacheFile = `.cache/asr/${path.basename(f)}.json`;
  let chunks = null;
  if (fs.existsSync(cacheFile)) {
    chunks = JSON.parse(fs.readFileSync(cacheFile, "utf8"));
    console.log(`${path.basename(f)}: önbellekten yüklendi (${chunks.length} parça)`);
  } else {
    if (!asr) asr = await pipeline("automatic-speech-recognition", MODEL, { dtype: "q8" });
    console.log(`${path.basename(f)}: ${(duration / 60).toFixed(1)} dk, tanınıyor…`);
    const r = await asr(audio, { language: "turkish", task: "transcribe", return_timestamps: true, chunk_length_s: 30, stride_length_s: 5 });
    chunks = r.chunks ?? [];
    fs.mkdirSync(".cache/asr", { recursive: true });
    fs.writeFileSync(cacheFile, JSON.stringify(chunks));
    console.log(`  ${chunks.length} parça, ${((Date.now() - t0) / 1000).toFixed(0)} sn`);
  }
  for (const c of chunks) {
    const c0 = c.timestamp[0];
    const c1 = c.timestamp[1] ?? (c0 + 2.5);
    const dur = Math.max(0.1, c1 - c0);
    const words = c.text.trim().split(/\s+/).filter(Boolean);
    const step = dur / Math.max(1, words.length);
    words.forEach((w, wi) => {
      const n = norm(w);
      if (n) asrWords.push({ t: ti, s: c0 + wi * step, e: c0 + (wi + 1) * step, n });
    });
  }
}

// ---------- Hizalama: dinamik programlama ----------
function sim(a, b) {
  if (a === b) return 1;
  if (!a || !b) return 0;
  const m = a.length, n = b.length;
  if (Math.abs(m - n) > Math.max(m, n) * 0.5) return 0;
  let prev = Array.from({ length: n + 1 }, (_, j) => j);
  for (let i = 1; i <= m; i++) {
    const cur = [i];
    for (let j = 1; j <= n; j++) cur[j] = Math.min(prev[j] + 1, cur[j - 1] + 1, prev[j - 1] + (a[i - 1] === b[j - 1] ? 0 : 1));
    prev = cur;
  }
  return 1 - prev[n] / Math.max(m, n);
}
const B = bookWords.length, A = asrWords.length;
const band = Math.max(400, Math.round(Math.abs(A - B) + 0.15 * Math.max(A, B)));
const GAP = -0.4;
const NEG = -1e9;
const center = (i) => Math.round((i * A) / Math.max(1, B));
const W = 2 * band + 1;
const back = new Uint8Array((B + 1) * W);
let prev = new Float64Array(W).fill(NEG);
for (let k = 0; k < W; k++) { const j = center(0) - band + k; if (j >= 0 && j <= A) { prev[k] = j * GAP * 0.5; back[k] = 3; } }
for (let i = 1; i <= B; i++) {
  const cur = new Float64Array(W).fill(NEG);
  const ci = center(i), cp = center(i - 1);
  for (let k = 0; k < W; k++) {
    const j = ci - band + k;
    if (j < 0 || j > A) continue;
    let best = NEG, bt = 0;
    const kp = j - (cp - band);
    if (kp >= 0 && kp < W && prev[kp] > NEG) { const v = prev[kp] + GAP; if (v > best) { best = v; bt = 2; } }
    if (j > 0) {
      const kd = kp - 1;
      if (kd >= 0 && kd < W && prev[kd] > NEG) {
        const s = sim(bookWords[i - 1].n, asrWords[j - 1].n);
        const v = prev[kd] + (s >= 0.6 ? s * 2 : -0.6);
        if (v > best) { best = v; bt = 1; }
      }
      if (k > 0 && cur[k - 1] > NEG) { const v = cur[k - 1] + GAP * 0.5; if (v > best) { best = v; bt = 3; } }
    }
    cur[k] = best;
    back[i * W + k] = bt;
  }
  prev = cur;
}
const match = new Int32Array(B).fill(-1);
let i = B, j = Math.min(A, center(B) + band);
{
  let bk = -1, bv = NEG;
  for (let k = 0; k < W; k++) { const jj = center(B) - band + k; if (jj >= 0 && jj <= A && prev[k] > bv) { bv = prev[k]; bk = k; } }
  j = center(B) - band + bk;
}
let matched = 0;
while (i > 0 && j >= 0) {
  const k = j - (center(i) - band);
  const bt = back[i * W + k];
  if (bt === 1) { if (sim(bookWords[i - 1].n, asrWords[j - 1].n) >= 0.6) { match[i - 1] = j - 1; matched++; } i--; j--; }
  else if (bt === 2) i--;
  else if (bt === 3) j--;
  else break;
}
console.log(`hizalama: ${matched}/${B} kitap kelimesi eşleşti (%${((matched / B) * 100).toFixed(0)})`);

// ---------- Zamanları doldur ----------
const times = bookWords.map((_, k) => (match[k] >= 0 ? { t: asrWords[match[k]].t, s: asrWords[match[k]].s, e: asrWords[match[k]].e } : null));
for (let k = 0; k < B; k++) {
  if (times[k]) continue;
  let a = k - 1; while (a >= 0 && !times[a]) a--;
  let b = k + 1; while (b < B && !times[b]) b++;
  const L = a >= 0 ? times[a] : { t: 0, s: 0, e: 0 };
  const R = b < B ? times[b] : { t: L.t, s: L.e + 1, e: L.e + 1 };
  if (L.t !== R.t) { times[k] = { t: L.t, s: L.e, e: L.e + 0.3 }; continue; }
  const frac = (k - a) / (b - a);
  const s = L.e + (R.s - L.e) * frac;
  times[k] = { t: L.t, s, e: s + 0.25 };
}

const out = { narrator: NARRATOR, slug, sectionId, model: MODEL, matchRate: +(matched / B).toFixed(3), tracks, sentences: [] };
let k = 0;
for (let si = 0; si < sentences.length; si++) {
  const n = sentences[si].split(/\s+/).length;
  const w = times.slice(k, k + n);
  k += n;
  out.sentences.push({ t: w[0].t, s: +w[0].s.toFixed(2), e: +w[w.length - 1].e.toFixed(2), w: w.map((x) => +x.s.toFixed(2)) });
}
const dir = `public/data/sync/${NARRATOR}/${slug}`;
fs.mkdirSync(dir, { recursive: true });
fs.writeFileSync(`${dir}/${sectionId}.json`, JSON.stringify(out));
console.log("yazıldı:", `${dir}/${sectionId}.json`);
