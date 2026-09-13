import assert from "node:assert/strict";
import test from "node:test";
import { changedUrls, parseSitemap } from "./submit-indexnow.mjs";

test("parseSitemap reads locations and material revision dates", () => {
    const sitemap = parseSitemap(`<?xml version="1.0"?><urlset>
        <url><loc>https://septicpath.com/a/</loc><lastmod>2026-09-12</lastmod></url>
        <url><loc>https://septicpath.com/b/?x=1&amp;y=2</loc></url>
    </urlset>`);
    assert.equal(sitemap.get("https://septicpath.com/a/"), "2026-09-12");
    assert.equal(sitemap.get("https://septicpath.com/b/?x=1&y=2"), "");
});

test("changedUrls returns added, materially changed, and deleted URLs", () => {
    const previous = new Map([
        ["https://septicpath.com/unchanged/", "2026-09-01"],
        ["https://septicpath.com/changed/", "2026-09-01"],
        ["https://septicpath.com/deleted/", "2026-09-01"]
    ]);
    const current = new Map([
        ["https://septicpath.com/unchanged/", "2026-09-01"],
        ["https://septicpath.com/changed/", "2026-09-12"],
        ["https://septicpath.com/added/", "2026-09-12"]
    ]);
    assert.deepEqual(changedUrls(previous, current), [
        "https://septicpath.com/added/",
        "https://septicpath.com/changed/",
        "https://septicpath.com/deleted/"
    ]);
});
