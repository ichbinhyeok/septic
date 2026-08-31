# Records Access Index Distribution Playbook

## Objective

Grow qualified referral traffic and citations to the single canonical asset:

`https://septicpath.com/septic-records-access-index/`

Do not create another county-page family for distribution. Share filtered
views of the existing index and let the county guides receive the downstream
clicks.

## Quality gate

Promote a route only when all of these are true:

- its state-and-county key is unique;
- its official records URL uses HTTPS and is not returning a confirmed 404 or
  410;
- every listed source ID resolves in the source registry;
- its latest source review is no more than 180 days old;
- the message says that confidence measures route specificity, not permit
  status.

Use `confidence=high` for cold distribution. Routes below 70 remain searchable
in the dataset, but do not lead an outreach message until their workflow is
deepened.

Automated 403, 429, 5xx, and timeout results are manual-review candidates, not
proof that a government route is broken. Replace a route automatically only
for a confirmed 404 or 410.

## First distribution wedge

Start with Tennessee, North Carolina, South Carolina, and Indiana. They combine
published county coverage with a state handoff packet and give each recipient
a concrete workflow after the index.

Use one tracked URL per audience:

- Agent and transaction teams:
  `?state=TN&confidence=high&src=agent-resource&utm_medium=outreach&utm_campaign=records-index`
- Inspectors and septic professionals:
  `?state=NC&confidence=high&src=septic-pro-review&utm_medium=outreach&utm_campaign=records-index`
- Data and resource editors:
  `?confidence=high&src=resource-editor&utm_medium=outreach&utm_campaign=records-index`
- Existing Deal Desk partners:
  `?state=TN&src=<partner-slug>&utm_medium=partner&utm_campaign=records-index`

Keep `src` unique at the partner or publication level. The filter code
preserves attribution parameters when the user changes state, query, or route
filters.

## Four audiences and one ask each

### Real-estate and transaction teams

Give them a state-filtered view that can be reused before an offer. Ask them to
test it on one active county and reply with the missing or confusing step.

### Septic inspectors, installers, and permit expediters

Lead with the official-source CSV and the review date. Ask for one correction
to the counties they know best. After a correction is accepted, ask whether
their client-resource page should cite the filtered view.

### County and environmental-health staff

Do not ask for a backlink first. Send only the row for their county, identify
the destination currently listed, and ask whether it is the correct public
starting point. A confirmed correction is more valuable than a generic link.

### Homeowner, data, and local-resource editors

Lead with the downloadable dataset, its row grain, review-date field, and
official URL. Ask whether a filtered state view or the CSV would improve an
existing records, rural-home, or buyer-diligence resource.

## Copy-ready messages

### Professional reviewer

Subject: Quick check of the public septic-record route for <county>

Hi <first-name>,

I maintain a public index of county septic-record routes. The <county> row
currently points to <official-label> and shows <confidence>% confidence based
on <source-count> listed government sources, last reviewed <date>.

Could you tell me if that is still the best public starting point for a buyer,
agent, or homeowner trying to pull the permit, layout, approval, repair file,
or a written no-record response?

Filtered row: <tracked-filtered-url>

If anything is wrong, I will correct the dataset and credit the official route.
The confidence score describes the route, not the status of a property's
permit.

### Resource editor

Subject: Reusable county septic-record index for <state>

Hi <first-name>,

Your <specific resource> helps readers with <specific task>. I built a
filterable <state> view of 325 county septic-record routes across 28 states.
Each row includes the first artifact to request, route type, confidence,
official-source count, last review date, government URL, and a downloadable
CSV.

View: <tracked-filtered-url>

If it fills a gap in your resource, you are welcome to cite the filtered view
or CSV. If you spot an incorrect route, reply with the county and I will review
it before asking you to link anywhere.

## Operating cadence

- Week 1: 10 individually researched contacts in each priority state.
- Week 2: follow up once with non-responders and process corrections.
- Week 3: approach resource editors only with the corrected, strongest state
  views.
- Week 4: expand one state only when its outreach produced a reply, referral
  session, citation, or accepted correction.

Do not use BCC, bulk DMs, bought links, directory blasts, or generic community
posts.

## Measurement and decisions

Track these events by `src` and campaign:

- landing sessions to the index;
- filtered-view shares;
- CSV downloads and citation copies;
- county-guide opens;
- official-source exits;
- accepted corrections and earned citations.

After 14 complete days:

- keep a segment if it produces at least two qualified replies or five
  downstream county/official-source clicks;
- rewrite the message if it produces visits but no downstream action;
- stop a segment if 20 personalized sends produce no reply and no meaningful
  click.

After 28 complete days, add a state-specific export only when one state has
repeat external use. Do not create an export merely because the data can be
split.
