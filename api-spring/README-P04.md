# P04 — Arquitectura Web 3.0 (PR10) · Equipo 4

Esqueleto compilable del backend (Spring Boot, Java 11) y contrato de la API.
P04 entrega **arquitectura + contrato + esqueleto**; la integración ejecutable
Angular–API–PostgreSQL es **P05**. Datos ficticios; sin credenciales en el repo.

## Modelo (6 entidades, coherente con P02)
`organizacion`, `estudiante_ficticio`, `practica`, `avance`, `evidencia`, `evento_plazo`,
relacionadas por llaves foráneas. La `practica` es el recurso principal y referencia a
`organizacion` y `estudiante_ficticio`. Modelo completo: `db/schema.sql`.
El recurso de referencia `telemetry` se conserva intacto para no romper la verificación.

## Contrato
- `GET  /api/practicas` → 200 (lista JSON)
- `POST /api/practicas` → 201 (creada) · 400 (inválida) · 401 (sin cabecera `X-Lab-Key`)
- `GET  /api/practicas/health` → 200 (UP)
- `GET  /api/organizaciones` → 200 (catálogo)
- `GET  /api/estudiantes` → 200 (catálogo)

La creación referencia a la organización y al estudiante por id (`organizacionId`,
`estudianteId`). La integridad referencial (FK) se define en `db/schema.sql`.
Contrato completo: `api-spring/openapi.yaml`.

## Compilar y probar (Java 11 + Maven)
```bash
cd api-spring
mvn -q test package        # ejecuta pruebas (telemetry y practica) y genera el jar
```

## Verificación del stack (Docker, opcional)
```bash
# desde dsw-evolucion-web
./scripts/verify-module.sh M04
./scripts/cleanup.sh
```

## Frontend
`frontend-angular` adapta el cliente para listar prácticas y capturarlas con menús de
organización y estudiante (poblados desde los catálogos), con manejo de errores 400/401.
Compila con Node 18 / Angular 16 (`npm ci && npm run build`).

## Capturas sugeridas (evidencia)
1. Diagrama de arquitectura. 2. Diagrama entidad-relación (6 entidades). 3. `mvn test` en verde.
4. `GET /api/practicas` (200). 5. `POST` sin clave → 401. 6. `POST` con estado inválido → 400.
7. `POST` válido → 201. Nómbralas `P04_EQUIPO_04_01.png`, etc., en `docs/evidencias/`.

## Estado de verificación
| Prueba | Estado |
|---|---|
| Modelo PostgreSQL de 6 entidades con FKs (idempotente, restricciones) | VERIFICADO |
| Contrato OpenAPI (YAML válido) | VERIFICADO |
| Compilación/pruebas Spring (`mvn test`) | PENDIENTE (ejecutar en el equipo con Maven) |
| `verify-module.sh M04` (Docker) | PENDIENTE (P05 lo ejercita a fondo) |
