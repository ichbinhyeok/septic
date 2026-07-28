package com.example.septic.web;

public record WorkflowStageForm(
        String sourcePage,
        String sourceContext,
        String workflowRunId,
        String countyKey,
        String stage,
        String outcome
) {
}
