package com.example.septic.web;

public record StatePlanningSnapshot(
        String installMidpoint,
        String replacementMidpoint,
        String percRange,
        String pumpingRange,
        String comparisonNote
) {
}
