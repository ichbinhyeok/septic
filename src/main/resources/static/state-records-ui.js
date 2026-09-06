(() => {
    "use strict";

    function reveal(target, focusTarget = target) {
        if (!(target instanceof HTMLElement)) return;
        const root = document.documentElement;
        const previousBehavior = root.style.scrollBehavior;
        root.style.scrollBehavior = "auto";
        target.scrollIntoView({block: "start"});
        if (focusTarget instanceof HTMLElement) focusTarget.focus({preventScroll: true});
        window.requestAnimationFrame(() => {
            root.style.scrollBehavior = previousBehavior;
        });
    }

    function showError(error, message, field) {
        if (error instanceof HTMLElement) {
            error.textContent = message;
            error.hidden = false;
        }
        if (field instanceof HTMLElement) {
            field.setAttribute("aria-invalid", "true");
            field.focus();
        }
    }

    function clearError(error, ...fields) {
        if (error instanceof HTMLElement) {
            error.textContent = "";
            error.hidden = true;
        }
        fields.forEach(field => field instanceof HTMLElement && field.removeAttribute("aria-invalid"));
    }

    function restoreTask(stateCode, routePath, county, clueType, clue) {
        const task = window.SepticRecordTask?.read();
        if (!task || task.context?.stateCode !== stateCode || task.context?.routePath !== routePath) return null;
        const countyKey = String(task.context?.countyKey || "").replace(new RegExp(`^${stateCode}::`, "i"), "");
        const identifierType = String(task.property?.identifierType || "");
        const identifierValue = String(task.property?.identifierValue || task.property?.address || "").trim();
        if (!countyKey || !identifierValue || !(county instanceof HTMLSelectElement)) return null;
        const option = Array.from(county.options).find(item => item.value === countyKey);
        if (!option) return null;
        county.value = countyKey;
        if (clueType instanceof HTMLSelectElement && Array.from(clueType.options).some(item => item.value === identifierType)) {
            clueType.value = identifierType;
        }
        if (clue instanceof HTMLInputElement) clue.value = identifierValue;
        return {identifierType: identifierType || "address", identifierValue};
    }

    window.SepticStateRecordsUi = Object.freeze({reveal, showError, clearError, restoreTask});
})();
