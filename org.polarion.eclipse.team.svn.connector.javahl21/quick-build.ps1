# Quick Build Script - Just runs the build with Java 21

$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "Building with Java 21..." -ForegroundColor Green
Write-Host ""

mvn clean package

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Build completed successfully!" -ForegroundColor Green
    Write-Host "JAR location: target\org.polarion.eclipse.team.svn.connector.javahl21-7.0.0-SNAPSHOT.jar" -ForegroundColor Cyan
}
