package com.example.septic.web;

import com.example.septic.service.ResearchDataService;
import com.example.septic.service.SeoService;
import com.example.septic.service.UsStateDirectoryService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SiteErrorController implements ErrorController {
    private final SeoService seoService;
    private final ResearchDataService researchDataService;
    private final UsStateDirectoryService usStateDirectoryService;

    public SiteErrorController(SeoService seoService, ResearchDataService researchDataService, UsStateDirectoryService usStateDirectoryService) {
        this.seoService = seoService;
        this.researchDataService = researchDataService;
        this.usStateDirectoryService = usStateDirectoryService;
    }

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public String handleError(HttpServletRequest request, Model model) {
        int statusCode = statusCode(request);
        String missingPath = requestPath(request);
        String message = statusCode == HttpStatus.NOT_FOUND.value()
                ? "That page is not live. Use the closest records, permit, estimator, or state path below instead."
                : "This page could not be loaded. Use the closest working path below while the issue is reviewed.";
        return renderNotFound(model, missingPath, message);
    }

    @RequestMapping(
            value = {
                    "/{first:[^.]+}",
                    "/{first:[^.]+}/",
                    "/{first:[^.]+}/{second:[^.]+}",
                    "/{first:[^.]+}/{second:[^.]+}/",
                    "/{first:[^.]+}/{second:[^.]+}/{third:[^.]+}",
                    "/{first:[^.]+}/{second:[^.]+}/{third:[^.]+}/",
                    "/{first:[^.]+}/{second:[^.]+}/{third:[^.]+}/{fourth:[^.]+}",
                    "/{first:[^.]+}/{second:[^.]+}/{third:[^.]+}/{fourth:[^.]+}/"
            },
            method = RequestMethod.GET
    )
    public String handleMissingHtmlPath(HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return renderNotFound(
                model,
                request.getRequestURI(),
                "That page is not live. Use the closest records, permit, estimator, or state path below instead."
        );
    }

    private String renderNotFound(Model model, String missingPath, String message) {
        model.addAttribute("page", seoService.notFound(message));
        model.addAttribute("message", message);
        model.addAttribute("relatedLinks", relatedLinks(missingPath));
        return "pages/not-found";
    }

    private int statusCode(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status instanceof Integer value) {
            return value;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private String requestPath(HttpServletRequest request) {
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (path instanceof String value && !value.isBlank()) {
            return value;
        }
        return request.getRequestURI();
    }

    private List<PageLink> relatedLinks(String missingPath) {
        LinkedHashMap<String, PageLink> links = new LinkedHashMap<>();
        String normalizedPath = normalizePath(missingPath);
        String contentSlug = targetContentSlug(normalizedPath);
        addIntentLinks(links, normalizedPath, contentSlug);

        String stateSlug = targetStateSlug(normalizedPath);
        if (stateSlug != null) {
            researchDataService.findPublicStateBySlug(stateSlug)
                    .ifPresent(state -> addLink(links, new PageLink(state.stateName() + " septic guide", "/septic-system-cost-calculator/" + state.slug() + "/", "Open the live guide for this state first.")));
            usStateDirectoryService.findBySlug(stateSlug)
                    .ifPresent(state -> addLink(links, new PageLink(state.stateName() + " coverage status", "/states/", "Check whether this state has a live guide or is still queued.")));
        }

        if (contentSlug != null) {
            researchDataService.findPublicContentPage(contentSlug)
                    .ifPresent(page -> addLink(links, new PageLink(page.title(), "/" + page.slug() + "/", "Open the national overview that matches the missing path.")));
        }

        addLink(links, new PageLink("Septic Records Lookup", "/septic-records-checklist/", "Use the records path when you need permits, as-builts, or lookup steps first."));
        addLink(links, new PageLink("Open the main cost estimator", "/septic-system-cost-calculator/", "Start from the estimator when the exact page path is missing."));
        addLink(links, new PageLink("Browse all live state guides", "/states/", "Use the state directory when you want the closest live guide instead of a dead URL."));
        return new ArrayList<>(links.values()).subList(0, Math.min(links.size(), 5));
    }

    private void addIntentLinks(LinkedHashMap<String, PageLink> links, String normalizedPath, String contentSlug) {
        String haystack = (normalizedPath + " " + (contentSlug == null ? "" : contentSlug)).toLowerCase();
        if (containsAny(haystack, "drain", "wet-yard", "wet", "reserve", "field")) {
            addLink(links, new PageLink("Drain field estimator", "/drain-field-estimator/", "Use the field-specific estimator when the missing path was about wet yard, reserve area, or drain field failure."));
            addLink(links, new PageLink("Wet Yard Over Septic Drain Field", "/wet-yard-over-septic-drain-field/", "Open the symptom-first guide when the user started from soggy ground, seepage, or odor."));
        }
        if (containsAny(haystack, "perc", "soil", "site-review")) {
            addLink(links, new PageLink("Failed Perc Test for Septic", "/failed-perc-test-septic/", "Use the failed-perc guide when testing or soil limits are driving the estimate."));
            addLink(links, new PageLink("Perc Test Cost", "/perc-test-cost/", "Open the site-review cost guide when the main question is testing scope or failed-site risk."));
        }
        if (containsAny(haystack, "record", "file", "as-built")) {
            addLink(links, new PageLink("Septic Records Lookup", "/septic-records-checklist/", "Use the records path when you need permits, as-builts, or lookup steps first."));
        }
        if (containsAny(haystack, "permit", "approval")) {
            addLink(links, new PageLink("Septic Permit Process", "/septic-permit-process/", "Open the permit guide when the main blocker is approvals, local office routing, or timing."));
        }
        if (containsAny(haystack, "inspect", "inspection")) {
            addLink(links, new PageLink("Septic Inspection Cost", "/septic-inspection-cost/", "Use the inspection guide when the next move is a buyer, lender, or transfer inspection."));
        }
    }

    private boolean containsAny(String haystack, String... needles) {
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
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
