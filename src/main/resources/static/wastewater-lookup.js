(() => {
    const tool = document.querySelector("[data-wastewater-lookup]");
    if (!tool) return;

    const form = tool.querySelector("[data-wastewater-form]");
    const addressInput = tool.querySelector("#wastewater-address");
    const cityInput = tool.querySelector("#wastewater-city");
    const heading = tool.querySelector("#wastewater-sources");
    const selection = tool.querySelector("[data-wastewater-selection]");
    const selectedAddress = tool.querySelector("[data-wastewater-address]");
    const copyStatus = tool.querySelector("[data-wastewater-copy-status]");
    const cards = tool.querySelectorAll("[data-wastewater-city]");
    const result = tool.querySelector("[data-wastewater-result]");
    const evidence = tool.querySelector("#wastewater-evidence");
    const conclusion = tool.querySelector("[data-wastewater-conclusion]");
    const guidance = {
        "": "Check both the city connection records and the health department archive for the same property.",
        connected: "The utility confirmation supports an active sanitary connection for this building. Keep the dated confirmation with the property file.",
        lateral: "You found evidence of a recorded sanitary connection. Confirm active service with the utility if you need the current status.",
        available: "You found evidence of service availability. Ask the utility for active service or a completed connection record for this building.",
        historic: "You found a historical septic record. Check for a later sanitary connection or septic abandonment record before deciding what serves the building today.",
        conflict: "Compare the dates and the exact property identifiers. Ask the utility to confirm current service and check for a dated septic abandonment record.",
        empty: "The wastewater service is unresolved. Ask the utility for the building’s connection status and the health department for its historical septic file."
    };

    form.hidden = false;
    form.addEventListener("input", () => {
        addressInput.setCustomValidity("");
        selection.hidden = true;
        result.hidden = true;
        cards.forEach((card) => { card.hidden = card.dataset.wastewaterCity === "other"; });
    });

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        const address = addressInput.value.trim();
        if (!address) {
            addressInput.setCustomValidity("Enter the building’s street address.");
            addressInput.reportValidity();
            return;
        }
        cards.forEach((card) => { card.hidden = card.dataset.wastewaterCity !== cityInput.value; });
        selectedAddress.textContent = address;
        copyStatus.textContent = "";
        evidence.value = "";
        conclusion.textContent = guidance[""];
        selection.hidden = false;
        result.hidden = cityInput.value === "other";
        heading.focus();
    });

    evidence.addEventListener("change", () => { conclusion.textContent = guidance[evidence.value]; });
    tool.querySelector("[data-wastewater-copy]").addEventListener("click", async () => {
        try {
            await navigator.clipboard.writeText(selectedAddress.textContent);
            copyStatus.textContent = "Address copied.";
        } catch {
            copyStatus.textContent = "Select the address above and copy it manually.";
        }
    });
})();
