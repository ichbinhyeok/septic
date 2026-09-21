(() => {
    'use strict';
    const form = document.querySelector('[data-tdec-studio-form]');
    const county = document.querySelector('[data-tdec-studio-county]');
    const result = document.querySelector('[data-tdec-studio-result]');
    if (!form || !county || !result) return;
    form.addEventListener('submit', event => {
        event.preventDefault();
        const option = county.selectedOptions[0];
        if (!option?.value) { county.focus(); return; }
        const name = option.dataset.countyName;
        const contract = option.dataset.contractCounty === 'true';
        const office = contract ? `${name} program` : option.dataset.fieldOfficeName;
        result.querySelector('[data-tdec-studio-title]').textContent = contract ? `${name} keeps its own route.` : `${office} is the responsible field office.`;
        result.querySelector('[data-tdec-studio-copy]').textContent = option.dataset.recordsHint || 'Confirm the property identifiers and request the matching septic file.';
        result.querySelector('[data-tdec-studio-owner]').textContent = office;
        result.querySelector('[data-tdec-studio-keys]').textContent = 'Address, parcel or tax-map ID, current or prior owner, subdivision, lot, legal description, and permit number when known.';
        const actions = result.querySelector('[data-tdec-studio-actions]');
        actions.replaceChildren();
        const primary = document.createElement('a'); primary.className = 'studio-button'; primary.href = option.dataset.recordsUrl || option.dataset.fieldOfficeUrl; primary.target = '_blank'; primary.rel = 'noopener noreferrer'; primary.textContent = option.dataset.recordsLabel || 'Open official route ↗'; actions.append(primary);
        if (option.dataset.internalPath) { const local = document.createElement('a'); local.href = option.dataset.internalPath.replace('/septic-records-checklist/tennessee/', '/design-preview/studio/tennessee/'); local.textContent = 'Open the county guide →'; actions.append(local); }
        const request = document.createElement('a'); request.href = '/design-preview/studio/tools/#draft-request'; request.textContent = option.dataset.requestEmail ? `Prepare a request for ${option.dataset.requestEmail} →` : 'Prepare the request →'; actions.append(request);
        result.hidden = false; result.focus({preventScroll:true}); result.scrollIntoView({behavior:matchMedia('(prefers-reduced-motion: reduce)').matches?'instant':'smooth',block:'nearest'});
    });
})();
