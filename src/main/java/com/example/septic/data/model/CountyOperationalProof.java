package com.example.septic.data.model;

import java.util.List;

/**
 * Privacy-reviewed, publishable evidence from an actual records workflow.
 * Private customer data must never be copied into this public model.
 */
public record CountyOperationalProof(
        String routeId,
        String statusLabel,
        String title,
        String summary,
        List<String> steps,
        List<String> findings,
        String limitation,
        List<CountyProofDocument> documents,
        String verifiedAt
) {
}
