(function (root) {
    "use strict";
    const marker = "shared-route";
    const storageKey = "septicpath_public_route_share_v1";
    const lifetime = 30 * 60 * 1000;
    const shareSourcePaths = new Set([
        "/", "/septic-record-finder/", "/septic-permit-lookup/", "/septic-permit-search-by-address/",
        "/septic-records-by-county/", "/septic-as-built-records/", "/septic-tank-location-records/",
        "/how-to-find-septic-records-online/", "/tdec-septic-records/", "/dhec-septic-records/"
    ]);
    const states = new Set("AL AK AZ AR CA CO CT DE FL GA HI ID IL IN IA KS KY LA ME MD MA MI MN MS MO MT NE NV NH NJ NM NY NC ND OH OK OR PA RI SC SD TN TX UT VT VA WA WV WI WY DC".split(" "));
    const filters = {
        method: new Set(["online", "request", "office"]),
        artifact: new Set(["permit", "as-built", "inspection", "final", "repair", "no-record"]),
        confidence: new Set(["high", "solid"]),
        parcelOnly: new Set(["true"])
    };

    function publicPath(path) {
        return path === "/septic-records-access-index/"
            || /^\/septic-records-checklist\/[a-z-]+\/(?:[a-z0-9-]+\/)?$/.test(path);
    }

    function buildPublicUrl(raw, origin) {
        try {
            const input = new URL(raw, origin);
            if (input.origin !== origin || !publicPath(input.pathname)) return null;
            const output = new URL(input.pathname, origin);
            if (input.pathname === "/septic-records-access-index/") {
                const state = input.searchParams.get("state");
                if (states.has(state)) output.searchParams.set("state", state);
                Object.entries(filters).forEach(([key, values]) => {
                    const value = input.searchParams.get(key);
                    if (values.has(value)) output.searchParams.set(key, value);
                });
                output.hash = "records-access-county-search";
            }
            output.searchParams.set("src", marker);
            return output.href;
        } catch (_) { return null; }
    }

    function install(win) {
        const doc = win.document;
        let visit = null;
        let storageAvailable = true;
        function readVisit() {
            if (storageAvailable) {
                try { visit = JSON.parse(win.sessionStorage.getItem(storageKey)); }
                catch (_) { storageAvailable = false; }
            }
            if (!visit || !Number.isFinite(visit.expires) || visit.expires <= Date.now()
                || !publicPath(visit.landing)) visit = null;
            return visit;
        }
        function saveVisit() {
            if (!storageAvailable) return;
            try { win.sessionStorage.setItem(storageKey, JSON.stringify(visit)); }
            catch (_) { storageAvailable = false; }
        }
        function emit(action, path) {
            const type = path === "/septic-records-access-index/" ? "public_route_index" : "public_county_guide";
            const sharingAction = ["share_clicked", "share_copied", "share_handoff"].includes(action);
            const currentPath = win.location.pathname;
            const sourcePath = sharingAction && (shareSourcePaths.has(currentPath) || publicPath(currentPath))
                ? currentPath : path;
            const payload = { sourcePage: sourcePath, sourceContext: "route_sharing", action, artifactType: type };
            const body = JSON.stringify(payload);
            try {
                if (!win.navigator.sendBeacon || !win.navigator.sendBeacon("/events/artifact-action", new win.Blob([body], { type: "application/json" }))) {
                    win.fetch("/events/artifact-action", { method: "POST", headers: { "Content-Type": "application/json" }, body, keepalive: true }).catch(() => {});
                }
            } catch (_) { /* Measurement must not block sharing. */ }
            if (typeof win.gtag === "function") {
                let referrer = "";
                try { const ref = new URL(doc.referrer); referrer = ref.origin; } catch (_) { /* No referrer. */ }
                try {
                    win.gtag("event", "route_" + action, {
                        route_path: path, artifact_type: type, measurement_version: "public_share_v2",
                        page_location: win.location.origin + sourcePath, page_referrer: referrer
                    });
                } catch (_) { /* Analytics must not interrupt the visitor's action. */ }
            }
        }
        readVisit();
        const current = new URL(win.location.href);
        if (publicPath(current.pathname) && current.searchParams.get("src") === marker
            && (!visit || visit.landing !== current.pathname)) {
            visit = { landing: current.pathname, expires: Date.now() + lifetime, officialOpened: false };
            saveVisit();
            emit("share_arrival", current.pathname);
        }

        async function copy(text) {
            if (win.navigator.clipboard && win.isSecureContext) {
                try { await win.navigator.clipboard.writeText(text); return; } catch (_) { /* Try legacy copy. */ }
            }
            const field = doc.createElement("textarea");
            field.value = text;
            field.setAttribute("readonly", "");
            field.style.position = "fixed";
            field.style.top = "-1000px";
            doc.body.appendChild(field);
            field.select();
            try { if (!doc.execCommand("copy")) throw new Error("Copy unavailable"); }
            finally { field.remove(); }
        }
        async function share(button) {
            if (button.dataset.shareBusy === "true") return;
            const url = buildPublicUrl(button.dataset.shareUrl || win.location.href, win.location.origin);
            if (!url) return;
            const path = new URL(url).pathname;
            const title = button.dataset.shareTitle || "SepticPath county records guide";
            const label = button.textContent;
            button.dataset.shareBusy = "true";
            emit("share_clicked", path);
            try {
                if (win.navigator.share && win.matchMedia("(max-width: 720px)").matches) {
                    try {
                        await win.navigator.share({ title, url });
                        button.textContent = "Shared";
                        emit("share_handoff", path);
                        return;
                    } catch (error) { if (error?.name === "AbortError") return; }
                }
                await copy(url);
                button.textContent = "Link copied";
                emit("share_copied", path);
            } catch (_) {
                button.textContent = "Copy unavailable";
            } finally {
                button.dataset.shareBusy = "false";
                win.setTimeout(() => { button.textContent = label; }, 1800);
            }
        }
        doc.addEventListener("click", (event) => {
            const target = event.target;
            if (!target || typeof target.closest !== "function") return;
            const button = target.closest("[data-share-route]");
            if (button && !button.disabled) { void share(button); return; }
            const link = target.closest("a[data-record-official-link], a[data-county-access-official]");
            if (!link || link.getAttribute("data-county-access-official") === "false") return;
            let destination;
            try { destination = new URL(link.href); } catch (_) { return; }
            if (destination.protocol !== "https:" || destination.origin === win.location.origin) return;
            readVisit();
            if (!visit || visit.officialOpened) return;
            visit.officialOpened = true;
            saveVisit();
            emit("share_official_open", visit.landing);
        });
        return { share };
    }
    const api = { buildPublicUrl, install };
    if (typeof module !== "undefined" && module.exports) module.exports = api;
    if (root?.document) {
        root.SepticPathRouteSharing = api;
        install(root);
    }
})(typeof window !== "undefined" ? window : null);
