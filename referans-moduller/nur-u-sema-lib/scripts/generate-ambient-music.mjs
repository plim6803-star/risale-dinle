// Risale-i Nur okuma ve tefekkür iklimine uygun 6 farklı yüksek kaliteli enstrümantal fon müziği üretir
// (1- Uşşak Ney Taksimi, 2- Hicaz Medrese İklimi, 3- Rast Seher Tefekkürü, 4- Saba Kalp Huzuru, 5- Rahmet Yağmuru & Ney, 6- Kâinat Tefekkürü)
import fs from "node:fs";
import path from "node:path";

const OUT_DIR = "public/ses/fon";
fs.mkdirSync(OUT_DIR, { recursive: true });

const SAMPLE_RATE = 22050;
const DURATION_SEC = 90; // 1.5 dakikalık kesintisiz döngü (loop)

function createWavBuffer(samples) {
  const dataLen = samples.length * 2;
  const buf = new ArrayBuffer(44 + dataLen);
  const view = new DataView(buf);
  const writeStr = (off, s) => { for (let i = 0; i < s.length; i++) view.setUint8(off + i, s.charCodeAt(i)); };

  writeStr(0, "RIFF");
  view.setUint32(4, 36 + dataLen, true);
  writeStr(8, "WAVE");
  writeStr(12, "fmt ");
  view.setUint32(16, 16, true);
  view.setUint16(20, 1, true);
  view.setUint16(22, 1, true);
  view.setUint32(24, SAMPLE_RATE, true);
  view.setUint32(28, SAMPLE_RATE * 2, true);
  view.setUint16(32, 2, true);
  view.setUint16(34, 16, true);
  writeStr(36, "data");
  view.setUint32(40, dataLen, true);

  for (let i = 0; i < samples.length; i++) {
    const clamped = Math.max(-0.96, Math.min(0.96, samples[i]));
    view.setInt16(44 + i * 2, Math.floor(clamped * 32767), true);
  }
  return Buffer.from(buf);
}

// Akustik yankı (Reverb / Medrese kubbe akustiği) uygular
function applyReverb(samples, delaysSec = [0.11, 0.23, 0.37, 0.53], decay = 0.36) {
  const out = new Float32Array(samples.length);
  out.set(samples);
  for (const dSec of delaysSec) {
    const d = Math.floor(dSec * SAMPLE_RATE);
    for (let i = d; i < out.length; i++) {
      out[i] += out[i - d] * decay;
    }
  }
  return out;
}

const TRACKS = [
  {
    file: "ussak-ney-taksimi.wav",
    title: "Uşşak Ney Taksimi & Dem",
    root: 196.0, // Sol (Rast/Dügah)
    scale: [1.0, 1.1, 1.2, 1.333, 1.5, 1.6, 1.8, 2.0],
    melodyIndices: [0, 1, 2, 4, 3, 2, 1, 0, 2, 3, 4, 5, 4, 2, 1, 0],
    phraseLen: 5.625,
    rain: 0,
    oud: true,
  },
  {
    file: "hicaz-tefekkur-iklimi.wav",
    title: "Hicaz Tefekkür İklimi (Ney & Ud)",
    root: 174.61, // Fa
    scale: [1.0, 1.067, 1.25, 1.333, 1.5, 1.6, 1.875, 2.0],
    melodyIndices: [0, 2, 3, 4, 3, 2, 1, 0, 4, 5, 4, 3, 2, 1, 2, 0],
    phraseLen: 5.625,
    rain: 0,
    oud: true,
  },
  {
    file: "rast-seher-huzuru.wav",
    title: "Rast Seher Vakti Huzuru",
    root: 164.81, // Mi
    scale: [1.0, 1.125, 1.25, 1.333, 1.5, 1.667, 1.875, 2.0],
    melodyIndices: [0, 2, 4, 5, 4, 2, 1, 0, 3, 4, 6, 4, 3, 2, 1, 0],
    phraseLen: 5.625,
    rain: 0,
    oud: false,
  },
  {
    file: "saba-kalp-zikri.wav",
    title: "Sabâ Kalp Sükûneti & Medrese",
    root: 185.0,
    scale: [1.0, 1.1, 1.2, 1.26, 1.5, 1.6, 1.78, 2.0],
    melodyIndices: [0, 1, 2, 3, 2, 1, 0, 1, 2, 4, 3, 2, 1, 2, 1, 0],
    phraseLen: 5.625,
    rain: 0,
    oud: true,
  },
  {
    file: "rahmet-yagmuru-ve-ney.wav",
    title: "Rahmet Yağmuru & Derin Ney",
    root: 196.0,
    scale: [1.0, 1.125, 1.2, 1.333, 1.5, 1.6, 1.8, 2.0],
    melodyIndices: [4, 3, 2, 1, 0, 2, 3, 4, 2, 1, 0, 1, 2, 3, 1, 0],
    phraseLen: 5.625,
    rain: 0.035,
    oud: false,
  },
  {
    file: "kainat-ayetleri-sema.wav",
    title: "Kâinat Tefekkürü (Semâvî Dem)",
    root: 146.83, // Re
    scale: [1.0, 1.125, 1.2, 1.333, 1.5, 1.667, 1.8, 2.0],
    melodyIndices: [0, 4, 2, 3, 4, 6, 4, 2, 0, 2, 4, 3, 2, 1, 2, 0],
    phraseLen: 5.625,
    rain: 0,
    oud: true,
  },
];

let lpNoise = 0;

for (const tr of TRACKS) {
  const totalSamples = SAMPLE_RATE * DURATION_SEC;
  const raw = new Float32Array(totalSamples);
  let phase = 0;
  let curFreq = tr.root;

  for (let i = 0; i < totalSamples; i++) {
    const t = i / SAMPLE_RATE;
    const step = Math.floor(t / tr.phraseLen) % tr.melodyIndices.length;
    const targetFreq = tr.root * tr.scale[tr.melodyIndices[step]];
    // Yumuşak perde geçişi (Legato / Ney kaydırması)
    curFreq += (targetFreq - curFreq) * 0.0012;

    // Canlı vibrato (Neyzen nefesi titremesi)
    const pt = t % tr.phraseLen;
    const vibrato = 1.0 + (pt > 0.8 ? 0.0045 * Math.sin(2 * Math.PI * 5.2 * t) : 0);
    phase += (2 * Math.PI * curFreq * vibrato) / SAMPLE_RATE;

    // Nefes zarfı (yumuşak giriş ve çıkış)
    const attack = Math.min(1, pt / 0.9);
    const release = Math.min(1, (tr.phraseLen - pt) / 1.1);
    const env = attack * release * (0.85 + 0.15 * Math.sin(2 * Math.PI * 0.25 * t));

    // Ney tınısı (Temel frekans + sıcak harmonikler + hafif nefes hışırtısı)
    const neyTone =
      0.58 * Math.sin(phase) +
      0.26 * Math.sin(phase * 2 + 0.3) +
      0.11 * Math.sin(phase * 3 + 0.8) +
      0.04 * Math.sin(phase * 4 + 1.4);

    const white = Math.random() * 2 - 1;
    lpNoise = lpNoise * 0.92 + white * 0.08;
    const breathAir = lpNoise * 0.035 * env;

    // Arka plan Dem (Sıcak sürekli bas ve beşli akor pedalı)
    const drone =
      0.22 * Math.sin(2 * Math.PI * (tr.root * 0.5) * t) +
      0.12 * Math.sin(2 * Math.PI * (tr.root * 0.75) * t) +
      0.09 * Math.sin(2 * Math.PI * tr.root * t);

    // Ud / Tanbur arpej dokunuşu (her cümlenin başında ve ortasında)
    let oudSignal = 0;
    if (tr.oud) {
      const pluckTime = t % (tr.phraseLen / 2);
      const oudEnv = Math.exp(-pluckTime * 3.2);
      const oudFreq = targetFreq * 0.5;
      oudSignal =
        0.16 *
        oudEnv *
        (Math.sin(2 * Math.PI * oudFreq * t) + 0.4 * Math.sin(2 * Math.PI * oudFreq * 2 * t));
    }

    // Rahmet yağmuru efekti
    const rainLayer = tr.rain > 0 ? lpNoise * tr.rain * (0.7 + 0.3 * Math.sin(t * 0.6)) : 0;

    // Loop başı ve sonu yumuşatma
    const loopFade = Math.min(1, t / 1.5, (DURATION_SEC - t) / 1.5);

    raw[i] = ((neyTone + breathAir) * env + drone + oudSignal + rainLayer) * 0.36 * loopFade;
  }

  const reverbed = applyReverb(raw);
  const wavBuf = createWavBuffer(reverbed);
  const outPath = path.join(OUT_DIR, tr.file);
  fs.writeFileSync(outPath, wavBuf);
  console.log("Üretildi:", outPath, (wavBuf.length / 1e6).toFixed(2), "MB -", tr.title);
}
