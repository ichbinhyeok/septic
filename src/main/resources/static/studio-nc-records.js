(() => {
  const form = document.querySelector('[data-nc-studio-form]');
  if (!form) return;
  const select = form.querySelector('[data-nc-studio-county]');
  const clue = form.querySelector('[data-nc-studio-clue]');
  const result = document.querySelector('[data-nc-studio-result]');
  const title = result.querySelector('[data-nc-studio-title]');
  const copy = result.querySelector('[data-nc-studio-copy]');
  const keys = result.querySelector('[data-nc-studio-keys]');
  const actions = result.querySelector('[data-nc-studio-actions]');

  const link = (href, label, primary = false, external = false) => {
    const anchor = document.createElement('a');
    anchor.href = href;
    anchor.textContent = label;
    if (primary) anchor.className = 'studio-button';
    if (external) {
      anchor.target = '_blank';
      anchor.rel = 'noreferrer';
    }
    return anchor;
  };

  form.addEventListener('submit', event => {
    event.preventDefault();
    const option = select.selectedOptions[0];
    if (!option || !option.value) {
      select.focus();
      return;
    }
    const countyName = option.dataset.name;
    const guide = option.dataset.internalPath.replace('/septic-records-checklist/north-carolina/', '/design-preview/studio/north-carolina/');
    title.textContent = `${countyName} route`;
    copy.textContent = `Use the county guide to confirm whether ${countyName} provides a portal, form, email, or phone workflow. Keep the official source open separately.`;
    keys.textContent = clue.value.trim() || 'Street address, parcel ID or PIN, current or prior owner, subdivision, lot, and permit number when known.';
    actions.replaceChildren(
      link(guide, 'Open the county guide', true),
      link(option.dataset.recordsUrl, option.dataset.recordsLabel || 'Open official county source ↗', false, true),
      link('/design-preview/studio/tools/#draft-request', 'Prepare a request →'),
      link('/design-preview/studio/intake/', 'Ask SepticPath to investigate →')
    );
    result.hidden = false;
    result.focus({ preventScroll: true });
    result.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  });
})();
