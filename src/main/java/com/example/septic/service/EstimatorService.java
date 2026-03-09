package com.example.septic.service;

import com.example.septic.data.model.BedroomBand;
import com.example.septic.data.model.ProjectCostAnchor;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateProfile;
import com.example.septic.service.ProjectType;
import com.example.septic.service.SoilPercStatus;
import com.example.septic.web.EstimateForm;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class EstimatorService {
    private final ResearchDataService researchDataService;

    public EstimatorService(ResearchDataService researchDataService) {
        this.researchDataService = researchDataService;
    }

    public EstimatorResult estimate(EstimateForm form) {
        StateProfile state = researchDataService.findStateByCode(form.getStateCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + form.getStateCode()));

        int bedrooms = Math.max(1, form.getBedrooms() == null ? 3 : form.getBedrooms());
        int likelyMinimum = resolveLikelyMinimumTank(state, bedrooms);
        int rangePadding = 250;
        int loadPadding = 0;

        List<String> drivers = new ArrayList<>();
        List<String> checklist = new ArrayList<>();

        if (form.isGarbageDisposal()) {
            if ("GA".equals(state.stateCode())) {
                loadPadding += Math.max(250, likelyMinimum / 2);
                drivers.add("Georgia's homeowner guide says a garbage disposal requires a septic tank that is 50 percent larger.");
            } else if ("PA".equals(state.stateCode())) {
                loadPadding += 100;
                drivers.add("Pennsylvania DEP says garbage disposal use should be sparse because it places a greater burden on the system.");
            } else {
                loadPadding += 150;
                drivers.add("Garbage disposal use can increase the effective wastewater load.");
            }
        }
        if (form.isAdditionalKitchen()) {
            loadPadding += 250;
            drivers.add("An additional kitchen or ADU can push the recommended tank range upward.");
        }
        if (form.getOccupants() != null && form.getOccupants() > bedrooms * 2) {
            if ("CT".equals(state.stateCode())) {
                loadPadding += 100;
                drivers.add("Connecticut's official design-flow method is bedroom-based, so unusually high occupancy mainly widens the planning range rather than rewriting the base rule.");
            } else {
                loadPadding += 150;
                drivers.add("Higher occupancy than the bedroom count suggests can increase the planning range.");
            }
        }

        SoilPercStatus soilStatus = SoilPercStatus.fromValue(form.getSoilPercStatus());
        int riskScore = 0;
        switch (soilStatus) {
            case FAILED -> {
                riskScore += 3;
                rangePadding += 250;
                drivers.add("A failed perc result can push the project toward a higher-cost alternative system.");
            }
            case POOR_DRAINAGE -> {
                riskScore += 2;
                rangePadding += 250;
                drivers.add("Poor drainage usually increases excavation and system-type risk.");
            }
            case UNKNOWN -> {
                rangePadding += 250;
                drivers.add("Unknown soil and perc conditions widen the estimate because the site is not yet defined.");
            }
            case PASSED -> {
                // no-op
            }
        }

        if (form.isHighWaterTableOrShallowBedrock()) {
            riskScore += 2;
            rangePadding += 250;
            drivers.add("A high water table or shallow bedrock can require a more complex system.");
        }

        AccessDifficulty accessDifficulty = AccessDifficulty.fromValue(form.getAccessDifficulty());
        if (accessDifficulty == AccessDifficulty.MEDIUM) {
            riskScore += 1;
            drivers.add("Medium access can add hauling and excavation time.");
        } else if (accessDifficulty == AccessDifficulty.HARD) {
            riskScore += 1;
            rangePadding += 150;
            drivers.add("Hard access often raises excavation, hauling, and restoration costs.");
        }

        ProjectType projectType = ProjectType.fromValue(form.getProjectType());
        if (projectType == ProjectType.REPLACEMENT || projectType == ProjectType.DRAINFIELD_REPLACEMENT) {
            riskScore += 1;
            drivers.add("Replacement work can uncover field, excavation, or restoration complexity.");
        }

        if ("OR".equals(state.stateCode())) {
            rangePadding += 250;
            drivers.add("Oregon puts site evaluation before permit certainty, and DEQ says the site evaluation does not guarantee approval of a specific system type.");
        }

        if ("CT".equals(state.stateCode()) && state.designFlowPerBedroomGpd() != null) {
            drivers.add("Connecticut's official residential design flow uses " + state.designFlowPerBedroomGpd() + " gallons per bedroom.");
        }

        if ("PA".equals(state.stateCode())) {
            checklist.add("Identify the municipality or local agency and Sewage Enforcement Officer before trusting the next-step permit path.");
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
        ProjectCostAnchor anchor = researchDataService.findNationalAnchor(projectType.value())
                .orElseThrow(() -> new IllegalStateException("Missing national cost anchor for " + projectType.value()));

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

        int totalCostLow = roundTo100(anchor.low() * multiplier);
        int totalCostMid = roundTo100(anchor.mid() * multiplier);
        int totalCostHigh = roundTo100(anchor.high() * (multiplier + 0.08));

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
        List<String> sourceLabels = researchDataService.getSources(state.officialSourceIds()).stream()
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
        if ("CT".equals(state.stateCode()) && state.designFlowPerBedroomGpd() != null) {
            return "Connecticut's official residential design flow uses " + state.designFlowPerBedroomGpd()
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
