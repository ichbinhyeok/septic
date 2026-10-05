package com.example.septic.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Page context is an editable research starting point, never verified property evidence. */
public record RecordHelpEntry(String intent, String title, String description, String action,
                              String researchGoal, String helpPurpose) {
    public static RecordHelpEntry of(String intent) {
        return switch (intent == null ? "" : intent) {
            case "location" -> new RecordHelpEntry("location", "Find the records with location clues.",
                    "Send the address. We’ll look for the permit and layout, explain the location clues, and identify what still needs an on-site check.",
                    "Find my location records", "system_layout", "location");
            case "missing" -> new RecordHelpEntry("missing", "Couldn’t find your septic record?",
                    "Send the address. We’ll check the record route and contact the relevant office when needed. You receive what we find, free.",
                    "Continue my record search", "original_documents", "");
            case "review" -> new RecordHelpEntry("review", "Have a file but still have questions?",
                    "Share the file and property address. We’ll explain what the record supports and what remains unanswered, free.",
                    "Get a free file review", "understand_file", "");
            case "buying" -> new RecordHelpEntry("buying", "Check the records behind the property.",
                    "Send the address. We’ll look for the permit, layout and approval records to help you ask better questions before buying or selling.",
                    "Check this property’s records", "original_documents", "inspection");
            case "building" -> new RecordHelpEntry("building", "Find the septic layout before planning the project.",
                    "Planning a deck, addition or other yard project? Send the address. We’ll look for the tank and drainfield layout and explain what still needs local confirmation. Records are not permission to build or dig.",
                    "Check my project’s septic records", "system_layout", "building");
            case "capacity" -> new RecordHelpEntry("capacity", "What capacity does the record support?",
                    "Send the address. We’ll look for the recorded design basis and explain what it says about bedrooms, flow and system capacity.",
                    "Check my recorded capacity", "design_capacity", "");
            default -> new RecordHelpEntry("records", "Tell us about the property.",
                    "Start with the address and your question. Get the records we find, our explanation and a useful next step, free.",
                    "Get free record help", "other", "");
        };
    }

    public static RecordHelpEntry fromSource(String source) {
        return of(source != null && source.startsWith("entry_") ? source.substring(6) : "records");
    }

    public static RecordHelpEntry forGuide(String slug) {
        return of(switch (slug) {
            case "septic-tank-location-records" -> "location";
            case "buying-a-house-with-a-septic-system", "septic-transfer-compliance" -> "buying";
            case "septic-permit-search-by-address", "how-to-find-septic-records-online", "septic-permit-records-request" -> "missing";
            default -> "records";
        });
    }

    public String link(String sourcePath, String state, String county) {
        String path = "/offer-prep-septic-file-check/?intent=" + intent;
        if ("review".equals(intent)) path += "&mode=review";
        if (safeSourcePath(sourcePath)) path += "&source=" + encode(sourcePath);
        if (state != null && state.matches("[A-Z]{2}")) path += "&state=" + state;
        if (county != null && !county.isBlank() && county.length() <= 120) path += "&county=" + encode(county);
        return path + "#intake-form";
    }

    public String link(String sourcePath) {
        return link(sourcePath, null, null);
    }

    public void prefill(ClosingRiskCheckForm form, String sourcePath) {
        form.setSourceContext("entry_" + intent);
        if (safeSourcePath(sourcePath)) form.setSourcePageHint(sourcePath);
        form.setResearchGoal(researchGoal);
        if (!helpPurpose.isEmpty()) form.setHelpPurpose(helpPurpose);
        // No record-status or field-work intent is inferred from an article or a CTA.
    }

    public static boolean safeSourcePath(String path) {
        return path != null && path.length() <= 240 && path.matches("/(?:[a-z0-9-]+/)*");
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
