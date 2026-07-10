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

    function sendEvent(endpoint, payload) {
        const body = JSON.stringify(payload);

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

    function sendNavigationEvent(payload) {
        sendEvent("/events/nav-click", payload);
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

    function downloadText(filename, text) {
        const blob = new Blob([text], { type: "text/plain;charset=utf-8" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = filename || "septicpath-note.txt";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }

    function setTemporaryStatus(element, message, resetMessage) {
        if (!element) {
            return;
        }
        element.textContent = message;
        window.setTimeout(() => {
            element.textContent = resetMessage;
        }, 1800);
    }

    function setupHashAnchorOffset() {
        const offsetTargets = new Set(["records-request-builder", "send-note"]);

        function alignHashTarget() {
            const id = window.location.hash ? decodeURIComponent(window.location.hash.slice(1)) : "";
            if (!offsetTargets.has(id)) {
                return;
            }
            const target = document.getElementById(id);
            if (!(target instanceof HTMLElement)) {
                return;
            }
            const header = document.querySelector(".site-header");
            const headerHeight = header instanceof HTMLElement ? header.getBoundingClientRect().height : 0;
            const desiredTop = headerHeight + 24;
            const currentTop = target.getBoundingClientRect().top;
            if (Math.abs(currentTop - desiredTop) < 8) {
                return;
            }
            const nextTop = Math.max(0, window.scrollY + currentTop - desiredTop);
            window.scrollTo({ top: nextTop, behavior: "auto" });
        }

        window.addEventListener("load", () => {
            window.setTimeout(alignHashTarget, 0);
        });
        window.addEventListener("hashchange", () => {
            window.setTimeout(alignHashTarget, 0);
        });
    }

    setupHashAnchorOffset();

    function setupWebVitalTracking() {
        if (!("PerformanceObserver" in window) || !Array.isArray(PerformanceObserver.supportedEntryTypes)) {
            return;
        }

        const supported = new Set(PerformanceObserver.supportedEntryTypes);
        const sent = new Set();

        function sourcePage() {
            return window.location.pathname + window.location.search + window.location.hash;
        }

        function navigationType() {
            const navigation = performance.getEntriesByType("navigation")[0];
            return navigation && navigation.type ? navigation.type : "navigate";
        }

        function rating(metricName, value) {
            if (metricName === "CLS") {
                return value <= 0.1 ? "good" : value <= 0.25 ? "needs-improvement" : "poor";
            }
            if (metricName === "LCP") {
                return value <= 2500 ? "good" : value <= 4000 ? "needs-improvement" : "poor";
            }
            if (metricName === "INP") {
                return value <= 200 ? "good" : value <= 500 ? "needs-improvement" : "poor";
            }
            if (metricName === "FCP") {
                return value <= 1800 ? "good" : value <= 3000 ? "needs-improvement" : "poor";
            }
            if (metricName === "TTFB") {
                return value <= 800 ? "good" : value <= 1800 ? "needs-improvement" : "poor";
            }
            return "unknown";
        }

        function normalizedValue(metricName, value) {
            if (!Number.isFinite(value) || value < 0) {
                return null;
            }
            return metricName === "CLS" ? Number(value.toFixed(4)) : Math.round(value);
        }

        function report(metricName, value, onceKey = metricName) {
            if (sent.has(onceKey)) {
                return;
            }
            const normalized = normalizedValue(metricName, value);
            if (normalized === null) {
                return;
            }
            sent.add(onceKey);
            sendEvent("/events/web-vital", {
                metricName,
                value: normalized,
                rating: rating(metricName, normalized),
                sourcePage: sourcePage(),
                navigationType: navigationType()
            });
        }

        function observe(type, callback, options = { buffered: true }) {
            if (!supported.has(type)) {
                return;
            }
            try {
                const observer = new PerformanceObserver((list) => callback(list.getEntries()));
                observer.observe({ type, ...options });
            } catch (_error) {
                // Older browsers may list support but reject newer observer options.
            }
        }

        const navigation = performance.getEntriesByType("navigation")[0];
        if (navigation && Number.isFinite(navigation.responseStart)) {
            report("TTFB", navigation.responseStart, "TTFB");
        }

        observe("paint", (entries) => {
            entries.forEach((entry) => {
                if (entry.name === "first-contentful-paint") {
                    report("FCP", entry.startTime, "FCP");
                }
            });
        });

        let latestLcp = 0;
        observe("largest-contentful-paint", (entries) => {
            const entry = entries[entries.length - 1];
            if (entry) {
                latestLcp = entry.startTime;
            }
        });

        let cumulativeLayoutShift = 0;
        observe("layout-shift", (entries) => {
            entries.forEach((entry) => {
                if (!entry.hadRecentInput) {
                    cumulativeLayoutShift += entry.value || 0;
                }
            });
        });

        let maxInteractionDuration = 0;
        observe("event", (entries) => {
            entries.forEach((entry) => {
                if (entry.interactionId && entry.duration > maxInteractionDuration) {
                    maxInteractionDuration = entry.duration;
                }
            });
        }, { buffered: true, durationThreshold: 40 });

        function flushFinalVitals() {
            if (latestLcp > 0) {
                report("LCP", latestLcp, "LCP");
            }
            report("CLS", cumulativeLayoutShift, "CLS");
            if (maxInteractionDuration > 0) {
                report("INP", maxInteractionDuration, "INP");
            }
        }

        document.addEventListener("visibilitychange", () => {
            if (document.visibilityState === "hidden") {
                flushFinalVitals();
            }
        });
        window.addEventListener("pagehide", flushFinalVitals);
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
    setupWebVitalTracking();

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

    function setupCalculatorResultScroll() {
        const result = document.getElementById("result-top");
        if (!result) {
            return;
        }

        if (window.location.hash && window.location.hash !== "#result-top") {
            return;
        }

        result.setAttribute("tabindex", "-1");
        window.requestAnimationFrame(() => {
            result.scrollIntoView({ block: "start" });
            result.focus({ preventScroll: true });
        });
    }

    setupCalculatorResultScroll();

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
            let results = Array.from(finder.querySelectorAll("[data-county-finder-result]"));
            const resultsContainer = finder.querySelector("[data-county-finder-results]");
            const apiPath = finder.dataset.countyFinderApi;
            const totalRoutes = Number(finder.dataset.countyFinderTotal || results.length);
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

            function renderRemoteResults(items) {
                if (!resultsContainer) {
                    return;
                }
                const nodes = items.map((item) => {
                    const result = document.createElement("a");
                    result.className = "county-finder__result";
                    result.href = item.path;
                    result.dataset.countyFinderResult = "";
                    result.dataset.search = item.searchText;
                    result.dataset.method = item.requestMethodLabel;
                    result.dataset.artifact = item.firstArtifactLabel;
                    result.dataset.confidenceScore = item.confidenceScore;
                    result.dataset.parcelAnchor = item.parcelAnchorAvailable;
                    result.dataset.shareUrl = item.absoluteUrl;
                    result.dataset.shareQuery = `${item.countyName} ${item.stateCode} septic permit lookup`;
                    result.dataset.trackClick = "nav";
                    result.dataset.trackSourceContext = "county_finder_search";
                    result.dataset.trackTargetType = "county_records_page";

                    const top = document.createElement("div");
                    top.className = "county-finder__result-top";
                    const title = document.createElement("span");
                    title.textContent = item.title;
                    const score = document.createElement("strong");
                    score.textContent = `${item.confidenceScore}%`;
                    top.append(title, score);

                    const meta = document.createElement("div");
                    meta.className = "county-finder__result-meta";
                    meta.setAttribute("aria-label", `${item.title} route metadata`);
                    [item.confidenceLabel, item.requestMethodLabel, item.sourceDepthLabel]
                        .concat(item.parcelAnchorAvailable ? ["Parcel anchor"] : [])
                        .forEach((value) => {
                            const badge = document.createElement("span");
                            badge.textContent = value;
                            meta.append(badge);
                        });

                    const firstPull = document.createElement("small");
                    const firstPullLabel = document.createElement("strong");
                    firstPullLabel.textContent = "First pull: ";
                    firstPull.append(firstPullLabel, item.firstArtifactLabel);
                    const note = document.createElement("small");
                    note.textContent = item.note;
                    result.append(top, meta, firstPull, note);
                    return result;
                });
                resultsContainer.replaceChildren(...nodes);
                results = nodes;
            }

            let searchRequest = 0;

            async function updateResults() {
                const query = normalize(input.value);
                const selectedMethod = methodFilter instanceof HTMLSelectElement ? methodFilter.value : "";
                const selectedArtifact = artifactFilter instanceof HTMLSelectElement ? artifactFilter.value : "";
                const selectedConfidence = confidenceFilter instanceof HTMLSelectElement ? confidenceFilter.value : "";
                const parcelOnly = parcelFilter instanceof HTMLInputElement && parcelFilter.checked;
                let remoteMatchCount = null;

                if (apiPath) {
                    const requestId = ++searchRequest;
                    const params = new URLSearchParams({
                        q: input.value,
                        method: selectedMethod,
                        artifact: selectedArtifact,
                        confidence: selectedConfidence,
                        parcelOnly: String(parcelOnly)
                    });
                    try {
                        const response = await fetch(`${apiPath}?${params.toString()}`, { headers: { Accept: "application/json" } });
                        if (!response.ok || requestId !== searchRequest) {
                            return;
                        }
                        renderRemoteResults(await response.json());
                        remoteMatchCount = Number(response.headers.get("X-County-Finder-Match-Count"));
                    } catch (_) {
                        // The server-rendered priority routes remain usable if search enhancement is unavailable.
                    }
                }

                const maxVisible = 18;
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
                    const matchingCount = Number.isFinite(remoteMatchCount) ? remoteMatchCount : matched;
                    count.textContent = query
                        ? `${matchingCount} matching county route${matchingCount === 1 ? "" : "s"}`
                        : `${selectedMethod || selectedArtifact || selectedConfidence || parcelOnly
                            ? `${matchingCount} filtered county route${matchingCount === 1 ? "" : "s"}`
                            : `${totalRoutes} county routes searchable`}`;
                    if (!query && (selectedMethod || selectedArtifact || selectedConfidence || parcelOnly)) {
                        count.textContent = `${matchingCount} filtered county route${matchingCount === 1 ? "" : "s"}`;
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

    function setupAddressRecordFinders() {
        const finders = Array.from(document.querySelectorAll("[data-address-record-finder]"));
        if (!finders.length) {
            return;
        }

        const statusLabels = {
            county_route: "County route found",
            state_route: "State route found",
            unsupported: "County resolved",
            not_found: "Try a fuller address",
            unavailable: "Use the county finder",
            invalid: "Address needed"
        };

        finders.forEach((finder) => {
            const form = finder.querySelector("[data-address-record-finder-form]");
            const input = finder.querySelector("[data-address-record-finder-input]");
            const submit = finder.querySelector("[data-address-record-finder-submit]");
            const result = finder.querySelector("[data-address-record-finder-result]");
            const status = finder.querySelector("[data-address-record-finder-status]");
            const heading = finder.querySelector("[data-address-record-finder-heading]");
            const message = finder.querySelector("[data-address-record-finder-message]");
            const meta = finder.querySelector("[data-address-record-finder-meta]");
            const actions = finder.querySelector("[data-address-record-finder-actions]");
            const apiPath = finder.dataset.addressRecordFinderApi;

            if (!(form instanceof HTMLFormElement)
                || !(input instanceof HTMLInputElement)
                || !(submit instanceof HTMLButtonElement)
                || !(result instanceof HTMLElement)
                || !apiPath) {
                return;
            }

            function button(label, href, primary, targetType) {
                const link = document.createElement("a");
                link.className = `button ${primary ? "button--primary" : "button--secondary"}`;
                link.href = href;
                link.textContent = label;
                link.dataset.trackClick = "nav";
                link.dataset.trackSourceContext = "address_record_finder_result";
                link.dataset.trackTargetType = targetType;
                return link;
            }

            function render(payload) {
                result.hidden = false;
                if (status) {
                    status.textContent = statusLabels[payload.status] || "Record route";
                }
                if (heading) {
                    heading.textContent = payload.heading || "Open the county records route";
                }
                if (message) {
                    message.textContent = payload.message || "Use the county route to pull the official file before pricing.";
                }
                if (meta) {
                    const values = [payload.countyName, payload.stateName].filter(Boolean);
                    meta.replaceChildren(...values.map((value) => {
                        const item = document.createElement("span");
                        item.textContent = value;
                        return item;
                    }));
                    meta.hidden = values.length === 0;
                }
                if (actions) {
                    const nextActions = [];
                    if (payload.routePath) {
                        nextActions.push(button(payload.routeTitle || "Open records route", payload.routePath, true,
                            payload.status === "county_route" ? "county_records_page" : "internal_page"));
                    }
                    if (payload.officialRouteUrl) {
                        const official = button("Open official file route", payload.officialRouteUrl, false, "official_source");
                        official.target = "_blank";
                        official.rel = "noreferrer";
                        nextActions.push(official);
                    }
                    actions.replaceChildren(...nextActions);
                }
            }

            form.addEventListener("submit", async (event) => {
                event.preventDefault();
                const address = input.value.trim();
                if (address.length < 8) {
                    render({
                        status: "invalid",
                        heading: "Enter a full U.S. property address",
                        message: "Include street, city, state, and ZIP so the county can be resolved reliably."
                    });
                    input.focus();
                    return;
                }

                const defaultLabel = "Find record route";
                submit.disabled = true;
                submit.textContent = "Finding county...";
                try {
                    const response = await fetch(apiPath, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            Accept: "application/json"
                        },
                        body: JSON.stringify({ address })
                    });
                    const payload = await response.json();
                    render(payload);
                } catch (_) {
                    render({
                        status: "unavailable",
                        heading: "Use the county finder while address lookup reconnects",
                        message: "No address was saved. Search by county to open a verified local records route.",
                        routeTitle: "Search county records",
                        routePath: "/septic-records-by-county/"
                    });
                } finally {
                    submit.disabled = false;
                    submit.textContent = defaultLabel;
                }
            });
        });
    }

    setupAddressRecordFinders();

    function setupRecordsRequestBuilders() {
        const builders = Array.from(document.querySelectorAll("[data-records-request-builder]"));
        if (!builders.length) {
            return;
        }

        const stateRouteNotes = {
            TN: "Start with the TDEC septic permit search, then use the regional or contract-county route if the parcel is not visible.",
            NC: "Route the request to the county environmental health office; North Carolina septic files are usually county-held.",
            TX: "Ask for the OSSF permit file, approved plan, inspection record, and local authorized-agent route.",
            SC: "Ask SCDES or the county/regional contact for the septic permit copy, D-1740 application trail, final inspection status, and ePermitting route.",
            FL: "Ask the county health department or eBridge-style records path for OSTDS permit, final approval, and archive details.",
            DEFAULT: "Route the request to the county health, environmental health, or onsite wastewater office that holds septic permit files."
        };

        const stateRouteDetails = {
            TN: {
                title: "TDEC search, then county or regional fallback",
                body: "Search the Tennessee SSDS/TDEC route first. If the parcel is unclear, carry the same request into the county, regional office, or contract-county records path.",
                channel: "TDEC search result, regional environmental office, contract county, or county health records desk",
                fallback: "Ask which regional or contract-county office owns old, scanned, repair, or pre-digital septic files."
            },
            NC: {
                title: "County environmental health file desk",
                body: "North Carolina septic records usually resolve through county environmental health. Treat the county office as the primary file owner unless an official county page says otherwise.",
                channel: "county environmental health records email, permit portal, public-records form, or phone handoff",
                fallback: "Ask whether improvement permits, construction authorizations, operation permits, and scanned layout files sit in a separate county archive."
            },
            TX: {
                title: "County or authorized OSSF agent",
                body: "Texas OSSF records often sit with the county, authorized agent, local permitting authority, or city/ETJ route rather than one statewide file desk.",
                channel: "county OSSF office, authorized agent, local permitting authority, or public-records channel",
                fallback: "Ask whether city-limit, ETJ, subdivision, or local-authority status changes the correct OSSF file owner."
            },
            SC: {
                title: "SCDES septic records and county/regional route",
                body: "South Carolina searches should translate old DHEC wording into the current SCDES route, then confirm whether ePermitting, county contact, or regional staff owns the D-1740 and permit-copy trail.",
                channel: "SCDES septic tank route, ePermitting path, county contact, regional office, or records request",
                fallback: "Ask for the D-1740 trail, final inspection status, county contact, and written no-record response if the permit copy is not visible."
            },
            FL: {
                title: "County health OSTDS records path",
                body: "Florida OSTDS records usually resolve through county health workflows, county-specific archives, or eBridge-style paths tied to the property county.",
                channel: "county health department OSTDS route, archive desk, eBridge-style portal, or public-records request",
                fallback: "Ask whether old OSTDS permits, final approvals, site plans, and repair files sit in a county archive or separate DEP-era records path."
            },
            DEFAULT: {
                title: "County health office or onsite wastewater records desk",
                body: "Send through the official county health, environmental health, onsite wastewater, permitting, or public-records channel that owns parcel-level septic files.",
                channel: "county health, environmental health, onsite wastewater, permitting, or public-records channel",
                fallback: "Ask which office owns archived, scanned, delegated, regional, or pre-digital septic files if the first desk has no match."
            }
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

        const artifactChecklist = {
            permit_copy: "Ask for permit copy, permit history, permit number, issue date, closeout status, and any related repair permit.",
            as_built: "Ask for the as-built, site sketch, approved plan, installed layout, tank location, drain-field location, and reserve-area note.",
            final_approval: "Ask for final approval, operation permit, installation certificate, inspection signoff, and any closeout condition.",
            repair_record: "Ask for repair permits, malfunction files, complaint history, corrective-action records, and the latest resolved status.",
            inspection_letter: "Ask whether the office can issue an inspection, lender, records, or file-status letter and what artifact supports it.",
            no_record: "Ask for a written no-record response that names the identifiers searched and the next archive or delegated-office route."
        };

        const reasonChecklist = {
            buying: "Keep the response with the buyer diligence file before negotiating credits, closing timing, or inspection scope.",
            repair: "Keep the response with repair photos, symptoms, contractor notes, and any emergency or malfunction timeline.",
            addition: "Keep the response with bedroom-count, pool, driveway, grading, or site-change plans before assuming the parcel can absorb the change.",
            replacement: "Keep the response with failure, reserve-area, redesign, and replacement-route notes before treating a quote as final scope.",
            lender: "Keep the response with lender, closing, title, and inspection-letter requirements so the office knows what will satisfy the file.",
            owner_records: "Keep the response with the owner file so future buyers, agents, contractors, or county staff can retrace the search."
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

        function currentBuilderState(builder) {
            const stateSelect = builder.querySelector("[data-request-state]");
            const recordSelect = builder.querySelector("[data-request-record]");
            const reasonSelect = builder.querySelector("[data-request-reason]");
            const stateCode = stateSelect instanceof HTMLSelectElement ? stateSelect.value : "DEFAULT";
            const recordKey = recordSelect instanceof HTMLSelectElement ? recordSelect.value : "permit_copy";
            const reasonKey = reasonSelect instanceof HTMLSelectElement ? reasonSelect.value : "owner_records";
            const county = valueOf(builder, "[data-request-county]");
            const address = valueOf(builder, "[data-request-address]");
            const parcel = valueOf(builder, "[data-request-parcel]");
            const owner = valueOf(builder, "[data-request-owner]");
            const routeDetail = stateRouteDetails[stateCode] || stateRouteDetails.DEFAULT;
            const stateLabel = labelOf(stateSelect) || "County health office";
            const routeLabel = stateCode === "DEFAULT"
                ? "the local county health, environmental health, or onsite wastewater office"
                : routeDetail.channel;

            return {
                stateCode,
                stateLabel,
                recordKey,
                recordLabel: labelOf(recordSelect) || "Permit copy and permit history",
                reasonKey,
                reasonLabel: labelOf(reasonSelect) || "Owner records",
                county,
                address,
                parcel,
                owner,
                routeDetail,
                routeNote: stateRouteNotes[stateCode] || stateRouteNotes.DEFAULT,
                artifact: artifactCopy[recordKey] || artifactCopy.permit_copy,
                artifactCheck: artifactChecklist[recordKey] || artifactChecklist.permit_copy,
                reason: reasonCopy[reasonKey] || reasonCopy.owner_records,
                reasonCheck: reasonChecklist[reasonKey] || reasonChecklist.owner_records,
                countyLine: county ? `${county} County` : "the property county",
                routeLabel,
                addressLine: address || "[property address]",
                parcelLine: parcel || "[parcel ID / APN / tax ID if known]",
                ownerLine: owner || "[current or prior owner name if known]"
            };
        }

        function submissionChecklist(current) {
            return [
                `Submit through the official ${current.routeDetail.channel}.`,
                `Include address, county, parcel/APN/TMS, owner, legal description, subdivision, lot number, and prior permit number when available.`,
                current.artifactCheck,
                current.reasonCheck,
                current.routeDetail.fallback
            ];
        }

        function escapeHtml(value) {
            return String(value)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#39;");
        }

        function buildRequest(builder) {
            const current = currentBuilderState(builder);

            return [
                `Subject: Septic records request for ${current.addressLine}`,
                "",
                `Hello, I am requesting septic system records for a property in ${current.countyLine}. Please route this through ${current.routeLabel}.`,
                "",
                `Property: ${current.addressLine}`,
                `Parcel / APN / tax ID: ${current.parcelLine}`,
                `Owner name: ${current.ownerLine}`,
                `Reason for request: ${current.reason}.`,
                "",
                `Please search for ${current.artifact}. If those records are held by another office, please tell me the correct office or public records route.`,
                "",
                "If no septic record is available, please provide a written no-record response or the best next step for confirming whether a file exists.",
                "",
                `Routing note: ${current.routeNote}`,
                "",
                "Thank you."
            ].join("\n");
        }

        function currentDateLabel() {
            return new Date().toLocaleDateString("en-US", {
                year: "numeric",
                month: "short",
                day: "numeric"
            });
        }

        function buildDownloadPacket(builder, script) {
            const current = currentBuilderState(builder);
            const checklist = submissionChecklist(current);

            return [
                "Septic Records Request Packet",
                "Generated by SepticPath",
                `Generated: ${currentDateLabel()}`,
                "",
                "Use",
                "- Save this file before contacting the official state, county, health department, delegated authority, or permitting office.",
                "- Copy the message section into the official email, portal, public-records form, or phone workflow.",
                "- Keep the identifier checklist with the response so a missing result does not get mistaken for a missing septic file.",
                "",
                "Request target",
                `- State route: ${current.stateLabel}`,
                `- Submission route: ${current.routeDetail.title}`,
                `- Channel: ${current.routeDetail.channel}`,
                `- County: ${current.county ? `${current.county} County` : "[property county]"}`,
                `- Record needed: ${current.recordLabel}`,
                `- Reason: ${current.reasonLabel}`,
                "",
                "Property identifiers",
                `- Address: ${current.address || "[property address]"}`,
                `- Parcel / APN / tax ID: ${current.parcel || "[parcel ID / APN / tax ID if known]"}`,
                `- Owner name: ${current.owner || "[current or prior owner name if known]"}`,
                "- Legal description: [add if known]",
                "- Subdivision / lot number: [add if known]",
                "- Prior permit number: [add if known]",
                "",
                "Before sending checklist",
                ...checklist.map((item) => `- ${item}`),
                "",
                "Fallback search keys",
                "- Retry by parcel/APN/TMS, owner, prior owner, legal description, subdivision, lot number, permit number, and street-only search.",
                "- Ask whether archived, scanned, regional, contract-county, delegated-office, or pre-digital files need a separate request.",
                "- If no septic record is available, ask for a written no-record response tied to the property identifiers searched.",
                "",
                "Routing note",
                `- ${current.routeNote}`,
                "",
                "Response log",
                "- Date sent: [fill in]",
                "- Office / staff contact: [fill in]",
                "- Response received: [fill in]",
                "- Next office or archive route: [fill in]",
                "",
                "Copy-ready message",
                "------------------",
                script
            ].join("\n");
        }

        function buildPrintablePacket(builder, script) {
            const current = currentBuilderState(builder);
            const checklistItems = submissionChecklist(current)
                .map((item) => `<li>${escapeHtml(item)}</li>`)
                .join("");
            const identifierRows = [
                ["State route", current.stateLabel],
                ["Submission route", current.routeDetail.title],
                ["County", current.county ? `${current.county} County` : "[property county]"],
                ["Address", current.address || "[property address]"],
                ["Parcel / APN / tax ID", current.parcel || "[parcel ID / APN / tax ID if known]"],
                ["Owner", current.owner || "[current or prior owner name if known]"],
                ["Record needed", current.recordLabel],
                ["Reason", current.reasonLabel]
            ]
                .map(([label, value]) => `<tr><th>${escapeHtml(label)}</th><td>${escapeHtml(value)}</td></tr>`)
                .join("");

            return `<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>${escapeHtml(requestFilename(builder).replace(/\.txt$/, ""))}</title>
<style>
    @page { margin: 0.55in; }
    * { box-sizing: border-box; }
    body { margin: 0; color: #142220; font-family: Arial, sans-serif; line-height: 1.45; }
    .packet { max-width: 7.6in; margin: 0 auto; }
    .kicker { color: #236c5f; font-size: 11px; font-weight: 800; letter-spacing: 0.08em; text-transform: uppercase; }
    h1 { margin: 8px 0 8px; font-size: 30px; line-height: 1.05; }
    h2 { margin: 24px 0 8px; font-size: 15px; letter-spacing: 0.03em; text-transform: uppercase; }
    p { margin: 0 0 10px; }
    .meta { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin: 18px 0; }
    .meta div, .route, .message, .log { border: 1px solid #cfd8d1; border-radius: 8px; padding: 10px; }
    .meta span { display: block; color: #586965; font-size: 11px; text-transform: uppercase; }
    .meta strong { display: block; margin-top: 4px; font-size: 13px; }
    table { width: 100%; border-collapse: collapse; margin-top: 8px; }
    th, td { border: 1px solid #d8e0da; padding: 8px; text-align: left; vertical-align: top; }
    th { width: 31%; background: #f3f7f4; font-size: 12px; text-transform: uppercase; }
    ul { margin: 8px 0 0; padding-left: 20px; }
    li { margin: 5px 0; }
    pre { white-space: pre-wrap; margin: 0; font-family: Consolas, Menlo, monospace; font-size: 12px; line-height: 1.5; }
    .route { background: #f7faf7; }
    .message { page-break-inside: avoid; }
    .log { display: grid; grid-template-columns: 1fr 1fr; gap: 8px 16px; }
    .line { border-bottom: 1px solid #bac7c0; min-height: 26px; }
</style>
</head>
<body>
<main class="packet">
    <div class="kicker">SepticPath printable records request packet</div>
    <h1>Septic records request packet</h1>
    <p>Use this packet to submit a precise septic records request and keep a clean response trail for a property file.</p>
    <section class="meta" aria-label="Packet summary">
        <div><span>Generated</span><strong>${escapeHtml(currentDateLabel())}</strong></div>
        <div><span>Record needed</span><strong>${escapeHtml(current.recordLabel)}</strong></div>
        <div><span>Reason</span><strong>${escapeHtml(current.reasonLabel)}</strong></div>
    </section>
    <section class="route">
        <h2>Submission route</h2>
        <p><strong>${escapeHtml(current.routeDetail.title)}</strong></p>
        <p>${escapeHtml(current.routeDetail.body)}</p>
        <p><strong>Channel:</strong> ${escapeHtml(current.routeDetail.channel)}</p>
    </section>
    <section>
        <h2>Property and request identifiers</h2>
        <table>${identifierRows}</table>
    </section>
    <section>
        <h2>Before sending checklist</h2>
        <ul>${checklistItems}</ul>
    </section>
    <section class="message">
        <h2>Copy-ready message</h2>
        <pre>${escapeHtml(script)}</pre>
    </section>
    <section>
        <h2>Response log</h2>
        <div class="log">
            <div>Date sent<div class="line"></div></div>
            <div>Office / staff contact<div class="line"></div></div>
            <div>Response received<div class="line"></div></div>
            <div>Next office or archive route<div class="line"></div></div>
        </div>
    </section>
</main>
</body>
</html>`;
        }

        function openPrintablePacket(builder, script) {
            const printWindow = window.open("", "_blank", "width=900,height=1100");
            if (!printWindow) {
                return false;
            }
            printWindow.opener = null;
            printWindow.document.open();
            printWindow.document.write(buildPrintablePacket(builder, script));
            printWindow.document.close();
            printWindow.focus();
            window.setTimeout(() => {
                try {
                    printWindow.print();
                } catch (_error) {
                    // The printable packet remains open even if the print dialog is blocked.
                }
            }, 350);
            return true;
        }

        function updateBuilder(builder) {
            const output = builder.querySelector("[data-records-request-output]");
            const preview = builder.querySelector("[data-records-request-preview]");
            const routeTitle = builder.querySelector("[data-records-request-route-title]");
            const routeBody = builder.querySelector("[data-records-request-route-body]");
            const checklist = builder.querySelector("[data-records-request-checklist]");
            const script = buildRequest(builder);
            const current = currentBuilderState(builder);

            if (output instanceof HTMLTextAreaElement) {
                output.value = script;
            }
            if (preview) {
                preview.textContent = script.split("\n").filter(Boolean).slice(0, 3).join(" ");
            }
            if (routeTitle) {
                routeTitle.textContent = current.routeDetail.title;
            }
            if (routeBody) {
                routeBody.textContent = current.routeDetail.body;
            }
            if (checklist) {
                checklist.replaceChildren(...submissionChecklist(current).slice(0, 4).map((item) => {
                    const listItem = document.createElement("li");
                    listItem.textContent = item;
                    return listItem;
                }));
            }
        }

        function slugifyFilePart(value) {
            return (value || "")
                .toLowerCase()
                .replace(/[^a-z0-9]+/g, "-")
                .replace(/^-+|-+$/g, "")
                .slice(0, 48);
        }

        function requestFilename(builder) {
            const county = slugifyFilePart(valueOf(builder, "[data-request-county]"));
            const address = slugifyFilePart(valueOf(builder, "[data-request-address]"));
            const recordSelect = builder.querySelector("[data-request-record]");
            const record = slugifyFilePart(labelOf(recordSelect).replace(/\band\b/g, " "));
            const filename = [county, address, "septic-records-request", record || "packet"].filter(Boolean).join("-");
            return filename + ".txt";
        }

        builders.forEach((builder) => {
            const inputs = Array.from(builder.querySelectorAll("input, select"));
            const copyButton = builder.querySelector("[data-records-request-copy]");
            const downloadButton = builder.querySelector("[data-records-request-download]");
            const printButton = builder.querySelector("[data-records-request-print]");
            const filenameLabel = builder.querySelector("[data-records-request-filename]");
            const status = builder.querySelector("[data-records-request-status]");
            const output = builder.querySelector("[data-records-request-output]");

            inputs.forEach((input) => {
                const refresh = () => {
                    updateBuilder(builder);
                    if (filenameLabel) {
                        filenameLabel.textContent = requestFilename(builder);
                    }
                };
                input.addEventListener("input", refresh);
                input.addEventListener("change", refresh);
            });

            if (copyButton instanceof HTMLButtonElement && output instanceof HTMLTextAreaElement) {
                copyButton.addEventListener("click", async () => {
                    const original = copyButton.textContent;
                    try {
                        await copyText(output.value);
                        copyButton.textContent = "Request copied";
                        copyButton.classList.add("is-copied");
                        setTemporaryStatus(status, "Copied to clipboard", "Ready to copy or download");
                    } catch (_error) {
                        copyButton.textContent = "Copy failed";
                        copyButton.classList.add("is-copy-failed");
                        setTemporaryStatus(status, "Copy failed. Select the text manually.", "Ready to copy or download");
                    }
                    window.setTimeout(() => {
                        copyButton.textContent = original;
                        copyButton.classList.remove("is-copied", "is-copy-failed");
                    }, 1800);
                });
            }

            if (downloadButton instanceof HTMLButtonElement && output instanceof HTMLTextAreaElement) {
                downloadButton.addEventListener("click", () => {
                    const filename = requestFilename(builder);
                    downloadText(filename, buildDownloadPacket(builder, output.value));
                    downloadButton.textContent = "Downloaded";
                    downloadButton.classList.add("is-copied");
                    setTemporaryStatus(status, `Downloaded packet ${filename}`, "Ready to copy or download");
                    window.setTimeout(() => {
                        downloadButton.textContent = "Download packet .txt";
                        downloadButton.classList.remove("is-copied");
                    }, 1800);
                });
            }

            if (printButton instanceof HTMLButtonElement && output instanceof HTMLTextAreaElement) {
                printButton.addEventListener("click", () => {
                    const original = printButton.textContent;
                    if (openPrintablePacket(builder, output.value)) {
                        printButton.textContent = "PDF view opened";
                        printButton.classList.add("is-copied");
                        setTemporaryStatus(status, "Printable packet opened", "Ready to copy or download");
                    } else {
                        printButton.textContent = "Print blocked";
                        printButton.classList.add("is-copy-failed");
                        setTemporaryStatus(status, "Pop-up blocked. Allow pop-ups to print the packet.", "Ready to copy or download");
                    }
                    window.setTimeout(() => {
                        printButton.textContent = original;
                        printButton.classList.remove("is-copied", "is-copy-failed");
                    }, 1800);
                });
            }

            updateBuilder(builder);
            if (filenameLabel) {
                filenameLabel.textContent = requestFilename(builder);
            }
        });
    }

    setupRecordsRequestBuilders();

    function setupPacketNoteActions() {
        const packets = Array.from(document.querySelectorAll("[data-packet-note]"));
        if (!packets.length) {
            return;
        }

        packets.forEach((packet) => {
            const body = packet.querySelector("[data-packet-note-body]");
            const copyButton = packet.querySelector("[data-packet-note-copy]");
            const downloadButton = packet.querySelector("[data-packet-note-download]");
            const status = packet.querySelector("[data-packet-note-status]");

            function noteText() {
                const subject = packet.querySelector(".packet-copy__subject")?.textContent?.trim() || "";
                const message = body?.textContent?.trim() || "";
                return [subject, "", message].filter(Boolean).join("\n");
            }

            if (copyButton instanceof HTMLButtonElement) {
                copyButton.addEventListener("click", async () => {
                    const original = copyButton.textContent;
                    try {
                        await copyText(noteText());
                        copyButton.textContent = "Note copied";
                        copyButton.classList.add("is-copied");
                        setTemporaryStatus(status, "Copied to clipboard", "Ready");
                    } catch (_error) {
                        copyButton.textContent = "Copy failed";
                        copyButton.classList.add("is-copy-failed");
                        setTemporaryStatus(status, "Copy failed. Select the note manually.", "Ready");
                    }
                    window.setTimeout(() => {
                        copyButton.textContent = original;
                        copyButton.classList.remove("is-copied", "is-copy-failed");
                    }, 1800);
                });
            }

            if (downloadButton instanceof HTMLButtonElement) {
                downloadButton.addEventListener("click", () => {
                    downloadText("septicpath-workflow-packet.txt", noteText());
                    downloadButton.textContent = "Downloaded";
                    downloadButton.classList.add("is-copied");
                    setTemporaryStatus(status, "Downloaded septicpath-workflow-packet.txt", "Ready");
                    window.setTimeout(() => {
                        downloadButton.textContent = "Download .txt";
                        downloadButton.classList.remove("is-copied");
                    }, 1800);
                });
            }
        });
    }

    setupPacketNoteActions();

    function setupShareActions() {
        const buttons = Array.from(document.querySelectorAll("[data-share-route]"));
        if (!buttons.length) {
            return;
        }

        function shareTargetPath() {
            return window.location.pathname + window.location.search + "#share";
        }

        async function fallbackCopy(text) {
            if (navigator.clipboard && window.isSecureContext) {
                try {
                    await navigator.clipboard.writeText(text);
                    return;
                } catch (_error) {
                    // Fall back to a selected textarea when clipboard permissions are denied.
                }
            }

            const textArea = document.createElement("textarea");
            textArea.value = text;
            textArea.setAttribute("readonly", "");
            textArea.style.position = "fixed";
            textArea.style.top = "-1000px";
            document.body.appendChild(textArea);
            textArea.focus();
            textArea.select();
            textArea.setSelectionRange(0, textArea.value.length);

            try {
                if (!document.execCommand("copy")) {
                    throw new Error("copy command rejected");
                }
            } catch (error) {
                throw error;
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

                try {
                    if (navigator.share && window.matchMedia && window.matchMedia("(max-width: 720px)").matches) {
                        try {
                            await navigator.share({ title, text, url });
                            setTemporaryLabel(button, "Shared", "is-copied");
                            sendNavigationEvent({
                                sourcePage: window.location.pathname + window.location.search + window.location.hash,
                                sourceContext: button.dataset.trackSourceContext || "share_route",
                                targetPath: shareTargetPath(),
                                targetType: "native_share",
                                targetLabel: title
                            });
                            return;
                        } catch (shareError) {
                            if (shareError && shareError.name === "AbortError") {
                                return;
                            }
                        }
                    }

                    await fallbackCopy(copyText);
                    setTemporaryLabel(button, "Link copied", "is-copied");

                    sendNavigationEvent({
                        sourcePage: window.location.pathname + window.location.search + window.location.hash,
                        sourceContext: button.dataset.trackSourceContext || "share_route",
                        targetPath: shareTargetPath(),
                        targetType: "clipboard",
                        targetLabel: title
                    });
                } catch (error) {
                    button.setAttribute("title", url);
                    setTemporaryLabel(button, "Copy URL manually", "is-copy-failed");
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
