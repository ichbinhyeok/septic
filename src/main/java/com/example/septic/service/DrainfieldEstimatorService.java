package com.example.septic.service;

import com.example.septic.web.DrainfieldEstimatorForm;
import com.example.septic.web.EstimateForm;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DrainfieldEstimatorService {
    private final EstimatorService estimatorService;

    public DrainfieldEstimatorService(EstimatorService estimatorService) {
        this.estimatorService = estimatorService;
    }

    public DrainfieldEstimatorResult estimate(DrainfieldEstimatorForm form) {
        EstimateForm estimateForm = new EstimateForm();
        int bedrooms = form.getBedrooms() == null || form.getBedrooms() < 1 ? 3 : form.getBedrooms();
        estimateForm.setStateCode(form.getStateCode());
        estimateForm.setProjectType(ProjectType.DRAINFIELD_REPLACEMENT.value());
        estimateForm.setBedrooms(bedrooms);
        estimateForm.setOccupants(Math.max(2, bedrooms * 2));
        estimateForm.setSoilPercStatus(form.getSoilPercStatus());
        estimateForm.setHighWaterTableOrShallowBedrock(form.isWetGroundOrSurfacing());
        estimateForm.setAccessDifficulty(form.getAccessDifficulty());
        estimateForm.setTimeline(form.getTimeline());
        SoilPercStatus soilPercStatus = SoilPercStatus.fromValue(form.getSoilPercStatus());
        AccessDifficulty accessDifficulty = AccessDifficulty.fromValue(form.getAccessDifficulty());

        int redesignRisk = 0;
        if (soilPercStatus == SoilPercStatus.UNKNOWN) {
            redesignRisk += 1;
        } else if (soilPercStatus == SoilPercStatus.POOR_DRAINAGE) {
            redesignRisk += 2;
        } else if (soilPercStatus == SoilPercStatus.FAILED) {
            redesignRisk += 3;
        }
        if (form.isWetGroundOrSurfacing()) {
            redesignRisk += 2;
        }
        if (form.isNoClearReplacementArea()) {
            redesignRisk += 3;
        }
        if (accessDifficulty == AccessDifficulty.HARD) {
            redesignRisk += 1;
        }

        EstimatorResult estimate = adjustedEstimate(
                estimatorService.estimate(estimateForm),
                redesignRisk,
                form.isNoClearReplacementArea()
        );

        String fieldOutlookLabel;
        if (redesignRisk >= 6) {
            fieldOutlookLabel = "Alternative field layout or site-specific redesign likely";
        } else if (redesignRisk >= 3) {
            fieldOutlookLabel = "Conventional replacement may still be possible, but redesign risk is material";
        } else {
            fieldOutlookLabel = "Conventional field replacement may still be plausible";
        }

        String redesignRiskLabel;
        if (form.isNoClearReplacementArea()) {
            redesignRiskLabel = "Reserve-area or layout risk is the main blocker";
        } else if (form.isWetGroundOrSurfacing()) {
            redesignRiskLabel = "Field saturation or high-water-table risk is visible";
        } else if (soilPercStatus == SoilPercStatus.FAILED) {
            redesignRiskLabel = "Failed soil or perc signal is driving redesign risk";
        } else if (soilPercStatus == SoilPercStatus.UNKNOWN) {
            redesignRiskLabel = "Site definition is still too thin for a clean field-only quote";
        } else {
            redesignRiskLabel = "The lot may still support a straightforward field path";
        }

        String fieldRiskNote;
        if (form.isWetGroundOrSurfacing() && form.isNoClearReplacementArea()) {
            fieldRiskNote = "Visible wetness plus no clear replacement area is a strong sign the job may widen beyond trench work and into layout or system-class redesign.";
        } else if (form.isWetGroundOrSurfacing()) {
            fieldRiskNote = "Wet ground, surfacing effluent, or persistent odors are strong signals that the field story may be bigger than a simple line-item repair.";
        } else if (soilPercStatus == SoilPercStatus.FAILED) {
            fieldRiskNote = "A failed perc or site signal often means the quote should be framed as a field viability problem first, not a routine like-for-like replacement.";
        } else {
            fieldRiskNote = "Drain field jobs look cheapest only when the replacement area, soil story, and field layout still support a conventional path.";
        }

        String replacementAreaNote = form.isNoClearReplacementArea()
                ? "No clear replacement area is one of the fastest ways a drain field quote stops looking conventional."
                : "If the lot still has a credible replacement area, the lower end remains more believable than if the field has to be relocated or redesigned.";

        String costSwingNote = "This estimator assumes the field is the main cost driver. "
                + "The range usually moves hardest when the replacement area, soil result, or water conditions stop supporting a straightforward conventional field.";

        List<String> decisionSignals = new ArrayList<>();
        decisionSignals.add(switch (soilPercStatus) {
            case PASSED -> "The current soil signal is not the main problem, so layout, access, and replacement-area questions matter more now.";
            case POOR_DRAINAGE -> "Poor drainage suspicion is already enough to widen the drain field path before a contractor promises the low end.";
            case FAILED -> "A failed perc or site signal is one of the clearest ways a field-only quote turns into redesign risk.";
            case UNKNOWN -> "Unknown soil status keeps the low end weak because the field may still fail the next site check.";
        });
        if (form.isWetGroundOrSurfacing()) {
            decisionSignals.add("Wet spots, surfacing, or odor signals raise the chance that the active field condition is worse than the visible trench footprint alone.");
        } else {
            decisionSignals.add("No visible wetness keeps the job closer to a planning exercise, but it does not prove the old field layout is still usable.");
        }
        if (form.isNoClearReplacementArea()) {
            decisionSignals.add("No clear replacement area pushes the job toward reserve-area, redesign, or alternative-system questions quickly.");
        } else {
            decisionSignals.add("A credible replacement area keeps a conventional field story more believable than a land-constrained parcel.");
        }
        if (accessDifficulty == AccessDifficulty.HARD) {
            decisionSignals.add("Hard access can turn a field job into a restoration and hauling problem even before the redesign question is settled.");
        }

        List<String> nextSteps = new ArrayList<>();
        nextSteps.add("Pull the permit, as-built, and any old field-layout or repair record before you trust a field-only quote.");
        nextSteps.add("Ask whether the parcel still has a clear replacement area or reserve area that matches the current bedroom count and use.");
        if (soilPercStatus == SoilPercStatus.UNKNOWN) {
            nextSteps.add("Schedule the site evaluation or perc step before you compare field-only versus full-system quotes.");
        } else {
            nextSteps.add("Use the current soil or site finding to separate a conventional field replacement from a redesign path.");
        }
        nextSteps.add("Compare the drain field estimate against the state guide and the full cost estimator before you move into quote mode.");

        return new DrainfieldEstimatorResult(
                estimate,
                fieldOutlookLabel,
                redesignRiskLabel,
                fieldRiskNote,
                replacementAreaNote,
                costSwingNote,
                decisionSignals,
                nextSteps
        );
    }

    private EstimatorResult adjustedEstimate(EstimatorResult estimate, int redesignRisk, boolean noClearReplacementArea) {
        if (!noClearReplacementArea) {
            return estimate;
        }

        double multiplier = redesignRisk >= 6 ? 1.18 : 1.10;
        String likelySystemClass = estimate.likelySystemClass();
        if (redesignRisk >= 6 && "Conventional likely".equals(likelySystemClass)) {
            likelySystemClass = "Alternative system likely";
        } else if (redesignRisk >= 6 && "Conventional or chamber likely".equals(likelySystemClass)) {
            likelySystemClass = "Alternative system likely";
        } else if (redesignRisk >= 3 && "Conventional likely".equals(likelySystemClass)) {
            likelySystemClass = "Conventional or chamber likely";
        }

        List<String> costDrivers = new ArrayList<>();
        costDrivers.add("No clear replacement area can force field relocation, redesign, or a wider system path before a contractor can honor the low end.");
        costDrivers.addAll(estimate.costDrivers());

        List<String> checklist = new ArrayList<>();
        checklist.add("Ask whether the parcel still has a reserve area or any viable replacement layout on record before you compare field-only quotes.");
        checklist.addAll(estimate.checklist());

        return new EstimatorResult(
                estimate.stateCode(),
                estimate.stateName(),
                estimate.agencyName(),
                estimate.projectTypeLabel(),
                estimate.likelyMinimumTankGallons(),
                estimate.recommendedTankLowGallons(),
                estimate.recommendedTankHighGallons(),
                likelySystemClass,
                roundTo100(estimate.totalCostLow() * multiplier),
                roundTo100(estimate.totalCostMid() * multiplier),
                roundTo100(estimate.totalCostHigh() * multiplier),
                estimate.confidenceLabel(),
                estimate.rangeReason(),
                estimate.officialMinimumNote(),
                estimate.localOverrideNote(),
                estimate.lastVerifiedAt(),
                estimate.costAnchorNote(),
                estimate.officialBasis(),
                estimate.heuristicAdjustments(),
                estimate.methodologyLimits(),
                costDrivers.stream().distinct().limit(4).toList(),
                checklist.stream().distinct().limit(4).toList(),
                estimate.ruleHighlights(),
                estimate.permitPathSteps(),
                estimate.sourceLabels()
        );
    }

    private int roundTo100(double value) {
        return (int) (Math.round(value / 100.0) * 100);
    }
}
