package com.example.septic.web;

import java.util.List;

public record DocumentDecision(
        String level,
        String label,
        String title,
        String answer,
        List<String> supportedBy,
        String notProven
) {
}
