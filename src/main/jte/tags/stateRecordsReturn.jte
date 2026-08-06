@param String stateCode
@param String stateName
@param String officeLabel
@param String outcomesId = "after-search"

<section class="state-record-return"
         id="state-records-return"
         data-state-records-return
         data-state-code="${stateCode}"
         data-state-name="${stateName}"
         data-office-label="${officeLabel}"
         data-outcomes-id="${outcomesId}"
         aria-labelledby="state-record-return-title">
    <div class="container state-record-return__inner">
        <div class="state-record-return__intro">
            <span class="eyebrow">Leave this tab open</span>
            <h2 id="state-record-return-title">Come back after the official search.</h2>
            <p>SepticPath will keep the next step on this device for 30 days. Property identifiers are not included in reminders or return links.</p>
        </div>
        <details class="state-record-return__checkin" data-state-return-checkin>
            <summary><span data-state-return-summary>I’m back — record what happened</span><small>Continue without starting over</small></summary>
            <div class="state-record-return__body">
                <div class="state-record-return__outcomes" role="group" aria-label="Official search result">
                    <button type="button" data-state-return-outcome="found">I found documents</button>
                    <button type="button" data-state-return-outcome="request_submitted">I submitted a request — I have details</button>
                    <button type="button" data-state-return-outcome="not_found_online">Nothing appeared online</button>
                    <button type="button" data-state-return-outcome="no_record_response">The office confirmed no record</button>
                    <button type="button" data-state-return-outcome="wrong_agency">This office does not own the file</button>
                    <button type="button" data-state-return-outcome="blocked">The official route was blocked</button>
                </div>
                <div class="state-record-return__next" data-state-return-next aria-live="polite" hidden></div>
                <button class="state-record-return__clear" type="button" data-state-return-clear hidden>Delete this saved step</button>
            </div>
        </details>
    </div>
</section>
