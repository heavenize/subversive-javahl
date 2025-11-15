# Build Script for Polarion JavaHL Connector
# This script sets up the build environment and launches Maven build

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Polarion JavaHL 1.14 Connector - Build Script" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Set Java 21 path
$JAVA_21_HOME = "C:\Program Files\Java\jdk-21"

# Verify Java 21 exists
if (-not (Test-Path $JAVA_21_HOME)) {
    Write-Host "ERROR: Java 21 not found at: $JAVA_21_HOME" -ForegroundColor Red
    Write-Host "Looking for alternative Java 21 installations..." -ForegroundColor Yellow
    
    # Try other common locations
    $alternatives = @(
        "C:\Program Files\Eclipse Adoptium\jdk-21*",
        "C:\Program Files\Java\jdk-21*",
        "C:\Program Files\OpenJDK\jdk-21*"
    )
    
    foreach ($alt in $alternatives) {
        $found = Get-Item $alt -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) {
            $JAVA_21_HOME = $found.FullName
            Write-Host "Found Java 21 at: $JAVA_21_HOME" -ForegroundColor Green
            break
        }
    }
    
    if (-not (Test-Path $JAVA_21_HOME)) {
        Write-Host ""
        Write-Host "Please install Java 21 from:" -ForegroundColor Yellow
        Write-Host "  https://adoptium.net/" -ForegroundColor White
        Write-Host "  or" -ForegroundColor White
        Write-Host "  https://www.oracle.com/java/technologies/downloads/" -ForegroundColor White
        exit 1
    }
}

# Set environment variables for this session
$env:JAVA_HOME = $JAVA_21_HOME
$env:PATH = "$JAVA_21_HOME\bin;$env:PATH"

Write-Host "Environment Configuration:" -ForegroundColor Green
Write-Host "  JAVA_HOME: $env:JAVA_HOME" -ForegroundColor White
Write-Host ""

# Verify Java version
Write-Host "Verifying Java version..." -ForegroundColor Yellow
& "$env:JAVA_HOME\bin\java.exe" -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Failed to run Java" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Verify Maven
Write-Host "Verifying Maven installation..." -ForegroundColor Yellow
mvn -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Maven not found. Please install Maven 3.9+" -ForegroundColor Red
    Write-Host "Download from: https://maven.apache.org/download.cgi" -ForegroundColor White
    exit 1
}
Write-Host ""

# Display build options
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Build Options:" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "1. Clean build    - mvn clean package" -ForegroundColor White
Write-Host "2. Quick build    - mvn package" -ForegroundColor White
Write-Host "3. Install        - mvn install" -ForegroundColor White
Write-Host "4. Clean only     - mvn clean" -ForegroundColor White
Write-Host ""

# Ask user for build option
$choice = Read-Host "Select build option (1-4) or press Enter for option 1"
if ([string]::IsNullOrWhiteSpace($choice)) {
    $choice = "1"
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Starting Build..." -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Execute Maven command based on choice
switch ($choice) {
    "1" {
        Write-Host "Running: mvn clean package" -ForegroundColor Green
        mvn clean package
    }
    "2" {
        Write-Host "Running: mvn package" -ForegroundColor Green
        mvn package
    }
    "3" {
        Write-Host "Running: mvn install" -ForegroundColor Green
        mvn install
    }
    "4" {
        Write-Host "Running: mvn clean" -ForegroundColor Green
        mvn clean
    }
    default {
        Write-Host "Invalid option. Running default: mvn clean package" -ForegroundColor Yellow
        mvn clean package
    }
}

$buildResult = $LASTEXITCODE

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
if ($buildResult -eq 0) {
    Write-Host "BUILD SUCCESSFUL!" -ForegroundColor Green
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Output location:" -ForegroundColor Yellow
    Write-Host "  target\org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar" -ForegroundColor White
} else {
    Write-Host "BUILD FAILED!" -ForegroundColor Red
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Check the error messages above for details." -ForegroundColor Yellow
}
Write-Host ""
