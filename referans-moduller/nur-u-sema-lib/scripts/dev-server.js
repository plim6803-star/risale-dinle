process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
const { spawn } = require("child_process");
const path = require("path");

const nextBin = path.resolve(__dirname, "../node_modules/next/dist/bin/next");
const child = spawn(process.execPath, [nextBin, "dev", "--webpack"], {
  stdio: "inherit",
  env: process.env,
});

child.on("exit", (code) => {
  process.exit(code || 0);
});
