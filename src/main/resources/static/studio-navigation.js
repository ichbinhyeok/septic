(() => {
  const menu = document.querySelector('.studio-menu');
  if (!menu) return;
  const summary = menu.querySelector('summary');
  document.addEventListener('keydown', event => {
    if (event.key === 'Escape' && menu.open) {
      menu.open = false;
      summary.focus();
    }
  });
  document.addEventListener('click', event => {
    if (menu.open && !menu.contains(event.target)) menu.open = false;
  });
  menu.querySelectorAll('a').forEach(link => link.addEventListener('click', () => { menu.open = false; }));
  menu.addEventListener('focusout', () => {
    requestAnimationFrame(() => {
      if (menu.open && !menu.contains(document.activeElement)) menu.open = false;
    });
  });
  matchMedia('(min-width: 701px)').addEventListener('change', event => {
    if (event.matches) menu.open = false;
  });
})();
