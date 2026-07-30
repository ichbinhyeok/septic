package com.example.septic.web;

public record CountyWorkflowFunnelRow(
        String countyKey,
        String countyLabel,
        long startsLastSevenDays,
        long startsLastTwentyEightDays,
        long preparedLastTwentyEightDays,
        long officialOpenedLastTwentyEightDays,
        long outcomesLastTwentyEightDays,
        long documentHandoffsLastTwentyEightDays,
        long documentsReviewedLastTwentyEightDays,
        long propertyFilesReadyLastTwentyEightDays,
        int officialOpenRate,
        int outcomeReturnRate,
        int documentHandoffRate,
        int documentReviewRate,
        int propertyFileReadyRate,
        String evidenceLabel,
        String nextAction
) {
}
