package com.example.septic.web;

import java.util.List;

public record StateCountyWorkflowSynthesisView(
        String eyebrow,
        String heading,
        String intro,
        List<StateCountyWorkflowSignalView> signals,
        List<CountyWorkflowFieldView> structureHighlights,
        List<String> firstArtifacts,
        List<String> countyDropTriggers,
        List<String> holdQuoteChecks,
        String firstArtifactsHeading,
        String countyDropHeading,
        String holdQuoteHeading
) {
}
