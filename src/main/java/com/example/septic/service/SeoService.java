package com.example.septic.service;

import com.example.septic.config.AppSiteProperties;
import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.data.model.FaqBlock;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.web.EditorialProfile;
import com.example.septic.web.PageMeta;
import com.example.septic.web.PageLink;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SeoService {
    private final AppSiteProperties siteProperties;
    private final PublishingPolicyService publishingPolicyService;
    private final ResearchDataService researchDataService;
    private final ObjectMapper objectMapper;

    public SeoService(
            AppSiteProperties siteProperties,
            PublishingPolicyService publishingPolicyService,
            ResearchDataService researchDataService
    ) {
        this.siteProperties = siteProperties;
        this.publishingPolicyService = publishingPolicyService;
        this.researchDataService = researchDataService;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    public PageMeta homePage() {
        String canonicalUrl = absoluteUrl("/");
        return pageMeta(
                "Septic Records Lookup, Permit Search & Cost Guides by State | SepticPath",
                "State-aware septic records lookup, permit search, county file paths, buyer workflow, and planning cost guides across all 50 states.",
                canonicalUrl,
                "index,follow",
                List.of(),
                List.of(
                        toJson(editorialOrganization()),
                        toJson(webSite(canonicalUrl, "SepticPath",
                                "State-aware septic records lookup, permit search, county file paths, buyer workflow, and planning cost guides across all 50 states.")),
                        toJson(webPage(canonicalUrl, "SepticPath",
                                "State-aware septic records lookup, permit search, county file paths, buyer workflow, and planning cost guides across all 50 states.", "CollectionPage"))
                )
        );
    }

    public PageMeta calculatorPage() {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/");
        return pageMeta(
                "Septic Cost Calculator by State | Records & Permit Checks | SepticPath",
                "Estimate septic cost, tank size, system class, and quote risk by state after you clarify the file, permit path, or buyer workflow.",
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Septic System Cost Calculator", canonicalUrl)
                ),
                List.of(
                        toJson(webPage(canonicalUrl, "Septic Cost Calculator by State",
                                "Estimate septic cost, tank size, system class, and quote risk by state after you clarify the file, permit path, or buyer workflow.", "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic System Cost Calculator", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta stateCoveragePage() {
        String canonicalUrl = absoluteUrl("/states/");
        return pageMeta(
                "State Septic Guides, Records Pages, and Permit Workflows | SepticPath",
                "Track live septic state guides and the permit, records, buyer, replacement, inspection, and site-risk pages behind them across all 50 states.",
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("State Coverage", canonicalUrl)
                ),
                List.of(
                        toJson(webPage(canonicalUrl,
                                "State Coverage",
                                "Track live septic state guides and the permit, records, buyer, replacement, inspection, and site-risk pages behind them across all 50 states.",
                                "CollectionPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("State Coverage", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta tankSizeEstimatorPage() {
        String canonicalUrl = absoluteUrl("/septic-tank-size-estimator/");
        List<FaqBlock> faqBlocks = tankSizeEstimatorFaqs();
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(webPage(canonicalUrl, "Septic Tank Size Estimator",
                "Estimate likely minimum septic tank size, a conservative range, and a rough pumping cadence by state.", "WebPage")));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb("Septic Tank Size Estimator", canonicalUrl)
        ))));
        jsonLdBlocks.add(toJson(faqPage(
                canonicalUrl,
                "Septic Tank Size Estimator",
                "Estimate likely minimum septic tank size, a conservative range, and a rough pumping cadence by state.",
                faqBlocks
        )));
        return pageMeta(
                "Septic Tank Size Estimator",
                "Estimate likely minimum septic tank size, a conservative range, and a rough pumping cadence by state.",
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Septic Tank Size Estimator", canonicalUrl)
                ),
                jsonLdBlocks
        );
    }

    public List<FaqBlock> tankSizeEstimatorFaqs() {
        return List.of(
                new FaqBlock(
                        "How should a homeowner use this septic tank size estimator?",
                        "Use it as a planning tool to estimate a likely minimum tank size and a conservative range before you collect quotes or rely on old paperwork."
                ),
                new FaqBlock(
                        "Do bedrooms matter more than current occupancy for septic tank size?",
                        "In many states, bedrooms or design flow are the main public sizing signal. Occupancy still helps widen the planning range when usage is clearly higher than the bedroom count suggests."
                ),
                new FaqBlock(
                        "Does a garbage disposal change the likely tank size?",
                        "Sometimes yes. Some states call this out directly, and even where the rule is less explicit, disposal use is a reasonable homeowner-facing signal that the conservative range may need to move up."
                ),
                new FaqBlock(
                        "Can seasonal use justify a much smaller septic tank?",
                        "Usually not. Seasonal use may soften the pumping cadence estimate, but it should not aggressively shrink a homeowner-facing size recommendation."
                )
        );
    }

    public PageMeta drainfieldEstimatorPage() {
        String canonicalUrl = absoluteUrl("/drain-field-estimator/");
        List<FaqBlock> faqBlocks = drainfieldEstimatorFaqs();
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(webPage(canonicalUrl, "Drain Field Replacement Estimator",
                "Estimate drain field replacement risk, redesign pressure, and likely cost swing by state before you collect quotes.", "WebPage")));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb("Drain Field Replacement Estimator", canonicalUrl)
        ))));
        jsonLdBlocks.add(toJson(faqPage(
                canonicalUrl,
                "Drain Field Replacement Estimator",
                "Estimate drain field replacement risk, redesign pressure, and likely cost swing by state before you collect quotes.",
                faqBlocks
        )));
        return pageMeta(
                "Drain Field Replacement Estimator",
                "Estimate drain field replacement risk, redesign pressure, and likely cost swing by state before you collect quotes.",
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Drain Field Replacement Estimator", canonicalUrl)
                ),
                jsonLdBlocks
        );
    }

    public List<FaqBlock> drainfieldEstimatorFaqs() {
        return List.of(
                new FaqBlock(
                        "When should a homeowner use a drain field estimator instead of a full septic calculator?",
                        "Use it when the field layout, replacement area, wetness, or site viability looks like the main problem and you need to know whether the quote may widen beyond simple trench work."
                ),
                new FaqBlock(
                        "Does a drain field issue always mean the whole septic system must be replaced?",
                        "Not always. Sometimes the field is the main issue, but weak soil, missing replacement area, or site limits can turn a field question into a broader redesign or full-system conversation."
                ),
                new FaqBlock(
                        "Why does replacement area matter so much to drain field cost?",
                        "Because a field job stays cheaper only when the parcel still supports a credible replacement layout. If the lot does not, redesign and alternative-system risk rise quickly."
                ),
                new FaqBlock(
                        "Should I trust the low end if the soil or perc status is still unknown?",
                        "No. Unknown soil status is one of the clearest reasons to keep the drain field range wide until the site story is more defined."
                )
        );
    }

    public PageMeta pumpScheduleEstimatorPage() {
        String canonicalUrl = absoluteUrl("/septic-pump-schedule-estimator/");
        return pageMeta(
                "Septic Pump Schedule Estimator",
                "Estimate a rough pumping cadence, inspection cadence, and maintenance reminder from tank size and use.",
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Septic Pump Schedule Estimator", canonicalUrl)
                ),
                List.of(
                        toJson(webPage(canonicalUrl, "Septic Pump Schedule Estimator",
                                "Estimate a rough pumping cadence, inspection cadence, and maintenance reminder from tank size and use.", "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic Pump Schedule Estimator", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta stateGuide(StateProfile state, EditorialProfile preparedBy, EditorialProfile reviewedBy) {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/");
        String title = stateGuideSeoTitle(state);
        String description = stateGuideDescription(state);
        List<FaqBlock> faqBlocks = stateGuideFaqs(state);
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withSemanticEvidence(
                withEditorialMeta(webPage(canonicalUrl,
                        title,
                        description,
                        "WebPage"), state.lastVerifiedAt(), preparedBy, reviewedBy),
                resolveSources(state.officialSourceIds()),
                List.of(state.stateName() + " septic systems", "septic permits", "septic records", "septic costs"),
                state.stateName() + " septic permit, records, and cost workflow"
        )));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb("Septic System Cost Calculator", absoluteUrl("/septic-system-cost-calculator/")),
                crumb(state.stateName() + " Guide", canonicalUrl)
        ))));
        if (!faqBlocks.isEmpty()) {
            jsonLdBlocks.add(toJson(faqPage(
                    canonicalUrl,
                    title,
                    description,
                    faqBlocks
            )));
        }
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Septic System Cost Calculator", absoluteUrl("/septic-system-cost-calculator/")),
                        crumb(state.stateName() + " Guide", canonicalUrl)
                ),
                jsonLdBlocks
        );
    }

    public PageMeta queuedStateGuide(String stateName, String stateSlug) {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/" + stateSlug + "/");
        String title = stateName + " Septic Guide | Research Queue";
        String description = "Planning starter for " + stateName + " homeowners while the official-source state guide is still in the research queue.";
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "noindex,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("State Coverage", absoluteUrl("/states/")),
                        crumb(stateName + " Research Queue", canonicalUrl)
                ),
                List.of(
                        toJson(webPage(
                                canonicalUrl,
                                title,
                                description,
                                "WebPage"
                        )),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("State Coverage", absoluteUrl("/states/")),
                                crumb(stateName + " Research Queue", canonicalUrl)
                        )))
                )
        );
    }

    public String stateGuideHeading(StateProfile state) {
        return switch (state.stateCode()) {
            case "WV" -> "West Virginia septic permit cost, sewage permit file, and local health guide";
            case "AL" -> "How much does a perc test cost in Alabama? Fees, quotes, and county records";
            case "AR" -> "Arkansas septic cost guide and county permit path";
            case "MS" -> "Mississippi septic cost guide and public records path";
            case "IN" -> "Indiana septic cost guide and county permit path";
            case "OK" -> "Oklahoma septic cost guide and soil-test path";
            case "KY" -> "Kentucky septic cost guide and local file path";
            case "MN" -> "Minnesota septic cost guide and property transfer risk";
            case "IL" -> "Illinois septic cost guide and local health file path";
            case "MD" -> "Maryland septic cost guide and property transfer risk";
            case "WI" -> "Wisconsin septic cost guide and POWTS inspection path";
            case "LA" -> "Louisiana septic cost guide and parish site-risk path";
            case "AZ" -> "Arizona septic cost guide and site approval path";
            case "CO" -> "Colorado septic cost guide and local OWTS permit path";
            case "VA" -> "Virginia septic cost guide and inspection obligations";
            case "TN" -> "Tennessee septic cost guide and permit file path";
            case "SC" -> "South Carolina septic permit cost, permit copy, and D-1740 guide";
            case "CA" -> "California septic cost guide and county permit path";
            case "TX" -> "Texas septic cost guide and local OSSF permit path";
            case "NY" -> "New York septic cost guide and Appendix 75-A rules";
            case "OH" -> "Ohio septic cost guide and local health permit path";
            case "MI" -> "Michigan septic cost guide and local health records path";
            case "GA" -> "Georgia septic permit cost, permit records, and soil analysis guide";
            case "RI" -> "Rhode Island septic permit cost, DEM file search, and suitability guide";
            case "PA" -> "Pennsylvania septic cost guide and SEO permit path";
            case "CT" -> "Connecticut septic cost guide and design flow rules";
            case "OR" -> "Oregon septic cost guide and site evaluation path";
            case "MA" -> "Massachusetts septic cost guide and Title 5 overview";
            case "FL" -> "Florida septic cost guide and DEP vs county path";
            case "WA" -> "Washington septic cost guide and inspection rules";
            case "NJ" -> "New Jersey septic cost guide and management rules";
            case "NC" -> "North Carolina septic cost guide and permit steps";
            case "MO" -> "Missouri septic cost guide and local permit path";
            default -> state.stateName() + " septic cost guide";
        };
    }

    public List<FaqBlock> stateGuideFaqs(StateProfile state) {
        List<FaqBlock> faqBlocks = new ArrayList<>();

        switch (state.stateCode()) {
            case "AL" -> {
                faqBlocks.add(new FaqBlock(
                        "How much does a septic permit cost in Alabama?",
                        "The county health department and the parcel file usually decide whether the cheap Alabama permit story is real. Permit to Install timing, soil testing or percolation work, and missing Approval for Use records can all widen the practical path before you trust the low end."
                ));
                faqBlocks.add(new FaqBlock(
                        "How do you get septic permit records in Alabama?",
                        "Start with the county health department that handles the property. Property owners or agents can request septic tank information from that office, while non-owners may need the Records Request path for a permit copy or related file history."
                ));
                faqBlocks.add(new FaqBlock(
                        "How much is a perc test in Alabama?",
                        "In Alabama, the county health department, prior soil information, and whether the parcel already has a usable Permit to Install file usually matter more than a generic perc-test number. Treat perc pricing as real only after the county file shows what has already been reviewed."
                ));
            }
            case "GA" -> {
                faqBlocks.add(new FaqBlock(
                        "How much is a septic permit in Georgia?",
                        "Georgia permit questions usually get real only after the county environmental health office, the soil-analysis requirement, and any existing permit file are clear. Garbage-disposal sizing rules can also change the real project cost before you treat the first quote like a permit-ready number."
                ));
                faqBlocks.add(new FaqBlock(
                        "What should you check before trusting a Georgia septic permit quote?",
                        "Check the county office first, then ask for any existing soil analysis, permit file, as-built sketch, repair history, and confirmation of whether a garbage disposal is installed. Those details usually tell you faster than the first quote whether the cheaper path is still realistic."
                ));
                faqBlocks.add(new FaqBlock(
                        "How much is a perc test in Georgia?",
                        "Georgia usually routes that conversation through the county environmental health office and the soil-analysis path, not a clean statewide fee. First confirm the county file, usable drainfield area, and any existing permit history before trusting a low-end perc-test number."
                ));
            }
            default -> {
            }
        }

        if (hasText(state.whoToCallFirst())) {
            faqBlocks.add(new FaqBlock(
                    "Who should a homeowner call first about septic work in " + state.stateName() + "?",
                    state.whoToCallFirst() + " Use that first call to confirm the local process before you rely on a national rule of thumb."
            ));
        }

        if (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty()) {
            faqBlocks.add(new FaqBlock(
                    "What septic records should you request first in " + state.stateName() + "?",
                    String.join(" ", state.recordsToRequest()) + " Those records help confirm whether the low end of a quote is still realistic."
            ));
        }

        if (state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty()) {
            faqBlocks.add(new FaqBlock(
                    "What usually pushes a " + state.stateName() + " septic quote above the low end?",
                    String.join(" ", state.lowEndRiskChecks()) + " " + state.localOverrideNote()
            ));
        }

        String specialContext = firstNonBlank(
                state.specialAreaNote(),
                state.pageAngle(),
                state.permitTimelineNote(),
                state.siteEvalSummary()
        );
        if (specialContext != null) {
            faqBlocks.add(new FaqBlock(
                    "What makes " + state.stateName() + " different from a generic septic cost estimate?",
                    specialContext + " Final design, permit timing, and approval still need local verification."
            ));
        }

        if (faqBlocks.size() < 4) {
            faqBlocks.add(new FaqBlock(
                    "How much should you trust an online septic estimate in " + state.stateName() + "?",
                    "Treat it as a planning range only. " + state.localOverrideNote()
                            + " Final design, permit path, and approval still depend on local review."
            ));
        }

        return faqBlocks;
    }

    public PageMeta contentPage(ContentPage contentPage, EditorialProfile preparedBy, EditorialProfile reviewedBy) {
        String canonicalUrl = absoluteUrl("/" + contentPage.slug() + "/");
        String seoTitle = contentPageSeoTitle(contentPage);
        List<Map<String, Object>> breadcrumbs = contentPageBreadcrumbs(contentPage, canonicalUrl);
        List<String> jsonLdBlocks = new ArrayList<>();
        String pageType = switch (contentPage.slug()) {
            case "septic-records-request-builder" -> "WebApplication";
            case "official-septic-lookup-tools", "septic-records-by-county" -> "CollectionPage";
            default -> "WebPage";
        };
        jsonLdBlocks.add(toJson(withSemanticEvidence(
                withEditorialMeta(
                        webPage(canonicalUrl, seoTitle, contentPage.metaDescription(), pageType),
                        contentPage.updatedAt(),
                        preparedBy,
                        reviewedBy
                ),
                List.of(),
                contentTopics(contentPage),
                contentPage.title()
        )));
        jsonLdBlocks.add(toJson(breadcrumb(breadcrumbs)));
        return pageMeta(
                seoTitle,
                contentPage.metaDescription(),
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(breadcrumbs),
                jsonLdBlocks
        );
    }

    public PageMeta stateMoneyPage(StateMoneyPage stateMoneyPage, StateProfile state, EditorialProfile preparedBy, EditorialProfile reviewedBy) {
        String canonicalUrl = absoluteUrl(stateMoneyPage.path(state.slug()));
        String seoTitle = stateMoneyPageSeoTitle(stateMoneyPage, state);
        String seoDescription = stateMoneyPageDescription(stateMoneyPage, state);
        String robots = publishingPolicyService.isIndexableStateMoneyPage(stateMoneyPage, state)
                ? "index,follow"
                : "noindex,follow";
        List<Map<String, Object>> breadcrumbs = stateMoneyPageBreadcrumbs(stateMoneyPage, state, canonicalUrl);
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withSemanticEvidence(
                withEditorialMeta(
                        webPage(canonicalUrl, seoTitle, seoDescription, "WebPage"),
                        stateMoneyPage.updatedAt(),
                        preparedBy,
                        reviewedBy
                ),
                resolveSources(stateMoneyPage.officialSourceIds()),
                List.of(
                        state.stateName() + " septic systems",
                        stateMoneyPage.title(),
                        stateMoneyPage.contentSlug().replace('-', ' ')
                ),
                stateMoneyPage.title()
        )));
        jsonLdBlocks.add(toJson(breadcrumb(breadcrumbs)));
        if (shouldExposeFaqStructuredData(stateMoneyPage)
                && stateMoneyPage.faqBlocks() != null
                && !stateMoneyPage.faqBlocks().isEmpty()) {
            jsonLdBlocks.add(toJson(faqPage(canonicalUrl, seoTitle, seoDescription, stateMoneyPage.faqBlocks())));
        }
        return pageMeta(
                seoTitle,
                seoDescription,
                canonicalUrl,
                robots,
                breadcrumbLinks(breadcrumbs),
                jsonLdBlocks
        );
    }

    private List<Map<String, Object>> contentPageBreadcrumbs(ContentPage contentPage, String canonicalUrl) {
        List<Map<String, Object>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(crumb("Home", absoluteUrl("/")));
        switch (contentPage.slug()) {
            case "official-septic-lookup-tools" ->
                    breadcrumbs.add(crumb("Septic Permit Lookup", absoluteUrl("/septic-permit-lookup/")));
            case "tdec-septic-records",
                 "north-carolina-septic-permit-lookup",
                 "texas-ossf-records-search",
                 "florida-ostds-permit-lookup",
                 "dhec-septic-permit-lookup" ->
                    breadcrumbs.add(crumb("Official Septic Lookup Tools", absoluteUrl("/official-septic-lookup-tools/")));
            case "septic-records-checklist",
                 "septic-permit-process",
                 "how-to-find-septic-records-online",
                 "septic-records-by-county",
                 "septic-permit-search-by-address",
                 "septic-permit-records-request",
                 "septic-records-request-builder",
                 "septic-as-built-records",
                 "septic-tank-location-records",
                 "septic-inspection-letter",
                 "septic-transfer-compliance" ->
                    breadcrumbs.add(crumb("Septic Permit Lookup", absoluteUrl("/septic-permit-lookup/")));
            default -> {
            }
        }
        breadcrumbs.add(crumb(contentPage.title(), canonicalUrl));
        return breadcrumbs;
    }

    private List<Map<String, Object>> stateMoneyPageBreadcrumbs(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            String canonicalUrl
    ) {
        Map<String, Object> parent = switch (stateMoneyPage.contentSlug()) {
            case "septic-records-checklist" ->
                    crumb("Septic Records Lookup", absoluteUrl("/septic-records-checklist/"));
            case "septic-permit-process" ->
                    crumb("Septic Permit Lookup", absoluteUrl("/septic-permit-lookup/"));
            default ->
                    crumb(state.stateName() + " Septic Guide", absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/"));
        };
        return List.of(
                crumb("Home", absoluteUrl("/")),
                parent,
                crumb(stateMoneyPage.title(), canonicalUrl)
        );
    }

    public PageMeta countyRecordsPage(CountyRecordsPage countyPage, StateProfile state, EditorialProfile preparedBy, EditorialProfile reviewedBy) {
        String canonicalUrl = absoluteUrl(countyPage.path(state.slug()));
        String title = countyRecordsTitle(countyPage, state);
        String description = countyRecordsDescription(countyPage, state);
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withSemanticEvidence(
                withEditorialMeta(webPage(
                        canonicalUrl,
                        title,
                        description,
                        "WebPage"
                ), countyPage.updatedAt(), preparedBy, reviewedBy),
                resolveSources(countyPage.officialSourceIds()),
                List.of(
                        countyPage.countyName() + " septic records",
                        countyPage.countyName() + " septic permit lookup",
                        state.stateName() + " septic systems"
                ),
                countyPage.countyName() + " official septic records workflow"
        )));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb("Septic Records Lookup", absoluteUrl("/septic-records-checklist/")),
                crumb(state.stateName() + " Septic Records Lookup", absoluteUrl("/septic-records-checklist/" + state.slug() + "/")),
                crumb(countyPage.countyName() + " Septic Records", canonicalUrl)
        ))));
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Septic Records Lookup", absoluteUrl("/septic-records-checklist/")),
                        crumb(state.stateName() + " Septic Records Lookup", absoluteUrl("/septic-records-checklist/" + state.slug() + "/")),
                        crumb(countyPage.countyName() + " Septic Records", canonicalUrl)
                ),
                jsonLdBlocks
        );
    }

    /**
     * A short, query-aligned answer shown above the long workflow content.
     * Keep this factual and route-oriented: the page should answer the search
     * question first, then explain the official path and edge cases below.
     */
    public String contentQuickAnswer(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "tdec-septic-records" -> "Open the working Tennessee SSDS program page, identify the field office or contract county, and request the permit, layout, or written no-record response with the parcel, owner, subdivision, lot, or legal description. The direct TDEC record search may return 403.";
            case "north-carolina-septic-permit-lookup" -> "North Carolina septic permits are usually held by county environmental health, not one statewide database. Identify the county, search by address or parcel, then request the permit packet, as-built, final approval, repair history, or written no-record response.";
            case "septic-records-checklist" -> "Start with the state records route, then open the county file path for the permit, as-built, final approval, inspection letter, or repair record that actually answers the property question.";
            case "septic-permit-lookup" -> "A septic permit lookup is usually state plus county. Use the official state route first, then follow the county health or environmental file path instead of treating a broad search result as the record itself.";
            case "how-to-find-septic-records-online" -> "You can often find septic records online, but the file may live in a state portal, county health office, GIS attachment, or records-request queue. The fastest move is identifying the office that owns the parcel file.";
            case "septic-records-by-county" -> "Choose the county first. County pages route you to the official permit copy, as-built, inspection letter, repair file, or records office instead of leaving you on a generic septic overview.";
            case "septic-permit-search-by-address" -> "An address search is a starting point, not proof that a record exists. Resolve the county, then retry with parcel, owner, legal-description, subdivision, or permit-number clues when the first search is incomplete.";
            case "septic-permit-records-request", "septic-records-request-builder" -> "Ask for the exact artifact you need: permit copy, as-built, final approval, inspection letter, repair history, or no-record response. Precise wording gets a more useful answer than a general septic-record request.";
            case "septic-as-built-records" -> "An as-built or site sketch shows the installed tank, field, and layout story that a permit title alone may not answer. Start with the official file owner and request the drawing or scanned attachment.";
            case "septic-tank-location-records" -> "Start with the permit file, as-built, site sketch, or approved plan. Those records can show the tank, drain field, reserve area, and access notes; if they do not, arrange a professional locate instead of guessing.";
            case "septic-inspection-letter" -> "A septic inspection letter is a separate workflow from a permit lookup. Confirm who issues the letter, what records support it, and whether the buyer, lender, or county requires a current professional inspection.";
            case "official-septic-lookup-tools" -> "Use the official state or county lookup first. SepticPath helps identify the correct source, expected document, and fallback request route when the government search is incomplete or difficult to navigate.";
            case "septic-system-cost-calculator" -> "A septic cost estimate is more useful after the permit file, site limits, bedroom count, and local approval path are clear. Use the state guide to identify what can widen the quote before comparing prices.";
            case "septic-replacement-cost", "drain-field-replacement-cost" -> "Replacement cost depends on the existing file, soil and site constraints, reserve area, system class, access, and restoration—not just the tank or field component named in the first quote.";
            case "perc-test-cost" -> "A perc test typically falls in a $300-$3,000 national planning range. The county, soil evidence, number of test locations, plot plan, professional scope, and permit stage determine where the real quote lands.";
            case "failed-perc-test-septic" -> "A failed perc test does not automatically mean the property is unusable. The next path depends on redesign options, reserve area, soil evidence, local rules, and whether the county will accept another site evaluation.";
            case "buying-a-house-with-a-septic-system" -> "Before buying, match the bedroom count, permit file, as-built, final approval, inspection scope, and repair history. A clean-looking property story is not the same as a complete septic file.";
            case "septic-inspection-cost" -> "Septic inspection cost depends on the requested scope and the records available before the visit. Pull the permit, as-built, pumping, and repair clues first so the inspection answers the real risk.";
            default -> contentPage.introCopy();
        };
    }

    public String stateGuideQuickAnswer(StateProfile state) {
        return switch (state.stateCode()) {
            case "AL" -> "Alabama's current perc and private soil-work planning band is $300-$2,700. ADPH separately publishes a $150-$250 public site-evaluation fee in participating counties and a $100-$200 dwelling permit-application band. Confirm the county program, sewer availability, and existing file before treating any of those numbers as the full project cost.";
            case "TN" -> "For Tennessee, start with the TDEC SSDS or county file route and confirm the permit, as-built, final approval, and repair history before relying on a cost or inspection assumption.";
            case "NC" -> "For North Carolina, start with the county environmental health file and identify the improvement permit, construction authorization, operation permit, as-built, or repair record tied to the property.";
            case "IN" -> "For Indiana, start with the county or local health office and confirm the permit, site file, soil report, sewer-availability record, and any transfer paperwork before comparing project quotes.";
            case "SC" -> "For South Carolina, start with the SCDES or county route and confirm the D-1740, permit copy, final inspection, and local office path before treating the project as routine.";
            case "TX" -> "For Texas, start with the county or authorized-agent OSSF route and confirm the permit, approved plan, address or parcel file, and ETJ context before pricing work.";
            default -> "Start with the official state and local health route for " + state.stateName() + ", confirm the file owner and first record, then use the estimate with the local permit and site assumptions in view.";
        };
    }

    public PageMeta recordFinderPage() {
        String canonicalUrl = absoluteUrl("/septic-record-finder/");
        String title = "Septic Records Finder by Address | County Permit and File Route | SepticPath";
        String description = "Enter a U.S. property address to identify its county and open the best available septic permit, records, as-built, or file-request route.";
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Septic Records Finder by Address", canonicalUrl)
                ),
                List.of(
                        toJson(webPage(canonicalUrl, title, description, "WebApplication")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic Records Finder by Address", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta recordsAccessIndexPage(String dataLastUpdated, int countyRouteCount, int stateCount) {
        String canonicalUrl = absoluteUrl("/septic-records-access-index/");
        String csvUrl = absoluteUrl("/septic-records-access-index.csv");
        String title = "Septic Records Access Index | Official County Permit and File Routes | SepticPath";
        String description = "Search and download official-source county septic records routes. Filter by state, route type, first artifact, confidence, review date, or parcel access, then open the government file path.";
        Map<String, Object> collectionPage = webPage(canonicalUrl, title, description, "CollectionPage");
        if (isIsoDate(dataLastUpdated)) {
            collectionPage.put("dateModified", dataLastUpdated);
        }
        collectionPage.put("mainEntity", Map.of(
                "@type", "Dataset",
                "@id", canonicalUrl + "#dataset"
        ));
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Septic Records Access Index", canonicalUrl)
                ),
                List.of(
                        toJson(collectionPage),
                        toJson(recordsAccessDataset(
                                canonicalUrl,
                                csvUrl,
                                description,
                                dataLastUpdated,
                                countyRouteCount,
                                stateCount
                        )),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic Records Access Index", canonicalUrl)
                        )))
                )
        ).withDataDownloadUrl(csvUrl);
    }

    public PageMeta offerPrepFileCheckPage() {
        String canonicalUrl = absoluteUrl("/offer-prep-septic-file-check/");
        String title = "Offer Prep Septic File Check | Records Route and Seller Request | SepticPath";
        String description = "Before an offer, resolve the septic records route by address or county, flag the bedroom-file question, and create a ready-to-send seller or listing-agent request.";
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Offer Prep Septic File Check", canonicalUrl)
                ),
                List.of(
                        toJson(webPage(canonicalUrl, title, description, "WebApplication")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Offer Prep Septic File Check", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta bedroomPermitCheckerPage() {
        String canonicalUrl = absoluteUrl("/septic-bedroom-permit-checker/");
        String title = "Septic Bedroom Permit Checker | Listing vs Permit Capacity | SepticPath";
        String description = "Compare a property's advertised bedroom count with the septic permit count, then create a records-first next-step note for buyers, sellers, and agents.";
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Septic Bedroom Permit Checker", canonicalUrl)
                ),
                List.of(
                        toJson(webPage(canonicalUrl, title, description, "WebApplication")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic Bedroom Permit Checker", canonicalUrl)
                        )))
                )
        );
    }

    private String countyRecordsTitle(CountyRecordsPage countyPage, StateProfile state) {
        String priorityTitle = switch (countyPage.key()) {
            case "VA::prince-william-county" -> "Prince William County Septic Records by Address or GPIN";
            case "TX::tarrant-county" -> "Tarrant County OSSF Records and Jurisdiction Check";
            case "TN::hamilton-county" -> "Hamilton County TN Septic Permit and Completion Certificate";
            case "NC::alamance-county" -> "Request Alamance County NC Septic Records";
            case "TN::knox-county" -> "Knox County TN SSDS File Search and Records Request";
            case "NC::lincoln-county" -> "Request Lincoln County NC Septic Records";
            case "NC::guilford-county" -> "Guilford County NC Septic Permit, Layout & Record Request";
            case "NC::iredell-county" -> "Iredell County NC Septic Records Online & Permit Files";
            case "GA::dekalb-county" -> "DeKalb County GA Septic Records or Certification Letter";
            case "TN::blount-county" -> "Blount County TN SSDS Records or Inspection Letter";
            case "TN::sumner-county" -> "Sumner County TN Septic Permit Search and Records";
            case "TN::montgomery-county" -> "Montgomery County TN Septic Permit Search & Records";
            case "TN::sevier-county" -> "Sevier County TN SSD Permit & Completion Records";
            case "TN::rutherford-county" -> "Rutherford County TN Septic Records and Permit Search";
            case "TN::bradley-county" -> "Bradley County TN Septic Permit Lookup and TDEC Records";
            case "TN::sullivan-county" -> "Sullivan County TN Septic Layout and Records Request";
            case "TN::loudon-county" -> "Loudon County TN Septic Records Search and Permit Files";
            case "TN::maury-county" -> "Maury County TN Septic Permit Lookup and TDEC Records";
            case "TN::jefferson-county" -> "Jefferson County TN Septic Permits, Final Approval and Records";
            case "TN::davidson-county" -> "Davidson County TN Septic Records and Property File Search";
            case "TN::madison-county" -> "Madison County TN Septic Drawing and Records Request";
            case "TN::shelby-county" -> "Shelby County TN Septic Permit, Repair and Inspection";
            case "TN::putnam-county" -> "Putnam County TN Septic Permit Search and TDEC Records";
            case "IN::monroe-county" -> "Monroe County IN Septic Permit and OpenGov Workflow";
            case "TN::anderson-county" -> "Anderson County TN Septic Permit Search and TDEC Records";
            case "NC::randolph-county" -> "Randolph County NC Septic Permits, Repairs and ePermits";
            case "NC::buncombe-county" -> "Buncombe County NC Septic Permit Lookup and Accela Records";
            case "NC::wake-county" -> "Wake County NC Septic Permit Search and iMAPS Records";
            case "NC::union-county" -> "Union County NC Septic Records & Existing System Inspection";
            case "NC::pitt-county" -> "Pitt County NC Septic Permit Search and Authorization to Construct";
            case "NC::pender-county" -> "Pender County NC Septic Permit Information Request";
            case "AL::tuscaloosa-county" -> "Tuscaloosa County AL Septic Permits and Records Contact";
            case "AL::calhoun-county" -> "Calhoun County AL Septic Permits and Environmental Office";
            case "SC::charleston-county" -> "Charleston County SC Septic Permit and Final Inspection Records";
            case "SC::greenville-county" -> "Greenville County SC Septic Permit and Final Inspection Records";
            case "SC::anderson-county" -> "Anderson County SC Septic Permit and Final Inspection Records";
            case "SC::spartanburg-county" -> "Spartanburg County SC Septic Permit and Final Inspection Records";
            case "TN::williamson-county" -> "Williamson County TN Septic Records and Sewage Disposal";
            case "MD::st-marys-county" -> "Search St. Mary's County Septic and Environmental Health Records";
            case "NY::suffolk-county" -> "Suffolk County NY Septic Location Record Request";
            case "AZ::maricopa-county" -> "Maricopa County Septic Records Search and Research Request";
            case "NC::brunswick-county" -> "Brunswick County Permit Search and Septic File Request";
            case "NC::forsyth-county" -> "Request Forsyth County NC Septic Permit and Soil Evaluation";
            case "TX::denton-county" -> "Denton County OSSF Records and Jurisdiction Check";
            case "TX::brazoria-county" -> "Brazoria County OSSF Permit Status and Records Request";
            case "WA::thurston-county" -> "Thurston County Septic Records and As-Built Search";
            case "NC::cumberland-county" -> "Cumberland County NC Septic Permit Search and Records";
            case "GA::forsyth-county" -> "Forsyth County GA Septic Permit Records Search";
            case "CA::san-bernardino-county" -> "San Bernardino County Septic Records and Permit Search";
            case "MI::washtenaw-county" -> "Washtenaw County Septic Records and Permit Search";
            case "MT::gallatin-county" -> "Gallatin County Septic Records and Permit Search";
            case "MD::frederick-county" -> "Frederick County Septic Records and Permit Search";
            case "GA::jackson-county" -> "Jackson County GA Septic Permit Records Search";
            case "NY::allegany-county" -> "Allegany County NY Septic Records and Permit Search";
            case "GA::hall-county" -> "Hall County GA Septic Permit Records Search";
            case "MD::harford-county" -> "Harford County Septic Records and Permit Search";
            case "MN::st-louis-county" -> "St. Louis County MN Septic Records Search";
            case "MI::livingston-county" -> "Livingston County Septic Records and Permit Search";
            case "NJ::gloucester-county" -> "Gloucester County NJ Septic Records Search";
            case "VA::spotsylvania-county" -> "Spotsylvania County Septic Records and Permit Search";
            case "MD::prince-georges-county" -> "Prince George's County Septic Records Search";
            case "VA::hanover-county" -> "Hanover County Septic Records and Permit Search";
            case "CA::san-diego-county" -> "San Diego County Septic Records and Permit Search";
            case "CA::tuolumne-county" -> "Tuolumne County Septic Records and Permit Search";
            case "NC::craven-county" -> "Craven County NC Septic Permit Search and Records";
            default -> "";
        };
        if (!priorityTitle.isBlank()) {
            return priorityTitle + " | SepticPath";
        }
        return countyPage.countyName() + " " + state.stateCode() + " Septic Permit Lookup & Records | SepticPath";
    }

    private String countyRecordsDescription(CountyRecordsPage countyPage, StateProfile state) {
        String priorityDescription = switch (countyPage.key()) {
            case "VA::prince-william-county" -> "Search the official Prince William Health District document portal by address or GPIN, then use the office fallback when the portal is blocked or incomplete.";
            case "TX::tarrant-county" -> "Confirm whether Tarrant County, a contract city, or an ETJ owns the OSSF file before requesting the permit, LTO, site evaluation, or recorded affidavit.";
            case "TN::hamilton-county" -> "Use Hamilton County document retrieval and its Groundwater fallback to obtain the septic permit and installation certificate of completion.";
            case "NC::alamance-county" -> "Request an existing Alamance County septic property file without confusing a historical record copy with a new application or paid field inspection.";
            case "TN::knox-county" -> "Prepare, send, and track the Knox County SSDS file-search request for the permit, soil mapping, layout, and completed repair records.";
            case "NC::lincoln-county" -> "Request Lincoln County septic records by address or parcel PIN and keep the request reference until the permit, approval, layout, or written response arrives.";
            case "NC::guilford-county" -> "Find the Guilford County permit, layout, or operation record through On-Site Water Protection or a public-records request using the address or parcel ID.";
            case "NC::iredell-county" -> "Search Iredell County septic records online by address, parcel, owner, or permit clue, then request the permit, layout, approval, or repair file if the GIS result is incomplete.";
            case "GA::dekalb-county" -> "Choose the DeKalb historical septic file route or the separate certification-letter evaluation based on the property task.";
            case "TN::blount-county" -> "Request Blount County SSDS approval and bedroom records, or use the separate inspection-letter path when a closing document is required.";
            case "TN::sumner-county" -> "Try the official TDEC septic search with a prepared property clue, then use Sumner County's published email and phone fallback when the search is blocked or incomplete.";
            case "TN::montgomery-county" -> "Search the TDEC SSDS and county route with the address, parcel, owner, or permit clue before requesting a Montgomery County file copy or inspection letter.";
            case "TN::sevier-county" -> "Prepare Sevier County's property fields, confirm the current Environmental Health intake channel by phone, and request the SSD permit, Certificate of Completion, or written no-record result.";
            case "TN::rutherford-county" -> "Find the Rutherford County TDEC septic map, permit, and approved bedroom count without treating a blocked or empty online search as a no-record result.";
            case "TN::bradley-county" -> "Search TDEC SSDS records for Bradley County, then use the Chattanooga field-office fallback for the permit, layout, closeout, repair file, or written response.";
            case "TN::sullivan-county" -> "Request a Sullivan County septic layout from TDEC using the address, subdivision, original owner, and previous owner, then retain the layout or written outcome.";
            case "TN::loudon-county" -> "Search the Loudon County septic file in TDEC, then use the county-published records email and Knoxville field-office fallback when the result is missing.";
            case "TN::maury-county" -> "Search TDEC SSDS records for Maury County, then use the Columbia field-office public-records fallback for the permit, layout, repair file, or written response.";
            case "TN::jefferson-county" -> "Use Jefferson County Environmental Health for the septic permit, final approval, inspection letter, existing-system evaluation, or verification record.";
            case "TN::davidson-county" -> "Search Metro Nashville's scanned Health Environmental Engineering files by parcel, then use the published phone or email fallback for a missing Davidson County septic record.";
            case "TN::madison-county" -> "Request a Madison County septic drawing with the current owner, known prior owners, full address, applicant details, and optional tax map or parcel number.";
            case "TN::shelby-county" -> "Prepare the Shelby County Water Quality application, plot plan, and soil analysis for septic installation, modification, repair, or abandonment before the site visit and permit.";
            case "TN::putnam-county" -> "Search TDEC SSDS records for Putnam County, then use the Cookeville field-office public-records fallback when the viewer is blocked or incomplete.";
            case "IN::monroe-county" -> "Follow Monroe County's OpenGov sequence from application and soil evaluation through Minimum Specs, approved site plan, and downloadable septic permit.";
            case "TN::anderson-county" -> "Search Anderson County SSDS records in TDEC, then use the verified Knoxville field-office route when the parcel file is blocked, missing, or incomplete.";
            case "NC::randolph-county" -> "Choose Randolph County's new, repair, expansion, or existing-system job and track the applicable IP, CA, Operation Permit, repair permit, or authorization in ePermits.";
            case "NC::buncombe-county" -> "Search Buncombe County well and septic records in Accela by address or parcel PIN, then use Environmental Health for an existing-system, inspection, new, or repair route.";
            case "NC::wake-county" -> "Match the Wake County parcel in iMAPS, open Permit Search, and download the scanned septic permit and attachments without treating a missing Septic box as no record.";
            case "NC::union-county" -> "Request the Union County septic permit file, then use the separate existing-system inspection path before additions, garages, decks, pools, or irrigation work.";
            case "NC::pitt-county" -> "Search Pitt County EnerGov records, then verify the Authorization to Construct, site plan, primary drainfield, repair area, and final approval with Environmental Health.";
            case "NC::pender-county" -> "Prepare Pender County's exact septic permit request fields and obtain the file status, permit type, layout, or written Environmental Health response.";
            case "AL::tuscaloosa-county" -> "Call Tuscaloosa County's Environmental Office with the address, parcel, owner, approximate year, and exact septic permit or record request.";
            case "AL::calhoun-county" -> "Use Calhoun County's direct Environmental Department route for an onsite sewage permit, historical file, inspection record, repair, or development request.";
            case "SC::charleston-county" -> "Find the Charleston County parcel ID or TMS, then request the SCDES Permit to Construct and final inspection through the Coastal records route.";
            case "SC::greenville-county" -> "Confirm the Greenville County map number, then request the SCDES Permit to Construct and final inspection through the Piedmont II route.";
            case "SC::anderson-county" -> "Confirm the Anderson County parcel in the assessor resources, then request the SCDES permit and final inspection through Piedmont II.";
            case "SC::spartanburg-county" -> "Confirm the Spartanburg County parcel in GIS, then request the SCDES permit and final inspection through the Piedmont I route.";
            case "TN::williamson-county" -> "Use Williamson County's dedicated inspection-record duplication request for the existing sewage-disposal file before entering electronic plan review for new work.";
            case "MD::st-marys-county" -> "Search the current St. Mary's County replacement GIS by address or Tax ID, then use the official Environmental Health request PDF when mapped records are missing or incomplete.";
            case "NY::suffolk-county" -> "Prepare the Tax Map number and construction details for a phone-assisted Suffolk County septic location record search and written fallback.";
            case "AZ::maricopa-county" -> "Run Maricopa County's free septic search first, then use the standard or expedited official research request when the online result is empty.";
            case "NC::brunswick-county" -> "Search Brunswick public permit metadata to identify the parcel and permit candidate, then request the original septic IP, CA, OP, or related file.";
            case "NC::forsyth-county" -> "Request the Forsyth County septic permit and soil evaluation, with separate release and repair routes for new property work.";
            case "TX::denton-county" -> "Confirm Denton County OSSF jurisdiction before requesting the existing license to operate, final approval, site plan, or maintenance record.";
            case "TX::brazoria-county" -> "Confirm the city, ETJ, or county OSSF authority before requesting Brazoria permit status, final approval, maintenance, or repair records.";
            case "WA::thurston-county" -> "Open the official Thurston County septic record route, carry the property address and parcel clues, and save the as-built, permit file, request reference, or written response.";
            case "NC::cumberland-county" -> "Use the official Cumberland County route for an existing septic permit or property file, then save the returned record, referral, or request reference.";
            case "GA::forsyth-county" -> "Open the official Forsyth County Environmental Health route, prepare the property identifiers it asks for, and track the septic permit record or office response.";
            case "CA::san-bernardino-county" -> "Find the responsible San Bernardino County septic record route, carry address and parcel clues, and preserve the permit file, referral, or request outcome.";
            case "MI::washtenaw-county" -> "Use the official Washtenaw County septic record path with the property address and available parcel clues, then save the file or written office response.";
            case "MT::gallatin-county" -> "Open the official Gallatin County septic record route, follow its current search or request fields, and keep the permit, layout, or documented response.";
            case "MD::frederick-county" -> "Use the official Frederick County septic record route, prepare the address and parcel information it requests, and retain the returned file or request reference.";
            case "GA::jackson-county" -> "Open the official Jackson County septic record path, follow only its published identifiers and submission steps, and record the resulting file or response.";
            case "NY::allegany-county" -> "Use the official Allegany County septic record route with the available property clues and preserve the permit file, referral, or written no-record response.";
            case "GA::hall-county" -> "Open the official Hall County septic permit record path, carry the property identifiers it asks for, and save the returned file or office outcome.";
            case "MD::harford-county" -> "Use the official Harford County septic record route, follow its current property-search or request steps, and retain the file or confirmation number.";
            case "MN::st-louis-county" -> "Open the official St. Louis County septic record route, prepare the property clues it publishes, and save the record, request reference, or written response.";
            case "MI::livingston-county" -> "Use the official Livingston County septic record path with the available address and parcel clues, then preserve the returned file or office response.";
            case "NJ::gloucester-county" -> "Open the official Gloucester County septic record route, follow the current search or request instructions, and track the resulting permit file or response.";
            case "VA::spotsylvania-county" -> "Use the official Spotsylvania County septic record path, carry the property identifiers it requests, and retain the permit, layout, referral, or written response.";
            case "MD::prince-georges-county" -> "Open the official Prince George's County septic record route, prepare the requested property clues, and save the returned record or request reference.";
            case "VA::hanover-county" -> "Use the official Hanover County septic record path, follow its current search or request fields, and preserve the permit file or documented response.";
            case "CA::san-diego-county" -> "Open the official San Diego County septic record route, follow the published property-search or request steps, and retain the file, referral, or response.";
            case "CA::tuolumne-county" -> "Use the official Tuolumne County septic record path with the property clues it requests and save the permit file, request reference, or written outcome.";
            case "NC::craven-county" -> "Open the official Craven County septic record route, prepare the address and parcel clues it publishes, and track the permit file or office response.";
            default -> "";
        };
        if (!priorityDescription.isBlank()) {
            return priorityDescription;
        }
        String originalLead = countyPage.countyName() + " septic records checklist and permit lookup";
        String searchLead = countyPage.countyName() + ", " + state.stateName() + " septic permit lookup and records request";
        if (countyPage.metaDescription() != null && countyPage.metaDescription().startsWith(originalLead)) {
            return searchLead + countyPage.metaDescription().substring(originalLead.length());
        }
        return searchLead + " path for address/parcel search, as-built files, inspection letters, and quote checks before pricing.";
    }

    public PageMeta basicPage(String title, String description, String path) {
        String canonicalUrl = absoluteUrl(path);
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb(title, canonicalUrl)
                ),
                List.of(
                        toJson(webPage(canonicalUrl, title, description, "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb(title, canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta workflowPacketPage(String title, String description, String path) {
        String canonicalUrl = absoluteUrl(path);
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "noindex,follow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb(title, canonicalUrl)
                ),
                List.of(
                        toJson(webPage(canonicalUrl, title, description, "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb(title, canonicalUrl)
                        )))
                )
        );
    }

    public List<String> staticPagePaths() {
        return Arrays.asList(
                "/states/",
                "/about/",
                "/editorial-standards/",
                "/methodology/",
                "/source-policy/",
                "/coverage/",
                "/privacy-policy/",
                "/terms-of-use/",
                "/contact/",
                "/septic-record-finder/",
                "/septic-records-access-index/",
                "/offer-prep-septic-file-check/",
                "/septic-bedroom-permit-checker/"
        );
    }

    public PageMeta notFound(String message) {
        return pageMeta(
                "Page Not Found | SepticPath",
                message,
                absoluteUrl("/404"),
                "noindex,nofollow",
                breadcrumbLinks(
                        crumb("Home", absoluteUrl("/")),
                        crumb("Not Found", absoluteUrl("/404"))
                ),
                List.of()
        );
    }

    public String absoluteUrl(String path) {
        String baseUrl = siteProperties.baseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        if ("/".equals(normalizedPath)) {
            return baseUrl + "/";
        }
        return baseUrl + normalizedPath;
    }

    private PageMeta pageMeta(String title, String description, String canonicalUrl, String robots, List<PageLink> breadcrumbs, List<String> jsonLdBlocks) {
        return new PageMeta(
                compactSeoTitle(title),
                compactSeoDescription(description),
                canonicalUrl,
                robots,
                absoluteUrl("/social-card.svg"),
                breadcrumbs,
                jsonLdBlocks
        );
    }

    private List<PageLink> breadcrumbLinks(Map<String, Object>... crumbs) {
        return Arrays.stream(crumbs)
                .map(crumb -> new PageLink((String) crumb.get("name"), relativePath((String) crumb.get("item")), ""))
                .toList();
    }

    private List<PageLink> breadcrumbLinks(List<Map<String, Object>> crumbs) {
        return crumbs.stream()
                .map(crumb -> new PageLink((String) crumb.get("name"), relativePath((String) crumb.get("item")), ""))
                .toList();
    }

    private String relativePath(String url) {
        if (url == null || url.isBlank()) {
            return "/";
        }
        String baseUrl = siteProperties.baseUrl();
        if (url.startsWith(baseUrl)) {
            String relativePath = url.substring(baseUrl.length());
            return relativePath.isBlank() ? "/" : relativePath;
        }
        return url;
    }

    private Map<String, Object> webSite(String url, String name, String description) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", "WebSite");
        payload.put("@id", absoluteUrl("/#website"));
        payload.put("name", name);
        payload.put("url", url);
        payload.put("description", description);
        payload.put("inLanguage", "en-US");
        return payload;
    }

    private Map<String, Object> webPage(String url, String name, String description, String type) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", type);
        payload.put("@id", url + "#webpage");
        payload.put("name", compactSeoTitle(name));
        payload.put("url", url);
        payload.put("description", compactSeoDescription(description));
        payload.put("inLanguage", "en-US");
        Map<String, Object> webSiteReference = new LinkedHashMap<>();
        webSiteReference.put("@type", "WebSite");
        webSiteReference.put("@id", absoluteUrl("/#website"));
        webSiteReference.put("name", "SepticPath");
        webSiteReference.put("url", absoluteUrl("/"));
        payload.put("isPartOf", webSiteReference);
        payload.put("publisher", editorialOrganizationReference());
        Map<String, Object> defaultSubject = new LinkedHashMap<>();
        defaultSubject.put("@type", "Thing");
        defaultSubject.put("name", compactSeoTitle(name));
        payload.put("about", List.of(defaultSubject));
        Map<String, Object> defaultMainEntity = new LinkedHashMap<>();
        defaultMainEntity.put("@type", "Thing");
        defaultMainEntity.put("@id", url + "#main-entity");
        defaultMainEntity.put("name", compactSeoTitle(name));
        defaultMainEntity.put("description", compactSeoDescription(description));
        payload.put("mainEntity", defaultMainEntity);
        return payload;
    }

    private Map<String, Object> recordsAccessDataset(
            String canonicalUrl,
            String csvUrl,
            String description,
            String dataLastUpdated,
            int countyRouteCount,
            int stateCount
    ) {
        Map<String, Object> dataset = new LinkedHashMap<>();
        dataset.put("@context", "https://schema.org");
        dataset.put("@type", "Dataset");
        dataset.put("@id", canonicalUrl + "#dataset");
        dataset.put("name", "Septic Records Access Index");
        dataset.put("description", description);
        dataset.put("url", canonicalUrl);
        dataset.put("inLanguage", "en-US");
        dataset.put("isAccessibleForFree", true);
        dataset.put("creator", editorialOrganizationReference());
        dataset.put("publisher", editorialOrganizationReference());
        if (isIsoDate(dataLastUpdated)) {
            dataset.put("dateModified", dataLastUpdated);
            dataset.put("version", dataLastUpdated);
        }
        dataset.put("spatialCoverage", Map.of(
                "@type", "Country",
                "name", "United States"
        ));
        dataset.put("keywords", List.of(
                "septic records",
                "septic permit lookup",
                "county environmental health",
                "septic as-built records",
                "onsite wastewater records"
        ));
        dataset.put("variableMeasured", List.of(
                "State",
                "County",
                "Records route type",
                "First artifact to request",
                "Route confidence score",
                "Official source count",
                "Last reviewed date",
                "Official records URL"
        ));
        dataset.put("measurementTechnique", "Editorial review of listed official government sources");
        dataset.put("abstract", "Includes " + countyRouteCount + " county routes across " + stateCount
                + " states, with official destination URLs and route-level review metadata.");
        dataset.put("distribution", List.of(Map.of(
                "@type", "DataDownload",
                "name", "Septic Records Access Index CSV",
                "contentUrl", csvUrl,
                "encodingFormat", "text/csv"
        )));
        return dataset;
    }

    private Map<String, Object> withEditorialMeta(
            Map<String, Object> payload,
            String lastReviewedAt,
            EditorialProfile preparedBy,
            EditorialProfile reviewedBy
    ) {
        payload.put("author", editorialContributorReference(preparedBy));
        payload.put("editor", editorialContributorReference(reviewedBy));
        payload.put("publisher", editorialOrganizationReference());
        if (isIsoDate(lastReviewedAt)) {
            payload.put("dateModified", lastReviewedAt);
        }
        return payload;
    }

    private Map<String, Object> withSemanticEvidence(
            Map<String, Object> payload,
            List<SourceRecord> sources,
            List<String> topics,
            String mainEntityName
    ) {
        List<Map<String, Object>> about = topics == null ? List.of() : topics.stream()
                .filter(this::hasText)
                .distinct()
                .map(topic -> {
                    Map<String, Object> subject = new LinkedHashMap<>();
                    subject.put("@type", "Thing");
                    subject.put("name", topic);
                    return subject;
                })
                .toList();
        if (!about.isEmpty()) {
            payload.put("about", about);
        }

        if (hasText(mainEntityName)) {
            Map<String, Object> mainEntity = new LinkedHashMap<>();
            mainEntity.put("@type", "Thing");
            mainEntity.put("@id", payload.get("url") + "#main-entity");
            mainEntity.put("name", mainEntityName);
            mainEntity.put("description", payload.get("description"));
            payload.put("mainEntity", mainEntity);
        }

        List<Map<String, Object>> citations = sources == null ? List.of() : sources.stream()
                .filter(source -> source != null && hasText(source.url()))
                .limit(8)
                .map(this::sourceCitation)
                .toList();
        if (!citations.isEmpty()) {
            payload.put("citation", citations);
        }
        return payload;
    }

    private Map<String, Object> sourceCitation(SourceRecord source) {
        Map<String, Object> citation = new LinkedHashMap<>();
        citation.put("@type", "CreativeWork");
        citation.put("name", firstNonBlank(source.title(), source.agencyName(), source.url()));
        citation.put("url", source.url());
        if (hasText(source.agencyName())) {
            Map<String, Object> publisher = new LinkedHashMap<>();
            publisher.put("@type", "Organization");
            publisher.put("name", source.agencyName());
            citation.put("publisher", publisher);
        }
        return citation;
    }

    private List<SourceRecord> resolveSources(List<String> sourceIds) {
        if (sourceIds == null) {
            return List.of();
        }
        return sourceIds.stream()
                .map(researchDataService::findSource)
                .flatMap(java.util.Optional::stream)
                .distinct()
                .toList();
    }

    private List<String> contentTopics(ContentPage contentPage) {
        List<String> topics = new ArrayList<>();
        topics.add(contentPage.primaryKeyword());
        if (contentPage.secondaryKeywords() != null) {
            topics.addAll(contentPage.secondaryKeywords().stream().limit(4).toList());
        }
        topics.add(contentPage.title());
        return topics;
    }

    private Map<String, Object> editorialOrganization() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", "Organization");
        payload.put("@id", absoluteUrl("/#organization"));
        payload.put("name", "SepticPath");
        payload.put("url", absoluteUrl("/"));
        payload.put("description", "Independent U.S. septic records routing and property-planning tool built from reviewed official sources.");
        payload.put("logo", Map.of(
                "@type", "ImageObject",
                "url", absoluteUrl("/favicon.svg")
        ));
        payload.put("publishingPrinciples", absoluteUrl("/editorial-standards/"));
        payload.put("contactPoint", Map.of(
                "@type", "ContactPoint",
                "contactType", "source corrections and privacy requests",
                "url", absoluteUrl("/contact/")
        ));
        return payload;
    }

    private Map<String, Object> editorialOrganizationReference() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@type", "Organization");
        payload.put("@id", absoluteUrl("/#organization"));
        payload.put("name", "SepticPath");
        payload.put("url", absoluteUrl("/"));
        return payload;
    }

    private Map<String, Object> editorialContributorReference(EditorialProfile profile) {
        if (profile == null) {
            return editorialOrganizationReference();
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@type", "Organization");
        payload.put("name", profile.displayName());
        payload.put("description", profile.roleTitle() + ". " + profile.focusSummary());
        payload.put("url", absoluteUrl("/about/"));
        return payload;
    }

    private Map<String, Object> faqPage(String url, String name, String description, List<FaqBlock> faqBlocks) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", "FAQPage");
        payload.put("name", name);
        payload.put("url", url);
        payload.put("description", description);
        payload.put("mainEntity", faqBlocks.stream().map(this::faqQuestion).toList());
        return payload;
    }

    private Map<String, Object> faqQuestion(FaqBlock faqBlock) {
        Map<String, Object> question = new LinkedHashMap<>();
        question.put("@type", "Question");
        question.put("name", faqBlock.question());
        question.put("acceptedAnswer", Map.of(
                "@type", "Answer",
                "text", faqBlock.answer()
        ));
        return question;
    }

    private Map<String, Object> breadcrumb(List<Map<String, Object>> items) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", "BreadcrumbList");
        payload.put("itemListElement", items);
        return payload;
    }

    private Map<String, Object> crumb(String name, String url) {
        Map<String, Object> crumb = new LinkedHashMap<>();
        crumb.put("@type", "ListItem");
        crumb.put("name", name);
        crumb.put("item", url);
        return crumb;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String compactSeoTitle(String value) {
        final String brandSuffix = " | SepticPath";
        final int maxLength = 68;
        if (value == null) {
            return value;
        }
        if (value.endsWith(brandSuffix)) {
            String unbrandedTitle = stripDanglingTitleEnding(
                    value.substring(0, value.length() - brandSuffix.length()).trim()
            );
            String brandedTitle = unbrandedTitle + brandSuffix;
            if (brandedTitle.length() <= maxLength) {
                return brandedTitle;
            }
            if (unbrandedTitle.length() <= maxLength) {
                return unbrandedTitle;
            }
            return cleanSeoTitleFragment(unbrandedTitle.substring(0, maxLength));
        }
        String cleanedTitle = stripDanglingTitleEnding(value);
        if (cleanedTitle.length() <= maxLength) {
            return cleanedTitle;
        }
        return cleanSeoTitleFragment(cleanedTitle.substring(0, maxLength));
    }

    private String cleanSeoTitleFragment(String value) {
        return stripDanglingTitleEnding(trimAtWordBoundary(value));
    }

    private String stripDanglingTitleEnding(String value) {
        return value
                .replaceFirst("[\\s,;:|/\\-]+$", "")
                .replaceFirst("(?i)\\s+(?:and|or|for|with|in|of|to|by|at|from|the)$", "");
    }

    private String compactSeoDescription(String value) {
        final int maxLength = 160;
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return trimAtWordBoundary(value.substring(0, maxLength - 3)) + "...";
    }

    private String trimAtWordBoundary(String value) {
        int boundary = value.lastIndexOf(' ');
        return boundary > value.length() / 2 ? value.substring(0, boundary).trim() : value.trim();
    }

    private boolean isIsoDate(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    private String stateGuideSeoTitle(StateProfile state) {
        return stateGuideTitle(state) + " | SepticPath";
    }

    private String contentPageSeoTitle(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Septic Replacement Cost | Quote Scope | SepticPath";
            case "perc-test-cost" -> "Perc Test Cost: $300-$3,000 Range and State Guide | SepticPath";
            case "drain-field-replacement-cost" -> "Drain Field Replacement Cost and Leach Field Replacement Price | SepticPath";
            case "failed-perc-test-septic" -> "Failed Perc Test for Septic | Soil & Redesign Risk | SepticPath";
            case "septic-replacement-area" -> "Septic Replacement Area Guide | Reserve & Field Risk | SepticPath";
            case "wet-yard-over-septic-drain-field" -> "Wet Yard Over Septic Drain Field | Failure Risk | SepticPath";
            case "septic-pumping-cost" -> "Septic Pumping Cost | Pumping cadence and maintenance risk | SepticPath";
            case "septic-inspection-cost" -> "Septic Inspection Cost | Buyer File Leverage | SepticPath";
            case "buying-a-house-with-a-septic-system" -> "Buying a House With a Septic System | Closing Risk | SepticPath";
            case "septic-permit-lookup" -> "Septic Permit Lookup by Address & County | Official Records | SepticPath";
            case "septic-permit-process" -> "Septic Permit Process by State | Office & Site Review | SepticPath";
            case "septic-records-checklist" -> "Septic Records Lookup by State & County | Permits & Official Files | SepticPath";
            case "septic-transfer-compliance" -> "Septic Transfer Compliance | Records, permits, and buyer workflow | SepticPath";
            case "how-to-find-septic-records-online" -> "How to Find Septic Records Online | County, Permit, and As-Built Search | SepticPath";
            case "septic-records-by-county" -> "Septic Records by County | Permit Lookup, As-Builts, and Health Files | SepticPath";
            case "septic-permit-search-by-address" -> "Septic Permit Search by Address | Find County Records | SepticPath";
            case "septic-permit-records-request" -> "Septic Permit Records Request | Copies & As-Builts | SepticPath";
            case "septic-records-request-builder" -> "Septic Records Request Builder | Download Permit Copy Request Packet | SepticPath";
            case "septic-as-built-records" -> "Septic As-Built Records | Layout & Permit Files | SepticPath";
            case "septic-tank-location-records" -> "Septic Tank Location Records | Find Tank & Drain Field Plans | SepticPath";
            case "septic-inspection-letter" -> "Septic Inspection Letter | Closing & Permit Checks | SepticPath";
            case "official-septic-lookup-tools" -> "Official Septic Lookup Tools | TDEC, DHEC, OSSF, OSTDS, and County Records | SepticPath";
            case "tdec-septic-records" -> "TDEC Septic Records Search: SSDS Permit Lookup & 403 Help | SepticPath";
            case "north-carolina-septic-permit-lookup" -> "How to Find NC Septic Permits: County Search & As-Builts | SepticPath";
            case "texas-ossf-records-search" -> "Texas OSSF Records Search & County File Routing | SepticPath";
            case "florida-ostds-permit-lookup" -> "Florida OSTDS Permit Lookup | Septic Records and County DOH Files | SepticPath";
            case "dhec-septic-permit-lookup" -> "DHEC Septic Tank Records & Permit Lookup | SCDES Files | SepticPath";
            case "septic-system-cost-calculator" -> "Septic Cost Calculator | Use after records, permits, and file checks | SepticPath";
            case "septic-tank-size" -> "Septic Tank Size Guide | Bedroom count, gallons, and sizing risk | SepticPath";
            default -> contentPage.title() + " | SepticPath";
        };
    }

    private String stateMoneyPageSeoTitle(StateMoneyPage stateMoneyPage, StateProfile state) {
        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug())) {
            return switch (state.stateCode()) {
                case "TN" -> "Tennessee Septic Records by County | Permit Files | SepticPath";
                case "NC" -> "North Carolina Septic Records: County Permit & As-Built Lookup | SepticPath";
                case "IN" -> "Indiana Septic Records Lookup & County Permit Search | SepticPath";
                case "SC" -> "South Carolina Septic Records & SCDES Permit Lookup | SepticPath";
                case "TX" -> "Texas OSSF Records & County Address Search | SepticPath";
                case "AL" -> "Alabama Septic Permit Lookup | County Health Records, Perc Files, and Address Search | SepticPath";
                default -> stateMoneyPage.title() + " | SepticPath";
            };
        }
        if ("perc-test-cost".equals(stateMoneyPage.contentSlug()) && "WV".equals(state.stateCode())) {
            return "How Much Does a Perc Test Cost in West Virginia? | SepticPath";
        }
        return stateMoneyPage.title() + switch (stateMoneyPage.contentSlug()) {
            case "septic-replacement-cost" -> " | Quote Scope | SepticPath";
            case "perc-test-cost" -> " | Soil & Permit Risk | SepticPath";
            case "failed-perc-test-septic" -> " | Soil & Redesign Risk | SepticPath";
            case "septic-replacement-area" -> " | Reserve & Field Risk | SepticPath";
            case "wet-yard-over-septic-drain-field" -> " | Failure Risk | SepticPath";
            case "buying-a-house-with-a-septic-system" -> " | Closing Risk | SepticPath";
            case "septic-records-checklist" -> " | SepticPath";
            case "septic-permit-process" -> " | Approval Steps | SepticPath";
            case "septic-inspection-cost" -> " | Buyer File Leverage | SepticPath";
            case "septic-pumping-cost" -> " | Maintenance Cadence | SepticPath";
            case "drain-field-replacement-cost" -> " | Field layout and replacement risk | SepticPath";
            default -> " | SepticPath";
        };
    }

    private String stateMoneyPageDescription(StateMoneyPage stateMoneyPage, StateProfile state) {
        if ("perc-test-cost".equals(stateMoneyPage.contentSlug()) && "WV".equals(state.stateCode())) {
            return "See the $300-$3,000 national perc-test planning range, then narrow West Virginia cost by local health department, site review, permit stage, and quote scope.";
        }
        if (!"septic-records-checklist".equals(stateMoneyPage.contentSlug())) {
            return stateMoneyPage.metaDescription();
        }
        return switch (state.stateCode()) {
            case "TN" -> "Find Tennessee septic records by county. Get permit copies, inspection letters, repair history, or no-record responses through TDEC or a contract county.";
            case "NC" -> "Find North Carolina septic permits, as-builts, final approvals, repair files, and no-record responses by address or county environmental health route.";
            case "IN" -> "Find Indiana septic records through county permit search, local health files, as-builts, soil reports, and the right next office when a record is missing.";
            case "SC" -> "Find South Carolina septic records through SCDES permit lookup, D-1740 files, ePermitting, county contacts, permit copies, and no-record fallback.";
            case "TX" -> "Find Texas OSSF records through county or authorized-agent routes, permit lookup, approved plans, address or parcel search, ETJ checks, and records request wording.";
            case "AL" -> "Find Alabama septic permit records through county health departments, perc or soil files, Permit to Install, Approval for Use, address search, and records request wording.";
            default -> stateMoneyPage.metaDescription();
        };
    }

    private boolean shouldExposeFaqStructuredData(StateMoneyPage stateMoneyPage) {
        return switch (stateMoneyPage.contentSlug()) {
            // These pages now behave more like workflow routers than FAQ-first articles.
            case "septic-records-checklist",
                 "septic-permit-process",
                 "buying-a-house-with-a-septic-system" -> false;
            default -> true;
        };
    }

    private String stateGuideTitle(StateProfile state) {
        return switch (state.stateCode()) {
            case "IA" -> "Iowa Septic Cost Guide and County Records Path";
            case "KS" -> "Kansas Septic Cost Guide and Soil-Profile Path";
            case "NE" -> "Nebraska Septic Cost Guide and Permit Path";
            case "NM" -> "New Mexico Septic Cost Guide and Buyer File Path";
            case "UT" -> "Utah Septic Cost Guide and Local Health Permit Path";
            case "WV" -> "West Virginia Septic Permit Cost, Sewage Permit File, and Local Health Guide";
            case "SD" -> "South Dakota Septic Cost Guide and Permit Path";
            case "ID" -> "Idaho Septic Cost Guide and Site Approval Path";
            case "NV" -> "Nevada Septic Cost Guide and Buyer File Path";
            case "DE" -> "Delaware Septic Cost Guide and Permit Path";
            case "ND" -> "North Dakota Septic Cost Guide and Local Permit Path";
            case "WY" -> "Wyoming Septic Cost Guide and Site-Risk Path";
            case "AK" -> "Alaska Septic Cost Guide and Buyer File Path";
            case "HI" -> "Hawaii Septic Cost Guide and Cesspool Upgrade Path";
            case "ME" -> "Maine Septic Cost Guide and HHE-200 File Path";
            case "NH" -> "New Hampshire Septic Cost Guide and Approval Status Path";
            case "RI" -> "Rhode Island Septic Permit Cost, DEM File Search, and Suitability Guide";
            case "VT" -> "Vermont Septic Cost Guide and WW Permit Path";
            case "MT" -> "Montana Septic Cost Guide and Site-Risk Path";
            case "AL" -> "Alabama Perc Test Cost: $300-$2,700 and County Fees";
            case "AR" -> "Arkansas Septic Cost Guide and County Permit Path";
            case "MS" -> "Mississippi Septic Cost Guide and Public Records Path";
            case "IN" -> "Indiana Septic Cost Guide and County Permit Path";
            case "OK" -> "Oklahoma Septic Cost Guide and Soil-Test Path";
            case "KY" -> "Kentucky Septic Cost Guide and Local File Path";
            case "MN" -> "Minnesota Septic Cost Guide and Property Transfer Risk";
            case "IL" -> "Illinois Septic Cost Guide and Local Health File Path";
            case "MD" -> "Maryland Septic Cost Guide and Property Transfer Risk";
            case "WI" -> "Wisconsin Septic Cost Guide and POWTS Inspection Path";
            case "LA" -> "Louisiana Septic Cost Guide and Parish Site-Risk Path";
            case "AZ" -> "Arizona Septic Cost Guide and Site Approval Path";
            case "CO" -> "Colorado Septic Cost Guide and Local OWTS Permit Path";
            case "VA" -> "Virginia Septic Cost Guide and Inspection Obligations";
            case "TN" -> "Tennessee Septic Cost Guide and Permit File Path";
            case "SC" -> "South Carolina Septic Permit Cost, Permit Copy, and D-1740 Guide";
            case "CA" -> "California Septic Cost Guide and County Permit Path";
            case "TX" -> "Texas Septic Cost Guide and Local OSSF Permit Path";
            case "NY" -> "New York Septic Cost Guide and Appendix 75-A Rules";
            case "OH" -> "Ohio Septic Cost Guide and Local Health Permit Path";
            case "MI" -> "Michigan Septic Cost Guide and Local Health Records Path";
            case "GA" -> "Georgia Septic Permit Cost, Permit Records, and Soil Analysis Guide";
            case "PA" -> "Pennsylvania Septic Cost Guide and SEO Permit Path";
            case "CT" -> "Connecticut Septic Cost Guide and Design Flow Rules";
            case "OR" -> "Oregon Septic Cost Guide and Site Evaluation Path";
            case "MA" -> "Massachusetts Septic Cost Guide and Title 5 Rules";
            case "FL" -> "Florida Septic Cost Guide, DEP Counties, and Permit Path";
            case "WA" -> "Washington Septic Cost Guide and Inspection Rules";
            case "NJ" -> "New Jersey Septic Cost Guide and Management Rules";
            case "NC" -> "North Carolina Septic Cost Guide and Permit Steps";
            case "MO" -> "Missouri Septic Cost Guide and Local Permit Path";
            default -> state.stateName() + " Septic Cost Guide";
        };
    }

    private String stateGuideDescription(StateProfile state) {
        return switch (state.stateCode()) {
            case "IA" -> "Iowa septic planning estimates with county-sanitarian routing, time-of-transfer context, and official-source links.";
            case "KS" -> "Kansas septic planning estimates with soil-profile requirements, local sanitary-code variation, and official-source links.";
            case "NE" -> "Nebraska septic planning estimates with DHHS permit filing, registered-system context, and official-source links.";
            case "NM" -> "New Mexico septic planning estimates with buyer-file checks, permit-search context, and official-source links.";
            case "UT" -> "Utah septic planning estimates with local health routing, R317-4 permit workflow, and official-source links.";
            case "WV" -> "West Virginia septic permit cost guide with local health routing, sewage-permit file checks, sanitarian-record context, public-record paths, and official OEHS links.";
            case "SD" -> "South Dakota septic planning estimates with permit-certificate context, inspection sequencing, and official-source links.";
            case "ID" -> "Idaho septic planning estimates with district-health routing, site-evaluation context, and official-source links.";
            case "NV" -> "Nevada septic planning estimates with local-authority split, buyer-file diligence, inspection-plus-as-built context, and official-source links.";
            case "DE" -> "Delaware septic planning estimates with DNREC permit routing, report-lookup context, county handoff, and official-source links.";
            case "ND" -> "North Dakota septic planning estimates with local public health routing, permit-file visibility, and official-source links.";
            case "WY" -> "Wyoming septic planning estimates with delegated-county routing, site-suitability context, and official-source links.";
            case "AK" -> "Alaska septic planning estimates with approved-system record pulls, local-office routing, difficult-site risk, and official-source links.";
            case "HI" -> "Hawaii septic planning estimates with cesspool-upgrade triggers, county building-permit handoff, approval-to-use timing, and official-source links.";
            case "ME" -> "Maine septic planning estimates with HHE-200 file pulls, town-office routing, Local Plumbing Inspector context, and official-source links.";
            case "NH" -> "New Hampshire septic planning estimates with approval-status checks, OneStop records, local verification, and official-source links.";
            case "RI" -> "Rhode Island septic permit cost guide with DEM permit searches, 1968-forward file retrieval, suitability checks, advanced-technology risk, and official DEM links.";
            case "VT" -> "Vermont septic planning estimates with permit-search context, town checks, five regional offices, and official-source links.";
            case "MT" -> "Montana septic planning estimates with COSA checks, local-health routing, DEQ-4 site-risk context, and official-source links.";
            case "AL" -> "Alabama perc test cost is typically planned at $300-$2,700. Compare separate ADPH county site-evaluation and permit fees, soil scope, and official next steps.";
            case "AR" -> "Arkansas septic planning estimates with county health routing, permit-copy context, and official-source links.";
            case "MS" -> "Mississippi septic planning estimates with county health routing, public-record context, and official-source links.";
            case "IN" -> "Indiana septic planning estimates with county permit routing, sewer-availability context, and official-source links.";
            case "OK" -> "Oklahoma septic planning estimates with local DEQ routing, soil-test context, and official-source links.";
            case "KY" -> "Kentucky septic planning estimates with local health routing, site-evaluation files, and official-source links.";
            case "MN" -> "Minnesota septic planning estimates with local SSTS transfer rules, seller-disclosure context, and official-source links.";
            case "IL" -> "Illinois septic planning estimates with local health file routing, evaluation-form context, and official-source links.";
            case "MD" -> "Maryland septic planning estimates with local approving authority routing, PTI transfer context, and official-source links.";
            case "WI" -> "Wisconsin septic planning estimates with county POWTS routing, maintenance-tracking context, and official-source links.";
            case "LA" -> "Louisiana septic planning estimates with parish health routing, sewer-availability context, and official-source links.";
            case "AZ" -> "Arizona septic planning estimates with county delegation, site-investigation paperwork, and official ADEQ source links.";
            case "CO" -> "Colorado septic planning estimates with local public health routing, site-and-soil paperwork, and official-source links.";
            case "VA" -> "Virginia septic planning estimates with local health district routing, operation-permit context, and inspection obligations.";
            case "TN" -> "Tennessee septic planning estimates with permit-file retrieval, repair-permit context, and official-source links.";
            case "SC" -> "South Carolina septic permit cost guide with D-1740 filing, permit-copy retrieval, local office routing, final-inspection steps, and official SCDES links.";
            case "CA" -> "California septic planning estimates with local agency routing, OWTS policy context, and county permit-file questions.";
            case "TX" -> "Texas septic planning estimates with local permitting authority routing, site-evaluation context, and official OSSF sources.";
            case "NY" -> "New York septic planning estimates with Appendix 75-A rules, county health workflow, and official-source links.";
            case "OH" -> "Ohio septic planning estimates with local health department routing, Chapter 3701-29 permit context, and official-source links.";
            case "MI" -> "Michigan septic planning estimates with local health department routing, file-retrieval context, and official-source links.";
            case "GA" -> "Georgia septic permit cost guide with county office lookups, permit records, soil analysis steps, garbage-disposal sizing risk, and official DPH links.";
            case "PA" -> "Pennsylvania septic planning estimates with Sewage Enforcement Officer workflow, local permit context, and official-source links.";
            case "CT" -> "Connecticut septic planning estimates with design flow, potential-bedroom risk, local health review, and official-source links.";
            case "OR" -> "Oregon septic planning estimates with site evaluation, permit sequencing, and official-source links.";
            case "MA" -> "Massachusetts septic planning estimates with Title 5 timing, buyer risk, and local Board of Health context.";
            case "FL" -> "Florida septic planning estimates with DEP-versus-county routing, inspection context, and official-source links.";
            case "WA" -> "Washington septic planning estimates with local health jurisdiction rules, inspection cadence, and official-source links.";
            case "NJ" -> "New Jersey septic planning estimates with management-program risk, Pinelands context, and official-source links.";
            case "NC" -> "North Carolina septic planning estimates with permit-ladder context, county health workflow, and official-source links.";
            case "MO" -> "Missouri septic planning estimates with local authority routing, permit path, and official-source links.";
            default -> "Planning estimates, permit context, and official sources for " + state.stateName() + " homeowners.";
        };
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String toJson(Map<String, Object> payload) {
        if ("BreadcrumbList".equals(payload.get("@type"))) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) payload.get("itemListElement");
            for (int index = 0; index < itemList.size(); index++) {
                itemList.get(index).put("position", index + 1);
            }
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize structured data", exception);
        }
    }
}
