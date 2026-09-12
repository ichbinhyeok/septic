(() => {
    const captions = { plan: "County original · approved site plan", approval: "County original · approval letter", findings: "SepticPath Record Brief · findings", guidance: "SepticPath Record Brief · guidance and limitations" };
    document.querySelectorAll("[data-record-evidence]").forEach((section) => {
        if (section.dataset.evidenceReady) return;
        section.dataset.evidenceReady = "true";
        const emit = (name, extra = {}) => {
            if (typeof window.gtag === "function") window.gtag("event", name, {
                source_context: section.dataset.evidenceContext,
                case_id: "shelby_tn_completed", ...extra
            });
        };
        if ("IntersectionObserver" in window) {
            const observer = new IntersectionObserver((entries) => {
                if (entries.some((entry) => entry.isIntersecting)) {
                    emit("record_evidence_viewed");
                    observer.disconnect();
                }
            }, { threshold: 0.3 });
            observer.observe(section.querySelector(".record-evidence__documents"));
        }
        const dialog = section.querySelector("dialog");
        let opener;
        section.querySelectorAll("[data-evidence-document]").forEach((link) => {
            link.addEventListener("click", (event) => {
                if (event.ctrlKey || event.metaKey || event.shiftKey || event.altKey || typeof dialog.showModal !== "function") return;
                event.preventDefault();
                opener = link;
                const image = dialog.querySelector("[data-evidence-image]");
                image.src = link.href;
                image.alt = captions[link.dataset.evidenceDocument];
                dialog.querySelector("[data-evidence-caption]").textContent = image.alt;
                dialog.showModal();
                emit("record_evidence_opened", { document_type: link.dataset.evidenceDocument });
            });
        });
        dialog.querySelector("[data-evidence-close]").addEventListener("click", () => dialog.close());
        dialog.addEventListener("click", (event) => { if (event.target === dialog && (event.clientX < dialog.getBoundingClientRect().left || event.clientX > dialog.getBoundingClientRect().right || event.clientY < dialog.getBoundingClientRect().top || event.clientY > dialog.getBoundingClientRect().bottom)) dialog.close(); });
        dialog.addEventListener("close", () => opener?.focus({ preventScroll: true }));
    });
})();
