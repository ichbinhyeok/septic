package com.example.septic.data.model;

import java.util.List;

public record StateCostProfile(
        String stateCode,
        String status,
        Integer installLow,
        Integer installMid,
        Integer installHigh,
        Integer replacementLow,
        Integer replacementMid,
        Integer replacementHigh,
        Integer drainfieldLow,
        Integer drainfieldHigh,
        Integer percLow,
        Integer percHigh,
        Integer pumpingLow,
        Integer pumpingHigh,
        Integer inspectionLow,
        Integer inspectionHigh,
        Double regionalMultiplier,
        List<String> sourceIds
) {
    public ProjectCostAnchor anchorForProjectType(String projectType) {
        return switch (projectType) {
            case "new_install" -> anchor(projectType, installLow, installMid, installHigh);
            case "replacement" -> anchor(projectType, replacementLow, replacementMid, replacementHigh);
            case "drainfield_replacement" -> anchor(projectType, drainfieldLow, midpoint(drainfieldLow, drainfieldHigh), drainfieldHigh);
            case "perc_test" -> anchor(projectType, percLow, midpoint(percLow, percHigh), percHigh);
            case "pumping" -> anchor(projectType, pumpingLow, midpoint(pumpingLow, pumpingHigh), pumpingHigh);
            case "inspection" -> anchor(projectType, inspectionLow, midpoint(inspectionLow, inspectionHigh), inspectionHigh);
            default -> null;
        };
    }

    private ProjectCostAnchor anchor(String projectType, Integer low, Integer mid, Integer high) {
        if (low == null || mid == null || high == null) {
            return null;
        }
        return new ProjectCostAnchor(projectType, low, mid, high, status, sourceIds);
    }

    private Integer midpoint(Integer low, Integer high) {
        if (low == null || high == null) {
            return null;
        }
        return (int) Math.round((low + high) / 2.0);
    }
}
