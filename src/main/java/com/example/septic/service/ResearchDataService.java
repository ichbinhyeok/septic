package com.example.septic.service;

import com.example.septic.config.AppDataProperties;
import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.ContentPagesDocument;
import com.example.septic.data.model.CostProfilesDocument;
import com.example.septic.data.model.ProjectCostAnchor;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateCostProfile;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateMoneyPagesDocument;
import com.example.septic.data.model.StateProfile;
import com.example.septic.data.model.StateProfilesDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
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
public class ResearchDataService {
    private final AppDataProperties dataProperties;
    private final ObjectMapper objectMapper;
    private final CsvMapper csvMapper;

    private List<StateProfile> stateProfiles = List.of();
    private Map<String, StateProfile> statesByCode = Map.of();
    private Map<String, StateProfile> statesBySlug = Map.of();
    private Map<String, SourceRecord> sourcesById = Map.of();
    private Map<String, ProjectCostAnchor> anchorsByProjectType = Map.of();
    private Map<String, StateCostProfile> costProfilesByStateCode = Map.of();
    private Map<String, ContentPage> contentPagesBySlug = Map.of();
    private Map<String, StateMoneyPage> stateMoneyPagesByKey = Map.of();

    public ResearchDataService(AppDataProperties dataProperties) {
        this.dataProperties = dataProperties;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
        this.csvMapper = CsvMapper.builder().findAndAddModules().build();
    }

    @PostConstruct
    void load() {
        Path root = Path.of(dataProperties.root());
        try {
            StateProfilesDocument stateDocument = objectMapper.readValue(
                    root.resolve("state_profiles.json").toFile(),
                    StateProfilesDocument.class
            );
            CostProfilesDocument costDocument = objectMapper.readValue(
                    root.resolve("cost_profiles.json").toFile(),
                    CostProfilesDocument.class
            );
            ContentPagesDocument contentPagesDocument = objectMapper.readValue(
                    root.resolve("content_pages.json").toFile(),
                    ContentPagesDocument.class
            );
            StateMoneyPagesDocument stateMoneyPagesDocument = objectMapper.readValue(
                    root.resolve("state_money_pages.json").toFile(),
                    StateMoneyPagesDocument.class
            );

            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            try (Reader reader = Files.newBufferedReader(root.resolve("source_registry.csv"))) {
                List<SourceRecord> sources = csvMapper.readerFor(SourceRecord.class)
                        .with(schema)
                        .<SourceRecord>readValues(reader)
                        .readAll();
                this.sourcesById = sources.stream()
                        .collect(Collectors.toMap(SourceRecord::sourceId, Function.identity(), (left, right) -> left, LinkedHashMap::new));
            }

            this.stateProfiles = stateDocument.states().stream()
                    .sorted(Comparator.comparing(StateProfile::stateName))
                    .toList();
            this.statesByCode = this.stateProfiles.stream()
                    .collect(Collectors.toMap(StateProfile::stateCode, Function.identity(), (left, right) -> left, LinkedHashMap::new));
            this.statesBySlug = this.stateProfiles.stream()
                    .collect(Collectors.toMap(StateProfile::slug, Function.identity(), (left, right) -> left, LinkedHashMap::new));
            this.anchorsByProjectType = costDocument.nationalAnchors().stream()
                    .collect(Collectors.toMap(ProjectCostAnchor::projectType, Function.identity(), (left, right) -> left, LinkedHashMap::new));
            this.costProfilesByStateCode = costDocument.states().stream()
                    .collect(Collectors.toMap(StateCostProfile::stateCode, Function.identity(), (left, right) -> left, LinkedHashMap::new));
            this.contentPagesBySlug = contentPagesDocument.pages().stream()
                    .collect(Collectors.toMap(ContentPage::slug, Function.identity(), (left, right) -> left, LinkedHashMap::new));
            this.stateMoneyPagesByKey = stateMoneyPagesDocument.pages().stream()
                    .collect(Collectors.toMap(StateMoneyPage::key, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load research data from " + root, exception);
        }
    }

    public List<StateProfile> getStateProfiles() {
        return stateProfiles;
    }

    public List<ContentPage> getContentPages() {
        return contentPagesBySlug.values().stream()
                .sorted(Comparator.comparing(ContentPage::title))
                .toList();
    }

    public List<StateMoneyPage> getStateMoneyPages() {
        return stateMoneyPagesByKey.values().stream()
                .sorted(Comparator.comparing(StateMoneyPage::title))
                .toList();
    }

    public Optional<StateProfile> findStateByCode(String stateCode) {
        if (stateCode == null || stateCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(statesByCode.get(stateCode.toUpperCase(Locale.US)));
    }

    public Optional<StateProfile> findStateBySlug(String stateSlug) {
        if (stateSlug == null || stateSlug.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(statesBySlug.get(stateSlug.toLowerCase(Locale.US)));
    }

    public List<SourceRecord> getSources(List<String> sourceIds) {
        return sourceIds.stream()
                .map(sourcesById::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public Optional<ProjectCostAnchor> findNationalAnchor(String projectType) {
        return Optional.ofNullable(anchorsByProjectType.get(projectType));
    }

    public Optional<StateCostProfile> findStateCostProfile(String stateCode) {
        if (stateCode == null || stateCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(costProfilesByStateCode.get(stateCode.toUpperCase(Locale.US)));
    }

    public Optional<ContentPage> findContentPage(String slug) {
        return Optional.ofNullable(contentPagesBySlug.get(slug));
    }

    public Optional<StateMoneyPage> findStateMoneyPage(String contentSlug, String stateSlug) {
        Optional<StateProfile> state = findStateBySlug(stateSlug);
        if (state.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(stateMoneyPagesByKey.get(contentSlug + "::" + state.get().stateCode()));
    }

    public boolean hasStateMoneyPage(String contentSlug, String stateCode) {
        return stateMoneyPagesByKey.containsKey(contentSlug + "::" + stateCode);
    }

    public List<StateMoneyPage> listStateMoneyPages(String stateCode) {
        return stateMoneyPagesByKey.values().stream()
                .filter(page -> page.stateCode().equalsIgnoreCase(stateCode))
                .sorted(Comparator.comparing(StateMoneyPage::title))
                .toList();
    }

    public List<StateMoneyPage> listStateMoneyPagesForContent(String contentSlug) {
        return stateMoneyPagesByKey.values().stream()
                .filter(page -> page.contentSlug().equals(contentSlug))
                .sorted(Comparator.comparing(StateMoneyPage::title))
                .toList();
    }
}
