package com.example.septic.service;

import com.example.septic.config.AppDataProperties;
import com.example.septic.data.model.StateQueuePlan;
import com.example.septic.data.model.StateQueuePlansDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StateQueuePlanService {
    private final AppDataProperties dataProperties;
    private final ObjectMapper objectMapper;

    private List<StateQueuePlan> plans = List.of();
    private Map<String, StateQueuePlan> plansByStateCode = Map.of();

    public StateQueuePlanService(AppDataProperties dataProperties) {
        this.dataProperties = dataProperties;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @PostConstruct
    void load() {
        Path root = Path.of(dataProperties.root());
        try {
            StateQueuePlansDocument document = objectMapper.readValue(
                    root.resolve("state_queue_plans.json").toFile(),
                    StateQueuePlansDocument.class
            );
            this.plans = document.states().stream()
                    .sorted(Comparator.comparing(StateQueuePlan::priorityRank))
                    .toList();
            this.plansByStateCode = this.plans.stream()
                    .collect(Collectors.toMap(
                            plan -> plan.stateCode().toUpperCase(Locale.US),
                            Function.identity(),
                            (left, right) -> left,
                            LinkedHashMap::new
                    ));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load state queue plans from " + root, exception);
        }
    }

    public List<StateQueuePlan> topPlans(int limit) {
        return plans.stream()
                .limit(Math.max(limit, 0))
                .toList();
    }

    public Optional<StateQueuePlan> findByStateCode(String stateCode) {
        if (stateCode == null || stateCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(plansByStateCode.get(stateCode.toUpperCase(Locale.US)));
    }
}
