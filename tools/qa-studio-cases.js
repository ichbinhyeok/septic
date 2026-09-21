// Read-only browser verification for an individual studio case.
window.caseQa = (async () => {
  const passed = [];
  const check = (condition, name) => { if (!condition) throw Error(name); passed.push(name); };
  check(document.querySelectorAll('h1').length === 1, 'One heading');
  check(document.documentElement.scrollWidth <= innerWidth, 'No overflow');
  check(document.querySelectorAll('.case-facts>div').length >= 4, 'Source-specific findings');
  check(document.querySelectorAll('.case-story li').length === 2, 'Two focused research decisions');
  check(document.querySelector('.case-delivery').textContent.length > 150, 'Customer closeout visible');
  check(document.querySelectorAll('.case-fact-lead').length === 2, 'Two primary source findings');
  check(document.querySelector('.case-story').compareDocumentPosition(document.querySelector('.case-evidence')) & Node.DOCUMENT_POSITION_FOLLOWING, 'Judgment before specifications');
  check(document.querySelector('.case-limit').textContent.length > 150, 'Limitations visible');
  check(document.querySelectorAll('.case-related a').length === 8, 'Eight related cases');
  const slug = location.pathname.split('/').filter(Boolean).at(-1);
  if (slug === 'morgan') {
    check(document.querySelector('.case-story').textContent.includes('public GIS'), 'Online search decisions visible');
    check(document.querySelector('.case-story').textContent.includes('did not average'), 'Conflicting entries kept distinct');
    check(document.querySelector('.case-hero img').src.endsWith('morgan-garden-v1.png'), 'Dedicated garden photograph');
  }
  check(document.querySelectorAll('.case-source').length === (['roane','overton'].includes(slug) ? 1 : 0), 'No substituted evidence');
  const urls = [...new Set([...document.querySelectorAll('main a[href],main img[src],main source[srcset]')].map(el => el.href || el.src || el.srcset))];
  const results = await Promise.all(urls.filter(url => !url.startsWith('#')).map(async url => [url, (await fetch(url)).status]));
  check(results.every(([,status]) => status === 200), 'All '+results.length+' destinations return 200');
  return JSON.stringify({slug,width:innerWidth,passed:passed.length,checks:passed});
})();
