@import com.example.septic.web.PageMeta
@import com.example.septic.web.WorkflowPacketView
@param PageMeta page
@param WorkflowPacketView packet

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${packet.eyebrow()}</span>
                <h1>${packet.heading()}</h1>
                <p class="lede">${packet.lede()}</p>
                <div class="hero__actions">
                    <a class="button button--primary"
                       href="${packet.primaryLink().path()}"
                       data-track-click="nav"
                       data-track-source-context="workflow_packet_primary"
                       data-track-target-type="state_workflow">${packet.primaryLink().title()}</a>
                    <a class="button button--secondary" href="#send-note">Jump to the send note</a>
                </div>
            </div>

            <article class="hero-sidecard">
                <span class="pill">${packet.packetLabel()}</span>
                <strong>${packet.sidecardTitle()}</strong>
                <p>${packet.sidecardBody()}</p>
                <p>${packet.channelNote()}</p>
            </article>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <div class="section-jump-strip" aria-label="Jump between sections">
                <span class="section-jump-strip__label">Jump between sections</span>
                <a href="#send-when">When to send</a>
                <a href="#send-note">Send note</a>
                <a href="#checklists">Checklists</a>
                @if (!packet.countyLinks().isEmpty())
                    <a href="#county-pages">County pages</a>
                @endif
                <a href="#sources">Sources</a>
            </div>

            <div class="intent-rail">
                <article class="intent-card intent-card--primary">
                    <span class="intent-card__label">Pinned first move</span>
                    <h2>${packet.primaryLink().title()}</h2>
                    <p>${packet.primaryLink().note()}</p>
                    <a class="button button--primary"
                       href="${packet.primaryLink().path()}"
                       data-track-click="nav"
                       data-track-source-context="workflow_packet_action_rail_primary"
                       data-track-target-type="state_workflow">Open the pinned workflow page</a>
                </article>

                <article class="intent-card">
                    <span class="intent-card__label">Why this exists</span>
                    <h2>Keep the first send narrow</h2>
                    <p>Send the workflow page first, then let the recipient move into county pages or official sources. Keep quote and estimator links out of the first explanation.</p>
                </article>

                <article class="intent-card">
                    <span class="intent-card__label">What this should change</span>
                    <h2>Shorter explanation, cleaner handoff</h2>
                    <p>${packet.channelNote()}</p>
                </article>
            </div>
        </div>
    </section>

    <section class="section section--tight" id="send-when">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>When to send this</h2>
                <ul class="detail-list">
                    @for (var bullet : packet.sendWhenBullets())
                        <li>${bullet}</li>
                    @endfor
                </ul>
            </article>

            <article class="detail-card">
                <h2>What the recipient should open first</h2>
                <p>${packet.primaryLink().note()}</p>
                <a class="button button--secondary"
                   href="${packet.primaryLink().path()}"
                   data-track-click="nav"
                   data-track-source-context="workflow_packet_primary_detail"
                   data-track-target-type="state_workflow">Open ${packet.primaryLink().title()}</a>
            </article>

            <article class="detail-card">
                <h2>Vendor guardrail</h2>
                <p>Do not lead with a generic cost page. The recipient should reach the state workflow page while the file, permit, or buyer question is still the main problem.</p>
            </article>
        </div>
    </section>

    <section class="section section--tight" id="send-note">
        <div class="container detail-grid">
            <article class="detail-card detail-card--full">
                <h2>Share-ready note</h2>
                <p class="packet-copy__subject"><strong>Subject:</strong> ${packet.shareSubject()}</p>
                <pre class="packet-copy">${packet.shareBody()}</pre>
            </article>
        </div>
    </section>

    <section class="section section--tight" id="checklists">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>Recipient checklist</h2>
                <ol class="detail-list detail-list--numbered">
                    @for (var item : packet.recipientChecklist())
                        <li>${item}</li>
                    @endfor
                </ol>
            </article>

            <article class="detail-card">
                <h2>Vendor checklist</h2>
                <ol class="detail-list detail-list--numbered">
                    @for (var item : packet.vendorChecklist())
                        <li>${item}</li>
                    @endfor
                </ol>
            </article>
        </div>
    </section>

    @if (!packet.countyLinks().isEmpty())
        <section class="section section--tight" id="county-pages">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">County Wedge</span>
                    <h2>County pages behind this packet</h2>
                    <p class="lede">Use a county page when the recipient already knows the state and the real blocker is a county file or local office path.</p>
                </div>

                <div class="detail-grid">
                    @for (var link : packet.countyLinks())
                        <article class="detail-card">
                            <h3><a href="${link.path()}"
                                   data-track-click="nav"
                                   data-track-source-context="workflow_packet_county_link"
                                   data-track-target-type="county_page">${link.title()}</a></h3>
                            <p>${link.note()}</p>
                            <a class="button button--secondary"
                               href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="workflow_packet_county_cta"
                               data-track-target-type="county_page">Open county page</a>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    <section class="section section--tight" id="sources">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>Supporting workflow pages</h2>
                <div class="packet-link-stack">
                    @for (var link : packet.supportingLinks())
                        <div>
                            <strong><a href="${link.path()}"
                                       data-track-click="nav"
                                       data-track-source-context="workflow_packet_supporting_link"
                                       data-track-target-type="internal_page">${link.title()}</a></strong>
                            <p>${link.note()}</p>
                        </div>
                    @endfor
                </div>
            </article>

            <article class="detail-card">
                <h2>Official sources to keep behind the handoff</h2>
                <div class="packet-link-stack">
                    @for (var source : packet.officialSources())
                        <div>
                            <strong><a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a></strong>
                            <p>${source.agencyName()} | Last verified ${source.lastVerifiedAt()}</p>
                        </div>
                    @endfor
                </div>
            </article>
        </div>
    </section>
`)
