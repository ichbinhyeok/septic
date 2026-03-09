package com.example.septic.web;

import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateProfile;
import com.example.septic.service.AccessDifficulty;
import com.example.septic.service.EstimatorResult;
import com.example.septic.service.EstimatorService;
import com.example.septic.service.ProjectType;
import com.example.septic.service.ResearchDataService;
import com.example.septic.service.SoilPercStatus;
import com.example.septic.service.TimelinePreference;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SiteController {
    private final ResearchDataService researchDataService;
    private final EstimatorService estimatorService;

    public SiteController(ResearchDataService researchDataService, EstimatorService estimatorService) {
        this.researchDataService = researchDataService;
        this.estimatorService = estimatorService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", new PageMeta(
                "Septic System Cost & Size Estimator",
                "State-aware septic planning estimates for tank size, system type, and rough cost."
        ));
        model.addAttribute("states", researchDataService.getStateProfiles());
        return "pages/home";
    }

    @GetMapping({"/septic-system-cost-calculator", "/septic-system-cost-calculator/"})
    public String calculator(Model model) {
        return renderCalculator(model, new EstimateForm(), null);
    }

    @PostMapping({"/septic-system-cost-calculator", "/septic-system-cost-calculator/"})
    public String calculate(@ModelAttribute EstimateForm estimateForm, Model model) {
        EstimatorResult result = estimatorService.estimate(estimateForm);
        return renderCalculator(model, estimateForm, result);
    }

    @GetMapping({"/septic-system-cost-calculator/{stateSlug}", "/septic-system-cost-calculator/{stateSlug}/"})
    public String stateGuide(@PathVariable String stateSlug, Model model) {
        StateProfile state = researchDataService.findStateBySlug(stateSlug)
                .orElseThrow(() -> new StateNotFoundException(stateSlug));
        List<SourceRecord> sources = researchDataService.getSources(state.officialSourceIds());

        model.addAttribute("page", new PageMeta(
                state.stateName() + " Septic Cost & Size Guide",
                "Planning estimates, permit context, and official sources for " + state.stateName() + " homeowners."
        ));
        model.addAttribute("state", state);
        model.addAttribute("sources", sources);
        return "pages/state-guide";
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

    private String renderCalculator(Model model, EstimateForm estimateForm, EstimatorResult result) {
        model.addAttribute("page", new PageMeta(
                "Septic System Cost Calculator",
                "Estimate likely tank size, system class, and septic project cost range by state."
        ));
        model.addAttribute("states", researchDataService.getStateProfiles());
        model.addAttribute("estimateForm", estimateForm);
        model.addAttribute("result", result);
        return "pages/calculator";
    }
}
