package com.example.septic.web;

import java.util.List;

public record StateCostScopeView(
        String eyebrow,
        String heading,
        String intro,
        List<CountyWorkflowFieldView> cards,
        String scopeWidenersHeading,
        List<String> scopeWideners,
        String readinessHeading,
        List<String> readinessChecks
) {
}
