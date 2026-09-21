param([string]$BaseUrl='http://localhost:8082')
$ErrorActionPreference='Stop'
$env:BROWSE_SERVER_SCRIPT='C:\Users\tlsgu\.codex\skills\gstack\browse\dist\server-node.mjs'
$browser='C:\Users\tlsgu\.codex\skills\gstack\browse\dist\browse.exe'
$destination=Join-Path (Get-Location) 'build/premium-qa/full-audit'
$inventory=Get-Content (Join-Path $destination 'inventory.json') -Raw | ConvertFrom-Json
& $browser goto ($BaseUrl+'/about/') | Out-Null
& $browser eval tools/audit-rendered-layout.js | Out-Null
$all=@()
for($start=0;$start -lt $inventory.Count;$start+=8) {
  $end=[Math]::Min($start+7,$inventory.Count-1)
  $paths=@($inventory[$start..$end] | ForEach-Object {$_.path}) | ConvertTo-Json -Compress
  $paths=$paths.Replace('"',"'")
  $batch=& $browser js "window.runLayoutBatch($paths)"
  if($LASTEXITCODE -ne 0) {throw "Browser audit failed at $start"}
  $parsed=$batch | ConvertFrom-Json
  if($parsed.PSObject.Properties.Name -contains 'value') {$parsed=$parsed.value}
  $all+=@($parsed)
  $all | ConvertTo-Json -Depth 8 | Set-Content (Join-Path $destination 'rendered.json') -Encoding utf8
  $issues=@($all | Where-Object {$_.error -or $_.scrollWidth -gt $_.width+3 -or @($_.broken | Where-Object {$_}).Count -gt 0 -or $_.tinyInputs.Count -gt 0})
  Write-Output "Audited $($end+1)/$($inventory.Count) routes at two viewports; $($issues.Count) flagged screens"
}
