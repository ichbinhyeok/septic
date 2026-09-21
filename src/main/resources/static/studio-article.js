/* Reading navigation only. No storage, analytics or form submission. */
(() => {
    const links = [...document.querySelectorAll('.article-sidebar nav a')];
    const sections = links.map(link => document.querySelector(link.hash));
    const toc = document.querySelector('#article-toc');
    const tocToggle = document.querySelector('.article-toc-toggle');
    const mobile = matchMedia('(max-width:700px)');
    function syncToc() {
        tocToggle.hidden = !mobile.matches;
        toc.hidden = mobile.matches;
        tocToggle.setAttribute('aria-expanded', String(!mobile.matches));
    }
    tocToggle.addEventListener('click', () => {
        toc.hidden = !toc.hidden;
        tocToggle.setAttribute('aria-expanded', String(!toc.hidden));
    });
    // Leave the menu open after anchor selection so collapsing cannot shift its destination.
    mobile.addEventListener('change', syncToc);
    syncToc();
    const states = [...document.querySelectorAll('#article-states li')];
    const statesToggle = document.querySelector('.article-states-toggle');
    let expanded = false;
    function syncStates() {
        states.forEach((item, index) => { item.hidden = !expanded && index >= 6; });
        if (!statesToggle) return;
        statesToggle.hidden = states.length <= 6;
        statesToggle.setAttribute('aria-expanded', String(expanded));
        statesToggle.textContent = expanded ? 'Show fewer state guides ↑' : `Show all ${states.length} state guides ↓`;
    }
    statesToggle?.addEventListener('click', () => { expanded = !expanded; syncStates(); });
    syncStates();
    let queued = false;
    function update() {
        queued = false;
        let active = 0;
        sections.forEach((section, index) => {
            if (section && section.getBoundingClientRect().top <= 150) active = index;
        });
        links.forEach((link, index) => {
            if (index === active) link.setAttribute('aria-current', 'location');
            else link.removeAttribute('aria-current');
        });
    }
    addEventListener('scroll', () => {
        if (!queued) { queued = true; requestAnimationFrame(update); }
    }, { passive: true });
    addEventListener('resize', update);
    update();
})();
