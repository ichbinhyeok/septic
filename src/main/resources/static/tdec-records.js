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
    const verification = desk.querySelector("[data-tdec-search-verification]");
    const routeExplanation = desk.querySelector("[data-tdec-route-explanation]");
    const routeActions = desk.querySelector("[data-tdec-route-actions]");
    const routeHandoff = desk.querySelector("[data-tdec-route-handoff]");
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
            internalPath: option.dataset.internalPath || "",
            fieldOfficeName: option.dataset.fieldOfficeName || "",
            fieldOfficeUrl: option.dataset.fieldOfficeUrl || ""
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
        const tidy = candidate => normalized(candidate).replace(/\s+([,;])/g, "$1").replace(/([,;]){2,}/g, "$1");
        const streetAddress = tidy(clean.split(",", 1)[0]);
        const withoutDirection = tidy(streetAddress.replace(/\b(North|South|East|West|Northeast|Northwest|Southeast|Southwest|N|S|E|W|NE|NW|SE|SW)\b\.?/gi, ""));
        const withoutSuffix = tidy(withoutDirection.replace(/\b(Street|St|Road|Rd|Lane|Ln|Drive|Dr|Avenue|Ave|Boulevard|Blvd|Highway|Hwy|Court|Ct|Circle|Cir|Trail|Trl|Parkway|Pkwy|Pike|Place|Pl|Terrace|Ter|Turnpike|Route|Rte|Way)\b\.?/gi, ""));
        const streetOnly = withoutSuffix.replace(/^\d+[A-Za-z-]*\s+/, "").trim();
        return [clean, streetAddress, withoutDirection, withoutSuffix, streetOnly]
            .filter((value, index, values) => value && values.indexOf(value) === index);
    }

    function clueError(type, value) {
        const compact = value.replace(/[^A-Za-z0-9]/g, "");
        if (type === "address" && !/[A-Za-z]{3,}/.test(value)) {
            return "Enter a street name, with the street number if you have it. A number by itself is not enough.";
        }
        if (type === "owner" && !/[A-Za-z]{2,}/.test(value)) {
            return "Enter at least two letters from the current or prior owner's name.";
        }
        if ((type === "parcel" || type === "permit") && compact.length < 3) {
            return `Enter a more complete ${type === "parcel" ? "parcel or tax-map ID" : "permit number"}.`;
        }
        return "";
    }

    function resetReturnState() {
        document.dispatchEvent(new CustomEvent("state-records-search-reset", { detail: { stateCode: "TN" } }));
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
        if (verification instanceof HTMLElement) {
            verification.textContent = data.type === "address"
                ? `County is based on your selection. SepticPath did not verify that this address is inside ${data.county.name}.`
                : `County is based on your selection. SepticPath did not independently match this ${fieldConfig[data.type]?.label.toLowerCase() || "property clue"} to ${data.county.name}.`;
        }

        routeExplanation.textContent = data.county.contract
            ? `${data.county.name} is one of Tennessee's nine locally administered septic counties.`
            : `TDEC manages this county. The ${data.county.fieldOfficeName} Environmental Field Office is the regional fallback if the online search is blocked or incomplete.`;
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
                makeLink("Open the current TDEC SSDS page", "https://www.tn.gov/environment/permits/water/septic-systems-permits.html", true, "tdec_ssds_program"),
                makeLink(`Contact the ${data.county.fieldOfficeName} Field Office`, data.county.fieldOfficeUrl, false, "tdec_field_office"),
                makeLink("Try the direct record viewer (may return 403)", "https://tdec.tn.gov/document-viewer/search/stp", false, "tdec_ssds_record_search")
            );
            if (data.county.internalPath) {
                routeActions.append(makeLink(`See ${data.county.name} fallback`, data.county.internalPath, false, "tdec_county_fallback"));
            }
        }
        if (routeHandoff instanceof HTMLElement) {
            routeHandoff.textContent = data.county.contract
                ? "This opens SepticPath's county instructions first. From there, use the verified county form or office route."
                : `Start with the current SSDS page. If it is blocked or incomplete, the ${data.county.fieldOfficeName} office is the county-specific fallback; use the direct viewer only as an optional attempt because it may return 403.`;
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
    form?.addEventListener("input", resetReturnState);
    form?.addEventListener("change", resetReturnState);
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
        const type = clueType instanceof HTMLSelectElement ? clueType.value : "address";
        const validationError = clueError(type, clueValue);
        if (validationError) {
            if (error instanceof HTMLElement) {
                error.textContent = validationError;
                error.hidden = false;
            }
            clue?.focus();
            emit("records_route_error", { error_type: `invalid_${type}` });
            return;
        }
        if (error instanceof HTMLElement) error.hidden = true;
        resetReturnState();
        prepared = {
            county: countyValue,
            type,
            clue: clueValue,
            subdivision: subdivision instanceof HTMLInputElement ? normalized(subdivision.value, 100) : "",
            owner: owner instanceof HTMLInputElement ? normalized(owner.value, 100) : ""
        };
        renderPreparedRoute(prepared);
    });

    desk.querySelector("[data-tdec-edit-search]")?.addEventListener("click", () => {
        resetReturnState();
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
