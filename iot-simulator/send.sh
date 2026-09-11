#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEVICE_ID="${DEVICE_ID:-sensor-simulado-01}"
VALUE="${VALUE:-24.5}"
docker compose -f "$ROOT/docker-compose.yml" exec -T mosquitto mosquitto_pub -t uv/dsw/telemetry -m "{\"deviceId\":\"${DEVICE_ID}\",\"value\":${VALUE}}"
