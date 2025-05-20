$currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
$isAdmin = (New-Object Security.Principal.WindowsPrincipal $currentUser).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if ($isAdmin) {
    Write-Host "Don't run this script as Administrator."
    Write-Host "When we build the project, we cannot be Administrator."
    Write-Host "But Docker will need elevation, and we'll ask you for that later."
    exit 1
}

Write-Host "Building projects..."

cd vote-challenge
./gradlew.bat clean build
cd ..

cd agenda-processor
./gradlew.bat clean build
cd ..

cd vote-visualizer
./gradlew.bat clean build
cd ..

Write-Host "Projects built successfully."
Write-Host "Now we'll build and start Docker containers (you might be prompted for admin rights)..."

Start-Process "docker-compose" "-f docker-compose.yml up --build" -Verb RunAs