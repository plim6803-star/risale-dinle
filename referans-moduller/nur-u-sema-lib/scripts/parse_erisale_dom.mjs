import fs from "node:fs";
const t = fs.readFileSync("temp_erisale_1soz.html", "utf8");
const idx = t.indexOf("Ey kardeş!");
console.log("HTML around Ey kardeş:\n", t.slice(idx - 600, idx + 1500));
