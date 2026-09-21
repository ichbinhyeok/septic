/* Only presentation: calculations and validation stay on the server. */
(() => {
    const editor = document.querySelector('.calc-editor');
    const service = document.querySelector('#quote-request');
    function openService() { if (service) service.open = true; }
    document.querySelectorAll('[data-open-service]').forEach(link => link.addEventListener('click', openService));
    if (location.hash === '#quote-request') openService();
    addEventListener('hashchange', () => { if (location.hash === '#quote-request') openService(); });
    document.querySelectorAll('[data-edit-property]').forEach(link => {
        link.addEventListener('click', () => {
            editor.open = true;
            requestAnimationFrame(() => document.querySelector('#cost-estimator-form select').focus({preventScroll:true}));
        });
    });

    const workspace = document.querySelector('[data-calculator-mode]');
    const projectType = document.querySelector('#cost-estimator-form select[name="projectType"]');
    if (workspace && projectType) {
        const landscape = workspace.querySelector('.calc-landscape');
        const title = workspace.querySelector('[data-calc-mode-title]');
        const kicker = workspace.querySelector('[data-calc-mode-kicker]');
        const intro = workspace.querySelector('[data-calc-mode-intro]');
        const details = workspace.querySelector('[data-calc-mode-details]');
        const water = workspace.querySelector('[data-calc-mode-water]');
        const applyMode = () => {
            const fieldMode = projectType.value === 'drainfield_replacement';
            workspace.dataset.calculatorMode = fieldMode ? 'drainfield' : 'general';
            workspace.classList.toggle('calc-workspace--field', fieldMode);
            if (landscape) landscape.src = fieldMode ? '/images/studio/oak-pasture-v1.webp' : '/images/studio/calculator-landscape-v2.webp';
            if (kicker) kicker.textContent = fieldMode ? 'Field recovery / Scope estimator' : 'Property planning / Cost estimator';
            if (intro) intro.textContent = fieldMode
                ? 'Map soil, saturation, access and the replacement area before treating trench work as the whole job.'
                : 'Start with what you know about the property.';
            if (details) details.textContent = fieldMode ? 'Define the field constraints' : 'Add soil & site details';
            if (water) water.textContent = fieldMode
                ? 'Wet spots, surfacing, odor, or high water suspected'
                : 'High water table or shallow bedrock suspected';
            if (title) title.innerHTML = fieldMode
                ? (title.tagName === 'H1' ? 'Read the field.<br>Price the real path.' : 'Refine the field.<br>Protect the low end.')
                : (title.tagName === 'H1' ? 'Plan the project.<br>Understand the range.' : 'Refine your<br>planning estimate.');
        };
        projectType.addEventListener('change', applyMode);
        applyMode();
    }
})();
