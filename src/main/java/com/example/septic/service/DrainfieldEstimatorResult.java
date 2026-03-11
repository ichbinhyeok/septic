package com.example.septic.service;

import java.util.List;

public record DrainfieldEstimatorResult(
        EstimatorResult estimate,
        String fieldOutlookLabel,
        String redesignRiskLabel,
        String fieldRiskNote,
        String replacementAreaNote,
        String costSwingNote,
        List<String> decisionSignals,
        List<String> nextSteps
) {
}
