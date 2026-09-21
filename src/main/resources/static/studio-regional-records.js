(() => {
    const root = document.querySelector('[data-regional-records]');
    if (!(root instanceof HTMLElement)) return;
    const form = root.querySelector('[data-regional-form]');
    const county = root.querySelector('[data-regional-county]');
    const clueType = root.querySelector('[data-regional-clue-type]');
    const clue = root.querySelector('[data-regional-clue]');
    const clueLabel = root.querySelector('[data-regional-clue-label]');
    const secondary = root.querySelector('[data-regional-secondary]');
    const year = root.querySelector('[data-regional-year]');
    const result = root.querySelector('[data-regional-result]');
    const error = root.querySelector('[data-regional-error]');
    const labels = { address: 'Street address', parcel: root.dataset.regionCode === 'SC' ? 'TMS / tax map number' : 'Parcel or property ID', owner: 'Current or prior owner', permit: 'Permit number' };
    const officialFallback = { TX: 'https://www6.tceq.texas.gov/oars/index.cfm?fuseaction=search.county', SC: 'https://epermitting.des.sc.gov/ext/nsite/default/map/help', FL: 'https://ostds.floridadep.gov/' };
    const officeFallback = { TX: 'TCEQ authority search, local authorized agent, or regional office', SC: 'SCDES Site Explorer or Onsite Wastewater', FL: 'Florida DEP or county Environmental Public Health' };
    if (!(form instanceof HTMLFormElement) || !(county instanceof HTMLSelectElement) || !(clueType instanceof HTMLSelectElement) || !(clue instanceof HTMLInputElement) || !(result instanceof HTMLElement)) return;

    function syncLabel() {
        const label = labels[clueType.value] || labels.address;
        if (clueLabel) clueLabel.textContent = label;
        clue.placeholder = clueType.value === 'address' ? '123 Main Street' : `Enter ${label.toLowerCase()}`;
        clue.autocomplete = clueType.value === 'address' ? 'street-address' : 'off';
        result.hidden = true;
    }
    clueType.addEventListener('change', syncLabel);
    form.addEventListener('input', () => { result.hidden = true; });
    form.addEventListener('change', () => { result.hidden = true; });
    syncLabel();

    form.addEventListener('submit', event => {
        event.preventDefault();
        if (!form.reportValidity()) return;
        const option = county.options[county.selectedIndex];
        const countyName = option.dataset.countyName || option.textContent.trim();
        const countyLabel = /\bCounty$/i.test(countyName) ? countyName : `${countyName} County`;
        const regionCode = root.dataset.regionCode || '';
        const regionName = root.dataset.regionName || '';
        const programName = root.dataset.programName || '';
        const entered = clue.value.trim();
        const second = secondary instanceof HTMLInputElement ? secondary.value.trim() : '';
        const installYear = year instanceof HTMLInputElement ? year.value.trim() : '';
        const officialUrl = option.dataset.officialUrl || officialFallback[regionCode] || '';
        const guidePath = option.dataset.guidePath || '/design-preview/studio/counties/';
        const office = option.dataset.routeOwner || option.dataset.officeLabel || officeFallback[regionCode];
        const title = root.querySelector('[data-regional-result-title]');
        const copy = root.querySelector('[data-regional-result-copy]');
        const keys = root.querySelector('[data-regional-keys]');
        const officeCopy = root.querySelector('[data-regional-office]');
        const official = root.querySelector('[data-regional-official]');
        const guide = root.querySelector('[data-regional-guide]');
        if (title) title.textContent = `${countyLabel} ${programName} route`;
        if (copy) copy.textContent = `Start with ${office}. Carry the ${labels[clueType.value].toLowerCase()} and keep the county route open if the first search is blank.`;
        const variants = [`${labels[clueType.value]}: ${entered}`, `${countyLabel}, ${regionName}`, second ? `Secondary clue: ${second}` : 'Add parcel, prior owner, subdivision, lot, or permit number when available', installYear ? `Approximate install year: ${installYear}` : 'Add an approximate install year when available'];
        if (keys) keys.replaceChildren(...variants.map(text => { const li = document.createElement('li'); li.textContent = text; return li; }));
        if (officeCopy) officeCopy.textContent = `${office}. The authority or portal identifies the route; it does not by itself prove that the parcel file was found.`;
        if (official instanceof HTMLAnchorElement) official.href = officialUrl;
        if (guide instanceof HTMLAnchorElement) guide.href = guidePath;
        const request = root.querySelector('[data-regional-request]');
        if (request instanceof HTMLAnchorElement) request.addEventListener('click', () => {
            const context = { savedAt: Date.now(), taskMode: true, stateCode: regionCode, stateName: regionName, countyName: countyLabel, matchedAddress: clueType.value === 'address' ? entered : '', officeLabel: office, contactLine: `Prepared from the ${countyLabel} ${programName} route.`, requestedRecord: 'permit_copy' };
            try { sessionStorage.setItem('septic-records-request-context', JSON.stringify(context)); } catch (_) {}
        }, { once: true });
        if (error) error.hidden = true;
        result.hidden = false;
        title?.focus({ preventScroll: true });
        result.scrollIntoView({ behavior: matchMedia('(prefers-reduced-motion: reduce)').matches ? 'instant' : 'smooth', block: 'start' });
    });

    const copyButton = root.querySelector('[data-regional-copy]');
    copyButton?.addEventListener('click', async () => {
        const values = Array.from(root.querySelectorAll('[data-regional-keys] li')).map(item => item.textContent).join('\n');
        const original = copyButton.textContent;
        try { await navigator.clipboard.writeText(values); copyButton.textContent = 'Search clues copied'; }
        catch (_) { copyButton.textContent = 'Copy failed'; }
        setTimeout(() => copyButton.textContent = original, 1600);
    });
})();
