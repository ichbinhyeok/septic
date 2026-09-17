(() => {
    "use strict";
    const publicIds = new Set([
        "septic-as-built-records", "septic-tank-location-records", "how-to-find-septic-records-online",
        "septic-permit-search-by-address", "septic-permit-process", "septic-transfer-compliance",
        "buying-a-house-with-a-septic-system", "septic-permit-records-request", "record-brief-example",
        "preview-example", "drawing-comparison", "location-sketch", "no-result-examples",
        "permit-status-example", "buyer-two-tanks", "preview-comparison", "historical-sketch",
        "layout-case", "sale-case", "sale-case-limits", "sale-plan",
        "addition-file-case", "old-address-case", "design-final-case", "real-cases", "record-finding-comparison",
        "record-finding-roane", "record-finding-shelby", "record-finding-stcroix", "record-finding-overton"
    ]);
    const seen = new Set();
    function track(action, id) {
        if (!publicIds.has(id) || !["read", "enlarge"].includes(action)) return;
        const key = `${action}:${id}`;
        if (seen.has(key)) return;
        seen.add(key);
        // Only editorial IDs and the pathname; never property details or the page query string.
        const payload = {
            sourcePage: window.location.pathname,
            sourceContext: "record_reading",
            action,
            artifactType: id.replace(/-/g, "_")
        };
        const body = JSON.stringify(payload);
        if (!navigator.sendBeacon || !navigator.sendBeacon("/events/artifact-action", new Blob([body], {type: "application/json"}))) {
            fetch("/events/artifact-action", {method: "POST", headers: {"Content-Type": "application/json"}, body, keepalive: true}).catch(() => {});
        }
        if (typeof window.gtag === "function") {
            window.gtag("event", "record_content_engagement", {content_id: id, action, page_path: window.location.pathname});
        }
    }
    if ("IntersectionObserver" in window) {
        const targets = new Map();
        document.querySelectorAll("[data-reading-guide], [data-reading-milestone]").forEach(element => {
            const heading = element.querySelector("h2, h3, strong") || element;
            targets.set(heading, element.dataset.readingMilestone || element.dataset.readingGuide);
        });
        const observer = new IntersectionObserver(entries => {
            entries.forEach(entry => {
                if (!entry.isIntersecting || entry.intersectionRatio < .5) return;
                track("read", targets.get(entry.target));
                observer.unobserve(entry.target);
            });
        }, {threshold: .5});
        targets.forEach((_id, heading) => observer.observe(heading));
    }
    document.querySelectorAll("a[data-reading-image]").forEach(link => {
        link.addEventListener("click", event => {
            if (event.ctrlKey || event.metaKey || event.shiftKey || event.altKey || event.button !== 0) return;
            if (typeof HTMLDialogElement === "undefined") return; // The ordinary image link still works.
            const original = link.querySelector("img");
            if (!original) return;
            event.preventDefault();
            const dialog = document.createElement("dialog");
            dialog.className = "record-image-dialog";
            dialog.setAttribute("aria-label", link.dataset.readingImage === "sale-plan" ? "Approved septic plan enlarged" : "Historical septic drawing enlarged");
            const close = document.createElement("button");
            close.type = "button";
            close.className = "record-image-dialog__close";
            close.textContent = "Close drawing";
            const img = document.createElement("img");
            img.src = original.src;
            img.alt = original.alt;
            const caption = document.createElement("p");
            caption.textContent = "Historical source excerpt. Read the written labels; do not measure your screen. This drawing does not establish current field conditions.";
            close.addEventListener("click", () => dialog.close());
            dialog.addEventListener("click", e => { if (e.target === dialog) dialog.close(); });
            dialog.addEventListener("close", () => { dialog.remove(); link.focus(); }, {once: true});
            dialog.append(close, img, caption);
            document.body.appendChild(dialog);
            dialog.showModal();
            close.focus();
            track("enlarge", link.dataset.readingImage);
        });
    });
})();
