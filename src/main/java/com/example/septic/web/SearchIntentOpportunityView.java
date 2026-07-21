package com.example.septic.web;

public record SearchIntentOpportunityView(
        String anchorId,
        String eyebrow,
        String heading,
        String summary,
        String actionLabel,
        String actionPath,
        String targetType
) {
    public String questionHeading() {
        if (heading == null || heading.isBlank()) {
            return "What should I check next?";
        }
        if (heading.endsWith("?")) {
            return heading;
        }
        String lowerHeading = heading.toLowerCase();
        if (lowerHeading.startsWith("how ") || lowerHeading.startsWith("where ")
                || lowerHeading.startsWith("what ") || lowerHeading.startsWith("who ")
                || lowerHeading.startsWith("when ") || lowerHeading.startsWith("can ")
                || lowerHeading.startsWith("does ") || lowerHeading.startsWith("is ")) {
            return heading + "?";
        }
        return "How do I handle " + heading + "?";
    }
}
