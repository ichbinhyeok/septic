import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import crypto from 'node:crypto';
import {evaluateDeliveryGate} from './delivery-gate.mjs';

function gateFixture() {
  const operationsHome = fs.mkdtempSync(path.join(os.tmpdir(), 'septicpath-delivery-gate-'));
  fs.mkdirSync(path.join(operationsHome, 'sources'));
  const bytes = Buffer.from('verified record');
  fs.writeFileSync(path.join(operationsHome, 'sources', 'record.pdf'), bytes);
  const sha256 = crypto.createHash('sha256').update(bytes).digest('hex');
  const ledger = {
    revision: 5,
    cases: [{
      id: 'case-1', email: 'customer@example.com', property: '2949 Louisville Road',
      parcel: '025 126.03', known_facts: ['Joe H. Wright Property, Plat 1666A, Lot 1']
    }],
    sources: [{id: 'source-1', local_path: 'sources/record.pdf', sha256}]
  };
  const manifest = {
    gate_version: 1,
    purpose: 'customer_delivery',
    case_id: 'case-1',
    recipient: 'customer@example.com',
    subject: 'Verified record',
    reviewer: 'operator',
    reviewed_at: '2026-09-16T00:00:00Z',
    identity_review_complete: true,
    artifacts: [{
      source_id: 'source-1', agency_filename: 'record.pdf', planned_filename: 'verified-record.pdf',
      rename_reason: 'Clear customer label', document_kind: 'map_or_plan', page_count: 1, pages_reviewed: [1],
      identity_anchors: [
        {field: 'street_address', document_value: 'Louisville Road', case_value: 'Louisville Road', result: 'match', note: 'road matches'},
        {field: 'parcel_id', document_value: '126.03', case_value: '025 126.03', result: 'match', note: 'parcel matches'},
        {field: 'plat_subdivision', document_value: 'Plat 1666A', case_value: 'Plat 1666A', result: 'match', note: 'plat matches'},
        {field: 'lot', document_value: 'Lot 1', case_value: 'Lot 1', result: 'match', note: 'lot matches'},
        {field: 'owner_applicant', document_value: '', case_value: 'Joe H. Wright Property', result: 'not_present', note: 'not printed'},
        {field: 'geometry', document_value: 'reviewed', case_value: 'case geometry', result: 'match', note: 'geometry matches'}
      ]
    }]
  };
  return {operationsHome, ledger, manifest};
}

test('passes only after every page and required identity anchor is reviewed', t => {
  const f = gateFixture();
  t.after(() => fs.rmSync(f.operationsHome, {recursive: true, force: true}));
  const result = evaluateDeliveryGate(f.ledger, f.manifest, {operationsHome: f.operationsHome, now: '2026-09-16T01:00:00Z'});
  assert.equal(result.ok, true);
  assert.equal(result.artifacts[0].result, 'PASS');
});

test('blocks a visible property-identity conflict', t => {
  const f = gateFixture();
  t.after(() => fs.rmSync(f.operationsHome, {recursive: true, force: true}));
  const street = f.manifest.artifacts[0].identity_anchors.find(anchor => anchor.field === 'street_address');
  street.document_value = 'Martin Mill Pike';
  street.result = 'mismatch';
  const result = evaluateDeliveryGate(f.ledger, f.manifest, {operationsHome: f.operationsHome, now: '2026-09-16T01:00:00Z'});
  assert.equal(result.ok, false);
  assert.match(result.errors.join('\n'), /street_address: document conflicts/);
});

test('blocks an attachment when not every page was reviewed', t => {
  const f = gateFixture();
  t.after(() => fs.rmSync(f.operationsHome, {recursive: true, force: true}));
  f.manifest.artifacts[0].page_count = 2;
  const result = evaluateDeliveryGate(f.ledger, f.manifest, {operationsHome: f.operationsHome, now: '2026-09-16T01:00:00Z'});
  assert.equal(result.ok, false);
  assert.match(result.errors.join('\n'), /pages_reviewed must contain every page/);
});

test('blocklist overrides a superficially passing identity review', t => {
  const f = gateFixture();
  t.after(() => fs.rmSync(f.operationsHome, {recursive: true, force: true}));
  const result = evaluateDeliveryGate(f.ledger, f.manifest, {
    operationsHome: f.operationsHome,
    now: '2026-09-16T01:00:00Z',
    blockedSources: [{source_id: 'source-1', reason: 'known wrong property'}]
  });
  assert.equal(result.ok, false);
  assert.match(result.errors.join('\n'), /blocked source: known wrong property/);
});
