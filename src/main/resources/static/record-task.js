(() => {
    "use strict";

    const STORAGE_KEY = "septicpath-record-task-v2";
    const LEGACY_TASK_KEY = "septicpath-record-task-progress-v1";
    const LEGACY_RETURN_KEY = "septicpath-official-return-v1";
    const LEGACY_TDEC_KEY = "septicpath:tdec-route:v2";
    const VERSION = 2;
    const LIFETIME = 30 * 24 * 60 * 60 * 1000;
    const STATES = new Set([
        "route_ready", "official_opened", "not_found_online", "request_prepared",
        "request_pending", "artifact_acquired", "no_record_response", "wrong_agency",
        "blocked", "document_reviewed", "decision_ready"
    ]);

    const safe = (value, limit = 180) => String(value || "").trim().replace(/\s+/g, " ").slice(0, limit);
    const identifier = () => typeof window.crypto?.randomUUID === "function"
        ? window.crypto.randomUUID()
        : `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`;

    function readJson(storage, key) {
        try {
            return JSON.parse(storage.getItem(key) || "null");
        } catch (_) {
            return null;
        }
    }

    function normalizeContext(value = {}) {
        return {
            stateCode: safe(value.stateCode, 2).toUpperCase(),
            stateName: safe(value.stateName, 60),
            countyKey: safe(value.countyKey, 80),
            countyName: safe(value.countyName, 100),
            purpose: safe(value.purpose || "buying", 40),
            officeLabel: safe(value.officeLabel, 140),
            routePath: safe(value.routePath || window.location.pathname, 220),
            routeMode: safe(value.routeMode, 40),
            routeReliability: safe(value.routeReliability, 40),
            officialRoute: safe(value.officialRoute, 500),
            requestRoute: safe(value.requestRoute, 500),
            requiredIdentifiers: Array.isArray(value.requiredIdentifiers)
                ? value.requiredIdentifiers.map(item => safe(item, 80)).filter(Boolean).slice(0, 8)
                : [],
            requestedDocuments: Array.isArray(value.requestedDocuments)
                ? value.requestedDocuments.map(item => safe(item, 100)).filter(Boolean).slice(0, 12)
                : []
        };
    }

    function normalizeProperty(value = {}) {
        return {
            address: safe(value.address, 180),
            identifierType: safe(value.identifierType, 32),
            identifierValue: safe(value.identifierValue, 180),
            alternateIdentifiers: Array.isArray(value.alternateIdentifiers)
                ? value.alternateIdentifiers.map(item => safe(item, 180)).filter(Boolean).slice(0, 8)
                : []
        };
    }

    function normalizeTask(value) {
        if (!value || typeof value !== "object") return null;
        const expiresAt = Number(value.expiresAt || 0);
        if (!expiresAt || Date.now() > expiresAt) return null;
        return {
            version: VERSION,
            workflowRunId: safe(value.workflowRunId || value.context?.workflowRunId, 64) || identifier(),
            status: STATES.has(value.status) ? value.status : "route_ready",
            outcome: safe(value.outcome, 40),
            context: normalizeContext(value.context),
            property: normalizeProperty(value.property),
            evidence: Array.isArray(value.evidence) ? value.evidence.slice(0, 20) : [],
            nextAction: safe(value.nextAction, 240),
            savedAt: Number(value.savedAt || Date.now()),
            expiresAt
        };
    }

    function isCurrentRoute(task) {
        const currentState = safe(document.querySelector("[data-state-records-return]")?.dataset.stateCode, 2).toUpperCase();
        const routeMatches = !task?.context?.routePath || task.context.routePath === window.location.pathname;
        const stateMatches = !currentState || !task?.context?.stateCode || task.context.stateCode === currentState;
        return routeMatches && stateMatches;
    }

    function migrateLegacy() {
        const legacy = readJson(localStorage, LEGACY_TASK_KEY) || readJson(sessionStorage, LEGACY_RETURN_KEY);
        if (legacy?.context) {
            const task = normalizeTask({
                workflowRunId: legacy.context.workflowRunId,
                status: legacy.stage === "official_opened" ? "official_opened" : "route_ready",
                outcome: legacy.outcome,
                context: legacy.context,
                property: {address: legacy.context.matchedAddress},
                evidence: [],
                savedAt: legacy.savedAt,
                expiresAt: legacy.expiresAt || Date.now() + LIFETIME
            });
            if (task && isCurrentRoute(task)) return task;
        }
        if (window.location.pathname === "/tdec-septic-records/") {
            const tdec = readJson(sessionStorage, LEGACY_TDEC_KEY);
            if (tdec && Date.now() - Number(tdec.savedAt || 0) < LIFETIME) {
                return normalizeTask({
                    status: tdec.opened ? "official_opened" : "route_ready",
                    context: {
                        stateCode: "TN",
                        stateName: "Tennessee",
                        countyKey: tdec.county?.key ? `TN::${safe(tdec.county.key, 70)}` : "",
                        countyName: tdec.county?.name,
                        purpose: tdec.purpose,
                        officeLabel: tdec.county?.fieldOfficeName,
                        routePath: window.location.pathname
                    },
                    property: {address: tdec.address, identifierType: "address", identifierValue: tdec.address},
                    savedAt: tdec.savedAt,
                    expiresAt: Date.now() + LIFETIME
                });
            }
        }
        return null;
    }

    function read() {
        let task = normalizeTask(readJson(localStorage, STORAGE_KEY));
        if (task && !isCurrentRoute(task)) task = null;
        if (!task) {
            task = migrateLegacy();
            if (task) write(task);
        }
        return task;
    }

    function write(value) {
        const task = normalizeTask({...value, savedAt: Date.now(), expiresAt: Date.now() + LIFETIME});
        if (!task) return null;
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(task));
        } catch (_) {
            // Visible workflows still work when local storage is disabled.
        }
        window.dispatchEvent(new CustomEvent("septic-record-task-changed", {detail: {status: task.status, outcome: task.outcome}}));
        return task;
    }

    function prepare(input = {}, propertyInput = null) {
        const current = read();
        const suppliedContext = normalizeContext(input.context || input);
        const suppliedProperty = normalizeProperty(propertyInput || input.property || {});
        const meaningful = value => Object.fromEntries(Object.entries(value).filter(([, item]) =>
            Array.isArray(item) ? item.length > 0 : Boolean(item)));
        return write({
            ...(current || {}),
            workflowRunId: safe(input.workflowRunId, 64) || current?.workflowRunId || identifier(),
            status: "route_ready",
            outcome: "",
            context: {...(current?.context || {}), ...meaningful(suppliedContext)},
            property: {...(current?.property || {}), ...meaningful(suppliedProperty)},
            evidence: current?.evidence || [],
            nextAction: input.nextAction || current?.nextAction || ""
        });
    }

    function emitStage(task, stage, outcome = "") {
        if (!task?.workflowRunId || !stage) return;
        const payload = {
            sourcePage: window.location.pathname,
            sourceContext: "record_task_v2",
            workflowRunId: task.workflowRunId,
            countyKey: task.context.countyKey || "",
            stage,
            outcome: safe(outcome, 40)
        };
        const body = JSON.stringify(payload);
        if (navigator.sendBeacon) {
            navigator.sendBeacon("/events/workflow-stage", new Blob([body], {type: "application/json"}));
        } else {
            fetch("/events/workflow-stage", {method: "POST", headers: {"Content-Type": "application/json"}, body, keepalive: true}).catch(() => {});
        }
    }

    function transition(status, outcome = "", changes = {}) {
        if (!STATES.has(status)) throw new Error(`Unsupported record task status: ${status}`);
        const current = read() || prepare(changes);
        const task = write({...current, ...changes, status, outcome: safe(outcome, 40)});
        const stage = {
            route_ready: "preparation_ready",
            official_opened: "official_route_opened",
            request_prepared: "request_prepared",
            request_pending: "request_evidence_added",
            artifact_acquired: "artifact_acquired",
            document_reviewed: "document_reviewed",
            decision_ready: "decision_ready"
        }[status] || "outcome_recorded";
        emitStage(task, stage, outcome && outcome !== status ? outcome : "");
        return task;
    }

    function addRequestEvidence({date, channel, reference = ""}) {
        const current = read();
        if (!current || !/^\d{4}-\d{2}-\d{2}$/.test(String(date || "")) || !safe(channel, 32)) return null;
        const evidence = [...current.evidence, {
            kind: "request_submission",
            date: safe(date, 10),
            channel: safe(channel, 32),
            reference: safe(reference, 240),
            addedAt: new Date().toISOString()
        }];
        return transition("request_pending", "request_submitted", {evidence});
    }

    function addArtifactEvidence(kind = "official_file") {
        const current = read() || prepare();
        const evidence = [...current.evidence, {kind: safe(kind, 40), addedAt: new Date().toISOString()}];
        return transition("artifact_acquired", "artifact", {evidence});
    }

    function clear() {
        try {
            [STORAGE_KEY, LEGACY_TASK_KEY].forEach(key => localStorage.removeItem(key));
            [LEGACY_RETURN_KEY, LEGACY_TDEC_KEY].forEach(key => sessionStorage.removeItem(key));
        } catch (_) {
            // The visible state is still cleared by each caller.
        }
        window.dispatchEvent(new CustomEvent("septic-record-task-changed", {detail: {status: "cleared"}}));
    }

    window.SepticRecordTask = Object.freeze({
        version: VERSION,
        states: Object.freeze([...STATES]),
        read,
        prepare,
        transition,
        addRequestEvidence,
        addArtifactEvidence,
        clear
    });
})();
