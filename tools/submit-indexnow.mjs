import fs from "node:fs/promises";
import path from "node:path";
import { pathToFileURL } from "node:url";

const DEFAULT_HOST = "septicpath.com";
const DEFAULT_KEY = "66e41240613868dc4e9a8fffdcad9b77";

export function parseSitemap(xml) {
    const entries = new Map();
    for (const block of xml.matchAll(/<url>([\s\S]*?)<\/url>/g)) {
        const location = block[1].match(/<loc>(.*?)<\/loc>/)?.[1]?.trim();
        const lastModified = block[1].match(/<lastmod>(.*?)<\/lastmod>/)?.[1]?.trim() ?? "";
        if (location) {
            entries.set(decodeXml(location), lastModified);
        }
    }
    return entries;
}

export function changedUrls(previous, current) {
    const changed = [];
    for (const [url, lastModified] of current) {
        if (!previous.has(url) || previous.get(url) !== lastModified) {
            changed.push(url);
        }
    }
    for (const url of previous.keys()) {
        if (!current.has(url)) {
            changed.push(url);
        }
    }
    return [...new Set(changed)].sort();
}

function decodeXml(value) {
    return value
        .replaceAll("&amp;", "&")
        .replaceAll("&lt;", "<")
        .replaceAll("&gt;", ">");
}

function parseArgs(argv) {
    const options = { sitemaps: [], previousSitemaps: [], dryRun: false };
    for (let index = 0; index < argv.length; index += 1) {
        const argument = argv[index];
        if (argument === "--sitemap") options.sitemaps.push(argv[++index]);
        else if (argument === "--previous-sitemap") options.previousSitemaps.push(argv[++index]);
        else if (argument === "--host") options.host = argv[++index];
        else if (argument === "--key") options.key = argv[++index];
        else if (argument === "--dry-run") options.dryRun = true;
        else throw new Error(`Unknown argument: ${argument}`);
    }
    if (options.sitemaps.length === 0) throw new Error("At least one --sitemap is required");
    if (options.previousSitemaps.length !== options.sitemaps.length) {
        throw new Error("Each --sitemap needs a matching --previous-sitemap");
    }
    return options;
}

async function readCurrentSitemap(location) {
    const url = new URL(location);
    url.searchParams.set("indexnow_check", Date.now().toString());
    const response = await fetch(url, { headers: { accept: "application/xml" } });
    if (!response.ok) throw new Error(`Could not fetch ${location}: HTTP ${response.status}`);
    return response.text();
}

async function readPreviousSitemap(file) {
    try {
        return await fs.readFile(file, "utf8");
    } catch (error) {
        if (error.code === "ENOENT") return "";
        throw error;
    }
}

function merge(target, source) {
    for (const [url, lastModified] of source) target.set(url, lastModified);
}

function validateUrls(urls, host) {
    for (const value of urls) {
        const url = new URL(value);
        if (url.protocol !== "https:" || url.hostname !== host) {
            throw new Error(`Refusing URL outside https://${host}: ${value}`);
        }
    }
}

export async function run(argv = process.argv.slice(2)) {
    const options = parseArgs(argv);
    const host = options.host ?? DEFAULT_HOST;
    const key = options.key ?? DEFAULT_KEY;
    const previous = new Map();
    const current = new Map();

    for (let index = 0; index < options.sitemaps.length; index += 1) {
        merge(previous, parseSitemap(await readPreviousSitemap(options.previousSitemaps[index])));
        merge(current, parseSitemap(await readCurrentSitemap(options.sitemaps[index])));
    }

    const urls = changedUrls(previous, current);
    validateUrls(urls, host);
    if (urls.length === 0) {
        console.log("IndexNow: no added, changed, or deleted sitemap URLs.");
        return;
    }

    const payload = {
        host,
        key,
        keyLocation: `https://${host}/${key}.txt`,
        urlList: urls
    };
    if (options.dryRun) {
        console.log(JSON.stringify(payload, null, 2));
        return;
    }

    const keyResponse = await fetch(payload.keyLocation, { cache: "no-store" });
    if (!keyResponse.ok || (await keyResponse.text()).trim() !== key) {
        throw new Error(`IndexNow key is not publicly verifiable at ${payload.keyLocation}`);
    }

    for (let offset = 0; offset < urls.length; offset += 10_000) {
        const response = await fetch("https://api.indexnow.org/indexnow", {
            method: "POST",
            headers: { "content-type": "application/json; charset=utf-8" },
            body: JSON.stringify({ ...payload, urlList: urls.slice(offset, offset + 10_000) })
        });
        if (![200, 202].includes(response.status)) {
            throw new Error(`IndexNow rejected the submission: HTTP ${response.status} ${(await response.text()).trim()}`);
        }
    }
    console.log(`IndexNow: submitted ${urls.length} changed URL(s).`);
}

const isMain = process.argv[1] && import.meta.url === pathToFileURL(path.resolve(process.argv[1])).href;
if (isMain) {
    run().catch(error => {
        console.error(error.message);
        process.exitCode = 1;
    });
}
