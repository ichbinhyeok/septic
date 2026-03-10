@import com.example.septic.web.PageMeta
@import com.example.septic.web.ContactRequestForm
@import com.example.septic.data.model.StateProfile
@param PageMeta page
@param ContactRequestForm contactRequestForm
@param boolean contactHasErrors
@param String contactRequestId
@param java.util.List<StateProfile> states

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container page-hero">
            <div class="page-hero__content">
                <span class="eyebrow">Contact</span>
                <h1>General questions, source corrections, and privacy requests</h1>
                <p class="lede">Use this page for questions about the site itself. If you need septic pricing help, start with the estimator or quote flow so the request carries the project context that makes it useful.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="#contact-request">Jump to contact form</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/?quoteMode=true#quote-request">Use the quote form for project pricing</a>
                </div>
                <div class="hero__notes">
                    <span>General questions</span>
                    <span>Source correction</span>
                    <span>Privacy request</span>
                    <span>Partnership inquiry</span>
                </div>
            </div>

            <aside class="hero-sidecard">
                <span class="pill">Working contact channel</span>
                <strong>Best for non-quote requests</strong>
                <p>This intake form gives the site a real public contact path before a support inbox or business address is published. Project-specific quote requests should still start in the estimator.</p>
                <p>The form appears immediately below this hero so source corrections and privacy requests do not get buried under trust copy.</p>
            </aside>
        </div>
    </section>

    <section class="section">
        <div class="container content-grid">
            <section class="quote-panel" id="contact-request">
                <div class="quote-panel__header">
                    <div>
                        <span class="eyebrow">Public intake</span>
                        <h2>Send a contact request</h2>
                    </div>
                    <span class="pill">Stored with provenance</span>
                </div>

                @if (contactRequestId != null)
                    <article class="status-box status-box--success">
                        <strong>Request received</strong>
                        <p>Your request is stored with reference ID ${contactRequestId}. Keep that ID if you need to follow up on a source correction or privacy request.</p>
                    </article>
                @endif

                @if (contactHasErrors)
                    <article class="status-box status-box--error">
                        <strong>Finish the required fields</strong>
                        <p>Name, email, message, and acknowledgement are required before this request can be stored.</p>
                    </article>
                @endif

                <form class="quote-form" method="post" action="/contact/">
                    <div class="field-grid">
                        <label class="field">
                            <span>Full name</span>
                            <input type="text" name="fullName" value="${contactRequestForm.getFullNameValue()}">
                        </label>

                        <label class="field">
                            <span>Email</span>
                            <input type="email" name="email" value="${contactRequestForm.getEmailValue()}">
                        </label>

                        <label class="field">
                            <span>Topic</span>
                            <select name="topic">
                                <option value="general_question" selected="${"general_question".equals(contactRequestForm.getTopic())}">General question</option>
                                <option value="source_correction" selected="${"source_correction".equals(contactRequestForm.getTopic())}">Source correction</option>
                                <option value="privacy_request" selected="${"privacy_request".equals(contactRequestForm.getTopic())}">Privacy request</option>
                                <option value="partnership" selected="${"partnership".equals(contactRequestForm.getTopic())}">Partnership inquiry</option>
                            </select>
                        </label>

                        <label class="field">
                            <span>Property state (optional)</span>
                            <select name="stateCode">
                                <option value="" selected="${contactRequestForm.getStateCodeValue().isBlank()}">No state selected</option>
                                @for (var state : states)
                                    <option value="${state.stateCode()}" selected="${state.stateCode().equals(contactRequestForm.getStateCodeValue())}">${state.stateName()}</option>
                                @endfor
                            </select>
                        </label>

                        <label class="field field--full">
                            <span>Message</span>
                            <textarea name="message" maxlength="2000">${contactRequestForm.getMessageValue()}</textarea>
                        </label>
                    </div>

                    <label class="checkbox checkbox--wide">
                        <input type="checkbox" name="acknowledgementAccepted" checked="${contactRequestForm.isAcknowledgementAccepted()}">
                        <span>${contactRequestForm.getAcknowledgementTextSnapshot()}</span>
                    </label>

                    <div class="form-actions">
                        <button class="button button--primary" type="submit">Send contact request</button>
                        <p>This form is for project operations and trust questions. Septic pricing requests should use the estimator or quote flow.</p>
                    </div>
                </form>
            </section>

            <aside class="detail-card">
                <h2>Response expectations</h2>
                <p>This site is still estimate-first. The contact form creates a durable intake record, but it should not be treated as emergency service dispatch.</p>
                <ul class="detail-list">
                    <li>Use the Source correction topic when a state page or official-source summary needs review.</li>
                    <li>Use the Privacy request topic when the question is about stored leads, events, or data-handling.</li>
                    <li>Use General question or Partnership inquiry for everything else that is not a quote request.</li>
                </ul>
            </aside>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>Use this form when</h2>
                <p>This form is for operational and trust-related questions about the project.</p>
                <ul class="detail-list">
                    <li>You found a source issue or want to flag a state-specific update.</li>
                    <li>You need a privacy or data-handling request routed through the site.</li>
                    <li>You have a partnership or project-level question that is not a local quote request.</li>
                </ul>
            </article>

            <article class="detail-card">
                <h2>Use the estimator instead when</h2>
                <p>Homeowner pricing and contractor matching work best when the request includes the project assumptions used in the estimate.</p>
                <ul class="detail-list">
                    <li>Run the cost estimator first for install, replacement, inspection, or buyer planning questions.</li>
                    <li>Use the short quote form after the estimate if you want local pricing help.</li>
                    <li>Keep this contact form for site operations, not urgent septic dispatch.</li>
                </ul>
            </article>
        </div>
    </section>
`)
