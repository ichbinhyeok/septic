package com.example.septic.web;

import java.util.List;

public record SepticDocumentAnalysisResult(
        String status,
        String heading,
        String summary,
        String purpose,
        String fileName,
        DocumentRecordOutcome recordOutcome,
        DocumentDecision decision,
        List<DocumentFinding> findings,
        List<String> missingItems,
        List<String> nextSteps
) {
}
