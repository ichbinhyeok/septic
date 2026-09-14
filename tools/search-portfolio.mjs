#!/usr/bin/env node
import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const readJson = relativePath => JSON.parse(fs.readFileSync(path.join(root, relativePath), 'utf8').replace(/^\uFEFF/, ''));
const targetsDocument = readJson('data/raw/search_response_targets.json');
const statesDocument = readJson('data/raw/state_profiles.json');
const stateMoneyDocument = readJson('data/raw/state_money_pages.json');
const countiesDocument = readJson('data/raw/county_records_pages.json');
const contentDocument = readJson('data/raw/content_pages.json');

const slugify = value => String(value || '').toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
const stateSlugs = new Map(statesDocument.states.map(state => [state.stateCode, slugify(state.stateName)]));
const targetsByPath = new Map(targetsDocument.targets.map(target => [target.path, target]));
const isPublished = item => (item.publishStatus || '').toLowerCase() === 'published';
const list = value => Array.isArray(value) ? value : [];
const words = value => String(value || '').trim().split(/\s+/).filter(Boolean).length;

function classify(page) {
    if (page.demandTarget) return ['core', 'Protect snippet; improve the next-action handoff and measure it.'];
    if (page.operationalProof) return ['core', 'Reuse the verified outcome as route-specific proof.'];
    if ((page.type === 'national' || page.sourceCount >= 2) && page.decisionDepth >= 3 && page.copyDepth >= 30) {
        return ['support', 'Keep connected to a core route; promote only after demand or a verified outcome appears.'];
    }
    return ['evidence_queue', 'Do not expand this page yet; add local source depth or a verified operating outcome first.'];
}

const pages = [];

for (const item of contentDocument.pages.filter(isPublished)) {
    const pagePath = `/${item.slug}/`;
    const target = targetsByPath.get(pagePath);
    pages.push({
        type: 'national',
        path: pagePath,
        title: item.title,
        demandTarget: Boolean(target),
        boost: target?.boost || 0,
        impressions: target?.impressions || 0,
        clicks: target?.clicks || 0,
        operationalProof: false,
        sourceCount: list(item.officialSourceIds).length,
        decisionDepth: list(item.decisionSteps).length + list(item.actionSteps).length,
        copyDepth: words(item.introCopy) + words(item.uniqueAngle)
    });
}

for (const item of stateMoneyDocument.pages.filter(isPublished)) {
    const stateSlug = stateSlugs.get(item.stateCode);
    if (!stateSlug) continue;
    const pagePath = `/${item.contentSlug}/${stateSlug}/`;
    const target = targetsByPath.get(pagePath);
    pages.push({
        type: 'state',
        path: pagePath,
        title: item.title,
        demandTarget: Boolean(target),
        boost: target?.boost || 0,
        impressions: target?.impressions || 0,
        clicks: target?.clicks || 0,
        operationalProof: false,
        sourceCount: list(item.officialSourceIds).length,
        decisionDepth: list(item.decisionSteps).length + list(item.quotePrepChecklist).length,
        copyDepth: words(item.introCopy) + words(item.uniqueAngle)
    });
}

for (const item of countiesDocument.pages.filter(isPublished)) {
    const stateSlug = stateSlugs.get(item.stateCode);
    if (!stateSlug) continue;
    const pagePath = `/septic-records-checklist/${stateSlug}/${item.countySlug}/`;
    const target = targetsByPath.get(pagePath);
    pages.push({
        type: 'county',
        path: pagePath,
        title: item.title,
        demandTarget: Boolean(target),
        boost: target?.boost || 0,
        impressions: target?.impressions || 0,
        clicks: target?.clicks || 0,
        operationalProof: Boolean(item.operationalProof),
        sourceCount: list(item.officialSourceIds).length,
        decisionDepth: list(item.decisionSteps).length + list(item.recordsToRequest).length,
        copyDepth: words(item.introCopy) + words(item.uniqueAngle)
    });
}

for (const page of pages) {
    const [classification, nextAction] = classify(page);
    page.classification = classification;
    page.nextAction = nextAction;
}

const rank = {core: 0, support: 1, evidence_queue: 2};
pages.sort((left, right) => rank[left.classification] - rank[right.classification]
    || right.boost - left.boost
    || right.impressions - left.impressions
    || left.path.localeCompare(right.path));

const counts = Object.fromEntries(['core', 'support', 'evidence_queue'].map(key => [key, pages.filter(page => page.classification === key).length]));
const report = {
    schemaVersion: 1,
    evidenceThrough: targetsDocument.generatedAt,
    source: targetsDocument.source,
    summary: {total: pages.length, ...counts},
    rules: {
        core: 'Observed search demand or route-specific operational proof.',
        support: 'At least two official sources, three decision artifacts, and 45 words of local decision copy.',
        evidence_queue: 'Published asset that needs stronger evidence before further SEO expansion.'
    },
    pages
};

const writeArg = process.argv.find(argument => argument.startsWith('--write='));
if (writeArg) {
    const outputPath = path.resolve(root, writeArg.slice('--write='.length));
    fs.mkdirSync(path.dirname(outputPath), {recursive: true});
    fs.writeFileSync(outputPath, `${JSON.stringify(report, null, 2)}\n`);
    process.stdout.write(`${outputPath}\n`);
} else {
    process.stdout.write(`${JSON.stringify(report, null, 2)}\n`);
}
