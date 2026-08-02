package com.example.septic.web;

public record WorkflowOutcomeRow(
        String outcome,
        String label,
        long lastSevenDays,
        long lastTwentyEightDays
) {
}
