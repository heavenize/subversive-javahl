# Build script for creating the update site with Java 21
# Ensures Maven uses the correct Java version for Tycho 4.0.10

$JAVA_HOME_ORIGINAL = $env:JAVA_HOME
$PATH_ORIGINAL = $env:PATH

try {
    # Set Java 21
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    
    Write-Host "Building with Java $((java -version 2>&1)[0])" -ForegroundColor Green
    Write-Host "Maven version: $((mvn -version 2>&1 | Select-Object -First 1))" -ForegroundColor Green
    Write-Host ""
    
    # Clean and build all projects including update site
    Write-Host "Building plugins, feature, and update site..." -ForegroundColor Cyan
    mvn clean package
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "BUILD SUCCESSFUL!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Update site created at:" -ForegroundColor Yellow
        Write-Host "  org.polarion.eclipse.team.svn.connector.javahl21.site\target\repository" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Built artifacts:" -ForegroundColor Yellow
        
        # Show built JARs
        Get-ChildItem -Path ".\org.polarion.eclipse.team.svn.connector.javahl21\target" -Filter *.jar | 
            Select-Object Name, @{Name="Size(KB)";Expression={[math]::Round($_.Length/1KB,1)}} |
            Format-Table -AutoSize
            
        Get-ChildItem -Path ".\org.polarion.eclipse.team.svn.connector.javahl21.win64\target" -Filter *.jar | 
            Select-Object Name, @{Name="Size(KB)";Expression={[math]::Round($_.Length/1KB,1)}} |
            Format-Table -AutoSize
            
        Get-ChildItem -Path ".\org.polarion.eclipse.team.svn.connector.javahl21.feature\target" -Filter *.jar | 
            Select-Object Name, @{Name="Size(KB)";Expression={[math]::Round($_.Length/1KB,1)}} |
            Format-Table -AutoSize
    }
    else {
        Write-Host ""
        Write-Host "BUILD FAILED!" -ForegroundColor Red
        Write-Host "Check the output above for errors" -ForegroundColor Red
    }
}
finally {
    # Restore original environment
    if ($JAVA_HOME_ORIGINAL) {
        $env:JAVA_HOME = $JAVA_HOME_ORIGINAL
    }
    else {
        Remove-Item Env:\JAVA_HOME -ErrorAction SilentlyContinue
    }
    $env:PATH = $PATH_ORIGINAL
}
