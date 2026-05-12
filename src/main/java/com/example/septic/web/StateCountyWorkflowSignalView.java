package com.example.septic.web;

public record StateCountyWorkflowSignalView(
        String label,
        String summary,
        String countyExamples,
        String coverageNote,
        String firstAsk
) {
}
