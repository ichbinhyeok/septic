package com.example.septic.web;

import java.util.List;

public record CountyLocalContentView(
        boolean priorityPage,
        int replacedRepeatedUnitCount,
        String introCopy,
        String uniqueAngle,
        String targetReader,
        List<CountyEvidenceFactView> evidenceFacts,
        List<String> decisionSteps,
        List<String> recordsToRequest,
        List<String> lowEndBreakers
) {
    public CountyLocalContentView {
        evidenceFacts = evidenceFacts == null ? List.of() : List.copyOf(evidenceFacts);
        decisionSteps = decisionSteps == null ? List.of() : List.copyOf(decisionSteps);
        recordsToRequest = recordsToRequest == null ? List.of() : List.copyOf(recordsToRequest);
        lowEndBreakers = lowEndBreakers == null ? List.of() : List.copyOf(lowEndBreakers);
    }

    public boolean hasEvidenceFacts() {
        return !evidenceFacts.isEmpty();
    }

    public long countyLocalFactCount() {
        return evidenceFacts.stream().filter(CountyEvidenceFactView::countyLocal).count();
    }
}
