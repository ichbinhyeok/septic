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
}
