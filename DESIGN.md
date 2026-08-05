# SepticPath product design direction

## Product promise

SepticPath is a public-records workflow, not a government-record database. Every page must state what the site can complete, what remains on an official system, and what the user should do after that handoff.

## Primary journey

1. Identify the correct state, county, and record owner.
2. Prepare the property identifiers the official source accepts.
3. Open the verified search or request route without implying that SepticPath retrieved a record.
4. Handle found, missing, blocked, and written no-record outcomes.
5. Move a returned document into interpretation, property diligence, or professional help only when the evidence supports that next step.

## Interface principles

- Lead with the task and the expected result, not editorial prose.
- Use one bounded workspace per page. Avoid card walls and repeated promotional panels.
- Put jurisdiction selection before property input when the official workflow is jurisdiction-first.
- Keep failure states beside the primary task so users can return after visiting an official site.
- Use square, ledger-like surfaces, restrained color, strong rules, and compact operational labels.
- Keep mobile forms single-column with the first actionable field visible near the first viewport.
- Preserve keyboard focus, explicit labels, 44px minimum controls, and visible error text.

## Truth boundary

The interface may route, prepare, organize, and explain. It must not claim to search a government database, retrieve a permit, prove no record exists, certify capacity or condition, or submit a government request unless the underlying integration actually performs and verifies that action.

## Content standard

Use official agency and county sources for jurisdiction, required fields, document names, contact routes, and exceptions. Put transient failures such as a 403 response in fallback guidance, never in the main search intent or page title.
