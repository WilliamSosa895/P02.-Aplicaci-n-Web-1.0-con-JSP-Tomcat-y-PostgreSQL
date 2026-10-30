# P05 — API REST, integración frontend/backend y persistencia (PR10) · Equipo 4

Integración **Angular ↔ Spring Boot (Java 11) ↔ PostgreSQL** del proyecto PR10
(gestión de prácticas profesionales). Continúa P02–P04 sobre el mismo repositorio.
Datos ficticios; sin credenciales en el repositorio.

## 1. Qué ofrece la API (recurso `practica`)

| Método y ruta | Descripción | Respuestas |
|---|---|---|
| `GET /api/practicas` | Lista las prácticas | 200 |
| `GET /api/practicas/{id}` | Consulta una práctica | 200 · 404 |
| `POST /api/practicas` | Registra una práctica (requiere `X-Lab-Key`) | 201 · 400 · 401 |
| `PATCH /api/practicas/{id}/estado` | **Cambia el estado** (requiere `X-Lab-Key`) | 200 · 400 · 401 · 404 |
| `GET /api/practicas/health` | Sonda de salud | 200 (UP) |
| `GET /api/organizaciones`, `GET /api/estudiantes` | Catálogos para el formulario | 200 |

Estados válidos: `POSTULADA`, `ACEPTADA`, `EN_CURSO`, `CONCLUIDA`, `RECHAZADA`.

## 2. Configuración (variables de entorno, sin secretos)

| Variable | Valor por defecto | Uso |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/dsw` | Conexión JDBC |
| `DB_USER` | `dsw` | Usuario |
| `DB_PASSWORD` | `dsw_local` | Contraseña (ficticia) |
| `LAB_API_KEY` | `lab-only-key` | Clave de laboratorio para escrituras |

El esquema y los datos de prueba se aplican con **Flyway** al arrancar
(`V1__esquema_inicial.sql`, `V2__datos_prueba.sql`). `ddl-auto=validate`: JPA no
modifica el esquema, solo lo valida contra las migraciones.

## 3. Ejecución

### Opción A — Docker Compose (recomendada)

```bash
# Verificación oficial del backend + API (compila, levanta, prueba y limpia)
./scripts/verify-module.sh M04
./scripts/cleanup.sh

# Levantar la integración completa (API + Angular + PostgreSQL) para capturar
docker compose --profile web3 up -d --build
# Frontend: http://localhost:18088/      API: http://localhost:18080/api/practicas
docker compose --profile web3 down -v
```

### Opción B — Local

```bash
# 1. PostgreSQL
psql -U postgres -c "CREATE DATABASE dsw;"
# 2. Backend (Flyway crea esquema y datos al arrancar)
cd api-spring
mvn spring-boot:run           # API en http://localhost:8080
# 3. Frontend (otra terminal)
cd frontend-angular
npm ci && npm start           # http://localhost:4200 (proxy a la API)
```

## 4. Pruebas

### Backend (JUnit + MockMvc)

```bash
cd api-spring
mvn -q test        # 10 pruebas: alta, consulta, salud y cambio de estado (positivas y negativas)
```

### Flujo extremo a extremo (curl) — con la API en el puerto 18080 o 8080

```bash
API=http://localhost:18080     # o 8080 en local
curl -s $API/api/practicas/health                       # UP
curl -s $API/api/practicas                              # 200 lista
# Cambio de estado válido (200)
curl -i -X PATCH $API/api/practicas/1/estado -H "X-Lab-Key: lab-only-key" \
  -H "Content-Type: application/json" -d '{"estado":"CONCLUIDA"}'
# Sin clave (401)
curl -i -X PATCH $API/api/practicas/1/estado \
  -H "Content-Type: application/json" -d '{"estado":"ACEPTADA"}'
# Estado inválido (400)
curl -i -X PATCH $API/api/practicas/1/estado -H "X-Lab-Key: lab-only-key" \
  -H "Content-Type: application/json" -d '{"estado":"NO_EXISTE"}'
# Práctica inexistente (404)
curl -i -X PATCH $API/api/practicas/999999/estado -H "X-Lab-Key: lab-only-key" \
  -H "Content-Type: application/json" -d '{"estado":"ACEPTADA"}'
```

## 5. Seguridad (checklist OWASP básico, C09)

- **Validación de entradas:** Bean Validation en el backend y restricciones CHECK en PostgreSQL.
- **Consultas parametrizadas:** acceso por Spring Data JPA (sin concatenación de SQL).
- **Autorización de escrituras:** cabecera `X-Lab-Key`; las lecturas son abiertas en laboratorio.
- **Protección de secretos:** credenciales por variables de entorno, no versionadas.
- **CORS:** restringido a los orígenes del frontend (`4200`, `8088`).
- **Errores sin información sensible:** `ApiExceptionHandler` devuelve JSON uniforme sin trazas.
- **Dependencias:** versiones fijas (Spring Boot 2.7.18, Angular 16, PostgreSQL 16).

## 6. Capturas de evidencia sugeridas

Nómbralas `P05_EQUIPO_04_01.png`, etc., en `docs/Capturas_De_Evidencia_<Nombre>/`:

1. `mvn test` en verde (10 pruebas). 2. Frontend con la tabla de prácticas.
3. Cambio de estado desde Angular (antes/después). 4. `GET /api/practicas` (200).
5. `PATCH .../estado` válido (200). 6. Sin clave (401). 7. Estado inválido (400).
8. Práctica inexistente (404). 9. Estado persistido tras recargar.

## 7. Estado de verificación

| Prueba | Estado |
|---|---|
| Compilación del frontend Angular (`ng build` producción) | VERIFICADO |
| Migraciones Flyway en PostgreSQL 16 (esquema + datos) | VERIFICADO |
| Cambio de estado y rechazo de estado inválido (CHECK) en PostgreSQL | VERIFICADO |
| Compilación/pruebas del backend (`mvn test`) | PENDIENTE (ejecutar en el equipo con Maven) |
| Flujo extremo a extremo con Docker | PENDIENTE (ejecutar en el equipo) |

## 8. Trazabilidad

El recurso `practica` evoluciona desde P04 (diseño) a una integración ejecutable.
El identificador PR10 se conserva para P06–P08. La evidencia individual de cada
integrante se registra en su carpeta bajo `docs/`.
