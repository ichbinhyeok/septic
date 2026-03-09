package com.example.septic.data.model;

import java.util.List;

public record CostProfilesDocument(
        Integer schemaVersion,
        String generatedAt,
        List<String> notes,
        List<String> nationalCostAnchorSources,
        List<String> projectTypes,
        List<ProjectCostAnchor> nationalAnchors,
        List<StateCostProfile> states
) {
}
