const fs = require('fs');
const html = fs.readFileSync('temp_sozler.html', 'utf8');

// Print full playlist JSON or objects from html
const match = html.match(/myPlaylist\s*=\s*new\s*jPlayerPlaylist\s*\([^,]+,\s*(\[[\s\S]*?\])\s*,/);
if (match) {
  console.log('Found playlist array!');
  try {
    const list = JSON.parse(match[1]);
    console.log('Parsed items:', list.length);
    console.log('First 5 items:', list.slice(0, 5));
    
    // Group by artist / reader
    const artists = {};
    list.forEach(item => {
      const a = item.artist || item.author || 'Belirtilmemiş';
      artists[a] = (artists[a] || 0) + 1;
    });
    console.log('Artists:', artists);

    fs.writeFileSync('parsed_sr_playlist.json', JSON.stringify(list, null, 2));
  } catch (e) {
    console.log('JSON parse error:', e.message);
    // Let's inspect raw snippet
    console.log(match[1].slice(0, 1000));
  }
} else {
  console.log('myPlaylist pattern not matched, looking for jPlayerPlaylist');
  const pos = html.indexOf('jPlayerPlaylist');
  if (pos !== -1) {
    console.log(html.slice(pos, pos + 1500));
  }
}
