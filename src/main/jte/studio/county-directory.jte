@import com.example.septic.web.PageMeta
@import com.example.septic.web.StudioPreviewController.DirectoryCounty
@import com.example.septic.web.StudioPreviewController.GuideState
@import com.example.septic.web.AssetPaths
@param java.util.List<DirectoryCounty> directoryCounties
@param java.util.List<GuideState> directoryStates
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = "Septic records by county", heroImage = "/images/studio/county-directory-lane-v1.webp", pageClass = "studio-directory", pageStyles = "/studio-directory.css", content = @`
<main id="main">
<section class="directory-hero" aria-labelledby="directory-title">
    <img src="/images/studio/county-directory-lane-v1.webp" alt="Illustrative countryside lane, not a customer property" width="1672" height="941" fetchpriority="high">
    <div class="studio-wrap"><p class="studio-overline">The records directory</p><h1 id="directory-title">Your county.<br>Your next step.</h1><p class="directory-deck">Find the local office. Follow the paper trail.</p>
    <div class="directory-search" role="search"><label for="county-search">County name or state<input id="county-search" type="search" placeholder="Try Morgan, Tennessee or TN" autocomplete="off" aria-controls="county-directory-list"></label><button class="studio-button" type="button" data-directory-find hidden>Find my county <span aria-hidden="true">→</span></button></div>
    <a class="directory-browse" href="#county-directory">Browse the directory ↓</a></div>
</section>
<section class="directory-listing studio-wrap" id="county-directory" aria-labelledby="county-directory-title">
<div class="directory-heading"><div><p class="studio-overline">The county directory</p><h2 id="county-directory-title">A local route,<br> clearly mapped.</h2></div><label>Filter by state<select data-directory-state aria-controls="county-directory-list"><option value="">All states</option>@for(var state : directoryStates)<option value="${state.code()}">${state.name()}</option>@endfor</select></label></div>
    <p class="studio-fine" data-directory-status aria-live="polite">${directoryCounties.size()} published county guides. A guide is not a guarantee that a property record exists.</p>
    <noscript><p>All county guides are listed below. Use your browser’s Find command to locate a county or state.</p></noscript>
    <ul class="directory-counties" id="county-directory-list">@for(var county : directoryCounties)<li data-county-name="${county.name()}" data-county-state="${county.stateName()}" data-county-code="${county.stateCode()}"><a href="${county.path()}"><span>${county.name()}</span><span>${county.stateName()}</span><span aria-hidden="true">↗</span></a></li>@endfor</ul>
    <div class="directory-empty" data-directory-empty hidden><h3>No published guide matches that search.</h3><p>Try a county name or choose another state. Missing from this list does not mean the records don’t exist.</p><a class="studio-text-link" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask about my property ↗</a></div>
    <button class="directory-more" type="button" data-directory-more hidden>Show more counties ↓</button>
    <p class="directory-help">Don’t know the county? <a href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Start with your property address ↗</a></p>
</section>
<section class="directory-story"><img src="/images/studio/research-room-v1.webp" alt="Illustrative research workspace" width="1672" height="941" loading="lazy"><div><p class="studio-overline">Our work</p><h2>An office is only<br>the beginning.</h2><p>See how we follow the clues and interpret the file.</p><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}">See our investigations <span aria-hidden="true">→</span></a><p class="studio-fine">Online research. Agency follow-through. Clear answers, with their limits.</p></div></section>
</main>
<script src="${AssetPaths.versioned("/studio-directory.js")}" defer></script>
`)
