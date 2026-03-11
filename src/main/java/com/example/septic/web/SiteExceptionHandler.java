package com.example.septic.web;

import com.example.septic.service.ResearchDataService;
import com.example.septic.service.SeoService;
import com.example.septic.service.UsStateDirectoryService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SiteExceptionHandler {
    private final SeoService seoService;
    private final ResearchDataService researchDataService;
    private final UsStateDirectoryService usStateDirectoryService;

    public SiteExceptionHandler(SeoService seoService, ResearchDataService researchDataService, UsStateDirectoryService usStateDirectoryService) {
        this.seoService = seoService;
        this.researchDataService = researchDataService;
        this.usStateDirectoryService = usStateDirectoryService;
    }

    @ExceptionHandler(StateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleStateNotFound(StateNotFoundException exception, Model model) {
        model.addAttribute("page", seoService.notFound(exception.getMessage()));
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("relatedLinks", relatedLinks(exception.missingPath()));
        return "pages/not-found";
    }

    private List<PageLink> relatedLinks(String missingPath) {
        LinkedHashMap<String, PageLink> links = new LinkedHashMap<>();
        addLink(links, new PageLink("Open the main cost estimator", "/septic-system-cost-calculator/", "Start from the estimator when the exact page path is missing."));
        addLink(links, new PageLink("Browse all live state guides", "/states/", "Use the state directory when you want the closest live guide instead of a dead URL."));

        String normalizedPath = normalizePath(missingPath);
        String contentSlug = targetContentSlug(normalizedPath);
        if (contentSlug != null) {
            researchDataService.findPublicContentPage(contentSlug)
                    .ifPresent(page -> addLink(links, new PageLink(page.title(), "/" + page.slug() + "/", "Open the national overview that matches the missing state-specific page.")));
        }

        String stateSlug = targetStateSlug(normalizedPath);
        if (stateSlug != null) {
            researchDataService.findPublicStateBySlug(stateSlug)
                    .ifPresent(state -> addLink(links, new PageLink(state.stateName() + " septic guide", "/septic-system-cost-calculator/" + state.slug() + "/", "Open the live guide for this state first.")));
            usStateDirectoryService.findBySlug(stateSlug)
                    .ifPresent(state -> addLink(links, new PageLink(state.stateName() + " coverage status", "/septic-system-cost-calculator/" + state.slug() + "/", "Check whether this state already has a live or queued guide.")));
        }

        addLink(links, new PageLink("Drain field estimator", "/drain-field-estimator/", "Use this when the missing page was related to reserve area, wet yard, or drain field failure."));
        addLink(links, new PageLink("Records checklist", "/septic-records-checklist/", "Use this when you need the file path before you trust any estimate or quote."));
        return new ArrayList<>(links.values()).subList(0, Math.min(links.size(), 5));
    }

    private void addLink(LinkedHashMap<String, PageLink> links, PageLink link) {
        links.putIfAbsent(link.path(), link);
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private String targetContentSlug(String path) {
        String[] parts = path.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        return parts.length >= 1 && !parts[0].isBlank() ? parts[0] : null;
    }

    private String targetStateSlug(String path) {
        String[] parts = path.replaceFirst("^/", "").replaceFirst("/$", "").split("/");
        return parts.length >= 2 && !parts[1].isBlank() ? parts[1] : null;
    }
}
