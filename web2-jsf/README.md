# P03 — Incremento Web 2.0 (JSF + PrimeFaces) · PR10 · Equipo 4

Qué hacer, de qué sacar capturas y dónde ponerlas. Nada más.

Proyecto: PR10 — Gestión de prácticas profesionales · Módulo M03 · Tabla del módulo: `web2_record`.
Datos ficticios. Sin credenciales en el repositorio.

---

## 1. Qué hacer para ejecutarlo

### Ruta A — Docker (recomendada, genera el `M03 VERIFICADO`)

Con Docker Desktop abierto y Git Bash en la carpeta `dsw-evolucion-web`:

```bash
# Verificación oficial (compila, despliega, prueba y limpia solo)
./scripts/verify-module.sh M03      # debe terminar en: M03 VERIFICADO
./scripts/cleanup.sh

# Levantar la app para capturar en el navegador
docker compose --profile web2 up -d --build
# Abre: http://localhost:18082/web2/index.xhtml
# ... toma las capturas ...
docker compose --profile web2 down -v
```

### Ruta B — Local con Tomcat 9 (si no usas Docker)

```bash
# Base de datos (te pedirá la contraseña: postgres)
psql -U postgres -c "CREATE DATABASE dsw02;"
psql -U postgres -d dsw02 -f web2-jsf/db/schema.sql
psql -U postgres -d dsw02 -f web2-jsf/db/seed.sql   # opcional (datos de ejemplo)

# Compilar el WAR
cd web2-jsf
mvn clean package        # genera target/web2.war
```

Copia `target/web2.war` a `TOMCAT_HOME/webapps/`, arranca Tomcat y abre
`http://localhost:8080/web2/index.xhtml`. (Los valores por defecto ya apuntan a
`dsw02` / `postgres` / `postgres`; en Docker el compose inyecta los suyos.)

## 2. Cómo probar (positiva y negativa)

- **Entrada válida:** escribe un título (≥ 3 caracteres), elige un estado y guarda → aparece el mensaje "Postulación registrada y persistida" y la fila se agrega a la tabla.
- **Entrada inválida:** deja el título vacío (o sin estado) y guarda → PrimeFaces muestra el mensaje de validación y **no** inserta nada.
- **Recarga / persistencia:** recarga la página; los registros siguen ahí (vienen de PostgreSQL).

---

## 3. De qué sacar capturas

Nómbralas `P03_EQUIPO_04_01.png`, `P03_EQUIPO_04_02.png`, … en este orden:

1. `M03 VERIFICADO` en la terminal (Ruta A).
2. La tabla del módulo en la base: `psql -U postgres -d dsw02 -c "\d web2_record"`.
3. Vista inicial en el navegador (`/web2/index.xhtml`) con el formulario y la tabla.
4. Entrada **válida**: formulario lleno + mensaje de éxito + fila nueva en la tabla.
5. Entrada **inválida**: mensaje de validación con el formulario sin guardar.
6. Recarga de la vista mostrando que el registro persiste.
7. (Opcional) `ElementoWeb2` visible en la tabla tras la verificación.

## 4. Dónde poner las capturas

1. **En el repositorio**, versionadas:
   ```bash
   mkdir -p docs/evidencias
   # copia aquí P03_EQUIPO_04_*.png
   git add web2-jsf/ docs/evidencias/
   git commit -m "P03 PR10: incremento web2-jsf (JSF/PrimeFaces) + evidencias"
   git push
   ```
2. **En Eminus:** sube la entrega `P03_EQUIPO_04` con esas capturas, el documento y el enlace del repositorio.

---

## Estado de verificación

| Prueba | Estado |
|---|---|
| Compilación de la capa de datos (Java 11) | VERIFICADO |
| Esquema `web2_record` en PostgreSQL 16 (idempotente) | VERIFICADO |
| Contrato M03: `ElementoWeb2` insertado y recuperado por la vista | VERIFICADO |
| `verify-module.sh M03` (Docker, extremo a extremo) | PENDIENTE (ejecutar en el equipo) |
| Recorrido visual JSF/PrimeFaces desplegado en Tomcat | PENDIENTE (tomar capturas) |
