// Local browser-only fixtures. No real customer information or external calls.
window.bedroomQa = (async () => {
    const $ = key => document.querySelector(`[data-bedroom-${key}]`);
    const passed = []; const assert = (ok, text) => { if (!ok) throw Error(text); passed.push(text); };
    const before = JSON.stringify({...localStorage});
    const compare = (listing, permit, status) => {
        $('state').value = 'TN'; $('listing-count').value = listing;
        $('permit-count').value = permit; $('file-status').value = status;
        $('permit-form').dispatchEvent(new Event('input', {bubbles:true}));
        $('permit-form').requestSubmit();
    };
    for (const [a,b,status,expected] of [
        ['4','3','official','mismatch'],['3','3','official','aligned'],
        ['2','3','official','under_listed'],['3','','official','unverified'],
        ['3','3','not_checked','unverified'],['3','3','missing','unverified'],
        ['3','3','conflicting','conflict']
    ]) {
        compare(a,b,status);
        assert(!$('result').hidden && $('result').dataset.bedroomCheckKind === expected, `${status} ${a}/${b}: ${expected}`);
        const numbers = [...$('result-facts').querySelectorAll('dd')].map(el => el.textContent);
        const comparable = ['mismatch', 'aligned', 'under_listed'].includes(expected);
        assert(numbers.length === 3 && numbers[0] === a, `${expected}: three-part comparison`);
        assert(numbers[2] === (comparable ? ((Number(a)>Number(b)?'+':'')+(Number(a)-Number(b))) : '—'), `${expected}: trustworthy difference`);
        assert($('result-facts').compareDocumentPosition($('result-heading')) & Node.DOCUMENT_POSITION_FOLLOWING, 'Numbers precede explanation');
    }
    for (const invalid of ['', '0', '13', '2.5']) {
        compare(invalid,'3','official'); assert($('result').hidden, `Invalid listing ${invalid || 'empty'} rejected`);
    }
    compare('3','3','official');
    assert($('result-note').value.includes('not an engineering'), 'Note retains decision limits');
    $('listing-count').dispatchEvent(new Event('input',{bubbles:true}));
    assert($('result').hidden, 'Changing inputs clears stale result');
    assert(JSON.stringify({...localStorage}) === before, 'No persistent storage');
    assert(document.documentElement.scrollWidth <= innerWidth, 'No horizontal overflow');
    return JSON.stringify({passed:passed.length,checks:passed});
})();
