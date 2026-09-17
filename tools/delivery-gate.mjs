#!/usr/bin/env node
// Local preflight gate for every external delivery that includes a customer-case file.
import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import {fileURLToPath} from 'node:url';

const repoRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const operationsHome = path.join(repoRoot, 'storage', 'operations');
const ledgerPath = path.join(operationsHome, 'ledger.json');
const blocklistPath = path.join(operationsHome, 'delivery-blocklist.json');
const requiredAnchorFields = ['street_address', 'parcel_id', 'plat_subdivision', 'lot', 'owner_applicant', 'geometry'];
const allowedAnchorResults = new Set(['match', 'mismatch', 'not_present', 'not_applicable']);
const allowedPurposes = new Set(['customer_delivery', 'agency_delivery']);
const allowedDocumentKinds = new Set(['map_or_plan', 'form_or_certificate', 'text_document', 'other']);

const readJson = file => JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, ''));
const fileSha256 = file => crypto.createHash('sha256').update(fs.readFileSync(file)).digest('hex');
const valueKey = value => String(value ?? '').toLowerCase().replace(/\b(street|st|road|rd|avenue|ave|highway|hwy|pike)\b/g, '').replace(/[^a-z0-9]/g, '');
const valuesMatch = (left, right) => {
  const a = valueKey(left);
  const b = valueKey(right);
  if (!a || !b) return false;
  if (a === b) return true;
  return Math.min(a.length, b.length) >= 5 && (a.includes(b) || b.includes(a));
};
const inside = (parent, child) => {
  const relative = path.relative(parent, child);
  return relative && !relative.startsWith('..') && !path.isAbsolute(relative);
};

export function evaluateDeliveryGate(ledger, manifest, options = {}) {
  const errors = [];
  const warnings = [];
  const checkedArtifacts = [];
  const nowMs = options.now ? Date.parse(options.now) : Date.now();
  const home = options.operationsHome || operationsHome;
  const blockedRows = options.blockedSources || [];
  const blocked = new Map(blockedRows.map(row => [row.source_id, row.reason || 'Source is blocked']));

  if (manifest.gate_version !== 1) errors.push('gate_version must be 1');
  if (!allowedPurposes.has(manifest.purpose)) errors.push('purpose must be customer_delivery or agency_delivery');
  if (!manifest.case_id) errors.push('case_id is required');
  if (!manifest.recipient) errors.push('recipient is required');
  if (!manifest.subject) errors.push('subject is required');
  if (!manifest.reviewer) errors.push('reviewer is required');
  if (manifest.identity_review_complete !== true) errors.push('identity_review_complete must be true');

  const reviewedAt = Date.parse(manifest.reviewed_at);
  if (!manifest.reviewed_at || Number.isNaN(reviewedAt)) {
    errors.push('reviewed_at must be a valid ISO timestamp');
  } else {
    const age = nowMs - reviewedAt;
    if (age < -5 * 60_000) errors.push('reviewed_at cannot be in the future');
    if (age > 24 * 60 * 60_000) errors.push('identity review is older than 24 hours; review the files again');
  }

  const customerCase = ledger.cases?.find(row => row.id === manifest.case_id);
  if (!customerCase) errors.push(`unknown case_id ${manifest.case_id || ''}`);
  if (customerCase && manifest.purpose === 'customer_delivery' && String(manifest.recipient).trim().toLowerCase() !== String(customerCase.email).trim().toLowerCase()) {
    errors.push(`customer recipient does not match case email ${customerCase.email}`);
  }

  if (!Array.isArray(manifest.artifacts) || manifest.artifacts.length === 0) {
    errors.push('at least one artifact is required');
  }

  const caseCorpus = customerCase ? JSON.stringify(customerCase) : '';
  for (const [index, artifact] of (manifest.artifacts || []).entries()) {
    const label = `artifact[${index}]`;
    const artifactErrors = [];
    const source = ledger.sources?.find(row => row.id === artifact.source_id);
    if (!source) artifactErrors.push(`unknown source_id ${artifact.source_id || ''}`);
    if (blocked.has(artifact.source_id)) artifactErrors.push(`blocked source: ${blocked.get(artifact.source_id)}`);
    if (!artifact.agency_filename) artifactErrors.push('agency_filename is required');
    if (!artifact.planned_filename) artifactErrors.push('planned_filename is required');
    if (!allowedDocumentKinds.has(artifact.document_kind)) artifactErrors.push('invalid document_kind');
    if (!Number.isInteger(artifact.page_count) || artifact.page_count < 1) artifactErrors.push('page_count must be a positive integer');
    const pages = Array.isArray(artifact.pages_reviewed) ? [...new Set(artifact.pages_reviewed)] : [];
    if (Number.isInteger(artifact.page_count) && (pages.length !== artifact.page_count || pages.some((page, i) => page !== i + 1))) {
      artifactErrors.push(`pages_reviewed must contain every page from 1 through ${artifact.page_count}`);
    }

    let resolvedPath = null;
    let actualSha256 = null;
    if (source) {
      if (!source.local_path) {
        artifactErrors.push('source has no local_path');
      } else {
        resolvedPath = path.resolve(home, source.local_path);
        if (!inside(home, resolvedPath)) artifactErrors.push('source path is outside private operations storage');
        else if (!fs.existsSync(resolvedPath)) artifactErrors.push('source file is missing');
        else {
          actualSha256 = fileSha256(resolvedPath);
          if (!source.sha256) artifactErrors.push('source has no recorded sha256');
          else if (actualSha256.toLowerCase() !== String(source.sha256).toLowerCase()) artifactErrors.push('source sha256 does not match the ledger');
        }
      }
    }

    if (artifact.agency_filename && artifact.planned_filename) {
      const originalExt = path.extname(artifact.agency_filename).toLowerCase();
      const plannedExt = path.extname(artifact.planned_filename).toLowerCase();
      if (!originalExt || originalExt !== plannedExt) artifactErrors.push('planned filename must preserve the original extension');
      if (artifact.agency_filename !== artifact.planned_filename && !artifact.rename_reason) artifactErrors.push('rename_reason is required when changing the filename');
    }

    const anchors = Array.isArray(artifact.identity_anchors) ? artifact.identity_anchors : [];
    const byField = new Map();
    for (const anchor of anchors) {
      if (byField.has(anchor.field)) artifactErrors.push(`duplicate identity anchor ${anchor.field}`);
      byField.set(anchor.field, anchor);
    }
    for (const field of requiredAnchorFields) if (!byField.has(field)) artifactErrors.push(`missing identity anchor ${field}`);

    const matches = new Set();
    for (const field of requiredAnchorFields) {
      const anchor = byField.get(field);
      if (!anchor) continue;
      if (!allowedAnchorResults.has(anchor.result)) {
        artifactErrors.push(`${field}: invalid result`);
        continue;
      }
      if (!anchor.note) artifactErrors.push(`${field}: note is required`);
      if (anchor.result === 'mismatch') artifactErrors.push(`${field}: document conflicts with the case`);
      if (anchor.result === 'match') {
        if (field === 'geometry') {
          matches.add(field);
        } else {
          if (!anchor.document_value || !anchor.case_value) artifactErrors.push(`${field}: document_value and case_value are required for a match`);
          else if (!valuesMatch(anchor.document_value, anchor.case_value)) artifactErrors.push(`${field}: stated match does not compare equal`);
          else {
            matches.add(field);
            if (caseCorpus && !valueKey(caseCorpus).includes(valueKey(anchor.case_value))) warnings.push(`${label} ${field}: case_value is not a literal ledger substring; preserve the note explaining the linkage`);
          }
        }
      }
      if (anchor.result === 'not_present' && anchor.document_value) artifactErrors.push(`${field}: document_value must be empty when not_present`);
    }

    const hasStrongIdentity = matches.has('parcel_id') || (matches.has('plat_subdivision') && matches.has('lot')) || (matches.has('street_address') && matches.has('owner_applicant'));
    if (!hasStrongIdentity) artifactErrors.push('no strong property identity match: require parcel, plat+lot, or street+owner');
    if (artifact.document_kind === 'map_or_plan' && !matches.has('geometry')) artifactErrors.push('map_or_plan requires an affirmative geometry match');
    if (matches.size < 2) artifactErrors.push('fewer than two identity anchors match');

    for (const message of artifactErrors) errors.push(`${label}: ${message}`);
    checkedArtifacts.push({
      source_id: artifact.source_id || null,
      agency_filename: artifact.agency_filename || null,
      planned_filename: artifact.planned_filename || null,
      sha256: actualSha256,
      matched_anchors: [...matches],
      result: artifactErrors.length ? 'BLOCKED' : 'PASS'
    });
  }

  return {ok: errors.length === 0, errors, warnings, artifacts: checkedArtifacts};
}

function main() {
  const manifestArg = process.argv[2];
  if (!manifestArg) throw new Error('Usage: node tools/delivery-gate.mjs storage/operations/delivery-gates/MANIFEST.json');
  if (!fs.existsSync(ledgerPath)) throw new Error('Private ledger missing');
  const manifestPath = path.resolve(manifestArg);
  if (!inside(operationsHome, manifestPath)) throw new Error('Gate manifest must remain inside storage/operations');
  const ledger = readJson(ledgerPath);
  const manifestBytes = fs.readFileSync(manifestPath);
  const manifest = JSON.parse(manifestBytes.toString('utf8').replace(/^\uFEFF/, ''));
  const blocklist = fs.existsSync(blocklistPath) ? readJson(blocklistPath).blocked_sources || [] : [];
  const result = evaluateDeliveryGate(ledger, manifest, {blockedSources: blocklist});
  if (!result.ok) {
    console.error('DELIVERY GATE: BLOCKED');
    for (const error of result.errors) console.error(`- ${error}`);
    process.exitCode = 1;
    return;
  }
  const receipt = {
    gate_version: 1,
    result: 'PASS',
    checked_at: new Date().toISOString(),
    expires_at: new Date(Date.now() + 24 * 60 * 60_000).toISOString(),
    ledger_revision: ledger.revision,
    manifest_sha256: crypto.createHash('sha256').update(manifestBytes).digest('hex'),
    case_id: manifest.case_id,
    purpose: manifest.purpose,
    recipient: manifest.recipient,
    subject: manifest.subject,
    reviewer: manifest.reviewer,
    artifacts: result.artifacts,
    warnings: result.warnings
  };
  const receiptPath = manifestPath.replace(/\.json$/i, '') + '.receipt.json';
  fs.writeFileSync(receiptPath, JSON.stringify(receipt, null, 2) + '\n');
  console.log(`DELIVERY GATE: PASS\nReceipt: ${receiptPath}`);
  for (const artifact of receipt.artifacts) console.log(`- ${artifact.source_id}: ${artifact.planned_filename} (${artifact.matched_anchors.join(', ')})`);
  for (const warning of result.warnings) console.log(`Warning: ${warning}`);
}

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  try { main(); } catch (error) { console.error(error.message); process.exitCode = 1; }
}
