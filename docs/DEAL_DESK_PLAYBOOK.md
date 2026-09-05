# First-Case Deal Desk Playbook

## Purpose

The Deal Desk is a proof-of-value motion, not a support channel. One active
case creates a concrete result; the second case must become self-serve.

Start with Tennessee and North Carolina. Prioritize rural listing agents,
buyer teams, and septic specialists who visibly work on active transactions.

## Case boundary

Accept a case only as a reply to the original outbound Gmail conversation.
The reply must include either:

- property address; or
- state and county, plus the listing bedroom count.

If an address lookup cannot resolve, use the state-and-county path. Do not ask
the sender to use the public contact form, and do not paste addresses or
request text into the tracker.

The promised turnaround is the end of the next business day. A partner gets
one manual case only. Later cases use their unique Offer Prep link.

## Daily four-hour operating loop

1. Spend 60 minutes finding 8 to 14 TN or NC prospects with clear evidence of
   rural, land, septic, buyer-side, or active-listing work.
2. Spend 75 minutes sending individual Gmail messages using the matching
   template in `OFFER_PREP_OUTREACH.md`. Every message needs one real,
   prospect-specific observation.
3. Spend up to 90 minutes completing at most five qualifying first cases in
   Offer Prep. Return the official route, three first documents, a
   bedroom/file question, the request text, and the next professional step.
4. Spend 45 minutes logging the motion, sending any due single follow-up, and
   checking partner-source events in `/ops/event-report/`.

## 21-day cadence

| Days | New personalized Gmail / day | Work emphasis |
| --- | ---: | --- |
| 1-7 | 14 | TN and NC prospecting; first cases capped at five per day |
| 8-14 | 9 | One follow-up after five business days; convert first-case partners to self-serve |
| 15-21 | 5 | Process cases, prompt a second use, and seek durable buyer-resource placement |

Targets for the full period:

- 196 new personal messages
- 20 first-case requests
- 10 repeat senders
- 4 durable resource-page placements

## First-case completion checklist

1. Open the partner's unique source URL locally and use Offer Prep.
2. Resolve the address. If it fails, select the supplied state and county.
3. Enter the listing bedroom count. Enter permit bedrooms or file state only
   when the sender supplied a credible file detail.
4. Verify the official route label and first three requested documents.
5. Copy the generated note into the original Gmail thread. Add a short,
   factual bedroom/file question and one appropriate professional next step.
6. End with the unique self-serve URL and the statement that future cases use
   that link.
7. Mark the tracker status without recording the address, property link, or
   drafted note.

## Gmail labels

Use these labels to make the workflow auditable without building a CRM:

- `Deal Desk/Prospect`
- `Deal Desk/First case requested`
- `Deal Desk/Result sent`
- `Deal Desk/Self-serve used`
- `Deal Desk/Repeat sender`
- `Deal Desk/Resource placement`

## Metrics and decision gates

The manual tracker records outreach state; `/ops/event-report/` records the
anonymous site behavior associated with each `src` value:

`opened -> generated -> copied or downloaded -> official_route_opened`

After 100 first messages:

- Fewer than six first-case requests: change the prospect type or opening
  message before increasing volume.
- Fewer than 40 percent of first-case partners using self-serve: shorten the
  return format so the partner can forward it immediately.
- A partner sends a second case: prioritize that role and local market. Expand
  the proven role to Indiana and South Carolina only after repeat use exists.

## Non-goals for this 21-day test

- No additional Deal Desk-specific public address form; the separate public
  Record Help beta remains available for consented intake.
- No widget, embed, login, CRM, payment flow, or shareable result page.
- No automated email sequences or automatic replies.
- No permit decision, legal judgment, inspection result, or engineering advice.
