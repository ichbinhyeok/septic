@import com.example.septic.web.StudioCaseStudies.Study
@import com.example.septic.web.StudioProofSelection
@param Study study
@param boolean production = false
@param boolean countyEntry = false
@param boolean directCase = false
@param String imageOverride = ""
@param String imageNote = ""
@param int imageWidth = 1672
@param int imageHeight = 941
<section class="research-proof" id="our-research" aria-labelledby="research-proof-title">
<div class="studio-wrap research-proof-heading"><div><p class="studio-overline">Beyond the records directory</p><h2 id="research-proof-title">See the work<br>behind the answer.</h2></div>@if(!countyEntry)<p>Online clues, focused questions, and a careful reading of what comes back. See how we turn a search into an answer the customer can use.</p>@endif</div>
<div class="studio-wrap research-proof-story">
<a class="research-proof-photo" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/" + study.slug() + "/", production)}" aria-label="Read the investigation in ${study.place()}"><img src="${imageOverride.isBlank() ? "/images/studio/" + study.hero() : imageOverride}" alt="" width="${imageWidth}" height="${imageHeight}" loading="eager" fetchpriority="low" decoding="async">@if(!imageNote.isBlank())<em>${imageNote}</em>@endif<span>Read the investigation <b aria-hidden="true">→</b></span></a>
<div class="research-proof-copy"><p class="studio-overline">Real customer case / ${study.place()}</p><h3><a class="research-proof-title" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/" + study.slug() + "/", production)}">${countyEntry ? StudioProofSelection.question(study.slug()) : study.angle()}</a></h3><p>${StudioProofSelection.teaser(study.slug())}</p>
@if(countyEntry || directCase)
<div class="research-proof-actions"><a class="studio-button research-proof-cta" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/" + study.slug() + "/", production)}">See how we investigated <span aria-hidden="true">→</span></a><a class="studio-text-link" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}">See our investigations <span aria-hidden="true">↗</span></a></div>
@else
<a class="studio-button research-proof-cta" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}">See our investigations <span aria-hidden="true">→</span></a><p class="research-proof-preview-note">Explore real cases: online research, agency follow-through, and answers delivered.</p>
@endif
<p class="studio-fine">This case is from ${study.place()}. It illustrates our approach, not record availability for your property.</p></div>
</div>
</section>
