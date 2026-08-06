(() => {
    "use strict";

    const desk = document.querySelector("[data-nc-records-desk]");
    if (!(desk instanceof HTMLElement)) return;

    const form = desk.querySelector("[data-nc-route-form]");
    const county = desk.querySelector("[data-nc-county]");
    const clueType = desk.querySelector("[data-nc-clue-type]");
    const clueLabel = desk.querySelector("[data-nc-clue-label]");
    const clue = desk.querySelector("[data-nc-clue]");
    const owner = desk.querySelector("[data-nc-owner]");
    const subdivision = desk.querySelector("[data-nc-subdivision]");
    const error = desk.querySelector("[data-nc-form-error]");
    const result = desk.querySelector("[data-nc-result]");
    const variantsList = desk.querySelector("[data-nc-search-variants]");
    const actions = desk.querySelector("[data-nc-route-actions]");
    const requestSection = desk.querySelector("[data-nc-request-section]");
    const requestCopy = desk.querySelector("[data-nc-request-copy]");
    const requestRoute = desk.querySelector("[data-nc-request-route]");
    let prepared = null;

    const fields = {
        address: { label: "Street address", placeholder: "123 Main Street", autocomplete: "street-address" },
        parcel: { label: "Parcel ID or PIN", placeholder: "County parcel identifier", autocomplete: "off" },
        owner: { label: "Current or prior owner", placeholder: "Name on the property file", autocomplete: "name" },
        permit: { label: "Permit number", placeholder: "Permit or application number", autocomplete: "off" }
    };

    const clean = (value, limit = 140) => String(value || "").trim().replace(/\s+/g, " ").slice(0, limit);
    const emit = (name, params = {}) => { if (typeof window.gtag === "function") window.gtag("event", name, params); };

    function selectedCounty() {
        if (!(county instanceof HTMLSelectElement)) return null;
        const option = county.selectedOptions[0];
        if (!option?.value) return null;
        return {
            key: option.value,
            name: option.dataset.countyName || option.textContent.trim(),
            internalPath: option.dataset.internalPath || "",
            recordsUrl: option.dataset.recordsUrl || "",
            recordsLabel: option.dataset.recordsLabel || "Open official county record path"
        };
    }

    function updateClue() {
        if (!(clueType instanceof HTMLSelectElement) || !(clue instanceof HTMLInputElement)) return;
        const config = fields[clueType.value] || fields.address;
        clueLabel.textContent = config.label;
        clue.placeholder = config.placeholder;
        clue.autocomplete = config.autocomplete;
    }

    function variants(type, value, ownerValue, subdivisionValue) {
        const list = [value];
        if (type === "address") {
            const noSuffix = value.replace(/\b(Street|St|Road|Rd|Lane|Ln|Drive|Dr|Avenue|Ave|Boulevard|Blvd|Highway|Hwy|Court|Ct|Circle|Cir|Trail|Trl|Parkway|Pkwy)\b\.?/gi, "").replace(/\s+/g, " ").trim();
            const streetOnly = noSuffix.replace(/^\d+[A-Za-z-]*\s+/, "").trim();
            list.push(noSuffix, streetOnly);
        }
        if (ownerValue && type !== "owner") list.push(`Prior owner: ${ownerValue}`);
        if (subdivisionValue) list.push(`Subdivision / lot: ${subdivisionValue}`);
        return list.filter((item, index, values) => item && values.indexOf(item) === index);
    }

    function link(label, href, primary, official = false) {
        const node = document.createElement("a");
        node.className = primary ? "button button--primary" : "button button--secondary";
        node.href = href;
        node.textContent = label;
        if (official) {
            node.target = "_blank";
            node.rel = "noreferrer";
            node.dataset.trackClick = "official_source";
            node.dataset.trackSourceContext = "nc_county_record_route";
            node.dataset.trackTargetType = "official_source";
            node.addEventListener("click", () => emit("official_source_clicked", { state_code: "NC", county_name: prepared?.county.name || "" }));
            node.addEventListener("click", () => window.SepticRecordTask?.transition("official_opened", "official_opened"));
        }
        return node;
    }

    function requestText(data) {
        const fieldLabel = fields[data.type]?.label || "Property clue";
        return [
            `Please provide all available onsite wastewater / septic records for this property in ${data.county.name}.`,
            `${fieldLabel}: ${data.clue}`,
            data.owner ? `Current or prior owner: ${data.owner}` : "",
            data.subdivision ? `Subdivision / lot: ${data.subdivision}` : "",
            "Requested records: Improvement Permit, site or soil evaluation, Construction Authorization, Operation Permit or final approval, approved layout/site sketch, and any repair, malfunction, or modification records.",
            "If no file is located, please provide a written no-record response and identify any paper, pre-digital, prior-owner, or predecessor-district archive that should also be checked."
        ].filter(Boolean).join("\n");
    }

    function showRequest() {
        if (!prepared) return;
        window.SepticRecordTask?.transition("request_prepared", "request_prepared");
        requestCopy.value = requestText(prepared);
        requestRoute.href = prepared.county.internalPath;
        requestSection.hidden = false;
        requestSection.scrollIntoView({ behavior: "smooth", block: "start" });
        emit("records_fallback_started", { state_code: "NC", county_name: prepared.county.name });
    }

    clueType?.addEventListener("change", updateClue);
    form?.addEventListener("submit", (event) => {
        event.preventDefault();
        const countyValue = selectedCounty();
        const clueValue = clean(clue?.value);
        if (!countyValue || !clueValue) {
            error.textContent = !countyValue ? "Choose the North Carolina county first." : "Enter at least one property clue.";
            error.hidden = false;
            (!countyValue ? county : clue)?.focus();
            emit("records_route_error", { state_code: "NC", error_type: !countyValue ? "missing_county" : "missing_clue" });
            return;
        }
        error.hidden = true;
        prepared = {
            county: countyValue,
            type: clueType.value,
            clue: clueValue,
            owner: clean(owner?.value, 100),
            subdivision: clean(subdivision?.value, 100)
        };
        window.SepticRecordTask?.prepare({
            stateCode: "NC", stateName: "North Carolina", countyName: countyValue.name,
            countyKey: `NC::${countyValue.key}`, routePath: window.location.pathname,
            routeMode: "verified_county", routeReliability: "source_reviewed",
            officialRoute: countyValue.recordsUrl, requestRoute: countyValue.internalPath,
            requiredIdentifiers: [fields[prepared.type]?.label || "Property clue", "Prior owner or subdivision if available"],
            requestedDocuments: ["Improvement Permit", "Construction Authorization", "Operation Permit or final approval", "approved layout", "repair history"]
        }, {address: prepared.type === "address" ? prepared.clue : "", identifierType: prepared.type, identifierValue: prepared.clue});
        window.SepticRecordTask?.transition("route_ready", "route_ready");
        desk.querySelector("[data-nc-result-title]").textContent = `${countyValue.name} record route`;
        desk.querySelector("[data-nc-result-summary]").textContent = "Start with the verified county workflow. Use the official source only after you know which portal, form, email, or office owns the file.";
        variantsList.replaceChildren(...variants(prepared.type, prepared.clue, prepared.owner, prepared.subdivision).map((value) => {
            const item = document.createElement("li");
            item.textContent = value;
            return item;
        }));
        actions.replaceChildren(
            link(`Open ${countyValue.name} instructions`, countyValue.internalPath, true),
            link(countyValue.recordsLabel, countyValue.recordsUrl, false, true)
        );
        result.hidden = false;
        requestSection.hidden = true;
        result.scrollIntoView({ behavior: "smooth", block: "start" });
        emit("county_route_viewed", { state_code: "NC", county_name: countyValue.name, route_type: "verified_county" });
    });
    desk.querySelector("[data-nc-edit-search]")?.addEventListener("click", () => {
        prepared = null;
        result.hidden = true;
        requestSection.hidden = true;
        form.scrollIntoView({ behavior: "smooth", block: "start" });
        county?.focus();
    });
    desk.querySelector("[data-nc-copy-clues]")?.addEventListener("click", async (event) => {
        if (!prepared) return;
        await navigator.clipboard.writeText(variants(prepared.type, prepared.clue, prepared.owner, prepared.subdivision).join("\n"));
        event.currentTarget.textContent = "Copied";
    });
    desk.querySelectorAll("[data-nc-open-request]").forEach((button) => button.addEventListener("click", showRequest));
    desk.querySelector("[data-nc-copy-request]")?.addEventListener("click", async (event) => {
        await navigator.clipboard.writeText(requestCopy.value);
        event.currentTarget.textContent = "Copied";
    });
    updateClue();
})();
