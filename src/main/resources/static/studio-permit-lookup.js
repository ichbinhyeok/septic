(() => {
    'use strict';
    const form = document.querySelector('[data-lookup-state-form]');
    const select = document.querySelector('#lookup-state');
    if (!form || !select) return;
    form.addEventListener('submit', event => {
        event.preventDefault();
        if (!select.value) { select.focus(); return; }
        window.location.assign(select.value);
    });
})();
