package com.example.septic.data.model;

/** One privacy-reviewed excerpt in an operational proof set. */
public record CountyProofDocument(
        String label,
        String imagePath,
        String imageAlt,
        String stage
) {
}
