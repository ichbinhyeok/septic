package com.example.septic.web;

import java.util.List;

public record CountySearchResponseView(
        String badgeLabel,
        String priorityLabel,
        String heading,
        String summary,
        List<String> queryExamples,
        List<CountyWorkflowFieldView> dossierRows,
        List<PageLink> actionLinks
) {
}
