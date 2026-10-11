package com.example.septic.web;

import com.example.septic.service.SeoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CaseResearchGuideController {
    private final SeoService seo;
    public CaseResearchGuideController(SeoService seo) { this.seo = seo; }
    @GetMapping({"/septic-repair-records", "/septic-repair-records/", "/septic-records-before-pool", "/septic-records-before-pool/", "/septic-final-approval-records", "/septic-final-approval-records/"})
    public String guide(HttpServletRequest request, Model model) {
        String slug = request.getRequestURI().replace("/", "");
        var guide = CaseResearchGuides.ALL.stream().filter(g -> g.slug().equals(slug)).findFirst().orElseThrow();
        var page = seo.basicPage(guide.title() + " | SepticPath", guide.description(), guide.path());
        model.addAttribute("page", new PageMeta(page.title(), page.description(), page.canonicalUrl(), page.robots(),
            seo.absoluteUrl("/images/guides/" + guide.image()), page.breadcrumbs(), page.jsonLdBlocks()));
        model.addAttribute("guide", guide);
        return "pages/case-research-guide";
    }
}
