#!/usr/bin/env python3
"""
Risale Dinle v10 — Sükûnet, Hikmetli Konu Fihristi, 18 Fonlu Stüdyo & Kişisel Radyo Akışı.
- Ana ekranda sıfır dikkat dağıtıcı öğe; yalnızca 4 sade bölüm: Konu Fihristi, Radyo Akışı, AI Liste, Tüm Dersler.
- 117 temiz Konu Fihristi başlığı (6.637 Doğrudan + 3.534 Hikmetli sıralanmış Dolaylı kesit = 10.171 hassas kesit).
- Her kesitte 'Doğrudan' / 'Dolaylı' ayrımı, neden o sırada yer aldığını açıklayan Türkçe Künye
  ve sade 'Hikmetli Sıra / Sadece Doğrudan / Dolaylı' filtresi.
- Kişisel Radyo Akışı: Zamanlı saat dilimleri veya 7/24 kesintisiz yayın, bloğa özel konu/filtre/fon/hız/okuyucu,
  kesitler arası Tefekkür Sükûtu (0–12 sn) ve isteğe bağlı yumuşak yayın bitiş saati.
- 18 dahili Ney/Saz, Tabiat/Rahmet ve Derin Odak fonu + Makamlar arası yumuşak döngü + IndexedDB MP3 yükleme.
- Tutarlı, sade ama zengin 2 katmanlı Ayarlar Stüdyosu: 4 tek-tık dinleme profili, segmentli hızlı seçiciler,
  özelleştirilebilir sarma adımı, yazı boyutu, 3 tema (Parşömen / Sepya / Gece), yer imleri ve JSON yedekleme.
"""

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = ROOT / "data"

catalog = json.loads((DATA_DIR / "risale-catalog.json").read_text(encoding="utf-8"))
segments_idx = json.loads((DATA_DIR / "segments-index.json").read_text(encoding="utf-8"))
playlists = json.loads((DATA_DIR / "topic-playlists.json").read_text(encoding="utf-8"))

# Compact segment reference lookup so the single-file bundle stays fast & light (~3.4 MB)
seg_lookup = {}
for i, s in enumerate(segments_idx):
    key = (s["trackId"], round(s["ss"], 1), round(s["es"], 1))
    if key not in seg_lookup:
        seg_lookup[key] = i


def compact_topic_clips(clips):
    out = []
    for c in clips:
        key = (c["trackId"], round(c["startSec"], 1), round(c["endSec"], 1))
        si = seg_lookup.get(key)
        rel_code = "d" if c.get("rel") == "direct" else "i"
        kunye = c.get("kunye", "")
        if si is not None:
            out.append([si, rel_code, kunye])
        else:
            out.append(c)
    return out


compact_topics = []
for p in playlists:
    compact_topics.append({
        "id": p["id"],
        "title": p["title"],
        "description": p["description"],
        "fihristRefs": p.get("fihristRefs", [])[:6],
        "curatedCount": len(p["clips"]),
        "directCount": p.get("directCount", 0),
        "indirectCount": p.get("indirectCount", 0),
        "clips": compact_topic_clips(p["clips"]),
    })

HTML_TEMPLATE = r"""<!DOCTYPE html>
<html lang="tr" data-theme="light">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, viewport-fit=cover" />
  <title>Risale Dinle</title>
  <meta name="description" content="Sade, dikkat dağıtmayan, hikmetli sıralamalı ve kişisel radyo akışlı Risale-i Nur sesli dinleme uygulaması." />
  <meta name="theme-color" content="#181512" />
  <meta name="mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />
  <meta name="apple-mobile-web-app-title" content="Risale Dinle" />
  <link rel="manifest" href="manifest.json" />
  <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 128 128'%3E%3Crect width='128' height='128' rx='28' fill='%23181512'/%3E%3Ccircle cx='64' cy='64' r='44' fill='none' stroke='%23cfa04e' stroke-width='3' opacity='0.45'/%3E%3Cpath d='M44 42h16c10 0 16 6 16 14 0 6-4 11-10 13l12 17H66L56 70h-4v16H44V42zm8 8v12h7c5 0 8-2 8-6s-3-6-8-6h-7z' fill='%23f5f0e7'/%3E%3C/svg%3E" />
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Amiri:wght@400;700&family=Plus+Jakarta+Sans:wght@400;500;600&display=swap" rel="stylesheet">
  <style>
    :root {
      --bg: #f9f6f0;
      --surface: #ffffff;
      --surface-soft: #fcfaf6;
      --subtle: #f1ece2;
      --text: #1c1814;
      --muted: #766d61;
      --border: rgba(28, 24, 20, 0.09);
      --border-strong: rgba(28, 24, 20, 0.16);
      --accent: #8f621f;
      --accent-soft: rgba(143, 98, 31, 0.09);
      --accent-mid: rgba(143, 98, 31, 0.24);
      --player-bg: #181512;
      --player-text: #f5f0e7;
      --player-muted: #9e9384;
      --radius: 12px;
      --ease: cubic-bezier(0.22, 1, 0.36, 1);
      --focus-font: 1.38rem;
    }

    [data-theme="sepia"] {
      --bg: #f2e8d5;
      --surface: #f8f1e2;
      --surface-soft: #f5ecdb;
      --subtle: #e9dcc4;
      --text: #271e15;
      --muted: #6e5b47;
      --border: rgba(39, 30, 21, 0.11);
      --border-strong: rgba(39, 30, 21, 0.20);
      --accent: #8a5617;
      --accent-soft: rgba(138, 86, 23, 0.10);
      --accent-mid: rgba(138, 86, 23, 0.25);
      --player-bg: #221a12;
      --player-text: #f5ecdc;
      --player-muted: #ae9a82;
    }

    [data-theme="dark"] {
      --bg: #12100e;
      --surface: #1a1714;
      --surface-soft: #1f1b17;
      --subtle: #25211c;
      --text: #ede7dc;
      --muted: #968b7c;
      --border: rgba(237, 231, 220, 0.08);
      --border-strong: rgba(237, 231, 220, 0.16);
      --accent: #cfa04e;
      --accent-soft: rgba(207, 160, 78, 0.12);
      --accent-mid: rgba(207, 160, 78, 0.28);
      --player-bg: #0d0b09;
      --player-text: #f0eae0;
      --player-muted: #8f8476;
    }

    * { box-sizing: border-box; margin: 0; padding: 0; }

    body {
      font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif;
      background: var(--bg);
      color: var(--text);
      line-height: 1.55;
      min-height: 100vh;
      padding-bottom: 100px;
      -webkit-font-smoothing: antialiased;
      transition: background 0.25s var(--ease), color 0.25s var(--ease);
    }

    h1, h2, h3, .serif { font-family: 'Amiri', Georgia, serif; font-weight: 700; }

    /* Ultra-Quiet Header */
    .topbar {
      position: sticky; top: 0; z-index: 30;
      background: color-mix(in srgb, var(--bg) 92%, transparent);
      backdrop-filter: blur(14px);
      border-bottom: 1px solid var(--border);
    }
    .topbar-inner {
      max-width: 840px; margin: 0 auto; padding: 13px 20px;
      display: flex; align-items: center; gap: 14px;
    }
    .brand {
      font-family: 'Amiri', serif; font-size: 1.45rem; font-weight: 700;
      color: var(--text); white-space: nowrap; cursor: pointer; letter-spacing: 0.01em;
    }
    .search-input {
      flex: 1; padding: 8px 14px; border-radius: 999px;
      border: 1px solid var(--border); background: var(--surface); color: var(--text);
      font-size: 0.84rem; outline: none; transition: border-color 0.2s, box-shadow 0.2s;
    }
    .search-input:focus { border-color: var(--accent); box-shadow: 0 0 0 3px var(--accent-soft); }
    .search-input::placeholder { color: var(--muted); }

    .quiet-btn {
      border: 1px solid var(--border); background: var(--surface); color: var(--text);
      border-radius: 999px; padding: 7px 14px; font-size: 0.81rem; font-weight: 500;
      cursor: pointer; white-space: nowrap; transition: border-color 0.18s, color 0.18s;
    }
    .quiet-btn:hover { border-color: var(--accent); color: var(--accent); }

    /* Main Container */
    .main { max-width: 840px; margin: 0 auto; padding: 16px 20px 36px; }

    /* Clean Underline Navigation — 4 Quiet Tabs */
    .nav-tabs {
      display: flex; align-items: center; gap: 24px;
      border-bottom: 1px solid var(--border); margin-bottom: 18px;
      overflow-x: auto; scrollbar-width: none;
    }
    .nav-tabs::-webkit-scrollbar { display: none; }
    .nav-link {
      border: none; background: transparent; color: var(--muted);
      padding: 10px 2px; font-size: 0.86rem; font-weight: 500;
      cursor: pointer; white-space: nowrap;
      border-bottom: 1.5px solid transparent; margin-bottom: -1px;
      transition: color 0.18s, border-color 0.18s;
    }
    .nav-link:hover { color: var(--text); }
    .nav-link.active { color: var(--text); font-weight: 600; border-bottom-color: var(--accent); }

    /* Quiet Segmented Control (Used cleanly in Topic Filter, Radio, & Settings) */
    .seg-ctrl {
      display: inline-flex; align-items: center; gap: 3px;
      background: var(--subtle); padding: 3px; border-radius: 9px;
      border: 1px solid var(--border); flex-wrap: wrap;
    }
    .seg-btn {
      border: none; background: transparent; color: var(--muted);
      padding: 5px 11px; border-radius: 7px; font-size: 0.76rem; font-weight: 500;
      cursor: pointer; white-space: nowrap; transition: all 0.15s var(--ease);
    }
    .seg-btn:hover { color: var(--text); }
    .seg-btn.active {
      background: var(--surface); color: var(--text); font-weight: 600;
      box-shadow: 0 1px 3px rgba(0,0,0,0.08);
    }

    /* Serene List Rows */
    .calm-list { display: flex; flex-direction: column; }
    .calm-item {
      display: grid; grid-template-columns: 1fr auto; align-items: center; gap: 14px;
      padding: 13px 10px; border-bottom: 1px solid var(--border);
      cursor: pointer; border-radius: 9px; transition: background 0.15s var(--ease);
    }
    .calm-item:hover { background: var(--subtle); }
    .calm-item.playing { background: var(--accent-soft); }

    .item-title {
      font-family: 'Amiri', Georgia, serif; font-size: 1.13rem; font-weight: 700;
      line-height: 1.32; color: var(--text);
    }
    .item-title.sans {
      font-family: 'Plus Jakarta Sans', sans-serif; font-size: 0.88rem; font-weight: 600; line-height: 1.38;
    }
    .item-sub {
      font-size: 0.77rem; color: var(--muted); margin-top: 3px;
      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
    }
    .item-kunye {
      font-size: 0.74rem; color: var(--muted); margin-top: 4px;
      line-height: 1.45; opacity: 0.88; font-style: italic;
    }

    .item-action {
      display: flex; align-items: center; gap: 8px;
      font-size: 0.74rem; color: var(--muted); white-space: nowrap;
    }
    .rel-tag { font-size: 0.71rem; color: var(--muted); letter-spacing: 0.01em; }
    .rel-tag.direct { color: var(--accent); font-weight: 500; }

    .circle-play {
      width: 31px; height: 31px; border-radius: 50%;
      border: 1px solid var(--border-strong); background: transparent; color: var(--text);
      display: grid; place-items: center; font-size: 0.7rem;
      cursor: pointer; transition: all 0.16s; flex-shrink: 0;
    }
    .calm-item:hover .circle-play,
    .calm-item.playing .circle-play {
      background: var(--accent); border-color: var(--accent); color: #fff;
    }

    .tiny-link {
      border: none; background: transparent; color: var(--muted);
      font-size: 0.73rem; cursor: pointer; padding: 3px 6px; border-radius: 6px;
    }
    .tiny-link:hover { background: var(--subtle); color: var(--accent); }

    /* Detail Header & Quiet Filter Bar */
    .detail-bar {
      display: flex; align-items: flex-start; justify-content: space-between;
      gap: 14px; margin-bottom: 14px; flex-wrap: wrap;
      padding-bottom: 14px; border-bottom: 1px solid var(--border);
    }
    .detail-bar h2 { font-size: 1.5rem; line-height: 1.22; }
    .detail-bar p { font-size: 0.79rem; color: var(--muted); margin-top: 4px; }

    .detail-toolbar {
      display: flex; align-items: center; justify-content: space-between;
      gap: 10px; flex-wrap: wrap; margin-bottom: 12px;
    }
    .detail-filters {
      display: flex; align-items: center; gap: 8px; flex-wrap: wrap; margin-bottom: 10px;
    }
    .detail-filters select, .detail-filters input {
      padding: 6px 10px; border-radius: 8px; border: 1px solid var(--border);
      background: var(--surface); color: var(--text); font-size: 0.78rem; outline: none;
    }
    .detail-filters select:focus, .detail-filters input:focus { border-color: var(--accent); }

    .btn-accent {
      background: var(--accent); color: #fff; border: none;
      border-radius: 8px; padding: 8px 14px; font-size: 0.81rem; font-weight: 500;
      cursor: pointer; transition: opacity 0.16s;
    }
    .btn-accent:hover { opacity: 0.9; }

    .btn-ghost {
      background: transparent; color: var(--text);
      border: 1px solid var(--border-strong); border-radius: 8px;
      padding: 7px 12px; font-size: 0.79rem; font-weight: 500; cursor: pointer;
      transition: border-color 0.16s, color 0.16s, background 0.16s;
    }
    .btn-ghost:hover { border-color: var(--accent); color: var(--accent); }
    .btn-ghost.active { border-color: var(--accent); color: var(--accent); background: var(--accent-soft); }

    .load-more-wrap { padding: 16px 0 4px; text-align: center; }

    /* =========================================================================
       RADYO AKIŞI (SERENE PERSONAL BROADCAST STUDIO)
       ========================================================================= */
    .radio-hero {
      background: var(--surface); border: 1px solid var(--border); border-radius: var(--radius);
      padding: 18px 18px 16px; margin-bottom: 14px;
      display: flex; align-items: center; justify-content: space-between; gap: 16px; flex-wrap: wrap;
    }
    .radio-status { font-size: 0.75rem; color: var(--accent); font-weight: 600; letter-spacing: 0.02em; }
    .radio-title { font-family: 'Amiri', serif; font-size: 1.45rem; line-height: 1.25; margin-top: 2px; }
    .radio-meta { font-size: 0.78rem; color: var(--muted); margin-top: 3px; }

    .radio-panel {
      background: var(--surface); border: 1px solid var(--border); border-radius: var(--radius);
      padding: 14px 16px; margin-bottom: 16px;
      display: flex; flex-direction: column; gap: 12px;
    }
    .radio-row {
      display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap;
      font-size: 0.82rem;
    }

    .block-card {
      background: var(--surface); border: 1px solid var(--border); border-radius: 10px;
      padding: 12px 14px; margin-bottom: 8px;
      display: flex; align-items: center; justify-content: space-between; gap: 12px;
      transition: border-color 0.16s, background 0.16s;
    }
    .block-card.live { border-color: var(--accent-mid); background: var(--accent-soft); }
    .block-card.disabled { opacity: 0.56; }
    .block-time { font-size: 0.74rem; color: var(--accent); font-weight: 600; font-variant-numeric: tabular-nums; }
    .block-name { font-size: 0.9rem; font-weight: 600; color: var(--text); margin-top: 1px; }
    .block-sub { font-size: 0.75rem; color: var(--muted); margin-top: 2px; }
    .block-ops { display: flex; align-items: center; gap: 5px; flex-shrink: 0; flex-wrap: wrap; }

    .radio-editor {
      background: var(--surface); border: 1px solid var(--border-strong); border-radius: var(--radius);
      padding: 16px; margin-bottom: 16px;
    }
    .radio-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin-bottom: 10px; }
    @media (max-width: 600px) { .radio-grid { grid-template-columns: 1fr; } }
    .field-lbl { display: block; font-size: 0.74rem; color: var(--muted); margin-bottom: 4px; }
    .field-inp {
      width: 100%; padding: 8px 10px; border-radius: 8px;
      border: 1px solid var(--border-strong); background: var(--bg); color: var(--text);
      font-size: 0.82rem; outline: none;
    }
    .field-inp:focus { border-color: var(--accent); }

    /* Minimal AI Prompt Bar */
    .ai-minimal { display: flex; gap: 8px; margin-bottom: 18px; flex-wrap: wrap; }
    .ai-minimal input {
      flex: 1; min-width: 220px; padding: 10px 14px; border-radius: 10px;
      border: 1px solid var(--border-strong); background: var(--surface); color: var(--text);
      font-size: 0.86rem; outline: none;
    }
    .ai-minimal input:focus { border-color: var(--accent); }
    .ai-minimal select {
      padding: 9px 11px; border-radius: 10px; border: 1px solid var(--border-strong);
      background: var(--surface); color: var(--text); font-size: 0.82rem;
    }

    /* Sub-Segments */
    .sub-segs {
      padding: 4px 10px 10px 22px; border-bottom: 1px solid var(--border);
      display: flex; flex-direction: column; gap: 4px;
    }
    .sub-seg-row {
      display: flex; justify-content: space-between; align-items: center; gap: 10px;
      padding: 7px 10px; border-radius: 7px; font-size: 0.78rem; color: var(--muted);
      cursor: pointer; transition: background 0.14s, color 0.14s;
    }
    .sub-seg-row:hover { background: var(--subtle); color: var(--text); }

    /* Minimalist Bottom Player */
    .player {
      position: fixed; left: 0; right: 0; bottom: 0; z-index: 40;
      background: var(--player-bg); color: var(--player-text);
      border-top: 1px solid rgba(255,255,255,0.07); padding: 10px 20px 12px;
    }
    .player-inner {
      max-width: 840px; margin: 0 auto;
      display: grid; grid-template-columns: 1.25fr auto 1.15fr;
      align-items: center; gap: 16px;
    }
    @media (max-width: 700px) {
      .player-inner { grid-template-columns: 1fr; gap: 8px; }
      body { padding-bottom: 150px; }
    }
    .p-info { min-width: 0; cursor: pointer; }
    .p-title { font-size: 0.85rem; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .p-sub { font-size: 0.72rem; color: var(--player-muted); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-top: 1px; }

    .p-controls { display: flex; align-items: center; justify-content: center; gap: 8px; }
    .p-btn {
      background: transparent; border: none; color: var(--player-text);
      cursor: pointer; font-size: 0.88rem; padding: 6px 8px; border-radius: 8px;
      opacity: 0.82; transition: opacity 0.15s, background 0.15s;
    }
    .p-btn:hover { opacity: 1; background: rgba(255,255,255,0.07); }
    .p-play {
      width: 36px; height: 36px; border-radius: 50%;
      background: var(--accent); color: #fff; opacity: 1;
      display: grid; place-items: center; font-size: 0.84rem;
    }
    .p-play:hover { background: var(--accent); opacity: 0.94; }

    .p-progress {
      display: flex; align-items: center; gap: 8px;
      font-size: 0.7rem; color: var(--player-muted); font-variant-numeric: tabular-nums;
    }
    .seek-wrap { position: relative; flex: 1; display: flex; align-items: center; height: 18px; }
    .seek-wrap input[type="range"] { width: 100%; accent-color: var(--accent); cursor: pointer; height: 3px; position: relative; z-index: 3; }
    .timeline-ticks { position: absolute; left: 0; right: 0; top: 50%; transform: translateY(-50%); height: 5px; pointer-events: none; z-index: 2; }
    .slice-win { position: absolute; top: 50%; transform: translateY(-50%); height: 5px; background: rgba(207, 160, 78, 0.34); border-radius: 3px; pointer-events: none; z-index: 1; }
    .q-tick { position: absolute; top: 0; bottom: 0; background: rgba(236, 194, 114, 0.75); border-radius: 2px; min-width: 2px; }

    /* Optional Full-Screen Tefekkür & Listening Studio Overlay */
    .focus-modal {
      position: fixed; inset: 0; z-index: 55; background: var(--bg);
      display: flex; flex-direction: column;
      opacity: 0; pointer-events: none; transition: opacity 0.25s var(--ease);
    }
    .focus-modal.open { opacity: 1; pointer-events: auto; }
    .focus-head {
      max-width: 780px; width: 100%; margin: 0 auto; padding: 16px 20px;
      display: flex; justify-content: space-between; align-items: center; gap: 12px;
      border-bottom: 1px solid var(--border);
    }
    .focus-tabs {
      max-width: 780px; width: 100%; margin: 0 auto; padding: 0 20px;
      display: flex; gap: 20px; border-bottom: 1px solid var(--border);
    }
    .focus-body { max-width: 780px; width: 100%; margin: 0 auto; flex: 1; overflow-y: auto; padding: 20px 20px 115px; }
    .focus-kunye {
      background: var(--surface); border: 1px solid var(--border); border-radius: 10px;
      padding: 10px 13px; margin: 10px 0 14px; font-size: 0.8rem; color: var(--muted); line-height: 1.5;
    }
    .focus-excerpt {
      font-family: 'Amiri', Georgia, serif; font-size: var(--focus-font);
      line-height: 1.82; color: var(--text); margin: 12px 0 22px; white-space: pre-line;
    }
    .focus-bar {
      display: flex; align-items: center; gap: 8px; flex-wrap: wrap;
      padding: 10px 0 16px; border-bottom: 1px solid var(--border); margin-bottom: 14px;
    }

    /* =========================================================================
       COHESIVE, SIMPLE YET RICH 2-LAYER SETTINGS STUDIO
       ========================================================================= */
    .drawer-backdrop {
      position: fixed; inset: 0; z-index: 60; background: rgba(12, 10, 8, 0.44);
      backdrop-filter: blur(3px); opacity: 0; pointer-events: none; transition: opacity 0.24s var(--ease);
    }
    .drawer-backdrop.open { opacity: 1; pointer-events: auto; }

    .drawer {
      position: fixed; top: 0; right: 0; bottom: 0; width: min(465px, 95vw); z-index: 70;
      background: var(--bg); border-left: 1px solid var(--border);
      box-shadow: -16px 0 44px rgba(0, 0, 0, 0.14);
      transform: translateX(100%); transition: transform 0.28s var(--ease);
      display: flex; flex-direction: column;
    }
    .drawer.open { transform: translateX(0); }

    .drawer-head {
      padding: 15px 20px; background: color-mix(in srgb, var(--surface) 90%, transparent);
      backdrop-filter: blur(12px); border-bottom: 1px solid var(--border);
      display: flex; align-items: center; justify-content: space-between; gap: 10px;
    }
    .drawer-head h2 { font-size: 1.3rem; }
    .drawer-body { flex: 1; overflow-y: auto; padding: 15px 18px 36px; }

    /* One-Click Listening Profiles at top of Settings Layer 1 */
    .preset-strip { margin-bottom: 14px; }
    .preset-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 7px; margin-top: 7px; }
    .preset-card {
      background: var(--surface); border: 1px solid var(--border); border-radius: 10px;
      padding: 10px 11px; text-align: left; cursor: pointer;
      transition: border-color 0.16s, background 0.16s;
    }
    .preset-card:hover { border-color: var(--accent-mid); background: var(--surface-soft); }
    .preset-name { font-size: 0.82rem; font-weight: 600; color: var(--text); }
    .preset-desc { font-size: 0.71rem; color: var(--muted); margin-top: 2px; }

    /* Layer 1 Category Cards */
    .cat-menu { display: flex; flex-direction: column; gap: 8px; }
    .cat-row {
      display: flex; align-items: center; justify-content: space-between; gap: 12px;
      padding: 12px 14px; background: var(--surface); border: 1px solid var(--border);
      border-radius: var(--radius); cursor: pointer;
      transition: border-color 0.18s var(--ease), background 0.18s var(--ease), transform 0.18s var(--ease);
    }
    .cat-row:hover { border-color: var(--accent-mid); background: var(--surface-soft); transform: translateX(-2px); }
    .cat-left { display: flex; align-items: center; gap: 12px; min-width: 0; }
    .cat-icon {
      width: 34px; height: 34px; border-radius: 9px; flex-shrink: 0;
      background: var(--accent-soft); color: var(--accent); display: grid; place-items: center;
    }
    .cat-icon svg { width: 17px; height: 17px; stroke: currentColor; fill: none; stroke-width: 1.7; stroke-linecap: round; stroke-linejoin: round; }
    .cat-row-title { font-size: 0.88rem; font-weight: 600; color: var(--text); }
    .cat-row-sub { font-size: 0.74rem; color: var(--muted); margin-top: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .cat-arrow { color: var(--muted); font-size: 1rem; flex-shrink: 0; }

    /* Layer 2 Bounded Studio Cards */
    .set-card {
      background: var(--surface); border: 1px solid var(--border);
      border-radius: var(--radius); padding: 14px 15px 12px; margin-bottom: 12px;
    }
    .set-card h3 {
      font-size: 1.04rem; margin-bottom: 11px; color: var(--text);
      padding-bottom: 7px; border-bottom: 1px solid var(--border);
    }
    .set-row {
      display: flex; align-items: center; justify-content: space-between;
      gap: 10px; margin-bottom: 11px; font-size: 0.81rem;
    }
    .set-row:last-child { margin-bottom: 2px; }
    .set-row label { cursor: pointer; flex: 1; }
    .set-row select, .set-row input[type="time"] {
      padding: 6px 9px; border-radius: 8px; border: 1px solid var(--border-strong);
      background: var(--bg); color: var(--text); font-size: 0.79rem; outline: none; max-width: 225px;
    }
    .set-row select:focus, .set-row input[type="time"]:focus { border-color: var(--accent); }
    .set-col { display: flex; flex-direction: column; gap: 6px; margin-bottom: 11px; font-size: 0.81rem; }
    .set-col:last-child { margin-bottom: 2px; }

    /* Custom Refined Toggle Switch */
    .switch {
      appearance: none; -webkit-appearance: none;
      width: 38px; height: 22px; border-radius: 999px; background: var(--border-strong);
      position: relative; cursor: pointer; flex-shrink: 0; border: none; outline: none;
      transition: background 0.2s var(--ease);
    }
    .switch::after {
      content: ''; position: absolute; top: 3px; left: 3px; width: 16px; height: 16px;
      border-radius: 50%; background: #fff; box-shadow: 0 1px 3px rgba(0,0,0,0.22);
      transition: transform 0.2s var(--ease);
    }
    .switch:checked { background: var(--accent); }
    .switch:checked::after { transform: translateX(16px); }

    .set-stack { display: flex; flex-direction: column; gap: 8px; }
    .set-stack input, .set-stack select {
      width: 100%; padding: 8px 10px; border-radius: 8px;
      border: 1px solid var(--border-strong); background: var(--bg); color: var(--text);
      font-size: 0.81rem; outline: none;
    }
    .set-stack input:focus, .set-stack select:focus { border-color: var(--accent); }

    .mixer-row { display: flex; flex-direction: column; gap: 5px; margin-bottom: 12px; }
    .mixer-lbl { display: flex; justify-content: space-between; font-size: 0.81rem; font-weight: 500; }
    .mixer-val { color: var(--accent); font-weight: 600; font-variant-numeric: tabular-nums; }
    .mixer-row input[type="range"] { width: 100%; accent-color: var(--accent); cursor: pointer; height: 5px; }
    .balance-track { height: 5px; border-radius: 999px; background: var(--subtle); overflow: hidden; display: flex; margin: 2px 0 10px; }
    .balance-risale { background: var(--accent); transition: width 0.2s; }
    .balance-fon { background: var(--muted); opacity: 0.55; transition: width 0.2s; }
    .ratio-row { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 4px; }

    /* Visual Theme Cards */
    .theme-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
    .theme-card {
      border: 1.5px solid var(--border); border-radius: 10px; padding: 10px 8px;
      cursor: pointer; text-align: center; font-size: 0.76rem; font-weight: 500;
      transition: border-color 0.18s, transform 0.16s;
    }
    .theme-card:hover { transform: translateY(-1px); }
    .theme-card.active { border-color: var(--accent); box-shadow: 0 0 0 2px var(--accent-soft); }
    .theme-swatch { height: 22px; border-radius: 6px; margin-bottom: 6px; border: 1px solid rgba(0,0,0,0.08); }

    /* Stats inside Settings */
    .stat-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; }
    .stat-box { background: var(--bg); border: 1px solid var(--border); border-radius: 9px; padding: 10px 12px; }
    .stat-num { font-family: 'Amiri', serif; font-size: 1.35rem; font-weight: 700; color: var(--accent); line-height: 1.1; }
    .stat-lbl { font-size: 0.72rem; color: var(--muted); margin-top: 3px; }

    .mini-item {
      display: flex; align-items: center; justify-content: space-between; gap: 8px;
      padding: 8px 10px; border: 1px solid var(--border); border-radius: 8px;
      background: var(--bg); font-size: 0.78rem; margin-bottom: 6px;
    }

    .days-row { display: flex; gap: 4px; flex-wrap: wrap; }
    .day-chip {
      border: 1px solid var(--border-strong); background: transparent; color: var(--muted);
      border-radius: 6px; padding: 4px 8px; font-size: 0.72rem; cursor: pointer;
    }
    .day-chip.on { background: var(--accent); color: #fff; border-color: var(--accent); }

    .kbd { font-family: monospace; font-size: 0.73rem; padding: 2px 6px; border-radius: 5px; background: var(--subtle); border: 1px solid var(--border-strong); }
    .hint { font-size: 0.74rem; color: var(--muted); line-height: 1.5; }
    .hidden { display: none !important; }

    .toast {
      position: fixed; top: 66px; right: 20px; z-index: 90;
      background: var(--player-bg); color: var(--player-text);
      padding: 8px 14px; border-radius: 9px; font-size: 0.79rem;
      box-shadow: 0 6px 20px rgba(0,0,0,0.2);
      opacity: 0; pointer-events: none; transition: opacity 0.2s;
    }
    .toast.show { opacity: 1; }
  </style>
</head>
<body>
  <!-- ULTRA-QUIET TOPBAR -->
  <header class="topbar">
    <div class="topbar-inner">
      <div class="brand" id="brandHome">Risale Dinle</div>
      <input id="searchInput" class="search-input" type="search" placeholder="Konu veya risale ara..." />
      <button id="openSettingsBtn" class="quiet-btn" aria-label="Ayarlar">Ayarlar</button>
    </div>
  </header>

  <!-- SERENE, DISTRACTION-FREE MAIN CANVAS (4 PURE SECTIONS) -->
  <main class="main">
    <nav class="nav-tabs" aria-label="Bölümler">
      <button class="nav-link active" id="viewTopicsBtn">Konu Fihristi</button>
      <button class="nav-link" id="viewRadioBtn">Radyo Akışı</button>
      <button class="nav-link" id="viewAiBtn">AI Liste</button>
      <button class="nav-link" id="viewAllBtn">Tüm Dersler</button>
    </nav>

    <!-- Topic / AI Detail View -->
    <section id="topicDetail" class="hidden">
      <div class="detail-bar">
        <div style="min-width:0; flex:1;">
          <h2 id="detailTitle"></h2>
          <p id="detailRefs"></p>
        </div>
        <div style="display:flex; gap:7px; flex-wrap:wrap;">
          <button class="btn-accent" id="detailPlayAll">Başlat</button>
          <button class="btn-ghost hidden" id="detailResumeBtn">Kaldığın Yerden</button>
          <button class="btn-ghost" id="detailShuffleBtn">Karışık</button>
          <button class="btn-ghost" id="detailFonBtn">Liste Fonu</button>
          <button class="btn-ghost" id="detailBack">Geri</button>
        </div>
      </div>

      <!-- Simple, Distraction-Free Direct / Hikmetli Filter -->
      <div id="detailRelBar" class="detail-toolbar hidden">
        <div class="seg-ctrl" id="detailRelSeg">
          <button class="seg-btn active" data-rel="all">Hikmetli Sıra</button>
          <button class="seg-btn" data-rel="direct">Sadece Doğrudan</button>
          <button class="seg-btn" data-rel="indirect">Dolaylı Bağlam</button>
        </div>
        <span class="hint" id="detailCountHint"></span>
      </div>

      <div id="detailFiltersBar" class="detail-filters hidden">
        <select id="detailChapterFilter"><option value="">Tüm Risaleler</option></select>
        <select id="detailReaderFilter"><option value="">Tüm Okuyucular</option></select>
        <input id="detailSearchFilter" type="search" placeholder="Bu konu içinde ara..." style="flex:1; min-width:150px;" />
      </div>

      <div class="calm-list" id="detailTracks"></div>
      <div id="detailLoadMoreWrap" class="load-more-wrap hidden">
        <button class="btn-ghost" id="detailLoadMoreBtn">Daha fazla kesit göster</button>
      </div>
    </section>

    <!-- 1. Konu Fihristi (117 Temiz & Hikmetli Konu) -->
    <section id="topicsSection">
      <div class="calm-list" id="topicList"></div>
    </section>

    <!-- 2. Radyo Akışı (Kişisel Sürekli / Zamanlı Yayın Akışı) -->
    <section id="radioSection" class="hidden">
      <div class="radio-hero">
        <div style="min-width:0; flex:1;">
          <div class="radio-status" id="radioLiveStatus">Radyo Hazır</div>
          <div class="radio-title" id="radioCurrentBlockTitle">Kişisel Risale-i Nur Yayın Akışı</div>
          <div class="radio-meta" id="radioCurrentMeta">Saat dilimlerine göre veya kesintisiz 7/24 kendi dinleme akışınızı yönetin.</div>
        </div>
        <div style="display:flex; gap:8px; flex-wrap:wrap;">
          <button class="btn-accent" id="radioToggleBtn">Yayını Başlat</button>
          <button class="btn-ghost" id="radioNextBlockBtn">Sonraki Blok</button>
        </div>
      </div>

      <div class="radio-panel">
        <div class="radio-row">
          <span>Yayın Modu</span>
          <div class="seg-ctrl" id="radioModeSeg">
            <button class="seg-btn active" data-rmode="scheduled">Zamanlı Yayın Akışı</button>
            <button class="seg-btn" data-rmode="continuous">Kesintisiz 7/24 Akış</button>
          </div>
        </div>
        <div class="radio-row">
          <span>Kesitler Arası Tefekkür Sükûtu</span>
          <div class="seg-ctrl" id="radioGapSeg">
            <button class="seg-btn" data-gap="0">0 sn</button>
            <button class="seg-btn" data-gap="3">3 sn</button>
            <button class="seg-btn active" data-gap="5">5 sn</button>
            <button class="seg-btn" data-gap="8">8 sn</button>
            <button class="seg-btn" data-gap="12">12 sn</button>
          </div>
        </div>
        <div class="radio-row">
          <div style="display:flex; align-items:center; gap:10px;">
            <input class="switch" id="radioStopEnabledChk" type="checkbox" />
            <label for="radioStopEnabledChk" style="cursor:pointer;">Otomatik Yayın Bitiş Saati (15 sn yumuşak kapanış)</label>
          </div>
          <input id="radioStopTimeInp" type="time" value="23:30" style="padding:5px 9px; border-radius:8px; border:1px solid var(--border-strong); background:var(--bg); color:var(--text); font-size:0.8rem;" />
        </div>
      </div>

      <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:10px;">
        <span style="font-family:'Amiri',serif; font-size:1.18rem; font-weight:700;">Yayın Akışı Blokları</span>
        <button class="btn-ghost" id="openNewBlockBtn">+ Blok Ekle</button>
      </div>

      <!-- Add / Edit Radio Block Form (Hidden until clicked) -->
      <div id="radioBlockEditor" class="radio-editor hidden">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <h3 id="radioEditorHeading" style="font-size:1.1rem;">Yeni Yayın Bloğu</h3>
          <button class="tiny-link" id="cancelBlockEditBtn">Vazgeç</button>
        </div>
        <div class="radio-grid">
          <div>
            <label class="field-lbl">Blok Adı</label>
            <input class="field-inp" id="rbTitle" type="text" placeholder="Örn: Seher ve Sabah Tefekkürü" />
          </div>
          <div>
            <label class="field-lbl">Yayın Kaynağı</label>
            <select class="field-inp" id="rbSource"></select>
          </div>
          <div>
            <label class="field-lbl">Başlangıç Saati</label>
            <input class="field-inp" id="rbStart" type="time" value="06:00" />
          </div>
          <div>
            <label class="field-lbl">Bitiş Saati</label>
            <input class="field-inp" id="rbEnd" type="time" value="09:00" />
          </div>
          <div>
            <label class="field-lbl">İçerik Sıralaması</label>
            <select class="field-inp" id="rbFilter">
              <option value="all">Hikmetli Sıra (Doğrudan + Dolaylı)</option>
              <option value="direct">Sadece Doğrudan Alakalılar</option>
              <option value="shuffle">Karışık Yayın Akışı</option>
            </select>
          </div>
          <div>
            <label class="field-lbl">Bloğa Özel Fon Müziği</label>
            <select class="field-inp" id="rbFon"></select>
          </div>
          <div>
            <label class="field-lbl">Fon Seviyesi (0–10): <span id="rbFonValLbl">2.5</span></label>
            <input id="rbFonVol" type="range" min="0" max="10" step="0.5" value="2.5" style="width:100%; accent-color:var(--accent); margin-top:7px;" />
          </div>
          <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
            <div>
              <label class="field-lbl">Hız</label>
              <select class="field-inp" id="rbSpeed">
                <option value="0.85">0.85x</option>
                <option value="0.9">0.9x</option>
                <option value="1" selected>1.0x</option>
                <option value="1.15">1.15x</option>
                <option value="1.25">1.25x</option>
              </select>
            </div>
            <div>
              <label class="field-lbl">Okuyucu</label>
              <select class="field-inp" id="rbReader"><option value="">Tümü</option></select>
            </div>
          </div>
        </div>
        <div style="margin-bottom:12px;">
          <label class="field-lbl">Yayın Günleri</label>
          <div class="days-row" id="rbDaysRow"></div>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end;">
          <button class="btn-accent" id="saveBlockBtn">Bloğu Kaydet</button>
        </div>
      </div>

      <div id="radioBlocksList"></div>
    </section>

    <!-- 3. AI Liste -->
    <section id="aiSection" class="hidden">
      <div class="ai-minimal">
        <input id="aiPromptInput" type="text" placeholder="Örn: Allah'a iman ve tevhid delillerinden 30 dakikalık kesit listesi" />
        <select id="aiDurationSelect">
          <option value="15">15 dk</option>
          <option value="30" selected>30 dk</option>
          <option value="45">45 dk</option>
          <option value="60">60 dk</option>
          <option value="90">90 dk</option>
        </select>
        <select id="aiModeSelect" title="Sıralama biçimi">
          <option value="all" selected>Hikmetli Sıra</option>
          <option value="direct">Sadece Doğrudan</option>
        </select>
        <button class="btn-accent" id="runAiBtn">Oluştur</button>
        <button class="btn-ghost" id="openAiApiBtn" title="Yapay Zeka Sağlayıcı ve API Anahtarı">API Ayarı</button>
      </div>
      <div id="aiEngineHint" class="hint" style="margin:-10px 0 16px 2px;"></div>
      <div id="aiResultBox" class="hidden" style="margin-bottom:24px;">
        <div class="detail-bar">
          <div>
            <h2 id="aiResultTitle"></h2>
            <p id="aiResultNote"></p>
          </div>
          <div style="display:flex; gap:7px; flex-wrap:wrap;">
            <button class="btn-accent" id="aiPlayBtn">Başlat</button>
            <button class="btn-ghost" id="aiSaveBtn">Kaydet</button>
            <button class="btn-ghost" id="aiToRadioBtn">Radyoya Ekle</button>
          </div>
        </div>
        <div class="calm-list" id="aiResultTracks"></div>
      </div>
      <div id="savedUserListsWrap"></div>
    </section>

    <!-- 4. Tüm Dersler -->
    <section id="allTracksSection" class="hidden">
      <div class="calm-list" id="allTracksList"></div>
    </section>
  </main>

  <!-- MINIMAL BOTTOM PLAYER -->
  <footer class="player">
    <audio id="audioEl" preload="metadata"></audio>
    <audio id="preloadEl" preload="auto" class="hidden"></audio>
    <audio id="fonAudioEl" preload="auto"></audio>
    <div class="player-inner">
      <div class="p-info" id="openFocusTrigger" title="Tefekkür Sahnesi, Künye, Kuyruk ve Yer İmleri">
        <div class="p-title" id="pTitle">Risale Dinle</div>
        <div class="p-sub" id="pSub">Dinlemek için bir konu, radyo akışı veya ders seçin</div>
      </div>
      <div class="p-controls">
        <button class="p-btn" id="prevBtn" title="Önceki (Shift+←)">⏮</button>
        <button class="p-btn p-play" id="playBtn" title="Oynat / Duraklat (Boşluk)">▶</button>
        <button class="p-btn" id="nextBtn" title="Sonraki (Shift+→)">⏭</button>
      </div>
      <div class="p-progress">
        <span id="tCur">00:00</span>
        <div class="seek-wrap">
          <div class="slice-win hidden" id="sliceWinBar"></div>
          <div class="timeline-ticks" id="timelineTicks"></div>
          <input id="seekBar" type="range" min="0" max="100" value="0" step="0.1" />
        </div>
        <span id="tDur">00:00</span>
      </div>
    </div>
  </footer>

  <!-- OPTIONAL FULL-SCREEN TEFEKKÜR, KÜNYE, KUYRUK & YER İMLERİ LAYER -->
  <div class="focus-modal" id="focusModal">
    <div class="focus-head">
      <div style="min-width:0;">
        <div id="focusTrackTitle" style="font-weight:600; font-size:0.95rem; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">Tefekkür Sahnesi</div>
        <div id="focusTrackMeta" class="hint"></div>
      </div>
      <button class="quiet-btn" id="closeFocusBtn">Kapat</button>
    </div>
    <div class="focus-tabs">
      <button class="nav-link active" id="fTabPasaj">Okunan Pasaj & Akış</button>
      <button class="nav-link" id="fTabQueue">Sıradakiler</button>
      <button class="nav-link" id="fTabMarks">Yer İmlerim & Geçmiş</button>
    </div>
    <div class="focus-body">
      <div id="focusPanelPasaj">
        <div class="focus-bar">
          <button class="btn-ghost" id="fRewBtn">-15 sn</button>
          <button class="btn-ghost" id="fFwdBtn">+15 sn</button>
          <button class="btn-ghost" id="fRepeatBtn">Tekrar: Kapalı</button>
          <button class="btn-ghost" id="fShuffleBtn">Karışık: Kapalı</button>
          <button class="btn-accent" id="fBookmarkBtn">Yer İmi Kaydet</button>
        </div>
        <div id="focusSegHeading" class="hint" style="text-transform:uppercase; letter-spacing:0.05em;"></div>
        <div id="focusKunyeBox" class="focus-kunye hidden"></div>
        <div id="focusExcerpt" class="focus-excerpt">Bir ders başlattığınızda o saniyede okunan Külliyat pasajı burada huzurla takip edilir.</div>
        <div style="border-top:1px solid var(--border); padding-top:16px;">
          <div class="hint" style="margin-bottom:10px;">Bu kaydın saniye-saniye Külliyat akışı (atlamak için dokunun):</div>
          <div id="focusTimelineList" style="display:flex; flex-direction:column; gap:4px;"></div>
        </div>
      </div>
      <div id="focusPanelQueue" class="hidden">
        <div class="hint" id="focusQueueInfo" style="margin-bottom:12px;"></div>
        <div id="focusQueueList" style="display:flex; flex-direction:column; gap:4px;"></div>
      </div>
      <div id="focusPanelMarks" class="hidden">
        <div class="set-stack" style="margin-bottom:16px;">
          <div style="display:flex; gap:8px;">
            <input id="bookmarkNoteInput" type="text" placeholder="İsteğe bağlı tefekkür notu (örn: Haşir delili)" />
            <button class="btn-accent" id="addBookmarkNoteBtn" style="white-space:nowrap;">Saniyeyi Kaydet</button>
          </div>
        </div>
        <div class="hint" style="margin-bottom:8px;">Kayıtlı Yer İmlerim</div>
        <div id="focusBookmarksList" style="margin-bottom:22px;"></div>
        <div class="hint" style="margin-bottom:8px;">Son Dinlenenler</div>
        <div id="focusHistoryList"></div>
      </div>
    </div>
  </div>

  <!-- CONSISTENT, SIMPLE YET RICH 2-LAYER SETTINGS STUDIO -->
  <div class="drawer-backdrop" id="drawerBackdrop"></div>
  <aside class="drawer" id="settingsDrawer" aria-label="Ayarlar">
    <div class="drawer-head">
      <div style="display:flex; align-items:center; gap:10px;">
        <button class="btn-ghost hidden" id="drawerBackBtn" style="padding:4px 10px;">← Geri</button>
        <h2 id="drawerTitle">Ayarlar</h2>
      </div>
      <button class="btn-ghost" id="closeSettingsBtn" style="padding:5px 11px;">Kapat</button>
    </div>

    <div class="drawer-body">
      <!-- LAYER 1: ONE-CLICK PROFILES + CLEAN CATEGORY MENU -->
      <div id="layer-menu">
        <div class="preset-strip">
          <div class="hint" style="margin-bottom:2px;">Tek Tıkla Hızlı Dinleme Profili</div>
          <div class="preset-grid">
            <button class="preset-card" data-profile="saf">
              <div class="preset-name">Saf Dinleme</div>
              <div class="preset-desc">Fonsuz · 1.0x · Parşömen</div>
            </button>
            <button class="preset-card" data-profile="tefekkur">
              <div class="preset-name">Derin Tefekkür</div>
              <div class="preset-desc">Uşşak Ney · 0.9x · 5 sn ara</div>
            </button>
            <button class="preset-card" data-profile="gece">
              <div class="preset-name">Gece Huzuru</div>
              <div class="preset-desc">Gece tema · Hicaz · 30 dk uyku</div>
            </button>
            <button class="preset-card" data-profile="hizli">
              <div class="preset-name">Hızlı Tekrar</div>
              <div class="preset-desc">Sadece Doğrudan · 1.25x</div>
            </button>
          </div>
        </div>

        <div class="cat-menu">
          <div class="cat-row" data-panel="ses">
            <div class="cat-left">
              <div class="cat-icon"><svg viewBox="0 0 24 24"><line x1="4" y1="21" x2="4" y2="14"/><line x1="4" y1="10" x2="4" y2="3"/><line x1="12" y1="21" x2="12" y2="12"/><line x1="12" y1="8" x2="12" y2="3"/><line x1="20" y1="21" x2="20" y2="16"/><line x1="20" y1="12" x2="20" y2="3"/><line x1="1" y1="14" x2="7" y2="14"/><line x1="9" y1="8" x2="15" y2="8"/><line x1="17" y1="16" x2="23" y2="16"/></svg></div>
              <div style="min-width:0;">
                <div class="cat-row-title">Ses, Mikser ve Oynatma</div>
                <div class="cat-row-sub" id="sum-ses">Risale 10 · Fon 2.5 · 1.0x hız</div>
              </div>
            </div>
            <span class="cat-arrow">›</span>
          </div>

          <div class="cat-row" data-panel="fon">
            <div class="cat-left">
              <div class="cat-icon"><svg viewBox="0 0 24 24"><path d="M9 18V5l12-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/></svg></div>
              <div style="min-width:0;">
                <div class="cat-row-title">Fon Müziği Stüdyosu (26 Fon)</div>
                <div class="cat-row-sub" id="sum-fon">Kapalı · 26 Ney/Tabiat/Odak Fonu & MP3</div>
              </div>
            </div>
            <span class="cat-arrow">›</span>
          </div>

          <div class="cat-row" data-panel="kuran">
            <div class="cat-left">
              <div class="cat-icon"><svg viewBox="0 0 24 24"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg></div>
              <div style="min-width:0;">
                <div class="cat-row-title">Kur'an Ayeti ve Besmele Koruması</div>
                <div class="cat-row-sub" id="sum-kuran">2.5 sn önce fonu kapat · Açılış 15 sn</div>
              </div>
            </div>
            <span class="cat-arrow">›</span>
          </div>

          <div class="cat-row" data-panel="ai">
            <div class="cat-left">
              <div class="cat-icon"><svg viewBox="0 0 24 24"><path d="M12 2L14.8 9.2L22 12L14.8 14.8L12 22L9.2 14.8L2 12L9.2 9.2L12 2Z"/></svg></div>
              <div style="min-width:0;">
                <div class="cat-row-title">Yapay Zeka ve API Anahtarları</div>
                <div class="cat-row-sub" id="sum-ai">Yerel Semantik AI · Gemini / OpenAI / Groq / DeepSeek</div>
              </div>
            </div>
            <span class="cat-arrow">›</span>
          </div>

          <div class="cat-row" data-panel="kayitlar">
            <div class="cat-left">
              <div class="cat-icon"><svg viewBox="0 0 24 24"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg></div>
              <div style="min-width:0;">
                <div class="cat-row-title">Dinleme Kayıtlarım ve Yer İmleri</div>
                <div class="cat-row-sub" id="sum-kayitlar">Yer imleri, son dinlenenler ve günlük süre</div>
              </div>
            </div>
            <span class="cat-arrow">›</span>
          </div>

          <div class="cat-row" data-panel="kesit">
            <div class="cat-left">
              <div class="cat-icon"><svg viewBox="0 0 24 24"><circle cx="6" cy="6" r="3"/><circle cx="6" cy="18" r="3"/><line x1="20" y1="4" x2="8.12" y2="15.88"/><line x1="14.47" y1="14.48" x2="20" y2="20"/><line x1="8.12" y1="8.12" x2="12" y2="12"/></svg></div>
              <div style="min-width:0;">
                <div class="cat-row-title">Fihrist, Künye ve Kesit Kesici</div>
                <div class="cat-row-sub" id="sum-kesit">Hikmetli sıra · Künye açık · Hassas kesit</div>
              </div>
            </div>
            <span class="cat-arrow">›</span>
          </div>

          <div class="cat-row" data-panel="oto">
            <div class="cat-left">
              <div class="cat-icon"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg></div>
              <div style="min-width:0;">
                <div class="cat-row-title">Zamanlayıcı ve Kısayol Tuşları</div>
                <div class="cat-row-sub" id="sum-oto">Otomatik başlatma ve tek tuşla çalma</div>
              </div>
            </div>
            <span class="cat-arrow">›</span>
          </div>

          <div class="cat-row" data-panel="sistem">
            <div class="cat-left">
              <div class="cat-icon"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 1 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 1 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 1 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 1 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg></div>
              <div style="min-width:0;">
                <div class="cat-row-title">Görünüm ve Yedekleme</div>
                <div class="cat-row-sub" id="sum-sistem">Parşömen tema · Yazı boyutu · JSON Yedek</div>
              </div>
            </div>
            <span class="cat-arrow">›</span>
          </div>
        </div>
      </div>

      <!-- LAYER 2: PANEL 1 — SES, MİKSER & OYNATMA -->
      <div id="panel-ses" class="hidden">
        <div class="set-card">
          <h3>Bağımsız Ses Mikseri (0–10 Birim)</h3>
          <div class="mixer-row">
            <div class="mixer-lbl"><span>Risale Okuma Sesi</span><span class="mixer-val" id="risaleUnitLabel">10 / 10</span></div>
            <input id="risaleVolRange" type="range" min="0" max="10" step="0.5" value="10" />
          </div>
          <div class="mixer-row">
            <div class="mixer-lbl"><span>Fon Müziği Sesi</span><span class="mixer-val" id="fonUnitLabel">2.5 / 10</span></div>
            <input id="fonVolRange" type="range" min="0" max="10" step="0.5" value="2.5" />
          </div>
          <div class="balance-track" title="Risale / Fon Ses Dengesi">
            <div class="balance-risale" id="balanceRisale" style="width:80%;"></div>
            <div class="balance-fon" id="balanceFon" style="width:20%;"></div>
          </div>
          <div class="ratio-row">
            <button class="btn-ghost" data-mix="10,1" style="padding:4px 9px; font-size:0.74rem;">10:1 Fısıltı</button>
            <button class="btn-ghost" data-mix="10,3" style="padding:4px 9px; font-size:0.74rem;">10:3 Dengeli</button>
            <button class="btn-ghost" data-mix="8,5" style="padding:4px 9px; font-size:0.74rem;">8:5 Derin Fon</button>
            <button class="btn-ghost" data-mix="1,10" style="padding:4px 9px; font-size:0.74rem;">1:10 Fon Önde</button>
          </div>
        </div>

        <div class="set-card">
          <h3>Hız, Tekrar ve Akış Konforu</h3>
          <div class="set-col">
            <span>Oynatma Hızı</span>
            <div class="seg-ctrl" id="speedSeg">
              <button class="seg-btn" data-spd="0.75">0.75x</button>
              <button class="seg-btn" data-spd="0.9">0.9x</button>
              <button class="seg-btn active" data-spd="1">1.0x</button>
              <button class="seg-btn" data-spd="1.15">1.15x</button>
              <button class="seg-btn" data-spd="1.25">1.25x</button>
              <button class="seg-btn" data-spd="1.5">1.5x</button>
              <button class="seg-btn" data-spd="2">2.0x</button>
            </div>
          </div>
          <div class="set-col">
            <span>Tekrarlama Modu</span>
            <div class="seg-ctrl" id="repeatSeg">
              <button class="seg-btn active" data-rep="off">Kapalı</button>
              <button class="seg-btn" data-rep="clip">Aktif Kesit</button>
              <button class="seg-btn" data-rep="list">Tüm Liste</button>
            </div>
          </div>
          <div class="set-col">
            <span>Kesitler Arası Tefekkür Sükûtu</span>
            <div class="seg-ctrl" id="gapSeg">
              <button class="seg-btn active" data-gap="0">0 sn</button>
              <button class="seg-btn" data-gap="3">3 sn</button>
              <button class="seg-btn" data-gap="5">5 sn</button>
              <button class="seg-btn" data-gap="8">8 sn</button>
              <button class="seg-btn" data-gap="12">12 sn</button>
            </div>
          </div>
          <div class="set-col">
            <span>İleri / Geri Sarma Adımı</span>
            <div class="seg-ctrl" id="skipSeg">
              <button class="seg-btn" data-skip="5">5 sn</button>
              <button class="seg-btn" data-skip="10">10 sn</button>
              <button class="seg-btn active" data-skip="15">15 sn</button>
              <button class="seg-btn" data-skip="30">30 sn</button>
            </div>
          </div>
          <div class="set-row">
            <span>Okuyucu Filtresi (Tüm Bölümler)</span>
            <select id="readerFilter"><option value="">Tümü (12 Okuyucu)</option></select>
          </div>
          <div class="set-row">
            <label for="crossfadeChk">Kesitler Arası Yumuşak Geçiş</label>
            <input class="switch" id="crossfadeChk" type="checkbox" checked />
          </div>
          <div class="set-row">
            <label for="autoResumeChk">Kaldığı Yerden Devam Et</label>
            <input class="switch" id="autoResumeChk" type="checkbox" checked />
          </div>
          <div class="set-row">
            <label for="autoNextChk">Sıralı Otomatik Geçiş</label>
            <input class="switch" id="autoNextChk" type="checkbox" checked />
          </div>
          <div class="set-row">
            <span>Uyku Zamanlayıcısı (15 sn Yumuşak Kapanış)</span>
            <select id="sleepSelect">
              <option value="0">Kapalı</option>
              <option value="15">15 Dakika</option>
              <option value="30">30 Dakika</option>
              <option value="45">45 Dakika</option>
              <option value="60">60 Dakika</option>
              <option value="-1">Bu Parça Bitince Dur</option>
            </select>
          </div>
        </div>
      </div>

      <!-- LAYER 2: PANEL 2 — FON MÜZİĞİ STÜDYOSU (26 DAHİLİ FON + MP3) -->
      <div id="panel-fon" class="hidden">
        <div class="set-card">
          <h3>Arka Plan Fon Motoru (26 Dahili Fon)</h3>
          <div class="set-row">
            <label for="fonEnabledChk">Dinlerken Fon Müziği Eşlik Etsin</label>
            <input class="switch" id="fonEnabledChk" type="checkbox" />
          </div>
          <div class="set-col">
            <span>Fon Çalma Modu</span>
            <div class="seg-ctrl" id="fonModeSeg">
              <button class="seg-btn active" data-fmode="synth">Tek Dahili Fon</button>
              <button class="seg-btn" data-fmode="cycle">Makam Döngüsü</button>
              <button class="seg-btn" data-fmode="playlist">MP3 Listem</button>
            </div>
          </div>
          <div class="set-col">
            <span>Dahili Ney, Saz, Tabiat & Odak Fonu</span>
            <select id="fonPresetSelect" class="field-inp">
              <optgroup label="Makamlı Ney ve Tasavvuf Sazları (14)">
                <option value="ussak">Uşşak Ney Taksimi & Dem</option>
                <option value="hicaz">Hicaz Tefekkür (Ney & Ud)</option>
                <option value="rast">Rast Seher Huzuru</option>
                <option value="saba">Sabâ Kalp Sükûneti</option>
                <option value="nihavend">Nihâvend Akşam Sükûnu (Ney & Tanbur)</option>
                <option value="segah">Segâh Huşû ve Vakar (Ney & Kanun)</option>
                <option value="huseyni">Hüseynî Seher & Sıcak Ney</option>
                <option value="suzinak">Sûzinâk Gece Tefekkürü (Ney & Ud)</option>
                <option value="acemasiran">Acemaşîran Şükür ve Ferahlık</option>
                <option value="isfahan">İsfahan Derin Tevekkül (Yumuşak Ney)</option>
                <option value="kurdili">Kürdîlihicazkâr Gece (Ney & Ud)</option>
                <option value="beyati">Beyâtî Şefkat ve İncelik (Ney)</option>
                <option value="hicazkar">Hicazkâr Derin Huzur (Ney & Tanbur)</option>
                <option value="mahur">Mahur Sabah Ferahlığı</option>
              </optgroup>
              <optgroup label="Tabiat ve Rahmet Sesleri (7)">
                <option value="yagmur">Rahmet Yağmuru & Ney</option>
                <option value="sade_yagmur">Sadece Rahmet Yağmuru (Hafif)</option>
                <option value="gece_yagmuru">Gece Yağmuru & Pencere Akustiği</option>
                <option value="dere_suyu">Şırıldayan Dere & Seher Esintisi</option>
                <option value="orman_ruzgari">Çam Ormanı Rüzgârı & Derin Sükût</option>
                <option value="somine">Kış Akşamı Şömine Çıtırtısı</option>
                <option value="seher_kuslari">Bahar Seheri Kuş Sesleri & Esinti</option>
              </optgroup>
              <optgroup label="Derin Odak ve Medrese Akustiği (5)">
                <option value="kainat">Kâinat Tefekkürü (Semâvî Dem)</option>
                <option value="medrese_odak">Medrese Derin Odak (Sıcak Akustik)</option>
                <option value="seher_demi">Seher Demi (Melodisiz Derin Ney Demi)</option>
                <option value="rezonans_432">432 Hz Derin Sükûnet Rezonansı</option>
                <option value="kutuphane">Kütüphane Derin Sessizlik (Yumuşak)</option>
              </optgroup>
            </select>
          </div>
          <div style="display:flex; gap:8px; margin-top:8px;">
            <button class="btn-ghost" id="previewFonBtn" style="flex:1;">Fonu Test Et</button>
            <button class="btn-ghost" id="bindFonPlaylistBtn" style="flex:1;">Aktif Listeye Bağla</button>
          </div>
        </div>

        <div class="set-card">
          <h3>Kendi MP3 / WAV Fonlarını Yükle (Kalıcı)</h3>
          <div class="set-stack">
            <label class="btn-accent" style="text-align:center; cursor:pointer;">
              Cihazdan MP3 / WAV Seç (Çoklu)
              <input id="fonFileInput" type="file" accept="audio/*" multiple class="hidden" />
            </label>
            <input id="fonUrlTitle" type="text" placeholder="URL Fon Adı" />
            <input id="fonUrlInput" type="url" placeholder="https://.../fon.mp3" />
            <button class="btn-ghost" id="addFonUrlBtn">URL Fon Ekle</button>
          </div>
        </div>
        <div class="set-card">
          <h3>Fon Çalma Listesi ve Tekrar Sayıları</h3>
          <div id="fonPlaylistBox"></div>
        </div>
      </div>

      <!-- LAYER 2: PANEL 3 — KUR'AN & BESMELE KORUMASI -->
      <div id="panel-kuran" class="hidden">
        <div class="set-card">
          <h3>Otomatik Fon Susturma</h3>
          <div class="set-row">
            <label for="duckEnabledChk">Kur'an Ayetlerinde Fonu Kapat</label>
            <input class="switch" id="duckEnabledChk" type="checkbox" checked />
          </div>
          <div class="set-row">
            <label for="duckBesmeleChk">Bismillahirrahmanirrahim & Hamdele'de Kapat</label>
            <input class="switch" id="duckBesmeleChk" type="checkbox" checked />
          </div>
          <div class="set-row">
            <span>Açılış Besmele/Ayet Koruması</span>
            <select id="openingMuteSelect">
              <option value="0">Kapalı</option>
              <option value="10">İlk 10 sn</option>
              <option value="15" selected>İlk 15 sn</option>
              <option value="20">İlk 20 sn</option>
              <option value="28">İlk 28 sn</option>
            </select>
          </div>
          <div class="set-row">
            <span>Ayetten Önce Kapanış (Pre-Roll)</span>
            <select id="preRollSelect">
              <option value="1.5">1.5 sn önce</option>
              <option value="2.0">2.0 sn önce</option>
              <option value="2.5" selected>2.5 sn önce</option>
              <option value="3.0">3.0 sn önce</option>
              <option value="4.0">4.0 sn önce</option>
            </select>
          </div>
          <div class="set-row">
            <span>Ayet Sonrası Geri Açılış</span>
            <select id="postRollSelect">
              <option value="0.5">0.5 sn sonra</option>
              <option value="1.2" selected>1.2 sn sonra</option>
              <option value="2.0">2.0 sn sonra</option>
            </select>
          </div>
          <button class="btn-ghost" id="manualMuteToggleBtn" style="width:100%; margin-top:4px;">Anlık Manuel Fon Sustur (M)</button>
        </div>
        <div class="set-card">
          <h3>Canlı Ayet / Besmele İşaretleme</h3>
          <div style="display:flex; gap:8px; margin-bottom:10px;">
            <button class="btn-accent" id="markQuranStartBtn" style="flex:1;">Ayet Başladı</button>
            <button class="btn-ghost" id="markQuranEndBtn" style="flex:1;">Bitti (Kaydet)</button>
          </div>
          <div id="activeQuranRangesBox"></div>
        </div>
      </div>

      <!-- LAYER 2: PANEL AI — YAPAY ZEKA & API ANAHTARLARI -->
      <div id="panel-ai" class="hidden">
        <div class="set-card">
          <h3>Yapay Zeka Sağlayıcısı ve API Anahtarı</h3>
          <div class="set-stack">
            <div>
              <label class="field-lbl">Sağlayıcı Seçin</label>
              <select id="aiProviderSelect">
                <option value="local">Yerel Semantik AI (Ücretsiz & Çevrimdışı)</option>
                <option value="gemini">Google Gemini API (gemini-2.5-flash)</option>
                <option value="openai">OpenAI API (gpt-4o-mini / gpt-4o)</option>
                <option value="groq">Groq API (Llama 3.3 70B · Çok Hızlı)</option>
                <option value="deepseek">DeepSeek API (deepseek-chat)</option>
                <option value="openrouter">OpenRouter API (Çoklu Model)</option>
                <option value="custom">Özel Uç Nokta (Ollama / LM Studio / Diğer)</option>
              </select>
            </div>
            <div id="aiExternalFields" class="set-stack hidden">
              <div>
                <label class="field-lbl">API Anahtarı (yalnızca bu cihazda saklanır)</label>
                <div style="display:flex; gap:6px;">
                  <input id="aiApiKey" type="password" placeholder="API anahtarınızı yapıştırın..." style="flex:1;" />
                  <button class="btn-ghost" id="toggleAiKeyVisBtn" type="button" style="padding:6px 10px;">Göster</button>
                </div>
              </div>
              <div>
                <label class="field-lbl">Model Adı</label>
                <input id="aiModelName" type="text" placeholder="örn: gemini-2.5-flash veya gpt-4o-mini" />
              </div>
              <div id="aiCustomUrlWrap">
                <label class="field-lbl">API Uç Noktası (URL)</label>
                <input id="aiApiUrl" type="url" placeholder="https://api.openai.com/v1/chat/completions" />
              </div>
              <div style="display:flex; gap:8px; margin-top:4px;">
                <button class="btn-accent" id="saveAiCfgBtn" style="flex:1;">Kaydet</button>
                <button class="btn-ghost" id="testAiCfgBtn" style="flex:1;">Bağlantıyı Test Et</button>
              </div>
              <div id="aiTestStatus" class="hint"></div>
            </div>
            <p class="hint" id="aiProviderHint">Yerel Semantik AI hiçbir API anahtarı gerektirmeden 2.373 Külliyat mikro-kesitini anında tarar, Doğrudan/Dolaylı olarak ayırır ve Hikmetli Sıra ile sunar.</p>
          </div>
        </div>

        <div class="set-card">
          <h3>AI Liste Tercihleri</h3>
          <div class="set-row">
            <label for="aiAutoFonChk">AI listesi oluşturunca önerilen fonu uygula</label>
            <input class="switch" id="aiAutoFonChk" type="checkbox" />
          </div>
          <p class="hint">Harici bir API anahtarı (Gemini, OpenAI, Groq, DeepSeek vb.) girildiğinde yapay zeka yalnızca doğrulanmış Külliyat kesit ID'lerinden seçim yapar, her kesite özel Türkçe künye yazar ve en uygun makam fonunu önerir.</p>
        </div>
      </div>

      <!-- LAYER 2: PANEL 4 — DİNLEME KAYITLARIM & YER İMLERİ -->
      <div id="panel-kayitlar" class="hidden">
        <div class="set-card">
          <h3>Dinleme Özeti</h3>
          <div class="stat-grid">
            <div class="stat-box">
              <div class="stat-num" id="statTodayMin">0 dk</div>
              <div class="stat-lbl">Bugün Dinlenen</div>
            </div>
            <div class="stat-box">
              <div class="stat-num" id="statTotalMin">0 dk</div>
              <div class="stat-lbl">Toplam Dinleme</div>
            </div>
          </div>
        </div>
        <div class="set-card">
          <h3>Yer İmlerim ve Tefekkür Notları</h3>
          <div style="display:flex; gap:6px; margin-bottom:10px;">
            <input id="settingsBookmarkNote" type="text" placeholder="Şu anki saniyeye not ekle..." style="flex:1; padding:7px 10px; border-radius:8px; border:1px solid var(--border-strong); background:var(--bg); color:var(--text); font-size:0.8rem;" />
            <button class="btn-accent" id="settingsAddBookmarkBtn" style="padding:7px 11px;">Kaydet</button>
          </div>
          <div id="settingsBookmarksBox"></div>
        </div>
        <div class="set-card">
          <h3>Son Dinlenen Dersler</h3>
          <div id="settingsHistoryBox"></div>
        </div>
      </div>

      <!-- LAYER 2: PANEL 5 — FİHRİST, KÜNYE & KESİT KESİCİ -->
      <div id="panel-kesit" class="hidden">
        <div class="set-card">
          <h3>Konu Fihristi ve Künye Tercihleri</h3>
          <div class="set-col">
            <span>Varsayılan Konu Sıralaması</span>
            <div class="seg-ctrl" id="defaultRelSeg">
              <button class="seg-btn active" data-defrel="all">Hikmetli Sıra</button>
              <button class="seg-btn" data-defrel="direct">Sadece Doğrudan</button>
            </div>
          </div>
          <div class="set-row">
            <label for="showKunyeChk">Konu içinde sıralama künyesini göster</label>
            <input class="switch" id="showKunyeChk" type="checkbox" checked />
          </div>
          <div class="set-row">
            <label for="sliceModeChk">Sadece İlgili Saniye Aralığını Oynat</label>
            <input class="switch" id="sliceModeChk" type="checkbox" checked />
          </div>
        </div>
        <div class="set-card">
          <h3>Aktif Kayıttan Kendi Kesitini Kes</h3>
          <div style="display:flex; gap:8px; margin-bottom:8px;">
            <button class="btn-ghost" id="clipSetStartBtn" style="flex:1;">Başlangıç: <span id="clipStartLbl">00:00</span></button>
            <button class="btn-ghost" id="clipSetEndBtn" style="flex:1;">Bitiş: <span id="clipEndLbl">--:--</span></button>
          </div>
          <div class="set-stack">
            <input id="clipTitleInput" type="text" placeholder="Kesit Başlığı" />
            <button class="btn-accent" id="saveCustomClipBtn">Kesiti Kaydet</button>
          </div>
        </div>
        <div class="set-card">
          <h3>Aktif Dersin Saniye-Saniye Bölümleri</h3>
          <div id="activeTrackSegmentsBox"></div>
        </div>
      </div>

      <!-- LAYER 2: PANEL 6 — ZAMANLAYICI & KISAYOL TUŞLARI -->
      <div id="panel-oto" class="hidden">
        <div class="set-card">
          <h3>Tek Tuşla Başlatma (Özel Kısayol)</h3>
          <div class="set-stack">
            <select id="hkActionSelect">
              <option value="play_list">Seçili Listeyi Başlat</option>
              <option value="toggle_radio">Radyo Akışını Başlat / Durdur</option>
              <option value="toggle_play">Oynat / Duraklat</option>
              <option value="toggle_fon">Fon Müziğini Aç / Kapat</option>
              <option value="toggle_mute">Manuel Ayet Sustur</option>
            </select>
            <select id="hkTargetPlaylist"></select>
            <button class="btn-accent" id="captureKeyBtn">Tuş Ata</button>
          </div>
          <div id="hotkeysListBox" style="margin-top:10px;"></div>
        </div>
        <div class="set-card">
          <h3>Yerleşik Dinleme Kısayolları</h3>
          <div class="hint" style="display:flex; flex-direction:column; gap:5px;">
            <div><span class="kbd">Boşluk</span> Oynat / Duraklat · <span class="kbd">←</span> / <span class="kbd">→</span> Geri / İleri Sar</div>
            <div><span class="kbd">Shift+←/→</span> Önceki / Sonraki Kesit · <span class="kbd">M</span> Fonu Sustur</div>
            <div><span class="kbd">B</span> Saniyeye Yer İmi Kaydet · <span class="kbd">F</span> Tefekkür Sahnesi</div>
          </div>
        </div>
        <div class="set-card">
          <h3>Zamanlanmış Tekil Otomatik Başlatma</h3>
          <div class="set-stack">
            <div class="set-row">
              <span>Saat</span>
              <input id="schedTime" type="time" value="06:30" />
            </div>
            <select id="schedPlaylist"></select>
            <div class="days-row" id="schedDaysRow"></div>
            <div class="set-row">
              <label for="schedFonChk">Fon müziğini otomatik aç</label>
              <input class="switch" id="schedFonChk" type="checkbox" checked />
            </div>
            <button class="btn-accent" id="addSchedBtn">Zamanlayıcı Ekle</button>
          </div>
          <div id="schedulesList" style="margin-top:10px;"></div>
        </div>
        <div class="set-card">
          <div class="set-row">
            <label for="autoStartOpenChk">Açılışta son listeyi otomatik hazırla</label>
            <input class="switch" id="autoStartOpenChk" type="checkbox" />
          </div>
        </div>
      </div>

      <!-- LAYER 2: PANEL 7 — GÖRÜNÜM, AI & YEDEKLEME -->
      <div id="panel-sistem" class="hidden">
        <div class="set-card">
          <h3>Görünüm ve Okuma Rahatlığı</h3>
          <div class="theme-grid" id="themeGrid" style="margin-bottom:12px;">
            <div class="theme-card" data-theme-pick="light">
              <div class="theme-swatch" style="background:linear-gradient(135deg,#f9f6f0 60%,#8f621f 100%);"></div>
              Parşömen
            </div>
            <div class="theme-card" data-theme-pick="sepia">
              <div class="theme-swatch" style="background:linear-gradient(135deg,#f2e8d5 60%,#8a5617 100%);"></div>
              Sepya
            </div>
            <div class="theme-card" data-theme-pick="dark">
              <div class="theme-swatch" style="background:linear-gradient(135deg,#12100e 60%,#cfa04e 100%);"></div>
              Gece
            </div>
          </div>
          <div class="set-col">
            <span>Tefekkür Sahnesi Yazı Boyutu</span>
            <div class="seg-ctrl" id="fontSizeSeg">
              <button class="seg-btn" data-fsize="sm">Küçük</button>
              <button class="seg-btn active" data-fsize="md">Orta</button>
              <button class="seg-btn" data-fsize="lg">Büyük</button>
            </div>
          </div>
        </div>
        <div class="set-card">
          <h3>Android APK, PWA ve Tek Dosya Uygulama</h3>
          <div class="set-stack" style="margin-bottom:6px;">
            <a class="btn-accent" id="downloadApkBtn" href="https://github.com/plim6803-star/RUT-N-UYGULAMASI/releases/download/v11.0-android/Risale-Dinle-v11.apk" download="Risale-Dinle-v11.apk" target="_blank" rel="noopener" style="text-align:center; text-decoration:none; display:block;">Android Uygulamasını İndir (.APK)</a>
            <div style="display:flex; gap:8px; flex-wrap:wrap;">
              <button class="btn-ghost" id="installPwaBtn" style="flex:1;">PWA Olarak Kur</button>
              <button class="btn-ghost" id="downloadStandaloneAppBtn" style="flex:1;">Tek Dosya İndir (.html)</button>
            </div>
          </div>
          <p class="hint">İmzalı <code>Risale-Dinle-v11.apk</code> dosyasını Android telefon veya tabletinize indirip doğrudan kurabilirsiniz.</p>
        </div>
        <div class="set-card">
          <h3>Özel Risale MP3, Yedekleme & Sıfırlama</h3>
          <div class="set-stack" style="margin-bottom:10px;">
            <input id="customTitle" type="text" placeholder="Özel Ders Başlığı" />
            <input id="customUrl" type="url" placeholder="https://.../risale.mp3" />
            <button class="btn-ghost" id="addCustomBtn">Özel Ders Ekle</button>
          </div>
          <div style="display:flex; gap:8px; margin-bottom:8px;">
            <button class="btn-ghost" id="exportBackupBtn" style="flex:1;">Yedekle (JSON)</button>
            <label class="btn-ghost" style="flex:1; text-align:center; cursor:pointer;">
              Yedekten Yükle
              <input id="importBackupInput" type="file" accept=".json" class="hidden" />
            </label>
          </div>
          <button class="btn-ghost" id="resetSettingsBtn" style="width:100%; opacity:0.78;">Ayarları Varsayılana Sıfırla</button>
        </div>
      </div>
    </div>
  </aside>

  <div class="toast" id="toast"></div>

  <script>
    const EMBEDDED_CATALOG = __CATALOG_JSON__;
    const EMBEDDED_SEGMENTS = __SEGMENTS_JSON__;
    const RAW_PLAYLISTS = __PLAYLISTS_JSON__;

    function hydrateTopicClip(c) {
      if (Array.isArray(c)) {
        const s = EMBEDDED_SEGMENTS[c[0]];
        return {
          segIdx: c[0],
          trackId: s.trackId,
          clipTitle: s.title,
          startSec: s.ss,
          endSec: s.es,
          startRatio: s.sr,
          endRatio: s.er,
          excerpt: s.excerpt,
          section: s.section,
          rel: c[1] === 'd' ? 'direct' : 'indirect',
          kunye: c[2] || '',
        };
      }
      return c;
    }

    const EMBEDDED_PLAYLISTS = RAW_PLAYLISTS.map(p => ({ ...p, clips: p.clips.map(hydrateTopicClip) }));

    function findTopicIdByName(substr) {
      const m = EMBEDDED_PLAYLISTS.find(p => p.title.toLowerCase().includes(substr));
      return m ? 'topic:' + m.id : ('topic:' + (EMBEDDED_PLAYLISTS[0]?.id || ''));
    }

    function buildDefaultRadioBlocks() {
      return [
        { id: 'rb-sabah', title: 'Seher ve Sabah Tefekkürü', start: '06:00', end: '09:30', days: [0,1,2,3,4,5,6], source: findTopicIdByName('varlığı'), filter: 'direct', fon: 'rast', fonVol: 2.5, speed: 1, reader: '', enabled: true },
        { id: 'rb-ogle', title: 'Gündüz Namaz ve İbadet Akışı', start: '09:30', end: '14:30', days: [0,1,2,3,4,5,6], source: findTopicIdByName('namaz'), filter: 'all', fon: 'ussak', fonVol: 2.0, speed: 1, reader: '', enabled: true },
        { id: 'rb-aksam', title: 'İkindi ve Akşam Kalp Huzuru', start: '14:30', end: '20:00', days: [0,1,2,3,4,5,6], source: findTopicIdByName('dua'), filter: 'all', fon: 'yagmur', fonVol: 2.5, speed: 1, reader: '', enabled: true },
        { id: 'rb-gece', title: 'Gece Haşir ve Ebediyet Tefekkürü', start: '20:00', end: '06:00', days: [0,1,2,3,4,5,6], source: findTopicIdByName('ahirete'), filter: 'all', fon: 'hicaz', fonVol: 2.0, speed: 0.9, reader: '', enabled: true },
      ];
    }

    const STORAGE_KEY = 'risale_dinle_v10';
    function loadSaved() {
      try {
        return JSON.parse(localStorage.getItem(STORAGE_KEY) || localStorage.getItem('risale_dinle_v9') || '{}');
      } catch (e) { return {}; }
    }
    const saved = loadSaved();

    function todayKey() {
      const d = new Date();
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    }

    const state = {
      tracks: [...EMBEDDED_CATALOG.tracks, ...(saved.customTracks || [])],
      segments: EMBEDDED_SEGMENTS,
      playlists: EMBEDDED_PLAYLISTS,
      userPlaylists: saved.userPlaylists || [],
      queue: saved.queue || [],
      queueIndex: saved.queueIndex || 0,
      positions: saved.positions || {},
      playlistProgress: saved.playlistProgress || {},
      bookmarks: saved.bookmarks || [],
      history: saved.history || [],
      listenStats: saved.listenStats || { todayKey: todayKey(), todaySec: 0, totalSec: 0 },
      customTracks: saved.customTracks || [],
      customQuranMarks: saved.customQuranMarks || {},
      playlistFonMap: saved.playlistFonMap || {},
      fonPlaylist: saved.fonPlaylist || [],
      fonIndex: 0,
      fonRepeatCounter: 0,
      fonEnabled: !!saved.fonEnabled,
      fonPreviewing: false,
      fonMode: saved.fonMode || 'synth',
      fonPreset: saved.fonPreset || 'ussak',
      risaleUnit: typeof saved.risaleUnit === 'number' ? saved.risaleUnit : 10,
      fonUnit: typeof saved.fonUnit === 'number' ? saved.fonUnit : 2.5,
      duckEnabled: saved.duckEnabled !== false,
      duckBesmele: saved.duckBesmele !== false,
      openingMuteSec: typeof saved.openingMuteSec === 'number' ? saved.openingMuteSec : 15,
      preRollSec: typeof saved.preRollSec === 'number' ? saved.preRollSec : 2.5,
      postRollSec: typeof saved.postRollSec === 'number' ? saved.postRollSec : 1.2,
      manualMute: false,
      sliceMode: saved.sliceMode !== false,
      crossfade: saved.crossfade !== false,
      autoResume: saved.autoResume !== false,
      autoNext: saved.autoNext !== false,
      autoStartOpen: !!saved.autoStartOpen,
      repeatMode: saved.repeatMode || 'off',
      shuffle: !!saved.shuffle,
      silenceGapSec: typeof saved.silenceGapSec === 'number' ? saved.silenceGapSec : 0,
      skipStepSec: typeof saved.skipStepSec === 'number' ? saved.skipStepSec : 15,
      defaultRelFilter: saved.defaultRelFilter || 'all',
      showKunye: saved.showKunye !== false,
      focusFontSize: saved.focusFontSize || 'md',
      theme: saved.theme || 'light',
      speed: saved.speed || 1,
      hotkeys: saved.hotkeys || [],
      schedules: saved.schedules || [],
      aiCfg: Object.assign({ provider: 'local', url: '', key: '', model: '', autoFon: false }, saved.aiCfg || {}),
      radio: saved.radio || {
        mode: 'scheduled',
        stopEnabled: false,
        stopTime: '23:30',
        blocks: buildDefaultRadioBlocks(),
      },
      radioRunning: false,
      radioActiveBlockId: null,
      radioEditingId: null,
      reader: '',
      view: 'topics',
      activePlaylistId: null,
      detailPlaylist: null,
      detailRelFilter: saved.defaultRelFilter || 'all',
      detailLimit: 60,
      currentTrackId: null,
      currentClip: null,
      isDucked: false,
      markStartSec: null,
      clipStartSec: 0,
      clipEndSec: null,
      lastAiResult: null,
      capturingKey: false,
      sleepTimerId: null,
      sleepFadeTimerId: null,
      sleepAtEnd: false,
      gapTimerId: null,
      cycleTimerId: null,
      lastFiredMinute: '',
      lastRadioStopMinute: '',
      fadingOut: false,
      advancingClip: false,
      focusTab: 'pasaj',
      lastTickSec: -1,
    };

    if (!state.radio.blocks || !state.radio.blocks.length) state.radio.blocks = buildDefaultRadioBlocks();
    if (state.listenStats.todayKey !== todayKey()) { state.listenStats.todayKey = todayKey(); state.listenStats.todaySec = 0; }

    const trackMap = new Map();
    function rebuildMap() { trackMap.clear(); state.tracks.forEach(t => trackMap.set(t.id, t)); }
    rebuildMap();

    function saveState() {
      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify({
          userPlaylists: state.userPlaylists,
          queue: state.queue.slice(0, 200),
          queueIndex: state.queueIndex,
          positions: state.positions,
          playlistProgress: state.playlistProgress,
          bookmarks: state.bookmarks.slice(0, 120),
          history: state.history.slice(0, 20),
          listenStats: state.listenStats,
          customTracks: state.customTracks,
          customQuranMarks: state.customQuranMarks,
          playlistFonMap: state.playlistFonMap,
          fonPlaylist: state.fonPlaylist.map(f => ({ id: f.id, title: f.title, url: f.isIdb ? '' : f.url, repeats: f.repeats, isIdb: !!f.isIdb })),
          fonEnabled: state.fonEnabled, fonMode: state.fonMode, fonPreset: state.fonPreset,
          risaleUnit: state.risaleUnit, fonUnit: state.fonUnit,
          duckEnabled: state.duckEnabled, duckBesmele: state.duckBesmele,
          openingMuteSec: state.openingMuteSec, preRollSec: state.preRollSec, postRollSec: state.postRollSec,
          sliceMode: state.sliceMode, crossfade: state.crossfade,
          autoResume: state.autoResume, autoNext: state.autoNext, autoStartOpen: state.autoStartOpen,
          repeatMode: state.repeatMode, shuffle: state.shuffle,
          silenceGapSec: state.silenceGapSec, skipStepSec: state.skipStepSec,
          defaultRelFilter: state.defaultRelFilter, showKunye: state.showKunye, focusFontSize: state.focusFontSize,
          theme: state.theme, speed: state.speed, hotkeys: state.hotkeys, schedules: state.schedules,
          aiCfg: state.aiCfg, radio: state.radio,
        }));
      } catch (e) {}
    }

    // IndexedDB for persistent local MP3 fon uploads
    function openFonDB() {
      return new Promise((res, rej) => {
        try {
          const req = indexedDB.open('RisaleDinleFonDB', 1);
          req.onupgradeneeded = () => { req.result.createObjectStore('files', { keyPath: 'id' }); };
          req.onsuccess = () => res(req.result);
          req.onerror = () => rej(req.error);
        } catch (e) { rej(e); }
      });
    }
    async function saveFonBlob(id, blob) { try { (await openFonDB()).transaction('files', 'readwrite').objectStore('files').put({ id, blob }); } catch (e) {} }
    async function deleteFonBlob(id) { try { (await openFonDB()).transaction('files', 'readwrite').objectStore('files').delete(id); } catch (e) {} }
    async function restoreIdbFonBlobs() {
      try {
        const db = await openFonDB();
        const store = db.transaction('files', 'readonly').objectStore('files');
        for (const f of state.fonPlaylist) {
          if (f.isIdb && !f.url) {
            const rec = await new Promise(r => { const q = store.get(f.id); q.onsuccess = () => r(q.result); q.onerror = () => r(null); });
            if (rec?.blob) f.url = URL.createObjectURL(rec.blob);
          }
        }
        state.fonPlaylist = state.fonPlaylist.filter(f => f.url);
      } catch (e) {}
    }

    function showToast(msg) {
      const el = document.getElementById('toast');
      el.textContent = msg; el.classList.add('show');
      clearTimeout(el._t); el._t = setTimeout(() => el.classList.remove('show'), 2200);
    }
    function fmtTime(sec) {
      if (!sec || !isFinite(sec)) return '00:00';
      const s = Math.max(0, Math.floor(sec));
      return `${String(Math.floor(s / 60)).padStart(2,'0')}:${String(s % 60).padStart(2,'0')}`;
    }
    function fmtMinText(sec) {
      const m = Math.floor((sec || 0) / 60);
      return m < 60 ? `${m} dk` : `${Math.floor(m / 60)} sa ${m % 60} dk`;
    }
    function escHtml(s) {
      return String(s || '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
    }

    // =========================================================================
    // 26 BUILT-IN MELODIC MAKAM NEY/SAZ, NATURE & DEEP FOCUS SOUNDSCAPES
    // =========================================================================
    const fonAudio = document.getElementById('fonAudioEl');
    let audioCtx = null, synthMaster = null, synthSrcNode = null;
    const presetBufferCache = {};

    const FON_LABELS = {
      ussak: 'Uşşak Ney Taksimi & Dem',
      hicaz: 'Hicaz Tefekkür (Ney & Ud)',
      rast: 'Rast Seher Huzuru',
      saba: 'Sabâ Kalp Sükûneti',
      nihavend: 'Nihâvend Akşam (Ney & Tanbur)',
      segah: 'Segâh Huşû (Ney & Kanun)',
      huseyni: 'Hüseynî Seher & Sıcak Ney',
      suzinak: 'Sûzinâk Gece (Ney & Ud)',
      acemasiran: 'Acemaşîran Şükür & Ferahlık',
      isfahan: 'İsfahan Derin Tevekkül',
      kurdili: 'Kürdîlihicazkâr Gece (Ney & Ud)',
      beyati: 'Beyâtî Şefkat & Ney',
      hicazkar: 'Hicazkâr Derin Huzur (Ney & Tanbur)',
      mahur: 'Mahur Sabah Ferahlığı',
      yagmur: 'Rahmet Yağmuru & Ney',
      sade_yagmur: 'Sadece Rahmet Yağmuru',
      gece_yagmuru: 'Gece Yağmuru & Pencere',
      dere_suyu: 'Şırıldayan Dere & Esinti',
      orman_ruzgari: 'Çam Ormanı Rüzgârı',
      somine: 'Kış Akşamı Şömine Çıtırtısı',
      seher_kuslari: 'Bahar Seheri Kuş Sesleri',
      kainat: 'Kâinat Tefekkürü (Semâvî Dem)',
      medrese_odak: 'Medrese Derin Odak',
      seher_demi: 'Seher Demi (Melodisiz)',
      rezonans_432: '432 Hz Derin Sükûnet',
      kutuphane: 'Kütüphane Derin Sessizlik',
    };

    const MAKAM_PRESETS = {
      // 14 Makamlı Ney & Tasavvuf Sazları
      ussak:      { root: 196.00, scale: [1, 1.1, 1.2, 1.333, 1.5, 1.6, 1.8, 2.0], mel: [0,1,2,4,3,2,1,0,2,3,4,5,4,2,1,0], oud: 0.14, rain: 0, ney: 1.0, drone: 0.20, wind: 0, stream: 0 },
      hicaz:      { root: 174.61, scale: [1, 1.067, 1.25, 1.333, 1.5, 1.6, 1.875, 2.0], mel: [0,2,3,4,3,2,1,0,4,5,4,3,2,1,2,0], oud: 0.16, rain: 0, ney: 1.0, drone: 0.22, wind: 0, stream: 0 },
      rast:       { root: 164.81, scale: [1, 1.125, 1.25, 1.333, 1.5, 1.667, 1.875, 2.0], mel: [0,2,4,5,4,2,1,0,3,4,6,4,3,2,1,0], oud: 0, rain: 0, ney: 1.0, drone: 0.20, wind: 0, stream: 0 },
      saba:       { root: 185.00, scale: [1, 1.1, 1.2, 1.26, 1.5, 1.6, 1.78, 2.0], mel: [0,1,2,3,2,1,0,1,2,4,3,2,1,2,1,0], oud: 0.15, rain: 0, ney: 0.95, drone: 0.22, wind: 0, stream: 0 },
      nihavend:   { root: 164.81, scale: [1, 1.125, 1.2, 1.333, 1.5, 1.6, 1.8, 2.0], mel: [0,2,4,3,2,1,2,0,4,5,4,2,3,2,1,0], oud: 0.18, rain: 0, ney: 0.9, drone: 0.20, wind: 0, stream: 0 },
      segah:      { root: 207.65, scale: [1, 1.08, 1.25, 1.333, 1.5, 1.62, 1.8, 2.0], mel: [0,1,2,3,4,3,2,1,0,2,3,2,1,2,1,0], oud: 0.20, rain: 0, ney: 0.85, drone: 0.18, wind: 0, stream: 0 },
      huseyni:    { root: 220.00, scale: [1, 1.1, 1.2, 1.333, 1.5, 1.667, 1.8, 2.0], mel: [4,5,4,3,2,1,0,2,4,5,6,5,4,2,1,0], oud: 0.10, rain: 0, ney: 0.95, drone: 0.22, wind: 0, stream: 0 },
      suzinak:    { root: 174.61, scale: [1, 1.125, 1.25, 1.333, 1.5, 1.6, 1.875, 2.0], mel: [0,2,4,5,6,5,4,2,3,4,2,1,2,1,0,0], oud: 0.17, rain: 0, ney: 0.9, drone: 0.22, wind: 0, stream: 0 },
      acemasiran: { root: 174.61, scale: [1, 1.125, 1.25, 1.333, 1.5, 1.667, 1.875, 2.0], mel: [0,2,3,4,5,4,3,2,4,6,5,4,3,2,1,0], oud: 0.12, rain: 0, ney: 0.9, drone: 0.18, wind: 0, stream: 0 },
      isfahan:    { root: 196.00, scale: [1, 1.1, 1.25, 1.333, 1.5, 1.6, 1.8, 2.0], mel: [2,3,4,2,1,0,1,2,4,3,2,1,0,1,2,0], oud: 0.12, rain: 0, ney: 0.92, drone: 0.21, wind: 0, stream: 0 },
      kurdili:    { root: 164.81, scale: [1, 1.067, 1.2, 1.333, 1.5, 1.6, 1.8, 2.0], mel: [4,3,2,1,0,2,3,4,5,4,3,2,1,0,1,0], oud: 0.18, rain: 0, ney: 0.92, drone: 0.23, wind: 0, stream: 0 },
      beyati:     { root: 196.00, scale: [1, 1.1, 1.2, 1.333, 1.5, 1.6, 1.78, 2.0], mel: [3,4,3,2,1,2,3,2,1,0,2,3,4,2,1,0], oud: 0.08, rain: 0, ney: 1.0, drone: 0.19, wind: 0, stream: 0 },
      hicazkar:   { root: 155.56, scale: [1, 1.067, 1.25, 1.333, 1.5, 1.6, 1.875, 2.0], mel: [4,5,6,5,4,3,2,1,0,2,3,4,2,1,2,0], oud: 0.19, rain: 0, ney: 0.88, drone: 0.24, wind: 0, stream: 0 },
      mahur:      { root: 196.00, scale: [1, 1.125, 1.25, 1.333, 1.5, 1.667, 1.875, 2.0], mel: [0,2,4,5,7,5,4,2,3,4,5,4,2,1,2,0], oud: 0.11, rain: 0, ney: 0.90, drone: 0.18, wind: 0, stream: 0 },
      // 7 Tabiat & Rahmet Sesleri
      yagmur:        { root: 196.00, scale: [1, 1.125, 1.2, 1.333, 1.5, 1.6, 1.8, 2.0], mel: [4,3,2,1,0,2,3,4,2,1,0,1,2,3,1,0], oud: 0, rain: 0.06, ney: 0.80, drone: 0.14, wind: 0, stream: 0 },
      sade_yagmur:   { root: 146.83, scale: [1], mel: [0], oud: 0, rain: 0.15, ney: 0, drone: 0.03, wind: 0, stream: 0 },
      gece_yagmuru:  { root: 130.81, scale: [1], mel: [0], oud: 0, rain: 0.11, ney: 0, drone: 0.06, wind: 0.07, stream: 0 },
      dere_suyu:     { root: 164.81, scale: [1], mel: [0], oud: 0, rain: 0.04, ney: 0, drone: 0.03, wind: 0.03, stream: 0.16 },
      orman_ruzgari: { root: 138.59, scale: [1], mel: [0], oud: 0, rain: 0, ney: 0, drone: 0.05, wind: 0.18, stream: 0 },
      somine:        { root: 123.47, scale: [1], mel: [0], oud: 0, rain: 0, ney: 0, drone: 0.05, wind: 0.09, stream: 0, fire: 1 },
      seher_kuslari: { root: 196.00, scale: [1], mel: [0], oud: 0, rain: 0, ney: 0, drone: 0.04, wind: 0.07, stream: 0.03, birds: 1 },
      // 5 Derin Odak & Medrese Akustiği
      kainat:        { root: 146.83, scale: [1, 1.125, 1.2, 1.333, 1.5, 1.667, 1.8, 2.0], mel: [0,4,2,3,4,6,4,2,0,2,4,3,2,1,2,0], oud: 0.14, rain: 0, ney: 0.82, drone: 0.26, wind: 0, stream: 0 },
      medrese_odak:  { root: 130.81, scale: [1, 1.5], mel: [0,1], oud: 0, rain: 0.035, ney: 0, drone: 0.12, wind: 0.05, stream: 0 },
      seher_demi:    { root: 146.83, scale: [1, 1.5], mel: [0,0], oud: 0, rain: 0, ney: 0, drone: 0.34, wind: 0.02, stream: 0 },
      rezonans_432:  { root: 216.00, scale: [1, 1.5], mel: [0,0], oud: 0, rain: 0, ney: 0, drone: 0.30, wind: 0, stream: 0, res432: 1 },
      kutuphane:     { root: 130.81, scale: [1], mel: [0], oud: 0, rain: 0, ney: 0, drone: 0.02, wind: 0.11, stream: 0, pink: 1 },
    };
    const CYCLE_ORDER = ['ussak', 'rast', 'hicaz', 'nihavend', 'yagmur', 'saba', 'huseyni', 'kurdili', 'beyati', 'kainat', 'segah', 'isfahan', 'hicazkar', 'mahur'];

    function getOrBuildMakamBuffer(ctx, key) {
      if (presetBufferCache[key]) return presetBufferCache[key];
      const cfg = MAKAM_PRESETS[key] || MAKAM_PRESETS.ussak;
      const sr = 22050, dur = 36, n = sr * dur;
      const raw = new Float32Array(n);
      const phraseLen = dur / Math.max(1, cfg.mel.length);
      let phase = 0, curFreq = cfg.root, lpNoise = 0, hpNoise = 0, prevWhite = 0, brown = 0, crackleEnv = 0;
      for (let i = 0; i < n; i++) {
        const t = i / sr;
        const step = Math.floor(t / phraseLen) % cfg.mel.length;
        const target = cfg.root * cfg.scale[cfg.mel[step]];
        curFreq += (target - curFreq) * 0.0011;
        const pt = t % phraseLen;
        const vib = 1.0 + (pt > 0.6 ? 0.004 * Math.sin(2 * Math.PI * 4.8 * t) : 0);
        phase += (2 * Math.PI * curFreq * vib) / sr;
        const env = Math.min(1, pt / 0.9) * Math.min(1, (phraseLen - pt) / 1.0) * (0.86 + 0.14 * Math.sin(2 * Math.PI * 0.21 * t));

        const white = Math.random() * 2 - 1;
        lpNoise = lpNoise * 0.92 + white * 0.08;
        hpNoise = 0.82 * (hpNoise + white - prevWhite);
        prevWhite = white;
        brown = (brown + 0.02 * white) / 1.02;

        let sig = 0;
        if (cfg.ney > 0) {
          const ney = (0.58 * Math.sin(phase) + 0.24 * Math.sin(phase * 2 + 0.3) + 0.10 * Math.sin(phase * 3 + 0.8) + 0.04 * Math.sin(phase * 4)) * cfg.ney;
          sig += (ney + lpNoise * 0.03 * env * cfg.ney) * env;
        }
        if (cfg.drone > 0) {
          const dMod = 0.85 + 0.15 * Math.sin(2 * Math.PI * 0.08 * t);
          sig += cfg.drone * dMod * (0.72 * Math.sin(2 * Math.PI * (cfg.root * 0.5) * t) + 0.28 * Math.sin(2 * Math.PI * (cfg.root * 0.75) * t));
        }
        if (cfg.oud > 0) {
          const pluck = t % (phraseLen / 2);
          const fOud = target * 0.5;
          sig += cfg.oud * Math.exp(-pluck * 3.5) * (Math.sin(2 * Math.PI * fOud * t) + 0.35 * Math.sin(2 * Math.PI * fOud * 2 * t));
        }
        if (cfg.rain > 0) sig += lpNoise * cfg.rain * (0.78 + 0.22 * Math.sin(t * 0.52));
        if (cfg.wind > 0) sig += brown * cfg.wind * (0.65 + 0.35 * Math.sin(2 * Math.PI * 0.07 * t));
        if (cfg.stream > 0) sig += (lpNoise * 0.7 + hpNoise * 0.12) * cfg.stream * (0.75 + 0.25 * Math.sin(t * 1.4));
        if (cfg.fire) {
          if (Math.random() < 0.00055) crackleEnv = 0.45 + Math.random() * 0.55;
          crackleEnv *= 0.992;
          sig += brown * 0.18 + hpNoise * crackleEnv * 0.24;
        }
        if (cfg.birds) {
          const bt = t % 4.5;
          if (bt > 0.8 && bt < 1.18) {
            const ct = bt - 0.8;
            const bFreq = 2650 + 550 * Math.sin(ct * 28);
            sig += 0.065 * Math.sin(Math.PI * ct / 0.38) * Math.sin(2 * Math.PI * bFreq * ct);
          } else if (bt > 2.6 && bt < 2.92) {
            const ct = bt - 2.6;
            const bFreq = 3100 - 420 * ct;
            sig += 0.05 * Math.sin(Math.PI * ct / 0.32) * Math.sin(2 * Math.PI * bFreq * ct);
          }
        }
        if (cfg.res432) {
          sig += 0.16 * Math.sin(2 * Math.PI * 432 * t) * (0.5 + 0.5 * Math.sin(2 * Math.PI * 0.11 * t))
               + 0.12 * Math.sin(2 * Math.PI * 216 * t);
        }
        if (cfg.pink) sig += (brown * 0.18 + lpNoise * 0.06);
        if (cfg.ney === 0 && cfg.rain === 0 && cfg.wind === 0 && cfg.stream === 0 && !cfg.fire && !cfg.birds && !cfg.res432 && !cfg.pink) sig += brown * 0.22;

        raw[i] = sig * 0.42 * Math.min(1, t / 1.8, (dur - t) / 1.8);
      }
      const out = new Float32Array(n); out.set(raw);
      for (const dSec of [0.11, 0.23, 0.37]) {
        const d = Math.floor(dSec * sr);
        for (let i = d; i < n; i++) out[i] += out[i - d] * 0.30;
      }
      const buf = ctx.createBuffer(1, n, sr); buf.getChannelData(0).set(out);
      presetBufferCache[key] = buf; return buf;
    }

    function stopSynth() { if (synthSrcNode) { try { synthSrcNode.stop(); synthSrcNode.disconnect(); } catch (e) {} synthSrcNode = null; } }
    function startSynth() {
      stopSynth();
      try {
        const AC = window.AudioContext || window.webkitAudioContext;
        if (!audioCtx) audioCtx = new AC();
        if (audioCtx.state === 'suspended') audioCtx.resume();
        if (!synthMaster) { synthMaster = audioCtx.createGain(); synthMaster.connect(audioCtx.destination); }
        const src = audioCtx.createBufferSource();
        src.buffer = getOrBuildMakamBuffer(audioCtx, state.fonPreset); src.loop = true;
        src.connect(synthMaster); src.start(); synthSrcNode = src;
        applyFonGain(isFonMutedNow() ? 0 : getFonGain(), 0.45);
      } catch (e) {}
    }

    function ensureCycleTimer() {
      clearInterval(state.cycleTimerId);
      if (state.fonMode === 'cycle' && state.fonEnabled) {
        state.cycleTimerId = setInterval(() => {
          if (!state.fonEnabled || state.fonMode !== 'cycle' || audio.paused) return;
          const idx = CYCLE_ORDER.indexOf(state.fonPreset);
          state.fonPreset = CYCLE_ORDER[(idx + 1) % CYCLE_ORDER.length];
          document.getElementById('fonPresetSelect').value = state.fonPreset;
          startSynth();
        }, 210000); // Softly rotate makam every 3.5 min
      }
    }

    function getFonGain() { return Math.max(0, Math.min(1, state.fonUnit / 10)); }
    function isFonMutedNow() { return state.isDucked || state.manualMute; }

    function playCurrentFonMp3() {
      if (!state.fonPlaylist.length) return;
      if (state.fonIndex >= state.fonPlaylist.length) state.fonIndex = 0;
      const item = state.fonPlaylist[state.fonIndex];
      if (!item.url) return;
      if (fonAudio.src !== item.url) fonAudio.src = item.url;
      fonAudio.volume = isFonMutedNow() ? 0 : getFonGain();
      fonAudio.play().catch(() => {});
    }

    fonAudio.addEventListener('ended', () => {
      if ((!state.fonEnabled && !state.fonPreviewing) || state.fonMode !== 'playlist' || !state.fonPlaylist.length) return;
      const cur = state.fonPlaylist[state.fonIndex];
      state.fonRepeatCounter++;
      const maxRep = cur.repeats === 0 ? Infinity : (cur.repeats || 1);
      if (state.fonRepeatCounter < maxRep) { fonAudio.currentTime = 0; fonAudio.play().catch(() => {}); }
      else { state.fonRepeatCounter = 0; state.fonIndex = (state.fonIndex + 1) % state.fonPlaylist.length; playCurrentFonMp3(); }
    });

    function syncFonPlayback() {
      const inGap = !!state.gapTimerId;
      const shouldRun = state.fonPreviewing || (state.fonEnabled && (!audio.paused || inGap) && (!!audio.src || state.radioRunning));
      if (!shouldRun) { stopSynth(); fonAudio.pause(); clearInterval(state.cycleTimerId); return; }
      if (state.fonMode === 'playlist' && state.fonPlaylist.length) { stopSynth(); playCurrentFonMp3(); }
      else { fonAudio.pause(); if (!synthSrcNode) startSynth(); ensureCycleTimer(); }
      applyFonGain(isFonMutedNow() ? 0 : getFonGain(), 0.35);
    }

    function applyFonGain(targetVol, rampSec = 1.2) {
      if (synthMaster && audioCtx) {
        const now = audioCtx.currentTime;
        const g = Math.max(0.0001, targetVol * 0.72);
        try {
          synthMaster.gain.cancelScheduledValues(now);
          synthMaster.gain.setValueAtTime(Math.max(0.0001, synthMaster.gain.value), now);
          synthMaster.gain.linearRampToValueAtTime(g, now + Math.max(0.05, rampSec));
        } catch (e) { synthMaster.gain.value = g; }
      }
      clearInterval(fonAudio._ramp);
      const startV = fonAudio.volume, steps = Math.max(1, Math.round(rampSec * 25));
      let step = 0;
      fonAudio._ramp = setInterval(() => {
        step++;
        fonAudio.volume = Math.max(0, Math.min(1, startV + ((targetVol - startV) * (step / steps))));
        if (step >= steps) clearInterval(fonAudio._ramp);
      }, 40);
    }

    // =========================================================================
    // SEAMLESS AUDIO ENGINE + SAME-TRACK SEEK + SILENCE GAP + PRELOAD
    // =========================================================================
    const audio = document.getElementById('audioEl');
    const preloadEl = document.getElementById('preloadEl');
    const playBtn = document.getElementById('playBtn');
    const seekBar = document.getElementById('seekBar');
    const tCur = document.getElementById('tCur');
    const tDur = document.getElementById('tDur');
    const pTitle = document.getElementById('pTitle');
    const pSub = document.getElementById('pSub');

    function cancelMainFade() { clearInterval(audio._fade); audio._fade = null; }
    function cancelGapTimer() { if (state.gapTimerId) { clearTimeout(state.gapTimerId); state.gapTimerId = null; } }

    function fadeMainAudio(toVol, ms = 1200, onDone = null) {
      cancelMainFade();
      const fromVol = audio.volume, steps = Math.max(1, Math.round(ms / 40));
      let s = 0;
      audio._fade = setInterval(() => {
        s++;
        audio.volume = Math.max(0, Math.min(1, fromVol + (toVol - fromVol) * (s / steps)));
        if (s >= steps) { cancelMainFade(); if (onDone) onDone(); }
      }, 40);
    }

    function getResolvedQuranRanges(trackId, duration) {
      const tr = trackMap.get(trackId);
      const list = [];
      if (state.duckBesmele && state.openingMuteSec > 0) {
        list.push({ ss: 0, es: state.openingMuteSec, kind: 'besmele', label: `Açılış Besmele/Ayet (0–${state.openingMuteSec}s)` });
      }
      const raw = [...(tr?.quranRanges || []), ...(state.customQuranMarks[trackId] || [])];
      const baseLen = tr?.quranRanges?.length || 0;
      raw.forEach((r, idx) => {
        const kind = r.kind || 'ayet';
        if (kind === 'besmele' && !state.duckBesmele) return;
        if (kind === 'ayet' && !state.duckEnabled) return;
        if (typeof r.ss === 'number' && typeof r.es === 'number') {
          list.push({ ss: r.ss, es: r.es, kind, label: r.label || 'Ayet Tilaveti', isCustom: idx >= baseLen });
        } else if (duration && typeof r.sr === 'number' && typeof r.er === 'number') {
          list.push({ ss: r.sr * duration, es: r.er * duration, kind, label: r.label || 'Ayet Tilaveti', isCustom: false });
        }
      });
      return list;
    }

    function renderTimelineTicks() {
      const box = document.getElementById('timelineTicks'); box.innerHTML = '';
      const winEl = document.getElementById('sliceWinBar');
      const dur = audio.duration || trackMap.get(state.currentTrackId)?.durationSec || 0;
      if (!state.currentTrackId || !dur) { winEl.classList.add('hidden'); return; }

      if (state.currentClip && state.sliceMode) {
        const s = typeof state.currentClip.startSec === 'number' ? state.currentClip.startSec : state.currentClip.startRatio * dur;
        const e = typeof state.currentClip.endSec === 'number' ? state.currentClip.endSec : state.currentClip.endRatio * dur;
        const l = Math.max(0, Math.min(100, (s / dur) * 100));
        const w = Math.max(1.2, Math.min(100 - l, ((e - s) / dur) * 100));
        winEl.style.left = `${l}%`; winEl.style.width = `${w}%`;
        winEl.classList.remove('hidden');
      } else { winEl.classList.add('hidden'); }

      getResolvedQuranRanges(state.currentTrackId, dur).forEach(r => {
        const t = document.createElement('div'); t.className = 'q-tick';
        const l = Math.max(0, (r.ss / dur) * 100);
        const w = Math.max(0.5, ((r.es - r.ss) / dur) * 100);
        t.style.left = `${l}%`; t.style.width = `${Math.min(100 - l, w)}%`;
        box.appendChild(t);
      });
    }

    function updatePlayerSubtext() {
      const tr = trackMap.get(state.currentTrackId);
      if (!tr) return;
      const dur = audio.duration || tr.durationSec || 600;
      const base = `${tr.reader}${tr.pages ? ' · ' + tr.pages : ''}`;
      const prefix = state.radioRunning ? 'Radyo · ' : '';
      if ((state.manualMute || state.isDucked) && state.fonEnabled) {
        pSub.textContent = `${prefix}Ayet/Besmele — Fon sessiz · ${base}`;
      } else if (state.currentClip && state.sliceMode) {
        const s = fmtTime(typeof state.currentClip.startSec === 'number' ? state.currentClip.startSec : state.currentClip.startRatio * dur);
        const e = fmtTime(typeof state.currentClip.endSec === 'number' ? state.currentClip.endSec : state.currentClip.endRatio * dur);
        const relTxt = state.currentClip.rel === 'direct' ? 'Doğrudan · ' : (state.currentClip.rel === 'indirect' ? 'Dolaylı · ' : '');
        pSub.textContent = `${prefix}${relTxt}${s}–${e} · ${base}`;
      } else {
        pSub.textContent = `${prefix}${base}`;
      }
    }

    function updateMediaSession(tr, title) {
      if ('mediaSession' in navigator) {
        try {
          navigator.mediaSession.metadata = new MediaMetadata({ title, artist: tr.reader, album: `Risale-i Nur · ${tr.section}` });
          navigator.mediaSession.setActionHandler('play', () => audio.play().catch(() => {}));
          navigator.mediaSession.setActionHandler('pause', () => audio.pause());
          navigator.mediaSession.setActionHandler('previoustrack', () => document.getElementById('prevBtn').click());
          navigator.mediaSession.setActionHandler('nexttrack', () => document.getElementById('nextBtn').click());
          navigator.mediaSession.setActionHandler('seekbackward', () => { audio.currentTime = Math.max(0, audio.currentTime - state.skipStepSec); });
          navigator.mediaSession.setActionHandler('seekforward', () => { audio.currentTime = Math.min(audio.duration || 0, audio.currentTime + state.skipStepSec); });
        } catch (e) {}
      }
    }

    function recordHistory(tr, clipTitle, sec) {
      const entry = {
        trackId: tr.id, title: clipTitle || tr.title, reader: tr.reader,
        sec: Math.floor(sec || 0), clip: state.currentClip ? { ...state.currentClip } : null,
        playlistId: state.activePlaylistId, at: Date.now(),
      };
      state.history = state.history.filter(h => !(h.trackId === entry.trackId && Math.abs((h.sec || 0) - entry.sec) < 25));
      state.history.unshift(entry);
      if (state.history.length > 18) state.history.length = 18;
    }

    function preloadNextTrack() {
      const nextItem = state.queue[state.queueIndex + 1];
      if (!nextItem) return;
      const nextTid = typeof nextItem === 'string' ? nextItem : nextItem.trackId;
      if (nextTid === state.currentTrackId) return;
      const nextTr = trackMap.get(nextTid);
      if (nextTr && preloadEl.src !== nextTr.mp3) { try { preloadEl.src = nextTr.mp3; } catch (e) {} }
    }

    function loadQueueItem(item, autoplay = true, explicitSec = null) {
      const trackId = typeof item === 'string' ? item : item.trackId;
      const tr = trackMap.get(trackId);
      if (!tr) return;

      cancelGapTimer();
      const prevTrackId = state.currentTrackId;
      const sameTrack = (prevTrackId === tr.id && audio.src && audio.readyState >= 1);

      cancelMainFade();
      state.currentTrackId = tr.id;
      state.currentClip = (typeof item === 'object' && (typeof item.startSec === 'number' || typeof item.startRatio === 'number')) ? item : null;
      state.isDucked = false; state.fonPreviewing = false; state.fadingOut = false; state.advancingClip = false;

      const targetVol = Math.max(0, Math.min(1, state.risaleUnit / 10));
      const displayTitle = (state.currentClip?.clipTitle) || tr.title;
      pTitle.textContent = displayTitle;
      tDur.textContent = fmtTime(audio.duration && sameTrack ? audio.duration : (tr.durationSec || 0));
      updatePlayerSubtext();
      updateMediaSession(tr, displayTitle);

      const applySeekAndStart = () => {
        const dur = audio.duration || tr.durationSec || 600;
        tDur.textContent = fmtTime(dur);
        let seekTo = 0;
        if (typeof explicitSec === 'number' && explicitSec >= 0) {
          seekTo = Math.min(dur - 2, explicitSec);
        } else if (state.currentClip && state.sliceMode) {
          seekTo = typeof state.currentClip.startSec === 'number' ? state.currentClip.startSec : state.currentClip.startRatio * dur;
          seekTo = Math.min(dur - 2, Math.max(0, seekTo));
        } else if (state.autoResume) {
          const rs = state.positions[tr.id] || 0;
          if (rs > 5 && rs < dur - 10) seekTo = rs;
        }
        audio.currentTime = seekTo;
        audio.playbackRate = state.speed;
        renderTimelineTicks();
        updatePlayerSubtext();
        recordHistory(tr, displayTitle, seekTo);
        if (state.activePlaylistId && !state.radioRunning) {
          state.playlistProgress[state.activePlaylistId] = { idx: state.queueIndex, sec: Math.floor(seekTo), updatedAt: Date.now() };
        }
        if (document.getElementById('focusModal').classList.contains('open')) renderFocusModal();
        if (state.crossfade && autoplay) { audio.volume = 0.05; fadeMainAudio(targetVol, 1100); }
        else audio.volume = targetVol;
        preloadNextTrack();
        saveState();
      };

      if (sameTrack) {
        applySeekAndStart();
        if (autoplay) audio.play().catch(() => {});
      } else {
        audio.src = tr.mp3;
        audio.volume = state.crossfade ? 0.05 : targetVol;
        audio.playbackRate = state.speed;
        audio.onloadedmetadata = applySeekAndStart;
        audio.onerror = () => { pSub.textContent = 'Ses bağlantısı kurulamadı'; };
        if (autoplay) audio.play().catch(() => {});
      }

      highlightPlaying();
      saveState();
    }

    function playQueue(items, startIdx = 0, label = '', playlistId = null, explicitSec = null, isRadioCall = false) {
      if (!items || !items.length) return;
      if (!isRadioCall && state.radioRunning) { state.radioRunning = false; renderRadioSection(); }
      state.queue = items.map(x => typeof x === 'string' ? { trackId: x } : x);
      state.queueIndex = Math.max(0, Math.min(startIdx, state.queue.length - 1));
      state.activePlaylistId = playlistId;
      if (!isRadioCall && playlistId && state.playlistFonMap[playlistId]) {
        const pf = state.playlistFonMap[playlistId];
        state.fonEnabled = !!pf.enabled;
        if (pf.mode) state.fonMode = pf.mode;
        if (pf.preset) state.fonPreset = pf.preset;
        if (typeof pf.fonUnit === 'number') state.fonUnit = pf.fonUnit;
        syncMixerAndFonUI();
      }
      loadQueueItem(state.queue[state.queueIndex], true, explicitSec);
      if (label) showToast(label);
    }

    function transitionWithOptionalGap(nextFn) {
      const gap = state.silenceGapSec || 0;
      if (gap > 0) {
        audio.pause();
        pSub.textContent = `Tefekkür arası (${gap} sn)...`;
        state.gapTimerId = setTimeout(() => {
          state.gapTimerId = null;
          nextFn();
        }, gap * 1000);
        syncFonPlayback();
      } else {
        nextFn();
      }
    }

    function advanceQueue() {
      if (state.advancingClip) return;
      state.advancingClip = true;

      if (state.sleepAtEnd) {
        state.sleepAtEnd = false; document.getElementById('sleepSelect').value = '0';
        audio.pause(); showToast('Parça sonunda durduruldu'); return;
      }
      if (state.repeatMode === 'clip' && state.queue[state.queueIndex]) {
        transitionWithOptionalGap(() => loadQueueItem(state.queue[state.queueIndex], true));
        return;
      }
      if (!state.autoNext && !state.radioRunning) { audio.pause(); return; }

      if (state.shuffle && state.queue.length > 1) {
        let nxt = state.queueIndex;
        while (nxt === state.queueIndex) nxt = Math.floor(Math.random() * state.queue.length);
        state.queueIndex = nxt;
        transitionWithOptionalGap(() => loadQueueItem(state.queue[state.queueIndex], true));
        return;
      }

      if (state.queueIndex + 1 < state.queue.length) {
        state.queueIndex++;
        transitionWithOptionalGap(() => loadQueueItem(state.queue[state.queueIndex], true));
      } else if (state.radioRunning) {
        // In Radio mode, loop within active scheduled block OR advance to next block in continuous 7/24 mode
        transitionWithOptionalGap(() => {
          if (state.radio.mode === 'continuous') advanceRadioBlock();
          else { state.queueIndex = 0; loadQueueItem(state.queue[0], true); }
        });
      } else if (state.repeatMode === 'list' && state.queue.length) {
        state.queueIndex = 0;
        transitionWithOptionalGap(() => loadQueueItem(state.queue[0], true));
      } else {
        audio.pause();
      }
    }

    audio.addEventListener('play', () => {
      playBtn.textContent = '⏸';
      if (!state.fadingOut) audio.volume = Math.max(0, Math.min(1, state.risaleUnit / 10));
      syncFonPlayback();
    });
    audio.addEventListener('pause', () => {
      cancelMainFade();
      playBtn.textContent = '▶';
      syncFonPlayback();
    });
    audio.addEventListener('ended', () => {
      if (state.currentTrackId) { delete state.positions[state.currentTrackId]; saveState(); }
      advanceQueue();
    });

    audio.addEventListener('timeupdate', () => {
      if (!audio.duration) return;
      const cur = audio.currentTime;
      seekBar.value = (cur / audio.duration) * 100;
      tCur.textContent = fmtTime(cur);

      const secInt = Math.floor(cur);
      if (!audio.paused && secInt !== state.lastTickSec) {
        state.lastTickSec = secInt;
        if (state.listenStats.todayKey !== todayKey()) { state.listenStats.todayKey = todayKey(); state.listenStats.todaySec = 0; }
        state.listenStats.todaySec += 1;
        state.listenStats.totalSec += 1;
      }

      if (state.currentClip && state.sliceMode) {
        const endSec = typeof state.currentClip.endSec === 'number' ? state.currentClip.endSec : state.currentClip.endRatio * audio.duration;
        if (state.crossfade && !state.fadingOut && cur >= endSec - 1.8 && cur < endSec) {
          state.fadingOut = true; fadeMainAudio(0.02, 1600);
        }
        if (cur >= endSec) { advanceQueue(); return; }
      }

      if (state.fonEnabled && (state.duckEnabled || state.duckBesmele) && state.currentTrackId) {
        const ranges = getResolvedQuranRanges(state.currentTrackId, audio.duration);
        const hit = ranges.find(r => cur >= Math.max(0, r.ss - state.preRollSec) && cur <= (r.es + state.postRollSec));
        const shouldDuck = !!hit;
        if (shouldDuck !== state.isDucked) {
          state.isDucked = shouldDuck;
          if (!state.manualMute) applyFonGain(shouldDuck ? 0 : getFonGain(), shouldDuck ? Math.min(state.preRollSec, 2.2) : 1.6);
          updatePlayerSubtext();
        }
      }

      if (document.getElementById('focusModal').classList.contains('open') && state.focusTab === 'pasaj' && secInt % 2 === 0) renderFocusPasaj();
      if (state.currentTrackId && secInt % 5 === 0) {
        if (!state.currentClip) state.positions[state.currentTrackId] = secInt;
        if (state.activePlaylistId && !state.radioRunning) {
          state.playlistProgress[state.activePlaylistId] = { idx: state.queueIndex, sec: secInt, updatedAt: Date.now() };
        }
        saveState();
      }
    });

    playBtn.onclick = () => {
      cancelGapTimer();
      if (!audio.src && state.queue.length) { loadQueueItem(state.queue[state.queueIndex], true); return; }
      if (audio.paused) audio.play().catch(() => {}); else audio.pause();
    };
    document.getElementById('prevBtn').onclick = () => {
      cancelGapTimer();
      if (state.queueIndex > 0) { state.queueIndex--; loadQueueItem(state.queue[state.queueIndex], true); }
    };
    document.getElementById('nextBtn').onclick = () => {
      cancelGapTimer();
      if (state.queueIndex + 1 < state.queue.length) { state.queueIndex++; loadQueueItem(state.queue[state.queueIndex], true); }
      else if (state.radioRunning) advanceRadioBlock();
    };
    seekBar.oninput = () => {
      cancelMainFade(); state.fadingOut = false;
      audio.volume = Math.max(0, Math.min(1, state.risaleUnit / 10));
      if (audio.duration) audio.currentTime = (seekBar.value / 100) * audio.duration;
    };

    // =========================================================================
    // RADYO AKIŞI — CONTINUOUS & SCHEDULED PERSONAL BROADCAST ENGINE
    // =========================================================================
    const DAY_LABELS = ['Paz', 'Pzt', 'Sal', 'Çar', 'Per', 'Cum', 'Cmt'];

    function hhmmToMin(s) {
      const [h, m] = (s || '00:00').split(':').map(Number);
      return (h || 0) * 60 + (m || 0);
    }

    function isBlockActiveAt(blk, day, curMin) {
      if (!blk.enabled) return false;
      if (blk.days && blk.days.length && !blk.days.includes(day)) return false;
      const st = hhmmToMin(blk.start), en = hhmmToMin(blk.end);
      if (st === en) return true;
      if (st < en) return curMin >= st && curMin < en;
      // Overnight block (e.g., 20:00 -> 06:00)
      return curMin >= st || curMin < en;
    }

    function resolveRadioBlockClips(blk) {
      let clips = [];
      if (blk.source === 'all:sozler') {
        clips = state.segments.map((s, segIdx) => ({ segIdx, trackId: s.trackId, clipTitle: s.title, startSec: s.ss, endSec: s.es, startRatio: s.sr, endRatio: s.er, excerpt: s.excerpt, section: s.section }));
      } else {
        const [kind, id] = (blk.source || '').split(':');
        const pool = kind === 'user' ? state.userPlaylists : state.playlists;
        const p = pool.find(x => x.id === id) || state.playlists[0];
        clips = p ? [...p.clips] : [];
      }
      if (blk.filter === 'direct') {
        const dOnly = clips.filter(c => c.rel === 'direct');
        if (dOnly.length) clips = dOnly;
      }
      if (blk.reader) {
        const rOnly = clips.filter(c => trackMap.get(c.trackId)?.reader === blk.reader);
        if (rOnly.length) clips = rOnly;
      }
      if (blk.filter === 'shuffle') {
        clips = [...clips].sort(() => Math.random() - 0.5);
      }
      return clips;
    }

    function startRadioBlock(blk) {
      if (!blk) return;
      const clips = resolveRadioBlockClips(blk);
      if (!clips.length) { showToast('Bu blokta oynatılacak kesit bulunamadı'); return; }

      state.radioRunning = true;
      state.radioActiveBlockId = blk.id;
      if (blk.speed) { state.speed = parseFloat(blk.speed); audio.playbackRate = state.speed; syncSpeedUI(); }
      if (blk.fon === 'off') {
        state.fonEnabled = false;
      } else if (blk.fon && blk.fon !== 'keep') {
        state.fonEnabled = true;
        if (blk.fon === 'playlist') { state.fonMode = 'playlist'; }
        else if (blk.fon === 'cycle') { state.fonMode = 'cycle'; }
        else { state.fonMode = 'synth'; state.fonPreset = blk.fon; }
        if (typeof blk.fonVol === 'number') state.fonUnit = blk.fonVol;
      }
      syncMixerAndFonUI();
      playQueue(clips, 0, `Radyo: ${blk.title}`, 'radio:' + blk.id, null, true);
      syncFonPlayback();
      renderRadioSection();
    }

    function findScheduledBlockNow() {
      const enabled = state.radio.blocks.filter(b => b.enabled);
      if (!enabled.length) return null;
      const now = new Date();
      const day = now.getDay();
      const curMin = now.getHours() * 60 + now.getMinutes();
      return enabled.find(b => isBlockActiveAt(b, day, curMin)) || enabled[0];
    }

    function startRadio() {
      const enabled = state.radio.blocks.filter(b => b.enabled);
      if (!enabled.length) { showToast('En az bir aktif yayın bloğu ekleyin'); return; }
      const blk = state.radio.mode === 'scheduled' ? findScheduledBlockNow() : enabled[0];
      startRadioBlock(blk);
    }

    function stopRadio() {
      cancelGapTimer();
      state.radioRunning = false;
      state.radioActiveBlockId = null;
      audio.pause();
      syncFonPlayback();
      renderRadioSection();
      showToast('Radyo yayını durduruldu');
    }

    function advanceRadioBlock() {
      const enabled = state.radio.blocks.filter(b => b.enabled);
      if (!enabled.length) return;
      const idx = enabled.findIndex(b => b.id === state.radioActiveBlockId);
      const nextBlk = enabled[(idx + 1) % enabled.length];
      startRadioBlock(nextBlk);
    }

    function getSourceLabel(srcVal) {
      if (srcVal === 'all:sozler') return 'Tüm Sözler (2.373 Kesit)';
      const [kind, id] = (srcVal || '').split(':');
      const pool = kind === 'user' ? state.userPlaylists : state.playlists;
      const p = pool.find(x => x.id === id);
      return p ? p.title : 'Konu Fihristi';
    }

    function renderRadioSection() {
      const toggleBtn = document.getElementById('radioToggleBtn');
      const statusEl = document.getElementById('radioLiveStatus');
      const titleEl = document.getElementById('radioCurrentBlockTitle');
      const metaEl = document.getElementById('radioCurrentMeta');

      toggleBtn.textContent = state.radioRunning ? 'Yayını Durdur' : 'Yayını Başlat';
      const activeBlk = state.radio.blocks.find(b => b.id === state.radioActiveBlockId);
      if (state.radioRunning && activeBlk) {
        statusEl.textContent = state.radio.mode === 'scheduled' ? `Canlı Zamanlı Yayın · ${activeBlk.start}–${activeBlk.end}` : 'Canlı Kesintisiz 7/24 Yayın';
        titleEl.textContent = activeBlk.title;
        const fonName = activeBlk.fon === 'off' ? 'Fonsuz' : (activeBlk.fon === 'keep' ? 'Mevcut Fon' : (activeBlk.fon === 'cycle' ? 'Makam Döngüsü' : (FON_LABELS[activeBlk.fon] || 'MP3 Listem')));
        metaEl.textContent = `${getSourceLabel(activeBlk.source)} · ${fonName} · ${activeBlk.speed || 1}x hız`;
      } else {
        const nextBlk = findScheduledBlockNow();
        statusEl.textContent = 'Radyo Hazır';
        titleEl.textContent = nextBlk ? `Sıradaki Yayın: ${nextBlk.title}` : 'Kişisel Risale-i Nur Yayın Akışı';
        metaEl.textContent = nextBlk ? `${nextBlk.start}–${nextBlk.end} · ${getSourceLabel(nextBlk.source)}` : 'Aşağıdan yeni yayın bloğu ekleyin.';
      }

      document.querySelectorAll('#radioModeSeg .seg-btn').forEach(b => b.classList.toggle('active', b.dataset.rmode === state.radio.mode));
      document.querySelectorAll('#radioGapSeg .seg-btn').forEach(b => b.classList.toggle('active', Number(b.dataset.gap) === state.silenceGapSec));
      document.getElementById('radioStopEnabledChk').checked = !!state.radio.stopEnabled;
      document.getElementById('radioStopTimeInp').value = state.radio.stopTime || '23:30';

      const list = document.getElementById('radioBlocksList'); list.innerHTML = '';
      if (!state.radio.blocks.length) {
        list.innerHTML = '<p class="hint" style="padding:10px 4px;">Henüz yayın bloğu eklenmedi.</p>';
        return;
      }

      state.radio.blocks.forEach((blk, idx) => {
        const card = document.createElement('div');
        card.className = 'block-card' + (state.radioRunning && state.radioActiveBlockId === blk.id ? ' live' : '') + (!blk.enabled ? ' disabled' : '');
        const fTxt = blk.filter === 'direct' ? 'Sadece Doğrudan' : (blk.filter === 'shuffle' ? 'Karışık' : 'Hikmetli Sıra');
        const fonTxt = blk.fon === 'off' ? 'Fonsuz' : (blk.fon === 'keep' ? 'Mevcut Fon' : (blk.fon === 'cycle' ? 'Makam Döngüsü' : (FON_LABELS[blk.fon] || 'MP3 Listem')));
        card.innerHTML = `
          <div style="min-width:0; flex:1;">
            <div class="block-time">${escHtml(blk.start)} – ${escHtml(blk.end)} · ${fTxt}</div>
            <div class="block-name">${escHtml(blk.title)}</div>
            <div class="block-sub">${escHtml(getSourceLabel(blk.source))} · ${escHtml(fonTxt)}${blk.fon !== 'off' && blk.fon !== 'keep' ? ` (${blk.fonVol}/10)` : ''} · ${blk.speed || 1}x${blk.reader ? ' · ' + escHtml(blk.reader) : ''}</div>
          </div>
          <div class="block-ops">
            <button class="tiny-link" data-act="play" title="Bu bloğu hemen çal">▶</button>
            <button class="tiny-link" data-act="up" title="Yukarı taşı">↑</button>
            <button class="tiny-link" data-act="down" title="Aşağı taşı">↓</button>
            <button class="tiny-link" data-act="edit">Düzenle</button>
            <button class="tiny-link" data-act="del" title="Sil">✕</button>
            <input class="switch" type="checkbox" data-act="toggle" ${blk.enabled ? 'checked' : ''} />
          </div>
        `;
        card.querySelector('[data-act="play"]').onclick = () => startRadioBlock(blk);
        card.querySelector('[data-act="up"]').onclick = () => {
          if (idx > 0) { [state.radio.blocks[idx-1], state.radio.blocks[idx]] = [state.radio.blocks[idx], state.radio.blocks[idx-1]]; saveState(); renderRadioSection(); }
        };
        card.querySelector('[data-act="down"]').onclick = () => {
          if (idx + 1 < state.radio.blocks.length) { [state.radio.blocks[idx+1], state.radio.blocks[idx]] = [state.radio.blocks[idx], state.radio.blocks[idx+1]]; saveState(); renderRadioSection(); }
        };
        card.querySelector('[data-act="edit"]').onclick = () => openRadioBlockEditor(blk);
        card.querySelector('[data-act="del"]').onclick = () => { state.radio.blocks.splice(idx, 1); saveState(); renderRadioSection(); showToast('Blok silindi'); };
        card.querySelector('[data-act="toggle"]').onchange = (e) => { blk.enabled = e.target.checked; saveState(); renderRadioSection(); };
        list.appendChild(card);
      });
    }

    let rbSelectedDays = new Set([0,1,2,3,4,5,6]);
    function populateRadioEditorDropdowns() {
      const srcSel = document.getElementById('rbSource'); srcSel.innerHTML = '';
      const optAll = document.createElement('option'); optAll.value = 'all:sozler'; optAll.textContent = 'Tüm Sözler (Tam Külliyat Kesitleri)'; srcSel.appendChild(optAll);
      state.userPlaylists.forEach(p => { const o = document.createElement('option'); o.value = 'user:' + p.id; o.textContent = `[Özel] ${p.title}`; srcSel.appendChild(o); });
      state.playlists.forEach(p => { const o = document.createElement('option'); o.value = 'topic:' + p.id; o.textContent = `${p.title} (${p.directCount}D/${p.indirectCount}İ)`; srcSel.appendChild(o); });

      const fonSel = document.getElementById('rbFon'); fonSel.innerHTML = '';
      [['off', 'Fon Kapalı'], ['keep', 'Mevcut Fon Ayarını Koru'], ['cycle', 'Makamlar Arası Yumuşak Döngü'], ['playlist', 'Kendi MP3 Fon Listem']].forEach(([v, l]) => {
        const o = document.createElement('option'); o.value = v; o.textContent = l; fonSel.appendChild(o);
      });
      Object.entries(FON_LABELS).forEach(([k, l]) => {
        const o = document.createElement('option'); o.value = k; o.textContent = l; fonSel.appendChild(o);
      });

      const rdSel = document.getElementById('rbReader'); rdSel.innerHTML = '<option value="">Tümü (12 Okuyucu)</option>';
      EMBEDDED_CATALOG.readers.forEach(r => { const o = document.createElement('option'); o.value = r.name; o.textContent = r.name; rdSel.appendChild(o); });

      const dRow = document.getElementById('rbDaysRow'); dRow.innerHTML = '';
      DAY_LABELS.forEach((lbl, idx) => {
        const b = document.createElement('button'); b.type = 'button'; b.className = 'day-chip on'; b.textContent = lbl;
        b.dataset.day = idx;
        b.onclick = () => { if (rbSelectedDays.has(idx)) rbSelectedDays.delete(idx); else rbSelectedDays.add(idx); b.classList.toggle('on', rbSelectedDays.has(idx)); };
        dRow.appendChild(b);
      });
    }

    function openRadioBlockEditor(blk = null) {
      populateRadioEditorDropdowns();
      state.radioEditingId = blk ? blk.id : null;
      document.getElementById('radioEditorHeading').textContent = blk ? 'Yayın Bloğunu Düzenle' : 'Yeni Yayın Bloğu';
      document.getElementById('rbTitle').value = blk ? blk.title : '';
      document.getElementById('rbStart').value = blk ? blk.start : '07:00';
      document.getElementById('rbEnd').value = blk ? blk.end : '10:00';
      document.getElementById('rbSource').value = blk ? blk.source : (document.getElementById('rbSource').options[1]?.value || 'all:sozler');
      document.getElementById('rbFilter').value = blk ? (blk.filter || 'all') : 'all';
      document.getElementById('rbFon').value = blk ? (blk.fon || 'ussak') : 'ussak';
      document.getElementById('rbFonVol').value = blk && typeof blk.fonVol === 'number' ? blk.fonVol : 2.5;
      document.getElementById('rbFonValLbl').textContent = document.getElementById('rbFonVol').value;
      document.getElementById('rbSpeed').value = String(blk?.speed || 1);
      document.getElementById('rbReader').value = blk?.reader || '';
      rbSelectedDays = new Set(blk?.days || [0,1,2,3,4,5,6]);
      document.querySelectorAll('#rbDaysRow .day-chip').forEach(b => b.classList.toggle('on', rbSelectedDays.has(Number(b.dataset.day))));
      document.getElementById('radioBlockEditor').classList.remove('hidden');
    }

    document.getElementById('rbFonVol').oninput = (e) => { document.getElementById('rbFonValLbl').textContent = e.target.value; };
    document.getElementById('openNewBlockBtn').onclick = () => openRadioBlockEditor(null);
    document.getElementById('cancelBlockEditBtn').onclick = () => document.getElementById('radioBlockEditor').classList.add('hidden');
    document.getElementById('saveBlockBtn').onclick = () => {
      const src = document.getElementById('rbSource').value;
      const obj = {
        id: state.radioEditingId || ('rb-' + Date.now()),
        title: document.getElementById('rbTitle').value.trim() || getSourceLabel(src),
        start: document.getElementById('rbStart').value || '06:00',
        end: document.getElementById('rbEnd').value || '09:00',
        days: [...rbSelectedDays],
        source: src,
        filter: document.getElementById('rbFilter').value,
        fon: document.getElementById('rbFon').value,
        fonVol: parseFloat(document.getElementById('rbFonVol').value),
        speed: parseFloat(document.getElementById('rbSpeed').value),
        reader: document.getElementById('rbReader').value,
        enabled: true,
      };
      if (state.radioEditingId) {
        const i = state.radio.blocks.findIndex(b => b.id === state.radioEditingId);
        if (i >= 0) state.radio.blocks[i] = obj;
      } else {
        state.radio.blocks.push(obj);
      }
      saveState();
      document.getElementById('radioBlockEditor').classList.add('hidden');
      renderRadioSection();
      showToast('Yayın bloğu kaydedildi');
    };

    document.getElementById('radioToggleBtn').onclick = () => { if (state.radioRunning) stopRadio(); else startRadio(); };
    document.getElementById('radioNextBlockBtn').onclick = advanceRadioBlock;
    document.querySelectorAll('#radioModeSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.radio.mode = b.dataset.rmode; saveState(); renderRadioSection(); };
    });
    document.querySelectorAll('#radioGapSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.silenceGapSec = Number(b.dataset.gap); saveState(); syncGapUI(); };
    });
    document.getElementById('radioStopEnabledChk').onchange = (e) => { state.radio.stopEnabled = e.target.checked; saveState(); };
    document.getElementById('radioStopTimeInp').onchange = (e) => { state.radio.stopTime = e.target.value; saveState(); };

    // =========================================================================
    // BOOKMARKS & TEFEKKÜR SAHNESİ
    // =========================================================================
    function addBookmark(customNote = '') {
      const tr = trackMap.get(state.currentTrackId);
      if (!tr) { showToast('Önce bir ders başlatın'); return; }
      const sec = Math.floor(audio.currentTime || 0);
      const seg = (tr.segments || []).find(s => sec >= s.ss && sec <= s.es);
      state.bookmarks.unshift({
        id: 'bm-' + Date.now(), trackId: tr.id,
        title: (state.currentClip?.clipTitle) || tr.title, reader: tr.reader,
        sec, excerpt: seg?.ex || state.currentClip?.excerpt || '', note: customNote.trim(), createdAt: Date.now(),
      });
      saveState();
      showToast(`Yer imi kaydedildi (${fmtTime(sec)})`);
      if (document.getElementById('focusModal').classList.contains('open')) renderFocusMarks();
      if (!document.getElementById('panel-kayitlar').classList.contains('hidden')) renderKayitlarPanel();
    }

    function getFullTextForCurrentTime(tr, cur) {
      if (state.currentClip && typeof state.currentClip.segIdx === 'number') {
        const s = EMBEDDED_SEGMENTS[state.currentClip.segIdx];
        if (s && cur >= s.ss - 3 && cur <= s.es + 3) return { heading: s.title, text: s.fullText || s.excerpt };
      }
      const seg = (tr.segments || []).find(s => cur >= s.ss && cur <= s.es) || tr.segments?.[0];
      if (!seg) return { heading: tr.section, text: tr.title };
      const fullSeg = EMBEDDED_SEGMENTS.find(x => x.trackId === tr.id && Math.abs(x.ss - seg.ss) < 2);
      return { heading: `${seg.t} · ${fmtTime(seg.ss)}–${fmtTime(seg.es)}`, text: fullSeg?.fullText || seg.ex || tr.title };
    }

    function syncRepeatShuffleUI() {
      const rMap = { off: 'Tekrar: Kapalı', clip: 'Tekrar: Kesit', list: 'Tekrar: Liste' };
      const rBtn = document.getElementById('fRepeatBtn');
      const sBtn = document.getElementById('fShuffleBtn');
      rBtn.textContent = rMap[state.repeatMode] || 'Tekrar: Kapalı';
      rBtn.classList.toggle('active', state.repeatMode !== 'off');
      sBtn.textContent = state.shuffle ? 'Karışık: Açık' : 'Karışık: Kapalı';
      sBtn.classList.toggle('active', state.shuffle);
      document.querySelectorAll('#repeatSeg .seg-btn').forEach(b => b.classList.toggle('active', b.dataset.rep === state.repeatMode));
    }

    function renderFocusPasaj() {
      const tr = trackMap.get(state.currentTrackId);
      if (!tr) return;
      document.getElementById('focusTrackTitle').textContent = (state.currentClip?.clipTitle) || tr.title;
      document.getElementById('focusTrackMeta').textContent = `${tr.reader}${tr.pages ? ' · ' + tr.pages : ''}`;
      document.getElementById('fRewBtn').textContent = `-${state.skipStepSec} sn`;
      document.getElementById('fFwdBtn').textContent = `+${state.skipStepSec} sn`;

      const cur = audio.currentTime || 0;
      const info = getFullTextForCurrentTime(tr, cur);
      document.getElementById('focusSegHeading').textContent = info.heading;

      const kBox = document.getElementById('focusKunyeBox');
      if (state.currentClip?.kunye) {
        const badge = state.currentClip.rel === 'direct' ? 'Doğrudan Alakalı' : 'Dolaylı Bağlam';
        kBox.innerHTML = `<strong style="color:var(--accent);">${badge}</strong> · ${escHtml(state.currentClip.kunye)}`;
        kBox.classList.remove('hidden');
      } else { kBox.classList.add('hidden'); }

      document.getElementById('focusExcerpt').textContent = info.text;

      const list = document.getElementById('focusTimelineList'); list.innerHTML = '';
      (tr.segments || []).forEach(s => {
        const active = cur >= s.ss && cur <= s.es;
        const row = document.createElement('div'); row.className = 'sub-seg-row';
        if (active) row.style.cssText = 'background:var(--accent-soft); color:var(--text); font-weight:600;';
        row.innerHTML = `<span>${fmtTime(s.ss)}–${fmtTime(s.es)} · ${escHtml(s.t)}</span><span>▶</span>`;
        row.onclick = () => { audio.currentTime = s.ss; audio.play().catch(() => {}); renderFocusPasaj(); };
        list.appendChild(row);
      });
      syncRepeatShuffleUI();
    }

    function renderFocusQueue() {
      const info = document.getElementById('focusQueueInfo');
      const box = document.getElementById('focusQueueList'); box.innerHTML = '';
      if (!state.queue.length) { info.textContent = 'Sırada parça yok.'; return; }
      info.textContent = `Sırada ${state.queue.length} parça · Şu an ${state.queueIndex + 1}. sırada`;
      const start = Math.max(0, state.queueIndex - 3);
      state.queue.slice(start, start + 60).forEach((it, offset) => {
        const idx = start + offset;
        const tid = typeof it === 'string' ? it : it.trackId;
        const tr = trackMap.get(tid); if (!tr) return;
        const row = document.createElement('div'); row.className = 'sub-seg-row';
        if (idx === state.queueIndex) row.style.cssText = 'background:var(--accent-soft); color:var(--text); font-weight:600;';
        const title = it.clipTitle || tr.title;
        const rel = it.rel === 'direct' ? 'Doğrudan · ' : (it.rel === 'indirect' ? 'Dolaylı · ' : '');
        const span = typeof it.startSec === 'number' ? `${rel}${fmtTime(it.startSec)}–${fmtTime(it.endSec)}` : fmtTime(tr.durationSec);
        row.innerHTML = `<span style="overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${idx + 1}. ${escHtml(title)}</span><span style="flex-shrink:0;">${span}</span>`;
        row.onclick = () => { state.queueIndex = idx; loadQueueItem(state.queue[idx], true); renderFocusQueue(); };
        box.appendChild(row);
      });
    }

    function renderFocusMarks() {
      const bBox = document.getElementById('focusBookmarksList'); bBox.innerHTML = '';
      if (!state.bookmarks.length) {
        bBox.innerHTML = '<p class="hint">Henüz yer imi kaydedilmedi. Dinlerken B tuşuna veya Yer İmi Kaydet düğmesine basabilirsiniz.</p>';
      } else {
        state.bookmarks.forEach((bm, idx) => {
          const row = document.createElement('div'); row.className = 'mini-item'; row.style.cursor = 'pointer';
          row.innerHTML = `<div style="min-width:0;flex:1;"><div style="font-weight:600;color:var(--text);white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${fmtTime(bm.sec)} · ${escHtml(bm.note || bm.title)}</div><div class="hint" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${escHtml(bm.title)} · ${escHtml(bm.excerpt || bm.reader)}</div></div><button class="btn-ghost" data-del="${idx}" style="padding:2px 7px;font-size:0.7rem;">✕</button>`;
          row.onclick = () => { playQueue([{ trackId: bm.trackId }], 0, bm.note || bm.title, null, bm.sec); document.getElementById('focusModal').classList.remove('open'); };
          row.querySelector('[data-del]').onclick = (e) => { e.stopPropagation(); state.bookmarks.splice(idx, 1); saveState(); renderFocusMarks(); };
          bBox.appendChild(row);
        });
      }
      const hBox = document.getElementById('focusHistoryList'); hBox.innerHTML = '';
      if (!state.history.length) { hBox.innerHTML = '<p class="hint">Henüz dinleme geçmişi oluşmadı.</p>'; }
      else {
        state.history.forEach(h => {
          const row = document.createElement('div'); row.className = 'sub-seg-row';
          row.innerHTML = `<span style="overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${escHtml(h.title)}</span><span style="flex-shrink:0;">${fmtTime(h.sec)}</span>`;
          row.onclick = () => { playQueue([h.clip || { trackId: h.trackId }], 0, h.title, h.playlistId || null, h.sec); document.getElementById('focusModal').classList.remove('open'); };
          hBox.appendChild(row);
        });
      }
    }

    function setFocusTab(t) {
      state.focusTab = t;
      document.getElementById('fTabPasaj').classList.toggle('active', t === 'pasaj');
      document.getElementById('fTabQueue').classList.toggle('active', t === 'queue');
      document.getElementById('fTabMarks').classList.toggle('active', t === 'marks');
      document.getElementById('focusPanelPasaj').classList.toggle('hidden', t !== 'pasaj');
      document.getElementById('focusPanelQueue').classList.toggle('hidden', t !== 'queue');
      document.getElementById('focusPanelMarks').classList.toggle('hidden', t !== 'marks');
      if (t === 'pasaj') renderFocusPasaj();
      else if (t === 'queue') renderFocusQueue();
      else renderFocusMarks();
    }
    function renderFocusModal() { setFocusTab(state.focusTab); }

    document.getElementById('fTabPasaj').onclick = () => setFocusTab('pasaj');
    document.getElementById('fTabQueue').onclick = () => setFocusTab('queue');
    document.getElementById('fTabMarks').onclick = () => setFocusTab('marks');
    document.getElementById('fRewBtn').onclick = () => { audio.currentTime = Math.max(0, audio.currentTime - state.skipStepSec); };
    document.getElementById('fFwdBtn').onclick = () => { audio.currentTime = Math.min(audio.duration || 0, audio.currentTime + state.skipStepSec); };
    document.getElementById('fRepeatBtn').onclick = () => {
      const order = ['off', 'clip', 'list'];
      state.repeatMode = order[(order.indexOf(state.repeatMode) + 1) % order.length];
      saveState(); syncRepeatShuffleUI();
    };
    document.getElementById('fShuffleBtn').onclick = () => { state.shuffle = !state.shuffle; saveState(); syncRepeatShuffleUI(); };
    document.getElementById('fBookmarkBtn').onclick = () => addBookmark('');
    document.getElementById('addBookmarkNoteBtn').onclick = () => { const inp = document.getElementById('bookmarkNoteInput'); addBookmark(inp.value); inp.value = ''; };
    document.getElementById('openFocusTrigger').onclick = () => {
      if (!state.currentTrackId && !state.bookmarks.length && !state.history.length) return;
      document.getElementById('focusModal').classList.add('open');
      setFocusTab(state.currentTrackId ? 'pasaj' : 'marks');
    };
    document.getElementById('closeFocusBtn').onclick = () => document.getElementById('focusModal').classList.remove('open');

    // =========================================================================
    // SERENE MAIN VIEWS (KONU FİHRİSTİ, RADYO AKIŞI, AI LİSTE, TÜM DERSLER)
    // =========================================================================
    function makeCalmRow(title, sub, rightHtml, onClick, isPlaying = false, isSans = false, kunye = '') {
      const row = document.createElement('div');
      row.className = 'calm-item' + (isPlaying ? ' playing' : '');
      row.innerHTML = `
        <div style="min-width:0;">
          <div class="item-title ${isSans ? 'sans' : ''}">${escHtml(title)}</div>
          ${sub ? `<div class="item-sub">${escHtml(sub)}</div>` : ''}
          ${kunye && state.showKunye ? `<div class="item-kunye">${escHtml(kunye)}</div>` : ''}
        </div>
        <div class="item-action">
          ${rightHtml}
          <button class="circle-play" aria-label="Oynat">${isPlaying ? '♫' : '▶'}</button>
        </div>
      `;
      row.onclick = onClick;
      return row;
    }

    function highlightPlaying() {
      document.querySelectorAll('[data-tid]').forEach(el => {
        const cur = el.dataset.tid === state.currentTrackId;
        el.classList.toggle('playing', cur);
        const btn = el.querySelector('.circle-play');
        if (btn) btn.textContent = cur ? '♫' : '▶';
      });
    }

    function renderTopics() {
      const q = document.getElementById('searchInput').value.trim().toLowerCase();
      const box = document.getElementById('topicList'); box.innerHTML = '';
      state.playlists
        .filter(p => !q || p.title.toLowerCase().includes(q) || p.description.toLowerCase().includes(q))
        .forEach(p => {
          const prog = state.playlistProgress[p.id];
          const cntLbl = p.directCount ? `${p.directCount} doğrudan${p.indirectCount ? ' · ' + p.indirectCount + ' dolaylı' : ''}` : `${p.curatedCount} kesit`;
          const rightLbl = `<span>${escHtml(prog ? cntLbl + ' · #' + (prog.idx + 1) : cntLbl)}</span>`;
          const row = makeCalmRow(p.title, p.description, rightLbl, () => openDetail(p));
          row.querySelector('.circle-play').onclick = (e) => {
            e.stopPropagation();
            let list = state.defaultRelFilter === 'direct' ? p.clips.filter(c => c.rel === 'direct') : p.clips;
            if (!list.length) list = p.clips;
            if (state.reader) { const rOnly = list.filter(c => trackMap.get(c.trackId)?.reader === state.reader); if (rOnly.length) list = rOnly; }
            playQueue(list, prog ? Math.min(prog.idx, list.length - 1) : 0, p.title, p.id);
          };
          box.appendChild(row);
        });
    }

    function renderAllTracks() {
      const q = document.getElementById('searchInput').value.trim().toLowerCase();
      const box = document.getElementById('allTracksList'); box.innerHTML = '';
      const filtered = state.tracks.filter(t => {
        if (state.reader && t.reader !== state.reader) return false;
        if (q && !`${t.title} ${t.section} ${t.reader} ${t.pages}`.toLowerCase().includes(q)) return false;
        return true;
      });
      const ids = filtered.map(x => x.id);
      filtered.forEach((tr, idx) => {
        const savedPos = state.positions[tr.id];
        const sub = `${tr.reader}${tr.pages ? ' · ' + tr.pages : ''}${savedPos ? ' · Kaldığı yer: ' + fmtTime(savedPos) : ''}`;
        const row = makeCalmRow(tr.title, sub, `<span>${fmtTime(tr.durationSec)}</span>`, () => playQueue(ids, idx, tr.title), state.currentTrackId === tr.id, true);
        row.dataset.tid = tr.id;
        if (tr.segments && tr.segments.length > 1) {
          const segWrap = document.createElement('div'); segWrap.className = 'sub-segs hidden';
          const expBtn = document.createElement('button'); expBtn.className = 'tiny-link'; expBtn.textContent = `${tr.segments.length} bölüm`;
          expBtn.onclick = (e) => {
            e.stopPropagation();
            if (!segWrap.childElementCount) {
              tr.segments.forEach(s => {
                const sr = document.createElement('div'); sr.className = 'sub-seg-row';
                sr.innerHTML = `<span>${fmtTime(s.ss)}–${fmtTime(s.es)} · ${escHtml(s.t)}</span><span>▶</span>`;
                sr.onclick = () => playQueue([{ trackId: tr.id, clipTitle: s.t, startSec: s.ss, endSec: s.es, startRatio: s.sr, endRatio: s.er, excerpt: s.ex }], 0, s.t);
                segWrap.appendChild(sr);
              });
            }
            segWrap.classList.toggle('hidden');
          };
          row.querySelector('.item-action').prepend(expBtn);
          box.appendChild(row); box.appendChild(segWrap);
        } else { box.appendChild(row); }
      });
    }

    // Topic Detail with Simple Direct/Hikmetli Filter + Künye
    function getFilteredDetailClips() {
      const p = state.detailPlaylist; if (!p) return [];
      const ch = document.getElementById('detailChapterFilter').value;
      const rd = document.getElementById('detailReaderFilter').value;
      const q = document.getElementById('detailSearchFilter').value.trim().toLowerCase();
      return p.clips.filter(it => {
        if (state.detailRelFilter === 'direct' && it.rel && it.rel !== 'direct') return false;
        if (state.detailRelFilter === 'indirect' && it.rel && it.rel !== 'indirect') return false;
        const tid = typeof it === 'string' ? it : it.trackId;
        const tr = trackMap.get(tid); if (!tr) return false;
        if (ch && tr.section !== ch) return false;
        if (rd && tr.reader !== rd) return false;
        if (q && !`${it.clipTitle || tr.title} ${it.excerpt || ''} ${it.kunye || ''} ${tr.section} ${tr.reader}`.toLowerCase().includes(q)) return false;
        return true;
      });
    }

    function renderDetailItems() {
      const p = state.detailPlaylist; if (!p) return;
      const items = getFilteredDetailClips();
      const list = document.getElementById('detailTracks'); list.innerHTML = '';

      document.querySelectorAll('#detailRelSeg .seg-btn').forEach(b => b.classList.toggle('active', b.dataset.rel === state.detailRelFilter));
      document.getElementById('detailCountHint').textContent = `${items.length} kesit`;

      if (!items.length) {
        list.innerHTML = '<p class="hint" style="padding:18px 8px;">Seçili filtrede kesit bulunamadı.</p>';
      } else {
        items.slice(0, state.detailLimit).forEach((it, idx) => {
          const tid = typeof it === 'string' ? it : it.trackId;
          const tr = trackMap.get(tid); if (!tr) return;
          const title = it.clipTitle || tr.title;
          const span = typeof it.startSec === 'number' ? `${fmtTime(it.startSec)}–${fmtTime(it.endSec)}` : fmtTime(tr.durationSec);
          const relHtml = it.rel ? `<span class="rel-tag ${it.rel === 'direct' ? 'direct' : ''}">${it.rel === 'direct' ? 'Doğrudan' : 'Dolaylı'}</span><span>· ${span}</span>` : `<span>${span}</span>`;
          const row = makeCalmRow(title, it.excerpt || `${tr.reader} · ${tr.pages}`, relHtml, () => playQueue(items, idx, p.title, p.id), state.currentTrackId === tr.id, true, it.kunye || '');
          row.dataset.tid = tr.id;
          list.appendChild(row);
        });
      }

      const moreWrap = document.getElementById('detailLoadMoreWrap');
      const rem = items.length - Math.min(items.length, state.detailLimit);
      if (rem > 0) {
        document.getElementById('detailLoadMoreBtn').textContent = `Daha fazla kesit göster (${rem} kesit daha)`;
        moreWrap.classList.remove('hidden');
      } else { moreWrap.classList.add('hidden'); }

      document.getElementById('detailPlayAll').onclick = () => playQueue(items, 0, p.title, p.id);
      document.getElementById('detailShuffleBtn').onclick = () => playQueue([...items].sort(() => Math.random() - 0.5), 0, `${p.title} (Karışık)`, p.id);
    }

    function openDetail(p) {
      state.detailPlaylist = p;
      state.detailLimit = 60;
      state.activePlaylistId = p.id;
      const hasRel = p.clips.some(c => c.rel === 'direct' || c.rel === 'indirect');
      state.detailRelFilter = hasRel ? (state.defaultRelFilter === 'direct' && p.directCount ? 'direct' : 'all') : 'all';

      ['topicsSection', 'radioSection', 'aiSection', 'allTracksSection'].forEach(id => document.getElementById(id).classList.add('hidden'));
      document.getElementById('topicDetail').classList.remove('hidden');
      document.getElementById('detailTitle').textContent = p.title;
      document.getElementById('detailRefs').textContent = p.fihristRefs && p.fihristRefs.length ? p.fihristRefs.slice(0, 4).join(' · ') : p.description;

      const relBar = document.getElementById('detailRelBar');
      relBar.classList.toggle('hidden', !hasRel);
      if (hasRel) {
        const dBtn = relBar.querySelector('[data-rel="direct"]');
        const iBtn = relBar.querySelector('[data-rel="indirect"]');
        dBtn.textContent = `Sadece Doğrudan (${p.directCount || 0})`;
        iBtn.textContent = `Dolaylı (${p.indirectCount || 0})`;
        dBtn.classList.toggle('hidden', !p.directCount);
        iBtn.classList.toggle('hidden', !p.indirectCount);
      }

      const prog = state.playlistProgress[p.id];
      const resBtn = document.getElementById('detailResumeBtn');
      if (prog && prog.idx > 0 && prog.idx < p.clips.length) {
        resBtn.textContent = `Kaldığın Yerden (#${prog.idx + 1})`;
        resBtn.classList.remove('hidden');
        resBtn.onclick = () => playQueue(p.clips, prog.idx, p.title, p.id, prog.sec);
      } else { resBtn.classList.add('hidden'); }

      const fBar = document.getElementById('detailFiltersBar');
      const chSel = document.getElementById('detailChapterFilter');
      const rdSel = document.getElementById('detailReaderFilter');
      document.getElementById('detailSearchFilter').value = '';
      if (p.clips.length > 18) {
        const chCounts = new Map(), rdCounts = new Map();
        p.clips.forEach(c => {
          const tr = trackMap.get(typeof c === 'string' ? c : c.trackId); if (!tr) return;
          chCounts.set(tr.section, (chCounts.get(tr.section) || 0) + 1);
          rdCounts.set(tr.reader, (rdCounts.get(tr.reader) || 0) + 1);
        });
        chSel.innerHTML = `<option value="">Tüm Risaleler (${p.clips.length})</option>` +
          [...chCounts.entries()].sort((a, b) => b[1] - a[1]).map(([k, v]) => `<option value="${escHtml(k)}">${escHtml(k)} (${v})</option>`).join('');
        rdSel.innerHTML = `<option value="">Tüm Okuyucular</option>` +
          [...rdCounts.entries()].sort((a, b) => b[1] - a[1]).map(([k, v]) => `<option value="${escHtml(k)}">${escHtml(k)} (${v})</option>`).join('');
        if (state.reader && rdCounts.has(state.reader)) rdSel.value = state.reader;
        fBar.classList.remove('hidden');
      } else {
        chSel.innerHTML = '<option value=""></option>'; rdSel.innerHTML = '<option value=""></option>';
        fBar.classList.add('hidden');
      }

      renderDetailItems();
      document.getElementById('detailFonBtn').onclick = () => { openDrawer(); openSettingsPanel('fon', 'Fon Müziği Stüdyosu (26 Fon)'); };
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    document.querySelectorAll('#detailRelSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.detailRelFilter = b.dataset.rel; state.detailLimit = 60; renderDetailItems(); };
    });
    document.getElementById('detailChapterFilter').onchange = () => { state.detailLimit = 60; renderDetailItems(); };
    document.getElementById('detailReaderFilter').onchange = () => { state.detailLimit = 60; renderDetailItems(); };
    document.getElementById('detailSearchFilter').oninput = () => { state.detailLimit = 60; renderDetailItems(); };
    document.getElementById('detailLoadMoreBtn').onclick = () => { state.detailLimit += 80; renderDetailItems(); };

    function setView(v) {
      state.view = v;
      const map = { topics: 'viewTopicsBtn', radio: 'viewRadioBtn', ai: 'viewAiBtn', all: 'viewAllBtn' };
      Object.entries(map).forEach(([k, id]) => document.getElementById(id).classList.toggle('active', k === v));
      document.getElementById('topicDetail').classList.add('hidden');
      document.getElementById('topicsSection').classList.toggle('hidden', v !== 'topics');
      document.getElementById('radioSection').classList.toggle('hidden', v !== 'radio');
      document.getElementById('aiSection').classList.toggle('hidden', v !== 'ai');
      document.getElementById('allTracksSection').classList.toggle('hidden', v !== 'all');
      if (v === 'topics') renderTopics();
      else if (v === 'radio') renderRadioSection();
      else if (v === 'ai') { updateAiEngineHint(); renderSavedUserLists(); }
      else renderAllTracks();
    }

    document.getElementById('viewTopicsBtn').onclick = () => setView('topics');
    document.getElementById('viewRadioBtn').onclick = () => setView('radio');
    document.getElementById('viewAiBtn').onclick = () => setView('ai');
    document.getElementById('viewAllBtn').onclick = () => setView('all');
    document.getElementById('brandHome').onclick = () => setView('topics');
    document.getElementById('detailBack').onclick = () => setView(state.view);
    document.getElementById('searchInput').oninput = () => {
      if (!document.getElementById('topicDetail').classList.contains('hidden')) setView(state.view);
      else if (state.view === 'topics') renderTopics();
      else if (state.view === 'all') renderAllTracks();
    };

    // =========================================================================
    // ADVANCED MULTI-PROVIDER AI & HİKMETLİ MICRO-SLICE BUILDER
    // =========================================================================
    const AI_PROVIDERS = {
      local:      { label: 'Yerel Semantik AI', url: '', model: '', needsKey: false, hint: 'Hiçbir API anahtarı gerektirmeden 2.373 Külliyat mikro-kesitini çevrimdışı tarar, Doğrudan/Dolaylı ayırır ve Hikmetli Sıra ile sunar.' },
      gemini:     { label: 'Google Gemini', url: 'https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent', model: 'gemini-2.5-flash', needsKey: true, hint: 'Google AI Studio üzerinden aldığınız Gemini API anahtarıyla (AIza...) doğrudan çalışır.' },
      openai:     { label: 'OpenAI', url: 'https://api.openai.com/v1/chat/completions', model: 'gpt-4o-mini', needsKey: true, hint: 'OpenAI API anahtarınızla (sk-...) yüksek hassasiyetli hikmetli liste ve künye üretir.' },
      groq:       { label: 'Groq', url: 'https://api.groq.com/openai/v1/chat/completions', model: 'llama-3.3-70b-versatile', needsKey: true, hint: 'Groq API anahtarıyla (gsk_...) saniyeler içinde hızlı ve yüksek kaliteli sıralama üretir.' },
      deepseek:   { label: 'DeepSeek', url: 'https://api.deepseek.com/v1/chat/completions', model: 'deepseek-chat', needsKey: true, hint: 'DeepSeek API anahtarıyla derin kavram analizi ve özel sıralama künyesi oluşturur.' },
      openrouter: { label: 'OpenRouter', url: 'https://openrouter.ai/api/v1/chat/completions', model: 'google/gemini-2.0-flash-001', needsKey: true, hint: 'OpenRouter anahtarıyla yüzlerce farklı modelden dilediğinizi seçebilirsiniz.' },
      custom:     { label: 'Özel Uç Nokta', url: 'http://localhost:11434/v1/chat/completions', model: 'llama3.1', needsKey: false, hint: 'Ollama, LM Studio veya kendi OpenAI-uyumlu yerel/uzak sunucunuzun adresini girebilirsiniz.' },
    };

    const SYNONYMS = {
      allah: ['tevhid','marifetullah','esma','burhan','pencere','halik','sani','rububiyet','vahdaniyet','kudret','ehadiyet'],
      namaz: ['ibadet','vakit','ubudiyet','usanc','vesvese','kulluk','secde','bes','abdest'],
      hasir: ['ahiret','dirilis','olum','cennet','cehennem','kiyamet','kabir','beka','ruh','bahar'],
      kader: ['irade','cuzi','ihtiyari','adalet','ser','musibet','tevekkul','imtihan','levh'],
      kuran: ['mucize','icaz','belagat','kelam','ayet','tefsir','vahiy','sule'],
      peygamber: ['nubuvvet','risalet','muhammed','mirac','resha','mucize','sunnet','sahabe'],
      teselli: ['sikinti','musibet','bela','sabir','rahmet','hastalik','dunya','kalp','sevecen'],
      ene: ['enaniyet','benlik','nefis','zerre','kibir','ubudiyet','emanet','vahid'],
      dua: ['munacat','niyaz','acz','fakr','sukur','icabet','dergah','iltica'],
      muhabbet: ['ask','sevgi','cemal','kemal','ihsan','vedud','esma'],
    };
    function normTr(s) {
      return (s || '').toLowerCase().replace(/İ/g,'i').replace(/ı/g,'i').replace(/ğ/g,'g').replace(/ü/g,'u')
        .replace(/ş/g,'s').replace(/ö/g,'o').replace(/ç/g,'c').replace(/[âîû]/g,'a').replace(/[^a-z0-9\s]/g,' ');
    }

    function updateAiEngineHint() {
      const pKey = state.aiCfg.provider || 'local';
      const pInfo = AI_PROVIDERS[pKey] || AI_PROVIDERS.local;
      const activeExt = pKey !== 'local' && (state.aiCfg.key || pKey === 'custom');
      const el = document.getElementById('aiEngineHint');
      if (el) {
        el.textContent = activeExt
          ? `Aktif Motor: ${pInfo.label} (${state.aiCfg.model || pInfo.model}) · Yalnızca doğrulanmış Külliyat kesitlerinden hikmetli sıralama ve künye üretir.`
          : 'Aktif Motor: Yerel Semantik AI (Çevrimdışı) · Harici yapay zeka (Gemini, OpenAI, Groq, DeepSeek) için sağdaki "API Ayarı" düğmesini kullanabilirsiniz.';
      }
    }

    function rankSegmentsLocally(query, mode = 'all') {
      const raw = normTr(query).split(/\s+/).filter(w => w.length >= 3);
      const directSet = new Set(raw);
      const indirectSet = new Set();
      raw.forEach(w => {
        Object.entries(SYNONYMS).forEach(([k, arr]) => {
          if (w === k || w.startsWith(k) || arr.some(x => w === x || w.startsWith(x))) {
            directSet.add(k);
            arr.forEach(syn => { if (!directSet.has(syn)) indirectSet.add(syn); });
          }
        });
      });

      const direct = [], indirect = [];
      state.segments.forEach((seg, segIdx) => {
        if (state.reader && trackMap.get(seg.trackId)?.reader !== state.reader) return;
        const hay = normTr(`${seg.title} ${seg.section} ${seg.excerpt} ${seg.fullText || ''}`);
        const words = hay.split(/\s+/);
        let dHits = 0, iHits = 0;
        const dMatch = [], iMatch = [];
        directSet.forEach(tok => {
          const hit = words.some(w => w.startsWith(tok));
          if (hit) { dHits++; dMatch.push(tok); }
        });
        indirectSet.forEach(tok => {
          const hit = words.some(w => w.startsWith(tok));
          if (hit) { iHits++; iMatch.push(tok); }
        });

        const baseObj = {
          segIdx, id: seg.id, trackId: seg.trackId, clipTitle: seg.title,
          startSec: seg.ss, endSec: seg.es, startRatio: seg.sr, endRatio: seg.er,
          estMin: seg.estMin || 2, excerpt: seg.excerpt, section: seg.section,
        };
        if (dHits >= 1) {
          const sc = dHits * 4.5 + iHits * 1.4;
          const kunye = `Doğrudan İzah — İsteğinizdeki '${dMatch.slice(0, 3).join(', ')}' kavramlarını ${seg.section} içinde doğrudan işlediği için ana akışa alındı.`;
          direct.push({ ...baseObj, score: sc, rel: 'direct', kunye });
        } else if (iHits >= 2 && mode !== 'direct') {
          const sc = iHits * 2.1;
          const kunye = `Destekleyici Bağlam — Doğrudan derslerin ardından konuyu '${iMatch.slice(0, 3).join(', ')}' hikmetiyle beslemek için ara geçiş olarak eklendi.`;
          indirect.push({ ...baseObj, score: sc, rel: 'indirect', kunye });
        }
      });

      const dedupe = arr => {
        arr.sort((a, b) => b.score - a.score);
        const out = [], seen = new Set();
        for (const s of arr) {
          const k = `${s.trackId}-${Math.round(s.startSec / 35)}`;
          const pk = `${s.section}-${normTr(s.excerpt).slice(0, 40)}`;
          if (seen.has(k) || seen.has(pk)) continue;
          seen.add(k); seen.add(pk); out.push(s);
        }
        return out;
      };

      const dClean = dedupe(direct);
      const iClean = dedupe(indirect);
      if (mode === 'direct') return dClean;

      // Hikmetli interleaving (2-3 Direct -> 1 Indirect)
      const merged = [];
      let di = 0, ii = 0;
      while (di < dClean.length || ii < iClean.length) {
        const blk = di === 0 ? 3 : 2;
        for (let k = 0; k < blk && di < dClean.length; k++) merged.push(dClean[di++]);
        if (ii < iClean.length) merged.push(iClean[ii++]);
        if (di >= dClean.length && ii < iClean.length) {
          while (ii < iClean.length && merged.length < 48) merged.push(iClean[ii++]);
          break;
        }
        if (merged.length >= 48) break;
      }
      return merged;
    }

    async function callExternalAi(systemPrompt, userPrompt) {
      const pKey = state.aiCfg.provider || 'local';
      const pInfo = AI_PROVIDERS[pKey] || AI_PROVIDERS.local;
      const key = (state.aiCfg.key || '').trim();
      const model = (state.aiCfg.model || pInfo.model || '').trim();
      if (pKey === 'local') return null;
      if (pInfo.needsKey && !key) return null;

      if (pKey === 'gemini') {
        const url = `https://generativelanguage.googleapis.com/v1beta/models/${encodeURIComponent(model || 'gemini-2.5-flash')}:generateContent?key=${encodeURIComponent(key)}`;
        const resp = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            systemInstruction: { parts: [{ text: systemPrompt }] },
            contents: [{ role: 'user', parts: [{ text: userPrompt }] }],
            generationConfig: { responseMimeType: 'application/json', temperature: 0.3 },
          }),
        });
        if (!resp.ok) throw new Error(`Gemini HTTP ${resp.status}`);
        const j = await resp.json();
        return j.candidates?.[0]?.content?.parts?.[0]?.text || '';
      }

      const url = (state.aiCfg.url || pInfo.url || '').trim();
      if (!url) return null;
      const headers = { 'Content-Type': 'application/json' };
      if (key) headers['Authorization'] = `Bearer ${key}`;
      if (pKey === 'openrouter') headers['X-Title'] = 'Risale Dinle';

      const resp = await fetch(url, {
        method: 'POST',
        headers,
        body: JSON.stringify({
          model: model || 'gpt-4o-mini',
          response_format: { type: 'json_object' },
          temperature: 0.3,
          messages: [
            { role: 'system', content: systemPrompt },
            { role: 'user', content: userPrompt },
          ],
        }),
      });
      if (!resp.ok) throw new Error(`${pInfo.label} HTTP ${resp.status}`);
      const j = await resp.json();
      return j.choices?.[0]?.message?.content || '';
    }

    function parseJsonLoose(raw) {
      if (!raw) return {};
      const cleaned = raw.replace(/^```json\s*/i, '').replace(/```\s*$/i, '').trim();
      try { return JSON.parse(cleaned); }
      catch (e) {
        const m = cleaned.match(/\{[\s\S]*\}/);
        return m ? JSON.parse(m[0]) : {};
      }
    }

    async function runAiGenerator() {
      const query = document.getElementById('aiPromptInput').value.trim();
      const targetMins = parseInt(document.getElementById('aiDurationSelect').value, 10) || 30;
      const mode = document.getElementById('aiModeSelect').value || 'all';
      if (!query) return;

      const btn = document.getElementById('runAiBtn');
      const oldTxt = btn.textContent;
      btn.textContent = 'Hazırlanıyor...'; btn.disabled = true;

      try {
        const cands = rankSegmentsLocally(query, mode).slice(0, 42);
        if (!cands.length) { showToast('Uygun kesit bulunamadı'); return; }

        let selected = [], title = query.slice(0, 58), suggestedFon = '', engineUsed = 'Yerel Semantik AI';
        const pKey = state.aiCfg.provider || 'local';
        if (pKey !== 'local' && (state.aiCfg.key || pKey === 'custom')) {
          try {
            const sys = 'Sen Risale-i Nur dinleme küratörüsün. YALNIZCA verilen aday ID listesinden seçim yap. Kesitleri hikmetli bir tefekkür sırasıyla diz; her biri için rel ("direct" veya "indirect") ve kısa Türkçe kunye ("neden bu sırada?") yaz. Ayrıca şu fon anahtarlarından en uygun birini öner: ussak, hicaz, rast, saba, nihavend, segah, huseyni, kurdili, yagmur, kainat, seher_demi. Sadece JSON döndür: {"title":"...","fon":"ussak","items":[{"id":"...","rel":"direct","kunye":"..."}]}';
            const usr = `İstek: "${query}" | Hedef süre: ${targetMins} dk | Mod: ${mode === 'direct' ? 'Sadece Doğrudan' : 'Hikmetli Sıra (Doğrudan + Dolaylı)'}\nAdaylar: ${JSON.stringify(cands.map(c => ({ id: c.id, t: c.clipTitle, ex: c.excerpt.slice(0, 110), m: c.estMin })))}`;
            const raw = await callExternalAi(sys, usr);
            const parsed = parseJsonLoose(raw);
            const m = new Map(cands.map(c => [c.id, c]));
            if (Array.isArray(parsed.items)) {
              selected = parsed.items.map(it => {
                const base = m.get(it.id);
                if (!base) return null;
                return { ...base, rel: it.rel === 'indirect' ? 'indirect' : 'direct', kunye: it.kunye || base.kunye };
              }).filter(Boolean);
            } else if (Array.isArray(parsed.ids)) {
              selected = parsed.ids.map(id => m.get(id)).filter(Boolean);
            }
            if (parsed.title) title = parsed.title;
            if (parsed.fon && FON_LABELS[parsed.fon]) suggestedFon = parsed.fon;
            if (selected.length) engineUsed = AI_PROVIDERS[pKey]?.label || 'Harici AI';
          } catch (e) {
            showToast('Harici API erişilemedi, Yerel AI kullanıldı');
          }
        }

        if (!selected.length) {
          let mins = 0;
          for (const c of cands) { if (mins >= targetMins && selected.length >= 3) break; selected.push(c); mins += c.estMin; }
        }

        const totalMin = selected.reduce((a, b) => a + (b.estMin || 2), 0);
        const dCnt = selected.filter(c => c.rel === 'direct').length;
        const iCnt = selected.length - dCnt;
        const fonNote = suggestedFon ? ` · Önerilen Fon: ${FON_LABELS[suggestedFon]}` : '';

        state.lastAiResult = {
          id: 'usr-' + Date.now(),
          title,
          description: `${selected.length} kesit (${dCnt} doğrudan${iCnt ? ' · ' + iCnt + ' dolaylı' : ''}) · ~${totalMin} dk · ${engineUsed}${fonNote}`,
          clips: selected,
          curatedCount: selected.length,
          directCount: dCnt,
          indirectCount: iCnt,
          suggestedFon,
        };

        if (suggestedFon && state.aiCfg.autoFon) {
          state.fonEnabled = true; state.fonMode = 'synth'; state.fonPreset = suggestedFon;
          syncMixerAndFonUI(); syncFonPlayback(); saveState();
        }

        document.getElementById('aiResultTitle').textContent = title;
        document.getElementById('aiResultNote').textContent = state.lastAiResult.description;
        const box = document.getElementById('aiResultTracks'); box.innerHTML = '';
        selected.forEach((c, idx) => {
          const tr = trackMap.get(c.trackId); if (!tr) return;
          const relHtml = c.rel ? `<span class="rel-tag ${c.rel === 'direct' ? 'direct' : ''}">${c.rel === 'direct' ? 'Doğrudan' : 'Dolaylı'}</span><span>· ${fmtTime(c.startSec)}–${fmtTime(c.endSec)}</span>` : `<span>${fmtTime(c.startSec)}–${fmtTime(c.endSec)}</span>`;
          const row = makeCalmRow(c.clipTitle, c.excerpt, relHtml, () => playQueue(selected, idx, title, state.lastAiResult.id), false, true, c.kunye || '');
          row.dataset.tid = tr.id; box.appendChild(row);
        });
        document.getElementById('aiResultBox').classList.remove('hidden');
      } finally {
        btn.textContent = oldTxt; btn.disabled = false;
      }
    }

    document.getElementById('runAiBtn').onclick = runAiGenerator;
    document.getElementById('aiPromptInput').onkeydown = (e) => { if (e.key === 'Enter') runAiGenerator(); };
    document.getElementById('openAiApiBtn').onclick = () => { openDrawer(); openSettingsPanel('ai', 'Yapay Zeka ve API Anahtarları'); };
    document.getElementById('aiPlayBtn').onclick = () => { if (state.lastAiResult) playQueue(state.lastAiResult.clips, 0, state.lastAiResult.title, state.lastAiResult.id); };
    document.getElementById('aiSaveBtn').onclick = () => {
      if (!state.lastAiResult) return;
      if (!state.userPlaylists.some(p => p.id === state.lastAiResult.id)) {
        state.userPlaylists.unshift(state.lastAiResult);
      }
      saveState(); renderSavedUserLists(); populatePlaylistSelectors(); showToast('AI listesi kaydedildi');
    };
    document.getElementById('aiToRadioBtn').onclick = () => {
      if (!state.lastAiResult) return;
      if (!state.userPlaylists.some(p => p.id === state.lastAiResult.id)) {
        state.userPlaylists.unshift(state.lastAiResult);
      }
      state.radio.blocks.push({
        id: 'rb-ai-' + Date.now(),
        title: state.lastAiResult.title.slice(0, 44),
        start: '12:00', end: '13:00', days: [0,1,2,3,4,5,6],
        source: 'user:' + state.lastAiResult.id,
        filter: 'all',
        fon: state.lastAiResult.suggestedFon || 'ussak',
        fonVol: 2.5, speed: 1, reader: '', enabled: true,
      });
      saveState(); renderSavedUserLists(); populatePlaylistSelectors();
      showToast('Radyo Akışına yeni blok eklendi');
      setView('radio');
    };

    function renderSavedUserLists() {
      const wrap = document.getElementById('savedUserListsWrap');
      if (!state.userPlaylists.length) { wrap.innerHTML = ''; return; }
      wrap.innerHTML = `<div class="hint" style="margin-bottom:8px;">Kayıtlı Özel Listelerim (${state.userPlaylists.length})</div><div class="calm-list" id="uListBox"></div>`;
      const box = document.getElementById('uListBox');
      state.userPlaylists.forEach((p, idx) => {
        const row = makeCalmRow(p.title, p.description, `<span>${p.clips.length} kesit</span>`, () => openDetail(p));
        const delBtn = document.createElement('button'); delBtn.className = 'tiny-link'; delBtn.textContent = 'Sil';
        delBtn.onclick = (e) => { e.stopPropagation(); state.userPlaylists.splice(idx, 1); saveState(); renderSavedUserLists(); populatePlaylistSelectors(); showToast('Liste silindi'); };
        row.querySelector('.item-action').prepend(delBtn);
        row.querySelector('.circle-play').onclick = (e) => { e.stopPropagation(); playQueue(p.clips, 0, p.title, p.id); };
        box.appendChild(row);
      });
    }

    // =========================================================================
    // COHESIVE, SIMPLE YET RICH 2-LAYER SETTINGS STUDIO
    // =========================================================================
    const drawer = document.getElementById('settingsDrawer');
    const backdrop = document.getElementById('drawerBackdrop');
    const ALL_PANELS = ['ses', 'fon', 'kuran', 'ai', 'kayitlar', 'kesit', 'oto', 'sistem'];

    function applyOneClickProfile(name) {
      if (name === 'saf') {
        state.fonEnabled = false; state.speed = 1; state.silenceGapSec = 0; state.theme = 'light';
        state.defaultRelFilter = 'all'; showToast('Profil: Saf Dinleme');
      } else if (name === 'tefekkur') {
        state.fonEnabled = true; state.fonMode = 'synth'; state.fonPreset = 'ussak';
        state.risaleUnit = 10; state.fonUnit = 2.5; state.speed = 0.9; state.silenceGapSec = 5;
        state.duckEnabled = true; state.duckBesmele = true; state.theme = 'sepia';
        showToast('Profil: Derin Tefekkür');
      } else if (name === 'gece') {
        state.fonEnabled = true; state.fonMode = 'synth'; state.fonPreset = 'hicaz';
        state.risaleUnit = 9.5; state.fonUnit = 2.0; state.speed = 0.9; state.silenceGapSec = 5;
        state.theme = 'dark'; document.getElementById('sleepSelect').value = '30';
        document.getElementById('sleepSelect').onchange({ target: document.getElementById('sleepSelect') });
        showToast('Profil: Gece Huzuru (30 dk uyku)');
      } else if (name === 'hizli') {
        state.fonEnabled = false; state.speed = 1.25; state.silenceGapSec = 0;
        state.defaultRelFilter = 'direct'; showToast('Profil: Hızlı Tekrar (Doğrudan · 1.25x)');
      }
      audio.playbackRate = state.speed;
      applyTheme(state.theme);
      syncAllSettingsUI();
      syncFonPlayback();
      updateSettingsSummaries();
      saveState();
    }
    document.querySelectorAll('[data-profile]').forEach(b => { b.onclick = () => applyOneClickProfile(b.dataset.profile); });

    function updateSettingsSummaries() {
      document.getElementById('sum-ses').textContent = `Risale ${state.risaleUnit} · Fon ${state.fonUnit} · ${state.speed}x · ${state.silenceGapSec}s ara`;
      const fLbl = state.fonMode === 'playlist' ? 'MP3 Listem' : (state.fonMode === 'cycle' ? 'Makam Döngüsü' : (FON_LABELS[state.fonPreset] || state.fonPreset));
      document.getElementById('sum-fon').textContent = state.fonEnabled ? `Açık · ${fLbl}` : 'Kapalı · 26 Ney/Tabiat/Odak Fonu & MP3';
      document.getElementById('sum-kuran').textContent = `${state.preRollSec} sn önce kapat · Açılış ${state.openingMuteSec} sn`;
      const pKey = state.aiCfg.provider || 'local';
      const pInfo = AI_PROVIDERS[pKey] || AI_PROVIDERS.local;
      document.getElementById('sum-ai').textContent = (pKey !== 'local' && (state.aiCfg.key || pKey === 'custom'))
        ? `${pInfo.label} · ${state.aiCfg.model || pInfo.model}`
        : 'Yerel Semantik AI · Harici API eklenebilir';
      document.getElementById('sum-kayitlar').textContent = `${state.bookmarks.length} yer imi · Bugün ${fmtMinText(state.listenStats.todaySec)}`;
      document.getElementById('sum-kesit').textContent = `${state.defaultRelFilter === 'direct' ? 'Doğrudan' : 'Hikmetli'} · Künye ${state.showKunye ? 'açık' : 'gizli'}`;
      document.getElementById('sum-oto').textContent = `${state.schedules.length} zamanlayıcı · ${state.hotkeys.length} özel tuş`;
      const thNames = { light: 'Parşömen', sepia: 'Sepya', dark: 'Gece' };
      document.getElementById('sum-sistem').textContent = `${thNames[state.theme] || 'Parşömen'} tema · JSON Yedekleme`;
    }

    function showSettingsMenu() {
      document.getElementById('drawerTitle').textContent = 'Ayarlar';
      document.getElementById('drawerBackBtn').classList.add('hidden');
      document.getElementById('layer-menu').classList.remove('hidden');
      ALL_PANELS.forEach(p => document.getElementById('panel-' + p).classList.add('hidden'));
      updateSettingsSummaries();
    }

    function openSettingsPanel(key, title) {
      document.getElementById('drawerTitle').textContent = title;
      document.getElementById('drawerBackBtn').classList.remove('hidden');
      document.getElementById('layer-menu').classList.add('hidden');
      ALL_PANELS.forEach(p => document.getElementById('panel-' + p).classList.toggle('hidden', p !== key));
      if (key === 'fon') renderFonStudio();
      if (key === 'kuran') renderQuranRangesUI();
      if (key === 'ai') syncAiSettingsUI();
      if (key === 'kayitlar') renderKayitlarPanel();
      if (key === 'kesit') renderActiveTrackSegmentsUI();
      if (key === 'oto') { renderHotkeys(); renderSchedules(); }
      if (key === 'sistem') syncThemeCards();
    }

    function openDrawer() { drawer.classList.add('open'); backdrop.classList.add('open'); showSettingsMenu(); }
    function closeDrawer() { drawer.classList.remove('open'); backdrop.classList.remove('open'); state.capturingKey = false; }

    document.getElementById('openSettingsBtn').onclick = openDrawer;
    document.getElementById('closeSettingsBtn').onclick = closeDrawer;
    document.getElementById('drawerBackBtn').onclick = showSettingsMenu;
    backdrop.onclick = closeDrawer;
    document.querySelectorAll('.cat-row').forEach(r => {
      r.onclick = () => openSettingsPanel(r.dataset.panel, r.querySelector('.cat-row-title').textContent);
    });

    // Mixer & Segmented Settings Controls
    function syncMixerAndFonUI() {
      document.getElementById('risaleVolRange').value = state.risaleUnit;
      document.getElementById('risaleUnitLabel').textContent = `${state.risaleUnit} / 10`;
      document.getElementById('fonVolRange').value = state.fonUnit;
      document.getElementById('fonUnitLabel').textContent = `${state.fonUnit} / 10`;
      const sum = Math.max(0.1, state.risaleUnit + state.fonUnit);
      document.getElementById('balanceRisale').style.width = `${(state.risaleUnit / sum) * 100}%`;
      document.getElementById('balanceFon').style.width = `${(state.fonUnit / sum) * 100}%`;
      document.getElementById('fonEnabledChk').checked = state.fonEnabled;
      document.querySelectorAll('#fonModeSeg .seg-btn').forEach(b => b.classList.toggle('active', b.dataset.fmode === state.fonMode));
      document.getElementById('fonPresetSelect').value = state.fonPreset;
      audio.volume = Math.max(0, Math.min(1, state.risaleUnit / 10));
      if (!isFonMutedNow()) applyFonGain(getFonGain(), 0.15);
    }
    function syncSpeedUI() {
      document.querySelectorAll('#speedSeg .seg-btn').forEach(b => b.classList.toggle('active', parseFloat(b.dataset.spd) === state.speed));
    }
    function syncGapUI() {
      document.querySelectorAll('#gapSeg .seg-btn').forEach(b => b.classList.toggle('active', Number(b.dataset.gap) === state.silenceGapSec));
      document.querySelectorAll('#radioGapSeg .seg-btn').forEach(b => b.classList.toggle('active', Number(b.dataset.gap) === state.silenceGapSec));
    }
    function syncSkipUI() {
      document.querySelectorAll('#skipSeg .seg-btn').forEach(b => b.classList.toggle('active', Number(b.dataset.skip) === state.skipStepSec));
    }
    function syncFocusFont() {
      const sz = state.focusFontSize === 'sm' ? '1.16rem' : (state.focusFontSize === 'lg' ? '1.66rem' : '1.38rem');
      document.documentElement.style.setProperty('--focus-font', sz);
      document.querySelectorAll('#fontSizeSeg .seg-btn').forEach(b => b.classList.toggle('active', b.dataset.fsize === state.focusFontSize));
    }
    function applyTheme(t) {
      state.theme = t;
      document.documentElement.setAttribute('data-theme', t);
      syncThemeCards();
    }
    function syncAllSettingsUI() {
      syncMixerAndFonUI(); syncSpeedUI(); syncGapUI(); syncSkipUI(); syncRepeatShuffleUI(); syncFocusFont(); syncThemeCards();
      document.querySelectorAll('#defaultRelSeg .seg-btn').forEach(b => b.classList.toggle('active', b.dataset.defrel === state.defaultRelFilter));
      document.getElementById('showKunyeChk').checked = state.showKunye;
      document.getElementById('crossfadeChk').checked = state.crossfade;
      document.getElementById('sliceModeChk').checked = state.sliceMode;
      document.getElementById('autoResumeChk').checked = state.autoResume;
      document.getElementById('autoNextChk').checked = state.autoNext;
      document.getElementById('autoStartOpenChk').checked = state.autoStartOpen;
      document.getElementById('duckEnabledChk').checked = state.duckEnabled;
      document.getElementById('duckBesmeleChk').checked = state.duckBesmele;
      document.getElementById('openingMuteSelect').value = String(state.openingMuteSec);
      document.getElementById('preRollSelect').value = String(state.preRollSec);
      document.getElementById('postRollSelect').value = String(state.postRollSec);
    }

    document.getElementById('risaleVolRange').oninput = (e) => { cancelMainFade(); state.risaleUnit = parseFloat(e.target.value); syncMixerAndFonUI(); saveState(); };
    document.getElementById('fonVolRange').oninput = (e) => { state.fonUnit = parseFloat(e.target.value); syncMixerAndFonUI(); saveState(); };
    document.querySelectorAll('[data-mix]').forEach(b => {
      b.onclick = () => {
        const [r, f] = b.dataset.mix.split(',').map(Number);
        state.risaleUnit = r; state.fonUnit = f;
        if (f > 0 && !state.fonEnabled) { state.fonEnabled = true; syncFonPlayback(); }
        syncMixerAndFonUI(); saveState(); showToast(`Mikser ${r}:${f}`);
      };
    });
    document.querySelectorAll('#speedSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.speed = parseFloat(b.dataset.spd); audio.playbackRate = state.speed; syncSpeedUI(); saveState(); };
    });
    document.querySelectorAll('#repeatSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.repeatMode = b.dataset.rep; syncRepeatShuffleUI(); saveState(); };
    });
    document.querySelectorAll('#gapSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.silenceGapSec = Number(b.dataset.gap); syncGapUI(); saveState(); };
    });
    document.querySelectorAll('#skipSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.skipStepSec = Number(b.dataset.skip); syncSkipUI(); saveState(); };
    });
    document.querySelectorAll('#defaultRelSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.defaultRelFilter = b.dataset.defrel; syncAllSettingsUI(); saveState(); };
    });
    document.querySelectorAll('#fontSizeSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.focusFontSize = b.dataset.fsize; syncFocusFont(); saveState(); };
    });
    document.getElementById('showKunyeChk').onchange = (e) => {
      state.showKunye = e.target.checked; saveState();
      if (!document.getElementById('topicDetail').classList.contains('hidden')) renderDetailItems();
    };
    document.getElementById('readerFilter').onchange = (e) => {
      state.reader = e.target.value;
      if (state.view === 'all') renderAllTracks();
      if (!document.getElementById('topicDetail').classList.contains('hidden')) {
        const rd = document.getElementById('detailReaderFilter');
        if ([...rd.options].some(o => o.value === state.reader)) rd.value = state.reader;
        renderDetailItems();
      }
    };
    document.getElementById('crossfadeChk').onchange = (e) => { state.crossfade = e.target.checked; saveState(); };
    document.getElementById('autoResumeChk').onchange = (e) => { state.autoResume = e.target.checked; saveState(); };
    document.getElementById('autoNextChk').onchange = (e) => { state.autoNext = e.target.checked; saveState(); };
    document.getElementById('sliceModeChk').onchange = (e) => { state.sliceMode = e.target.checked; saveState(); renderTimelineTicks(); updatePlayerSubtext(); };

    // Gentle 15-second Sleep Timer Fade-Out
    document.getElementById('sleepSelect').onchange = (e) => {
      const val = parseInt(e.target.value, 10);
      clearTimeout(state.sleepTimerId); clearTimeout(state.sleepFadeTimerId); state.sleepAtEnd = false;
      if (val === -1) { state.sleepAtEnd = true; showToast('Parça sonunda duracak'); }
      else if (val > 0) {
        const ms = val * 60000;
        const fadeLead = Math.min(15000, ms / 2);
        state.sleepFadeTimerId = setTimeout(() => { fadeMainAudio(0.01, fadeLead); applyFonGain(0, fadeLead / 1000); }, Math.max(0, ms - fadeLead));
        state.sleepTimerId = setTimeout(() => {
          audio.pause(); state.radioRunning = false;
          audio.volume = Math.max(0, Math.min(1, state.risaleUnit / 10));
          e.target.value = '0'; showToast('Uyku zamanlayıcısı tamamlandı');
        }, ms);
        showToast(`Uyku zamanlayıcısı: ${val} dk`);
      }
    };

    // Dinleme Kayıtlarım Panel
    function renderKayitlarPanel() {
      document.getElementById('statTodayMin').textContent = fmtMinText(state.listenStats.todaySec);
      document.getElementById('statTotalMin').textContent = fmtMinText(state.listenStats.totalSec);
      const bBox = document.getElementById('settingsBookmarksBox'); bBox.innerHTML = '';
      if (!state.bookmarks.length) { bBox.innerHTML = '<p class="hint">Henüz yer imi kaydedilmedi.</p>'; }
      else {
        state.bookmarks.slice(0, 25).forEach((bm, idx) => {
          const div = document.createElement('div'); div.className = 'mini-item'; div.style.cursor = 'pointer';
          div.innerHTML = `<div style="min-width:0;flex:1;"><div style="font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${fmtTime(bm.sec)} · ${escHtml(bm.note || bm.title)}</div><div class="hint" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${escHtml(bm.title)}</div></div><button class="btn-ghost" data-del="${idx}" style="padding:2px 6px;font-size:0.7rem;">✕</button>`;
          div.onclick = () => { playQueue([{ trackId: bm.trackId }], 0, bm.note || bm.title, null, bm.sec); closeDrawer(); };
          div.querySelector('[data-del]').onclick = (e) => { e.stopPropagation(); state.bookmarks.splice(idx, 1); saveState(); renderKayitlarPanel(); };
          bBox.appendChild(div);
        });
      }
      const hBox = document.getElementById('settingsHistoryBox'); hBox.innerHTML = '';
      if (!state.history.length) { hBox.innerHTML = '<p class="hint">Henüz dinlenen ders yok.</p>'; }
      else {
        state.history.slice(0, 12).forEach(h => {
          const div = document.createElement('div'); div.className = 'mini-item'; div.style.cursor = 'pointer';
          div.innerHTML = `<span style="overflow:hidden;text-overflow:ellipsis;white-space:nowrap;flex:1;">${escHtml(h.title)}</span><span class="hint">${fmtTime(h.sec)}</span>`;
          div.onclick = () => { playQueue([h.clip || { trackId: h.trackId }], 0, h.title, h.playlistId || null, h.sec); closeDrawer(); };
          hBox.appendChild(div);
        });
      }
    }
    document.getElementById('settingsAddBookmarkBtn').onclick = () => { const inp = document.getElementById('settingsBookmarkNote'); addBookmark(inp.value); inp.value = ''; };

    // Fon Studio
    document.getElementById('fonEnabledChk').onchange = (e) => { state.fonEnabled = e.target.checked; saveState(); syncFonPlayback(); };
    document.querySelectorAll('#fonModeSeg .seg-btn').forEach(b => {
      b.onclick = () => { state.fonMode = b.dataset.fmode; syncMixerAndFonUI(); saveState(); syncFonPlayback(); };
    });
    document.getElementById('fonPresetSelect').onchange = (e) => { state.fonPreset = e.target.value; saveState(); if (state.fonEnabled || state.fonPreviewing) startSynth(); };
    document.getElementById('previewFonBtn').onclick = () => { state.fonPreviewing = !state.fonPreviewing; syncFonPlayback(); showToast(state.fonPreviewing ? 'Fon test ediliyor' : 'Durduruldu'); };
    document.getElementById('bindFonPlaylistBtn').onclick = () => {
      if (!state.activePlaylistId) { showToast('Önce bir konu veya liste açın'); return; }
      state.playlistFonMap[state.activePlaylistId] = { enabled: state.fonEnabled, mode: state.fonMode, preset: state.fonPreset, fonUnit: state.fonUnit };
      saveState(); showToast('Aktif listeye bağlandı');
    };
    document.getElementById('fonFileInput').onchange = async (e) => {
      const files = [...e.target.files];
      for (const f of files) {
        const id = 'idb-' + Date.now() + '-' + Math.random().toString(36).slice(2, 6);
        await saveFonBlob(id, f);
        state.fonPlaylist.push({ id, title: f.name.replace(/\.[^.]+$/, ''), url: URL.createObjectURL(f), repeats: 2, isIdb: true });
      }
      state.fonMode = 'playlist'; state.fonEnabled = true;
      saveState(); syncMixerAndFonUI(); renderFonStudio(); syncFonPlayback();
      showToast(`${files.length} fon yüklendi`);
    };
    document.getElementById('addFonUrlBtn').onclick = () => {
      const title = document.getElementById('fonUrlTitle').value.trim() || 'Özel Fon';
      const url = document.getElementById('fonUrlInput').value.trim();
      if (!url.startsWith('http')) return;
      state.fonPlaylist.push({ id: 'fu-' + Date.now(), title, url, repeats: 2, isIdb: false });
      state.fonMode = 'playlist'; state.fonEnabled = true;
      document.getElementById('fonUrlTitle').value = ''; document.getElementById('fonUrlInput').value = '';
      saveState(); syncMixerAndFonUI(); renderFonStudio(); syncFonPlayback();
    };
    function renderFonStudio() {
      syncMixerAndFonUI();
      const box = document.getElementById('fonPlaylistBox');
      if (!state.fonPlaylist.length) { box.innerHTML = '<p class="hint">Henüz özel MP3 fon yüklenmedi.</p>'; return; }
      box.innerHTML = '';
      state.fonPlaylist.forEach((f, idx) => {
        const div = document.createElement('div'); div.className = 'mini-item';
        div.innerHTML = `<span style="overflow:hidden;text-overflow:ellipsis;white-space:nowrap;max-width:190px;">${idx+1}. ${escHtml(f.title)}</span>
          <div style="display:flex;gap:4px;"><select data-rep="${idx}" style="padding:2px 4px;font-size:0.74rem;"><option value="1" ${f.repeats===1?'selected':''}>1x</option><option value="2" ${f.repeats===2?'selected':''}>2x</option><option value="3" ${f.repeats===3?'selected':''}>3x</option><option value="0" ${f.repeats===0?'selected':''}>∞</option></select>
          <button class="btn-ghost" data-del="${idx}" style="padding:2px 6px;font-size:0.7rem;">✕</button></div>`;
        div.querySelector('[data-rep]').onchange = (e) => { state.fonPlaylist[idx].repeats = parseInt(e.target.value, 10); saveState(); };
        div.querySelector('[data-del]').onclick = () => { const r = state.fonPlaylist.splice(idx, 1)[0]; if (r?.isIdb) deleteFonBlob(r.id); saveState(); renderFonStudio(); };
        box.appendChild(div);
      });
    }

    // Quran & Besmele
    document.getElementById('duckEnabledChk').onchange = (e) => { state.duckEnabled = e.target.checked; saveState(); renderTimelineTicks(); };
    document.getElementById('duckBesmeleChk').onchange = (e) => { state.duckBesmele = e.target.checked; saveState(); renderTimelineTicks(); };
    document.getElementById('openingMuteSelect').onchange = (e) => { state.openingMuteSec = parseInt(e.target.value, 10); saveState(); renderTimelineTicks(); };
    document.getElementById('preRollSelect').onchange = (e) => { state.preRollSec = parseFloat(e.target.value); saveState(); };
    document.getElementById('postRollSelect').onchange = (e) => { state.postRollSec = parseFloat(e.target.value); saveState(); };
    function toggleManualMute() {
      state.manualMute = !state.manualMute;
      applyFonGain(isFonMutedNow() ? 0 : getFonGain(), 0.4);
      document.getElementById('manualMuteToggleBtn').textContent = state.manualMute ? 'Fon Susturmayı Kaldır (M)' : 'Anlık Manuel Fon Sustur (M)';
      updatePlayerSubtext();
      showToast(state.manualMute ? 'Fon sessize alındı' : 'Fon açıldı');
    }
    document.getElementById('manualMuteToggleBtn').onclick = toggleManualMute;
    document.getElementById('markQuranStartBtn').onclick = () => { if (state.currentTrackId) { state.markStartSec = Math.max(0, audio.currentTime); showToast(`Başlangıç: ${fmtTime(state.markStartSec)}`); } };
    document.getElementById('markQuranEndBtn').onclick = () => {
      if (!state.currentTrackId || state.markStartSec === null) return;
      state.customQuranMarks[state.currentTrackId] = state.customQuranMarks[state.currentTrackId] || [];
      state.customQuranMarks[state.currentTrackId].push({ ss: state.markStartSec, es: Math.max(state.markStartSec + 2, audio.currentTime), kind: 'ayet', label: 'İşaretlenen Ayet' });
      state.markStartSec = null; saveState(); renderTimelineTicks(); renderQuranRangesUI(); showToast('Ayet aralığı kaydedildi');
    };
    function renderQuranRangesUI() {
      const box = document.getElementById('activeQuranRangesBox');
      if (!state.currentTrackId) { box.innerHTML = '<p class="hint">Aktif ders seçildiğinde ayet/besmele zamanları burada görünür.</p>'; return; }
      const ranges = getResolvedQuranRanges(state.currentTrackId, audio.duration || 900);
      box.innerHTML = '';
      ranges.forEach(r => {
        const div = document.createElement('div'); div.className = 'mini-item'; div.style.cursor = 'pointer';
        div.innerHTML = `<span>${fmtTime(r.ss)}–${fmtTime(r.es)} · ${escHtml(r.label.slice(0, 32))}</span>`;
        div.onclick = () => { audio.currentTime = Math.max(0, r.ss - state.preRollSec); };
        box.appendChild(div);
      });
    }

    // Clip Cutter & Segments
    document.getElementById('clipSetStartBtn').onclick = () => { state.clipStartSec = Math.floor(audio.currentTime || 0); document.getElementById('clipStartLbl').textContent = fmtTime(state.clipStartSec); };
    document.getElementById('clipSetEndBtn').onclick = () => { state.clipEndSec = Math.floor(audio.currentTime || 0); document.getElementById('clipEndLbl').textContent = fmtTime(state.clipEndSec); };
    document.getElementById('saveCustomClipBtn').onclick = () => {
      if (!state.currentTrackId || !audio.duration) return;
      const endS = state.clipEndSec && state.clipEndSec > state.clipStartSec ? state.clipEndSec : Math.min(audio.duration, state.clipStartSec + 90);
      const title = document.getElementById('clipTitleInput').value.trim() || `${trackMap.get(state.currentTrackId).title} (${fmtTime(state.clipStartSec)}–${fmtTime(endS)})`;
      let myList = state.userPlaylists.find(p => p.id === 'my-custom-clips');
      if (!myList) { myList = { id: 'my-custom-clips', title: 'Kendi Kestiğim Kesitler', description: 'Özel kesitler', clips: [], curatedCount: 0 }; state.userPlaylists.unshift(myList); }
      myList.clips.push({ trackId: state.currentTrackId, clipTitle: title, startSec: state.clipStartSec, endSec: endS, startRatio: state.clipStartSec / audio.duration, endRatio: endS / audio.duration, excerpt: `${fmtTime(state.clipStartSec)}–${fmtTime(endS)}` });
      myList.curatedCount = myList.clips.length;
      document.getElementById('clipTitleInput').value = ''; saveState(); populatePlaylistSelectors(); showToast('Kesit kaydedildi');
    };
    function renderActiveTrackSegmentsUI() {
      const box = document.getElementById('activeTrackSegmentsBox');
      const tr = trackMap.get(state.currentTrackId);
      if (!tr || !tr.segments?.length) { box.innerHTML = '<p class="hint">Aktif ders seçildiğinde saniye-bölümleri görünür.</p>'; return; }
      box.innerHTML = '';
      tr.segments.forEach(s => {
        const div = document.createElement('div'); div.className = 'mini-item'; div.style.cursor = 'pointer';
        div.innerHTML = `<span>${fmtTime(s.ss)}–${fmtTime(s.es)} · ${escHtml(s.t)}</span>`;
        div.onclick = () => playQueue([{ trackId: tr.id, clipTitle: s.t, startSec: s.ss, endSec: s.es, startRatio: s.sr, endRatio: s.er, excerpt: s.ex }], 0, s.t);
        box.appendChild(div);
      });
    }

    // Hotkeys & Schedules & Live Radio Clock Watcher
    let selectedDays = new Set([0,1,2,3,4,5,6]);
    function populatePlaylistSelectors() {
      const opts = [];
      state.userPlaylists.forEach(p => opts.push({ val: 'user:' + p.id, label: `[Özel] ${p.title}` }));
      state.playlists.forEach(p => opts.push({ val: 'topic:' + p.id, label: `[Fihrist] ${p.title}` }));
      ['schedPlaylist', 'hkTargetPlaylist'].forEach(id => {
        const sel = document.getElementById(id); sel.innerHTML = '';
        opts.forEach(o => { const el = document.createElement('option'); el.value = o.val; el.textContent = o.label; sel.appendChild(el); });
      });
    }
    function launchByTarget(target, withFon = false) {
      if (withFon) { state.fonEnabled = true; syncMixerAndFonUI(); }
      const [kind, id] = target.split(':');
      const pool = kind === 'user' ? state.userPlaylists : state.playlists;
      const p = pool.find(x => x.id === id);
      if (p) playQueue(p.clips || p.trackIds, 0, p.title, p.id);
    }
    document.getElementById('hkActionSelect').onchange = (e) => document.getElementById('hkTargetPlaylist').classList.toggle('hidden', e.target.value !== 'play_list');
    document.getElementById('captureKeyBtn').onclick = () => { state.capturingKey = true; document.getElementById('captureKeyBtn').textContent = 'Şimdi bir tuşa basın...'; };
    window.addEventListener('keydown', (e) => {
      if (state.capturingKey) {
        if (['Control','Shift','Alt','Meta'].includes(e.key)) return;
        e.preventDefault(); state.capturingKey = false; document.getElementById('captureKeyBtn').textContent = 'Tuş Ata';
        const parts = []; if (e.ctrlKey) parts.push('Ctrl'); if (e.altKey) parts.push('Alt'); if (e.shiftKey) parts.push('Shift');
        parts.push(e.key.length === 1 ? e.key.toUpperCase() : e.key);
        const keyLabel = parts.join('+');
        const action = document.getElementById('hkActionSelect').value;
        const target = document.getElementById('hkTargetPlaylist').value;
        const targetLabel = action === 'play_list' ? document.getElementById('hkTargetPlaylist').selectedOptions[0]?.textContent : document.getElementById('hkActionSelect').selectedOptions[0]?.textContent;
        state.hotkeys = state.hotkeys.filter(h => h.keyLabel !== keyLabel);
        state.hotkeys.push({ keyLabel, code: e.code, alt: e.altKey, ctrl: e.ctrlKey, shift: e.shiftKey, action, target, targetLabel });
        saveState(); renderHotkeys(); showToast(`Atandı: ${keyLabel}`); return;
      }
      if (e.key === 'Escape') { document.getElementById('focusModal').classList.remove('open'); closeDrawer(); return; }
      const tag = document.activeElement?.tagName;
      if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return;

      const match = state.hotkeys.find(h => h.code === e.code && !!h.alt === e.altKey && !!h.ctrl === e.ctrlKey && !!h.shift === e.shiftKey);
      if (match) {
        e.preventDefault();
        if (match.action === 'play_list') launchByTarget(match.target, false);
        else if (match.action === 'toggle_radio') { if (state.radioRunning) stopRadio(); else startRadio(); }
        else if (match.action === 'toggle_play') playBtn.click();
        else if (match.action === 'toggle_fon') { state.fonEnabled = !state.fonEnabled; syncMixerAndFonUI(); syncFonPlayback(); saveState(); }
        else if (match.action === 'toggle_mute') toggleManualMute();
        return;
      }
      if (!e.ctrlKey && !e.altKey && !e.metaKey) {
        if (e.code === 'Space') { e.preventDefault(); playBtn.click(); }
        else if (e.code === 'ArrowLeft' && e.shiftKey) { e.preventDefault(); document.getElementById('prevBtn').click(); }
        else if (e.code === 'ArrowRight' && e.shiftKey) { e.preventDefault(); document.getElementById('nextBtn').click(); }
        else if (e.code === 'ArrowLeft') { e.preventDefault(); audio.currentTime = Math.max(0, audio.currentTime - state.skipStepSec); }
        else if (e.code === 'ArrowRight') { e.preventDefault(); audio.currentTime = Math.min(audio.duration || 0, audio.currentTime + state.skipStepSec); }
        else if (e.code === 'KeyM') { e.preventDefault(); toggleManualMute(); }
        else if (e.code === 'KeyB') { e.preventDefault(); addBookmark(''); }
        else if (e.code === 'KeyF') {
          e.preventDefault();
          const fm = document.getElementById('focusModal');
          if (fm.classList.contains('open')) fm.classList.remove('open');
          else if (state.currentTrackId) { fm.classList.add('open'); setFocusTab('pasaj'); }
        }
      }
    });

    function renderHotkeys() {
      const box = document.getElementById('hotkeysListBox');
      if (!state.hotkeys.length) { box.innerHTML = '<p class="hint">Henüz özel kısayol tuşu atanmadı.</p>'; return; }
      box.innerHTML = '';
      state.hotkeys.forEach((h, i) => {
        const div = document.createElement('div'); div.className = 'mini-item';
        div.innerHTML = `<span><span class="kbd">${escHtml(h.keyLabel)}</span> ${escHtml(h.targetLabel.slice(0, 30))}</span><button class="btn-ghost" style="padding:2px 6px;font-size:0.7rem;">✕</button>`;
        div.querySelector('button').onclick = () => { state.hotkeys.splice(i, 1); saveState(); renderHotkeys(); };
        box.appendChild(div);
      });
    }

    const rowDays = document.getElementById('schedDaysRow');
    DAY_LABELS.forEach((lbl, idx) => {
      const b = document.createElement('button'); b.className = 'day-chip on'; b.textContent = lbl;
      b.onclick = () => { if (selectedDays.has(idx)) selectedDays.delete(idx); else selectedDays.add(idx); b.classList.toggle('on', selectedDays.has(idx)); };
      rowDays.appendChild(b);
    });
    document.getElementById('addSchedBtn').onclick = () => {
      const time = document.getElementById('schedTime').value, target = document.getElementById('schedPlaylist').value;
      if (!time || !target || !selectedDays.size) return;
      state.schedules.push({ time, target, label: document.getElementById('schedPlaylist').selectedOptions[0].textContent, days: [...selectedDays], withFon: document.getElementById('schedFonChk').checked, enabled: true });
      saveState(); renderSchedules(); showToast(`Zamanlandı: ${time}`);
    };
    function renderSchedules() {
      const box = document.getElementById('schedulesList');
      if (!state.schedules.length) { box.innerHTML = '<p class="hint">Zamanlanmış yayın yok.</p>'; return; }
      box.innerHTML = '';
      state.schedules.forEach((sc, i) => {
        const div = document.createElement('div'); div.className = 'mini-item';
        div.innerHTML = `<span><strong>${escHtml(sc.time)}</strong> · ${escHtml(sc.label.slice(0, 28))}</span><button class="btn-ghost" style="padding:2px 6px;font-size:0.7rem;">✕</button>`;
        div.querySelector('button').onclick = () => { state.schedules.splice(i, 1); saveState(); renderSchedules(); };
        box.appendChild(div);
      });
    }

    // 10-second clock watcher: single schedules + Scheduled Radio block transitions + Global Radio End Time
    setInterval(() => {
      const now = new Date();
      const hhmm = `${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`;

      if (state.lastFiredMinute !== hhmm) {
        const m = state.schedules.find(sc => sc.enabled && sc.time === hhmm && sc.days.includes(now.getDay()));
        if (m) { state.lastFiredMinute = hhmm; launchByTarget(m.target, m.withFon); }
      }

      if (state.radioRunning) {
        if (state.radio.stopEnabled && state.radio.stopTime === hhmm && state.lastRadioStopMinute !== hhmm) {
          state.lastRadioStopMinute = hhmm;
          fadeMainAudio(0.01, 15000, () => stopRadio());
          applyFonGain(0, 15);
          showToast('Yayın bitiş saati: yumuşak kapanış');
          return;
        }
        if (state.radio.mode === 'scheduled') {
          const targetBlk = findScheduledBlockNow();
          if (targetBlk && targetBlk.id !== state.radioActiveBlockId) {
            startRadioBlock(targetBlk);
          }
        }
      }
    }, 10000);

    // Theme Cards, Multi-Provider AI Config, Backup & Reset
    function syncThemeCards() {
      document.querySelectorAll('[data-theme-pick]').forEach(el => el.classList.toggle('active', el.dataset.themePick === state.theme));
    }
    document.querySelectorAll('[data-theme-pick]').forEach(el => {
      el.onclick = () => { applyTheme(el.dataset.themePick); saveState(); };
    });

    function syncAiSettingsUI() {
      const pKey = state.aiCfg.provider || 'local';
      const pInfo = AI_PROVIDERS[pKey] || AI_PROVIDERS.local;
      document.getElementById('aiProviderSelect').value = pKey;
      document.getElementById('aiExternalFields').classList.toggle('hidden', pKey === 'local');
      document.getElementById('aiCustomUrlWrap').classList.toggle('hidden', pKey === 'gemini' || pKey === 'local');
      document.getElementById('aiApiKey').value = state.aiCfg.key || '';
      document.getElementById('aiModelName').value = state.aiCfg.model || pInfo.model || '';
      document.getElementById('aiApiUrl').value = state.aiCfg.url || pInfo.url || '';
      document.getElementById('aiProviderHint').textContent = pInfo.hint;
      document.getElementById('aiAutoFonChk').checked = !!state.aiCfg.autoFon;
      document.getElementById('aiTestStatus').textContent = '';
    }

    document.getElementById('aiProviderSelect').onchange = (e) => {
      const pKey = e.target.value;
      const pInfo = AI_PROVIDERS[pKey] || AI_PROVIDERS.local;
      state.aiCfg.provider = pKey;
      state.aiCfg.model = pInfo.model || '';
      state.aiCfg.url = pInfo.url || '';
      saveState(); syncAiSettingsUI(); updateAiEngineHint(); updateSettingsSummaries();
      if (pKey === 'local') showToast('Yerel Semantik AI aktif');
    };
    document.getElementById('toggleAiKeyVisBtn').onclick = () => {
      const inp = document.getElementById('aiApiKey');
      const show = inp.type === 'password';
      inp.type = show ? 'text' : 'password';
      document.getElementById('toggleAiKeyVisBtn').textContent = show ? 'Gizle' : 'Göster';
    };
    document.getElementById('aiAutoFonChk').onchange = (e) => { state.aiCfg.autoFon = e.target.checked; saveState(); };
    document.getElementById('saveAiCfgBtn').onclick = () => {
      const pKey = document.getElementById('aiProviderSelect').value;
      const pInfo = AI_PROVIDERS[pKey] || AI_PROVIDERS.local;
      state.aiCfg = {
        provider: pKey,
        url: document.getElementById('aiApiUrl').value.trim() || pInfo.url,
        key: document.getElementById('aiApiKey').value.trim(),
        model: document.getElementById('aiModelName').value.trim() || pInfo.model,
        autoFon: document.getElementById('aiAutoFonChk').checked,
      };
      saveState(); updateAiEngineHint(); updateSettingsSummaries(); showToast('AI ayarları kaydedildi');
    };
    document.getElementById('testAiCfgBtn').onclick = async () => {
      document.getElementById('saveAiCfgBtn').click();
      const st = document.getElementById('aiTestStatus');
      st.textContent = 'Bağlantı test ediliyor...';
      try {
        const res = await callExternalAi('Sadece {"ok":true} JSON döndür.', 'Test');
        const parsed = parseJsonLoose(res);
        st.textContent = parsed ? 'Bağlantı başarılı — API anahtarı doğrulandı.' : 'Yanıt alındı.';
        showToast('API bağlantısı başarılı');
      } catch (err) {
        st.textContent = `Bağlantı kurulamadı: ${err.message || 'API anahtarını veya adresi kontrol edin.'}`;
        showToast('API testi başarısız');
      }
    };

    document.getElementById('addCustomBtn').onclick = () => {
      const title = document.getElementById('customTitle').value.trim(), mp3 = document.getElementById('customUrl').value.trim();
      if (!title || !mp3.startsWith('http')) return;
      const tr = { id: 'c-' + Date.now(), book: 'Özel', section: title, title, pages: '', pageStart: 1, pageEnd: 5, reader: 'Özel', mp3, durationSec: 900, estMinutes: 15, quranRanges: [], segments: [] };
      state.customTracks.push(tr); state.tracks.unshift(tr); rebuildMap(); saveState(); playQueue([tr.id], 0, title); closeDrawer();
    };
    document.getElementById('exportBackupBtn').onclick = () => {
      let data = '{}';
      try { data = localStorage.getItem(STORAGE_KEY) || '{}'; } catch (e) {}
      const a = document.createElement('a'); a.href = URL.createObjectURL(new Blob([data], { type: 'application/json' }));
      a.download = 'risale-dinle-yedek.json'; a.click();
    };
    document.getElementById('importBackupInput').onchange = async (e) => {
      const f = e.target.files[0]; if (!f) return;
      try { const txt = await f.text(); JSON.parse(txt); localStorage.setItem(STORAGE_KEY, txt); location.reload(); } catch (err) { showToast('Geçersiz yedek'); }
    };
    document.getElementById('resetSettingsBtn').onclick = () => {
      state.fonEnabled = false; state.fonMode = 'synth'; state.fonPreset = 'ussak';
      state.risaleUnit = 10; state.fonUnit = 2.5; state.speed = 1; state.silenceGapSec = 0;
      state.skipStepSec = 15; state.repeatMode = 'off'; state.defaultRelFilter = 'all';
      state.showKunye = true; state.focusFontSize = 'md'; state.theme = 'light';
      state.duckEnabled = true; state.duckBesmele = true; state.openingMuteSec = 15;
      state.preRollSec = 2.5; state.postRollSec = 1.2;
      applyTheme('light'); syncAllSettingsUI(); syncFonPlayback(); saveState();
      showToast('Ayarlar varsayılana sıfırlandı');
    };
    document.getElementById('autoStartOpenChk').onchange = (e) => { state.autoStartOpen = e.target.checked; saveState(); };

    // PWA Install & Standalone Single-File App Exporter
    let deferredPwaPrompt = null;
    window.addEventListener('beforeinstallprompt', (e) => {
      e.preventDefault();
      deferredPwaPrompt = e;
    });
    if ('serviceWorker' in navigator && location.protocol.startsWith('http')) {
      navigator.serviceWorker.register('sw.js').catch(() => {});
    }
    document.getElementById('installPwaBtn').onclick = async () => {
      if (deferredPwaPrompt) {
        deferredPwaPrompt.prompt();
        await deferredPwaPrompt.userChoice.catch(() => {});
        deferredPwaPrompt = null;
      } else {
        showToast('Tarayıcı menüsünden "Ana Ekrana Ekle / Uygulamayı Yükle" seçeneğini kullanın veya Tek Dosya İndirin');
      }
    };
    document.getElementById('downloadStandaloneAppBtn').onclick = () => {
      const htmlStr = '<!DOCTYPE html>\n' + document.documentElement.outerHTML;
      const a = document.createElement('a');
      a.href = URL.createObjectURL(new Blob([htmlStr], { type: 'text/html;charset=utf-8' }));
      a.download = 'Risale-Dinle-Uygulamasi.html';
      a.click();
      showToast('Tek dosya uygulama indiriliyor');
    };

    // Native Android Back Button Hook (returns true if handled inside UI, false to minimize app)
    window.__handleAndroidBack = function() {
      if (drawer.classList.contains('open')) {
        if (document.getElementById('layer-menu').classList.contains('hidden')) { showSettingsMenu(); return true; }
        closeDrawer(); return true;
      }
      if (document.getElementById('focusModal').classList.contains('open')) {
        document.getElementById('focusModal').classList.remove('open'); return true;
      }
      if (!document.getElementById('topicDetail').classList.contains('hidden')) {
        setView(state.view); return true;
      }
      if (state.view !== 'topics') {
        setView('topics'); return true;
      }
      return false;
    };

    // Init
    applyTheme(state.theme);
    syncAiSettingsUI();
    updateAiEngineHint();
    syncAllSettingsUI();

    const rf = document.getElementById('readerFilter');
    EMBEDDED_CATALOG.readers.forEach(r => { const o = document.createElement('option'); o.value = r.name; o.textContent = `${r.name} (${r.count})`; rf.appendChild(o); });
    populatePlaylistSelectors();
    restoreIdbFonBlobs();
    setView('topics');
    if (state.queue.length && state.queue[state.queueIndex]) loadQueueItem(state.queue[state.queueIndex], state.autoStartOpen);
  </script>
</body>
</html>
"""

html_out = (
    HTML_TEMPLATE
    .replace("__CATALOG_JSON__", json.dumps(catalog, ensure_ascii=False, separators=(",", ":")))
    .replace("__SEGMENTS_JSON__", json.dumps(segments_idx, ensure_ascii=False, separators=(",", ":")))
    .replace("__PLAYLISTS_JSON__", json.dumps(compact_topics, ensure_ascii=False, separators=(",", ":")))
)

out_file = ROOT / "index.html"
out_file.write_text(html_out, encoding="utf-8")

icon_svg = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
  <rect width="512" height="512" rx="112" fill="#181512"/>
  <circle cx="256" cy="256" r="176" fill="none" stroke="#cfa04e" stroke-width="10" opacity="0.48"/>
  <path d="M176 168h64c40 0 64 24 64 56 0 24-16 44-40 52l48 68h-48l-40-64h-16v64h-32V168zm32 32v48h28c20 0 32-8 32-24s-12-24-32-24h-28z" fill="#f5f0e7"/>
</svg>
"""
(ROOT / "icon.svg").write_text(icon_svg, encoding="utf-8")

manifest = {
    "name": "Risale Dinle — Risale-i Nur Külliyatı Dinleme Uygulaması",
    "short_name": "Risale Dinle",
    "description": "Saniye-saniye eşleşmeli, hikmetli sıralamalı, 26 fonlu ve kişisel radyo akışlı Risale-i Nur dinleme uygulaması.",
    "start_url": "./index.html",
    "display": "standalone",
    "background_color": "#f9f6f0",
    "theme_color": "#181512",
    "lang": "tr",
    "icons": [
        {"src": "icon.svg", "sizes": "any", "type": "image/svg+xml", "purpose": "any maskable"}
    ],
}
(ROOT / "manifest.json").write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding="utf-8")

sw_js = """const CACHE_NAME = 'risale-dinle-v11';
const ASSETS = ['./', './index.html', './manifest.json', './icon.svg'];
self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(CACHE_NAME).then(c => c.addAll(ASSETS)).then(() => self.skipWaiting()));
});
self.addEventListener('activate', (e) => {
  e.waitUntil(caches.keys().then(keys => Promise.all(keys.filter(k => k !== CACHE_NAME).map(k => caches.delete(k)))).then(() => self.clients.claim()));
});
self.addEventListener('fetch', (e) => {
  if (e.request.method !== 'GET' || e.request.url.includes('.mp3')) return;
  e.respondWith(caches.match(e.request).then(cached => cached || fetch(e.request)));
});
"""
(ROOT / "sw.js").write_text(sw_js, encoding="utf-8")

print(f"Built v11 Risale Dinle PWA & Standalone App -> {out_file} ({len(html_out):,} bytes)")

