/* Isolated route lookup and local-only drafting. No leads, sends or persistent storage. */
(() => {
    'use strict';
    const form = document.querySelector('#route-form');
    const address = document.querySelector('#route-address');
    const submit = document.querySelector('#route-submit');
    const result = document.querySelector('#route-result');
    const heading = document.querySelector('#result-heading');
    const message = document.querySelector('#result-message');
    const facts = document.querySelector('#result-facts');
    const actions = document.querySelector('#result-actions');
    const steps = document.querySelector('#result-steps');
    const draft = document.querySelector('#draft-form');
    const output = document.querySelector('#request-output');
    const status = document.querySelector('#draft-status');
    let pending = null;
    let version = 0;

    function safeLink(label, path) {
        if (!path) return null;
        let url;
        try { url = new URL(path, location.origin); } catch { return null; }
        if (!['https:', 'http:'].includes(url.protocol)) return null;
        const link = document.createElement('a');
        link.textContent = label;
        link.href = url.href;
        if (url.origin !== location.origin) { link.target = '_blank'; link.rel = 'noopener noreferrer'; }
        return link;
    }
    function showResult(title, copy) {
        result.hidden = false;
        heading.textContent = title;
        message.textContent = copy;
        facts.replaceChildren(); actions.replaceChildren(); steps.replaceChildren();
    }
    function addFact(label, value) {
        if (!value) return;
        const row = document.createElement('div');
        const term = document.createElement('dt'); term.textContent = label;
        const detail = document.createElement('dd'); detail.textContent = value;
        row.append(term, detail); facts.append(row);
    }
    function clearLookup() {
        version++;
        pending?.abort(); pending = null;
        submit.disabled = false;
        submit.textContent = 'Find the official route ↗';
        form.removeAttribute('aria-busy'); result.hidden = true;
    }
    address.addEventListener('input', clearLookup);
    form.addEventListener('submit', async event => {
        event.preventDefault();
        clearLookup();
        const attempt = version;
        const controller = new AbortController(); pending = controller;
        submit.disabled = true; submit.textContent = 'Finding the route…';
        form.setAttribute('aria-busy', 'true');
        showResult('Finding your county…', 'Checking the address and available records routes.');
        const timer = setTimeout(() => controller.abort(), 20000);
        try {
            const response = await fetch('/api/address-record-finder', {
                method: 'POST', headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ address: address.value.trim() }), signal: controller.signal
            });
            if (!response.ok && response.status !== 400) throw new Error('Lookup unavailable');
            const data = await response.json();
            if (attempt !== version) return;
            if (!data || typeof data.heading !== 'string' || typeof data.status !== 'string') throw new Error('Invalid response');
            showResult(data.heading, data.message || '');
            addFact('Matched address', data.matchedAddress);
            addFact('County / state', [data.countyName, data.stateName].filter(Boolean).join(', '));
            addFact('Responsible office', data.officeLabel);
            addFact('Contact', data.contactLine);
            addFact('Route reviewed', data.routeReviewedAt);
            addFact('Identifiers to prepare', Array.isArray(data.requiredIdentifiers) ? data.requiredIdentifiers.join(', ') : '');
            const links = [
                { label: 'Open official source ↗', path: data.officialRouteUrl },
                { label: data.routeTitle || 'Explore the records route ↗', path: data.routePath },
                ...(Array.isArray(data.relayActions) ? data.relayActions : [])
            ];
            const seen = new Set();
            links.forEach(item => {
                const link = safeLink(item.label, item.path);
                if (link && !seen.has(link.href)) { seen.add(link.href); actions.append(link); }
            });
            if (data.matchedAddress && !['invalid', 'not_found', 'unavailable'].includes(data.status)) {
                const carry = document.createElement('button');
                carry.type = 'button'; carry.className = 'studio-button';
                carry.textContent = 'Use this address in my draft ↓';
                carry.addEventListener('click', () => {
                    const changedProperty = draft.elements.address.value.trim().toLowerCase() !== data.matchedAddress.trim().toLowerCase();
                    if (changedProperty) {
                        draft.elements.parcel.value = '';
                        draft.elements.owner.value = '';
                    }
                    draft.elements.address.value = data.matchedAddress;
                    draft.elements.county.value = data.countyName || '';
                    draft.elements.state.value = data.stateCode || '';
                    refreshDraft();
                    status.textContent = changedProperty
                        ? 'Matched address added; previous parcel and owner details cleared. Confirm the property before sending.'
                        : 'Matched address added. Confirm it is your property before sending.';
                    draft.elements.address.focus();
                    document.querySelector('#draft-request').scrollIntoView({ behavior: matchMedia('(prefers-reduced-motion: reduce)').matches ? 'instant' : 'smooth' });
                });
                actions.prepend(carry);
            }
            (Array.isArray(data.relaySteps) ? data.relaySteps : []).forEach(text => {
                const item = document.createElement('li'); item.textContent = text; steps.append(item);
            });
        } catch {
            if (attempt !== version) return;
            showResult('We could not finish the lookup.', 'Try again, or browse the state guides. No property record has been retrieved.');
            actions.append(safeLink('Browse the state guides ↗', '/design-preview/studio/guides/'));
        } finally {
            clearTimeout(timer);
            if (attempt === version) {
                pending = null; submit.disabled = false; submit.textContent = 'Find the official route ↗';
                form.removeAttribute('aria-busy'); heading.focus({ preventScroll: true });
            }
        }
    });

    function fitMessage() {
        // Grow with the full letter, including font loading and responsive line wraps.
        const scrollX = window.scrollX, scrollY = window.scrollY;
        output.style.height = 'auto';
        output.style.height = `${output.scrollHeight + 2}px`;
        window.scrollTo({ left: scrollX, top: scrollY, behavior: 'instant' });
    }
    function refreshDraft() {
        const value = name => draft.elements[name].value.trim();
        const county = value('county').replace(/\s+county$/i, '');
        const state = draft.elements.state.selectedOptions[0];
        const location = [county ? county + ' County' : '[county]', value('state') ? state.textContent : '[state]'].join(', ');
        output.value = `Subject: Septic records request — ${value('address') || '[property address]'}\n\nHello,\n\nI am requesting septic records for the following property in ${location}.\n\nProperty address: ${value('address') || '[full property address]'}\nParcel ID: ${value('parcel') || '[add if known]'}\nOwner / applicant: ${value('owner') || '[add if known]'}\nRecord requested: ${value('record')}\nPurpose: ${value('reason')}\n\nPlease let me know which matching records are available and whether there are any fees or additional identifiers needed. If another office holds these records, please direct me to the appropriate records custodian. If no matching record is found, please confirm that in writing and describe the search scope.\n\nThank you,\n[your name and preferred contact information]`;
        fitMessage();
    }
    let messageWidth = 0;
    new ResizeObserver(entries => {
        const width = entries[0].contentRect.width;
        if (Math.abs(width - messageWidth) > 1) { messageWidth = width; fitMessage(); }
    }).observe(output);
    document.fonts.ready.then(fitMessage);
    draft.addEventListener('submit', event => event.preventDefault());
    draft.addEventListener('input', () => { refreshDraft(); status.textContent = 'Draft updated. Nothing has been sent.'; });
    draft.addEventListener('change', refreshDraft);
    draft.addEventListener('reset', () => setTimeout(() => { refreshDraft(); status.textContent = 'Draft cleared. Nothing has been sent.'; }, 0));
    document.querySelector('#copy-request').addEventListener('click', async () => {
        try { await navigator.clipboard.writeText(output.value); status.textContent = 'Request copied. Review the placeholders before sending.'; }
        catch { output.focus(); output.select(); status.textContent = 'Automatic copy is unavailable. The draft is selected for manual copying.'; }
    });
    document.querySelector('#download-request').addEventListener('click', () => {
        const url = URL.createObjectURL(new Blob([output.value], { type: 'text/plain;charset=utf-8' }));
        const link = document.createElement('a'); link.href = url; link.download = 'septic-records-request.txt';
        document.body.append(link); link.click(); link.remove(); setTimeout(() => URL.revokeObjectURL(url), 1000);
        status.textContent = 'Download prepared. Review the placeholders and official submission channel.';
    });
    refreshDraft();
})();
