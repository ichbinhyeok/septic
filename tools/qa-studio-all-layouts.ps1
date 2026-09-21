param([string]$BaseUrl='http://localhost:8084')
$ErrorActionPreference='Stop'
$env:BROWSE_SERVER_SCRIPT='C:/Users/tlsgu/.codex/skills/gstack/browse/dist/server-node.mjs'
$browser='C:/Users/tlsgu/.codex/skills/gstack/browse/dist/browse.exe'
$destination=Join-Path (Get-Location) 'build/premium-qa/studio-audit'
New-Item -ItemType Directory -Force $destination | Out-Null
& $browser goto ($BaseUrl+'/design-preview/studio/service/') | Out-Null
& $browser eval tools/audit-studio-coverage.js | Out-Null
$inventory=& $browser js 'JSON.stringify(window.studioCoverage)'
if($LASTEXITCODE -ne 0){throw 'Inventory failed'}
$inventory | Set-Content (Join-Path $destination 'inventory.json') -Encoding utf8
$paths=@(($inventory | ConvertFrom-Json).pages.path | Sort-Object)
$all=@()
for($start=0;$start -lt $paths.Count;$start+=8){
  & $browser goto ($BaseUrl+'/design-preview/studio/service/') | Out-Null
  & $browser eval tools/audit-rendered-layout.js | Out-Null
  $end=[Math]::Min($start+7,$paths.Count-1)
  $batchPaths=ConvertTo-Json -InputObject @($paths[$start..$end]) -Compress
  $batch=& $browser js ("window.runLayoutBatch("+$batchPaths.Replace('"',"'")+")")
  if($LASTEXITCODE -ne 0){throw "Layout failed at $start"}
  $all+=@($batch | ConvertFrom-Json)
  $all | ConvertTo-Json -Depth 8 | Set-Content (Join-Path $destination 'rendered.json') -Encoding utf8
  $issues=@($all | Where-Object {$_.error -or $_.scrollWidth -gt $_.width+3 -or @($_.broken | Where-Object {$_}).Count -gt 0 -or $_.tinyInputs.Count -gt 0})
  Write-Output "$($end+1)/$($paths.Count) routes; $($issues.Count) flagged screens"
}
