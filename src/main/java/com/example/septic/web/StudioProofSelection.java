package com.example.septic.web;

import java.util.Map;

/** Explicit editorial matching; never implies that a case occurred in another county. */
public final class StudioProofSelection {
    private StudioProofSelection() {}
    public static StudioCaseStudies.Study fromSource(String source) {
        return StudioCaseStudies.ALL.stream()
                .filter(s -> ("/design-preview/studio/work/" + s.slug() + "/").equals(source))
                .findFirst().orElse(null);
    }
    public static String question(String slug) {
        return switch (slug) {
            case "morgan" -> "The map had no answer. What did the installation file reveal?";
            case "roane" -> "No layout in the listing. How did we find the sketch?";
            case "anderson" -> "300 feet or 200? Why did the records disagree?";
            case "st-croix" -> "26 pages. Which ones actually answered the question?";
            case "overton" -> "We found a repair file. Could we prove it was the right property?";
            case "williamson" -> "What did the completion record add to the permit?";
            case "haverhill" -> "A reported replacement. What did the records actually show?";
            case "cole" -> "Nothing online. Was that enough to close the search?";
            default -> throw new IllegalArgumentException("No editorial question for " + slug);
        };
    }
    public static String teaser(String slug) {
        return switch (slug) {
            case "morgan" -> "The map could not locate the system. The two-page installation file gave us a starting point—and two different length entries. Here is how we read them.";
            case "roane" -> "The listing could not show the underground layout. A focused archive request recovered a sketch. See how we turned its markings into a usable explanation.";
            case "anderson" -> "One record said 300 feet. Another said 200. The document stages explained why reading only the permit would miss part of the story.";
            case "st-croix" -> "Twenty-six pages were not one consistent answer. See how we separated revised records, conflicting entries and an unrelated appendix.";
            case "overton" -> "Older owner and road names led to a repair file. But one missing identity clue kept us from calling it a confirmed property match.";
            case "williamson" -> "The permit described a design. The completion record added another stage. See what reading them together answered for the customer.";
            case "haverhill" -> "A recent replacement was reported. Online checks did not substantiate it, and the city returned a much older repair permit. What could the customer actually rely on?";
            case "cole" -> "An empty online search was not enough. We followed the County route and obtained a separate Health Department confirmation before closing the documentary search.";
            default -> throw new IllegalArgumentException("No editorial teaser for " + slug);
        };
    }
    private static final Map<String, String> LOCAL = Map.of(
        "tennessee/roane-county", "roane", "tennessee/overton-county", "overton",
        "tennessee/williamson-county", "williamson", "alabama/morgan-county", "morgan",
        "south-carolina/anderson-county", "anderson", "wisconsin/st-croix-county", "st-croix",
        "massachusetts/essex-county", "haverhill", "missouri/cole-county", "cole");
    private static final Map<String, String> STATE = Map.of(
        "tennessee", "roane", "alabama", "morgan", "south-carolina", "anderson",
        "wisconsin", "st-croix", "massachusetts", "haverhill", "missouri", "cole");
    public static StudioCaseStudies.Study select(String state, String county) {
        String slug = LOCAL.getOrDefault(state + "/" + county, STATE.getOrDefault(state, "haverhill"));
        return StudioCaseStudies.ALL.stream().filter(study -> study.slug().equals(slug)).findFirst().orElseThrow();
    }
}
