(() => {
  const search = document.querySelector('#county-search');
  if (!search) return;
  const state = document.querySelector('[data-directory-state]');
  const rows = [...document.querySelectorAll('[data-county-name]')];
  const more = document.querySelector('[data-directory-more]');
  const find = document.querySelector('[data-directory-find]');
  const status = document.querySelector('[data-directory-status]');
  let limit = 6;
  const normalize = value => value.toLowerCase().replace(/[^a-z0-9]+/g, ' ').trim();
  const stateCodes = new Set([...state.options].map(option=>option.value.toLowerCase()).filter(Boolean));
  function render(reset = false) {
    if (reset) limit = 6;
    const words = normalize(search.value).split(' ').filter(Boolean);
    let count = 0, shown = 0;
    for (const row of rows) {
      const haystack = normalize(row.dataset.countyName + ' ' + row.dataset.countyState + ' ' + row.dataset.countyCode);
      const match = (!state.value || state.value === row.dataset.countyCode) && words.every(word =>
        stateCodes.has(word) ? row.dataset.countyCode.toLowerCase() === word : haystack.includes(word));
      if (match) count++;
      row.hidden = !match || count > limit;
      if (!row.hidden) shown++;
    }
    status.textContent = `${shown} of ${count} matching county guides. A guide does not guarantee a property record exists.`;
    document.querySelector('[data-directory-empty]').hidden = count > 0;
    more.hidden = count <= limit;
    more.textContent = 'Show more counties ↓';
  }
  find.hidden = false;
  function jump() { render(); document.querySelector('#county-directory').scrollIntoView({behavior:matchMedia('(prefers-reduced-motion: reduce)').matches?'instant':'smooth'}); }
  find.addEventListener('click', jump);
  search.addEventListener('keydown', event => {if(event.key === 'Enter'){event.preventDefault();jump();}});
  search.addEventListener('input', () => render(true));
  state.addEventListener('change', () => render(true));
  more.addEventListener('click', () => {limit += 24;render();});
  window.addEventListener('pageshow', () => render());
  render();
})();
