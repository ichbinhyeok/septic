// Read-only journey checks: never submits a request or uploads a customer file.
(() => {
  const checks = [];
  const check = (name, pass) => checks.push({name, pass: Boolean(pass)});
  check('no horizontal overflow', document.documentElement.scrollWidth <= innerWidth);
  check('one main heading', document.querySelectorAll('h1').length === 1);
  check('preview noindex', document.querySelector('meta[name=robots]').content.includes('noindex'));
  if (document.querySelector('.county-hero')) {
    const proof = document.querySelector('#our-research');
    check('case before detailed route', Boolean(proof.compareDocumentPosition(document.querySelector('#official-route')) & Node.DOCUMENT_POSITION_FOLLOWING));
    check('no repeated county introduction', !proof.querySelector('.research-proof-heading>p'));
    check('photo and button lead to same case', proof.querySelector('.research-proof-photo').href === proof.querySelector('.research-proof-cta').href);
    check('actual case geography visible', proof.querySelector('.studio-fine').textContent.includes('This case is from'));
  }
  if (document.querySelector('.case-editorial')) {
    check('early inquiry shortcut resolves', document.querySelector(document.querySelector('.case-hero-inquiry').hash));
    check('case context in intake link', document.querySelector('.case-next .studio-button').search.includes('from=case'));
  }
  const form = document.querySelector('[data-intake-form]');
  if (form) {
    const source = form.elements.sourcePageHint.value;
    check('case reference retained', source.startsWith('/design-preview/studio/work/'));
    check('no copied customer facts', !form.elements.propertyAddress.value && !form.elements.concern.value);
    check('compact handoff', document.querySelector('.intake-handoff').offsetHeight < 170);
    form.querySelector('[name=intakeMode][value=review]').click();
    check('review reveals required file input', document.querySelector('[data-intake-uploads]').open && !form.elements.documents.checkValidity());
    form.querySelector('[name=intakeMode][value=research]').click();
    check('research does not require file', form.elements.documents.checkValidity());
    check('mode switch preserves case reference', source === form.elements.sourcePageHint.value);
    check('empty address blocked locally', !form.elements.propertyAddress.checkValidity());
  }
  return {checks, failures: checks.filter(c => !c.pass)};
})();
