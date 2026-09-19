(() => {
    "use strict";

    document.documentElement.classList.add("js");
    window.SepticPathCoreLoaded = true;

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

    function sendEvent(endpoint, payload) {
        const body = JSON.stringify(payload);
        if (navigator.sendBeacon) {
            navigator.sendBeacon(endpoint, new Blob([body], { type: "application/json" }));
            return;
        }
        fetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body,
            keepalive: true
        }).catch(() => {});
    }

    function setupHashAnchorOffset() {
        const offsetTargets = new Set(["records-request-builder", "county-access-workflow", "county-acquisition-workspace", "send-note"]);

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

        toggle.addEventListener("click", () => setExpanded(toggle.getAttribute("aria-expanded") !== "true"));
        nav.querySelectorAll("a").forEach((link) => link.addEventListener("click", closeIfMobile));

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

    function buildGaParams(element) {
        const params = {};
        for (const attribute of element.attributes) {
            if (attribute.name.startsWith("data-ga-param-") && attribute.value !== "") {
                params[attribute.name.substring("data-ga-param-".length).replace(/-/g, "_")] = attribute.value;
            }
        }
        return params;
    }

    function emitGaEvent(eventName, params) {
        if (eventName && typeof window.gtag === "function") {
            window.gtag("event", eventName, params);
        }
    }

    function setupPrimaryFunnelEvents() {
        const costForm = document.querySelector("#cost-estimator-form");
        if (costForm instanceof HTMLFormElement) {
            costForm.addEventListener("submit", () => emitGaEvent("calculator_started", { calculator_type: "septic_cost" }));
        }
        if (document.querySelector("#result-top")) {
            emitGaEvent("calculator_completed", { calculator_type: "septic_cost" });
        }
        if (document.querySelector("[data-county-access-workflow]")) {
            emitGaEvent("county_route_viewed", { page_type: "county_records" });
        }
        document.addEventListener("click", (event) => {
            if (!(event.target instanceof Element)) {
                return;
            }
            const anchor = event.target.closest("a[data-track-click]");
            if (!(anchor instanceof HTMLAnchorElement)) {
                return;
            }
            const targetType = anchor.dataset.trackTargetType || "";
            if (targetType.startsWith("official")) {
                emitGaEvent("official_source_clicked", { source_context: anchor.dataset.trackSourceContext || "", source_type: targetType });
            }
            if (targetType === "quote_form" || targetType === "lead_form" || anchor.getAttribute("href") === "#quote-request") {
                emitGaEvent("lead_cta_clicked", { source_context: anchor.dataset.trackSourceContext || "", cta_type: targetType || "quote_form" });
            }
        });
    }

    function setupRecordHelpFunnel() {
        const contextKey = "septicpath_record_help_context";
        const sourcePageKey = "septicpath_record_help_source_page";
        const entryPageKey = "septicpath_record_help_entry_page";
        const attributionTtlMs = 2 * 60 * 60 * 1000;
        const currentPage = analyticsSourcePage();
        const readPageAttribution = (key) => {
            try {
                const saved = JSON.parse(window.sessionStorage.getItem(key) || "null");
                if (!saved?.path || !saved?.savedAt || Date.now() - Number(saved.savedAt) > attributionTtlMs) return "";
                return String(saved.path).slice(0, 240);
            } catch (_error) {
                return "";
            }
        };
        const savePageAttribution = (key, path) => {
            try {
                window.sessionStorage.setItem(key, JSON.stringify({ savedAt: Date.now(), path: String(path || "").slice(0, 240) }));
            } catch (_error) {
                // Attribution must never block the records workflow.
            }
        };
        let entryPage = readPageAttribution(entryPageKey);
        if (!entryPage) {
            entryPage = currentPage;
            savePageAttribution(entryPageKey, entryPage);
        }
        const getSourcePage = () => readPageAttribution(sourcePageKey) || currentPage;
        const attributionParams = () => ({
            entry_page: entryPage,
            source_page: getSourcePage()
        });
        const getSourceContext = () => {
            try {
                return window.sessionStorage.getItem("septicpath_record_help_source") || "direct";
            } catch (_error) {
                return "direct";
            }
        };
        const saveRecordHelpContext = () => {
            const task = window.SepticRecordTask?.read?.();
            if (!task?.property?.address && !task?.context?.stateCode && !task?.context?.countyName) return;
            try {
                window.sessionStorage.setItem(contextKey, JSON.stringify({
                    savedAt: Date.now(),
                    address: String(task.property?.address || "").slice(0, 180),
                    stateCode: String(task.context?.stateCode || "").slice(0, 2).toUpperCase(),
                    countyName: String(task.context?.countyName || "").slice(0, 120),
                    purpose: String(task.context?.purpose || "").slice(0, 40),
                    status: String(task.status || "").slice(0, 40)
                }));
            } catch (_error) {
                // Context carryover is optional; the help form remains usable without browser storage.
            }
        };
        const readRecordHelpContext = () => {
            try {
                const context = JSON.parse(window.sessionStorage.getItem(contextKey) || "null");
                if (!context?.savedAt || Date.now() - Number(context.savedAt) > 2 * 60 * 60 * 1000) return null;
                return context;
            } catch (_error) {
                return null;
            }
        };
        const viewedSources = new Set();
        const ctas = document.querySelectorAll("[data-record-help-cta]");

        document.addEventListener("click", (event) => {
            if (!(event.target instanceof Element)) return;
            const cta = event.target.closest("[data-record-help-cta], [data-record-help-link]");
            if (!(cta instanceof HTMLAnchorElement)) return;
            const sourceContext = cta.dataset.trackSourceContext || "unknown";
            try {
                window.sessionStorage.setItem("septicpath_record_help_source", sourceContext);
            } catch (_error) {
                // Attribution is helpful, but navigation must never depend on storage.
            }
            savePageAttribution(sourcePageKey, currentPage);
            saveRecordHelpContext();
            const sourceInput = document.querySelector("[data-record-help-source-context]");
            if (sourceInput instanceof HTMLInputElement) {
                sourceInput.value = sourceContext;
            }
            emitGaEvent("record_help_cta_clicked", {
                source_context: sourceContext,
                request_type: "record_help_beta",
                cta_variant: "task_adjacent_v1",
                ...attributionParams()
            });
        });

        if ("IntersectionObserver" in window) {
            const ctaObserver = new IntersectionObserver((entries) => {
                entries.forEach((entry) => {
                    if (!entry.isIntersecting) return;
                    const sourceContext = entry.target.dataset.trackSourceContext || "unknown";
                    if (!viewedSources.has(sourceContext)) {
                        viewedSources.add(sourceContext);
                        emitGaEvent("record_help_cta_viewed", {
                            source_context: sourceContext,
                            request_type: "record_help_beta",
                            cta_variant: "task_adjacent_v1",
                            entry_page: entryPage,
                            source_page: currentPage
                        });
                    }
                    ctaObserver.unobserve(entry.target);
                });
            }, { threshold: 0.5 });
            ctas.forEach((cta) => ctaObserver.observe(cta));
            // Outcome actions are created after an official-site return.
            const addedCtas = new MutationObserver((records) => {
                records.forEach((record) => record.addedNodes.forEach((node) => {
                    if (!(node instanceof Element)) return;
                    if (node.matches("[data-record-help-cta]")) ctaObserver.observe(node);
                    node.querySelectorAll("[data-record-help-cta]").forEach((cta) => ctaObserver.observe(cta));
                }));
            });
            addedCtas.observe(document.body, { childList: true, subtree: true });
        }

        const form = document.querySelector("[data-record-help-request-form]");
        if (!(form instanceof HTMLFormElement)) return;

        const sourceInput = form.querySelector("[data-record-help-source-context]");
        if (sourceInput instanceof HTMLInputElement) {
            sourceInput.value = getSourceContext();
        }
        const sourcePageInput = form.querySelector("[data-record-help-source-page]");
        if (sourcePageInput instanceof HTMLInputElement && !sourcePageInput.value) {
            sourcePageInput.value = getSourcePage();
        }
        const entryPageInput = form.querySelector("[data-record-help-entry-page]");
        if (entryPageInput instanceof HTMLInputElement && !entryPageInput.value) {
            entryPageInput.value = entryPage;
        }
        const success = document.querySelector("[data-closing-risk-request-success]");
        if (success instanceof HTMLElement) {
            success.dataset.gaParamEntryPage = entryPage;
            success.dataset.gaParamSourcePage = getSourcePage();
        }
        const carriedContext = readRecordHelpContext();
        if (carriedContext) {
            const address = form.querySelector('[name="propertyAddress"]');
            const state = form.querySelector('[name="stateCode"]');
            const county = form.querySelector('[name="countyName"]');
            const recordType = form.querySelector('[name="recordType"]');
            const researchGoal = form.querySelector('[name="researchGoal"]');
            const recordStatus = form.querySelector('[name="recordStatus"]');
            if (address instanceof HTMLInputElement && !address.value) address.value = carriedContext.address || "";
            if (state instanceof HTMLSelectElement
                && !state.value
                && Array.from(state.options).some(option => option.value === carriedContext.stateCode)) {
                state.value = carriedContext.stateCode;
            }
            if (county instanceof HTMLInputElement && !county.value) county.value = carriedContext.countyName || "";
            if (recordType instanceof HTMLSelectElement && (!recordType.value || recordType.value === "not_sure")) {
                recordType.value = "septic";
            }
            const purposeGoalMap = {
                buying: "original_documents",
                bedrooms: "design_capacity",
                location: "system_layout",
                repair: "repair_history",
                replacement: "original_documents",
                lender: "approval_status",
                owner: "original_documents"
            };
            const carriedGoal = purposeGoalMap[carriedContext.purpose] || "";
            if (researchGoal instanceof HTMLSelectElement
                && (!researchGoal.value || researchGoal.value === "other")
                && Array.from(researchGoal.options).some(option => option.value === carriedGoal)) {
                researchGoal.value = carriedGoal;
            }
            const statusMap = {
                route_ready: "not_started",
                not_found_online: "missing",
                blocked: "route_unknown",
                wrong_agency: "route_unknown",
                no_record_response: "missing"
            };
            const carriedStatus = statusMap[carriedContext.status] || "";
            if (recordStatus instanceof HTMLSelectElement
                && (!recordStatus.value || recordStatus.value === "not_started")
                && Array.from(recordStatus.options).some(option => option.value === carriedStatus)) {
                recordStatus.value = carriedStatus;
            }
        }
        const sourcePath = sourcePageInput instanceof HTMLInputElement ? sourcePageInput.value : "";
        const countyRoute = sourcePath.match(/^\/septic-records-checklist\/([^/]+)\/([^/]+)-county\/?/i);
        if (countyRoute) {
            const state = form.querySelector('[name="stateCode"]');
            const county = form.querySelector('[name="countyName"]');
            const normalizeSlug = (value) => String(value || "")
                .toLowerCase()
                .replace(/&/g, "and")
                .replace(/[^a-z0-9]+/g, "-")
                .replace(/^-|-$/g, "");
            if (state instanceof HTMLSelectElement && !state.value) {
                const stateOption = Array.from(state.options)
                    .find((option) => normalizeSlug(option.textContent) === countyRoute[1].toLowerCase());
                if (stateOption) state.value = stateOption.value;
            }
            if (county instanceof HTMLInputElement && !county.value) {
                county.value = countyRoute[2]
                    .split("-")
                    .filter(Boolean)
                    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
                    .join(" ") + " County";
            }
        }

        let formViewed = false;
        if ("IntersectionObserver" in window) {
            const formObserver = new IntersectionObserver((entries) => {
                if (formViewed || !entries.some((entry) => entry.isIntersecting)) return;
                formViewed = true;
                emitGaEvent("record_help_form_viewed", {
                    source_context: getSourceContext(),
                    request_type: "record_help_beta",
                    cta_variant: "task_adjacent_v1",
                    ...attributionParams()
                });
                formObserver.disconnect();
            }, { threshold: 0.25 });
            formObserver.observe(form);
        }

        let formStarted = false;
        form.addEventListener("input", (event) => {
            if (formStarted || !(event.target instanceof HTMLElement) || event.target.getAttribute("name") === "website") return;
            formStarted = true;
            emitGaEvent("record_help_form_started", {
                source_context: getSourceContext(),
                request_type: "record_help_beta",
                cta_variant: "task_adjacent_v1",
                ...attributionParams()
            });
        });

        const stage = form.querySelector("[data-record-help-stage]");
        const goal = form.querySelector("[data-record-help-goal]");
        const transactionDetails = form.querySelector("[data-record-help-transaction-details]");
        const documentDetails = form.querySelector("[data-record-help-documents]");
        const documentInput = form.querySelector("[data-record-help-document-input]");
        const questionLabel = form.querySelector("[data-record-help-question-label]");
        const questionInput = form.querySelector("[data-record-help-question]");
        const questionHelp = form.querySelector("[data-record-help-question-help]");
        const submitButton = form.querySelector("[data-record-help-submit]");

        const syncDocumentReviewMode = () => {
            if (!(goal instanceof HTMLSelectElement)) return;
            const reviewingDocument = goal.value === "understand_file";
            if (documentDetails instanceof HTMLElement) {
                documentDetails.hidden = !reviewingDocument;
                documentDetails.dataset.reviewActive = String(reviewingDocument);
            }
            if (documentInput instanceof HTMLInputElement) documentInput.required = reviewingDocument;
            if (questionLabel instanceof HTMLElement) {
                questionLabel.textContent = reviewingDocument
                    ? "What questions do you want answered?"
                    : "What do you need to decide, or what is missing?";
            }
            if (questionInput instanceof HTMLTextAreaElement) {
                questionInput.placeholder = reviewingDocument
                    ? "Example: Does this approve four bedrooms, and does the sketch show the installed drainfield or only the proposed design?"
                    : "Example: I’m buying this property and need the approved bedroom count before September 25. I found a parcel page, but no permit or layout.";
            }
            if (questionHelp instanceof HTMLElement) {
                questionHelp.textContent = reviewingDocument
                    ? "Ask the exact questions affecting your purchase, sale, repair, addition, inspection, or records decision."
                    : "One or two sentences is enough. Include any deadline or conflicting information.";
            }
            if (submitButton instanceof HTMLButtonElement) {
                submitButton.textContent = reviewingDocument
                    ? "Send my file for human review"
                    : "Ask SepticPath to investigate";
            }
        };

        if (documentInput instanceof HTMLInputElement) {
            documentInput.addEventListener("change", () => {
                const files = Array.from(documentInput.files || []);
                const totalBytes = files.reduce((sum, file) => sum + file.size, 0);
                const oversized = files.some(file => file.size > 10 * 1024 * 1024);
                let message = "";
                if (files.length > 3) message = "Add no more than three files.";
                else if (oversized) message = "Each file must be 10 MB or smaller.";
                else if (totalBytes > 15 * 1024 * 1024) message = "The combined file size must be 15 MB or smaller.";
                documentInput.setCustomValidity(message);
                if (message) documentInput.reportValidity();
            });
        }
        syncDocumentReviewMode();
        goal?.addEventListener("change", syncDocumentReviewMode);

        if (stage instanceof HTMLSelectElement
            && goal instanceof HTMLSelectElement
            && transactionDetails instanceof HTMLDetailsElement) {
            const syncTransactionDetails = (trackSelection) => {
                const hasTransaction = stage.value !== "" && stage.value !== "researching";
                const needsDocumentContext = goal.value === "design_capacity"
                    || goal.value === "understand_file"
                    || goal.value === "approval_status";
                transactionDetails.open = hasTransaction || needsDocumentContext;
                if (trackSelection && stage.value !== "") {
                    emitGaEvent("record_help_stage_selected", {
                        source_context: getSourceContext(),
                        request_type: "record_help_beta",
                        process_stage: stage.value,
                        transaction_intent: hasTransaction ? "active" : "research",
                        ...attributionParams()
                    });
                }
            };
            syncTransactionDetails(false);
            stage.addEventListener("change", () => syncTransactionDetails(true));
            goal.addEventListener("change", () => syncTransactionDetails(false));
        }

        let validationQueued = false;
        form.addEventListener("invalid", () => {
            if (validationQueued) return;
            validationQueued = true;
            window.queueMicrotask(() => {
                emitGaEvent("record_help_form_validation_error", {
                    source_context: getSourceContext(),
                    request_type: "record_help_beta",
                    invalid_count: form.querySelectorAll(":invalid").length,
                    ...attributionParams()
                });
                validationQueued = false;
            });
        }, true);
    }

    function trackGaEvents() {
        document.querySelectorAll("[data-ga-event]").forEach((element) => {
            const eventName = element.getAttribute("data-ga-event");
            const trackOnceKey = element.getAttribute("data-ga-track-once");
            if (!trackOnceKey) {
                emitGaEvent(eventName, buildGaParams(element));
                return;
            }
            try {
                const storageKey = `septicpath_ga:${trackOnceKey}`;
                if (window.sessionStorage.getItem(storageKey) !== "1") {
                    emitGaEvent(eventName, buildGaParams(element));
                    window.sessionStorage.setItem(storageKey, "1");
                }
            } catch (_error) {
                emitGaEvent(eventName, buildGaParams(element));
            }
        });
    }

    function setupPremiumMotion() {
        if (!("IntersectionObserver" in window)
            || window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
            return;
        }

        const sections = document.querySelectorAll([
            ".national-records-page > section:not(.national-records-hero)",
            ".national-records-page > .record-evidence-band",
            ".state-records-page > section:not(.state-records-hero)",
            "main > .records-access-index-hero ~ section",
            ".calculator-intro ~ section"
        ].join(","));
        if (!sections.length) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (!entry.isIntersecting) return;
                entry.target.classList.add("is-inview");
                observer.unobserve(entry.target);
            });
        }, { rootMargin: "0px 0px -10%", threshold: 0.08 });

        sections.forEach((section) => {
            if (section.querySelector("[data-address-record-finder], [data-county-finder], form")) {
                return;
            }
            section.classList.add("premium-scroll-reveal");
            observer.observe(section);
        });
    }

    setupHashAnchorOffset();
    setupSiteNav();
    setupWebVitalTracking();
    setupStickyMobileCtas();
    setupPrimaryFunnelEvents();
    setupRecordHelpFunnel();
    setupPremiumMotion();
    trackGaEvents();

    document.addEventListener("click", (event) => {
        if (!(event.target instanceof Element)) {
            return;
        }
        const anchor = event.target.closest("a[data-track-click]");
        if (!(anchor instanceof HTMLAnchorElement)) {
            return;
        }
        const targetPath = navigationTarget(anchor);
        if (!targetPath || (targetPath.startsWith("/") && targetPath.startsWith("/events/"))) {
            return;
        }
        sendEvent("/events/nav-click", {
            sourcePage: analyticsSourcePage(),
            sourceContext: anchor.dataset.trackSourceContext || "",
            targetPath,
            targetType: anchor.dataset.trackTargetType || "",
            targetLabel: (anchor.dataset.trackLabel || anchor.textContent || "").trim().replace(/\s+/g, " ")
        });
    });
})();
