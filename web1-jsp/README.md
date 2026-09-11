# P02 — Incremento Web 1.0 (PR10: Gestión de prácticas profesionales)

Experiencia educativa: Desarrollo de Sistemas Web (DSW-19559)
Módulo: M02 — Web 1.0 con JSP, Tomcat y PostgreSQL
Proyecto integrador: **PR10 — Gestión de prácticas profesionales**
Equipo: **Equipo 4**

| Integrante | Matrícula |
|---|---|
| William Tehuatle Sosa | `[MATRÍCULA]` |
| Daniel Méndez Ramos | `[MATRÍCULA]` |
| Gael Domínguez García | `[MATRÍCULA]` |
| Alan Toríz Hernández | `[MATRÍCULA]` |

> Todos los datos son ficticios. No se versionan credenciales, tokens ni datos personales reales.

---

## 1. Qué hace este incremento

Primer incremento Web 1.0 del proyecto PR10. Renderizado del lado servidor con **JSP + Servlets**,
persistencia con **JDBC parametrizado** sobre **PostgreSQL 16**, empaquetado como **WAR** para **Tomcat 9**.

Flujo principal implementado:

1. Registrar una **organización** ficticia que oferta prácticas.
2. Registrar una **postulación** de un **estudiante ficticio** a una organización (crea una `practica`).
3. Consultar el **catálogo de postulaciones** con su estado.

El módulo de datos incluye las **6 entidades** del banco C11 para PR10:
`organizacion`, `estudiante_ficticio`, `practica`, `avance`, `evidencia`, `evento_plazo`.
El incremento Web 1.0 ejercita de forma activa `organizacion`, `estudiante_ficticio` y `practica`;
las tres restantes quedan modeladas con sus llaves y relaciones para los incrementos siguientes.

Roles funcionales del dominio (máximo tres): estudiante, asesor y enlace de organización simulado.

---

## 2. Requisitos

- Java 11
- Maven 3.9.9
- Tomcat 9.0.x (Servlet 4.0.1, espacio de nombres `javax`)
- PostgreSQL 16
- Opcional: Docker + Docker Compose (para `verify-module.sh`)

---

## 3. Configuración (sin secretos en el repositorio)

La conexión se toma de variables de entorno; los valores por defecto sirven para desarrollo local:

| Variable | Valor por defecto (local) | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/dsw02` | URL JDBC |
| `DB_USER` | `postgres` | Usuario |
| `DB_PASSWORD` | `postgres` | Contraseña local (ficticia) |

En el laboratorio con Docker Compose estas variables las inyecta el contenedor y sobreescriben los
valores locales. Ver `.env.example`. **Nunca** se debe subir un `.env` con credenciales reales.

---

## 4. Instalación y despliegue

### Opción A — Local con Tomcat 9 (entorno del equipo)

1. Crear la base de datos y cargar el esquema y los datos de referencia:

   ```bash
   psql -U postgres -c "CREATE DATABASE dsw02;"
   psql -U postgres -d dsw02 -f db/schema.sql
   psql -U postgres -d dsw02 -f db/seed.sql
   ```

   > La aplicación también crea el esquema al arrancar (idempotente), por lo que estos scripts
   > son para revisión y para preparar la base desde consola.

2. Compilar y empaquetar el WAR:

   ```bash
   mvn -q clean package
   # genera target/web1.war
   ```

3. Desplegar en Tomcat 9: copiar `target/web1.war` a `TOMCAT_HOME/webapps/` y arrancar Tomcat.

4. Abrir en el navegador:

   - Aplicación: `http://localhost:8080/web1/catalog`
   - Salud: `http://localhost:8080/web1/health` → `{"status":"UP","stage":"web1"}`

### Opción B — Docker Compose (reproduce la verificación del curso)

Desde `codigo/dsw-evolucion-web`:

```bash
./scripts/verify-module.sh M02
./scripts/cleanup.sh
```

Levanta PostgreSQL 16 y el WAR (puerto **18081**), verifica salud/navegación, hace un POST y comprueba
que el dato persiste con un GET. La app queda en `http://localhost:18081/web1/catalog` mientras corre.

---

## 5. Datos de prueba y recorrido

Al arrancar se siembran 3 organizaciones y 3 estudiantes ficticios. El catálogo de postulaciones inicia
vacío para poder demostrar el flujo completo.

| Paso | Acción | Resultado esperado |
|---|---|---|
| GET inicial | Abrir `/web1/catalog` | Se listan organizaciones y estudiantes; postulaciones vacío |
| POST válido | Registrar organización con nombre ≥ 3 caracteres | Redirige y aparece en la tabla |
| POST válido | Postular estudiante a organización con título ≥ 3 | Aparece en el catálogo de postulaciones |
| GET con persistencia | Recargar `/web1/catalog` | El dato registrado se conserva |

### Casos negativos (validación)

| Caso | Entrada | Respuesta |
|---|---|---|
| Nombre de organización < 3 | `name=ab` | HTTP 400 con mensaje |
| Organización duplicada | nombre repetido | HTTP 400 (restricción `UNIQUE`) |
| Postulación a organización inexistente | `organizacionId` inválido | HTTP 400 (llave foránea) |
| Postulación duplicada | misma org + estudiante + título | HTTP 400 (restricción `UNIQUE`) |
| Título de práctica < 3 | `titulo=ab` | HTTP 400 con mensaje |

---

## 6. Estructura del módulo

```
web1-jsp/
├── pom.xml
├── Dockerfile
├── README.md
├── .env.example
├── db/
│   ├── schema.sql          # 6 entidades, llaves, relaciones y restricciones
│   └── seed.sql            # datos de referencia ficticios
└── src/
    ├── main/java/mx/uv/dsw/web1/
    │   ├── DbConfig.java            # configuración por entorno
    │   ├── Organizacion.java        # dominio
    │   ├── EstudianteFicticio.java  # dominio
    │   ├── Postulacion.java         # dominio (vista de practica)
    │   ├── PracticasRepository.java # JDBC parametrizado + esquema idempotente
    │   ├── CatalogServlet.java      # GET/POST /catalog (flujo principal)
    │   ├── PostulacionServlet.java  # POST /postulacion
    │   └── HealthServlet.java       # GET /health
    ├── main/webapp/
    │   ├── WEB-INF/web.xml
    │   ├── home.jsp                 # redirige la raíz a /catalog
    │   └── index.jsp                # vista (solo presentación)
    └── test/java/mx/uv/dsw/web1/
        └── DbConfigTest.java
```

Separación de responsabilidades: el **JSP** presenta, el **Servlet** coordina y el **Repository**
concentra el JDBC parametrizado. Las reglas de negocio y las restricciones viven en Java y en PostgreSQL,
no en la vista.

---

## 7. Mapeo con la rúbrica R02 (10 puntos)

| Criterio R02 | Puntos | Dónde se evidencia |
|---|---|---|
| Navegación JSP/Servlet y flujo funcional | 2.5 | `CatalogServlet`, `PostulacionServlet`, `index.jsp`, recorrido §5 |
| Despliegue correcto en Tomcat | 2 | §4 (WAR en Tomcat 9 / Docker perfil web1) |
| Persistencia en PostgreSQL | 2.5 | `db/schema.sql`, `PracticasRepository`, operaciones verificables §5 |
| README, capturas y repositorio | 2 | este README + capturas de evidencia + repo del equipo |
| Orden y limpieza técnica | 1 | estructura §6, nombres estables, sin archivos innecesarios |

---

## 8. Estado de verificación

Declarado por prueba (conforme a M02):

| Prueba | Estado | Nota |
|---|---|---|
| Esquema PostgreSQL (6 entidades, llaves, restricciones) | VERIFICADO | Ejecutado en PostgreSQL 16 |
| Compilación Java 11 de las fuentes del módulo | VERIFICADO | `javac` release 11, sin errores |
| Alta de organización (POST válido) y recuperación (GET) | VERIFICADO | Contra PostgreSQL real |
| Casos negativos (nombre corto, duplicado, FK, título corto) | VERIFICADO | Rechazados con HTTP 400 |
| `verify-module.sh M02` (Docker end-to-end) | PENDIENTE | Ejecutar en el equipo con Docker |
| Capturas del flujo desplegado en Tomcat | PENDIENTE | Tomarlas al desplegar (ver `docs/guia_capturas_P02.md`) |

---

## 9. Trazabilidad y autoría

- Identificador de proyecto: **PR10**, confirmado en F02.
- Conservar commits, pruebas y el presente README como parte de la trazabilidad P02–P08.
- La evidencia individual de cada integrante se anexa por separado (bitácora y contribución).
