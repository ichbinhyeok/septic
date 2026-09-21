@import com.example.septic.web.PageMeta
@import com.example.septic.service.PaidUnlockStore
@param PageMeta page
@param PaidUnlockStore.Offer offer
@param String publicToken
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
            <p class="transaction-kicker">Private result preview</p>
            <h1 id="paid-unlock-title">Your reviewed property record is ready.</h1>
            <p>Matched to <strong>${offer.propertyLabel()}</strong>. Read against your question. Ready to deliver.</p>
            <span class="transaction-caption">Official-source records · Human review · Clear limits</span>
        </div>
    </section>

    <section class="transaction-evidence" aria-labelledby="transaction-evidence-title">
        <div class="container transaction-grid">
            <header><p class="transaction-kicker">Before you unlock</p><h2 id="transaction-evidence-title">What the record can answer.</h2></header>
            <dl class="transaction-ledger">
                <div><dt>Official source</dt><dd>${offer.sourceSummary()}</dd></div>
                <div><dt>Document scope</dt><dd>${offer.documentScope()}</dd></div>
                <div><dt>Your question</dt><dd>${offer.answerableQuestion()}</dd></div>
                <div><dt>Important limits</dt><dd>${offer.limitations()}</dd></div>
            </dl>
        </div>
    </section>

    <section class="transaction-purchase" aria-labelledby="transaction-purchase-title">
        <div class="container transaction-purchase__grid">
            <div class="transaction-purchase__intro"><p class="transaction-kicker">The reviewed package</p><h2 id="transaction-purchase-title">A complete, human review of the records that matter.</h2><p>Research and agency requests were free. This optional purchase is for the useful, property-matched records and reviewed answers described above.</p></div>
            <ul class="transaction-includes"><li>Original source records</li><li>Property-specific findings</li><li>Plain-English limits and next steps</li><li>A private, time-limited delivery link</li></ul>
            <div class="transaction-checkout">
                <p class="transaction-price">One-time <span>·</span> US $29</p>
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
    <footer class="transaction-reference"><div class="container"><span>Private offer</span><span>Reference: ${offer.requestReference()}</span><span>Official records can document history; they do not verify current field conditions.</span></div></footer>
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
