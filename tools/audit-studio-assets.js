return await (async () => {
  const assets = new Set(), errors = [];
  const pages = window.studioCoverage.pages;
  for (let i = 0; i < pages.length; i += 8) {
    await Promise.all(pages.slice(i, i + 8).map(async ({path}) => {
      const doc = new DOMParser().parseFromString(await (await fetch(path)).text(), 'text/html');
      for (const el of doc.querySelectorAll('img[src],source[srcset],img[srcset],script[src],link[rel=stylesheet]')) {
        const raw = el.getAttribute('srcset');
        const urls = raw ? raw.split(',').map(s=>s.trim().split(/\s+/)[0]) : [el.getAttribute('src') || el.getAttribute('href')];
        for (const value of urls) {
          if (!value) continue;
          const url = new URL(value, location.origin + path);
          if (url.origin === location.origin) assets.add(url.pathname + url.search);
        }
      }
    }));
  }
  const urls = [...assets];
  for (let i = 0; i < urls.length; i += 8) {
    await Promise.all(urls.slice(i, i + 8).map(async path => {
      const response = await fetch(path, {method:'HEAD'});
      if (!response.ok) errors.push({path,status:response.status});
    }));
  }
  window.assetAudit = {pages:pages.length,assets:urls.length,errors};
  return JSON.stringify(window.assetAudit);
})();
