package com.example.septic.web;

import java.util.List;

public record CountyWorkflowStructureView(
        List<CountyWorkflowFieldView> fields,
        String quoteGate
) {
}
