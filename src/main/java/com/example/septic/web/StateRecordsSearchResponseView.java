package com.example.septic.web;

import java.util.List;

public record StateRecordsSearchResponseView(
        String badgeLabel,
        String priorityLabel,
        String heading,
        String summary,
        List<String> queryExamples,
        List<CountyWorkflowFieldView> responseRows,
        List<PageLink> countyLinks,
        List<PageLink> actionLinks
) {
    public List<String> questions() {
        return queryExamples == null ? List.of() : queryExamples.stream()
                .filter(query -> query != null && !query.isBlank())
                .map(query -> query.endsWith("?") ? query : "How do I find " + query + "?")
                .toList();
    }
}
