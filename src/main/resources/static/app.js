(() => {
    document.documentElement.classList.add("js");
    const coreAlreadyLoaded = Boolean(window.SepticPathCoreLoaded);

    const analyticsQueryKeys = new Set([
        "src", "utm_source", "utm_medium", "utm_campaign", "utm_content", "utm_term",
        "mode", "purpose", "projectType", "recordsMode"
    ]);

    function analyticsSafePath(url) {
        const params = new URLSearchParams();
        url.searchParams.forEach((value, key) => {
            if (analyticsQueryKeys.has(key) && /^[A-Za-z0-9._~-]{1,80}$/.test(value)) {
                params.append(key, value);
            }
        });
        const query = params.toString();
        return `${url.pathname}${query ? `?${query}` : ""}`;
    }

    function analyticsSourcePage() {
        return analyticsSafePath(new URL(window.location.href));
    }

    function navigationTarget(anchor) {
        try {
            const url = new URL(anchor.href, window.location.origin);
            if (url.origin === window.location.origin) {
                return analyticsSafePath(url);
            }
            if (url.protocol !== "https:") {
                return null;
            }
            return url.origin + url.pathname;
        } catch (_error) {
            return null;
        }
    }

    function officialRouteLabel(url) {
        return typeof url === "string" && /\.pdf(?:[?#]|$)/i.test(url)
            ? "Open county search instructions (PDF)"
            : "Open official file route";
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

    function sendArtifactAction(sourceContext, action, artifactType) {
        sendEvent("/events/artifact-action", {
            sourcePage: analyticsSourcePage(),
            sourceContext,
            action,
            artifactType
        });
    }

    function sendWorkflowStage(sourceContext, workflowRunId, countyKey, stage, outcome = "") {
        if (!workflowRunId || !stage) {
            return;
        }
        sendEvent("/events/workflow-stage", {
            sourcePage: analyticsSourcePage(),
            sourceContext,
            workflowRunId,
            countyKey: countyKey || "",
            stage,
            outcome
        });
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
        const offsetTargets = new Set(["home-address-record-finder", "records-request-builder", "county-access-workflow", "county-acquisition-workspace", "tdec-search-workspace", "send-note"]);

        function alignHashTarget() {
            const id = window.location.hash ? decodeURIComponent(window.location.hash.slice(1)) : "";
            if (!id) {
                return;
            }
            const target = document.getElementById(id);
            if (!(target instanceof HTMLElement)) {
                return;
            }
            let disclosure = target.closest("details");
            while (disclosure instanceof HTMLDetailsElement) {
                disclosure.open = true;
                disclosure = disclosure.parentElement?.closest("details") || null;
            }
            if (!offsetTargets.has(id)) {
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

    if (!coreAlreadyLoaded) {
        setupHashAnchorOffset();
    }

    function setupWebVitalTracking() {
        if (!("PerformanceObserver" in window) || !Array.isArray(PerformanceObserver.supportedEntryTypes)) {
            return;
        }

        const supported = new Set(PerformanceObserver.supportedEntryTypes);
        const sent = new Set();

        function sourcePage() {
            return analyticsSourcePage();
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

    if (!coreAlreadyLoaded) {
        setupSiteNav();
        setupWebVitalTracking();
    }

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

    if (!coreAlreadyLoaded) {
        setupStickyMobileCtas();
    }

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
            const stateFilter = finder.querySelector("[data-county-finder-state]");
            const parcelFilter = finder.querySelector("[data-county-finder-parcel]");
            const syncUrl = finder.dataset.countyFinderSyncUrl === "true";

            if (!input || !results.length) {
                return;
            }

            if (window.location.hash === `#${finder.id}`) {
                window.setTimeout(() => input.focus(), 0);
            }

            if (syncUrl) {
                const initialParams = new URLSearchParams(window.location.search);
                input.value = initialParams.get("q") || "";
                if (stateFilter instanceof HTMLSelectElement) {
                    stateFilter.value = initialParams.get("state") || "";
                }
                if (methodFilter instanceof HTMLSelectElement) {
                    methodFilter.value = initialParams.get("method") || "";
                }
                if (artifactFilter instanceof HTMLSelectElement) {
                    artifactFilter.value = initialParams.get("artifact") || "";
                }
                if (confidenceFilter instanceof HTMLSelectElement) {
                    confidenceFilter.value = initialParams.get("confidence") || "";
                }
                if (parcelFilter instanceof HTMLInputElement) {
                    parcelFilter.checked = initialParams.get("parcelOnly") === "true";
                }
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
                    result.dataset.stateCode = item.stateCode;
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
                    top.append(title);

                    const meta = document.createElement("div");
                    meta.className = "county-finder__result-meta";
                    meta.setAttribute("aria-label", `${item.title} route summary`);
                    [item.requestMethodLabel]
                        .concat(item.parcelAnchorAvailable ? ["Parcel search available"] : [])
                        .forEach((value) => {
                            const badge = document.createElement("span");
                            badge.textContent = value;
                            meta.append(badge);
                        });

                    const firstPull = document.createElement("small");
                    const firstPullLabel = document.createElement("strong");
                    firstPullLabel.textContent = "Start with: ";
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
                const selectedState = stateFilter instanceof HTMLSelectElement ? stateFilter.value : "";
                const parcelOnly = parcelFilter instanceof HTMLInputElement && parcelFilter.checked;
                let remoteMatchCount = null;

                if (syncUrl) {
                    const params = new URLSearchParams(window.location.search);
                    [
                        ["q", input.value.trim()],
                        ["state", selectedState],
                        ["method", selectedMethod],
                        ["artifact", selectedArtifact],
                        ["confidence", selectedConfidence],
                        ["parcelOnly", parcelOnly ? "true" : ""]
                    ].forEach(([key, value]) => value ? params.set(key, value) : params.delete(key));
                    const nextSearch = params.toString();
                    window.history.replaceState(
                        null,
                        "",
                        window.location.pathname + (nextSearch ? `?${nextSearch}` : "") + window.location.hash
                    );
                }

                if (apiPath) {
                    const requestId = ++searchRequest;
                    const params = new URLSearchParams({
                        q: input.value,
                        method: selectedMethod,
                        artifact: selectedArtifact,
                        confidence: selectedConfidence,
                        state: selectedState,
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
                        && (!selectedState || result.dataset.stateCode === selectedState)
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
                        : `${selectedState || selectedMethod || selectedArtifact || selectedConfidence || parcelOnly
                            ? `${matchingCount} filtered county route${matchingCount === 1 ? "" : "s"}`
                            : `${totalRoutes} county routes searchable`}`;
                    if (!query && (selectedState || selectedMethod || selectedArtifact || selectedConfidence || parcelOnly)) {
                        count.textContent = `${matchingCount} filtered county route${matchingCount === 1 ? "" : "s"}`;
                    }
                }
                if (empty) {
                    empty.hidden = matched > 0;
                }
            }

            input.addEventListener("input", updateResults);
            [stateFilter, methodFilter, artifactFilter, confidenceFilter].forEach((select) => {
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
                    [stateFilter, methodFilter, artifactFilter, confidenceFilter].forEach((select) => {
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

    function setupRecordsAccessIndex() {
        const root = document.querySelector("[data-records-access-index]");
        if (!root) {
            return;
        }

        const citation = document.querySelector("[data-records-index-citation]");
        const copyButton = document.querySelector("[data-records-index-copy-citation]");
        const status = document.querySelector("[data-records-index-status]");
        const downloadLinks = Array.from(document.querySelectorAll("[data-records-index-download]"));

        downloadLinks.forEach((link) => {
            link.addEventListener("click", () => {
                sendArtifactAction("records_access_index", "downloaded", "county_records_csv");
            });
        });

        if (citation instanceof HTMLTextAreaElement && copyButton instanceof HTMLButtonElement) {
            copyButton.addEventListener("click", async () => {
                try {
                    await copyText(citation.value.trim());
                    copyButton.textContent = "Citation copied";
                    copyButton.classList.add("is-copied");
                    setTemporaryStatus(status, "Citation copied to clipboard.", "Ready to copy. Verify the linked government source before relying on a file-status conclusion.");
                    sendArtifactAction("records_access_index", "copied", "dataset_citation");
                    window.setTimeout(() => {
                        copyButton.textContent = "Copy citation";
                        copyButton.classList.remove("is-copied");
                    }, 1800);
                } catch (_error) {
                    citation.focus();
                    citation.select();
                    setTemporaryStatus(status, "Select the citation and copy it manually.", "Ready to copy. Verify the linked government source before relying on a file-status conclusion.");
                }
            });
        }
    }

    setupRecordsAccessIndex();

    function setupAddressRecordFinders() {
        const finders = Array.from(document.querySelectorAll("[data-address-record-finder]"));
        if (!finders.length) {
            return;
        }

        const stateCodePattern = /(?:^|[\s,])(AL|AK|AZ|AR|CA|CO|CT|DE|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY|DC)(?:[\s,]|$)/i;

        function isFullUsAddress(value) {
            const normalized = value.trim();
            const hasLocalityShape = (normalized.match(/,/g) || []).length >= 2
                || normalized.split(/\s+/).length >= 5;
            return normalized.length >= 8
                && normalized.length <= 180
                && /\d/.test(normalized)
                && /[A-Za-z]/.test(normalized)
                && hasLocalityShape
                && (stateCodePattern.test(normalized) || /\b\d{5}(?:-\d{4})?\s*$/.test(normalized));
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
            const purposeSelect = finder.querySelector("[data-address-record-finder-purpose]");
            const submit = finder.querySelector("[data-address-record-finder-submit]");
            const directDocumentButton = finder.querySelector("[data-record-document-direct]");
            const result = finder.querySelector("[data-address-record-finder-result]");
            const status = finder.querySelector("[data-address-record-finder-status]");
            const heading = finder.querySelector("[data-address-record-finder-heading]");
            const message = finder.querySelector("[data-address-record-finder-message]");
            const meta = finder.querySelector("[data-address-record-finder-meta]");
            const office = finder.querySelector("[data-address-record-finder-office]");
            const officeLabel = finder.querySelector("[data-address-record-finder-office-label]");
            const contact = finder.querySelector("[data-address-record-finder-contact]");
            const reviewed = finder.querySelector("[data-address-record-finder-reviewed]");
            const steps = finder.querySelector("[data-address-record-finder-steps]");
            const actions = finder.querySelector("[data-address-record-finder-actions]");
            const officialNote = finder.querySelector(".record-finder__official-note");
            const routeDetails = finder.querySelector(".record-finder__route-details");
            const searchPacket = finder.querySelector("[data-record-search-packet]");
            const searchPacketOutput = finder.querySelector("[data-record-search-packet-output]");
            const searchPacketCopy = finder.querySelector("[data-record-search-packet-copy]");
            const returnPanel = finder.querySelector("[data-address-record-finder-return]");
            const outcomes = finder.querySelector("[data-address-record-finder-outcomes]");
            const clearProgressButton = finder.querySelector("[data-record-progress-clear]");
            const next = finder.querySelector("[data-address-record-finder-next]");
            const documentWorkspace = finder.querySelector("[data-record-document-workspace]");
            const documentForm = finder.querySelector("[data-record-document-form]");
            const documentFile = finder.querySelector("[data-record-document-file]");
            const documentSubmit = finder.querySelector("[data-record-document-submit]");
            const documentPaste = finder.querySelector("[data-record-document-paste]");
            const documentPasteSubmit = finder.querySelector("[data-record-document-paste-submit]");
            const workspaceImport = finder.querySelector("[data-record-workspace-import]");
            const documentStatus = finder.querySelector("[data-record-document-status]");
            const documentAnalysis = finder.querySelector("[data-record-document-analysis]");
            const apiPath = finder.dataset.addressRecordFinderApi;
            const workspaceStorageKey = "septicpath-document-workspace-v2";
            const legacyWorkspaceStorageKey = "septicpath-document-workspace-v1";
            const pendingReturnStorageKey = "septicpath-official-return-v1";
            const taskProgressStorageKey = "septicpath-record-task-progress-v1";
            const progressLifetime = 30 * 24 * 60 * 60 * 1000;
            let routeContext = null;
            let awaitingOfficialReturn = false;
            let workspaceState = { documents: [] };
            const confirmedFindingKeys = new Set();
            const finderQuery = new URLSearchParams(window.location.search);
            const requestedAddress = finderQuery.get("address")?.trim();
            const requestedCountyKey = finderQuery.get("countyKey")?.trim() || "";
            const requestedWorkflowRunId = finderQuery.get("workflowRunId")?.trim() || "";
            let activeWorkflowRunId = requestedWorkflowRunId;
            const workflowStagesSent = new Set();

            function ensureWorkflowRunId() {
                if (!activeWorkflowRunId) {
                    activeWorkflowRunId = typeof window.crypto?.randomUUID === "function"
                        ? window.crypto.randomUUID()
                        : `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`;
                }
                return activeWorkflowRunId;
            }

            function recordFinderStage(stage, outcome = "") {
                const eventKey = `${stage}:${outcome}`;
                if (workflowStagesSent.has(eventKey)) {
                    return;
                }
                workflowStagesSent.add(eventKey);
                sendWorkflowStage(
                    "address_record_finder",
                    ensureWorkflowRunId(),
                    requestedCountyKey,
                    stage,
                    outcome
                );
            }

            if (requestedAddress && input instanceof HTMLInputElement && !input.value) {
                input.value = requestedAddress.slice(0, 180);
            }

            const purposeRequirements = {
                buying: [
                    ["permit_number", "Permit or record number"],
                    ["approved_bedrooms", "Approved bedroom count"],
                    ["final_approval", "Final approval"],
                    ["layout", "As-built or site plan"],
                    ["repair_history", "Repair history"]
                ],
                bedrooms: [
                    ["permit_number", "Permit or record number"],
                    ["approved_bedrooms", "Approved bedroom count"],
                    ["design_flow", "Approved design flow"],
                    ["final_approval", "Final approval"]
                ],
                location: [
                    ["permit_number", "Permit or record number"],
                    ["layout", "As-built or site plan"]
                ],
                repair: [
                    ["permit_number", "Permit or record number"],
                    ["layout", "As-built or site plan"],
                    ["repair_history", "Repair history"],
                    ["system_type", "Existing system type"]
                ],
                replacement: [
                    ["permit_number", "Permit or record number"],
                    ["layout", "As-built or site plan"],
                    ["system_type", "Existing system type"],
                    ["tank_capacity", "Tank capacity"],
                    ["design_flow", "Approved design flow"]
                ],
                lender: [
                    ["permit_number", "Permit or record number"],
                    ["approved_bedrooms", "Approved bedroom count"],
                    ["final_approval", "Final approval"]
                ],
                owner: [
                    ["permit_number", "Permit or record number"],
                    ["final_approval", "Final approval"],
                    ["layout", "As-built or site plan"],
                    ["repair_history", "Repair history"]
                ]
            };

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
                let resolvedHref = href;
                if (targetType === "county_records_page"
                    && routeContext?.matchedAddress
                    && typeof href === "string"
                    && href.startsWith("/")
                    && !href.startsWith("//")) {
                    const routeUrl = new URL(href, window.location.origin);
                    routeUrl.searchParams.set("address", routeContext.matchedAddress);
                    if (routeContext.purpose) {
                        routeUrl.searchParams.set("purpose", routeContext.purpose);
                    }
                    routeUrl.searchParams.set("workflowRunId", ensureWorkflowRunId());
                    resolvedHref = `${routeUrl.pathname}${routeUrl.search}${routeUrl.hash}`;
                }
                link.href = resolvedHref;
                link.textContent = label;
                link.dataset.trackClick = "nav";
                link.dataset.trackSourceContext = "address_record_finder_result";
                link.dataset.trackTargetType = targetType;
                return link;
            }

            function showReturnPrompt() {
                if (returnPanel instanceof HTMLElement) {
                    returnPanel.hidden = false;
                    returnPanel.scrollIntoView({ behavior: "smooth", block: "nearest" });
                }
            }

            const purposeContent = {
                buying: {
                    title: "Check whether the property file is complete.",
                    copy: "Upload the permit or approval file. We will pull out common fields and flag missing final approval, layout, or repair history.",
                    linkLabel: "Open buyer records checklist",
                    linkPath: "/buying-a-house-with-a-septic-system/"
                },
                bedrooms: {
                    title: "Check the approval before relying on bedroom count.",
                    copy: "Upload the permit or operation record so approved bedrooms, design flow, and final approval can be checked together.",
                    linkLabel: "Open bedroom permit checker",
                    linkPath: "/septic-bedroom-permit-checker/"
                },
                location: {
                    title: "Look for the drawing, not just the permit number.",
                    copy: "Upload the as-built or site plan. The original drawing should remain the source of truth before anyone digs.",
                    linkLabel: "Review tank location records",
                    linkPath: "/septic-tank-location-records/"
                },
                repair: {
                    title: "Build a repair-ready file.",
                    copy: "Upload the layout, permit, or repair record before a contractor defines the problem or scope.",
                    linkLabel: "Review repair and records steps",
                    linkPath: "/septic-records-checklist/"
                },
                replacement: {
                    title: "Define the existing system before pricing replacement.",
                    copy: "Upload the permit or layout. Cost planning comes after system type, tank size, and reserve-area clues are visible.",
                    linkLabel: "Open replacement planning",
                    linkPath: "/septic-replacement-cost/"
                },
                lender: {
                    title: "Check what the closing file actually proves.",
                    copy: "Upload the approval, inspection, or lender letter and check whether the permit trail is still incomplete.",
                    linkLabel: "Review inspection letter requirements",
                    linkPath: "/septic-inspection-letter/"
                },
                owner: {
                    title: "Turn the document into a reusable owner file.",
                    copy: "Upload the record to extract common identifiers and see which supporting files are still missing.",
                    linkLabel: "Open the records checklist",
                    linkPath: "/septic-records-checklist/"
                }
            };

            function currentPurpose() {
                return purposeSelect instanceof HTMLSelectElement ? purposeSelect.value : "buying";
            }

            function saveRequestContext(extra = {}) {
                const value = JSON.stringify({
                    ...routeContext,
                    savedAt: Date.now(),
                    ...extra
                });
                try {
                    sessionStorage.setItem("septic-records-request-context", value);
                    localStorage.setItem("septic-records-request-context", value);
                } catch (_) {
                    // The builder remains usable without browser storage.
                }
            }

            function saveTaskProgress(stage, outcome = "") {
                if (!routeContext) {
                    return;
                }
                const value = JSON.stringify({
                    savedAt: Date.now(),
                    expiresAt: Date.now() + progressLifetime,
                    stage,
                    outcome,
                    context: routeContext
                });
                try {
                    sessionStorage.setItem(pendingReturnStorageKey, value);
                    localStorage.setItem(taskProgressStorageKey, value);
                } catch (_) {
                    // The focus-based return prompt still works without browser storage.
                }
            }

            function renderOffice(context) {
                if (!(office instanceof HTMLElement)) {
                    return;
                }
                const hasOffice = Boolean(context?.officeLabel || context?.contactLine);
                office.hidden = !hasOffice;
                if (!hasOffice) {
                    return;
                }
                if (officeLabel) {
                    officeLabel.textContent = context.officeLabel || `${context.countyName || "Local"} septic records office`;
                }
                if (contact) {
                    contact.textContent = context.contactLine || "Use the reviewed county route below and confirm the current submission method on the official destination.";
                }
                if (reviewed) {
                    reviewed.textContent = context.routeReviewedAt
                        ? `Official route reviewed ${context.routeReviewedAt}`
                        : "Official route linked; confirm current submission details on the destination site.";
                }
            }

            function searchPacketText(context) {
                return [
                    context?.matchedAddress ? `Property: ${context.matchedAddress}` : "",
                    context?.countyName || context?.stateName
                        ? `Location: ${[context.countyName, context.stateName].filter(Boolean).join(", ")}`
                        : "",
                    context?.officeLabel ? `Starting office/source: ${context.officeLabel}` : "",
                    context?.contactLine ? `Fallback contact: ${context.contactLine}` : "",
                    "Ask for: permit, installed layout or as-built, final approval, and repair history."
                ].filter(Boolean).join("\n");
            }

            function renderSearchPacket(context) {
                if (!(searchPacket instanceof HTMLElement) || !(searchPacketOutput instanceof HTMLElement)) {
                    return;
                }
                const packetText = searchPacketText(context);
                searchPacketOutput.textContent = packetText.replace(/\n/g, " · ");
                searchPacket.hidden = !packetText;
            }

            function findingsByKey(documents) {
                const grouped = new Map();
                documents.forEach((documentResult) => {
                    const sourceFile = documentResult.fileName || "Document";
                    const findings = Array.isArray(documentResult.findings) ? documentResult.findings : [];
                    findings.forEach((finding) => {
                        if (!finding?.key || !finding?.value) {
                            return;
                        }
                        const entries = grouped.get(finding.key) || [];
                        entries.push({ ...finding, sourceFile });
                        grouped.set(finding.key, entries);
                    });
                });
                return grouped;
            }

            function workspaceSummary(documents) {
                const grouped = findingsByKey(documents);
                const conflictKeys = new Set([
                    "approved_bedrooms",
                    "tank_capacity",
                    "design_flow",
                    "system_type"
                ]);
                const requirements = purposeRequirements[routeContext?.purpose || currentPurpose()]
                    || purposeRequirements.buying;
                const checklist = requirements.map(([key, label]) => {
                    const entries = grouped.get(key) || [];
                    const values = [...new Set(entries.map((entry) => entry.value))];
                    return {
                        key,
                        label,
                        status: conflictKeys.has(key) && values.length > 1
                            ? "conflict"
                            : values.length >= 1 ? "complete" : "missing",
                        values,
                        sources: [...new Set(entries.map((entry) => entry.sourceFile))]
                    };
                });
                const conflicts = [];
                grouped.forEach((entries, key) => {
                    const values = [...new Set(entries.map((entry) => entry.value))];
                    if (conflictKeys.has(key) && values.length > 1) {
                        conflicts.push({ key, label: entries[0]?.label || key, entries });
                    }
                });
                return {
                    documents,
                    grouped,
                    checklist,
                    conflicts,
                    completeCount: checklist.filter((item) => item.status === "complete").length,
                    totalCount: checklist.length
                };
            }

            function addDocumentToWorkspace(payload) {
                const documents = Array.isArray(workspaceState.documents)
                    ? [...workspaceState.documents]
                    : [];
                const normalized = {
                    ...payload,
                    addedAt: Date.now(),
                    findings: Array.isArray(payload?.findings) ? payload.findings : [],
                    missingItems: Array.isArray(payload?.missingItems) ? payload.missingItems : [],
                    nextSteps: Array.isArray(payload?.nextSteps) ? payload.nextSteps : []
                };
                const existingIndex = documents.findIndex((item) => item.fileName === normalized.fileName);
                if (existingIndex >= 0) {
                    documents.splice(existingIndex, 1, normalized);
                } else {
                    documents.push(normalized);
                }
                workspaceState = { documents: documents.slice(-8) };
                return workspaceSummary(workspaceState.documents);
            }

            function saveWorkspace() {
                try {
                    sessionStorage.setItem(workspaceStorageKey, JSON.stringify({
                        savedAt: Date.now(),
                        context: routeContext,
                        documents: workspaceState.documents
                    }));
                    sessionStorage.removeItem(legacyWorkspaceStorageKey);
                } catch (_) {
                    // The on-screen result remains available without browser storage.
                }
            }

            function safeImportedText(value, maximum = 240) {
                return typeof value === "string" ? value.trim().slice(0, maximum) : "";
            }

            function normalizeImportedWorkspace(value) {
                if (!value || typeof value !== "object"
                    || value.format !== "septicpath-property-file"
                    || value.version !== 1
                    || !Array.isArray(value.documents)) {
                    throw new Error("This is not a SepticPath property-file session.");
                }
                const documents = value.documents.slice(0, 8).map((item, documentIndex) => {
                    if (!item || typeof item !== "object") {
                        throw new Error(`Document ${documentIndex + 1} is not valid.`);
                    }
                    const findings = Array.isArray(item.findings)
                        ? item.findings.slice(0, 60).map((finding) => ({
                            key: safeImportedText(finding?.key, 60),
                            label: safeImportedText(finding?.label, 120),
                            value: safeImportedText(finding?.value, 240),
                            confidence: safeImportedText(finding?.confidence, 20),
                            evidence: safeImportedText(finding?.evidence, 500),
                            pageNumber: Number.isInteger(finding?.pageNumber)
                                && finding.pageNumber > 0 && finding.pageNumber <= 100
                                ? finding.pageNumber
                                : null
                        })).filter((finding) => finding.key && finding.label && finding.value)
                        : [];
                    return {
                        fileName: safeImportedText(item.fileName, 180) || `Saved document ${documentIndex + 1}`,
                        summary: safeImportedText(item.summary, 500),
                        purpose: safeImportedText(item.purpose, 30),
                        findings,
                        missingItems: Array.isArray(item.missingItems)
                            ? item.missingItems.slice(0, 30).map((entry) => safeImportedText(entry, 200)).filter(Boolean)
                            : [],
                        nextSteps: Array.isArray(item.nextSteps)
                            ? item.nextSteps.slice(0, 20).map((entry) => safeImportedText(entry, 300)).filter(Boolean)
                            : [],
                        addedAt: Number.isFinite(Number(item.addedAt)) ? Number(item.addedAt) : Date.now()
                    };
                });
                if (!documents.length) {
                    throw new Error("The saved session does not contain any reviewed documents.");
                }
                const context = value.context && typeof value.context === "object"
                    ? {
                        stateCode: safeImportedText(value.context.stateCode, 3),
                        stateName: safeImportedText(value.context.stateName, 80),
                        countyName: safeImportedText(value.context.countyName, 120),
                        matchedAddress: safeImportedText(value.context.matchedAddress, 180),
                        routePath: safeImportedText(value.context.routePath, 240),
                        officeLabel: safeImportedText(value.context.officeLabel, 180),
                        contactLine: safeImportedText(value.context.contactLine, 400),
                        routeReviewedAt: safeImportedText(value.context.routeReviewedAt, 30),
                        purpose: safeImportedText(value.context.purpose, 30) || "buying"
                    }
                    : { purpose: "buying" };
                if (context.routePath && (!context.routePath.startsWith("/") || context.routePath.startsWith("//"))) {
                    context.routePath = "";
                }
                return { context, documents };
            }

            function downloadWorkspaceSession() {
                const payload = {
                    format: "septicpath-property-file",
                    version: 1,
                    savedAt: new Date().toISOString(),
                    context: routeContext,
                    documents: workspaceState.documents
                };
                downloadText("septicpath-property-file-session.json", JSON.stringify(payload, null, 2));
            }

            function applyImportedWorkspace(imported) {
                routeContext = imported.context;
                workspaceState = { documents: imported.documents };
                if (purposeSelect instanceof HTMLSelectElement && routeContext.purpose) {
                    purposeSelect.value = routeContext.purpose;
                }
                if (routeContext.matchedAddress) {
                    input.value = routeContext.matchedAddress;
                }
                result.hidden = false;
                if (returnPanel instanceof HTMLElement) {
                    returnPanel.hidden = false;
                }
                if (documentWorkspace instanceof HTMLElement) {
                    documentWorkspace.hidden = false;
                }
                if (status) {
                    status.textContent = "Saved property file resumed";
                }
                if (heading) {
                    heading.textContent = routeContext.countyName
                        ? `Continue the ${routeContext.countyName} property file`
                        : "Continue the saved property file";
                }
                if (message) {
                    message.textContent = "Only the extracted summary was restored. Add the original documents again if you need to re-check them.";
                }
                renderOffice(routeContext);
                saveWorkspace();
                renderPropertyWorkspace(workspaceSummary(workspaceState.documents));
                documentAnalysis?.scrollIntoView({ behavior: "smooth", block: "nearest" });
            }

            function clearWorkspace() {
                try {
                    sessionStorage.removeItem(workspaceStorageKey);
                    sessionStorage.removeItem(legacyWorkspaceStorageKey);
                    sessionStorage.removeItem(pendingReturnStorageKey);
                    localStorage.removeItem(taskProgressStorageKey);
                } catch (_) {
                    // Nothing else needs clearing if browser storage is unavailable.
                }
                if (documentAnalysis instanceof HTMLElement) {
                    documentAnalysis.replaceChildren();
                    documentAnalysis.hidden = true;
                }
                if (documentStatus) {
                    documentStatus.textContent = "Session summary cleared. The original file was never stored.";
                }
                if (documentFile instanceof HTMLInputElement) {
                    documentFile.value = "";
                }
                workspaceState = { documents: [] };
                confirmedFindingKeys.clear();
            }

            function downloadWorkspaceSummary(summary) {
                const contextLines = [
                    routeContext?.matchedAddress ? `Property: ${routeContext.matchedAddress}` : "",
                    routeContext?.countyName ? `File owner context: ${routeContext.countyName}` : "",
                    routeContext?.stateName || routeContext?.stateCode
                        ? `State: ${routeContext.stateName || routeContext.stateCode}`
                        : "",
                    `Purpose: ${routeContext?.purpose || currentPurpose()}`,
                    `Documents reviewed: ${summary.documents.length}`
                ].filter(Boolean);
                const checklist = summary.checklist.map((item) => {
                    const status = item.status === "complete"
                        ? "CONFIRMED"
                        : item.status === "conflict" ? "CONFLICT" : "MISSING";
                    const value = item.values.length ? `: ${item.values.join(" / ")}` : "";
                    const sources = item.sources.length ? ` (${item.sources.join(", ")})` : "";
                    return `- [${status}] ${item.label}${value}${sources}`;
                });
                const conflicts = summary.conflicts.flatMap((conflict) => [
                    `- ${conflict.label}`,
                    ...conflict.entries.map((entry) => `  - ${entry.value} — ${entry.sourceFile}`)
                ]);
                const findings = [];
                summary.grouped.forEach((entries) => {
                    entries.forEach((entry) => {
                        findings.push(`- ${entry.label}: ${entry.value} — ${entry.sourceFile}`);
                    });
                });
                const fileNames = summary.documents.map((documentResult) => `- ${documentResult.fileName || "Document"}`);
                const body = [
                    "SEPTICPATH PROPERTY FILE",
                    "Browser-session summary — confirm every field against the original official document.",
                    "",
                    ...contextLines,
                    "",
                    `CHECKLIST PROGRESS: ${summary.completeCount} OF ${summary.totalCount}`,
                    ...checklist,
                    "",
                    "CONFLICTS TO RESOLVE",
                    ...(conflicts.length ? conflicts : ["- None detected"]),
                    "",
                    "ALL EXTRACTED FACTS",
                    ...(findings.length ? findings : ["- No common fields extracted"]),
                    "",
                    "SOURCE FILES",
                    ...fileNames,
                    "",
                    "LIMIT",
                    "This file review is not a property inspection, title opinion, code determination, or guarantee of system condition."
                ].filter((line) => line !== null && line !== undefined);
                downloadText("septicpath-property-file.txt", body.join("\n"));
            }

            function renderNextStep(outcome) {
                if (!(next instanceof HTMLElement)) {
                    return;
                }
                if (documentWorkspace instanceof HTMLElement) {
                    documentWorkspace.hidden = true;
                }
                const countyPath = routeContext?.routePath || "/septic-records-by-county/";
                const content = document.createElement("div");
                const title = document.createElement("strong");
                const copy = document.createElement("p");
                const links = document.createElement("div");
                links.className = "record-finder__result-actions";

                if (outcome === "found") {
                    const purpose = purposeContent[routeContext?.purpose] || purposeContent.buying;
                    title.textContent = purpose.title;
                    copy.textContent = purpose.copy;
                    links.append(button(purpose.linkLabel, purpose.linkPath, false, "internal_tool"));
                    if (documentWorkspace instanceof HTMLElement) {
                        documentWorkspace.hidden = false;
                    }
                } else if (outcome === "blocked") {
                    title.textContent = "Use the responsible office or fallback instead of repeating the search.";
                    copy.textContent = "Open the county route for the responsible office and verified fallback, or carry this property context into a routing draft.";
                    const localGuide = button("Open county-specific route", countyPath, true, "county_records_page");
                    const request = button("Build a prefilled records request", "/septic-records-request-builder/?mode=task#records-request-builder", false, "internal_tool");
                    request.dataset.recordRequestBuilder = "true";
                    links.append(localGuide, request);
                } else {
                    title.textContent = "Prepare the request your purpose requires.";
                    copy.textContent = "The request builder carries the address, county, state, and purpose into a routing draft. Confirm the official intake before sending it.";
                    const request = button("Build a prefilled records request", "/septic-records-request-builder/?mode=task#records-request-builder", true, "internal_tool");
                    request.dataset.recordRequestBuilder = "true";
                    links.append(request, button("Review the county route", countyPath, false, "county_records_page"));
                }

                content.append(title, copy, links);
                next.replaceChildren(content);
                next.hidden = false;
                if (typeof window.gtag === "function") {
                    window.gtag("event", "record_finder_outcome", {
                        outcome,
                        state_code: routeContext?.stateCode || "unknown",
                        county_name: routeContext?.countyName || "unknown"
                    });
                }
            }

            function render(payload) {
                result.hidden = false;
                if (officialNote instanceof HTMLElement) {
                    officialNote.hidden = false;
                }
                if (routeDetails instanceof HTMLDetailsElement) {
                    routeDetails.hidden = false;
                }
                routeContext = {
                    stateCode: payload.stateCode || "",
                    stateName: payload.stateName || "",
                    countyName: payload.countyName || "",
                    matchedAddress: payload.matchedAddress || input.value.trim(),
                    routePath: payload.routePath || "",
                    officeLabel: payload.officeLabel || "",
                    contactLine: payload.contactLine || "",
                    routeReviewedAt: payload.routeReviewedAt || "",
                    purpose: currentPurpose()
                };
                if (returnPanel instanceof HTMLElement) {
                    returnPanel.hidden = true;
                }
                if (next instanceof HTMLElement) {
                    next.hidden = true;
                    next.replaceChildren();
                }
                if (documentWorkspace instanceof HTMLElement) {
                    documentWorkspace.hidden = true;
                }
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
                renderOffice(routeContext);
                renderSearchPacket(routeContext);
                if (steps) {
                    const relaySteps = Array.isArray(payload.relaySteps) ? payload.relaySteps.filter(Boolean) : [];
                    steps.replaceChildren(...relaySteps.map((value) => {
                        const item = document.createElement("li");
                        item.textContent = value;
                        return item;
                    }));
                    steps.hidden = relaySteps.length === 0;
                }
                if (actions) {
                    const nextActions = [];
                    const relayActions = Array.isArray(payload.relayActions) ? payload.relayActions : [];
                    relayActions.forEach((action) => {
                        if (!action || !action.path) {
                            return;
                        }
                        const link = button(action.label || "Open records route", action.path, Boolean(action.primary), action.targetType || "internal_page");
                        if (action.external) {
                            link.target = "_blank";
                            link.rel = "noreferrer";
                            link.dataset.recordOfficialLink = "true";
                        }
                        nextActions.push(link);
                    });
                    if (!nextActions.length && payload.routePath) {
                        nextActions.push(button(payload.routeTitle || "Open records route", payload.routePath, true,
                            payload.status === "county_route" ? "county_records_page" : "internal_page"));
                    }
                    if (!relayActions.length && payload.officialRouteUrl) {
                        const official = button(officialRouteLabel(payload.officialRouteUrl), payload.officialRouteUrl, false, "official_source");
                        official.target = "_blank";
                        official.rel = "noreferrer";
                        official.dataset.recordOfficialLink = "true";
                        nextActions.push(official);
                    }
                    actions.replaceChildren(...nextActions);
                }
            }

            directDocumentButton?.addEventListener("click", () => {
                recordFinderStage("workflow_viewed");
                try {
                    sessionStorage.removeItem(pendingReturnStorageKey);
                    localStorage.removeItem(taskProgressStorageKey);
                } catch (_) {
                    // Direct document review still works when browser storage is unavailable.
                }
                routeContext = {
                    stateCode: "",
                    stateName: "",
                    countyName: "",
                    matchedAddress: input.value.trim(),
                    routePath: "",
                    officeLabel: "",
                    contactLine: "",
                    routeReviewedAt: "",
                    directDocument: true,
                    purpose: currentPurpose()
                };
                result.hidden = false;
                if (status) {
                    status.textContent = "Document in hand";
                }
                if (heading) {
                    heading.textContent = "Check the record you already have";
                }
                if (message) {
                    message.textContent = "Upload a PDF, photo, text file, or letter. SepticPath will extract common permit facts, show the source text, and keep missing or conflicting items visible.";
                }
                if (meta) {
                    meta.hidden = true;
                    meta.replaceChildren();
                }
                actions?.replaceChildren();
                if (searchPacket instanceof HTMLElement) {
                    searchPacket.hidden = true;
                }
                if (officialNote instanceof HTMLElement) {
                    officialNote.hidden = true;
                }
                if (routeDetails instanceof HTMLDetailsElement) {
                    routeDetails.hidden = true;
                }
                if (returnPanel instanceof HTMLElement) {
                    returnPanel.hidden = true;
                }
                if (next instanceof HTMLElement) {
                    next.hidden = true;
                    next.replaceChildren();
                }
                if (documentWorkspace instanceof HTMLElement) {
                    documentWorkspace.hidden = false;
                    documentWorkspace.scrollIntoView({ behavior: "smooth", block: "start" });
                    window.setTimeout(() => documentFile?.focus(), 320);
                }
                sendArtifactAction("address_record_finder", "direct_document_opened", "document_workspace");
            });

            searchPacketCopy?.addEventListener("click", async () => {
                const packetText = searchPacketText(routeContext);
                if (!packetText) {
                    return;
                }
                const originalLabel = searchPacketCopy.textContent;
                try {
                    await navigator.clipboard.writeText(packetText);
                    searchPacketCopy.textContent = "Copied";
                    sendArtifactAction("address_record_finder", "copied", "official_search_packet");
                } catch (_) {
                    searchPacketCopy.textContent = "Copy unavailable";
                }
                window.setTimeout(() => {
                    searchPacketCopy.textContent = originalLabel;
                }, 1600);
            });

            actions?.addEventListener("click", (event) => {
                const officialLink = event.target instanceof Element
                    ? event.target.closest("[data-record-official-link]")
                    : null;
                if (!officialLink) {
                    return;
                }
                awaitingOfficialReturn = true;
                saveTaskProgress("official_opened");
                recordFinderStage("official_route_opened");
                if (typeof window.gtag === "function") {
                    window.gtag("event", "record_finder_official_open", {
                        state_code: routeContext?.stateCode || "unknown",
                        county_name: routeContext?.countyName || "unknown"
                    });
                }
                window.setTimeout(showReturnPrompt, 350);
            });

            outcomes?.addEventListener("click", (event) => {
                const outcomeButton = event.target instanceof Element
                    ? event.target.closest("[data-record-outcome]")
                    : null;
                if (!(outcomeButton instanceof HTMLButtonElement)) {
                    return;
                }
                outcomes.querySelectorAll("button").forEach((item) => item.removeAttribute("aria-pressed"));
                outcomeButton.setAttribute("aria-pressed", "true");
                const outcome = outcomeButton.dataset.recordOutcome || "missing";
                saveTaskProgress("outcome_selected", outcome);
                renderNextStep(outcome);
                recordFinderStage("outcome_recorded", outcome);
                if (outcome === "found") {
                    recordFinderStage("record_reported", outcome);
                }
                sendArtifactAction("address_record_finder", `outcome_${outcome}`, "official_record_route");
            });

            clearProgressButton?.addEventListener("click", () => {
                try {
                    localStorage.removeItem(taskProgressStorageKey);
                    localStorage.removeItem("septic-records-request-context");
                    sessionStorage.removeItem(pendingReturnStorageKey);
                    sessionStorage.removeItem("septic-records-request-context");
                } catch (_) {
                    // The visible task can still be cleared when storage is unavailable.
                }
                routeContext = null;
                awaitingOfficialReturn = false;
                workspaceState = { documents: [] };
                confirmedFindingKeys.clear();
                form.reset();
                result.hidden = true;
                input.focus();
            });

            next?.addEventListener("click", (event) => {
                const builderLink = event.target instanceof Element
                    ? event.target.closest("[data-record-request-builder]")
                    : null;
                if (builderLink) {
                    saveRequestContext();
                }
            });

            function renderDocumentAnalysis(payload) {
                if (!(documentAnalysis instanceof HTMLElement)) {
                    return;
                }
                const wrapper = document.createElement("div");
                const headingElement = document.createElement("h5");
                const summary = document.createElement("p");
                const decision = payload.decision;

                if (decision && typeof decision === "object") {
                    const decisionPanel = document.createElement("section");
                    const decisionLabel = document.createElement("span");
                    const decisionTitle = document.createElement("h5");
                    const decisionAnswer = document.createElement("p");
                    const decisionLimit = document.createElement("p");
                    decisionPanel.className = `record-decision record-decision--${decision.level || "incomplete"}`;
                    decisionLabel.className = "record-decision__label";
                    decisionLabel.textContent = decision.label || "Document decision";
                    decisionTitle.textContent = decision.title || "More review is needed";
                    decisionAnswer.textContent = decision.answer || "";
                    decisionLimit.className = "record-decision__limit";
                    decisionLimit.textContent = decision.notProven ? `What this does not prove: ${decision.notProven}` : "";
                    decisionPanel.append(decisionLabel, decisionTitle, decisionAnswer);
                    if (Array.isArray(decision.supportedBy) && decision.supportedBy.length) {
                        const support = document.createElement("ul");
                        support.className = "record-decision__support";
                        decision.supportedBy.forEach((item) => {
                            const listItem = document.createElement("li");
                            listItem.textContent = item;
                            support.append(listItem);
                        });
                        decisionPanel.append(support);
                    }
                    if (decisionLimit.textContent) {
                        decisionPanel.append(decisionLimit);
                    }
                    wrapper.append(decisionPanel);
                }

                headingElement.textContent = payload.heading || "Extracted document facts";
                summary.textContent = payload.summary || "Review the extracted fields against the original document.";
                wrapper.append(headingElement, summary);

                if (Array.isArray(payload.findings) && payload.findings.length) {
                    const findings = document.createElement("dl");
                    findings.className = "record-document__findings";
                    payload.findings.forEach((finding) => {
                        const item = document.createElement("div");
                        const term = document.createElement("dt");
                        const value = document.createElement("dd");
                        const evidence = document.createElement("small");
                        term.textContent = finding.label || finding.key;
                        value.textContent = finding.value || "Mentioned";
                        evidence.textContent = finding.evidence ? `Source text: ${finding.evidence}` : "";
                        item.append(term, value);
                        if (evidence.textContent) {
                            item.append(evidence);
                        }
                        findings.append(item);
                    });
                    wrapper.append(findings);
                }

                if (Array.isArray(payload.missingItems) && payload.missingItems.length) {
                    const missingHeading = document.createElement("strong");
                    const missingList = document.createElement("ul");
                    missingHeading.textContent = "Still missing for this purpose";
                    payload.missingItems.forEach((item) => {
                        const listItem = document.createElement("li");
                        listItem.textContent = item;
                        missingList.append(listItem);
                    });
                    wrapper.append(missingHeading, missingList);
                }

                if (Array.isArray(payload.nextSteps) && payload.nextSteps.length) {
                    const nextHeading = document.createElement("strong");
                    const nextList = document.createElement("ol");
                    nextHeading.textContent = "Next steps";
                    payload.nextSteps.forEach((item) => {
                        const listItem = document.createElement("li");
                        listItem.textContent = item;
                        nextList.append(listItem);
                    });
                    wrapper.append(nextHeading, nextList);
                }

                const analysisActions = document.createElement("div");
                const downloadButton = document.createElement("button");
                const clearButton = document.createElement("button");
                analysisActions.className = "record-document__analysis-actions";
                downloadButton.type = "button";
                downloadButton.className = "button button--secondary";
                downloadButton.textContent = "Download review";
                downloadButton.addEventListener("click", () => downloadWorkspaceSummary(workspaceSummary([payload])));
                clearButton.type = "button";
                clearButton.className = "button button--quiet";
                clearButton.textContent = "Clear session result";
                clearButton.addEventListener("click", clearWorkspace);
                analysisActions.append(downloadButton, clearButton);
                wrapper.append(analysisActions);
                documentAnalysis.replaceChildren(wrapper);
                documentAnalysis.hidden = false;
            }

            function recordRequestKey(checklist) {
                const firstOpen = checklist.find((item) => item.status !== "complete");
                return {
                    permit_number: "permit_copy",
                    approved_bedrooms: "permit_copy",
                    final_approval: "final_approval",
                    layout: "as_built",
                    repair_history: "repair_record",
                    system_type: "permit_copy",
                    tank_capacity: "permit_copy",
                    design_flow: "permit_copy"
                }[firstOpen?.key] || "permit_copy";
            }

            function calculatorPath(summary) {
                const params = new URLSearchParams();
                if (routeContext?.stateCode) {
                    params.set("state", routeContext.stateCode);
                }
                const projectTypes = {
                    buying: "buying_home",
                    bedrooms: "new_install",
                    location: "inspection",
                    repair: "inspection",
                    replacement: "replacement",
                    lender: "inspection",
                    owner: "inspection"
                };
                params.set("projectType", projectTypes[routeContext?.purpose] || "inspection");
                const bedrooms = confirmedFindingKeys.has("approved_bedrooms")
                    ? summary.grouped.get("approved_bedrooms") || []
                    : [];
                const bedroomValues = [...new Set(bedrooms.map((entry) => entry.value))];
                if (bedroomValues.length === 1 && /^\d{1,2}$/.test(bedroomValues[0])) {
                    params.set("bedrooms", bedroomValues[0]);
                }
                [
                    ["system_type", "recordSystemType"],
                    ["tank_capacity", "recordTankCapacity"],
                    ["design_flow", "recordDesignFlow"]
                ].forEach(([findingKey, parameter]) => {
                    if (!confirmedFindingKeys.has(findingKey)) {
                        return;
                    }
                    const entries = summary.grouped.get(findingKey) || [];
                    const values = [...new Set(entries.map((entry) => entry.value))];
                    if (values.length === 1) {
                        params.set(parameter, values[0]);
                    }
                });
                params.set("recordsMode", "true");
                return `/septic-system-cost-calculator/?${params.toString()}`;
            }

            function renderPropertyWorkspace(summary) {
                if (!(documentAnalysis instanceof HTMLElement)) {
                    return;
                }
                const wrapper = document.createElement("div");
                wrapper.className = "record-workspace";

                const overview = document.createElement("section");
                overview.className = "record-workspace__overview";
                const progressCopy = document.createElement("div");
                const progressLabel = document.createElement("span");
                const progressTitle = document.createElement("h5");
                const progressBody = document.createElement("p");
                const progress = document.createElement("div");
                const progressBar = document.createElement("span");
                progressLabel.className = "record-decision__label";
                progressLabel.textContent = "Property file progress";
                progressTitle.textContent = summary.conflicts.length
                    ? "Resolve conflicting records before using the file"
                    : `${summary.completeCount} of ${summary.totalCount} checks complete`;
                progressBody.textContent = summary.conflicts.length
                    ? "Two documents report different values. Compare both originals or ask the file owner which record controls."
                    : summary.completeCount === summary.totalCount
                        ? "The core records for this purpose are present. This still does not prove current system condition."
                        : "Add another official record or request the missing items below. You will not need to re-enter confirmed facts.";
                progress.className = "record-workspace__progress";
                progress.setAttribute("role", "progressbar");
                progress.setAttribute("aria-valuemin", "0");
                progress.setAttribute("aria-valuemax", String(summary.totalCount));
                progress.setAttribute("aria-valuenow", String(summary.completeCount));
                progressBar.style.width = `${Math.round((summary.completeCount / Math.max(1, summary.totalCount)) * 100)}%`;
                progress.append(progressBar);
                progressCopy.append(progressLabel, progressTitle, progressBody, progress);

                const documentCount = document.createElement("div");
                const documentNumber = document.createElement("strong");
                const documentLabel = document.createElement("span");
                documentCount.className = "record-workspace__count";
                documentNumber.textContent = String(summary.documents.length);
                documentLabel.textContent = summary.documents.length === 1 ? "document reviewed" : "documents reviewed";
                documentCount.append(documentNumber, documentLabel);
                overview.append(progressCopy, documentCount);
                wrapper.append(overview);

                if (!summary.conflicts.length && summary.completeCount === summary.totalCount) {
                    const completion = document.createElement("section");
                    const completionCopy = document.createElement("div");
                    const completionHeading = document.createElement("strong");
                    const completionBody = document.createElement("p");
                    const finishButton = document.createElement("button");
                    completion.className = "record-workspace__completion";
                    completionHeading.textContent = "The core file checklist is complete";
                    completionBody.textContent = "You can stop here. Keep the originals with the property file, or continue only if your lender, buyer, contractor, or local office needs another document.";
                    finishButton.type = "button";
                    finishButton.className = "button button--secondary";
                    finishButton.textContent = "Finish this task";
                    finishButton.addEventListener("click", () => {
                        try {
                            localStorage.removeItem(taskProgressStorageKey);
                            localStorage.removeItem("septic-records-request-context");
                        } catch (_) {
                            // The completion message remains useful without browser storage.
                        }
                        finishButton.textContent = "Task finished";
                        finishButton.disabled = true;
                        recordFinderStage("task_finished");
                    });
                    completionCopy.append(completionHeading, completionBody);
                    completion.append(completionCopy, finishButton);
                    wrapper.append(completion);
                }

                const saveReminder = document.createElement("section");
                const saveCopy = document.createElement("div");
                const saveHeading = document.createElement("strong");
                const saveBody = document.createElement("p");
                const saveSession = document.createElement("button");
                saveReminder.className = "record-workspace__save";
                saveHeading.textContent = "Keep this work before you close the tab";
                saveBody.textContent = "Save a small session file containing the extracted facts and sources. It does not contain the original PDFs or scans.";
                saveSession.type = "button";
                saveSession.className = "button button--secondary";
                saveSession.textContent = "Save session for later";
                saveSession.addEventListener("click", downloadWorkspaceSession);
                saveCopy.append(saveHeading, saveBody);
                saveReminder.append(saveCopy, saveSession);
                wrapper.append(saveReminder);

                const checklistSection = document.createElement("section");
                const checklistHeading = document.createElement("h5");
                const checklist = document.createElement("ul");
                checklistSection.className = "record-workspace__section";
                checklistHeading.textContent = "What the property file confirms";
                checklist.className = "record-workspace__checklist";
                summary.checklist.forEach((check) => {
                    const item = document.createElement("li");
                    const marker = document.createElement("span");
                    const copy = document.createElement("div");
                    const label = document.createElement("strong");
                    const detail = document.createElement("small");
                    item.dataset.status = check.status;
                    marker.textContent = check.status === "complete" ? "✓" : check.status === "conflict" ? "!" : "○";
                    label.textContent = check.label;
                    detail.textContent = check.status === "complete"
                        ? `${check.values.join(" / ")} - ${check.sources.join(", ")}`
                        : check.status === "conflict"
                            ? `${check.values.join(" versus ")} - compare the originals`
                            : "Not found in the documents added so far";
                    copy.append(label, detail);
                    item.append(marker, copy);
                    checklist.append(item);
                });
                checklistSection.append(checklistHeading, checklist);
                wrapper.append(checklistSection);

                const confirmableChecks = summary.checklist.filter((check) => check.status === "complete");
                if (confirmableChecks.length) {
                    const verificationSection = document.createElement("section");
                    const verificationHeading = document.createElement("h5");
                    const verificationCopy = document.createElement("p");
                    const verificationList = document.createElement("div");
                    verificationSection.className = "record-workspace__section record-workspace__verification-section";
                    verificationHeading.textContent = "Confirm values before using them";
                    verificationCopy.textContent = "Check each value against the original page. Only checked values can prefill the cost calculator.";
                    verificationList.className = "record-workspace__verification-list";
                    confirmableChecks.forEach((check) => {
                        const verification = document.createElement("label");
                        const verificationInput = document.createElement("input");
                        const verificationText = document.createElement("span");
                        verificationInput.type = "checkbox";
                        verificationInput.checked = confirmedFindingKeys.has(check.key);
                        verificationText.textContent = `${check.label}: ${check.values.join(" / ")}`;
                        verificationInput.addEventListener("change", () => {
                            if (verificationInput.checked) {
                                confirmedFindingKeys.add(check.key);
                            } else {
                                confirmedFindingKeys.delete(check.key);
                            }
                            renderPropertyWorkspace(summary);
                        });
                        verification.append(verificationInput, verificationText);
                        verificationList.append(verification);
                    });
                    verificationSection.append(verificationHeading, verificationCopy, verificationList);
                    wrapper.append(verificationSection);
                }

                if (summary.conflicts.length) {
                    const conflictSection = document.createElement("section");
                    const conflictHeading = document.createElement("h5");
                    const conflictList = document.createElement("ul");
                    conflictSection.className = "record-workspace__section record-workspace__conflicts";
                    conflictHeading.textContent = "Values that need resolution";
                    summary.conflicts.forEach((conflict) => {
                        const item = document.createElement("li");
                        const label = document.createElement("strong");
                        const values = document.createElement("span");
                        label.textContent = conflict.label;
                        values.textContent = conflict.entries
                            .map((entry) => `${entry.value} (${entry.sourceFile})`)
                            .join(" vs. ");
                        item.append(label, values);
                        conflictList.append(item);
                    });
                    conflictSection.append(conflictHeading, conflictList);
                    wrapper.append(conflictSection);
                }

                if (summary.grouped.size) {
                    const factsSection = document.createElement("details");
                    const factsSummary = document.createElement("summary");
                    const facts = document.createElement("dl");
                    factsSection.className = "record-workspace__facts";
                    factsSummary.textContent = `Review all extracted facts (${summary.grouped.size})`;
                    facts.className = "record-document__findings";
                    summary.grouped.forEach((entries) => {
                        const item = document.createElement("div");
                        const term = document.createElement("dt");
                        const value = document.createElement("dd");
                        const evidenceList = document.createElement("ul");
                        const values = [...new Set(entries.map((entry) => entry.value))];
                        term.textContent = entries[0]?.label || entries[0]?.key;
                        value.textContent = values.join(" / ");
                        evidenceList.className = "record-workspace__evidence";
                        entries.forEach((entry) => {
                            const evidenceItem = document.createElement("li");
                            const source = document.createElement("strong");
                            const quote = document.createElement("blockquote");
                            source.textContent = entry.pageNumber
                                ? `${entry.sourceFile} · page ${entry.pageNumber}`
                                : entry.sourceFile;
                            quote.textContent = entry.evidence
                                ? `“${entry.evidence}”`
                                : "No source excerpt was captured. Check the original document.";
                            evidenceItem.append(source, quote);
                            evidenceList.append(evidenceItem);
                        });
                        item.append(term, value, evidenceList);
                        facts.append(item);
                    });
                    factsSection.append(factsSummary, facts);
                    wrapper.append(factsSection);
                }

                const files = document.createElement("p");
                files.className = "record-workspace__files";
                files.textContent = `Source names in this tab: ${summary.documents.map((item) => item.fileName || "Document").join(", ")}. Closing the tab clears the browser copy.`;
                wrapper.append(files);

                const ocrUsed = summary.documents.some((item) =>
                    String(item.summary || "").startsWith("OCR read typed text from this scan."));
                if (ocrUsed) {
                    const warning = document.createElement("p");
                    warning.className = "record-workspace__warning";
                    warning.textContent = "OCR was used for at least one document. Compare permit numbers, dates, bedroom counts, tank size, and flow with the original scan.";
                    wrapper.append(warning);
                }

                const actions = document.createElement("div");
                const hasMissing = summary.checklist.some((item) => item.status === "missing");
                const hasConflict = summary.conflicts.length > 0;
                const confirmedCount = confirmedFindingKeys.size;
                const request = button(
                    hasConflict
                        ? "Request the controlling record"
                        : hasMissing ? "Request the missing record" : "Ask the file owner a follow-up",
                    "/septic-records-request-builder/?mode=task#records-request-builder",
                    hasMissing || hasConflict,
                    "internal_tool"
                );
                const estimate = button(
                    hasConflict
                        ? "Plan cost without disputed values"
                        : confirmedCount
                            ? `Use ${confirmedCount} checked ${confirmedCount === 1 ? "value" : "values"} in cost planning`
                            : "Open cost planning without extracted values",
                    calculatorPath(summary),
                    false,
                    "calculator"
                );
                const download = document.createElement("button");
                const clear = document.createElement("button");
                actions.className = "record-document__analysis-actions";
                request.addEventListener("click", () => saveRequestContext({
                    requestedRecord: recordRequestKey(summary.checklist),
                    taskMode: true,
                    conflictNote: summary.conflicts.map((conflict) => {
                        const values = conflict.entries
                            .map((entry) => `${entry.value} in ${entry.sourceFile}`)
                            .join(" versus ");
                        return `${conflict.label}: ${values}`;
                    }).join("; ")
                }));
                download.type = "button";
                download.className = "button button--secondary";
                download.textContent = "Download readable summary";
                download.addEventListener("click", () => downloadWorkspaceSummary(summary));
                clear.type = "button";
                clear.className = "button button--quiet";
                clear.textContent = "Clear browser file";
                clear.addEventListener("click", clearWorkspace);
                actions.append(request, estimate, download, clear);
                wrapper.append(actions);

                const limit = document.createElement("p");
                limit.className = "record-decision__limit";
                limit.textContent = "This file review is not a property inspection, title opinion, code determination, or guarantee of system condition.";
                wrapper.append(limit);

                documentAnalysis.replaceChildren(wrapper);
                documentAnalysis.hidden = false;
            }

            function restoreSessionWorkspace() {
                let stored = null;
                let pending = null;
                try {
                    stored = JSON.parse(sessionStorage.getItem(workspaceStorageKey) || "null")
                        || JSON.parse(sessionStorage.getItem(legacyWorkspaceStorageKey) || "null");
                    pending = JSON.parse(sessionStorage.getItem(pendingReturnStorageKey) || "null")
                        || JSON.parse(localStorage.getItem(taskProgressStorageKey) || "null");
                } catch (_) {
                    return;
                }
                const storedDocuments = Array.isArray(stored?.documents)
                    ? stored.documents
                    : stored?.analysis ? [stored.analysis] : [];
                const active = storedDocuments.length ? stored : pending;
                const activeLifetime = storedDocuments.length ? 8 * 60 * 60 * 1000 : progressLifetime;
                const expired = active?.expiresAt
                    ? Date.now() > Number(active.expiresAt)
                    : Date.now() - Number(active?.savedAt || 0) > activeLifetime;
                if (!active?.context || expired) {
                    try {
                        localStorage.removeItem(taskProgressStorageKey);
                    } catch (_) {
                        // There is no persistent progress to clear when storage is unavailable.
                    }
                    return;
                }
                routeContext = active.context;
                const directDocumentSession = Boolean(storedDocuments.length && routeContext.directDocument);
                if (purposeSelect instanceof HTMLSelectElement && routeContext.purpose) {
                    purposeSelect.value = routeContext.purpose;
                }
                if (routeContext.matchedAddress) {
                    input.value = routeContext.matchedAddress;
                }
                result.hidden = false;
                if (status) {
                    status.textContent = directDocumentSession ? "Document session restored" : "Browser session restored";
                }
                if (heading) {
                    heading.textContent = directDocumentSession
                        ? "Continue checking your documents"
                        : routeContext.countyName
                        ? `Continue the ${routeContext.countyName} record check`
                        : "Continue the property record check";
                }
                if (message) {
                    message.textContent = directDocumentSession
                        ? "The extracted summary stayed in this browser tab. Add the original document again only if you need to re-run the extraction."
                        : storedDocuments.length
                        ? "The address context and extracted summary stayed only in this browser tab. The original document was not stored."
                        : "Your last official-route step was restored from this device. Choose what happened to continue.";
                }
                renderOffice(routeContext);
                renderSearchPacket(routeContext);
                if (directDocumentSession) {
                    if (officialNote instanceof HTMLElement) {
                        officialNote.hidden = true;
                    }
                    if (routeDetails instanceof HTMLDetailsElement) {
                        routeDetails.hidden = true;
                    }
                    if (returnPanel instanceof HTMLElement) {
                        returnPanel.hidden = true;
                    }
                } else {
                    showReturnPrompt();
                }
                if (pending?.outcome && outcomes instanceof HTMLElement) {
                    const savedButton = outcomes.querySelector(`[data-record-outcome="${pending.outcome}"]`);
                    if (savedButton instanceof HTMLButtonElement) {
                        savedButton.setAttribute("aria-pressed", "true");
                    }
                    renderNextStep(pending.outcome);
                }
                if (storedDocuments.length) {
                    workspaceState = { documents: storedDocuments.slice(-8) };
                    if (documentWorkspace instanceof HTMLElement) {
                        documentWorkspace.hidden = false;
                    }
                    renderPropertyWorkspace(workspaceSummary(workspaceState.documents));
                    if (documentStatus) {
                        documentStatus.textContent = `Restored ${workspaceState.documents.length} document ${workspaceState.documents.length === 1 ? "summary" : "summaries"} from this browser session.`;
                    }
                }
            }

            restoreSessionWorkspace();

            workspaceImport?.addEventListener("change", async () => {
                if (!(workspaceImport instanceof HTMLInputElement) || !workspaceImport.files?.length) {
                    return;
                }
                const file = workspaceImport.files[0];
                if (file.size > 1024 * 1024) {
                    if (documentStatus) {
                        documentStatus.textContent = "The saved session file must be 1 MB or smaller.";
                    }
                    workspaceImport.value = "";
                    return;
                }
                try {
                    const imported = normalizeImportedWorkspace(JSON.parse(await file.text()));
                    applyImportedWorkspace(imported);
                    if (documentStatus) {
                        documentStatus.textContent = `Resumed ${imported.documents.length} reviewed document ${imported.documents.length === 1 ? "summary" : "summaries"}.`;
                    }
                } catch (error) {
                    if (documentStatus) {
                        documentStatus.textContent = error instanceof Error
                            ? error.message
                            : "This saved session could not be resumed.";
                    }
                } finally {
                    workspaceImport.value = "";
                }
            });

            const analyzeRecordSource = async (file, sourceType, submitButton) => {
                const data = new FormData();
                data.append("file", file);
                data.append("purpose", routeContext?.purpose || currentPurpose());
                data.append("stateCode", routeContext?.stateCode || "");
                data.append("countyName", routeContext?.countyName || "");
                const originalLabel = submitButton.textContent;
                submitButton.disabled = true;
                submitButton.textContent = "Analyzing...";
                if (documentStatus) {
                    documentStatus.textContent = sourceType === "pasted"
                        ? "Checking the pasted details in memory. Nothing is being saved."
                        : "Reading the source in memory. Photos and scans may take longer while OCR runs. Nothing is being saved.";
                }
                if (typeof window.gtag === "function") {
                    window.gtag("event", "document_upload_started", { source_type: sourceType });
                }
                try {
                    const response = await fetch("/api/septic-document-analyzer", {
                        method: "POST",
                        headers: { Accept: "application/json" },
                        body: data
                    });
                    const payload = await response.json();
                    if (response.ok) {
                        const summary = addDocumentToWorkspace(payload);
                        saveWorkspace();
                        renderPropertyWorkspace(summary);
                        recordFinderStage("document_reviewed");
                        if (!summary.conflicts.length && summary.completeCount === summary.totalCount) {
                            recordFinderStage("property_file_ready");
                        }
                        sendArtifactAction("address_record_finder", sourceType, "property_record");
                        if (typeof window.gtag === "function") {
                            window.gtag("event", "record_finder_document_added", {
                                state_code: routeContext?.stateCode || "unknown",
                                county_name: routeContext?.countyName || "unknown",
                                source_type: sourceType,
                                document_count: workspaceState.documents.length
                            });
                            if (requestedCountyKey && requestedWorkflowRunId) {
                                const [requestedStateCode = "", requestedCountySlug = ""] = requestedCountyKey.split("::");
                                window.gtag("event", "county_record_obtained", {
                                    county_key: requestedCountyKey,
                                    state_code: requestedStateCode,
                                    county_slug: requestedCountySlug,
                                    workflow_run_id: requestedWorkflowRunId,
                                    result_source: "upload_detected",
                                    case_status: "needs_document_review",
                                    artifact_count: workspaceState.documents.length
                                });
                            }
                        }
                        if (typeof window.gtag === "function") {
                            window.gtag("event", "document_upload_completed", { source_type: sourceType, outcome: "success" });
                        }
                    } else {
                        renderDocumentAnalysis(payload);
                        if (typeof window.gtag === "function") {
                            window.gtag("event", "document_upload_completed", { source_type: sourceType, outcome: "rejected" });
                        }
                    }
                    if (documentStatus) {
                        documentStatus.textContent = response.ok
                            ? `${payload.fileName || "Document"} added. Add another record to fill the remaining gaps.`
                            : (payload.summary || "The file could not be analyzed.");
                    }
                    return response.ok;
                } catch (_) {
                    if (documentStatus) {
                        documentStatus.textContent = "Analysis could not connect. The file was not saved.";
                    }
                    if (typeof window.gtag === "function") {
                        window.gtag("event", "document_upload_completed", { source_type: sourceType, outcome: "network_error" });
                    }
                    return false;
                } finally {
                    submitButton.disabled = false;
                    submitButton.textContent = originalLabel;
                }
            };

            documentForm?.addEventListener("submit", async (event) => {
                event.preventDefault();
                if (!(documentFile instanceof HTMLInputElement)
                    || !(documentSubmit instanceof HTMLButtonElement)
                    || !documentFile.files?.length) {
                    if (documentStatus) {
                        documentStatus.textContent = "Choose a PDF, photo, or text file.";
                    }
                    return;
                }
                await analyzeRecordSource(documentFile.files[0], "uploaded", documentSubmit);
            });

            documentPasteSubmit?.addEventListener("click", async () => {
                if (!(documentPaste instanceof HTMLTextAreaElement)
                    || !(documentPasteSubmit instanceof HTMLButtonElement)) {
                    return;
                }
                const pastedText = documentPaste.value.trim();
                if (pastedText.length < 30) {
                    if (documentStatus) {
                        documentStatus.textContent = "Paste at least a few lines of record text so there is enough context to check.";
                    }
                    documentPaste.focus();
                    return;
                }
                const pastedFile = new File([pastedText], "pasted-official-record.txt", { type: "text/plain" });
                const added = await analyzeRecordSource(pastedFile, "pasted", documentPasteSubmit);
                if (added) {
                    documentPaste.value = "";
                }
            });

            window.addEventListener("focus", () => {
                if (!awaitingOfficialReturn) {
                    return;
                }
                awaitingOfficialReturn = false;
                try {
                    sessionStorage.removeItem(pendingReturnStorageKey);
                } catch (_) {
                    // The prompt is already visible.
                }
                recordFinderStage("official_returned");
                showReturnPrompt();
            });

            form.addEventListener("submit", async (event) => {
                event.preventDefault();
                const address = input.value.trim();
                if (!isFullUsAddress(address)) {
                    render({
                        status: "invalid",
                        heading: "Enter a full U.S. property address",
                        message: "Include the street, city, and a state abbreviation or ZIP so the county can be resolved reliably."
                    });
                    input.focus();
                    return;
                }

                recordFinderStage("workflow_viewed");
                if (typeof window.gtag === "function") {
                    window.gtag("event", "address_search_started", { search_purpose: currentPurpose() });
                }
                const defaultLabel = "Find septic records";
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
                    if (typeof window.gtag === "function") {
                        window.gtag("event", "address_search_completed", { search_purpose: currentPurpose(), outcome: payload.status || "unknown" });
                    }
                    if (typeof window.gtag === "function") {
                        window.gtag("event", "record_finder_submit", {
                            finder_status: payload.status || "unknown",
                            finder_route_type: payload.status === "county_route" ? "county" : "state"
                        });
                    }
                } catch (_) {
                    if (typeof window.gtag === "function") {
                        window.gtag("event", "address_search_completed", { search_purpose: currentPurpose(), outcome: "network_error" });
                    }
                    render({
                        status: "unavailable",
                        heading: "Use the county finder while address lookup reconnects",
                        message: "No address was saved. Search by county to open the best available local records route and see its verification depth.",
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

    function setupTdecSearchWorkspace() {
        const workspace = document.querySelector("[data-tdec-search-workspace]");
        if (!(workspace instanceof HTMLElement)) return;

        const method = workspace.querySelector("[data-tdec-search-method]");
        const clue = workspace.querySelector("[data-tdec-search-clue]");
        const clueLabel = workspace.querySelector("[data-tdec-search-label]");
        const copyButton = workspace.querySelector("[data-tdec-search-copy]");
        const openLink = workspace.querySelector("[data-tdec-search-open]");
        const status = workspace.querySelector("[data-tdec-search-status]");
        const methodCopy = {
            address: {label: "Property address", placeholder: "Enter the Tennessee property address"},
            parcel: {label: "Parcel or tax-map ID", placeholder: "Enter the parcel or tax-map ID"},
            owner: {label: "Current or prior owner", placeholder: "Enter the owner name"},
            permit: {label: "Permit number", placeholder: "Enter the septic permit number"}
        };

        function emitTdecEvent(eventName, extra = {}) {
            if (typeof window.gtag !== "function") return;
            window.gtag("event", eventName, {
                page_path: window.location.pathname,
                search_field: method instanceof HTMLSelectElement ? method.value : "unknown",
                ...extra
            });
        }

        function updateMethod() {
            if (!(method instanceof HTMLSelectElement) || !(clue instanceof HTMLInputElement)) return;
            const selected = methodCopy[method.value] || methodCopy.address;
            if (clueLabel instanceof HTMLElement) clueLabel.textContent = selected.label;
            clue.placeholder = selected.placeholder;
            clue.value = "";
            if (status instanceof HTMLElement) {
                status.textContent = `Add the ${selected.label.toLowerCase()}, copy it, then open the official TDEC search.`;
                status.classList.remove("is-warning");
            }
        }

        const requestedAddress = new URLSearchParams(window.location.search).get("address")?.trim() || "";
        if (requestedAddress && method instanceof HTMLSelectElement && clue instanceof HTMLInputElement) {
            method.value = "address";
            clue.value = requestedAddress.slice(0, 140);
        }

        method?.addEventListener("change", updateMethod);

        async function copyClue() {
            if (!(clue instanceof HTMLInputElement) || !(method instanceof HTMLSelectElement)) return;
            const value = clue.value.trim().replace(/\s+/g, " ").slice(0, 140);
            clue.value = value;
            if (value.length < 2) {
                if (status instanceof HTMLElement) {
                    status.textContent = "Enter an address, parcel, owner, or permit clue before opening TDEC.";
                    status.classList.add("is-warning");
                }
                clue.focus();
                return;
            }
            try {
                await copyText(value);
                if (status instanceof HTMLElement) {
                    status.textContent = `${value} copied. Open TDEC and paste it into the matching search field.`;
                    status.classList.remove("is-warning");
                }
            } catch (_) {
                clue.select();
                if (status instanceof HTMLElement) {
                    status.textContent = "Clipboard access was unavailable. Copy the selected clue, then open TDEC.";
                    status.classList.add("is-warning");
                }
            }
            sendArtifactAction("tdec_search_workspace", "search_clue_prepared", method.value);
            emitTdecEvent("tdec_search_clue_prepared");
        }

        copyButton?.addEventListener("click", copyClue);
        clue?.addEventListener("keydown", (event) => {
            if (event.key === "Enter") {
                event.preventDefault();
                copyClue();
            }
        });
        openLink?.addEventListener("click", () => {
            sendArtifactAction("tdec_search_workspace", "official_search_opened", method instanceof HTMLSelectElement ? method.value : "unknown");
            emitTdecEvent("tdec_official_search_opened");
        });
    }

    setupTdecSearchWorkspace();

    function setupCountyAccessWorkflows() {
        const workflows = Array.from(document.querySelectorAll("[data-county-access-workflow]"));
        if (!workflows.length) {
            return;
        }

        const lifetime = 30 * 24 * 60 * 60 * 1000;

        workflows.forEach((workflow) => {
            const countyKey = workflow.dataset.countyKey || window.location.pathname;
            const mode = workflow.dataset.countyMode || "official_request";
            const acquisitionMethod = workflow.dataset.countyAcquisitionMethod || "";
            const profileScope = workflow.dataset.countyProfileScope || "official_starting_point";
            const capabilityTier = workflow.dataset.countyCapabilityTier || "official_start";
            const routeReviewedAt = workflow.dataset.countyRouteReviewedAt || "not_published";
            const agency = workflow.dataset.countyAgency || "";
            const requiresAddressOrParcel = workflow.dataset.countyAddressOrParcel === "true";
            const requiresDocumentSelection = workflow.dataset.countyDocumentSelectionRequired === "true";
            const requestedPropertyAddress = new URLSearchParams(window.location.search).get("address")?.trim() || "";
            const requestedPurpose = new URLSearchParams(window.location.search).get("purpose")?.trim() || "";
            const requestedWorkflowRunId = new URLSearchParams(window.location.search).get("workflowRunId")?.trim() || "";
            const requestedResume = new URLSearchParams(window.location.search).get("resume") === "1";
            const primaryUrl = workflow.dataset.countyPrimaryUrl || "";
            const secondaryUrl = workflow.dataset.countySecondaryUrl || "";
            const address = workflow.querySelector("[data-county-access-address]");
            const parcel = workflow.querySelector("[data-county-access-parcel]");
            const copy = workflow.querySelector("[data-county-access-copy]");
            const status = workflow.querySelector("[data-county-access-status]");
            const returnPanel = workflow.querySelector("[data-county-access-return]");
            const next = workflow.querySelector("[data-county-access-next]");
            const clear = workflow.querySelector("[data-county-access-clear]");
            const reference = workflow.querySelector("[data-county-access-reference]");
            const outcomes = workflow.querySelectorAll("[data-county-access-outcome]");
            const officialLinks = document.querySelectorAll("[data-county-access-official]");
            const storageKey = `septicpath-county-access-v1:${countyKey}`;
            const officialReturnStorageKey = "septicpath-official-return-v1";
            const recordTaskStorageKey = "septicpath-record-task-progress-v1";
            let awaitingReturn = false;
            let gaPreparationStarted = false;
            let gaLastOutcome = "";
            let workflowRunId = "";
            let workflowReturnRecorded = false;
            const gaReadyPaths = new Set();
            const [stateCode = "", countySlug = ""] = countyKey.split("::");

            function emitCountyGaEvent(eventName, extra = {}) {
                emitGaEvent(eventName, {
                    county_key: countyKey,
                    state_code: stateCode,
                    county_slug: countySlug,
                    access_mode: mode,
                    acquisition_method: acquisitionMethod || "not_published",
                    profile_scope: profileScope,
                    capability_tier: capabilityTier,
                    route_reviewed_at: routeReviewedAt,
                    workflow_run_id: workflowRunId,
                    ...extra
                });
            }

            function recordCountyStage(stage, outcome = "") {
                sendWorkflowStage("county_access_workflow", workflowRunId, countyKey, stage, outcome);
            }

            function markGaPreparationStarted(entryPoint) {
                if (gaPreparationStarted) {
                    return;
                }
                gaPreparationStarted = true;
                emitCountyGaEvent("county_prepare_started", { entry_point: entryPoint });
                recordCountyStage("preparation_started");
            }

            function safeValue(input) {
                return input instanceof HTMLInputElement ? input.value.trim() : "";
            }

            function readState() {
                try {
                    const value = JSON.parse(localStorage.getItem(storageKey) || "null");
                    if (!value || typeof value !== "object" || Date.now() - Number(value.updatedAt || 0) > lifetime) {
                        localStorage.removeItem(storageKey);
                        return null;
                    }
                    return value;
                } catch (_) {
                    return null;
                }
            }

            function writeState(patch) {
                const previous = readState() || {};
                const value = {
                    ...previous,
                    ...patch,
                    address: safeValue(address),
                    parcel: safeValue(parcel),
                    reference: safeValue(reference),
                    mode,
                    updatedAt: Date.now()
                };
                try {
                    localStorage.setItem(storageKey, JSON.stringify(value));
                } catch (_) {
                    // The visible workflow continues without persistent browser storage.
                }
                return value;
            }

            const initialWorkflowState = readState();
            workflowRunId = requestedWorkflowRunId || String(initialWorkflowState?.workflowRunId || "");
            if (!workflowRunId) {
                workflowRunId = typeof window.crypto?.randomUUID === "function"
                    ? window.crypto.randomUUID()
                    : `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`;
                writeState({ workflowRunId, stage: "viewed" });
            } else if (requestedWorkflowRunId && requestedWorkflowRunId !== initialWorkflowState?.workflowRunId) {
                writeState({ workflowRunId, stage: "viewed", outcome: "" });
            }
            emitCountyGaEvent("county_workflow_viewed");
            recordCountyStage("workflow_viewed");

            function showReturn() {
                if (returnPanel instanceof HTMLElement) {
                    returnPanel.hidden = false;
                }
            }

            function countyNameFromSlug() {
                return countySlug
                    .split("-")
                    .filter(Boolean)
                    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
                    .join(" ");
            }

            function saveDocumentWorkspaceHandoff() {
                const now = Date.now();
                const context = {
                    matchedAddress: safeValue(address),
                    countyKey,
                    countyName: countyNameFromSlug(),
                    stateCode,
                    stateName: stateCode,
                    purpose: requestedPurpose || "buying",
                    officeLabel: agency || `${countyNameFromSlug()} septic records office`,
                    contactLine: "Continue from the county route you already prepared. Add the returned file or written response here.",
                    routeReviewedAt,
                    workflowRunId,
                    directDocument: false
                };
                const value = JSON.stringify({
                    savedAt: now,
                    expiresAt: now + lifetime,
                    stage: "official_returned",
                    outcome: "found",
                    context
                });
                try {
                    sessionStorage.setItem(officialReturnStorageKey, value);
                    localStorage.setItem(recordTaskStorageKey, value);
                } catch (_) {
                    // Query parameters still preserve the county and workflow identifiers.
                }
                writeState({ stage: "document_handoff", outcome: "artifact" });
                recordCountyStage("document_handoff", "artifact");
                emitCountyGaEvent("county_document_handoff", {
                    result_source: "user_reported",
                    case_status: "needs_document_review"
                });
            }

            function actionLink(label, path, primary = false) {
                const link = document.createElement("a");
                link.className = `button ${primary ? "button--primary" : "button--secondary"}`;
                link.textContent = label;
                link.href = path;
                if (typeof path === "string" && path.startsWith("/septic-record-finder/")) {
                    link.addEventListener("click", saveDocumentWorkspaceHandoff);
                }
                if (/^https?:/i.test(path)) {
                    link.target = "_blank";
                    link.rel = "noreferrer";
                }
                return link;
            }

            function actionButton(label, primary = false) {
                const button = document.createElement("button");
                button.className = `button ${primary ? "button--primary" : "button--secondary"}`;
                button.type = "button";
                button.textContent = label;
                return button;
            }

            function resumeUrl() {
                const url = new URL(window.location.pathname, window.location.origin);
                url.searchParams.set("resume", "1");
                url.searchParams.set("workflowRunId", workflowRunId);
                url.hash = "county-access-return";
                return url.toString();
            }

            function calendarTimestamp(date) {
                return date.toISOString().replace(/[-:]/g, "").replace(/\.\d{3}Z$/, "Z");
            }

            function calendarText(value) {
                return String(value || "")
                    .replace(/\\/g, "\\\\")
                    .replace(/\r?\n/g, "\\n")
                    .replace(/;/g, "\\;")
                    .replace(/,/g, "\\,");
            }

            function addBusinessDays(date, days) {
                const result = new Date(date);
                let added = 0;
                while (added < days) {
                    result.setDate(result.getDate() + 1);
                    const day = result.getDay();
                    if (day !== 0 && day !== 6) {
                        added += 1;
                    }
                }
                return result;
            }

            function followupSchedule() {
                return countyKey === "NC::alamance-county"
                    ? { days: 3, businessDays: true, label: "3-business-day" }
                    : { days: 7, businessDays: false, label: "7-day" };
            }

            function downloadFollowupCalendar(outcome, status) {
                const createdAt = new Date();
                const schedule = followupSchedule();
                const startsAt = schedule.businessDays
                    ? addBusinessDays(createdAt, schedule.days)
                    : new Date(createdAt.getTime() + schedule.days * 24 * 60 * 60 * 1000);
                const endsAt = new Date(startsAt.getTime() + 30 * 60 * 1000);
                const countyName = countyNameFromSlug();
                const returnUrl = resumeUrl();
                const calendar = [
                    "BEGIN:VCALENDAR",
                    "VERSION:2.0",
                    "PRODID:-//SepticPath//County records follow-up//EN",
                    "CALSCALE:GREGORIAN",
                    "METHOD:PUBLISH",
                    "BEGIN:VEVENT",
                    `UID:${calendarText(workflowRunId)}@septicpath.com`,
                    `DTSTAMP:${calendarTimestamp(createdAt)}`,
                    `DTSTART:${calendarTimestamp(startsAt)}`,
                    `DTEND:${calendarTimestamp(endsAt)}`,
                    `SUMMARY:${calendarText(`Check ${countyName} septic records request`)}`,
                    `DESCRIPTION:${calendarText("Return to SepticPath and record whether the county sent a document or a written response. Property details and request numbers are not included in this reminder.")}`,
                    `URL:${calendarText(returnUrl)}`,
                    "END:VEVENT",
                    "END:VCALENDAR",
                    ""
                ].join("\r\n");
                const blob = new Blob([calendar], { type: "text/calendar;charset=utf-8" });
                const objectUrl = URL.createObjectURL(blob);
                const download = document.createElement("a");
                download.href = objectUrl;
                download.download = `septicpath-${countySlug || "county"}-follow-up.ics`;
                document.body.append(download);
                download.click();
                download.remove();
                window.setTimeout(() => URL.revokeObjectURL(objectUrl), 0);
                writeState({ followupScheduledAt: createdAt.getTime(), followupDueAt: startsAt.getTime() });
                recordCountyStage("followup_scheduled", outcome);
                emitCountyGaEvent("county_followup_calendar_downloaded", {
                    followup_days: schedule.days,
                    followup_business_days: schedule.businessDays,
                    result_source: "user_scheduled"
                });
                if (status instanceof HTMLElement) {
                    status.textContent = "Calendar reminder downloaded. It contains no address, parcel ID, or request number.";
                }
            }

            async function copyResumeLink(outcome, status) {
                const returnUrl = resumeUrl();
                let copied = false;
                try {
                    await navigator.clipboard.writeText(returnUrl);
                    copied = true;
                } catch (_) {
                    const fallback = document.createElement("textarea");
                    fallback.value = returnUrl;
                    fallback.setAttribute("readonly", "");
                    fallback.style.position = "fixed";
                    fallback.style.opacity = "0";
                    document.body.append(fallback);
                    fallback.select();
                    copied = document.execCommand("copy");
                    fallback.remove();
                }
                if (copied) {
                    recordCountyStage("resume_link_copied", outcome);
                    emitCountyGaEvent("county_resume_link_copied", {
                        result_source: "user_copied"
                    });
                }
                if (status instanceof HTMLElement) {
                    status.textContent = copied
                        ? "Return link copied. It identifies only this county workflow, not the property or request number."
                        : "Copy was blocked. Use the calendar reminder or bookmark this page instead.";
                }
            }

            function appendFollowupActions(block, outcome) {
                const schedule = followupSchedule();
                const followup = document.createElement("div");
                followup.className = "county-access-followup";
                const heading = document.createElement("strong");
                heading.textContent = "Come back when the county replies.";
                const body = document.createElement("p");
                body.textContent = `Set a ${schedule.label} calendar check or copy a private-safe return link. The reminder never includes the address, parcel ID, or county reference.`;
                const actions = document.createElement("div");
                actions.className = "county-access-workflow__actions";
                const calendarButton = actionButton(`Add a ${schedule.label} calendar reminder`, true);
                const copyButton = actionButton("Copy private-safe return link");
                const status = document.createElement("span");
                status.className = "county-access-followup__status";
                status.setAttribute("aria-live", "polite");
                const savedDueAt = Number(readState()?.followupDueAt || 0);
                if (savedDueAt > Date.now()) {
                    status.textContent = `Reminder already created for ${new Date(savedDueAt).toLocaleDateString()}.`;
                }
                calendarButton.addEventListener("click", () => downloadFollowupCalendar(outcome, status));
                copyButton.addEventListener("click", () => copyResumeLink(outcome, status));
                actions.append(calendarButton, copyButton);
                followup.append(heading, body, actions, status);
                block.append(followup);
            }

            function workspacePath() {
                const params = new URLSearchParams();
                if (safeValue(address)) {
                    params.set("address", safeValue(address));
                }
                if (requestedPurpose) {
                    params.set("purpose", requestedPurpose);
                }
                params.set("countyKey", countyKey);
                params.set("workflowRunId", workflowRunId);
                return `/septic-record-finder/${params.toString() ? `?${params.toString()}` : ""}`;
            }

            function requestPath() {
                return "/septic-records-request-builder/?mode=task#records-request-builder";
            }

            function externalSecondaryUrl() {
                return /^(https?:|mailto:|tel:)/i.test(secondaryUrl) ? secondaryUrl : "";
            }

            function officialActionUrl(outcome = "") {
                const secondary = externalSecondaryUrl();
                if (acquisitionMethod === "official_route_blocked") {
                    return secondary || primaryUrl;
                }
                if (countyKey === "TX::tarrant-county") {
                    return primaryUrl;
                }
                if (countyKey === "AZ::maricopa-county" && outcome !== "artifact") {
                    return secondary || primaryUrl;
                }
                if ((outcome === "not_found_online" || outcome === "blocked")
                    || (outcome === "partial" && acquisitionMethod === "official_search")) {
                    return secondary || primaryUrl;
                }
                return primaryUrl;
            }

            function officialActionLabel(prefix = "Return to") {
                const route = {
                    official_search: "the official search",
                    official_pdf: "the official PDF",
                    official_portal: "the official portal",
                    official_contact_form: "the official contact form",
                    official_contact: "the official OSSF contact",
                    official_phone: "the official phone instructions",
                    official_route_blocked: "the verified county contact"
                }[acquisitionMethod] || "the official route";
                return `${prefix} ${route}`;
            }

            function appendOfficialAction(actions, label = officialActionLabel(), primary = false, outcome = "") {
                const path = officialActionUrl(outcome);
                if (path) {
                    const fallback = externalSecondaryUrl();
                    const resolvedLabel = outcome
                        && fallback
                        && path === fallback
                        && path !== primaryUrl
                        ? "Use the verified request fallback"
                        : label;
                    actions.append(actionLink(resolvedLabel, path, primary));
                }
            }

            function renderNext(outcome) {
                if (!(next instanceof HTMLElement)) {
                    return;
                }
                const block = document.createElement("div");
                const heading = document.createElement("strong");
                const body = document.createElement("p");
                const actions = document.createElement("div");
                actions.className = "county-access-workflow__actions";

                if (outcome === "artifact") {
                    heading.textContent = "Add the document to the property file.";
                    body.textContent = "The upload workspace extracts permit facts with source evidence and keeps missing documents visible.";
                    actions.append(actionLink("Upload and review the document", workspacePath(), true));
                } else if (outcome === "partial") {
                    heading.textContent = "Keep the partial file and request only what is missing.";
                    body.textContent = acquisitionMethod
                        ? "Upload what you found, then return through this county's verified route for the missing item. SepticPath does not substitute an unverified request form."
                        : "Upload the document you found, then prepare a narrower request for the missing artifact.";
                    actions.append(actionLink("Add the partial document", workspacePath(), true));
                    if (acquisitionMethod) {
                        appendOfficialAction(actions, officialActionLabel(), false, outcome);
                    } else {
                        actions.append(actionLink("Prepare the missing-file request", requestPath()));
                    }
                } else if (outcome === "not_found_online") {
                    heading.textContent = "No online result is not an official no-record finding.";
                    if (acquisitionMethod === "official_search") {
                        body.textContent = "Retry with another verified property identifier, then use the county's own fallback route if the index still returns nothing.";
                    } else if (acquisitionMethod) {
                        body.textContent = "Use this county's verified PDF, portal, or contact route. Do not treat a blank web result as proof that no property file exists.";
                    } else {
                        body.textContent = "Ask the responsible office to search by the property identifiers it accepts.";
                    }
                    if (acquisitionMethod) {
                        appendOfficialAction(actions, officialActionLabel("Use"), true, outcome);
                    } else {
                        actions.append(actionLink("Prepare an official records request", requestPath(), true));
                        if (secondaryUrl) {
                            actions.append(actionLink("Open the fallback route", secondaryUrl));
                        }
                    }
                } else if (outcome === "blocked") {
                    heading.textContent = "Stop retrying the blocked route.";
                    body.textContent = acquisitionMethod
                        ? "Use the alternate official contact shown for this county and preserve the property details you already collected."
                        : "Use the documented office or request fallback and preserve the property details you already collected.";
                    if (acquisitionMethod) {
                        const blockedLabel = {
                            "TX::tarrant-county": "Use the official OSSF office",
                            "NC::lincoln-county": "Use the official Environmental Health page",
                            "TN::blount-county": "Use the official SSDS form fallback",
                            "NY::suffolk-county": "Review the official phone instructions"
                        }[countyKey] || officialActionLabel("Use");
                        appendOfficialAction(actions, blockedLabel, true, outcome);
                    } else {
                        actions.append(actionLink("Prepare the fallback request", requestPath(), true));
                        if (secondaryUrl) {
                            actions.append(actionLink("Open the alternate route", secondaryUrl));
                        }
                    }
                } else if (outcome === "followup_due") {
                    heading.textContent = "Follow up on the existing-file request now.";
                    body.textContent = countyKey === "NC::alamance-county"
                        ? "The county's stated three-business-day window has passed. Reopen the request form or call Environmental Health with the property address or GPIN ready."
                        : "The expected response window has passed. Reopen the verified county route with the property details you already collected.";
                    appendOfficialAction(actions, officialActionLabel("Reopen"), true, outcome);
                    actions.append(actionLink("Open the document workspace", workspacePath()));
                } else {
                    heading.textContent = "Track the request until the office sends a property-specific result.";
                    const savedReference = safeValue(reference);
                    body.textContent = savedReference
                        ? `Saved county reference: ${savedReference}. A submitted request is pending until the office sends a file or written no-record response.`
                        : "A submitted request is pending, not complete. Save the confirmation above and return with the reply or written no-record response.";
                    if (acquisitionMethod) {
                        appendOfficialAction(actions, officialActionLabel("Reopen"), true, outcome);
                        actions.append(actionLink("Open the document workspace", workspacePath()));
                    } else {
                        actions.append(actionLink("Open the document workspace", workspacePath(), true));
                    }
                }

                block.append(heading, body, actions);
                if (outcome === "request_submitted") {
                    appendFollowupActions(block, outcome);
                }
                next.replaceChildren(block);
            }

            const saved = readState();
            if (saved) {
                if (address instanceof HTMLInputElement && saved.address && !requestedPropertyAddress) {
                    address.value = String(saved.address).slice(0, 180);
                }
                if (parcel instanceof HTMLInputElement && saved.parcel) {
                    parcel.value = String(saved.parcel).slice(0, 100);
                }
                if (reference instanceof HTMLInputElement && saved.reference) {
                    reference.value = String(saved.reference).slice(0, 120);
                }
                if (saved.stage === "official_opened" || saved.outcome) {
                    showReturn();
                }
                if (saved.outcome) {
                    outcomes.forEach((button) => {
                        if (button.dataset.countyAccessOutcome === saved.outcome) {
                            button.setAttribute("aria-pressed", "true");
                        } else {
                            button.removeAttribute("aria-pressed");
                        }
                    });
                    renderNext(saved.outcome);
                }
            }
            if (requestedResume) {
                showReturn();
                recordCountyStage("followup_resumed", saved?.outcome || "pending");
                emitCountyGaEvent("county_followup_resumed", {
                    result_source: "reminder_or_saved_link"
                });
                window.setTimeout(() => returnPanel?.scrollIntoView({ behavior: "smooth", block: "start" }), 150);
            }
            if (address instanceof HTMLInputElement && requestedPropertyAddress) {
                address.value = requestedPropertyAddress.slice(0, 180);
                writeState({ stage: "prepared", purpose: requestedPurpose });
                markGaPreparationStarted("address_handoff");
            }

            [address, parcel].forEach((input) => {
                input?.addEventListener("input", () => {
                    markGaPreparationStarted(input === address ? "property_address" : "parcel_id");
                    writeState({ stage: "prepared" });
                    if (status instanceof HTMLElement) {
                        status.textContent = "Property clues saved on this device.";
                    }
                });
            });

            reference?.addEventListener("input", () => {
                const savedOutcome = readState()?.outcome || "";
                writeState({ stage: savedOutcome ? "outcome_recorded" : "official_opened" });
                if (savedOutcome) {
                    renderNext(savedOutcome);
                }
            });

            copy?.addEventListener("click", async () => {
                const lines = [
                    safeValue(address) ? `Property address: ${safeValue(address)}` : "",
                    safeValue(parcel) ? `Parcel / local ID: ${safeValue(parcel)}` : "",
                    `County access mode: ${mode.replaceAll("_", " ")}`,
                    primaryUrl ? `Official route: ${primaryUrl}` : ""
                ].filter(Boolean);
                if (!lines.length) {
                    if (status instanceof HTMLElement) {
                        status.textContent = "Add an address or parcel identifier first.";
                    }
                    address?.focus();
                    return;
                }
                try {
                    await copyText(lines.join("\n"));
                    if (status instanceof HTMLElement) {
                        status.textContent = "Search details copied.";
                    }
                    sendArtifactAction("county_access_workflow", "search_details_copied", mode);
                } catch (_) {
                    if (status instanceof HTMLElement) {
                        status.textContent = "Copy unavailable. Select the details manually.";
                    }
                }
            });

            const acquisitionWorkspace = workflow.querySelector("[data-county-acquisition-workspace]");
            const acquisitionTemplate = workflow.querySelector("[data-county-acquisition-template]");
            const acquisitionPreview = workflow.querySelector("[data-county-acquisition-preview]");
            const acquisitionCopy = workflow.querySelector("[data-county-acquisition-copy]");
            const acquisitionInputCopy = workflow.querySelector("[data-county-acquisition-input-copy]");
            const acquisitionEmail = workflow.querySelector("[data-county-acquisition-email]");
            const acquisitionDownload = workflow.querySelector("[data-county-acquisition-download]");
            const acquisitionPrint = workflow.querySelector("[data-county-acquisition-print]");
            const acquisitionNextCopy = workflow.querySelector("[data-county-acquisition-next-copy]");
            const handoffTemplate = workflow.querySelector("[data-county-handoff-template]");
            const handoffPreview = workflow.querySelector("[data-county-handoff-preview]");
            const handoffCopy = workflow.querySelector("[data-county-handoff-copy]");
            const officialPdfPrepare = workflow.querySelector("[data-county-official-pdf-prepare]");
            const preparationDownload = workflow.querySelector("[data-county-preparation-download]");
            const preparationPrint = workflow.querySelector("[data-county-preparation-print]");
            const acquisitionStatus = workflow.querySelector("[data-county-acquisition-status]");
            const acquisitionReadiness = workflow.querySelector("[data-county-acquisition-readiness]");
            const acquisitionFallback = workflow.querySelector("[data-county-acquisition-fallback]");
            const acquisitionInputs = Array.from(workflow.querySelectorAll("[data-county-acquisition-field]"));
            const acquisitionFieldCopies = Array.from(
                workflow.querySelectorAll("[data-county-acquisition-field-copy]")
            );
            const acquisitionDocuments = Array.from(
                workflow.querySelectorAll(".county-acquisition-documents li")
            ).map((item) => item.textContent.trim()).filter(Boolean);
            const recipient = workflow.dataset.countyRecipient || "";
            const emailSubjectTemplate = workflow.dataset.countyEmailSubject || "Septic record request";
            const countyAgency = workflow.dataset.countyAgency || "";
            const countyFee = workflow.dataset.countyFee || "Not published";
            const countyTiming = workflow.dataset.countyTiming || "Not published";
            const countyManualBoundary = workflow.dataset.countyManualBoundary || "";
            const primaryLabel = workflow.dataset.countyPrimaryLabel || "Open official route";

            function acquisitionInputValue(input) {
                if (input instanceof HTMLInputElement
                    || input instanceof HTMLSelectElement
                    || input instanceof HTMLTextAreaElement) {
                    return input.value.trim();
                }
                return "";
            }

            function acquisitionInputIsActive(input) {
                const fallback = input?.closest("[data-county-acquisition-fallback]");
                return !(fallback instanceof HTMLDetailsElement) || fallback.open;
            }

            function hasRequiredDocumentSelection() {
                return acquisitionInputs.some((input) =>
                    acquisitionInputIsActive(input)
                    && /Requested$/.test(input.dataset.countyAcquisitionField || "")
                    && acquisitionInputValue(input) === "Request this record"
                );
            }

            function acquisitionDetails() {
                return Object.fromEntries(acquisitionInputs.map((input) => [
                    input.dataset.countyAcquisitionField || "",
                    acquisitionInputValue(input)
                ]).filter(([key]) => key));
            }

            function acquisitionLabels() {
                const labels = {
                    address: "property address",
                    parcel: "parcel or local ID",
                    documents: "requested documents"
                };
                acquisitionInputs.forEach((input) => {
                    const key = input.dataset.countyAcquisitionField || "";
                    const label = input.closest("label")?.querySelector("span")?.textContent
                        ?.replace("optional", "")
                        .trim();
                    if (key && label) {
                        labels[key] = label;
                    }
                });
                return labels;
            }

            function acquisitionFieldLabel(input) {
                if (input === address) {
                    return "Property address";
                }
                if (input === parcel) {
                    return "Parcel, GPIN, PIN, or Tax ID";
                }
                return input?.closest("label")?.querySelector("span")?.textContent
                    ?.replace(/\s*optional.*$/i, "")
                    .trim() || "Prepared field";
            }

            function acquisitionTransferFields() {
                return [address, parcel, ...acquisitionInputs].filter((input, index, all) =>
                    (input instanceof HTMLInputElement
                    || input instanceof HTMLSelectElement
                    || input instanceof HTMLTextAreaElement)
                        ? all.indexOf(input) === index && acquisitionInputIsActive(input)
                        : false
                );
            }

            function acquisitionRequiredFields() {
                return new Set([
                    address?.hasAttribute("required") ? "address" : "",
                    parcel?.hasAttribute("required") ? "parcel" : "",
                    ...acquisitionInputs
                        .filter((input) => input.hasAttribute("required") && acquisitionInputIsActive(input))
                        .map((input) => input.dataset.countyAcquisitionField || "")
                        .filter(Boolean)
                ].filter(Boolean));
            }

            function acquisitionValues() {
                return {
                    address: safeValue(address),
                    parcel: safeValue(parcel),
                    documents: acquisitionDocuments.map((document) => `- ${document}`).join("\n"),
                    ...acquisitionDetails()
                };
            }

            function fillAcquisitionTemplate(template) {
                const values = acquisitionValues();
                const labels = acquisitionLabels();
                const requiredFields = acquisitionRequiredFields();
                return template.replace(/\{\{([a-zA-Z0-9]+)}}/g, (_, key) => {
                    const value = values[key];
                    if (value) {
                        return value;
                    }
                    return requiredFields.has(key)
                        ? `[add ${labels[key] || key}]`
                        : "Not provided";
                });
            }

            function missingAcquisitionFields() {
                const missing = [];
                if (address?.hasAttribute("required") && !safeValue(address)) {
                    missing.push("Property address");
                }
                if (parcel?.hasAttribute("required") && !safeValue(parcel)) {
                    missing.push("Parcel, GPIN, PIN, or Tax ID");
                }
                if (requiresAddressOrParcel && !safeValue(address) && !safeValue(parcel)) {
                    missing.push("Property address or parcel ID");
                }
                acquisitionInputs.forEach((input) => {
                    if (acquisitionInputIsActive(input)
                        && input.hasAttribute("required")
                        && !acquisitionInputValue(input)) {
                        const label = input.closest("label")?.querySelector("span")?.textContent
                            ?.replace("optional", "")
                            .trim();
                        missing.push(label || "Required field");
                    }
                });
                if (requiresDocumentSelection && !hasRequiredDocumentSelection()) {
                    missing.push("At least one official document selection");
                }
                return missing;
            }

            function renderAcquisitionReadiness() {
                if (!(acquisitionReadiness instanceof HTMLElement)) {
                    return;
                }
                const requiredControls = [
                    address?.hasAttribute("required") ? address : null,
                    parcel?.hasAttribute("required") ? parcel : null,
                    ...acquisitionInputs.filter((input) =>
                        input.hasAttribute("required") && acquisitionInputIsActive(input)
                    )
                ].filter(Boolean);
                const groupRequired = requiresAddressOrParcel ? 1 : 0;
                const groupReady = requiresAddressOrParcel && (safeValue(address) || safeValue(parcel)) ? 1 : 0;
                const documentGroupRequired = requiresDocumentSelection ? 1 : 0;
                const documentGroupReady = requiresDocumentSelection && hasRequiredDocumentSelection() ? 1 : 0;
                const ready = requiredControls.filter((input) => {
                    if (input === address || input === parcel) {
                        return Boolean(safeValue(input));
                    }
                    return Boolean(acquisitionInputValue(input));
                }).length + groupReady + documentGroupReady;
                const total = requiredControls.length + groupRequired + documentGroupRequired;
                acquisitionReadiness.textContent = total === 0
                    ? acquisitionFallback instanceof HTMLDetailsElement
                        ? "The primary route is ready. Open the fallback only if the free search is empty or incomplete."
                        : "No additional required fields are published for this route."
                    : ready === total
                        ? `Preparation complete: ${ready} of ${total} required details are ready. Only the county step remains.`
                        : `Preparation progress: ${ready} of ${total} required details are ready.`;
                acquisitionReadiness.classList.toggle("is-complete", total > 0 && ready === total);
                const preparationPath = acquisitionFallback instanceof HTMLDetailsElement
                    ? acquisitionFallback.open ? "fallback_field_pack" : "primary_route"
                    : total === 0 ? "no_published_fields" : "field_pack";
                if (!gaReadyPaths.has(preparationPath) && (total === 0 || ready === total)) {
                    gaReadyPaths.add(preparationPath);
                    emitCountyGaEvent("county_prepare_ready", {
                        required_detail_count: total,
                        preparation_path: preparationPath
                    });
                    recordCountyStage("preparation_ready");
                }
            }

            function renderAcquisitionPreview() {
                if (!(acquisitionTemplate instanceof HTMLTemplateElement)
                    || !(acquisitionPreview instanceof HTMLTextAreaElement)) {
                    return "";
                }
                const output = fillAcquisitionTemplate(acquisitionTemplate.content.textContent.trim());
                acquisitionPreview.value = output;
                return output;
            }

            function renderHandoffPreview() {
                if (!(handoffTemplate instanceof HTMLTemplateElement)
                    || !(handoffPreview instanceof HTMLTextAreaElement)) {
                    return "";
                }
                const output = fillAcquisitionTemplate(handoffTemplate.content.textContent.trim());
                handoffPreview.value = output;
                return output;
            }

            function showAcquisitionStatus(message, error = false) {
                if (acquisitionStatus instanceof HTMLElement) {
                    acquisitionStatus.textContent = message;
                    acquisitionStatus.classList.toggle("is-error", error);
                }
            }

            function preparationSheetText() {
                const labels = acquisitionLabels();
                const values = acquisitionValues();
                const missing = missingAcquisitionFields();
                const preparedLines = Object.entries(values)
                    .filter(([key, value]) => key !== "documents" && value)
                    .map(([key, value]) => `${labels[key] || key}: ${value}`);
                const lines = [
                    "SEPTICPATH PREPARATION SHEET — NOT AN OFFICIAL COUNTY FORM",
                    "",
                    `County task: ${countyKey.replace("::", " ")}`,
                    countyAgency ? `Official file owner: ${countyAgency}` : "",
                    `Official route: ${primaryLabel}`,
                    primaryUrl ? `Official URL: ${primaryUrl}` : "",
                    secondaryUrl ? `Official fallback: ${secondaryUrl}` : "",
                    `Published fee: ${countyFee}`,
                    `Published timing: ${countyTiming}`,
                    "",
                    "PREPARED PROPERTY AND ROUTE VALUES",
                    ...(preparedLines.length ? preparedLines : ["No property values added yet."]),
                    ...(missing.length ? ["", "STILL TO PREPARE", ...missing.map((item) => `- ${item}`)] : []),
                    ...(acquisitionDocuments.length
                        ? ["", "TASK SCOPE TO REVIEW — NOT COUNTY-AUTHORED REQUEST WORDING",
                            ...acquisitionDocuments.map((item) => `- ${item}`)]
                        : []),
                    ...(handoffTemplate instanceof HTMLTemplateElement
                        ? ["", "PHONE HANDOFF SCRIPT - WRITTEN BY SEPTICPATH",
                            renderHandoffPreview()]
                        : []),
                    "",
                    "FINAL MANUAL STEP",
                    countyManualBoundary || "Review the details and complete the county's final submission step yourself.",
                    "",
                    "Use the county's current PDF, portal, search, contact form, or phone process as the official route."
                ];
                return lines.filter((line) => line !== "").reduce((output, line) => {
                    const previous = output.at(-1);
                    const isHeading = /^[A-Z][A-Z \u2014-]+$/.test(line);
                    if (isHeading && previous !== undefined && previous !== "") {
                        output.push("");
                    }
                    output.push(line);
                    return output;
                }, []).join("\n");
            }

            function downloadPreparationSheet() {
                const body = preparationSheetText();
                const blob = new Blob([body], { type: "text/plain;charset=utf-8" });
                const url = URL.createObjectURL(blob);
                const link = document.createElement("a");
                link.href = url;
                link.download = `${countyKey.toLowerCase().replaceAll("::", "-")}-official-route-preparation.txt`;
                document.body.append(link);
                link.click();
                link.remove();
                URL.revokeObjectURL(url);
                writeState({ stage: "preparation_downloaded", acquisitionDetails: acquisitionDetails() });
                showAcquisitionStatus("Preparation sheet downloaded. Keep it beside the official county route.");
                sendArtifactAction("county_acquisition", "preparation_sheet_downloaded", countyKey);
                emitCountyGaEvent("county_preparation_downloaded", { artifact_format: "text" });
            }

            function printPreparationSheet() {
                const body = preparationSheetText();
                const printWindow = window.open("", "_blank");
                if (!printWindow) {
                    showAcquisitionStatus("Printing was blocked. Allow pop-ups, then try again.", true);
                    return;
                }
                const escapeHtml = (value) => String(value)
                    .replaceAll("&", "&amp;")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("'", "&#039;");
                const title = `${countyKey.replace("::", " ")} official-route preparation`;
                printWindow.opener = null;
                printWindow.document.write(`<!doctype html>
                    <html lang="en">
                    <head>
                        <meta charset="utf-8">
                        <title>${escapeHtml(title)}</title>
                        <style>
                            body{font:15px/1.55 system-ui,sans-serif;color:#17251f;max-width:780px;margin:42px auto;padding:0 28px}
                            h1{font-size:24px;line-height:1.2;margin:0 0 10px}
                            .notice{margin:0 0 28px;padding:12px 0;border-block:2px solid #17251f;font-weight:750}
                            pre{font:14px/1.6 ui-monospace,SFMono-Regular,Menlo,monospace;white-space:pre-wrap;overflow-wrap:anywhere}
                            @media print{body{margin:0;max-width:none}}
                        </style>
                    </head>
                    <body>
                        <h1>${escapeHtml(title)}</h1>
                        <p class="notice">Preparation aid only — use the county's current document or system for the official action.</p>
                        <pre>${escapeHtml(body)}</pre>
                        <script>window.addEventListener("load",()=>window.print())<\/script>
                    </body>
                    </html>`);
                printWindow.document.close();
                writeState({ stage: "preparation_printed", acquisitionDetails: acquisitionDetails() });
                showAcquisitionStatus("Print view opened. Save it as a reference sheet, not as a substitute county form.");
                sendArtifactAction("county_acquisition", "preparation_sheet_printed", countyKey);
                emitCountyGaEvent("county_preparation_printed");
            }

            function validateAcquisition() {
                const missing = missingAcquisitionFields();
                if (missing.length) {
                    showAcquisitionStatus(`Add before sending: ${missing.join(", ")}.`, true);
                    const firstMissing = address?.hasAttribute("required") && !safeValue(address)
                        ? address
                        : parcel?.hasAttribute("required") && !safeValue(parcel)
                            ? parcel
                            : requiresAddressOrParcel && !safeValue(address) && !safeValue(parcel)
                                ? (address || parcel)
                                : requiresDocumentSelection && !hasRequiredDocumentSelection()
                                    ? acquisitionInputs.find((input) =>
                                        /Requested$/.test(input.dataset.countyAcquisitionField || "")
                                    )
                                    : acquisitionInputs.find((input) =>
                                        acquisitionInputIsActive(input)
                                        && input.hasAttribute("required")
                                        && !acquisitionInputValue(input)
                                    );
                    firstMissing?.focus();
                    return false;
                }
                return true;
            }

            if (acquisitionWorkspace instanceof HTMLElement) {
                let nextTransferIndex = 0;
                const savedAcquisition = readState()?.acquisitionDetails;
                if (savedAcquisition && typeof savedAcquisition === "object") {
                    acquisitionInputs.forEach((input) => {
                        const key = input.dataset.countyAcquisitionField || "";
                        const value = String(savedAcquisition[key] || "");
                        if ((input instanceof HTMLInputElement
                                || input instanceof HTMLSelectElement
                                || input instanceof HTMLTextAreaElement) && value) {
                            input.value = value.slice(0, 220);
                        }
                    });
                }

                const refreshAcquisition = () => {
                    renderAcquisitionPreview();
                    renderHandoffPreview();
                    renderAcquisitionReadiness();
                    updateNextTransferLabel();
                    writeState({
                        stage: "prepared",
                        acquisitionDetails: acquisitionDetails()
                    });
                };

                acquisitionInputs.forEach((input) => {
                    input.addEventListener("input", () => {
                        markGaPreparationStarted("county_field");
                        refreshAcquisition();
                    });
                    input.addEventListener("change", () => {
                        markGaPreparationStarted("county_field");
                        refreshAcquisition();
                    });
                });

                const hamiltonQuickSearch = workflow.querySelector("[data-hamilton-quick-search]");
                const hamiltonQuickStreet = workflow.querySelector("[data-hamilton-quick-street]");
                const hamiltonQuickOpen = workflow.querySelector("[data-hamilton-quick-open]");
                const hamiltonQuickStatus = workflow.querySelector("[data-hamilton-quick-status]");
                const hamiltonStreetField = acquisitionInputs.find((input) =>
                    input.dataset.countyAcquisitionField === "streetName"
                );

                if (hamiltonQuickSearch instanceof HTMLElement
                    && hamiltonQuickStreet instanceof HTMLInputElement
                    && hamiltonQuickOpen instanceof HTMLAnchorElement) {
                    hamiltonQuickOpen.addEventListener("click", (event) => {
                        const streetName = hamiltonQuickStreet.value.trim();
                        if (!streetName) {
                            event.preventDefault();
                            if (hamiltonQuickStatus instanceof HTMLElement) {
                                hamiltonQuickStatus.textContent = "Add the street name first. Leave out the house number.";
                            }
                            hamiltonQuickStreet.focus();
                            return;
                        }
                        if (hamiltonStreetField instanceof HTMLInputElement) {
                            hamiltonStreetField.value = streetName;
                            hamiltonStreetField.dispatchEvent(new Event("input", { bubbles: true }));
                        }
                        if (hamiltonQuickStatus instanceof HTMLElement) {
                            hamiltonQuickStatus.textContent = "Opening the county search in a new tab. Choose the exact full address there.";
                        }
                        markGaPreparationStarted("hamilton_quick_search");
                    });
                }

                const alamanceRequestStart = workflow.querySelector("[data-alamance-request-start]");
                const alamanceRequestOpen = workflow.querySelector("[data-alamance-request-open]");
                const alamanceRequestStatus = workflow.querySelector("[data-alamance-request-status]");

                if (alamanceRequestStart instanceof HTMLElement
                    && alamanceRequestOpen instanceof HTMLAnchorElement) {
                    alamanceRequestOpen.addEventListener("click", (event) => {
                        if (!safeValue(address) && !safeValue(parcel)) {
                            event.preventDefault();
                            if (alamanceRequestStatus instanceof HTMLElement) {
                                alamanceRequestStatus.textContent = "Add the property address or GPIN first so the county can identify the existing file.";
                            }
                            address?.focus();
                            return;
                        }
                        if (alamanceRequestStatus instanceof HTMLElement) {
                            alamanceRequestStatus.textContent = "Opening the official form in a new tab. Complete the requester details and select the records you need there.";
                        }
                        markGaPreparationStarted("alamance_request_start");
                    });
                }

                acquisitionFallback?.addEventListener("toggle", () => {
                    if (acquisitionFallback.open) {
                        markGaPreparationStarted("fallback_opened");
                        emitCountyGaEvent("county_fallback_opened");
                    }
                    refreshAcquisition();
                });
                address?.addEventListener("input", refreshAcquisition);
                parcel?.addEventListener("input", refreshAcquisition);
                renderAcquisitionPreview();
                renderHandoffPreview();
                renderAcquisitionReadiness();

                function updateNextTransferLabel() {
                    if (!(acquisitionNextCopy instanceof HTMLButtonElement)) {
                        return;
                    }
                    const fields = acquisitionTransferFields();
                    const nextField = fields.slice(nextTransferIndex).find((input) =>
                        Boolean(acquisitionInputValue(input)) || input.hasAttribute("required")
                    );
                    if (!nextField) {
                        acquisitionNextCopy.textContent = nextTransferIndex > 0
                            ? "Start field transfer again"
                            : "Copy next prepared field";
                        return;
                    }
                    acquisitionNextCopy.textContent = acquisitionInputValue(nextField)
                        ? `Copy next: ${acquisitionFieldLabel(nextField)}`
                        : `Add next: ${acquisitionFieldLabel(nextField)}`;
                }

                updateNextTransferLabel();

                acquisitionFieldCopies.forEach((copyButton) => {
                    copyButton.addEventListener("click", async () => {
                        const field = copyButton.closest(".county-acquisition-field")
                            ?.querySelector("input, select, textarea");
                        const value = field === address || field === parcel
                            ? safeValue(field)
                            : acquisitionInputValue(field);
                        const label = field?.closest("label")?.querySelector("span")?.textContent
                            ?.replace("optional", "")
                            .trim() || "Field";
                        if (!value) {
                            showAcquisitionStatus(`Add ${label.toLowerCase()} before copying.`, true);
                            field?.focus();
                            return;
                        }
                        try {
                            await copyText(value);
                            const originalLabel = copyButton.textContent;
                            copyButton.textContent = "Copied";
                            copyButton.classList.add("is-copied");
                            showAcquisitionStatus(`${label} copied. Paste it into the matching county field.`);
                            window.setTimeout(() => {
                                copyButton.textContent = originalLabel;
                                copyButton.classList.remove("is-copied");
                            }, 1400);
                            sendArtifactAction("county_acquisition", "field_copied", countyKey);
                        } catch (_) {
                            showAcquisitionStatus("Copy unavailable. Select the field value manually.", true);
                        }
                    });
                });

                acquisitionNextCopy?.addEventListener("click", async () => {
                    const fields = acquisitionTransferFields();
                    if (!fields.length) {
                        showAcquisitionStatus("No verified transfer fields are published for this route.", true);
                        return;
                    }
                    if (nextTransferIndex >= fields.length) {
                        nextTransferIndex = 0;
                        updateNextTransferLabel();
                        showAcquisitionStatus("Field transfer restarted from the first prepared value.");
                        return;
                    }
                    if (requiresAddressOrParcel && !safeValue(address) && !safeValue(parcel)) {
                        showAcquisitionStatus("Add the property address or parcel ID before starting the county transfer.", true);
                        (address || parcel)?.focus();
                        return;
                    }
                    for (let index = nextTransferIndex; index < fields.length; index += 1) {
                        const field = fields[index];
                        const value = acquisitionInputValue(field);
                        const label = acquisitionFieldLabel(field);
                        if (!value && field.hasAttribute("required")) {
                            nextTransferIndex = index;
                            updateNextTransferLabel();
                            showAcquisitionStatus(`Add ${label.toLowerCase()} before moving to the next county field.`, true);
                            field.focus();
                            return;
                        }
                        if (!value) {
                            continue;
                        }
                        try {
                            await copyText(value);
                            nextTransferIndex = index + 1;
                            updateNextTransferLabel();
                            showAcquisitionStatus(`${label} copied. Paste it into the matching official field, then use “Copy next.”`);
                            sendArtifactAction("county_acquisition", "next_field_copied", countyKey);
                        } catch (_) {
                            showAcquisitionStatus("Copy unavailable. Select the prepared value manually.", true);
                        }
                        return;
                    }
                    nextTransferIndex = fields.length;
                    updateNextTransferLabel();
                    showAcquisitionStatus("All filled values have been carried through. Review the county page before your final manual submission.");
                });

                preparationDownload?.addEventListener("click", downloadPreparationSheet);
                preparationPrint?.addEventListener("click", printPreparationSheet);

                handoffCopy?.addEventListener("click", async () => {
                    const output = renderHandoffPreview();
                    if (!output) {
                        return;
                    }
                    try {
                        await copyText(output);
                        const missing = missingAcquisitionFields();
                        showAcquisitionStatus(missing.length
                            ? `Call script copied. Still prepare: ${missing.join(", ")}.`
                            : "Call script copied. Ask the office only to confirm the current intake, fee, turnaround, and reference number.");
                        sendArtifactAction("county_acquisition", "handoff_script_copied", countyKey);
                        emitCountyGaEvent("county_handoff_script_copied");
                    } catch (_) {
                        showAcquisitionStatus("Copy unavailable. Keep the prepared script open during the call.", true);
                    }
                });

                officialPdfPrepare?.addEventListener("click", async () => {
                    if (!validateAcquisition()) {
                        return;
                    }
                    officialPdfPrepare.disabled = true;
                    officialPdfPrepare.textContent = "Filling the official PDF…";
                    showAcquisitionStatus("Retrieving the county's original PDF and adding the values you entered.");
                    try {
                        const response = await fetch("/api/county-records/prepare-official-pdf", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                                "Accept": "application/pdf"
                            },
                            body: JSON.stringify({
                                countyKey,
                                address: safeValue(address),
                                parcel: safeValue(parcel),
                                fields: acquisitionDetails()
                            })
                        });
                        if (!response.ok) {
                            let message = "The county's PDF could not be prepared right now.";
                            try {
                                const error = await response.json();
                                message = error.message || message;
                            } catch (_) {
                                // Keep the safe fallback message for a non-JSON county/network response.
                            }
                            throw new Error(message);
                        }
                        const blob = await response.blob();
                        const url = URL.createObjectURL(blob);
                        const link = document.createElement("a");
                        const disposition = response.headers.get("Content-Disposition") || "";
                        const fileName = disposition.match(/filename="([^"]+)"/i)?.[1]
                            || `${countyKey.toLowerCase().replaceAll("::", "-")}-official-form-prepared.pdf`;
                        link.href = url;
                        link.download = fileName;
                        document.body.append(link);
                        link.click();
                        link.remove();
                        URL.revokeObjectURL(url);
                        writeState({ stage: "official_pdf_prepared", acquisitionDetails: acquisitionDetails() });
                        showAcquisitionStatus("County original downloaded with the supported fields filled. Review every page and complete checkboxes, signature, attachments, and final sending yourself.");
                        sendArtifactAction("county_acquisition", "official_pdf_prepared", countyKey);
                        emitCountyGaEvent("county_official_pdf_prepared", { artifact_format: "pdf" });
                    } catch (error) {
                        showAcquisitionStatus(`${error.message} Open the official blank PDF and use the preparation sheet instead.`, true);
                    } finally {
                        officialPdfPrepare.disabled = false;
                        officialPdfPrepare.textContent = "Fill the county's original PDF";
                    }
                });

                acquisitionCopy?.addEventListener("click", async () => {
                    const output = renderAcquisitionPreview();
                    if (!output) {
                        return;
                    }
                    try {
                        await copyText(output);
                        const missing = missingAcquisitionFields();
                        showAcquisitionStatus(missing.length
                            ? `Draft copied. Still add: ${missing.join(", ")}.`
                            : "Complete request copied.");
                        sendArtifactAction("county_acquisition", "request_copied", countyKey);
                    } catch (_) {
                        showAcquisitionStatus("Copy unavailable. Select the prepared request manually.", true);
                    }
                });

                acquisitionInputCopy?.addEventListener("click", async () => {
                    const values = acquisitionValues();
                    const labels = acquisitionLabels();
                    const lines = Object.entries(values)
                        .filter(([key, value]) => key !== "documents" && value)
                        .map(([key, value]) => `${labels[key] || key}: ${value}`);
                    if (!lines.length) {
                        showAcquisitionStatus("Fill at least one verified route input first.", true);
                        address?.focus();
                        return;
                    }
                    try {
                        await copyText(lines.join("\n"));
                        showAcquisitionStatus("Filled route inputs copied. Paste them into the county's official search, form, portal, or use them during the call.");
                        sendArtifactAction("county_acquisition", "verified_inputs_copied", countyKey);
                    } catch (_) {
                        showAcquisitionStatus("Copy unavailable. Keep this page open while completing the official route.", true);
                    }
                });

                const knoxFormHandoff = workflow.querySelector("[data-knox-form-handoff]");
                const knoxFormOpen = workflow.querySelector("[data-knox-form-open]");
                const knoxFormCopy = workflow.querySelector("[data-knox-form-copy]");
                const knoxFormStatus = workflow.querySelector("[data-knox-form-status]");

                function showKnoxFormStatus(message) {
                    if (knoxFormStatus instanceof HTMLElement) {
                        knoxFormStatus.textContent = message;
                    }
                }

                if (knoxFormHandoff instanceof HTMLElement) {
                    knoxFormOpen?.addEventListener("click", (event) => {
                        if (!validateAcquisition()) {
                            event.preventDefault();
                            showKnoxFormStatus("Complete the required field pack before opening the live county form.");
                            return;
                        }
                        showKnoxFormStatus("Opening the county-branded SSDS file-search form in a new tab. Paste the prepared values, review them, and submit in your own name.");
                        markGaPreparationStarted("knox_form_handoff");
                    });
                    knoxFormCopy?.addEventListener("click", () => {
                        if (!validateAcquisition()) {
                            showKnoxFormStatus("Complete the required field pack before copying.");
                            return;
                        }
                        acquisitionInputCopy?.click();
                        showKnoxFormStatus("Prepared values copied. Paste them into the matching live county-form fields.");
                    });
                }

                acquisitionEmail?.addEventListener("click", () => {
                    if (!recipient || !validateAcquisition()) {
                        return;
                    }
                    const subject = fillAcquisitionTemplate(emailSubjectTemplate);
                    const body = renderAcquisitionPreview();
                    writeState({ stage: "email_draft_opened", acquisitionDetails: acquisitionDetails() });
                    window.location.href = `mailto:${recipient}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
                    showAcquisitionStatus(`Email draft opened for ${recipient}. Attach the official PDF when the county requires it.`);
                    sendArtifactAction("county_acquisition", "email_draft_opened", countyKey);
                    emitCountyGaEvent("county_email_draft_opened");
                });

                acquisitionDownload?.addEventListener("click", () => {
                    if (!validateAcquisition()) {
                        return;
                    }
                    const body = renderAcquisitionPreview();
                    const blob = new Blob([body], { type: "text/plain;charset=utf-8" });
                    const url = URL.createObjectURL(blob);
                    const link = document.createElement("a");
                    link.href = url;
                    link.download = `${countyKey.toLowerCase().replaceAll("::", "-")}-septic-record-request.txt`;
                    document.body.append(link);
                    link.click();
                    link.remove();
                    URL.revokeObjectURL(url);
                    writeState({ stage: "request_downloaded", acquisitionDetails: acquisitionDetails() });
                    showAcquisitionStatus("Request text downloaded. Keep it with the county confirmation and reply.");
                    sendArtifactAction("county_acquisition", "request_downloaded", countyKey);
                    emitCountyGaEvent("county_request_downloaded", { artifact_format: "text" });
                });

                acquisitionPrint?.addEventListener("click", () => {
                    if (!validateAcquisition()) {
                        return;
                    }
                    const body = renderAcquisitionPreview();
                    const printWindow = window.open("", "_blank");
                    if (!printWindow) {
                        showAcquisitionStatus("Printing was blocked. Allow pop-ups, then try again.", true);
                        return;
                    }
                    printWindow.opener = null;
                    const title = `${countyKey.replace("::", " ")} septic record request`;
                    const escapeHtml = (value) => value
                        .replaceAll("&", "&amp;")
                        .replaceAll("<", "&lt;")
                        .replaceAll(">", "&gt;")
                        .replaceAll("\"", "&quot;")
                        .replaceAll("'", "&#039;");
                    printWindow.document.write(`<!doctype html>
                        <html lang="en">
                        <head>
                            <meta charset="utf-8">
                            <title>${escapeHtml(title)}</title>
                            <style>
                                body{font:16px/1.55 Georgia,serif;color:#17251f;max-width:760px;margin:48px auto;padding:0 28px}
                                h1{font:700 24px/1.2 system-ui,sans-serif;margin:0 0 28px}
                                pre{font:inherit;white-space:pre-wrap;overflow-wrap:anywhere}
                                .signature{margin-top:48px;padding-top:18px;border-top:1px solid #9aa8a1}
                                @media print{body{margin:0;max-width:none}.no-print{display:none}}
                            </style>
                        </head>
                        <body>
                            <h1>${escapeHtml(title)}</h1>
                            <pre>${escapeHtml(body)}</pre>
                            <div class="signature">Signature (if required): ____________________ &nbsp; Date: __________</div>
                            <script>window.addEventListener("load",()=>window.print())<\/script>
                        </body>
                        </html>`);
                    printWindow.document.close();
                    writeState({ stage: "request_printed", acquisitionDetails: acquisitionDetails() });
                    showAcquisitionStatus("Print view opened. Choose “Save as PDF” to keep an attachment-ready copy.");
                    sendArtifactAction("county_acquisition", "request_printed", countyKey);
                });
            }

            const brunswickLookup = workflow.querySelector("[data-brunswick-permit-lookup]");
            const brunswickSearch = workflow.querySelector("[data-brunswick-permit-search]");
            const brunswickResults = workflow.querySelector("[data-brunswick-permit-results]");

            function appendPermitValue(container, label, value) {
                if (!value) {
                    return;
                }
                const row = document.createElement("p");
                const term = document.createElement("strong");
                term.textContent = `${label}: `;
                row.append(term, document.createTextNode(value));
                container.append(row);
            }

            function renderBrunswickResults(payload) {
                if (!(brunswickResults instanceof HTMLElement)) {
                    return;
                }
                const heading = document.createElement("h4");
                heading.textContent = payload.heading || "County permit metadata result";
                const summary = document.createElement("p");
                summary.textContent = payload.summary || "";
                const fragment = document.createDocumentFragment();
                fragment.append(heading, summary);

                const candidates = Array.isArray(payload.candidates) ? payload.candidates : [];
                candidates.forEach((candidate) => {
                    const card = document.createElement("article");
                    card.className = "brunswick-permit-lookup__candidate";
                    if (candidate.septicCandidate) {
                        const badge = document.createElement("span");
                        badge.className = "pill";
                        badge.textContent = "Septic-related wording found";
                        card.append(badge);
                    }
                    appendPermitValue(card, "Permit", candidate.permitNumber);
                    appendPermitValue(card, "Address", candidate.parcelAddress);
                    appendPermitValue(card, "Parcel ID", candidate.parcelId);
                    appendPermitValue(card, "Type", candidate.permitType || candidate.projectType);
                    appendPermitValue(card, "Category", candidate.projectCategory);
                    appendPermitValue(card, "Status", candidate.permitStatus);
                    appendPermitValue(card, "Description", candidate.description);
                    appendPermitValue(card, "Issued", candidate.dateIssued);
                    fragment.append(card);
                });

                const actions = document.createElement("div");
                actions.className = "county-access-workflow__actions";
                if (payload.sourceUrl) {
                    actions.append(actionLink("Inspect the official dataset", payload.sourceUrl));
                }
                if (acquisitionMethod) {
                    const sourceFileRoute = externalSecondaryUrl() || primaryUrl;
                    if (sourceFileRoute) {
                        actions.append(actionLink(
                            "Use Brunswick County's official source-file route",
                            sourceFileRoute,
                            candidates.length > 0
                        ));
                    }
                } else {
                    actions.append(actionLink("Request the septic source file", requestPath(), candidates.length > 0));
                }
                fragment.append(actions);
                brunswickResults.replaceChildren(fragment);
                brunswickResults.hidden = false;
            }

            brunswickSearch?.addEventListener("click", async () => {
                if (!(brunswickLookup instanceof HTMLElement)) {
                    return;
                }
                const searchAddress = safeValue(address);
                const searchParcel = safeValue(parcel);
                if (searchAddress.length < 3 && searchParcel.length < 3) {
                    if (status instanceof HTMLElement) {
                        status.textContent = "Add a Brunswick address or parcel ID first.";
                    }
                    address?.focus();
                    return;
                }

                const originalLabel = brunswickSearch.textContent;
                brunswickSearch.disabled = true;
                brunswickSearch.textContent = "Searching the county index...";
                try {
                    const response = await fetch("/api/brunswick-permit-lookup", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ address: searchAddress, parcelId: searchParcel })
                    });
                    const payload = await response.json();
                    renderBrunswickResults(payload);
                    writeState({ stage: "metadata_queried", metadataStatus: payload.status || "unknown" });
                    sendArtifactAction("county_access_workflow", `metadata_${payload.status || "unknown"}`, mode);
                } catch (_) {
                    renderBrunswickResults({
                        heading: "The county permit index did not respond",
                        summary: "Your property clues remain here. Open the official dataset or prepare a source-file request.",
                        sourceUrl: primaryUrl,
                        candidates: []
                    });
                } finally {
                    brunswickSearch.disabled = false;
                    brunswickSearch.textContent = originalLabel;
                }
            });

            const adamsLookup = workflow.querySelector("[data-adams-septic-lookup]");
            const adamsClue = workflow.querySelector("[data-adams-septic-clue]");
            const adamsSearch = workflow.querySelector("[data-adams-septic-search]");
            const adamsResults = workflow.querySelector("[data-adams-septic-results]");

            function renderAdamsResults(payload) {
                if (!(adamsResults instanceof HTMLElement)) return;
                const fragment = document.createDocumentFragment();
                const heading = document.createElement("h4"); heading.textContent = payload.heading || "Adams County result";
                const summary = document.createElement("p"); summary.textContent = payload.summary || "";
                fragment.append(heading, summary);
                const candidates = Array.isArray(payload.candidates) ? payload.candidates : [];
                candidates.forEach((candidate) => {
                    const card = document.createElement("article"); card.className = "brunswick-permit-lookup__candidate";
                    appendPermitValue(card, "Address", candidate.address);
                    appendPermitValue(card, "APN", candidate.apn);
                    appendPermitValue(card, "Record", candidate.recordId);
                    appendPermitValue(card, "Description", candidate.description);
                    appendPermitValue(card, "Application date", candidate.applicationDate);
                    fragment.append(card);
                });
                const actions = document.createElement("div"); actions.className = "county-access-workflow__actions";
                actions.append(actionLink("Open the official Adams map", primaryUrl, candidates.length > 0));
                if (secondaryUrl) actions.append(actionLink("Open current septic forms", secondaryUrl));
                fragment.append(actions); adamsResults.replaceChildren(fragment); adamsResults.hidden = false;
            }

            adamsSearch?.addEventListener("click", async () => {
                if (!(adamsLookup instanceof HTMLElement) || !(adamsClue instanceof HTMLInputElement)) return;
                const clue = adamsClue.value.trim();
                if (clue.length < 3) { adamsClue.focus(); renderAdamsResults({heading:"Enter an address, APN, or record number",summary:"Use at least three characters.",candidates:[]}); return; }
                const label = adamsSearch.textContent; adamsSearch.disabled = true; adamsSearch.textContent = "Searching Adams County...";
                try {
                    const safeClue = clue.replace(/[^\p{L}\p{N} .#'/-]/gu, " ").replace(/\s+/g, " ").slice(0, 100);
                    const sqlClue = safeClue.toUpperCase().replaceAll("'", "''");
                    const query = new URLSearchParams({
                        f: "json",
                        where: `UPPER(Address_Full) LIKE '%${sqlClue}%' OR UPPER(APN) LIKE '%${sqlClue}%' OR UPPER(RECORD_ID) LIKE '%${sqlClue}%'`,
                        outFields: "Address_Full,APN,RECORD_ID,PE_Description,APPLICATION_DATE",
                        returnGeometry: "false",
                        orderByFields: "APPLICATION_DATE DESC",
                        resultRecordCount: "10"
                    });
                    const response = await fetch(`https://services8.arcgis.com/8G2jD4VY84pgX1Z5/arcgis/rest/services/Septic_Records/FeatureServer/6/query?${query}`);
                    const source = await response.json();
                    if (!response.ok || source.error) throw new Error("county query unavailable");
                    const candidates = Array.isArray(source.features) ? source.features.map((feature) => {
                        const attributes = feature.attributes || {};
                        return {
                            address: attributes.Address_Full || "",
                            apn: attributes.APN || "",
                            recordId: attributes.RECORD_ID || "",
                            description: attributes.PE_Description || "",
                            applicationDate: attributes.APPLICATION_DATE ? new Date(attributes.APPLICATION_DATE).toLocaleDateString() : ""
                        };
                    }) : [];
                    const payload = candidates.length ? {
                        status: "found",
                        heading: `${candidates.length} official candidate${candidates.length === 1 ? "" : "s"} found`,
                        summary: "Match the address or APN before relying on a row. These are index candidates, not proof of current system condition.",
                        candidates
                    } : {
                        status: "not_found",
                        heading: "No matching online candidate appeared",
                        summary: "This is not an official no-record finding. Try another clue and check the official county map.",
                        candidates: []
                    };
                    renderAdamsResults(payload);
                    writeState({stage:"metadata_queried",metadataStatus:payload.status || "unknown"});
                    emitCountyGaEvent("county_public_index_queried",{index_name:"adams_septic_arcgis",lookup_status:payload.status || "unknown"});
                } catch (_) { renderAdamsResults({heading:"The official Adams dataset did not respond",summary:"Open the county map and try the same clue there.",candidates:[]}); }
                finally { adamsSearch.disabled = false; adamsSearch.textContent = label; }
            });

            const sanDiegoLookup = workflow.querySelector("[data-san-diego-document-lookup]");
            const sanDiegoMethod = workflow.querySelector("[data-san-diego-search-method]");
            const sanDiegoSingleField = workflow.querySelector("[data-san-diego-single-field]");
            const sanDiegoClueLabel = workflow.querySelector("[data-san-diego-clue-label]");
            const sanDiegoClue = workflow.querySelector("[data-san-diego-clue]");
            const sanDiegoStreetFields = workflow.querySelector("[data-san-diego-street-fields]");
            const sanDiegoStreetNumber = workflow.querySelector("[data-san-diego-street-number]");
            const sanDiegoStreetName = workflow.querySelector("[data-san-diego-street-name]");
            const sanDiegoSearch = workflow.querySelector("[data-san-diego-document-search]");
            const sanDiegoResults = workflow.querySelector("[data-san-diego-document-results]");

            function renderSanDiegoResults(payload) {
                if (!(sanDiegoResults instanceof HTMLElement)) return;
                const fragment = document.createDocumentFragment();
                const heading = document.createElement("h4"); heading.textContent = payload.heading || "San Diego County result";
                const summary = document.createElement("p"); summary.textContent = payload.summary || "";
                fragment.append(heading, summary);
                const candidates = Array.isArray(payload.candidates) ? payload.candidates : [];
                candidates.forEach((candidate) => {
                    const card = document.createElement("article"); card.className = "brunswick-permit-lookup__candidate";
                    appendPermitValue(card, "Record ID", candidate.recordId);
                    appendPermitValue(card, "APN", candidate.apn);
                    appendPermitValue(card, "Document category", candidate.category);
                    appendPermitValue(card, "Document type", candidate.subcategory);
                    appendPermitValue(card, "Description", candidate.description);
                    appendPermitValue(card, "Document date", candidate.documentDate);
                    if (candidate.url) {
                        const actions = document.createElement("div"); actions.className = "county-access-workflow__actions";
                        actions.append(actionLink("Open official document", candidate.url, true));
                        card.append(actions);
                    }
                    fragment.append(card);
                });
                const actions = document.createElement("div"); actions.className = "county-access-workflow__actions";
                actions.append(actionLink("Open the official document library", primaryUrl, candidates.length > 0));
                if (secondaryUrl) actions.append(actionLink("Request records through PRRC", secondaryUrl));
                fragment.append(actions);
                sanDiegoResults.replaceChildren(fragment);
                sanDiegoResults.hidden = false;
            }

            function updateSanDiegoMethod() {
                if (!(sanDiegoMethod instanceof HTMLSelectElement)) return;
                const streetSearch = sanDiegoMethod.value === "street";
                if (sanDiegoSingleField instanceof HTMLElement) sanDiegoSingleField.hidden = streetSearch;
                if (sanDiegoStreetFields instanceof HTMLElement) sanDiegoStreetFields.hidden = !streetSearch;
                if (!(sanDiegoClue instanceof HTMLInputElement) || !(sanDiegoClueLabel instanceof HTMLElement)) return;
                if (sanDiegoMethod.value === "record_id") {
                    sanDiegoClueLabel.textContent = "County record ID";
                    sanDiegoClue.placeholder = "Example: DEH2024-...";
                } else {
                    sanDiegoClueLabel.textContent = "Assessor parcel number (APN)";
                    sanDiegoClue.placeholder = "xxx-xxx-xx-xx";
                    if (!sanDiegoClue.value && safeValue(parcel)) sanDiegoClue.value = safeValue(parcel);
                }
            }

            sanDiegoMethod?.addEventListener("change", updateSanDiegoMethod);
            updateSanDiegoMethod();

            sanDiegoSearch?.addEventListener("click", async () => {
                if (!(sanDiegoLookup instanceof HTMLElement) || !(sanDiegoMethod instanceof HTMLSelectElement)) return;
                const method = sanDiegoMethod.value;
                const clue = sanDiegoClue instanceof HTMLInputElement ? sanDiegoClue.value.trim() : "";
                const streetNumber = sanDiegoStreetNumber instanceof HTMLInputElement ? sanDiegoStreetNumber.value.trim() : "";
                const streetName = sanDiegoStreetName instanceof HTMLInputElement ? sanDiegoStreetName.value.trim() : "";
                if (method === "street" ? (!streetNumber || streetName.length < 2) : clue.length < 3) {
                    const target = method === "street" ? (!streetNumber ? sanDiegoStreetNumber : sanDiegoStreetName) : sanDiegoClue;
                    target?.focus();
                    renderSanDiegoResults({
                        heading: method === "street" ? "Enter a street number and street name" : "Enter a complete search clue",
                        summary: method === "street" ? "Leave the street type out of the name, as the county requires." : "Use at least three characters.",
                        candidates: []
                    });
                    return;
                }

                const query = new URLSearchParams({maxrecord_count: "25", ts: String(Date.now())});
                if (method === "street") {
                    query.set("street_number", streetNumber.replace(/[^0-9A-Za-z-]/g, "").slice(0, 12).toUpperCase());
                    query.set("street_name", streetName.replace(/[^\p{L}\p{N} .'-]/gu, " ").replace(/\s+/g, " ").slice(0, 80).toUpperCase());
                } else {
                    query.set(method, clue.replace(/[^\p{L}\p{N} .#'/-]/gu, " ").replace(/\s+/g, " ").slice(0, 100).toUpperCase());
                }

                const label = sanDiegoSearch.textContent;
                sanDiegoSearch.disabled = true;
                sanDiegoSearch.textContent = "Searching San Diego County...";
                try {
                    const endpoint = "https://file.sandiegocounty.gov/CoSD_LUEG_Repository_External_API/rest/DEHQDocumentLibrary/SearchDocuments";
                    const response = await fetch(`${endpoint}?${query}`);
                    const source = await response.json();
                    if (!response.ok || !Array.isArray(source.records)) throw new Error("county query unavailable");
                    const allowedHost = "file.sandiegocounty.gov";
                    const candidates = Array.isArray(source.records) ? source.records.map((record) => {
                        let documentUrl = "";
                        try {
                            const parsedUrl = new URL(record.url);
                            if (parsedUrl.protocol === "https:" && parsedUrl.hostname === allowedHost) documentUrl = parsedUrl.href;
                        } catch (_) {
                            // Ignore malformed or non-county document links.
                        }
                        return {
                            recordId: record.permit_id || "",
                            apn: record.parcel_nbr || "",
                            category: record.lueg_type || "",
                            subcategory: record.lueg_subtype || "",
                            description: record.description || "",
                            documentDate: record.r_creation_date ? String(record.r_creation_date).slice(0, 10) : "",
                            url: documentUrl
                        };
                    }) : [];
                    const payload = candidates.length ? {
                        status: "found",
                        heading: `${candidates.length} official document candidate${candidates.length === 1 ? "" : "s"} found`,
                        summary: "Match the APN or record ID before relying on a file. A candidate does not establish the system's current condition.",
                        candidates
                    } : {
                        status: "not_found",
                        heading: "No matching online candidate appeared",
                        summary: "This is not an official no-record finding. Try another single search method or use the county PRRC request.",
                        candidates: []
                    };
                    renderSanDiegoResults(payload);
                    writeState({stage: "metadata_queried", metadataStatus: payload.status});
                    emitCountyGaEvent("county_public_index_queried", {index_name: "san_diego_dehq_document_library", lookup_status: payload.status});
                } catch (_) {
                    renderSanDiegoResults({
                        heading: "The official San Diego document service did not respond",
                        summary: "Your search terms remain in this page. Open the county library or use PRRC to continue.",
                        candidates: []
                    });
                    emitCountyGaEvent("county_public_index_queried", {index_name: "san_diego_dehq_document_library", lookup_status: "unavailable"});
                } finally {
                    sanDiegoSearch.disabled = false;
                    sanDiegoSearch.textContent = label;
                }
            });

            [sanDiegoClue, sanDiegoStreetNumber, sanDiegoStreetName].forEach((input) => {
                input?.addEventListener("keydown", (event) => {
                    if (event.key === "Enter") {
                        event.preventDefault();
                        sanDiegoSearch?.click();
                    }
                });
            });

            const washtenawLookup = workflow.querySelector("[data-washtenaw-permit-lookup]");
            const washtenawStreetNumber = workflow.querySelector("[data-washtenaw-street-number]");
            const washtenawLoad = workflow.querySelector("[data-washtenaw-permit-load]");
            const washtenawStatus = workflow.querySelector("[data-washtenaw-permit-status]");
            const washtenawFrameWrap = workflow.querySelector("[data-washtenaw-permit-frame-wrap]");
            const washtenawFrame = workflow.querySelector("[data-washtenaw-permit-frame]");
            let washtenawLoadTimer;

            function showWashtenawStatus(message, isWarning = false) {
                if (!(washtenawStatus instanceof HTMLElement)) return;
                washtenawStatus.textContent = message;
                washtenawStatus.classList.toggle("is-warning", isWarning);
            }

            function suggestedWashtenawStreetNumber() {
                const match = safeValue(address).match(/^\s*(\d{1,8})\b/);
                return match?.[1] || "";
            }

            if (washtenawStreetNumber instanceof HTMLInputElement && !washtenawStreetNumber.value) {
                washtenawStreetNumber.value = suggestedWashtenawStreetNumber();
            }

            address?.addEventListener("input", () => {
                if (!(washtenawLookup instanceof HTMLElement)
                    || !(washtenawStreetNumber instanceof HTMLInputElement)
                    || washtenawStreetNumber.value.trim()) return;
                washtenawStreetNumber.value = suggestedWashtenawStreetNumber();
            });

            washtenawFrame?.addEventListener("load", () => {
                if (!washtenawFrame.getAttribute("src")) return;
                window.clearTimeout(washtenawLoadTimer);
                showWashtenawStatus("The official search panel opened. Paste the copied street number into Search Value; use the county-page link if the panel stays blank.");
                emitCountyGaEvent("county_official_embed_loaded", {embed_name: "washtenaw_public_access"});
            });

            async function loadWashtenawSearch() {
                if (!(washtenawLookup instanceof HTMLElement)
                    || !(washtenawStreetNumber instanceof HTMLInputElement)
                    || !(washtenawFrame instanceof HTMLIFrameElement)
                    || !(washtenawFrameWrap instanceof HTMLElement)) return;
                const streetNumber = washtenawStreetNumber.value.replace(/\D/g, "").slice(0, 8);
                washtenawStreetNumber.value = streetNumber;
                if (!streetNumber) {
                    showWashtenawStatus("Enter the property's street number before loading the county search.", true);
                    washtenawStreetNumber.focus();
                    return;
                }

                let copied = false;
                try {
                    await copyText(streetNumber);
                    copied = true;
                } catch (_) {
                    // The field remains selected below when clipboard access is unavailable.
                    washtenawStreetNumber.select();
                }

                washtenawFrameWrap.hidden = false;
                if (!washtenawFrame.src) {
                    washtenawFrame.src = washtenawFrame.dataset.src || "";
                }
                showWashtenawStatus(copied
                    ? `${streetNumber} copied. Loading the official county search...`
                    : "Loading the official county search. Copy the selected street number manually if needed.");
                window.clearTimeout(washtenawLoadTimer);
                washtenawLoadTimer = window.setTimeout(() => {
                    showWashtenawStatus("The county search is still loading. Your street number is preserved here; use the county-page link if the panel stays blank.", true);
                }, 12000);
                writeState({stage: "official_search_loaded", metadataStatus: "opened"});
                emitCountyGaEvent("county_official_embed_opened", {embed_name: "washtenaw_public_access"});
            }

            washtenawLoad?.addEventListener("click", loadWashtenawSearch);
            washtenawStreetNumber?.addEventListener("keydown", (event) => {
                if (event.key === "Enter") {
                    event.preventDefault();
                    loadWashtenawSearch();
                }
            });

            const gallatinLookup = workflow.querySelector("[data-gallatin-permit-lookup]");
            const gallatinMethod = workflow.querySelector("[data-gallatin-permit-method]");
            const gallatinClue = workflow.querySelector("[data-gallatin-permit-clue]");
            const gallatinClueLabel = workflow.querySelector("[data-gallatin-permit-clue-label]");
            const gallatinLoad = workflow.querySelector("[data-gallatin-permit-load]");
            const gallatinStatus = workflow.querySelector("[data-gallatin-permit-status]");
            const gallatinFrameWrap = workflow.querySelector("[data-gallatin-permit-frame-wrap]");
            const gallatinFrame = workflow.querySelector("[data-gallatin-permit-frame]");
            let gallatinLoadTimer;

            const gallatinFields = {
                RoadAddress: ["Road address", "Enter the property road address"],
                PropertyOwnerID: ["Property owner", "Enter a current or prior owner"],
                Subdivision: ["Subdivision", "Enter the subdivision name"],
                COSNumber: ["Certificate of survey (COS)", "Enter the COS number"],
                TractLotID: ["Tract or lot", "Enter the tract or lot"],
                PermitID: ["Permit number", "Enter the county permit number"]
            };

            function showGallatinStatus(message, isWarning = false) {
                if (!(gallatinStatus instanceof HTMLElement)) return;
                gallatinStatus.textContent = message;
                gallatinStatus.classList.toggle("is-warning", isWarning);
            }

            function updateGallatinMethod(replaceValue = false) {
                if (!(gallatinMethod instanceof HTMLSelectElement)
                    || !(gallatinClue instanceof HTMLInputElement)) return;
                const [label, placeholder] = gallatinFields[gallatinMethod.value] || gallatinFields.RoadAddress;
                if (gallatinClueLabel instanceof HTMLElement) gallatinClueLabel.textContent = label;
                gallatinClue.placeholder = placeholder;
                if (replaceValue || !gallatinClue.value.trim()) {
                    gallatinClue.value = gallatinMethod.value === "RoadAddress" ? safeValue(address) : "";
                }
            }

            gallatinMethod?.addEventListener("change", () => updateGallatinMethod(true));
            updateGallatinMethod();
            address?.addEventListener("input", () => {
                if (gallatinLookup instanceof HTMLElement
                    && gallatinMethod instanceof HTMLSelectElement
                    && gallatinMethod.value === "RoadAddress"
                    && gallatinClue instanceof HTMLInputElement) {
                    gallatinClue.value = safeValue(address);
                }
            });

            gallatinFrame?.addEventListener("load", () => {
                if (!gallatinFrame.getAttribute("src")) return;
                window.clearTimeout(gallatinLoadTimer);
                showGallatinStatus("The official archive opened. Choose Enter, then paste the copied clue into the matching search field.");
                emitCountyGaEvent("county_official_embed_loaded", {embed_name: "gallatin_eaglecm"});
            });

            async function loadGallatinArchive() {
                if (!(gallatinLookup instanceof HTMLElement)
                    || !(gallatinMethod instanceof HTMLSelectElement)
                    || !(gallatinClue instanceof HTMLInputElement)
                    || !(gallatinFrame instanceof HTMLIFrameElement)
                    || !(gallatinFrameWrap instanceof HTMLElement)) return;
                const clue = gallatinClue.value.trim().replace(/\s+/g, " ").slice(0, 120);
                gallatinClue.value = clue;
                if (clue.length < 2) {
                    showGallatinStatus("Enter a search clue before loading the county archive.", true);
                    gallatinClue.focus();
                    return;
                }
                let copied = false;
                try {
                    await copyText(clue);
                    copied = true;
                } catch (_) {
                    gallatinClue.select();
                }
                gallatinFrameWrap.hidden = false;
                if (!gallatinFrame.src) gallatinFrame.src = gallatinFrame.dataset.src || "";
                showGallatinStatus(copied
                    ? `${clue} copied. Loading Gallatin County's official archive...`
                    : "Loading the official archive. Copy the selected clue manually if needed.");
                window.clearTimeout(gallatinLoadTimer);
                gallatinLoadTimer = window.setTimeout(() => {
                    showGallatinStatus("The county archive is still loading. Your clue is preserved here; use the separate archive link if the panel stays blank.", true);
                }, 12000);
                writeState({stage: "official_search_loaded", metadataStatus: "opened"});
                emitCountyGaEvent("county_official_embed_opened", {
                    embed_name: "gallatin_eaglecm",
                    search_field: gallatinMethod.value
                });
            }

            gallatinLoad?.addEventListener("click", loadGallatinArchive);
            gallatinClue?.addEventListener("keydown", (event) => {
                if (event.key === "Enter") {
                    event.preventDefault();
                    loadGallatinArchive();
                }
            });

            const stLouisLookup = workflow.querySelector("[data-st-louis-septic-lookup]");
            const stLouisMethod = workflow.querySelector("[data-st-louis-septic-method]");
            const stLouisClue = workflow.querySelector("[data-st-louis-septic-clue]");
            const stLouisClueLabel = workflow.querySelector("[data-st-louis-septic-clue-label]");
            const stLouisCopy = workflow.querySelector("[data-st-louis-septic-copy]");
            const stLouisStatus = workflow.querySelector("[data-st-louis-septic-status]");
            const stLouisExplorer = workflow.querySelector("[data-st-louis-land-explorer]");

            function showStLouisStatus(message, isWarning = false) {
                if (!(stLouisStatus instanceof HTMLElement)) return;
                stLouisStatus.textContent = message;
                stLouisStatus.classList.toggle("is-warning", isWarning);
            }

            function updateStLouisMethod(replaceValue = false) {
                if (!(stLouisMethod instanceof HTMLSelectElement)
                    || !(stLouisClue instanceof HTMLInputElement)) return;
                const useParcel = stLouisMethod.value === "parcel";
                if (stLouisClueLabel instanceof HTMLElement) stLouisClueLabel.textContent = useParcel ? "Parcel PIN" : "Property location";
                stLouisClue.placeholder = useParcel ? "Enter the parcel PIN" : "Enter the property location";
                if (replaceValue || !stLouisClue.value.trim()) {
                    stLouisClue.value = useParcel ? safeValue(parcel) : safeValue(address);
                }
            }

            if (stLouisLookup instanceof HTMLElement && stLouisMethod instanceof HTMLSelectElement) {
                stLouisMethod.value = safeValue(parcel) ? "parcel" : "address";
            }
            stLouisMethod?.addEventListener("change", () => updateStLouisMethod(true));
            updateStLouisMethod();
            parcel?.addEventListener("input", () => {
                if (!(stLouisLookup instanceof HTMLElement)
                    || !(stLouisMethod instanceof HTMLSelectElement)
                    || !(stLouisClue instanceof HTMLInputElement)) return;
                const parcelValue = safeValue(parcel);
                if (parcelValue) {
                    stLouisMethod.value = "parcel";
                    updateStLouisMethod(true);
                } else if (stLouisMethod.value === "parcel") {
                    stLouisClue.value = "";
                }
            });
            address?.addEventListener("input", () => {
                if (stLouisLookup instanceof HTMLElement
                    && stLouisMethod instanceof HTMLSelectElement
                    && stLouisMethod.value === "address"
                    && stLouisClue instanceof HTMLInputElement) stLouisClue.value = safeValue(address);
            });

            async function copyStLouisClue() {
                if (!(stLouisLookup instanceof HTMLElement)
                    || !(stLouisMethod instanceof HTMLSelectElement)
                    || !(stLouisClue instanceof HTMLInputElement)) return;
                const clue = stLouisClue.value.trim().replace(/\s+/g, " ").slice(0, 120);
                stLouisClue.value = clue;
                if (clue.length < 2) {
                    showStLouisStatus("Enter the parcel PIN or property location first.", true);
                    stLouisClue.focus();
                    return;
                }
                try {
                    await copyText(clue);
                    showStLouisStatus(`${clue} copied. Open Land Explorer, choose On-Site Wastewater, select the parcel, then open Septic Records → View Doc.`);
                } catch (_) {
                    stLouisClue.select();
                    showStLouisStatus("Clipboard access was unavailable. Copy the selected clue, then open Land Explorer and choose On-Site Wastewater.", true);
                }
                writeState({stage: "official_search_prepared", metadataStatus: "prepared"});
                emitCountyGaEvent("county_official_handoff_prepared", {
                    destination_name: "st_louis_land_explorer",
                    search_field: stLouisMethod.value
                });
            }

            stLouisCopy?.addEventListener("click", copyStLouisClue);
            stLouisClue?.addEventListener("keydown", (event) => {
                if (event.key === "Enter") {
                    event.preventDefault();
                    copyStLouisClue();
                }
            });
            stLouisExplorer?.addEventListener("click", () => {
                emitCountyGaEvent("county_official_route_opened", {route_name: "st_louis_land_explorer"});
            });

            const tennesseeRecordHandoff = workflow.querySelector("[data-tennessee-record-handoff]");
            const tennesseeRecordMethod = workflow.querySelector("[data-tennessee-record-method]");
            const tennesseeRecordClue = workflow.querySelector("[data-tennessee-record-clue]");
            const tennesseeRecordClueLabel = workflow.querySelector("[data-tennessee-record-clue-label]");
            const tennesseeRecordCopy = workflow.querySelector("[data-tennessee-record-copy]");
            const tennesseeRecordStatus = workflow.querySelector("[data-tennessee-record-status]");
            const tennesseeRecordOpen = workflow.querySelector("[data-tennessee-record-open]");

            function showTennesseeRecordStatus(message, isWarning = false) {
                if (!(tennesseeRecordStatus instanceof HTMLElement)) return;
                tennesseeRecordStatus.textContent = message;
                tennesseeRecordStatus.classList.toggle("is-warning", isWarning);
            }

            function updateTennesseeRecordMethod(replaceValue = false) {
                if (!(tennesseeRecordMethod instanceof HTMLSelectElement)
                    || !(tennesseeRecordClue instanceof HTMLInputElement)) return;
                const useParcel = tennesseeRecordMethod.value === "parcel";
                if (tennesseeRecordClueLabel instanceof HTMLElement) {
                    tennesseeRecordClueLabel.textContent = useParcel ? "Parcel or tax-map ID" : "Property address";
                }
                tennesseeRecordClue.placeholder = useParcel ? "Enter the parcel or tax-map ID" : "Enter the property address";
                if (replaceValue || !tennesseeRecordClue.value.trim()) {
                    tennesseeRecordClue.value = useParcel ? safeValue(parcel) : safeValue(address);
                }
            }

            if (tennesseeRecordHandoff instanceof HTMLElement
                && tennesseeRecordMethod instanceof HTMLSelectElement) {
                tennesseeRecordMethod.value = safeValue(address) ? "address" : (safeValue(parcel) ? "parcel" : "address");
            }
            tennesseeRecordMethod?.addEventListener("change", () => updateTennesseeRecordMethod(true));
            updateTennesseeRecordMethod();
            address?.addEventListener("input", () => {
                if (tennesseeRecordHandoff instanceof HTMLElement
                    && tennesseeRecordMethod instanceof HTMLSelectElement
                    && tennesseeRecordMethod.value === "address"
                    && tennesseeRecordClue instanceof HTMLInputElement) {
                    tennesseeRecordClue.value = safeValue(address);
                }
            });
            parcel?.addEventListener("input", () => {
                if (tennesseeRecordHandoff instanceof HTMLElement
                    && tennesseeRecordMethod instanceof HTMLSelectElement
                    && tennesseeRecordMethod.value === "parcel"
                    && tennesseeRecordClue instanceof HTMLInputElement) {
                    tennesseeRecordClue.value = safeValue(parcel);
                }
            });

            async function copyTennesseeRecordClue() {
                if (!(tennesseeRecordHandoff instanceof HTMLElement)
                    || !(tennesseeRecordMethod instanceof HTMLSelectElement)
                    || !(tennesseeRecordClue instanceof HTMLInputElement)) return;
                const clue = tennesseeRecordClue.value.trim().replace(/\s+/g, " ").slice(0, 120);
                tennesseeRecordClue.value = clue;
                if (clue.length < 2) {
                    showTennesseeRecordStatus("Enter a property address or parcel clue first.", true);
                    tennesseeRecordClue.focus();
                    return;
                }
                try {
                    await copyText(clue);
                    showTennesseeRecordStatus(countyKey === "TN::williamson-county"
                        ? `${clue} copied. Open the county form and request the existing inspection or sewage-disposal file.`
                        : `${clue} copied. Open TDEC and search; a 403 or empty result is not a no-record determination.`);
                } catch (_) {
                    tennesseeRecordClue.select();
                    showTennesseeRecordStatus("Clipboard access was unavailable. Copy the selected clue manually before opening the official route.", true);
                }
                writeState({stage: "official_search_prepared", metadataStatus: "prepared"});
                emitCountyGaEvent("county_official_handoff_prepared", {
                    destination_name: countyKey === "TN::williamson-county" ? "williamson_records_form" : "tdec_septic_search",
                    search_field: tennesseeRecordMethod.value
                });
            }

            tennesseeRecordCopy?.addEventListener("click", copyTennesseeRecordClue);
            tennesseeRecordClue?.addEventListener("keydown", (event) => {
                if (event.key === "Enter") {
                    event.preventDefault();
                    copyTennesseeRecordClue();
                }
            });
            tennesseeRecordOpen?.addEventListener("click", () => {
                emitCountyGaEvent("county_official_route_opened", {
                    route_name: countyKey === "TN::williamson-county" ? "williamson_records_form" : "tdec_septic_search"
                });
            });

            const thurstonLookup = workflow.querySelector("[data-thurston-record-lookup]");
            const thurstonSearch = workflow.querySelector("[data-thurston-record-search]");
            const thurstonResults = workflow.querySelector("[data-thurston-record-results]");

            function renderThurstonResults(payload) {
                if (!(thurstonResults instanceof HTMLElement)) {
                    return;
                }
                const heading = document.createElement("h4");
                heading.textContent = payload.heading || "County archive result";
                const summary = document.createElement("p");
                summary.textContent = payload.summary || "";
                const fragment = document.createDocumentFragment();
                fragment.append(heading, summary);

                const candidates = Array.isArray(payload.candidates) ? payload.candidates : [];
                candidates.forEach((candidate) => {
                    const card = document.createElement("article");
                    card.className = "thurston-record-lookup__candidate";
                    if (candidate.septicCandidate) {
                        const badge = document.createElement("span");
                        badge.className = "pill";
                        badge.textContent = "Septic-related wording found";
                        card.append(badge);
                    }
                    const title = document.createElement("strong");
                    title.textContent = candidate.title || "County document";
                    card.append(title);
                    if (candidate.documentUrl) {
                        const actions = document.createElement("div");
                        actions.className = "county-access-workflow__actions";
                        actions.append(actionLink("Open the official document", candidate.documentUrl, true));
                        card.append(actions);
                    }
                    fragment.append(card);
                });

                const actions = document.createElement("div");
                actions.className = "county-access-workflow__actions";
                if (payload.sourceUrl) {
                    actions.append(actionLink("Inspect the official archive", payload.sourceUrl));
                }
                if (secondaryUrl) {
                    actions.append(actionLink("Use the official record-drawing request", secondaryUrl, candidates.length > 0));
                }
                fragment.append(actions);
                thurstonResults.replaceChildren(fragment);
                thurstonResults.hidden = false;
            }

            thurstonSearch?.addEventListener("click", async () => {
                if (!(thurstonLookup instanceof HTMLElement)) {
                    return;
                }
                const searchParcel = safeValue(parcel);
                if (searchParcel.length < 3) {
                    if (status instanceof HTMLElement) {
                        status.textContent = "Add the Thurston parcel number first. The county archive does not accept wildcard address searches.";
                    }
                    parcel?.focus();
                    return;
                }

                const originalLabel = thurstonSearch.textContent;
                thurstonSearch.disabled = true;
                thurstonSearch.textContent = "Searching the county archive...";
                try {
                    const response = await fetch("/api/thurston-record-lookup", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ parcelId: searchParcel })
                    });
                    const payload = await response.json();
                    renderThurstonResults(payload);
                    writeState({ stage: "metadata_queried", metadataStatus: payload.status || "unknown" });
                    sendArtifactAction("county_access_workflow", `thurston_archive_${payload.status || "unknown"}`, mode);
                    emitCountyGaEvent("county_public_index_queried", {
                        index_name: "thurston_laserfiche",
                        lookup_status: payload.status || "unknown"
                    });
                } catch (_) {
                    renderThurstonResults({
                        heading: "The Thurston County archive did not respond",
                        summary: "Your parcel number remains on this device. Open the official archive or use the record-drawing request.",
                        sourceUrl: primaryUrl,
                        candidates: []
                    });
                } finally {
                    thurstonSearch.disabled = false;
                    thurstonSearch.textContent = originalLabel;
                }
            });

            officialLinks.forEach((link) => {
                link.addEventListener("click", () => {
                    if (!workflow.contains(link) && !document.body.contains(workflow)) {
                        return;
                    }
                    awaitingReturn = true;
                    writeState({ stage: "official_opened", outcome: "" });
                    window.setTimeout(showReturn, 350);
                    sendArtifactAction("county_access_workflow", "official_route_opened", mode);
                    recordCountyStage("official_route_opened");
                    emitCountyGaEvent("county_official_route_opened", {
                        route_position: link.dataset.countyRoutePosition || "page_action"
                    });
                });
            });

            window.addEventListener("focus", () => {
                if (awaitingReturn || readState()?.stage === "official_opened") {
                    awaitingReturn = false;
                    if (!workflowReturnRecorded) {
                        workflowReturnRecorded = true;
                        recordCountyStage("official_returned");
                    }
                    showReturn();
                }
            });

                outcomes.forEach((button) => {
                    button.addEventListener("click", () => {
                    outcomes.forEach((item) => item.removeAttribute("aria-pressed"));
                    button.setAttribute("aria-pressed", "true");
                    const outcome = button.dataset.countyAccessOutcome || "not_found_online";
                    writeState({ stage: "outcome_recorded", outcome });
                    renderNext(outcome);
                    sendArtifactAction("county_access_workflow", outcome, mode);
                    recordCountyStage("outcome_recorded", outcome);
                    if (gaLastOutcome !== outcome) {
                        gaLastOutcome = outcome;
                        emitCountyGaEvent("county_return_outcome", {
                            outcome,
                            result_source: "user_reported"
                        });
                    }
                    if (outcome === "request_submitted") {
                        recordCountyStage("request_submitted", outcome);
                        emitCountyGaEvent("county_request_submitted", {
                            result_source: "user_reported",
                            case_status: "pending"
                        });
                    } else if (outcome === "artifact") {
                        recordCountyStage("record_reported", outcome);
                        emitCountyGaEvent("county_record_reported", {
                            result_source: "user_reported",
                            case_status: "needs_document_review"
                        });
                    }
                    });
                });

                const alamanceRequestSent = workflow.querySelector("[data-alamance-request-sent]");
                alamanceRequestSent?.addEventListener("click", () => {
                    writeState({ stage: "outcome_recorded", outcome: "request_submitted" });
                    renderNext("request_submitted");
                    sendArtifactAction("county_access_workflow", "request_submitted", mode);
                    recordCountyStage("request_submitted", "request_submitted");
                    emitCountyGaEvent("county_request_submitted", {
                        result_source: "user_reported",
                        case_status: "pending"
                    });
                });

                clear?.addEventListener("click", () => {
                try {
                    localStorage.removeItem(storageKey);
                } catch (_) {
                    // The fields can still be cleared when storage is unavailable.
                }
                if (address instanceof HTMLInputElement) {
                    address.value = "";
                }
                if (parcel instanceof HTMLInputElement) {
                    parcel.value = "";
                }
                if (reference instanceof HTMLInputElement) {
                    reference.value = "";
                }
                outcomes.forEach((item) => item.removeAttribute("aria-pressed"));
                if (next instanceof HTMLElement) {
                    next.replaceChildren();
                }
                if (returnPanel instanceof HTMLElement) {
                    returnPanel.hidden = true;
                }
                if (status instanceof HTMLElement) {
                    status.textContent = "Saved property task cleared.";
                }
            });
        });
    }

    setupCountyAccessWorkflows();

    function setupOfferPrepFileChecks() {
        const tools = Array.from(document.querySelectorAll("[data-offer-prep-file-check]"));
        if (!tools.length) {
            return;
        }

        const supportedStates = new Set(["TN", "IN", "NC", "SC"]);
        const stateArtifacts = {
            TN: ["septic permit or construction approval", "system layout or site plan", "final approval, repair history, and inspection-letter response if available"],
            IN: ["onsite sewage permit", "soil evaluation and site plan", "final inspection, as-built, and repair history if available"],
            NC: ["improvement permit and construction authorization", "operation permit or final record", "site plan, bedroom capacity, and repair history if available"],
            SC: ["septic permit or D-1740-related file", "site plan or system layout", "final approval, inspection, and repair history if available"]
        };

        function normalized(value) {
            return (value || "").toLowerCase().replace(/[^a-z0-9]/g, "");
        }

        function textOf(select) {
            return select.options[select.selectedIndex]?.textContent?.trim() || "Not confirmed";
        }

        function escapeHtml(value) {
            return (value || "")
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/\"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }

        tools.forEach((tool) => {
            const form = tool.querySelector("[data-offer-prep-form]");
            const address = tool.querySelector("[data-offer-prep-address]");
            const state = tool.querySelector("[data-offer-prep-state]");
            const county = tool.querySelector("[data-offer-prep-county]");
            const listingBedrooms = tool.querySelector("[data-offer-prep-listing-bedrooms]");
            const permitBedrooms = tool.querySelector("[data-offer-prep-permit-bedrooms]");
            const fileStatus = tool.querySelector("[data-offer-prep-file-status]");
            const recipient = tool.querySelector("[data-offer-prep-recipient]");
            const submit = tool.querySelector("[data-offer-prep-submit]");
            const result = tool.querySelector("[data-offer-prep-result]");
            const status = tool.querySelector("[data-offer-prep-status]");
            const heading = tool.querySelector("[data-offer-prep-heading]");
            const message = tool.querySelector("[data-offer-prep-message]");
            const facts = tool.querySelector("[data-offer-prep-facts]");
            const routeTitle = tool.querySelector("[data-offer-prep-route-title]");
            const routeNote = tool.querySelector("[data-offer-prep-route-note]");
            const routeActions = tool.querySelector("[data-offer-prep-route-actions]");
            const note = tool.querySelector("[data-offer-prep-note]");
            const copy = tool.querySelector("[data-offer-prep-copy]");
            const download = tool.querySelector("[data-offer-prep-download]");
            const print = tool.querySelector("[data-offer-prep-print]");

            if (!(form instanceof HTMLFormElement)
                || !(address instanceof HTMLInputElement)
                || !(state instanceof HTMLSelectElement)
                || !(county instanceof HTMLInputElement)
                || !(listingBedrooms instanceof HTMLSelectElement)
                || !(permitBedrooms instanceof HTMLSelectElement)
                || !(fileStatus instanceof HTMLSelectElement)
                || !(recipient instanceof HTMLSelectElement)
                || !(submit instanceof HTMLButtonElement)
                || !(result instanceof HTMLElement)
                || !(note instanceof HTMLTextAreaElement)) {
                return;
            }

            let currentRoute = null;
            sendArtifactAction("offer_prep_file_check", "opened", "offer_prep_tool");

            function routeAction(label, href, primary, targetType, external) {
                const link = document.createElement("a");
                link.className = `button ${primary ? "button--primary" : "button--secondary"}`;
                link.href = href;
                link.textContent = label;
                link.dataset.trackClick = "nav";
                link.dataset.trackSourceContext = "offer_prep_file_check_route";
                link.dataset.trackTargetType = targetType;
                if (external) {
                    link.target = "_blank";
                    link.rel = "noreferrer";
                    link.addEventListener("click", () => {
                        sendArtifactAction("offer_prep_file_check", "official_route_opened", "offer_prep_tool");
                    });
                }
                return link;
            }

            function selectedArtifacts(route) {
                return stateArtifacts[route.stateCode] || ["septic permit", "system layout", "final approval or written no-record response"];
            }

            function countyLabel(route) {
                const countyName = route.countyName || "Property county";
                return /county$/i.test(countyName) ? countyName : `${countyName} County`;
            }

            function mismatchText() {
                if (listingBedrooms.value !== "unknown"
                    && permitBedrooms.value !== "unknown"
                    && listingBedrooms.value !== permitBedrooms.value) {
                    return "The listing bedroom count does not match the permit bedroom count provided. Please confirm the official file before relying on either number.";
                }
                if (listingBedrooms.value !== "unknown" && permitBedrooms.value === "unknown") {
                    return "Please confirm the bedroom capacity shown in the official septic file before relying on the listing count.";
                }
                return "Please confirm the permit bedroom capacity and system record in the official file.";
            }

            function buildNote(route) {
                const property = address.value.trim() || `${countyLabel(route)}, ${route.stateName} property`;
                const recipientLabel = recipient.value === "seller" ? "Seller" : "Listing Agent";
                const statusLabel = textOf(fileStatus).toLowerCase();
                const artifacts = selectedArtifacts(route);
                const lines = [
                    `Subject: Septic file request before offer - ${property}`,
                    "",
                    `Hello ${recipientLabel},`,
                    "",
                    `Before we rely on the septic information for ${property}, please share the current official septic file or confirm the route used to obtain it. The file status currently appears to be: ${statusLabel}.`,
                    "",
                    "Please provide, if available:",
                    ...artifacts.map((artifact, index) => `${index + 1}. ${artifact}.`),
                    `\nListing bedrooms: ${textOf(listingBedrooms)}.`,
                    `Permit bedrooms: ${textOf(permitBedrooms)}.`,
                    "",
                    mismatchText(),
                    "",
                    `The public starting route for this property is ${route.routeTitle || `${countyLabel(route)} records`}. Please let us know if there is a more current county file, repair file, or written no-record response.`,
                    "",
                    "Thank you."
                ];
                return lines.join("\n");
            }

            function renderRoute(route, resolvedBy) {
                currentRoute = route;
                result.hidden = false;
                if (status) {
                    status.textContent = resolvedBy === "address" ? "County route resolved" : "County route selected";
                }
                if (heading) {
                    heading.textContent = route.heading || `${countyLabel(route)}, ${route.stateName} septic file route`;
                }
                if (message) {
                    message.textContent = route.message || "Open the records path first, then use the request below to obtain the file needed for the transaction.";
                }
                if (facts) {
                    const values = [route.stateName, route.countyName ? countyLabel(route) : "", textOf(fileStatus)];
                    facts.replaceChildren(...values.filter(Boolean).map((value) => {
                        const item = document.createElement("span");
                        item.textContent = value;
                        return item;
                    }));
                }
                if (routeTitle) {
                    routeTitle.textContent = route.routeTitle || `${countyLabel(route)} septic records`;
                }
                if (routeNote) {
                    routeNote.textContent = "Open the public route before treating a listing detail, old report, or verbal statement as the current septic file.";
                }
                if (routeActions) {
                    const actions = [];
                    const relayActions = Array.isArray(route.relayActions) ? route.relayActions : [];
                    relayActions.forEach((action) => {
                        if (action && action.path) {
                            actions.push(routeAction(action.label || "Open records route", action.path, Boolean(action.primary), action.targetType || "internal_page", Boolean(action.external)));
                        }
                    });
                    if (!actions.length && route.routePath) {
                        actions.push(routeAction(route.routeTitle || "Open records route", route.routePath, true, "county_records_page", false));
                    }
                    if (route.officialRouteUrl) {
                        actions.push(routeAction(officialRouteLabel(route.officialRouteUrl), route.officialRouteUrl, actions.length === 0, "official_source", true));
                    }
                    routeActions.replaceChildren(...actions);
                }
                note.value = buildNote(route);
                result.scrollIntoView({ behavior: "smooth", block: "start" });
            }

            async function resolveByAddress(value) {
                const response = await fetch("/api/address-record-finder", {
                    method: "POST",
                    headers: { "Content-Type": "application/json", Accept: "application/json" },
                    body: JSON.stringify({ address: value })
                });
                const payload = await response.json();
                if (payload.status === "unavailable") {
                    throw new Error("address_unavailable");
                }
                if (!supportedStates.has(payload.stateCode)) {
                    throw new Error("unsupported_state");
                }
                if (!["county_route", "state_route"].includes(payload.status)) {
                    throw new Error("route_not_found");
                }
                return payload;
            }

            async function resolveByCounty() {
                if (!state.value || !county.value.trim()) {
                    throw new Error("county_required");
                }
                const response = await fetch(`/api/county-finder?q=${encodeURIComponent(county.value.trim())}`, {
                    headers: { Accept: "application/json" }
                });
                const matches = await response.json();
                const countyKey = normalized(county.value.replace(/county/i, ""));
                const match = Array.isArray(matches) && matches.find((candidate) => candidate.stateCode === state.value
                    && normalized(candidate.countyName).replace(/county$/, "") === countyKey);
                if (!match) {
                    throw new Error("county_not_found");
                }
                return {
                    status: "county_route",
                    heading: `${match.countyName}, ${match.stateName} septic file route`,
                    message: match.note || "This county route is the public starting point for the permit file and records request.",
                    stateCode: match.stateCode,
                    stateName: match.stateName,
                    countyName: match.countyName,
                    routeTitle: match.title || `Open ${match.countyName} records`,
                    routePath: match.path,
                    officialRouteUrl: match.recordsUrl || "",
                    relayActions: []
                };
            }

            function renderError(error) {
                result.hidden = false;
                if (status) {
                    status.textContent = "Choose a file route";
                }
                if (heading) {
                    heading.textContent = error.message === "county_required"
                        ? "Enter a full address or choose both state and county"
                        : error.message === "address_unavailable"
                            ? "Address lookup is temporarily unavailable"
                        : error.message === "unsupported_state"
                            ? "This offer tool currently supports TN, IN, NC, and SC"
                            : "We could not confirm that county route";
                }
                if (message) {
                    message.textContent = error.message === "address_unavailable"
                        ? "Use the state and county fields above while the address resolver reconnects. The county route works without sending an address."
                        : "Try the address again or use the county route with the state selected. No address was saved.";
                }
                if (facts) {
                    facts.replaceChildren();
                }
                if (routeActions) {
                    routeActions.replaceChildren(routeAction("Open county records finder", "/septic-records-by-county/", true, "internal_page", false));
                }
                if (routeTitle) {
                    routeTitle.textContent = "Need a different route?";
                }
                if (routeNote) {
                    routeNote.textContent = "Use the county finder to search the broader records network.";
                }
                if (note) {
                    note.value = "";
                }
            }

            form.addEventListener("submit", async (event) => {
                event.preventDefault();
                if (typeof window.gtag === "function") {
                    window.gtag("event", "offer_prep_started", { route_input: address.value.trim().length >= 8 ? "address" : "county" });
                }
                const original = submit.textContent;
                submit.disabled = true;
                submit.textContent = "Finding file route...";
                try {
                    const route = address.value.trim().length >= 8
                        ? await resolveByAddress(address.value.trim())
                        : await resolveByCounty();
                    renderRoute(route, address.value.trim().length >= 8 ? "address" : "county");
                    sendArtifactAction("offer_prep_file_check", "generated", "offer_prep_tool");
                    if (typeof window.gtag === "function") {
                        window.gtag("event", "offer_prep_completed", { outcome: "route_ready" });
                    }
                } catch (error) {
                    renderError(error instanceof Error ? error : new Error("route_not_found"));
                    if (typeof window.gtag === "function") {
                        window.gtag("event", "offer_prep_completed", { outcome: "route_unavailable" });
                    }
                } finally {
                    submit.disabled = false;
                    submit.textContent = original;
                }
            });

            if (copy instanceof HTMLButtonElement) {
                copy.addEventListener("click", async () => {
                    if (!note.value) {
                        return;
                    }
                    const original = copy.textContent;
                    try {
                        await copyText(note.value);
                        sendArtifactAction("offer_prep_file_check", "copied", "offer_prep_request");
                        copy.textContent = "Request copied";
                        copy.classList.add("is-copied");
                    } catch (_error) {
                        copy.textContent = "Copy failed";
                        copy.classList.add("is-copy-failed");
                    }
                    window.setTimeout(() => {
                        copy.textContent = original;
                        copy.classList.remove("is-copied", "is-copy-failed");
                    }, 1800);
                });
            }

            if (download instanceof HTMLButtonElement) {
                download.addEventListener("click", () => {
                    if (!note.value || !currentRoute) {
                        return;
                    }
                    downloadText(`${currentRoute.stateCode.toLowerCase()}-${normalized(currentRoute.countyName)}-offer-prep-septic-file-request.txt`, note.value);
                    sendArtifactAction("offer_prep_file_check", "downloaded", "offer_prep_request");
                    const original = download.textContent;
                    download.textContent = "Downloaded";
                    download.classList.add("is-copied");
                    window.setTimeout(() => {
                        download.textContent = original;
                        download.classList.remove("is-copied");
                    }, 1800);
                });
            }

            if (print instanceof HTMLButtonElement) {
                print.addEventListener("click", () => {
                    if (!note.value || !currentRoute) {
                        return;
                    }
                    const popup = window.open("", "_blank", "width=900,height=1000");
                    if (!popup) {
                        print.textContent = "Print blocked";
                        window.setTimeout(() => { print.textContent = "Print request"; }, 1800);
                        return;
                    }
                    popup.opener = null;
                    popup.document.write(`<!doctype html><html lang="en"><head><meta charset="utf-8"><title>Septic file request</title><style>@page{margin:.6in}body{font-family:Arial,sans-serif;color:#142220;line-height:1.5}main{max-width:7in;margin:auto}.kicker{color:#236c5f;font-size:11px;font-weight:700;letter-spacing:.08em;text-transform:uppercase}h1{font-size:25px;margin:8px 0}pre{white-space:pre-wrap;font:13px/1.55 Consolas,monospace;border:1px solid #cfd8d1;padding:16px}</style></head><body><main><div class="kicker">SepticPath offer prep</div><h1>Septic file request</h1><p>${escapeHtml(countyLabel(currentRoute))}, ${escapeHtml(currentRoute.stateName)} public records path</p><pre>${escapeHtml(note.value)}</pre></main></body></html>`);
                    popup.document.close();
                    popup.focus();
                    sendArtifactAction("offer_prep_file_check", "downloaded", "offer_prep_printable_request");
                    window.setTimeout(() => popup.print(), 300);
                });
            }
        });
    }

    setupOfferPrepFileChecks();

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
                    `Reviewed septic permit bedrooms: ${countLabel(permit)}`,
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

            function render() {
                const value = current();
                result.hidden = false;
                result.dataset.bedroomCheckKind = value.kind;
                if (label) {
                    label.textContent = value.kind === "aligned" ? "Counts align" : value.kind === "mismatch" || value.kind === "conflict" ? "File gap flagged" : "Official file needed";
                }
                if (heading) {
                    heading.textContent = value.nextHeading;
                }
                if (body) {
                    body.textContent = value.nextBody;
                }
                if (facts) {
                    const items = [
                        `Listing: ${countLabel(value.listing)}`,
                        `Permit: ${countLabel(value.permit)}`,
                        value.stateLabel
                    ];
                    facts.replaceChildren(...items.map((item) => {
                        const fact = document.createElement("span");
                        fact.textContent = item;
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
                    routeLink.href = value.route;
                    if (window.self !== window.top) {
                        routeLink.target = "_blank";
                        routeLink.rel = "noreferrer";
                    }
                }
                if (requestLink instanceof HTMLAnchorElement && window.self !== window.top) {
                    requestLink.target = "_blank";
                    requestLink.rel = "noreferrer";
                }
                result.scrollIntoView({ behavior: "smooth", block: "nearest" });
            }

            form.addEventListener("submit", (event) => {
                event.preventDefault();
                render();
                sendArtifactAction("bedroom_permit_checker", "generated", "bedroom_permit_note");
            });

            if (copyButton instanceof HTMLButtonElement && note instanceof HTMLTextAreaElement) {
                copyButton.addEventListener("click", async () => {
                    const original = copyButton.textContent;
                    try {
                        await copyText(note.value);
                        sendArtifactAction("bedroom_permit_checker", "copied", "bedroom_permit_note");
                        copyButton.textContent = "Transaction note copied";
                        copyButton.classList.add("is-copied");
                    } catch (_) {
                        copyButton.textContent = "Copy failed";
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
                    downloadText(`septic-bedroom-file-check-${statePart}.txt`, note.value);
                    sendArtifactAction("bedroom_permit_checker", "downloaded", "bedroom_permit_note");
                    const original = downloadButton.textContent;
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

    function setupAlabamaPercScopeCheckers() {
        const checkers = Array.from(document.querySelectorAll("[data-alabama-perc-scope]"));
        if (!checkers.length) {
            return;
        }

        checkers.forEach((checker) => {
            const form = checker.querySelector("[data-alabama-perc-scope-form]");
            const project = checker.querySelector("[data-alabama-perc-project]");
            const evidence = checker.querySelector("[data-alabama-perc-evidence]");
            const sewer = checker.querySelector("[data-alabama-perc-sewer]");
            const county = checker.querySelector("[data-alabama-perc-county]");
            const property = checker.querySelector("[data-alabama-perc-property]");
            const result = checker.querySelector("[data-alabama-perc-result]");
            const label = checker.querySelector("[data-alabama-perc-label]");
            const heading = checker.querySelector("[data-alabama-perc-heading]");
            const body = checker.querySelector("[data-alabama-perc-body]");
            const steps = checker.querySelector("[data-alabama-perc-steps]");
            const note = checker.querySelector("[data-alabama-perc-note]");
            const copy = checker.querySelector("[data-alabama-perc-copy]");
            const selectedCounty = checker.querySelector("[data-alabama-perc-selected-county]");
            const selectedCountyHeading = checker.querySelector("[data-alabama-perc-selected-county-heading]");
            const selectedCountyQuestion = checker.querySelector("[data-alabama-perc-selected-county-question]");
            const selectedCountyPage = checker.querySelector("[data-alabama-perc-selected-county-page]");
            const selectedCountyForm = checker.querySelector("[data-alabama-perc-selected-county-form]");

            if (!(form instanceof HTMLFormElement)
                || !(project instanceof HTMLSelectElement)
                || !(evidence instanceof HTMLSelectElement)
                || !(sewer instanceof HTMLSelectElement)
                || !(result instanceof HTMLElement)
                || !(note instanceof HTMLTextAreaElement)) {
                return;
            }

            function optionText(select) {
                return select.options[select.selectedIndex]?.textContent?.trim() || "Not provided";
            }

            function scope() {
                const projectText = optionText(project);
                const evidenceText = optionText(evidence);
                const sewerText = optionText(sewer);
                const countyText = (county instanceof HTMLInputElement || county instanceof HTMLSelectElement) && county.value.trim()
                    ? county.value.trim()
                    : "Not provided";
                const countyOption = county instanceof HTMLSelectElement
                    ? county.options[county.selectedIndex]
                    : null;
                const countyStatus = countyOption?.dataset.countyStatus || "";
                const countyPhone = countyOption?.dataset.countyPhone || "";
                const countyPageUrl = countyOption?.dataset.countyPage || "";
                const countyPageLabel = countyOption?.dataset.countyPageLabel || "";
                const countyFormUrl = countyOption?.dataset.countyForm || "";
                const countyFormLabel = countyOption?.dataset.countyFormLabel || "";
                const countyQuestion = countyOption?.dataset.countyQuestion || "";
                const propertyText = property instanceof HTMLInputElement && property.value.trim()
                    ? property.value.trim()
                    : "Not provided";
                let resultLabel = "Quote the usable scope";
                let resultHeading = "Request a professional soil and site evaluation with the usable deliverables";
                let resultBody = "For Alabama land without a usable prior file, ask for the evaluation, test result, and plot plan together. That is the package that can support the county Permit to Install path, not merely a standalone test number.";
                let nextSteps = [
                    "Confirm public sewer availability with the local sewer authority or county health department.",
                    "Ask a qualified Alabama professional to price the soil and site evaluation, required testing, and plot plan as one scope.",
                    "Submit the resulting application and attachments to the county health department for Permit to Install review."
                ];

                if (sewer.value === "yes") {
                    resultLabel = "Sewer check comes first";
                    resultHeading = "Confirm the sewer decision before paying for an onsite evaluation";
                    resultBody = "ADPH directs buyers and builders to check sewer availability first. Do not treat an onsite test quote as the default path until the local sewer answer is documented.";
                    nextSteps = [
                        "Ask the sewer authority whether service is available to this parcel and what connection constraints apply.",
                        "Ask the county health department whether onsite evaluation is still required for the property.",
                        "Only then request a soil and site evaluation if the onsite path remains active."
                    ];
                } else if (evidence.value === "report") {
                    resultLabel = "Existing report needs review";
                    resultHeading = "Price a report review and county-ready application, not a duplicate test by default";
                    resultBody = "A prior soil or perc report may reduce repeat field work, but it is useful only if the county accepts it for this project. Send the report and plot plan with your request before assuming the lowest quote applies.";
                    nextSteps = [
                        "Send the prior report and plot plan to the county health department or qualified professional for an acceptance check.",
                        "Ask what must be refreshed, re-staked, redesigned, or added before a Permit to Install can be reviewed.",
                        "Request a quote that separates report review, any field update, design, and county application work."
                    ];
                } else if (evidence.value === "file") {
                    resultLabel = "Permit file must be matched";
                    resultHeading = "Verify that the existing permit file fits the new project before you price anything";
                    resultBody = "An older Permit to Install or Approval for Use can be valuable evidence, but it does not automatically authorize a replacement, addition, or different building. Match the file to the current project before ordering new work.";
                    nextSteps = [
                        "Request the permit, Approval for Use, system diagram, and any soil-test history from the county file owner.",
                        "Compare the recorded building, bedroom count, system layout, and current project scope.",
                        "Ask the county or qualified professional whether the existing file can be used, amended, or must be re-evaluated."
                    ];
                } else if (evidence.value === "unknown" || project.value === "purchase") {
                    resultLabel = "File search and quote in parallel";
                    resultHeading = "Ask for the county file before paying for a new test";
                    resultBody = "For a purchase or an unknown file, the fastest defensible route is to request the existing permit and soil history while asking what a new evaluation would include if the record is missing or unusable.";
                    nextSteps = [
                        "Request the Permit to Install, Approval for Use, diagram, and soil-test history from the county health department.",
                        "Ask a professional for a conditional quote that states what happens if no usable record exists.",
                        "Use the written county response before making a price, contract, or closing decision."
                    ];
                }

                const noteLines = [
                    "Subject: Alabama onsite sewage evaluation and Permit to Install quote request",
                    "",
                    `Project: ${projectText}`,
                    `County: ${countyText}`,
                    `Property address or parcel ID: ${propertyText}`,
                    `Existing evidence: ${evidenceText}`,
                    `Public sewer: ${sewerText}`,
                    ...(countyPhone ? [
                        `County handoff: ${countyStatus}`,
                        `Environmental contact: ${countyPhone}`,
                        `Official county page: ${countyPageUrl}`,
                        `Official form path: ${countyFormUrl}`,
                        `Ask the county: ${countyQuestion}`
                    ] : []),
                    "",
                    "Please quote the scope needed to produce a county-usable Alabama onsite sewage package. Please state separately:",
                    "1. Soil and site evaluation or percolation testing required for this parcel.",
                    "2. Plot plan, design, or other deliverables included with the evaluation.",
                    "3. Whether you will identify what the county health department needs for a Permit to Install.",
                    "4. What prior permit, Approval for Use, soil report, or survey you need before field work.",
                    "5. Any exclusions, re-staking, redesign, county fees, or follow-up work not included in the quoted price.",
                    "6. Whether the ADPH $150-$250 public site-evaluation band applies in this county or a private registered professional is required.",
                    "7. The exact county Permit to Install application fee; ADPH currently publishes a $100-$200 dwelling band.",
                    "",
                    "I understand this request is for a scope and price estimate, not a guarantee that the parcel or a particular system will be approved."
                ];

                return {
                    resultLabel,
                    resultHeading,
                    resultBody,
                    nextSteps,
                    noteText: noteLines.join("\n"),
                    countyText,
                    countyStatus,
                    countyPhone,
                    countyPageUrl,
                    countyPageLabel,
                    countyFormUrl,
                    countyFormLabel,
                    countyQuestion
                };
            }

            function render() {
                const value = scope();
                result.hidden = false;
                if (label) {
                    label.textContent = value.resultLabel;
                }
                if (heading) {
                    heading.textContent = value.resultHeading;
                }
                if (body) {
                    body.textContent = value.resultBody;
                }
                if (steps) {
                    steps.replaceChildren(...value.nextSteps.map((step) => {
                        const item = document.createElement("li");
                        item.textContent = step;
                        return item;
                    }));
                }
                if (selectedCounty instanceof HTMLElement) {
                    selectedCounty.hidden = !value.countyPhone;
                }
                if (selectedCountyHeading) {
                    selectedCountyHeading.textContent = value.countyPhone
                        ? `${value.countyText} · ${value.countyPhone} · ${value.countyStatus}`
                        : "";
                }
                if (selectedCountyQuestion) {
                    selectedCountyQuestion.textContent = value.countyQuestion
                        ? `Ask: “${value.countyQuestion}”`
                        : "";
                }
                if (selectedCountyPage instanceof HTMLAnchorElement) {
                    selectedCountyPage.href = value.countyPageUrl || "#";
                    selectedCountyPage.textContent = value.countyPageLabel || "Open official county page";
                }
                if (selectedCountyForm instanceof HTMLAnchorElement) {
                    selectedCountyForm.href = value.countyFormUrl || "#";
                    selectedCountyForm.textContent = value.countyFormLabel || "Open official form";
                }
                note.value = value.noteText;
                result.scrollIntoView({ behavior: "smooth", block: "start" });
            }

            form.addEventListener("submit", (event) => {
                event.preventDefault();
                render();
                sendArtifactAction("alabama_perc_scope", "generated", "alabama_perc_quote_scope");
            });

            if (copy instanceof HTMLButtonElement) {
                copy.addEventListener("click", async () => {
                    const original = copy.textContent;
                    try {
                        await copyText(note.value);
                        sendArtifactAction("alabama_perc_scope", "copied", "alabama_perc_quote_scope");
                        copy.textContent = "Request copied";
                        copy.classList.add("is-copied");
                    } catch (_error) {
                        copy.textContent = "Copy failed";
                        copy.classList.add("is-copy-failed");
                    }
                    window.setTimeout(() => {
                        copy.textContent = original;
                        copy.classList.remove("is-copied", "is-copy-failed");
                    }, 1800);
                });
            }
        });
    }

    setupAlabamaPercScopeCheckers();

    function setupBedroomEmbedCopies() {
        const copies = Array.from(document.querySelectorAll("[data-bedroom-embed-copy]"));
        copies.forEach((button) => {
            const code = button.parentElement?.querySelector("[data-bedroom-embed-code]");
            if (!(button instanceof HTMLButtonElement) || !(code instanceof HTMLTextAreaElement)) {
                return;
            }
            button.addEventListener("click", async () => {
                const original = button.textContent;
                try {
                    await copyText(code.value);
                    button.textContent = "Embed code copied";
                    button.classList.add("is-copied");
                } catch (_) {
                    button.textContent = "Copy failed";
                    button.classList.add("is-copy-failed");
                }
                window.setTimeout(() => {
                    button.textContent = original;
                    button.classList.remove("is-copied", "is-copy-failed");
                }, 1800);
            });
        });
    }

    setupBedroomEmbedCopies();

    function setupRecordsRequestBuilders() {
        const builders = Array.from(document.querySelectorAll("[data-records-request-builder]"));
        if (!builders.length) {
            return;
        }
        const requestedTaskMode = new URLSearchParams(window.location.search).get("mode") === "task";
        const requestProgressStorageKey = "septicpath-records-request-progress-v1";
        const requestProgressLifetime = 30 * 24 * 60 * 60 * 1000;
        if (requestedTaskMode) {
            document.body.classList.add("records-task-mode");
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
            const contextStateName = builder.dataset.requestContextStateName || "";
            const contextCountyName = builder.dataset.requestContextCountyName || county;
            const contextOfficeLabel = builder.dataset.requestContextOfficeLabel || "";
            const contextContactLine = builder.dataset.requestContextContactLine || "";
            const conflictNote = builder.dataset.requestConflictNote || "";
            const defaultRoute = stateRouteDetails.DEFAULT;
            const hasExactOfficeContext = Boolean(contextOfficeLabel || contextContactLine);
            const routeDetail = hasExactOfficeContext || (stateCode === "DEFAULT" && (contextStateName || contextCountyName))
                ? {
                    ...defaultRoute,
                    title: contextOfficeLabel || (contextCountyName
                        ? `${contextCountyName} septic records office`
                        : `${contextStateName} local septic records office`),
                    body: contextContactLine
                        ? `Start with ${contextOfficeLabel || contextCountyName || contextStateName}. ${contextContactLine}`
                        : `Start with the verified ${contextCountyName || contextStateName} records route. Send the request to the county health, environmental health, onsite wastewater, permitting, or public-records desk that owns parcel-level septic files.`,
                    channel: contextOfficeLabel
                        || (contextCountyName
                            ? `${contextCountyName} health, environmental health, onsite wastewater, permitting, or public-records desk`
                            : defaultRoute.channel)
                }
                : stateRouteDetails[stateCode] || defaultRoute;
            const stateLabel = stateCode === "DEFAULT" && contextStateName
                ? `${contextStateName} / county file owner`
                : labelOf(stateSelect) || "County health office";
            const routeLabel = stateCode === "DEFAULT"
                ? `the ${routeDetail.channel}`
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
                conflictNote,
                routeDetail,
                routeNote: contextCountyName
                    ? contextContactLine
                        ? `Use ${contextOfficeLabel || contextCountyName} first. ${contextContactLine} If it does not own the requested file, ask it to name the delegated or archived file owner.`
                        : `Use the verified ${contextCountyName} county route first. If the first desk does not own septic files, ask it to name the delegated, regional, archived, or pre-digital file owner.`
                    : stateRouteNotes[stateCode] || stateRouteNotes.DEFAULT,
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
            const items = [
                `Submit through the official ${current.routeDetail.channel}.`,
                `Include address, county, parcel/APN/TMS, owner, legal description, subdivision, lot number, and prior permit number when available.`,
                current.artifactCheck,
                current.reasonCheck,
                current.routeDetail.fallback
            ];
            if (current.conflictNote) {
                items.splice(2, 0, "Attach or identify both conflicting source files and ask which record is current and controlling.");
            }
            return items;
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
            const conflictLines = current.conflictNote
                ? [
                    "",
                    `The documents already collected disagree: ${current.conflictNote}.`,
                    "Please confirm which record is current and controlling, and return the document or written explanation that resolves the difference."
                ]
                : [];

            return [
                `Subject: Septic records request for ${current.addressLine}`,
                "",
                `Hello, I am requesting septic system records for a property in ${current.countyLine}. Please route this through ${current.routeLabel}.`,
                "",
                `Property: ${current.addressLine}`,
                `Parcel / APN / tax ID: ${current.parcelLine}`,
                `Owner name: ${current.ownerLine}`,
                `Reason for request: ${current.reason}.`,
                ...conflictLines,
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
                "- Use the message as free-text only when the official route accepts it. Otherwise transfer the factual fields into the government form or portal.",
                "- This packet is not an official form and must not replace a required county or state document.",
                "- Keep the identifier checklist with the response so a missing result does not get mistaken for a missing septic file.",
                "",
                "Request target",
                `- State route: ${current.stateLabel}`,
                `- Submission route: ${current.routeDetail.title}`,
                `- Channel: ${current.routeDetail.channel}`,
                `- County: ${current.county ? `${current.county} County` : "[property county]"}`,
                `- Record needed: ${current.recordLabel}`,
                `- Reason: ${current.reasonLabel}`,
                ...(current.conflictNote ? [`- Known conflict: ${current.conflictNote}`] : []),
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
                ["Reason", current.reasonLabel],
                ...(current.conflictNote ? [["Known conflict", current.conflictNote]] : [])
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

        function consumeFinderRequestContext(builder) {
            let context = null;
            try {
                context = JSON.parse(sessionStorage.getItem("septic-records-request-context") || "null")
                    || JSON.parse(localStorage.getItem("septic-records-request-context") || "null");
            } catch (_) {
                context = null;
            }
            if (!context || typeof context !== "object"
                || (context.savedAt && Date.now() - Number(context.savedAt) > 30 * 24 * 60 * 60 * 1000)) {
                return;
            }

            const state = builder.querySelector("[data-request-state]");
            const record = builder.querySelector("[data-request-record]");
            const reason = builder.querySelector("[data-request-reason]");
            const county = builder.querySelector("[data-request-county]");
            const address = builder.querySelector("[data-request-address]");
            const taskContext = builder.querySelector("[data-records-task-context]");
            const taskHeading = builder.querySelector("[data-records-task-heading]");
            const taskCopy = builder.querySelector("[data-records-task-copy]");
            const purposeReasons = {
                buying: "buying",
                bedrooms: "addition",
                location: "owner_records",
                repair: "repair",
                replacement: "replacement",
                lender: "lender",
                owner: "owner_records"
            };

            if (state instanceof HTMLSelectElement
                && Array.from(state.options).some((option) => option.value === context.stateCode)) {
                state.value = context.stateCode;
            }
            if (reason instanceof HTMLSelectElement && purposeReasons[context.purpose]) {
                reason.value = purposeReasons[context.purpose];
            }
            if (record instanceof HTMLSelectElement
                && Array.from(record.options).some((option) => option.value === context.requestedRecord)) {
                record.value = context.requestedRecord;
            }
            if (county instanceof HTMLInputElement) {
                county.value = String(context.countyName || "").replace(/\s+County$/i, "");
            }
            if (address instanceof HTMLInputElement) {
                address.value = context.matchedAddress || "";
            }
            builder.dataset.requestContextStateName = context.stateName || context.stateCode || "";
            builder.dataset.requestContextCountyName = context.countyName || "";
            builder.dataset.requestContextOfficeLabel = context.officeLabel || "";
            builder.dataset.requestContextContactLine = context.contactLine || "";
            builder.dataset.requestConflictNote = String(context.conflictNote || "").slice(0, 800);

            const taskMode = context.taskMode
                || new URLSearchParams(window.location.search).get("mode") === "task";
            if (taskMode) {
                document.body.classList.add("records-task-mode");
                if (taskContext instanceof HTMLElement) {
                    taskContext.hidden = false;
                }
                if (taskHeading) {
                    taskHeading.textContent = context.conflictNote
                        ? `Resolve the conflicting ${context.countyName || "property"} records.`
                        : context.countyName
                            ? `Request the missing ${context.countyName} record.`
                        : "Request the missing property record.";
                }
                if (taskCopy) {
                    const recordLabel = record instanceof HTMLSelectElement
                        ? record.options[record.selectedIndex]?.text
                        : "record";
                    taskCopy.textContent = context.conflictNote
                        ? `${context.matchedAddress || "This property"} is already filled in. The request names the conflicting values and asks the file owner which record controls.`
                        : `${context.matchedAddress || "This property"} is already filled in. The request is set to ${String(recordLabel || "the missing record").toLowerCase()}.`;
                }
            }
        }

        builders.forEach((builder) => {
            const inputs = Array.from(builder.querySelectorAll("input, select"));
            const copyButton = builder.querySelector("[data-records-request-copy]");
            const downloadButton = builder.querySelector("[data-records-request-download]");
            const printButton = builder.querySelector("[data-records-request-print]");
            const filenameLabel = builder.querySelector("[data-records-request-filename]");
            const status = builder.querySelector("[data-records-request-status]");
            const output = builder.querySelector("[data-records-request-output]");
            const progressStatus = builder.querySelector("[data-records-request-progress-status]");
            const sentDate = builder.querySelector("[data-records-request-sent-date]");
            const followupWindow = builder.querySelector("[data-records-request-followup-window]");
            const followupDate = builder.querySelector("[data-records-request-followup-date]");
            const progressNote = builder.querySelector("[data-records-request-progress-note]");
            const markSentButton = builder.querySelector("[data-records-request-mark-sent]");
            const channelConfirmed = builder.querySelector("[data-records-request-channel-confirmed]");
            const saveProgressButton = builder.querySelector("[data-records-request-save-progress]");
            const clearProgressButton = builder.querySelector("[data-records-request-clear-progress]");
            const progressMessage = builder.querySelector("[data-records-request-progress-message]");

            const taskContext = builder.querySelector("[data-records-task-context]");
            if (requestedTaskMode && taskContext instanceof HTMLElement) {
                taskContext.hidden = false;
            }
            consumeFinderRequestContext(builder);

            if (channelConfirmed instanceof HTMLInputElement && markSentButton instanceof HTMLButtonElement) {
                const syncChannelConfirmation = () => {
                    markSentButton.disabled = !channelConfirmed.checked;
                    if (status) {
                        status.textContent = channelConfirmed.checked
                            ? "Official intake checked · ready for your final submission"
                            : "Confirm official intake before sending";
                    }
                };
                channelConfirmed.addEventListener("change", syncChannelConfirmation);
                syncChannelConfirmation();
            }

            function dateInputValue(date) {
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, "0");
                const day = String(date.getDate()).padStart(2, "0");
                return `${year}-${month}-${day}`;
            }

            function requestDraft() {
                return {
                    state: valueOf(builder, "[data-request-state]"),
                    record: valueOf(builder, "[data-request-record]"),
                    reason: valueOf(builder, "[data-request-reason]"),
                    county: valueOf(builder, "[data-request-county]"),
                    address: valueOf(builder, "[data-request-address]"),
                    parcel: valueOf(builder, "[data-request-parcel]"),
                    owner: valueOf(builder, "[data-request-owner]")
                };
            }

            function saveRequestProgress(message = "") {
                const selectedStatus = progressStatus instanceof HTMLSelectElement ? progressStatus.value : "draft";
                const payload = {
                    version: 1,
                    updatedAt: Date.now(),
                    expiresAt: Date.now() + requestProgressLifetime,
                    status: selectedStatus,
                    sentDate: sentDate instanceof HTMLInputElement ? sentDate.value : "",
                    followupWindow: followupWindow instanceof HTMLSelectElement ? followupWindow.value : "7",
                    followupDate: followupDate instanceof HTMLInputElement ? followupDate.value : "",
                    note: progressNote instanceof HTMLInputElement ? progressNote.value.trim().slice(0, 240) : "",
                    draft: requestDraft()
                };
                try {
                    localStorage.setItem(requestProgressStorageKey, JSON.stringify(payload));
                    if (progressMessage) {
                        progressMessage.textContent = message || (selectedStatus === "closed"
                            ? "This task is complete. You can clear it now or keep the response note on this device."
                            : "Progress saved on this device.");
                    }
                } catch (_) {
                    if (progressMessage) {
                        progressMessage.textContent = "This browser could not save progress. Download the packet instead.";
                    }
                }
            }

            function restoreRequestProgress() {
                let saved = null;
                try {
                    saved = JSON.parse(localStorage.getItem(requestProgressStorageKey) || "null");
                } catch (_) {
                    return;
                }
                if (!saved || saved.version !== 1 || Date.now() > Number(saved.expiresAt || 0)) {
                    try {
                        localStorage.removeItem(requestProgressStorageKey);
                    } catch (_) {
                        // Nothing needs clearing when storage is unavailable.
                    }
                    return;
                }
                const incomingAddress = valueOf(builder, "[data-request-address]");
                const savedAddress = String(saved.draft?.address || "");
                if (incomingAddress && savedAddress
                    && incomingAddress.toLowerCase() !== savedAddress.toLowerCase()) {
                    if (progressMessage) {
                        progressMessage.textContent = "A new property was carried in. Older saved progress was not applied.";
                    }
                    return;
                }
                const draftSelectors = {
                    state: "[data-request-state]",
                    record: "[data-request-record]",
                    reason: "[data-request-reason]",
                    county: "[data-request-county]",
                    address: "[data-request-address]",
                    parcel: "[data-request-parcel]",
                    owner: "[data-request-owner]"
                };
                Object.entries(draftSelectors).forEach(([key, selector]) => {
                    const field = builder.querySelector(selector);
                    const value = String(saved.draft?.[key] || "");
                    if (field instanceof HTMLSelectElement) {
                        if (Array.from(field.options).some((option) => option.value === value)) {
                            field.value = value;
                        }
                    } else if (field instanceof HTMLInputElement && value) {
                        field.value = value;
                    }
                });
                if (progressStatus instanceof HTMLSelectElement
                    && Array.from(progressStatus.options).some((option) => option.value === saved.status)) {
                    progressStatus.value = saved.status;
                }
                if (sentDate instanceof HTMLInputElement) {
                    sentDate.value = String(saved.sentDate || "");
                }
                if (followupWindow instanceof HTMLSelectElement
                    && Array.from(followupWindow.options).some((option) => option.value === saved.followupWindow)) {
                    followupWindow.value = saved.followupWindow;
                }
                if (followupDate instanceof HTMLInputElement) {
                    followupDate.value = String(saved.followupDate || "");
                }
                if (progressNote instanceof HTMLInputElement) {
                    progressNote.value = String(saved.note || "").slice(0, 240);
                }
                if (progressMessage) {
                    const statusLabel = progressStatus instanceof HTMLSelectElement
                        ? progressStatus.options[progressStatus.selectedIndex]?.text
                        : "Saved";
                    progressMessage.textContent = `${statusLabel || "Saved"} · restored from this device.`;
                }
                const tracker = builder.querySelector("[data-records-request-tracker]");
                if (tracker instanceof HTMLDetailsElement && saved.status !== "draft") {
                    tracker.open = true;
                }
            }

            restoreRequestProgress();

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
                        sendArtifactAction("records_request_builder", "copied", "records_request_packet");
                        copyButton.textContent = "Request copied";
                        copyButton.classList.add("is-copied");
                        setTemporaryStatus(status, "Draft copied to clipboard", channelConfirmed?.checked
                            ? "Official intake checked · ready for your final submission"
                            : "Confirm official intake before sending");
                    } catch (_error) {
                        copyButton.textContent = "Copy failed";
                        copyButton.classList.add("is-copy-failed");
                        setTemporaryStatus(status, "Copy failed. Select the text manually.", "Confirm official intake before sending");
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
                    sendArtifactAction("records_request_builder", "downloaded", "records_request_packet");
                    downloadButton.textContent = "Downloaded";
                    downloadButton.classList.add("is-copied");
                    setTemporaryStatus(status, `Downloaded preparation packet ${filename}`, "Confirm official intake before sending");
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
                        sendArtifactAction("records_request_builder", "pdf_opened", "records_request_packet");
                        printButton.textContent = "PDF view opened";
                        printButton.classList.add("is-copied");
                        setTemporaryStatus(status, "Printable preparation packet opened", "Confirm official intake before sending");
                    } else {
                        printButton.textContent = "Print blocked";
                        printButton.classList.add("is-copy-failed");
                        setTemporaryStatus(status, "Pop-up blocked. Allow pop-ups to print the packet.", "Confirm official intake before sending");
                    }
                    window.setTimeout(() => {
                        printButton.textContent = original;
                        printButton.classList.remove("is-copied", "is-copy-failed");
                    }, 1800);
                });
            }

            markSentButton?.addEventListener("click", () => {
                if (!(channelConfirmed instanceof HTMLInputElement) || !channelConfirmed.checked) {
                    if (progressMessage) {
                        progressMessage.textContent = "Check the official route and confirm its accepted intake before marking this request sent.";
                    }
                    return;
                }
                const today = new Date();
                const followup = new Date(today);
                const followupDays = followupWindow instanceof HTMLSelectElement
                    && followupWindow.value === "14" ? 14 : 7;
                followup.setDate(followup.getDate() + followupDays);
                if (progressStatus instanceof HTMLSelectElement) {
                    progressStatus.value = "sent";
                }
                if (sentDate instanceof HTMLInputElement) {
                    sentDate.value = dateInputValue(today);
                }
                if (followupDate instanceof HTMLInputElement
                    && followupWindow instanceof HTMLSelectElement
                    && followupWindow.value !== "custom") {
                    followupDate.value = dateInputValue(followup);
                }
                saveRequestProgress("Marked sent. Your personal follow-up is saved on this device.");
                const tracker = builder.querySelector("[data-records-request-tracker]");
                if (tracker instanceof HTMLDetailsElement) {
                    tracker.open = true;
                    tracker.scrollIntoView({ behavior: "smooth", block: "nearest" });
                }
                sendArtifactAction("records_request_builder", "request_marked_sent", "records_request_progress");
            });

            followupWindow?.addEventListener("change", () => {
                if (!(followupWindow instanceof HTMLSelectElement)
                    || !(followupDate instanceof HTMLInputElement)
                    || followupWindow.value === "custom") {
                    followupDate?.focus();
                    return;
                }
                const base = sentDate instanceof HTMLInputElement && sentDate.value
                    ? new Date(`${sentDate.value}T12:00:00`)
                    : new Date();
                base.setDate(base.getDate() + Number(followupWindow.value));
                followupDate.value = dateInputValue(base);
            });

            saveProgressButton?.addEventListener("click", () => {
                saveRequestProgress();
                sendArtifactAction("records_request_builder", "progress_saved", "records_request_progress");
            });

            clearProgressButton?.addEventListener("click", () => {
                try {
                    localStorage.removeItem(requestProgressStorageKey);
                } catch (_) {
                    // Reset the visible fields even when storage is unavailable.
                }
                if (progressStatus instanceof HTMLSelectElement) {
                    progressStatus.value = "draft";
                }
                if (sentDate instanceof HTMLInputElement) {
                    sentDate.value = "";
                }
                if (followupDate instanceof HTMLInputElement) {
                    followupDate.value = "";
                }
                if (followupWindow instanceof HTMLSelectElement) {
                    followupWindow.value = "7";
                }
                if (progressNote instanceof HTMLInputElement) {
                    progressNote.value = "";
                }
                if (progressMessage) {
                    progressMessage.textContent = "Saved progress cleared from this device.";
                }
            });

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
                        sendArtifactAction("workflow_packet", "copied", "workflow_packet");
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
                    sendArtifactAction("workflow_packet", "downloaded", "workflow_packet");
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
            return `${analyticsSourcePage()}#share`;
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
                                sourcePage: analyticsSourcePage(),
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
                        sourcePage: analyticsSourcePage(),
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

    function setupCountyRoutePickers() {
        document.querySelectorAll("[data-county-route-picker]").forEach((picker) => {
            const select = picker.querySelector("[data-county-route-select]");
            const submit = picker.querySelector("[data-county-route-submit]");
            const status = picker.querySelector("[data-county-route-status]");
            const routePrefix = picker.dataset.countyRoutePrefix || "";
            const stateLabel = picker.dataset.countyRouteState || "State";
            if (!(select instanceof HTMLSelectElement) || !(submit instanceof HTMLButtonElement)) {
                return;
            }

            const sync = () => {
                const selected = select.options[select.selectedIndex];
                const hasRoute = Boolean(selected?.value);
                submit.disabled = !hasRoute;
                if (status) {
                    status.textContent = hasRoute
                        ? `${selected.text.trim()} is ready. Open its local permit-record workflow.`
                        : "Select a county to stay on SepticPath and open its local permit-record workflow.";
                }
            };

            select.addEventListener("change", sync);
            window.addEventListener("pageshow", sync);
            picker.addEventListener("submit", (event) => {
                event.preventDefault();
                const targetPath = select.value;
                if (!routePrefix.startsWith("/septic-records-checklist/") || !targetPath.startsWith(routePrefix)) {
                    sync();
                    return;
                }
                sendNavigationEvent({
                    sourcePage: analyticsSourcePage(),
                    sourceContext: "state_records_county_picker",
                    targetPath,
                    targetType: "county_records_page",
                    targetLabel: select.options[select.selectedIndex]?.text?.trim() || `${stateLabel} county records`
                });
                window.location.assign(targetPath);
            });
            sync();
        });
    }

    setupCountyRoutePickers();
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

    function setupCalculatorFunnelTracking() {
        const form = document.querySelector("#cost-estimator-form");
        if (!(form instanceof HTMLFormElement)) {
            return;
        }

        let started = false;
        const emitStarted = () => {
            if (started) {
                return;
            }
            started = true;
            const state = form.querySelector("[name='stateCode']");
            const project = form.querySelector("[name='projectType']");
            const params = { estimator_type: "cost_estimator" };
            if (state instanceof HTMLSelectElement && state.value) {
                params.state_code = state.value;
            }
            if (project instanceof HTMLSelectElement && project.value) {
                params.project_type = project.value;
            }
            emitGaEvent("calculator_started", params);
        };

        form.addEventListener("input", emitStarted);
        form.addEventListener("change", emitStarted);
        form.addEventListener("submit", emitStarted);
    }

    if (!coreAlreadyLoaded) {
        trackGaEvents();
        setupCalculatorFunnelTracking();

        document.addEventListener("click", (event) => {
            const anchor = event.target.closest("a[data-track-click]");
            if (!anchor) {
                return;
            }

            const targetPath = navigationTarget(anchor);
            if (!targetPath || (targetPath.startsWith("/") && targetPath.startsWith("/events/"))) {
                return;
            }

            sendNavigationEvent({
                sourcePage: analyticsSourcePage(),
                sourceContext: anchor.dataset.trackSourceContext || "",
                targetPath,
                targetType: anchor.dataset.trackTargetType || "",
                targetLabel: (anchor.dataset.trackLabel || anchor.textContent || "").trim().replace(/\s+/g, " ")
            });
            if (anchor.dataset.trackTargetType === "quote_form") {
                emitGaEvent("lead_cta_clicked", {
                    cta_type: "quote_form",
                    source_context: anchor.dataset.trackSourceContext || ""
                });
            }
        });
    }
})();
