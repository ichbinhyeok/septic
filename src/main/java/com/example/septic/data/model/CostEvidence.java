package com.example.septic.data.model;

import java.util.List;

public record CostEvidence(
        String stateCode,
        String projectType,
        String evidenceType,
        String title,
        Integer low,
        Integer mid,
        Integer high,
        Double multiplier,
        List<String> sourceIds,
        String note,
        String lastVerifiedAt,
        String publishStatus
) {
    public boolean isPublished() {
        return "published".equalsIgnoreCase(publishStatus)
                && title != null && !title.isBlank()
                && sourceIds != null && !sourceIds.isEmpty();
    }
}
