(() => {
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
