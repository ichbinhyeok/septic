package com.example.septic.web;

import com.example.septic.data.model.SourceRecord;
import java.util.List;

public record ContentEvidenceLaneView(
        String title,
        String stateName,
        String path,
        String reviewedAgainst,
        String lastReviewedAt,
        List<SourceRecord> sources
) {
}
