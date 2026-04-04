package com.example.septic.data.model;

import java.util.List;

public record CountyRecordsPagesDocument(
        int schemaVersion,
        String generatedAt,
        List<CountyRecordsPage> pages
) {
}
