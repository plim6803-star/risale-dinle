const r = await fetch("https://www.erisale.com/", { headers: { "User-Agent": "Mozilla/5.0" } });
const t = await r.text();
const m = [...t.matchAll(/(?:href|push)\(["']?([^"'<>)\s]+)/g)].map(x => x[1]);
console.log("Matches:", [...new Set(m)].slice(0, 50));
// look for json or api urls in chunks or html
const urls = [...t.matchAll(/https?:\/\/[^"'\\s<>]+/g)].map(x => x[0]);
console.log("URLs:", [...new Set(urls)].slice(0, 30));
const paths = [...t.matchAll(/\\?"(\/[a-zA-Z0-9_-]+\/[a-zA-Z0-9_/-]+)\\?"/g)].map(x => x[1]);
console.log("Paths:", [...new Set(paths)].slice(0, 50));
