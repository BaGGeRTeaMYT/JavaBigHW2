# Build all JARs
Write-Host "Building JARs..." -ForegroundColor Green
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build JARs. Please check the errors above." -ForegroundColor Red
    exit 1
}

Write-Host "JARs built successfully!" -ForegroundColor Green 