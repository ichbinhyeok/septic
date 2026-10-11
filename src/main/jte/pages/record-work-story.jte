@import com.example.septic.web.PageMeta
@import com.example.septic.web.RecordWorkStories
@import com.example.septic.web.RecordHelpEntry
@import com.example.septic.web.AssetPaths
@param PageMeta page
@param RecordWorkStories.Story story
@param java.util.List<RecordWorkStories.Story> stories
@template.layouts.app(page = page, designFamily = "editorial", pageStyles = "/record-work-story.css", content = @`
<article class="work-story">
    <header class="work-story__header container">
        <a class="work-story__back" href="/septic-record-brief-example/">Our work ↗</a>
        <p class="eyebrow">A completed record investigation · ${story.region()}</p>
        <h1>${story.title()}</h1>
        <p class="work-story__lede">${story.takeaway()}</p>
        <a class="text-button" href="#investigation">See how we worked ↓</a>
    </header>
    <figure class="work-story__visual">
        <a href="${AssetPaths.versioned("/images/work/" + story.image())}" target="_blank" rel="noopener" aria-label="Open reconstructed research explanation at full size">
            <img src="${AssetPaths.versioned("/images/work/" + story.image())}" alt="Reconstructed SepticPath research notes: ${story.takeaway()} Not an agency original or property plan." width="1536" height="1024" fetchpriority="high">
        </a>
        <figcaption class="container">Reconstructed explanation of real case findings. Original documents and identifying property details are not reproduced; diagrams are conceptual.</figcaption>
    </figure>
    <section class="work-story__investigation container" id="investigation" aria-labelledby="investigation-title">
        <div class="work-story__sticky"><p class="eyebrow">The work behind the answer</p><h2 id="investigation-title">Find. Match.<br>Make sense of it.</h2><p>Each step follows this file's evidence—not a promise of the same result at every address.</p></div>
        <ol>@for(int i=0; i<story.steps().size(); i++)<li><span aria-hidden="true">0${i+1}</span><div><h3>${story.steps().get(i).label()}</h3><p>${story.steps().get(i).text()}</p></div></li>@endfor</ol>
    </section>
    <section class="work-story__findings container" aria-labelledby="findings-title">
        <p class="eyebrow">Source-linked findings</p><h2 id="findings-title">What the records answered.</h2>
        <dl>@for(var finding : story.findings())<div><dt>${finding.label()}</dt><dd>${finding.text()}</dd></div>@endfor</dl>
        <p class="work-story__source">Records route: <a href="${story.sourceUrl()}" target="_blank" rel="noopener">${story.sourceName()} ↗</a>. This is the general official route, not a link exposing the customer's file.</p>
    </section>
    <section class="work-story__handoff"><div class="container"><div><p class="eyebrow">Delivered to the customer</p><h2>Originals.<br>A useful explanation.</h2><p>${story.delivered()}</p></div><aside><h3>What still needs local confirmation</h3><p>${story.limit()}</p></aside></div></section>
    <section class="work-story__next container" aria-labelledby="next-title"><div><p class="eyebrow">Your file. Your question.</p><h2 id="next-title">Let us connect the pages.</h2><p>Already have documents? Share them for a free explanation. Still missing the file? Start with the address.</p></div><div><a class="button button--primary" href="${RecordHelpEntry.of("review").link(story.path())}" data-record-help-cta data-track-click="nav" data-track-source-context="work_story_review" data-track-target-type="lead_form">Help me understand my file</a><a class="text-button" href="${RecordHelpEntry.of("missing").link(story.path())}" data-record-help-cta data-track-click="nav" data-track-source-context="work_story_missing" data-track-target-type="lead_form">Find my missing records →</a><small>Research and explanation are free. Any agency fee needs your approval.</small></div></section>
    <nav class="work-story__related container" aria-label="Related investigations"><h2>More ways we help.</h2>@for(var other : stories)@if(!other.slug().equals(story.slug()))<a href="${other.path()}">${other.title()} ↗</a>@endif@endfor<a href="${story.guideUrl()}">${story.guideLabel()} →</a></nav>
</article>
`)
