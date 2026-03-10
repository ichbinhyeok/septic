package com.example.septic.web;

import java.util.List;

public record StateQueuePlanView(
        String stateCode,
        String stateName,
        int priorityRank,
        String rolloutWave,
        String whyNow,
        String launchAngle,
        String statePath,
        PageLink recommendedLink,
        List<String> researchTasks
) {
}
