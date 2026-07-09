(() => {
    document.documentElement.classList.add("js");

    function sameOriginPath(anchor) {
        try {
            const url = new URL(anchor.href, window.location.origin);
            if (url.origin !== window.location.origin) {
                return null;
            }
            return url.pathname + url.search + url.hash;
        } catch (_error) {
            return null;
        }
    }

    function sendNavigationEvent(payload) {
        const body = JSON.stringify(payload);
        const endpoint = "/events/nav-click";

        if (navigator.sendBeacon) {
            const blob = new Blob([body], { type: "application/json" });
            navigator.sendBeacon(endpoint, blob);
            return;
        }

        fetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body,
            keepalive: true
        }).catch(() => {});
    }

    function setupSiteNav() {
        const header = document.querySelector(".site-header");
        const toggle = document.querySelector("[data-site-nav-toggle]");
        const nav = document.getElementById("site-nav-menu");

        if (!header || !toggle || !nav || !window.matchMedia) {
            return;
        }

        const mobileQuery = window.matchMedia("(max-width: 720px)");

        function setExpanded(expanded) {
            toggle.setAttribute("aria-expanded", String(expanded));
            toggle.setAttribute("aria-label", expanded ? "Close navigation menu" : "Open navigation menu");

            if (expanded) {
                header.setAttribute("data-nav-open", "true");
                return;
            }

            header.removeAttribute("data-nav-open");
        }

        function closeIfMobile() {
            if (mobileQuery.matches) {
                setExpanded(false);
            }
        }

        toggle.addEventListener("click", () => {
            setExpanded(toggle.getAttribute("aria-expanded") !== "true");
        });

        nav.querySelectorAll("a").forEach((link) => {
            link.addEventListener("click", closeIfMobile);
        });

        document.addEventListener("click", (event) => {
            if (toggle.getAttribute("aria-expanded") !== "true") {
                return;
            }

            if (event.target.closest(".site-nav") || event.target.closest("[data-site-nav-toggle]")) {
                return;
            }

            closeIfMobile();
        });

        document.addEventListener("keydown", (event) => {
            if (event.key === "Escape") {
                closeIfMobile();
            }
        });

        const handleViewportChange = (event) => {
            if (!event.matches) {
                setExpanded(false);
            }
        };

        if (typeof mobileQuery.addEventListener === "function") {
            mobileQuery.addEventListener("change", handleViewportChange);
        } else if (typeof mobileQuery.addListener === "function") {
            mobileQuery.addListener(handleViewportChange);
        }

        setExpanded(false);
    }

    setupSiteNav();

    function setupStickyMobileCtas() {
        const stickyCtas = Array.from(document.querySelectorAll("[data-sticky-mobile-cta]"));
        if (!stickyCtas.length || !window.matchMedia) {
            return;
        }
        document.body.classList.add("has-sticky-mobile-cta");

        const mobileQuery = window.matchMedia("(max-width: 720px)");
        const updates = [];

        function setVisible(stickyCta, visible) {
            stickyCta.classList.toggle("is-visible", mobileQuery.matches && visible);
        }

        function installTracker(stickyCta, anchor) {
            const update = () => {
                if (anchor) {
                    const rect = anchor.getBoundingClientRect();
                    const revealLine = window.innerHeight - 88;
                    setVisible(stickyCta, rect.top <= revealLine);
                    return;
                }

                const threshold = Math.min(window.innerHeight * 0.7, 420);
                setVisible(stickyCta, window.scrollY > threshold);
            };

            window.addEventListener("scroll", update, { passive: true });
            window.addEventListener("resize", update);
            update();
            updates.push({ stickyCta, update });
        }

        stickyCtas.forEach((stickyCta) => {
            const selector = stickyCta.dataset.showAfter;
            const anchor = selector ? document.querySelector(selector) : null;
            installTracker(stickyCta, anchor);
        });

        const handleViewportChange = (event) => {
            if (event.matches) {
                updates.forEach(({ update }) => update());
                return;
            }

            stickyCtas.forEach((stickyCta) => setVisible(stickyCta, false));
        };

        if (typeof mobileQuery.addEventListener === "function") {
            mobileQuery.addEventListener("change", handleViewportChange);
        } else if (typeof mobileQuery.addListener === "function") {
            mobileQuery.addListener(handleViewportChange);
        }

        handleViewportChange(mobileQuery);
    }

    setupStickyMobileCtas();

    function setupChoiceGroups() {
        const groups = Array.from(document.querySelectorAll("[data-choice-group]"));
        if (!groups.length) {
            return;
        }

        groups.forEach((group) => {
            const buttons = Array.from(group.querySelectorAll("[data-choice]"));
            const panels = Array.from(group.querySelectorAll("[data-choice-panel]"));

            if (!buttons.length || !panels.length) {
                return;
            }

            function activate(choice) {
                buttons.forEach((button) => {
                    const active = button.dataset.choice === choice;
                    button.classList.toggle("is-active", active);
                    button.setAttribute("aria-pressed", String(active));
                });

                panels.forEach((panel) => {
                    const active = panel.dataset.choicePanel === choice;
                    panel.hidden = !active;
                    panel.classList.toggle("is-active", active);
                });
            }

            buttons.forEach((button) => {
                button.addEventListener("click", () => {
                    activate(button.dataset.choice);
                });
            });

            const initialChoice = buttons.find((button) => button.classList.contains("is-active"))?.dataset.choice
                || buttons[0].dataset.choice;
            activate(initialChoice);
        });
    }

    setupChoiceGroups();

    function setupCountyFinders() {
        const finders = Array.from(document.querySelectorAll("[data-county-finder]"));
        if (!finders.length) {
            return;
        }

        const normalize = (value) => (value || "")
            .toLowerCase()
            .replace(/[^a-z0-9]+/g, " ")
            .trim();

        finders.forEach((finder) => {
            const input = finder.querySelector("[data-county-finder-input]");
            const clear = finder.querySelector("[data-county-finder-clear]");
            const results = Array.from(finder.querySelectorAll("[data-county-finder-result]"));
            const count = finder.querySelector("[data-county-finder-count]");
            const empty = finder.querySelector("[data-county-finder-empty]");
            const methodFilter = finder.querySelector("[data-county-finder-method]");
            const artifactFilter = finder.querySelector("[data-county-finder-artifact]");
            const confidenceFilter = finder.querySelector("[data-county-finder-confidence]");
            const parcelFilter = finder.querySelector("[data-county-finder-parcel]");

            if (!input || !results.length) {
                return;
            }

            function matchesMethod(result, selectedMethod) {
                if (!selectedMethod) {
                    return true;
                }

                const method = normalize(result.dataset.method);
                if (selectedMethod === "online") {
                    return /\b(online|portal|search|lookup|gis)\b/.test(method);
                }
                if (selectedMethod === "request") {
                    return /\b(request|form|email|written|copy)\b/.test(method);
                }
                if (selectedMethod === "office") {
                    return /\b(office|phone|contact|department|county)\b/.test(method);
                }
                return true;
            }

            function matchesArtifact(result, selectedArtifact) {
                if (!selectedArtifact) {
                    return true;
                }

                const artifact = normalize(result.dataset.artifact);
                if (selectedArtifact === "as-built") {
                    return artifact.includes("as built") || artifact.includes("layout") || artifact.includes("site sketch");
                }
                if (selectedArtifact === "no-record") {
                    return artifact.includes("no record") || artifact.includes("missing") || artifact.includes("written response");
                }
                return artifact.includes(normalize(selectedArtifact));
            }

            function matchesConfidence(result, selectedConfidence) {
                if (!selectedConfidence) {
                    return true;
                }

                const score = Number(result.dataset.confidenceScore || 0);
                if (selectedConfidence === "high") {
                    return score >= 82;
                }
                if (selectedConfidence === "solid") {
                    return score >= 70;
                }
                return true;
            }

            function updateResults() {
                const query = normalize(input.value);
                const selectedMethod = methodFilter instanceof HTMLSelectElement ? methodFilter.value : "";
                const selectedArtifact = artifactFilter instanceof HTMLSelectElement ? artifactFilter.value : "";
                const selectedConfidence = confidenceFilter instanceof HTMLSelectElement ? confidenceFilter.value : "";
                const parcelOnly = parcelFilter instanceof HTMLInputElement && parcelFilter.checked;
                const maxVisible = query ? 18 : 10;
                let matched = 0;
                let shown = 0;

                results.forEach((result) => {
                    const haystack = normalize(result.dataset.search);
                    const isTextMatch = !query || haystack.includes(query);
                    const isMatch = isTextMatch
                        && matchesMethod(result, selectedMethod)
                        && matchesArtifact(result, selectedArtifact)
                        && matchesConfidence(result, selectedConfidence)
                        && (!parcelOnly || result.dataset.parcelAnchor === "true");
                    if (isMatch) {
                        matched += 1;
                    }

                    const shouldShow = isMatch && shown < maxVisible;
                    result.hidden = !shouldShow;
                    result.classList.toggle("is-match", shouldShow && Boolean(query));
                    if (shouldShow) {
                        shown += 1;
                    }
                });

                if (count) {
                    count.textContent = query
                        ? `${matched} matching county route${matched === 1 ? "" : "s"}`
                        : `${results.length} priority county routes indexed`;
                    if (!query && (selectedMethod || selectedArtifact || selectedConfidence || parcelOnly)) {
                        count.textContent = `${matched} filtered county route${matched === 1 ? "" : "s"}`;
                    }
                }
                if (empty) {
                    empty.hidden = matched > 0;
                }
            }

            input.addEventListener("input", updateResults);
            [methodFilter, artifactFilter, confidenceFilter].forEach((select) => {
                if (select instanceof HTMLSelectElement) {
                    select.addEventListener("change", updateResults);
                }
            });
            if (parcelFilter instanceof HTMLInputElement) {
                parcelFilter.addEventListener("change", updateResults);
            }
            input.addEventListener("keydown", (event) => {
                if (event.key !== "Enter") {
                    return;
                }

                const firstVisible = results.find((result) => !result.hidden);
                if (firstVisible) {
                    event.preventDefault();
                    firstVisible.click();
                }
            });

            if (clear) {
                clear.addEventListener("click", () => {
                    input.value = "";
                    [methodFilter, artifactFilter, confidenceFilter].forEach((select) => {
                        if (select instanceof HTMLSelectElement) {
                            select.value = "";
                        }
                    });
                    if (parcelFilter instanceof HTMLInputElement) {
                        parcelFilter.checked = false;
                    }
                    input.focus();
                    updateResults();
                });
            }

            updateResults();
        });
    }

    setupCountyFinders();

    function setupRecordsRequestBuilders() {
        const builders = Array.from(document.querySelectorAll("[data-records-request-builder]"));
        if (!builders.length) {
            return;
        }

        const stateRouteNotes = {
            TN: "Start with the TDEC septic permit search, then use the regional or contract-county route if the parcel is not visible.",
            NC: "Route the request to the county environmental health office; North Carolina septic files are usually county-held.",
            TX: "Ask for the OSSF permit file, approved plan, inspection record, and local authorized-agent route.",
            FL: "Ask the county health department or eBridge-style records path for OSTDS permit, final approval, and archive details.",
            DEFAULT: "Route the request to the county health, environmental health, or onsite wastewater office that holds septic permit files."
        };

        const artifactCopy = {
            permit_copy: "the septic permit copy and permit history",
            as_built: "the septic as-built, site sketch, approved plan, or installed layout",
            final_approval: "the final approval, operation permit, installation certificate, or closeout record",
            repair_record: "any repair permit, malfunction file, complaint record, or corrective-action history",
            inspection_letter: "the inspection letter, lender letter, buyer due-diligence response, or records letter",
            no_record: "a written no-record response if no septic file is available"
        };

        const reasonCopy = {
            buying: "buyer due diligence or a property transaction",
            repair: "repair planning or a suspected system problem",
            addition: "an addition, bedroom-count question, pool, driveway, grading, or site-change review",
            replacement: "replacement planning or drain-field risk review",
            lender: "a lender, closing, or inspection-letter request",
            owner_records: "owner records and file verification"
        };

        function valueOf(builder, selector) {
            const element = builder.querySelector(selector);
            return element instanceof HTMLInputElement || element instanceof HTMLSelectElement || element instanceof HTMLTextAreaElement
                ? element.value.trim()
                : "";
        }

        function labelOf(select) {
            if (!(select instanceof HTMLSelectElement)) {
                return "";
            }
            return select.options[select.selectedIndex]?.text?.trim() || "";
        }

        function buildRequest(builder) {
            const stateSelect = builder.querySelector("[data-request-state]");
            const recordSelect = builder.querySelector("[data-request-record]");
            const reasonSelect = builder.querySelector("[data-request-reason]");
            const stateCode = stateSelect instanceof HTMLSelectElement ? stateSelect.value : "DEFAULT";
            const stateLabel = labelOf(stateSelect) || "the property state";
            const recordKey = recordSelect instanceof HTMLSelectElement ? recordSelect.value : "permit_copy";
            const reasonKey = reasonSelect instanceof HTMLSelectElement ? reasonSelect.value : "owner_records";
            const county = valueOf(builder, "[data-request-county]");
            const address = valueOf(builder, "[data-request-address]");
            const parcel = valueOf(builder, "[data-request-parcel]");
            const owner = valueOf(builder, "[data-request-owner]");
            const routeNote = stateRouteNotes[stateCode] || stateRouteNotes.DEFAULT;
            const artifact = artifactCopy[recordKey] || artifactCopy.permit_copy;
            const reason = reasonCopy[reasonKey] || reasonCopy.owner_records;
            const countyLine = county ? `${county} County` : "the property county";
            const routeLabel = stateCode === "DEFAULT"
                ? "the local county health, environmental health, or onsite wastewater office"
                : stateLabel;
            const addressLine = address || "[property address]";
            const parcelLine = parcel || "[parcel ID / APN / tax ID if known]";
            const ownerLine = owner || "[current or prior owner name if known]";

            return [
                `Subject: Septic records request for ${addressLine}`,
                "",
                `Hello, I am requesting septic system records for a property in ${countyLine}. Please route this through ${routeLabel}.`,
                "",
                `Property: ${addressLine}`,
                `Parcel / APN / tax ID: ${parcelLine}`,
                `Owner name: ${ownerLine}`,
                `Reason for request: ${reason}.`,
                "",
                `Please search for ${artifact}. If those records are held by another office, please tell me the correct office or public records route.`,
                "",
                "If no septic record is available, please provide a written no-record response or the best next step for confirming whether a file exists.",
                "",
                `Routing note: ${routeNote}`,
                "",
                "Thank you."
            ].join("\n");
        }

        function updateBuilder(builder) {
            const output = builder.querySelector("[data-records-request-output]");
            const preview = builder.querySelector("[data-records-request-preview]");
            const script = buildRequest(builder);

            if (output instanceof HTMLTextAreaElement) {
                output.value = script;
            }
            if (preview) {
                preview.textContent = script.split("\n").filter(Boolean).slice(0, 3).join(" ");
            }
        }

        function copyText(text) {
            if (navigator.clipboard && window.isSecureContext) {
                return navigator.clipboard.writeText(text);
            }
            const textarea = document.createElement("textarea");
            textarea.value = text;
            textarea.setAttribute("readonly", "");
            textarea.style.position = "fixed";
            textarea.style.top = "-1000px";
            document.body.appendChild(textarea);
            textarea.select();
            try {
                document.execCommand("copy");
                return Promise.resolve();
            } catch (error) {
                return Promise.reject(error);
            } finally {
                document.body.removeChild(textarea);
            }
        }

        builders.forEach((builder) => {
            const inputs = Array.from(builder.querySelectorAll("input, select"));
            const copyButton = builder.querySelector("[data-records-request-copy]");
            const output = builder.querySelector("[data-records-request-output]");

            inputs.forEach((input) => {
                input.addEventListener("input", () => updateBuilder(builder));
                input.addEventListener("change", () => updateBuilder(builder));
            });

            if (copyButton instanceof HTMLButtonElement && output instanceof HTMLTextAreaElement) {
                copyButton.addEventListener("click", async () => {
                    const original = copyButton.textContent;
                    try {
                        await copyText(output.value);
                        copyButton.textContent = "Request copied";
                        copyButton.classList.add("is-copied");
                    } catch (_error) {
                        copyButton.textContent = "Copy failed";
                        copyButton.classList.add("is-copy-failed");
                    }
                    window.setTimeout(() => {
                        copyButton.textContent = original;
                        copyButton.classList.remove("is-copied", "is-copy-failed");
                    }, 1800);
                });
            }

            updateBuilder(builder);
        });
    }

    setupRecordsRequestBuilders();

    function setupShareActions() {
        const buttons = Array.from(document.querySelectorAll("[data-share-route]"));
        if (!buttons.length) {
            return;
        }

        function shareTargetPath() {
            return window.location.pathname + window.location.search + "#share";
        }

        function fallbackCopy(text) {
            if (navigator.clipboard && window.isSecureContext) {
                return navigator.clipboard.writeText(text);
            }

            const textArea = document.createElement("textarea");
            textArea.value = text;
            textArea.setAttribute("readonly", "");
            textArea.style.position = "fixed";
            textArea.style.top = "-1000px";
            document.body.appendChild(textArea);
            textArea.select();

            try {
                document.execCommand("copy");
                return Promise.resolve();
            } catch (error) {
                return Promise.reject(error);
            } finally {
                document.body.removeChild(textArea);
            }
        }

        function setTemporaryLabel(button, label, className) {
            const originalLabel = button.dataset.originalLabel || button.textContent;
            button.dataset.originalLabel = originalLabel;
            button.textContent = label;
            button.classList.add(className);
            window.setTimeout(() => {
                button.textContent = originalLabel;
                button.classList.remove(className);
            }, 1800);
        }

        buttons.forEach((button) => {
            button.addEventListener("click", async () => {
                const url = button.dataset.shareUrl || window.location.href;
                const title = button.dataset.shareTitle || document.title;
                const text = button.dataset.shareText || "";
                const copyText = [title, text, url].filter(Boolean).join("\n");
                let method = "clipboard";

                try {
                    await fallbackCopy(copyText);
                    setTemporaryLabel(button, "Link copied", "is-copied");

                    if (navigator.share && window.matchMedia && window.matchMedia("(max-width: 720px)").matches) {
                        navigator.share({ title, text, url })
                            .then(() => {
                                method = "native_share";
                                setTemporaryLabel(button, "Shared", "is-copied");
                            })
                            .catch(() => {});
                    }

                    sendNavigationEvent({
                        sourcePage: window.location.pathname + window.location.search + window.location.hash,
                        sourceContext: button.dataset.trackSourceContext || "share_route",
                        targetPath: shareTargetPath(),
                        targetType: method,
                        targetLabel: title
                    });
                } catch (error) {
                    if (navigator.share) {
                        try {
                            await navigator.share({ title, text, url });
                            setTemporaryLabel(button, "Shared", "is-copied");
                            return;
                        } catch (shareError) {
                            if (shareError && shareError.name === "AbortError") {
                                return;
                            }
                        }
                    }
                    setTemporaryLabel(button, "Copy failed", "is-copy-failed");
                }
            });
        });
    }

    setupShareActions();

    function supportsStateAwareTools(pathname) {
        return pathname === "/septic-system-cost-calculator/"
            || pathname === "/septic-system-cost-calculator"
            || pathname === "/septic-tank-size-estimator/"
            || pathname === "/septic-tank-size-estimator"
            || pathname === "/drain-field-estimator/"
            || pathname === "/drain-field-estimator";
    }

    function sameOriginRelativePathFromUrl(url) {
        return url.pathname + url.search + url.hash;
    }

    function buildStateAwareHref(kind, baseHref, stateCode, statePath) {
        if (!baseHref) {
            return "";
        }

        if (kind === "state-page") {
            return statePath || baseHref;
        }

        if (kind !== "tool" || !stateCode) {
            return baseHref;
        }

        try {
            const url = new URL(baseHref, window.location.origin);
            if (url.origin !== window.location.origin) {
                return baseHref;
            }

            if (supportsStateAwareTools(url.pathname)) {
                url.searchParams.set("state", stateCode);
            }

            return sameOriginRelativePathFromUrl(url);
        } catch (_error) {
            return baseHref;
        }
    }

    function setupStateSurfaceTools() {
        const containers = Array.from(document.querySelectorAll("[data-state-surface-tool]"));
        if (!containers.length) {
            return;
        }

        containers.forEach((container) => {
            const select = container.querySelector("[data-state-surface-select]");
            const links = Array.from(container.querySelectorAll("[data-state-surface-link]"));
            const resultHeading = container.querySelector("[data-state-surface-result-heading]");
            const resultBody = container.querySelector("[data-state-surface-result-body]");
            const resultFit = container.querySelector("[data-state-surface-result-fit]");
            const resultFitNote = container.querySelector("[data-state-surface-result-fit-note]");
            const resultEvidence = container.querySelector("[data-state-surface-result-evidence]");
            const resultEvidenceNote = container.querySelector("[data-state-surface-result-evidence-note]");
            const resultHandoff = container.querySelector("[data-state-surface-result-handoff]");
            const resultHandoffNote = container.querySelector("[data-state-surface-result-handoff-note]");
            const resultPrimary = container.querySelector("[data-state-surface-result-primary]");
            const resultPrimaryNote = container.querySelector("[data-state-surface-result-primary-note]");
            const resultSecondary = container.querySelector("[data-state-surface-result-secondary]");
            const resultSecondaryNote = container.querySelector("[data-state-surface-result-secondary-note]");
            const mode = container.dataset.stateSurfaceMode || "state-first";
            const toolLabel = container.dataset.stateSurfaceToolLabel || "Run the estimate";
            const toolSupportsState = container.dataset.stateSurfaceToolSupportsState === "true";

            if (!(select instanceof HTMLSelectElement) || !links.length) {
                return;
            }

            function asActionPhrase(label) {
                if (!label) {
                    return "use the tool";
                }

                const trimmed = label.trim();
                const knownPrefixes = ["Run ", "Open ", "Start ", "Browse ", "Return to "];
                const matchedPrefix = knownPrefixes.find((prefix) => trimmed.startsWith(prefix));

                if (matchedPrefix) {
                    return matchedPrefix.toLowerCase() + trimmed.substring(matchedPrefix.length);
                }

                return `use ${trimmed}`;
            }

            function buildSurfaceResultCopy(stateLabel, stateTitle, signals) {
                const defaultTitle = "Choose a state to turn this broad page into a sharper next-step route.";
                const defaultBody = "This page should help you decide the next move fast. Once you choose a state, the route below will tell you whether to open the local workflow first or move straight into the tool.";
                const defaultSignals = {
                    fit: "Choose a state",
                    fitNote: "See how strong the selected state is as the local wedge for this broad surface.",
                    evidence: "Waiting for state",
                    evidenceNote: "We will show whether the state page is lightly directional or strongly source-backed.",
                    handoff: "Waiting for state",
                    handoffNote: "We will show if the tool should be used now, after one local check, or only as a backstop."
                };

                if (!stateLabel || !stateTitle) {
                    return mode === "state-first"
                        ? {
                            heading: defaultTitle,
                            body: defaultBody,
                            fit: defaultSignals.fit,
                            fitNote: defaultSignals.fitNote,
                            evidence: defaultSignals.evidence,
                            evidenceNote: defaultSignals.evidenceNote,
                            handoff: defaultSignals.handoff,
                            handoffNote: defaultSignals.handoffNote,
                            primary: "Open a selected state page",
                            primaryNote: "Use the matching state page when the main blocker is local workflow, records, permits, or buyer timing.",
                            secondary: toolLabel,
                            secondaryNote: "Use the tool only after the route is narrow enough that the number means something."
                        }
                        : {
                            heading: defaultTitle,
                            body: defaultBody,
                            fit: defaultSignals.fit,
                            fitNote: defaultSignals.fitNote,
                            evidence: defaultSignals.evidence,
                            evidenceNote: defaultSignals.evidenceNote,
                            handoff: defaultSignals.handoff,
                            handoffNote: defaultSignals.handoffNote,
                            primary: toolLabel,
                            primaryNote: "Use the tool first when the broad question is already clear enough to price or size.",
                            secondary: "Open a selected state page",
                            secondaryNote: "Use a live state page right after when the result still needs local workflow or file context."
                        };
                }

                if (mode === "state-first") {
                    return {
                        heading: `For ${stateLabel}, start with the state-specific workflow.`,
                        body: `Open ${stateTitle} first. Once the file, permit, or buyer lane is clearer, ${asActionPhrase(toolLabel)}.`,
                        fit: signals.fit,
                        fitNote: signals.fitNote,
                        evidence: signals.evidence,
                        evidenceNote: signals.evidenceNote,
                        handoff: signals.handoff,
                        handoffNote: signals.handoffNote,
                        primary: stateTitle,
                        primaryNote: `${stateLabel} is now the active wedge. The broad page stays useful as the surface, but the local workflow should carry the next decision.`,
                        secondary: toolLabel,
                        secondaryNote: `Use ${toolLabel} after the ${stateLabel} workflow narrows the problem enough to price honestly.`
                    };
                }

                return {
                    heading: `For ${stateLabel}, use the tool with a local backstop.`,
                    body: toolSupportsState
                        ? `${toolLabel} with ${stateLabel} attached, then check ${stateTitle} if the range still feels too broad or generic.`
                        : `${toolLabel} first, then open ${stateTitle} to ground the next move in a local workflow.`,
                    fit: signals.fit,
                    fitNote: signals.fitNote,
                    evidence: signals.evidence,
                    evidenceNote: signals.evidenceNote,
                    handoff: signals.handoff,
                    handoffNote: signals.handoffNote,
                    primary: toolLabel,
                    primaryNote: toolSupportsState
                        ? `${stateLabel} now follows the tool actions below, so the estimate or quote step stays anchored to one state.`
                        : `This tool does not prefill state context directly, so ${stateTitle} is the local backstop that keeps the route honest.`,
                    secondary: stateTitle,
                    secondaryNote: `Open ${stateTitle} when you need the local file path, permit sequence, or buyer-risk story behind the broad surface.`
                };
            }

            function syncResult(stateLabel, stateTitle, signals) {
                if (!resultHeading || !resultBody || !resultFit || !resultFitNote || !resultEvidence || !resultEvidenceNote || !resultHandoff || !resultHandoffNote || !resultPrimary || !resultPrimaryNote || !resultSecondary || !resultSecondaryNote) {
                    return;
                }

                const copy = buildSurfaceResultCopy(stateLabel, stateTitle, signals);
                resultHeading.textContent = copy.heading;
                resultBody.textContent = copy.body;
                resultFit.textContent = copy.fit;
                resultFitNote.textContent = copy.fitNote;
                resultEvidence.textContent = copy.evidence;
                resultEvidenceNote.textContent = copy.evidenceNote;
                resultHandoff.textContent = copy.handoff;
                resultHandoffNote.textContent = copy.handoffNote;
                resultPrimary.textContent = copy.primary;
                resultPrimaryNote.textContent = copy.primaryNote;
                resultSecondary.textContent = copy.secondary;
                resultSecondaryNote.textContent = copy.secondaryNote;
            }

            function syncLinks() {
                const selectedOption = select.options[select.selectedIndex];
                const stateCode = selectedOption?.value || "";
                const stateLabel = selectedOption?.text?.trim() || "";
                const statePath = selectedOption?.dataset.statePath || "";
                const stateTitle = selectedOption?.dataset.stateTitle?.trim() || "";
                const signals = {
                    fit: selectedOption?.dataset.stateWorkflowFit?.trim() || "",
                    fitNote: selectedOption?.dataset.stateWorkflowFitNote?.trim() || "",
                    evidence: selectedOption?.dataset.stateEvidenceDepth?.trim() || "",
                    evidenceNote: selectedOption?.dataset.stateEvidenceDepthNote?.trim() || "",
                    handoff: selectedOption?.dataset.stateToolHandoff?.trim() || "",
                    handoffNote: selectedOption?.dataset.stateToolHandoffNote?.trim() || ""
                };
                const hasSelectedState = stateCode !== "" && statePath !== "";

                links.forEach((link) => {
                    if (!(link instanceof HTMLAnchorElement)) {
                        return;
                    }

                    const kind = link.dataset.stateSurfaceLink || "";
                    const baseHref = link.dataset.stateSurfaceBaseHref || link.getAttribute("href") || "";
                    const fallbackTargetType = link.dataset.trackTargetType || "";
                    const selectedTargetType = link.dataset.stateSurfaceSelectedTargetType || fallbackTargetType;
                    const defaultLabel = link.dataset.stateSurfaceDefaultLabel || link.textContent || "";
                    const selectedLabel = link.dataset.stateSurfaceSelectedLabel || defaultLabel;
                    const href = buildStateAwareHref(kind, baseHref, hasSelectedState ? stateCode : "", hasSelectedState ? statePath : "");

                    link.dataset.stateSurfaceBaseHref = baseHref;
                    link.setAttribute("href", href);
                    link.dataset.trackTargetType = hasSelectedState ? selectedTargetType : fallbackTargetType;
                    link.textContent = hasSelectedState ? selectedLabel : defaultLabel;

                    if (hasSelectedState && stateLabel) {
                        link.dataset.trackLabel = `${selectedLabel} for ${stateLabel}`;
                        return;
                    }

                    link.removeAttribute("data-track-label");
                });

                syncResult(hasSelectedState ? stateLabel : "", hasSelectedState ? stateTitle : "", hasSelectedState ? signals : null);
            }

            select.addEventListener("change", syncLinks);
            syncLinks();
        });
    }

    setupStateSurfaceTools();

    function buildGaParams(element) {
        const params = {};

        for (const attribute of element.attributes) {
            if (!attribute.name.startsWith("data-ga-param-") || attribute.value === "") {
                continue;
            }

            const parameterName = attribute.name
                .substring("data-ga-param-".length)
                .replace(/-/g, "_");

            params[parameterName] = attribute.value;
        }

        return params;
    }

    function emitGaEvent(eventName, params) {
        if (!eventName || typeof window.gtag !== "function") {
            return;
        }

        window.gtag("event", eventName, params);
    }

    function trackGaEvents() {
        document.querySelectorAll("[data-ga-event]").forEach((element) => {
            const eventName = element.getAttribute("data-ga-event");
            const trackOnceKey = element.getAttribute("data-ga-track-once");
            const params = buildGaParams(element);

            if (!trackOnceKey) {
                emitGaEvent(eventName, params);
                return;
            }

            try {
                const storageKey = `septicpath_ga:${trackOnceKey}`;
                if (window.sessionStorage.getItem(storageKey) === "1") {
                    return;
                }

                emitGaEvent(eventName, params);
                window.sessionStorage.setItem(storageKey, "1");
            } catch (_error) {
                emitGaEvent(eventName, params);
            }
        });
    }

    trackGaEvents();

    document.addEventListener("click", (event) => {
        const anchor = event.target.closest("a[data-track-click]");
        if (!anchor) {
            return;
        }

        const targetPath = sameOriginPath(anchor);
        if (!targetPath || !targetPath.startsWith("/") || targetPath.startsWith("/events/")) {
            return;
        }

        sendNavigationEvent({
            sourcePage: window.location.pathname + window.location.search + window.location.hash,
            sourceContext: anchor.dataset.trackSourceContext || "",
            targetPath,
            targetType: anchor.dataset.trackTargetType || "",
            targetLabel: (anchor.dataset.trackLabel || anchor.textContent || "").trim().replace(/\s+/g, " ")
        });
    });
})();
