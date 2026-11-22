const fs = require('fs');
const html = fs.readFileSync('temp_sozler.html', 'utf8');

// Let's find where 'sozler-p001' appears
const idx = html.indexOf('sozler-p001');
console.log('Index of sozler-p001:', idx);
if (idx !== -1) {
  console.log(html.slice(Math.max(0, idx - 400), idx + 800));
}
