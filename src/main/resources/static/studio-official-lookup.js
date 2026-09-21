(() => {
  const form = document.querySelector('[data-official-lookup-form]');
  if (!form) return;

  const select = form.querySelector('[data-official-lookup-state]');
  const countyInput = form.querySelector('[data-official-lookup-county]');
  const result = document.querySelector('[data-official-lookup-result]');
  const title = result.querySelector('[data-official-lookup-title]');
  const copy = result.querySelector('[data-official-lookup-copy]');
  const actions = result.querySelector('[data-official-lookup-actions]');

  const priority = {
    tennessee: { label: 'Open the Tennessee route', href: '/design-preview/studio/tdec-records/', copy: 'Start with TDEC or the responsible contract county, then carry parcel, owner, subdivision, lot, and permit clues into the request.' },
    'north-carolina': { label: 'Open the North Carolina route', href: '/north-carolina-septic-permit-lookup/', copy: 'North Carolina files usually resolve through county environmental health rather than one statewide public lookup.' },
    texas: { label: 'Open the Texas OSSF route', href: '/texas-ossf-records-search/', copy: 'Use TCEQ for program context, then identify the county, authorized agent, or local permitting authority that owns the file.' },
    'south-carolina': { label: 'Open the South Carolina route', href: '/dhec-septic-permit-lookup/', copy: 'Use the current SCDES path, then preserve the county, parcel or TMS, address, owner, and D-1740 request clues.' },
    florida: { label: 'Open the Florida OSTDS route', href: '/florida-ostds-permit-lookup/', copy: 'Start with Florida’s onsite-sewage program, then follow the county health or local archive route for the property file.' }
  };

  const link = (href, label, primary = false) => {
    const anchor = document.createElement('a');
    anchor.href = href;
    anchor.className = primary ? 'studio-button' : '';
    anchor.textContent = label;
    return anchor;
  };

  form.addEventListener('submit', event => {
    event.preventDefault();
    const option = select.selectedOptions[0];
    if (!option || !option.value) {
      select.focus();
      return;
    }

    const state = option.dataset.name;
    const county = countyInput.value.trim();
    const special = priority[option.value];
    const published = option.dataset.published === 'true';
    const statePath = published ? `/design-preview/studio/${option.value}/` : '/design-preview/studio/counties/';

    title.textContent = county ? `${county} County, ${state}` : state;
    copy.textContent = special?.copy || `Start with the ${state} guide, then confirm which county or delegated office owns the permit, layout, approval, repair, or no-record response.`;
    actions.replaceChildren();
    actions.append(link(special?.href || statePath, special?.label || (published ? `Open the ${state} guide` : 'Browse county routes'), true));
    actions.append(link('/design-preview/studio/tools/#draft-request', 'Prepare a targeted request →'));
    actions.append(link('/design-preview/studio/intake/', 'Ask SepticPath to investigate →'));
    result.hidden = false;
    result.focus({ preventScroll: true });
    result.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  });
})();
