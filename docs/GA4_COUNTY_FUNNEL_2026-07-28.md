# GA4 county-record funnel

Measurement ID: `G-S1SY1NS71P`

## Event sequence

| Event | Meaning |
|---|---|
| `county_workflow_viewed` | A county acquisition workspace loaded. |
| `county_prepare_started` | The user first entered a property clue, edited a verified county field, or opened a fallback pack. |
| `county_prepare_ready` | The active route has all published required details ready. A free primary route and an opened fallback field pack are measured separately. |
| `county_fallback_opened` | The user opened a request fallback after the primary search route. |
| `county_preparation_downloaded` | A preparation sheet was downloaded. |
| `county_preparation_printed` | A preparation sheet print view was opened. |
| `county_official_pdf_prepared` | The county's original PDF was downloaded with supported fields filled. |
| `county_email_draft_opened` | A prepared email draft was opened. |
| `county_request_downloaded` | Prepared request text was downloaded. |
| `county_official_route_opened` | A current official search, form, portal, page, email, or phone route was opened. |
| `county_return_outcome` | The user reported what happened at the official route. |
| `county_request_submitted` | The user reported a submitted request. The case remains pending. |
| `county_record_reported` | The user reported obtaining a document, before SepticPath sees the file. |
| `county_record_obtained` | A returned document was added to the document workspace. It still needs review. |
| `county_case_resolved` | Reserved for a future explicit review step after the document or written official outcome has been checked. Do not emit this from a request reference alone. |

## Event parameters

Register these as event-scoped custom dimensions in GA4:

- `county_key`
- `state_code`
- `county_slug`
- `access_mode`
- `acquisition_method`
- `profile_scope`
- `capability_tier`
- `route_reviewed_at`
- `workflow_run_id`
- `result_source`
- `case_status`
- `entry_point`
- `preparation_path`
- `route_position`
- `outcome`
- `completion_type`
- `artifact_format`

`required_detail_count` is numeric and can be registered as a custom metric if
needed.

No address, parcel identifier, owner name, requester name, email address, phone
number, reference number, or request text is sent to GA4.

## Recommended key event

Do not mark a request submission or a self-reported download as a resolved-case
key event. Use `county_record_obtained` as the current high-intent event. Promote
`county_case_resolved` only after the explicit review checkpoint is implemented.
Keep `county_official_pdf_prepared` as a secondary preparation event.

## Recommended funnel explorations

### Search-first routes

1. `county_workflow_viewed`
2. `county_prepare_ready` with `preparation_path = primary_route`
3. `county_official_route_opened`
4. `county_return_outcome`
5. `county_record_reported`
6. `county_record_obtained`

### Request or PDF routes

1. `county_workflow_viewed`
2. `county_prepare_started`
3. `county_prepare_ready` with `preparation_path` equal to `field_pack` or
   `fallback_field_pack`
4. `county_official_pdf_prepared`, `county_email_draft_opened`, or
   `county_request_downloaded`
5. `county_official_route_opened`
6. `county_request_submitted`
7. `county_record_obtained` after the office reply is added

Break the funnel down by `county_key`, `acquisition_method`, `profile_scope`,
`capability_tier`, device category, session source/medium, and landing page.

External agency pages do not run SepticPath analytics. Their final result is
measured only when the user returns and chooses an outcome in SepticPath.

## Record Help demand funnel

Use this separate funnel to measure whether a visitor whose record search is
blocked will ask for human routing help, then qualify the subset with a live
transaction:

1. `record_help_cta_viewed`
2. `record_help_cta_clicked`
3. `record_help_form_viewed`
4. `record_help_form_started`
5. `record_help_stage_selected`
6. `record_help_request_submitted`

`record_help_form_validation_error` is diagnostic and should not be treated as
a conversion. Break the funnel down by `source_context`, `request_type`, and
`cta_variant`. Use `transaction_intent` and `process_stage` only after the user
selects a stage; those values contain no property or contact details. Header and
footer links emit clicks but are excluded from CTA impression counts so a global
navigation item cannot inflate meaningful offer exposure. `invalid_count` is
the only form-error detail sent to GA4. Property and contact values are never
included in these browser events.
