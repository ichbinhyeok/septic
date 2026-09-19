@import com.example.septic.web.PageMeta
@import com.example.septic.service.PaidUnlockStore
@param PageMeta page
@param PaidUnlockStore.Offer offer
@param String publicToken
@param boolean paymentConfigured
@param String paypalSdkUrl

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish paid-unlock" aria-labelledby="paid-unlock-title">
        <div class="container narrow">
            <span class="eyebrow">Private result preview · No automatic charge</span>
            <h1 id="paid-unlock-title">Your reviewed property record is ready.</h1>
            <p class="lede">We matched an official record package to <strong>${offer.propertyLabel()}</strong> and reviewed what it can answer before offering payment.</p>

            <article class="detail-card paid-unlock__card">
                <h2>What we found</h2>
                <dl class="detail-list">
                    <div><dt>Official source</dt><dd>${offer.sourceSummary()}</dd></div>
                    <div><dt>Document scope</dt><dd>${offer.documentScope()}</dd></div>
                    <div><dt>Your question</dt><dd>${offer.answerableQuestion()}</dd></div>
                    <div><dt>Important limits</dt><dd>${offer.limitations()}</dd></div>
                </dl>
            </article>

            <article class="detail-card paid-unlock__card">
                <span class="eyebrow">One-time purchase · US $29</span>
                <h2>Unlock the complete reviewed package</h2>
                <ul class="detail-list">
                    <li>The original source records we located</li>
                    <li>The property-specific answer to your question</li>
                    <li>A plain-English explanation of what the record proves and what it cannot establish</li>
                    <li>A private download link sent automatically after verified payment</li>
                </ul>
                <p class="detail-list__note">Research and agency requests were free. This optional payment is for the useful, property-matched records and reviewed answers described above.</p>

                @if (offer.status().equals("PAID"))
                    <p class="form-status form-status--success">Payment has already been completed for this package. Use the private download link in your delivery email.</p>
                @elseif (paymentConfigured)
                    <div id="paypal-button-container" aria-label="Pay securely with PayPal or card"></div>
                    <p id="paid-unlock-status" class="form-status" aria-live="polite"></p>
                    <noscript>JavaScript is required for secure checkout. Reply to your SepticPath preview email if you need help.</noscript>
                @else
                    <p class="form-status">Secure checkout is being prepared. You have not been charged.</p>
                @endif
            </article>

            <p class="detail-list__note">Reference: ${offer.requestReference()} · Never send card details by email.</p>
        </div>
    </section>

    @if (paymentConfigured && !offer.status().equals("PAID"))
        <script src="${paypalSdkUrl}"></script>
        <script>
            (() => {
                const status = document.getElementById('paid-unlock-status');
                const token = '${publicToken}';
                const setStatus = (message, error = false) => {
                    status.textContent = message;
                    status.classList.toggle('form-status--error', error);
                };
                paypal.Buttons({
                    style: { layout: 'vertical', shape: 'rect', label: 'pay' },
                    createOrder: async () => {
                        setStatus('Opening secure checkout…');
                        const response = await fetch('/api/paid-unlocks/' + token + '/paypal/orders', {
                            method: 'POST',
                            headers: { 'Accept': 'application/json' }
                        });
                        const data = await response.json();
                        if (!response.ok || !data.id) throw new Error(data.error || 'Could not start checkout.');
                        return data.id;
                    },
                    onApprove: async (data) => {
                        setStatus('Confirming payment and preparing your private download…');
                        const response = await fetch('/api/paid-unlocks/' + token + '/paypal/orders/' + data.orderID + '/capture', {
                            method: 'POST',
                            headers: { 'Accept': 'application/json' }
                        });
                        const result = await response.json();
                        if (!response.ok || !result.downloadUrl) throw new Error(result.error || 'Payment needs review.');
                        setStatus(result.emailSent
                            ? 'Payment confirmed. Your package is ready, and the private link was emailed to you.'
                            : 'Payment confirmed. Your package is ready for download now.');
                        window.location.assign(result.downloadUrl);
                    },
                    onCancel: () => setStatus('Checkout was cancelled. You have not been charged.'),
                    onError: () => setStatus('Checkout could not be completed. You have not been charged; please try again.', true)
                }).render('#paypal-button-container');
            })();
        </script>
    @endif
`)
