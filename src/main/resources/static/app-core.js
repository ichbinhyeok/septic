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
            if (targetType === "quote_form" || anchor.getAttribute("href") === "#quote-request") {
                emitGaEvent("lead_cta_clicked", { source_context: anchor.dataset.trackSourceContext || "", cta_type: targetType || "quote_form" });
            }
        });
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

    setupHashAnchorOffset();
    setupSiteNav();
    setupWebVitalTracking();
    setupStickyMobileCtas();
    trackGaEvents();
    setupPrimaryFunnelEvents();

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
