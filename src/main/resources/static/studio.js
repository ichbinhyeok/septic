(() => {
  const search = document.querySelector('[data-studio-county-search]');
  if (!search) return;
  const rows = [...document.querySelectorAll('[data-studio-county]')];
  const status = document.querySelector('[data-studio-county-status]');
  const toggle = document.querySelector('[data-studio-county-toggle]');
  const initialLimit = toggle ? 8 : rows.length;
  let expanded = !toggle;
  const render = () => {
    const query = search.value.trim().toLocaleLowerCase();
    let visible = 0;
    for (const [index, row] of rows.entries()) {
      const matches = row.dataset.studioCounty.toLocaleLowerCase().includes(query);
      row.hidden = !matches || (!query && !expanded && index >= initialLimit);
      if (!row.hidden) visible++;
    }
    status.textContent = query && visible
      ? `${visible} county ${visible === 1 ? 'route' : 'routes'} shown. A listed route does not guarantee a property record exists.`
      : visible
        ? `${expanded ? 'Showing all' : `Showing ${visible} of`} ${rows.length} published county routes. A listed route does not guarantee a property record exists.`
      : 'No matching published county route. Start with the address or ask us to investigate.';
    if (toggle) toggle.hidden = Boolean(query);
  };
  search.addEventListener('input', render);
  toggle?.addEventListener('click', () => {
    expanded = !expanded;
    toggle.setAttribute('aria-expanded', String(expanded));
    toggle.innerHTML = expanded
      ? 'Show fewer county routes <span aria-hidden="true">↑</span>'
      : `Show every county route <span aria-hidden="true">↓</span>`;
    render();
  });
  render();
})();
