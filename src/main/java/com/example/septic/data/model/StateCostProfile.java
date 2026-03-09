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
}
