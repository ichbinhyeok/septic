package com.example.septic.data.model;

import java.util.List;

public record ProjectCostAnchor(
        String projectType,
        Integer low,
        Integer mid,
        Integer high,
        String status,
        List<String> sourceIds
) {
}
