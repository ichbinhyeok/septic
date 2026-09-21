(function () {
    "use strict";

    const root = document.querySelector("[data-institution-records]");
    if (!root) return;

    const form = root.querySelector("[data-institution-form]");
    const result = root.querySelector("[data-institution-result]");
    if (!form || !result) return;

    const fields = {
        address: form.querySelector("[data-institution-address]"),
        area: form.querySelector("[data-institution-area]"),
        parcel: form.querySelector("[data-institution-parcel]"),
        owner: form.querySelector("[data-institution-owner]"),
        year: form.querySelector("[data-institution-year]")
    };
    const error = form.querySelector("[data-institution-error]");
    const locationStatus = form.querySelector("[data-institution-location-status]");
    const locationMessage = form.querySelector("[data-institution-location-message]");
    const locationAction = form.querySelector("[data-institution-location-action]");
    const submitButton = form.querySelector('button[type="submit"]');
    const title = result.querySelector("[data-institution-result-title]");
    const kicker = result.querySelector("[data-institution-result-kicker]");
    const summary = result.querySelector("[data-institution-result-summary]");
    const clueList = result.querySelector("[data-institution-clues]");
    const custodian = result.querySelector("[data-institution-custodian]");
    const routeNote = result.querySelector("[data-institution-route-note]");
    const actionHeading = result.querySelector("[data-institution-action-heading]");
    const actionCopy = result.querySelector("[data-institution-action-copy]");
    const primaryRoute = result.querySelector("[data-institution-primary-route]");
    const secondaryRoute = result.querySelector("[data-institution-secondary-route]");
    const artifactHeading = result.querySelector("[data-institution-artifact-heading]");
    const artifactTitle = result.querySelector("[data-institution-artifact-title]");
    const artifactLabel = result.querySelector("[data-institution-artifact-label]");
    const request = result.querySelector("[data-institution-request]");
    const agency = root.dataset.agency || "the responsible agency";
    const program = root.dataset.program || "onsite wastewater program";
    const subject = root.dataset.requestSubject || "Septic records for the property";
    const areaLabel = fields.area ? fields.area.closest("label").childNodes[0].textContent.trim() : "Locality";
    const areaIsCounty = areaLabel.toLowerCase().includes("county");

    const routes = Array.from(root.querySelectorAll("[data-institution-route-rule]")).map((node) => ({
        key: node.dataset.routeKey || "default",
        areaMatches: (node.dataset.areaMatches || "").split("|").filter(Boolean),
        requesterRelation: node.dataset.requesterRelation || "",
        minimumYear: node.dataset.minimumYear ? Number(node.dataset.minimumYear) : null,
        maximumYear: node.dataset.maximumYear ? Number(node.dataset.maximumYear) : null,
        custodian: node.dataset.custodian || root.dataset.requestRecipient || "the responsible records office",
        note: node.dataset.routeNote || "",
        primaryUrl: node.dataset.primaryUrl || "",
        primaryLabel: node.dataset.primaryLabel || "Open the official route",
        secondaryUrl: node.dataset.secondaryUrl || "",
        secondaryLabel: node.dataset.secondaryLabel || "Review the fallback route",
        recipient: node.dataset.requestRecipient || root.dataset.requestRecipient || "the responsible records office",
        contactMode: node.dataset.contactMode || "request",
        artifactHeading: node.dataset.artifactHeading || "Ask for the complete property file.",
        artifactLabel: node.dataset.artifactLabel || "Request draft"
    }));

    const value = (field) => field && field.value ? field.value.trim() : "";
    const normalize = (text) => (text || "").toLowerCase().replace(/[^a-z0-9]+/g, " ").trim();
    const selectedStatus = () => {
        const selected = form.querySelector('input[name="status"]:checked');
        return selected ? selected.value : "not_started";
    };
    const selectedRelation = () => {
        const selected = form.querySelector('input[name="requester_relation"]:checked');
        return selected ? selected.value : "";
    };

    function resolveRoute() {
        const location = normalize(value(fields.area) || value(fields.address));
        const relation = selectedRelation();
        const year = /^\d{4}$/.test(value(fields.year)) ? Number(value(fields.year)) : null;
        const eligible = routes.filter((route) => !route.requesterRelation || route.requesterRelation === relation);
        const matchesPlace = (route) => !route.areaMatches.length || route.areaMatches.some((match) => location.includes(normalize(match)));
        const matchesYear = (route) => year !== null
            && (route.minimumYear === null || year >= route.minimumYear)
            && (route.maximumYear === null || year <= route.maximumYear);
        return eligible.find((route) => matchesPlace(route) && (route.minimumYear !== null || route.maximumYear !== null) && matchesYear(route))
            || eligible.find((route) => route.areaMatches.length && route.minimumYear === null && route.maximumYear === null && matchesPlace(route))
            || eligible.find((route) => !route.areaMatches.length && route.requesterRelation === relation && relation)
            || eligible.find((route) => !route.areaMatches.length && !route.requesterRelation && route.minimumYear === null && route.maximumYear === null)
            || routes[0]
            || {
                key: "fallback", custodian: root.dataset.requestRecipient, note: "", primaryUrl: "", primaryLabel: "Open the official route",
                secondaryUrl: "", secondaryLabel: "Review the fallback route", recipient: root.dataset.requestRecipient,
                contactMode: "request", artifactHeading: "Ask for the complete property file.", artifactLabel: "Request draft"
            };
    }

    function clues() {
        return [
            ["Property address", value(fields.address)],
            [fields.area ? fields.area.closest("label").childNodes[0].textContent.trim() : "Locality", value(fields.area)],
            [fields.parcel ? fields.parcel.closest("label").childNodes[0].textContent.trim() : "Parcel", value(fields.parcel)],
            ["Owner or applicant", value(fields.owner)],
            ["Year clue", value(fields.year)]
        ].filter((entry) => entry[1]);
    }

    function statusContent(status, route) {
        const modeHeading = route.contactMode === "call"
            ? "Call the office that holds the file"
            : route.contactMode === "search"
                ? "Search the correct official system"
                : "Open the correct official request route";
        if (status === "no_result") {
            return {
                kicker: "Search recovery prepared",
                title: "An empty search is not the end of this record trail.",
                summary: `The working custodian is ${route.custodian}. Use the fallback below before treating the record as missing.`,
                heading: route.contactMode === "call" ? "Move from the empty search to the file owner" : "Retry once, then use the documented fallback",
                copy: route.note
            };
        }
        if (status === "incomplete") {
            return {
                kicker: "File gap identified",
                title: `Complete the file with ${route.custodian}.`,
                summary: "Keep the document you found, then name the missing plan, inspection, approval, repair, or operating record.",
                heading: "Return to the actual custodian",
                copy: route.note
            };
        }
        if (status === "have_file") {
            return {
                kicker: "File review route",
                title: "Turn the file into a usable property answer.",
                summary: "Preserve the original pages, confirm the property match, and separate historical approval from present condition.",
                heading: "Confirm completeness with the custodian",
                copy: route.note
            };
        }
        return {
            kicker: "Custodian resolved",
            title: `Start with ${route.custodian}.`,
            summary: "This route is based on the property location and the record-custody rules published for this state.",
            heading: modeHeading,
            copy: route.note
        };
    }

    function documentNames() {
        return Array.from(result.querySelectorAll(".institution-result__steps > li:nth-child(3) li"))
            .map((item) => item.textContent.trim())
            .filter(Boolean);
    }

    function requestText(items, status, route) {
        const lines = items.map((entry) => `${entry[0]}: ${entry[1]}`);
        const situation = {
            no_result: "I searched the public route but did not find a usable property record.",
            incomplete: "I located a partial record, but the property file appears incomplete.",
            have_file: "I have received part of the property file and would like to confirm whether the complete record is included.",
            not_started: "I am trying to obtain the onsite wastewater records for this property."
        }[status] || "I am trying to obtain the onsite wastewater records for this property.";
        const documents = documentNames();

        if (route.contactMode === "call") {
            return [
                `Call: ${route.recipient}`,
                "",
                "I’m calling to locate the onsite wastewater permit file for a property.",
                "",
                ...lines,
                "",
                "Could the Onsite Environmental Specialist check for:",
                ...documents.map((document) => `- ${document}`),
                "",
                "If there is no match, could you also check the build year, subdivision and lot, and original owner or developer—or tell me which office holds the older file?"
            ].join("\n");
        }

        const opening = route.contactMode === "search"
            ? "I searched the official record system and am following up because the result was missing or incomplete."
            : situation;
        return [
            `Subject: ${subject}`,
            "",
            `Hello ${route.recipient},`,
            "",
            opening,
            "",
            ...lines,
            "",
            `Please search the ${program} file and provide any available records, including:`,
            ...documents.map((document) => `- ${document}`),
            "",
            "If no responsive record is located, please let me know whether an older, paper, regional, municipal, or differently indexed file should be checked and which office would hold it.",
            "",
            "Thank you."
        ].join("\n");
    }

    function setRouteLink(link, url, label) {
        if (!link) return;
        link.href = url;
        link.textContent = `${label} ↗`;
    }

    function showLocationStatus(message, status, action) {
        if (!locationStatus) return;
        if (locationMessage) locationMessage.textContent = message;
        locationStatus.dataset.status = status;
        locationStatus.hidden = false;
        if (locationAction) {
            locationAction.hidden = !action;
            if (action) {
                locationAction.href = action.href;
                locationAction.firstChild.textContent = action.label + " ";
            }
        }
    }

    function trackLocationResolution(resolution) {
        if (typeof window.gtag !== "function") return;
        window.gtag("event", "institution_jurisdiction_resolution", {
            state_code: root.dataset.stateCode,
            resolution_outcome: resolution.outcome,
            matched_state_code: resolution.matchedState || "",
            has_area_value: Boolean(value(fields.area))
        });
    }

    async function resolveOfficialLocation() {
        const address = value(fields.address);
        try {
            const response = await fetch("/api/address-record-finder", {
                method: "POST",
                headers: { "Content-Type": "application/json", Accept: "application/json" },
                body: JSON.stringify({ address })
            });
            const payload = await response.json();
            if (payload.stateCode && payload.stateCode !== root.dataset.stateCode) {
                showLocationStatus(
                    `This address resolves to ${payload.countyName || "another county"}, ${payload.stateCode}, not ${root.dataset.stateCode}. Open the records page for the matching state before continuing.`,
                    "error",
                    {
                        href: payload.routePath || "/septic-records-by-county/",
                        label: `Continue to ${payload.stateName || payload.stateCode} records`
                    }
                );
                return { blocking: true, outcome: "state_mismatch", matchedState: payload.stateCode };
            }
            if (payload.countyName) {
                const county = payload.countyName.toLowerCase().endsWith("county")
                    ? payload.countyName
                    : `${payload.countyName} County`;
                if (areaIsCounty && fields.area) fields.area.value = county;
                showLocationStatus(
                    areaIsCounty
                        ? `County confirmed from the address: ${county}. The route below uses this jurisdiction.`
                        : `Address matched in ${county}. Your town entry is preserved because this state routes some records by municipality.`,
                    "matched"
                );
                return { blocking: false, outcome: "matched", matchedState: payload.stateCode };
            }
        } catch (ignored) {
            // The manual locality field remains the resilient fallback.
        }

        if (areaIsCounty && !value(fields.area)) {
            showLocationStatus(`The county could not be confirmed automatically. Enter the ${areaLabel.toLowerCase()} so the custodian is not guessed.`, "error");
            fields.area.focus();
            return { blocking: true, outcome: "manual_required", matchedState: "" };
        }
        showLocationStatus(`The official county check is temporarily unavailable. The route below uses the ${areaLabel.toLowerCase()} you entered.`, "warning");
        return { blocking: false, outcome: "manual_fallback", matchedState: "" };
    }

    async function copyText(text, button, doneLabel) {
        if (!text) return;
        try {
            await navigator.clipboard.writeText(text);
        } catch (ignored) {
            const helper = document.createElement("textarea");
            helper.value = text;
            helper.setAttribute("readonly", "");
            helper.style.position = "fixed";
            helper.style.opacity = "0";
            document.body.appendChild(helper);
            helper.select();
            document.execCommand("copy");
            helper.remove();
        }
        const previous = button.textContent;
        button.textContent = doneLabel;
        window.setTimeout(() => { button.textContent = previous; }, 1800);
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();
        if (!value(fields.address)) {
            error.hidden = false;
            fields.address.focus();
            return;
        }
        error.hidden = true;
        const defaultSubmitContent = submitButton ? submitButton.innerHTML : "";
        if (submitButton) {
            submitButton.disabled = true;
            submitButton.textContent = "Confirming jurisdiction...";
        }
        const locationResolution = await resolveOfficialLocation();
        trackLocationResolution(locationResolution);
        if (submitButton) {
            submitButton.disabled = false;
            submitButton.innerHTML = defaultSubmitContent;
        }
        if (locationResolution.blocking) return;
        const items = clues();
        const route = resolveRoute();
        clueList.replaceChildren();
        items.forEach((entry) => {
            const li = document.createElement("li");
            const strong = document.createElement("strong");
            strong.textContent = `${entry[0]}: `;
            li.append(strong, document.createTextNode(entry[1]));
            clueList.appendChild(li);
        });

        const status = selectedStatus();
        const copy = statusContent(status, route);
        kicker.textContent = copy.kicker;
        title.textContent = copy.title;
        summary.textContent = copy.summary;
        custodian.textContent = route.custodian;
        routeNote.textContent = route.note;
        actionHeading.textContent = copy.heading;
        actionCopy.textContent = copy.copy;
        setRouteLink(primaryRoute, route.primaryUrl, route.primaryLabel);
        setRouteLink(secondaryRoute, route.secondaryUrl, route.secondaryLabel);
        artifactHeading.textContent = route.artifactHeading;
        artifactTitle.textContent = route.artifactHeading;
        artifactLabel.textContent = route.artifactLabel;
        request.value = requestText(items, status, route);
        result.hidden = false;
        result.scrollIntoView({ behavior: window.matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth", block: "start" });
        window.setTimeout(() => title.focus({ preventScroll: true }), 350);

        if (typeof window.gtag === "function") {
            window.gtag("event", "institution_record_route_prepared", {
                state_code: root.dataset.stateCode,
                agency: agency,
                route_status: status,
                route_key: route.key,
                contact_mode: route.contactMode,
                location_resolution: locationResolution.outcome,
                has_parcel: Boolean(value(fields.parcel)),
                has_owner: Boolean(value(fields.owner)),
                has_year: Boolean(value(fields.year))
            });
        }
    });

    result.querySelector("[data-institution-edit]").addEventListener("click", function () {
        form.scrollIntoView({ behavior: "smooth", block: "center" });
        fields.address.focus({ preventScroll: true });
    });

    result.querySelector("[data-institution-copy-clues]").addEventListener("click", function () {
        copyText(clues().map((entry) => `${entry[0]}: ${entry[1]}`).join("\n"), this, "Match keys copied");
    });

    result.querySelector("[data-institution-copy-request]").addEventListener("click", function () {
        copyText(request.value, this, "Wording copied");
    });
})();
