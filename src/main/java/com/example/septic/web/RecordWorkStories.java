package com.example.septic.web;

import java.util.List;

/** Reviewed public factual explanations only; never reads customer storage. */
public final class RecordWorkStories {
    private RecordWorkStories() {}
    public record Entry(String label, String text) {}
    public record Story(String slug, String region, String title, String description, String image,
                        String takeaway, List<Entry> steps, List<Entry> findings,
                        String delivered, String limit, String sourceName, String sourceUrl,
                        String guideUrl, String guideLabel) {
        public String path() { return "/septic-record-brief-example/" + slug + "/"; }
    }
    public static final List<Story> ALL = List.of(
        new Story("davidson", "Tennessee", "Which septic drawing should you use after a repair?",
            "A real record investigation: recover earlier and later as-builts, separate original field lines from repairs, and explain which drawing answers the question.",
            "davidson-repair-review-v2.webp", "The older drawing was only part of the answer.",
            List.of(new Entry("Find the complete drawing history", "We matched the property identifiers and searched the official Metro Health archive. The search recovered an earlier as-built, a later repair as-built and supporting design material."),
                new Entry("Separate the relevant originals", "We reviewed the pages individually. A mixed historical archive contained other address references, so it was excluded from the delivered package."),
                new Entry("Compare what changed", "The later as-built explicitly distinguishes original field lines from three added lines. Reading both stages avoids presenting the earlier drawing as the whole recorded system.")),
            List.of(new Entry("Earlier as-built", "Historical original layout"),
                new Entry("Later repair as-built", "Original lines + three added lines"),
                new Entry("Source selection", "Two matched as-built packets; mixed archive excluded")),
            "The earlier and later original as-built PDFs, a two-page explanation and a one-page visual comparison were made available to the customer free. The explanation connects each finding to its source rather than merging the drawings into an invented plan.",
            "These are historical records. Neither the original drawings nor this conceptual illustration confirms today's buried positions, working condition or permission to dig.",
            "Metro Health public HealthCode archive", "https://documents.nashville.gov/Request/Form/HealthCode",
            "/how-to-read-septic-as-built/", "How to read an as-built drawing"),
        new Story("harnett", "North Carolina", "Septic originals found online. No agency wait needed.",
            "A real septic record search recovered an operation permit and design files from a public archive, then separated the installed layout from the repair area.",
            "harnett-record-recovery-v2.webp", "Finding the file was only the first step.",
            List.of(new Entry("Match the property before the permit", "We cross-checked the official parcel and permit index rather than relying on a similar address or a nearby result."),
                new Entry("Follow the original attachments", "The county's public permit attachment directory supplied an operation permit and a design/soil package. These originals were recovered without registration or an agency request."),
                new Entry("Read installation and design separately", "We reviewed the original pages and separated the operation-permit layout from design and repair-area information. A reserved area is not another installed field.")),
            List.of(new Entry("Operation permit", "3 bedrooms · 1,000-gallon tank"),
                new Entry("Recorded field specification", "Four lines · 70 ft each"),
                new Entry("Search outcome", "Two original files recovered online")),
            "Both matched original files were delivered free with an explanation of the installed record, design information and repair area. The handoff answered the documentary question without waiting for a new agency search.",
            "Public recovery worked for this file, not necessarily every property. Historical layout and a repair area do not establish current field positions, present condition or approval for an addition.",
            "County public permit index", "https://gis.harnett.org/arcgis/rest/services/Planning/Permits/MapServer/0",
            "/no-septic-records-found/", "What to try when no septic record appears"),
        new Story("williamson", "Tennessee", "A septic permit is not the completion record.",
            "See how a construction permit, Certificate of Completion and source-linked Record Brief answer different parts of a septic-record question.",
            "williamson-completion-review-v2.webp", "The permit described the design. The certificate added completion.",
            List.of(new Entry("Ask for the complete existing file", "The county returned a construction permit and Certificate of Completion as its complete holding for the request. We checked both against the property identifiers and consistent site geometry."),
                new Entry("Keep the document stages distinct", "The permit records a three-bedroom design, separate septic and pump tanks, and a four-trench specification. The completion certificate records historical construction approval and a relative sketch."),
                new Entry("Turn the sources into an answer", "We tied the specifications to the permit and the completion finding to the certificate. Ambiguous handwritten lengths were not used to silently replace the permit's trench specification.")),
            List.of(new Entry("Permit / design basis", "3 bedrooms"),
                new Entry("Separate components", "1,000-gallon septic tank + 1,000-gallon pump tank"),
                new Entry("Permit / trench specification", "370 linear ft · four trenches"),
                new Entry("Completion certificate", "Historical construction approval recorded")),
            "Both unaltered county originals and a two-page Record Brief were delivered. The brief brings together the source, the answer and the limits without making the customer interpret two historical forms alone.",
            "Historical completion approval does not verify today's condition, later changes or exact underground positions. A relative sketch is a starting point for a current field locate, not a digging instruction.",
            "County Septic Records Request", "https://williamsoncounty-tn.gov/FormCenter/SewageDisposal-12/Septic-Records-Request-60",
            "/septic-permit-process/", "Permit, installation and final approval stages")
    );
}
