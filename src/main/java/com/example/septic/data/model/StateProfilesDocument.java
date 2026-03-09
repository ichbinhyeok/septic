package com.example.septic.data.model;

import java.util.List;

public record StateProfilesDocument(
        Integer schemaVersion,
        String generatedAt,
        List<StateProfile> states
) {
}
