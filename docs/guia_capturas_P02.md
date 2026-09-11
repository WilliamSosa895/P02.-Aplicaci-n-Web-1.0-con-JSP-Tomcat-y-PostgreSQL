# Guía de capturas — P02 (PR10, Equipo 4)

Estas capturas son la evidencia del recorrido reproducible que pide R02. Tómalas al desplegar el
incremento en tu equipo. Nombra los archivos de forma estable, por ejemplo `P02_EQUIPO_04_01.png`, etc.

| # | Qué capturar | Cómo obtenerla |
|---|---|---|
| 01 | Compilación/empaquetado exitoso | Salida de `mvn clean package` con `BUILD SUCCESS` y `web1.war` generado |
| 02 | Base de datos y esquema | `psql -d dsw02 -c "\dt"` mostrando las 6 tablas |
| 03 | GET inicial | Navegador en `/web1/catalog`: organizaciones y estudiantes sembrados, postulaciones vacío |
| 04 | POST válido (organización) | Registrar una organización y ver que aparece en la tabla |
| 05 | POST válido (postulación) | Postular un estudiante y ver la nueva fila en el catálogo de postulaciones |
| 06 | GET con persistencia | Recargar la página y comprobar que el dato se conserva |
| 07 | Caso negativo | Intentar una postulación duplicada o un nombre < 3 y capturar el error HTTP 400 |
| 08 | Salud del servicio | `/web1/health` mostrando `{"status":"UP","stage":"web1"}` |
| 09 | (Opcional) Verificación Docker | Salida de `./scripts/verify-module.sh M02` terminando en `M02 VERIFICADO` |

Sugerencia: en la bitácora individual registra fecha, comando, resultado e interpretación de cada paso,
como pide M02. Un resultado fallido diagnosticado con honestidad también es evidencia válida.
