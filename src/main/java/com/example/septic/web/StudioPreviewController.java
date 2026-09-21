package com.example.septic.web;

import com.example.septic.service.ResearchDataService;
import com.example.septic.service.UsStateDirectoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

/** Isolated design implementation; never enabled in production by default. */
@Controller
@ConditionalOnProperty(name = "app.studio-preview.enabled", havingValue = "true")
public class StudioPreviewController {
    private final ResearchDataService research;
    private final SiteController site;
    private final UsStateDirectoryService directory;

    public StudioPreviewController(ResearchDataService research, SiteController site, UsStateDirectoryService directory) {
        this.research = research;
        this.site = site;
        this.directory = directory;
    }

    public record GuideState(String code, String name, String slug, boolean published) {}
    public record DirectoryCounty(String name, String stateName, String stateCode, String path) {}

    @GetMapping("/design-preview/studio/north-carolina-records/")
    public String northCarolinaRecords(HttpServletRequest request, Model model) {
        site.contentPage(new org.springframework.web.context.request.ServletWebRequest(request) {
            @Override public String getDescription(boolean includeClientInfo) {
                return "uri=/north-carolina-septic-permit-lookup/";
            }
        }, model);
        model.addAttribute("featuredStudy", StudioProofSelection.select("north-carolina", "durham-county"));
        return "studio/north-carolina-records";
    }

    @GetMapping("/design-preview/studio/texas-ossf-records/")
    public String texasOssfRecords(HttpServletRequest request, Model model) {
        return regionalRecords("texas-ossf-records-search", "TX", request, model);
    }

    @GetMapping("/design-preview/studio/south-carolina-records/")
    public String southCarolinaRecords(HttpServletRequest request, Model model) {
        return regionalRecords("dhec-septic-permit-lookup", "SC", request, model);
    }

    @GetMapping("/design-preview/studio/florida-ostds-records/")
    public String floridaOstdsRecords(HttpServletRequest request, Model model) {
        return regionalRecords("florida-ostds-permit-lookup", "FL", request, model);
    }

    private String regionalRecords(String slug, String regionCode, HttpServletRequest request, Model model) {
        site.contentPage(new org.springframework.web.context.request.ServletWebRequest(request) {
            @Override public String getDescription(boolean includeClientInfo) {
                return "uri=/" + slug + "/";
            }
        }, model);
        model.addAttribute("regionCode", regionCode);
        model.addAttribute("featuredStudy", StudioProofSelection.select(
                "TX".equals(regionCode) ? "texas" : "SC".equals(regionCode) ? "south-carolina" : "florida", ""));
        return "studio/regional-records";
    }

    @GetMapping("/design-preview/studio/official-lookup-tools/")
    public String officialLookupTools(HttpServletRequest request, Model model) {
        site.contentPage(new org.springframework.web.context.request.ServletWebRequest(request) {
            @Override public String getDescription(boolean includeClientInfo) {
                return "uri=/official-septic-lookup-tools/";
            }
        }, model);
        model.addAttribute("guideStates", directory.allStates().stream()
                .map(state -> new GuideState(state.stateCode(), state.stateName(), state.slug(),
                        research.findPublicStateBySlug(state.slug()).isPresent()))
                .toList());
        model.addAttribute("featuredStudy", StudioProofSelection.select("tennessee", "overton-county"));
        return "studio/official-lookup-tools";
    }

    @GetMapping("/design-preview/studio/tdec-records/")
    public String tdecRecords(HttpServletRequest request, Model model) {
        site.contentPage(new org.springframework.web.context.request.ServletWebRequest(request) {
            @Override public String getDescription(boolean includeClientInfo) {
                return "uri=/tdec-septic-records/";
            }
        }, model);
        model.addAttribute("featuredStudy", StudioProofSelection.select("tennessee", "overton-county"));
        return "studio/tdec-records";
    }

    @GetMapping("/design-preview/studio/permit-lookup/")
    public String permitLookup(HttpServletRequest request, Model model) {
        site.contentPage(new org.springframework.web.context.request.ServletWebRequest(request) {
            @Override public String getDescription(boolean includeClientInfo) {
                return "uri=/septic-permit-lookup/";
            }
        }, model);
        model.addAttribute("guideStates", directory.allStates().stream()
                .map(state -> new GuideState(state.stateCode(), state.stateName(), state.slug(),
                        research.findPublicStateBySlug(state.slug()).isPresent()))
                .toList());
        model.addAttribute("featuredStudy", StudioProofSelection.select("tennessee", "overton-county"));
        return "studio/permit-lookup";
    }

    @GetMapping("/design-preview/studio/checklist/")
    public String recordsChecklist(Model model) {
        model.addAttribute("featuredStudy", StudioProofSelection.select("tennessee", "overton-county"));
        model.addAttribute("guideStates", directory.allStates().stream()
                .map(state -> new GuideState(state.stateCode(), state.stateName(), state.slug(),
                        research.findPublicStateMoneyPage("septic-records-checklist", state.slug()).isPresent()))
                .toList());
        return "studio/checklist";
    }

    @GetMapping("/design-preview/studio/records/{stateSlug}/")
    public String stateRecords(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/septic-records-checklist/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("recordsHeroImage", recordsHeroImage(state.stateCode()));
        return "studio/state-records";
    }

    @GetMapping("/design-preview/studio/permit-process/{stateSlug}/")
    public String statePermitProcess(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/septic-permit-process/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("processHeroImage", recordsHeroImage(state.stateCode()));
        return "studio/state-permit-process";
    }

    @GetMapping("/design-preview/studio/inspection-cost/{stateSlug}/")
    public String stateInspectionCost(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/septic-inspection-cost/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("inspectionHeroImage", recordsHeroImage(state.stateCode()));
        return "studio/state-inspection-cost";
    }

    @GetMapping("/design-preview/studio/replacement-cost/{stateSlug}/")
    public String stateReplacementCost(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/septic-replacement-cost/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("replacementHeroImage", costHeroImage(state.stateCode()));
        return "studio/state-replacement-cost";
    }

    @GetMapping("/design-preview/studio/perc-test-cost/{stateSlug}/")
    public String statePercTestCost(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/perc-test-cost/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("percHeroImage", costHeroImage(state.stateCode()));
        return "studio/state-perc-test-cost";
    }

    @GetMapping("/design-preview/studio/drain-field-cost/{stateSlug}/")
    public String stateDrainFieldCost(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/drain-field-replacement-cost/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("drainFieldHeroImage", costHeroImage(state.stateCode()));
        return "studio/state-drain-field-cost";
    }

    @GetMapping("/design-preview/studio/failed-perc/{stateSlug}/")
    public String stateFailedPerc(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/failed-perc-test-septic/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("problemHeroImage", costHeroImage(state.stateCode()));
        return "studio/state-failed-perc";
    }

    @GetMapping("/design-preview/studio/replacement-area/{stateSlug}/")
    public String stateReplacementArea(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/septic-replacement-area/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("problemHeroImage", recordsHeroImage(state.stateCode()));
        return "studio/state-replacement-area";
    }

    @GetMapping("/design-preview/studio/wet-yard/{stateSlug}/")
    public String stateWetYard(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/wet-yard-over-septic-drain-field/" + stateSlug + "/";
            }
        }, model);
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("problemHeroImage", costHeroImage(state.stateCode()));
        return "studio/state-wet-yard";
    }

    @GetMapping("/design-preview/studio/counties/")
    public String countyDirectory(Model model) {
        var states = directory.allStates();
        model.addAttribute("directoryStates", states.stream()
                .map(s -> new GuideState(s.stateCode(), s.stateName(), s.slug(), true)).toList());
        model.addAttribute("directoryCounties", research.getPublicCountyRecordsPages().stream().map(county -> {
            var state = states.stream().filter(s -> s.stateCode().equals(county.stateCode())).findFirst().orElseThrow();
            return new DirectoryCounty(county.countyName(), state.stateName(), state.stateCode(),
                    "/design-preview/studio/" + state.slug() + "/" + county.countySlug() + "/");
        }).sorted(java.util.Comparator.comparing(DirectoryCounty::stateName).thenComparing(DirectoryCounty::name)).toList());
        return "studio/county-directory";
    }

    @GetMapping("/design-preview/studio/calculator/")
    public String calculator(@RequestParam(required = false) String state,
            @RequestParam(required = false) String projectType, @RequestParam(defaultValue = "cost") String mode,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(defaultValue = "false") boolean recordsMode,
            @RequestParam(defaultValue = "") String recordSystemType, @RequestParam(defaultValue = "") String recordTankCapacity,
            @RequestParam(defaultValue = "") String recordDesignFlow, @RequestParam(defaultValue = "") String county,
            @RequestParam(defaultValue = "") String recordStatus, @RequestParam(defaultValue = "planned_project") String serviceNeed,
            @RequestParam(defaultValue = "researching") String timeline, @RequestParam(required = false) String sourcePageHint,
            @RequestParam(defaultValue = "false") boolean quoteMode, Model model) {
        site.calculator(state, projectType, mode, bedrooms, recordsMode, recordSystemType, recordTankCapacity,
                recordDesignFlow, county, recordStatus, serviceNeed, timeline, sourcePageHint, quoteMode, model);
        model.addAttribute("production", false);
        return "studio/calculator";
    }

    @PostMapping("/design-preview/studio/calculator/")
    public String calculate(@ModelAttribute EstimateForm estimateForm,
            @RequestParam(defaultValue = "false") boolean recordsMode,
            @RequestParam(defaultValue = "") String recordSystemType, @RequestParam(defaultValue = "") String recordTankCapacity,
            @RequestParam(defaultValue = "") String recordDesignFlow, Model model) {
        site.calculate(estimateForm, recordsMode, recordSystemType, recordTankCapacity, recordDesignFlow, model);
        model.addAttribute("production", false);
        return "studio/calculator";
    }

    @PostMapping("/design-preview/studio/calculator/quote/")
    public String calculatorQuote(@Valid @ModelAttribute QuoteLeadForm quoteLeadForm,
            BindingResult bindingResult, HttpServletRequest request, Model model) {
        site.submitQuote(quoteLeadForm, bindingResult, request, model);
        model.addAttribute("production", false);
        return "studio/calculator";
    }

    @GetMapping("/design-preview/studio/topics/{topicSlug}/")
    public String topic(@PathVariable String topicSlug, HttpServletRequest request, Model model) {
        // Only migrate audited editorial surfaces, not tools that need their own interaction model.
        if (!java.util.Set.of("septic-permit-process", "septic-inspection-cost").contains(topicSlug)
                && !StudioGuideRoutes.RECORDS.containsKey(topicSlug)
                && !StudioGuideRoutes.PLANNING.containsKey(topicSlug)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        site.contentPage(new org.springframework.web.context.request.ServletWebRequest(request) {
            @Override public String getDescription(boolean includeClientInfo) {
                return "uri=/" + topicSlug + "/";
            }
        }, model);
        model.addAttribute("permitTopic", topicSlug.equals("septic-permit-process"));
        model.addAttribute("recordTopic", StudioGuideRoutes.RECORDS.containsKey(topicSlug));
        model.addAttribute("planningTopic", StudioGuideRoutes.PLANNING.containsKey(topicSlug));
        model.addAttribute("recordHero", StudioGuideRoutes.RECORDS.containsKey(topicSlug)
                ? StudioGuideRoutes.RECORDS.get(topicSlug)
                : StudioGuideRoutes.PLANNING.get(topicSlug));
        model.addAttribute("featuredStudy", StudioProofSelection.select("tennessee", "overton-county"));
        return "studio/topic-guide";
    }

    @GetMapping("/design-preview/studio/buying-guide/{stateSlug}/")
    public String stateBuyingGuide(@PathVariable String stateSlug, HttpServletRequest request, Model model) {
        site.stateMoneyPage(stateSlug, new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override public String getRequestURI() {
                return "/buying-a-house-with-a-septic-system/" + stateSlug + "/";
            }
        }, model);
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        return "studio/state-buyer";
    }

    @GetMapping("/design-preview/studio/buying-guide/")
    public String buyingGuide(HttpServletRequest request, Model model) {
        // Use the existing public article's complete curated model without its presentation.
        site.contentPage(new org.springframework.web.context.request.ServletWebRequest(request) {
            @Override public String getDescription(boolean includeClientInfo) {
                return "uri=/buying-a-house-with-a-septic-system/";
            }
        }, model);
        return "studio/buying-guide";
    }

    @GetMapping("/design-preview/studio/bedroom-check/")
    public String bedroomCheck(Model model) {
        model.addAttribute("guideStates", directory.allStates().stream()
                .map(state -> new GuideState(state.stateCode(), state.stateName(), state.slug(), false)).toList());
        return "studio/bedroom-check";
    }

    @GetMapping("/design-preview/studio/tools/")
    public String tools(Model model) {
        model.addAttribute("guideStates", directory.allStates().stream()
                .map(state -> new GuideState(state.stateCode(), state.stateName(), state.slug(), false)).toList());
        return "studio/tools";
    }

    @GetMapping("/design-preview/studio/request-builder/")
    public String requestBuilder() {
        return "studio/request-builder";
    }

    @GetMapping({"/design-preview/studio/guides/", "/design-preview/studio/states/"})
    public String guides(Model model) {
        model.addAttribute("guideStates", directory.allStates().stream()
                .map(state -> new GuideState(state.stateCode(), state.stateName(), state.slug(),
                        research.findPublicStateBySlug(state.slug()).isPresent()))
                .toList());
        return "studio/guides";
    }

    @GetMapping("/design-preview/studio/{stateSlug}/")
    public String state(@PathVariable String stateSlug, Model model) {
        String sourceView = site.stateGuide(stateSlug, model);
        if (!"pages/state-guide".equals(sourceView)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var state = (com.example.septic.data.model.StateProfile) model.getAttribute("state");
        model.addAttribute("counties", research.listPublicCountyRecordsPages(state.stateCode()));
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, ""));
        model.addAttribute("costHeroImage", costHeroImage(state.stateCode()));
        return "studio/state-guide";
    }

    @GetMapping("/design-preview/studio/service/")
    public String service() {
        return "studio/service";
    }

    @GetMapping("/design-preview/studio/policies/{slug}/")
    public String policy(@PathVariable String slug, Model model) {
        switch (slug) {
            case "privacy-policy" -> site.privacyPolicy(model);
            case "terms-of-use" -> site.termsOfUse(model);
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "studio/policy";
    }

    @GetMapping("/design-preview/studio/methodology/")
    public String methodology(Model model) {
        site.methodology(model);
        return "studio/methodology";
    }

    @GetMapping("/design-preview/studio/work/")
    public String work() {
        return "studio/work";
    }

    @GetMapping("/design-preview/studio/recovery/{kind}/")
    public String recovery(@PathVariable String kind, Model model) {
        if (!java.util.Set.of("not-found", "server").contains(kind)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("recoveryKind", kind);
        return "studio/recovery";
    }

    @GetMapping("/design-preview/studio/checkout/")
    public String checkout() {
        return "studio/checkout";
    }

    @GetMapping("/design-preview/studio/delivery/")
    public String delivery() {
        return "studio/delivery";
    }

    @GetMapping("/design-preview/studio/work/{caseSlug}/")
    public String caseStudy(@PathVariable String caseSlug, Model model) {
        var study = StudioCaseStudies.ALL.stream().filter(item -> item.slug().equals(caseSlug))
                .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("study", study);
        model.addAttribute("related", StudioCaseStudies.ALL);
        return "studio/case-study";
    }

    @GetMapping("/design-preview/studio/intake/")
    public String intake(@RequestParam(required = false) String mode,
                         @RequestParam(required = false) String uploadError,
                         @RequestParam(required = false) String from,
                         @RequestParam(required = false) String caseStudy,
                         @RequestParam(required = false) String state,
                         @RequestParam(required = false) String project,
                         @RequestParam(required = false) String bedrooms, Model model) {
        site.offerPrepSepticFileCheck(mode, uploadError, model);
        model.addAttribute("production", false);
        if ("case".equals(from)) {
            StudioCaseStudies.ALL.stream().filter(s -> s.slug().equals(caseStudy)).findFirst().ifPresent(s -> {
                var form = (ClosingRiskCheckForm) model.getAttribute("closingRiskCheckForm");
                String source = "/design-preview/studio/work/" + s.slug() + "/";
                form.setSourcePageHint(source);
                form.setEntryPageHint(source);
            });
        }
        if ("calculator".equals(from)) {
            var form = (ClosingRiskCheckForm) model.getAttribute("closingRiskCheckForm");
            directory.allStates().stream().filter(s -> s.stateCode().equalsIgnoreCase(state))
                    .findFirst().ifPresent(s -> form.setStateCode(s.stateCode()));
            StudioCalculatorHandoff.prefill(form, project, bedrooms);
        }
        return "studio/intake";
    }

    @PostMapping("/design-preview/studio/intake/")
    public String submitIntake(@Valid @ModelAttribute ClosingRiskCheckForm closingRiskCheckForm,
                              BindingResult bindingResult, @RequestParam(required = false) String intakeMode,
                              HttpServletRequest request, Model model) {
        if ("review".equals(intakeMode)) {
            closingRiskCheckForm.setResearchGoal("understand_file");
            closingRiskCheckForm.setSourceContext("document_review");
        }
        // Share validation, document policy, throttling, private storage and notification handling.
        site.submitClosingRiskCheck(closingRiskCheckForm, bindingResult, request, model);
        model.addAttribute("production", false);
        model.addAttribute("intakeErrors", bindingResult.getAllErrors());
        return "studio/intake";
    }

    @GetMapping("/design-preview/studio/{stateSlug}/{countySlug}/")
    public String county(@PathVariable String stateSlug, @PathVariable String countySlug, Model model) {
        // Reuse the public route's curated data and access overrides, never its presentation.
        site.countyRecordsPage(stateSlug, countySlug, model);
        model.addAttribute("featuredStudy", StudioProofSelection.select(stateSlug, countySlug));
        return "studio/county-guide";
    }

    private static String recordsHeroImage(String stateCode) {
        if (java.util.Set.of("AK", "CO", "ID", "ME", "MT", "NC", "NH", "OR", "PA", "TN", "UT", "VA", "VT", "WA", "WV", "WY")
                .contains(stateCode)) {
            return "/images/studio/property-hero-v1.webp";
        }
        if (java.util.Set.of("AL", "AR", "FL", "GA", "KS", "KY", "LA", "MS", "MO", "NE", "OK", "SC", "SD", "TX")
                .contains(stateCode)) {
            return "/images/studio/oak-pasture-v1.webp";
        }
        return "/images/studio/county-property-v1.webp";
    }

    private static String costHeroImage(String stateCode) {
        if (java.util.Set.of("AK", "CO", "ID", "ME", "MT", "NH", "OR", "UT", "VT", "WA", "WV", "WY")
                .contains(stateCode)) {
            return "/images/studio/property-hero-v1.webp";
        }
        if (java.util.Set.of("AL", "AR", "FL", "GA", "LA", "MS", "NC", "OK", "SC", "TN", "TX")
                .contains(stateCode)) {
            return "/images/studio/oak-pasture-v1.webp";
        }
        return "/images/studio/calculator-landscape-v2.webp";
    }
}
