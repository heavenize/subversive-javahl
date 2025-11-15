# Diagnostic script to check connector registration in Eclipse

Write-Host "=== Eclipse Connector Diagnostics ===" -ForegroundColor Cyan
Write-Host ""

# Check if Eclipse is running
$eclipseProcess = Get-Process eclipse -ErrorAction SilentlyContinue
if ($eclipseProcess) {
    Write-Host "⚠ Eclipse is running. Please close it first." -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "Checking installed connector..." -ForegroundColor Green
Write-Host ""

# Path to Eclipse plugins directory
$eclipsePlugins = "C:\bin\eclipse-4.37-Java\plugins"

if (Test-Path $eclipsePlugins) {
    Write-Host "📁 Eclipse plugins directory: $eclipsePlugins" -ForegroundColor Cyan
    Write-Host ""
    
    # Find JavaHL connector
    $javahl21 = Get-ChildItem -Path $eclipsePlugins -Filter "*javahl21*" | Select-Object Name, Length, LastWriteTime
    
    if ($javahl21) {
        Write-Host "✅ JavaHL 21 Connector Found:" -ForegroundColor Green
        $javahl21 | Format-Table -AutoSize
    } else {
        Write-Host "❌ JavaHL 21 Connector NOT found in plugins directory" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "All SVN-related plugins:" -ForegroundColor Cyan
    Get-ChildItem -Path $eclipsePlugins -Filter "*svn*" | 
        Select-Object Name, @{Name="Size(KB)";Expression={[math]::Round($_.Length/1KB,1)}}, LastWriteTime |
        Format-Table -AutoSize
}

Write-Host ""
Write-Host "=== Checking plugin.xml ===" -ForegroundColor Green
$pluginXml = ".\org.polarion.eclipse.team.svn.connector.javahl21\plugin.xml"
if (Test-Path $pluginXml) {
    Write-Host "Content of plugin.xml:" -ForegroundColor Cyan
    Get-Content $pluginXml | Write-Host
} else {
    Write-Host "❌ plugin.xml not found" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Checking MANIFEST.MF ===" -ForegroundColor Green
$manifest = ".\org.polarion.eclipse.team.svn.connector.javahl21\META-INF\MANIFEST.MF"
if (Test-Path $manifest) {
    Write-Host "Content of MANIFEST.MF:" -ForegroundColor Cyan
    Get-Content $manifest | Write-Host
} else {
    Write-Host "❌ MANIFEST.MF not found" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Recommendations ===" -ForegroundColor Yellow
Write-Host "1. Verify the connector appears in Eclipse's Extension Registry:" -ForegroundColor White
Write-Host "   - In Eclipse, open the Extension Point view" -ForegroundColor Gray
Write-Host "   - Search for: org.eclipse.team.svn.core.svnconnector" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Check Eclipse error log at:" -ForegroundColor White
Write-Host "   - <workspace>\.metadata\.log" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Try running Eclipse with:" -ForegroundColor White
Write-Host "   eclipse.exe -clean -consoleLog" -ForegroundColor Gray
