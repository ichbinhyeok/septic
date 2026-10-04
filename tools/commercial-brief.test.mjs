import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { intakeBrief, caseBrief, renderBrief, prepareOutcome, run } from './commercial-brief.mjs';
import { applyChange, validate } from './operations.mjs';

const ledger = () => ({ revision: 7, sources: [{ id: 'evidence-1' }], cases: [{ id: 'case-synthetic', state: 'NC', county: 'Private County',
  property: '99 SECRET ADDRESS', email: 'private@example.com', intake_at: '2026-10-01T00:00:00Z',
  status: 'waiting_agency', summary: 'Parcel SECRET-123: agency file pending', intent: 'Private customer request', source_ids: ['evidence-1'] }] });
const detail = (kind = 'accepted') => ({ id: 'test-event', provider_id: 'test-provider', kind,
  occurred_at: '2026-10-03T00:00:00Z', source_ids: ['evidence-1'] });

test('anonymous case example excludes identifiers, dates, contacts, free text and county', () => {
  const brief = caseBrief(ledger(), 'case-synthetic', 'location');
  const output = renderBrief(brief, true);
  for (const value of ['SECRET', 'private@example.com', 'Private County', '2026-10-01', 'case-synthetic', 'evidence-1', 'Private customer']) assert.ok(!output.includes(value));
  assert.match(output, /NC/); assert.match(output, /Historical/); assert.match(output, /not a current sale offer/i);
  assert.match(renderBrief(brief), /99 SECRET ADDRESS/);
});

test('untrusted purpose and state cannot leak into anonymous preview', () => {
  const brief = intakeBrief({ requestId: 'private-id', property: { stateCode: 'secret@example.com', helpPurpose: 'secret@example.com' }, contact: {} });
  assert.ok(!renderBrief(brief, true).includes('secret@example.com'));
  assert.equal(brief.purpose, 'unsure'); assert.equal(brief.state, 'Unknown');
});

test('service intake is not upgraded to a booking and missing fields remain unknown', () => {
  const brief = intakeBrief({ leadId: 'service', projectType: 'pumping', contact: { phone: '9195550100' }, userInputs: { timeline: '' } });
  assert.match(brief.inquiryClass, /not a confirmed booking/); assert.match(brief.phoneStatus, /not verified/);
  assert.equal(brief.timeframe, ''); assert.match(brief.researchStatus, /not established/);
});

test('accepted terms retain the actual record-help or service version key', () => {
  assert.equal(intakeBrief({ requestId: 'record', consent: { languageVersion: 'record-version' } }).consentVersion, 'record-version');
  assert.equal(intakeBrief({ leadId: 'service', consent: { consentLanguageVersion: 'service-version' } }).consentVersion, 'service-version');
});

test('CLI writes private artifacts without modifying the ledger or performing outreach', () => {
  const temp = fs.mkdtempSync(path.join(os.tmpdir(), 'septic-commercial-test-'));
  try {
    const root = path.join(temp, 'storage/operations');
    fs.mkdirSync(root, { recursive: true });
    const original = JSON.stringify(ledger());
    fs.writeFileSync(path.join(root, 'ledger.json'), original);
    const result = run(['case', 'case-synthetic', '--purpose', 'location'], temp);
    assert.equal(result.externalActions, 0);
    assert.ok(result.output.startsWith(root + path.sep));
    assert.match(fs.readFileSync(path.join(result.output, 'internal.md'), 'utf8'), /99 SECRET ADDRESS/);
    assert.ok(!fs.readFileSync(path.join(result.output, 'anonymous.md'), 'utf8').includes('SECRET'));
    fs.writeFileSync(path.join(temp, 'evidence.json'), JSON.stringify(detail()));
    const outcome = run(['prepare-outcome', 'case-synthetic', 'evidence.json'], temp);
    assert.equal(outcome.applied, false);
    assert.equal(outcome.externalActions, 0);
    assert.equal(JSON.parse(fs.readFileSync(outcome.change, 'utf8')).expected_revision, 7);
    assert.equal(fs.readFileSync(path.join(root, 'ledger.json'), 'utf8'), original);
  } finally {
    fs.rmSync(temp, { recursive: true, force: true });
  }
});

test('acceptance records no revenue and leaves operational status alone', () => {
  const change = prepareOutcome(ledger(), 'case-synthetic', detail());
  assert.equal(change.expected_revision, 7); assert.equal(change.upsert.cases[0].commercial_followup.net_received_cents, 0);
  assert.ok(!('status' in change.upsert.cases[0]));
});

test('payment and refund track actual net receipts, with provider-specific refund cap', () => {
  const l = ledger();
  l.cases[0].commercial_followup = prepareOutcome(l, 'case-synthetic', { ...detail('paid'), amount_cents: 1200 }).upsert.cases[0].commercial_followup;
  const refund = prepareOutcome(l, 'case-synthetic', { ...detail('refunded'), id: 'refund-1', amount_cents: 300 });
  assert.equal(refund.upsert.cases[0].commercial_followup.net_received_cents, 900);
  assert.throws(() => prepareOutcome(l, 'case-synthetic', { ...detail('refunded'), id: 'refund-2', amount_cents: 1201 }), /exceeds/);
  assert.throws(() => prepareOutcome(l, 'case-synthetic', { ...detail('refunded'), id: 'refund-3', provider_id: 'other', amount_cents: 1 }), /exceeds/);
  assert.throws(() => prepareOutcome(l, 'case-synthetic', detail()), /Duplicate/);
});

test('outcomes require real source references, valid money and actual timestamps', () => {
  for (const invalid of [{ ...detail(), source_ids: [] }, { ...detail(), source_ids: ['missing'] },
    { ...detail(), occurred_at: '2099-01-01' }, { ...detail('paid'), amount_cents: 1.5 },
    { ...detail('paid'), amount_cents: -1 }, { ...detail(), amount_cents: 200 }]) {
    assert.throws(() => prepareOutcome(ledger(), 'case-synthetic', invalid));
  }
});

test('backfilled older evidence does not replace newest outcome', () => {
  const l = ledger();
  l.cases[0].commercial_followup = prepareOutcome(l, 'case-synthetic', { ...detail('paid'), amount_cents: 1200 }).upsert.cases[0].commercial_followup;
  const change = prepareOutcome(l, 'case-synthetic', { ...detail('offered'), id: 'old-event', occurred_at: '2026-10-02T00:00:00Z' });
  assert.equal(change.upsert.cases[0].commercial_followup.last_outcome, 'paid');
});

test('prepared commercial change passes the real transactional ledger validator', () => {
  const l = ledger();
  Object.assign(l, { schema_version: 2, sync: {}, branches: [], routes: [], route_observations: [], growth_signals: [],
    events: [], tasks: [], lessons: [], inbox: [] });
  Object.assign(l.cases[0], { customer: 'Synthetic', intake_message_id: 'evidence-1', owner: 'operator',
    next_check: '2026-10-06', next_action: 'Check agency response', request_ids: [], route_ids: [], branch_ids: [] });
  assert.deepEqual(validate(l), []);
  const next = applyChange(l, prepareOutcome(l, 'case-synthetic', detail()));
  assert.deepEqual(validate(next), []);
  assert.equal(next.cases[0].status, 'waiting_agency');
  assert.equal(next.cases[0].commercial_followup.last_outcome, 'accepted');
  assert.equal(next.events.at(-1).case_id, 'case-synthetic');
  assert.equal(l.cases[0].commercial_followup, undefined);
});
