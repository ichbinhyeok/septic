// Run with gstack browse eval after navigating to the calculator. Read-only assertions.
(() => {
    const checks = [];
    const check = (name, ok) => checks.push({name, pass: Boolean(ok)});
    check('one heading', document.querySelectorAll('h1').length === 1);
    check('noindex preview', document.querySelector('meta[name="robots"]').content.includes('noindex'));
    check('no horizontal overflow', document.documentElement.scrollWidth <= innerWidth);
    check('studio form action', document.querySelector('#cost-estimator-form').action.includes('/design-preview/studio/calculator/'));
    check('seven project choices', document.querySelector('#cost-estimator-form select[name="projectType"]').options.length === 7);
    check('all input contracts', ['stateCode','projectType','bedrooms','occupants','soilPercStatus','accessDifficulty','timeline','garbageDisposal','additionalKitchen','highWaterTableOrShallowBedrock','sourcePageHint','recordsMode','recordSystemType','recordTankCapacity','recordDesignFlow'].every(name => document.querySelector(`#cost-estimator-form [name="${name}"]`)));
    check('all fragment links resolve', [...document.querySelectorAll('a[href^="#"]')].every(a => document.querySelector(a.hash)));
    check('native menu is not given a second plus', getComputedStyle(document.querySelector('.studio-menu summary'), '::after').content === 'none');
    check('no legacy stylesheet', [...document.querySelectorAll('link[rel="stylesheet"]')].every(l => !/\/(app|pages|tool)\.css/.test(l.href)));
    const quote = document.querySelector('#quote-request-form');
    if (quote) check('studio quote action', quote.action.includes('/design-preview/studio/calculator/quote/'));
    const research = document.querySelector('#records-investigation');
    if (research) {
        check('explanation precedes case', Boolean(document.querySelector('#understand-estimate').compareDocumentPosition(research) & Node.DOCUMENT_POSITION_FOLLOWING));
        check('calculation evidence remains available', document.querySelector('#calculation-evidence') instanceof HTMLDetailsElement);
        check('case and archive links', research.querySelector('a[href="/design-preview/studio/work/roane/"]') && research.querySelector('a[href="/design-preview/studio/work/"]'));
        check('records inquiry stays separate', document.querySelector('.calc-records-cta a[href^="/design-preview/studio/intake/?from=calculator"]') && document.querySelector('#quote-request') instanceof HTMLDetailsElement);
    }
    return {checks, failures:checks.filter(c=>!c.pass)};
})();
