package com.example.septic.web;

import java.util.List;

/** Publication-reviewed lessons, independent of the private operations database. */
public final class CaseResearchGuides {
    private CaseResearchGuides() {}
    public record Step(String title, String text) {}
    public record Guide(String slug, String title, String description, String image, String intent,
                        String question, String answer, String example, List<Step> steps,
                        String boundary, String sourceName, String sourceUrl, String related, String relatedLabel) {
        public String path() { return "/" + slug + "/"; }
        public String headline() { return switch (slug) {
            case "septic-repair-records" -> "Told the septic system was replaced? Check the records behind the claim.";
            case "septic-records-before-pool" -> "Planning a pool? Find the septic layout before choosing the spot.";
            default -> "Septic system installed. But is the final approval in the file?";
        }; }
        public String promise() { return switch (slug) {
            case "septic-repair-records" -> "We compare the permit, repair and installation records so you can see what replacement work is documented—and what still needs confirmation.";
            case "septic-records-before-pool" -> "We find and explain the recorded tank, field and repair-area clues so you know what to take to a local professional.";
            default -> "We connect the permit, inspection and completion records so you can see which stages are documented and which questions remain.";
        }; }
        public String action() { return switch (slug) {
            case "septic-repair-records" -> "Check this property's repair records";
            case "septic-records-before-pool" -> "Find my septic layout records";
            default -> "Check my approval records";
        }; }
        public List<Step> caseTrace() { return switch (slug) {
            case "septic-repair-records" -> List.of(
                new Step("The question", "Does the file support the reported septic replacement?"),
                new Step("The record review", "We checked the returned Haverhill repair file against the work being verified."),
                new Step("The useful answer", "The file documented an older repair, not the claimed later replacement. We separated the documented work from the remaining evidence gap."));
            case "septic-records-before-pool" -> List.of(
                new Step("The record question", "What layout and field lengths do the installation pages actually describe?"),
                new Step("The page comparison", "We read Morgan's approval sketch and installer certificate together, keeping the washer line separate."),
                new Step("The useful answer", "The pages contained different field totals. We explained the discrepancy rather than turning an uncertain total into a site-planning fact."));
            default -> List.of(
                new Step("The question", "Does an Installed status mean every record stage is complete?"),
                new Step("The status review", "We compared the King County installation and inspection entries with the final record-drawing workflow."),
                new Step("The useful answer", "The historical snapshot supported installation and inspection findings, but not a finalized record-drawing stage. We kept those conclusions separate."));
        }; }
        public String handoff() { return switch (slug) {
            case "septic-repair-records" -> "The records we recover, a comparison of documented repair scope and dates, and a clear list of any missing installation or completion evidence.";
            case "septic-records-before-pool" -> "The layout records we recover, an explanation of the tank and field clues, and the uncertainties to resolve with your local office or septic professional.";
            default -> "The records we recover, a stage-by-stage explanation of permit, inspection and completion evidence, and the precise final-record question to resolve.";
        }; }
    }
    public static final List<Guide> ALL = List.of(
        new Guide("septic-repair-records", "Septic Repair Records: Verify What Was Replaced",
            "Find septic repair permits, installation records and completion evidence. Learn how to check whether a repair file supports a claimed replacement.",
            "repair-records-v1.webp", "review", "Does this repair permit prove the system was replaced?",
            "Not by itself. Match the repair scope to the installation and completion records.",
            "In a Haverhill, Massachusetts investigation, the returned file documented a historical repair. It did not establish the more recent replacement the customer was trying to verify. In another reviewed repair file, an issued repair authorization likewise did not establish completed work. Neither gap proves that the claimed work never happened.",
            List.of(new Step("Match the property", "Check the address, parcel, lot and drawing references on every page. A nearby property or an older parcel reference needs its own identity check."),
                new Step("Read the authorized scope", "Look for the components named in the repair: tank, distribution box, field lines or another part. A repair to one component is not automatically a whole-system replacement."),
                new Step("Find the installation evidence", "Compare dates, installer records, inspection notes and any later as-built. Keep proposed work separate from what the returned record documents as installed."),
                new Step("Ask for the missing stage", "Request the repair permit, supporting design, installation inspection, final approval and any later record drawing for the property. Ask which of these the office actually holds.")),
            "An old repair permit cannot establish today's condition or resolve an undocumented later change. A current inspection answers a different question from a historical file search.",
            "Haverhill public records request", "https://www.haverhillma.gov/government/public-records-request/",
            "/septic-record-brief-example/davidson/", "See an earlier and later repair drawing compared"),
        new Guide("septic-records-before-pool", "Find Septic Records Before Planning a Pool",
            "Find tank, drainfield and repair-area records before planning a pool. Understand layout clues, conflicting dimensions and what requires an on-site locate.",
            "pool-records-v1.webp", "building", "Where can a pool go without interfering with the septic system?",
            "Start with the recorded layout. Then have the site and local requirements checked.",
            "A Morgan County, Alabama file illustrates why the pages need to be read together. Its approval page described three 70-foot lines, totaling 210 feet; the installer certificate appeared to say 200 feet. It also described a separate 50-foot washer line. The discrepancy was preserved in the explanation rather than silently choosing one total or adding the washer line to the septic field.",
            List.of(new Step("Find the installed layout", "Ask for the existing septic file, Approval for Use or equivalent installation record, and any later repair drawings. A proposed design and an installed layout can differ."),
                new Step("Separate the system areas", "Identify the tank, field lines, access points and any reserved repair area. A reserved area is not necessarily another installed field, but it can still matter to project planning."),
                new Step("Keep uncertain dimensions visible", "Note conflicting lengths and missing orientation. A GIS parcel map or aerial image may help identify the property; it does not by itself locate the buried system."),
                new Step("Confirm on site before committing", "Share the file with the local permitting office and an appropriate licensed septic professional. Have relevant components located and project constraints checked before excavation or final placement.")),
            "This is a record-research starting point, not a pool siting approval, survey or digging plan. Setbacks and review requirements depend on the jurisdiction and site.",
            "Morgan County Health Department", "https://www.alabamapublichealth.gov/morgan/contact.html",
            "/septic-tank-location-records/", "Find records with tank and field location clues"),
        new Guide("septic-final-approval-records", "Septic Final Approval Records: Installed Is Not Always Finalized",
            "Distinguish a septic construction permit, installation inspection and final record. Learn what to request when final approval or an as-built is missing.",
            "final-approval-v1.webp", "review", "The portal says installed. Is the septic file complete?",
            "Check the final record stage separately from the installation status.",
            "In a reviewed King County, Washington snapshot, the system showed Installed and a Satisfactory inspection, while the Finalize Date was blank and the record-drawing workflow remained open. Those fields answered different questions. This describes the historical snapshot we reviewed, not the permit's current status. By contrast, a Williamson County return included a separate Certificate of Completion alongside the construction permit.",
            List.of(new Step("Identify the design authorization", "The permit or approved design describes what may be constructed. Do not treat that document alone as evidence that all work was completed."),
                new Step("Read the inspection result", "Check what was inspected, the date and any conditions or follow-up notes. An inspection result is evidence for that inspection stage, not every administrative stage."),
                new Step("Look for the final document", "Search for a completion certificate, final approval or approved record drawing. Names and workflows vary by office; a missing portal field alone does not prove the system is unapproved."),
                new Step("Ask a precise closing question", "Ask the custodian: Is this the complete existing file? Is an approved record drawing or completion document held elsewhere, awaiting publication, or not present? Request the document rather than relying on a summary status.")),
            "Historical approval is not a current condition inspection. Do not call a system illegal or incomplete solely because one public search or field is blank.",
            "King County on-site sewage system records", "https://kingcounty.gov/en/dept/dph/health-safety/environmental-health/on-site-sewage-systems/oss-system-records",
            "/septic-record-brief-example/williamson/", "See a permit and completion certificate explained")
    );
}
