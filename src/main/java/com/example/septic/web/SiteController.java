package com.example.septic.web;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.data.model.CountyWorkflowStructureData;
import com.example.septic.data.model.ProjectCostAnchor;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateCostProfile;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import com.example.septic.data.model.StateQueuePlan;
import com.example.septic.service.AccessDifficulty;
import com.example.septic.service.DrainfieldEstimatorResult;
import com.example.septic.service.DrainfieldEstimatorService;
import com.example.septic.service.EstimatorResult;
import com.example.septic.service.EstimatorService;
import com.example.septic.service.LeadStorageService;
import com.example.septic.service.ProjectType;
import com.example.septic.service.ResearchDataService;
import com.example.septic.service.SeoService;
import com.example.septic.service.SitemapService;
import com.example.septic.service.SoilPercStatus;
import com.example.septic.service.StateQueuePlanService;
import com.example.septic.service.TankSizeEstimatorResult;
import com.example.septic.service.TankSizeEstimatorService;
import com.example.septic.service.TimelinePreference;
import com.example.septic.service.PumpScheduleResult;
import com.example.septic.service.PumpScheduleService;
import com.example.septic.service.OccupancyProfile;
import com.example.septic.service.PublishingPolicyService;
import com.example.septic.service.UsStateDirectoryService;
import com.example.septic.service.UsageProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class SiteController {
    private static final List<String> CORE_STATE_CODES = List.of("GA", "PA", "CT", "OR", "MA", "FL");
    private static final List<String> ORGANIC_SPRINT_STATE_CODES = List.of("TN", "NC", "TX", "AL", "IN", "GA");
    private static final String PERMIT_LOOKUP_SLUG = "septic-permit-lookup";
    private static final String TRANSFER_COMPLIANCE_SLUG = "septic-transfer-compliance";
    private static final List<String> PERMIT_LOOKUP_STATE_SLUGS = List.of(
            "septic-records-checklist",
            "septic-permit-process"
    );
    private static final List<String> TRANSFER_COMPLIANCE_STATE_SLUGS = List.of(
            "septic-records-checklist",
            "septic-permit-process",
            "buying-a-house-with-a-septic-system"
    );
    private static final String INDIANA_RECORDS_PACKET_PATH = "/for-professionals/records-packet/indiana/";
    private static final String NEW_YORK_BUYER_PACKET_PATH = "/for-professionals/buyer-diligence-packet/new-york/";
    private static final String SOUTH_CAROLINA_PERMIT_PACKET_PATH = "/for-professionals/permit-prep-packet/south-carolina/";
    private static final String STATE_EDITORIAL_NOTE = "This page is maintained as conservative homeowner guidance and updated when linked official materials or local workflow notes change.";
    private static final String CONTENT_EDITORIAL_NOTE = "This page is a planning hub. Use the linked state-specific pages when rule style, local authority, or records workflow differences matter.";
    private static final EditorialProfile STATE_PAGE_PREPARER = new EditorialProfile(
            "Homeowner Planning Desk",
            "Planning editor",
            "Turns state rules, permit friction, and buyer-risk signals into estimate-first homeowner guidance."
    );
    private static final EditorialProfile CONTENT_PAGE_PREPARER = new EditorialProfile(
            "Intent Map Desk",
            "Content editor",
            "Keeps national pages aligned with the estimator, state guides, and the highest-intent next steps."
    );
    private static final EditorialProfile SOURCE_REVIEWER = new EditorialProfile(
            "State Source Review Desk",
            "Source reviewer",
            "Checks official links, verification dates, and local workflow notes before a page stays public."
    );

    private final ResearchDataService researchDataService;
    private final EstimatorService estimatorService;
    private final LeadStorageService leadStorageService;
    private final SeoService seoService;
    private final SitemapService sitemapService;
    private final DrainfieldEstimatorService drainfieldEstimatorService;
    private final TankSizeEstimatorService tankSizeEstimatorService;
    private final PumpScheduleService pumpScheduleService;
    private final StateQueuePlanService stateQueuePlanService;
    private final UsStateDirectoryService usStateDirectoryService;
    private final PublishingPolicyService publishingPolicyService;

    public SiteController(
            ResearchDataService researchDataService,
            EstimatorService estimatorService,
            LeadStorageService leadStorageService,
            SeoService seoService,
            SitemapService sitemapService,
            DrainfieldEstimatorService drainfieldEstimatorService,
            TankSizeEstimatorService tankSizeEstimatorService,
            PumpScheduleService pumpScheduleService,
            StateQueuePlanService stateQueuePlanService,
            UsStateDirectoryService usStateDirectoryService,
            PublishingPolicyService publishingPolicyService
    ) {
        this.researchDataService = researchDataService;
        this.estimatorService = estimatorService;
        this.leadStorageService = leadStorageService;
        this.seoService = seoService;
        this.sitemapService = sitemapService;
        this.drainfieldEstimatorService = drainfieldEstimatorService;
        this.tankSizeEstimatorService = tankSizeEstimatorService;
        this.pumpScheduleService = pumpScheduleService;
        this.stateQueuePlanService = stateQueuePlanService;
        this.usStateDirectoryService = usStateDirectoryService;
        this.publishingPolicyService = publishingPolicyService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", seoService.homePage());
        List<StateProfile> publicStates = researchDataService.getPublicStateProfiles();
        WorkflowNetworkSnapshotView workflowNetworkSnapshot = workflowNetworkSnapshot();
        model.addAttribute("featuredStates", publicStates.stream()
                .filter(state -> "anchor".equalsIgnoreCase(state.launchTier()))
                .toList());
        model.addAttribute("spotlightStates", publicStates.stream()
                .sorted(Comparator
                        .comparing((StateProfile state) -> !"anchor".equalsIgnoreCase(state.launchTier()))
                        .thenComparing(StateProfile::stateName))
                .limit(6)
                .toList());
        model.addAttribute("featuredIntentPages", homeGrowthSpotlights());
        model.addAttribute("liveGuideCount", publicStates.size());
        model.addAttribute("liveIntentCount", researchDataService.getPublicStateMoneyPages().size());
        model.addAttribute("liveCountyCount", workflowNetworkSnapshot.liveCountyCount());
        model.addAttribute("countyBackedStateCount", workflowNetworkSnapshot.countyBackedStateCount());
        model.addAttribute("workflowNetworkSnapshot", workflowNetworkSnapshot);
        model.addAttribute("queuedStateCount", Math.max(usStateDirectoryService.allStates().size() - publicStates.size(), 0));
        return "pages/home";
    }

    @GetMapping({"/states", "/states/"})
    public String stateCoverage(Model model) {
        List<StateCoverageCardView> coverageCards = buildStateCoverageCards();
        WorkflowNetworkSnapshotView workflowNetworkSnapshot = workflowNetworkSnapshot();
        model.addAttribute("page", seoService.stateCoveragePage());
        model.addAttribute("liveStates", coverageCards.stream()
                .filter(StateCoverageCardView::published)
                .toList());
        model.addAttribute("priorityQueuePlans", stateQueuePlanService.topPlans(10).stream()
                .filter(plan -> researchDataService.findStateByCode(plan.stateCode())
                        .filter(StateProfile::isPublished)
                        .isEmpty())
                .map(this::stateQueuePlanView)
                .toList());
        model.addAttribute("featuredIntentPages", coverageGrowthSpotlights());
        model.addAttribute("queuedStates", coverageCards.stream()
                .filter(card -> !card.published())
                .toList());
        model.addAttribute("liveGuideCount", researchDataService.getPublicStateProfiles().size());
        model.addAttribute("liveIntentCount", researchDataService.getPublicStateMoneyPages().size());
        model.addAttribute("liveCountyCount", workflowNetworkSnapshot.liveCountyCount());
        model.addAttribute("countyBackedStateCount", workflowNetworkSnapshot.countyBackedStateCount());
        model.addAttribute("workflowNetworkSnapshot", workflowNetworkSnapshot);
        model.addAttribute("queuedStateCount", Math.max(usStateDirectoryService.allStates().size() - researchDataService.getPublicStateProfiles().size(), 0));
        return "pages/state-coverage";
    }

    @GetMapping({"/about", "/about/"})
    public String about(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "About SepticPath",
                        "Why this estimator exists, how it uses official sources, and what it is designed to do.",
                        "/about/"
                ),
                "About this project",
                "Built for homeowner planning, not engineered outputs.",
                "This site exists to give homeowners and homebuyers a faster starting point for septic budgeting, likely system class, and the next practical questions to ask before they request quotes.",
                Arrays.asList(
                        new SitePageSection(
                                "What this site is for",
                                "The goal is to reduce permit anxiety and cost uncertainty without pretending the result is permit-ready.",
                                List.of(
                                        "Planning ranges for septic cost, likely tank size, and likely system class.",
                                        "State-aware pages with official-source links, agency attribution, and last verified dates.",
                                        "Short quote-request flow after the user has already seen value."
                                )
                        ),
                        new SitePageSection(
                                "What this site is not",
                                "This product is intentionally conservative where inputs are weak or local conditions are unknown.",
                                List.of(
                                        "Not engineering design software.",
                                        "Not permit-ready calculation software.",
                                        "Not a code-compliance certification tool."
                                )
                        ),
                        new SitePageSection(
                                "How data is handled",
                                "Research data is versioned in files and reviewed before it becomes publishable guidance. Runtime leads and events are stored separately for auditability and export.",
                                List.of(
                                        "Official sources are preferred for rules and permit process context.",
                                        "Commercial sources are used only for broad public cost anchors.",
                                        "Where rules are unclear, the estimate widens instead of inventing certainty."
                                )
                        )
                ),
                "Estimate-first and source-transparent",
                "Every result should be read as a planning estimate that still needs local verification."
        );
    }

    @GetMapping({"/privacy-policy", "/privacy-policy/"})
    public String privacyPolicy(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "Privacy Policy",
                        "What information this site collects, why it is stored, and how lead requests are handled.",
                        "/privacy-policy/"
                ),
                "Privacy policy",
                "What this site collects and why.",
                "This page describes the current handling of form submissions and site interaction data for SepticPath. It is an operational policy page, not legal advice.",
                Arrays.asList(
                        new SitePageSection(
                                "Information collected",
                                "When you request quotes, the site stores the contact and project details needed to route a homeowner inquiry and preserve source provenance.",
                                List.of(
                                        "Contact details such as name, email, phone number, and ZIP code.",
                                        "Project inputs such as state, project type, bedroom count, and site-condition answers.",
                                        "Technical request data such as timestamp, referring page, user agent, and remote address."
                                )
                        ),
                        new SitePageSection(
                                "Why it is stored",
                                "Lead submissions are stored so the estimate can be tied to the original consent and to support later routing to service partners.",
                                List.of(
                                        "To keep an auditable record of consent language and submission time.",
                                        "To export normalized lead records for approved partner workflows.",
                                        "To understand which pages and estimate flows create useful homeowner leads."
                                )
                        ),
                        new SitePageSection(
                                "Operational limits",
                                "This site stores submissions for routing, audit logging, and export operations. Storage and routing practices may evolve as coverage and partner workflows change.",
                                List.of(
                                        "Do not submit payment-card, bank-account, government-ID, or other highly sensitive personal information through the forms.",
                                        "Do not treat a quote request as a guarantee that a contractor will contact you or accept the project.",
                                        "Material changes to storage, export, or routing practices should be reflected in this policy page."
                                )
                        )
                ),
                "Consent matters here",
                "Quote requests are tied to a stored consent snapshot and timestamp so the lead record remains attributable."
        );
    }

    @GetMapping({"/editorial-standards", "/editorial-standards/"})
    public String editorialStandards(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "Editorial Standards",
                        "How SepticPath builds, reviews, updates, and limits source-backed septic planning guidance.",
                        "/editorial-standards/"
                ),
                "Editorial standards",
                "How this site is reviewed, updated, and kept inside planning-tool boundaries.",
                "This page explains how SepticPath turns public rules, file paths, and official-source context into homeowner-facing planning guidance without pretending to be permit software or engineering design.",
                Arrays.asList(
                        new SitePageSection(
                                "What we prefer as evidence",
                                "Official state, county, district, or delegated authority sources come first whenever they are available and readable enough to support a homeowner-facing explanation.",
                                List.of(
                                        "Rules, forms, manuals, and official local office directories are preferred for workflow claims.",
                                        "Public cost anchors are used only as broad planning context, not as permit truth.",
                                        "When sources conflict or stay vague, the page should widen uncertainty instead of inventing precision."
                                )
                        ),
                        new SitePageSection(
                                "How pages are reviewed",
                                "Published pages are expected to carry source references, reviewed-against copy, and a last-reviewed date tied to the workflow described on the page.",
                                List.of(
                                        "State guides are reviewed against official state-level or delegated local sources.",
                                        "State intent pages are reviewed against the sources tied to that exact workflow and state.",
                                        "FAQ, CTA, and internal links are written to move the user toward the next real file, office, or estimate step."
                                )
                        ),
                        new SitePageSection(
                                "What we deliberately avoid",
                                "The product is intentionally conservative where local file context, site conditions, or delegated authority rules could change the answer.",
                                List.of(
                                        "We do not present outputs as engineered design recommendations.",
                                        "We do not present outputs as permit approval or code-compliance decisions.",
                                        "We do not narrow cost or scope ranges aggressively when source coverage is weak."
                                )
                        )
                ),
                "Trust the source trail",
                "Use the pages as planning guidance, then confirm the local file, reviewing office, and site conditions before relying on a quote or design decision."
        );
    }

    @GetMapping({"/terms-of-use", "/terms-of-use/"})
    public String termsOfUse(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "Terms of Use",
                        "The core use conditions for this estimate-only septic planning website.",
                        "/terms-of-use/"
                ),
                "Terms of use",
                "Use this site as a planning tool, not as engineering or legal approval.",
                "These terms describe the intended use of the public estimator and related content. They set the operating boundaries for a planning tool, not a permit or compliance service.",
                Arrays.asList(
                        new SitePageSection(
                                "Estimate-only use",
                                "Results are planning estimates designed to help users ask better questions before speaking with local septic professionals.",
                                List.of(
                                        "Outputs are not engineered designs.",
                                        "Outputs are not code-compliance determinations.",
                                        "Outputs are not permit approvals or official state calculations."
                                )
                        ),
                        new SitePageSection(
                                "User responsibility",
                                "Users remain responsible for confirming local permit rules, system feasibility, and contractor qualifications.",
                                List.of(
                                        "County and local authorities may override state-level general guidance.",
                                        "Actual cost depends on site evaluation, system type, access, and local scope.",
                                        "Homebuyers should still request system records and inspection evidence before closing."
                                )
                        ),
                        new SitePageSection(
                                "Commercial use and availability",
                                "The site may evolve, change coverage, or stop accepting quote requests without notice if source coverage, partner availability, or product scope changes.",
                                List.of(
                                        "Content may be updated when sources change or pages are re-verified.",
                                        "Quote matching is not guaranteed in every state or project category.",
                                        "No warranty is made that any estimate range will match a final contractor proposal."
                                )
                        )
                ),
                "Trust the workflow, not fake precision",
                "Use the estimate to narrow the likely range, then verify locally and collect real quotes."
        );
    }

    @GetMapping({"/contact", "/contact/"})
    public String contact(Model model) {
        return renderContactPage(model, new ContactRequestForm(), false, null);
    }

    @PostMapping({"/contact", "/contact/"})
    public String submitContactRequest(
            @Valid @ModelAttribute ContactRequestForm contactRequestForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return renderContactPage(model, contactRequestForm, true, null);
        }

        String requestId = leadStorageService.saveContactRequest(contactRequestForm, "/contact/", request);
        return renderContactPage(model, new ContactRequestForm(), false, requestId);
    }

    @GetMapping({"/for-professionals/records-packet/indiana", "/for-professionals/records-packet/indiana/"})
    public String indianaRecordsPacket(Model model) {
        StateProfile state = researchDataService.findPublicStateBySlug("indiana")
                .orElseThrow(() -> new StateNotFoundException("indiana"));
        StateMoneyPage recordsPage = researchDataService.findPublicStateMoneyPage("septic-records-checklist", "indiana")
                .orElseThrow(() -> new StateNotFoundException("septic-records-checklist/indiana"));
        StateMoneyPage buyerPage = researchDataService.findPublicStateMoneyPage("buying-a-house-with-a-septic-system", "indiana")
                .orElseThrow(() -> new StateNotFoundException("buying-a-house-with-a-septic-system/indiana"));
        String packetUrl = seoService.absoluteUrl(INDIANA_RECORDS_PACKET_PATH);
        List<PageLink> countyLinks = countyRecordPageLinks(state.stateCode()).stream().limit(4).toList();
        WorkflowPacketView packet = new WorkflowPacketView(
                "Professional workflow packet",
                "Indiana septic records packet for buyer agents and coordinators",
                "Use this share page when the real blocker is the county file, sewer-availability note, or local board history behind an Indiana septic story. The first send should narrow into the records workflow, not a broad cost page.",
                "Records packet",
                "Public noindex handoff for Indiana file checks",
                "This packet is meant to shorten the first buyer or seller explanation when the next real move is county records, existing permits, or local-board confirmation.",
                "The packet should move the recipient into the Indiana records lookup first, then into the county page or official file source that matches the parcel.",
                "Indiana septic records and county file check",
                """
Hi,

Before we rely on the current septic story, start with this Indiana records packet:
%s

Open the Indiana records lookup first. If the parcel is already clearly tied to a county health office, use one of the linked county pages right after that.

The goal is to confirm the file, the local office, and any sewer-availability note before we treat a quote or seller summary as the real answer.
""".formatted(packetUrl),
                new PageLink(
                        recordsPage.title(),
                        recordsPage.path(state.slug()),
                        "Start with the Indiana records lookup before you price the downside. That page is the pinned first move because Indiana's county-first file path changes the next action faster than a broad guide."
                ),
                List.of(
                        new PageLink(
                                buyerPage.title(),
                                buyerPage.path(state.slug()),
                                "Use the Indiana buyer workflow after the file path is clearer and the next question shifts from record retrieval to deal risk."
                        ),
                        new PageLink(
                                "Indiana Septic Guide",
                                "/septic-system-cost-calculator/indiana/",
                                "Use the broader Indiana guide only when you still need statewide permit-path and sewer-gating context around the file."
                        )
                ),
                countyLinks,
                workflowPacketSources(
                        recordsPage.officialSourceIds(),
                        state.recordsLookupSourceIds(),
                        state.localAuthoritySourceIds(),
                        state.officialSourceIds()
                ),
                List.of(
                        "A buyer or seller cannot produce a septic file with enough confidence for closing.",
                        "You need a county or local board path before you let the conversation drift into quote mode.",
                        "The parcel story may break if sanitary sewer is available or the local board file contradicts the seller summary."
                ),
                state.recordsToRequest(),
                List.of(
                        "Confirm the county before you send this packet so the recipient can move into the right county page fast.",
                        "Send the Indiana records lookup first, and use the county page only when the parcel already has a clear county office.",
                        "Keep the estimator and quote links out of the first send unless the file is already strong enough to trust."
                )
        );
        return renderWorkflowPacketPage(
                model,
                seoService.workflowPacketPage(
                        "Indiana Septic Records Packet for Professionals | SepticPath",
                        "Public noindex Indiana septic records packet for buyer agents and coordinators who need a county-file-first handoff before a quote story.",
                        INDIANA_RECORDS_PACKET_PATH
                ),
                packet
        );
    }

    @GetMapping({"/for-professionals/buyer-diligence-packet/new-york", "/for-professionals/buyer-diligence-packet/new-york/"})
    public String newYorkBuyerDiligencePacket(Model model) {
        StateProfile state = researchDataService.findPublicStateBySlug("new-york")
                .orElseThrow(() -> new StateNotFoundException("new-york"));
        StateMoneyPage buyerPage = researchDataService.findPublicStateMoneyPage("buying-a-house-with-a-septic-system", "new-york")
                .orElseThrow(() -> new StateNotFoundException("buying-a-house-with-a-septic-system/new-york"));
        StateMoneyPage recordsPage = researchDataService.findPublicStateMoneyPage("septic-records-checklist", "new-york")
                .orElseThrow(() -> new StateNotFoundException("septic-records-checklist/new-york"));
        String packetUrl = seoService.absoluteUrl(NEW_YORK_BUYER_PACKET_PATH);
        WorkflowPacketView packet = new WorkflowPacketView(
                "Professional workflow packet",
                "New York septic buyer diligence packet for agents and coordinators",
                "Use this when the deal needs Appendix 75-A, county health file, and waiver history clarified before anyone pretends the septic story is routine. The first send should frame the deal risk, then hand off into records.",
                "Buyer diligence packet",
                "Public noindex handoff for New York septic deals",
                "This packet is for buyer-side teams who keep repeating the same file and waiver explanation in New York transactions.",
                "The packet should move the recipient into the New York buyer workflow first, then into the New York records lookup and official file sources.",
                "New York septic due diligence before closing",
                """
Hi,

Start with this New York septic buyer diligence packet:
%s

Open the New York buyer workflow first. It then hands off into the records lookup and the official file path that matters for Appendix 75-A, waiver history, and county health review.

The goal is to settle the file and local authority story before we treat the septic issue like a simple inspection line item.
""".formatted(packetUrl),
                new PageLink(
                        buyerPage.title(),
                        buyerPage.path(state.slug()),
                        "Start with the New York buyer workflow when the deal risk is still broader than one records request. That page pins the first move around buyer diligence, then narrows into the records file."
                ),
                List.of(
                        new PageLink(
                                recordsPage.title(),
                                recordsPage.path(state.slug()),
                                "Use the New York records lookup right after the buyer page when the next move is pulling the Appendix 75-A file, waiver history, or county health record."
                        ),
                        new PageLink(
                                "New York Septic Guide",
                                "/septic-system-cost-calculator/new-york/",
                                "Use the broader New York guide only when you still need statewide design-baseline context around the file."
                        )
                ),
                List.of(),
                workflowPacketSources(
                        buyerPage.officialSourceIds(),
                        recordsPage.officialSourceIds(),
                        state.recordsLookupSourceIds(),
                        state.localAuthoritySourceIds(),
                        state.officialSourceIds()
                ),
                List.of(
                        "A buyer needs the septic file story clarified before inspection or repair conversations drift.",
                        "The deal is already moving, but Appendix 75-A, waiver history, or county health review is still unclear.",
                        "You need a reusable buyer-facing explanation that keeps the next move on records instead of generic fear or quote shopping."
                ),
                state.recordsToRequest(),
                List.of(
                        "Use the buyer packet first when the main problem is deal diligence, not a stand-alone records request.",
                        "Expect the next internal click to be the New York records lookup, not the broad state guide.",
                        "Do not lead with estimate ranges until the buyer understands the file quality and waiver story."
                )
        );
        return renderWorkflowPacketPage(
                model,
                seoService.workflowPacketPage(
                        "New York Septic Buyer Diligence Packet for Professionals | SepticPath",
                        "Public noindex New York septic buyer diligence packet for agents and coordinators who need an Appendix 75-A and county-file-first handoff.",
                        NEW_YORK_BUYER_PACKET_PATH
                ),
                packet
        );
    }

    @GetMapping({"/for-professionals/permit-prep-packet/south-carolina", "/for-professionals/permit-prep-packet/south-carolina/"})
    public String southCarolinaPermitPrepPacket(Model model) {
        StateProfile state = researchDataService.findPublicStateBySlug("south-carolina")
                .orElseThrow(() -> new StateNotFoundException("south-carolina"));
        StateMoneyPage permitPage = researchDataService.findPublicStateMoneyPage("septic-permit-process", "south-carolina")
                .orElseThrow(() -> new StateNotFoundException("septic-permit-process/south-carolina"));
        String packetUrl = seoService.absoluteUrl(SOUTH_CAROLINA_PERMIT_PACKET_PATH);
        WorkflowPacketView packet = new WorkflowPacketView(
                "Professional workflow packet",
                "South Carolina septic permit prep packet for installers and coordinators",
                "Use this when the real blocker is D-1740, site review, permit-copy retrieval, or county routing in South Carolina. The first send should narrow into the permit workflow before anyone treats the project like a standard install quote.",
                "Permit prep packet",
                "Public noindex handoff for South Carolina permit prep",
                "This packet is for teams that keep re-explaining South Carolina permit routing, permit-copy retrieval, and local office handoff before the job is truly permit-ready.",
                "The packet should move the recipient into the South Carolina permit workflow first, then into the permit links and county routing inside that page.",
                "South Carolina septic permit prep and D-1740 path",
                """
Hi,

Use this South Carolina permit prep packet as the first reference:
%s

Open the South Carolina permit workflow first. It points to the permit links and county routing that matter for D-1740, site review, and permit-copy questions.

The goal is to settle the permit path before we frame the project as a normal install or replacement quote.
""".formatted(packetUrl),
                new PageLink(
                        permitPage.title(),
                        permitPage.path(state.slug()),
                        "Start with the South Carolina permit workflow when D-1740, local office routing, or permit-copy retrieval is the real bottleneck."
                ),
                List.of(
                        new PageLink(
                                "South Carolina Septic Guide",
                                "/septic-system-cost-calculator/south-carolina/",
                                "Use the broader South Carolina guide only when you still need statewide permit context around the local office path."
                        )
                ),
                List.of(),
                workflowPacketSources(
                        permitPage.officialSourceIds(),
                        state.localAuthoritySourceIds(),
                        state.recordsLookupSourceIds(),
                        state.officialSourceIds()
                ),
                List.of(
                        "The recipient still needs the permit sequence clarified before site work or contractor scheduling starts.",
                        "D-1740, site approval, or permit-copy retrieval is still an open question.",
                        "You need a sendable explanation that narrows the recipient into the permit path instead of a broad cost discussion."
                ),
                permitPage.quotePrepChecklist(),
                List.of(
                        "Send this before an estimate when D-1740 or permit-copy friction is still unresolved.",
                        "Expect the next move to be the permit workflow page and then the official source links inside it.",
                        "Keep quote and estimator links out of the first handoff until the permit path is actually clearer."
                )
        );
        return renderWorkflowPacketPage(
                model,
                seoService.workflowPacketPage(
                        "South Carolina Septic Permit Prep Packet for Professionals | SepticPath",
                        "Public noindex South Carolina septic permit prep packet for installers and coordinators who need a D-1740 and permit-path-first handoff.",
                        SOUTH_CAROLINA_PERMIT_PACKET_PATH
                ),
                packet
        );
    }

    @GetMapping(value = {"/robots.txt"}, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String robotsTxt() {
        return sitemapService.robotsTxt();
    }

    @GetMapping(value = {"/sitemap.xml"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemapXml() {
        return sitemapService.sitemapXml();
    }

    @GetMapping({"/septic-system-cost-calculator", "/septic-system-cost-calculator/"})
    public String calculator(
            @RequestParam(name = "state", required = false) String stateCode,
            @RequestParam(name = "projectType", required = false) String projectType,
            @RequestParam(name = "sourcePageHint", required = false) String sourcePageHint,
            @RequestParam(name = "quoteMode", defaultValue = "false") boolean quoteMode,
            Model model
    ) {
        EstimateForm estimateForm = new EstimateForm();
        if (stateCode != null && usStateDirectoryService.findByCode(stateCode).isPresent()) {
            estimateForm.setStateCode(stateCode.toUpperCase(Locale.US));
        }
        if (projectType != null) {
            estimateForm.setProjectType(ProjectType.fromValue(projectType).value());
        }
        if (isValidSourcePageHint(sourcePageHint)) {
            estimateForm.setSourcePageHint(sourcePageHint);
        }
        return renderCalculator(model, estimateForm, null, QuoteLeadForm.fromEstimateForm(estimateForm), null, false, quoteMode);
    }

    @GetMapping({"/septic-tank-size-estimator", "/septic-tank-size-estimator/"})
    public String tankSizeEstimator(
            @RequestParam(name = "state", required = false) String stateCode,
            Model model
    ) {
        TankSizeForm tankSizeForm = new TankSizeForm();
        if (stateCode != null && researchDataService.findStateByCode(stateCode).filter(StateProfile::isPublished).isPresent()) {
            tankSizeForm.setStateCode(stateCode.toUpperCase(Locale.US));
        }
        return renderTankSizeEstimator(model, tankSizeForm, null);
    }

    @PostMapping({"/septic-tank-size-estimator", "/septic-tank-size-estimator/"})
    public String calculateTankSize(@ModelAttribute TankSizeForm tankSizeForm, Model model) {
        TankSizeEstimatorResult result = tankSizeEstimatorService.estimate(tankSizeForm);
        return renderTankSizeEstimator(model, tankSizeForm, result);
    }

    @GetMapping({"/septic-pump-schedule-estimator", "/septic-pump-schedule-estimator/"})
    public String pumpScheduleEstimator(Model model) {
        return renderPumpScheduleEstimator(model, new PumpScheduleForm(), null);
    }

    @PostMapping({"/septic-pump-schedule-estimator", "/septic-pump-schedule-estimator/"})
    public String calculatePumpSchedule(@ModelAttribute PumpScheduleForm pumpScheduleForm, Model model) {
        PumpScheduleResult result = pumpScheduleService.estimate(pumpScheduleForm);
        return renderPumpScheduleEstimator(model, pumpScheduleForm, result);
    }

    @GetMapping({"/drain-field-estimator", "/drain-field-estimator/"})
    public String drainfieldEstimator(
            @RequestParam(name = "state", required = false) String stateCode,
            Model model
    ) {
        DrainfieldEstimatorForm drainfieldEstimatorForm = new DrainfieldEstimatorForm();
        if (stateCode != null && researchDataService.findStateByCode(stateCode).filter(StateProfile::isPublished).isPresent()) {
            drainfieldEstimatorForm.setStateCode(stateCode.toUpperCase(Locale.US));
        }
        return renderDrainfieldEstimator(model, drainfieldEstimatorForm, null);
    }

    @PostMapping({"/drain-field-estimator", "/drain-field-estimator/"})
    public String calculateDrainfield(@ModelAttribute DrainfieldEstimatorForm drainfieldEstimatorForm, Model model) {
        DrainfieldEstimatorResult result = drainfieldEstimatorService.estimate(drainfieldEstimatorForm);
        return renderDrainfieldEstimator(model, drainfieldEstimatorForm, result);
    }

    @PostMapping({"/septic-system-cost-calculator", "/septic-system-cost-calculator/"})
    public String calculate(@ModelAttribute EstimateForm estimateForm, Model model) {
        EstimatorResult result = estimatorService.estimate(estimateForm);
        return renderCalculator(model, estimateForm, result, QuoteLeadForm.fromEstimateForm(estimateForm), null, false, true);
    }

    @PostMapping({"/quote-request", "/quote-request/"})
    public String submitQuote(
            @Valid @ModelAttribute QuoteLeadForm quoteLeadForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model
    ) {
        EstimateForm estimateForm = quoteLeadForm.toEstimateForm();
        EstimatorResult result = estimatorService.estimate(estimateForm);

        if (bindingResult.hasErrors()) {
            return renderCalculator(model, estimateForm, result, quoteLeadForm, null, true, true);
        }

        String leadId = leadStorageService.saveQuoteLead(
                quoteLeadForm,
                estimateForm,
                result,
                "/septic-system-cost-calculator/",
                request
        );
        QuoteLeadForm clearedQuoteForm = QuoteLeadForm.fromEstimateForm(estimateForm);
        return renderCalculator(model, estimateForm, result, clearedQuoteForm, leadId, false, true);
    }

    @PostMapping(value = {"/events/nav-click", "/events/nav-click/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> recordNavigationClick(
            @RequestBody NavigationClickForm navigationClickForm,
            HttpServletRequest request
    ) {
        if (!isTrackableInternalPath(navigationClickForm.sourcePage())
                || !isTrackableInternalPath(navigationClickForm.targetPath())) {
            return ResponseEntity.noContent().build();
        }

        leadStorageService.saveNavigationClick(
                navigationClickForm.sourcePage(),
                navigationClickForm.sourceContext(),
                navigationClickForm.targetPath(),
                navigationClickForm.targetType(),
                navigationClickForm.targetLabel(),
                request
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping({"/septic-system-cost-calculator/{stateSlug}", "/septic-system-cost-calculator/{stateSlug}/"})
    public String stateGuide(@PathVariable String stateSlug, Model model) {
        Optional<StateProfile> stateProfile = researchDataService.findStateBySlug(stateSlug);
        if (stateProfile.filter(StateProfile::isPublished).isPresent()) {
            return renderPublishedStateGuide(model, stateProfile.orElseThrow());
        }
        UsStateDirectoryService.UsStateReference stateReference = stateProfile
                .map(state -> new UsStateDirectoryService.UsStateReference(state.stateCode(), state.stateName()))
                .or(() -> usStateDirectoryService.findBySlug(stateSlug))
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        return renderQueuedStateGuide(model, stateReference);
    }

    private String renderPublishedStateGuide(Model model, StateProfile state) {
        List<SourceRecord> sources = researchDataService.getSources(state.officialSourceIds());
        List<SourceRecord> localAuthoritySources = researchDataService.getSources(state.localAuthoritySourceIds());
        List<SourceRecord> recordsLookupSources = researchDataService.getSources(state.recordsLookupSourceIds());
        List<StateRuleFactView> stateRuleFacts = stateRuleFactViews(state.stateCode());
        StateActionCopy stateActionCopy = stateActionCopy(state);
        StatePlanningSnapshot planningSnapshot = statePlanningSnapshot(state.stateCode());
        List<CoreStateComparisonRow> coreStateComparisonRows = coreStateComparisonRows(state);
        List<PageLink> countyRecordLinks = countyRecordPageLinks(state.stateCode());
        List<StateMoneyPage> sortedStateMoneyPages = researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                .sorted(Comparator
                        .comparingInt((StateMoneyPage page) -> stateMoneyPagePriorityScore(state, page))
                        .reversed()
                        .thenComparing(StateMoneyPage::title))
                .toList();
        StateCountyWorkflowSynthesisView guideCountyWorkflowSynthesis = sortedStateMoneyPages.stream()
                .filter(page -> "septic-records-checklist".equals(page.contentSlug()))
                .findFirst()
                .map(page -> stateCountyWorkflowSynthesis(page, state))
                .orElse(null);
        String lastReviewedAt = latestVerifiedAt(sources, state.lastVerifiedAt());

        model.addAttribute("page", seoService.stateGuide(state, lastReviewedAt, STATE_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("localAuthoritySources", localAuthoritySources);
        model.addAttribute("recordsLookupSources", recordsLookupSources);
        model.addAttribute("primaryLocalAuthoritySource", localAuthoritySources.stream().findFirst().orElse(null));
        model.addAttribute("primaryRecordsLookupSource", recordsLookupSources.stream().findFirst().orElse(null));
        model.addAttribute("stateMoneyPages", sortedStateMoneyPages);
        model.addAttribute("featuredStateMoneyPages", sortedStateMoneyPages.stream().limit(5).toList());
        model.addAttribute("featuredStateWorkflowLinks", sortedStateMoneyPages.stream()
                .limit(5)
                .map(page -> stateGuideHeroWorkflowLink(page, state))
                .toList());
        model.addAttribute("stateRuleFacts", stateRuleFacts);
        model.addAttribute("guideFaqs", seoService.stateGuideFaqs(state));
        model.addAttribute("guideHeading", seoService.stateGuideHeading(state));
        model.addAttribute("calculatorCtaLabel", stateActionCopy.buttonLabel());
        model.addAttribute("calculatorCtaNote", stateActionCopy.supportingNote());
        model.addAttribute("planningSnapshot", planningSnapshot);
        model.addAttribute("coreStateComparisonRows", coreStateComparisonRows);
        model.addAttribute("countyRecordLinks", countyRecordLinks);
        model.addAttribute("featuredCountyRecordLinks", countyRecordLinks.stream().limit(30).toList());
        researchDataService.findPublicStateMoneyPage("septic-records-checklist", state.slug())
                .ifPresentOrElse(
                        recordsPage -> {
                            model.addAttribute("stateRecordsLookupPath", recordsPage.path(state.slug()));
                            model.addAttribute("stateRecordsLookupTitle", recordsPage.title());
                        },
                        () -> {
                            model.addAttribute("stateRecordsLookupPath", null);
                            model.addAttribute("stateRecordsLookupTitle", "");
                        }
                );
        model.addAttribute("guideCountyWorkflowSynthesis", guideCountyWorkflowSynthesis);
        model.addAttribute("editorialPreparedBy", STATE_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", "Reviewed against " + sources.size()
                + " official sources listed below and " + countyRecordLinks.size()
                + " live county workflow pages already connected to this state.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", STATE_EDITORIAL_NOTE);
        return "pages/state-guide";
    }

    private String renderQueuedStateGuide(Model model, UsStateDirectoryService.UsStateReference stateReference) {
        Optional<StateQueuePlan> queuePlan = stateQueuePlanService.findByStateCode(stateReference.stateCode());
        java.util.ArrayList<String> starterPaths = new java.util.ArrayList<>();
        queuePlan.map(StateQueuePlan::recommendedPath).ifPresent(starterPaths::add);
        for (String path : List.of(
                "/septic-permit-process/",
                "/septic-records-checklist/",
                "/buying-a-house-with-a-septic-system/",
                "/septic-replacement-cost/",
                "/perc-test-cost/"
        )) {
            if (!starterPaths.contains(path)) {
                starterPaths.add(path);
            }
        }
        model.addAttribute("page", seoService.queuedStateGuide(stateReference.stateName(), stateReference.slug()));
        model.addAttribute("stateCode", stateReference.stateCode());
        model.addAttribute("stateName", stateReference.stateName());
        model.addAttribute("queuePlan", queuePlan.map(plan -> stateQueuePlanView(plan, stateReference)).orElse(null));
        model.addAttribute("starterLinks", pageLinks(starterPaths, "septic-system-cost-calculator", null));
        model.addAttribute("benchmarkGuides", pageLinks(
                CORE_STATE_CODES.stream()
                        .map(researchDataService::findStateByCode)
                        .flatMap(Optional::stream)
                        .map(state -> "/septic-system-cost-calculator/" + state.slug() + "/")
                        .toList(),
                "septic-system-cost-calculator",
                null
        ));
        model.addAttribute("queuedStateCount", Math.max(usStateDirectoryService.allStates().size() - researchDataService.getPublicStateProfiles().size(), 0));
        return "pages/state-guide-queued";
    }

    @GetMapping({
            "/septic-replacement-cost", "/septic-replacement-cost/",
            "/septic-tank-size", "/septic-tank-size/",
            "/perc-test-cost", "/perc-test-cost/",
            "/drain-field-replacement-cost", "/drain-field-replacement-cost/",
            "/failed-perc-test-septic", "/failed-perc-test-septic/",
            "/septic-replacement-area", "/septic-replacement-area/",
            "/wet-yard-over-septic-drain-field", "/wet-yard-over-septic-drain-field/",
            "/septic-pumping-cost", "/septic-pumping-cost/",
            "/septic-inspection-cost", "/septic-inspection-cost/",
            "/buying-a-house-with-a-septic-system", "/buying-a-house-with-a-septic-system/",
            "/septic-permit-lookup", "/septic-permit-lookup/",
            "/septic-permit-process", "/septic-permit-process/",
            "/septic-records-checklist", "/septic-records-checklist/",
            "/septic-transfer-compliance", "/septic-transfer-compliance/"
    })
    public String contentPage(org.springframework.web.context.request.WebRequest request, Model model) {
        String path = request.getDescription(false).replace("uri=", "");
        String slug = path.replaceFirst("^/", "").replaceFirst("/$", "");
        ContentPage contentPage = researchDataService.findPublicContentPage(slug)
                .orElseThrow(() -> new StateNotFoundException(slug));
        List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntries = rankedStateEntriesForContentPage(contentPage);
        List<StateMoneyPageLink> stateMoneyPageLinks = rankedStateEntries.stream()
                .map(entry -> new StateMoneyPageLink(
                        entry.getKey().title(),
                        entry.getValue().stateName(),
                        entry.getValue().stateCode(),
                        stateSurfaceRouteTitle(entry.getKey(), entry.getValue()),
                        entry.getKey().path(entry.getValue().slug()),
                        stateSurfaceSignalView(contentPage, entry.getKey(), entry.getValue())))
                .toList();
        List<ContentEvidenceLaneView> contentEvidenceLanes = rankedStateEntries.stream()
                .limit(6)
                .map(entry -> contentEvidenceLane(entry.getKey(), entry.getValue()))
                .toList();
        ContentWorkflowCoverageView contentWorkflowCoverage = contentWorkflowCoverage(contentPage, rankedStateEntries);
        List<PageLink> internalLinks = pageLinks(contentPage.internalLinkTargets(), contentPage.slug(), null);
        List<PageLink> permitLookupCountyLinks = permitLookupCountyLaunchpadLinks(contentPage);
        String lastReviewedAt = researchDataService.contentPagesGeneratedAt();

        model.addAttribute("page", seoService.contentPage(contentPage, lastReviewedAt, CONTENT_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("contentPage", contentPage);
        model.addAttribute("states", researchDataService.getPublicStateProfiles());
        model.addAttribute("stateMoneyPageLinks", stateMoneyPageLinks);
        model.addAttribute("featuredStateMoneyPageLinks", stateMoneyPageLinks.stream().limit(10).toList());
        model.addAttribute("contentEvidenceLanes", contentEvidenceLanes);
        model.addAttribute("featuredContentEvidenceLanes", contentEvidenceLanes.stream().limit(3).toList());
        model.addAttribute("contentWorkflowCoverage", contentWorkflowCoverage);
        model.addAttribute("internalLinks", internalLinks);
        model.addAttribute("featuredInternalLinks", internalLinks.stream().limit(5).toList());
        model.addAttribute("secondaryInternalLinks", internalLinks.stream().skip(4).toList());
        model.addAttribute("permitLookupCountyLinks", permitLookupCountyLinks);
        model.addAttribute("featuredPermitLookupCountyLinks", permitLookupCountyLinks.stream().limit(12).toList());
        model.addAttribute("secondaryPermitLookupCountyLinks", permitLookupCountyLinks.stream().skip(12).toList());
        model.addAttribute("calculatorPath", primaryActionPathForContentPage(contentPage, "/" + contentPage.slug() + "/"));
        model.addAttribute("contentQuotePath", shouldLeadWithStateWorkflow(contentPage)
                ? null
                : contentQuotePathForContentPage(contentPage, "/" + contentPage.slug() + "/"));
        model.addAttribute("calculatorCtaHeading", contentActionHeading(contentPage));
        model.addAttribute("calculatorCtaLabel", contentActionLabel(contentPage));
        model.addAttribute("calculatorCtaNote", contentActionNote(contentPage));
        model.addAttribute("calculatorCtaTargetType", contentActionTargetType(contentPage));
        model.addAttribute("secondaryActionPath", secondaryActionPathForContentPage(contentPage, "/" + contentPage.slug() + "/"));
        model.addAttribute("secondaryActionLabel", secondaryActionLabel(contentPage));
        model.addAttribute("secondaryActionNote", secondaryActionNote(contentPage));
        model.addAttribute("secondaryActionTargetType", secondaryActionTargetType(contentPage));
        model.addAttribute("editorialPreparedBy", CONTENT_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", contentEvidenceLanes.isEmpty()
                ? "Reviewed against the linked state-specific pages, county workflow network, and source policy."
                : "Reviewed against " + contentEvidenceLanes.size() + " source-backed state-specific pages, the county workflow network underneath them, and the source policy.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", CONTENT_EDITORIAL_NOTE);
        return "pages/content-page";
    }

    @GetMapping({
            "/septic-records-checklist/{stateSlug}/{countySlug}", "/septic-records-checklist/{stateSlug}/{countySlug}/"
    })
    public String countyRecordsPage(@PathVariable String stateSlug, @PathVariable String countySlug, Model model) {
        CountyRecordsPage countyPage = researchDataService.findPublicCountyRecordsPage(stateSlug, countySlug)
                .orElseThrow(() -> new StateNotFoundException("septic-records-checklist/" + stateSlug + "/" + countySlug));
        StateProfile state = researchDataService.findStateByCode(countyPage.stateCode())
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(countyPage.officialSourceIds());
        List<PageLink> internalLinks = pageLinks(
                countyPage.internalLinkTargets(),
                "septic-records-checklist",
                state.stateCode()
        );
        String lastReviewedAt = latestVerifiedAt(sources, state.lastVerifiedAt());

        model.addAttribute("page", seoService.countyRecordsPage(countyPage, state, lastReviewedAt, STATE_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("countyPage", countyPage);
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("countyWorkflowStructure", countyWorkflowStructure(countyPage, state));
        model.addAttribute("internalLinks", internalLinks);
        model.addAttribute("featuredInternalLinks", internalLinks.stream().limit(4).toList());
        model.addAttribute("editorialPreparedBy", STATE_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", "Reviewed against " + sources.size() + " official county or state sources tied to this county workflow.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", STATE_EDITORIAL_NOTE);
        return "pages/county-records-page";
    }

    @GetMapping({
            "/septic-replacement-cost/{stateSlug}", "/septic-replacement-cost/{stateSlug}/",
            "/perc-test-cost/{stateSlug}", "/perc-test-cost/{stateSlug}/",
            "/failed-perc-test-septic/{stateSlug}", "/failed-perc-test-septic/{stateSlug}/",
            "/septic-replacement-area/{stateSlug}", "/septic-replacement-area/{stateSlug}/",
            "/wet-yard-over-septic-drain-field/{stateSlug}", "/wet-yard-over-septic-drain-field/{stateSlug}/",
            "/buying-a-house-with-a-septic-system/{stateSlug}", "/buying-a-house-with-a-septic-system/{stateSlug}/",
            "/drain-field-replacement-cost/{stateSlug}", "/drain-field-replacement-cost/{stateSlug}/",
            "/septic-pumping-cost/{stateSlug}", "/septic-pumping-cost/{stateSlug}/",
            "/septic-inspection-cost/{stateSlug}", "/septic-inspection-cost/{stateSlug}/",
            "/septic-permit-process/{stateSlug}", "/septic-permit-process/{stateSlug}/",
            "/septic-records-checklist/{stateSlug}", "/septic-records-checklist/{stateSlug}/"
    })
    public String stateMoneyPage(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        String path = request.getRequestURI().replaceFirst("^/", "").replaceFirst("/$", "");
        String contentSlug = path.substring(0, path.lastIndexOf('/'));

        StateMoneyPage stateMoneyPage = researchDataService.findPublicStateMoneyPage(contentSlug, stateSlug)
                .orElseThrow(() -> new StateNotFoundException(path));
        StateProfile state = researchDataService.findStateByCode(stateMoneyPage.stateCode())
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(stateMoneyPage.officialSourceIds());
        List<SourceRecord> localAuthoritySources = researchDataService.getSources(state.localAuthoritySourceIds());
        List<SourceRecord> recordsLookupSources = researchDataService.getSources(state.recordsLookupSourceIds());
        SourceRecord primaryLocalAuthoritySource = localAuthoritySources.stream().findFirst().orElse(null);
        SourceRecord primaryRecordsLookupSource = recordsLookupSources.stream().findFirst().orElse(null);
        StateActionCopy stateActionCopy = stateActionCopy(state);
        StatePlanningSnapshot planningSnapshot = statePlanningSnapshot(state.stateCode());
        List<PageLink> internalLinks = pageLinks(
                mergedPaths(stateMoneyPage.internalLinkTargets(), defaultStateCrossLinks(stateMoneyPage, state)),
                stateMoneyPage.contentSlug(),
                state.stateCode()
        );
        List<PageLink> countyRecordLinks = supportsCountyWorkflowSummary(stateMoneyPage.contentSlug())
                ? countyRecordPageLinks(state.stateCode())
                : List.of();
        StateCountyWorkflowSynthesisView countyWorkflowSynthesis = stateCountyWorkflowSynthesis(stateMoneyPage, state);
        StateWorkflowDecisionView workflowDecision = stateWorkflowDecisionView(
                stateMoneyPage,
                state,
                countyWorkflowSynthesis,
                primaryLocalAuthoritySource,
                primaryRecordsLookupSource
        );
        StateCostScopeView costScopeView = stateCostScopeView(
                stateMoneyPage,
                state,
                countyWorkflowSynthesis
        );
        String calculatorPath = stateMoneyCalculatorPath(stateMoneyPage, state);
        StateMoneyPrimaryAction primaryAction = stateMoneyPrimaryAction(
                stateMoneyPage,
                state,
                stateActionCopy,
                countyRecordLinks,
                primaryLocalAuthoritySource,
                primaryRecordsLookupSource,
                countyWorkflowSynthesis
        );
        String lastReviewedAt = latestVerifiedAt(sources, state.lastVerifiedAt());
        boolean showQuoteCta = publishingPolicyService.allowDirectQuote(stateMoneyPage, state);

        model.addAttribute("page", seoService.stateMoneyPage(stateMoneyPage, state, lastReviewedAt, STATE_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("stateMoneyPage", stateMoneyPage);
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("localAuthoritySources", localAuthoritySources);
        model.addAttribute("recordsLookupSources", recordsLookupSources);
        model.addAttribute("primaryLocalAuthoritySource", primaryLocalAuthoritySource);
        model.addAttribute("primaryRecordsLookupSource", primaryRecordsLookupSource);
        model.addAttribute("primaryAction", primaryAction);
        model.addAttribute("calculatorPath", calculatorPath);
        model.addAttribute("quotePath", stateMoneyQuotePath(calculatorPath));
        model.addAttribute("showQuoteCta", showQuoteCta);
        model.addAttribute("guidePath", "/septic-system-cost-calculator/" + state.slug() + "/");
        model.addAttribute("calculatorCtaLabel", stateActionCopy.buttonLabel());
        model.addAttribute("calculatorCtaNote", stateActionCopy.supportingNote());
        model.addAttribute("planningSnapshot", planningSnapshot);
        model.addAttribute("internalLinks", internalLinks);
        model.addAttribute("featuredInternalLinks", internalLinks.stream().limit(5).toList());
        model.addAttribute("secondaryInternalLinks", internalLinks.stream().skip(4).toList());
        model.addAttribute("countyRecordLinks", countyRecordLinks);
        model.addAttribute("featuredCountyRecordLinks", countyRecordLinks.stream().limit(30).toList());
        model.addAttribute("countyWorkflowSynthesis", countyWorkflowSynthesis);
        model.addAttribute("workflowDecision", workflowDecision);
        model.addAttribute("costScopeView", costScopeView);
        model.addAttribute("editorialPreparedBy", STATE_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", "Reviewed against " + sources.size() + " official sources tied to this page and state workflow.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", STATE_EDITORIAL_NOTE);
        return "pages/state-money-page";
    }

    @ModelAttribute("projectTypes")
    public ProjectType[] projectTypes() {
        return ProjectType.values();
    }

    @ModelAttribute("soilStatuses")
    public SoilPercStatus[] soilStatuses() {
        return SoilPercStatus.values();
    }

    @ModelAttribute("accessLevels")
    public AccessDifficulty[] accessLevels() {
        return AccessDifficulty.values();
    }

    @ModelAttribute("timelines")
    public TimelinePreference[] timelines() {
        return TimelinePreference.values();
    }

    @ModelAttribute("occupancyProfiles")
    public OccupancyProfile[] occupancyProfiles() {
        return OccupancyProfile.values();
    }

    @ModelAttribute("usageProfiles")
    public UsageProfile[] usageProfiles() {
        return UsageProfile.values();
    }

    private String renderCalculator(
            Model model,
            EstimateForm estimateForm,
            EstimatorResult result,
            QuoteLeadForm quoteLeadForm,
            String leadId,
            boolean quoteHasErrors,
            boolean showQuotePanel
    ) {
        ContentPage calculatorLanding = researchDataService.findPublicContentPage("septic-system-cost-calculator")
                .orElse(null);
        model.addAttribute("page", seoService.calculatorPage());
        model.addAttribute("states", calculatorStateOptions());
        model.addAttribute("estimateForm", estimateForm);
        model.addAttribute("result", result);
        model.addAttribute("quoteLeadForm", quoteLeadForm);
        model.addAttribute("leadId", leadId);
        model.addAttribute("quoteHasErrors", quoteHasErrors);
        model.addAttribute("showQuotePanel", showQuotePanel || result != null || leadId != null || quoteHasErrors);
        model.addAttribute("calculatorLanding", calculatorLanding);
        model.addAttribute("calculatorLandingLinks", calculatorLanding == null
                ? List.of()
                : pageLinks(calculatorLanding.internalLinkTargets(), calculatorLanding.slug(), null));
        model.addAttribute("costEvidence", result == null
                ? List.of()
                : costEvidenceViews(result.stateCode(), estimateForm.getProjectType()));
        return "pages/calculator";
    }

    private List<StateOptionView> calculatorStateOptions() {
        return usStateDirectoryService.allStates().stream()
                .map(state -> new StateOptionView(state.stateCode(), state.stateName()))
                .toList();
    }

    private boolean isTrackableInternalPath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        return path.startsWith("/") && !path.startsWith("//") && !path.startsWith("/events/");
    }

    private String renderTankSizeEstimator(Model model, TankSizeForm tankSizeForm, TankSizeEstimatorResult result) {
        model.addAttribute("page", seoService.tankSizeEstimatorPage());
        List<StateProfile> publicStates = researchDataService.getPublicStateProfiles();
        StateProfile selectedState = researchDataService.findStateByCode(tankSizeForm.getStateCode())
                .filter(StateProfile::isPublished)
                .orElseGet(() -> preferredTankSizeState(publicStates));
        model.addAttribute("states", publicStates);
        model.addAttribute("tankSizeForm", tankSizeForm);
        model.addAttribute("result", result);
        model.addAttribute("selectedState", selectedState);
        model.addAttribute("tankSizeFaqs", seoService.tankSizeEstimatorFaqs());
        model.addAttribute("stateRuleFacts", selectedState == null ? List.of() : stateRuleFactViews(selectedState.stateCode()));
        return "pages/tank-size-estimator";
    }

    private String renderDrainfieldEstimator(
            Model model,
            DrainfieldEstimatorForm drainfieldEstimatorForm,
            DrainfieldEstimatorResult result
    ) {
        model.addAttribute("page", seoService.drainfieldEstimatorPage());
        List<StateProfile> publicStates = researchDataService.getPublicStateProfiles();
        StateProfile selectedState = researchDataService.findStateByCode(drainfieldEstimatorForm.getStateCode())
                .filter(StateProfile::isPublished)
                .orElseGet(() -> preferredTankSizeState(publicStates));
        String selectedDrainfieldPagePath = "";
        String selectedDrainfieldPageTitle = "";
        if (selectedState != null) {
            Optional<StateMoneyPage> drainfieldPage = researchDataService.findPublicStateMoneyPage("drain-field-replacement-cost", selectedState.slug());
            if (drainfieldPage.isPresent()) {
                selectedDrainfieldPagePath = drainfieldPage.get().path(selectedState.slug());
                selectedDrainfieldPageTitle = drainfieldPage.get().title();
            }
        }
        List<StateMoneyPageLink> drainfieldStateLinks = researchDataService.listPublicStateMoneyPagesForContent("drain-field-replacement-cost").stream()
                .flatMap(page -> researchDataService.findStateByCode(page.stateCode())
                        .filter(StateProfile::isPublished)
                        .map(state -> new StateMoneyPageLink(page.title(), state.stateName(), state.stateCode(), page.path(state.slug())))
                        .stream())
                .limit(8)
                .toList();
        model.addAttribute("states", publicStates);
        model.addAttribute("drainfieldEstimatorForm", drainfieldEstimatorForm);
        model.addAttribute("result", result);
        model.addAttribute("selectedState", selectedState);
        model.addAttribute("drainfieldFaqs", seoService.drainfieldEstimatorFaqs());
        model.addAttribute("drainfieldStateLinks", drainfieldStateLinks);
        model.addAttribute("selectedDrainfieldPagePath", selectedDrainfieldPagePath);
        model.addAttribute("selectedDrainfieldPageTitle", selectedDrainfieldPageTitle);
        return "pages/drainfield-estimator";
    }

    private StateProfile preferredTankSizeState(List<StateProfile> publicStates) {
        if (publicStates.isEmpty()) {
            return null;
        }
        return researchDataService.findStateByCode("GA")
                .filter(StateProfile::isPublished)
                .orElse(publicStates.get(0));
    }

    private List<StateCoverageCardView> buildStateCoverageCards() {
        return usStateDirectoryService.allStates().stream()
                .map(stateReference -> researchDataService.findStateByCode(stateReference.stateCode())
                        .filter(StateProfile::isPublished)
                        .map(this::publishedCoverageCard)
                        .orElseGet(() -> queuedCoverageCard(stateReference)))
                .sorted(Comparator
                        .comparing((StateCoverageCardView card) -> !card.published())
                        .thenComparingInt(card -> card.published()
                                ? 0
                                : stateQueuePlanService.findByStateCode(card.stateCode())
                                        .map(StateQueuePlan::priorityRank)
                                        .orElse(Integer.MAX_VALUE))
                        .thenComparing(StateCoverageCardView::stateName))
                .toList();
    }

    private WorkflowNetworkSnapshotView workflowNetworkSnapshot() {
        List<StateCountyBackbone> backbones = researchDataService.getPublicStateProfiles().stream()
                .map(state -> {
                    int countyCount = researchDataService.listPublicCountyRecordsPages(state.stateCode()).size();
                    int workflowPageCount = researchDataService.listPublicStateMoneyPages(state.stateCode()).size();
                    Optional<StateMoneyPage> recordsPage = researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                            .filter(page -> "septic-records-checklist".equals(page.contentSlug()))
                            .findFirst();
                    String path = recordsPage
                            .map(page -> page.path(state.slug()))
                            .orElse("/septic-system-cost-calculator/" + state.slug() + "/");
                    String title = recordsPage
                            .map(page -> state.stateName() + " county records workflow")
                            .orElse(state.stateName() + " septic guide");
                    String note = countyCount + " live county workflow pages | "
                            + workflowPageCount + " live state workflow pages";
                    return new StateCountyBackbone(state, countyCount, workflowPageCount, new PageLink(title, path, note));
                })
                .filter(backbone -> backbone.countyCount() > 0 || backbone.workflowPageCount() > 0)
                .sorted(Comparator
                        .comparingInt(StateCountyBackbone::countyCount)
                        .reversed()
                        .thenComparingInt(StateCountyBackbone::workflowPageCount)
                        .reversed()
                        .thenComparing(backbone -> backbone.state().stateName()))
                .toList();

        int liveCountyCount = backbones.stream()
                .mapToInt(StateCountyBackbone::countyCount)
                .sum();
        int countyBackedStateCount = (int) backbones.stream()
                .filter(backbone -> backbone.countyCount() > 0)
                .count();
        int countyFirstStateCount = (int) backbones.stream()
                .filter(backbone -> backbone.countyCount() >= 2)
                .count();
        int liveWorkflowPageCount = backbones.stream()
                .mapToInt(StateCountyBackbone::workflowPageCount)
                .sum();
        String topStates = backbones.stream()
                .filter(backbone -> backbone.countyCount() > 0)
                .limit(5)
                .map(backbone -> backbone.state().stateName())
                .collect(Collectors.joining(", "));

        return new WorkflowNetworkSnapshotView(
                "Live county workflow backbone",
                "The public network now carries " + liveWorkflowPageCount
                        + " source-backed state workflow pages and " + liveCountyCount
                        + " live county workflow pages across " + countyBackedStateCount
                        + " states.",
                List.of(
                        countyFirstStateCount + " states already have enough county depth to route users into county-first follow-up instead of a generic state-only answer.",
                        topStates.isBlank()
                                ? "The strongest live states already connect records, permit, buyer, and transfer questions back to actual local files."
                                : "The thickest live county workflow backbones today are " + topStates + ".",
                        "Use those county-backed state pages when file owner, permit closeout, transfer artifact, or quote-gate differences matter more than a statewide average."
                ),
                backbones.stream()
                        .filter(backbone -> backbone.countyCount() > 0)
                        .limit(5)
                        .map(StateCountyBackbone::link)
                        .toList(),
                liveCountyCount,
                countyBackedStateCount,
                countyFirstStateCount
        );
    }

    private List<PageLink> homeGrowthSpotlights() {
        return ORGANIC_SPRINT_STATE_CODES.stream()
                .map(researchDataService::findStateByCode)
                .flatMap(Optional::stream)
                .filter(StateProfile::isPublished)
                .map(state -> researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                        .sorted(Comparator
                                .comparingInt((StateMoneyPage page) -> growthPageScore(page, state))
                                .reversed()
                                .thenComparing(StateMoneyPage::title))
                        .findFirst()
                        .map(page -> growthSpotlightLink(page, state))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private List<PageLink> coverageGrowthSpotlights() {
        return researchDataService.getPublicStateProfiles().stream()
                .flatMap(state -> researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                        .sorted(Comparator
                                .comparingInt((StateMoneyPage page) -> growthPageScore(page, state))
                                .reversed()
                                .thenComparing(StateMoneyPage::title))
                        .limit(2)
                        .map(page -> Map.entry(page, state)))
                .sorted(Comparator
                        .comparingInt((Map.Entry<StateMoneyPage, StateProfile> entry) -> growthPageScore(entry.getKey(), entry.getValue()))
                        .reversed()
                        .thenComparing(entry -> entry.getValue().stateName())
                        .thenComparing(entry -> entry.getKey().title()))
                .limit(12)
                .map(entry -> growthSpotlightLink(entry.getKey(), entry.getValue()))
                .toList();
    }

    private PageLink growthSpotlightLink(StateMoneyPage page, StateProfile state) {
        return new PageLink(
                page.title(),
                page.path(state.slug()),
                state.stateName() + " | " + firstNonBlank(page.uniqueAngle(), page.metaDescription())
        );
    }

    private PageLink stateGuideHeroWorkflowLink(StateMoneyPage page, StateProfile state) {
        return new PageLink(
                stateGuideHeroWorkflowLabel(page),
                page.path(state.slug()),
                page.title()
        );
    }

    private String stateSurfaceRouteTitle(StateMoneyPage page, StateProfile state) {
        return switch (page.contentSlug()) {
            case "septic-records-checklist" -> state.stateName() + " records workflow";
            case "septic-permit-process" -> state.stateName() + " permit workflow";
            case "buying-a-house-with-a-septic-system" -> state.stateName() + " buyer workflow";
            case "septic-inspection-cost" -> state.stateName() + " inspection file path";
            case "perc-test-cost" -> state.stateName() + " perc path";
            case "failed-perc-test-septic" -> state.stateName() + " failed-perc path";
            case "septic-replacement-cost" -> state.stateName() + " replacement path";
            case "drain-field-replacement-cost" -> state.stateName() + " drain field path";
            case "septic-replacement-area" -> state.stateName() + " replacement-area path";
            case "wet-yard-over-septic-drain-field" -> state.stateName() + " field-failure path";
            case "septic-pumping-cost" -> state.stateName() + " maintenance path";
            default -> state.stateName() + " state page";
        };
    }

    private String stateGuideHeroWorkflowLabel(StateMoneyPage page) {
        return switch (page.contentSlug()) {
            case "septic-permit-process" -> "Open permit workflow";
            case "septic-records-checklist" -> "Open records lookup";
            case "buying-a-house-with-a-septic-system" -> "Open buyer workflow";
            case "perc-test-cost" -> "Open perc page";
            case "failed-perc-test-septic" -> "Open failed-perc page";
            case "septic-replacement-cost" -> "Open replacement path";
            case "drain-field-replacement-cost" -> "Open drain field page";
            case "septic-replacement-area" -> "Open replacement-area guide";
            case "wet-yard-over-septic-drain-field" -> "Open wet-yard guide";
            case "septic-inspection-cost" -> "Open inspection page";
            default -> "Open state page";
        };
    }

    private int growthPageScore(StateMoneyPage page, StateProfile state) {
        int score = switch (page.contentSlug()) {
            case "septic-permit-process" -> 120;
            case "septic-records-checklist" -> 116;
            case "buying-a-house-with-a-septic-system" -> 112;
            case "septic-replacement-cost" -> 108;
            case "perc-test-cost" -> 104;
            case "drain-field-replacement-cost" -> 78;
            case "failed-perc-test-septic" -> 74;
            case "septic-replacement-area" -> 70;
            case "wet-yard-over-septic-drain-field" -> 66;
            case "septic-inspection-cost" -> 60;
            case "septic-pumping-cost" -> 24;
            default -> 40;
        };

        score += switch (state.stateCode()) {
            case "TN" -> 28;
            case "AL", "IN" -> 20;
            case "TX" -> 16;
            case "NC" -> 14;
            case "WA" -> 18;
            case "NJ" -> 12;
            case "MO" -> 8;
            default -> 0;
        };

        if (CORE_STATE_CODES.contains(state.stateCode())) {
            score += 30;
        }
        if ("anchor".equalsIgnoreCase(state.launchTier())) {
            score += 8;
        }
        if (page.highlightBuyerTrigger()) {
            score += 4;
        }
        if (page.highlightMaintenanceNote()) {
            score += 2;
        }
        return score;
    }

    private StateCoverageCardView publishedCoverageCard(StateProfile state) {
        int liveIntentCount = researchDataService.listPublicStateMoneyPages(state.stateCode()).size();
        String confidenceLabel = confidenceLabel(state.confidenceScore());
        String metaLine = liveIntentCount + " live intent pages"
                + " | " + size(state.officialSourceIds()) + " official sources"
                + (confidenceLabel.isBlank() ? "" : " | " + confidenceLabel);
        return new StateCoverageCardView(
                state.stateCode(),
                state.stateName(),
                true,
                "Live guide",
                "live",
                state.pageAngle(),
                metaLine,
                "/septic-system-cost-calculator/" + state.slug() + "/",
                "Open live guide"
        );
    }

    private StateCoverageCardView queuedCoverageCard(UsStateDirectoryService.UsStateReference stateReference) {
        Optional<StateQueuePlan> queuePlan = stateQueuePlanService.findByStateCode(stateReference.stateCode());
        if (queuePlan.isPresent()) {
            PageLink recommendedLink = pageLink(queuePlan.get().recommendedPath(), "septic-system-cost-calculator", null);
            return new StateCoverageCardView(
                    stateReference.stateCode(),
                    stateReference.stateName(),
                    false,
                    "Priority #" + queuePlan.get().priorityRank(),
                    "queued",
                    queuePlan.get().launchAngle(),
                    queuePlan.get().rolloutWave() + " | Start with " + recommendedLink.title(),
                    "/septic-system-cost-calculator/" + stateReference.slug() + "/",
                    "Open rollout plan"
            );
        }
        return new StateCoverageCardView(
                stateReference.stateCode(),
                stateReference.stateName(),
                false,
                "Research queue",
                "queued",
                "Official-source guide work has not been published yet. Use the national estimator while this state moves through the source and workflow review queue.",
                "Queued for 50-state rollout | not yet published",
                "/septic-system-cost-calculator/" + stateReference.slug() + "/",
                "Open research page"
        );
    }

    private String renderPumpScheduleEstimator(Model model, PumpScheduleForm pumpScheduleForm, PumpScheduleResult result) {
        model.addAttribute("page", seoService.pumpScheduleEstimatorPage());
        model.addAttribute("pumpScheduleForm", pumpScheduleForm);
        model.addAttribute("result", result);
        return "pages/pump-schedule-estimator";
    }

    private String renderSitePage(
            Model model,
            PageMeta page,
            String eyebrow,
            String heading,
            String intro,
            List<SitePageSection> sections,
            String calloutTitle,
            String calloutBody
    ) {
        model.addAttribute("page", page);
        model.addAttribute("eyebrow", eyebrow);
        model.addAttribute("heading", heading);
        model.addAttribute("intro", intro);
        model.addAttribute("sections", sections);
        model.addAttribute("calloutTitle", calloutTitle);
        model.addAttribute("calloutBody", calloutBody);
        return "pages/site-page";
    }

    private String renderWorkflowPacketPage(Model model, PageMeta page, WorkflowPacketView packet) {
        model.addAttribute("page", page);
        model.addAttribute("packet", packet);
        return "pages/workflow-packet-page";
    }

    private String renderContactPage(
            Model model,
            ContactRequestForm contactRequestForm,
            boolean contactHasErrors,
            String contactRequestId
    ) {
        model.addAttribute("page", seoService.basicPage(
                "Contact",
                "Contact the project for general questions, source corrections, privacy requests, and partnership inquiries.",
                "/contact/"
        ));
        model.addAttribute("contactRequestForm", contactRequestForm);
        model.addAttribute("contactHasErrors", contactHasErrors);
        model.addAttribute("contactRequestId", contactRequestId);
        model.addAttribute("states", researchDataService.getPublicStateProfiles());
        return "pages/contact-page";
    }

    private String calculatorPathForModule(String calculatorModule) {
        return switch (calculatorModule) {
            case "tank_size_estimator" -> "/septic-tank-size-estimator/";
            case "pump_schedule_estimator" -> "/septic-pump-schedule-estimator/";
            case "drainfield_estimator" -> "/drain-field-estimator/";
            default -> "/septic-system-cost-calculator/";
        };
    }

    private String primaryActionPathForContentPage(ContentPage contentPage, String sourcePage) {
        if (shouldLeadWithStateWorkflow(contentPage)) {
            return "#state-pages";
        }
        return calculatorPathForContentPage(contentPage, sourcePage);
    }

    private String secondaryActionPathForContentPage(ContentPage contentPage, String sourcePage) {
        if (!shouldLeadWithStateWorkflow(contentPage)) {
            return null;
        }
        return calculatorPathForContentPage(contentPage, sourcePage);
    }

    private String calculatorPathForContentPage(ContentPage contentPage, String sourcePage) {
        String modulePath = calculatorPathForModule(contentPage.calculatorModule());
        if (!"/septic-system-cost-calculator/".equals(modulePath)) {
            return modulePath;
        }
        if (contentPage.calculatorProjectType() == null || contentPage.calculatorProjectType().isBlank()) {
            return appendSourcePageHint(modulePath, sourcePage);
        }
        return appendSourcePageHint("/septic-system-cost-calculator/?projectType=" + contentPage.calculatorProjectType(), sourcePage);
    }

    private String contentQuotePathForContentPage(ContentPage contentPage, String sourcePage) {
        String calculatorPath = calculatorPathForContentPage(contentPage, sourcePage);
        if ("/drain-field-estimator/".equals(calculatorPath)) {
            calculatorPath = appendSourcePageHint("/septic-system-cost-calculator/?projectType=drainfield_replacement", sourcePage);
        }
        if (!calculatorPath.startsWith("/septic-system-cost-calculator/")) {
            return null;
        }
        return calculatorPath + (calculatorPath.contains("?") ? "&" : "?") + "quoteMode=true#quote-request";
    }

    private String appendSourcePageHint(String path, String sourcePage) {
        if (sourcePage == null || sourcePage.isBlank() || !path.startsWith("/")) {
            return path;
        }
        return org.springframework.web.util.UriComponentsBuilder.fromUriString(path)
                .queryParam("sourcePageHint", sourcePage)
                .build()
                .toUriString();
    }

    private boolean isValidSourcePageHint(String sourcePageHint) {
        return sourcePageHint != null
                && !sourcePageHint.isBlank()
                && sourcePageHint.startsWith("/")
                && !sourcePageHint.startsWith("//");
    }

    private String contentActionHeading(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Use the replacement estimate before you compare contractor quotes.";
            case "perc-test-cost" -> "Open a state perc page first.";
            case "drain-field-replacement-cost" -> "Open a state drain field page first.";
            case "failed-perc-test-septic" -> "Open a state failed-perc page first.";
            case "septic-replacement-area" -> "Use the field-layout estimate before you assume the parcel still has a viable backup area.";
            case "wet-yard-over-septic-drain-field" -> "Use the field-failure estimate before you treat a wet yard as a small repair story.";
            case "septic-inspection-cost" -> "Use the inspection-risk estimate after you know what the file is missing.";
            case "buying-a-house-with-a-septic-system" -> "Open a state buyer page first.";
            case PERMIT_LOOKUP_SLUG -> "Open a state permit lookup path first.";
            case "septic-permit-process" -> "Open a state permit page first.";
            case "septic-records-checklist" -> "Open a state records lookup first.";
            case TRANSFER_COMPLIANCE_SLUG -> "Open a state transfer page first.";
            case "septic-tank-size" -> "Open the tank size estimator before you guess the minimum gallon band.";
            case "septic-pumping-cost" -> "Open the pump schedule estimator before you assume a maintenance cadence.";
            default -> "Use the main estimator before you ask for quotes.";
        };
    }

    private String contentActionLabel(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Run a replacement planning estimate";
            case "perc-test-cost" -> "Open state perc pages";
            case "drain-field-replacement-cost" -> "Open state drain field pages";
            case "failed-perc-test-septic" -> "Open state failed-perc pages";
            case "septic-replacement-area" -> "Run a replacement-area estimate";
            case "wet-yard-over-septic-drain-field" -> "Run a field-failure estimate";
            case "septic-inspection-cost" -> "Run an inspection-scope estimate";
            case "buying-a-house-with-a-septic-system" -> "Open state buyer pages";
            case PERMIT_LOOKUP_SLUG -> "Open state permit lookup pages";
            case "septic-permit-process" -> "Open state permit pages";
            case "septic-records-checklist" -> "Open state records lookup pages";
            case TRANSFER_COMPLIANCE_SLUG -> "Open state transfer pages";
            case "septic-tank-size" -> "Open the tank size estimator";
            case "septic-pumping-cost" -> "Open the pump schedule estimator";
            default -> "Open the main estimator";
        };
    }

    private String contentActionNote(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Prefill the replacement lane first so field condition, restoration, and system-class risk show up before you talk price.";
            case "perc-test-cost" -> "Perc, soil-test, and site-evaluation language changes by state, so the state-specific page is the faster first move. Use the estimate after the local file and site path are clearer.";
            case "drain-field-replacement-cost" -> "Field replacement, reserve-area risk, and redesign pressure vary by state and file history, so the state-specific page is the faster first move. Use the estimate after the layout and records path are clearer.";
            case "failed-perc-test-septic" -> "Failed perc results turn into different permit, reserve-area, and redesign stories by state, so the state-specific page is the faster first move. Use the estimate after the review path is clearer.";
            case "septic-replacement-area" -> "Use the drain field lane when reserve area, replacement footprint, or code-complying layout risk is the main blocker.";
            case "wet-yard-over-septic-drain-field" -> "Use the drain field lane when seepage, odor, or soggy ground near the field is already visible.";
            case "septic-inspection-cost" -> "Pull the permit file, as-built, pumping history, and O&M records first, then use the estimate to judge whether the visit is routine diligence or leverage for a bigger next step.";
            case "buying-a-house-with-a-septic-system" -> "Transfer rules, county records, inspection triggers, and bedroom-use mismatches vary enough that the state-specific page is the faster first move.";
            case PERMIT_LOOKUP_SLUG -> "The useful lookup is usually state plus county: open the state records or permit path first, then follow the county file route when the state page exposes one.";
            case "septic-permit-process" -> "The first real answer is usually which office, file, or site-review step controls this property, so start with the state-specific permit page before you model the cost.";
            case "septic-records-checklist" -> "County records lookup, permit search, and as-built availability vary enough that the state-specific records page is the faster first move.";
            case TRANSFER_COMPLIANCE_SLUG -> "Transfer problems usually resolve through records, permit path, buyer timing, and county workflow, so open the state-specific page before you try to compress everything into one quote number.";
            case "septic-tank-size" -> "Use the dedicated estimator when bedroom count, occupancy profile, or disposal load matter more than a full project quote.";
            case "septic-pumping-cost" -> "Use the dedicated estimator when cadence, use profile, and tank size matter more than a one-time pumping invoice.";
            default -> "Use your state and project assumptions first, then verify locally.";
        };
    }

    private String contentActionTargetType(ContentPage contentPage) {
        if (shouldLeadWithStateWorkflow(contentPage)) {
            return "state_money_page_directory";
        }
        return switch (contentPage.calculatorModule()) {
            case "tank_size_estimator" -> "tank_size_estimator";
            case "pump_schedule_estimator" -> "pump_schedule_estimator";
            case "drainfield_estimator" -> "drainfield_estimator";
            default -> "calculator";
        };
    }

    private String secondaryActionLabel(ContentPage contentPage) {
        if (!shouldLeadWithStateWorkflow(contentPage)) {
            return null;
        }
        return switch (contentPage.slug()) {
            case "perc-test-cost" -> "Run a site-risk estimate";
            case "drain-field-replacement-cost" -> "Run a drain field replacement estimate";
            case "failed-perc-test-septic" -> "Run a failed-perc estimate";
            case "buying-a-house-with-a-septic-system" -> "Run a buyer due-diligence estimate";
            case PERMIT_LOOKUP_SLUG -> "Run a lookup-aware estimate";
            case "septic-permit-process" -> "Run a permit-path estimate";
            case "septic-records-checklist" -> "Run a records-aware estimate";
            case TRANSFER_COMPLIANCE_SLUG -> "Run a transfer-risk estimate";
            default -> "Open the main estimator";
        };
    }

    private String secondaryActionNote(ContentPage contentPage) {
        if (!shouldLeadWithStateWorkflow(contentPage)) {
            return null;
        }
        return switch (contentPage.slug()) {
            case "perc-test-cost" -> "Use the estimate after you know whether the real blocker is a perc result, a broader site evaluation, or county permit routing.";
            case "drain-field-replacement-cost" -> "Use the estimate after you know whether the live issue is field-only scope, replacement area, or a wider redesign path.";
            case "failed-perc-test-septic" -> "Use the estimate after you know whether the live issue is retesting, reserve area, permit routing, or a broader redesign path.";
            case "buying-a-house-with-a-septic-system" -> "Pull the permit file, as-built, pumping history, and bedroom-use story first, then use the estimate to judge whether the deal risk is routine diligence, a credit fight, or a wider replacement problem.";
            case PERMIT_LOOKUP_SLUG -> "Use the estimate after the lookup tells you whether the file gap is routine, a county routing problem, or a repair-permit risk.";
            case "septic-permit-process" -> "Use the estimate after you know whether the real blocker is authority routing, project classification, or site review.";
            case "septic-records-checklist" -> "Use the estimate after you know which missing record is actually changing the downside.";
            case TRANSFER_COMPLIANCE_SLUG -> "Use the estimate after you know whether the live issue is missing records, permit sequence, buyer inspection timing, or a county-file gap that changes the downside.";
            default -> "Use your state and project assumptions first, then verify locally.";
        };
    }

    private String secondaryActionTargetType(ContentPage contentPage) {
        if (!shouldLeadWithStateWorkflow(contentPage)) {
            return null;
        }
        return switch (contentPage.calculatorModule()) {
            case "tank_size_estimator" -> "tank_size_estimator";
            case "pump_schedule_estimator" -> "pump_schedule_estimator";
            case "drainfield_estimator" -> "drainfield_estimator";
            default -> "calculator";
        };
    }

    private boolean shouldLeadWithStateWorkflow(ContentPage contentPage) {
        return "workflow_page".equals(contentPage.intentType())
                || "buyer_page".equals(contentPage.intentType())
                || "perc-test-cost".equals(contentPage.slug())
                || "drain-field-replacement-cost".equals(contentPage.slug())
                || "failed-perc-test-septic".equals(contentPage.slug());
    }

    private boolean isTransferComplianceHub(ContentPage contentPage) {
        return TRANSFER_COMPLIANCE_SLUG.equals(contentPage.slug());
    }

    private boolean isPermitLookupHub(ContentPage contentPage) {
        return PERMIT_LOOKUP_SLUG.equals(contentPage.slug());
    }

    private String latestVerifiedAt(List<SourceRecord> sources, String fallback) {
        return sources.stream()
                .map(SourceRecord::lastVerifiedAt)
                .filter(value -> value != null && !value.isBlank())
                .max(String::compareTo)
                .orElse(fallback == null ? "" : fallback);
    }

    private StateActionCopy stateActionCopy(StateProfile state) {
        return switch (state.stateCode()) {
            case "IA" -> new StateActionCopy(
                    "Estimate before the county file pull",
                    "Iowa quote conversations get more real once you know which county office or county sanitarian holds the file and whether the time-of-transfer record is already in view."
            );
            case "KS" -> new StateActionCopy(
                    "Estimate before the soil-profile check",
                    "Kansas quote conversations get more real once you know which local sanitary code controls the parcel and whether the lot is still on a straightforward soil-profile path."
            );
            case "NE" -> new StateActionCopy(
                    "Estimate before the permit filing",
                    "Nebraska quote conversations get more real once you know whether the parcel already has a registered-system file and whether the state permit path is still clean."
            );
            case "NM" -> new StateActionCopy(
                    "Estimate before the buyer file check",
                    "New Mexico quote conversations get more real once you know whether the permit-search result, homeowner notice, and transfer paperwork support the seller story."
            );
            case "UT" -> new StateActionCopy(
                    "Estimate before the health-district handoff",
                    "Utah quote conversations get more real once you know which local health department owns the file and whether soil logs, percolation tests, or an operating-permit wrinkle are already in play."
            );
            case "WV" -> new StateActionCopy(
                    "Estimate after the local file check",
                    "West Virginia quotes get real after you confirm the local health file, the sewage permit application, and any sanitarian or public-record trail."
            );
            case "SD" -> new StateActionCopy(
                    "Estimate before the permit certificate",
                    "South Dakota quote conversations get more real once you know whether the installation certificate, inspection sequence, and any city-run inspection exception are already in play."
            );
            case "ID" -> new StateActionCopy(
                    "Estimate before the site evaluation",
                    "Idaho quote conversations get more real once you know which public health district owns the file and whether the site evaluation or permit record is already in play."
            );
            case "NV" -> new StateActionCopy(
                    "Estimate before the buyer file pull",
                    "Nevada quote conversations get more real once you know whether NDEP, Southern Nevada Health District, or another local path holds the file and whether the inspection and as-built trail is actually complete."
            );
            case "DE" -> new StateActionCopy(
                    "Estimate before the permit-file pull",
                    "Delaware quote conversations get more real once you know whether the DNREC report trail is usable and whether a county building-permit handoff changes the septic path."
            );
            case "ND" -> new StateActionCopy(
                    "Estimate before the local permit call",
                    "North Dakota quote conversations get more real once you know which local public health unit holds the permit file and whether inspection or complaint history is already in view."
            );
            case "WY" -> new StateActionCopy(
                    "Estimate before the county site check",
                    "Wyoming quote conversations get more real once you know which county issues the permit under DEQ delegation and whether perc, site-plan, or engineer-design friction is already in play."
            );
            case "AK" -> new StateActionCopy(
                    "Estimate before the buyer file pull",
                    "Alaska quote conversations get more real once you know whether the local DEC office or the Municipality of Anchorage holds the approved-system record and whether difficult-site notes already widen the path."
            );
            case "HI" -> new StateActionCopy(
                    "Estimate before the cesspool-upgrade path",
                    "Hawaii quote conversations get more real once you know whether a cesspool trigger is active and whether the county building-permit handoff and approval-to-use file are already in play."
            );
            case "ME" -> new StateActionCopy(
                    "Estimate before the buyer file pull",
                    "Maine quote conversations get more real once you know whether the town office can surface the HHE-200 and whether the Local Plumbing Inspector trail actually supports the property story."
            );
            case "NH" -> new StateActionCopy(
                    "Estimate before the approval-status check",
                    "New Hampshire quote conversations get more real once you know whether the property is operationally approved for the intended use and whether OneStop or archive records actually support the file story."
            );
            case "RI" -> new StateActionCopy(
                    "Estimate after the DEM file pull",
                    "Rhode Island quotes get real after you confirm the DEM search results, the full file, and any suitability or advanced-technology trigger."
            );
            case "VT" -> new StateActionCopy(
                    "Estimate before the regional-office handoff",
                    "Vermont quote conversations get more real once you know whether the parcel already has a state-issued permit, whether the Town changes the path, and which regional office owns the next filing."
            );
            case "MT" -> new StateActionCopy(
                    "Estimate before the lot-review check",
                    "Montana quote conversations get more real once you know whether the lot already has COSA or sanitary restrictions, whether the local health department still owns the drainfield permit, and whether DEQ-4 site-risk paperwork already widens the project."
            );
            case "AL" -> new StateActionCopy(
                    "Estimate before trusting permit cost or county records",
                    "Alabama quote conversations get more real once you know which county health department holds the file and whether a Permit to Install, soil test, or Approval for Use is already in view."
            );
            case "AR" -> new StateActionCopy(
                    "Estimate before the health-unit call",
                    "Arkansas quote conversations get more real once you know which county health unit holds the file and whether a permit copy or lot-suitability issue is already in view."
            );
            case "MS" -> new StateActionCopy(
                    "Estimate before the file lookup",
                    "Mississippi quote conversations get more real once you know whether the county health file and public-record trail can actually surface a site-evaluation record or Permit or Recommendation."
            );
            case "IN" -> new StateActionCopy(
                    "Estimate before the county permit call",
                    "Indiana quote conversations get more real once you know which county office holds the file and whether sewer availability or local ordinance variation changes the onsite path."
            );
            case "OK" -> new StateActionCopy(
                    "Estimate before the soil-test request",
                    "Oklahoma quote conversations get more real once you know which local DEQ office handles the parcel and whether the site still sits on a conventional soil-test path."
            );
            case "KY" -> new StateActionCopy(
                    "Estimate before the local health file pull",
                    "Kentucky quote conversations get more real once you know whether the local health department already holds the site-evaluation and permit file behind the property story."
            );
            case "MN" -> new StateActionCopy(
                    "Estimate before the disclosure check",
                    "Minnesota quote conversations get more real once you know which local SSTS program controls the sale and whether disclosure or compliance-inspection friction is already in play."
            );
            case "IL" -> new StateActionCopy(
                    "Estimate before the local file pull",
                    "Illinois quote conversations get more real once you know which county or local health department holds the file and whether an evaluation form or flagged condition is already in view."
            );
            case "MD" -> new StateActionCopy(
                    "Estimate before the property-transfer file search",
                    "Maryland quote conversations get more real once you know which local approving authority holds the file and whether a PTI-backed transfer record is already in play."
            );
            case "WI" -> new StateActionCopy(
                    "Estimate with county maintenance tracking in mind",
                    "Wisconsin quote conversations get more real once you know which county or delegated agent owns the file and whether maintenance-tracking and inspection records are current."
            );
            case "LA" -> new StateActionCopy(
                    "Estimate before the parish health unit call",
                    "Louisiana quote conversations get more real once you know whether the parish office still treats the parcel as a septic path and whether sewer availability or packet friction changes the site story."
            );
            case "AZ" -> new StateActionCopy(
                    "Estimate before site approval",
                    "Arizona quote conversations get more real once you know which county controls the permit file and whether the site-investigation paperwork is already on record."
            );
            case "CO" -> new StateActionCopy(
                    "Estimate before calling the local public health agency",
                    "Colorado quote conversations get more real once you know which local public health agency owns the file and whether site-and-soil or transfer-of-title paperwork is already in play."
            );
            case "VA" -> new StateActionCopy(
                    "Estimate before the local health-district call",
                    "Virginia quote conversations get more real once you know whether the system is conventional or AOSS and whether operation-permit or inspection records are already in the file."
            );
            case "TN" -> new StateActionCopy(
                    "Estimate before the permit-file pull",
                    "Tennessee quote conversations get more real once you know whether the parcel runs through a contract county or TDEC contact and whether a repair permit or inspection letter is already in the file."
            );
            case "SC" -> new StateActionCopy(
                    "Estimate after the permit-copy pull",
                    "South Carolina quotes get real after you confirm the SCDES office, the D-1740 path, the permit copy, and final-inspection status."
            );
            case "CA" -> new StateActionCopy(
                    "Estimate before the county file pull",
                    "California usually gets real once you know the local agency path and whether the property sits in a default Tier 1 workflow or a LAMP-driven local program."
            );
            case "TX" -> new StateActionCopy(
                    "Estimate before calling the permitting authority",
                    "Texas quote conversations get sharper once you know the local permitting authority and whether the site evaluation is already on file."
            );
            case "NY" -> new StateActionCopy(
                    "Estimate with Appendix 75-A context",
                    "New York questions often turn on Appendix 75-A, county health files, and any waiver history rather than the seller's simple septic summary."
            );
            case "OH" -> new StateActionCopy(
                    "Estimate before calling the health district",
                    "Ohio quote conversations get more real once you know which local health department holds the permit file and whether the property already has an operation-permit or inspection history."
            );
            case "MI" -> new StateActionCopy(
                    "Estimate before the local file pull",
                    "Michigan questions get more real once you know which local health department holds the file and whether failure evidence or system-location uncertainty is already on record."
            );
            case "GA" -> new StateActionCopy(
                    "Estimate after the county file pull",
                    "Georgia quotes get real after you confirm the county office, the permit file, the soil analysis, and the garbage-disposal sizing rule."
            );
            case "PA" -> new StateActionCopy(
                    "Estimate before calling the SEO",
                    "Pennsylvania often turns into a records and local SEO workflow fast, so it helps to walk in with a realistic planning range first."
            );
            case "CT" -> new StateActionCopy(
                    "Estimate with design flow context",
                    "Connecticut questions often turn on bedroom count and potential-bedroom logic, not just what fixtures you see today."
            );
            case "OR" -> new StateActionCopy(
                    "Estimate before site evaluation",
                    "Oregon homeowners usually need a planning range before the site evaluation and permit path narrow the real system options."
            );
            case "MA" -> new StateActionCopy(
                    "Estimate with Title 5 timing in mind",
                    "Massachusetts buyers and sellers usually need to line up the estimate with Title 5 timing, records, and inspection results."
            );
            case "FL" -> new StateActionCopy(
                    "Estimate after the county path check",
                    "Florida homeowners should confirm whether the local path runs through a county health department or a DEP-managed county before comparing quotes."
            );
            case "WA" -> new StateActionCopy(
                    "Estimate before calling the LHJ",
                    "Washington workflows usually move faster when you know whether the local health jurisdiction will ask for records, O&M history, or advanced-system context."
            );
            case "NJ" -> new StateActionCopy(
                    "Estimate with management rules in mind",
                    "New Jersey costs can shift once management-area rules, local health review, or Pinelands context enter the conversation."
            );
            case "NC" -> new StateActionCopy(
                    "Estimate before the permit ladder",
                    "North Carolina homeowners usually get better quote conversations when they understand the improvement-permit sequence before pricing systems."
            );
            case "MO" -> new StateActionCopy(
                    "Estimate after confirming local authority",
                    "Missouri can route homeowners through different county or local authorities, so the planning estimate is strongest after that first local check."
            );
            default -> new StateActionCopy(
                    "Open the main calculator",
                    "Use your state and project assumptions first, then verify the actual permit path locally."
            );
        };
    }

    private StatePlanningSnapshot statePlanningSnapshot(String stateCode) {
        StateCostProfile costProfile = researchDataService.findStateCostProfile(stateCode).orElse(null);
        if (costProfile == null) {
            return null;
        }

        ProjectCostAnchor nationalReplacement = researchDataService.findNationalAnchor("replacement").orElse(null);
        int nationalReplacementMid = nationalReplacement != null ? nationalReplacement.mid() : 0;
        String comparisonNote = nationalReplacementMid > 0
                ? replacementComparison(costProfile.replacementMid(), nationalReplacementMid)
                : "Planning-only snapshot built from public cost anchors and broad state price-level adjustments.";

        return new StatePlanningSnapshot(
                money(costProfile.installMid()),
                money(costProfile.replacementMid()),
                range(costProfile.percLow(), costProfile.percHigh()),
                range(costProfile.pumpingLow(), costProfile.pumpingHigh()),
                comparisonNote
        );
    }

    private String replacementComparison(Integer replacementMid, int nationalReplacementMid) {
        if (replacementMid == null) {
            return "Replacement planning midpoint is still under review for this state.";
        }

        int delta = replacementMid - nationalReplacementMid;
        int percent = (int) Math.round(Math.abs(delta) * 100.0 / nationalReplacementMid);
        if (Math.abs(delta) < 250) {
            return "Replacement planning midpoint runs close to the current national planning midpoint.";
        }
        if (delta > 0) {
            return "Replacement planning midpoint runs about " + percent + "% above the current national planning midpoint.";
        }
        return "Replacement planning midpoint runs about " + percent + "% below the current national planning midpoint.";
    }

    private String money(Integer amount) {
        if (amount == null) {
            return "Under review";
        }
        return String.format(Locale.US, "$%,d", amount);
    }

    private String range(Integer low, Integer high) {
        if (low == null || high == null) {
            return "Under review";
        }
        return money(low) + " to " + money(high);
    }

    private List<CoreStateComparisonRow> coreStateComparisonRows(StateProfile currentState) {
        if (!CORE_STATE_CODES.contains(currentState.stateCode())) {
            return List.of();
        }

        return CORE_STATE_CODES.stream()
                .map(researchDataService::findStateByCode)
                .flatMap(Optional::stream)
                .map(state -> new CoreStateComparisonRow(
                        state.stateName(),
                        state.slug(),
                        state.whoToCallFirst(),
                        firstListItem(state.recordsToRequest(), "Local septic permit and inspection records."),
                        firstListItem(state.lowEndRiskChecks(), "Local review and site constraints can erase the low end quickly."),
                        nextBestIntentTitle(state),
                        nextBestIntentPath(state),
                        state.stateCode().equals(currentState.stateCode())
                ))
                .toList();
    }

    private StateQueuePlanView stateQueuePlanView(StateQueuePlan plan) {
        UsStateDirectoryService.UsStateReference stateReference = usStateDirectoryService.findByCode(plan.stateCode())
                .orElseThrow(() -> new StateNotFoundException(plan.stateCode()));
        return stateQueuePlanView(plan, stateReference);
    }

    private StateQueuePlanView stateQueuePlanView(StateQueuePlan plan, UsStateDirectoryService.UsStateReference stateReference) {
        return new StateQueuePlanView(
                stateReference.stateCode(),
                stateReference.stateName(),
                plan.priorityRank(),
                plan.rolloutWave(),
                plan.whyNow(),
                plan.launchAngle(),
                "/septic-system-cost-calculator/" + stateReference.slug() + "/",
                pageLink(plan.recommendedPath(), "septic-system-cost-calculator", null),
                plan.researchTasks()
        );
    }

    private String nextBestIntentTitle(StateProfile state) {
        return findPriorityStateMoneyPage(state)
                .map(StateMoneyPage::title)
                .orElse("Open the main cost calculator");
    }

    private String nextBestIntentPath(StateProfile state) {
        return findPriorityStateMoneyPage(state)
                .map(page -> page.path(state.slug()))
                .orElse("/septic-system-cost-calculator/?state=" + state.stateCode());
    }

    private Optional<StateMoneyPage> findPriorityStateMoneyPage(StateProfile state) {
        return researchDataService.listPublicStateMoneyPages(state.stateCode()).stream()
                .max(Comparator
                        .comparingInt((StateMoneyPage page) -> stateMoneyPagePriorityScore(state, page))
                        .thenComparing(StateMoneyPage::title));
    }

    private ContentEvidenceLaneView contentEvidenceLane(StateMoneyPage page, StateProfile state) {
        List<SourceRecord> sources = researchDataService.getSources(page.officialSourceIds()).stream()
                .limit(3)
                .toList();
        String lastReviewedAt = latestVerifiedAt(sources, state.lastVerifiedAt());
        String reviewedAgainst = "Reviewed against " + sources.size() + " official source" + (sources.size() == 1 ? "" : "s")
                + " tied to the " + state.stateName() + " workflow.";
        return new ContentEvidenceLaneView(
                page.title(),
                state.stateName(),
                page.path(state.slug()),
                page.uniqueAngle(),
                reviewedAgainst,
                lastReviewedAt,
                sources
        );
    }

    private ContentWorkflowCoverageView contentWorkflowCoverage(
            ContentPage contentPage,
            List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntries
    ) {
        List<Map.Entry<StateMoneyPage, StateProfile>> featuredEntries = rankedStateEntries.stream()
                .limit(6)
                .toList();
        if (featuredEntries.isEmpty()) {
            return null;
        }

        List<String> stateCodes = featuredEntries.stream()
                .map(entry -> entry.getValue().stateCode())
                .distinct()
                .toList();
        long stateCount = stateCodes.size();
        long statePagesCount = featuredEntries.size();
        long countyCount = stateCodes.stream()
                .mapToLong(code -> researchDataService.listPublicCountyRecordsPages(code).size())
                .sum();
        long countyBackedStates = stateCodes.stream()
                .filter(code -> !researchDataService.listPublicCountyRecordsPages(code).isEmpty())
                .count();

        String heading = "What the live state pages already resolve";
        String summary = "This national page is backed by " + statePagesCount + " source-backed state workflow pages across "
                + stateCount + " states, with " + countyCount + " live county workflow pages already underneath those states.";
        List<String> bullets = List.of(
                countyBackedStates + " of those states already route users into county-first follow-up before pricing.",
                "The linked state pages are where file owner, permit closeout, transfer artifact, and quote-gate differences stop being generic.",
                "Use this national page to frame the problem, then move into the state page once you need a real office, file, or county branch."
        );
        return new ContentWorkflowCoverageView(heading, summary, bullets);
    }

    private record StateCountyBackbone(
            StateProfile state,
            int countyCount,
            int workflowPageCount,
            PageLink link
    ) {
    }

    @SafeVarargs
    private final List<SourceRecord> workflowPacketSources(List<String>... sourceIdGroups) {
        return Stream.of(sourceIdGroups)
                .filter(java.util.Objects::nonNull)
                .flatMap(List::stream)
                .filter(sourceId -> sourceId != null && !sourceId.isBlank())
                .distinct()
                .map(researchDataService::findSource)
                .flatMap(Optional::stream)
                .limit(5)
                .toList();
    }

    private List<PageLink> countyRecordPageLinks(String stateCode) {
        return researchDataService.listPublicCountyRecordsPages(stateCode).stream()
                .sorted(Comparator
                        .comparingInt(this::countyRecordPriorityScore)
                        .reversed()
                        .thenComparing(CountyRecordsPage::countyName))
                .map(page -> researchDataService.findStateByCode(page.stateCode())
                        .map(state -> new PageLink(
                                page.title(),
                                page.path(state.slug()),
                                page.uniqueAngle()
                        )))
                .flatMap(Optional::stream)
                .toList();
    }

    private List<PageLink> permitLookupCountyLaunchpadLinks(ContentPage contentPage) {
        if (!isPermitLookupHub(contentPage)) {
            return List.of();
        }
        return List.of(
                        countyLaunchpadLink("tennessee", "davidson-county"),
                        countyLaunchpadLink("tennessee", "knox-county"),
                        countyLaunchpadLink("tennessee", "shelby-county"),
                        countyLaunchpadLink("north-carolina", "wake-county"),
                        countyLaunchpadLink("north-carolina", "alamance-county"),
                        countyLaunchpadLink("north-carolina", "union-county"),
                        countyLaunchpadLink("north-carolina", "johnston-county"),
                        countyLaunchpadLink("texas", "travis-county"),
                        countyLaunchpadLink("texas", "hays-county"),
                        countyLaunchpadLink("texas", "montgomery-county"),
                        countyLaunchpadLink("texas", "fort-bend-county"),
                        countyLaunchpadLink("texas", "brazoria-county"),
                        countyLaunchpadLink("alabama", "madison-county"),
                        countyLaunchpadLink("alabama", "baldwin-county"),
                        countyLaunchpadLink("alabama", "shelby-county"),
                        countyLaunchpadLink("alabama", "tuscaloosa-county"),
                        countyLaunchpadLink("indiana", "elkhart-county"),
                        countyLaunchpadLink("indiana", "st-joseph-county"),
                        countyLaunchpadLink("indiana", "porter-county"),
                        countyLaunchpadLink("indiana", "marshall-county")
                ).stream()
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<PageLink> countyLaunchpadLink(String stateSlug, String countySlug) {
        return researchDataService.findPublicCountyRecordsPage(stateSlug, countySlug)
                .flatMap(page -> researchDataService.findStateByCode(page.stateCode())
                        .map(state -> new PageLink(
                                page.countyName() + " permit lookup",
                                page.path(state.slug()),
                                countyLaunchpadNote(page, state)
                        )));
    }

    private String countyLaunchpadNote(CountyRecordsPage page, StateProfile state) {
        return page.countyName() + " routes " + state.stateName()
                + " septic permit lookup traffic into " + page.recordsLabel()
                + " before the user trusts a quote, sale file, repair story, or new permit path.";
    }

    private int countyRecordPriorityScore(CountyRecordsPage page) {
        int score = switch (page.stateCode()) {
            case "TN" -> 70;
            case "NC" -> 64;
            case "TX" -> 62;
            case "AL" -> 58;
            case "IN" -> 54;
            default -> 20;
        };
        score += switch (page.countySlug()) {
            case "davidson-county", "wake-county", "travis-county", "madison-county", "elkhart-county", "howard-county" -> 34;
            case "knox-county", "alamance-county", "hays-county", "baldwin-county", "st-joseph-county" -> 28;
            case "shelby-county", "union-county", "montgomery-county", "shelby-county-alabama", "porter-county" -> 24;
            case "johnston-county", "fort-bend-county", "tuscaloosa-county", "marshall-county" -> 20;
            case "chatham-county", "orange-county", "brazoria-county", "lee-county" -> 16;
            case "mecklenburg-county", "williamson-county", "limestone-county", "floyd-county" -> 12;
            default -> 0;
        };
        score += Math.min(size(page.officialSourceIds()), 4) * 2;
        if (page.recordsLabel() != null && page.recordsLabel().toLowerCase(Locale.US).contains("search")) {
            score += 5;
        }
        if (page.recordsLabel() != null && page.recordsLabel().toLowerCase(Locale.US).contains("lookup")) {
            score += 5;
        }
        return score;
    }

    private CountyWorkflowStructureView countyWorkflowStructure(CountyRecordsPage countyPage, StateProfile state) {
        String combinedText = countyCombinedText(countyPage);
        CountyWorkflowStructureData structure = countyPage.workflowStructure();
        List<CountyWorkflowFieldView> fields = List.of(
                new CountyWorkflowFieldView("File owner model", firstNonBlank(
                        structure == null ? null : structure.fileOwnerModel(),
                        countyFileOwnerModel(countyFileOwnerCategory(countyPage, combinedText), state.stateName())
                )),
                new CountyWorkflowFieldView("First artifact to pull", firstNonBlank(
                        structure == null ? null : structure.firstArtifactToPull(),
                        countyFirstArtifact(countyPage)
                )),
                new CountyWorkflowFieldView("Permit closeout signal", firstNonBlank(
                        structure == null ? null : structure.permitCloseoutSignal(),
                        countyPermitCloseoutSignal(countyPermitCloseoutCategory(countyPage, combinedText))
                )),
                new CountyWorkflowFieldView("Transfer or buyer artifact", firstNonBlank(
                        structure == null ? null : structure.transferArtifact(),
                        countyTransferArtifact(countyTransferCategory(countyPage, combinedText))
                )),
                new CountyWorkflowFieldView("Special program or local exception", firstNonBlank(
                        structure == null ? null : structure.specialProgramSignal(),
                        countySpecialProgramSignal(countySpecialProgramCategory(countyPage, combinedText))
                )),
                new CountyWorkflowFieldView("Malfunction or repair trail", firstNonBlank(
                        structure == null ? null : structure.malfunctionSignal(),
                        countyMalfunctionSignal(countyMalfunctionCategory(countyPage, combinedText))
                ))
        );
        return new CountyWorkflowStructureView(fields, firstNonBlank(
                structure == null ? null : structure.quoteGate(),
                countyQuoteGate(countyPage, combinedText)
        ));
    }

    private String countyCombinedText(CountyRecordsPage page) {
        return String.join(" ",
                nullSafe(page.introCopy()),
                nullSafe(page.uniqueAngle()),
                nullSafe(page.targetReader()),
                nullSafe(page.officeLabel()),
                nullSafe(page.recordsLabel()),
                String.join(" ", page.decisionSteps() == null ? List.of() : page.decisionSteps()),
                String.join(" ", page.recordsToRequest() == null ? List.of() : page.recordsToRequest()),
                String.join(" ", page.lowEndBreakers() == null ? List.of() : page.lowEndBreakers())
        ).toLowerCase(Locale.US);
    }

    private String countyFileOwnerModel(String combinedText, StateProfile state) {
        return countyFileOwnerModel(countyFileOwnerCategoryText(combinedText), state.stateName());
    }

    private String countyFileOwnerCategoryText(String combinedText) {
        if (containsAny(combinedText, "board of health", "ceha", "municipal", "municipality", "incorporated town")) {
            return "split_local";
        }
        if (containsAny(combinedText, "local health department", "health district", "regional office", "contract county")) {
            return "district_health";
        }
        return "county_first";
    }

    private String countyFirstArtifact(CountyRecordsPage countyPage) {
        if (countyPage.recordsToRequest() != null && !countyPage.recordsToRequest().isEmpty()) {
            return countyPage.recordsToRequest().get(0);
        }
        return "The first county file or permit artifact tied to the parcel.";
    }

    private String countyPermitCloseoutSignal(String category) {
        return countyPermitCloseoutSignalFromCategory(category);
    }

    private String countyTransferArtifact(String category) {
        return countyTransferArtifactFromCategory(category);
    }

    private String countySpecialProgramSignal(String category) {
        return countySpecialProgramSignalFromCategory(category);
    }

    private String countyMalfunctionSignal(String category) {
        return countyMalfunctionSignalFromCategory(category);
    }

    private String countyQuoteGate(CountyRecordsPage countyPage, String combinedText) {
        if (!"generic".equals(countyMalfunctionCategory(countyPage, combinedText))) {
            return "Do not move into pricing until the county repair or malfunction trail is clear, because the job may already be wider than the visible system story.";
        }
        if (!"county_first".equals(countyFileOwnerCategory(countyPage, combinedText))) {
            return "Do not move into pricing until you know which office actually owns the file, because a split authority path can make the first answer misleading.";
        }
        if (!"generic".equals(countyPermitCloseoutCategory(countyPage, combinedText))) {
            return "Do not move into pricing until the county closeout artifact is visible, because the permit mention alone may not prove the system is actually cleared for use.";
        }
        return "Do not move into pricing until the county file is strong enough to show the right parcel, the right office, and the last real approval or inspection signal.";
    }

    private boolean supportsCountyWorkflowSummary(String contentSlug) {
        return "septic-records-checklist".equals(contentSlug)
                || "septic-permit-process".equals(contentSlug)
                || "buying-a-house-with-a-septic-system".equals(contentSlug)
                || isWorkflowCostSlug(contentSlug);
    }

    private boolean isWorkflowCostSlug(String contentSlug) {
        return isReplacementWorkflowCostSlug(contentSlug)
                || isInspectionWorkflowCostSlug(contentSlug)
                || isPercWorkflowCostSlug(contentSlug)
                || isPumpingWorkflowCostSlug(contentSlug);
    }

    private boolean isReplacementWorkflowCostSlug(String contentSlug) {
        return "septic-replacement-cost".equals(contentSlug)
                || "drain-field-replacement-cost".equals(contentSlug)
                || "failed-perc-test-septic".equals(contentSlug)
                || "septic-replacement-area".equals(contentSlug)
                || "wet-yard-over-septic-drain-field".equals(contentSlug);
    }

    private boolean isInspectionWorkflowCostSlug(String contentSlug) {
        return "septic-inspection-cost".equals(contentSlug);
    }

    private boolean isPercWorkflowCostSlug(String contentSlug) {
        return "perc-test-cost".equals(contentSlug);
    }

    private boolean isPumpingWorkflowCostSlug(String contentSlug) {
        return "septic-pumping-cost".equals(contentSlug);
    }

    private StateCountyWorkflowSynthesisView stateCountyWorkflowSynthesis(StateMoneyPage stateMoneyPage, StateProfile state) {
        if (!supportsCountyWorkflowSummary(stateMoneyPage.contentSlug())) {
            return null;
        }
        List<CountyRecordsPage> countyPages = researchDataService.listPublicCountyRecordsPages(state.stateCode());
        if (countyPages.size() < 2) {
            return null;
        }

        List<CountyPatternType> topPatterns = Stream.of(CountyPatternType.values())
                .filter(pattern -> countyPages.stream().anyMatch(page -> countyMatchesPattern(page, pattern)))
                .sorted(Comparator
                        .comparingInt((CountyPatternType pattern) -> countyPagesMatchingPattern(countyPages, pattern).size())
                        .reversed()
                        .thenComparing(Comparator.comparingInt(
                                (CountyPatternType pattern) -> countyPatternPriority(pattern, stateMoneyPage.contentSlug())
                        ).reversed())
                        .thenComparingInt(CountyPatternType::displayOrder))
                .limit(3)
                .toList();

        if (topPatterns.isEmpty()) {
            return null;
        }

        List<StateCountyWorkflowSignalView> signals = topPatterns.stream()
                .map(pattern -> {
                    List<CountyRecordsPage> matches = countyPagesMatchingPattern(countyPages, pattern);
                    String exampleCounties = matches.stream()
                            .map(CountyRecordsPage::countyName)
                            .distinct()
                            .limit(3)
                            .reduce((left, right) -> left + ", " + right)
                            .orElse(state.stateName() + " county pages");
                    String coverageNote = "Seen across " + matches.stream()
                            .map(CountyRecordsPage::countySlug)
                            .distinct()
                            .count() + " live county pages.";
                    return new StateCountyWorkflowSignalView(
                            pattern.label(),
                            pattern.summary(state.stateName()),
                            exampleCounties,
                            coverageNote,
                            pattern.firstArtifact()
                    );
                })
                .toList();

        List<CountyWorkflowFieldView> structureHighlights = List.of(
                stateStructureHighlight(
                        "Most common file owner pattern",
                        countyPages,
                        this::countyFileOwnerCategory,
                        category -> countyFileOwnerAggregateText(category, state.stateName())
                ),
                stateStructureHighlight(
                        "Most common permit closeout signal",
                        countyPages,
                        this::countyPermitCloseoutCategory,
                        this::countyPermitCloseoutAggregateText
                ),
                stateStructureHighlight(
                        "Most common buyer or transfer artifact",
                        countyPages,
                        this::countyTransferCategory,
                        this::countyTransferAggregateText
                ),
                stateStructureHighlight(
                        "Most common special program or exception",
                        countyPages,
                        this::countySpecialProgramCategory,
                        this::countySpecialProgramAggregateText
                ),
                stateStructureHighlight(
                        "Most common malfunction or repair trail",
                        countyPages,
                        this::countyMalfunctionCategory,
                        this::countyMalfunctionAggregateText
                ),
                stateStructureHighlight(
                        "Most common quote gate",
                        countyPages,
                        this::countyQuoteGateCategory,
                        this::countyQuoteGateAggregateText
                )
        );

        List<String> firstArtifacts = topPatterns.stream()
                .map(CountyPatternType::firstArtifact)
                .distinct()
                .limit(4)
                .toList();

        List<String> countyDropTriggers = topPatterns.stream()
                .map(CountyPatternType::countyDropTrigger)
                .distinct()
                .limit(4)
                .toList();

        List<String> holdQuoteChecks = topPatterns.stream()
                .map(CountyPatternType::holdQuoteCheck)
                .distinct()
                .limit(4)
                .toList();

        return new StateCountyWorkflowSynthesisView(
                countyWorkflowEyebrow(stateMoneyPage.contentSlug()),
                countyWorkflowHeading(stateMoneyPage.contentSlug(), state.stateName()),
                countyWorkflowIntro(stateMoneyPage.contentSlug(), state.stateName(), countyPages.stream()
                        .map(CountyRecordsPage::countySlug)
                        .distinct()
                        .count()),
                signals,
                structureHighlights,
                firstArtifacts,
                countyDropTriggers,
                holdQuoteChecks,
                countyWorkflowFirstArtifactsHeading(stateMoneyPage.contentSlug()),
                countyWorkflowDropHeading(stateMoneyPage.contentSlug()),
                countyWorkflowHoldQuoteHeading(stateMoneyPage.contentSlug())
        );
    }

    private CountyWorkflowFieldView stateStructureHighlight(
            String label,
            List<CountyRecordsPage> countyPages,
            java.util.function.Function<CountyRecordsPage, String> categoryResolver,
            java.util.function.Function<String, String> textResolver
    ) {
        java.util.Map<String, Long> counts = countyPages.stream()
                .map(categoryResolver)
                .collect(Collectors.groupingBy(category -> category, LinkedHashMap::new, Collectors.counting()));

        String topCategory = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("generic");

        long topCount = counts.getOrDefault(topCategory, 0L);
        return new CountyWorkflowFieldView(
                label,
                textResolver.apply(topCategory) + " Seen in " + topCount + " county pages."
        );
    }

    private String countyFileOwnerCategory(CountyRecordsPage countyPage) {
        return countyFileOwnerCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyFileOwnerCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().fileOwnerCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        return countyFileOwnerCategoryText(combinedText);
    }

    private String countyFileOwnerModel(String category, String stateName) {
        return switch (category) {
            case "split_local" -> "The county path is split. Confirm whether the real septic file sits with the county, a municipality, or a local board before treating one office as the full answer.";
            case "district_health" -> "The real file likely lives with a county or district health office rather than a generic statewide desk. Confirm the exact local office before moving into pricing.";
            case "county_engineer" -> "The real file is county-first here and usually runs through a named engineering or development-services office rather than a generic statewide desk.";
            case "county_public_health", "county_environmental_health", "county_first" -> "The real file is county-first here once you reach the named local health or environmental office.";
            default -> stateName + " looks county-first here. Start with the named county office and only widen out if the file points to another local authority.";
        };
    }

    private String countyFileOwnerAggregateText(String category, String stateName) {
        return switch (category) {
            case "split_local" -> "Many county workflows in " + stateName + " split the real file between county health, a municipality, or a local board.";
            case "district_health" -> "Many county workflows in " + stateName + " still turn on identifying the correct district or local health office first.";
            case "county_engineer" -> "Many county workflows in " + stateName + " are county-first once you reach the named engineering or development-services office.";
            case "county_public_health", "county_environmental_health", "county_first" -> "Many county workflows in " + stateName + " are county-first once you reach the named local health or environmental office.";
            default -> "Many county workflows in " + stateName + " are county-first once you reach the named local office.";
        };
    }

    private String countyPermitCloseoutCategory(CountyRecordsPage countyPage) {
        return countyPermitCloseoutCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyPermitCloseoutCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().permitCloseoutCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        if (containsAny(combinedText, "operation permit", "license-to-operate")) {
            return "use_approval";
        }
        if (containsAny(combinedText, "completion certificate", "certificate of completion", "certificate of occupancy", "final inspection")) {
            return "completion_artifact";
        }
        if (containsAny(combinedText, "construction authorization", "improvement permit", "sanitary construction permit")) {
            return "permit_ladder";
        }
        return "generic";
    }

    private String countyPermitCloseoutSignalFromCategory(String category) {
        return switch (category) {
            case "use_approval" -> "Look for the operating or use approval, because the county treats that as the proof that the system can still be used as described.";
            case "completion_artifact" -> "Look for the closeout or completion artifact, not just the application. The county workflow suggests the job is not really closed without that final signal.";
            case "permit_ladder" -> "The permit ladder matters here. Pull the approval step that shows the county moved beyond preliminary review into an actual buildable path.";
            default -> "Do not stop at the first permit mention. Look for the county artifact that proves the job actually cleared the last approval step.";
        };
    }

    private String countyPermitCloseoutAggregateText(String category) {
        return switch (category) {
            case "use_approval" -> "The most common county closeout signal is an operating or use approval rather than a bare permit application.";
            case "completion_artifact" -> "The most common county closeout signal is a completion or final inspection artifact.";
            case "permit_ladder" -> "The most common county closeout signal is a permit ladder step that proves the parcel moved beyond preliminary review.";
            default -> "County files often need a stronger closeout artifact than the first permit mention.";
        };
    }

    private String countyTransferCategory(CountyRecordsPage countyPage) {
        return countyTransferCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyTransferCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().transferCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        if (containsAny(combinedText, "pti", "property status report", "real-estate evaluation", "transfer inspection", "inspection letter")) {
            return "formal_transfer_artifact";
        }
        if (containsAny(combinedText, "closing", "buyer", "seller", "refinance", "loan")) {
            return "deal_side_risk";
        }
        return "generic";
    }

    private String countyTransferArtifactFromCategory(String category) {
        return switch (category) {
            case "formal_transfer_artifact" -> "This county has a buyer-side artifact that matters more than a generic permit copy. Pull the transfer or status document before you treat the sale as routine.";
            case "deal_side_risk" -> "The county file already hints at deal-side risk. Pull the inspection or transaction-facing document before you negotiate credits or timing.";
            default -> "If the property is being sold, ask whether the county keeps a buyer, transfer, or status artifact that changes closing risk.";
        };
    }

    private String countyTransferAggregateText(String category) {
        return switch (category) {
            case "formal_transfer_artifact" -> "The most common buyer-side county artifact is a formal transfer, status, or real-estate evaluation record.";
            case "deal_side_risk" -> "County pages in this state often surface buyer, seller, or lender risk before the deal reaches pricing.";
            default -> "County pages still matter for buyer diligence even when the transfer artifact is less standardized.";
        };
    }

    private String countySpecialProgramCategory(CountyRecordsPage countyPage) {
        return countySpecialProgramCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countySpecialProgramCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().specialProgramCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        if (containsAny(combinedText, "bay restoration fund", "brf", "bat", "critical area")) {
            return "grant_upgrade";
        }
        if (containsAny(combinedText, "pinelands", "service contract", "management plan", "management program", "o&m", "maintenance permit")) {
            return "managed_obligation";
        }
        if (containsAny(combinedText, "sewer", "lamp", "nitrogen", "reserve", "special-area")) {
            return "local_exception";
        }
        return "generic";
    }

    private String countySpecialProgramSignalFromCategory(String category) {
        return switch (category) {
            case "grant_upgrade" -> "A grant or upgrade program may already control the path. Treat BRF, BAT, or Critical Area paperwork as part of the real file, not as optional context.";
            case "managed_obligation" -> "There may be a long-tail management obligation on the property. Pull the service, management, or maintenance file before treating ownership costs as simple.";
            case "local_exception" -> "A local exception or area rule may already be changing the septic path. Check that program file before trusting the easiest replacement or reuse story.";
            default -> "Ask whether this parcel sits in any local program, exception area, or managed lane that changes the normal septic workflow.";
        };
    }

    private String countySpecialProgramAggregateText(String category) {
        return switch (category) {
            case "grant_upgrade" -> "County pages in this state often route through BRF, BAT, Critical Area, or another upgrade-program file before replacement is straightforward.";
            case "managed_obligation" -> "County pages in this state often surface management plans, service contracts, or long-tail O&M obligations before the file is really clean.";
            case "local_exception" -> "County pages in this state often turn on a local exception, sewer branch, reserve-area limit, or other area rule before the normal path applies.";
            default -> "County pages in this state still need a special-program check even when no single program dominates the workflow.";
        };
    }

    private String countyMalfunctionCategory(CountyRecordsPage countyPage) {
        return countyMalfunctionCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyMalfunctionCategory(CountyRecordsPage countyPage, String combinedText) {
        String explicitCategory = countyPage.workflowStructure() == null ? null : countyPage.workflowStructure().malfunctionCategory();
        if (hasText(explicitCategory)) {
            return explicitCategory;
        }
        if (containsAny(combinedText, "malfunction", "complaint", "violation", "failing system")) {
            return "complaint_trail";
        }
        if (containsAny(combinedText, "repair permit", "repair questionnaire", "repair area", "off-lot discharge")) {
            return "repair_branch";
        }
        return "generic";
    }

    private String countyMalfunctionSignalFromCategory(String category) {
        return switch (category) {
            case "complaint_trail" -> "There is a live failure or complaint trail in play. That history is usually more important than the first quote or seller summary.";
            case "repair_branch" -> "The county repair branch matters here. Pull the repair or failure-side file before assuming the cheapest visible scope is still available.";
            default -> "If there are signs of failure, ask for the repair, complaint, or malfunction trail before you trust a clean-looking system story.";
        };
    }

    private String countyMalfunctionAggregateText(String category) {
        return switch (category) {
            case "complaint_trail" -> "County pages in this state often surface a complaint, violation, or failing-system trail before any clean pricing story is safe.";
            case "repair_branch" -> "County pages in this state often move into a repair, malfunction, or off-lot-discharge branch before the low-end scope is real.";
            default -> "County pages in this state still reward checking the repair or malfunction side before trusting the simplest system story.";
        };
    }

    private String countyQuoteGateCategory(CountyRecordsPage countyPage) {
        return countyQuoteGateCategory(countyPage, countyCombinedText(countyPage));
    }

    private String countyQuoteGateCategory(CountyRecordsPage countyPage, String combinedText) {
        if (!"generic".equals(countyMalfunctionCategory(countyPage, combinedText))) {
            return "repair_path";
        }
        if (countyFileOwnerNeedsOfficeResolution(countyFileOwnerCategory(countyPage, combinedText))) {
            return "office_split";
        }
        if (!"generic".equals(countyPermitCloseoutCategory(countyPage, combinedText))) {
            return "closeout_artifact";
        }
        if (!"generic".equals(countyTransferCategory(countyPage, combinedText))) {
            return "buyer_artifact";
        }
        return "county_file";
    }

    private boolean countyFileOwnerNeedsOfficeResolution(String category) {
        return "split_local".equals(category) || "office_split".equals(category);
    }

    private String countyQuoteGateAggregateText(String category) {
        return switch (category) {
            case "repair_path" -> "The most common quote gate is a repair, malfunction, or failing-system branch that has to be cleared before pricing is trustworthy.";
            case "office_split" -> "The most common quote gate is figuring out which local office actually owns the file before a buyer or contractor trusts the first answer.";
            case "closeout_artifact" -> "The most common quote gate is waiting for the county closeout or use artifact instead of trusting the first permit mention.";
            case "buyer_artifact" -> "The most common quote gate is pulling the buyer-side or transfer artifact before the property story reaches pricing.";
            default -> "The most common quote gate is making sure the county file still proves the right parcel, the right office, and the last real approval signal.";
        };
    }

    private int countyPatternPriority(CountyPatternType pattern, String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> switch (pattern) {
                case PERMIT -> 6;
                case AUTHORITY -> 5;
                case LOOKUP -> 4;
                case BRF -> 3;
                case COMPLAINT -> 2;
                case TRANSFER -> 1;
            };
            case "buying-a-house-with-a-septic-system" -> switch (pattern) {
                case TRANSFER -> 6;
                case LOOKUP -> 5;
                case AUTHORITY -> 4;
                case PERMIT -> 3;
                case BRF -> 2;
                case COMPLAINT -> 1;
            };
            case "septic-inspection-cost" -> switch (pattern) {
                case LOOKUP -> 6;
                case COMPLAINT -> 5;
                case TRANSFER -> 4;
                case AUTHORITY -> 3;
                case PERMIT -> 2;
                case BRF -> 1;
            };
            case "perc-test-cost" -> switch (pattern) {
                case PERMIT -> 6;
                case LOOKUP -> 5;
                case AUTHORITY -> 4;
                case BRF -> 3;
                case COMPLAINT -> 2;
                case TRANSFER -> 1;
            };
            case "septic-pumping-cost" -> switch (pattern) {
                case LOOKUP -> 6;
                case TRANSFER -> 5;
                case AUTHORITY -> 4;
                case COMPLAINT -> 3;
                case PERMIT -> 2;
                case BRF -> 1;
            };
            default -> switch (pattern) {
                case LOOKUP -> isReplacementWorkflowCostSlug(contentSlug) ? 4 : 6;
                case AUTHORITY -> isReplacementWorkflowCostSlug(contentSlug) ? 3 : 5;
                case PERMIT -> isReplacementWorkflowCostSlug(contentSlug) ? 5 : 4;
                case TRANSFER -> isReplacementWorkflowCostSlug(contentSlug) ? 1 : 3;
                case BRF -> isReplacementWorkflowCostSlug(contentSlug) ? 4 : 2;
                case COMPLAINT -> isReplacementWorkflowCostSlug(contentSlug) ? 6 : 1;
            };
        };
    }

    private String countyWorkflowEyebrow(String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "County Permit Summary";
            case "buying-a-house-with-a-septic-system" -> "County Buyer Summary";
            case "septic-inspection-cost" -> "County Inspection Summary";
            case "perc-test-cost" -> "County Site-Review Summary";
            case "septic-pumping-cost" -> "County Maintenance Summary";
            default -> isReplacementWorkflowCostSlug(contentSlug) ? "County Replacement Summary" : "State Pattern Summary";
        };
    }

    private String countyWorkflowHeading(String contentSlug, String stateName) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "How county permit paths usually break down in " + stateName;
            case "buying-a-house-with-a-septic-system" -> "How county due diligence usually breaks down in " + stateName;
            case "septic-inspection-cost" -> "How county inspection files usually break down in " + stateName;
            case "perc-test-cost" -> "How county site-review files usually break down in " + stateName;
            case "septic-pumping-cost" -> "How county maintenance files usually break down in " + stateName;
            default -> isReplacementWorkflowCostSlug(contentSlug)
                    ? "How county replacement files usually break down in " + stateName
                    : "How county files usually break down in " + stateName;
        };
    }

    private String countyWorkflowIntro(String contentSlug, String stateName, long countyCount) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "These county pages show the local permit branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which permit desk, closeout artifact, or local file matters before you treat the permit path like routine paperwork.";
            case "buying-a-house-with-a-septic-system" -> "These county pages show the due-diligence branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which local file, transfer artifact, or management trail matters before you treat the deal like a generic inspection question.";
            case "septic-inspection-cost" -> "These county pages show the inspection-file branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which pumping log, transfer artifact, or failing-system trail matters before you price the inspection scope like routine fieldwork.";
            case "perc-test-cost" -> "These county pages show the site-review branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which parcel file, permit lane, or redesign trigger matters before you price soils, perc, or site-evaluation work like a generic first step.";
            case "septic-pumping-cost" -> "These county pages show the maintenance branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which operating history, pumping log, or maintenance obligation matters before you price this like a simple tank visit.";
            default -> "These county pages show the local branches that keep repeating in " + stateName
                    + ". This summary is built from " + countyCount
                    + " live county workflows so you can decide which county file, replacement branch, or failure-side trigger matters before you treat the first cost number like the final answer.";
        };
    }

    private String countyWorkflowFirstArtifactsHeading(String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "First county permit artifacts to pull";
            case "buying-a-house-with-a-septic-system" -> "First county buyer artifacts to pull";
            case "septic-inspection-cost" -> "First county inspection artifacts to pull";
            case "perc-test-cost" -> "First county site-review artifacts to pull";
            case "septic-pumping-cost" -> "First county maintenance artifacts to pull";
            default -> isReplacementWorkflowCostSlug(contentSlug)
                    ? "First county replacement artifacts to pull"
                    : "First county artifacts to pull";
        };
    }

    private String countyWorkflowDropHeading(String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "Drop to a county permit page when";
            case "buying-a-house-with-a-septic-system" -> "Drop to a county page when the deal risk turns local";
            case "septic-inspection-cost" -> "Drop to a county inspection page when";
            case "perc-test-cost" -> "Drop to a county site-review page when";
            case "septic-pumping-cost" -> "Drop to a county maintenance page when";
            default -> isReplacementWorkflowCostSlug(contentSlug)
                    ? "Drop to a county replacement page when"
                    : "Drop to a county page when";
        };
    }

    private String countyWorkflowHoldQuoteHeading(String contentSlug) {
        return switch (contentSlug) {
            case "septic-permit-process" -> "Do not schedule permit pricing yet when";
            case "buying-a-house-with-a-septic-system" -> "Do not treat this as a routine deal yet when";
            case "septic-inspection-cost" -> "Do not price inspection scope yet when";
            case "perc-test-cost" -> "Do not price site-review scope yet when";
            case "septic-pumping-cost" -> "Do not price maintenance scope yet when";
            default -> isReplacementWorkflowCostSlug(contentSlug)
                    ? "Do not price replacement scope yet when"
                    : "Do not quote yet when";
        };
    }

    private List<CountyRecordsPage> countyPagesMatchingPattern(List<CountyRecordsPage> countyPages, CountyPatternType pattern) {
        return countyPages.stream()
                .filter(page -> countyMatchesPattern(page, pattern))
                .toList();
    }

    private boolean countyMatchesPattern(CountyRecordsPage page, CountyPatternType pattern) {
        String combinedText = String.join(" ",
                nullSafe(page.introCopy()),
                nullSafe(page.uniqueAngle()),
                nullSafe(page.recordsLabel()),
                nullSafe(page.officeLabel()),
                String.join(" ", page.decisionSteps() == null ? List.of() : page.decisionSteps()),
                String.join(" ", page.recordsToRequest() == null ? List.of() : page.recordsToRequest()),
                String.join(" ", page.lowEndBreakers() == null ? List.of() : page.lowEndBreakers())
        ).toLowerCase(Locale.US);
        return pattern.matches(combinedText);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private enum CountyPatternType {
        LOOKUP(1, "Parcel and records lookup",
                "County files often start with parcel, GIS, permit-search, or formal document-request lookup before anyone trusts the seller summary.",
                "Parcel identifier, address, owner name, or permit number needed to pull the county file.",
                "You already have the parcel, address, or owner in hand and the next real move is pulling the county file.",
                "Do not move into quote mode while the parcel, GIS, or records-request trail is still missing."),
        AUTHORITY(2, "File owner and local office split",
                "%s counties often split the real file owner between county health, a municipality, a board of health, or another local office. The first win is identifying the right desk.",
                "The exact county, municipal, board-of-health, or CEHA office that actually owns the septic file.",
                "The story mentions a town, local board, or other office that does not sound like the main county file owner.",
                "Hold off on pricing if the caller still does not know which office actually owns the septic file."),
        PERMIT(3, "Permit ladder and closeout file",
                "Many county files are not one permit receipt. They usually widen into permit ladders, operation approvals, completion certificates, or reuse and addition branches.",
                "Improvement permit, construction authorization, operation permit, sanitary construction permit, or completion certificate.",
                "The project involves an addition, reuse, repair, or change-of-use instead of a simple existing-system lookup.",
                "Do not trust a clean reuse story until the permit ladder and closeout artifact are both visible."),
        TRANSFER(4, "Transfer and buyer diligence",
                "Buyer and transfer risk often lives in inspection, property-status, PTI, or completion artifacts rather than a generic permit copy.",
                "Transfer inspection, property status report, PTI-backed record, or buyer-side completion proof.",
                "The real question is closing risk, lender diligence, or inspection leverage rather than basic permit history.",
                "Do not jump to quote mode while the buyer or lender still lacks the transfer-side inspection or status artifact."),
        BRF(5, "Grant and special-program file",
                "Some counties add a separate BRF, BAT, Critical Area, or sewer-connection lane that can change both timing and ownership cost.",
                "BRF or BAT application, Critical Area note, sewer-connection alternative, or upgrade-program file.",
                "The parcel may be in a Critical Area, failing-system, or upgrade-program lane where grant and replacement rules change the next step.",
                "Do not frame the job as a simple replacement if grant, BAT, Critical Area, or sewer-connection rules might still control the path."),
        COMPLAINT(6, "Repair and malfunction trail",
                "Repair questionnaires, malfunction complaints, or violation files often tell you more than a clean-looking estimate or seller note.",
                "Repair questionnaire, malfunction complaint, violation notice, or repair-permit history.",
                "There are failure symptoms, complaint history, or repair questions already in play and the state page is still too abstract.",
                "Stop before quoting if there are failure symptoms, complaint history, or an unresolved repair trail in the county file.");

        private final int displayOrder;
        private final String label;
        private final String summary;
        private final String firstArtifact;
        private final String countyDropTrigger;
        private final String holdQuoteCheck;

        CountyPatternType(
                int displayOrder,
                String label,
                String summary,
                String firstArtifact,
                String countyDropTrigger,
                String holdQuoteCheck
        ) {
            this.displayOrder = displayOrder;
            this.label = label;
            this.summary = summary;
            this.firstArtifact = firstArtifact;
            this.countyDropTrigger = countyDropTrigger;
            this.holdQuoteCheck = holdQuoteCheck;
        }

        int displayOrder() {
            return displayOrder;
        }

        String label() {
            return label;
        }

        String summary(String stateName) {
            return summary.formatted(stateName);
        }

        String firstArtifact() {
            return firstArtifact;
        }

        String countyDropTrigger() {
            return countyDropTrigger;
        }

        String holdQuoteCheck() {
            return holdQuoteCheck;
        }

        boolean matches(String text) {
            return switch (this) {
                case LOOKUP -> containsAny(text,
                        "parcel", "gis", "request for document", "records search", "file search",
                        "property status", "tax map", "apn", "permit search", "opra");
                case AUTHORITY -> containsAny(text,
                        "local health department", "municipal", "board of health", "ceha",
                        "local approving authority", "incorporated town", "municipality");
                case PERMIT -> containsAny(text,
                        "operation permit", "construction authorization", "completion certificate",
                        "certificate of occupancy", "improvement permit", "existing system approval",
                        "sanitary construction permit", "certificate of completion", "interim permit");
                case TRANSFER -> containsAny(text,
                        "transfer", "buyer", "property status report", "pti", "real estate", "closing");
                case BRF -> containsAny(text,
                        "bay restoration fund", "brf", "critical area", "bat", "nitrogen");
                case COMPLAINT -> containsAny(text,
                        "malfunction", "repair permit", "repair questionnaire", "complaint",
                        "violation", "failing system");
            };
        }
    }

    private static boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private List<PageLink> pageLinks(List<String> paths, String sourceSlug, String sourceStateCode) {
        if (paths == null || paths.isEmpty()) {
            return List.of();
        }
        return paths.stream()
                .map(this::canonicalEditorialPath)
                .filter(path -> path != null && !path.isBlank())
                .distinct()
                .map(path -> pageLink(path, sourceSlug, sourceStateCode))
                .sorted(Comparator
                        .comparingInt((PageLink link) -> relatedLinkScore(sourceSlug, sourceStateCode, link.path()))
                        .reversed()
                        .thenComparing(PageLink::title))
                .toList();
    }

    private PageLink pageLink(String path, String sourceSlug, String sourceStateCode) {
        String title = calculatorLinkTitle(path)
                .or(() -> stateGuideLinkTitle(path))
                .or(() -> countyRecordsPageLinkTitle(path))
                .or(() -> stateMoneyPageLinkTitle(path))
                .or(() -> contentPageLinkTitle(path))
                .orElseGet(() -> prettifyPath(path));
        return new PageLink(title, path, relatedLinkNote(sourceSlug, sourceStateCode, path));
    }

    private List<String> mergedPaths(List<String> primaryPaths, List<String> supplementalPaths) {
        return Stream.concat(
                        primaryPaths == null ? Stream.<String>empty() : primaryPaths.stream(),
                        supplementalPaths == null ? Stream.<String>empty() : supplementalPaths.stream()
                )
                .filter(path -> path != null && !path.isBlank())
                .distinct()
                .toList();
    }

    private List<String> defaultStateCrossLinks(StateMoneyPage stateMoneyPage, StateProfile state) {
        List<String> targetSlugs = switch (stateMoneyPage.contentSlug()) {
            case "septic-inspection-cost" -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    "wet-yard-over-septic-drain-field",
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic"
            );
            case "septic-records-checklist" -> List.of(
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic",
                    "septic-replacement-area"
            );
            case "buying-a-house-with-a-septic-system" -> List.of(
                    "septic-records-checklist",
                    "septic-inspection-cost",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "wet-yard-over-septic-drain-field",
                    "septic-replacement-area"
            );
            case "drain-field-replacement-cost" -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "septic-replacement-area",
                    "wet-yard-over-septic-drain-field",
                    "failed-perc-test-septic"
            );
            case "failed-perc-test-septic" -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "septic-replacement-area",
                    "perc-test-cost"
            );
            case "septic-replacement-area" -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic"
            );
            case "wet-yard-over-septic-drain-field" -> List.of(
                    "septic-inspection-cost",
                    "septic-records-checklist",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "septic-replacement-area"
            );
            default -> List.of();
        };

        return targetSlugs.stream()
                .map(targetSlug -> researchDataService.findPublicStateMoneyPage(targetSlug, state.slug()))
                .flatMap(Optional::stream)
                .map(page -> page.path(state.slug()))
                .toList();
    }

    private String canonicalEditorialPath(String path) {
        var uri = UriComponentsBuilder.fromUriString(path).build();
        String normalizedPath = uri.getPath();
        if (!"/septic-system-cost-calculator/".equals(normalizedPath) && !"/septic-system-cost-calculator".equals(normalizedPath)) {
            return path;
        }

        Map<String, List<String>> queryParams = uri.getQueryParams();
        String stateCode = queryParams.getOrDefault("state", List.of()).stream().findFirst().orElse(null);
        String projectType = queryParams.getOrDefault("projectType", List.of()).stream().findFirst().orElse(null);
        Optional<StateProfile> state = researchDataService.findStateByCode(stateCode);

        if (state.isPresent() && projectType != null) {
            Optional<String> contentSlug = contentSlugForProjectType(projectType);
            if (contentSlug.isPresent()) {
                Optional<StateMoneyPage> stateMoneyPage = researchDataService.findPublicStateMoneyPage(contentSlug.get(), state.get().slug());
                if (stateMoneyPage.isPresent()) {
                    return stateMoneyPage.get().path(state.get().slug());
                }
            }
        }

        if (state.isPresent()) {
            return "/septic-system-cost-calculator/" + state.get().slug() + "/";
        }

        return contentSlugForProjectType(projectType)
                .map(contentSlug -> "/" + contentSlug + "/")
                .orElse(path);
    }

    private Optional<String> contentSlugForProjectType(String projectType) {
        if (projectType == null || projectType.isBlank()) {
            return Optional.empty();
        }
        return switch (projectType) {
            case "replacement" -> Optional.of("septic-replacement-cost");
            case "perc_test" -> Optional.of("perc-test-cost");
            case "drainfield_replacement" -> Optional.of("drain-field-replacement-cost");
            case "inspection" -> Optional.of("septic-inspection-cost");
            default -> Optional.empty();
        };
    }

    private Optional<String> calculatorLinkTitle(String path) {
        var uri = UriComponentsBuilder.fromUriString(path).build();
        String normalizedPath = uri.getPath();
        if ("/septic-system-cost-calculator/".equals(normalizedPath) || "/septic-system-cost-calculator".equals(normalizedPath)) {
            Map<String, List<String>> queryParams = uri.getQueryParams();
            String stateCode = queryParams.getOrDefault("state", List.of()).stream().findFirst().orElse(null);
            String projectType = queryParams.getOrDefault("projectType", List.of()).stream().findFirst().orElse(null);
            Optional<StateProfile> state = researchDataService.findStateByCode(stateCode);
            if (state.isPresent() && projectType != null) {
                return Optional.of(state.get().stateName() + " " + projectTypeLabel(projectType) + " estimate");
            }
            if (state.isPresent()) {
                return Optional.of(state.get().stateName() + " septic cost estimate");
            }
            return Optional.of("Main septic cost calculator");
        }
        if ("/septic-tank-size-estimator/".equals(normalizedPath) || "/septic-tank-size-estimator".equals(normalizedPath)) {
            return Optional.of("Septic tank size estimator");
        }
        if ("/septic-pump-schedule-estimator/".equals(normalizedPath) || "/septic-pump-schedule-estimator".equals(normalizedPath)) {
            return Optional.of("Septic pump schedule estimator");
        }
        if ("/drain-field-estimator/".equals(normalizedPath) || "/drain-field-estimator".equals(normalizedPath)) {
            return Optional.of("Drain field replacement estimator");
        }
        return Optional.empty();
    }

    private List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntriesForContentPage(ContentPage contentPage) {
        Stream<StateMoneyPage> stateMoneyPages = stateMoneyPageSlugsForContentPage(contentPage)
                        .flatMap(slug -> researchDataService.listPublicStateMoneyPagesForContent(slug).stream())
                .distinct();

        return stateMoneyPages
                .flatMap(page -> researchDataService.findStateByCode(page.stateCode())
                        .map(state -> Map.entry(page, state))
                        .stream())
                .sorted(Comparator
                        .comparingInt((Map.Entry<StateMoneyPage, StateProfile> entry) -> contentStateLinkScore(contentPage, entry.getKey(), entry.getValue()))
                        .reversed()
                        .thenComparing(entry -> entry.getValue().stateName())
                        .thenComparing(entry -> entry.getKey().title()))
                .toList();
    }

    private StateSurfaceSignalView stateSurfaceSignalView(ContentPage contentPage, StateMoneyPage page, StateProfile state) {
        int linkScore = contentStateLinkScore(contentPage, page, state);
        int observedBoost = observedIntentSignalBoost(contentPage.slug(), state.stateCode(), page.contentSlug());
        int pageSourceCount = size(page.officialSourceIds());
        int backingSourceCount = distinctSourceCount(
                page.officialSourceIds(),
                state.officialSourceIds(),
                state.localAuthoritySourceIds(),
                state.recordsLookupSourceIds()
        );
        int localRiskCheckCount = Math.max(size(page.lowEndBreakers()), size(state.lowEndRiskChecks()));
        boolean exactMatch = contentPage.slug().equals(page.contentSlug());
        boolean anchorState = "anchor".equalsIgnoreCase(state.launchTier());
        boolean hasCountyRecords = !researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty();
        boolean leadWithStateWorkflow = shouldLeadWithStateWorkflow(contentPage);
        boolean stateAwareTool = supportsStateAwareTool(contentPage);

        String workflowFitLabel = linkScore >= 48 || observedBoost >= 16
                ? "Strong"
                : linkScore >= 34 || observedBoost >= 8
                        ? "Good"
                        : "Emerging";
        List<String> workflowReasons = new ArrayList<>();
        if (exactMatch) {
            workflowReasons.add("the live page already matches this intent directly");
        } else if (isPermitLookupHub(contentPage)) {
            workflowReasons.add("the lookup route already resolves through a live records or permit workflow page");
        } else if (isTransferComplianceHub(contentPage)) {
            workflowReasons.add("the transfer route already resolves through a live supporting workflow page");
        }
        if (anchorState) {
            workflowReasons.add("it is an anchor launch state");
        }
        if (observedBoost >= 12) {
            workflowReasons.add("observed search and lead signals are already strong");
        } else if (observedBoost >= 8) {
            workflowReasons.add("observed intent signals are already present");
        }
        if (hasCountyRecords) {
            workflowReasons.add("county file pages are already live");
        }
        String workflowFitPrefix = switch (workflowFitLabel) {
            case "Strong" -> "a strong";
            case "Good" -> "a good";
            default -> "an emerging";
        };
        String workflowFitNote = workflowReasons.isEmpty()
                ? state.stateName() + " is live, but the local wedge is still more directional than dominant."
                : state.stateName() + " is " + workflowFitPrefix + " local wedge because " + joinWithAnd(workflowReasons) + ".";

        Double confidence = state.confidenceScore();
        String confidenceText = confidenceLabel(confidence);
        String evidenceDepthLabel = backingSourceCount >= 7 || (pageSourceCount >= 3 && confidence != null && confidence >= 0.75)
                ? "Source-backed"
                : backingSourceCount >= 4 || (pageSourceCount >= 2 && confidence != null && confidence >= 0.6)
                        ? "Solid"
                        : "Developing";
        String evidenceDepthNote = confidenceText.isBlank()
                ? "Backed by " + backingSourceCount + " distinct official or workflow source"
                        + (backingSourceCount == 1 ? "" : "s")
                        + " across the state page, records path, and authority notes."
                : "Backed by " + backingSourceCount + " distinct official or workflow source"
                        + (backingSourceCount == 1 ? "" : "s")
                        + " and a " + confidenceText.toLowerCase(Locale.US) + " state profile.";

        String toolHandoffLabel;
        String toolHandoffNote;
        String riskSuffix = localRiskCheckCount >= 3
                ? " It already surfaces " + localRiskCheckCount + " local risk checks that can widen the downside."
                : "";
        if (leadWithStateWorkflow) {
            if (!stateAwareTool) {
                toolHandoffLabel = "Use after local workflow";
                toolHandoffNote = "The next tool step does not carry " + state.stateName()
                        + " directly, so keep the state page open as the backstop." + riskSuffix;
            } else if (backingSourceCount >= 6 || observedBoost >= 8 || hasCountyRecords) {
                toolHandoffLabel = "Ready after one local check";
                toolHandoffNote = state.stateName()
                        + " can follow into the tool, but narrow the file, permit, or buyer lane first." + riskSuffix;
            } else {
                toolHandoffLabel = "Cautious handoff";
                toolHandoffNote = "The tool can take " + state.stateName()
                        + ", but one local workflow check should happen before the number carries much weight." + riskSuffix;
            }
        } else if (stateAwareTool && (backingSourceCount >= 4 || (confidence != null && confidence >= 0.6))) {
            toolHandoffLabel = "Ready now";
            toolHandoffNote = "The tool can carry " + state.stateName()
                    + " directly, so this page can hand off fast and use the state page only as a backstop." + riskSuffix;
        } else if (stateAwareTool) {
            toolHandoffLabel = "Ready with backstop";
            toolHandoffNote = "Start in the tool with " + state.stateName()
                    + " attached, then open the state page if the result still feels broad." + riskSuffix;
        } else {
            toolHandoffLabel = "Use with local backstop";
            toolHandoffNote = "The tool step does not hold " + state.stateName()
                    + " context directly, so keep the local page beside the estimate." + riskSuffix;
        }

        return new StateSurfaceSignalView(
                workflowFitLabel,
                workflowFitNote,
                evidenceDepthLabel,
                evidenceDepthNote,
                toolHandoffLabel,
                toolHandoffNote
        );
    }

    private int contentStateLinkScore(ContentPage contentPage, StateMoneyPage page, StateProfile state) {
        if (isPermitLookupHub(contentPage)) {
            return permitLookupStateLinkScore(page, state);
        }
        if (isTransferComplianceHub(contentPage)) {
            return transferComplianceStateLinkScore(page, state);
        }
        int score = 0;
        if ("anchor".equalsIgnoreCase(state.launchTier())) {
            score += 20;
        }
        score += (int) Math.round((state.confidenceScore() == null ? 0.0 : state.confidenceScore()) * 10);
        score += Math.min(size(page.officialSourceIds()), 3) * 3;
        score += Math.min(size(page.lowEndBreakers()), 2);
        if (contentPage.slug().equals(page.contentSlug())) {
            score += 12;
        }
        score += observedIntentSignalBoost(contentPage.slug(), state.stateCode(), page.contentSlug());
        return score;
    }

    private int observedIntentSignalBoost(String contentSlug, String stateCode, String pageContentSlug) {
        return switch (contentSlug) {
            case PERMIT_LOOKUP_SLUG -> switch (pageContentSlug) {
                case "septic-records-checklist" -> switch (stateCode) {
                    case "TN" -> 34;
                    case "NC", "TX" -> 20;
                    case "AL", "IN" -> 16;
                    case "GA" -> 12;
                    default -> 0;
                };
                case "septic-permit-process" -> switch (stateCode) {
                    case "TN" -> 28;
                    case "NC", "TX", "AL" -> 14;
                    case "SC", "IN" -> 10;
                    default -> 0;
                };
                default -> 0;
            };
            case "septic-records-checklist" -> "septic-records-checklist".equals(pageContentSlug)
                    ? switch (stateCode) {
                        case "TN" -> 30;
                        case "NC", "TX" -> 18;
                        case "IN" -> 18;
                        case "AL", "GA" -> 12;
                        default -> 0;
                    }
                    : 0;
            case "septic-permit-process" -> "septic-permit-process".equals(pageContentSlug)
                    ? switch (stateCode) {
                        case "TN" -> 18;
                        case "NC", "TX", "AL" -> 12;
                        case "SC" -> 14;
                        case "NE", "RI" -> 8;
                        default -> 0;
                    }
                    : 0;
            case "buying-a-house-with-a-septic-system" -> "buying-a-house-with-a-septic-system".equals(pageContentSlug)
                    ? switch (stateCode) {
                        case "NY" -> 16;
                        default -> 0;
                    }
                    : 0;
            case "perc-test-cost" -> "perc-test-cost".equals(pageContentSlug)
                    ? switch (stateCode) {
                        case "AL" -> 22;
                        case "GA" -> 20;
                        case "SC" -> 8;
                        default -> 0;
                    }
                    : 0;
            default -> 0;
        };
    }

    private Stream<String> stateMoneyPageSlugsForContentPage(ContentPage contentPage) {
        if (isPermitLookupHub(contentPage)) {
            return PERMIT_LOOKUP_STATE_SLUGS.stream();
        }
        if (isTransferComplianceHub(contentPage)) {
            return TRANSFER_COMPLIANCE_STATE_SLUGS.stream();
        }
        return Stream.of(contentPage.slug());
    }

    private int permitLookupStateLinkScore(StateMoneyPage page, StateProfile state) {
        int score = stateMoneyPagePriorityScore(state, page);
        score += switch (page.contentSlug()) {
            case "septic-records-checklist" -> 34;
            case "septic-permit-process" -> 28;
            default -> -100;
        };
        if (!researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty()) {
            score += 12;
        }
        score += switch (state.stateCode()) {
            case "TN" -> 34;
            case "NC" -> 22;
            case "TX" -> 20;
            case "AL", "IN" -> 16;
            case "GA", "SC" -> 10;
            default -> 0;
        };
        if ("septic-records-checklist".equals(page.contentSlug())) {
            score += switch (state.stateCode()) {
                case "TN" -> 12;
                case "NC", "TX" -> 8;
                case "AL", "IN" -> 6;
                default -> 0;
            };
        }
        if (page.highlightBuyerTrigger()) {
            score += 3;
        }
        if (page.highlightMaintenanceNote()) {
            score += 2;
        }
        return score;
    }

    private int transferComplianceStateLinkScore(StateMoneyPage page, StateProfile state) {
        int score = stateMoneyPagePriorityScore(state, page);
        score += switch (page.contentSlug()) {
            case "septic-records-checklist" -> 28;
            case "septic-permit-process" -> 24;
            case "buying-a-house-with-a-septic-system" -> 20;
            default -> -100;
        };
        if (!researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty()) {
            score += 10;
        }
        score += switch (state.stateCode()) {
            case "GA" -> 22;
            case "AL" -> 20;
            case "IN" -> 12;
            case "CO", "NC", "NY" -> 8;
            default -> 0;
        };
        if ("septic-records-checklist".equals(page.contentSlug())) {
            score += switch (state.stateCode()) {
                case "GA" -> 10;
                case "AL" -> 8;
                case "IN" -> 4;
                default -> 0;
            };
        }
        if (page.highlightBuyerTrigger()) {
            score += 4;
        }
        if (page.highlightMaintenanceNote()) {
            score += 2;
        }
        return score;
    }

    private boolean supportsStateAwareTool(ContentPage contentPage) {
        return switch (calculatorPathForModule(contentPage.calculatorModule())) {
            case "/septic-system-cost-calculator/", "/septic-tank-size-estimator/", "/drain-field-estimator/" -> true;
            default -> false;
        };
    }

    private int stateMoneyPagePriorityScore(StateProfile state, StateMoneyPage page) {
        int score = Math.min(size(page.officialSourceIds()), 3) * 3
                + Math.min(size(page.decisionSteps()), 4)
                + Math.min(size(page.lowEndBreakers()), 3)
                + Math.min(size(page.quotePrepChecklist()), 3);

        if ("anchor".equalsIgnoreCase(state.launchTier())) {
            score += 2;
        }

        if (state.confidenceScore() != null && state.confidenceScore() < 0.7) {
            score += switch (page.contentSlug()) {
                case "septic-records-checklist", "septic-inspection-cost" -> 4;
                default -> 0;
            };
        }

        score += switch (page.contentSlug()) {
            case "septic-records-checklist" -> (hasItems(state.recordsToRequest(), 2) ? 14 : 6)
                    + (hasItems(state.recordsLookupSourceIds(), 1) ? 8 : 0)
                    + (!researchDataService.listPublicCountyRecordsPages(state.stateCode()).isEmpty() ? 5 : 0);
            case "buying-a-house-with-a-septic-system" -> (hasText(state.buyerInspectionTrigger()) ? 15 : 0)
                    + (hasText(state.specialAreaNote()) ? 3 : 0);
            case "septic-inspection-cost" -> (hasText(state.maintenanceInspectionNote()) ? 10 : 0)
                    + (hasText(state.buyerInspectionTrigger()) ? 6 : 0);
            case "septic-permit-process" -> (hasItems(state.permitPathSteps(), 3) ? 13 : 6)
                    + (hasText(state.whoToCallFirst()) ? 4 : 0)
                    + (hasItems(state.localAuthoritySourceIds(), 1) ? 6 : 0);
            case "septic-replacement-cost" -> (researchDataService.findStateCostProfile(state.stateCode())
                    .map(StateCostProfile::replacementMid)
                    .filter(value -> value != null)
                    .isPresent() ? 9 : 0)
                    + (hasItems(state.lowEndRiskChecks(), 2) ? 4 : 0);
            case "perc-test-cost" -> (hasText(state.siteEvalSummary()) ? 9 : 0)
                    + switch (state.stateCode()) {
                        case "AL", "GA" -> 10;
                        default -> 0;
                    };
            case "failed-perc-test-septic" -> (hasText(state.siteEvalSummary()) ? 9 : 0)
                    + (hasItems(state.recordsLookupSourceIds(), 1) ? 4 : 0);
            case "septic-replacement-area" -> (hasText(state.siteEvalSummary()) ? 8 : 0)
                    + (hasItems(state.recordsToRequest(), 2) ? 4 : 0);
            case "wet-yard-over-septic-drain-field" -> (hasText(state.siteEvalSummary()) ? 8 : 0)
                    + (hasItems(state.recordsToRequest(), 2) ? 4 : 0);
            case "drain-field-replacement-cost" -> hasText(state.siteEvalSummary()) ? 6 : 0;
            case "septic-pumping-cost" -> hasText(state.maintenanceInspectionNote()) ? 7 : 0;
            default -> 0;
        };

        return score;
    }

    private Optional<String> stateGuideLinkTitle(String path) {
        String normalizedPath = normalizePath(path);
        String prefix = "/septic-system-cost-calculator/";
        if (normalizedPath != null && normalizedPath.startsWith(prefix)) {
            String stateSlug = normalizedPath.substring(prefix.length()).replaceFirst("/$", "");
            if (!stateSlug.isBlank() && !stateSlug.contains("/")) {
                return researchDataService.findStateBySlug(stateSlug)
                        .map(StateProfile::stateName)
                        .or(() -> usStateDirectoryService.findBySlug(stateSlug).map(UsStateDirectoryService.UsStateReference::stateName))
                        .map(stateName -> stateName + " septic guide");
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    private StateMoneyPrimaryAction stateMoneyPrimaryAction(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            StateActionCopy stateActionCopy,
            List<PageLink> countyRecordLinks,
            SourceRecord primaryLocalAuthoritySource,
            SourceRecord primaryRecordsLookupSource,
            StateCountyWorkflowSynthesisView countyWorkflowSynthesis
    ) {
        String calculatorPath = stateMoneyCalculatorPath(stateMoneyPage, state);

        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county file lookup",
                    "Open county record lookup paths",
                    countyAwareNote(
                            "Use the county page first when the state lookup is still too broad and the real blocker is a county file, permit-search result, site-review note, or local records form.",
                            countyWorkflowSynthesis
                    ),
                    "Open county record lookup paths",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if ("septic-permit-process".equals(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county permit desk",
                    "Open county permit pages",
                    countyAwareNote(
                            "Use the county page first when the state permit path is still too broad and the real blocker is a county permit desk, closeout file, or local repair branch.",
                            countyWorkflowSynthesis
                    ),
                    "Open county permit pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to county diligence",
                    "Open county diligence pages",
                    countyAwareNote(
                            "Use the county page first when the buyer page is still too broad and the real blocker is a local file, transfer artifact, or maintenance obligation tied to the property.",
                            countyWorkflowSynthesis
                    ),
                    "Open county diligence pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if (isReplacementWorkflowCostSlug(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county replacement file",
                    "Open county replacement pages",
                    countyAwareNote(
                            "Use the county page first when the replacement number is still broad and the real blocker is a failure-side file, reserve-area rule, sewer branch, or local replacement lane.",
                            countyWorkflowSynthesis
                    ),
                    "Open county replacement pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if (isInspectionWorkflowCostSlug(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county inspection file",
                    "Open county inspection pages",
                    countyAwareNote(
                            "Use the county page first when the inspection number is still broad and the real blocker is a pumping log, operating-history file, transfer artifact, or failure trail tied to the parcel.",
                            countyWorkflowSynthesis
                    ),
                    "Open county inspection pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if (isPercWorkflowCostSlug(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county site-review file",
                    "Open county site-review pages",
                    countyAwareNote(
                            "Use the county page first when the perc or site-review number is still broad and the real blocker is a parcel file, permit lane, redesign trigger, or local evaluator path.",
                            countyWorkflowSynthesis
                    ),
                    "Open county site-review pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if (isPumpingWorkflowCostSlug(stateMoneyPage.contentSlug()) && countyRecordLinks.size() >= 2) {
            return new StateMoneyPrimaryAction(
                    "Narrow to the county maintenance file",
                    "Open county maintenance pages",
                    countyAwareNote(
                            "Use the county page first when the pumping number is still broad and the real blocker is a maintenance log, O&M requirement, or inspection cadence tied to the parcel.",
                            countyWorkflowSynthesis
                    ),
                    "Open county maintenance pages",
                    "#county-pages",
                    "state_money_primary_county_pages",
                    "county_page_directory",
                    false
            );
        }

        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Open the official records path before you compress a records problem into one planning number or quote request.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()) && primaryLocalAuthoritySource != null) {
            return new StateMoneyPrimaryAction(
                    "Verify the file owner first",
                    primaryLocalAuthoritySource.title(),
                    countyAwareNote(
                            "Use the office that controls the septic file before you trust a seller summary or start chasing quotes.",
                            countyWorkflowSynthesis
                    ),
                    "Open local authority source",
                    primaryLocalAuthoritySource.url(),
                    "state_money_primary_authority_source",
                    "official_source",
                    false
            );
        }

        if (isReplacementWorkflowCostSlug(stateMoneyPage.contentSlug()) && primaryLocalAuthoritySource != null) {
            return new StateMoneyPrimaryAction(
                    "Check the local replacement desk first",
                    primaryLocalAuthoritySource.title(),
                    countyAwareNote(
                            state.stateName() + " replacement questions usually turn on the local authority, failure lane, or sewer branch before the planning range matters.",
                            countyWorkflowSynthesis
                    ),
                    "Open local authority source",
                    primaryLocalAuthoritySource.url(),
                    "state_money_primary_authority_source",
                    "official_source",
                    false
            );
        }

        if (isReplacementWorkflowCostSlug(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the replacement file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Replacement pricing gets more honest once the county file shows the live failure branch, the parcel history, and the last real approval signal.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug())
                && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the file before pricing buyer risk",
                    "Open the official records path",
                    countyAwareNote(
                            "Buyer risk gets concrete once the permit, as-built, inspection, and maintenance file is in hand.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if (isPercWorkflowCostSlug(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            if (primaryLocalAuthoritySource != null) {
                return new StateMoneyPrimaryAction(
                        "Check the site-review desk first",
                        primaryLocalAuthoritySource.title(),
                        countyAwareNote(
                                state.stateName() + " site-review questions usually turn on the local authority, parcel lane, and approval path before a perc number means much.",
                                countyWorkflowSynthesis
                        ),
                        "Open local authority source",
                        primaryLocalAuthoritySource.url(),
                        "state_money_primary_authority_source",
                        "official_source",
                        false
                );
            }
            return new StateMoneyPrimaryAction(
                    "Pull the site-review file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Site-review pricing gets more honest once the county file shows the parcel history, prior approval lane, and redesign trigger behind the lot.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug())
                && researchDataService.findPublicStateMoneyPage("septic-records-checklist", state.slug()).isPresent()) {
            return new StateMoneyPrimaryAction(
                    "Pull the file before pricing buyer risk",
                    "Open the " + state.stateName() + " records lookup",
                    countyAwareNote(
                            state.stateName() + " buyer risk gets concrete once the permit file, as-built, and local record path are in hand.",
                            countyWorkflowSynthesis
                    ),
                    "Open " + state.stateName() + " records lookup",
                    "/septic-records-checklist/" + state.slug() + "/",
                    "state_money_primary_records_page",
                    "state_money_page",
                    false
            );
        }

        if (isPumpingWorkflowCostSlug(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the maintenance file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Pumping pricing gets more honest once the county file shows the last pumping, inspection, and O&M signal tied to the parcel.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("septic-inspection-cost".equals(stateMoneyPage.contentSlug()) && primaryRecordsLookupSource != null) {
            return new StateMoneyPrimaryAction(
                    "Pull the inspection file first",
                    primaryRecordsLookupSource.title(),
                    countyAwareNote(
                            "Inspection pricing gets more honest once the permit, as-built, pumping, and maintenance file is in hand.",
                            countyWorkflowSynthesis
                    ),
                    "Open records lookup",
                    primaryRecordsLookupSource.url(),
                    "state_money_primary_records_source",
                    "official_source",
                    false
            );
        }

        if ("septic-permit-process".equals(stateMoneyPage.contentSlug()) && primaryLocalAuthoritySource != null) {
            return new StateMoneyPrimaryAction(
                    "Check the permit desk first",
                    primaryLocalAuthoritySource.title(),
                    countyAwareNote(
                            state.stateName() + " permit questions usually turn on the local authority and approval path before the planning range matters.",
                            countyWorkflowSynthesis
                    ),
                    "Open permit authority",
                    primaryLocalAuthoritySource.url(),
                    "state_money_primary_authority_source",
                    "official_source",
                    false
            );
        }

        return new StateMoneyPrimaryAction(
                "Run the state estimate",
                stateActionCopy.buttonLabel(),
                countyAwareNote(stateActionCopy.supportingNote(), countyWorkflowSynthesis),
                "Run the estimate",
                calculatorPath,
                "state_money_primary_calculator",
                "calculator",
                true
        );
    }

    private String countyAwareNote(String baseNote, StateCountyWorkflowSynthesisView countyWorkflowSynthesis) {
        if (countyWorkflowSynthesis == null) {
            return baseNote;
        }
        String firstArtifact = countyWorkflowSynthesis.firstArtifacts().isEmpty() ? null : countyWorkflowSynthesis.firstArtifacts().get(0);
        String holdQuote = countyWorkflowSynthesis.holdQuoteChecks().isEmpty() ? null : countyWorkflowSynthesis.holdQuoteChecks().get(0);
        if (hasText(firstArtifact) && hasText(holdQuote)) {
            return baseNote + " Pull first: " + firstArtifact + " Hold pricing when " + holdQuote.toLowerCase() + ".";
        }
        if (hasText(firstArtifact)) {
            return baseNote + " Pull first: " + firstArtifact;
        }
        if (hasText(holdQuote)) {
            return baseNote + " Hold pricing when " + holdQuote.toLowerCase() + ".";
        }
        return baseNote;
    }

    private StateWorkflowDecisionView stateWorkflowDecisionView(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            StateCountyWorkflowSynthesisView countyWorkflowSynthesis,
            SourceRecord primaryLocalAuthoritySource,
            SourceRecord primaryRecordsLookupSource
    ) {
        if (countyWorkflowSynthesis == null) {
            return null;
        }
        String firstArtifact = countyWorkflowSynthesis.firstArtifacts().isEmpty()
                ? "The county-side permit, file, or inspection record tied to the parcel."
                : countyWorkflowSynthesis.firstArtifacts().get(0);
        String countyDrop = countyWorkflowSynthesis.countyDropTriggers().isEmpty()
                ? "The state page is still too broad and the real blocker is a county file or office."
                : countyWorkflowSynthesis.countyDropTriggers().get(0);
        String holdQuote = countyWorkflowSynthesis.holdQuoteChecks().isEmpty()
                ? "the county file is strong enough to show the right parcel and last real approval signal"
                : countyWorkflowSynthesis.holdQuoteChecks().get(0);
        String route = stateMoneyPage.contentSlug();
        String firstMove;
        String heading;
        String intro;

        if ("septic-permit-process".equals(route)) {
            heading = "Decision router for " + state.stateName() + " permit work";
            intro = "Use this when the permit page is still broad and you need the fastest way to identify the real county branch before you price anything.";
            firstMove = primaryLocalAuthoritySource != null
                    ? "Confirm the county permit desk and the closeout artifact that proves the system actually cleared the last approval step."
                    : "Identify the county permit desk and the closeout artifact before treating the permit path like routine paperwork.";
        } else if ("buying-a-house-with-a-septic-system".equals(route)) {
            heading = "Decision router for " + state.stateName() + " buyer diligence";
            intro = "Use this when the buyer page is still broad and you need the fastest route to the local file, transfer artifact, and quote gate behind the deal.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Match the seller story to the county file and the buyer-side artifact before you negotiate credits, timing, or scope."
                    : "Resolve the local file and buyer-side artifact before you treat the deal like a routine inspection question.";
        } else if (isInspectionWorkflowCostSlug(route)) {
            heading = "Decision router for " + state.stateName() + " inspection pricing";
            intro = "Use this when the inspection page is still broad and you need the fastest route to the county file, operating history, and hold-pricing trigger behind the scope.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county inspection, pumping, and operating-history file before you price a routine inspection scope."
                    : "Resolve the county inspection file and the last operating-history artifact before you trust a routine inspection number.";
        } else if (isPercWorkflowCostSlug(route)) {
            heading = "Decision router for " + state.stateName() + " perc and site-review pricing";
            intro = "Use this when the perc or site-review page is still broad and you need the fastest route to the parcel file, permit lane, and redesign trigger behind the lot.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county parcel file and confirm the site-review or permit lane before you price soils, perc, or redesign work."
                    : "Resolve the county site-review lane and the first parcel artifact before you treat the first perc number like the real scope.";
        } else if (isPumpingWorkflowCostSlug(route)) {
            heading = "Decision router for " + state.stateName() + " pumping and maintenance pricing";
            intro = "Use this when the pumping page is still broad and you need the fastest route to the maintenance lane, last service artifact, and quote gate behind the parcel.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county pumping, inspection, or O&M file before you price this like a basic tank visit."
                    : "Resolve the county maintenance lane and the last service artifact before you trust a routine pumping number.";
        } else if (isReplacementWorkflowCostSlug(route)) {
            heading = "Decision router for " + state.stateName() + " replacement pricing";
            intro = "Use this when the replacement page is still broad and you need the fastest route to the county file, failure branch, and hold-pricing trigger behind the number.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county file and confirm the live repair, failure, reserve-area, or sewer branch before you trust one replacement number."
                    : "Resolve the county file, the local replacement branch, and the last real approval artifact before you treat the first number like the real scope.";
        } else {
            heading = "Decision router for " + state.stateName() + " records work";
            intro = "Use this when the records page is still broad and you need the fastest route to the county file, first artifact, and pricing gate.";
            firstMove = primaryRecordsLookupSource != null
                    ? "Pull the county file and match it to the parcel before you trust any seller, owner, or contractor story."
                    : "Resolve the county file owner and first parcel artifact before you compress this into one estimate or quote.";
        }

        return new StateWorkflowDecisionView(
                "Decision router",
                heading,
                intro,
                List.of(
                        new CountyWorkflowFieldView("Resolve first", firstMove),
                        new CountyWorkflowFieldView("Pull first", firstArtifact),
                        new CountyWorkflowFieldView("Escalate to county when", countyDrop),
                        new CountyWorkflowFieldView("Hold pricing when", holdQuote)
                )
        );
    }

    private StateCostScopeView stateCostScopeView(
            StateMoneyPage stateMoneyPage,
            StateProfile state,
            StateCountyWorkflowSynthesisView countyWorkflowSynthesis
    ) {
        if (!isWorkflowCostSlug(stateMoneyPage.contentSlug())) {
            return null;
        }

        String route = stateMoneyPage.contentSlug();
        String clearFirst = firstNonBlank(
                countyWorkflowSynthesis == null || countyWorkflowSynthesis.firstArtifacts().isEmpty()
                        ? null
                        : countyWorkflowSynthesis.firstArtifacts().get(0),
                firstOf(state.recordsToRequest()),
                "The county file, parcel identifier, and latest approval artifact tied to this system."
        );
        String lowEndBreaker = firstNonBlank(
                firstOf(stateMoneyPage.lowEndBreakers()),
                firstOf(state.lowEndRiskChecks()),
                "The county file still leaves a permit, repair, or location branch unresolved."
        );
        String countyWidener = firstNonBlank(
                preferredCostWidener(countyWorkflowSynthesis, route),
                firstOf(stateMoneyPage.driverBullets()),
                state.specialAreaNote(),
                "A county-side permit, repair, or maintenance lane is still widening the scope."
        );
        String midpointGate = firstNonBlank(
                countyWorkflowSynthesis == null || countyWorkflowSynthesis.holdQuoteChecks().isEmpty()
                        ? null
                        : countyWorkflowSynthesis.holdQuoteChecks().get(0),
                "the county file still leaves the failure branch, permit lane, or maintenance obligation unresolved"
        );

        List<String> scopeWideners = Stream.concat(
                        safeList(stateMoneyPage.driverBullets()).stream(),
                        safeList(stateMoneyPage.lowEndBreakers()).stream()
                )
                .filter(this::hasText)
                .distinct()
                .limit(6)
                .toList();

        List<String> readinessChecks = Stream.concat(
                        safeList(stateMoneyPage.quotePrepChecklist()).stream(),
                        safeList(countyWorkflowSynthesis == null ? null : countyWorkflowSynthesis.holdQuoteChecks()).stream()
                )
                .filter(this::hasText)
                .distinct()
                .limit(6)
                .toList();

        return new StateCostScopeView(
                "Cost scope router",
                costScopeHeading(route, state.stateName()),
                costScopeIntro(route, state.stateName()),
                List.of(
                        new CountyWorkflowFieldView("Clear first", clearFirst),
                        new CountyWorkflowFieldView("Low-end breaker", lowEndBreaker),
                        new CountyWorkflowFieldView("County widener", countyWidener),
                        new CountyWorkflowFieldView("Stop trusting midpoint when", midpointGate)
                ),
                costScopeWidenersHeading(route, state.stateName()),
                scopeWideners,
                costReadinessHeading(route),
                readinessChecks
        );
    }

    private String costScopeHeading(String route, String stateName) {
        return switch (route) {
            case "septic-inspection-cost" -> "What actually widens " + stateName + " inspection pricing";
            case "perc-test-cost" -> "What actually widens " + stateName + " site-review pricing";
            case "septic-pumping-cost" -> "What actually widens " + stateName + " pumping and maintenance pricing";
            default -> "What actually widens " + stateName + " replacement pricing";
        };
    }

    private String costScopeIntro(String route, String stateName) {
        return switch (route) {
            case "septic-inspection-cost" -> "Use this router before you trust the midpoint. It separates a routine inspection visit from the county artifacts and failure trails that make the scope wider in " + stateName + ".";
            case "perc-test-cost" -> "Use this router before you trust the first perc or site-review number. It separates a routine soils visit from the parcel, redesign, and permit branches that widen the scope in " + stateName + ".";
            case "septic-pumping-cost" -> "Use this router before you trust a basic pumping number. It separates a routine service visit from the operating history, inspection cadence, and maintenance obligations that widen the scope in " + stateName + ".";
            default -> "Use this router before you trust the midpoint. It separates a straightforward replacement story from the county file, failure lane, and redesign triggers that widen the real scope in " + stateName + ".";
        };
    }

    private String costScopeWidenersHeading(String route, String stateName) {
        return switch (route) {
            case "septic-inspection-cost" -> "What keeps widening " + stateName + " inspection scope";
            case "perc-test-cost" -> "What keeps widening " + stateName + " site-review scope";
            case "septic-pumping-cost" -> "What keeps widening " + stateName + " maintenance scope";
            default -> "What keeps widening " + stateName + " replacement scope";
        };
    }

    private String costReadinessHeading(String route) {
        return switch (route) {
            case "septic-inspection-cost" -> "What to line up before you price inspection scope";
            case "perc-test-cost" -> "What to line up before you price site-review scope";
            case "septic-pumping-cost" -> "What to line up before you price maintenance scope";
            default -> "What to line up before you price replacement scope";
        };
    }

    private String preferredCostWidener(StateCountyWorkflowSynthesisView countyWorkflowSynthesis, String route) {
        if (countyWorkflowSynthesis == null || countyWorkflowSynthesis.structureHighlights().isEmpty()) {
            return null;
        }
        String preferredLabel = switch (route) {
            case "septic-inspection-cost" -> "Most common malfunction or repair trail";
            case "perc-test-cost" -> "Most common permit closeout signal";
            case "septic-pumping-cost" -> "Most common special program or exception";
            default -> "Most common malfunction or repair trail";
        };
        return countyWorkflowSynthesis.structureHighlights().stream()
                .filter(field -> preferredLabel.equals(field.label()))
                .map(CountyWorkflowFieldView::value)
                .findFirst()
                .orElseGet(() -> countyWorkflowSynthesis.structureHighlights().get(0).value());
    }

    private String firstOf(List<String> values) {
        return safeList(values).stream()
                .filter(this::hasText)
                .findFirst()
                .orElse(null);
    }

    private List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    private String stateMoneyCalculatorPath(StateMoneyPage stateMoneyPage, StateProfile state) {
        return "/septic-system-cost-calculator/?state=" + state.stateCode()
                + "&projectType=" + stateMoneyPage.calculatorProjectType()
                + "&sourcePageHint=" + stateMoneyPage.path(state.slug());
    }

    private String stateMoneyQuotePath(String calculatorPath) {
        return calculatorPath + "&quoteMode=true#quote-request";
    }

    private Optional<String> stateMoneyPageLinkTitle(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return Optional.empty();
        }
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length == 2) {
            return researchDataService.findStateMoneyPage(parts[0], parts[1])
                    .map(StateMoneyPage::title);
        }
        return Optional.empty();
    }

    private Optional<String> countyRecordsPageLinkTitle(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return Optional.empty();
        }
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length == 3 && "septic-records-checklist".equals(parts[0])) {
            return researchDataService.findCountyRecordsPage(parts[1], parts[2])
                    .map(CountyRecordsPage::title);
        }
        return Optional.empty();
    }

    private Optional<String> contentPageLinkTitle(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return Optional.empty();
        }
        String slug = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "");
        if (slug.isBlank()) {
            return Optional.empty();
        }
        return researchDataService.findContentPage(slug)
                .map(ContentPage::title);
    }

    private String normalizePath(String path) {
        return UriComponentsBuilder.fromUriString(path).build().getPath();
    }

    private String projectTypeLabel(String projectType) {
        return switch (projectType) {
            case "replacement" -> "replacement";
            case "perc_test" -> "perc test";
            case "drainfield_replacement" -> "drain field";
            case "pumping" -> "pumping";
            case "inspection" -> "inspection";
            case "buying_home" -> "buyer";
            default -> "project";
        };
    }

    private String prettifyPath(String path) {
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null || normalizedPath.isBlank() || "/".equals(normalizedPath)) {
            return "Home";
        }
        String lastSegment = normalizedPath.replaceFirst("/$", "");
        lastSegment = lastSegment.substring(lastSegment.lastIndexOf('/') + 1);
        return Arrays.stream(lastSegment.split("-"))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .reduce((left, right) -> left + " " + right)
                .orElse("Related page");
    }

    private int relatedLinkScore(String sourceSlug, String sourceStateCode, String targetPath) {
        String normalizedPath = normalizePath(targetPath);
        if (normalizedPath == null) {
            return 0;
        }

        int score = 0;
        if (isCalculatorPath(normalizedPath)) {
            score += 20;
            if ("septic-system-cost-calculator".equals(sourceSlug)) {
                score += switch (calculatorProjectTypeFromPath(targetPath)) {
                    case "replacement" -> 8;
                    case "inspection" -> 7;
                    case "perc_test" -> 6;
                    default -> 0;
                };
            }
        }

        Optional<String> targetStateSlug = stateSlugFromPath(normalizedPath);
        if (sourceStateCode != null && targetStateSlug.isPresent()) {
            Optional<StateProfile> targetState = researchDataService.findStateBySlug(targetStateSlug.get());
            if (targetState.filter(state -> state.stateCode().equalsIgnoreCase(sourceStateCode)).isPresent()) {
                score += 18;
            }
        }

        String targetContentSlug = targetContentSlug(normalizedPath);
        if (targetContentSlug != null) {
            List<String> preferredTargets = preferredTargetSlugs(sourceSlug);
            int preferredIndex = preferredTargets.indexOf(targetContentSlug);
            if (preferredIndex >= 0) {
                score += 30 - (preferredIndex * 4);
            }
            if (targetContentSlug.equals(sourceSlug)) {
                score -= 10;
            }
        }

        if (targetStateSlug.isPresent()) {
            score += 4;
        }

        if (isCountyRecordsPath(normalizedPath)) {
            score += switch (sourceSlug) {
                case PERMIT_LOOKUP_SLUG -> 46;
                case TRANSFER_COMPLIANCE_SLUG -> 42;
                case "septic-records-checklist" -> 18;
                default -> 8;
            };
            score += targetStateSlug
                    .flatMap(researchDataService::findStateBySlug)
                    .map(StateProfile::stateCode)
                    .map(stateCode -> switch (stateCode) {
                        case "TN" -> 14;
                        case "NC", "TX" -> 10;
                        case "GA" -> 10;
                        case "AL" -> 8;
                        case "IN" -> 4;
                        default -> 0;
                    })
                    .orElse(0);
        }

        return score;
    }

    private String relatedLinkNote(String sourceSlug, String sourceStateCode, String targetPath) {
        String normalizedPath = normalizePath(targetPath);
        if (normalizedPath == null) {
            return "Use this page when you need the next step to be more specific than the current overview.";
        }

        if (isCalculatorPath(normalizedPath)) {
            String projectType = calculatorProjectTypeFromPath(targetPath);
            if (sourceStateCode != null && projectType != null) {
                return "Run the estimate with " + sourceStateCode + " and " + projectTypeLabel(projectType) + " prefilled before you compare local quotes.";
            }
            return "Use the estimator when you still need a planning range before committing to one narrative.";
        }

        Optional<String> guideStateSlug = stateSlugFromPath(normalizedPath);
        if (normalizedPath.startsWith("/septic-system-cost-calculator/") && guideStateSlug.isPresent()) {
            return researchDataService.findStateBySlug(guideStateSlug.get())
                    .map(StateProfile::stateName)
                    .or(() -> usStateDirectoryService.findBySlug(guideStateSlug.get()).map(UsStateDirectoryService.UsStateReference::stateName))
                    .map(stateName -> "Open the " + stateName + " guide for permit path, local office, and records workflow context.")
                    .orElse("Open the state guide for permit path and records context.");
        }

        if (isCountyRecordsPath(normalizedPath)) {
            if (TRANSFER_COMPLIANCE_SLUG.equals(sourceSlug)) {
                return "Use this when closing risk turns on a county file, certification letter, or local health-office workflow instead of one statewide summary.";
            }
            return "Use this when the next step is a county file, certification letter, or local health-office workflow rather than a broader state page.";
        }

        String contentSlug = targetContentSlug(normalizedPath);
        if (contentSlug != null) {
            String intentNote = switch (contentSlug) {
                case "septic-replacement-cost" -> "Use this when failure scope or full replacement risk is the real blocker.";
                case "perc-test-cost" -> "Use this when soil, perc, or site-approval uncertainty is driving the decision.";
                case "drain-field-replacement-cost" -> "Use this when the field layout may be the real problem rather than the tank alone.";
                case "failed-perc-test-septic" -> "Use this when a failed or weak perc result is forcing a bigger field or system decision.";
                case "septic-replacement-area" -> "Use this when reserve area or replacement-layout viability is the real blocker.";
                case "wet-yard-over-septic-drain-field" -> "Use this when seepage, odor, or soggy ground near the field is driving urgency.";
                case "septic-pumping-cost" -> "Use this when maintenance cadence or advanced-system upkeep is the open question.";
                case "septic-inspection-cost" -> "Use this when due-diligence scope or inspection leverage matters more than a generic average.";
                case "buying-a-house-with-a-septic-system" -> "Use this when the property deal, not just the system price, is driving risk.";
                case PERMIT_LOOKUP_SLUG -> "Use this when the searcher needs one permit lookup doorway before choosing the state records or permit path.";
                case "septic-permit-process" -> "Use this when the next office, permit step, or approval sequence is the real bottleneck.";
                case "septic-records-checklist" -> "Use this when the file is thinner than the current seller, owner, or contractor story.";
                case TRANSFER_COMPLIANCE_SLUG -> "Use this when records, permits, buyer timing, and county workflow need to be resolved together.";
                case "septic-tank-size" -> "Use this when bedroom sizing and minimum gallon band matter more than a full project quote.";
                default -> "Use this page for the next layer of detail after the current overview.";
            };
            if (sourceStateCode != null && guideStateSlug.isPresent()) {
                return intentNote;
            }
            return intentNote;
        }

        return "Use this page when you need the next step to be more specific than the current overview.";
    }

    private List<String> preferredTargetSlugs(String sourceSlug) {
        return switch (sourceSlug) {
            case "septic-system-cost-calculator" -> List.of(
                    TRANSFER_COMPLIANCE_SLUG,
                    PERMIT_LOOKUP_SLUG,
                    "septic-replacement-cost",
                    "septic-inspection-cost",
                    "perc-test-cost",
                    "septic-records-checklist",
                    "buying-a-house-with-a-septic-system",
                    "septic-tank-size",
                    "septic-pumping-cost"
            );
            case "septic-replacement-cost" -> List.of(
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "perc-test-cost",
                    "buying-a-house-with-a-septic-system",
                    "drain-field-replacement-cost"
            );
            case "perc-test-cost" -> List.of(
                    "failed-perc-test-septic",
                    "septic-replacement-area",
                    PERMIT_LOOKUP_SLUG,
                    "septic-permit-process",
                    "septic-replacement-cost",
                    "septic-records-checklist",
                    "drain-field-replacement-cost"
            );
            case "drain-field-replacement-cost" -> List.of(
                    "septic-replacement-area",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "wet-yard-over-septic-drain-field",
                    "failed-perc-test-septic",
                    "perc-test-cost",
                    "septic-system-cost-calculator"
            );
            case "failed-perc-test-septic" -> List.of(
                    "perc-test-cost",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "septic-replacement-area",
                    "septic-inspection-cost",
                    "septic-system-cost-calculator"
            );
            case "septic-replacement-area" -> List.of(
                    "drain-field-replacement-cost",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "failed-perc-test-septic",
                    "perc-test-cost",
                    "septic-system-cost-calculator"
            );
            case "wet-yard-over-septic-drain-field" -> List.of(
                    "septic-inspection-cost",
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "septic-replacement-area",
                    "septic-replacement-cost",
                    "septic-system-cost-calculator"
            );
            case "septic-pumping-cost" -> List.of("septic-tank-size", "septic-system-cost-calculator", "septic-inspection-cost");
            case "septic-inspection-cost" -> List.of(
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "buying-a-house-with-a-septic-system",
                    "wet-yard-over-septic-drain-field",
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic",
                    "septic-system-cost-calculator"
            );
            case "buying-a-house-with-a-septic-system" -> List.of(
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-inspection-cost",
                    "septic-permit-process",
                    "drain-field-replacement-cost",
                    "wet-yard-over-septic-drain-field",
                    "septic-replacement-area",
                    "septic-replacement-cost"
            );
            case "septic-permit-process" -> List.of(
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-replacement-cost",
                    "buying-a-house-with-a-septic-system",
                    "septic-system-cost-calculator"
            );
            case "septic-records-checklist" -> List.of(
                    PERMIT_LOOKUP_SLUG,
                    "drain-field-replacement-cost",
                    "failed-perc-test-septic",
                    "septic-replacement-area",
                    "buying-a-house-with-a-septic-system",
                    "septic-permit-process",
                    "septic-inspection-cost",
                    "septic-replacement-cost"
            );
            case TRANSFER_COMPLIANCE_SLUG -> List.of(
                    PERMIT_LOOKUP_SLUG,
                    "septic-records-checklist",
                    "septic-permit-process",
                    "buying-a-house-with-a-septic-system",
                    "septic-inspection-cost",
                    "septic-system-cost-calculator"
            );
            case PERMIT_LOOKUP_SLUG -> List.of(
                    "septic-records-checklist",
                    "septic-permit-process",
                    TRANSFER_COMPLIANCE_SLUG,
                    "buying-a-house-with-a-septic-system",
                    "septic-inspection-cost",
                    "septic-system-cost-calculator"
            );
            default -> List.of();
        };
    }

    private boolean isCalculatorPath(String normalizedPath) {
        return "/septic-system-cost-calculator/".equals(normalizedPath)
                || "/septic-system-cost-calculator".equals(normalizedPath)
                || "/septic-tank-size-estimator/".equals(normalizedPath)
                || "/septic-tank-size-estimator".equals(normalizedPath)
                || "/septic-pump-schedule-estimator/".equals(normalizedPath)
                || "/septic-pump-schedule-estimator".equals(normalizedPath)
                || "/drain-field-estimator/".equals(normalizedPath)
                || "/drain-field-estimator".equals(normalizedPath);
    }

    private String calculatorProjectTypeFromPath(String path) {
        var uri = UriComponentsBuilder.fromUriString(path).build();
        String normalizedPath = uri.getPath();
        if (!"/septic-system-cost-calculator/".equals(normalizedPath) && !"/septic-system-cost-calculator".equals(normalizedPath)) {
            return null;
        }
        return uri.getQueryParams().getOrDefault("projectType", List.of()).stream().findFirst().orElse(null);
    }

    private Optional<String> stateSlugFromPath(String normalizedPath) {
        String prefix = "/septic-system-cost-calculator/";
        if (normalizedPath.startsWith(prefix)) {
            String stateSlug = normalizedPath.substring(prefix.length()).replaceFirst("/$", "");
            if (!stateSlug.isBlank() && !stateSlug.contains("/")) {
                return Optional.of(stateSlug);
            }
        }

        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length == 2 || (parts.length == 3 && "septic-records-checklist".equals(parts[0]))) {
            return Optional.of(parts[1]);
        }
        return Optional.empty();
    }

    private String targetContentSlug(String normalizedPath) {
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        if (parts.length == 1) {
            return parts[0];
        }
        if (parts.length == 2) {
            return parts[0];
        }
        if (parts.length == 3 && "septic-records-checklist".equals(parts[0])) {
            return parts[0];
        }
        return null;
    }

    private boolean isCountyRecordsPath(String normalizedPath) {
        String[] parts = normalizedPath.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        return parts.length == 3 && "septic-records-checklist".equals(parts[0]);
    }

    private List<StateRuleFactView> stateRuleFactViews(String stateCode) {
        return researchDataService.listPublicStateRuleFacts(stateCode).stream()
                .map(fact -> {
                    SourceRecord source = researchDataService.findSource(fact.sourceId()).orElse(null);
                    return new StateRuleFactView(
                            fact.label(),
                            fact.renderedValue(),
                            fact.note(),
                            firstNonBlank(fact.effectiveDate(), source != null ? source.effectiveDate() : null),
                            firstNonBlank(fact.lastVerifiedAt(), source != null ? source.lastVerifiedAt() : null),
                            confidenceLabel(fact.confidence()),
                            source != null ? source.agencyName() : "",
                            source != null ? source.title() : "",
                            source != null ? source.url() : "",
                            fact.sourceSection(),
                            source != null ? source.trustLevel() : "",
                            source != null ? source.draftOrFinalStatus() : ""
                    );
                })
                .toList();
    }

    private List<CostEvidenceView> costEvidenceViews(String stateCode, String projectType) {
        return researchDataService.listCostEvidence(stateCode, projectType).stream()
                .map(evidence -> new CostEvidenceView(
                        evidence.title(),
                        costEvidenceValueSummary(evidence),
                        evidence.note(),
                        evidence.sourceIds().stream()
                                .map(researchDataService::findSource)
                                .flatMap(Optional::stream)
                                .map(source -> source.agencyName() + ": " + source.title())
                                .reduce((left, right) -> left + " | " + right)
                                .orElse("Source under review")
                ))
                .toList();
    }

    private String costEvidenceValueSummary(com.example.septic.data.model.CostEvidence evidence) {
        if (evidence.multiplier() != null) {
            return "Multiplier " + String.format(Locale.US, "%.3f", evidence.multiplier());
        }
        if (evidence.low() != null && evidence.high() != null) {
            String range = money(evidence.low()) + " to " + money(evidence.high());
            if (evidence.mid() != null) {
                return range + " | midpoint about " + money(evidence.mid());
            }
            return range;
        }
        return "Planning evidence";
    }

    private String confidenceLabel(Double confidence) {
        if (confidence == null) {
            return "";
        }
        if (confidence >= 0.9) {
            return "Very high confidence";
        }
        if (confidence >= 0.75) {
            return "High confidence";
        }
        if (confidence >= 0.6) {
            return "Moderate confidence";
        }
        return "Directional confidence";
    }

    @SafeVarargs
    private final int distinctSourceCount(List<String>... sourceIdGroups) {
        return (int) Stream.of(sourceIdGroups)
                .filter(group -> group != null)
                .flatMap(List::stream)
                .filter(sourceId -> sourceId != null && !sourceId.isBlank())
                .distinct()
                .count();
    }

    private String joinWithAnd(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        if (values.size() == 1) {
            return values.get(0);
        }
        if (values.size() == 2) {
            return values.get(0) + " and " + values.get(1);
        }
        return String.join(", ", values.subList(0, values.size() - 1))
                + ", and " + values.get(values.size() - 1);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean hasItems(List<?> values, int minimumSize) {
        return values != null && values.size() >= minimumSize;
    }

    private int size(List<?> values) {
        return values == null ? 0 : values.size();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private String firstListItem(List<String> items, String fallback) {
        if (items == null || items.isEmpty()) {
            return fallback;
        }
        return items.get(0);
    }
}
