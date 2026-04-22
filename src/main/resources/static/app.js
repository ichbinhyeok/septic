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
