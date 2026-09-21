// Read-only: inventory studio pages and outgoing legacy destinations. No form submissions.
return await (async () => {
  const queue = ['/design-preview/studio/service/', '/design-preview/studio/guides/', '/design-preview/studio/buying-guide/'];
  const seen = new Set(queue), pages = [], legacy = new Map();
  for (let i = 0; i < queue.length;) {
    const batch = queue.slice(i, i + 8);
    i += batch.length;
    await Promise.all(batch.map(async path => {
      const response = await fetch(path);
      const doc = new DOMParser().parseFromString(await response.text(), 'text/html');
      pages.push({path, status: response.status, title: doc.title, h1: doc.querySelectorAll('h1').length,
        noindex: doc.querySelector('meta[name=robots]')?.content.includes('noindex'),
        family: doc.body.className});
      for (const a of doc.querySelectorAll('a[href]')) {
        const url = new URL(a.getAttribute('href'), location.origin + path);
        if (url.origin !== location.origin || !url.pathname.endsWith('/')) continue;
        if (url.pathname.startsWith('/design-preview/studio/')) {
          if (!seen.has(url.pathname)) { seen.add(url.pathname); queue.push(url.pathname); }
        } else {
          const sources = legacy.get(url.pathname) || new Set(); sources.add(path); legacy.set(url.pathname, sources);
        }
      }
    }));
    if (queue.length > 2000) throw new Error('Unexpected route count');
  }
  window.studioCoverage = {pages, legacy: [...legacy].map(([path, sources]) => ({path, count:sources.size}))};
  return JSON.stringify({pages:pages.length, issues:pages.filter(p=>p.status!==200 || p.h1!==1 || !p.noindex),
    families:Object.fromEntries([...new Set(pages.map(p=>p.family))].map(f=>[f,pages.filter(p=>p.family===f).length])),
    legacy:window.studioCoverage.legacy.sort((a,b)=>b.count-a.count)});
})()
