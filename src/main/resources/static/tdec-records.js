(() => {
    "use strict";

    const desk = document.querySelector("[data-tdec-records-desk]");
    if (!(desk instanceof HTMLElement)) return;

    const SESSION_KEY = "septicpath:tdec-route:v2";
    const TDEC_VIEWER = "https://tdec.tn.gov/document-viewer/search/stp";
    const TDEC_SERVICES = "https://www.tn.gov/environment/permits/water/septic-systems-permits/ssp/wr-sds-online-application-for-ground-water-protection-services.html";
    const TDEC_PUBLIC_RECORDS = "https://www.tn.gov/environment/contacts/public-records-request.html";
    const TPAD = "https://assessment.cot.tn.gov/TPAD";

    const form = desk.querySelector("[data-tdec-route-form]");
    const county = desk.querySelector("[data-tdec-county]");
    const countyHelp = desk.querySelector("[data-tdec-county-help]");
    const address = desk.querySelector("[data-tdec-address]");
    const parcel = desk.querySelector("[data-tdec-parcel]");
    const owner = desk.querySelector("[data-tdec-owner]");
    const subdivision = desk.querySelector("[data-tdec-subdivision]");
    const permit = desk.querySelector("[data-tdec-permit]");
    const error = desk.querySelector("[data-tdec-form-error]");
    const result = desk.querySelector("[data-tdec-result]");
    const resultStatus = desk.querySelector("[data-tdec-result-status]");
    const resultTitle = desk.querySelector("[data-tdec-result-title]");
    const resultSummary = desk.querySelector("[data-tdec-result-summary]");
    const routeTitle = desk.querySelector("[data-tdec-route-title]");
    const routeExplanation = desk.querySelector("[data-tdec-route-explanation]");
    const routeActions = desk.querySelector("[data-tdec-route-actions]");
    const routeHint = desk.querySelector("[data-tdec-route-hint]");
    const cluesList = desk.querySelector("[data-tdec-search-clues]");
    const verification = desk.querySelector("[data-tdec-search-verification]");
    const returnPanel = desk.querySelector("[data-tdec-return]");
    const outcomeNext = desk.querySelector("[data-tdec-outcome-next]");
    const requestSection = desk.querySelector("[data-tdec-request-section]");
    const requestCopy = desk.querySelector("[data-tdec-request-copy]");
    const requestGuidance = desk.querySelector("[data-tdec-request-guidance]");
    const requestLink = desk.querySelector("[data-tdec-request-link]");
    let prepared = null;
    let routeRevision = 0;
    let activeAddressVerification = null;

    function emit(eventName, params = {}) {
        if (typeof window.gtag === "function") window.gtag("event", eventName, params);
    }

    function normalized(value, limit = 160) {
        return String(value || "").trim().replace(/\s+/g, " ").slice(0, limit);
    }

    function purposeValue() {
        const checked = form?.querySelector('input[name="tdec-purpose"]:checked');
        return checked instanceof HTMLInputElement ? checked.value : "records";
    }

    function selectedCounty() {
        if (!(county instanceof HTMLSelectElement)) return null;
        const option = county.selectedOptions[0];
        if (!option?.value) return null;
        return {
            key: option.value,
            name: option.dataset.countyName || option.textContent.trim(),
            contract: option.dataset.contractCounty === "true",
            internalPath: option.dataset.internalPath || "",
            fieldOfficeName: option.dataset.fieldOfficeName || "TDEC",
            fieldOfficeUrl: option.dataset.fieldOfficeUrl || "",
            recordsUrl: option.dataset.recordsUrl || TDEC_VIEWER,
            recordsLabel: option.dataset.recordsLabel || "Open official record search",
            recordsHint: option.dataset.recordsHint || ""
        };
    }

    function updateCountyHelp() {
        if (!(countyHelp instanceof HTMLElement)) return;
        const selected = selectedCounty();
        countyHelp.textContent = !selected
            ? "The county determines whether TDEC or a local program owns the record."
            : selected.contract
                ? `${selected.name} runs its own septic program. We will bypass the statewide TDEC record route.`
                : `${selected.name} is routed through TDEC and the ${selected.fieldOfficeName} Environmental Field Office.`;
    }

    function values() {
        return {
            purpose: purposeValue(),
            county: selectedCounty(),
            address: normalized(address?.value),
            parcel: normalized(parcel?.value, 80),
            owner: normalized(owner?.value, 100),
            subdivision: normalized(subdivision?.value, 100),
            permit: normalized(permit?.value, 80)
        };
    }

    function addressLooksComplete(value) {
        const upper = value.toUpperCase();
        const hasState = /(?:^|[\s,])(AL|AK|AZ|AR|CA|CO|CT|DE|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY|DC)(?:[\s,]|$)/.test(upper);
        const hasZip = /\b\d{5}(?:-\d{4})?\s*$/.test(upper);
        const localityShape = (value.match(/,/g) || []).length >= 2 || value.split(/\s+/).length >= 5;
        return value.length >= 8 && /\d/.test(value) && /[A-Za-z]/.test(value) && localityShape && (hasState || hasZip);
    }

    function showError(message, target) {
        if (error instanceof HTMLElement) {
            error.textContent = message;
            error.hidden = false;
        }
        target?.focus();
    }

    function clearError() {
        if (error instanceof HTMLElement) error.hidden = true;
    }

    function clearPreparedState() {
        routeRevision += 1;
        activeAddressVerification?.controller.abort();
        activeAddressVerification = null;
        prepared = null;
        if (result instanceof HTMLElement) result.hidden = true;
        if (returnPanel instanceof HTMLElement) returnPanel.hidden = true;
        if (requestSection instanceof HTMLElement) requestSection.hidden = true;
        if (outcomeNext instanceof HTMLElement) outcomeNext.hidden = true;
        try {
            sessionStorage.removeItem(SESSION_KEY);
        } catch (_) {
            // Storage may be disabled; the visible state is still cleared.
        }
    }

    function countyKeyFromName(name) {
        return normalized(name).replace(/\s+County$/i, "").toLowerCase().replace(/\s+/g, "-");
    }

    async function verifyAddress(data, signal) {
        if (!data.address) return { data, message: "County is based on your selection because no full address was entered." };
        if (!addressLooksComplete(data.address)) {
            throw new Error("Include the street, city, and a state abbreviation or ZIP, or leave address blank and use another property clue.");
        }

        emit("address_search_started", { state_code: "TN", source: "tdec_records_desk" });
        try {
            const response = await fetch("/api/address-record-finder", {
                method: "POST",
                headers: { "Content-Type": "application/json", "Accept": "application/json" },
                body: JSON.stringify({ address: data.address }),
                signal
            });
            const lookup = await response.json();
            if (!response.ok || lookup.status === "invalid") throw new Error(lookup.message || "Enter a complete U.S. property address.");
            if (lookup.status === "unavailable") {
                emit("records_fallback_started", { fallback_type: "county_lookup_unavailable", state_code: "TN" });
                return { data, message: "Address verification is temporarily unavailable. The route below uses your selected county." };
            }
            if (lookup.stateCode && String(lookup.stateCode).toUpperCase() !== "TN") {
                throw new Error(`That address matched ${lookup.stateName || "a state outside Tennessee"}. Use the correct state records route.`);
            }
            if (lookup.status === "not_found") {
                emit("records_fallback_started", { fallback_type: "address_not_found", state_code: "TN" });
                return { data, message: "The Census address service did not match this address. Continue with the selected county and confirm it before relying on the route." };
            }
            if (!["county_route", "state_route"].includes(lookup.status)) {
                throw new Error("We could not verify that address. Check the city, state, and ZIP, or leave it blank and use a parcel or owner clue.");
            }

            const selectedKey = data.county.key;
            const matchedKey = countyKeyFromName(lookup.countyName);
            const countyChanged = Boolean(matchedKey && matchedKey !== selectedKey);
            if (countyChanged && county instanceof HTMLSelectElement) {
                const matchedOption = Array.from(county.options).find(option => option.value === matchedKey);
                if (matchedOption) {
                    county.value = matchedKey;
                    data.county = selectedCounty();
                    updateCountyHelp();
                }
            }
            data.address = normalized(lookup.matchedAddress || data.address);
            emit("address_search_completed", { state_code: "TN", county_name: data.county.name, match_status: "matched" });
            return {
                data,
                message: countyChanged
                    ? `Address matched ${data.county.name}, so the official route was updated: ${data.address}`
                    : `Address matched ${data.county.name}: ${data.address}`
            };
        } catch (failure) {
            if (failure instanceof TypeError) {
                emit("records_fallback_started", { fallback_type: "address_network_error", state_code: "TN" });
                return { data, message: "Address verification could not connect. The route below uses your selected county." };
            }
            if (failure?.name !== "AbortError") emit("records_route_error", { error_type: "address_not_verified", state_code: "TN" });
            throw failure;
        }
    }

    function cluesFor(data) {
        const clues = [];
        if (data.address) {
            clues.push(data.address);
            const street = normalized(data.address.split(",", 1)[0]);
            const streetOnly = normalized(street.replace(/^\d+[A-Za-z-]*\s+/, ""));
            if (street && street !== data.address) clues.push(street);
            if (streetOnly && streetOnly !== street) clues.push(`${streetOnly} (street name only)`);
        }
        if (data.parcel) clues.push(`Parcel / tax map: ${data.parcel}`);
        if (data.owner) clues.push(`Owner: ${data.owner}`);
        if (data.subdivision) clues.push(`Subdivision / lot: ${data.subdivision}`);
        if (data.permit) clues.push(`Permit: ${data.permit}`);
        if (clues.length === 0) clues.push("Start with the street address; retry with parcel, prior owner, subdivision, or permit number if needed.");
        return [...new Set(clues)];
    }

    function routeFor(data) {
        const countyData = data.county;
        if (countyData.contract) {
            return {
                title: countyData.recordsLabel,
                url: countyData.recordsUrl,
                explanation: `${countyData.name} is one of Tennessee’s nine locally administered septic counties. Start with the county program, not the statewide TDEC viewer.`,
                hint: countyData.recordsHint,
                label: countyData.recordsLabel,
                context: "tn_contract_county_records"
            };
        }
        if (data.purpose === "status") {
            return {
                title: "Request the official service that answers the status question",
                url: TDEC_SERVICES,
                explanation: "A permit PDF shows record history; it does not certify current status. Use TDEC Online Services for an Inspection Letter or the service named by TDEC.",
                hint: `The ${countyData.fieldOfficeName} Environmental Field Office is the county-specific fallback if the service route is unclear.`,
                label: "Open TDEC Online Services",
                context: "tdec_inspection_letter"
            };
        }
        if (data.purpose === "repair") {
            return {
                title: "Start the repair or modification service",
                url: TDEC_SERVICES,
                explanation: "Existing records can help the reviewer, but repair approval is a separate current service. Start with TDEC Online Services for this property.",
                hint: `Keep any old layout or permit, then follow instructions from the ${countyData.fieldOfficeName} Environmental Field Office.`,
                label: "Open TDEC repair services",
                context: "tdec_repair_service"
            };
        }
        if (data.purpose === "missing") {
            return {
                title: `Contact the ${countyData.fieldOfficeName} Environmental Field Office`,
                url: countyData.fieldOfficeUrl,
                explanation: "An empty or blocked online search is not a no-record determination. Give the field office the prepared property keys and ask it to check archived and pre-digital SSDS files.",
                hint: "Ask for a written no-record response if the office cannot locate a file.",
                label: `Open ${countyData.fieldOfficeName} Field Office`,
                context: "tdec_missing_record_office"
            };
        }
        return {
            title: "Search the official TDEC SSDS records",
            url: TDEC_VIEWER,
            explanation: "TDEC manages existing septic records for this county. Start with the statewide SSDS search using the property address, parcel, prior owner, subdivision, or permit number.",
            hint: `If the search is empty or unavailable, contact the ${countyData.fieldOfficeName} Environmental Field Office for archived and pre-digital files.`,
            label: "Open official TDEC SSDS record search",
            context: "tdec_ssds_record_search"
        };
    }

    function makeOfficialLink(route, data) {
        const link = document.createElement("a");
        link.className = "button button--primary";
        link.href = route.url;
        link.target = "_blank";
        link.rel = "noreferrer";
        link.textContent = route.label;
        link.addEventListener("click", () => {
            saveSession(data, true);
            window.SepticRecordTask?.transition("official_opened", "official_opened");
            if (returnPanel instanceof HTMLElement) returnPanel.hidden = false;
            emit("official_source_clicked", {
                state_code: "TN",
                county_name: data.county.name,
                destination_type: route.context,
                purpose: data.purpose
            });
        });
        return link;
    }

    function makeSecondaryLink(label, href, context, data) {
        const link = document.createElement("a");
        link.className = "text-button";
        link.href = href;
        link.target = "_blank";
        link.rel = "noreferrer";
        link.textContent = label;
        link.addEventListener("click", () => emit("official_source_clicked", {
            state_code: "TN", county_name: data.county.name, destination_type: context, purpose: data.purpose
        }));
        return link;
    }

    function saveSession(data, opened = false) {
        try {
            sessionStorage.setItem(SESSION_KEY, JSON.stringify({ ...data, opened, savedAt: Date.now() }));
        } catch (_) {
            // The route remains usable when storage is disabled.
        }
    }

    function restoreForm(data) {
        const purpose = form?.querySelector(`input[name="tdec-purpose"][value="${data.purpose}"]`);
        if (purpose instanceof HTMLInputElement) purpose.checked = true;
        if (address instanceof HTMLInputElement) address.value = data.address || "";
        if (parcel instanceof HTMLInputElement) parcel.value = data.parcel || "";
        if (owner instanceof HTMLInputElement) owner.value = data.owner || "";
        if (subdivision instanceof HTMLInputElement) subdivision.value = data.subdivision || "";
        if (permit instanceof HTMLInputElement) permit.value = data.permit || "";
        const more = desk.querySelector(".tdec-property__more");
        if (more instanceof HTMLDetailsElement) {
            more.open = Boolean(data.parcel || data.owner || data.subdivision || data.permit);
        }
    }

    function render(data, verificationMessage, shouldFocus = true, trackView = true) {
        if (!(result instanceof HTMLElement) || !(routeActions instanceof HTMLElement) || !(cluesList instanceof HTMLElement)) return;
        const route = routeFor(data);
        prepared = data;
        window.SepticRecordTask?.prepare({
            stateCode: "TN", stateName: "Tennessee", countyName: data.county.name,
            countyKey: `TN::${data.county.key}`, purpose: data.purpose,
            officeLabel: data.county.contract ? data.county.name : data.county.fieldOfficeName,
            routePath: window.location.pathname,
            routeMode: data.county.contract ? "contract_county" : "official_viewer",
            routeReliability: data.county.contract ? "county_owned" : "viewer_primary",
            officialRoute: route.url,
            requestRoute: data.county.contract ? data.county.recordsUrl : data.county.fieldOfficeUrl,
            requiredIdentifiers: ["Property address", "Parcel, prior owner, subdivision, or permit number if available"],
            requestedDocuments: ["construction permit", "soil evaluation", "system layout", "final approval", "repair history"]
        }, {address: data.address, identifierType: data.parcel ? "parcel" : data.permit ? "permit" : "address", identifierValue: data.parcel || data.permit || data.address, alternates: [data.owner, data.subdivision]});
        window.SepticRecordTask?.transition("route_ready", "route_ready");
        resultStatus.textContent = data.county.contract ? "Local county program" : "TDEC-managed county";
        resultTitle.textContent = `${data.county.name} record route`;
        resultSummary.textContent = data.county.contract
            ? "This county keeps its own septic workflow. The statewide viewer is not the right first step."
            : `TDEC routes this county through the ${data.county.fieldOfficeName} Environmental Field Office.`;
        routeTitle.textContent = route.title;
        routeExplanation.textContent = route.explanation;
        routeHint.textContent = route.hint;
        routeActions.replaceChildren(makeOfficialLink(route, data));
        if (!data.county.contract && data.purpose === "records") {
            routeActions.append(makeSecondaryLink(`Contact ${data.county.fieldOfficeName} if no file appears`, data.county.fieldOfficeUrl, "tdec_field_office_request", data));
        }
        if (!data.parcel) routeActions.append(makeSecondaryLink("Need a parcel ID? Open Tennessee Property Assessment Data", TPAD, "tn_property_assessment", data));

        cluesList.replaceChildren(...cluesFor(data).map(value => {
            const item = document.createElement("li");
            item.textContent = value;
            return item;
        }));
        verification.textContent = verificationMessage;
        result.hidden = false;
        returnPanel.hidden = true;
        requestSection.hidden = true;
        outcomeNext.hidden = true;
        saveSession(data, false);
        if (shouldFocus) {
            result.scrollIntoView({ behavior: window.matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth", block: "start" });
            result.focus({ preventScroll: true });
        }
        if (trackView) {
            emit("route_ready", {
                state_code: "TN",
                county_name: data.county.name,
                route_type: data.county.contract ? "contract_county" : "tdec",
                purpose: data.purpose
            });
            emit("county_route_viewed", {
                state_code: "TN",
                county_name: data.county.name,
                route_type: data.county.contract ? "contract_county" : "tdec",
                purpose: data.purpose
            });
        }
    }

    async function verifyAddressInBackground(data, revision) {
        if (!data.address) return;
        if (!addressLooksComplete(data.address)) {
            if (verification instanceof HTMLElement) verification.textContent = "Address was not verified. The route uses the county you selected.";
            emit("route_error", { error_type: "address_incomplete", state_code: "TN" });
            return;
        }
        const startedAt = performance.now();
        const controller = new AbortController();
        activeAddressVerification?.controller.abort();
        activeAddressVerification = {controller, revision};
        const timeout = window.setTimeout(() => controller.abort(), 4000);
        try {
            const verified = await verifyAddress(data, controller.signal);
            if (revision !== routeRevision) return;
            const countyChanged = prepared?.county?.key !== verified.data.county.key;
            if (countyChanged) {
                let officialAlreadyOpened = false;
                try {
                    officialAlreadyOpened = Boolean(JSON.parse(sessionStorage.getItem(SESSION_KEY) || "null")?.opened);
                } catch (_) {
                    // Keep the updated visible route even when storage is unavailable.
                }
                render(verified.data, verified.message, false, false);
                if (officialAlreadyOpened && returnPanel instanceof HTMLElement) {
                    returnPanel.hidden = false;
                    saveSession(verified.data, true);
                }
            } else {
                prepared = verified.data;
                if (verification instanceof HTMLElement) verification.textContent = verified.message;
                if (cluesList instanceof HTMLElement) {
                    cluesList.replaceChildren(...cluesFor(verified.data).map(value => {
                        const item = document.createElement("li");
                        item.textContent = value;
                        return item;
                    }));
                }
            }
            emit("address_verification_latency", {
                state_code: "TN",
                county_name: verified.data.county.name,
                status: "completed",
                value: Math.round(performance.now() - startedAt)
            });
        } catch (failure) {
            if (revision !== routeRevision) return;
            const timedOut = failure?.name === "AbortError";
            if (verification instanceof HTMLElement) {
                verification.textContent = timedOut
                    ? "Address verification took too long. The route still uses the county you selected."
                    : "Address verification did not complete. Confirm the selected county before relying on this route.";
            }
            emit("address_verification_latency", {
                state_code: "TN",
                county_name: data.county.name,
                status: timedOut ? "timeout" : "error",
                value: Math.round(performance.now() - startedAt)
            });
            emit("route_error", { error_type: timedOut ? "address_timeout" : "address_verification_failed", state_code: "TN" });
        } finally {
            window.clearTimeout(timeout);
            if (activeAddressVerification?.revision === revision) activeAddressVerification = null;
        }
    }

    function buildRequestText(data) {
        const lines = [
            `Please search for all available SSDS/septic system records for this property in ${data.county.name}.`,
            data.address ? `Property address: ${data.address}` : "",
            data.parcel ? `Parcel or tax-map ID: ${data.parcel}` : "",
            data.owner ? `Current or prior owner: ${data.owner}` : "",
            data.subdivision ? `Subdivision / lot: ${data.subdivision}` : "",
            data.permit ? `Permit number: ${data.permit}` : "",
            "",
            "Please include the original construction permit, soil map or evaluation, system layout/as-built, final inspection or Certificate of Completion, and any repair or modification records.",
            "If no file is located, please provide a written no-record response and identify any archive, delegated office, or pre-digital file location that should be checked."
        ];
        return lines.filter((line, index) => line || index > 5).join("\n").replace(/\n{3,}/g, "\n\n");
    }

    function showRequest(data, reason) {
        if (!(requestCopy instanceof HTMLTextAreaElement) || !(requestLink instanceof HTMLAnchorElement)) return;
        window.SepticRecordTask?.transition("request_prepared", reason);
        requestCopy.value = buildRequestText(data);
        requestGuidance.textContent = data.county.contract
            ? `${data.county.name} owns this record route. Use its county page or form with the wording below.`
            : `Start with the ${data.county.fieldOfficeName} Environmental Field Office. Use the formal TDEC public-records form only when appropriate.`;
        requestLink.href = data.county.contract ? data.county.recordsUrl : data.county.fieldOfficeUrl;
        requestLink.textContent = data.county.contract ? data.county.recordsLabel : `Open ${data.county.fieldOfficeName} Field Office`;
        requestSection.hidden = false;
        requestSection.scrollIntoView({ behavior: "smooth", block: "start" });
        emit("records_fallback_started", { fallback_type: reason, county_name: data.county.name, state_code: "TN" });
    }

    function outcomeContent(kind, data) {
        const wrapper = document.createElement("div");
        const title = document.createElement("strong");
        const description = document.createElement("p");
        const actions = document.createElement("div");
        actions.className = "tdec-return__actions";
        if (kind === "found") {
            title.textContent = "Verify the file before relying on it";
            description.textContent = "Match county, parcel or address, owner, permit number, and dates. Then check for layout, final approval, bedrooms, and repair history.";
            const link = document.createElement("a");
            link.className = "button button--secondary";
            link.href = "/septic-record-finder/?mode=document";
            link.textContent = "Add and review the official file";
            actions.append(link);
        } else if (kind === "empty" || kind === "blocked") {
            title.textContent = kind === "blocked" ? "Treat this as an access failure" : "Retry the prepared keys, then contact the office";
            description.textContent = kind === "blocked"
                ? "A 403 or failed page does not say anything about this property. Use the field office or local county route."
                : "Try street name, parcel, prior owner, subdivision, and permit number. If still empty, ask for archived and pre-digital files.";
            const button = document.createElement("button");
            button.className = "button button--secondary";
            button.type = "button";
            button.textContent = "Prepare office request";
            button.addEventListener("click", () => showRequest(data, kind === "blocked" ? "viewer_blocked" : "no_online_result"));
            actions.append(button);
        } else {
            title.textContent = "Keep the written response and clarify what was searched";
            description.textContent = "Ask whether prior owners, repair records, archived or pre-digital files, and any delegated local office were included. A missing permit is not proof of current system condition.";
            const button = document.createElement("button");
            button.className = "button button--secondary";
            button.type = "button";
            button.textContent = "Copy clarification request";
            button.addEventListener("click", () => showRequest(data, "written_no_record"));
            actions.append(button);
        }
        wrapper.append(title, description, actions);
        return wrapper;
    }

    form?.addEventListener("submit", event => {
        event.preventDefault();
        clearError();
        const data = values();
        if (!data.county) {
            showError("Choose the Tennessee county first.", county);
            emit("records_route_error", { error_type: "missing_county", state_code: "TN" });
            return;
        }
        const revision = ++routeRevision;
        emit("route_started", { state_code: "TN", county_name: data.county.name, purpose: data.purpose });
        render(data, data.address
            ? "Route ready. Address verification is continuing in the background."
            : "Route uses the county you selected. Add property keys only if they help the official search.");
        void verifyAddressInBackground(data, revision);
    });

    desk.querySelector("[data-tdec-hero-official]")?.addEventListener("click", () => {
        emit("hero_official_click", { state_code: "TN", destination_type: "tdec_ssds_record_search" });
    });

    county?.addEventListener("change", updateCountyHelp);
    form?.addEventListener("input", clearPreparedState);
    form?.addEventListener("change", clearPreparedState);
    desk.querySelector("[data-tdec-edit-search]")?.addEventListener("click", () => {
        clearPreparedState();
        form.scrollIntoView({ behavior: "smooth", block: "start" });
        county?.focus();
    });
    desk.querySelector("[data-tdec-copy-clues]")?.addEventListener("click", async event => {
        if (!prepared) return;
        try {
            await navigator.clipboard.writeText(cluesFor(prepared).join("\n"));
            event.currentTarget.textContent = "Copied";
        } catch (_) {
            emit("records_route_error", { error_type: "clipboard_unavailable", state_code: "TN" });
        }
    });
    desk.querySelector("[data-tdec-copy-request]")?.addEventListener("click", async event => {
        if (!(requestCopy instanceof HTMLTextAreaElement)) return;
        try {
            await navigator.clipboard.writeText(requestCopy.value);
            event.currentTarget.textContent = "Copied";
        } catch (_) {
            requestCopy.focus();
            requestCopy.select();
        }
    });
    desk.querySelectorAll("[data-tdec-outcome]").forEach(button => button.addEventListener("click", () => {
        if (!prepared || !(outcomeNext instanceof HTMLElement)) return;
        desk.querySelectorAll("[data-tdec-outcome]").forEach(candidate => candidate.setAttribute("aria-pressed", String(candidate === button)));
        const taskState = {empty: "not_found_online", blocked: "blocked", no_record: "no_record_response"}[button.dataset.tdecOutcome];
        if (taskState) window.SepticRecordTask?.transition(taskState, button.dataset.tdecOutcome);
        outcomeNext.replaceChildren(outcomeContent(button.dataset.tdecOutcome, prepared));
        outcomeNext.hidden = false;
        emit("tdec_outcome_selected", { outcome: button.dataset.tdecOutcome, county_name: prepared.county.name, state_code: "TN" });
    }));

    window.addEventListener("focus", () => {
        if (prepared && returnPanel instanceof HTMLElement) {
            try {
                const state = JSON.parse(sessionStorage.getItem(SESSION_KEY) || "null");
                if (state?.opened) returnPanel.hidden = false;
            } catch (_) {
                // No saved handoff state.
            }
        }
    });

    try {
        const restored = JSON.parse(sessionStorage.getItem(SESSION_KEY) || "null");
        if (restored?.county?.key && Date.now() - restored.savedAt < 2 * 60 * 60 * 1000) {
            if (county instanceof HTMLSelectElement) county.value = restored.county.key;
            const restoredData = { ...restored, county: selectedCounty() };
            if (restoredData.county) {
                restoreForm(restoredData);
                render(restoredData, "Restored from this browser tab. Confirm the property keys before continuing.", false, false);
                if (restored.opened) returnPanel.hidden = false;
            }
        }
    } catch (_) {
        sessionStorage.removeItem(SESSION_KEY);
    }
    updateCountyHelp();
})();
