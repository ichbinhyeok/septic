package com.example.septic.web;

import java.util.Locale;

public record PageLink(
        String title,
        String path,
        String note
) {
    public String compactTitle() {
        String label = title == null ? "" : title.trim();
        if (label.isEmpty()) {
            return "";
        }

        String lowerLabel = label.toLowerCase(Locale.US);
        if (label.contains(", ") && lowerLabel.contains("permit lookup")) {
            return label;
        }

        int countyIndex = label.indexOf(" County");
        if (countyIndex >= 0 && (lowerLabel.contains("septic records") || lowerLabel.contains("records checklist"))) {
            return label.substring(0, countyIndex + " County".length()) + " records";
        }
        if (countyIndex >= 0 && lowerLabel.contains("permit lookup")) {
            return label.substring(0, countyIndex + " County".length()) + " permit lookup";
        }

        return label;
    }
}
