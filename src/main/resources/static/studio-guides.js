(() => {
  const search = document.querySelector('[data-state-search]');
  if (!search) return;
  const rows = [...document.querySelectorAll('[data-guide-state]')];
  const expand = document.querySelector('[data-state-expand]');
  const status = document.querySelector('[data-state-status]');
  const empty = document.querySelector('[data-state-empty]');
  let expanded = false;
  function render() {
    const query = search.value.trim().toLowerCase();
    let count = 0;
    rows.forEach((row, index) => {
      const matches = row.dataset.guideState.toLowerCase().includes(query) || row.dataset.stateCode.toLowerCase() === query;
      row.hidden = query ? !matches : !expanded && index >= 12;
      if (!row.hidden) count++;
    });
    empty.hidden = count > 0;
    expand.hidden = Boolean(query) || rows.length <= 12;
    expand.setAttribute('aria-expanded', String(expanded));
    expand.textContent = expanded ? 'Show fewer states ↑' : `View all ${rows.length} states ↓`;
    status.textContent = `${count} of ${rows.length} states shown. A published guide does not guarantee a property record exists.`;
  }
  expand.addEventListener('click', () => { expanded = !expanded; render(); });
  search.addEventListener('input', render);
  window.addEventListener('pageshow', render);
  render();
})();
