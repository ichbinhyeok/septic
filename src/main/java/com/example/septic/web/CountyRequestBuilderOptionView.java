package com.example.septic.web;

import java.util.List;

public record CountyRequestBuilderOptionView(
        String key,
        String label,
        String heading,
        String subjectLine,
        List<String> scriptLines,
        List<String> checklist,
        String actionLabel,
        String actionUrl,
        boolean external
) {
}
