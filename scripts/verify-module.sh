#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"; MODULE="${1:-ALL}"
run_maven(){ docker run --rm -v "$ROOT/$1:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-11 mvn -q test package; }
compose(){ docker compose -f "$ROOT/docker-compose.yml" "$@"; }
reset_lab(){ compose --profile "*" down -v --remove-orphans >/dev/null 2>&1 || true; }
wait_url(){ url="$1"; for _ in $(seq 1 60); do if curl -fsS "$url" >/dev/null 2>&1; then return 0; fi; sleep 2; done; echo "No respondio $url" >&2; return 1; }
verify_web1(){ reset_lab; compose --profile web1 up -d --build; wait_url http://localhost:18081/web1/catalog; curl -fsS -X POST -d 'name=ElementoWeb1' http://localhost:18081/web1/catalog >/dev/null; curl -fsS http://localhost:18081/web1/catalog | grep -q ElementoWeb1; reset_lab; }
verify_web2(){ reset_lab; compose --profile web2 up -d --build; wait_url http://localhost:18082/web2/index.xhtml; compose exec -T postgres psql -U dsw -d dsw -c "create table if not exists web2_record(id bigserial primary key,name varchar(100) not null); insert into web2_record(name) values ('ElementoWeb2');" >/dev/null; curl -fsS http://localhost:18082/web2/index.xhtml | grep -q ElementoWeb2; reset_lab; }
verify_web3(){ reset_lab; compose --profile web3 up -d --build; wait_url http://localhost:18080/api/telemetry/health; test "$(curl -sS -o /dev/null -w '%{http_code}' -X POST -H 'Content-Type: application/json' -d '{"deviceId":"sin-clave","value":20}' http://localhost:18080/api/telemetry)" = 401; test "$(curl -sS -o /dev/null -w '%{http_code}' -X POST -H 'X-Lab-Key: lab-only-key' -H 'Content-Type: application/json' -d '{"deviceId":"fuera-rango","value":999}' http://localhost:18080/api/telemetry)" = 400; curl -fsS -X POST -H 'X-Lab-Key: lab-only-key' -H 'Content-Type: application/json' -d '{"deviceId":"web3","value":22.5}' http://localhost:18080/api/telemetry >/dev/null; curl -fsS http://localhost:18080/api/telemetry | grep -q web3; wait_url http://localhost:18088/; reset_lab; }
verify_web4(){ reset_lab; compose --profile web4 up -d --build; wait_url http://localhost:18080/api/telemetry/health; "$ROOT/iot-simulator/send.sh"; for _ in $(seq 1 30); do curl -fsS http://localhost:18080/api/telemetry | grep -q sensor-simulado-01 && break; sleep 2; done; curl -fsS http://localhost:18080/api/telemetry | grep -q sensor-simulado-01; compose logs mqtt-bridge | grep -q MQTT_PERSISTED; wait_url http://localhost:18088/; reset_lab; }
case "$MODULE" in
  M01) test -s "$ROOT/static-web/index.html"; grep -q '<main>' "$ROOT/static-web/index.html" ;;
  M02) run_maven web1-jsp; verify_web1 ;;
  M03) run_maven web2-jsf; verify_web2 ;;
  M04) run_maven api-spring; verify_web3 ;;
  M05) verify_web4 ;;
  M06) test -s "$ROOT/docs/release-checklist.md"; docker compose -f "$ROOT/docker-compose.yml" config >/dev/null; grep -q 'M01-M05' "$ROOT/docs/release-checklist.md" ;;
  ALL) "$0" M01; "$0" M02; "$0" M03; "$0" M04; "$0" M05; "$0" M06 ;;
  *) echo "Use M01..M06 o ALL" >&2; exit 2 ;;
esac
echo "$MODULE VERIFICADO"
