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
