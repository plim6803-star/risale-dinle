const fs = require('fs');
const html = fs.readFileSync('temp_sozler.html', 'utf8');

const idx = html.indexOf('sozler-p001');
// Let's search backward for '['
const arrayStart = html.lastIndexOf('[', idx);
console.log('Array start pos:', arrayStart);
console.log('Surrounding code before array:', html.slice(arrayStart - 200, arrayStart + 50));

let depth = 0;
let endIdx = -1;
for (let i = arrayStart; i < html.length; i++) {
  if (html[i] === '[') depth++;
  else if (html[i] === ']') {
    depth--;
    if (depth === 0) {
      endIdx = i + 1;
      break;
    }
  }
}

if (endIdx !== -1) {
  const raw = html.slice(arrayStart, endIdx);
  const list = JSON.parse(raw);
  console.log('Found list with items:', list.length);

  const artistCounts = {};
  const artistSamples = {};
  list.forEach(item => {
    const artist = item.artist || 'Bilinmiyor';
    artistCounts[artist] = (artistCounts[artist] || 0) + 1;
    if (!artistSamples[artist]) artistSamples[artist] = [];
    artistSamples[artist].push(item.title ? item.title.replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim() : '');
  });

  console.log('\n--- OKUYUCULAR ---');
  for (const [artist, count] of Object.entries(artistCounts)) {
    console.log(`Okuyucu: "${artist}" => ${count} parça`);
    console.log(`  Örnekler:`, artistSamples[artist].slice(0, 3));
  }

  fs.writeFileSync('sorularlarisale_sozler_playlist.json', JSON.stringify(list, null, 2));
}
