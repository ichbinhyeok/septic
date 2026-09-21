// Read-only browser checks for the buyer article preview.
window.articleQa = (async () => {
    const passed = [];
    const check = (ok, name) => { if (!ok) throw Error(name); passed.push(name); };
    const links = [...document.querySelectorAll('.article-sidebar nav a')];
    const toc = document.querySelector('#article-toc');
    const tocToggle = document.querySelector('.article-toc-toggle');
    const mobile = matchMedia('(max-width:700px)').matches;
    check(toc.hidden === mobile && tocToggle.hidden === !mobile, 'Responsive TOC initial state');
    if (mobile) {
        tocToggle.click();
        check(!toc.hidden && tocToggle.getAttribute('aria-expanded') === 'true', 'Mobile TOC expands');
        tocToggle.click();
        check(toc.hidden && tocToggle.getAttribute('aria-expanded') === 'false', 'Mobile TOC collapses');
    }
    const states = [...document.querySelectorAll('#article-states li')];
    const statesToggle = document.querySelector('.article-states-toggle');
    check(states.length === 50 && states.filter(item => !item.hidden).length === 6, 'All 50 states retained, six initially visible');
    statesToggle.click();
    check(states.every(item => !item.hidden) && statesToggle.getAttribute('aria-expanded') === 'true', 'All states expand');
    statesToggle.click();
    check(states.filter(item => !item.hidden).length === 6 && statesToggle.getAttribute('aria-expanded') === 'false', 'States collapse');
    check(links.length === 6, 'Six article anchors');
    check(links.every(link => document.querySelector(link.hash)), 'Every anchor resolves');
    document.querySelector('#buyer-checklist').scrollIntoView({behavior:'instant'});
    await new Promise(resolve => setTimeout(resolve, 120));
    check(document.querySelector('.article-sidebar [aria-current]')?.hash === '#buyer-checklist', 'Reading location follows scroll');
    const faq = document.querySelector('#buyer-questions details');
    faq.querySelector('summary').click();
    check(faq.open, 'FAQ expands');
    faq.querySelector('summary').click();
    check(!faq.open, 'FAQ collapses');
    check(document.querySelectorAll('h1').length === 1, 'Single page heading');
    check(document.documentElement.scrollWidth <= innerWidth, 'No horizontal overflow');
    check([...document.images].every(image => image.complete && image.naturalWidth), 'Images loaded');
    check([...document.querySelectorAll('.article-photo img')].every(image => image.clientHeight < image.clientWidth), 'Article photographs respect landscape crop');
    const paths = [...new Set([...document.querySelectorAll('main a[href]')].map(a => new URL(a.href)).filter(url => url.origin === location.origin).map(url => url.pathname))];
    const responses = await Promise.all(paths.map(async path => [path, (await fetch(path)).status]));
    const failed = responses.filter(([,status]) => status !== 200);
    check(!failed.length, 'All '+paths.length+' linked local pages return 200: '+JSON.stringify(failed));
    return JSON.stringify({passed:passed.length,checks:passed});
})();
