# Check Eclipse OSGi Console for Bundle Status

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "Eclipse Bundle Diagnostic Script" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "STEP 1: Check if connector is installed" -ForegroundColor Yellow
Write-Host "In Eclipse, open the OSGi Console:" -ForegroundColor White
Write-Host "  Window -> Show View -> Other -> OSGi Console" -ForegroundColor Cyan
Write-Host ""
Write-Host "If OSGi Console is not available:" -ForegroundColor White
Write-Host "  Help -> About Eclipse -> Installation Details -> Configuration tab" -ForegroundColor Cyan
Write-Host "  Search for 'javahl21' in the text" -ForegroundColor Cyan
Write-Host ""

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "STEP 2: In OSGi Console, run these commands:" -ForegroundColor Yellow
Write-Host ""
Write-Host "Command 1: Check bundle status" -ForegroundColor White
Write-Host "  ss javahl21" -ForegroundColor Cyan
Write-Host ""
Write-Host "Expected output:" -ForegroundColor White
Write-Host "  <id>  ACTIVE      org.polarion.eclipse.team.svn.connector.javahl21_7.0.0.202511111049" -ForegroundColor Green
Write-Host "  <id>  RESOLVED    org.polarion.eclipse.team.svn.connector.javahl21.win64_7.0.0.202511111049" -ForegroundColor Green
Write-Host ""
Write-Host "If status is INSTALLED (not ACTIVE):" -ForegroundColor Red
Write-Host "  start <bundle-id>" -ForegroundColor Cyan
Write-Host ""

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "STEP 3: Check for errors" -ForegroundColor Yellow
Write-Host "  diag <bundle-id>" -ForegroundColor Cyan
Write-Host ""
Write-Host "This will show why the bundle failed to start" -ForegroundColor White
Write-Host ""

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "STEP 4: Check Error Log" -ForegroundColor Yellow
Write-Host "  Window -> Show View -> Error Log" -ForegroundColor Cyan
Write-Host ""
Write-Host "Look for errors containing:" -ForegroundColor White
Write-Host "  - 'javahl'" -ForegroundColor Cyan
Write-Host "  - 'UnsatisfiedLinkError'" -ForegroundColor Cyan
Write-Host "  - 'ClassNotFoundException'" -ForegroundColor Cyan
Write-Host ""

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "STEP 5: Verify extension point" -ForegroundColor Yellow
Write-Host "In OSGi Console:" -ForegroundColor White
Write-Host "  pt org.eclipse.team.svn.core.svnconnector" -ForegroundColor Cyan
Write-Host ""
Write-Host "Should show:" -ForegroundColor White
Write-Host "  org.polarion.team.svn.connector.javahl.JavaHLConnectorFactory" -ForegroundColor Green
Write-Host ""

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "ALTERNATIVE: Check configuration directly" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. In Eclipse: Help -> About Eclipse -> Installation Details" -ForegroundColor White
Write-Host "2. Configuration tab" -ForegroundColor White
Write-Host "3. Search (Ctrl+F) for: 'javahl21'" -ForegroundColor Cyan
Write-Host ""
Write-Host "You should see both bundles listed with version 7.0.0.202511111049" -ForegroundColor White
Write-Host ""

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Please run these checks and report back:" -ForegroundColor Yellow
Write-Host "1. Bundle status (ACTIVE/RESOLVED/INSTALLED)" -ForegroundColor White
Write-Host "2. Any error messages from 'diag' command" -ForegroundColor White
Write-Host "3. Any errors in Error Log" -ForegroundColor White
Write-Host ""
