(() => {
  'use strict';
  const form = document.querySelector('[data-intake-form]');
  document.querySelector('[data-intake-success]')?.focus({preventScroll: true});
  if (!form) return;
  const files = form.elements.documents;
  const goal = form.elements.researchGoal;
  const production = form.dataset.production === 'true';
  const researchSource = form.elements.sourceContext.value
    || (form.elements.sourcePageHint.value === '/design-preview/studio/calculator/' ? 'studio_calculator' : 'studio_intake');
  const uploads = form.querySelector('[data-intake-uploads]');
  const status = document.getElementById('upload-status');
  let lastResearchGoal = goal.value === 'understand_file' ? 'other' : goal.value;
  function validateFiles() {
    const list = Array.from(files.files);
    let error = '';
    if (goal.value === 'understand_file' && !list.length) error = 'Add at least one source file for review.';
    else if (list.length > 3) error = 'Choose no more than 3 files.';
    else if (list.some(file => file.size > 10 * 1024 * 1024)) error = 'Each file must be 10 MB or smaller.';
    else if (list.reduce((total, file) => total + file.size, 0) > 15 * 1024 * 1024) error = 'Combined files must be 15 MB or smaller.';
    else if (list.some(file => !/\.(pdf|txt|png|jpe?g)$/i.test(file.name))) error = 'Use PDF, TXT, PNG or JPG files only.';
    files.setCustomValidity(error);
    status.textContent = error || list.map(file => `${file.name} (${(file.size / 1024 / 1024).toFixed(1)} MB)`).join(' · ');
    status.toggleAttribute('data-error', Boolean(error));
    return !error;
  }
  function syncMode() {
    const review = goal.value === 'understand_file';
    form.elements.intakeMode.value = review ? 'review' : 'research';
    form.action = (production ? '/offer-prep-septic-file-check/' : '/design-preview/studio/intake/') + (review ? '?mode=review' : '');
    form.elements.sourceContext.value = review ? 'document_review' : researchSource;
    form.querySelector('[data-upload-label]').textContent = review ? 'Your source files (required for review)' : 'Attach files (optional)';
    form.querySelector('[data-submit-copy]').textContent = review ? 'Send my file for review' : (production ? 'Start my record investigation' : 'Start my free research');
    if (review) uploads.open = true;
    validateFiles();
  }
  form.querySelectorAll('[name=intakeMode]').forEach(radio => radio.addEventListener('change', () => {
    if (radio.value === 'review') {
      lastResearchGoal = goal.value;
      goal.value = 'understand_file';
      if (form.elements.recordStatus.value === 'not_started') form.elements.recordStatus.value = 'partial';
    } else goal.value = lastResearchGoal;
    syncMode();
  }));
  goal.addEventListener('change', syncMode);
  files.addEventListener('change', validateFiles);
  form.addEventListener('invalid', event => {
    const disclosure = event.target.closest('details');
    if (disclosure) disclosure.open = true;
  }, true);
  form.querySelectorAll('[data-error-field]').forEach(link => {
    const field = document.getElementById('field-' + link.dataset.errorField);
    if (!field) return;
    field.setAttribute('aria-invalid', 'true');
    link.id = 'error-' + link.dataset.errorField;
    field.setAttribute('aria-describedby', link.id);
    if (field.closest('details')) field.closest('details').open = true;
    link.addEventListener('click', event => { event.preventDefault(); field.focus(); });
    field.addEventListener('input', () => field.removeAttribute('aria-invalid'), {once: true});
  });
  form.addEventListener('submit', event => {
    if (!validateFiles()) { event.preventDefault(); uploads.open = true; files.reportValidity(); return; }
    form.querySelector('[type=submit]').disabled = true;
    form.querySelector('[data-submit-copy]').textContent = 'Sending your request…';
    form.setAttribute('aria-busy', 'true');
  });
  window.addEventListener('pageshow', () => {
    form.querySelector('[type=submit]').disabled = false;
    form.removeAttribute('aria-busy');
    syncMode();
  });
  syncMode();
  document.querySelector('[data-intake-errors]')?.focus();
})();
