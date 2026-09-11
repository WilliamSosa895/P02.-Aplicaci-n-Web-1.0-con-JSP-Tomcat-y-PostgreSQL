# Starter acumulativo: Desarrollo de Sistemas Web

Perfil didactico: Java 11, Maven 3.9.9, Tomcat 9, PostgreSQL 16, JSF 2.3,
PrimeFaces 12, Spring Boot 2.7.18, Angular 16 y Node 18. El perfil conserva
compatibilidad con la planeacion aprobada; es una concrecion didactica y no
representa por si mismo el contenido literal del programa oficial B01.

## Modulos

- `static-web`: M01, protocolo HTTP y pagina semantica.
- `web1-jsp`: M02, catalogo JSP/Servlet/JDBC desplegable en Tomcat 9.
- `web2-jsf`: M03, registro JSF/PrimeFaces/JDBC desplegable en Tomcat 9.
- `api-spring`: M04-M06, API REST Java 11 con PostgreSQL, validacion y pruebas.
- `frontend-angular`: M04-M06, cliente Angular del API.
- `iot-simulator`: M05, publicador MQTT y puente MQTT-API sin hardware obligatorio.

Ejecute `./scripts/verify-module.sh MNN`. Los contenedores temporales no usan
credenciales reales. Consulte `docs/verification-report.md` para el estado
comprobado y `./scripts/cleanup.sh` para limpiar el laboratorio.
