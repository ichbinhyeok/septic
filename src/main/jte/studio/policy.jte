@import com.example.septic.web.PageMeta
@param String eyebrow
@param String heading
@param String intro
@param java.util.List<com.example.septic.web.SitePageSection> sections
@param String calloutTitle
@param String calloutBody
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = eyebrow, heroImage = "/images/studio/research-room-v1.webp", pageClass = "studio-policy", pageStyles = "/studio-policy.css", content = @`
<main id="main">
<header class="policy-hero"><img src="/images/studio/research-room-v1.webp" alt="" width="1672" height="941" fetchpriority="high"><div class="studio-wrap"><p class="studio-overline">SepticPath / ${eyebrow}</p><h1>${eyebrow}</h1><p>${intro}</p></div></header>
<div class="policy-layout studio-wrap"><aside><p class="studio-overline">On this page</p><nav aria-label="Policy sections">@for(var section : sections)<a href="${"#" + ("Research findings and public examples".equals(section.title()) ? "research-publication" : "section-" + sections.indexOf(section))}">${section.title()}</a>@endfor</nav></aside>
<article class="policy-copy"><h2 class="policy-intro">${heading}</h2>@for(var section : sections)<section id="${"Research findings and public examples".equals(section.title()) ? "research-publication" : "section-" + sections.indexOf(section)}"><h2>${section.title()}</h2><p>${section.body()}</p>@if(section.bullets() != null && !section.bullets().isEmpty())<ul>@for(var bullet : section.bullets())<li>${bullet}</li>@endfor</ul>@endif</section>@endfor
<section><p class="studio-overline">Important note</p><h2>${calloutTitle}</h2><p>${calloutBody}</p><a class="studio-text-link" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Start a property record investigation ↗</a></section></article></div>
</main>
`)
