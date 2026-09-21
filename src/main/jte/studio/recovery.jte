@import com.example.septic.web.PageMeta
@param String recoveryKind = "not-found"
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = recoveryKind.equals("server") ? "Temporary interruption" : "Route not found", heroImage = "/images/brand/septicpath-aerial-hero-v1.jpg", pageClass = "studio-recovery", pageStyles = "/studio-recovery.css", content = @`
<main id="main">
    <section class="recovery-preview-hero" aria-labelledby="recovery-preview-title">
        <div class="studio-wrap recovery-preview-hero__inner">
            <p class="studio-overline">${recoveryKind.equals("server") ? "Temporary interruption · 500" : "Route not found · 404"}</p>
            <h1 id="recovery-preview-title">${recoveryKind.equals("server") ? "This route paused. Your research can keep moving." : "This route ends here. Your property search doesn’t."}</h1>
            <p>${recoveryKind.equals("server") ? "We couldn’t load this page. The records workspace and guides are still available while the issue is reviewed." : "The page may have moved. Start again with the records workspace, browse a guide, or tell us what you need."}</p>
            <div class="recovery-preview-actions"><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-records-request-builder/", production)}">Open the records workspace <span aria-hidden="true">→</span></a><a href="${com.example.septic.web.StudioGuideRoutes.route("/states/", production)}">Browse property guides</a></div>
            <small>No request or payment is created on this screen.</small>
        </div>
    </section>
    <section class="recovery-preview-next" aria-labelledby="recovery-preview-next-title">
        <div class="studio-wrap">
            <header><h2 id="recovery-preview-next-title">Choose the next useful step.</h2><p>Your work continues from here.</p></header>
            <nav aria-label="Recovery options"><a href="${com.example.septic.web.StudioGuideRoutes.route("/official-septic-lookup-tools/", production)}"><span>Find an official record route</span><small>Start with the agency and source path.</small><b aria-hidden="true">→</b></a><a href="${com.example.septic.web.StudioGuideRoutes.route("/states/", production)}"><span>Read a field guide</span><small>Get the context before the next decision.</small><b aria-hidden="true">→</b></a><a href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}"><span>Ask us to research it</span><small>Tell us the property and the open question.</small><b aria-hidden="true">→</b></a></nav>
        </div>
    </section>
    <section class="recovery-preview-help"><div class="studio-wrap"><p class="studio-overline">A human route</p><h2>Still not sure where the record lives?</h2><a class="studio-button studio-button--light" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask SepticPath <span aria-hidden="true">→</span></a></div></section>
</main>
`)
