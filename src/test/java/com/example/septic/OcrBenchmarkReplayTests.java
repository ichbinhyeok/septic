package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.service.DocumentOcrService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.mock.web.MockMultipartFile;

class OcrBenchmarkReplayTests {
    @Test
    @EnabledIfSystemProperty(named = "ocrBenchmarkResults", matches = ".+")
    void ocrTextMaintainsUsefulFieldRecallWithoutInventingManyFacts() throws Exception {
        Path resultPath = Path.of(System.getProperty("ocrBenchmarkResults"));
        JsonNode root = new ObjectMapper().readTree(Files.readString(resultPath));
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
                document -> DocumentOcrService.OcrResult.unavailable("not used in replay")
        );
        int expectedFields = 0;
        int correctFields = 0;
        int unexpectedFields = 0;
        int samplesWithAnyCorrectField = 0;
        List<Map<String, String>> mismatches = new ArrayList<>();
        Map<String, int[]> byVariant = new LinkedHashMap<>();

        for (JsonNode sample : root.path("samples")) {
            Map<String, String> expected = objectFields(sample.path("expected"));
            SepticDocumentAnalysisResult result = analyzer.analyze(
                    new MockMultipartFile(
                            "file",
                            sample.path("fixture").asText() + ".txt",
                            "text/plain",
                            sample.path("recognizedText").asText().getBytes(StandardCharsets.UTF_8)
                    ),
                    sample.path("purpose").asText(),
                    sample.path("stateCode").asText(),
                    "OCR Validation County"
            );
            Map<String, String> actual = new LinkedHashMap<>();
            result.findings().forEach(finding -> actual.put(finding.key(), finding.value()));

            int sampleCorrect = 0;
            int[] variantCounts = byVariant.computeIfAbsent(sample.path("variant").asText(), ignored -> new int[3]);
            for (Map.Entry<String, String> expectedField : expected.entrySet()) {
                expectedFields++;
                variantCounts[0]++;
                if (expectedField.getValue().equals(actual.get(expectedField.getKey()))) {
                    correctFields++;
                    sampleCorrect++;
                    variantCounts[1]++;
                } else {
                    mismatches.add(Map.of(
                            "fixture", sample.path("fixture").asText(),
                            "variant", sample.path("variant").asText(),
                            "field", expectedField.getKey(),
                            "expected", expectedField.getValue(),
                            "actual", actual.getOrDefault(expectedField.getKey(), "<missing>")
                    ));
                }
            }
            for (String actualKey : actual.keySet()) {
                if (!expected.containsKey(actualKey)) {
                    unexpectedFields++;
                    variantCounts[2]++;
                    mismatches.add(Map.of(
                            "fixture", sample.path("fixture").asText(),
                            "variant", sample.path("variant").asText(),
                            "field", actualKey,
                            "expected", "<none>",
                            "actual", actual.get(actualKey)
                    ));
                }
            }
            if (sampleCorrect > 0) {
                samplesWithAnyCorrectField++;
            }
        }

        int sampleCount = root.path("sampleCount").asInt();
        double recall = expectedFields == 0 ? 0 : (double) correctFields / expectedFields;
        double unexpectedPerSample = sampleCount == 0 ? 0 : (double) unexpectedFields / sampleCount;
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("sampleCount", sampleCount);
        summary.put("expectedFields", expectedFields);
        summary.put("correctFields", correctFields);
        summary.put("fieldRecall", recall);
        summary.put("unexpectedFields", unexpectedFields);
        summary.put("unexpectedFieldsPerSample", unexpectedPerSample);
        summary.put("samplesWithAnyCorrectField", samplesWithAnyCorrectField);
        Map<String, Object> variantSummary = new LinkedHashMap<>();
        byVariant.forEach((variant, counts) -> variantSummary.put(variant, Map.of(
                "expectedFields", counts[0],
                "correctFields", counts[1],
                "fieldRecall", counts[0] == 0 ? 0 : (double) counts[1] / counts[0],
                "unexpectedFields", counts[2]
        )));
        summary.put("byVariant", variantSummary);
        summary.put("mismatches", mismatches);
        Path summaryPath = resultPath.getParent().resolve("pipeline-summary.json");
        Files.writeString(summaryPath, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(summary));

        assertThat(sampleCount).isGreaterThanOrEqualTo(40);
        assertThat(recall).as("exact field recall after OCR").isGreaterThanOrEqualTo(0.75);
        assertThat(unexpectedPerSample).as("invented property facts per OCR sample").isLessThanOrEqualTo(0.10);
        assertThat(samplesWithAnyCorrectField).isGreaterThanOrEqualTo((int) Math.floor(sampleCount * 0.90));
    }

    private Map<String, String> objectFields(JsonNode node) {
        Map<String, String> values = new LinkedHashMap<>();
        node.properties().forEach(entry -> values.put(entry.getKey(), entry.getValue().asText()));
        return values;
    }
}
