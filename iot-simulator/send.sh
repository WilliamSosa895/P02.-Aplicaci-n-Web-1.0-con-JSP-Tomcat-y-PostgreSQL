#!/usr/bin/env bash
# Publica una lectura unica de telemetria a traves del broker mosquitto del compose.
# Compatible con verify-module.sh M05 (deviceId + value); añade variable y unidad.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEVICE_ID="${DEVICE_ID:-sensor-simulado-01}"
VARIABLE="${VARIABLE:-temperatura}"
UNIT="${UNIT:-C}"
VALUE="${VALUE:-24.5}"
docker compose -f "$ROOT/docker-compose.yml" exec -T mosquitto \
  mosquitto_pub -t uv/dsw/telemetry \
  -m "{\"deviceId\":\"${DEVICE_ID}\",\"variable\":\"${VARIABLE}\",\"unit\":\"${UNIT}\",\"value\":${VALUE}}"
