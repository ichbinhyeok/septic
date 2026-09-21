/* Preview adapter of the production checker decision logic. No tracking, storage or network calls. */
(() => {
    const copyText = text => navigator.clipboard.writeText(text);
    function downloadText(filename, text) {
        const url = URL.createObjectURL(new Blob([text], { type: 'text/plain;charset=utf-8' }));
        const link = document.createElement('a'); link.href = url; link.download = filename;
        document.body.append(link); link.click(); link.remove();
        setTimeout(() => URL.revokeObjectURL(url), 1000);
    }
    function setupBedroomPermitCheckers() {
        const checkers = Array.from(document.querySelectorAll("[data-bedroom-permit-checker]"));
        if (!checkers.length) {
            return;
        }

        const stateRoutes = {
            TN: "/septic-records-checklist/tennessee/",
            NC: "/septic-records-checklist/north-carolina/",
            IN: "/septic-records-checklist/indiana/",
            SC: "/septic-records-checklist/south-carolina/"
        };

        function countLabel(value) {
            return value ? `${value} bedroom${value === 1 ? "" : "s"}` : "not confirmed";
        }

        function recordRoute(stateCode) {
            return stateRoutes[stateCode] || "/septic-records-by-county/";
        }

        checkers.forEach((checker) => {
            const form = checker.querySelector("[data-bedroom-permit-form]");
            const state = checker.querySelector("[data-bedroom-state]");
            const listingCount = checker.querySelector("[data-bedroom-listing-count]");
            const permitCount = checker.querySelector("[data-bedroom-permit-count]");
            const fileStatus = checker.querySelector("[data-bedroom-file-status]");
            const address = checker.querySelector("[data-bedroom-address]");
            const result = checker.querySelector("[data-bedroom-result]");
            const label = checker.querySelector("[data-bedroom-result-label]");
            const heading = checker.querySelector("[data-bedroom-result-heading]");
            const body = checker.querySelector("[data-bedroom-result-body]");
            const facts = checker.querySelector("[data-bedroom-result-facts]");
            const steps = checker.querySelector("[data-bedroom-result-steps]");
            const note = checker.querySelector("[data-bedroom-result-note]");
            const copyButton = checker.querySelector("[data-bedroom-copy]");
            const downloadButton = checker.querySelector("[data-bedroom-download]");
            const routeLink = checker.querySelector("[data-track-source-context='bedroom_checker_result_address']");
            const requestLink = checker.querySelector("[data-track-source-context='bedroom_checker_result_request']");

            if (!(form instanceof HTMLFormElement)
                || !(state instanceof HTMLSelectElement)
                || !(listingCount instanceof HTMLInputElement)
                || !(permitCount instanceof HTMLSelectElement)
                || !(fileStatus instanceof HTMLSelectElement)
                || !(result instanceof HTMLElement)) {
                return;
            }

            const requestedState = new URLSearchParams(window.location.search).get("state")?.trim().toUpperCase();
            if (requestedState && Array.from(state.options).some((option) => option.value === requestedState)) {
                state.value = requestedState;
            }

            function current() {
                const listing = Math.max(1, Math.min(12, Number.parseInt(listingCount.value, 10) || 0));
                const permit = Number.parseInt(permitCount.value, 10) || 0;
                const status = fileStatus.value;
                const stateLabel = state.options[state.selectedIndex]?.textContent?.trim() || "this state";
                const property = address instanceof HTMLInputElement ? address.value.trim() : "";
                const official = status === "official";
                const hasConflict = status === "conflicting";
                const missing = status === "missing" || !permit || !official;
                let kind = "unverified";
                let nextHeading = "The septic bedroom count is not verified yet";
                let nextBody = "Do not treat the listing count as a septic-capacity answer until an official permit, approval, or county response identifies the number supported by the file.";
                let nextSteps = [
                    "Open the official records path and search by address, parcel, owner, subdivision, or permit number.",
                    "Ask for the permit or approval that states the supported bedroom count or design flow.",
                    "Keep the written response with the buyer, listing, inspection, or lender file before changing the transaction story."
                ];

                if (official && permit && listing > permit) {
                    kind = "mismatch";
                    nextHeading = "The listing count is higher than the reviewed septic permit count";
                    nextBody = "This is a transaction-critical file mismatch. It does not decide legal compliance by itself, but the listing, buyer, lender, and inspection conversation should not treat the extra room capacity as cleared until the responsible local source explains the file.";
                    nextSteps = [
                        "Keep the official permit or approval copy that shows the lower bedroom count.",
                        "Request the as-built, final approval, repair or expansion history, and written local guidance for this parcel.",
                        "Have the responsible broker, county office, inspector, lender, or qualified local professional determine the required transaction response."
                    ];
                } else if (official && permit && listing === permit && !hasConflict) {
                    kind = "aligned";
                    nextHeading = "The reviewed permit count matches the listing count";
                    nextBody = "The two counts align, which is a useful file signal. Keep the permit copy and still check final approval, layout, repair history, and current inspection needs before relying on the record for a closing or project decision.";
                    nextSteps = [
                        "Save the permit or approval copy with the transaction file.",
                        "Check for final approval, operation record, layout, repair history, or inspection requirements.",
                        "Use the county or state records route again if the address, owner, or permit number does not match cleanly."
                    ];
                } else if (official && permit && listing < permit && !hasConflict) {
                    kind = "under_listed";
                    nextHeading = "The reviewed permit count is higher than the listing count";
                    nextBody = "The listing is below the reviewed permit count. That does not prove every room, improvement, or current condition is settled, so preserve the permit and verify the rest of the septic file before treating the property story as complete.";
                    nextSteps = [
                        "Keep the reviewed permit or approval in the transaction file.",
                        "Check final approval, layout, repair history, and any later additions or conversions.",
                        "Ask the file owner for written guidance if the tax, seller, or listing record conflicts with the permit."
                    ];
                } else if (hasConflict) {
                    kind = "conflict";
                    nextHeading = "The property records conflict, so the septic file needs a written resolution";
                    nextBody = "A seller statement, tax card, listing, or permit may be using different bedroom information. Keep each source, then ask the record owner which approval governs the parcel and whether another permit, amendment, or archive file exists.";
                    nextSteps = [
                        "Save the conflicting listing, tax, seller, and permit information together.",
                        "Ask the official file owner for the controlling permit, approval, amendment, or written no-record response.",
                        "Do not resolve the conflict by selecting the most convenient number for a transaction."
                    ];
                }

                const subject = `${stateLabel} septic bedroom-capacity file check${property ? `: ${property}` : ""}`;
                const noteLines = [
                    `Subject: ${subject}`,
                    "",
                    `Property: ${property || "[property address / parcel]"}`,
                    `State: ${stateLabel}`,
                    `Listing bedrooms: ${countLabel(listing)}`,
                    `Entered septic permit bedrooms: ${countLabel(permit)}`,
                    `File status: ${fileStatus.options[fileStatus.selectedIndex]?.textContent?.trim() || "Not stated"}`,
                    "",
                    `File check: ${nextHeading}`,
                    nextBody,
                    "",
                    "Requested next records:",
                    ...nextSteps.map((item, index) => `${index + 1}. ${item}`),
                    "",
                    "Please provide the septic permit or approval, approved bedroom or design-flow count, final approval or operation record, as-built or layout, repair/expansion history, and written direction if another office owns the file.",
                    "",
                    "This note flags a records question only. It is not an engineering, permit, MLS, lender, or legal compliance determination."
                ];

                return { listing, permit, stateLabel, property, kind, nextHeading, nextBody, nextSteps, noteText: noteLines.join("\n"), route: recordRoute(state.value) };
            }

            function fitNote() {
                if (!note || result.hidden) return;
                note.style.height = 'auto';
                note.style.height = (note.scrollHeight + 2) + 'px';
            }
            let previousWidth = 0;
            if (note) new ResizeObserver(entries => {
                const width = entries[0].contentRect.width;
                if (Math.abs(width - previousWidth) > 1) { previousWidth = width; fitNote(); }
            }).observe(note);
            document.fonts.ready.then(fitNote);
            function render() {
                const value = current();
                result.hidden = false;
                result.dataset.bedroomCheckKind = value.kind;
                if (label) {
                    label.textContent = `Your comparison / ${value.stateLabel}`;
                }
                const summaries = {
                    mismatch: ['The listing count is higher.', 'Ask the responsible office to explain the difference before relying on the extra bedroom count. This comparison is not a compliance decision.'],
                    aligned: ['The entered counts match.', 'Matching counts do not establish approval or current condition. Keep the permit and check the rest of the file.'],
                    under_listed: ['The listing count is lower.', 'A higher permit count is not permission to add or market bedrooms. Confirm the applicable approval and current property details.'],
                    unverified: ['The permit count is unconfirmed.', 'Find the official approval before treating the listing count as a septic-capacity answer.'],
                    conflict: ['The records need clarification.', 'Keep the conflicting sources and ask the official file owner which approval applies. No reliable difference is shown yet.']
                };
                if (heading) {
                    heading.textContent = summaries[value.kind][0];
                }
                if (body) {
                    body.textContent = summaries[value.kind][1];
                }
                if (facts) {
                    const comparable = ['mismatch', 'aligned', 'under_listed'].includes(value.kind);
                    const difference = value.listing - value.permit;
                    const items = [
                        ['Listing', String(value.listing), 'bedrooms entered'],
                        ['Permit', value.permit ? String(value.permit) : '—', comparable ? 'bedrooms entered' : 'not confirmed'],
                        ['Difference', comparable ? (difference > 0 ? '+' : '') + difference : '—', comparable ? 'listing minus permit' : 'check the source first']
                    ];
                    facts.replaceChildren(...items.map((item) => {
                        const fact = document.createElement('div');
                        const term = document.createElement('dt'); term.textContent = item[0];
                        const number = document.createElement('dd'); number.textContent = item[1];
                        const detail = document.createElement('span'); detail.textContent = item[2];
                        fact.append(term, number, detail);
                        return fact;
                    }));
                }
                if (steps) {
                    steps.replaceChildren(...value.nextSteps.map((step) => {
                        const item = document.createElement("li");
                        item.textContent = step;
                        return item;
                    }));
                }
                if (note instanceof HTMLTextAreaElement) {
                    note.value = value.noteText;
                }
                if (routeLink instanceof HTMLAnchorElement) {
                    routeLink.href = '/design-preview/studio/tools/';
                    if (window.self !== window.top) {
                        routeLink.target = "_blank";
                        routeLink.rel = "noreferrer";
                    }
                }
                if (requestLink instanceof HTMLAnchorElement && window.self !== window.top) {
                    requestLink.target = "_blank";
                    requestLink.rel = "noreferrer";
                }
                fitNote();
                heading?.focus({ preventScroll: true });
                result.scrollIntoView({ behavior: matchMedia('(prefers-reduced-motion: reduce)').matches ? 'instant' : 'smooth', block: 'start' });
            }

            form.addEventListener('input', () => { result.hidden = true; });
            form.addEventListener('change', () => { result.hidden = true; });
            form.addEventListener("submit", (event) => {
                event.preventDefault();
                if (!form.reportValidity()) return;
                render();            });

            if (copyButton instanceof HTMLButtonElement && note instanceof HTMLTextAreaElement) {
                copyButton.addEventListener("click", async () => {
                    const original = copyButton.textContent;
                    try {
                        await copyText(note.value);                        copyButton.textContent = "Transaction note copied";
                        copyButton.classList.add("is-copied");
                    } catch (_) {
                        note.focus(); note.select();
                        copyButton.textContent = "Select and copy the note";
                        copyButton.classList.add("is-copy-failed");
                    }
                    window.setTimeout(() => {
                        copyButton.textContent = original;
                        copyButton.classList.remove("is-copied", "is-copy-failed");
                    }, 1800);
                });
            }

            if (downloadButton instanceof HTMLButtonElement && note instanceof HTMLTextAreaElement) {
                downloadButton.addEventListener("click", () => {
                    const statePart = state.value ? state.value.toLowerCase() : "state";
                    downloadText(`septic-bedroom-file-check-${statePart}.txt`, note.value);                    const original = downloadButton.textContent;
                    downloadButton.textContent = "Downloaded";
                    downloadButton.classList.add("is-copied");
                    window.setTimeout(() => {
                        downloadButton.textContent = original;
                        downloadButton.classList.remove("is-copied");
                    }, 1800);
                });
            }
        });
    }

    setupBedroomPermitCheckers();

})();
