package com.example.septic.service;

import java.util.List;

public record PumpScheduleResult(
        int tankSizeGallons,
        int occupants,
        String usageProfileLabel,
        String suggestedInspectionCadence,
        String roughPumpingCadence,
        String maintenanceBudgetReminder,
        String confidenceNote,
        List<String> drivers,
        List<String> sourceLabels
) {
}
