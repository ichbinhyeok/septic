import fs from 'node:fs';
import path from 'node:path';
import { pathToFileURL } from 'node:url';

const purposes = {
  location: 'Tank or drain-field location', inspection: 'Inspection or buying / selling',
  pumping: 'Pumping or maintenance', repair: 'Current problem or repair', building: 'Addition or installation',
  records: 'Record copies only', unsure: 'Purpose not classified', diagnosis: 'Site diagnosis',
  buying_home: 'Buying a home', new_install: 'New installation', replacement: 'Replacement',
  drainfield_replacement: 'Drain-field replacement', perc_test: 'Perc or soil evaluation'
};
const states = new Set('AL AK AZ AR CA CO CT DE FL GA HI ID IL IN IA KS KY LA ME MD MA MI MN MS MO MT NE NV NH NJ NM NY NC ND OH OK OR PA RI SC SD TN TX UT VT VA WA WV WI WY DC'.split(' '));
const roles = new Set(['owner', 'buyer', 'seller', 'agent', 'realtor', 'contractor', 'other']);
const text = v => typeof v === 'string' ? v : '';
const purpose = v => Object.hasOwn(purposes, v) ? v : 'unsure';
const region = v => states.has(v) ? v : 'Unknown';
const md = v => text(v).replace(/[\\`*_[\]<>#!|]/g, '\\$&').replace(/\r?\n/g, ' ');

export function intakeBrief(input) {
  const records = Boolean(input.requestId);
  const property = input.property || {};
  const contact = input.contact || {};
  if (!input.requestId && !input.leadId) throw new Error('Expected a stored intake with requestId or leadId');
  return {
    version: 1, sourceKind: 'intake', reference: text(input.requestId || input.leadId),
    inquiryClass: records ? 'Record research request; field work not confirmed' : 'Service inquiry; not a confirmed booking',
    receivedAt: text(input.submittedAt), state: region(records ? property.stateCode : input.stateCode),
    county: text(records ? property.countyName : input.countyName),
    purpose: purpose(records ? property.helpPurpose : input.projectType),
    role: roles.has(contact.transactionRole) ? contact.transactionRole : 'Unknown',
    timeframe: text(records ? property.timeline : input.userInputs?.timeline),
    address: text(property.address), email: text(contact.email), phone: text(contact.phone),
    phoneStatus: text(contact.phone) ? 'Supplied; not verified by this brief' : 'Not supplied in this intake',
    researchStatus: 'Agency findings not established by intake',
    researchSummary: 'Customer-reported record status: ' + (text(records ? property.recordStatus : input.recordStatus) || 'Unknown'),
    buyerStatus: text(input.commercialFollowup?.buyerAcceptance) || 'not_requested',
    sourceIds: [], consentVersion: text(input.consent?.languageVersion || input.consent?.consentLanguageVersion),
    acceptedTerms: text(input.consent?.consentText),
    nextAction: 'Review the original accepted terms, buyer area and inquiry definition before sharing.'
  };
}

export function caseBrief(ledger, caseId, selectedPurpose = 'unsure') {
  const c = ledger.cases.find(c => c.id === caseId);
  if (!c) throw new Error('Unknown case ID');
  if (!Object.hasOwn(purposes, selectedPurpose)) throw new Error('Unsupported purpose');
  return {
    version: 1, sourceKind: 'operations_ledger', ledgerRevision: ledger.revision, reference: c.id,
    inquiryClass: 'Historical record-help case; current field-work interest must be checked',
    receivedAt: text(c.intake_at), state: region(c.state), county: text(c.county),
    purpose: selectedPurpose, role: 'Unknown', timeframe: '', address: text(c.property), email: text(c.email), phone: '',
    phoneStatus: 'Unknown; consult the original intake', researchStatus: text(c.status), researchSummary: text(c.summary),
    customerRequest: text(c.intent), buyerStatus: text(c.commercial_followup?.last_outcome) || 'not_recorded',
    sourceIds: c.source_ids || [], routeIds: c.route_ids || [], nextAction: text(c.next_action),
    consentVersion: '', acceptedTerms: 'Not inferred from current website terms. Review the historical accepted terms.'
  };
}

export function renderBrief(brief, anonymous = false) {
  const lines = [anonymous ? '# Anonymous inquiry-format example' : '# Private commercial inquiry brief', '',
    anonymous ? 'Identity, county, dates, contact details, source IDs and free-text findings are deliberately omitted. Not a current sale offer.'
      : 'Internal review only. Not a work order, permission to share, or a substitute for the delivery gate.', '',
    `- State: ${region(brief.state)}`,
    `- Inquiry class: ${brief.sourceKind === 'operations_ledger' ? 'Historical record-help case; current interest unconfirmed' : brief.inquiryClass}`,
    `- Purpose: ${purposes[purpose(brief.purpose)]}${brief.sourceKind === 'operations_ledger' ? ' (operator classification)' : ' (submitted selection)'}`,
    '- Booking / job outcome: not established by this summary'
  ];
  if (anonymous) {
    lines.push('', 'A live handoff can include agreed geography, the submitted purpose, contact details within the accepted sharing scope, and reviewed research status. Original files are not included automatically.');
    return lines.join('\n') + '\n';
  }
  lines.push(`- Reference: ${md(brief.reference)}`, `- Received (UTC): ${md(brief.receivedAt) || 'Unknown'}`,
    `- County: ${md(brief.county) || 'Unknown'}`, `- Role: ${md(brief.role)}`,
    `- Stated timeframe: ${md(brief.timeframe) || 'Not supplied / unknown'}`,
    `- Phone status: ${md(brief.phoneStatus)}`, `- Buyer response: ${md(brief.buyerStatus)}`, '',
    '## Private contact', `- Property: ${md(brief.address) || 'See source intake'}`,
    `- Email: ${md(brief.email) || 'Unknown'}`, `- Phone: ${md(brief.phone) || 'Unknown'}`, '',
    '## Research context', `Status: ${md(brief.researchStatus)}`, '', md(brief.researchSummary), '',
    'This is the saved operating conclusion, not a new file review. Any external message relying on a customer-case file still requires the delivery gate.', '',
    '## Sharing review', `Accepted version: ${md(brief.consentVersion) || 'Not established in this source'}`, '', md(brief.acceptedTerms), '',
    'Do not infer current sharing permission from a phone number or retroactively apply new terms. No attachment is included.', '',
    '## Next action', md(brief.nextAction), '', `Source IDs: ${(brief.sourceIds || []).map(md).join(', ') || 'See stored intake'}`);
  return lines.join('\n') + '\n';
}

export function prepareOutcome(ledger, caseId, details, now = new Date()) {
  const c = ledger.cases.find(c => c.id === caseId);
  if (!c) throw new Error('Unknown case ID');
  const kinds = new Set(['offered', 'accepted', 'declined', 'paid', 'refunded', 'contact_reached']);
  if (!kinds.has(details.kind)) throw new Error('Unsupported outcome');
  for (const key of ['id', 'provider_id']) if (!/^[a-zA-Z0-9._-]{1,160}$/.test(details[key] || '')) throw new Error(`Valid ${key} required`);
  const at = Date.parse(details.occurred_at);
  if (!Number.isFinite(at) || at > now.getTime()) throw new Error('Actual non-future occurred_at required');
  const sources = new Set(ledger.sources.map(s => s.id));
  if (!Array.isArray(details.source_ids) || !details.source_ids.length || details.source_ids.some(id => !sources.has(id))) throw new Error('Existing evidence source IDs required');
  const previous = c.commercial_followup?.events || [];
  if (ledger.cases.some(row => row.commercial_followup?.events?.some(e => e.id === details.id))) throw new Error('Duplicate commercial event ID');
  const money = details.kind === 'paid' || details.kind === 'refunded';
  if (money && (!Number.isSafeInteger(details.amount_cents) || details.amount_cents <= 0)) throw new Error('Positive integer amount_cents required for received money or refunds');
  if (!money && details.amount_cents !== undefined) throw new Error('Only received payments and refunds have amounts');
  const providerNet = previous.filter(e => e.provider_id === details.provider_id)
    .reduce((n, e) => n + (e.kind === 'paid' ? e.amount_cents : e.kind === 'refunded' ? -e.amount_cents : 0), 0);
  if (details.kind === 'refunded' && details.amount_cents > providerNet) throw new Error('Refund exceeds recorded receipts for this provider');
  const entry = { id: details.id, kind: details.kind, provider_id: details.provider_id, occurred_at: new Date(at).toISOString(),
    source_ids: [...new Set(details.source_ids)], note: text(details.note), ...(money ? { amount_cents: details.amount_cents, currency: 'USD' } : {}) };
  const events = [...previous, entry];
  const net = events.reduce((n, e) => n + (e.kind === 'paid' ? e.amount_cents : e.kind === 'refunded' ? -e.amount_cents : 0), 0);
  const last = [...events].sort((a, b) => Date.parse(a.occurred_at) - Date.parse(b.occurred_at)).at(-1);
  return { expected_revision: ledger.revision, reason: 'Record an evidenced commercial outcome; no outreach is performed',
    upsert: { cases: [{ id: caseId, commercial_followup: { ...c.commercial_followup, events, last_outcome: last.kind, net_received_cents: net, currency: 'USD' } }] },
    event: { case_id: caseId, summary: `Commercial ${entry.kind} recorded for ${entry.provider_id}; not evidence of completed field work.`, source_ids: entry.source_ids } };
}

function writeNew(file, contents) { fs.writeFileSync(file, contents, { encoding: 'utf8', flag: 'wx' }); }

export function run(argv, cwd = process.cwd()) {
  const [command, input, ...rest] = argv;
  const root = path.resolve(cwd, 'storage/operations');
  if (!fs.existsSync(path.join(root, 'ledger.json'))) throw new Error('Private ledger missing; recover it before proceeding');
  const ledger = JSON.parse(fs.readFileSync(path.join(root, 'ledger.json'), 'utf8'));
  if (command === 'case' || command === 'intake') {
    let selectedPurpose = 'unsure';
    if (rest.length) {
      if (command !== 'case') throw new Error('An intake brief uses its submitted purpose; --purpose is only for case review');
      if (rest.length !== 2 || rest[0] !== '--purpose') throw new Error('Only --purpose <known-purpose> is supported');
      selectedPurpose = rest[1];
    }
    const brief = command === 'case' ? caseBrief(ledger, input, selectedPurpose)
      : intakeBrief(JSON.parse(fs.readFileSync(path.resolve(cwd, input), 'utf8')));
    const output = fs.mkdtempSync(path.join(root, 'commercial-brief-'));
    writeNew(path.join(output, 'internal.md'), renderBrief(brief));
    writeNew(path.join(output, 'anonymous.md'), renderBrief(brief, true));
    writeNew(path.join(output, 'brief.json'), JSON.stringify(brief, null, 2) + '\n');
    return { output, externalActions: 0 };
  }
  if (command === 'prepare-outcome') {
    if (rest.length !== 1) throw new Error('Usage: prepare-outcome CASE_ID evidence-details.json');
    const details = JSON.parse(fs.readFileSync(path.resolve(cwd, rest[0]), 'utf8'));
    const change = prepareOutcome(ledger, input, details);
    const output = fs.mkdtempSync(path.join(root, 'commercial-outcome-'));
    writeNew(path.join(output, 'change.json'), JSON.stringify(change, null, 2) + '\n');
    return { change: path.join(output, 'change.json'), applied: false, externalActions: 0,
      next: 'Review actual evidence, then use node tools/operations.mjs apply <change.json> and validate. Nothing was sent or recorded as completed by this preparation.' };
  }
  throw new Error('Use: case CASE_ID [--purpose location] | intake PRIVATE_INTAKE.json | prepare-outcome CASE_ID DETAILS.json');
}

if (process.argv[1] && import.meta.url === pathToFileURL(path.resolve(process.argv[1])).href) {
  try { console.log(JSON.stringify(run(process.argv.slice(2)), null, 2)); }
  catch (error) { console.error(error.message); process.exitCode = 1; }
}
