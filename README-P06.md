# P06 — Web 4.0, IoT, dashboard y datos (PR10) · Equipo 4

Fuente IoT **simulada** → ingesta por **MQTT** → persistencia histórica en **PostgreSQL**
→ **dashboard Angular** con alertas y estado de conexión. Continúa P02–P05 sobre el mismo
repositorio (perfil `web4`). Datos ficticios; sin credenciales ni infraestructura de terceros.

## 1. Arquitectura (recorrido extremo a extremo)

```
simulador (publisher.py / simulate.sh)
      │  publica JSON  {deviceId, variable, unit, value}
      ▼
mosquitto (broker MQTT, topic uv/dsw/telemetry)
      │
      ▼
bridge.py  ──POST /api/telemetry (X-Lab-Key)──►  API Spring Boot (Java 11)
                                                      │  clasifica calidad (Rules) y persiste
                                                      ▼
                                                 PostgreSQL (tabla telemetry, historial)
                                                      ▲
      dashboard Angular (18088) ──GET /latest /alerts /history──┘
```

## 2. Esquema de telemetría

Tabla `telemetry`: `device_id`, `variable`, `unit`, `measurement_value`, `quality`, `captured_at`.
Cumple los campos de R06: **dispositivo, fecha-hora, variable, unidad y valor**. La `quality`
(NORMAL / ALERTA / ANOMALA) la calcula el servidor con reglas deterministas.

**Frecuencia:** el simulador publica cada 3 s (configurable con `--interval`).
**Estructura del mensaje:** `{"deviceId":"sensor-temp-01","variable":"temperatura","unit":"C","value":24.1}`.

## 3. Reglas de análisis y alertas (criterio "Análisis y alertas")

| Variable | NORMAL | ALERTA | ANOMALA (fuera de rango físico) |
|---|---|---|---|
| temperatura (C) | < 30 | ≥ 30 y ≤ 60 | < −10 o > 60 |
| humedad (%) | < 85 | ≥ 85 y ≤ 100 | < 0 o > 100 |

- **Dato anómalo:** se marca `ANOMALA` y el dashboard lo resalta en rojo.
- **Valor físicamente imposible** (fuera de −100..200): la API responde **400** y no persiste.
- **Desconexión:** si un dispositivo no envía lecturas en más de 30 s, el dashboard lo marca
  `DESCONECTADO` (el simulador incluye un escenario que deja de publicar para demostrarlo).

## 4. API REST

| Método y ruta | Descripción | Respuestas |
|---|---|---|
| `POST /api/telemetry` | Ingesta una lectura (requiere `X-Lab-Key`) | 201 · 400 · 401 |
| `GET /api/telemetry` | Historial completo | 200 |
| `GET /api/telemetry/latest` | Último valor por dispositivo + estado de conexión | 200 |
| `GET /api/telemetry/history/{deviceId}` | Historial temporal de un dispositivo | 200 |
| `GET /api/telemetry/alerts` | Lecturas en ALERTA o ANOMALA | 200 |
| `GET /api/telemetry/health` | Sonda de salud | 200 |

## 5. Ejecución (Docker Compose, perfil web4)

```bash
cd codigo/dsw-evolucion-web      # raíz del paquete

# Verificación oficial del módulo
./scripts/verify-module.sh M05
./scripts/cleanup.sh

# Levantar la pila IoT completa (broker + API + PostgreSQL + bridge + dashboard)
docker compose --profile web4 up -d --build
#   Dashboard: http://localhost:18088/      API: http://localhost:18080/api/telemetry/latest

# Generar telemetría:
#   a) secuencia demo reproducible (normal, alerta, anómala, desconexión):
./iot-simulator/simulate.sh
#   b) flujo periódico continuo (requiere paho-mqtt en el host):
MQTT_PORT=11883 python iot-simulator/publisher.py --scenario demo --loop

docker compose --profile web4 down -v
```

## 6. Pruebas

```bash
cd api-spring && mvn -q test     # 8 pruebas: ingesta, clasificación (NORMAL/ALERTA/ANOMALA),
                                 # rechazo sin clave y fuera de rango, latest, alerts, salud
```

Flujo extremo a extremo (con la pila levantada):

```bash
API=http://localhost:18080
curl -s $API/api/telemetry/health                                  # UP
./iot-simulator/simulate.sh                                        # publica la secuencia demo
curl -s $API/api/telemetry/latest   | python -m json.tool          # estado por dispositivo
curl -s $API/api/telemetry/alerts   | python -m json.tool          # alertas (ALERTA/ANOMALA)
# Sin clave (401):
curl -i -X POST $API/api/telemetry -H "Content-Type: application/json" \
  -d '{"deviceId":"x","value":20}'
# Fuera de rango físico (400):
curl -i -X POST $API/api/telemetry -H "X-Lab-Key: lab-only-key" -H "Content-Type: application/json" \
  -d '{"deviceId":"x","value":999}'
```

## 7. Seguridad y alcance

- Ingesta protegida con `X-Lab-Key` (clave de laboratorio por variable de entorno, no es un secreto real).
- Consultas parametrizadas (Spring Data JPA); CORS restringido a los orígenes del frontend.
- Sin datos personales, credenciales ni servicios de terceros; broker MQTT local sin autenticación solo para laboratorio.

## 8. Índice de evidencia (relación con R06)

Nombra las capturas `P06_EQUIPO_04_01.png`, etc., en `docs/Capturas_De_Evidencia_<Nombre>/`:

| # | Evidencia | Criterio R06 |
|---|---|---|
| 1 | Simulador publicando (log `PUBLICADO`) | Integración IoT |
| 2 | `bridge.py` con `MQTT_PERSISTED` (mensajes MQTT) | Eventos/MQTT |
| 3 | Dashboard con tarjetas por dispositivo y estados | Dashboard |
| 4 | Panel de alertas con ALERTA/ANOMALA | Análisis y alertas |
| 5 | Dispositivo marcado DESCONECTADO | Análisis y alertas |
| 6 | `GET /api/telemetry/history/...` o consulta SQL temporal | Historial PostgreSQL |
| 7 | `mvn test` en verde (8 pruebas) | Integración / verificación |

## 9. Estado de verificación

| Comprobación | Estado |
|---|---|
| Compilación del dashboard Angular (build de producción) | VERIFICADO |
| Migración Flyway V3 en PostgreSQL 16 (columnas e índices) | VERIFICADO |
| Consultas de dashboard (latest, alerts, history) en PostgreSQL | VERIFICADO |
| Reglas de clasificación NORMAL/ALERTA/ANOMALA (prueba unitaria) | VERIFICADO |
| Pruebas del backend (`mvn test`) | PENDIENTE (ejecutar en el equipo con Maven) |
| `verify-module.sh M05` y recorrido con Docker | PENDIENTE (ejecutar en el equipo) |
