// Read-only UI verification; does not submit forms or contact external offices.
(() => {
  const checks = [];
  const check = (name, pass) => checks.push({name, pass:Boolean(pass)});
  check('one heading', document.querySelectorAll('h1').length === 1);
  check('no overflow', document.documentElement.scrollWidth <= innerWidth);
  check('all sidebar anchors resolve', [...document.querySelectorAll('.article-sidebar nav a')].every(a => document.querySelector(a.hash)));
  check('case collection and intake links', document.querySelector('main a[href="/design-preview/studio/work/"]') && document.querySelector('main a[href="/design-preview/studio/intake/"]'));
  const toggle = document.querySelector('.article-toc-toggle');
  if (innerWidth <= 700) {
    toggle.click(); check('mobile contents opens', !document.querySelector('#article-toc').hidden);
    toggle.click(); check('mobile contents closes', document.querySelector('#article-toc').hidden);
  }
  const faq = document.querySelector('#topic-questions details');
  if (faq) { faq.querySelector('summary').click(); check('FAQ opens', faq.open); faq.querySelector('summary').click(); }
  check('loaded hero', document.querySelector('.article-hero img').naturalWidth > 0);
  check('preview remains noindex', document.querySelector('meta[name=robots]').content.includes('noindex'));
  const evidence = document.querySelector('#drawing-visible-evidence');
  if (evidence) {
    check('investigation precedes reference article', Boolean(evidence.compareDocumentPosition(document.querySelector('.article-layout')) & Node.DOCUMENT_POSITION_FOLLOWING));
    check('core evidence is not collapsed', !evidence.closest('details'));
    check('findings precede original previews', Boolean(evidence.querySelector('.drawing-record-facts').compareDocumentPosition(evidence.querySelector('.drawing-source-library')) & Node.DOCUMENT_POSITION_FOLLOWING));
    check('original previews stay compact', [...evidence.querySelectorAll('.drawing-source-thumb')].length === 3 && [...evidence.querySelectorAll('.drawing-source-thumb')].every(el => el.getBoundingClientRect().height <= 130));
    check('three Shelby sources remain available', ['approved-site-plan-full-public.png','agency-approval-letter-full-public.png','shelby-brief-findings-public.png'].every(file => evidence.querySelector(`a[href="/images/case-study/redacted/${file}"]`)));
    check('historical facts and limits are distinct', document.querySelectorAll('.drawing-record-facts dt').length === 3 && document.querySelector('.drawing-investigation-boundary'));
  }
  return {checks, failures:checks.filter(c=>!c.pass)};
})();
