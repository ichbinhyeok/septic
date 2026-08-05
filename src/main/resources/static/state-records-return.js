(() => {
    "use strict";

    const panels = Array.from(document.querySelectorAll("[data-state-records-return]"));
    if (!panels.length) return;

    const TASK_KEY = "septicpath-record-task-progress-v1";
    const RETURN_KEY = "septicpath-official-return-v1";
    const LIFETIME = 30 * 24 * 60 * 60 * 1000;

    const emit = (name, params = {}) => {
        if (typeof window.gtag === "function") window.gtag("event", name, params);
    };

    panels.forEach(panel => {
        const stateCode = panel.dataset.stateCode || "";
        const stateName = panel.dataset.stateName || stateCode;
        const officeLabel = panel.dataset.officeLabel || `${stateName} septic records office`;
        const outcomesId = panel.dataset.outcomesId || "after-search";
        const checkin = panel.querySelector("[data-state-return-checkin]");
        const summary = panel.querySelector("[data-state-return-summary]");
        const next = panel.querySelector("[data-state-return-next]");
        const clear = panel.querySelector("[data-state-return-clear]");
        const outcomeButtons = Array.from(panel.querySelectorAll("[data-state-return-outcome]"));
        let awaitingReturn = false;

        function selectedCounty() {
            const option = Array.from(document.querySelectorAll("select option:checked"))
                .find(item => item.dataset.countyName);
            if (!option) return {name: "", key: ""};
            const name = String(option.dataset.countyName || option.textContent || "").trim();
            const key = String(option.value || name)
                .toLowerCase()
                .replace(/\s+county$/i, "")
                .replace(/[^a-z0-9]+/g, "-")
                .replace(/^-|-$/g, "");
            return {name, key};
        }

        function workflowId() {
            const saved = readTask();
            if (saved?.context?.routePath === window.location.pathname && saved.context.workflowRunId) {
                return String(saved.context.workflowRunId);
            }
            return typeof window.crypto?.randomUUID === "function"
                ? window.crypto.randomUUID()
                : `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`;
        }

        function readTask() {
            try {
                const value = JSON.parse(localStorage.getItem(TASK_KEY) || "null");
                if (!value || typeof value !== "object" || Date.now() > Number(value.expiresAt || 0)) return null;
                return value;
            } catch (_) {
                return null;
            }
        }

        function context() {
            const county = selectedCounty();
            const existing = readTask()?.context;
            return {
                stateCode,
                stateName,
                countyName: county.name || (existing?.routePath === window.location.pathname ? existing.countyName : "") || "",
                countyKey: county.key
                    ? `${stateCode}::${county.key}`
                    : (existing?.routePath === window.location.pathname ? existing.countyKey : "") || "",
                matchedAddress: "",
                routePath: window.location.pathname,
                officeLabel,
                contactLine: `Return to the ${stateName} route you already prepared and record the official result.`,
                routeReviewedAt: "",
                purpose: existing?.purpose || "buying",
                workflowRunId: existing?.routePath === window.location.pathname ? existing.workflowRunId : workflowId(),
                directDocument: false
            };
        }

        function save(stage, outcome = "") {
            const value = {
                savedAt: Date.now(),
                expiresAt: Date.now() + LIFETIME,
                stage,
                outcome,
                context: context()
            };
            try {
                localStorage.setItem(TASK_KEY, JSON.stringify(value));
                sessionStorage.setItem(RETURN_KEY, JSON.stringify(value));
            } catch (_) {
                // The visible return controls still work without browser storage.
            }
            return value;
        }

        function finderUrl(mode, saved) {
            const url = new URL("/septic-record-finder/", window.location.origin);
            url.searchParams.set("mode", mode);
            const countyKey = saved?.context?.countyKey || "";
            if (countyKey) url.searchParams.set("countyKey", countyKey);
            if (saved?.context?.workflowRunId) url.searchParams.set("workflowRunId", saved.context.workflowRunId);
            return `${url.pathname}${url.search}`;
        }

        function link(label, href, primary = false) {
            const item = document.createElement("a");
            item.className = `button ${primary ? "button--primary" : "button--secondary"}`;
            item.href = href;
            item.textContent = label;
            return item;
        }

        function button(label, handler, primary = false) {
            const item = document.createElement("button");
            item.type = "button";
            item.className = `button ${primary ? "button--primary" : "button--secondary"}`;
            item.textContent = label;
            item.addEventListener("click", handler);
            return item;
        }

        function returnUrl(saved) {
            const url = new URL(window.location.pathname, window.location.origin);
            url.searchParams.set("resume", "1");
            if (saved?.context?.workflowRunId) url.searchParams.set("workflowRunId", saved.context.workflowRunId);
            url.hash = "state-records-return";
            return url.toString();
        }

        async function copyReturnLink(saved, status) {
            try {
                await navigator.clipboard.writeText(returnUrl(saved));
                status.textContent = "Private-safe return link copied. It contains no address or parcel number.";
                emit("state_record_return_link_copied", {state_code: stateCode});
            } catch (_) {
                status.textContent = "Copy was blocked. Bookmark this page or use the calendar reminder.";
            }
        }

        function downloadReminder(saved, status) {
            const start = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
            const end = new Date(start.getTime() + 30 * 60 * 1000);
            const stamp = value => value.toISOString().replace(/[-:]/g, "").replace(/\.\d{3}Z$/, "Z");
            const safe = value => String(value || "").replace(/\\/g, "\\\\").replace(/\r?\n/g, "\\n").replace(/[,;]/g, "\\$&");
            const county = saved?.context?.countyName ? ` ${saved.context.countyName}` : "";
            const calendar = [
                "BEGIN:VCALENDAR", "VERSION:2.0", "PRODID:-//SepticPath//State records return//EN", "BEGIN:VEVENT",
                `UID:${safe(saved?.context?.workflowRunId || Date.now())}@septicpath.com`,
                `DTSTAMP:${stamp(new Date())}`, `DTSTART:${stamp(start)}`, `DTEND:${stamp(end)}`,
                `SUMMARY:${safe(`Check${county} septic record response`)}`,
                `DESCRIPTION:${safe("Return to SepticPath and record what the official source sent. No property identifier is stored in this reminder.")}`,
                `URL:${safe(returnUrl(saved))}`, "END:VEVENT", "END:VCALENDAR", ""
            ].join("\r\n");
            const objectUrl = URL.createObjectURL(new Blob([calendar], {type: "text/calendar;charset=utf-8"}));
            const download = document.createElement("a");
            download.href = objectUrl;
            download.download = `septicpath-${stateCode.toLowerCase()}-records-follow-up.ics`;
            document.body.append(download);
            download.click();
            download.remove();
            window.setTimeout(() => URL.revokeObjectURL(objectUrl), 0);
            status.textContent = "Seven-day reminder downloaded. It contains no property identifiers.";
            emit("state_record_followup_scheduled", {state_code: stateCode, followup_days: 7});
        }

        function openFallback(outcome) {
            const target = document.getElementById(outcomesId);
            const detail = target?.querySelector(`[data-state-fallback-outcome~="${outcome}"]`);
            if (detail instanceof HTMLDetailsElement) detail.open = true;
            target?.scrollIntoView({behavior: "smooth", block: "start"});
            window.setTimeout(() => detail?.querySelector("summary")?.focus(), 350);
        }

        function render(outcome, saved) {
            if (!(next instanceof HTMLElement)) return;
            const wrapper = document.createElement("div");
            const heading = document.createElement("strong");
            const copy = document.createElement("p");
            const actions = document.createElement("div");
            const status = document.createElement("span");
            actions.className = "state-record-return__actions";
            status.className = "state-record-return__status";
            status.setAttribute("aria-live", "polite");

            if (outcome === "found") {
                heading.textContent = "Add the official file while it is in front of you.";
                copy.textContent = "The document workspace checks the property match, approvals, layout, capacity, and missing records without claiming to inspect the system.";
                actions.append(link("Add and review the documents", finderUrl("document", saved), true));
            } else if (outcome === "no_record_response") {
                heading.textContent = "Keep the written no-record response as evidence.";
                copy.textContent = "Upload the response or paste its text. A dated office response is different from a blank portal result.";
                actions.append(link("Add the written response", finderUrl("document", saved), true));
            } else if (outcome === "request_submitted") {
                heading.textContent = "The request is pending, not complete.";
                copy.textContent = "Set a private-safe reminder or copy a return link so the official reply can re-enter this property-file workflow.";
                actions.append(button("Add a 7-day reminder", () => downloadReminder(saved, status), true));
                actions.append(button("Copy return link", () => copyReturnLink(saved, status)));
            } else {
                const text = {
                    not_found_online: ["A blank search is not a no-record finding.", "Use this page’s alternate identifiers and official request fallback."],
                    wrong_agency: ["Resolve the responsible office before resending details.", "Use the authority guidance below instead of trying another generic inbox."],
                    blocked: ["Stop retrying the blocked route.", "Use the verified alternate office or request path on this page."]
                }[outcome];
                heading.textContent = text?.[0] || "Continue from the official result.";
                copy.textContent = text?.[1] || "Use the result-specific guidance below.";
                actions.append(button("Open the correct fallback", () => openFallback(outcome), true));
                actions.append(link("Open the saved task workspace", finderUrl("missing", saved)));
            }

            wrapper.append(heading, copy, actions, status);
            next.replaceChildren(wrapper);
            next.hidden = false;
            clear.hidden = false;
        }

        function selectOutcome(outcome) {
            const saved = save("outcome_recorded", outcome);
            outcomeButtons.forEach(item => item.setAttribute("aria-pressed", String(item.dataset.stateReturnOutcome === outcome)));
            render(outcome, saved);
            if (summary) summary.textContent = "Saved result ready";
            emit("state_record_outcome_recorded", {state_code: stateCode, outcome});
        }

        document.addEventListener("click", event => {
            const official = event.target instanceof Element ? event.target.closest('a[data-track-click="official_source"]') : null;
            if (!(official instanceof HTMLAnchorElement) || !/^https?:/i.test(official.href)) return;
            awaitingReturn = true;
            save("official_opened");
            if (summary) summary.textContent = "Official source opened — return here with the result";
            emit("state_record_return_prepared", {state_code: stateCode});
        });

        window.addEventListener("focus", () => {
            if (!awaitingReturn) return;
            awaitingReturn = false;
            if (checkin instanceof HTMLDetailsElement) checkin.open = true;
            if (summary) summary.textContent = "Welcome back — what happened?";
            panel.classList.add("is-returning");
            save("official_returned");
            emit("state_record_official_returned", {state_code: stateCode});
        });

        outcomeButtons.forEach(item => item.addEventListener("click", () => selectOutcome(item.dataset.stateReturnOutcome || "")));
        clear?.addEventListener("click", () => {
            try { localStorage.removeItem(TASK_KEY); sessionStorage.removeItem(RETURN_KEY); } catch (_) {}
            outcomeButtons.forEach(item => item.removeAttribute("aria-pressed"));
            if (next instanceof HTMLElement) { next.hidden = true; next.replaceChildren(); }
            clear.hidden = true;
            if (summary) summary.textContent = "I’m back — record what happened";
        });

        const saved = readTask();
        const requestedResume = new URLSearchParams(window.location.search).get("resume") === "1";
        if (saved?.context?.routePath === window.location.pathname) {
            if (checkin instanceof HTMLDetailsElement && (requestedResume || saved.stage !== "official_opened")) checkin.open = true;
            if (saved.outcome) {
                outcomeButtons.forEach(item => item.setAttribute("aria-pressed", String(item.dataset.stateReturnOutcome === saved.outcome)));
                render(saved.outcome, saved);
            }
            if (summary) summary.textContent = saved.outcome ? "Saved result ready" : "Official search ready to continue";
        }
        if (requestedResume) window.setTimeout(() => panel.scrollIntoView({behavior: "smooth", block: "start"}), 150);
    });
})();
