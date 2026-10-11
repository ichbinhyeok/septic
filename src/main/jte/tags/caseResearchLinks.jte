@import com.example.septic.web.CaseResearchGuides
<section class="container" aria-labelledby="case-research-links"><p class="eyebrow">Questions our record work helps answer</p><h2 id="case-research-links">Go beyond finding the PDF.</h2><ul>@for(var guide : CaseResearchGuides.ALL)<li><a href="${guide.path()}">${guide.title()} →</a></li>@endfor</ul></section>
