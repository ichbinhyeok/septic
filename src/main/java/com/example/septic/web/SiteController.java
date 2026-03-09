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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SiteController {
    private static final List<String> CORE_STATE_CODES = List.of("GA", "PA", "CT", "OR", "MA", "FL");

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
        model.addAttribute("states", researchDataService.getStateProfiles());
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
                                "This project is still an early-stage file-backed application. Storage and routing practices will be refined as partner workflows become more specific.",
                                List.of(
                                        "Do not submit sensitive financial information through the quote form.",
                                        "Do not treat a quote request as a guarantee that a contractor will contact you.",
                                        "This page should be reviewed by counsel before public launch if you commercialize lead routing at scale."
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
                "These terms describe the intended use of the public estimator and related content. They are operational terms for the site and should be reviewed before commercial launch.",
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
                                "The site may evolve, change, or stop accepting quote requests without notice while the product is in active development.",
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
        return renderSitePage(
                model,
                seoService.basicPage(
                        "Contact",
                        "How to contact the project and what channel to use for septic quote requests.",
                        "/contact/"
                ),
                "Contact",
                "Use the quote form for project-specific help.",
                "Project-specific quote requests should start on the calculator result page so the lead is saved with the estimate context. This contact page exists for general project and policy questions.",
                Arrays.asList(
                        new SitePageSection(
                                "Best path for homeowner projects",
                                "If you want septic pricing help, use the estimate flow first so the request includes the state, project type, and site assumptions that make the lead useful.",
                                List.of(
                                        "Open the main estimator and complete the project inputs.",
                                        "Review the planning result and state-specific context.",
                                        "Submit the short quote request form after you have seen value."
                                )
                        ),
                        new SitePageSection(
                                "General questions",
                                "Business contact details should be published here before public launch. Until then, operational questions should be handled through the project owner workflow outside the public site.",
                                List.of(
                                        "Use this page as the placeholder for a public support email or business address.",
                                        "Review privacy and terms pages before enabling broader lead routing.",
                                        "Keep the public contact point aligned with the actual business entity and domain owner."
                                )
                        )
                ),
                "Publish real business contact details before launch",
                "This page is intentionally honest: replace placeholder operational copy with the real support contact before you drive paid or organic traffic here."
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
            Model model
    ) {
        EstimateForm estimateForm = new EstimateForm();
        if (stateCode != null && researchDataService.findStateByCode(stateCode).isPresent()) {
            estimateForm.setStateCode(stateCode.toUpperCase(Locale.US));
        }
        if (projectType != null) {
            estimateForm.setProjectType(ProjectType.fromValue(projectType).value());
        }
        return renderCalculator(model, estimateForm, null, QuoteLeadForm.fromEstimateForm(estimateForm), null, false);
    }

    @GetMapping({"/septic-tank-size-estimator", "/septic-tank-size-estimator/"})
    public String tankSizeEstimator(Model model) {
        return renderTankSizeEstimator(model, new TankSizeForm(), null);
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
        return renderCalculator(model, estimateForm, result, QuoteLeadForm.fromEstimateForm(estimateForm), null, false);
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
            return renderCalculator(model, estimateForm, result, quoteLeadForm, null, true);
        }

        String leadId = leadStorageService.saveQuoteLead(
                quoteLeadForm,
                estimateForm,
                result,
                "/septic-system-cost-calculator/",
                request
        );
        QuoteLeadForm clearedQuoteForm = QuoteLeadForm.fromEstimateForm(estimateForm);
        return renderCalculator(model, estimateForm, result, clearedQuoteForm, leadId, false);
    }

    @GetMapping({"/septic-system-cost-calculator/{stateSlug}", "/septic-system-cost-calculator/{stateSlug}/"})
    public String stateGuide(@PathVariable String stateSlug, Model model) {
        StateProfile state = researchDataService.findStateBySlug(stateSlug)
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(state.officialSourceIds());
        List<SourceRecord> localAuthoritySources = researchDataService.getSources(state.localAuthoritySourceIds());
        List<SourceRecord> recordsLookupSources = researchDataService.getSources(state.recordsLookupSourceIds());
        StateActionCopy stateActionCopy = stateActionCopy(state);
        StatePlanningSnapshot planningSnapshot = statePlanningSnapshot(state.stateCode());
        List<CoreStateComparisonRow> coreStateComparisonRows = coreStateComparisonRows(state);

        model.addAttribute("page", seoService.stateGuide(state));
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("localAuthoritySources", localAuthoritySources);
        model.addAttribute("recordsLookupSources", recordsLookupSources);
        model.addAttribute("primaryLocalAuthoritySource", localAuthoritySources.stream().findFirst().orElse(null));
        model.addAttribute("primaryRecordsLookupSource", recordsLookupSources.stream().findFirst().orElse(null));
        model.addAttribute("stateMoneyPages", researchDataService.listStateMoneyPages(state.stateCode()));
        model.addAttribute("guideFaqs", seoService.stateGuideFaqs(state));
        model.addAttribute("guideHeading", seoService.stateGuideHeading(state));
        model.addAttribute("calculatorCtaLabel", stateActionCopy.buttonLabel());
        model.addAttribute("calculatorCtaNote", stateActionCopy.supportingNote());
        model.addAttribute("planningSnapshot", planningSnapshot);
        model.addAttribute("coreStateComparisonRows", coreStateComparisonRows);
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
        ContentPage contentPage = researchDataService.findContentPage(slug)
                .orElseThrow(() -> new StateNotFoundException(slug));
        List<StateMoneyPageLink> stateMoneyPageLinks = researchDataService.listStateMoneyPagesForContent(slug).stream()
                .map(page -> researchDataService.findStateByCode(page.stateCode())
                        .map(state -> new StateMoneyPageLink(page.title(), state.stateName(), page.path(state.slug()))))
                .flatMap(Optional::stream)
                .toList();

        model.addAttribute("page", seoService.contentPage(contentPage));
        model.addAttribute("contentPage", contentPage);
        model.addAttribute("states", researchDataService.getStateProfiles().stream().limit(6).toList());
        model.addAttribute("stateMoneyPageLinks", stateMoneyPageLinks);
        model.addAttribute("calculatorPath", calculatorPathForModule(contentPage.calculatorModule()));
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

        StateMoneyPage stateMoneyPage = researchDataService.findStateMoneyPage(contentSlug, stateSlug)
                .orElseThrow(() -> new StateNotFoundException(path));
        StateProfile state = researchDataService.findStateByCode(stateMoneyPage.stateCode())
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(stateMoneyPage.officialSourceIds());
        List<SourceRecord> localAuthoritySources = researchDataService.getSources(state.localAuthoritySourceIds());
        List<SourceRecord> recordsLookupSources = researchDataService.getSources(state.recordsLookupSourceIds());
        StateActionCopy stateActionCopy = stateActionCopy(state);
        StatePlanningSnapshot planningSnapshot = statePlanningSnapshot(state.stateCode());

        model.addAttribute("page", seoService.stateMoneyPage(stateMoneyPage, state));
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
            boolean quoteHasErrors
    ) {
        model.addAttribute("page", seoService.calculatorPage());
        model.addAttribute("states", researchDataService.getStateProfiles());
        model.addAttribute("estimateForm", estimateForm);
        model.addAttribute("result", result);
        model.addAttribute("quoteLeadForm", quoteLeadForm);
        model.addAttribute("leadId", leadId);
        model.addAttribute("quoteHasErrors", quoteHasErrors);
        return "pages/calculator";
    }

    private String renderTankSizeEstimator(Model model, TankSizeForm tankSizeForm, TankSizeEstimatorResult result) {
        model.addAttribute("page", seoService.tankSizeEstimatorPage());
        model.addAttribute("states", researchDataService.getStateProfiles());
        model.addAttribute("tankSizeForm", tankSizeForm);
        model.addAttribute("result", result);
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

    private String calculatorPathForModule(String calculatorModule) {
        return switch (calculatorModule) {
            case "tank_size_estimator" -> "/septic-tank-size-estimator/";
            case "pump_schedule_estimator" -> "/septic-pump-schedule-estimator/";
            default -> "/septic-system-cost-calculator/";
        };
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
        List<String> prioritySlugs = List.of(
                "septic-inspection-cost",
                "buying-a-house-with-a-septic-system",
                "septic-records-checklist",
                "septic-permit-process",
                "septic-replacement-cost"
        );

        for (String contentSlug : prioritySlugs) {
            Optional<StateMoneyPage> page = researchDataService.findStateMoneyPage(contentSlug, state.slug());
            if (page.isPresent()) {
                return page;
            }
        }
        return Optional.empty();
    }

    private String firstListItem(List<String> items, String fallback) {
        if (items == null || items.isEmpty()) {
            return fallback;
        }
        return items.get(0);
    }
}
