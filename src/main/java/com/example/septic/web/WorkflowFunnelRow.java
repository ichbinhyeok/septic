package com.example.septic.web;

public record WorkflowFunnelRow(
        String stage,
        String label,
        long lastSevenDays,
        long lastTwentyEightDays,
        int sevenDayRate
) {
}
