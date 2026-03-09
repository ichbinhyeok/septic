package com.example.septic.data.model;

import java.util.List;

public record ContentPagesDocument(
        Integer schemaVersion,
        String generatedAt,
        List<ContentPage> pages
) {
}
