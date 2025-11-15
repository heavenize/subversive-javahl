# Test if connector shows up in Eclipse

Write-Host "Creating OSGi console commands to check connector registration..." -ForegroundColor Cyan
Write-Host ""

$commands = @"
# Commands to run in Eclipse OSGi console:
# (Help -> About Eclipse -> Installation Details -> Configuration tab -> scroll to bottom)

1. Check if your bundles are ACTIVE:
   ss org.polarion.eclipse.team.svn.connector.javahl21

   Expected output:
   ACTIVE    org.polarion.eclipse.team.svn.connector.javahl21_7.0.0.202511111037
   RESOLVED  org.polarion.eclipse.team.svn.connector.javahl21.win64_7.0.0.202511111037

2. Check extension registry:
   pt org.eclipse.team.svn.core.svnconnector

   Should list your connector factory class

3. If bundle is INSTALLED (not ACTIVE), start it manually:
   start <bundle-id>

4. Check for errors:
   diag <bundle-id>
"@

Write-Host $commands -ForegroundColor Yellow
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "IMPORTANT: Restart Eclipse with -clean flag" -ForegroundColor Red
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Close Eclipse completely" -ForegroundColor Yellow
Write-Host "2. Open PowerShell or Command Prompt" -ForegroundColor Yellow
Write-Host "3. Navigate to Eclipse directory:" -ForegroundColor Yellow
Write-Host "   cd C:\bin\eclipse-4.37-Java" -ForegroundColor Cyan
Write-Host "4. Run with -clean flag:" -ForegroundColor Yellow  
Write-Host "   .\eclipse.exe -clean" -ForegroundColor Cyan
Write-Host ""
Write-Host "This forces Eclipse to rebuild its plugin registry." -ForegroundColor Green
Write-Host ""
Write-Host "After restart, check:" -ForegroundColor Yellow
Write-Host "  Window -> Preferences -> Team -> SVN -> SVN Connector" -ForegroundColor Cyan
Write-Host ""
