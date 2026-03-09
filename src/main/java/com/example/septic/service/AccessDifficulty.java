package com.example.septic.service;

import java.util.Arrays;

public enum AccessDifficulty {
    EASY("easy", "Easy"),
    MEDIUM("medium", "Medium"),
    HARD("hard", "Hard");

    private final String value;
    private final String label;

    AccessDifficulty(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public static AccessDifficulty fromValue(String value) {
        return Arrays.stream(values())
                .filter(access -> access.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(EASY);
    }
}
