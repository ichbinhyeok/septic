package com.example.septic.service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public record EstimatorResult(
        String stateCode,
        String stateName,
        String agencyName,
        String projectTypeLabel,
        int likelyMinimumTankGallons,
        int recommendedTankLowGallons,
        int recommendedTankHighGallons,
        String likelySystemClass,
        int totalCostLow,
        int totalCostMid,
        int totalCostHigh,
        String confidenceLabel,
        String rangeReason,
        String officialMinimumNote,
        String localOverrideNote,
        String lastVerifiedAt,
        String costAnchorNote,
        List<String> officialBasis,
        List<String> heuristicAdjustments,
        List<String> methodologyLimits,
        List<String> costDrivers,
        List<String> checklist,
        List<String> ruleHighlights,
        List<String> permitPathSteps,
        List<String> sourceLabels
) {
    public String formattedLikelyMinimumTank() {
        return formatNumber(likelyMinimumTankGallons) + " gal";
    }

    public String formattedRecommendedTankRange() {
        return formatNumber(recommendedTankLowGallons) + "-" + formatNumber(recommendedTankHighGallons) + " gal";
    }

    public String formattedTotalCostRange() {
        return "$" + formatNumber(totalCostLow) + " - $" + formatNumber(totalCostHigh);
    }

    public String formattedTotalCostMid() {
        return "$" + formatNumber(totalCostMid);
    }

    private String formatNumber(int value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}
