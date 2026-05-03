#!/usr/bin/env bash
set -euo pipefail
if [ $# -ne 1 ]; then
  echo "Usage: $0 <PORT>"
  exit 1
fi
PORT="$1"

# detect arch -> docker platform
ARCH=$(uname -m)
case "$ARCH" in
  x86_64|amd64) PLATFORM="linux/amd64" ;;
  aarch64|arm64) PLATFORM="linux/arm64" ;;
  *) echo "Unsupported arch: $ARCH"; exit 1 ;;
esac

# ensure buildx builder exists and is ready
docker buildx create --use --name multi-builder >/dev/null 2>&1 || true
docker buildx inspect --bootstrap >/dev/null

# build image for the detected platform and load into local daemon
docker buildx build --platform "$PLATFORM" --load -t myapp:dev .

# export PORT for docker-compose and start
export PORT
docker-compose up --build --force-recreate --scale app=3 -d

echo "App available at http://localhost:${PORT}"
