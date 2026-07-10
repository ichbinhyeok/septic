package com.example.septic.web;

public record EventAnalyticsRow(
        String sourcePage,
        String sourceContext,
        String detail,
        long lastSevenDays,
        long lastTwentyEightDays,
        String lastSeenAt
) {
}
