package com.example.septic.web;

import java.util.List;

public record StateWorkflowDecisionView(
        String eyebrow,
        String heading,
        String intro,
        List<CountyWorkflowFieldView> cards
) {
}
