// Render fetched HTML in same-origin srcdoc frames: real CSS layout, not top-level route interactions.
window.runLayoutBatch = async function(paths) {
  const results = [];
  for (const path of paths) {
    const html = await (await fetch(path)).text();
    for (const width of [1440,390]) {
      const frame = document.createElement('iframe');
      frame.style.cssText = `position:fixed;left:0;top:0;width:${width}px;height:900px;border:0;z-index:99999;background:white`;
      document.body.appendChild(frame);
      try {
        await new Promise((resolve,reject) => {const timer=setTimeout(()=>reject(new Error('load timeout')),12000);frame.onload=()=>{clearTimeout(timer);resolve()};frame.srcdoc=html.replace('<head>','<head><base href="'+location.origin+path+'">');});
        const win=frame.contentWindow, doc=frame.contentDocument;
        await Promise.race([doc.fonts.ready,new Promise(r=>setTimeout(r,1500))]);
        await new Promise(r=>requestAnimationFrame(()=>requestAnimationFrame(r)));
        const visible=e=>{const s=win.getComputedStyle(e),r=e.getBoundingClientRect();return r.width>0&&r.height>0&&s.visibility!=='hidden'&&s.display!=='none'};
        const over=[...doc.querySelectorAll('main *')].filter(e=>visible(e)&&e.getBoundingClientRect().right>width+3&&win.getComputedStyle(e).position!=='absolute').slice(0,6).map(e=>({tag:e.tagName,cls:e.className,right:Math.round(e.getBoundingClientRect().right)}));
        results.push({path,width,scrollWidth:doc.documentElement.scrollWidth,height:doc.documentElement.scrollHeight,family:doc.body.className,h1:[...doc.querySelectorAll('h1')].filter(visible).length,overflow:over,broken:[...doc.images].filter(e=>e.complete&&!e.naturalWidth).map(e=>e.getAttribute('src')),tinyInputs:[...doc.querySelectorAll('input:not([type=hidden]),select,textarea')].filter(e=>visible(e)&&e.getBoundingClientRect().width<90&&!['checkbox','radio'].includes(e.type)).map(e=>e.id||e.name)});
      } catch(e) {results.push({path,width,error:String(e)});}
      frame.remove();
    }
  }
  return JSON.stringify(results);
};
return 'Layout audit ready';
