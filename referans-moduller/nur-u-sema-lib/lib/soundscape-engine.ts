// ============================================================================
// Nûr-u Sema: Çok Kanallı Akustik Soundscape, Makamlı Fon Müziği & Atmosfer Motoru
// Gerçek WAV fon müziklerini (/ses/fon/*.wav) ve Web Audio API prosedürel
// enstrümanlarını (Ney, Ud, Kanun, Tanbur, Rahmet Yağmuru, Âb-ı Hayat Şelalesi,
// Seher Rüzgârı, Medrese Kubbe Akustiği, Kozmik Semâ Dem'i) canlı olarak birleştirir.
// Kıraat (okuma) başladığında arka planda otomatik eşlik eder ve Kur'an-ı Kerim
// ayetleri okunurken otomatik kısılma (Auto-Ducking / Mute) özelliğini destekler.
// ============================================================================

export interface AmbientTrackTheme {
  id: string;
  label: string;
  makam: string;
  description: string;
  moodTag: string;
  wavSrc?: string; // /ses/fon/*.wav varsa doğrudan çalar + WebAudio armonisi ekler
  synthFreqs: number[]; // Makamın karakteristik armoni frekansları (Hz)
  defaultChannels: Record<string, number>;
}

export interface SoundscapeChannel {
  id: string;
  label: string;
  description: string;
  icon: string;
  volume: number; // 0.0 - 1.0
  muted: boolean;
  color: string;
}

export interface SoundscapePreset {
  id: string;
  name: string;
  description: string;
  ambientThemeId: string;
  channels: Record<string, number>; // channelId -> volume
  reverbAmount: number;
}

export const AMBIENT_MUSIC_THEMES: AmbientTrackTheme[] = [
  {
    id: "ussak",
    label: "Uşşak Ney Taksimi & Derin Dem",
    makam: "Uşşak Makamı",
    description: "Kalbi sükûnete erdiren klasik Uşşak ney üflemesi ve sıcak dem akustiği",
    moodTag: "Huzur & İhlas",
    wavSrc: "/ses/fon/ussak-ney-taksimi.wav",
    synthFreqs: [220, 293.66, 329.63, 440], // A3, D4, E4, A4
    defaultChannels: { ney: 0.7, ud_kanun: 0.25, rain: 0.15, water: 0.2, wind: 0.1, medrese: 0.3, cosmic: 0.1 },
  },
  {
    id: "hicaz",
    label: "Hicaz Tefekkür İklimi (Ney & Ud)",
    makam: "Hicaz Makamı",
    description: "Sahra yolculuğu ve tevhid burhanlarına eşlik eden derin Hicaz nağmeleri",
    moodTag: "Derin Tefekkür",
    wavSrc: "/ses/fon/hicaz-tefekkur-iklimi.wav",
    synthFreqs: [220, 277.18, 329.63, 392.0], // A3, C#4, E4, G4
    defaultChannels: { ney: 0.65, ud_kanun: 0.55, rain: 0.0, water: 0.15, wind: 0.35, medrese: 0.25, cosmic: 0.15 },
  },
  {
    id: "rast",
    label: "Rast Seher Vakti Huzuru & İnşirah",
    makam: "Rast Makamı",
    description: "Fecir ve seher vaktinin ferahlığını, ümit ve sürurunu hissettiren makam",
    moodTag: "Ferahlık & Ümit",
    wavSrc: "/ses/fon/rast-seher-huzuru.wav",
    synthFreqs: [196.0, 246.94, 293.66, 392.0], // G3, B3, D4, G4
    defaultChannels: { ney: 0.6, ud_kanun: 0.4, rain: 0.0, water: 0.45, wind: 0.25, medrese: 0.15, cosmic: 0.1 },
  },
  {
    id: "saba",
    label: "Sabâ Kalp Zikri & Medrese Sükûneti",
    makam: "Sabâ Makamı",
    description: "Acz, fakr ve şefkat tefekküründe ruhu rikkat ve duaya hazırlayan Sabâ iklimi",
    moodTag: "Rikkat & Dua",
    wavSrc: "/ses/fon/saba-kalp-zikri.wav",
    synthFreqs: [220, 261.63, 311.13, 440], // A3, C4, Eb4, A4
    defaultChannels: { ney: 0.75, ud_kanun: 0.2, rain: 0.3, water: 0.1, wind: 0.15, medrese: 0.55, cosmic: 0.2 },
  },
  {
    id: "rahmet",
    label: "Rahmet Yağmuru & Âb-ı Hayat Neyi",
    makam: "Hüseynî / Rahmet",
    description: "Bahar haşri, rahmet hazineleri ve şükür bahisleri için yağmur ve su eşlikli ney",
    moodTag: "Rahmet & Şükür",
    wavSrc: "/ses/fon/rahmet-yagmuru-ve-ney.wav",
    synthFreqs: [220, 329.63, 392.0, 493.88],
    defaultChannels: { ney: 0.55, ud_kanun: 0.3, rain: 0.7, water: 0.5, wind: 0.1, medrese: 0.2, cosmic: 0.0 },
  },
  {
    id: "kainat",
    label: "Kâinat Âyetleri & Semâvî Heybet",
    makam: "Segâh / Semâvî",
    description: "Yıldızlar, güneş, Sikke-i Rubûbiyet ve kozmik intizam sahneleri için belgesel atmosferi",
    moodTag: "Hayret & Azamet",
    wavSrc: "/ses/fon/kainat-ayetleri-sema.wav",
    synthFreqs: [130.81, 196.0, 261.63, 329.63], // C3, G3, C4, E4
    defaultChannels: { ney: 0.5, ud_kanun: 0.35, rain: 0.0, water: 0.15, wind: 0.3, medrese: 0.4, cosmic: 0.75 },
  },
  {
    id: "neva_bahar",
    label: "Nevâ Bahar Haşri & Çiçeklenme",
    makam: "Nevâ Makamı",
    description: "10. Söz Haşir ve diriliş bahislerine özel canlı, ümitvar ve nurlu bahar akustiği",
    moodTag: "Diriliş & Haşir",
    wavSrc: "/ses/fon/rast-seher-huzuru.wav",
    synthFreqs: [293.66, 349.23, 440.0, 587.33], // D4, F4, A4, D5
    defaultChannels: { ney: 0.65, ud_kanun: 0.5, rain: 0.25, water: 0.6, wind: 0.2, medrese: 0.15, cosmic: 0.2 },
  },
  {
    id: "segah_irfan",
    label: "Segâh İrfan & Hakikat Dershanesi",
    makam: "Segâh Makamı",
    description: "Temsil ve burhan analizlerinde zihni berraklaştıran ağırbaşlı klasik fon",
    moodTag: "İlim & Hikmet",
    wavSrc: "/ses/fon/ussak-ney-taksimi.wav",
    synthFreqs: [164.81, 246.94, 329.63, 392.0],
    defaultChannels: { ney: 0.6, ud_kanun: 0.45, rain: 0.0, water: 0.2, wind: 0.0, medrese: 0.5, cosmic: 0.25 },
  },
  {
    id: "none",
    label: "Fon Müziği Kapalı (Yalnız Kıraat)",
    makam: "Sessiz",
    description: "Arka plan fon müziği çalmaz, yalnızca okuyucunun sesi işitilir",
    moodTag: "Sade Okuma",
    synthFreqs: [],
    defaultChannels: { ney: 0, ud_kanun: 0, rain: 0, water: 0, wind: 0, medrese: 0, cosmic: 0 },
  },
];

export const SOUNDSCAPE_CHANNELS: SoundscapeChannel[] = [
  {
    id: "ney",
    label: "Ney & Dem Sadâsı",
    description: "Makamlı canlı ney üflemesi ve huzurlu dem nefesi",
    icon: "Music",
    volume: 0.65,
    muted: false,
    color: "#B8862D",
  },
  {
    id: "ud_kanun",
    label: "Ud, Tanbur & Kanun Tınısı",
    description: "Derin ve yumuşak mızraplı klasik Türk musikisi armonisi",
    icon: "Music",
    volume: 0.35,
    muted: false,
    color: "#C87D55",
  },
  {
    id: "rain",
    label: "Rahmet Yağmuru",
    description: "Toprağa hayat veren huzurlu yağmur damlaları",
    icon: "CloudRain",
    volume: 0.25,
    muted: false,
    color: "#4A90E2",
  },
  {
    id: "water",
    label: "Âb-ı Hayat / Pınar Şırıltısı",
    description: "Kaya ve taşlar arasından akan berrak çeşme ve ırmak sesi",
    icon: "Waves",
    volume: 0.25,
    muted: false,
    color: "#36B37E",
  },
  {
    id: "wind",
    label: "Sahra & Seher Rüzgârı",
    description: "Çöl kumulları ve seher vaktinin tatlı rüzgâr esintisi",
    icon: "Wind",
    volume: 0.2,
    muted: false,
    color: "#8E7CC3",
  },
  {
    id: "medrese",
    label: "Taş Medrese & Kubbe Yankısı",
    description: "Tarihi medrese kubbesinin huşu veren derin sükûneti",
    icon: "Sparkles",
    volume: 0.2,
    muted: false,
    color: "#D4A84F",
  },
  {
    id: "cosmic",
    label: "Semâvî Tefekkür & Belgesel Dem'i",
    description: "Sinematik belgesel hissi uyandıran geniş orkestral uzay/kâinat katmanı",
    icon: "Sparkles",
    volume: 0.2,
    muted: false,
    color: "#6366F1",
  },
];

export const SOUNDSCAPE_PRESETS: SoundscapePreset[] = [
  {
    id: "sahra_tefekkur",
    name: "Sahra & Çöl Tefekkürü",
    description: "1. ve 2. Söz'deki sahra yolculuğuna uygun rüzgâr, ud ve seher neyi",
    ambientThemeId: "hicaz",
    channels: { ney: 0.7, ud_kanun: 0.45, rain: 0.0, water: 0.15, wind: 0.45, medrese: 0.2, cosmic: 0.2 },
    reverbAmount: 0.35,
  },
  {
    id: "sinematik_belgesel",
    name: "Efsanevi Belgesel & Kâinat",
    description: "Hayret ve tefekkürü zirveye taşıyan sinematik semâvî dem ve ney",
    ambientThemeId: "kainat",
    channels: { ney: 0.6, ud_kanun: 0.4, rain: 0.1, water: 0.25, wind: 0.3, medrese: 0.35, cosmic: 0.8 },
    reverbAmount: 0.55,
  },
  {
    id: "rahmet_yagmuru",
    name: "Rahmet & Âb-ı Hayat İklimi",
    description: "Yağmur damlaları, kayadan akan pınar ve derin Hüseynî/Uşşak ney",
    ambientThemeId: "rahmet",
    channels: { ney: 0.6, ud_kanun: 0.3, rain: 0.65, water: 0.5, wind: 0.1, medrese: 0.25, cosmic: 0.1 },
    reverbAmount: 0.45,
  },
  {
    id: "seher_zikri",
    name: "Seher Vakti & Kalp Huzuru",
    description: "Fecir vaktinde su şırıltısı ve Rast/Sabâ makamında sükûnet",
    ambientThemeId: "rast",
    channels: { ney: 0.65, ud_kanun: 0.35, rain: 0.0, water: 0.45, wind: 0.2, medrese: 0.15, cosmic: 0.1 },
    reverbAmount: 0.25,
  },
  {
    id: "medrese_kubbe",
    name: "Tarihi Taş Medrese",
    description: "Kubbe akustiği, Sabâ kalp zikri ve derin ney dem nefesi",
    ambientThemeId: "saba",
    channels: { ney: 0.8, ud_kanun: 0.25, rain: 0.0, water: 0.0, wind: 0.0, medrese: 0.65, cosmic: 0.2 },
    reverbAmount: 0.65,
  },
  {
    id: "saf_kirat",
    name: "Sade / Yalnız Kıraat",
    description: "Bütün fon ve ortam sesleri kapalı, sadece okuyucu sesi",
    ambientThemeId: "none",
    channels: { ney: 0.0, ud_kanun: 0.0, rain: 0.0, water: 0.0, wind: 0.0, medrese: 0.0, cosmic: 0.0 },
    reverbAmount: 0.0,
  },
];

class SoundscapeSynthesizer {
  private ctx: AudioContext | null = null;
  private masterGain: GainNode | null = null;
  private channelGains: Map<string, GainNode> = new Map();
  private wavAudio: HTMLAudioElement | null = null;
  private currentThemeId: string = "ussak";
  private masterVolume: number = 0.32;
  private isRunning: boolean = false;
  private isDuckedForQuran: boolean = false;
  private melodyInterval: ReturnType<typeof setInterval> | null = null;
  private listeners: Set<() => void> = new Set();

  public subscribe(fn: () => void): () => void {
    this.listeners.add(fn);
    return () => this.listeners.delete(fn);
  }

  private notify() {
    this.listeners.forEach((fn) => fn());
  }

  private initContext() {
    if (typeof window === "undefined") return;
    if (!this.ctx) {
      const AudioCtx =
        window.AudioContext ||
        (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      this.ctx = new AudioCtx();
      this.masterGain = this.ctx.createGain();
      this.masterGain.gain.setValueAtTime(this.masterVolume, this.ctx.currentTime);
      this.masterGain.connect(this.ctx.destination);
    }
    if (this.ctx.state === "suspended") {
      this.ctx.resume().catch(() => {});
    }
  }

  private createPinkNoiseBuffer(): AudioBuffer {
    if (!this.ctx) throw new Error("AudioContext not ready");
    const bufferSize = this.ctx.sampleRate * 4;
    const buffer = this.ctx.createBuffer(1, bufferSize, this.ctx.sampleRate);
    const data = buffer.getChannelData(0);
    let b0 = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0;
    for (let i = 0; i < bufferSize; i++) {
      const white = Math.random() * 2 - 1;
      b0 = 0.99886 * b0 + white * 0.0555179;
      b1 = 0.99332 * b1 + white * 0.0750759;
      b2 = 0.96900 * b2 + white * 0.1538520;
      b3 = 0.86650 * b3 + white * 0.3104856;
      b4 = 0.55000 * b4 + white * 0.5329522;
      b5 = -0.7616 * b5 - white * 0.0168980;
      data[i] = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362) * 0.04;
      b6 = white * 0.115926;
    }
    return buffer;
  }

  // 1. Makamlı Ney & Dem Sentezleyicisi (Nefesli Vibrato + Harmonikler)
  private buildNeyNode(targetGain: GainNode) {
    if (!this.ctx) return;
    const theme = AMBIENT_MUSIC_THEMES.find((t) => t.id === this.currentThemeId) || AMBIENT_MUSIC_THEMES[0];
    const baseFreq = theme.synthFreqs[0] || 220;

    // Dem (Drone) sesi
    const droneOsc = this.ctx.createOscillator();
    droneOsc.type = "sine";
    droneOsc.frequency.setValueAtTime(baseFreq * 0.5, this.ctx.currentTime);

    // Ney ana sesi
    const neyOsc = this.ctx.createOscillator();
    neyOsc.type = "triangle";
    neyOsc.frequency.setValueAtTime(baseFreq, this.ctx.currentTime);

    // Vibrato LFO
    const vib = this.ctx.createOscillator();
    vib.frequency.setValueAtTime(4.8, this.ctx.currentTime);
    const vibGain = this.ctx.createGain();
    vibGain.gain.setValueAtTime(2.2, this.ctx.currentTime);
    vib.connect(vibGain);
    vibGain.connect(neyOsc.frequency);

    // Yumuşak nefes filtresi
    const filter = this.ctx.createBiquadFilter();
    filter.type = "lowpass";
    filter.frequency.setValueAtTime(520, this.ctx.currentTime);

    const subGain = this.ctx.createGain();
    subGain.gain.setValueAtTime(0.18, this.ctx.currentTime);

    droneOsc.connect(filter);
    neyOsc.connect(filter);
    filter.connect(subGain);
    subGain.connect(targetGain);

    droneOsc.start();
    neyOsc.start();
    vib.start();

    // Makam notaları arasında yumuşak gezinme
    let step = 0;
    if (this.melodyInterval) clearInterval(this.melodyInterval);
    this.melodyInterval = setInterval(() => {
      if (!this.ctx || !this.isRunning) return;
      const curTheme =
        AMBIENT_MUSIC_THEMES.find((t) => t.id === this.currentThemeId) || AMBIENT_MUSIC_THEMES[0];
      if (!curTheme.synthFreqs.length) return;
      const nextFreq = curTheme.synthFreqs[step % curTheme.synthFreqs.length];
      step++;
      try {
        neyOsc.frequency.setTargetAtTime(nextFreq, this.ctx.currentTime, 1.8);
      } catch {}
    }, 5500);
  }

  // 2. Ud & Kanun Yumuşak Armoni Katmanı
  private buildUdKanunNode(targetGain: GainNode) {
    if (!this.ctx) return;
    const osc1 = this.ctx.createOscillator();
    osc1.type = "sine";
    osc1.frequency.setValueAtTime(146.83, this.ctx.currentTime); // D3

    const osc2 = this.ctx.createOscillator();
    osc2.type = "sine";
    osc2.frequency.setValueAtTime(220.0, this.ctx.currentTime); // A3

    const filter = this.ctx.createBiquadFilter();
    filter.type = "lowpass";
    filter.frequency.setValueAtTime(380, this.ctx.currentTime);

    const g = this.ctx.createGain();
    g.gain.setValueAtTime(0.12, this.ctx.currentTime);

    osc1.connect(filter);
    osc2.connect(filter);
    filter.connect(g);
    g.connect(targetGain);

    osc1.start();
    osc2.start();
  }

  // 3. Doğal Su Şırıltısı / Çeşme
  private buildWaterNode(targetGain: GainNode) {
    if (!this.ctx) return;
    const buffer = this.createPinkNoiseBuffer();
    const source = this.ctx.createBufferSource();
    source.buffer = buffer;
    source.loop = true;

    const bandpass = this.ctx.createBiquadFilter();
    bandpass.type = "bandpass";
    bandpass.frequency.setValueAtTime(650, this.ctx.currentTime);
    bandpass.Q.setValueAtTime(3.5, this.ctx.currentTime);

    const lfo = this.ctx.createOscillator();
    lfo.frequency.setValueAtTime(0.4, this.ctx.currentTime);
    const lfoGain = this.ctx.createGain();
    lfoGain.gain.setValueAtTime(250, this.ctx.currentTime);
    lfo.connect(lfoGain);
    lfoGain.connect(bandpass.frequency);

    source.connect(bandpass);
    bandpass.connect(targetGain);
    source.start();
    lfo.start();
  }

  // 4. Rahmet Yağmuru
  private buildRainNode(targetGain: GainNode) {
    if (!this.ctx) return;
    const buffer = this.createPinkNoiseBuffer();
    const source = this.ctx.createBufferSource();
    source.buffer = buffer;
    source.loop = true;

    const lowpass = this.ctx.createBiquadFilter();
    lowpass.type = "lowpass";
    lowpass.frequency.setValueAtTime(1100, this.ctx.currentTime);

    const highpass = this.ctx.createBiquadFilter();
    highpass.type = "highpass";
    highpass.frequency.setValueAtTime(180, this.ctx.currentTime);

    source.connect(lowpass);
    lowpass.connect(highpass);
    highpass.connect(targetGain);
    source.start();
  }

  // 5. Seher Rüzgârı
  private buildWindNode(targetGain: GainNode) {
    if (!this.ctx) return;
    const buffer = this.createPinkNoiseBuffer();
    const source = this.ctx.createBufferSource();
    source.buffer = buffer;
    source.loop = true;

    const filter = this.ctx.createBiquadFilter();
    filter.type = "bandpass";
    filter.frequency.setValueAtTime(320, this.ctx.currentTime);
    filter.Q.setValueAtTime(2.0, this.ctx.currentTime);

    const lfo = this.ctx.createOscillator();
    lfo.frequency.setValueAtTime(0.12, this.ctx.currentTime);
    const lfoGain = this.ctx.createGain();
    lfoGain.gain.setValueAtTime(140, this.ctx.currentTime);
    lfo.connect(lfoGain);
    lfoGain.connect(filter.frequency);

    source.connect(filter);
    filter.connect(targetGain);
    source.start();
    lfo.start();
  }

  // 6. Medrese / Kubbe Derin Dem Nefesi
  private buildMedreseNode(targetGain: GainNode) {
    if (!this.ctx) return;
    const osc1 = this.ctx.createOscillator();
    osc1.type = "sine";
    osc1.frequency.setValueAtTime(110, this.ctx.currentTime);

    const osc2 = this.ctx.createOscillator();
    osc2.type = "triangle";
    osc2.frequency.setValueAtTime(165, this.ctx.currentTime);

    const filter = this.ctx.createBiquadFilter();
    filter.type = "lowpass";
    filter.frequency.setValueAtTime(240, this.ctx.currentTime);

    const sub = this.ctx.createGain();
    sub.gain.setValueAtTime(0.15, this.ctx.currentTime);

    osc1.connect(filter);
    osc2.connect(filter);
    filter.connect(sub);
    sub.connect(targetGain);

    osc1.start();
    osc2.start();
  }

  // 7. Kozmik Semâvî Belgesel Pad'i
  private buildCosmicNode(targetGain: GainNode) {
    if (!this.ctx) return;
    const osc1 = this.ctx.createOscillator();
    osc1.type = "sine";
    osc1.frequency.setValueAtTime(65.41, this.ctx.currentTime); // C2 derin uzay bası

    const osc2 = this.ctx.createOscillator();
    osc2.type = "sine";
    osc2.frequency.setValueAtTime(98.0, this.ctx.currentTime); // G2 beşli armoni

    const filter = this.ctx.createBiquadFilter();
    filter.type = "lowpass";
    filter.frequency.setValueAtTime(190, this.ctx.currentTime);

    const sub = this.ctx.createGain();
    sub.gain.setValueAtTime(0.2, this.ctx.currentTime);

    osc1.connect(filter);
    osc2.connect(filter);
    filter.connect(sub);
    sub.connect(targetGain);

    osc1.start();
    osc2.start();
  }

  private syncWavPlayback() {
    if (typeof window === "undefined") return;
    const theme = AMBIENT_MUSIC_THEMES.find((t) => t.id === this.currentThemeId);
    if (!this.isRunning || !theme || !theme.wavSrc || this.currentThemeId === "none") {
      if (this.wavAudio) {
        this.wavAudio.pause();
      }
      return;
    }

    if (!this.wavAudio) {
      this.wavAudio = new Audio();
      this.wavAudio.loop = true;
    }

    if (!this.wavAudio.src.endsWith(theme.wavSrc)) {
      this.wavAudio.src = theme.wavSrc;
    }

    const effectiveVol = this.isDuckedForQuran ? 0.02 : Math.min(1, this.masterVolume * 0.85);
    this.wavAudio.volume = effectiveVol;
    if (this.wavAudio.paused) {
      this.wavAudio.play().catch(() => {});
    }
  }

  public start(
    channelConfigs?: Record<string, { volume: number; muted: boolean }>,
    themeId?: string
  ) {
    if (themeId) this.currentThemeId = themeId;
    if (this.currentThemeId === "none") {
      this.stop();
      return;
    }

    this.initContext();
    if (!this.ctx || !this.masterGain) return;

    if (!this.isRunning) {
      this.isRunning = true;
      const theme =
        AMBIENT_MUSIC_THEMES.find((t) => t.id === this.currentThemeId) || AMBIENT_MUSIC_THEMES[0];

      for (const ch of SOUNDSCAPE_CHANNELS) {
        const g = this.ctx.createGain();
        const defaultVol = theme.defaultChannels[ch.id] ?? ch.volume;
        const cfg = channelConfigs?.[ch.id] || { volume: defaultVol, muted: ch.muted };
        const vol = cfg.muted ? 0.0 : cfg.volume;
        g.gain.setValueAtTime(vol, this.ctx.currentTime);
        g.connect(this.masterGain);
        this.channelGains.set(ch.id, g);

        if (ch.id === "ney") this.buildNeyNode(g);
        else if (ch.id === "ud_kanun") this.buildUdKanunNode(g);
        else if (ch.id === "rain") this.buildRainNode(g);
        else if (ch.id === "water") this.buildWaterNode(g);
        else if (ch.id === "wind") this.buildWindNode(g);
        else if (ch.id === "medrese") this.buildMedreseNode(g);
        else if (ch.id === "cosmic") this.buildCosmicNode(g);
      }
    }

    this.syncWavPlayback();
    this.notify();
  }

  public setTheme(themeId: string) {
    this.currentThemeId = themeId;
    if (themeId === "none") {
      this.stop();
      return;
    }
    const theme = AMBIENT_MUSIC_THEMES.find((t) => t.id === themeId);
    if (theme && this.isRunning && this.ctx) {
      for (const ch of SOUNDSCAPE_CHANNELS) {
        const targetVol = theme.defaultChannels[ch.id] ?? ch.volume;
        this.setChannelVolume(ch.id, targetVol, targetVol === 0);
      }
    }
    if (this.isRunning) {
      this.syncWavPlayback();
    }
    this.notify();
  }

  public setMasterVolume(vol: number) {
    this.masterVolume = Math.max(0, Math.min(1, vol));
    if (this.ctx && this.masterGain) {
      const target = this.isDuckedForQuran ? 0.02 : this.masterVolume;
      this.masterGain.gain.setTargetAtTime(target, this.ctx.currentTime, 0.15);
    }
    if (this.wavAudio) {
      this.wavAudio.volume = this.isDuckedForQuran
        ? 0.02
        : Math.min(1, this.masterVolume * 0.85);
    }
    this.notify();
  }

  // Kur'an-ı Kerim ayetleri okunurken fon müziğini edeben otomatik kısar / kapatır
  public setQuranDucking(isQuranReading: boolean) {
    if (this.isDuckedForQuran === isQuranReading) return;
    this.isDuckedForQuran = isQuranReading;
    if (this.ctx && this.masterGain) {
      const target = isQuranReading ? 0.01 : this.masterVolume;
      this.masterGain.gain.setTargetAtTime(target, this.ctx.currentTime, 0.25);
    }
    if (this.wavAudio) {
      this.wavAudio.volume = isQuranReading ? 0.0 : Math.min(1, this.masterVolume * 0.85);
    }
    this.notify();
  }

  public setChannelVolume(channelId: string, volume: number, muted: boolean = false) {
    if (!this.ctx) return;
    const g = this.channelGains.get(channelId);
    if (g) {
      const val = muted ? 0.0 : Math.max(0, Math.min(1, volume));
      g.gain.setTargetAtTime(val, this.ctx.currentTime, 0.08);
    }
  }

  public stop() {
    if (this.melodyInterval) {
      clearInterval(this.melodyInterval);
      this.melodyInterval = null;
    }
    if (this.wavAudio) {
      this.wavAudio.pause();
    }
    if (this.ctx && this.isRunning) {
      this.ctx.close().catch(() => {});
      this.ctx = null;
      this.masterGain = null;
      this.isRunning = false;
      this.channelGains.clear();
    } else {
      this.isRunning = false;
    }
    this.notify();
  }

  public getRunning(): boolean {
    return this.isRunning;
  }

  public getCurrentThemeId(): string {
    return this.currentThemeId;
  }

  public getMasterVolume(): number {
    return this.masterVolume;
  }

  public getIsDuckedForQuran(): boolean {
    return this.isDuckedForQuran;
  }
}

export const soundscapeEngine = new SoundscapeSynthesizer();
