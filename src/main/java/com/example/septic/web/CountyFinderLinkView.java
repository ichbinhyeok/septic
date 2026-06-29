package com.example.septic.web;

public record CountyFinderLinkView(
        String title,
        String path,
        String note,
        String stateCode,
        String stateName,
        String countyName,
        String searchText
) {
}
