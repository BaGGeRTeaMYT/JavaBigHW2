function Test-PostgresConnection {
    try {
        $env:PGPASSWORD = "postgres"
        $result = & psql -U postgres -h localhost -p 5432 -c "\conninfo" 2>&1
        return $LASTEXITCODE -eq 0
    }
    catch {
        return $false
    }
}

function Initialize-Databases {
    Write-Host "Checking and initializing databases..." -ForegroundColor Yellow
    
    $env:PGPASSWORD = "postgres"
    
    $filesDbExists = & psql -U postgres -h localhost -tAc "SELECT 1 FROM pg_database WHERE datname='textscanner_files'" 2>&1
    $analysisDbExists = & psql -U postgres -h localhost -tAc "SELECT 1 FROM pg_database WHERE datname='textscanner_analysis'" 2>&1
    
    if ($filesDbExists -ne "1" -or $analysisDbExists -ne "1") {
        Write-Host "Creating databases..." -ForegroundColor Yellow
        & psql -U postgres -h localhost -f init-db.sql
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Databases initialized successfully!" -ForegroundColor Green
        }
        else {
            Write-Host "Failed to initialize databases. Please check PostgreSQL installation and permissions." -ForegroundColor Red
            exit 1
        }
    }
    else {
        Write-Host "Databases already exist, continuing..." -ForegroundColor Green
    }
}

Write-Host "Checking PostgreSQL connection..." -ForegroundColor Yellow
if (-not (Test-PostgresConnection)) {
    Write-Host "Error: Cannot connect to PostgreSQL. Please make sure PostgreSQL is running and accessible." -ForegroundColor Red
    Write-Host "Check that:" -ForegroundColor Yellow
    Write-Host "1. PostgreSQL is installed" -ForegroundColor Yellow
    Write-Host "2. PostgreSQL service is running" -ForegroundColor Yellow
    Write-Host "3. PostgreSQL is accessible at localhost:5432" -ForegroundColor Yellow
    Write-Host "4. User 'postgres' with password 'postgres' exists" -ForegroundColor Yellow
    exit 1
}

Initialize-Databases

Write-Host "Building the project..." -ForegroundColor Green
mvn clean install

if ($LASTEXITCODE -eq 0) {
    Write-Host "Build successful! Starting services..." -ForegroundColor Green

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "Write-Host 'Starting API Gateway...' -ForegroundColor Cyan; cd api-gateway; mvn spring-boot:run"
    )

    Start-Sleep -Seconds 5

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "Write-Host 'Starting File Storing Service...' -ForegroundColor Yellow; cd file-storing-service; mvn spring-boot:run"
    )

    Start-Sleep -Seconds 5

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "Write-Host 'Starting File Analysis Service...' -ForegroundColor Magenta; cd file-analysis-service; mvn spring-boot:run"
    )

    Write-Host "`nAll services are starting up!" -ForegroundColor Green
    Write-Host "API Gateway will be available at: http://localhost:8080" -ForegroundColor Cyan
    Write-Host "File Storage Service will be available at: http://localhost:8081" -ForegroundColor Yellow
    Write-Host "File Analysis Service will be available at: http://localhost:8082" -ForegroundColor Magenta
    Write-Host "`nPress Ctrl+C in each window to stop the services." -ForegroundColor Gray
} else {
    Write-Host "Build failed! Please check the errors above." -ForegroundColor Red
} 