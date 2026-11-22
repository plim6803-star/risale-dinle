// Kullanım: node scripts/fetch-links.mjs <url> [filtreRegex]
const [url, filt = "ses|mp3|indir|dinle|zip"] = process.argv.slice(2);
try {
  const r = await fetch(url, { headers: { "User-Agent": "Mozilla/5.0" } });
  const t = await r.text();
  console.log("STATUS", r.status, "LEN", t.length, "FINAL", r.url);
  const links = [...new Set([...t.matchAll(/(?:href|src)=["']([^"']+)["']/g)].map((m) => m[1]))];
  const re = new RegExp(filt, "i");
  console.log(links.filter((l) => re.test(l)).slice(0, 80).join("\n"));
  if (process.env.SHOWTEXT) console.log(t.replace(/<script[\s\S]*?<\/script>|<style[\s\S]*?<\/style>/g, "").replace(/<[^>]+>/g, " ").replace(/\s+/g, " ").slice(0, 3000));
} catch (e) {
  console.log("ERR", e.cause?.code || e.message);
}
