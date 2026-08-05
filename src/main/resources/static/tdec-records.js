(() => {
    "use strict";

    const desk = document.querySelector("[data-tdec-records-desk]");
    if (!(desk instanceof HTMLElement)) return;

    const form = desk.querySelector("[data-tdec-route-form]");
    const county = desk.querySelector("[data-tdec-county]");
    const countyHelp = desk.querySelector("[data-tdec-county-help]");
    const clueType = desk.querySelector("[data-tdec-clue-type]");
    const clueLabel = desk.querySelector("[data-tdec-clue-label]");
    const clue = desk.querySelector("[data-tdec-clue]");
    const subdivision = desk.querySelector("[data-tdec-subdivision]");
    const owner = desk.querySelector("[data-tdec-owner]");
    const error = desk.querySelector("[data-tdec-form-error]");
    const result = desk.querySelector("[data-tdec-result]");
    const resultStatus = desk.querySelector("[data-tdec-result-status]");
    const resultTitle = desk.querySelector("[data-tdec-result-title]");
    const resultSummary = desk.querySelector("[data-tdec-result-summary]");
    const variantsList = desk.querySelector("[data-tdec-search-variants]");
    const routeExplanation = desk.querySelector("[data-tdec-route-explanation]");
    const routeActions = desk.querySelector("[data-tdec-route-actions]");
    const requestSection = desk.querySelector("[data-tdec-request-section]");
    const requestCopy = desk.querySelector("[data-tdec-request-copy]");
    const viewerOutcome = desk.querySelector("[data-tdec-viewer-outcome]");
    const fallbackButtons = Array.from(desk.querySelectorAll("[data-tdec-open-request]"));
    let prepared = null;

    const fieldConfig = {
        address: { label: "Street address", placeholder: "123 Main Street", autocomplete: "street-address" },
        parcel: { label: "Parcel or tax-map ID", placeholder: "Map and parcel number", autocomplete: "off" },
        owner: { label: "Current or prior owner", placeholder: "Name shown on the property file", autocomplete: "name" },
        permit: { label: "Permit number", placeholder: "Existing permit or application number", autocomplete: "off" }
    };

    function emit(eventName, params = {}) {
        if (typeof window.gtag === "function") window.gtag("event", eventName, params);
    }

    function normalized(value, limit = 140) {
        return String(value || "").trim().replace(/\s+/g, " ").slice(0, limit);
    }

    function selectedCounty() {
        if (!(county instanceof HTMLSelectElement)) return null;
        const option = county.selectedOptions[0];
        if (!option || !option.value) return null;
        return {
            key: option.value,
            name: option.dataset.countyName || option.textContent.trim(),
            contract: option.dataset.contractCounty === "true",
            internalPath: option.dataset.internalPath || ""
        };
    }

    function updateCountyHelp() {
        const selected = selectedCounty();
        if (!(countyHelp instanceof HTMLElement)) return;
        if (!selected) {
            countyHelp.textContent = "Tennessee has 9 counties that use their own septic program. The county decides the route.";
        } else if (selected.contract) {
            countyHelp.textContent = `${selected.name} uses its own local septic program, not the statewide TDEC service route.`;
        } else {
            countyHelp.textContent = `${selected.name} uses the TDEC SSDS record route. A field office or public-records request is the fallback.`;
        }
    }

    function updateClueField() {
        if (!(clueType instanceof HTMLSelectElement) || !(clue instanceof HTMLInputElement)) return;
        const config = fieldConfig[clueType.value] || fieldConfig.address;
        if (clueLabel instanceof HTMLElement) clueLabel.textContent = config.label;
        clue.placeholder = config.placeholder;
        clue.autocomplete = config.autocomplete;
    }

    function addressVariants(value) {
        const clean = normalized(value);
        const withoutDirection = clean.replace(/\b(North|South|East|West|Northeast|Northwest|Southeast|Southwest|N|S|E|W|NE|NW|SE|SW)\b\.?/gi, "").replace(/\s+/g, " ").trim();
        const withoutSuffix = withoutDirection.replace(/\b(Street|St|Road|Rd|Lane|Ln|Drive|Dr|Avenue|Ave|Boulevard|Blvd|Highway|Hwy|Court|Ct|Circle|Cir|Trail|Trl|Parkway|Pkwy)\b\.?/gi, "").replace(/\s+/g, " ").trim();
        const streetOnly = withoutSuffix.replace(/^\d+[A-Za-z-]*\s+/, "").trim();
        return [clean, withoutDirection, withoutSuffix, streetOnly].filter((value, index, values) => value && values.indexOf(value) === index);
    }

    function searchVariants(type, value, extraOwner, extraSubdivision) {
        const variants = type === "address" ? addressVariants(value) : [value];
        if (extraOwner && type !== "owner") variants.push(`Owner: ${extraOwner}`);
        if (extraSubdivision) variants.push(`Subdivision / lot: ${extraSubdivision}`);
        return variants.filter((value, index, values) => value && values.indexOf(value) === index);
    }

    function makeLink(label, href, primary, context) {
        const link = document.createElement("a");
        link.className = primary ? "button button--primary" : "button button--secondary";
        link.href = href;
        link.textContent = label;
        if (/^https?:/.test(href)) {
            link.target = "_blank";
            link.rel = "noreferrer";
            link.dataset.trackClick = "official_source";
            link.dataset.trackSourceContext = context;
            link.dataset.trackTargetType = "official_source";
            link.addEventListener("click", () => emit("official_source_clicked", {
                county_name: prepared?.county.name || "",
                route_type: prepared?.county.contract ? "contract_county" : "tdec",
                destination_name: context
            }));
        }
        return link;
    }

    function buildRequestText(data) {
        const clueName = fieldConfig[data.type]?.label || "Property clue";
        const optional = [
            data.subdivision ? `Subdivision / lot: ${data.subdivision}` : "",
            data.owner ? `Current or prior owner: ${data.owner}` : ""
        ].filter(Boolean);
        return [
            `Please provide all available SSDS / septic system records for the property in ${data.county.name}.`,
            `${clueName}: ${data.clue}`,
            ...optional,
            "Requested records: original construction permit, soil map or soil evaluation, system layout or as-built, final inspection, Certificate of Completion, and any repair or modification permits.",
            "If no file is located, please provide a written no-record response and identify any field office, local program, archived collection, or prior-owner index that should also be checked."
        ].join("\n");
    }

    function showRequest(fallbackType = "no_result") {
        if (!(requestSection instanceof HTMLElement) || !(requestCopy instanceof HTMLTextAreaElement)) return;
        if (!prepared) {
            prepared = {
                county: selectedCounty() || { name: "the selected Tennessee county", contract: false },
                type: clueType instanceof HTMLSelectElement ? clueType.value : "address",
                clue: clue instanceof HTMLInputElement ? normalized(clue.value) || "[property address or parcel ID]" : "[property address or parcel ID]",
                subdivision: subdivision instanceof HTMLInputElement ? normalized(subdivision.value, 100) : "",
                owner: owner instanceof HTMLInputElement ? normalized(owner.value, 100) : ""
            };
        }
        if (prepared.county.contract) {
            emit("records_fallback_started", { fallback_type: "contract_county", county_name: prepared.county.name });
            if (prepared.county.internalPath) {
                window.location.assign(prepared.county.internalPath);
            } else {
                window.open("https://www.tn.gov/environment/permits/water/septic-systems-permits/ssp/wr-sds-online-application-for-ground-water-protection-services.html", "_blank", "noopener,noreferrer");
            }
            return;
        }
        requestCopy.value = buildRequestText(prepared);
        requestSection.hidden = false;
        requestSection.scrollIntoView({ behavior: "smooth", block: "start" });
        emit("records_fallback_started", { fallback_type: fallbackType, county_name: prepared.county.name });
    }

    function renderPreparedRoute(data) {
        if (!(result instanceof HTMLElement) || !(variantsList instanceof HTMLOListElement) || !(routeActions instanceof HTMLElement)) return;
        const variants = searchVariants(data.type, data.clue, data.owner, data.subdivision);
        resultStatus.textContent = data.county.contract ? "Local county program" : "TDEC-managed county";
        resultTitle.textContent = data.county.contract
            ? `${data.county.name} uses a local septic records route`
            : `Search TDEC records for ${data.county.name}`;
        resultSummary.textContent = data.county.contract
            ? "Do not use an empty TDEC result to judge this property. Start with the county's own instructions."
            : "Try the prepared values in the official SSDS Record Search. Return here if the viewer is blocked or nothing appears.";

        variantsList.replaceChildren(...variants.map((value) => {
            const item = document.createElement("li");
            item.textContent = value;
            return item;
        }));

        routeExplanation.textContent = data.county.contract
            ? `${data.county.name} is one of Tennessee's nine locally administered septic counties.`
            : "TDEC lists the SSDS Record Search as the online source for existing septic records. The public-records form is the written fallback.";
        routeActions.replaceChildren();
        if (viewerOutcome instanceof HTMLDetailsElement) viewerOutcome.hidden = data.county.contract;
        fallbackButtons.forEach((button) => {
            button.textContent = data.county.contract ? `Open ${data.county.name} fallback instructions` : (button.closest("details") === viewerOutcome ? "Prepare blocked-viewer fallback" : "No-result request");
        });
        if (data.county.contract && data.county.internalPath) {
            routeActions.append(makeLink(`Open ${data.county.name} instructions`, data.county.internalPath, true, "tdec_contract_county_guide"));
        } else if (data.county.contract) {
            routeActions.append(makeLink("Open Tennessee local-service directory", "https://www.tn.gov/environment/permits/water/septic-systems-permits/ssp/wr-sds-online-application-for-ground-water-protection-services.html", true, "tdec_contract_county_directory"));
        } else {
            routeActions.append(
                makeLink("Open official SSDS Record Search", "https://tdec.tn.gov/document-viewer/search/stp", true, "tdec_ssds_record_search"),
                makeLink("Open current TDEC SSDS page", "https://www.tn.gov/environment/permits/water/septic-systems-permits.html", false, "tdec_ssds_program")
            );
            if (data.county.internalPath) {
                routeActions.append(makeLink(`See ${data.county.name} fallback`, data.county.internalPath, false, "tdec_county_fallback"));
            }
        }
        result.hidden = false;
        requestSection.hidden = true;
        result.scrollIntoView({ behavior: "smooth", block: "start" });
        emit("county_route_viewed", {
            state_code: "TN",
            county_name: data.county.name,
            route_type: data.county.contract ? "contract_county" : "tdec"
        });
    }

    county?.addEventListener("change", updateCountyHelp);
    clueType?.addEventListener("change", updateClueField);
    form?.addEventListener("submit", (event) => {
        event.preventDefault();
        const countyValue = selectedCounty();
        const clueValue = clue instanceof HTMLInputElement ? normalized(clue.value) : "";
        if (!countyValue || !clueValue) {
            if (error instanceof HTMLElement) {
                error.textContent = !countyValue ? "Choose the Tennessee county first." : "Enter at least one property clue.";
                error.hidden = false;
            }
            (!countyValue ? county : clue)?.focus();
            emit("records_route_error", { error_type: !countyValue ? "missing_county" : "missing_clue" });
            return;
        }
        if (error instanceof HTMLElement) error.hidden = true;
        prepared = {
            county: countyValue,
            type: clueType instanceof HTMLSelectElement ? clueType.value : "address",
            clue: clueValue,
            subdivision: subdivision instanceof HTMLInputElement ? normalized(subdivision.value, 100) : "",
            owner: owner instanceof HTMLInputElement ? normalized(owner.value, 100) : ""
        };
        renderPreparedRoute(prepared);
    });

    desk.querySelector("[data-tdec-edit-search]")?.addEventListener("click", () => {
        prepared = null;
        result.hidden = true;
        requestSection.hidden = true;
        if (viewerOutcome instanceof HTMLDetailsElement) viewerOutcome.hidden = false;
        fallbackButtons.forEach((button) => {
            button.textContent = button.closest("details") === viewerOutcome ? "Prepare blocked-viewer fallback" : "No-result request";
        });
        form.scrollIntoView({ behavior: "smooth", block: "start" });
        county?.focus();
    });
    desk.querySelector("[data-tdec-copy-clues]")?.addEventListener("click", async (event) => {
        if (!prepared) return;
        const values = searchVariants(prepared.type, prepared.clue, prepared.owner, prepared.subdivision).join("\n");
        await navigator.clipboard.writeText(values);
        event.currentTarget.textContent = "Copied";
    });
    fallbackButtons.forEach((button) => button.addEventListener("click", () => showRequest(button.closest("details") === viewerOutcome ? "viewer_blocked" : "no_result")));
    desk.querySelector("[data-tdec-copy-request]")?.addEventListener("click", async (event) => {
        if (!(requestCopy instanceof HTMLTextAreaElement)) return;
        await navigator.clipboard.writeText(requestCopy.value);
        event.currentTarget.textContent = "Copied";
    });
    updateCountyHelp();
    updateClueField();
})();
