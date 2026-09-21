package com.example.septic.web;

import java.util.Map;

/** Only exact equivalent national guides; never replaces a regional guide with a generic page. */
public final class StudioGuideRoutes {
    public record Hero(String label, String heading, String intro) {}
    public static final Map<String, Hero> RECORDS = Map.of(
            "septic-as-built-records", new Hero("As-built records", "Read the layout. Know its limits.", "Find the recorded drawing—and understand what it can establish."),
            "how-to-find-septic-records-online", new Hero("Online records", "Search broadly. Verify narrowly.", "Move from an online clue to the office and artifact that can actually answer the property question."),
            "septic-tank-location-records", new Hero("Tank location records", "Find the drawing before the lid.", "Use the recorded layout to narrow the search without pretending an old sketch is a current locate."),
            "septic-permit-search-by-address", new Hero("Search by address", "An address is the beginning.", "Trace the property, identify the office, and follow the records route."),
            "septic-permit-records-request", new Hero("Request the file", "Ask for the right record.", "Prepare the property clues and a clear request for the responsible office."),
            "septic-inspection-letter", new Hero("Inspection letters", "Know what the letter confirms.", "Separate the recorded finding from the questions that still need an answer."));
    public static final Map<String, Hero> PLANNING = Map.of(
            "septic-replacement-cost", new Hero("Whole-system replacement", "Price the scope, not the fear.", "Separate a full replacement path from field-only work, site uncertainty, and the records that can narrow the story."),
            "perc-test-cost", new Hero("Perc & site testing", "Pay to learn the right thing.", "Define the test, the report, and the approval question before comparing a bare visit price."),
            "drain-field-replacement-cost", new Hero("Drain-field replacement", "Read the field before the quote.", "Separate trench work from soil, reserve-area, access, and redesign risk."),
            "failed-perc-test-septic", new Hero("Failed perc result", "A failed result changes the path.", "Understand the next site, design, and approval questions before treating the first result as the final answer."),
            "septic-replacement-area", new Hero("Replacement area", "Protect the future field.", "Read the reserve-area and parcel constraints before an addition, sale, or replacement makes the question urgent."),
            "wet-yard-over-septic-drain-field", new Hero("Wet yard & drain field", "Treat the symptom as a signal.", "Separate an urgent exposure problem from the records, inspection, and field questions that define the next move."));
    private StudioGuideRoutes() {}

    public static String recordsHeroImage(String stateCode) {
        return switch (stateCode == null ? "" : stateCode) {
            case "TN" -> "/images/studio/tdec-desk-v1.webp";
            case "NC" -> "/images/studio/property-hero-v1.webp";
            case "TX" -> "/images/studio/oak-pasture-v1.webp";
            case "SC", "FL" -> "/images/studio/county-property-v1.webp";
            default -> "/images/studio/county-property-v1.webp";
        };
    }

    public static String costHeroImage(String stateCode) {
        return switch (stateCode == null ? "" : stateCode) {
            case "AL", "FL" -> "/images/studio/calculator-landscape-v2.webp";
            case "TX" -> "/images/studio/oak-pasture-v1.webp";
            case "NC" -> "/images/studio/property-hero-v1.webp";
            case "TN" -> "/images/studio/tdec-desk-v1.webp";
            default -> "/images/studio/oak-pasture-v1.webp";
        };
    }

    public static String preview(String path) {
        if (path == null || path.isBlank() || !path.startsWith("/")) {
            return path == null ? "" : path;
        }
        String suffix = "";
        int suffixAt = firstSuffixIndex(path);
        String cleanPath = path;
        if (suffixAt >= 0) {
            suffix = path.substring(suffixAt);
            cleanPath = path.substring(0, suffixAt);
        }
        String direct = switch (cleanPath) {
            case "/" -> "/design-preview/studio/";
            case "/offer-prep-septic-file-check/" -> "/design-preview/studio/intake/";
            case "/septic-record-brief-example/" -> "/design-preview/studio/work/";
            case "/states/" -> "/design-preview/studio/guides/";
            case "/septic-records-by-county/" -> "/design-preview/studio/counties/";
            case "/septic-bedroom-permit-checker/" -> "/design-preview/studio/bedroom-check/";
            case "/septic-records-request-builder/" -> "/design-preview/studio/request-builder/";
            case "/septic-permit-process/" -> "/design-preview/studio/topics/septic-permit-process/";
            case "/septic-inspection-cost/" -> "/design-preview/studio/topics/septic-inspection-cost/";
            case "/texas-ossf-records-search/" -> "/design-preview/studio/texas-ossf-records/";
            case "/florida-ostds-permit-lookup/" -> "/design-preview/studio/florida-ostds-records/";
            case "/dhec-septic-permit-lookup/" -> "/design-preview/studio/south-carolina-records/";
            case "/methodology/" -> "/design-preview/studio/methodology/";
            case "/privacy-policy/" -> "/design-preview/studio/policies/privacy-policy/";
            case "/terms-of-use/" -> "/design-preview/studio/policies/terms-of-use/";
            default -> null;
        };
        if (direct != null) return direct + suffix;
        if (cleanPath.startsWith("/septic-record-brief-example/") && cleanPath.length() > "/septic-record-brief-example/".length()) {
            return "/design-preview/studio/work/" + cleanPath.substring("/septic-record-brief-example/".length()) + suffix;
        }
        for (String slug : RECORDS.keySet()) {
            if (cleanPath.equals("/" + slug + "/")) {
                return "/design-preview/studio/topics/" + slug + "/" + suffix;
            }
        }
        for (String slug : PLANNING.keySet()) {
            if (cleanPath.equals("/" + slug + "/")) {
                return "/design-preview/studio/topics/" + slug + "/" + suffix;
            }
        }
        if (cleanPath.equals("/septic-system-cost-calculator/")) {
            return "/design-preview/studio/calculator/" + suffix;
        }
        if (cleanPath.matches("/septic-system-cost-calculator/[^/]+/")) {
            String state = cleanPath.substring("/septic-system-cost-calculator/".length(), cleanPath.length() - 1);
            return "/design-preview/studio/" + state + "/" + suffix;
        }
        if (cleanPath.equals("/drain-field-estimator/")) {
            return appendQuery("/design-preview/studio/calculator/", "projectType=drainfield_replacement", suffix);
        }
        if (cleanPath.equals("/septic-tank-size-estimator/")) {
            return appendQuery("/design-preview/studio/calculator/", "mode=tank_size", suffix);
        }
        if (cleanPath.equals("/septic-pump-schedule-estimator/")) {
            return appendQuery("/design-preview/studio/calculator/", "mode=pump_schedule", suffix);
        }
        if (cleanPath.equals("/septic-records-checklist/")) return "/design-preview/studio/checklist/" + suffix;
        if (cleanPath.equals("/septic-permit-lookup/")) return "/design-preview/studio/permit-lookup/" + suffix;
        if (cleanPath.equals("/tdec-septic-records/")) return "/design-preview/studio/tdec-records/" + suffix;
        if (cleanPath.equals("/official-septic-lookup-tools/")) return "/design-preview/studio/official-lookup-tools/" + suffix;
        if (cleanPath.equals("/north-carolina-septic-permit-lookup/")) return "/design-preview/studio/north-carolina-records/" + suffix;

        String state = segment(cleanPath, "/septic-records-checklist/");
        if (state != null) return "/design-preview/studio/records/" + state + "/" + suffix;
        String county = countySegments(cleanPath, "/septic-records-checklist/");
        if (county != null) return "/design-preview/studio/" + county + "/" + suffix;
        state = segment(cleanPath, "/septic-permit-process/");
        if (state != null) return "/design-preview/studio/permit-process/" + state + "/" + suffix;
        state = segment(cleanPath, "/septic-inspection-cost/");
        if (state != null) return "/design-preview/studio/inspection-cost/" + state + "/" + suffix;
        state = segment(cleanPath, "/buying-a-house-with-a-septic-system/");
        if (state != null) return "/design-preview/studio/buying-guide/" + state + "/" + suffix;
        state = segment(cleanPath, "/septic-replacement-cost/");
        if (state != null) return "/design-preview/studio/replacement-cost/" + state + "/" + suffix;
        state = segment(cleanPath, "/perc-test-cost/");
        if (state != null) return "/design-preview/studio/perc-test-cost/" + state + "/" + suffix;
        state = segment(cleanPath, "/drain-field-replacement-cost/");
        if (state != null) return "/design-preview/studio/drain-field-cost/" + state + "/" + suffix;
        state = segment(cleanPath, "/failed-perc-test-septic/");
        if (state != null) return "/design-preview/studio/failed-perc/" + state + "/" + suffix;
        state = segment(cleanPath, "/septic-replacement-area/");
        if (state != null) return "/design-preview/studio/replacement-area/" + state + "/" + suffix;
        state = segment(cleanPath, "/wet-yard-over-septic-drain-field/");
        if (state != null) return "/design-preview/studio/wet-yard/" + state + "/" + suffix;
        return path;
    }

    public static String route(String path, boolean production) {
        return production ? path : preview(path);
    }

    private static int firstSuffixIndex(String path) {
        int query = path.indexOf('?');
        int fragment = path.indexOf('#');
        if (query < 0) return fragment;
        if (fragment < 0) return query;
        return Math.min(query, fragment);
    }

    private static String appendQuery(String base, String query, String suffix) {
        if (suffix.startsWith("?")) return base + "?" + query + "&" + suffix.substring(1);
        return base + "?" + query + suffix;
    }

    private static String segment(String path, String prefix) {
        if (!path.startsWith(prefix)) return null;
        String rest = path.substring(prefix.length());
        if (!rest.endsWith("/") || rest.substring(0, rest.length() - 1).contains("/")) return null;
        String value = rest.substring(0, rest.length() - 1);
        return value.isBlank() ? null : value;
    }

    private static String countySegments(String path, String prefix) {
        if (!path.startsWith(prefix)) return null;
        String rest = path.substring(prefix.length());
        if (!rest.endsWith("/")) return null;
        String value = rest.substring(0, rest.length() - 1);
        return value.chars().filter(ch -> ch == '/').count() == 1 ? value : null;
    }
}
