# P03 · Evidencias de Alan (web2-jsf · JSF + PrimeFaces)

Fecha: 25-sep-2026 · Rama: `Alan` · Base de datos: `dsw02` · Tabla: `web2_record`

## Entorno de prueba

- Compilación: Maven con Java 11 (`release 11`) → `web2-jsf/target/web2.war`
- Servidor: Tomcat 9.0.121 · Mojarra 2.3.9 · PrimeFaces 12.0.0 · Weld 3.1.9
- Base: PostgreSQL 16 (Ruta B del README de `web2-jsf`: `dsw02` / `postgres`)
- Nota: la captura 01 es el **mismo contrato** que `verify_web2` de
  `scripts/verify-module.sh M03`, pero corrido sin Docker. La captura oficial de
  `./scripts/verify-module.sh M03` (Ruta A, Docker) debe tomarla quien tenga Docker Desktop.

## Resultado de pruebas (12/12 aprobadas)

| # | Prueba | Resultado |
|---|---|---|
| 1 | La vista inicial carga el formulario y la tabla | PASA |
| 2 | La tabla muestra `ElementoWeb2` (contrato M03) | PASA |
| 3 | Negativa, vacío: "El titulo es obligatorio." | PASA |
| 4 | Negativa, vacío: "Seleccione un estado." | PASA |
| 5 | Negativa, vacío: no inserta en la BD | PASA |
| 6 | Negativa, título de 2 caracteres: validación de longitud | PASA |
| 7 | Negativa, título de 2 caracteres: no inserta en la BD | PASA |
| 8 | Positiva: "Postulacion registrada y persistida." | PASA |
| 9 | Positiva: fila nueva en la tabla (AJAX) | PASA |
| 10 | Positiva: exactamente 1 fila nueva en la BD | PASA |
| 11 | Positiva: el formulario se limpia | PASA |
| 12 | Recarga: el registro persiste | PASA |

Además, `web2-jsf/db/schema.sql` se ejecutó dos veces sin errores (es idempotente).

## Capturas

| Archivo | Contenido |
|---|---|
| `P03_EQUIPO_04_00_compilacion_Alan.png` | Compilación del WAR y arranque de JSF/PrimeFaces en Tomcat |
| `P03_EQUIPO_04_01_Alan.png` | Contrato M03 → `M03 VERIFICADO` (local, sin Docker) |
| `P03_EQUIPO_04_02_Alan.png` | `\d web2_record` y los registros de la tabla |
| `P03_EQUIPO_04_03_Alan.png` | Vista inicial `/web2/index.xhtml` (incluye `ElementoWeb2`) |
| `P03_EQUIPO_04_04a_Alan.png` | Entrada válida: formulario lleno |
| `P03_EQUIPO_04_04b_Alan.png` | Entrada válida: mensaje de éxito y fila nueva |
| `P03_EQUIPO_04_05a_Alan.png` | Entrada inválida: título y estado vacíos |
| `P03_EQUIPO_04_05b_Alan.png` | Entrada inválida: título de 2 caracteres |
| `P03_EQUIPO_04_06_Alan.png` | Recarga: el registro persiste |

## Para el resto del equipo (William, Gael, Daniel)

```bash
git fetch origin
git checkout <tu-rama>
git pull origin Alan          # trae web2-jsf de P03 y estas evidencias
```

Después sigan `web2-jsf/README.md` (Ruta A con Docker o Ruta B con Tomcat) y
guarden sus capturas en `docs/Capturas_De_Evidencia_<Nombre>/P03/` con el nombre
`P03_EQUIPO_04_0N_<Nombre>.png`. Luego hagan commit y push a su rama.
