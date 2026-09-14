(() => {
    const captions = { plan: "Agency original · approved site plan", approval: "Agency original · approval letter", permit: "State original · historical septic permit", findings: "SepticPath delivery · Record Brief", guidance: "SepticPath Record Brief · guidance and limitations" };
    document.querySelectorAll("[data-record-evidence]").forEach((section) => {
        if (section.dataset.evidenceReady) return;
        section.dataset.evidenceReady = "true";
        const emit = (name, extra = {}) => {
            if (typeof window.gtag === "function") window.gtag("event", name, {
                source_context: section.dataset.evidenceContext,
                evidence_set: "completed_requests", ...extra
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
        const papers = section.querySelector(".record-evidence__papers");
        const progress = section.querySelector("[data-evidence-progress]");
        if (papers && progress) {
            const documents = Array.from(papers.querySelectorAll("a[data-evidence-document]"));
            let progressFrame = 0;
            const updateProgress = () => {
                progressFrame = 0;
                const center = papers.scrollLeft + papers.clientWidth / 2;
                let nearestIndex = 0;
                let nearestDistance = Number.POSITIVE_INFINITY;
                documents.forEach((documentLink, index) => {
                    const documentCenter = documentLink.offsetLeft + documentLink.offsetWidth / 2;
                    const distance = Math.abs(documentCenter - center);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestIndex = index;
                    }
                });
                progress.textContent = `Document ${nearestIndex + 1} of ${documents.length}`;
            };
            papers.addEventListener("scroll", () => {
                if (!progressFrame) progressFrame = window.requestAnimationFrame(updateProgress);
            }, { passive: true });
            updateProgress();
        }
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
