package com.example.septic.service;

import com.example.septic.data.model.BedroomBand;
import com.example.septic.data.model.ProjectCostAnchor;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateCostProfile;
import com.example.septic.data.model.StateProfile;
import com.example.septic.service.ProjectType;
import com.example.septic.service.SoilPercStatus;
import com.example.septic.web.EstimateForm;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class EstimatorService {
    private final ResearchDataService researchDataService;
    private final PlanningStateService planningStateService;

    public EstimatorService(ResearchDataService researchDataService, PlanningStateService planningStateService) {
        this.researchDataService = researchDataService;
        this.planningStateService = planningStateService;
    }

    public EstimatorResult estimate(EstimateForm form) {
        StateProfile state = planningStateService.planningStateByCode(form.getStateCode());

        int bedrooms = Math.max(1, form.getBedrooms() == null ? 3 : form.getBedrooms());
        int likelyMinimum = resolveLikelyMinimumTank(state, bedrooms);
        int rangePadding = 250;
        int loadPadding = 0;

        List<String> drivers = new ArrayList<>();
        List<String> checklist = new ArrayList<>();
        List<String> officialBasis = new ArrayList<>();
        List<String> heuristicAdjustments = new ArrayList<>();
        List<String> methodologyLimits = new ArrayList<>();

        officialBasis.add(sizingBasisNote(state, bedrooms));

        if (form.isGarbageDisposal()) {
            if ("GA".equals(state.stateCode())) {
                loadPadding += Math.max(250, likelyMinimum / 2);
                drivers.add("Georgia's homeowner guide says a garbage disposal requires a septic tank that is 50 percent larger.");
                officialBasis.add("Georgia's published homeowner guidance treats a garbage disposal as a larger-tank signal, so this estimate widens the sizing range before cost is calculated.");
            } else if ("PA".equals(state.stateCode())) {
                loadPadding += 100;
                drivers.add("Pennsylvania DEP says garbage disposal use should be sparse because it places a greater burden on the system.");
                officialBasis.add("Pennsylvania DEP warns that garbage disposal use places a greater burden on the system, so the estimate treats that input as a state-backed load signal.");
            } else {
                loadPadding += 150;
                drivers.add("Garbage disposal use can increase the effective wastewater load.");
                heuristicAdjustments.add("Garbage disposal input adds extra wastewater load and a small planning uplift even where the public state source set does not publish a specific rule.");
            }
        }
        if (form.isAdditionalKitchen()) {
            loadPadding += 250;
            drivers.add("An additional kitchen or ADU can push the recommended tank range upward.");
            heuristicAdjustments.add("Additional kitchen or ADU input adds +250 gallons of planning padding and a +8% cost multiplier.");
        }
        if (form.getOccupants() != null && form.getOccupants() > bedrooms * 2) {
            if (state.designFlowPerBedroomGpd() != null && state.bedroomTable().isEmpty()) {
                loadPadding += 100;
                drivers.add(state.stateName() + "'s official design-flow method is bedroom-based, so unusually high occupancy mainly widens the planning range rather than rewriting the base rule.");
                officialBasis.add(state.stateName() + "'s public design-flow method is bedroom-based, so higher occupancy widens the planning band instead of replacing the official bedroom anchor.");
            } else {
                loadPadding += 150;
                drivers.add("Higher occupancy than the bedroom count suggests can increase the planning range.");
                heuristicAdjustments.add("Higher-than-typical occupancy adds planning padding because live load can outrun the simple bedroom count.");
            }
        }

        SoilPercStatus soilStatus = SoilPercStatus.fromValue(form.getSoilPercStatus());
        int riskScore = 0;
        switch (soilStatus) {
            case FAILED -> {
                riskScore += 3;
                rangePadding += 250;
                drivers.add("A failed perc result can push the project toward a higher-cost alternative system.");
                heuristicAdjustments.add("Failed perc input raises system risk and adds +20% to the planning cost model.");
            }
            case POOR_DRAINAGE -> {
                riskScore += 2;
                rangePadding += 250;
                drivers.add("Poor drainage usually increases excavation and system-type risk.");
                heuristicAdjustments.add("Poor drainage raises system risk and adds +10% to the planning cost model.");
            }
            case UNKNOWN -> {
                rangePadding += 250;
                drivers.add("Unknown soil and perc conditions widen the estimate because the site is not yet defined.");
                methodologyLimits.add("Soil and perc conditions are still unknown, so the low end stays intentionally conservative.");
            }
            case PASSED -> {
                // no-op
            }
        }

        if (form.isHighWaterTableOrShallowBedrock()) {
            riskScore += 2;
            rangePadding += 250;
            drivers.add("A high water table or shallow bedrock can require a more complex system.");
            heuristicAdjustments.add("High water table or shallow bedrock adds +12% to the planning cost model and widens the likely system class.");
        }

        AccessDifficulty accessDifficulty = AccessDifficulty.fromValue(form.getAccessDifficulty());
        if (accessDifficulty == AccessDifficulty.MEDIUM) {
            riskScore += 1;
            drivers.add("Medium access can add hauling and excavation time.");
            heuristicAdjustments.add("Medium access adds +8% for hauling and excavation friction.");
        } else if (accessDifficulty == AccessDifficulty.HARD) {
            riskScore += 1;
            rangePadding += 150;
            drivers.add("Hard access often raises excavation, hauling, and restoration costs.");
            heuristicAdjustments.add("Hard access adds +16% and extra range padding for excavation, hauling, and restoration complexity.");
        }

        ProjectType projectType = ProjectType.fromValue(form.getProjectType());
        if (projectType == ProjectType.REPLACEMENT || projectType == ProjectType.DRAINFIELD_REPLACEMENT) {
            riskScore += 1;
            drivers.add("Replacement work can uncover field, excavation, or restoration complexity.");
            if (projectType == ProjectType.REPLACEMENT) {
                heuristicAdjustments.add("Replacement jobs add +12% because demolition, field surprises, and restoration can widen the planning range.");
            }
        }

        if ("OR".equals(state.stateCode())) {
            rangePadding += 250;
            drivers.add("Oregon puts site evaluation before permit certainty, and DEQ says the site evaluation does not guarantee approval of a specific system type.");
            officialBasis.add("Oregon's published homeowner path is site-evaluation-first, so the estimate widens before pretending a specific system type is approved.");
        }

        if (state.designFlowPerBedroomGpd() != null && state.bedroomTable().isEmpty()) {
            drivers.add(state.stateName() + "'s official residential design flow uses " + state.designFlowPerBedroomGpd() + " gallons per bedroom.");
            officialBasis.add(state.stateName() + "'s public sizing basis starts from " + state.designFlowPerBedroomGpd() + " gallons per bedroom before local design review.");
        }

        if ("PA".equals(state.stateCode())) {
            checklist.add("Identify the municipality or local agency and Sewage Enforcement Officer before trusting the next-step permit path.");
        }
        if ("AZ".equals(state.stateCode())) {
            checklist.add("Confirm which Arizona county controls the permit file and whether the site investigation paperwork is already on record.");
        }
        if ("CA".equals(state.stateCode())) {
            checklist.add("Confirm which local agency controls the file and whether the property is in a default Tier 1 path or a LAMP-driven local program.");
        }
        if ("TX".equals(state.stateCode())) {
            checklist.add("Use OARS to confirm the local permitting authority and whether the site evaluation is already on file.");
        }
        if ("NY".equals(state.stateCode())) {
            checklist.add("Ask the county health department for the Appendix 75-A file and any specific waiver before trusting the low end.");
        }
        if ("OH".equals(state.stateCode())) {
            checklist.add("Ask the local health department whether an installation permit, operation permit, or operational-inspection file already exists.");
        }
        if ("MI".equals(state.stateCode())) {
            checklist.add("Confirm which local health department controls the file and whether any failed-system evaluation or location note already exists.");
        }
        if ("OR".equals(state.stateCode())) {
            checklist.add("Confirm whether Oregon site evaluation or an authorization notice applies before trusting the low end of the range.");
        }
        if ("CT".equals(state.stateCode())) {
            checklist.add("Confirm the local director of health or approved agent path before assuming a simple replacement or addition stays compliant.");
        }
        if ("GA".equals(state.stateCode())) {
            checklist.add("Check the county health department process and whether the disposal rule changes the likely tank band for your project.");
        }

        int recommendedLow = roundUpTo250(likelyMinimum + Math.min(loadPadding, 250));
        int recommendedHigh = roundUpTo250(Math.max(recommendedLow + 250, likelyMinimum + loadPadding + rangePadding));

        String likelySystemClass = inferSystemClass(riskScore);
        ProjectCostAnchor nationalAnchor = researchDataService.findNationalAnchor(projectType.value())
                .orElseThrow(() -> new IllegalStateException("Missing national cost anchor for " + projectType.value()));
        StateCostProfile stateCostProfile = researchDataService.findStateCostProfile(state.stateCode()).orElse(null);
        ProjectCostAnchor costAnchor = stateCostProfile == null ? null : stateCostProfile.anchorForProjectType(projectType.value());

        double multiplier = 1.0;
        multiplier += systemClassMultiplier(likelySystemClass);
        multiplier += accessMultiplier(accessDifficulty);
        multiplier += timelineMultiplier(TimelinePreference.fromValue(form.getTimeline()));
        if (form.isAdditionalKitchen()) {
            multiplier += 0.08;
        }
        if (form.isGarbageDisposal()) {
            multiplier += 0.04;
        }
        if (soilStatus == SoilPercStatus.FAILED) {
            multiplier += 0.2;
        } else if (soilStatus == SoilPercStatus.POOR_DRAINAGE) {
            multiplier += 0.1;
        }
        if (form.isHighWaterTableOrShallowBedrock()) {
            multiplier += 0.12;
        }
        if (projectType == ProjectType.REPLACEMENT) {
            multiplier += 0.12;
        }
        boolean usingStateSpecificAnchor = costAnchor != null;
        String costAnchorStatus = stateCostProfile == null ? null : stateCostProfile.status();
        double stateRegionalMultiplier = usingStateSpecificAnchor
                ? 1.0
                : stateCostProfile != null && stateCostProfile.regionalMultiplier() != null
                        ? stateCostProfile.regionalMultiplier()
                        : 1.0;
        if (!usingStateSpecificAnchor && stateRegionalMultiplier >= 1.05) {
            drivers.add(state.stateName() + " runs above the national price level, which can lift a homeowner planning estimate before site-specific adjustments.");
        } else if (!usingStateSpecificAnchor && stateRegionalMultiplier <= 0.95) {
            drivers.add(state.stateName() + " runs below the national price level on a broad regional basis, but site and system complexity can erase that advantage quickly.");
        } else if (usingStateSpecificAnchor && costAnchorStatus != null && costAnchorStatus.startsWith("derived_state_planning_anchor")) {
            drivers.add(0, state.stateName() + " has a derived state planning cost anchor in this estimator, built from national public septic ranges and BEA regional price levels.");
        } else if (usingStateSpecificAnchor) {
            drivers.add(0, state.stateName() + " has a state-level planning cost anchor in this estimator, so the base range is not relying on the national public anchor alone.");
        }
        officialBasis.add(costAnchorNote(state, usingStateSpecificAnchor ? costAnchor : nationalAnchor, stateCostProfile));
        addMultiplierNote(heuristicAdjustments, "Likely system class \"" + likelySystemClass + "\"", systemClassMultiplier(likelySystemClass));
        addMultiplierNote(heuristicAdjustments, "Timeline preference", timelineMultiplier(TimelinePreference.fromValue(form.getTimeline())));

        ProjectCostAnchor baseAnchor = usingStateSpecificAnchor ? costAnchor : nationalAnchor;
        int totalCostLow = roundTo100(baseAnchor.low() * multiplier * stateRegionalMultiplier);
        int totalCostMid = roundTo100(baseAnchor.mid() * multiplier * stateRegionalMultiplier);
        int totalCostHigh = roundTo100(baseAnchor.high() * (multiplier + 0.08) * stateRegionalMultiplier);

        while (drivers.size() < 3) {
            drivers.add(defaultDriverFor(drivers.size()));
        }

        checklist.add("Confirm local permit and site-evaluation steps with the county or local authority.");
        checklist.add("Ask whether the existing field, access path, or restoration work could change the quote.");
        checklist.add("Get at least two local quotes after sharing the site constraints and bedroom count.");
        if (soilStatus == SoilPercStatus.UNKNOWN) {
            checklist.add("Schedule a perc or site evaluation before trusting the lower end of the range.");
        }

        String officialMinimumNote = officialMinimumNote(state);

        String confidenceLabel = confidenceLabel(state, soilStatus);
        String rangeReason = rangeReason(state);
        String costAnchorNote = costAnchorNote(state, baseAnchor, stateCostProfile);
        methodologyLimits.add("This is a planning estimate, not a permit-ready design, final permit fee schedule, or contractor quote.");
        methodologyLimits.add(state.localOverrideNote());
        methodologyLimits.add("Final pricing still depends on the local permit path, the real system type, and what the site evaluation or inspection uncovers.");
        if (heuristicAdjustments.isEmpty()) {
            heuristicAdjustments.add("No major heuristic uplifts were applied beyond the baseline state profile and the selected job type.");
        }
        List<String> sourceLabels = Stream.concat(
                        researchDataService.getSources(state.officialSourceIds()).stream(),
                        researchDataService.getSources(baseAnchor.sourceIds()).stream()
                )
                .distinct()
                .map(this::formatSourceLabel)
                .toList();

        return new EstimatorResult(
                state.stateCode(),
                state.stateName(),
                state.agencyName(),
                projectType.label(),
                likelyMinimum,
                recommendedLow,
                recommendedHigh,
                likelySystemClass,
                totalCostLow,
                totalCostMid,
                totalCostHigh,
                confidenceLabel,
                rangeReason,
                officialMinimumNote,
                state.localOverrideNote(),
                state.lastVerifiedAt(),
                costAnchorNote,
                officialBasis.stream().distinct().limit(4).toList(),
                heuristicAdjustments.stream().distinct().limit(5).toList(),
                methodologyLimits.stream().filter(value -> value != null && !value.isBlank()).distinct().limit(4).toList(),
                drivers.stream().distinct().limit(4).toList(),
                checklist.stream().distinct().limit(4).toList(),
                state.ruleHighlights().stream().limit(4).toList(),
                state.permitPathSteps().stream().limit(4).toList(),
                sourceLabels
        );
    }

    private int resolveLikelyMinimumTank(StateProfile state, int bedrooms) {
        int fromTable = state.bedroomTable().stream()
                .filter(band -> band.matches(bedrooms))
                .max(Comparator.comparing(BedroomBand::minTankGallons))
                .map(BedroomBand::minTankGallons)
                .orElse(0);

        if (fromTable > 0) {
            return Math.max(fromTable, fallbackMinimumTank(bedrooms, state.minTankSizeGallons()));
        }

        if (state.designFlowPerBedroomGpd() != null) {
            int estimatedDailyFlow = bedrooms * state.designFlowPerBedroomGpd();
            return flowToTankHeuristic(estimatedDailyFlow, state.minTankSizeGallons());
        }

        return fallbackMinimumTank(bedrooms, state.minTankSizeGallons());
    }

    private int fallbackMinimumTank(int bedrooms, Integer stateMinimum) {
        int heuristic;
        if (bedrooms <= 3) {
            heuristic = 1000;
        } else if (bedrooms == 4) {
            heuristic = 1250;
        } else if (bedrooms == 5) {
            heuristic = 1500;
        } else {
            heuristic = 1750;
        }
        if (stateMinimum == null) {
            return heuristic;
        }
        return Math.max(heuristic, stateMinimum);
    }

    private String inferSystemClass(int riskScore) {
        if (riskScore >= 8) {
            return "Site-specific design required";
        }
        if (riskScore >= 5) {
            return "Alternative system likely";
        }
        if (riskScore >= 2) {
            return "Conventional or chamber likely";
        }
        return "Conventional likely";
    }

    private double systemClassMultiplier(String likelySystemClass) {
        return switch (likelySystemClass) {
            case "Conventional likely" -> 0.0;
            case "Conventional or chamber likely" -> 0.12;
            case "Alternative system likely" -> 0.45;
            case "Site-specific design required" -> 0.85;
            default -> 0.0;
        };
    }

    private double accessMultiplier(AccessDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 0.0;
            case MEDIUM -> 0.08;
            case HARD -> 0.16;
        };
    }

    private double timelineMultiplier(TimelinePreference timeline) {
        return switch (timeline) {
            case THIS_MONTH -> 0.06;
            case ONE_TO_THREE_MONTHS -> 0.03;
            case RESEARCHING -> 0.0;
        };
    }

    private String confidenceLabel(StateProfile state, SoilPercStatus soilStatus) {
        double score = state.confidenceScore() == null ? 0.35 : state.confidenceScore();
        if (soilStatus == SoilPercStatus.UNKNOWN) {
            score -= 0.1;
        }
        if ("OR".equals(state.stateCode())) {
            score -= 0.05;
        }
        if (score >= 0.7) {
            return "Medium-high";
        }
        if (score >= 0.5) {
            return "Medium";
        }
        return "Low";
    }

    private String defaultDriverFor(int index) {
        return switch (index) {
            case 0 -> "Permit, site-evaluation, and inspection fees can vary by county.";
            case 1 -> "Excavation access and restoration work can move the final quote up or down.";
            default -> "The final system type often matters more than the tank alone.";
        };
    }

    private String formatSourceLabel(SourceRecord sourceRecord) {
        return sourceRecord.agencyName() + " - " + sourceRecord.title();
    }

    private String costAnchorNote(StateProfile state, ProjectCostAnchor baseAnchor, StateCostProfile stateCostProfile) {
        if (stateCostProfile == null || baseAnchor == null) {
            return "This cost band is using national public cost anchors with site-specific multipliers layered on top.";
        }
        String status = stateCostProfile.status();
        if (status != null && status.startsWith("derived_state_planning_anchor")) {
            return state.stateName() + " is using a derived planning anchor built from national public septic cost ranges and BEA regional price levels. It is state-aware, but it is not an official state fee schedule or contractor quote feed.";
        }
        if (status != null && status.startsWith("regional_multiplier_from_bea_rpp")) {
            return state.stateName() + " is using national public septic cost anchors with a broad BEA regional price-level adjustment. Treat it as a state-aware planning range, not a local market quote.";
        }
        return state.stateName() + " is using a state-level planning anchor in this estimator. Final pricing still depends on the site, the permit path, and the system type.";
    }

    private void addMultiplierNote(List<String> notes, String label, double multiplier) {
        if (multiplier <= 0.0) {
            return;
        }
        notes.add(label + " adds +" + Math.round(multiplier * 100) + "% to the planning cost model.");
    }

    private String sizingBasisNote(StateProfile state, int bedrooms) {
        if (!state.bedroomTable().isEmpty()) {
            return state.stateName() + " has a publishable bedroom-to-tank signal in the public source set, so the starting minimum is anchored before site-specific widening.";
        }
        if (state.designFlowPerBedroomGpd() != null) {
            return state.stateName() + " uses a design-flow basis of " + state.designFlowPerBedroomGpd()
                    + " gallons per bedroom, and this planner bridges that into a conservative tank band for " + bedrooms + " bedrooms.";
        }
        if (state.minTankSizeGallons() != null) {
            return state.stateName() + " publishes a minimum tank-size anchor, so the planner starts there and widens only when the job inputs justify it.";
        }
        return state.stateName() + " does not publish a clean statewide homeowner tank table in the current source set, so the planner starts from a conservative fallback minimum.";
    }

    private int flowToTankHeuristic(int estimatedDailyFlow, Integer stateMinimum) {
        int heuristic;
        if (estimatedDailyFlow <= 600) {
            heuristic = 1000;
        } else if (estimatedDailyFlow <= 750) {
            heuristic = 1250;
        } else if (estimatedDailyFlow <= 900) {
            heuristic = 1500;
        } else {
            heuristic = 1750;
        }

        if (stateMinimum == null) {
            return heuristic;
        }
        return Math.max(heuristic, stateMinimum);
    }

    private String officialMinimumNote(StateProfile state) {
        if (state.designFlowPerBedroomGpd() != null && state.bedroomTable().isEmpty()) {
            return state.stateName() + "'s official residential design flow uses " + state.designFlowPerBedroomGpd()
                    + " gallons per bedroom. The gallon-size recommendation shown here is a product planning bridge, not an official tank table.";
        }
        if ("OR".equals(state.stateCode())) {
            return "Oregon's official value is the site-evaluation-first permit path, not a simple statewide homeowner tank table. This result stays intentionally conservative for that reason.";
        }
        if (state.minTankSizeGallons() == null) {
            return "This state profile is still using a conservative planning range because the publishable minimum has not been fully verified.";
        }
        return "Official state-facing guidance suggests a minimum around " + formatGallons(state.minTankSizeGallons()) + " gallons before site-specific adjustments.";
    }

    private String rangeReason(StateProfile state) {
        if ("AZ".equals(state.stateCode())) {
            return "This Arizona range stays wide because county delegation and site-investigation paperwork often decide whether the project stays on a straightforward permit path.";
        }
        if ("CA".equals(state.stateCode())) {
            return "This California range stays wide because local agency routing and LAMP versus Tier 1 differences matter more than a single statewide homeowner tank table.";
        }
        if ("TX".equals(state.stateCode())) {
            return "This Texas range stays wide because the local permitting authority and the site evaluation often decide the real system path.";
        }
        if ("NY".equals(state.stateCode())) {
            return "This New York range bridges Appendix 75-A design-flow rules into a homeowner planning estimate. County health files and any waiver history can still move the job.";
        }
        if ("OH".equals(state.stateCode())) {
            return "This Ohio range stays wide because the local health department permit file and any operational-inspection history often decide the real path.";
        }
        if ("MI".equals(state.stateCode())) {
            return "This Michigan range stays wide because local health department files, system-location uncertainty, and community-specific rules can move the job fast.";
        }
        if ("OR".equals(state.stateCode())) {
            return "This Oregon range is intentionally wide because DEQ says site evaluation does not guarantee approval of any specific system type.";
        }
        if ("CT".equals(state.stateCode())) {
            return "This Connecticut range bridges official bedroom-based design flow into a homeowner planning estimate. Final tank and system decisions still need local review.";
        }
        return "This is a planning range, not an engineered design. Site evaluation, local permitting, and system type can move the final quote.";
    }

    private int roundUpTo250(int value) {
        return ((value + 249) / 250) * 250;
    }

    private int roundTo100(double value) {
        return (int) (Math.round(value / 100.0) * 100);
    }

    private String formatGallons(int gallons) {
        return NumberFormat.getNumberInstance(Locale.US).format(gallons);
    }
}
