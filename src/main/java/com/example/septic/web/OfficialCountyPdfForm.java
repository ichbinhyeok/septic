package com.example.septic.web;

import java.util.Map;

public record OfficialCountyPdfForm(
        String countyKey,
        String address,
        String parcel,
        Map<String, String> fields
) {}
