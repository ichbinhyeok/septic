@import com.example.septic.web.PageMeta
@import com.example.septic.service.PaidUnlockStore
@param PageMeta page
@param PaidUnlockStore.Offer offer
@param String publicToken
@param java.util.List<String> previewSlots
@param boolean paymentConfigured
@param String paypalSdkUrl

@template.layouts.app(page = page, designFamily = "transaction", content = @`
<article class="transaction-page transaction-page--checkout"
         data-ga-event="unlock_offer_viewed"
         data-ga-track-once="unlock-offer:${publicToken}"
         data-ga-param-offer-type="reviewed_record_package"
         data-ga-param-currency="USD"
         data-ga-param-value="29">
    <section class="transaction-hero" aria-labelledby="paid-unlock-title">
        <div class="transaction-hero__shade" aria-hidden="true"></div>
        <img class="transaction-hero__image" src="/images/studio/work-desk-v1.webp" alt="" width="1536" height="1024" fetchpriority="high">
        <div class="container transaction-hero__inner">
            <p class="transaction-kicker">Private record packet for ${offer.propertyLabel()}</p>
            <h1 id="paid-unlock-title">The records are found.<br>Make sense of them.</h1>
            <p>Your property-matched sources have been reviewed and assembled with a clear reading guide. See exactly what is included before you choose to unlock the findings.</p>
            <a class="transaction-hero__cta" href="#unlock-checkout">See your $29 packet <span aria-hidden="true">↗</span></a>
            <span class="transaction-caption">One-time payment · Original source PDFs · Private delivery</span>
        </div>
    </section>

    <section class="transaction-evidence" aria-labelledby="transaction-evidence-title">
        <div class="container transaction-grid">
            <header><p class="transaction-kicker">Verified before checkout</p><h2 id="transaction-evidence-title">A real file. A defined answer.</h2><p class="transaction-evidence__intro">The source, contents and limits are visible here. The property-specific findings and original pages are reserved for your private packet.</p></header>
            <dl class="transaction-ledger">
                <div><dt>Official source</dt><dd>${offer.sourceSummary()}</dd></div>
                <div><dt>Document scope</dt><dd>${offer.documentScope()}</dd></div>
                <div><dt>Your question</dt><dd>${offer.answerableQuestion()}</dd></div>
                <div><dt>Important limits</dt><dd>${offer.limitations()}</dd></div>
            </dl>
        </div>
    </section>

    @if (!previewSlots.isEmpty())
    <section class="transaction-preview" aria-labelledby="transaction-preview-title">
        <div class="container">
            <header class="transaction-preview__heading">
                <div><p class="transaction-kicker">Actual pages, safely masked</p><h2 id="transaction-preview-title">See the real packet before you unlock.</h2></div>
                <p>These images come from the prepared PDFs for this property. The address and document structure remain visible; findings, diagrams and personal details are permanently covered in the preview images.</p>
            </header>
            <div class="transaction-preview__grid">
                @if (previewSlots.contains("official-record"))
                <figure>
                    <img src="/unlock/${publicToken}/preview/official-record/" alt="Masked image of the actual matched official record" loading="lazy" width="977" height="1613">
                    <figcaption><strong>Matched official source</strong><span>The actual source page; location details and personal information are covered.</span></figcaption>
                </figure>
                @endif
                @if (previewSlots.contains("reviewed-brief"))
                <figure>
                    <img src="/unlock/${publicToken}/preview/reviewed-brief/" alt="Masked image of the actual completed SepticPath review" loading="lazy" width="979" height="1268">
                    <figcaption><strong>Completed SepticPath review</strong><span>The property match and structure remain visible; the findings unlock with the full package.</span></figcaption>
                </figure>
                @endif
            </div>
            <ol class="transaction-preview__benefits"><li>Matched to this property</li><li>Reviewed page by page</li><li>Full findings and unchanged originals after payment</li></ol>
        </div>
    </section>
    @endif

    <section class="transaction-purchase" id="unlock-checkout" aria-labelledby="transaction-purchase-title">
        <div class="container transaction-purchase__grid">
            <div class="transaction-purchase__intro"><p class="transaction-kicker">Your choice</p><h2 id="transaction-purchase-title">Unlock your complete packet.</h2><p>The property-specific answer, reading guide and original records are ready. Historical documents cannot verify today's underground position or condition; the limits above are part of the review.</p></div>
            <ul class="transaction-includes"><li>Exact-property source matching</li><li>Your property-specific reviewed answer</li><li>Original source records and reviewed findings</li><li>Plain-English limits and next steps</li><li>A private, time-limited delivery link</li></ul>
            <div class="transaction-checkout">
                <p class="transaction-price">One complete packet <span>·</span> US $29</p>
                <p class="transaction-payment-options">One-time payment. No subscription. PayPal handles checkout; eligible buyers can choose debit or credit card without a PayPal account.</p>
                @if (offer.status().equals("PAID"))
                    <p class="transaction-state transaction-state--success">Payment is complete. Use the private delivery link in your email.</p>
                @elseif (paymentConfigured)
                    <div id="paypal-button-container" aria-label="Pay securely with PayPal or card"></div>
                    <p id="paid-unlock-status" class="transaction-state" aria-live="polite"></p>
                    <noscript>JavaScript is required for secure checkout. Reply to your SepticPath preview email if you need help.</noscript>
                @else
                    <p class="transaction-state">Secure checkout is being prepared. You have not been charged.</p>
                @endif
                <p class="transaction-security">Secure provider-hosted checkout. No automatic charge.<br>Never send card details by email.</p>
            </div>
        </div>
    </section>
    <footer class="transaction-reference"><div class="container"><span>Private, property-matched offer</span><span>Official records can document history; they do not verify current field conditions.</span></div></footer>
</article>

@if (paymentConfigured && !offer.status().equals("PAID"))
<script src="${paypalSdkUrl}"></script>
<script>
(() => {
    const status = document.getElementById('paid-unlock-status');
    const token = '${publicToken}';
    const setStatus = (message, error = false) => {
        status.textContent = message;
        status.classList.toggle('transaction-state--error', error);
    };
    paypal.Buttons({
        style: { layout: 'vertical', shape: 'rect', label: 'pay' },
        createOrder: async () => {
            setStatus('Opening secure checkout…');
            if (typeof window.gtag === 'function') {
                window.gtag('event', 'begin_checkout', { currency: 'USD', value: 29, offer_type: 'reviewed_record_package' });
                window.gtag('event', 'unlock_checkout_started', { currency: 'USD', value: 29, offer_type: 'reviewed_record_package' });
            }
            const response = await fetch('/api/paid-unlocks/' + token + '/paypal/orders', { method: 'POST', headers: { 'Accept': 'application/json' } });
            const data = await response.json();
            if (!response.ok || !data.id) throw new Error(data.error || 'Could not start checkout.');
            return data.id;
        },
        onApprove: async (data) => {
            setStatus('Confirming payment and preparing your private delivery…');
            const response = await fetch('/api/paid-unlocks/' + token + '/paypal/orders/' + data.orderID + '/capture', { method: 'POST', headers: { 'Accept': 'application/json' } });
            const result = await response.json();
            if (!response.ok || !result.deliveryUrl) throw new Error(result.error || 'Payment needs review.');
            setStatus(result.emailSent ? 'Payment confirmed. Your private delivery link was emailed to you.' : 'Payment confirmed. Opening your private delivery now.');
            if (typeof window.gtag === 'function') {
                window.gtag('event', 'unlock_purchase_completed', { currency: 'USD', value: 29, offer_type: 'reviewed_record_package' });
            }
            window.location.assign(result.deliveryUrl);
        },
        onCancel: () => {
            if (typeof window.gtag === 'function') window.gtag('event', 'unlock_checkout_cancelled', { offer_type: 'reviewed_record_package' });
            setStatus('Checkout was cancelled. You have not been charged.');
        },
        onError: () => {
            if (typeof window.gtag === 'function') window.gtag('event', 'unlock_checkout_failed', { offer_type: 'reviewed_record_package' });
            setStatus('Checkout could not be completed. You have not been charged; please try again.', true);
        }
    }).render('#paypal-button-container');
})();
</script>
@endif
`)
