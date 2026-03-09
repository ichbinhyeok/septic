package com.example.septic.data.model;

import java.util.List;

public record StateMoneyPagesDocument(
        Integer schemaVersion,
        String generatedAt,
        List<StateMoneyPage> pages
) {
}
