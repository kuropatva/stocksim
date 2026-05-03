param([Parameter(Mandatory=$true)][int]$Port)

# detect arch
$arch = (Get-CimInstance Win32_Processor).AddressWidth
# AddressWidth is 64 or 32; detect architecture name fallback
$hw = (Get-CimInstance Win32_ComputerSystem).SystemType
if ($hw -match "ARM") { $platform = "linux/arm64" }
elseif ($hw -match "x64" -or $arch -eq 64) { $platform = "linux/amd64" }
else { Write-Error "Unsupported architecture: $hw"; exit 1 }

# create builder if needed
docker buildx create --use --name multi-builder -ErrorAction SilentlyContinue | Out-Null
docker buildx inspect --bootstrap | Out-Null

# build and load
docker buildx build --platform $platform --load -t myapp:dev .

# set env and start compose
$env:PORT = $Port.ToString()
docker-compose up --build --force-recreate

Write-Host "App available at http://localhost:$Port"
