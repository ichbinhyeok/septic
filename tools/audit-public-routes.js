// Read-only local HTML inventory. No form submission or external navigation.
return await (async () => {
  const origin = location.origin;
  const sitemap = new DOMParser().parseFromString(await (await fetch('/sitemap.xml')).text(), 'text/xml');
  const queue = [...new Set([...sitemap.querySelectorAll('loc')].map(e => new URL(e.textContent).pathname))];
  const seen = new Set(queue), results = [];
  for (let offset = 0; offset < queue.length; offset += 8) {
    await Promise.all(queue.slice(offset, offset + 8).map(async path => {
      try {
        const response = await fetch(path);
        if (!response.headers.get('content-type')?.includes('text/html')) return;
        const doc = new DOMParser().parseFromString(await response.text(), 'text/html');
        results.push({path, status:response.status, title:doc.title, family:doc.body.className,
          canonical:doc.querySelector('link[rel=canonical]')?.getAttribute('href'),
          robots:doc.querySelector('meta[name=robots]')?.content,
          h1:[...doc.querySelectorAll('h1')].map(e=>e.textContent.trim()),
          sections:[...doc.querySelectorAll('main section')].map(e=>e.className),
          heading:doc.querySelector('h1')?.textContent.trim(), sitemap:[...sitemap.querySelectorAll('loc')].some(e=>new URL(e.textContent).pathname===path)});
        for (const a of doc.querySelectorAll('a[href]')) {
          const u = new URL(a.getAttribute('href'), origin + path);
          if (![origin,'https://septicpath.com'].includes(u.origin) || u.search || !u.pathname.endsWith('/') || /^\/(api|admin|internal|assets|images|uploads|storage|checkout|payment)\//.test(u.pathname)) continue;
          if (!seen.has(u.pathname)) {seen.add(u.pathname); queue.push(u.pathname);}
        }
      } catch (e) {results.push({path,error:String(e)});}
    }));
    if (queue.length > 2500) throw new Error('Unexpected inventory size');
  }
  return JSON.stringify(results.sort((a,b)=>a.path.localeCompare(b.path)));
})()
