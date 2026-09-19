param([string]$BaseUrl = 'http://localhost:8082', [ValidateSet('desktop','mobile')][string]$Size = 'desktop')
$ErrorActionPreference = 'Stop'
$env:BROWSE_SERVER_SCRIPT = 'C:\Users\tlsgu\.codex\skills\gstack\browse\dist\server-node.mjs'
$browser = 'C:\Users\tlsgu\.codex\skills\gstack\browse\dist\browse.exe'
$routes = [ordered]@{
  home='/'; tdec='/tdec-septic-records/'; florida='/florida-ostds-permit-lookup/'
  nc='/north-carolina-septic-permit-lookup/'; sc='/dhec-septic-permit-lookup/'; texas='/texas-ossf-records-search/'
  intake='/offer-prep-septic-file-check/'; review='/offer-prep-septic-file-check/?mode=review'; cases='/septic-record-brief-example/'
  county='/septic-records-checklist/indiana/howard-county/'; countyAdvanced='/septic-records-checklist/north-carolina/wake-county/'
  stateRecords='/septic-records-checklist/tennessee/'; guide='/septic-system-cost-calculator/georgia/'
  guideComplex='/septic-system-cost-calculator/alabama/'; stateMoney='/septic-inspection-cost/tennessee/'
  directory='/septic-records-by-county/'; index='/septic-records-access-index/'; finder='/septic-record-finder/'; states='/states/'
  article='/buying-a-house-with-a-septic-system/'; builder='/septic-records-request-builder/'
  about='/about/'; privacy='/privacy-policy/'; methodology='/methodology/'; contact='/contact/'
  packet='/for-professionals/records-packet/indiana/'; calculator='/septic-system-cost-calculator/'
  tank='/septic-tank-size-estimator/'; pump='/septic-pump-schedule-estimator/'; drain='/drain-field-estimator/'
  bedroom='/septic-bedroom-permit-checker/'; missing='/premium-design-missing-page/'
}
$destination = Join-Path (Get-Location) 'build/premium-qa/families'
New-Item -ItemType Directory -Force -Path $destination | Out-Null
& $browser viewport $(if ($Size -eq 'desktop') {'1440x1000'} else {'390x844'}) | Out-Null
$results = @()
foreach($entry in $routes.GetEnumerator()) {
  $navigation = & $browser goto ($BaseUrl + $entry.Value)
  & $browser wait --networkidle | Out-Null
  $json = & $browser js "JSON.stringify({family:document.body.className,title:document.title,h1:document.querySelectorAll('h1').length,canonical:document.querySelector('link[rel=canonical]')?.href,robots:document.querySelector('meta[name=robots]')?.content,width:innerWidth,scrollWidth:document.documentElement.scrollWidth,overflow:Array.from(document.querySelectorAll('main *')).filter(e=>{const r=e.getBoundingClientRect();return r.width>0 && r.right>innerWidth+4 && getComputedStyle(e).position!=='absolute'}).slice(0,8).map(e=>e.className)})"
  $metrics = $json | ConvertFrom-Json
  & $browser screenshot --viewport (Join-Path $destination ($entry.Key+'-'+$Size+'.png')) | Out-Null
  $results += [pscustomobject]@{ name=$entry.Key; path=$entry.Value; navigation="$navigation"; metrics=$metrics }
  Write-Output ($entry.Key+' '+$Size+' '+$metrics.family+' overflow='+($metrics.scrollWidth-$metrics.width)+' h1='+$metrics.h1)
}
$results | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath (Join-Path $destination ('audit-'+$Size+'.json')) -Encoding utf8
& $browser console --errors
