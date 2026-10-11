package com.example.septic.web;

import com.example.septic.service.SeoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class RecordWorkStoryController {
    private final SeoService seo;
    public RecordWorkStoryController(SeoService seo) { this.seo = seo; }

    @GetMapping({"/septic-record-brief-example/{slug}", "/septic-record-brief-example/{slug}/"})
    public String story(@PathVariable String slug, Model model) {
        var story = RecordWorkStories.ALL.stream().filter(item -> item.slug().equals(slug))
            .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var page = seo.basicPage(story.title() + " | SepticPath", story.description(), story.path());
        model.addAttribute("page", new PageMeta(page.title(), page.description(), page.canonicalUrl(), page.robots(),
            seo.absoluteUrl("/images/work/" + story.image()), page.breadcrumbs(), page.jsonLdBlocks()));
        model.addAttribute("story", story);
        model.addAttribute("stories", RecordWorkStories.ALL);
        return "pages/record-work-story";
    }
}
