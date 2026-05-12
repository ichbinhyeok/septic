package com.example.septic.web;

import java.util.List;

public record ContentWorkflowCoverageView(
        String heading,
        String summary,
        List<String> bullets
) {
}
