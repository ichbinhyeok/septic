@import com.example.septic.web.PageMeta
@import com.example.septic.service.PaidUnlockStore
@param PageMeta page
@param PaidUnlockStore.Offer offer
@param String publicToken
@param java.util.List<String> previewSlots

@template.layouts.app(page = page, designFamily = "transaction", content = @`
<article class="transaction-page transaction-page--checkout">
    <section class="transaction-hero" aria-labelledby="paid-unlock-title">
        <div class="transaction-hero__shade" aria-hidden="true"></div>
        <img class="transaction-hero__image" src="/images/studio/work-desk-v1.webp" alt="" width="1536" height="1024" fetchpriority="high">
        <div class="container transaction-hero__inner">
            <p class="transaction-kicker">Private record result for ${offer.propertyLabel()}</p>
            <h1 id="paid-unlock-title">Your records.<br>No paid unlock.</h1>
            <p>Record results are now free. This is an earlier result link; use your private delivery email or reply with the reference below for an updated link after release review.</p>
            <a class="transaction-hero__cta" href="#result-reference">View your reference <span aria-hidden="true">↓</span></a>
            <span class="transaction-caption">Original sources · Reviewed explanation · Private delivery</span>
        </div>
    </section>
    <section class="transaction-evidence" aria-labelledby="transaction-evidence-title">
        <div class="container transaction-grid">
            <header><p class="transaction-kicker">Your earlier result</p><h2 id="transaction-evidence-title">The source and its scope.</h2><p>These details describe the previously prepared record package. They do not establish current field conditions.</p></header>
            <dl class="transaction-ledger">
                <div><dt>Official source</dt><dd>${offer.sourceSummary()}</dd></div>
                <div><dt>Document scope</dt><dd>${offer.documentScope()}</dd></div>
                <div><dt>Your question</dt><dd>${offer.answerableQuestion()}</dd></div>
                <div><dt>Important limits</dt><dd>${offer.limitations()}</dd></div>
            </dl>
        </div>
    </section>
    @if (!previewSlots.isEmpty())
    <section class="transaction-preview" aria-label="Earlier masked source previews">
        <div class="container"><div class="transaction-preview__grid">
            @if (previewSlots.contains("official-record"))
            <figure><img src="/unlock/${publicToken}/preview/official-record/" alt="Earlier masked image of the matched official record" loading="lazy" width="977" height="1613"><figcaption>Earlier masked source preview. Your originals are delivered privately.</figcaption></figure>
            @endif
            @if (previewSlots.contains("reviewed-brief"))
            <figure><img src="/unlock/${publicToken}/preview/reviewed-brief/" alt="Earlier masked image of the reviewed explanation" loading="lazy" width="979" height="1268"><figcaption>Earlier masked explanation. No new payment is required.</figcaption></figure>
            @endif
        </div></div>
    </section>
    @endif
    <section class="delivery-followup" id="result-reference"><div class="container"><div><p class="transaction-kicker">Keep this reference</p><h2>${offer.requestReference()}</h2></div><div>
        @if ("PAID".equals(offer.status()))
        <p>Your earlier payment is recorded. Use the private delivery link in your email, or reply if it needs attention. You will not be asked to pay again.</p>
        @else
        <p>Reply to your SepticPath result email with this reference. We will check the approved files and recipient before issuing the free private delivery link.</p>
        @endif
        <p>Your original files are not made public by this change.</p>
    </div></div></section>
</article>
`)
