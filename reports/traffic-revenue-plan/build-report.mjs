import { readFileSync, writeFileSync } from "node:fs";
import { buildPortableArtifact } from "file:///C:/Users/tlsgu/.codex/plugins/cache/openai-curated-remote/data-analytics/0.2.8-13ceeea1f599/skills/build-report/scripts/build_portable_artifact.mjs";
import { extractPortableChartSvgs } from "file:///C:/Users/tlsgu/.codex/plugins/cache/openai-curated-remote/data-analytics/0.2.8-13ceeea1f599/skills/build-report/scripts/extract_portable_chart_svgs.mjs";

const root = "C:/Development/Owner/septic/reports/traffic-revenue-plan";
const input = JSON.parse(readFileSync(`${root}/artifact.json`, "utf8"));
const staticCharts = await extractPortableChartSvgs({
  actionTimeoutMs: 5000,
  htmlPath: `${root}/report-debug.html`,
  readyTimeoutMs: 15000
});
const runtimeWidthFix = [
  "<style data-portable-runtime-width-fix>",
  ".analytics-top-bar{width:100%!important;left:0!important;right:0!important;transform:none!important}",
  "</style>"
].join("");
const html = buildPortableArtifact(input, { staticCharts })
  .replace("</head>", `${runtimeWidthFix}</head>`);
writeFileSync(`${root}/report.html`, html, "utf8");
