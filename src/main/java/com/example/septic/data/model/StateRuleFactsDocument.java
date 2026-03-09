package com.example.septic.data.model;

import java.util.List;

public record StateRuleFactsDocument(
        Integer schemaVersion,
        String generatedAt,
        List<StateRuleFact> facts
) {
}
