package com.example.septic.service;

import com.example.septic.config.AppDataProperties;
import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.ContentPagesDocument;
import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.data.model.CountyRecordsPagesDocument;
import com.example.septic.data.model.CostEvidence;
import com.example.septic.data.model.CostEvidenceDocument;
import com.example.septic.data.model.CostProfilesDocument;
import com.example.septic.data.model.ProjectCostAnchor;
import com.example.septic.data.model.SearchResponseTarget;
import com.example.septic.data.model.SearchResponseTargetsDocument;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateCostProfile;
import com.example.septic.data.model.StateRuleFact;
import com.example.septic.data.model.StateRuleFactsDocument;
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
    private Map<String, CountyRecordsPage> countyRecordsPagesByKey = Map.of();
    private Map<String, SearchResponseTarget> searchResponseTargetsByLookupKey = Map.of();
    private List<SearchResponseTarget> searchResponseTargets = List.of();
    private Map<String, List<StateRuleFact>> stateRuleFactsByStateCode = Map.of();
    private List<CostEvidence> costEvidence = List.of();
    private String stateProfilesGeneratedAt = "";
    private String contentPagesGeneratedAt = "";
    private String stateMoneyPagesGeneratedAt = "";
    private String countyRecordsPagesGeneratedAt = "";
    private String searchResponseTargetsGeneratedAt = "";
    private String stateRuleFactsGeneratedAt = "";
    private String costEvidenceGeneratedAt = "";

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
            CostEvidenceDocument costEvidenceDocument = objectMapper.readValue(
                    root.resolve("cost_evidence.json").toFile(),
                    CostEvidenceDocument.class
            );
            StateMoneyPagesDocument stateMoneyPagesDocument = objectMapper.readValue(
                    root.resolve("state_money_pages.json").toFile(),
                    StateMoneyPagesDocument.class
            );
            CountyRecordsPagesDocument countyRecordsPagesDocument = objectMapper.readValue(
                    root.resolve("county_records_pages.json").toFile(),
                    CountyRecordsPagesDocument.class
            );
            SearchResponseTargetsDocument searchResponseTargetsDocument = readSearchResponseTargets(root);
            StateRuleFactsDocument stateRuleFactsDocument = objectMapper.readValue(
                    root.resolve("state_rule_facts.json").toFile(),
                    StateRuleFactsDocument.class
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
            this.stateProfilesGeneratedAt = stateDocument.generatedAt();
            this.contentPagesGeneratedAt = contentPagesDocument.generatedAt();
            this.stateMoneyPagesGeneratedAt = stateMoneyPagesDocument.generatedAt();
            this.countyRecordsPagesGeneratedAt = countyRecordsPagesDocument.generatedAt();
            this.searchResponseTargetsGeneratedAt = firstNonBlank(searchResponseTargetsDocument.generatedAt(), "");
            this.stateRuleFactsGeneratedAt = stateRuleFactsDocument.generatedAt();
            this.costEvidenceGeneratedAt = costEvidenceDocument.generatedAt();
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
            this.countyRecordsPagesByKey = countyRecordsPagesDocument.pages().stream()
                    .collect(Collectors.toMap(CountyRecordsPage::key, Function.identity(), (left, right) -> left, LinkedHashMap::new));
            this.searchResponseTargets = safeList(searchResponseTargetsDocument.targets()).stream()
                    .filter(target -> hasText(target.targetType()) && hasText(target.key()))
                    .toList();
            this.searchResponseTargetsByLookupKey = this.searchResponseTargets.stream()
                    .collect(Collectors.toMap(SearchResponseTarget::lookupKey, Function.identity(), (left, right) -> left, LinkedHashMap::new));
            this.costEvidence = costEvidenceDocument.evidence().stream()
                    .filter(CostEvidence::isPublished)
                    .toList();
            this.stateRuleFactsByStateCode = stateRuleFactsDocument.facts().stream()
                    .collect(Collectors.groupingBy(
                            fact -> fact.stateCode().toUpperCase(Locale.US),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load research data from " + root, exception);
        }
    }

    private SearchResponseTargetsDocument readSearchResponseTargets(Path root) throws IOException {
        Path path = root.resolve("search_response_targets.json");
        if (Files.notExists(path)) {
            return new SearchResponseTargetsDocument("", "missing", List.of());
        }
        return objectMapper.readValue(path.toFile(), SearchResponseTargetsDocument.class);
    }

    public List<StateProfile> getStateProfiles() {
        return stateProfiles;
    }

    public List<StateProfile> getPublicStateProfiles() {
        return stateProfiles.stream()
                .filter(StateProfile::isPublished)
                .toList();
    }

    public List<ContentPage> getContentPages() {
        return contentPagesBySlug.values().stream()
                .sorted(Comparator.comparing(ContentPage::title))
                .toList();
    }

    public List<ContentPage> getPublicContentPages() {
        return getContentPages().stream()
                .filter(ContentPage::isPublished)
                .toList();
    }

    public List<StateMoneyPage> getStateMoneyPages() {
        return stateMoneyPagesByKey.values().stream()
                .sorted(Comparator.comparing(StateMoneyPage::title))
                .toList();
    }

    public List<StateMoneyPage> getPublicStateMoneyPages() {
        return getStateMoneyPages().stream()
                .filter(StateMoneyPage::isPublished)
                .filter(page -> findStateByCode(page.stateCode()).map(StateProfile::isPublished).orElse(false))
                .toList();
    }

    public List<CountyRecordsPage> getCountyRecordsPages() {
        return countyRecordsPagesByKey.values().stream()
                .sorted(Comparator
                        .comparing(CountyRecordsPage::stateCode)
                        .thenComparing(CountyRecordsPage::countyName))
                .toList();
    }

    public List<CountyRecordsPage> getPublicCountyRecordsPages() {
        return getCountyRecordsPages().stream()
                .filter(CountyRecordsPage::isPublished)
                .filter(page -> findStateByCode(page.stateCode()).map(StateProfile::isPublished).orElse(false))
                .toList();
    }

    public List<SearchResponseTarget> getSearchResponseTargets() {
        return searchResponseTargets;
    }

    public List<SearchResponseTarget> listSearchResponseTargets(String targetType) {
        if (!hasText(targetType)) {
            return List.of();
        }
        return searchResponseTargets.stream()
                .filter(target -> target.targetType().equalsIgnoreCase(targetType))
                .toList();
    }

    public Optional<SearchResponseTarget> findSearchResponseTarget(String targetType, String key) {
        if (!hasText(targetType) || !hasText(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(searchResponseTargetsByLookupKey.get(SearchResponseTarget.lookupKey(targetType, key)));
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

    public Optional<StateProfile> findPublicStateBySlug(String stateSlug) {
        return findStateBySlug(stateSlug).filter(StateProfile::isPublished);
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

    public Optional<ContentPage> findPublicContentPage(String slug) {
        return findContentPage(slug).filter(ContentPage::isPublished);
    }

    public Optional<StateMoneyPage> findStateMoneyPage(String contentSlug, String stateSlug) {
        Optional<StateProfile> state = findStateBySlug(stateSlug);
        if (state.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(stateMoneyPagesByKey.get(contentSlug + "::" + state.get().stateCode()));
    }

    public Optional<StateMoneyPage> findPublicStateMoneyPage(String contentSlug, String stateSlug) {
        Optional<StateProfile> state = findPublicStateBySlug(stateSlug);
        if (state.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(stateMoneyPagesByKey.get(contentSlug + "::" + state.get().stateCode()))
                .filter(StateMoneyPage::isPublished);
    }

    public Optional<CountyRecordsPage> findCountyRecordsPage(String stateSlug, String countySlug) {
        Optional<StateProfile> state = findStateBySlug(stateSlug);
        if (state.isEmpty() || countySlug == null || countySlug.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(countyRecordsPagesByKey.get(state.get().stateCode() + "::" + countySlug.toLowerCase(Locale.US)));
    }

    public Optional<CountyRecordsPage> findPublicCountyRecordsPage(String stateSlug, String countySlug) {
        return findCountyRecordsPage(stateSlug, countySlug)
                .filter(CountyRecordsPage::isPublished)
                .filter(page -> findStateByCode(page.stateCode()).map(StateProfile::isPublished).orElse(false));
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

    public List<StateMoneyPage> listPublicStateMoneyPages(String stateCode) {
        if (findStateByCode(stateCode).filter(StateProfile::isPublished).isEmpty()) {
            return List.of();
        }
        return listStateMoneyPages(stateCode).stream()
                .filter(StateMoneyPage::isPublished)
                .toList();
    }

    public List<CountyRecordsPage> listCountyRecordsPages(String stateCode) {
        return countyRecordsPagesByKey.values().stream()
                .filter(page -> page.stateCode().equalsIgnoreCase(stateCode))
                .sorted(Comparator.comparing(CountyRecordsPage::countyName))
                .toList();
    }

    public List<CountyRecordsPage> listPublicCountyRecordsPages(String stateCode) {
        if (findStateByCode(stateCode).filter(StateProfile::isPublished).isEmpty()) {
            return List.of();
        }
        return listCountyRecordsPages(stateCode).stream()
                .filter(CountyRecordsPage::isPublished)
                .toList();
    }

    public List<StateMoneyPage> listStateMoneyPagesForContent(String contentSlug) {
        return stateMoneyPagesByKey.values().stream()
                .filter(page -> page.contentSlug().equals(contentSlug))
                .sorted(Comparator.comparing(StateMoneyPage::title))
                .toList();
    }

    public List<StateMoneyPage> listPublicStateMoneyPagesForContent(String contentSlug) {
        return listStateMoneyPagesForContent(contentSlug).stream()
                .filter(StateMoneyPage::isPublished)
                .filter(page -> findStateByCode(page.stateCode()).map(StateProfile::isPublished).orElse(false))
                .toList();
    }

    public java.util.Optional<SourceRecord> findSource(String sourceId) {
        if (sourceId == null || sourceId.isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.ofNullable(sourcesById.get(sourceId));
    }

    public List<StateRuleFact> listStateRuleFacts(String stateCode) {
        if (stateCode == null || stateCode.isBlank()) {
            return List.of();
        }
        return stateRuleFactsByStateCode.getOrDefault(stateCode.toUpperCase(Locale.US), List.of());
    }

    public List<StateRuleFact> listPublicStateRuleFacts(String stateCode) {
        return findStateByCode(stateCode)
                .filter(StateProfile::isPublished)
                .map(state -> listStateRuleFacts(state.stateCode()))
                .orElse(List.of());
    }

    public List<CostEvidence> listCostEvidence(String stateCode, String projectType) {
        return costEvidence.stream()
                .filter(item -> {
                    if ("US".equalsIgnoreCase(item.stateCode())) {
                        return projectType != null && projectType.equalsIgnoreCase(item.projectType());
                    }
                    return stateCode != null
                            && stateCode.equalsIgnoreCase(item.stateCode())
                            && ("state_price_level".equalsIgnoreCase(item.evidenceType())
                            || projectType != null && projectType.equalsIgnoreCase(item.projectType()));
                })
                .toList();
    }

    public String stateProfilesGeneratedAt() {
        return stateProfilesGeneratedAt;
    }

    public String contentPagesGeneratedAt() {
        return contentPagesGeneratedAt;
    }

    public String stateMoneyPagesGeneratedAt() {
        return stateMoneyPagesGeneratedAt;
    }

    public String countyRecordsPagesGeneratedAt() {
        return countyRecordsPagesGeneratedAt;
    }

    public String searchResponseTargetsGeneratedAt() {
        return searchResponseTargetsGeneratedAt;
    }

    public String stateRuleFactsGeneratedAt() {
        return stateRuleFactsGeneratedAt;
    }

    public String costEvidenceGeneratedAt() {
        return costEvidenceGeneratedAt;
    }

    private static <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String firstNonBlank(String first, String fallback) {
        return hasText(first) ? first : fallback;
    }
}
