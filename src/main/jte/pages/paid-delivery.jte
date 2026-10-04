@import com.example.septic.web.PageMeta
@import com.example.septic.service.PaidUnlockStore
@param PageMeta page
@param PaidUnlockStore.DeliveryPreview delivery
@param String downloadToken
@param String deliveryExpiresAt
@param com.example.septic.web.RecordResultNextStep nextStep

@template.layouts.app(page = page, designFamily = "transaction", content = @`
<article class="transaction-page transaction-page--delivery">
    <section class="delivery-hero" aria-labelledby="delivery-title">
        <img src="/images/studio/official-lookup-files-v1.webp" alt="" width="1536" height="1024" fetchpriority="high">
        <div class="delivery-hero__shade" aria-hidden="true"></div>
        <div class="container delivery-hero__inner">
            <p class="delivery-status"><span aria-hidden="true"></span>${"RELEASED_FREE".equals(delivery.offer().status()) ? "Free result · Ready to download" : "Payment confirmed · Package ready"}</p>
            <h1 id="delivery-title">Your record package is ready.</h1>
            <p>Private delivery for <strong>${delivery.offer().propertyLabel()}</strong>.</p>
            <a class="delivery-download" href="/paid-unlock/download/${downloadToken}"><span aria-hidden="true">↓</span> Download the reviewed package <b aria-hidden="true">→</b></a>
            <small>Keep the downloaded file. This private link expires and has a download limit.</small>
        </div>
    </section>

    <section class="delivery-contents" aria-labelledby="delivery-contents-title">
        <div class="container">
            <header><p class="transaction-kicker">Private delivery</p><h2 id="delivery-contents-title">What’s inside.</h2></header>
            <dl class="delivery-ledger">
                <div><dt>Original source records</dt><dd>${delivery.offer().documentScope()}</dd></div>
                <div><dt>Reviewed findings</dt><dd>${delivery.offer().answerableQuestion()}</dd></div>
                <div><dt>Limits and next steps</dt><dd>${delivery.offer().limitations()}</dd></div>
                <div><dt>File integrity verified</dt><dd>The delivered file still matches the approved release package.</dd></div>
                <div><dt>Private link</dt><dd>Expires ${deliveryExpiresAt}</dd></div>
                <div><dt>Downloads remaining</dt><dd>${delivery.downloadsRemaining()} of ${delivery.maxDownloads()}</dd></div>
            </dl>
        </div>
    </section>

    <section class="delivery-followup" aria-labelledby="delivery-next-title"><div class="container"><div><p class="transaction-kicker">Your next step</p><h2 id="delivery-next-title">${nextStep.heading()}</h2><p>${nextStep.introduction()}</p></div><div><ol class="delivery-checklist">@for(var item : nextStep.checklist())<li>${item}</li>@endfor</ol><p>Questions? Reply to your delivery email with reference <strong>${delivery.offer().requestReference()}</strong>. No new records request is needed.</p>@if(nextStep.offersService())<p>Local professionals price their work separately. Availability varies; a service inquiry is not a booking.</p><a class="button button--secondary" href="${nextStep.servicePath()}">${nextStep.action()} →</a>@endif</div></div></section>
</article>
`)
