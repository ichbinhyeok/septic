package com.example.septic.web;

public record CountyEvidenceFactView(
        String agencyName,
        String sourceTitle,
        String sourceUrl,
        String summary,
        String verifiedAt,
        boolean countyLocal
) {
    public String scopeLabel() {
        return countyLocal ? "County-scoped source" : "State source";
    }
}
