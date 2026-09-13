@import com.example.septic.data.model.CountyOperationalProof
@param CountyOperationalProof proof
@param String countyName
@param String sourceContext

<section class="section section--tight county-operational-proof" aria-labelledby="county-operational-proof-title">
    <div class="container county-operational-proof__frame" data-route-id="${proof.routeId()}">
        <div class="county-operational-proof__copy">
            <span class="eyebrow">Observed retrieval · ${proof.verifiedAt()}</span>
            <div class="county-operational-proof__status"><i aria-hidden="true"></i>${proof.statusLabel()}</div>
            <h2 id="county-operational-proof-title">${proof.title()}</h2>
            <p>${proof.summary()}</p>
            <ol class="county-operational-proof__steps" aria-label="Observed record retrieval steps">
                @for (var step : proof.steps())
                    <li>${step}</li>
                @endfor
            </ol>
            <div class="county-operational-proof__result">
                <span>What came back</span>
                <ul>
                    @for (var finding : proof.findings())
                        <li>${finding}</li>
                    @endfor
                </ul>
                <p><strong>Boundary:</strong> ${proof.limitation()}</p>
                <a href="/offer-prep-septic-file-check/#record-help"
                   data-record-help-cta
                   data-track-click="nav"
                   data-track-source-context="${sourceContext}_help"
                   data-track-target-type="lead_form">Ask us to investigate a ${countyName} property →</a>
            </div>
        </div>
        <div class="county-operational-proof__archive">
            <header>
                <span>The actual file trail</span>
                <p>Selected pages from separate completed requests, shown at near-full-page scale.</p>
            </header>
            <div class="county-operational-proof__documents county-operational-proof__documents--${proof.documents().size()}" aria-label="Privacy-reviewed full document pages">
                @for (var document : proof.documents())
                    <figure class="county-operational-proof__document">
                        <span>${document.stage()}</span>
                        <a href="${document.imagePath()}" target="_blank" rel="noreferrer"
                           data-track-click="nav"
                           data-track-source-context="${sourceContext}"
                           data-track-target-type="case_evidence"
                           aria-label="Open ${document.label()} at full size">
                            <img src="${document.imagePath()}" loading="lazy" width="1275" height="1650" alt="${document.imageAlt()}">
                        </a>
                        <figcaption><strong>${document.label()}</strong><small>Open full page ↗</small></figcaption>
                    </figure>
                @endfor
            </div>
            <p class="county-operational-proof__privacy"><i aria-hidden="true"></i><strong>Privacy reviewed:</strong> black bars remove identifying details; agency language, form structure, system data, and drawing geometry remain visible. Samples are not one property file.</p>
        </div>
    </div>
</section>
