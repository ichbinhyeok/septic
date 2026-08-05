(() => {
    "use strict";
    const desk = document.querySelector("[data-tx-records-desk]");
    if (!(desk instanceof HTMLElement)) return;
    const form = desk.querySelector("[data-tx-route-form]");
    const county = desk.querySelector("[data-tx-county]");
    const clueType = desk.querySelector("[data-tx-clue-type]");
    const clue = desk.querySelector("[data-tx-clue]");
    const legal = desk.querySelector("[data-tx-legal]");
    const subdivision = desk.querySelector("[data-tx-subdivision]");
    const result = desk.querySelector("[data-tx-result]");
    const error = desk.querySelector("[data-tx-form-error]");
    const requestSection = desk.querySelector("[data-tx-request-section]");
    const requestCopy = desk.querySelector("[data-tx-request-copy]");
    const requestRoute = desk.querySelector("[data-tx-request-route]");
    let prepared = null;
    const fields = { address:{label:"Street address",placeholder:"123 Main Street",autocomplete:"street-address"}, parcel:{label:"Parcel or account ID",placeholder:"County parcel identifier",autocomplete:"off"}, owner:{label:"Current or prior owner",placeholder:"Name on the OSSF file",autocomplete:"name"}, permit:{label:"Permit number",placeholder:"OSSF permit or application number",autocomplete:"off"} };
    const clean = (value, limit=140) => String(value||"").trim().replace(/\s+/g," ").slice(0,limit);
    const emit = (name, params={}) => { if (typeof window.gtag === "function") window.gtag("event",name,params); };
    function selectedCounty(){ const option=county?.selectedOptions?.[0]; if(!option?.value)return null; return {name:option.dataset.countyName||option.textContent.trim(),internalPath:option.dataset.internalPath||"",recordsUrl:option.dataset.recordsUrl||"",recordsLabel:option.dataset.recordsLabel||"Open official record route"}; }
    function updateClue(){ const config=fields[clueType?.value]||fields.address; desk.querySelector("[data-tx-clue-label]").textContent=config.label; clue.placeholder=config.placeholder; clue.autocomplete=config.autocomplete; }
    function variants(data){ const list=[data.clue]; if(data.type==="address"){const short=data.clue.replace(/\b(Street|St|Road|Rd|Lane|Ln|Drive|Dr|Avenue|Ave|Boulevard|Blvd|Highway|Hwy|Court|Ct|Circle|Cir|Trail|Trl|Parkway|Pkwy)\b\.?/gi,"").replace(/\s+/g," ").trim();list.push(short,short.replace(/^\d+[A-Za-z-]*\s+/,"").trim());} if(data.legal)list.push(`Legal description: ${data.legal}`);if(data.subdivision)list.push(`Subdivision / lot: ${data.subdivision}`);return list.filter((v,i,a)=>v&&a.indexOf(v)===i); }
    function makeLink(label,href,primary,official=false){const a=document.createElement("a");a.className=primary?"button button--primary":"button button--secondary";a.href=href;a.textContent=label;if(official){a.target="_blank";a.rel="noreferrer";a.dataset.trackClick="official_source";a.dataset.trackSourceContext="tx_county_record_route";a.dataset.trackTargetType="official_source";a.addEventListener("click",()=>emit("official_source_clicked",{state_code:"TX",county_name:prepared?.county.name||""}));}return a;}
    function requestText(data){return [`Please provide all available OSSF / septic records for this property in ${data.county.name}.`,`${fields[data.type]?.label||"Property clue"}: ${data.clue}`,data.legal?`Legal description: ${data.legal}`:"",data.subdivision?`Subdivision / lot: ${data.subdivision}`:"","Requested records: permit application and permit, Authorization to Construct, site evaluation and planning materials, inspections, Notice of Approval, and any repair, alteration, complaint, or maintenance records.","If no file is located, please provide a written no-record response and identify any predecessor authorized agent, TCEQ regional archive, prior-owner index, or other collection that should also be checked."].filter(Boolean).join("\n");}
    function showRequest(){if(!prepared)return;requestCopy.value=requestText(prepared);requestRoute.href=prepared.county.internalPath;requestSection.hidden=false;requestSection.scrollIntoView({behavior:"smooth",block:"start"});emit("records_fallback_started",{state_code:"TX",county_name:prepared.county.name});}
    clueType?.addEventListener("change",updateClue);
    form?.addEventListener("submit",event=>{event.preventDefault();const c=selectedCounty();const clueValue=clean(clue?.value);if(!c||!clueValue){error.textContent=!c?"Choose a verified Texas county or use the TCEQ authority search.":"Enter at least one property clue.";error.hidden=false;(!c?county:clue)?.focus();emit("records_route_error",{state_code:"TX",error_type:!c?"missing_county":"missing_clue"});return;}error.hidden=true;prepared={county:c,type:clueType.value,clue:clueValue,legal:clean(legal?.value,120),subdivision:clean(subdivision?.value,100)};desk.querySelector("[data-tx-result-title]").textContent=`${c.name} OSSF record route`;desk.querySelector("[data-tx-result-summary]").textContent="This county has a source-reviewed record workflow. Confirm the permitting authority in OARS if the county says another city, district, or TCEQ region owns the address.";desk.querySelector("[data-tx-search-variants]").replaceChildren(...variants(prepared).map(v=>{const li=document.createElement("li");li.textContent=v;return li;}));desk.querySelector("[data-tx-route-actions]").replaceChildren(makeLink(`Open ${c.name} instructions`,c.internalPath,true),makeLink(c.recordsLabel,c.recordsUrl,false,true));result.hidden=false;requestSection.hidden=true;result.scrollIntoView({behavior:"smooth",block:"start"});emit("county_route_viewed",{state_code:"TX",county_name:c.name,route_type:"verified_county"});});
    desk.querySelector("[data-tx-edit-search]")?.addEventListener("click",()=>{prepared=null;result.hidden=true;requestSection.hidden=true;form.scrollIntoView({behavior:"smooth",block:"start"});county?.focus();});
    desk.querySelector("[data-tx-copy-clues]")?.addEventListener("click",async event=>{if(!prepared)return;await navigator.clipboard.writeText(variants(prepared).join("\n"));event.currentTarget.textContent="Copied";});
    desk.querySelectorAll("[data-tx-open-request]").forEach(button=>button.addEventListener("click",showRequest));
    desk.querySelector("[data-tx-copy-request]")?.addEventListener("click",async event=>{await navigator.clipboard.writeText(requestCopy.value);event.currentTarget.textContent="Copied";});
    updateClue();
})();
