package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.service.DocumentOcrService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockMultipartFile;

class OfficialDocumentCorpusTests {
    private static final Path CORPUS_DIRECTORY =
            Path.of("src", "test", "resources", "septic-document-corpus");

    private final SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
            document -> DocumentOcrService.OcrResult.unavailable("OCR is disabled for searchable corpus tests.")
    );

    record CompletedFixture(
            String name,
            String purpose,
            String stateCode,
            String text,
            Map<String, String> expected
    ) {
        @Override
        public String toString() {
            return name;
        }
    }

    @Test
    void blankOfficialFormsDoNotBecomePropertySpecificFindings() throws Exception {
        List<String> falsePositives = new ArrayList<>();
        List<String> rows = Files.readAllLines(CORPUS_DIRECTORY.resolve("corpus.csv")).stream()
                .skip(1)
                .filter(line -> !line.isBlank())
                .toList();

        for (String row : rows) {
            String[] fields = row.split(",", 5);
            String id = fields[0];
            String stateCode = fields[1];
            Path pdf = CORPUS_DIRECTORY.resolve(id + ".pdf");
            assertThat(pdf)
                    .as("downloaded official corpus file for %s", id)
                    .exists();

            SepticDocumentAnalysisResult result = analyzer.analyze(
                    new MockMultipartFile("file", pdf.getFileName().toString(), "application/pdf", Files.readAllBytes(pdf)),
                    "buying",
                    stateCode,
                    ""
            );
            if (!result.findings().isEmpty()) {
                falsePositives.add(id + " -> " + result.findings().stream()
                        .map(finding -> finding.key() + "=" + finding.value())
                        .toList());
            }
        }

        assertThat(rows).hasSizeGreaterThanOrEqualTo(25);
        assertThat(falsePositives)
                .as("Blank official labels, checkbox options, and examples must not be treated as property facts")
                .isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("completedOfficialStyleFixtures")
    void extractsOnlyExpectedFactsFromDeidentifiedCompletedOfficialStyles(CompletedFixture fixture) throws Exception {
        SepticDocumentAnalysisResult result = analyzer.analyze(
                new MockMultipartFile(
                        "file",
                        fixture.name() + ".txt",
                        "text/plain",
                        fixture.text().getBytes(java.nio.charset.StandardCharsets.UTF_8)
                ),
                fixture.purpose(),
                fixture.stateCode(),
                "Validation County"
        );
        Map<String, String> actual = new LinkedHashMap<>();
        result.findings().forEach(finding -> actual.put(finding.key(), finding.value()));

        assertThat(actual)
                .as("property-specific facts for %s", fixture.name())
                .containsExactlyInAnyOrderEntriesOf(fixture.expected());
    }

    static List<CompletedFixture> completedOfficialStyleFixtures() {
        return List.of(
                fixture("tn-construction-approval", "bedrooms", "TN", """
                        Septic Permit Number: SSDS-2026-1842
                        Approved for 4 bedrooms. Design flow: 600 GPD.
                        Existing conventional system. 1250 gallon septic tank.
                        Final approval date: 07/18/2026.
                        The attached as-built shows the reserve area.
                        """, Map.of(
                        "permit_number", "SSDS-2026-1842",
                        "approved_bedrooms", "4",
                        "design_flow", "600 GPD",
                        "tank_capacity", "1250 gallons",
                        "system_type", "Conventional system",
                        "approval_date", "07/18/2026",
                        "final_approval", "Dated",
                        "layout", "Mentioned",
                        "reserve_area", "Mentioned"
                )),
                fixture("nc-aowe-authorization", "bedrooms", "NC", """
                        Permit ID: AOWE-24-7811
                        Number of Bedrooms: 3
                        Daily Design Flow: 360 GPD
                        System Type: Low Pressure Pipe
                        Final approval date: 2026-06-03
                        """, Map.of(
                        "permit_number", "AOWE-24-7811",
                        "approved_bedrooms", "3",
                        "design_flow", "360 GPD",
                        "system_type", "Low-pressure pipe",
                        "approval_date", "2026-06-03",
                        "final_approval", "Dated"
                )),
                fixture("or-existing-system-evaluation", "repair", "OR", """
                        Record Number: OR-ESER-44819
                        Existing gravity system.
                        Septic tank capacity: 1000 gallons.
                        Previous repair dated 04/11/2019.
                        The record drawing shows the installed tank and disposal trenches.
                        """, Map.of(
                        "permit_number", "OR-ESER-44819",
                        "system_type", "Gravity system",
                        "tank_capacity", "1000 gallons",
                        "repair_history", "Mentioned",
                        "layout", "Mentioned"
                )),
                fixture("wa-record-drawing", "location", "WA", """
                        OSS Permit No. KC-2025-9914
                        No. of Bedrooms designed for: 5
                        Operational Capacity: 600 gals/day
                        Installed mound system.
                        The record drawing shows the primary and reserve area.
                        """, Map.of(
                        "permit_number", "KC-2025-9914",
                        "approved_bedrooms", "5",
                        "design_flow", "600 GPD",
                        "system_type", "Mound system",
                        "layout", "Mentioned",
                        "reserve_area", "Mentioned"
                )),
                fixture("mn-as-built", "replacement", "MN", """
                        Septic Permit Number: SSTS-77821
                        System Type: Gravity
                        Septic Tank: Size: 1500 gallons
                        Site plan shows the installed field. Reserve area designated.
                        """, Map.of(
                        "permit_number", "SSTS-77821",
                        "system_type", "Gravity system",
                        "tank_capacity", "1500 gallons",
                        "layout", "Mentioned",
                        "reserve_area", "Mentioned"
                )),
                fixture("ca-existing-system-certification", "buying", "CA", """
                        Permit No: OWTS-26-4408
                        Approved Bedrooms: 4
                        System Type: Aerobic Treatment Unit
                        Septic Tank Volume: 1250 gal.
                        Inspection date: 06/24/2026
                        """, Map.of(
                        "permit_number", "OWTS-26-4408",
                        "approved_bedrooms", "4",
                        "system_type", "Aerobic treatment unit",
                        "tank_capacity", "1250 gallons",
                        "approval_date", "06/24/2026",
                        "final_approval", "Dated"
                )),
                fixture("ma-certificate-style", "lender", "MA", """
                        Application Number: T5-2026-831
                        Permitted for 3 bedrooms.
                        Design flow is 330 gallons per day.
                        Final approval date: 2026-05-19
                        """, Map.of(
                        "permit_number", "T5-2026-831",
                        "approved_bedrooms", "3",
                        "design_flow", "330 GPD",
                        "approval_date", "2026-05-19",
                        "final_approval", "Dated"
                )),
                fixture("co-repair-record", "repair", "CO", """
                        Permit # OWTS-R-10442
                        System Type: Drip Dispersal
                        Tank capacity: 1000 gallons
                        Repair permit number R-10442 was issued for the pump controls.
                        """, Map.of(
                        "permit_number", "OWTS-R-10442",
                        "system_type", "Drip dispersal",
                        "tank_capacity", "1000 gallons",
                        "repair_history", "Mentioned"
                )),
                fixture("buyer-no-repair-history", "buying", "VA", """
                        Permit Number: HD-2020-5541
                        Approved for 3 bedrooms.
                        No repair history.
                        Final approval date: 09/30/2020
                        """, Map.of(
                        "permit_number", "HD-2020-5541",
                        "approved_bedrooms", "3",
                        "repair_history", "Mentioned",
                        "approval_date", "09/30/2020",
                        "final_approval", "Dated"
                )),
                fixture("location-drawing", "location", "IN", """
                        Record ID: OSS-88219
                        As-built shows the tank, distribution box, and drain field.
                        Reserve area shown southeast of the primary field.
                        """, Map.of(
                        "permit_number", "OSS-88219",
                        "layout", "Mentioned",
                        "reserve_area", "Mentioned"
                )),
                fixture("replacement-low-pressure", "replacement", "PA", """
                        Permit Number: SEO-7719
                        Existing low-pressure system.
                        Septic tank size: 1250 gallons.
                        Layout drawing shows the absorption area.
                        """, Map.of(
                        "permit_number", "SEO-7719",
                        "system_type", "Low-pressure pipe",
                        "tank_capacity", "1250 gallons",
                        "layout", "Mentioned"
                )),
                fixture("owner-conventional-field", "owner", "GA", """
                        Record No. EH-2024-1109
                        System Type: Conventional Gravity
                        Approved bedrooms: 4
                        """, Map.of(
                        "permit_number", "EH-2024-1109",
                        "system_type", "Conventional system",
                        "approved_bedrooms", "4"
                )),
                fixture("blank-application-note", "buying", "WA", """
                        Permit application form
                        Number of Bedrooms:
                        System Type: Gravity PD Mound Sand Filter Other specify
                        Maximum daily flow > 1,000 GPD requires engineering review.
                        Final approval:
                        """, Map.of()),
                fixture("negated-final-approval", "bedrooms", "TN", """
                        Permit Number: SSDS-1102
                        Approved for 3 bedrooms.
                        No final approval or design flow document was included.
                        """, Map.of(
                        "permit_number", "SSDS-1102",
                        "approved_bedrooms", "3"
                )),
                fixture("inspection-date-format", "buying", "FL", """
                        Record Number: DOH-22-18440
                        Inspection date: 12/05/2022
                        Existing conventional system.
                        """, Map.of(
                        "permit_number", "DOH-22-18440",
                        "approval_date", "12/05/2022",
                        "final_approval", "Dated",
                        "system_type", "Conventional system"
                )),
                fixture("maximum-design-fields", "bedrooms", "AZ", """
                        Application ID: APP-260077
                        Maximum 6 bedrooms
                        Maximum daily flow: 900 GPD
                        Proposed aerobic treatment unit system.
                        """, Map.of(
                        "permit_number", "APP-260077",
                        "approved_bedrooms", "6",
                        "design_flow", "900 GPD",
                        "system_type", "Aerobic treatment unit"
                ))
        );
    }

    private static CompletedFixture fixture(
            String name,
            String purpose,
            String stateCode,
            String text,
            Map<String, String> expected
    ) {
        return new CompletedFixture(name, purpose, stateCode, text, expected);
    }
}
