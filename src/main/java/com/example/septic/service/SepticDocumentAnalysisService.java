package com.example.septic.service;

import com.example.septic.web.DocumentFinding;
import com.example.septic.web.DocumentDecision;
import com.example.septic.web.SepticDocumentAnalysisResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SepticDocumentAnalysisService {
    public static final long MAX_FILE_BYTES = 10L * 1024L * 1024L;
    private static final int MAX_PDF_PAGES = 100;
    private static final int MAX_EXTRACTED_CHARACTERS = 250_000;
    private static final Semaphore ANALYSIS_SLOTS = new Semaphore(4);

    private static final Pattern PERMIT_NUMBER = Pattern.compile(
            "(?i)\\b(?:permit|application|record)\\s*(?:number|no\\.?|#|id)\\s*[:#-]?\\s*([A-Z0-9][A-Z0-9-]{3,24})");
    private static final Pattern BEDROOMS = Pattern.compile(
            "(?i)\\b(?:approved|permitted|designed|maximum|max\\.?)\\s*(?:for|at|to|:)?\\s*(\\d{1,2})\\s*(?:bedroom|bedrooms|br)\\b");
    private static final Pattern BEDROOMS_FIELD = Pattern.compile(
            "(?i)\\b(?:approved\\s+)?(?:number\\s+of\\s+)?bedrooms?\\s*(?:designed\\s+for)?\\s*[:#-]\\s*(\\d{1,2})\\b");
    private static final Pattern TANK_GALLONS_BEFORE = Pattern.compile(
            "(?i)\\b(\\d{3,5})\\s*(?:gallon|gallons|gal\\.?)\\s+(?:septic\\s+)?tank\\b");
    private static final Pattern TANK_GALLONS_AFTER = Pattern.compile(
            "(?i)\\b(?:septic\\s+)?tank\\s*[:#-]?\\s*(?:capacity|size|volume)?\\s*[:#-]?\\s*"
                    + "(\\d{3,5})\\s*(?:gallon|gallons|gal\\.?)\\b");
    private static final Pattern DESIGN_FLOW = Pattern.compile(
            "(?i)\\b(?:approved|design(?:ed)?|daily|maximum|max\\.?)?\\s*(?:wastewater\\s+)?"
                    + "(?:design\\s+)?flow\\s*(?:of|is|:|#|-)?\\s*(\\d{2,5})\\s*(?:gpd|gallons?\\s+per\\s+day)\\b");
    private static final Pattern OPERATIONAL_CAPACITY = Pattern.compile(
            "(?i)\\b(?:operational\\s+capacity|max(?:imum)?\\s+daily\\s+flow)\\s*[:#-]?\\s*"
                    + "(\\d{2,5})\\s*(?:gpd|gals?\\.?/day|gallons?\\s+per\\s+day)\\b");
    private static final Pattern SYSTEM_TYPE = Pattern.compile(
            "(?i)\\b(?:system|treatment|disposal)\\s+type\\s*[:#-]\\s*"
                    + "(aerobic(?:\\s+treatment\\s+unit)?|mound|drip(?:\\s+dispersal)?|"
                    + "low[- ]pressure(?:\\s+pipe)?|conventional(?:\\s+gravity)?|gravity)\\b");
    private static final Pattern SYSTEM_TYPE_NARRATIVE = Pattern.compile(
            "(?i)\\b(?:existing|installed|approved|proposed)\\s+"
                    + "(aerobic(?:\\s+treatment\\s+unit)?|mound|drip(?:\\s+dispersal)?|"
                    + "low[- ]pressure(?:\\s+pipe)?|conventional(?:\\s+gravity)?|gravity)\\s+system\\b");
    private static final Pattern DATE = Pattern.compile(
            "(?i)\\b(?:approved|approval|final\\s+(?:approval|inspection)|inspection)\\s*(?:date)?\\s*[:#-]?\\s*"
                    + "((?:0?[1-9]|1[0-2])[/-](?:0?[1-9]|[12]\\d|3[01])[/-](?:19|20)\\d{2}|"
                    + "(?:19|20)\\d{2}-\\d{2}-\\d{2})");

    private final DocumentOcrService documentOcrService;

    public SepticDocumentAnalysisService(DocumentOcrService documentOcrService) {
        this.documentOcrService = documentOcrService;
    }

    public SepticDocumentAnalysisResult analyze(
            MultipartFile file,
            String purpose,
            String stateCode,
            String countyName
    ) throws IOException {
        validate(file);
        if (!ANALYSIS_SLOTS.tryAcquire()) {
            throw new RejectedExecutionException("Document analysis is temporarily busy.");
        }
        try {
            return analyzeDocument(file, purpose, stateCode, countyName);
        } finally {
            ANALYSIS_SLOTS.release();
        }
    }

    private SepticDocumentAnalysisResult analyzeDocument(
            MultipartFile file,
            String purpose,
            String stateCode,
            String countyName
    ) throws IOException {
        ExtractedDocument extracted = extractText(file);
        String text = extracted.text();
        String normalizedPurpose = normalizePurpose(purpose);
        String fileName = safeFileName(file.getOriginalFilename());

        if (text.isBlank() || text.replaceAll("\\s+", "").length() < 30) {
            return new SepticDocumentAnalysisResult(
                    "no_text",
                    "This file does not contain readable text",
                    extracted.message(),
                    normalizedPurpose,
                    fileName,
                    unreadableDecision(normalizedPurpose),
                    List.of(),
                    purposeMissingItems(normalizedPurpose),
                    List.of(
                            "Try a clearer scan or a searchable PDF exported from the official site.",
                            "If OCR remains unreadable, keep the document and use the local office route to confirm the key fields.",
                            "Do not rely on an unreadable scan for bedroom, approval, or repair decisions."
                    )
            );
        }

        List<DocumentFinding> findings = attachPageNumbers(
                extractFindings(text, extracted.ocrUsed()),
                extracted.pages()
        );
        List<String> missingItems = missingItems(normalizedPurpose, findings);
        List<String> nextSteps = nextSteps(
                normalizedPurpose, findings, missingItems, stateCode, countyName, extracted.ocrUsed()
        );
        DocumentDecision decision = decisionFor(
                normalizedPurpose, findings, missingItems, extracted.ocrUsed()
        );
        String readingMethod = extracted.ocrUsed()
                ? "OCR read typed text from this scan. "
                : "";
        String summary = findings.isEmpty()
                ? readingMethod + "Readable text was found, but no common septic permit fields could be identified automatically."
                : readingMethod + "We found " + findings.size() + " useful field"
                        + (findings.size() == 1 ? "" : "s")
                        + ". Confirm them against the original document before acting.";

        return new SepticDocumentAnalysisResult(
                findings.isEmpty() ? "needs_review" : "analyzed",
                headingFor(normalizedPurpose),
                summary,
                normalizedPurpose,
                fileName,
                decision,
                findings,
                missingItems,
                nextSteps
        );
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Choose a PDF or text file.");
        }
        if (file.getSize() > MAX_FILE_BYTES) {
            throw new IllegalArgumentException("The file must be 10 MB or smaller.");
        }
        String name = safeFileName(file.getOriginalFilename()).toLowerCase(Locale.US);
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.US);
        boolean pdf = name.endsWith(".pdf") || "application/pdf".equals(contentType);
        boolean text = name.endsWith(".txt") || contentType.startsWith("text/");
        if (!pdf && !text) {
            throw new IllegalArgumentException("Use a searchable PDF or plain-text file.");
        }
    }

    private ExtractedDocument extractText(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String name = safeFileName(file.getOriginalFilename()).toLowerCase(Locale.US);
        if (name.endsWith(".pdf") || "application/pdf".equalsIgnoreCase(file.getContentType())) {
            try (PDDocument document = Loader.loadPDF(bytes)) {
                if (document.isEncrypted()) {
                    throw new IllegalArgumentException("Remove the PDF password before uploading.");
                }
                if (document.getNumberOfPages() > MAX_PDF_PAGES) {
                    throw new IllegalArgumentException("The PDF must contain 100 pages or fewer.");
                }
                List<String> searchablePages = new ArrayList<>();
                int extractedCharacters = 0;
                for (int pageNumber = 1; pageNumber <= document.getNumberOfPages()
                        && extractedCharacters < MAX_EXTRACTED_CHARACTERS; pageNumber++) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setSortByPosition(true);
                    stripper.setStartPage(pageNumber);
                    stripper.setEndPage(pageNumber);
                    String pageText = stripper.getText(document);
                    int remaining = MAX_EXTRACTED_CHARACTERS - extractedCharacters;
                    String boundedPageText = pageText.length() > remaining
                            ? pageText.substring(0, remaining)
                            : pageText;
                    searchablePages.add(boundedPageText);
                    extractedCharacters += boundedPageText.length();
                }
                String searchableText = String.join("\n", searchablePages);
                if (hasReadableText(searchableText)) {
                    return new ExtractedDocument(
                            searchableText,
                            false,
                            "Searchable PDF text was read in memory.",
                            searchablePages
                    );
                }
                DocumentOcrService.OcrResult ocrResult = documentOcrService.read(document);
                return new ExtractedDocument(limitText(ocrResult.text()), true, ocrResult.message(), List.of());
            }
        }
        return new ExtractedDocument(
                limitText(new String(bytes, StandardCharsets.UTF_8)),
                false,
                "Plain text was read in memory.",
                List.of()
        );
    }

    private List<DocumentFinding> attachPageNumbers(
            List<DocumentFinding> findings,
            List<String> pages
    ) {
        if (pages == null || pages.isEmpty()) {
            return findings;
        }
        List<String> normalizedPages = pages.stream()
                .map(this::normalizeEvidenceText)
                .toList();
        return findings.stream()
                .map(finding -> new DocumentFinding(
                        finding.key(),
                        finding.label(),
                        finding.value(),
                        finding.confidence(),
                        finding.evidence(),
                        locateEvidencePage(finding.evidence(), finding.value(), normalizedPages)
                ))
                .toList();
    }

    private Integer locateEvidencePage(String evidence, String value, List<String> normalizedPages) {
        String normalizedEvidence = normalizeEvidenceText(evidence);
        if (!normalizedEvidence.isBlank()) {
            for (int index = 0; index < normalizedPages.size(); index++) {
                if (normalizedPages.get(index).contains(normalizedEvidence)) {
                    return index + 1;
                }
            }
        }
        String normalizedValue = normalizeEvidenceText(value);
        if (normalizedValue.length() >= 4) {
            for (int index = 0; index < normalizedPages.size(); index++) {
                if (normalizedPages.get(index).contains(normalizedValue)) {
                    return index + 1;
                }
            }
        }
        Set<String> evidenceTokens = Pattern.compile("[^a-z0-9-]+")
                .splitAsStream(normalizedEvidence)
                .filter(token -> token.length() >= 4)
                .collect(Collectors.toSet());
        int bestPage = -1;
        int bestScore = 0;
        for (int index = 0; index < normalizedPages.size(); index++) {
            Set<String> pageTokens = Pattern.compile("[^a-z0-9-]+")
                    .splitAsStream(normalizedPages.get(index))
                    .collect(Collectors.toSet());
            int score = evidenceTokens.stream()
                    .filter(pageTokens::contains)
                    .mapToInt(String::length)
                    .sum();
            if (score > bestScore) {
                bestScore = score;
                bestPage = index;
            }
        }
        if (bestScore >= 12) {
            return bestPage + 1;
        }
        return null;
    }

    private String normalizeEvidenceText(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim().toLowerCase(Locale.US);
    }

    private List<DocumentFinding> extractFindings(String text, boolean ocrUsed) {
        Map<String, DocumentFinding> findings = new LinkedHashMap<>();
        addPatternFinding(findings, text, "permit_number", "Permit or record number", PERMIT_NUMBER, "High");
        addPatternFinding(findings, text, "approved_bedrooms", "Approved bedrooms", BEDROOMS, "Medium");
        addPatternFinding(findings, text, "approved_bedrooms", "Approved bedrooms", BEDROOMS_FIELD, "Medium");
        addPatternFinding(findings, text, "tank_capacity", "Tank capacity", TANK_GALLONS_BEFORE, "Medium", " gallons");
        addPatternFinding(findings, text, "tank_capacity", "Tank capacity", TANK_GALLONS_AFTER, "Medium", " gallons");
        addPatternFinding(findings, text, "design_flow", "Design flow", DESIGN_FLOW, "Medium", " GPD");
        addPatternFinding(findings, text, "design_flow", "Design flow", OPERATIONAL_CAPACITY, "Medium", " GPD");
        addPatternFinding(findings, text, "approval_date", "Approval or inspection date", DATE, "Medium");
        DocumentFinding permitFinding = findings.get("permit_number");
        if (permitFinding != null && isPermitStopWord(permitFinding.value())) {
            findings.remove("permit_number");
        }

        String lower = text.toLowerCase(Locale.US);
        addSystemTypeFinding(findings, text);
        if (findings.containsKey("approval_date")) {
            DocumentFinding approvalDate = findings.get("approval_date");
            findings.put("final_approval", new DocumentFinding(
                    "final_approval", "Final approval", "Dated",
                    "Medium", approvalDate.evidence()
            ));
        }
        addContextFinding(findings, lower, text, "layout", "Layout or as-built", List.of(
                "attached as-built shows", "as-built shows", "site plan shows",
                "record drawing shows", "layout drawing shows"
        ));
        addContextFinding(findings, lower, text, "repair_history", "Repair history", List.of(
                "repair history attached", "repair records attached", "previous repair dated",
                "repair permit number", "repair permit issued", "repair permit was issued",
                "repair approval issued", "repair completed", "no repair history"
        ));
        addContextFinding(findings, lower, text, "reserve_area", "Reserve area", List.of(
                "reserve area shown", "shows the reserve area", "reserve area designated",
                "primary and reserve area", "reserve area approved", "reserve area available",
                "reserve area intact"
        ));
        if (ocrUsed) {
            findings.replaceAll((key, finding) -> new DocumentFinding(
                    finding.key(),
                    finding.label(),
                    finding.value(),
                    "Low",
                    finding.evidence()
            ));
        }
        return List.copyOf(findings.values());
    }

    private boolean hasReadableText(String text) {
        return !text.isBlank() && text.replaceAll("\\s+", "").length() >= 30;
    }

    private void addSystemTypeFinding(Map<String, DocumentFinding> findings, String text) {
        Matcher matcher = SYSTEM_TYPE.matcher(text);
        boolean fieldMatch = true;
        if (!matcher.find()) {
            matcher = SYSTEM_TYPE_NARRATIVE.matcher(text);
            fieldMatch = false;
        }
        if (!matcher.find(0)) {
            return;
        }
        if (fieldMatch) {
            int lineEnd = text.indexOf('\n', matcher.end());
            String remainingLine = text.substring(
                    matcher.end(),
                    lineEnd < 0 ? Math.min(text.length(), matcher.end() + 100) : Math.min(lineEnd, matcher.end() + 100)
            ).toLowerCase(Locale.US);
            if (remainingLine.matches("(?s).*\\b(?:mound|sand\\s+filter|drip|other\\s+specify|pressure\\s+distribution)\\b.*")) {
                return;
            }
        }
        String raw = matcher.group(1);
        String normalized = switch (raw.toLowerCase(Locale.US)) {
            case "aerobic", "aerobic treatment unit" -> "Aerobic treatment unit";
            case "mound" -> "Mound system";
            case "drip", "drip dispersal" -> "Drip dispersal";
            case "low-pressure", "low pressure", "low-pressure pipe", "low pressure pipe" -> "Low-pressure pipe";
            case "gravity" -> "Gravity system";
            default -> "Conventional system";
        };
        findings.put("system_type", new DocumentFinding(
                "system_type", "System type", normalized, "Medium",
                snippet(text, matcher.start(), matcher.end())
        ));
    }

    private boolean isPermitStopWord(String value) {
        return switch (value.toLowerCase(Locale.US)) {
            case "activity", "address", "application", "drawing", "evaluation", "fact",
                    "form", "guide", "installation", "number", "option", "procedure",
                    "requested", "satisfactory", "transfer" -> true;
            default -> false;
        };
    }

    private void addContextFinding(
            Map<String, DocumentFinding> findings,
            String lower,
            String original,
            String key,
            String label,
            List<String> phrases
    ) {
        for (String phrase : phrases) {
            int index = lower.indexOf(phrase);
            if (index >= 0) {
                findings.put(key, new DocumentFinding(
                        key, label, "Mentioned", "Low",
                        snippet(original, index, index + phrase.length())
                ));
                return;
            }
        }
    }

    private void addPatternFinding(
            Map<String, DocumentFinding> findings,
            String text,
            String key,
            String label,
            Pattern pattern,
            String confidence
    ) {
        addPatternFinding(findings, text, key, label, pattern, confidence, "");
    }

    private void addPatternFinding(
            Map<String, DocumentFinding> findings,
            String text,
            String key,
            String label,
            Pattern pattern,
            String confidence,
            String suffix
    ) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            findings.put(key, new DocumentFinding(
                    key,
                    label,
                    matcher.group(1) + suffix,
                    confidence,
                    snippet(text, matcher.start(), matcher.end())
            ));
        }
    }

    private List<String> missingItems(String purpose, List<DocumentFinding> findings) {
        Map<String, DocumentFinding> byKey = findings.stream()
                .collect(LinkedHashMap::new, (map, finding) -> map.put(finding.key(), finding), Map::putAll);
        List<String> missing = new ArrayList<>();
        for (String item : purposeMissingItems(purpose)) {
            String key = item.substring(0, item.indexOf('|'));
            String label = item.substring(item.indexOf('|') + 1);
            if (!byKey.containsKey(key)) {
                missing.add(label);
            }
        }
        return missing;
    }

    private List<String> purposeMissingItems(String purpose) {
        return switch (purpose) {
            case "bedrooms" -> List.of(
                    "approved_bedrooms|Approved bedroom count",
                    "design_flow|Design flow",
                    "final_approval|Final approval status"
            );
            case "location" -> List.of(
                    "layout|As-built or site plan",
                    "reserve_area|Drain field or reserve-area detail"
            );
            case "repair" -> List.of(
                    "system_type|System type",
                    "layout|As-built or site plan",
                    "repair_history|Repair history"
            );
            case "replacement" -> List.of(
                    "system_type|System type",
                    "tank_capacity|Tank capacity",
                    "layout|As-built or site plan",
                    "reserve_area|Reserve-area information"
            );
            case "lender" -> List.of(
                    "permit_number|Permit or record number",
                    "final_approval|Final approval or inspection letter"
            );
            case "owner" -> List.of(
                    "permit_number|Permit or record number",
                    "layout|As-built or site plan",
                    "final_approval|Final approval"
            );
            default -> List.of(
                    "permit_number|Permit or record number",
                    "final_approval|Final approval",
                    "layout|As-built or site plan",
                    "repair_history|Repair history"
            );
        };
    }

    private List<String> nextSteps(
            String purpose,
            List<DocumentFinding> findings,
            List<String> missing,
            String stateCode,
            String countyName,
            boolean ocrUsed
    ) {
        List<String> steps = new ArrayList<>();
        if (ocrUsed) {
            steps.add("Compare every OCR value, especially permit numbers, dates, bedroom counts, and flow, with the original scan.");
        }
        if (!missing.isEmpty()) {
            steps.add("Request the missing items listed above from the official file owner.");
        }
        switch (purpose) {
            case "bedrooms" -> steps.add("Compare the approved bedroom count with the current listing or planned addition before changing use.");
            case "location" -> steps.add("Use the drawing as a starting point only; confirm tank and field locations on site before digging.");
            case "repair" -> steps.add("Share the permit, layout, and repair trail with the service provider before accepting a repair scope.");
            case "replacement" -> steps.add("Use the documented system type, tank size, and reserve-area note before comparing replacement estimates.");
            case "lender" -> steps.add("Confirm which document the lender or closing agent will accept before ordering another inspection.");
            case "owner" -> steps.add("Save the original document with the property file and record where it came from.");
            default -> steps.add("Check final approval, layout, and repair history before treating the property file as complete.");
        }
        if (countyName != null && !countyName.isBlank()) {
            steps.add("Use " + countyName + " as the file owner context if a follow-up request is needed.");
        } else if (stateCode != null && !stateCode.isBlank()) {
            steps.add("Use the " + stateCode.toUpperCase(Locale.US) + " records route for any follow-up request.");
        }
        return steps.stream().distinct().toList();
    }

    private DocumentDecision unreadableDecision(String purpose) {
        return new DocumentDecision(
                "blocked",
                "No decision yet",
                "This scan cannot support a property decision here",
                "No readable source text was available, so the document has not been interpreted.",
                List.of(),
                "The scan does not prove approval, capacity, location, condition, or lender acceptance."
        );
    }

    private DocumentDecision decisionFor(
            String purpose,
            List<DocumentFinding> findings,
            List<String> missingItems,
            boolean ocrUsed
    ) {
        Map<String, DocumentFinding> byKey = findings.stream()
                .collect(LinkedHashMap::new, (map, finding) -> map.put(finding.key(), finding), Map::putAll);
        List<String> supportedBy = findings.stream()
                .filter(finding -> purposeKeys(purpose).contains(finding.key()))
                .map(finding -> finding.label() + ": " + finding.value())
                .limit(4)
                .toList();
        boolean complete = missingItems.isEmpty();
        String level = complete ? "supported" : supportedBy.isEmpty() ? "blocked" : "incomplete";
        String label = complete ? "Main fields present" : supportedBy.isEmpty() ? "Cannot answer yet" : "More proof needed";

        DocumentDecision decision = switch (purpose) {
            case "bedrooms" -> {
                DocumentFinding bedrooms = byKey.get("approved_bedrooms");
                String title = bedrooms == null
                        ? "The approved bedroom count is not established"
                        : "This file mentions approval for " + bedrooms.value() + " bedrooms";
                String answer = complete
                        ? "The main approval fields appear together in this file. Use the original record to compare this count with the listing or addition plan."
                        : "Treat the bedroom count as provisional until the missing approval fields are obtained.";
                yield new DocumentDecision(level, label, title, answer, supportedBy,
                        "This does not prove current system condition, that an addition is allowed, or that later permits did not change the approval.");
            }
            case "location" -> new DocumentDecision(level, label,
                    byKey.containsKey("layout") ? "A layout drawing is mentioned" : "The installed system location is not established",
                    complete
                            ? "The file contains the main drawing clues for a field review."
                            : "Do not use this extraction alone to choose a digging location.",
                    supportedBy,
                    "Text extraction cannot read drawing coordinates or confirm where buried components are in the field.");
            case "repair" -> new DocumentDecision(level, label,
                    complete ? "The file is ready for a repair-scope conversation" : "The repair file is incomplete",
                    complete
                            ? "Share the original permit, layout, and repair trail before accepting a scope."
                            : "A contractor would still be defining scope without key history or layout information.",
                    supportedBy,
                    "The document does not diagnose the failure or verify the current condition of the tank or field.");
            case "replacement" -> new DocumentDecision(level, label,
                    complete ? "The main replacement-scope clues are present" : "Do not price a final replacement scope from this file yet",
                    complete
                            ? "Use these fields to compare planning ranges, then confirm site and permit requirements locally."
                            : "Missing system or site details can materially change the replacement method and cost.",
                    supportedBy,
                    "This is not a contractor bid, soil evaluation, design, or permit approval.");
            case "lender" -> new DocumentDecision(level, label,
                    complete ? "The basic permit trail appears present" : "The closing file may not be sufficient",
                    complete
                            ? "Confirm that the lender or closing agent accepts these exact original documents."
                            : "Obtain the missing approval evidence before relying on this file for closing.",
                    supportedBy,
                    "Only the lender, closing agent, or local authority can decide whether the document is acceptable.");
            case "owner" -> new DocumentDecision(level, label,
                    complete ? "This is a useful owner-file foundation" : "The owner file still has gaps",
                    complete
                            ? "Keep the original document with its source and any later service records."
                            : "Add the missing official records before treating the property file as complete.",
                    supportedBy,
                    "The file does not prove current system condition or include future repairs and permits.");
            default -> new DocumentDecision(level, label,
                    complete ? "The main buyer-file checks are present" : "The property file is not complete yet",
                    complete
                            ? "The file contains the main record categories to review before relying on the property description."
                            : "Resolve the missing official records before treating the listing or seller file as verified.",
                    supportedBy,
                    "This document review is not a property inspection, title opinion, code determination, or guarantee of system condition.");
        };
        if (ocrUsed && !supportedBy.isEmpty()) {
            return new DocumentDecision(
                    "incomplete",
                    "Confirm OCR values",
                    decision.title(),
                    "OCR can confuse similar letters and digits. Compare every extracted value with the original scan before using it. "
                            + decision.answer(),
                    decision.supportedBy(),
                    decision.notProven()
            );
        }
        return decision;
    }

    private List<String> purposeKeys(String purpose) {
        return purposeMissingItems(purpose).stream()
                .map(item -> item.substring(0, item.indexOf('|')))
                .toList();
    }

    private String headingFor(String purpose) {
        return switch (purpose) {
            case "bedrooms" -> "Check the approval details that affect bedroom count";
            case "location" -> "Check whether the file can locate the tank and drain field";
            case "repair" -> "Build a repair-ready property file";
            case "replacement" -> "Build a replacement-ready scope";
            case "lender" -> "Check the closing or lender file";
            case "owner" -> "Organize the owner record";
            default -> "Check the buyer or seller file";
        };
    }

    private String normalizePurpose(String purpose) {
        if (purpose == null) {
            return "buying";
        }
        return switch (purpose.trim().toLowerCase(Locale.US)) {
            case "bedrooms", "location", "repair", "replacement", "lender", "owner" -> purpose.trim().toLowerCase(Locale.US);
            default -> "buying";
        };
    }

    private String snippet(String text, int start, int end) {
        int from = Math.max(0, start - 45);
        int to = Math.min(text.length(), end + 65);
        return text.substring(from, to).replaceAll("\\s+", " ").trim();
    }

    private String safeFileName(String original) {
        if (original == null || original.isBlank()) {
            return "septic-record";
        }
        String normalized = original.replace('\\', '/');
        return normalized.substring(normalized.lastIndexOf('/') + 1).replaceAll("[\\r\\n]", "");
    }

    private String limitText(String text) {
        if (text == null || text.length() <= MAX_EXTRACTED_CHARACTERS) {
            return text == null ? "" : text;
        }
        return text.substring(0, MAX_EXTRACTED_CHARACTERS);
    }

    private record ExtractedDocument(
            String text,
            boolean ocrUsed,
            String message,
            List<String> pages
    ) {
    }
}
