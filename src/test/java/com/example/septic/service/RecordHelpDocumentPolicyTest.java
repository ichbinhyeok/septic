package com.example.septic.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordHelpDocumentPolicyTest {
    private final RecordHelpDocumentPolicy policy = new RecordHelpDocumentPolicy();

    @Test
    void requiresAFileForHumanDocumentReview() {
        assertTrue(policy.validate(List.of(), true).contains("at least one"));
        assertEquals("", policy.validate(List.of(), false));
    }

    @Test
    void acceptsMatchingPdfAndSanitizesItsStoredName() {
        MockMultipartFile pdf = new MockMultipartFile(
                "documents",
                "../Agency Permit #42.PDF",
                "application/pdf",
                "%PDF-1.7\nrecord".getBytes(StandardCharsets.US_ASCII)
        );

        assertEquals("", policy.validate(List.of(pdf), true));
        assertEquals("01-agency-permit-42.pdf", policy.safeFileName(pdf, 1));
        assertEquals("Agency Permit #42.PDF", policy.displayFileName(pdf, 1));
    }

    @Test
    void rejectsAnExtensionThatDoesNotMatchTheBytes() {
        MockMultipartFile disguisedPdf = new MockMultipartFile(
                "documents",
                "permit.pdf",
                "application/pdf",
                "not a pdf".getBytes(StandardCharsets.UTF_8)
        );

        assertTrue(policy.validate(List.of(disguisedPdf), true).contains("does not match"));
    }

    @Test
    void rejectsMoreThanThreeFiles() {
        MockMultipartFile text = new MockMultipartFile(
                "documents", "record.txt", "text/plain", "record".getBytes(StandardCharsets.UTF_8));
        assertTrue(policy.validate(List.of(text, text, text, text), true).contains("no more than three"));
    }
}
