package com.example.septic.data.model;

import java.util.List;

public record StateQueuePlansDocument(
        Integer schemaVersion,
        String generatedAt,
        List<StateQueuePlan> states
) {
}
