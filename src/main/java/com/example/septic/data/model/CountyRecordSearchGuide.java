package com.example.septic.data.model;

import java.util.List;

/** Source-reviewed search instructions, not a claim that a property has a record. */
public record CountyRecordSearchGuide(
        String summary,
        String coverage,
        String identifiers,
        String documents,
        List<String> steps,
        String noResult,
        List<String> sourceIds,
        String reviewedAt
) {
}
