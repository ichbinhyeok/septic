(() => {
    "use strict";

    const desk = document.querySelector("[data-sc-records-desk]");
    if (!(desk instanceof HTMLElement)) return;

    const form = desk.querySelector("[data-sc-route-form]");
    const county = desk.querySelector("[data-sc-county]");
    const age = desk.querySelector("[data-sc-age]");
    const clueType = desk.querySelector("[data-sc-clue-type]");
    const clue = desk.querySelector("[data-sc-clue]");
    const lot = desk.querySelector("[data-sc-lot]");
    const owner = desk.querySelector("[data-sc-owner]");
    const error = desk.querySelector("[data-sc-form-error]");
    const result = desk.querySelector("[data-sc-result]");
    const requestSection = desk.querySelector("[data-sc-request-section]");
    const requestCopy = desk.querySelector("[data-sc-request-copy]");
    const SITE_EXPLORER = "https://epermitting.des.sc.gov/ext/nsite/default/map/help";
    let prepared = null;

    const fields = {
        address: {label: "Physical address", placeholder: "123 Main Street", autocomplete: "street-address"},
        tms: {label: "Tax map / TMS number", placeholder: "County tax map identifier", autocomplete: "off"},
        owner: {label: "Original or prior owner", placeholder: "Name used when installed", autocomplete: "name"},
        permit: {label: "Permit number", placeholder: "SCDES / DHEC permit number", autocomplete: "off"}
    };
    const clean = (value, limit = 140) => String(value || "").trim().replace(/\s+/g, " ").slice(0, limit);
    const emit = (name, params = {}) => { if (typeof window.gtag === "function") window.gtag("event", name, params); };

    function selectedCounty() {
        const option = county?.selectedOptions?.[0];
        if (!option?.value) return null;
        return {key: option.value, name: option.dataset.countyName || option.textContent.trim(), internalPath: option.dataset.internalPath || ""};
    }

    function updateClue() {
        const config = fields[clueType?.value] || fields.address;
        desk.querySelector("[data-sc-clue-label]").textContent = config.label;
        clue.placeholder = config.placeholder;
        clue.autocomplete = config.autocomplete;
    }

    function variants(data) {
        const items = [data.clue];
        if (data.type === "address") {
            const shortened = data.clue.replace(/\b(Street|St|Road|Rd|Lane|Ln|Drive|Dr|Avenue|Ave|Boulevard|Blvd|Highway|Hwy|Court|Ct|Circle|Cir|Trail|Trl|Parkway|Pkwy)\b\.?/gi, "").replace(/\s+/g, " ").trim();
            items.push(shortened, shortened.replace(/^\d+[A-Za-z-]*\s+/, "").trim());
        }
        if (data.owner && data.type !== "owner") items.push(`Original / prior owner: ${data.owner}`);
        if (data.lot) items.push(`Subdivision / lot / block: ${data.lot}`);
        return items.filter((value, index, all) => value && all.indexOf(value) === index);
    }

    function officialLink(label, href, primary, context, internal = false) {
        const link = document.createElement("a");
        link.className = primary ? "button button--primary" : "button button--secondary";
        link.href = href;
        link.textContent = label;
        if (!internal) { link.target = "_blank"; link.rel = "noreferrer"; }
        link.dataset.trackClick = internal ? "county_route" : "official_source";
        link.dataset.trackSourceContext = context;
        link.dataset.trackTargetType = internal ? "internal_route" : "official_search";
        if (!internal) link.addEventListener("click", () => {
            emit("official_source_clicked", {state_code: "SC", county_name: prepared?.county.name || "", source_type: "scdes_site_explorer"});
            window.SepticRecordTask?.transition("official_opened", "official_opened");
        });
        return link;
    }

    function requestButton(label, primary) {
        const node = document.createElement("button");
        node.type = "button";
        node.className = primary ? "button button--primary" : "button button--secondary";
        node.textContent = label;
        node.addEventListener("click", showRequest);
        return node;
    }

    function requestText(data) {
        return [
            `Please provide all available onsite wastewater / septic system records for this property in ${data.county.name}, South Carolina.`,
            `${fields[data.type]?.label || "Property clue"}: ${data.clue}`,
            data.lot ? `Subdivision, lot, or block: ${data.lot}` : "",
            data.owner ? `Original or prior permit holder: ${data.owner}` : "",
            `Approximate property age: ${data.age === "newer" ? "built within about 20 years" : data.age === "older" ? "older than about 20 years" : "unknown"}.`,
            "Requested records: application or D-1740 trail, Permit to Construct, soil evaluation, approved layout or site drawing, final inspection / DES 4432, Approval to Operate, and any repair or modification records.",
            "If no file is located, please provide written confirmation of the identifiers and archives searched and identify the appropriate next office or record custodian."
        ].filter(Boolean).join("\n");
    }

    function showRequest() {
        if (!prepared) return;
        window.SepticRecordTask?.transition("request_prepared", "request_prepared");
        requestCopy.value = requestText(prepared);
        requestSection.hidden = false;
        requestSection.scrollIntoView({behavior: "smooth", block: "start"});
        emit("records_fallback_started", {state_code: "SC", county_name: prepared.county.name, property_age: prepared.age});
    }

    clueType?.addEventListener("change", updateClue);
    form?.addEventListener("submit", event => {
        event.preventDefault();
        const selected = selectedCounty();
        const clueValue = clean(clue?.value);
        if (!selected || !clueValue) {
            error.textContent = !selected ? "Choose the South Carolina county first." : "Enter at least one property identifier.";
            error.hidden = false;
            (!selected ? county : clue)?.focus();
            emit("records_route_error", {state_code: "SC", error_type: !selected ? "missing_county" : "missing_clue"});
            return;
        }
        error.hidden = true;
        prepared = {county: selected, age: age.value, type: clueType.value, clue: clueValue, lot: clean(lot?.value, 120), owner: clean(owner?.value, 100)};
        const directIdentifier = prepared.type === "tms" || prepared.type === "permit";
        window.SepticRecordTask?.prepare({
            stateCode: "SC", stateName: "South Carolina", countyName: selected.name,
            countyKey: `SC::${selected.key}`, routePath: window.location.pathname,
            routeMode: directIdentifier ? "official_search" : "identifier_or_request",
            routeReliability: directIdentifier ? "identifier_required" : "request_first",
            officialRoute: directIdentifier ? SITE_EXPLORER : selected.internalPath,
            requestRoute: window.location.pathname,
            requiredIdentifiers: directIdentifier ? [fields[prepared.type].label] : ["TMS / tax map number or permit number"],
            requestedDocuments: ["Permit to Construct", "soil evaluation", "approved layout", "DES 4432 final inspection", "repair history"]
        }, {address: prepared.type === "address" ? prepared.clue : "", identifierType: prepared.type, identifierValue: prepared.clue});
        window.SepticRecordTask?.transition("route_ready", "route_ready");
        desk.querySelector("[data-sc-result-status]").textContent = directIdentifier ? "Official identifier ready" : "TMS or file request needed";
        desk.querySelector("[data-sc-result-title]").textContent = `${selected.name} septic record search`;
        desk.querySelector("[data-sc-result-summary]").textContent = directIdentifier
            ? "Use this TMS or permit number in Site Explorer. A map pin alone is not a record result; return with the permit details or attachment."
            : "An address or owner name is not a reliable Site Explorer key. Get the county TMS first, or prepare an SCDES file request here.";
        desk.querySelector("[data-sc-search-variants]").replaceChildren(...variants(prepared).map(value => { const item = document.createElement("li"); item.textContent = value; return item; }));
        const actions = desk.querySelector("[data-sc-route-actions]");
        if (directIdentifier) {
            actions.replaceChildren(officialLink("Search SCDES with this identifier", SITE_EXPLORER, true, "sc_site_explorer"));
            actions.append(requestButton("Prepare a file request instead", false));
        } else {
            actions.replaceChildren(officialLink(`Get the ${selected.name} TMS`, selected.internalPath, true, "sc_county_guide", true));
            actions.append(requestButton("Prepare an SCDES file request", false));
        }
        result.hidden = false;
        requestSection.hidden = true;
        result.scrollIntoView({behavior: "smooth", block: "start"});
        emit("county_route_viewed", {state_code: "SC", county_name: selected.name, route_type: directIdentifier ? "scdes_site_explorer" : "identifier_or_request", property_age: age.value});
    });

    desk.querySelector("[data-sc-edit-search]")?.addEventListener("click", () => { prepared = null; result.hidden = true; requestSection.hidden = true; form.scrollIntoView({behavior: "smooth", block: "start"}); county?.focus(); });
    desk.querySelector("[data-sc-copy-clues]")?.addEventListener("click", async event => { if (!prepared) return; await navigator.clipboard.writeText(variants(prepared).join("\n")); event.currentTarget.textContent = "Copied"; });
    desk.querySelectorAll("[data-sc-open-request]").forEach(button => button.addEventListener("click", showRequest));
    desk.querySelector("[data-sc-copy-request]")?.addEventListener("click", async event => { await navigator.clipboard.writeText(requestCopy.value); event.currentTarget.textContent = "Copied"; });
    desk.querySelector("[data-sc-confirm-request]")?.addEventListener("click", () => {
        const checkin = desk.querySelector("[data-state-return-checkin]");
        const submitted = desk.querySelector('[data-state-return-outcome="request_submitted"]');
        if (checkin instanceof HTMLDetailsElement) checkin.open = true;
        if (submitted instanceof HTMLButtonElement) submitted.click();
        const returnPanel = desk.querySelector("[data-state-records-return]");
        returnPanel?.scrollIntoView({behavior: "smooth", block: "start"});
        window.setTimeout(() => returnPanel?.querySelector('input[name="submittedOn"]')?.focus(), 350);
    });
    updateClue();
})();
