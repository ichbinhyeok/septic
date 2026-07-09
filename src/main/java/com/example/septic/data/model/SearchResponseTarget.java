package com.example.septic.data.model;

import java.util.List;
import java.util.Locale;

public record SearchResponseTarget(
        String targetType,
        String key,
        String path,
        Integer boost,
        List<String> queries,
        List<String> recommendedAnchors,
        String evidenceWindow,
        Integer clicks,
        Integer impressions,
        Double ctr,
        Double position,
        String evidenceNote
) {
    public String lookupKey() {
        return lookupKey(targetType, key);
    }

    public int boostValue() {
        return boost == null ? 0 : boost;
    }

    public List<String> queryList() {
        return queries == null ? List.of() : queries;
    }

    public static String lookupKey(String targetType, String key) {
        String safeType = targetType == null ? "" : targetType.trim().toLowerCase(Locale.US);
        String safeKey = key == null ? "" : key.trim();
        return safeType + "::" + safeKey;
    }
}
