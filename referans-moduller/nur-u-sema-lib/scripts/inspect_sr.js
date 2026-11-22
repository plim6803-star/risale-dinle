const fs = require('fs');
const html = fs.readFileSync('temp_sozler.html', 'utf8');
const scripts = html.match(/<script[\s\S]*?<\/script>/gi) || [];
scripts.forEach((s, idx) => {
  const matches = s.match(/["'][^"']*(?:json|playlist|audio|mp3|voice|okuyan|reader)[^"']*["']/gi);
  if (matches) {
    console.log(`Script #${idx}:`, matches.slice(0, 10));
  }
});
