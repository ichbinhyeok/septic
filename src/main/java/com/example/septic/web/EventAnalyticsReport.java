package com.example.septic.web;

import java.util.List;

public record EventAnalyticsReport(
        String generatedAt,
        long trackedActionsLastSevenDays,
        long trackedActionsLastTwentyEightDays,
        long officialSourceClicksLastSevenDays,
        long artifactActionsLastSevenDays,
        long internalNavigationClicksLastSevenDays,
        boolean storageReadable,
        List<EventAnalyticsRow> officialSourceClicks,
        List<EventAnalyticsRow> artifactActions,
        List<EventAnalyticsRow> internalNavigationClicks
) {
    public boolean hasTrackedActions() {
        return trackedActionsLastTwentyEightDays > 0;
    }
}
