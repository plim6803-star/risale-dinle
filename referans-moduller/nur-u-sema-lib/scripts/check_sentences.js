const fs = require('fs');
const soz = JSON.parse(fs.readFileSync('public/data/kulliyat/sozler.json', 'utf8'));
const s2 = soz.sections.find(s => s.id === 2);
console.log('Section 2:', s2.title, 'Paragraphs:', s2.paragraphs.length);
const sentences = [];
for (const p of s2.paragraphs) {
  if (p === '﴾…﴿' || p === '* * *') continue;
  const parts = p.match(/[^.!?…]+(?:[.!?…]+[”"’)]*|$)/g) ?? [p];
  for (const s of parts) if (s.trim()) sentences.push(s.trim());
}
console.log('Total sentences:', sentences.length);
console.log('First 5 sentences:\n' + sentences.slice(0, 5).join('\n---\n'));
