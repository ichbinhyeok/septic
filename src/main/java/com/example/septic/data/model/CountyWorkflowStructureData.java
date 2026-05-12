package com.example.septic.data.model;

public record CountyWorkflowStructureData(
        String fileOwnerCategory,
        String fileOwnerModel,
        String firstArtifactToPull,
        String permitCloseoutCategory,
        String permitCloseoutSignal,
        String transferCategory,
        String transferArtifact,
        String specialProgramCategory,
        String specialProgramSignal,
        String malfunctionCategory,
        String malfunctionSignal,
        String quoteGate
) {
}
