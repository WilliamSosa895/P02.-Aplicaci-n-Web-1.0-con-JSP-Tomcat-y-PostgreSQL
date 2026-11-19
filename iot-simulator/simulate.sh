#!/usr/bin/env bash
# Publica una secuencia demo de telemetria (normal, alerta, anomala) a traves del
# broker mosquitto del compose. No requiere dependencias en el host.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
pub() { docker compose -f "$ROOT/docker-compose.yml" exec -T mosquitto \
  mosquitto_pub -t uv/dsw/telemetry -m "$1"; echo "publicado: $1"; sleep 1; }

pub '{"deviceId":"sensor-temp-01","variable":"temperatura","unit":"C","value":24.1}'
pub '{"deviceId":"sensor-temp-01","variable":"temperatura","unit":"C","value":25.3}'
pub '{"deviceId":"sensor-temp-01","variable":"temperatura","unit":"C","value":31.5}'   # ALERTA
pub '{"deviceId":"sensor-hum-02","variable":"humedad","unit":"%","value":88.0}'        # ALERTA
pub '{"deviceId":"sensor-temp-03","variable":"temperatura","unit":"C","value":85.0}'   # ANOMALA
echo "Listo. sensor-temp-03 no volvera a publicar: en ~30 s el dashboard lo marcara DESCONECTADO."
