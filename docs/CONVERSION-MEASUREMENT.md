# Conversion measurement

SepticPath measures the path from organic landing page to useful customer action without sending property addresses, parcel IDs, email addresses, phone numbers, request references, uploaded file names, or payment tokens to GA4.

## Primary funnel

1. `conversion_page_viewed` — normalized page family and privacy-safe source path
2. `case_study_viewed` / `case_study_clicked` — proof content consumption
3. `address_search_started` / `address_search_completed` — property-route discovery
4. `calculator_started` / `calculator_completed` — planning-tool use
5. `record_help_cta_clicked` — research intent
6. `record_help_form_viewed` / `record_help_form_started` / `record_help_form_submit_attempted`
7. `record_help_request_submitted` and GA4 recommended event `generate_lead`
8. `unlock_offer_viewed` / `begin_checkout` / `unlock_checkout_started`
9. `unlock_purchase_completed`, with cancellation and failure events kept separate

`meaningful_engagement` requires at least 30 visible seconds and 50% page depth. It is intentionally stricter than a page view.

## GA4 key events

Mark these as key events in GA4 after production traffic confirms they are arriving:

- `generate_lead` — primary lead conversion
- `unlock_purchase_completed` — paid conversion

Keep `calculator_completed`, `address_search_completed`, `case_study_clicked`, and `meaningful_engagement` as diagnostic events. They explain funnel quality without inflating the primary conversion count.

## Useful explorations

- Landing quality: `page_family` → `meaningful_engagement` → `record_help_cta_clicked`
- Proof influence: `case_study_clicked` → `record_help_form_started` → `generate_lead`
- Tool influence: `address_search_completed` or `calculator_completed` → `generate_lead`
- Revenue path: `unlock_offer_viewed` → `begin_checkout` → `unlock_purchase_completed`

Use `entry_page`, `source_page`, `source_context`, `page_family`, `calculator_type`, and categorical outcomes as dimensions. Do not add customer-entered values as analytics parameters.
