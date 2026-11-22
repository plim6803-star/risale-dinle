const fs = require('fs');
const html = fs.readFileSync('temp_sozler.html', 'utf8');

// jPlayer playlist structure
// Let's look for playlist items or jPlayer initialization
const matches = html.match(/\{[^{}]*?title[^{}]*?mp3[^{}]*?\}/g) || [];
console.log('Playlist items count:', matches.length);

const items = [];
const allAudioMatches = [...html.matchAll(/title:\s*["']([^"']+)["'][^}]+?mp3:\s*["']([^"']+)["']/g)];
console.log('Regex allAudioMatches:', allAudioMatches.length);

// Let's dump all mp3 URLs and their surrounding title/author
const mp3s = [...html.matchAll(/(https?:\\?\/\\?\/[^\s"']+\.mp3)/g)].map(m => m[1].replace(/\\\//g, '/'));
console.log('Total MP3s:', mp3s.length);
console.log('Unique MP3s:', [...new Set(mp3s)].slice(0, 30));

// Find narrator patterns in filenames or titles
const narrators = new Set();
mp3s.forEach(url => {
  const file = url.split('/').pop();
  narrators.add(file);
});
console.log('Sample files:', [...narrators].slice(0, 40));
