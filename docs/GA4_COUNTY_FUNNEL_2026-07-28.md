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
| `county_task_completed` | The user reported either a downloaded document or a submitted request. |

## Event parameters

Register these as event-scoped custom dimensions in GA4:

- `county_key`
- `state_code`
- `county_slug`
- `access_mode`
- `acquisition_method`
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

Mark `county_task_completed` as the primary key event. Keep
`county_official_pdf_prepared` as a secondary key event if PDF preparation is a
high-value product action.

## Recommended funnel explorations

### Search-first routes

1. `county_workflow_viewed`
2. `county_prepare_ready` with `preparation_path = primary_route`
3. `county_official_route_opened`
4. `county_return_outcome`
5. `county_task_completed`

### Request or PDF routes

1. `county_workflow_viewed`
2. `county_prepare_started`
3. `county_prepare_ready` with `preparation_path` equal to `field_pack` or
   `fallback_field_pack`
4. `county_official_pdf_prepared`, `county_email_draft_opened`, or
   `county_request_downloaded`
5. `county_official_route_opened`
6. `county_task_completed`

Break the funnel down by `county_key`, `acquisition_method`, device category,
session source/medium, and landing page.

External agency pages do not run SepticPath analytics. Their final result is
measured only when the user returns and chooses an outcome in SepticPath.
