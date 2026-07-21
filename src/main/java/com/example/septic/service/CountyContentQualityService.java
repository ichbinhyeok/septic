package com.example.septic.service;

import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.data.model.CountyWorkflowStructureData;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.web.CountyEvidenceFactView;
import com.example.septic.web.CountyLocalContentView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class CountyContentQualityService {
    public static final int PRIORITY_PAGE_COUNT = 75;

    private final Set<String> repeatedUnits;
    private final Set<String> priorityPageKeys;

    public CountyContentQualityService(ResearchDataService researchDataService) {
        List<CountyRecordsPage> pages = researchDataService.getPublicCountyRecordsPages();
        Map<String, Integer> unitCounts = new HashMap<>();
        for (CountyRecordsPage page : pages) {
            narrativeUnits(page).stream()
                    .map(value -> normalize(page, value))
                    .filter(value -> !value.isBlank())
                    .forEach(value -> unitCounts.merge(value, 1, Integer::sum));
        }
        repeatedUnits = unitCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());

        Map<String, Integer> repeatedCountsByPage = new HashMap<>();
        for (CountyRecordsPage page : pages) {
            int repeatedCount = (int) narrativeUnits(page).stream()
                    .map(value -> normalize(page, value))
                    .filter(repeatedUnits::contains)
                    .count();
            repeatedCountsByPage.put(page.key(), repeatedCount);
        }
        priorityPageKeys = pages.stream()
                .filter(page -> repeatedCountsByPage.getOrDefault(page.key(), 0) > 0)
                .sorted(Comparator
                        .comparingInt((CountyRecordsPage page) -> repeatedCountsByPage.getOrDefault(page.key(), 0))
                        .reversed()
                        .thenComparing(CountyRecordsPage::key))
                .limit(PRIORITY_PAGE_COUNT)
                .map(CountyRecordsPage::key)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    public CountyLocalContentView build(CountyRecordsPage page, List<SourceRecord> sources) {
        if (!priorityPageKeys.contains(page.key())) {
            return new CountyLocalContentView(
                    false,
                    0,
                    page.introCopy(),
                    page.uniqueAngle(),
                    page.targetReader(),
                    List.of(),
                    safeList(page.decisionSteps()),
                    safeList(page.recordsToRequest()),
                    safeList(page.lowEndBreakers())
            );
        }

        List<CountyEvidenceFactView> facts = evidenceFacts(page, sources);
        Counter replaced = new Counter();
        String introCopy = replaceRepeated(
                page,
                page.introCopy(),
                evidenceIntro(page, facts),
                replaced
        );
        String uniqueAngle = replaceRepeated(
                page,
                page.uniqueAngle(),
                evidenceAngle(page, facts),
                replaced
        );
        String targetReader = replaceRepeated(
                page,
                page.targetReader(),
                evidenceReader(page, facts),
                replaced
        );

        List<String> decisionSteps = replaceRepeatedItems(
                page,
                safeList(page.decisionSteps()),
                replaced,
                (index, original) -> decisionStep(page, facts, index)
        );
        List<String> recordsToRequest = replaceRepeatedItems(
                page,
                safeList(page.recordsToRequest()),
                replaced,
                (index, original) -> recordRequest(page, facts, index)
        );
        List<String> lowEndBreakers = replaceRepeatedItems(
                page,
                safeList(page.lowEndBreakers()),
                replaced,
                (index, original) -> lowEndBreaker(page, facts, index)
        );

        return new CountyLocalContentView(
                true,
                replaced.value,
                introCopy,
                uniqueAngle,
                targetReader,
                facts,
                decisionSteps,
                recordsToRequest,
                lowEndBreakers
        );
    }

    public Set<String> priorityPageKeys() {
        return priorityPageKeys;
    }

    private List<CountyEvidenceFactView> evidenceFacts(CountyRecordsPage page, List<SourceRecord> sources) {
        if (sources == null) {
            return List.of();
        }
        List<SourceRecord> usableSources = sources.stream()
                .filter(source -> hasText(source.notes()))
                .filter(source -> !"dead".equalsIgnoreCase(source.httpCheckStatus()))
                .toList();
        boolean hasCountyLocalSource = usableSources.stream().anyMatch(source -> isCountyLocal(page, source));
        return usableSources.stream()
                .filter(source -> !hasCountyLocalSource || isCountyLocal(page, source))
                .map(source -> new CountyEvidenceFactView(
                        source.agencyName(),
                        source.title(),
                        source.url(),
                        sentence(source.notes()),
                        source.contentVerifiedAt(),
                        isCountyLocal(page, source)
                ))
                .sorted(Comparator
                        .comparing(CountyEvidenceFactView::countyLocal)
                        .reversed()
                        .thenComparing(CountyEvidenceFactView::agencyName)
                        .thenComparing(CountyEvidenceFactView::sourceTitle))
                .filter(distinctBySummary())
                .limit(4)
                .toList();
    }

    private java.util.function.Predicate<CountyEvidenceFactView> distinctBySummary() {
        Set<String> seen = new LinkedHashSet<>();
        return fact -> seen.add(fact.summary().toLowerCase(Locale.US));
    }

    private boolean isCountyLocal(CountyRecordsPage page, SourceRecord source) {
        if (source.countyOrLocal() == null) {
            return false;
        }
        String scope = source.countyOrLocal().trim().toLowerCase(Locale.US);
        return scope.equals(page.countyName().toLowerCase(Locale.US));
    }

    private String evidenceIntro(CountyRecordsPage page, List<CountyEvidenceFactView> facts) {
        CountyEvidenceFactView fact = factAt(facts, 0);
        if (fact == null) {
            return page.countyName() + " records start with " + page.recordsLabel()
                    + " and the office handoff at " + page.officeLabel() + ".";
        }
        return page.countyName() + " has a documented official route through " + fact.agencyName()
                + ". " + fact.summary();
    }

    private String evidenceAngle(CountyRecordsPage page, List<CountyEvidenceFactView> facts) {
        CountyEvidenceFactView fact = factAt(facts, 1);
        if (fact == null) {
            return "The practical distinction for " + page.countyName() + " is the route itself: "
                    + page.recordsLabel() + " is the records handoff and " + firstArtifact(page)
                    + " is the first file item to resolve.";
        }
        return page.countyName() + " has a second documented workflow signal in " + fact.sourceTitle()
                + ". " + fact.summary();
    }

    private String evidenceReader(CountyRecordsPage page, List<CountyEvidenceFactView> facts) {
        CountyEvidenceFactView fact = factFor(facts, 2);
        if (fact != null) {
            return "Use this route for a " + page.countyName() + " parcel when " + fact.agencyName()
                    + " is the documented source for the next records decision. " + fact.summary();
        }
        return "Use this route for a " + page.countyName() + " parcel when you need to connect "
                + firstArtifact(page) + " to " + page.recordsLabel()
                + " before a sale, repair, permit decision, or estimate.";
    }

    private String decisionStep(CountyRecordsPage page, List<CountyEvidenceFactView> facts, int index) {
        CountyEvidenceFactView fact = factFor(facts, index);
        if (fact != null) {
            String action = switch (index % 3) {
                case 0 -> "Start with ";
                case 1 -> "Cross-check ";
                default -> "Resolve the handoff through ";
            };
            return action + fact.sourceTitle() + " from " + fact.agencyName()
                    + ". The verified source note for this route is: " + fact.summary();
        }
        if (index == 1) {
            return "Use " + page.recordsLabel() + " and ask first for " + firstArtifact(page) + ".";
        }
        return page.hasParcelAnchor()
                ? "Carry the identifier from " + page.parcelAnchorLabel() + " into the request before treating a blank result as no record."
                : "Give " + page.officeLabel() + " the address, parcel identifier, owner, or legal description it needs to resolve the file.";
    }

    private String recordRequest(CountyRecordsPage page, List<CountyEvidenceFactView> facts, int index) {
        CountyEvidenceFactView fact = factFor(facts, index);
        if (index == 0) {
            return fact == null
                    ? page.countyName() + " file item: " + firstArtifact(page)
                    : "First " + page.countyName() + " file item to match against " + fact.agencyName()
                            + ": " + firstArtifact(page);
        }
        CountyWorkflowStructureData structure = page.workflowStructure();
        if (index == 1 && structure != null && hasText(structure.transferArtifact())) {
            return fact == null
                    ? page.countyName() + " transfer item: " + plain(structure.transferArtifact())
                    : "Transfer item to verify through " + fact.agencyName() + ": "
                            + plain(structure.transferArtifact());
        }
        if (fact != null) {
            return "The file, form, map, or approval described by " + fact.sourceTitle()
                    + ": " + fact.summary();
        }
        return "A written response from " + page.officeLabel()
                + " that identifies the next file owner when the online route has no match.";
    }

    private String lowEndBreaker(CountyRecordsPage page, List<CountyEvidenceFactView> facts, int index) {
        CountyEvidenceFactView fact = factFor(facts, index);
        if (fact != null) {
            String unresolved = switch (index % 3) {
                case 0 -> "records route";
                case 1 -> "parcel evidence";
                default -> "office handoff";
            };
            return "Do not treat the low end as resolved while the " + unresolved + " in " + fact.sourceTitle()
                    + " is still open. " + fact.summary();
        }
        CountyWorkflowStructureData structure = page.workflowStructure();
        if (structure != null && hasText(structure.quoteGate())) {
            return structure.quoteGate();
        }
        return page.countyName() + " still needs a usable response from " + page.officeLabel()
                + " before the cheapest visible scope is safe to rely on.";
    }

    private List<String> replaceRepeatedItems(
            CountyRecordsPage page,
            List<String> items,
            Counter replaced,
            BiFunction<Integer, String, String> replacement
    ) {
        List<String> result = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            String item = items.get(index);
            if (isRepeated(page, item)) {
                replaced.value++;
                result.add(replacement.apply(index, item));
            } else {
                result.add(item);
            }
        }
        return result;
    }

    private String replaceRepeated(CountyRecordsPage page, String value, String replacement, Counter counter) {
        if (!isRepeated(page, value)) {
            return value;
        }
        counter.value++;
        return replacement;
    }

    private boolean isRepeated(CountyRecordsPage page, String value) {
        return repeatedUnits.contains(normalize(page, value));
    }

    private String firstArtifact(CountyRecordsPage page) {
        if (page.workflowStructure() != null && hasText(page.workflowStructure().firstArtifactToPull())) {
            return plain(page.workflowStructure().firstArtifactToPull());
        }
        return safeList(page.recordsToRequest()).stream().findFirst()
                .map(CountyContentQualityService::plain)
                .orElse("the first permit, approval, or written no-record artifact tied to the parcel");
    }

    private CountyEvidenceFactView factAt(List<CountyEvidenceFactView> facts, int index) {
        return index >= 0 && index < facts.size() ? facts.get(index) : null;
    }

    private CountyEvidenceFactView factFor(List<CountyEvidenceFactView> facts, int index) {
        return facts.isEmpty() ? null : facts.get(Math.floorMod(index, facts.size()));
    }

    private List<String> narrativeUnits(CountyRecordsPage page) {
        return Stream.of(
                        Stream.of(page.introCopy(), page.uniqueAngle(), page.targetReader()),
                        safeList(page.decisionSteps()).stream(),
                        safeList(page.recordsToRequest()).stream(),
                        safeList(page.lowEndBreakers()).stream()
                )
                .flatMap(stream -> stream)
                .filter(CountyContentQualityService::hasText)
                .toList();
    }

    private String normalize(CountyRecordsPage page, String value) {
        if (!hasText(value)) {
            return "";
        }
        String normalized = value.toLowerCase(Locale.US)
                .replace(page.countyName().toLowerCase(Locale.US), " <place> ");
        normalized = normalized.replaceAll(
                "\\b" + Pattern.quote(page.stateCode().toLowerCase(Locale.US)) + "\\b",
                " <state> "
        );
        return normalized.replaceAll("[^a-z0-9<>]+", " ").trim().replaceAll("\\s+", " ");
    }

    private static String sentence(String value) {
        if (!hasText(value)) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.matches(".*[.!?]$") ? trimmed : trimmed + ".";
    }

    private static String plain(String value) {
        if (!hasText(value)) {
            return "";
        }
        return value.trim().replaceFirst("[.!?]+$", "");
    }

    private static List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static final class Counter {
        private int value;
    }
}
