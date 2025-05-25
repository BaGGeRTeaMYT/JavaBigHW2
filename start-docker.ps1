# Check if Docker is running
$dockerRunning = docker info 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Docker is not running. Please start Docker Desktop and try again." -ForegroundColor Red
    exit 1
}

Write-Host "Building JARs..." -ForegroundColor Green
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build JARs. Please check the errors above." -ForegroundColor Red
    exit 1
}

Write-Host "Starting services with Docker Compose..." -ForegroundColor Green

docker-compose up --build

$Host.UI.RawUI.FlushInputBuffer()
Write-Host "`nStopping services..." -ForegroundColor Yellow
docker-compose down 