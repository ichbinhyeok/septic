@param String pageClass
@param boolean primaryAction = false
@param boolean production = false
<a href="${production ? "/offer-prep-septic-file-check/" : "/design-preview/studio/service/"}" aria-current="${"studio-service".equals(pageClass) ? "page" : null}">Our service</a>
<a href="${production ? "/septic-record-brief-example/#real-cases" : "/design-preview/studio/work/"}" aria-current="${"studio-work".equals(pageClass) ? "page" : null}">Our work</a>
<a href="${production ? "/septic-records-checklist/" : "/design-preview/studio/guides/"}" aria-current="${"studio-guides".equals(pageClass) ? "page" : null}">Guides</a>
<a class="${primaryAction ? "studio-button" : "studio-nav-inquiry"}" href="${production ? "/offer-prep-septic-file-check/#record-help" : "/design-preview/studio/intake/"}" aria-current="${"studio-intake".equals(pageClass) ? "page" : null}">Ask SepticPath <span aria-hidden="true">↗</span></a>
