const fs = require('fs');
const html = fs.readFileSync('temp_sozler.html', 'utf8');

// Find the playlist array in Drupal.settings
const startPattern = '"playlist":[';
const startIdx = html.indexOf(startPattern);
if (startIdx !== -1) {
  let depth = 0;
  let endIdx = -1;
  const jsonStart = startIdx + '"playlist":'.length;
  for (let i = jsonStart; i < html.length; i++) {
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
    const rawJson = html.slice(jsonStart, endIdx);
    const list = JSON.parse(rawJson);
    console.log('Total playlist items found:', list.length);

    const artistCounts = {};
    const artistTracks = {};
    list.forEach(item => {
      const artist = item.artist || 'Bilinmiyor';
      artistCounts[artist] = (artistCounts[artist] || 0) + 1;
      if (!artistTracks[artist]) artistTracks[artist] = [];
      artistTracks[artist].push(item);
    });

    console.log('\n--- Okuyucular ve Parça Sayıları ---');
    for (const [artist, count] of Object.entries(artistCounts)) {
      console.log(`Okuyucu: "${artist}" => ${count} parça`);
    }

    fs.writeFileSync('sorularlarisale_readers.json', JSON.stringify({
      artistCounts,
      list
    }, null, 2));
    console.log('\nKaydedildi: sorularlarisale_readers.json');
  } else {
    console.log('Could not find matching end bracket for playlist array.');
  }
} else {
  console.log('Could not find "playlist":[ pattern.');
}
