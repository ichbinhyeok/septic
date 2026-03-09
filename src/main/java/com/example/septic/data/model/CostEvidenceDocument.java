package com.example.septic.data.model;

import java.util.List;

public record CostEvidenceDocument(
        Integer schemaVersion,
        String generatedAt,
        List<CostEvidence> evidence
) {
}
