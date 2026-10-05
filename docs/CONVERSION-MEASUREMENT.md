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

The 2026-10-04 free-records pivot retires the private checkout events. Private
result pages suppress analytics and never emit bearer tokens. Provider acceptance,
actual payment and refunds must be recorded separately in private operations;
`generate_lead` alone is not revenue. See [FREE_RECORDS_PIVOT.md](FREE_RECORDS_PIVOT.md).

`meaningful_engagement` requires at least 30 visible seconds and 50% page depth. It is intentionally stricter than a page view.

## GA4 key events

Mark these as key events in GA4 after production traffic confirms they are arriving:

- `generate_lead` — primary lead conversion
- Historical `unlock_purchase_completed` events are not a current conversion path.

Keep `calculator_completed`, `address_search_completed`, `case_study_clicked`, and `meaningful_engagement` as diagnostic events. They explain funnel quality without inflating the primary conversion count.

## Useful explorations

- Landing quality: `page_family` → `meaningful_engagement` → `record_help_cta_clicked`
- Proof influence: `case_study_clicked` → `record_help_form_started` → `generate_lead`
- Tool influence: `address_search_completed` or `calculator_completed` → `generate_lead`
- Commercial validation: actual provider acceptance → received payment → repeat purchase,
  reconciled in private operations rather than inferred from GA4 form events.

Use `entry_page`, `source_page`, `source_context`, `page_family`, `calculator_type`, and categorical outcomes as dimensions. Do not add customer-entered values as analytics parameters.

## Intake validation, October 2026

`measurement_version=intake_v2` marks the revised attempt/error events. A native-validation rejection now emits `record_help_form_submit_attempted` with `validation_state=blocked`; a native-valid submit emits `validation_state=passed`. Passed means browser validation passed, not that the server stored or accepted the request. Keep the confirmed submission event separate. Do not compare the earlier submit-only attempt count as an unchanged funnel denominator.

Bounded event names `record_help_invalid_address`, `_state`, `_email`, `_phone`, `_role`, `_purpose`, `_consent`, `_upload`, `_deadline`, and `_other` identify the rejected field category without a custom-dimension setup. Count once per category per native validation cycle; one user can have several errors and later submit successfully. Native validity checks invoked by scripts can also emit these events. Do not report errors as lost customers. No entered field value, filename or validation-message text is sent to analytics.

The studio intake owns its file-mode validation and displays a linked error summary. Phone validation accepts the same 10–15-digit formats as the server. Required contact, role and consent fields remain unchanged. New analytics results are unavailable until this change is deployed and receives traffic.
