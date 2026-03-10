package com.example.septic.data.model;

import java.util.List;

public record StateQueuePlan(
        String stateCode,
        Integer priorityRank,
        String rolloutWave,
        String whyNow,
        String launchAngle,
        String recommendedPath,
        List<String> researchTasks
) {
}
