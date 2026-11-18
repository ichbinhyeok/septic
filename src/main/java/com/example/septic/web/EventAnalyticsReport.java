package com.example.septic.web;

import java.util.List;
import java.util.Map;

public record EventAnalyticsReport(
        String generatedAt,
        long trackedActionsLastSevenDays,
        long trackedActionsLastTwentyEightDays,
        long officialSourceClicksLastSevenDays,
        long artifactActionsLastSevenDays,
        long internalNavigationClicksLastSevenDays,
        boolean storageReadable,
        long workflowTasksLastSevenDays,
        long workflowTasksLastTwentyEightDays,
        List<WorkflowFunnelRow> workflowFunnel,
        List<WorkflowOutcomeRow> workflowOutcomes,
        List<CountyWorkflowFunnelRow> countyWorkflowFunnels,
        List<EventAnalyticsRow> officialSourceClicks,
        List<EventAnalyticsRow> artifactActions,
        List<EventAnalyticsRow> internalNavigationClicks
) {
    public boolean hasTrackedActions() {
        return trackedActionsLastTwentyEightDays > 0;
    }

    public record StoredRecordHelpReport(boolean readable, long requests, Map<String, Long> channels,
                                         List<RequestAttributionRow> rows) {}

    public record RequestAttributionRow(String channel, String entryPage, String sourcePage,
                                        String ctaContext, long requests) {}
}
