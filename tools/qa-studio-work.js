// GET-only checks. Does not submit intake or contact anyone.
window.workQa = (async () => {
  const checks = [];
  const check = (ok, label) => { if (!ok) throw Error(label); checks.push(label); };
  check(document.querySelectorAll('h1').length === 1, 'One H1');
  check(document.documentElement.scrollWidth <= innerWidth, 'No horizontal overflow');
  const source = document.querySelector('.work-source-gallery a');
  check(source && !source.closest('details') && source.target === '_blank', 'Original evidence visible with full-size link');
  check(source.querySelector('img').getAttribute('src') === '/images/case-study/redacted/shelby-site-plan-detail-public.png', 'Unmodified public plan asset');
  check(document.querySelectorAll('.work-source-gallery img').length === 2, 'Detail view and complete public excerpt both retained');
  check(document.querySelector('.work-hero source').srcset.includes('work-archive-mobile-v3.png'), 'Dedicated mobile composition');
  check(document.querySelectorAll('.work-limits>div').length === 2, 'Findings and limitations separated');
  check(document.querySelectorAll('.work-stories li').length === 8, 'Eight related cases retained');
  const paths = [...new Set([...document.querySelectorAll('main a[href], main img[src]')].map(el => new URL(el.href || el.src)).filter(url => url.origin === location.origin).map(url => url.pathname))];
  const results = await Promise.all(paths.map(async path => [path, (await fetch(path)).status]));
  check(results.every(([,status]) => status === 200), 'All '+paths.length+' linked pages and images return 200');
  check(document.querySelector('meta[name=robots]').content === 'noindex,nofollow', 'Preview remains noindex');
  return JSON.stringify({passed:checks.length,checks});
})();
