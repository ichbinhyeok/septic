package com.example.septic.service;

public record TankSizeEstimatorResult(
        EstimatorResult estimate,
        PumpScheduleResult pumpSchedule,
        String occupancyProfileLabel,
        String occupancyNote
) {
}
