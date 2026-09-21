@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.ClosingRiskCheckForm
@import com.example.septic.web.AssetPaths
@import com.example.septic.web.StudioProofSelection
@import org.springframework.validation.ObjectError
@import org.springframework.validation.FieldError
@param ClosingRiskCheckForm closingRiskCheckForm
@param boolean closingRiskHasErrors = false
@param String closingRiskDocumentError = null
@param String closingRiskRequestId = null
@param String closingRiskSuccessSourceContext = "direct"
@param boolean closingRiskReceiptSent = false
@param String closingRiskMinimumDeadline
@param boolean documentReviewMode = false
@param java.util.List<StateProfile> states = java.util.List.of()
@param java.util.List<ObjectError> intakeErrors = java.util.List.of()
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = "Start your research", heroImage = "/images/studio/intake-room-v1.webp", pageClass = "studio-intake", pageStyles = "/studio-intake.css", content = @`
<main id="main" class="intake-layout" data-offer-prep-file-check data-review-active="${documentReviewMode}">
    <aside class="intake-scene" aria-label="Start with an address">
        <img src="/images/studio/intake-room-v1.webp" alt="" fetchpriority="high" width="1024" height="1536">
        <div class="intake-scene__copy"><p class="studio-overline">Let’s find yours</p><h2>Every property<br>has a paper trail.</h2><p>Start with an address.<br>We’ll take it from here.</p><a href="#intake-form" class="intake-mobile-link">Tell us about the property <span aria-hidden="true">↓</span></a></div>
        <p class="intake-scene__note">Free research. No upfront payment.</p>
    </aside>
    <section class="intake-workspace" id="intake-form" aria-labelledby="intake-title">
    @if (closingRiskRequestId != null)
        <div class="intake-success" role="status" tabindex="-1" data-intake-success data-closing-risk-request-success data-ga-event="record_help_request_submitted" data-ga-param-source-context="${closingRiskSuccessSourceContext}">
            <p class="studio-overline">Request received</p><h1 id="intake-title">${documentReviewMode ? "Your document review is in." : "Your record investigation is in."}</h1>
            <p>We’ll ${documentReviewMode ? "review your file" : "start the research"} and aim to email an initial update within 1–2 business days. Agency records can take longer.</p>
            <div class="intake-reference"><span>Your reference</span><strong>${closingRiskRequestId}</strong></div>
            <p>${closingRiskReceiptSent ? "We also emailed your reference and request details to you." : "Save this reference for follow-up. An email receipt has not been confirmed."}</p>
            <p>No payment is due now. If a useful verified result is ready, we’ll send a free preview and an optional US $29 unlock offer. Agency fees need your approval first.</p>
            <a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}">Explore our work <span aria-hidden="true">↗</span></a>
        </div>
    @else
        <h1 id="intake-title">${documentReviewMode ? "Send the file. We’ll answer the questions it raises." : "Tell us about the property."}</h1>
        @if (StudioProofSelection.fromSource(closingRiskCheckForm.getSourcePageHintValue()) != null)
        <div class="intake-handoff intake-handoff--case"><p class="studio-overline">Inspired by an investigation</p><p>Case reference: <a href="${closingRiskCheckForm.getSourcePageHintValue()}">${StudioProofSelection.fromSource(closingRiskCheckForm.getSourcePageHintValue()).place()} ↗</a></p><small>Included with your request. Your property needs its own investigation.</small></div>
        @endif
        @if ("studio_calculator".equals(closingRiskCheckForm.getSourceContextValue()) || "/septic-system-cost-calculator/".equals(closingRiskCheckForm.getSourcePageHintValue()))
        <div class="intake-handoff"><p class="studio-overline">Continuing from your estimate</p><p>Your planning details are filled in below. Review or edit them, then add the property address and email.</p><small>Planning inputs are not verified permit facts. Research is separate from a construction quote or inspection.</small></div>
        @endif
        <form method="post" enctype="multipart/form-data" action="${com.example.septic.web.StudioGuideRoutes.route(documentReviewMode ? "/offer-prep-septic-file-check/?mode=review" : "/offer-prep-septic-file-check/", production)}" data-intake-form data-record-help-request-form data-production="${production}">
            <fieldset class="intake-modes"><legend class="intake-sr">What would you like help with?</legend>
                <label><input type="radio" name="intakeMode" value="research" checked="${!documentReviewMode}"><span>Find my records</span></label>
                <label><input type="radio" name="intakeMode" value="review" checked="${documentReviewMode}"><span>Review my file</span></label>
            </fieldset>
            @if (closingRiskHasErrors)
            <div class="intake-error" role="alert" tabindex="-1" data-intake-errors>
                <div class="intake-error__heading"><span aria-hidden="true">!</span><div><strong>Check the highlighted details.</strong><p>Your typed details are still here. Correct the items below and send again.</p></div></div>
                @if (!intakeErrors.isEmpty())
                <ul>@for (var error : intakeErrors)
                    <li>@if (error instanceof FieldError fieldError)<a href="#field-${fieldError.getField()}" data-error-field="${fieldError.getField()}">${error.getDefaultMessage()}</a>@else${error.getDefaultMessage()}@endif</li>
                @endfor</ul>
                @else<p>${closingRiskDocumentError == null ? "Please complete the required details and consent." : closingRiskDocumentError}</p>@endif
                <p class="intake-error__file-note">Files cannot be retained by the browser. If you selected any, attach them again before resubmitting.</p>
            </div>
            @endif
            <div hidden aria-hidden="true"><label>Website<input name="website" tabindex="-1" autocomplete="off"></label></div>
            <input type="hidden" name="sourceContext" value="${closingRiskCheckForm.getSourceContextValue().isBlank() ? (production ? "direct" : "studio_intake") : closingRiskCheckForm.getSourceContextValue()}" data-record-help-source-context>
            <input type="hidden" name="sourcePageHint" value="${closingRiskCheckForm.getSourcePageHintValue()}" data-record-help-source-page>
            <input type="hidden" name="entryPageHint" value="${closingRiskCheckForm.getEntryPageHintValue()}" data-record-help-entry-page>
            <div class="intake-fields">
                <label class="intake-full" for="field-propertyAddress">Property address<input id="field-propertyAddress" name="propertyAddress" value="${closingRiskCheckForm.getPropertyAddressValue()}" autocomplete="street-address" placeholder="Street address, city and ZIP code" required minlength="8" maxlength="220"></label>
                <label for="field-stateCode">State<select id="field-stateCode" name="stateCode" required><option value="">Select state</option>@for (var state : states)<option value="${state.stateCode()}" selected="${state.stateCode().equals(closingRiskCheckForm.getStateCode())}">${state.stateName()}</option>@endfor</select></label>
                <label for="field-email">Email<input id="field-email" name="email" type="email" value="${closingRiskCheckForm.getEmailValue()}" autocomplete="email" placeholder="you@example.com" required maxlength="160"></label>
                <label class="intake-full" for="field-concern">${documentReviewMode ? "What questions do you want answered?" : "What would you like to find out?"}<textarea id="field-concern" name="concern" required minlength="10" maxlength="1200" rows="3" placeholder="Tell us what you need to decide, what’s missing, or what your file doesn’t explain.">${closingRiskCheckForm.getConcernValue()}</textarea><small>One or two sentences is enough. Include any deadline.</small></label>
            </div>
            <details class="intake-disclosure" open="${documentReviewMode || closingRiskDocumentError != null}" data-intake-uploads><summary><span data-upload-label>${documentReviewMode ? "Your source files (required for review)" : "Attach files (optional)"}</span><span aria-hidden="true">+</span></summary>
                <div class="intake-upload"><label for="field-documents">Choose your files<input id="field-documents" type="file" name="documents" multiple accept="application/pdf,text/plain,image/png,image/jpeg,.pdf,.txt,.png,.jpg,.jpeg" aria-describedby="upload-help upload-status"></label><p id="upload-help">Up to 3 PDF, TXT, PNG or JPG files. 10 MB per file; 15 MB combined. Attach agency records when available.</p><p id="upload-status" aria-live="polite"></p></div>
            </details>
            <details class="intake-disclosure closing-risk-optional" open="${closingRiskHasErrors}" data-intake-additional><summary>Additional details <span aria-hidden="true">+</span></summary>
                <div class="intake-fields intake-extra">
                    <label for="field-recordType">Record type<select name="recordType" id="field-recordType"><option value="not_sure" selected="${"not_sure".equals(closingRiskCheckForm.getRecordType())}">Not sure</option><option value="septic" selected="${"septic".equals(closingRiskCheckForm.getRecordType())}">Septic system</option><option value="well" selected="${"well".equals(closingRiskCheckForm.getRecordType())}">Private well</option></select></label>
                    <label for="field-researchGoal">Research goal<select name="researchGoal" id="field-researchGoal">
                        <option value="other" selected="${"other".equals(closingRiskCheckForm.getResearchGoal())}">Not sure / something else</option><option value="original_documents" selected="${"original_documents".equals(closingRiskCheckForm.getResearchGoal())}">Find original documents</option><option value="system_layout" selected="${"system_layout".equals(closingRiskCheckForm.getResearchGoal())}">Find system location</option><option value="approval_status" selected="${"approval_status".equals(closingRiskCheckForm.getResearchGoal())}">Verify approval status</option><option value="design_capacity" selected="${"design_capacity".equals(closingRiskCheckForm.getResearchGoal())}">Check design capacity</option><option value="repair_history" selected="${"repair_history".equals(closingRiskCheckForm.getResearchGoal())}">Find repair history</option><option value="understand_file" selected="${"understand_file".equals(closingRiskCheckForm.getResearchGoal())}">Understand my file</option>
                    </select></label>
                    <label class="intake-full" for="field-recordStatus">What have you found so far?<select id="field-recordStatus" name="recordStatus"><option value="not_started" selected="${"not_started".equals(closingRiskCheckForm.getRecordStatus())}">I haven’t searched yet</option><option value="missing" selected="${"missing".equals(closingRiskCheckForm.getRecordStatus())}">No matching record</option><option value="route_unknown" selected="${"route_unknown".equals(closingRiskCheckForm.getRecordStatus())}">I don’t know which office</option><option value="request_unclear" selected="${"request_unclear".equals(closingRiskCheckForm.getRecordStatus())}">I’m not sure what to request</option><option value="partial" selected="${"partial".equals(closingRiskCheckForm.getRecordStatus())}">Only part of the file</option><option value="conflicting" selected="${"conflicting".equals(closingRiskCheckForm.getRecordStatus())}">Conflicting information</option><option value="unknown" selected="${"unknown".equals(closingRiskCheckForm.getRecordStatus())}">Not sure</option></select></label>
                    <label for="field-deadline">Deadline (optional)<input id="field-deadline" type="date" name="deadline" min="${closingRiskMinimumDeadline}" value="${closingRiskCheckForm.getDeadlineValue()}"></label>
                    <label for="field-transactionRole">Your stage<select name="transactionRole" data-record-help-stage><option value="">Choose if relevant</option><option value="researching" selected="${"researching".equals(closingRiskCheckForm.getTransactionRole())}">Researching</option><option value="buyer" selected="${"buyer".equals(closingRiskCheckForm.getTransactionRole())}">Buyer</option><option value="seller" selected="${"seller".equals(closingRiskCheckForm.getTransactionRole())}">Seller</option><option value="agent" selected="${"agent".equals(closingRiskCheckForm.getTransactionRole())}">Real-estate agent</option><option value="other" selected="${"other".equals(closingRiskCheckForm.getTransactionRole())}">Other</option></select></label>
                    <label for="field-fullName">Name (optional)<input id="field-fullName" name="fullName" autocomplete="name" maxlength="120" value="${closingRiskCheckForm.getFullNameValue()}"></label>
                    <label for="field-countyName">County (optional)<input id="field-countyName" name="countyName" maxlength="120" value="${closingRiskCheckForm.getCountyNameValue()}"></label>
                    <label class="intake-full" for="field-phone">Mobile number (optional)<input id="field-phone" type="tel" name="phone" autocomplete="tel" maxlength="30" value="${closingRiskCheckForm.getPhoneValue()}"><small>Only add this if you want connected professionals to call or text about this request. Free research does not require a phone number.</small></label>
                    <label class="intake-full" for="field-listingUrl">Listing URL (optional)<input id="field-listingUrl" type="url" name="listingUrl" maxlength="500" value="${closingRiskCheckForm.getListingUrlValue()}" placeholder="https://..."></label>
                    <label for="field-listingBedrooms">Listing bedrooms<input id="field-listingBedrooms" type="number" name="listingBedrooms" min="1" max="20" value="${closingRiskCheckForm.getListingBedroomsValue()}"></label>
                    <label for="field-permitBedrooms">Permit bedrooms<input id="field-permitBedrooms" type="number" name="permitBedrooms" min="1" max="20" value="${closingRiskCheckForm.getPermitBedroomsValue()}"></label>
                </div>
            </details>
            <label class="intake-consent" for="field-consentAccepted"><input id="field-consentAccepted" name="consentAccepted" type="checkbox" required checked="${closingRiskCheckForm.isConsentAccepted()}"><span>I agree to free research and email updates. If records cannot answer my question, SepticPath may connect me with up to three relevant local professionals using the contact details I provide.</span></label>
            <details class="intake-terms"><summary>Research consent, privacy &amp; pricing</summary><p>${closingRiskCheckForm.getConsentTextSnapshot()}</p><p>Your contact details and private correspondence are not published. A connected professional may email or, if you provide a mobile number, manually call or text about this request. You are never required to hire anyone we connect you with. No automated marketing, unrelated solicitations, or resale of your request.</p><p>Submitted details and files are stored privately and emailed to the SepticPath operator. Do not submit Social Security numbers, payment details, access codes, or unrelated personal documents. <a href="${com.example.septic.web.StudioGuideRoutes.route("/privacy-policy/#research-publication", production)}">How research findings are used</a>.</p><p>We only offer the $29 unlock when we find useful, property-matched evidence that can answer your question. Agency fees are separate, require approval, and may apply even if no record is found or you do not unlock.</p></details>
            <button class="studio-button intake-submit" type="submit"><span data-submit-copy>${documentReviewMode ? "Send my file for review" : "Start my record investigation"}</span><span aria-hidden="true">→</span></button>
            <p class="intake-price">No card required. No upfront payment or automatic charge.<br>Optional $29 result unlock. Agency fees are additional at cost, with approval.</p>
            <noscript><p>You can use this form without JavaScript. Choose “Understand my file” under Additional details for document review.</p></noscript>
        </form>
        <aside class="intake-self-serve" data-offer-prep-download><p class="studio-overline">Self-serve alternative</p><h2>Prefer to contact the office yourself?</h2><p>Use the official lookup tools and request builder to prepare the property clues before you send—or Ask SepticPath to investigate.</p><a href="${com.example.septic.web.StudioGuideRoutes.route("/official-septic-lookup-tools/", production)}">Find the official route ↗</a></aside>
        <section class="intake-reply"><p class="studio-overline">Your reply will include</p><div><strong>FREE PREVIEW</strong><p>Source, property match, document scope, answerable questions and material limits—aimed within 1–2 business days when available.</p></div><div><strong>OPTIONAL $29 PACKAGE</strong><p>Property-specific answers and located source files only when the evidence supports a useful result.</p></div></section>
    @endif
        <ol class="intake-next"><li><span>01</span><h3>We trace the source.</h3><p>We check the property and contact the right office.</p></li><li><span>02</span><h3>You preview the findings.</h3><p>A free preview of the source, match and answerable questions.</p></li><li><span>03</span><h3>You choose what’s next.</h3><p>Unlock useful results for $29, only if you choose.</p></li></ol>
    </section>
</main>
<script src="${AssetPaths.versioned("/studio-intake.js")}" defer></script>
`)
