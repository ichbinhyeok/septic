package com.example.septic.data.model;

import java.util.List;

public record StateMoneyPage(
        String contentSlug,
        String stateCode,
        String title,
        String metaDescription,
        String introCopy,
        String uniqueAngle,
        List<String> driverBullets,
        List<FaqBlock> faqBlocks,
        List<String> internalLinkTargets,
        List<String> officialSourceIds
) {
    public String key() {
        return contentSlug + "::" + stateCode;
    }
}
