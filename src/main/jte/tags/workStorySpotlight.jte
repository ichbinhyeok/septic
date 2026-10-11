@import com.example.septic.web.RecordWorkStories
@import com.example.septic.web.AssetPaths
@param String slug = "davidson"
@param String sourcePath = "/how-to-read-septic-as-built/"
@for(var story : RecordWorkStories.ALL)
@if(story.slug().equals(slug))
<link rel="stylesheet" href="${AssetPaths.versioned("/work-story-spotlight.css")}">
<section class="work-spotlight" aria-label="A real record investigation">
    <a class="work-spotlight__image" href="${story.path()}" data-track-click="nav" data-track-source-context="work_spotlight" data-track-target-type="case_study"><img src="${AssetPaths.versioned("/images/work/" + story.image())}" alt="Reconstructed research notes illustrating this completed investigation, not an original agency record." width="1536" height="1024" loading="lazy" decoding="async"></a>
    <div><span class="eyebrow">Inside a completed investigation</span><h2>${story.title()}</h2><p>${story.takeaway()}</p><a href="${story.path()}" data-track-click="nav" data-track-source-context="work_spotlight" data-track-target-type="case_study">See the search, source checks and delivered answer →</a><small>Real findings · Reconstructed visual · Identifying details omitted</small></div>
</section>
@endif
@endfor
