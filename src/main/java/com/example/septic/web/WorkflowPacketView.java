package com.example.septic.web;

import com.example.septic.data.model.SourceRecord;
import java.util.List;

public record WorkflowPacketView(
        String eyebrow,
        String heading,
        String lede,
        String packetLabel,
        String sidecardTitle,
        String sidecardBody,
        String channelNote,
        String shareSubject,
        String shareBody,
        PageLink primaryLink,
        List<PageLink> supportingLinks,
        List<PageLink> countyLinks,
        List<SourceRecord> officialSources,
        List<String> sendWhenBullets,
        List<String> recipientChecklist,
        List<String> vendorChecklist
) {
    public WorkflowPacketView {
        supportingLinks = supportingLinks == null ? List.of() : List.copyOf(supportingLinks);
        countyLinks = countyLinks == null ? List.of() : List.copyOf(countyLinks);
        officialSources = officialSources == null ? List.of() : List.copyOf(officialSources);
        sendWhenBullets = sendWhenBullets == null ? List.of() : List.copyOf(sendWhenBullets);
        recipientChecklist = recipientChecklist == null ? List.of() : List.copyOf(recipientChecklist);
        vendorChecklist = vendorChecklist == null ? List.of() : List.copyOf(vendorChecklist);
    }
}
