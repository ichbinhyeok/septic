package com.example.septic.web;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.ProjectCostAnchor;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateCostProfile;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import com.example.septic.service.AccessDifficulty;
import com.example.septic.service.EstimatorResult;
import com.example.septic.service.EstimatorService;
import com.example.septic.service.LeadStorageService;
import com.example.septic.service.ProjectType;
import com.example.septic.service.ResearchDataService;
import com.example.septic.service.SeoService;
import com.example.septic.service.SitemapService;
import com.example.septic.service.SoilPercStatus;
import com.example.septic.service.TankSizeEstimatorResult;
import com.example.septic.service.TankSizeEstimatorService;
import com.example.septic.service.TimelinePreference;
import com.example.septic.service.PumpScheduleResult;
import com.example.septic.service.PumpScheduleService;
import com.example.septic.service.OccupancyProfile;
import com.example.septic.service.UsageProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
    private final TankSizeEstimatorService tankSizeEstimatorService;
    private final PumpScheduleService pumpScheduleService;

    public SiteController(
            ResearchDataService researchDataService,
            EstimatorService estimatorService,
            LeadStorageService leadStorageService,
            SeoService seoService,
            SitemapService sitemapService,
            TankSizeEstimatorService tankSizeEstimatorService,
            PumpScheduleService pumpScheduleService
    ) {
        this.researchDataService = researchDataService;
        this.estimatorService = estimatorService;
        this.leadStorageService = leadStorageService;
        this.seoService = seoService;
        this.sitemapService = sitemapService;
        this.tankSizeEstimatorService = tankSizeEstimatorService;
        this.pumpScheduleService = pumpScheduleService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", seoService.homePage());
        List<StateProfile> publicStates = researchDataService.getPublicStateProfiles();
        model.addAttribute("featuredStates", publicStates.stream()
                .filter(state -> "anchor".equalsIgnoreCase(state.launchTier()))
                .toList());
        model.addAttribute("additionalStates", publicStates.stream()
                .filter(state -> !"anchor".equalsIgnoreCase(state.launchTier()))
                .toList());
        return "pages/home";
    }

    @GetMapping({"/about", "/about/"})
    public String about(Model model) {
        return renderSitePage(
                model,
                seoService.basicPage(
                        "About Septic System Cost & Size Estimator",
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
                "This page describes the current handling of form submissions and site interaction data for Septic System Cost & Size Estimator. It is an operational policy page, not legal advice.",
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
            @RequestParam(name = "quoteMode", defaultValue = "false") boolean quoteMode,
            Model model
    ) {
        EstimateForm estimateForm = new EstimateForm();
        if (stateCode != null && researchDataService.findStateByCode(stateCode).isPresent()) {
            estimateForm.setStateCode(stateCode.toUpperCase(Locale.US));
        }
        if (projectType != null) {
            estimateForm.setProjectType(ProjectType.fromValue(projectType).value());
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
        StateProfile state = researchDataService.findPublicStateBySlug(stateSlug)
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(state.officialSourceIds());
        List<SourceRecord> localAuthoritySources = researchDataService.getSources(state.localAuthoritySourceIds());
        List<SourceRecord> recordsLookupSources = researchDataService.getSources(state.recordsLookupSourceIds());
        List<StateRuleFactView> stateRuleFacts = stateRuleFactViews(state.stateCode());
        StateActionCopy stateActionCopy = stateActionCopy(state);
        StatePlanningSnapshot planningSnapshot = statePlanningSnapshot(state.stateCode());
        List<CoreStateComparisonRow> coreStateComparisonRows = coreStateComparisonRows(state);
        String lastReviewedAt = latestVerifiedAt(sources, state.lastVerifiedAt());

        model.addAttribute("page", seoService.stateGuide(state, lastReviewedAt, STATE_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("localAuthoritySources", localAuthoritySources);
        model.addAttribute("recordsLookupSources", recordsLookupSources);
        model.addAttribute("primaryLocalAuthoritySource", localAuthoritySources.stream().findFirst().orElse(null));
        model.addAttribute("primaryRecordsLookupSource", recordsLookupSources.stream().findFirst().orElse(null));
        model.addAttribute("stateMoneyPages", researchDataService.listPublicStateMoneyPages(state.stateCode()));
        model.addAttribute("stateRuleFacts", stateRuleFacts);
        model.addAttribute("guideFaqs", seoService.stateGuideFaqs(state));
        model.addAttribute("guideHeading", seoService.stateGuideHeading(state));
        model.addAttribute("calculatorCtaLabel", stateActionCopy.buttonLabel());
        model.addAttribute("calculatorCtaNote", stateActionCopy.supportingNote());
        model.addAttribute("planningSnapshot", planningSnapshot);
        model.addAttribute("coreStateComparisonRows", coreStateComparisonRows);
        model.addAttribute("editorialPreparedBy", STATE_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", "Reviewed against " + sources.size() + " official sources listed below.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", STATE_EDITORIAL_NOTE);
        return "pages/state-guide";
    }

    @GetMapping({
            "/septic-replacement-cost", "/septic-replacement-cost/",
            "/septic-tank-size", "/septic-tank-size/",
            "/perc-test-cost", "/perc-test-cost/",
            "/drain-field-replacement-cost", "/drain-field-replacement-cost/",
            "/septic-pumping-cost", "/septic-pumping-cost/",
            "/septic-inspection-cost", "/septic-inspection-cost/",
            "/buying-a-house-with-a-septic-system", "/buying-a-house-with-a-septic-system/",
            "/septic-permit-process", "/septic-permit-process/",
            "/septic-records-checklist", "/septic-records-checklist/"
    })
    public String contentPage(org.springframework.web.context.request.WebRequest request, Model model) {
        String path = request.getDescription(false).replace("uri=", "");
        String slug = path.replaceFirst("^/", "").replaceFirst("/$", "");
        ContentPage contentPage = researchDataService.findPublicContentPage(slug)
                .orElseThrow(() -> new StateNotFoundException(slug));
        List<Map.Entry<StateMoneyPage, StateProfile>> rankedStateEntries = researchDataService.listPublicStateMoneyPagesForContent(slug).stream()
                .flatMap(page -> researchDataService.findStateByCode(page.stateCode())
                        .map(state -> Map.entry(page, state))
                        .stream())
                .sorted(Comparator
                        .comparingInt((Map.Entry<StateMoneyPage, StateProfile> entry) -> contentStateLinkScore(contentPage, entry.getKey(), entry.getValue()))
                        .reversed()
                        .thenComparing(entry -> entry.getValue().stateName()))
                .toList();
        List<StateMoneyPageLink> stateMoneyPageLinks = rankedStateEntries.stream()
                .map(entry -> new StateMoneyPageLink(
                        entry.getKey().title(),
                        entry.getValue().stateName(),
                        entry.getKey().path(entry.getValue().slug())))
                .toList();
        List<ContentEvidenceLaneView> contentEvidenceLanes = rankedStateEntries.stream()
                .limit(4)
                .map(entry -> contentEvidenceLane(entry.getKey(), entry.getValue()))
                .toList();
        String lastReviewedAt = researchDataService.contentPagesGeneratedAt();

        model.addAttribute("page", seoService.contentPage(contentPage, lastReviewedAt, CONTENT_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("contentPage", contentPage);
        model.addAttribute("states", researchDataService.getPublicStateProfiles());
        model.addAttribute("stateMoneyPageLinks", stateMoneyPageLinks);
        model.addAttribute("contentEvidenceLanes", contentEvidenceLanes);
        model.addAttribute("internalLinks", pageLinks(contentPage.internalLinkTargets(), contentPage.slug(), null));
        model.addAttribute("calculatorPath", calculatorPathForContentPage(contentPage));
        model.addAttribute("calculatorCtaHeading", contentActionHeading(contentPage));
        model.addAttribute("calculatorCtaLabel", contentActionLabel(contentPage));
        model.addAttribute("calculatorCtaNote", contentActionNote(contentPage));
        model.addAttribute("calculatorCtaTargetType", contentActionTargetType(contentPage));
        model.addAttribute("editorialPreparedBy", CONTENT_PAGE_PREPARER);
        model.addAttribute("editorialReviewedBy", SOURCE_REVIEWER);
        model.addAttribute("editorialReviewedAgainst", contentEvidenceLanes.isEmpty()
                ? "Reviewed against the linked state-specific pages and source policy."
                : "Reviewed against " + contentEvidenceLanes.size() + " source-backed state-specific pages and the source policy.");
        model.addAttribute("editorialLastReviewedAt", lastReviewedAt);
        model.addAttribute("editorialNote", CONTENT_EDITORIAL_NOTE);
        return "pages/content-page";
    }

    @GetMapping({
            "/septic-replacement-cost/{stateSlug}", "/septic-replacement-cost/{stateSlug}/",
            "/perc-test-cost/{stateSlug}", "/perc-test-cost/{stateSlug}/",
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
        StateActionCopy stateActionCopy = stateActionCopy(state);
        StatePlanningSnapshot planningSnapshot = statePlanningSnapshot(state.stateCode());
        String lastReviewedAt = latestVerifiedAt(sources, state.lastVerifiedAt());

        model.addAttribute("page", seoService.stateMoneyPage(stateMoneyPage, state, lastReviewedAt, STATE_PAGE_PREPARER, SOURCE_REVIEWER));
        model.addAttribute("stateMoneyPage", stateMoneyPage);
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("localAuthoritySources", localAuthoritySources);
        model.addAttribute("recordsLookupSources", recordsLookupSources);
        model.addAttribute("primaryLocalAuthoritySource", localAuthoritySources.stream().findFirst().orElse(null));
        model.addAttribute("primaryRecordsLookupSource", recordsLookupSources.stream().findFirst().orElse(null));
        model.addAttribute("calculatorCtaLabel", stateActionCopy.buttonLabel());
        model.addAttribute("calculatorCtaNote", stateActionCopy.supportingNote());
        model.addAttribute("planningSnapshot", planningSnapshot);
        model.addAttribute("internalLinks", pageLinks(stateMoneyPage.internalLinkTargets(), stateMoneyPage.contentSlug(), state.stateCode()));
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
        model.addAttribute("states", researchDataService.getPublicStateProfiles());
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
                .orElse(publicStates.isEmpty() ? null : publicStates.get(0));
        model.addAttribute("states", publicStates);
        model.addAttribute("tankSizeForm", tankSizeForm);
        model.addAttribute("result", result);
        model.addAttribute("selectedState", selectedState);
        model.addAttribute("tankSizeFaqs", seoService.tankSizeEstimatorFaqs());
        model.addAttribute("stateRuleFacts", selectedState == null ? List.of() : stateRuleFactViews(selectedState.stateCode()));
        return "pages/tank-size-estimator";
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
            default -> "/septic-system-cost-calculator/";
        };
    }

    private String calculatorPathForContentPage(ContentPage contentPage) {
        String modulePath = calculatorPathForModule(contentPage.calculatorModule());
        if (!"/septic-system-cost-calculator/".equals(modulePath)) {
            return modulePath;
        }
        if (contentPage.calculatorProjectType() == null || contentPage.calculatorProjectType().isBlank()) {
            return modulePath;
        }
        return "/septic-system-cost-calculator/?projectType=" + contentPage.calculatorProjectType();
    }

    private String contentActionHeading(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Use the replacement estimate before you compare contractor quotes.";
            case "perc-test-cost" -> "Use the site-risk estimate before you trust the low end.";
            case "drain-field-replacement-cost" -> "Use the drain field estimate before you assume the old layout still works.";
            case "septic-inspection-cost" -> "Use the inspection-risk estimate before you schedule the next call.";
            case "buying-a-house-with-a-septic-system" -> "Use the buyer-risk estimate before you rely on the seller story.";
            case "septic-permit-process" -> "Use the permit-path estimate before you call the next office.";
            case "septic-records-checklist" -> "Use the records-aware estimate before you trust the file.";
            case "septic-tank-size" -> "Open the tank size estimator before you guess the minimum gallon band.";
            case "septic-pumping-cost" -> "Open the pump schedule estimator before you assume a maintenance cadence.";
            default -> "Use the main estimator before you ask for quotes.";
        };
    }

    private String contentActionLabel(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Run a replacement planning estimate";
            case "perc-test-cost" -> "Run a site-risk estimate";
            case "drain-field-replacement-cost" -> "Run a drain field estimate";
            case "septic-inspection-cost" -> "Run an inspection-risk estimate";
            case "buying-a-house-with-a-septic-system" -> "Run a buyer-risk estimate";
            case "septic-permit-process" -> "Run a permit-path estimate";
            case "septic-records-checklist" -> "Run a records-aware estimate";
            case "septic-tank-size" -> "Open the tank size estimator";
            case "septic-pumping-cost" -> "Open the pump schedule estimator";
            default -> "Open the main estimator";
        };
    }

    private String contentActionNote(ContentPage contentPage) {
        return switch (contentPage.slug()) {
            case "septic-replacement-cost" -> "Prefill the replacement lane first so field condition, restoration, and system-class risk show up before you talk price.";
            case "perc-test-cost" -> "Use the estimate with site uncertainty in view. If perc status is still unknown, the range should stay wide on purpose.";
            case "drain-field-replacement-cost" -> "Use the drain field lane when the tank is not the main issue and the field may be driving the cost swing.";
            case "septic-inspection-cost" -> "This estimate is most useful when inspection timing, records gaps, or advanced-system scope are still unclear.";
            case "buying-a-house-with-a-septic-system" -> "Treat the estimate as a due-diligence tool first, then compare it against the inspection and records story tied to the property.";
            case "septic-permit-process" -> "Start with the install lane to frame cost and system type, then verify the real local path before you anchor on the low end.";
            case "septic-records-checklist" -> "Use the buyer lane as a planning shortcut when the file is still thin and you need to understand downside risk before asking for quotes.";
            case "septic-tank-size" -> "Use the dedicated estimator when bedroom count, occupancy profile, or disposal load matter more than a full project quote.";
            case "septic-pumping-cost" -> "Use the dedicated estimator when cadence, use profile, and tank size matter more than a one-time pumping invoice.";
            default -> "Use your state and project assumptions first, then verify locally.";
        };
    }

    private String contentActionTargetType(ContentPage contentPage) {
        return switch (contentPage.calculatorModule()) {
            case "tank_size_estimator" -> "tank_size_estimator";
            case "pump_schedule_estimator" -> "pump_schedule_estimator";
            default -> "calculator";
        };
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
            case "GA" -> new StateActionCopy(
                    "Estimate with the disposal rule in mind",
                    "Georgia homeowners often need to check whether a garbage disposal changes the likely tank band before they call the county office."
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
                reviewedAgainst,
                lastReviewedAt,
                sources
        );
    }

    private List<PageLink> pageLinks(List<String> paths, String sourceSlug, String sourceStateCode) {
        if (paths == null || paths.isEmpty()) {
            return List.of();
        }
        return paths.stream()
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
                .or(() -> stateMoneyPageLinkTitle(path))
                .or(() -> contentPageLinkTitle(path))
                .orElseGet(() -> prettifyPath(path));
        return new PageLink(title, path, relatedLinkNote(sourceSlug, sourceStateCode, path));
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
        return Optional.empty();
    }

    private int contentStateLinkScore(ContentPage contentPage, StateMoneyPage page, StateProfile state) {
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
        return score;
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
                    + (hasItems(state.recordsLookupSourceIds(), 1) ? 8 : 0);
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
            case "perc-test-cost" -> hasText(state.siteEvalSummary()) ? 9 : 0;
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
                        .map(state -> state.stateName() + " septic guide");
            }
        }
        return Optional.empty();
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
                    .map(state -> "Open the " + state.stateName() + " guide for permit path, local office, and records workflow context.")
                    .orElse("Open the state guide for permit path and records context.");
        }

        String contentSlug = targetContentSlug(normalizedPath);
        if (contentSlug != null) {
            String intentNote = switch (contentSlug) {
                case "septic-replacement-cost" -> "Use this when failure scope or full replacement risk is the real blocker.";
                case "perc-test-cost" -> "Use this when soil, perc, or site-approval uncertainty is driving the decision.";
                case "drain-field-replacement-cost" -> "Use this when the field layout may be the real problem rather than the tank alone.";
                case "septic-pumping-cost" -> "Use this when maintenance cadence or advanced-system upkeep is the open question.";
                case "septic-inspection-cost" -> "Use this when due-diligence scope or inspection leverage matters more than a generic average.";
                case "buying-a-house-with-a-septic-system" -> "Use this when the property deal, not just the system price, is driving risk.";
                case "septic-permit-process" -> "Use this when the next office, permit step, or approval sequence is the real bottleneck.";
                case "septic-records-checklist" -> "Use this when the file is thinner than the current seller, owner, or contractor story.";
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
                    "septic-replacement-cost",
                    "septic-inspection-cost",
                    "perc-test-cost",
                    "septic-records-checklist",
                    "buying-a-house-with-a-septic-system",
                    "septic-tank-size",
                    "septic-pumping-cost"
            );
            case "septic-replacement-cost" -> List.of("drain-field-replacement-cost", "perc-test-cost", "buying-a-house-with-a-septic-system");
            case "perc-test-cost" -> List.of("septic-replacement-cost", "drain-field-replacement-cost", "septic-permit-process");
            case "drain-field-replacement-cost" -> List.of("septic-replacement-cost", "perc-test-cost", "septic-system-cost-calculator");
            case "septic-pumping-cost" -> List.of("septic-tank-size", "septic-system-cost-calculator", "septic-inspection-cost");
            case "septic-inspection-cost" -> List.of("septic-records-checklist", "buying-a-house-with-a-septic-system", "septic-system-cost-calculator");
            case "buying-a-house-with-a-septic-system" -> List.of("septic-records-checklist", "septic-inspection-cost", "septic-replacement-cost");
            case "septic-permit-process" -> List.of("septic-records-checklist", "septic-system-cost-calculator", "septic-replacement-cost");
            case "septic-records-checklist" -> List.of("buying-a-house-with-a-septic-system", "septic-inspection-cost", "septic-permit-process");
            default -> List.of();
        };
    }

    private boolean isCalculatorPath(String normalizedPath) {
        return "/septic-system-cost-calculator/".equals(normalizedPath)
                || "/septic-system-cost-calculator".equals(normalizedPath)
                || "/septic-tank-size-estimator/".equals(normalizedPath)
                || "/septic-tank-size-estimator".equals(normalizedPath)
                || "/septic-pump-schedule-estimator/".equals(normalizedPath)
                || "/septic-pump-schedule-estimator".equals(normalizedPath);
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
        if (parts.length == 2) {
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
        return null;
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
