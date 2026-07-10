package com.example.septic.web;

public record AddressRecordFinderAction(
        String label,
        String path,
        String targetType,
        boolean external,
        boolean primary
) {
}
