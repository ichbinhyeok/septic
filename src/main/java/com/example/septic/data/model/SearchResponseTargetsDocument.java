package com.example.septic.data.model;

import java.util.List;

public record SearchResponseTargetsDocument(
        String generatedAt,
        String source,
        List<SearchResponseTarget> targets
) {
}
