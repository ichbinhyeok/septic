#!/usr/bin/env node
// Private, local-first operations ledger. No network access or email sending.
import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import {fileURLToPath} from 'node:url';

export const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const home = path.join(root, 'storage/operations');
const db = path.join(home, 'ledger.json');
export const collections = ['cases', 'branches', 'routes', 'route_observations', 'growth_signals', 'events', 'sources', 'tasks', 'lessons', 'inbox'];
const caseStates = new Set(['new', 'researching', 'waiting_customer', 'waiting_agency', 'blocked_intake', 'delivery_issue', 'delivered', 'delivered_limited', 'closed']);
const closed = new Set(['delivered', 'delivered_limited', 'closed']);
const branchStates = new Set(['planned', 'researching', 'waiting_customer', 'sent_unconfirmed', 'acknowledged', 'blocked_intake', 'delivery_issue', 'metadata_only', 'official_no_match', 'inconclusive', 'received', 'delivered', 'not_requested']);
const sha = s => crypto.createHash('sha256').update(s).digest('hex');
const now = () => new Date().toISOString();
const json = p => JSON.parse(fs.readFileSync(p, 'utf8').replace(/^\uFEFF/, ''));
const escape = x => String(x ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const cell = x => String(x ?? '').replace(/\|/g, '\\|').replace(/\r?\n/g, ' ');
const observationKinds = new Set(['research','waiting_customer','request_sent','acknowledged','blocked','delivery_issue','metadata_only','no_match','inconclusive','record_received','delivered','not_requested']);
const observedStatusKind = status => ({
  planned:'research', researching:'research', waiting_customer:'waiting_customer',
  sent_unconfirmed:'request_sent', acknowledged:'acknowledged', blocked_intake:'blocked',
  delivery_issue:'delivery_issue', metadata_only:'metadata_only', official_no_match:'no_match',
  inconclusive:'inconclusive', received:'record_received', delivered:'delivered', not_requested:'not_requested'
}[status] || 'research');
const observationFingerprint = branch => JSON.stringify({
  status:branch?.status, custodian:branch?.custodian, request_number:branch?.request_number,
  summary:branch?.summary, route_ids:branch?.route_ids || [], source_ids:branch?.source_ids || []
});

export function upgradeLedger(d) {
  const n = structuredClone(d);
  if (n.schema_version === 1) {
    n.schema_version = 2;
    n.route_observations = n.route_observations || [];
  }
  n.growth_signals = n.growth_signals || [];
  return n;
}

function appendRouteObservation(d, branch, observedAt, seeded=false) {
  for (const routeId of branch.route_ids || []) {
    d.route_observations.push({
      id:crypto.randomUUID(), route_id:routeId, case_id:branch.case_id, branch_id:branch.id,
      observed_at:observedAt, kind:observedStatusKind(branch.status), status:branch.status,
      custodian:branch.custodian || null, request_number:branch.request_number || null,
      summary:branch.summary || '', source_ids:branch.source_ids || [], seeded
    });
  }
}

export function routeIntelligence(d) {
  const normalized = upgradeLedger(d);
  return normalized.routes.map(route => {
    const observations = normalized.route_observations
      .filter(item => item.route_id === route.id)
      .sort((left,right) => left.observed_at.localeCompare(right.observed_at));
    const branchIds = [...new Set(observations.map(item => item.branch_id))];
    const caseIds = [...new Set(observations.map(item => item.case_id))];
    const statusCounts = Object.fromEntries([...observationKinds].map(kind => [kind, observations.filter(item => item.kind === kind).length]).filter(([,count]) => count));
    const responseHours = [];
    for (const branchId of branchIds) {
      const timeline = observations.filter(item => item.branch_id === branchId);
      const sent = timeline.find(item => item.kind === 'request_sent');
      const answered = sent && timeline.find(item => item.observed_at >= sent.observed_at && ['acknowledged','record_received','no_match','delivered'].includes(item.kind));
      if (sent && answered) responseHours.push((Date.parse(answered.observed_at) - Date.parse(sent.observed_at)) / 3_600_000);
    }
    responseHours.sort((a,b) => a-b);
    const middle = Math.floor(responseHours.length / 2);
    const medianResponseHours = responseHours.length === 0 ? null : responseHours.length % 2
      ? responseHours[middle]
      : (responseHours[middle - 1] + responseHours[middle]) / 2;
    const verifiedOutcomeBranches = new Set(observations.filter(item => ['record_received','no_match','delivered'].includes(item.kind)).map(item => item.branch_id));
    const deliveredCaseCount = caseIds.filter(caseId => closed.has(normalized.cases.find(item => item.id === caseId)?.status)).length;
    return {
      route_id:route.id, title:route.title, state:route.state, county:route.county,
      record_type:route.record_type, verification:route.verification,
      first_action:route.first_action, fallback:route.fallback, limitations:route.limitations,
      availability:route.availability || 'unknown', primary_channel:route.primary_channel || 'unknown',
      requester_requirements:route.requester_requirements || [], fee_policy:route.fee_policy || 'unknown',
      case_count:caseIds.length, delivered_case_count:deliveredCaseCount, branch_count:branchIds.length,
      observation_count:observations.length, verified_outcome_count:verifiedOutcomeBranches.size,
      status_counts:statusCounts, response_time_sample_count:responseHours.length,
      median_response_hours:medianResponseHours, last_observed:observations.at(-1)?.observed_at || null,
      last_verified:route.last_verified, source_ids:route.source_ids || []
    };
  });
}

function latestGrowthSignals(d) {
  const latest = new Map();
  for (const signal of upgradeLedger(d).growth_signals) {
    const key = `${signal.platform}|${signal.scope_type}|${signal.scope_key}`;
    const previous = latest.get(key);
    if (!previous || `${signal.window_end}|${signal.observed_at}` > `${previous.window_end}|${previous.observed_at}`) latest.set(key, signal);
  }
  return [...latest.values()];
}

export function growthOpportunities(d) {
  const signals = latestGrowthSignals(d);
  const publishedProofRoutes = publicProofRouteIds();
  return routeIntelligence(d).map(route => {
    const linked = signals.filter(signal => (signal.route_ids || []).includes(route.route_id));
    const search = linked.filter(signal => ['bing_search','google_search_console'].includes(signal.platform));
    const impressions = search.reduce((total,signal) => total + Number(signal.metrics.impressions || 0), 0);
    const clicks = search.reduce((total,signal) => total + Number(signal.metrics.clicks || 0), 0);
    const ctr = impressions ? clicks / impressions : null;
    const aiCitations = linked.filter(signal => signal.platform === 'bing_ai').reduce((total,signal) => total + Number(signal.metrics.citations || 0), 0);
    let opportunityType = 'collect_more_evidence';
    let nextAction = 'Continue using the route and capture the next verified outcome.';
    if (impressions >= 1000 && ctr !== null && ctr < 0.025) {
      opportunityType = 'ctr_and_handoff';
      nextAction = 'Protect ranking, test one search snippet variable, and make the verified route the first useful action.';
    } else if (route.delivered_case_count > 0 && publishedProofRoutes.has(route.route_id)) {
      opportunityType = 'measure_published_proof';
      nextAction = 'The anonymized route proof is published. Measure indexing, impressions, clicks, and qualified requests before expanding it.';
    } else if (route.delivered_case_count > 0) {
      opportunityType = 'publish_verified_proof';
      nextAction = 'Create or strengthen the matching public route page using anonymized, source-backed outcome proof.';
    } else if (route.case_count > 0 && route.verified_outcome_count === 0) {
      opportunityType = 'operational_bottleneck';
      nextAction = 'Resolve the intake or agency bottleneck before driving more search demand to this route.';
    } else if (aiCitations >= 500 && route.case_count === 0) {
      opportunityType = 'citation_without_case_signal';
      nextAction = 'Improve the action handoff and measure qualified requests before expanding citation content.';
    }
    const score = Math.round(
      Math.log10(impressions + 1) * 20 + Math.log10(aiCitations + 1) * 10
      + route.case_count * 10 + route.verified_outcome_count * 10 + route.delivered_case_count * 20
    );
    return {
      route_id:route.route_id, title:route.title, opportunity_score:score,
      opportunity_type:opportunityType, search_impressions:impressions,
      search_clicks:clicks, search_ctr:ctr, ai_citations:aiCitations,
      case_count:route.case_count, delivered_case_count:route.delivered_case_count, verified_outcome_count:route.verified_outcome_count,
      next_action:nextAction
    };
  }).sort((left,right) => right.opportunity_score - left.opportunity_score || left.route_id.localeCompare(right.route_id));
}

export function caseAttribution(d) {
  const rows = upgradeLedger(d).cases.map(caseRow => ({
    case_id: caseRow.id,
    intake_at: caseRow.intake_at || null,
    state: caseRow.state,
    county: caseRow.county,
    status: caseRow.status,
    outcome: closed.has(caseRow.status) ? 'delivered' : caseRow.status === 'blocked_intake' ? 'blocked' : 'open',
    entry_page: caseRow.entry_page || null,
    source_context: caseRow.source_context || 'unknown',
    attribution_key: caseRow.entry_page || `context:${caseRow.source_context || 'unknown'}`,
    route_ids: caseRow.route_ids || []
  }));
  const groups = new Map();
  for (const row of rows) {
    const key = `${row.attribution_key}|${row.source_context}`;
    const group = groups.get(key) || {
      attribution_key: row.attribution_key,
      entry_page: row.entry_page,
      source_context: row.source_context,
      case_count: 0,
      delivered_count: 0,
      delivered_limited_count: 0,
      open_count: 0,
      blocked_count: 0,
      observed_delivery_rate: 0,
      states: new Set(),
      counties: new Set(),
      case_ids: [],
      route_ids: new Set()
    };
    group.case_count += 1;
    if (row.status === 'delivered') group.delivered_count += 1;
    if (row.status === 'delivered_limited') group.delivered_limited_count += 1;
    if (row.outcome === 'open') group.open_count += 1;
    if (row.outcome === 'blocked') group.blocked_count += 1;
    group.states.add(row.state);
    group.counties.add(`${row.county}, ${row.state}`);
    group.case_ids.push(row.case_id);
    row.route_ids.forEach(routeId => group.route_ids.add(routeId));
    groups.set(key, group);
  }
  const summary = [...groups.values()].map(group => ({
    ...group,
    observed_delivery_rate: (group.delivered_count + group.delivered_limited_count) / group.case_count,
    states: [...group.states].sort(),
    counties: [...group.counties].sort(),
    route_ids: [...group.route_ids].sort()
  })).sort((left, right) =>
    right.case_count - left.case_count
    || right.observed_delivery_rate - left.observed_delivery_rate
    || left.attribution_key.localeCompare(right.attribution_key));
  return {rows, summary};
}

export function publicProofRouteIds() {
  const publicPagesPath = path.join(root, 'data/raw/county_records_pages.json');
  if (!fs.existsSync(publicPagesPath)) return new Set();
  const pages = json(publicPagesPath).pages || [];
  return new Set(pages
    .filter(page => page.publishStatus === 'published' && page.operationalProof?.routeId)
    .map(page => page.operationalProof.routeId));
}

export function validate(d) {
  d = upgradeLedger(d);
  const errors = [];
  if (d.schema_version !== 2 || !Number.isInteger(d.revision)) errors.push('Invalid schema/revision');
  for (const k of collections) {
    if (!Array.isArray(d[k])) { errors.push(`Missing ${k}`); continue; }
    const ids = d[k].map(x => x.id);
    if (ids.some(x => !x) || new Set(ids).size !== ids.length) errors.push(`Missing/duplicate ID in ${k}`);
  }
  if (errors.length) return errors;
  const ids = Object.fromEntries(collections.map(k=>[k,new Set(d[k].map(x=>x.id))]));
  const date = x => !x || (/^\d{4}-\d{2}-\d{2}$/.test(x) && !isNaN(Date.parse(x)));
  const reqs = new Set();
  for (const c of d.cases) {
    if (!caseStates.has(c.status)) errors.push(`${c.id}: invalid status`);
    if (!c.intent || !c.summary || !c.next_action) errors.push(`${c.id}: missing intent/summary/action`);
    if (!/^[A-Z]{2}$/.test(c.state)) errors.push(`${c.id}: invalid state`);
    if (/^Email:/.test(c.customer)) errors.push(`${c.id}: intake field spillover`);
    if (!closed.has(c.status) && (!c.next_check || !c.owner)) errors.push(`${c.id}: open case needs owner/check date`);
    if (!date(c.deadline) || !date(c.next_check)) errors.push(`${c.id}: invalid date`);
    if (closed.has(c.status) && !c.closure_evidence?.length) errors.push(`${c.id}: completion needs delivery evidence`);
    for (const r of c.request_ids || []) { if (reqs.has(r)) errors.push(`Duplicate intake ${r}`); reqs.add(r); }
  }
  for (const b of d.branches) {
    if (!ids.cases.has(b.case_id)) errors.push(`${b.id}: missing case`);
    if (!branchStates.has(b.status)) errors.push(`${b.id}: invalid branch status`);
    if (!date(b.next_check)) errors.push(`${b.id}: invalid check date`);
    if (['acknowledged','received','official_no_match','delivered','delivery_issue'].includes(b.status) && !b.source_ids?.length) errors.push(`${b.id}: state requires evidence`);
  }
  for (const k of ['cases','branches','routes','growth_signals','events','tasks','lessons']) for (const row of d[k]) {
    for (const s of [...(row.source_ids||[]), ...(row.closure_evidence||[])]) if (!ids.sources.has(s)) errors.push(`${row.id}: unknown source ${s}`);
    for (const r of row.route_ids||[]) if (!ids.routes.has(r)) errors.push(`${row.id}: unknown route ${r}`);
    for (const b of row.branch_ids||[]) if (!ids.branches.has(b)) errors.push(`${row.id}: unknown branch ${b}`);
    if (row.case_id && !ids.cases.has(row.case_id)) errors.push(`${row.id}: unknown case`);
  }
  for (const t of d.tasks) if (!t.owner || !date(t.due) || (!t.done && !t.due)) errors.push(`${t.id}: task needs owner and valid due date`);
  for (const i of d.inbox) if (!ids.sources.has(i.source_id)) errors.push(`${i.id}: inbox source missing`);
  for (const o of d.route_observations) {
    if (!ids.routes.has(o.route_id) || !ids.branches.has(o.branch_id) || !ids.cases.has(o.case_id)) errors.push(`${o.id}: observation link missing`);
    if (!observationKinds.has(o.kind) || !o.observed_at || Number.isNaN(Date.parse(o.observed_at))) errors.push(`${o.id}: invalid route observation`);
    for (const sourceId of o.source_ids || []) if (!ids.sources.has(sourceId)) errors.push(`${o.id}: unknown source ${sourceId}`);
  }
  for (const signal of d.growth_signals) {
    if (!['bing_search','bing_ai','google_search_console','ga4','first_party_intake'].includes(signal.platform)) errors.push(`${signal.id}: invalid growth platform`);
    if (!signal.observed_at || Number.isNaN(Date.parse(signal.observed_at)) || !date(signal.window_start) || !date(signal.window_end)) errors.push(`${signal.id}: invalid growth dates`);
    if (!signal.scope_type || !signal.scope_key || !signal.metrics || typeof signal.metrics !== 'object') errors.push(`${signal.id}: incomplete growth signal`);
  }
  return errors;
}

export function applyChange(d, change) {
  if (change.expected_revision !== d.revision) throw new Error('Revision conflict; reload ledger before applying');
  if (!change.reason || !change.event?.summary) throw new Error('Reason and event summary required');
  const upgrading = d.schema_version === 1;
  const n = upgradeLedger(d);
  const previousBranches = new Map(n.branches.map(branch => [branch.id, structuredClone(branch)]));
  for (const [kind, rows] of Object.entries(change.upsert || {})) {
    if (!collections.includes(kind) || ['events','route_observations'].includes(kind)) throw new Error(`Invalid upsert collection: ${kind}`);
    for (const row of rows) {
      const index = n[kind].findIndex(x=>x.id===row.id);
      if (index >= 0 && ['sources','growth_signals'].includes(kind) && JSON.stringify(n[kind][index]) !== JSON.stringify(row)) throw new Error('Evidence and growth snapshots are immutable; add a correction/new row');
      if (index >= 0) n[kind][index] = {...n[kind][index], ...row}; else n[kind].push(row);
    }
  }
  n.revision++;
  n.updated_at = now();
  if (upgrading) {
    for (const branch of n.branches) appendRouteObservation(n, branch, n.updated_at, true);
  } else {
    for (const changed of change.upsert?.branches || []) {
      const current = n.branches.find(branch => branch.id === changed.id);
      const previous = previousBranches.get(changed.id);
      if (current && observationFingerprint(previous) !== observationFingerprint(current)) appendRouteObservation(n, current, n.updated_at);
    }
  }
  n.events.push({id:crypto.randomUUID(), timestamp:n.updated_at, type:'operator_update', ...change.event, reason:change.reason});
  if (change.sync_checkpoint) {
    if (n.inbox.some(x=>x.status==='untriaged')) throw new Error('Triage inbox items before advancing checkpoint');
    n.sync = {...n.sync, ...change.sync_checkpoint};
  }
  const errors = validate(n); if (errors.length) throw new Error(errors.join('\n'));
  return n;
}

function save(d) {
  fs.mkdirSync(path.join(home,'backups'), {recursive:true});
  if (fs.existsSync(db)) {
    const old = fs.readFileSync(db);
    const backup = path.join(home,'backups',`ledger-${Date.now()}-${sha(old).slice(0,12)}.json`);
    fs.writeFileSync(backup,old,{flag:'wx'});
  }
  const tmp = db+'.'+crypto.randomUUID()+'.tmp';
  fs.writeFileSync(tmp,JSON.stringify(d,null,2)+'\n',{flag:'wx'});
  fs.renameSync(tmp,db);
}

function commit(d, expectedRevision) {
  const lock=path.join(home,'write.lock');
  let fd;
  try { fd=fs.openSync(lock,'wx'); } catch { throw new Error('Another ledger write is active. Retry after it finishes; do not remove another process lock.'); }
  try {
    if(json(db).revision!==expectedRevision)throw new Error('Revision conflict on disk; reload before retrying');
    save(d);
    return render(d);
  } finally { fs.closeSync(fd); fs.unlinkSync(lock); }
}

function sourceLink(d, id) {
  const s=d.sources.find(s=>s.id===id);
  if (!s) return escape(id);
  return `<a href="${escape(s.local_path||s.url||'#')}" target="_blank" rel="noreferrer">${escape(s.title||id)}</a>`;
}

export function render(d) {
  d=upgradeLedger(d);
  const errors=validate(d); if(errors.length) throw new Error(errors.join('\n'));
  fs.mkdirSync(path.join(home,'views'),{recursive:true});
  const states={waiting_agency:'기관 대기',waiting_customer:'고객 대기',blocked_intake:'접수 막힘',delivery_issue:'발송 문제',delivered:'전달 완료',delivered_limited:'한계 설명 후 전달',researching:'조사 중',new:'신규',closed:'종료'};
  const tasks = [...d.tasks.filter(t=>!t.done), ...d.branches.filter(b=>b.next_check).map(b=>({id:b.id,case_id:b.case_id,due:b.next_check,owner:b.owner||'SepticPath founder',action:b.next_action,status:b.status}))].sort((a,b)=>a.due.localeCompare(b.due));
  let md=`# SepticPath 운영대장\n\n기록 갱신: ${d.updated_at} / revision ${d.revision}\n\n메일 확인 기준: ${d.sync.checked_through} — 자동 메일 동기화는 실행 중이지 않습니다.\n\n## 다음 행동\n\n| 확인일 | 케이스 | 할 일 |\n|---|---|---|\n`;
  md+=tasks.map(t=>`| ${t.due} | ${cell(d.cases.find(c=>c.id===t.case_id)?.customer||'공통 운영')} | ${cell(t.action)} |`).join('\n');
  md+='\n\n## 고객 현황\n\n| 고객 | 지역 / 필지 | 의도 | 상태 | 현재 결론 | 다음 행동 | 마감 |\n|---|---|---|---|---|---|---|\n';
  md+=d.cases.map(c=>`| ${cell(c.customer)} | ${cell(c.state+' / '+c.county+' / '+c.parcel)} | ${cell(c.intent)} | ${states[c.status]||c.status} | ${cell(c.summary)} | ${cell(c.next_action)} | ${c.deadline||'미제공'} |`).join('\n');
  fs.writeFileSync(path.join(home,'STATUS.md'),md+'\n');
  const csv=(rows,fields)=>'\uFEFF'+fields.join(',')+'\n'+rows.map(r=>fields.map(f=>'"'+String(Array.isArray(r[f])?r[f].join(' | '):r[f]??'').replace(/"/g,'""')+'"').join(',')).join('\n')+'\n';
  fs.writeFileSync(path.join(home,'views/cases.csv'),csv(d.cases,['id','customer','email','property','state','county','parcel','intent','status','summary','source_context','entry_page','deadline','next_check','next_action','last_customer_update']));
  fs.writeFileSync(path.join(home,'views/branches.csv'),csv(d.branches,['id','case_id','topic','custodian','status','summary','request_number','next_check','next_action','source_ids','route_ids']));
  fs.writeFileSync(path.join(home,'views/routes.csv'),csv(d.routes,['id','title','state','county','record_type','verification','last_verified','first_action','fallback','limitations','source_ids']));
  const intelligence=routeIntelligence(d);
  fs.writeFileSync(path.join(home,'views/route-intelligence.json'),JSON.stringify(intelligence,null,2)+'\n');
  fs.writeFileSync(path.join(home,'views/route-intelligence.csv'),csv(intelligence.map(item=>({...item,status_counts:JSON.stringify(item.status_counts),requester_requirements:item.requester_requirements})),['route_id','title','state','county','record_type','verification','availability','primary_channel','fee_policy','case_count','delivered_case_count','branch_count','observation_count','verified_outcome_count','response_time_sample_count','median_response_hours','last_observed','last_verified','status_counts','requester_requirements']));
  fs.writeFileSync(path.join(home,'views/growth-signals.json'),JSON.stringify(d.growth_signals,null,2)+'\n');
  fs.writeFileSync(path.join(home,'views/growth-signals.csv'),csv(d.growth_signals.map(signal=>({...signal,metrics:JSON.stringify(signal.metrics)})),['id','observed_at','platform','window_start','window_end','scope_type','scope_key','metrics','route_ids','notes']));
  const opportunities=growthOpportunities(d);
  fs.writeFileSync(path.join(home,'views/growth-opportunities.json'),JSON.stringify(opportunities,null,2)+'\n');
  fs.writeFileSync(path.join(home,'views/growth-opportunities.csv'),csv(opportunities,['route_id','title','opportunity_score','opportunity_type','search_impressions','search_clicks','search_ctr','ai_citations','case_count','delivered_case_count','verified_outcome_count','next_action']));
  const attribution=caseAttribution(d);
  fs.writeFileSync(path.join(home,'views/case-attribution.json'),JSON.stringify(attribution,null,2)+'\n');
  fs.writeFileSync(path.join(home,'views/case-attribution.csv'),csv(attribution.summary.map(row=>({...row,states:row.states.join(' | '),counties:row.counties.join(' | '),case_ids:row.case_ids.join(' | '),route_ids:row.route_ids.join(' | ')})),['attribution_key','entry_page','source_context','case_count','delivered_count','delivered_limited_count','open_count','blocked_count','observed_delivery_rate','states','counties','case_ids','route_ids']));
  // Read-only public compatibility export; never include customer identities.
  fs.writeFileSync(path.join(root,'docs/RECORD_HELP_CASE_LOG.csv'),csv(d.cases.map(c=>({anonymous_case_id:c.id,last_updated:d.updated_at.slice(0,10),state:c.state,county:c.county,outcome_state:c.status,next_check:c.next_check,route_ids:c.route_ids,private_detail:'storage/operations/ledger.json'})),['anonymous_case_id','last_updated','state','county','outcome_state','next_check','route_ids','private_detail']));
  const caseCards=d.cases.map(c=>`<details class="record" data-status="${escape(c.status)}"><summary><span class="badge">${escape(states[c.status]||c.status)}</span><strong>${escape(c.customer)}</strong><span>${escape(c.state+' · '+c.county)}</span><span>${escape(c.deadline?'마감 '+c.deadline:'마감 미제공')}</span></summary><div class="content"><p class="intent">${escape(c.intent)}</p><p>${escape(c.summary)}</p><p class="muted">${escape(c.property)} · ${escape(c.parcel)} · ${escape(c.email)}</p><p><b>다음 행동:</b> ${escape(c.next_action)} (${escape(c.next_check||'추가 문의 시')})</p><p>최근 고객 전달: ${escape(c.last_customer_update||'확인된 발송 없음')}</p><h3>개별 조사 상태</h3><div class="scroll"><table><thead><tr><th>분야 / 담당</th><th>상태</th><th>현재 결론</th><th>다음 확인</th></tr></thead><tbody>${d.branches.filter(b=>b.case_id===c.id).map(b=>`<tr><td>${escape(b.topic)}<br><small>${escape(b.custodian)}</small></td><td>${escape(b.status)}<br>${escape(b.request_number||'')}</td><td>${escape(b.summary)}<br>${(b.source_ids||[]).map(id=>sourceLink(d,id)).join(' · ')}</td><td>${escape(b.next_check||'—')}<br>${escape(b.next_action)}</td></tr>`).join('')}</tbody></table></div><h3>근거와 보존 자료</h3><ul>${c.source_ids.map(id=>`<li>${sourceLink(d,id)}</li>`).join('')}</ul><h3>적용 경로</h3><p>${c.route_ids.map(id=>`<a href="#${escape(id)}" onclick="showTab('routes')">${escape(id)}</a>`).join(' · ')}</p><h3>처리 이력</h3>${d.events.filter(e=>e.case_id===c.id).sort((a,b)=>b.timestamp.localeCompare(a.timestamp)).map(e=>`<p><small>${escape(e.timestamp)} · ${escape(e.type)}</small><br>${escape(e.summary)} ${(e.source_ids||[]).map(id=>sourceLink(d,id)).join(' ')}</p>`).join('')}</div></details>`).join('');
  const routeCards=d.routes.map(r=>{const m=intelligence.find(item=>item.route_id===r.id);return `<details class="record" id="${escape(r.id)}"><summary><strong>${escape(r.title)}</strong><span>${escape(r.verification)}</span><span>${escape(r.last_verified)}</span></summary><div class="content"><p><b>운영 관측:</b> ${m.case_count}개 사례 · ${m.observation_count}개 상태 기록 · ${m.verified_outcome_count}개 확인 결과${m.median_response_hours===null?'':` · 응답 중앙값 ${Math.round(m.median_response_hours)}시간 (${m.response_time_sample_count}건)`}</p><p><b>접근:</b> ${escape(m.availability)} · ${escape(m.primary_channel)} · 비용 ${escape(m.fee_policy)}</p><p><b>먼저:</b> ${escape(r.first_action)}</p><p><b>대체 경로:</b> ${escape(r.fallback)}</p><p><b>한계:</b> ${escape(r.limitations)}</p><pre>${escape(r.notes)}</pre><p>${(r.source_ids||[]).map(id=>sourceLink(d,id)).join(' · ')}</p></div></details>`}).join('');
  const html=`<!doctype html><html lang="ko"><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>SepticPath 운영대장</title><style>:root{font-family:system-ui,'Malgun Gothic',sans-serif;color:#233a3a;background:#f3f5f2}*{box-sizing:border-box}body{max-width:1240px;margin:auto;padding:32px 20px}h1{font-size:30px;margin-bottom:8px}p{line-height:1.7}a{color:#196a60;overflow-wrap:anywhere}header{margin-bottom:24px}.muted,small{color:#627272}.notice{background:#fff4dc;padding:14px;border-radius:10px}nav{display:flex;gap:8px;flex-wrap:wrap;margin:20px 0}button,input,select{font:inherit;padding:11px 14px;border:1px solid #bdccc5;border-radius:8px;background:white}button{cursor:pointer}button.active{background:#214f46;color:white}input{min-width:230px;flex:1}.controls{display:flex;gap:10px;margin:18px 0;flex-wrap:wrap}.stats{display:flex;gap:12px;flex-wrap:wrap}.stat{background:white;padding:18px;min-width:160px;border-radius:12px}.stat b{font-size:28px;display:block}.record{background:white;border:1px solid #d6dfd9;border-radius:12px;margin:12px 0}.record summary{display:flex;align-items:center;gap:16px;padding:18px;cursor:pointer;flex-wrap:wrap}.record summary strong{min-width:160px}.badge{font-size:12px;padding:5px 8px;background:#e7f0e9;border-radius:6px}.content{padding:0 20px 22px;border-top:1px solid #eee}.intent{font-size:18px;font-weight:650}table{width:100%;border-collapse:collapse;background:white}th,td{text-align:left;vertical-align:top;padding:14px;border-bottom:1px solid #e0e7e1;min-width:110px}td{font-size:14px;line-height:1.6}th{background:#e7eee7}.scroll{overflow:auto}pre{white-space:pre-wrap;overflow-wrap:anywhere;font:14px/1.65 system-ui}section[hidden]{display:none}.overdue{background:#fff1e7}.task{border-left:4px solid #547869}footer{margin-top:35px;font-size:13px;color:#64756b}@media(max-width:600px){body{padding:20px 12px}h1{font-size:25px}.record summary{gap:8px}.stat{min-width:130px}.content{padding:0 12px 18px}}</style><header><p class="muted">SEPTICPATH · PRIVATE OPERATIONS</p><h1>고객 현황과 조사 노하우</h1><p>마지막 메일 확인: ${escape(d.sync.checked_through)} · 기록 revision ${d.revision}</p><div class="notice">이 화면은 비공개 로컬 운영대장입니다. 실시간 메일 연결은 없습니다. 새 메일을 가져오고 처리 결과를 기록하면 갱신됩니다. <a href="STATUS.md">상태 문서</a> · <a href="views/cases.csv">고객 CSV</a> · <a href="views/branches.csv">조사 CSV</a> · <a href="views/routes.csv">경로 CSV</a> · <a href="views/route-intelligence.csv">경로 성과 CSV</a> · <a href="views/growth-signals.csv">검색 신호 CSV</a> · <a href="views/growth-opportunities.csv">성장 기회 CSV</a></div></header><div class="stats"><div class="stat">고객 케이스<b>${d.cases.length}</b></div><div class="stat">진행 중<b>${d.cases.filter(c=>!closed.has(c.status)).length}</b></div><div class="stat">전달 완료 / 한계 포함<b>${d.cases.filter(c=>closed.has(c.status)).length}</b></div><div class="stat">재사용 경로<b>${d.routes.length}</b></div><div class="stat">경로 관측<b>${d.route_observations.length}</b></div><div class="stat">검색 신호<b>${d.growth_signals.length}</b></div><div class="stat">미분류 새 메일<b>${d.inbox.filter(i=>i.status==='untriaged').length}</b></div></div><nav><button class="active" data-tab="cases" onclick="showTab('cases')">고객 현황</button><button data-tab="tasks" onclick="showTab('tasks')">다음 행동</button><button data-tab="routes" onclick="showTab('routes')">지역별 노하우</button><button data-tab="lessons" onclick="showTab('lessons')">실패·교정 기록</button></nav><div class="controls"><input id="search" placeholder="고객 · 주소 · 카운티 · 경로 검색" aria-label="검색" oninput="filter()"><select id="state" aria-label="상태" onchange="filter()"><option value="">모든 상태</option>${[...caseStates].map(s=>`<option value="${s}">${states[s]||s}</option>`).join('')}</select></div><section id="cases">${caseCards}</section><section id="tasks" hidden>${tasks.map(t=>`<article class="record task" data-due="${escape(t.due)}"><div class="content"><h3>${escape(t.due)} · ${escape(d.cases.find(c=>c.id===t.case_id)?.customer||'공통 운영')}</h3><p>${escape(t.action)}</p><small>${escape(t.owner)} · ${escape(t.status||'planned')}</small></div></article>`).join('')}</section><section id="routes" hidden>${routeCards}</section><section id="lessons" hidden>${d.lessons.map(l=>`<article class="record"><div class="content"><h3>${escape(l.title)}</h3><p>${escape(l.what_happened)}</p><p><b>다음부터:</b> ${escape(l.correct_rule)}</p><p>${(l.source_ids||[]).map(id=>sourceLink(d,id)).join(' · ')}</p></div></article>`).join('')}</section><p id="empty" hidden>일치하는 항목이 없습니다.</p><footer>원본: storage/operations/ledger.json · 생성: node tools/operations.mjs render · 개인 정보가 포함되어 있으므로 외부 배포 금지.</footer><script>function showTab(id){document.querySelectorAll('section').forEach(e=>e.hidden=e.id!==id);document.querySelectorAll('nav button').forEach(e=>e.classList.toggle('active',e.dataset.tab===id));filter()}function filter(){const q=document.getElementById('search').value.toLowerCase(),s=document.getElementById('state').value;let n=0;document.querySelectorAll('section:not([hidden]) .record').forEach(e=>{e.hidden=!e.textContent.toLowerCase().includes(q)||(e.dataset.status&&s&&e.dataset.status!==s);if(!e.hidden)n++});document.getElementById('empty').hidden=n>0}const today=new Date().toLocaleDateString('en-CA');document.querySelectorAll('[data-due]').forEach(e=>e.classList.toggle('overdue',e.dataset.due<=today));</script></html>`;
  const table=`<div class="scroll"><table><thead><tr><th>고객</th><th>지역</th><th>상태</th><th>다음 확인</th><th>마감</th><th>다음 행동</th></tr></thead><tbody>${d.cases.map((c,i)=>`<tr class="record" data-status="${escape(c.status)}"><td><button onclick="openCase(${i})">${escape(c.customer)}</button></td><td>${escape(c.state+' / '+c.county)}</td><td>${escape(states[c.status]||c.status)}</td><td>${escape(c.next_check||'—')}</td><td>${escape(c.deadline||'미제공')}</td><td>${escape(c.next_action)}<span hidden>${escape(c.property+' '+c.parcel+' '+c.email)}</span></td></tr>`).join('')}</tbody></table></div><h2>고객별 상세 기록</h2>`;
  const ui=html.replace('<section id="cases">','<section id="cases">'+table).replace('<script>','<script>function openCase(i){const e=document.querySelectorAll("#cases details")[i];e.open=true;e.scrollIntoView({behavior:"smooth",block:"start"})}');
  fs.writeFileSync(path.join(home,'index.html'),ui);
  return {cases:d.cases.length,branches:d.branches.length,routes:d.routes.length,sources:d.sources.length,tasks:tasks.length,untriaged:d.inbox.filter(x=>x.status==='untriaged').length};
}

function main(){
  const [command, arg]=process.argv.slice(2);
  if (!fs.existsSync(db)) throw new Error('Private ledger missing. Restore storage/operations from backup. Do not silently initialize empty state.');
  let d=json(db);
  if(command==='status') {render(d);console.log(fs.readFileSync(path.join(home,'STATUS.md'),'utf8'));return;}
  if(command==='show') {
    const c=d.cases.find(c=>c.id===arg);
    const result=c?{...c,investigations:d.branches.filter(b=>b.case_id===c.id),history:d.events.filter(e=>e.case_id===c.id),evidence:d.sources.filter(s=>c.source_ids.includes(s.id)).map(({id,title,local_path,url})=>({id,title,local_path,url}))}:d.routes.find(c=>c.id===arg)||d.branches.find(b=>b.id===arg)||null;
    console.log(JSON.stringify(result,null,2));return;
  }
  if(command==='validate') {
    const errors=validate(d);
    for(const s of d.sources) if(s.local_path){const p=path.resolve(home,s.local_path);if(!fs.existsSync(p))errors.push(`Missing local evidence ${s.id}`);else if(s.sha256&&sha(fs.readFileSync(p))!==s.sha256)errors.push(`Changed original evidence ${s.id}`);}
    if(errors.length) throw new Error(errors.join('\n'));
    console.log(`Valid revision ${d.revision}: ${d.cases.length} cases / ${d.branches.length} branches / ${d.sources.length} sources`);return;
  }
  if(command==='apply'){d=applyChange(d,json(path.resolve(arg)));console.log(commit(d,d.revision-1));return;}
  if(command==='ingest-mail') {
    const data=json(path.resolve(arg));
    const known=new Set(d.sources.map(s=>s.id));
    const additions=(data.messages||[]).filter(m=>!known.has(m.id));
    if(!additions.length){console.log('No new messages');return;}
    const sources=additions.map(m=>({id:m.id,type:'email',title:m.subject,url:m.url||`https://mail.google.com/mail/u/0/#all/${m.id}`,timestamp:m.timestamp,thread_id:m.thread_id,from:m.from,to:m.to,text:m.text,attachments:m.attachments||[]}));
    d=applyChange(d,{expected_revision:d.revision,reason:'Incremental inbox import; case statuses unchanged pending triage',upsert:{sources,inbox:sources.map(s=>({id:'inbox-'+s.id,source_id:s.id,status:'untriaged'}))},event:{summary:`Imported ${sources.length} new messages for triage`,source_ids:sources.map(s=>s.id)}});
    console.log(commit(d,d.revision-1));return;
  }
  if(command==='render'){console.log(render(d));return;}
  throw new Error('Usage: node tools/operations.mjs status|show ID|validate|render|apply CHANGE.json|ingest-mail MAIL.json');
}
if(process.argv[1]&&path.resolve(process.argv[1])===fileURLToPath(import.meta.url))try{main();}catch(e){console.error(e.message);process.exitCode=1;}
