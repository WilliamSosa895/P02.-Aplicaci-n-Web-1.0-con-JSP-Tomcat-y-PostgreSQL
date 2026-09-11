#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
docker compose -f "$ROOT/docker-compose.yml" --profile "*" down -v --remove-orphans 2>/dev/null || true
rm -rf "$ROOT/web1-jsp/target" "$ROOT/web2-jsf/target" "$ROOT/api-spring/target" "$ROOT/frontend-angular/dist" "$ROOT/frontend-angular/node_modules"
echo "Laboratorio limpio"
