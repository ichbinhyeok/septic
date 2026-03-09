package com.example.septic.web;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.SourceRecord;
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
import com.example.septic.service.TimelinePreference;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    private final ResearchDataService researchDataService;
    private final EstimatorService estimatorService;
    private final LeadStorageService leadStorageService;
    private final SeoService seoService;
    private final SitemapService sitemapService;

    public SiteController(
            ResearchDataService researchDataService,
            EstimatorService estimatorService,
            LeadStorageService leadStorageService,
            SeoService seoService,
            SitemapService sitemapService
    ) {
        this.researchDataService = researchDataService;
        this.estimatorService = estimatorService;
        this.leadStorageService = leadStorageService;
        this.seoService = seoService;
        this.sitemapService = sitemapService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", seoService.homePage());
        model.addAttribute("states", researchDataService.getStateProfiles());
        return "pages/home";
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

    @PostMapping({"/septic-system-cost-calculator", "/septic-system-cost-calculator/"})
    public String calculate(@ModelAttribute EstimateForm estimateForm, Model model) {
        EstimatorResult result = estimatorService.estimate(estimateForm);
        return renderCalculator(model, estimateForm, result, QuoteLeadForm.fromEstimateForm(estimateForm), null, false);
    }

    @PostMapping({"/quote-request", "/quote-request/"})
    public String submitQuote(@Valid @ModelAttribute QuoteLeadForm quoteLeadForm, BindingResult bindingResult, Model model) {
        EstimateForm estimateForm = quoteLeadForm.toEstimateForm();
        EstimatorResult result = estimatorService.estimate(estimateForm);

        if (bindingResult.hasErrors()) {
            return renderCalculator(model, estimateForm, result, quoteLeadForm, null, true);
        }

        String leadId = leadStorageService.saveQuoteLead(
                quoteLeadForm,
                estimateForm,
                result,
                "/septic-system-cost-calculator/"
        );
        QuoteLeadForm clearedQuoteForm = QuoteLeadForm.fromEstimateForm(estimateForm);
        return renderCalculator(model, estimateForm, result, clearedQuoteForm, leadId, false);
    }

    @GetMapping({"/septic-system-cost-calculator/{stateSlug}", "/septic-system-cost-calculator/{stateSlug}/"})
    public String stateGuide(@PathVariable String stateSlug, Model model) {
        StateProfile state = researchDataService.findStateBySlug(stateSlug)
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(state.officialSourceIds());

        model.addAttribute("page", seoService.stateGuide(state));
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        model.addAttribute("stateMoneyPages", researchDataService.listStateMoneyPages(state.stateCode()));
        return "pages/state-guide";
    }

    @GetMapping({
            "/septic-replacement-cost", "/septic-replacement-cost/",
            "/septic-tank-size", "/septic-tank-size/",
            "/perc-test-cost", "/perc-test-cost/",
            "/drain-field-replacement-cost", "/drain-field-replacement-cost/",
            "/septic-pumping-cost", "/septic-pumping-cost/",
            "/buying-a-house-with-a-septic-system", "/buying-a-house-with-a-septic-system/"
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
        return "pages/content-page";
    }

    @GetMapping({
            "/septic-replacement-cost/{stateSlug}", "/septic-replacement-cost/{stateSlug}/",
            "/perc-test-cost/{stateSlug}", "/perc-test-cost/{stateSlug}/",
            "/buying-a-house-with-a-septic-system/{stateSlug}", "/buying-a-house-with-a-septic-system/{stateSlug}/",
            "/drain-field-replacement-cost/{stateSlug}", "/drain-field-replacement-cost/{stateSlug}/",
            "/septic-pumping-cost/{stateSlug}", "/septic-pumping-cost/{stateSlug}/"
    })
    public String stateMoneyPage(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        String path = request.getRequestURI().replaceFirst("^/", "").replaceFirst("/$", "");
        String contentSlug = path.substring(0, path.lastIndexOf('/'));

        StateMoneyPage stateMoneyPage = researchDataService.findStateMoneyPage(contentSlug, stateSlug)
                .orElseThrow(() -> new StateNotFoundException(path));
        StateProfile state = researchDataService.findStateByCode(stateMoneyPage.stateCode())
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(stateMoneyPage.officialSourceIds());

        model.addAttribute("page", seoService.stateMoneyPage(stateMoneyPage, state));
        model.addAttribute("stateMoneyPage", stateMoneyPage);
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
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
}
