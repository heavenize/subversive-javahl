# Diagnostic Script - Check Subversive Installation

Write-Host "Checking Subversive Installation..." -ForegroundColor Cyan
Write-Host ""

# Ask for Eclipse installation path
$eclipsePath = Read-Host "Enter your Eclipse installation path (e.g., C:\Eclipse)"

if (-not (Test-Path $eclipsePath)) {
    Write-Host "Eclipse path not found!" -ForegroundColor Red
    exit 1
}

$pluginsPath = Join-Path $eclipsePath "plugins"

Write-Host "Searching for Subversive plugins..." -ForegroundColor Yellow
Write-Host ""

# Find all Subversive-related plugins
$subversivePlugins = Get-ChildItem -Path $pluginsPath -Filter "*svn*" | 
    Where-Object { $_.Name -like "*team.svn*" -or $_.Name -like "*subversive*" }

if ($subversivePlugins.Count -eq 0) {
    Write-Host "No Subversive plugins found!" -ForegroundColor Red
    Write-Host "Please ensure Subversive is installed from: https://gitlab.eclipse.org/eclipse/subversive/subversive" -ForegroundColor Yellow
    exit 1
}

Write-Host "Found Subversive plugins:" -ForegroundColor Green
$subversivePlugins | ForEach-Object {
    Write-Host "  - $($_.Name)" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "Checking for JavaHL connector..." -ForegroundColor Yellow

$javahlConnectors = Get-ChildItem -Path $pluginsPath -Filter "*javahl*"
if ($javahlConnectors.Count -gt 0) {
    Write-Host "Found JavaHL connectors:" -ForegroundColor Green
    $javahlConnectors | ForEach-Object {
        Write-Host "  - $($_.Name)" -ForegroundColor Cyan
    }
} else {
    Write-Host "No JavaHL connectors found" -ForegroundColor Red
}

Write-Host ""
Write-Host "Checking Subversive core bundle..." -ForegroundColor Yellow

$corePlugin = Get-ChildItem -Path $pluginsPath | 
    Where-Object { $_.Name -match "org\.eclipse\.team\.svn\.core" -or $_.Name -match "subversive.*core" }

if ($corePlugin) {
    Write-Host "Found Subversive core:" -ForegroundColor Green
    $corePlugin | ForEach-Object {
        $name = $_.Name
        Write-Host "  Bundle: $name" -ForegroundColor Cyan
        
        # Try to extract version from filename
        if ($name -match "_(\d+\.\d+\.\d+)") {
            Write-Host "  Version: $($matches[1])" -ForegroundColor Cyan
        }
        
        # Check if it's a JAR and has MANIFEST.MF
        if ($_.Extension -eq ".jar") {
            try {
                Add-Type -AssemblyName System.IO.Compression.FileSystem
                $zip = [System.IO.Compression.ZipFile]::OpenRead($_.FullName)
                $manifest = $zip.Entries | Where-Object { $_.FullName -eq "META-INF/MANIFEST.MF" }
                if ($manifest) {
                    $stream = $manifest.Open()
                    $reader = New-Object System.IO.StreamReader($stream)
                    $content = $reader.ReadToEnd()
                    $reader.Close()
                    $stream.Close()
                    
                    # Extract key info
                    if ($content -match "Bundle-SymbolicName:\s*([^\r\n;]+)") {
                        Write-Host "  Symbolic Name: $($matches[1])" -ForegroundColor Cyan
                    }
                    if ($content -match "Bundle-Version:\s*([^\r\n]+)") {
                        Write-Host "  Bundle Version: $($matches[1])" -ForegroundColor Cyan
                    }
                    if ($content -match "org.eclipse.team.svn.core.svnconnector") {
                        Write-Host "  Has svnconnector extension point: YES" -ForegroundColor Green
                    }
                }
                $zip.Dispose()
            }
            catch {
                Write-Host "  (Could not read manifest)" -ForegroundColor Yellow
            }
        }
    }
} else {
    Write-Host "ERROR: Subversive core bundle not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "This is the problem. Subversive core is required for connectors to work." -ForegroundColor Yellow
    Write-Host "Install Subversive from: https://download.eclipse.org/technology/subversive/updates/release/latest" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Diagnosis Complete" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
