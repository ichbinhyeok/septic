@param String sourceContext
@param String servicePath = "/septic-system-cost-calculator/?quoteMode=true#quote-request"
@param boolean urgent = false
<section class="local-service-next" aria-label="Local service next step">
    <p class="eyebrow">When the next step is on site</p>
    <h2>Get help with the job.</h2>
    <p>Tell us the location and what you need. We can share your request with relevant local septic professionals where available. No completed records search is required.</p>
    @if (urgent)
    <p><strong>For an active sewage backup, contact a local septic service directly. This form is not emergency dispatch.</strong></p>
    @endif
    <a class="button button--primary" href="${servicePath}" rel="nofollow" data-track-click="nav" data-track-source-context="${sourceContext}" data-track-target-type="quote_form">Request local service help →</a>
    <p><small>Free to submit. Professionals price their work separately. Availability and response times vary.</small></p>
</section>
