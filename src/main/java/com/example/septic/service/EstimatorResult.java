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

    public String formattedBaselineLane() {
        return "$" + formatNumber(totalCostLow) + " - $" + formatNumber(totalCostMid);
    }

    public String formattedComplexSiteLane() {
        return "$" + formatNumber(totalCostMid) + " - $" + formatNumber(totalCostHigh);
    }

    public double costRangeMultiple() {
        return totalCostLow <= 0 ? 0 : (double) totalCostHigh / totalCostLow;
    }

    public String pricePrecisionLabel() {
        double multiple = costRangeMultiple();
        if (multiple >= 4.0) {
            return "Low";
        }
        if (multiple >= 2.5) {
            return "Low-medium";
        }
        if (multiple >= 1.75) {
            return "Medium";
        }
        return "Higher";
    }

    public String pricePrecisionNote() {
        return switch (pricePrecisionLabel()) {
            case "Low" -> "The high end is at least 4× the low end. Use scenarios, not a single expected price.";
            case "Low-medium" -> "The range is still broad. A permit file, site facts, and written scope should narrow it.";
            case "Medium" -> "Useful for budgeting, but not tight enough to substitute for a local written scope.";
            default -> "The planning band is relatively tighter, but it is still not a contractor bid.";
        };
    }

    private String formatNumber(int value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}
