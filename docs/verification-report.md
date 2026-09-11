# Reporte de verificacion tecnica

**Fecha:** 7 de agosto de 2026  
**Entorno:** Docker Compose y contenedores temporales  
**Responsable academico:** Dr. Gabriel Rodriguez Vasquez

## Resultado final

| Modulo | Comprobacion ejecutada | Estado |
|---|---|---|
| M01 | HTML semantico y estructura del sitio estatico | VERIFICADO |
| M02 | Java 11, WAR JSP/Servlet, Tomcat 9, POST/GET JDBC y PostgreSQL 16 | VERIFICADO |
| M03 | WAR JSF/PrimeFaces, Tomcat 9, Weld CDI, tabla y PostgreSQL 16 | VERIFICADO |
| M04 | Pruebas Spring, 401/400/201, PostgreSQL real, Angular y Nginx | VERIFICADO |
| M05 | MQTT, puente MQTT-API, clave, PostgreSQL, GET y dashboard periodico | VERIFICADO |
| M06 | Contrato Compose y lista de liberacion M01-M05 | VERIFICADO; defensa humana NO_APLICA a automatizacion |

## Recorridos comprobados

1. M02 inserta `ElementoWeb1` mediante Servlet y lo recupera desde PostgreSQL en JSP.
2. M03 recupera `ElementoWeb2` desde PostgreSQL y lo presenta en la tabla PrimeFaces.
3. M04 rechaza POST sin clave con 401, rechaza rango invalido con 400 y persiste una solicitud valida con 201.
4. M05 publica `sensor-simulado-01` por MQTT, registra `MQTT_PERSISTED` y recupera el dato mediante la API.
5. Los perfiles Compose se destruyen con volumen y recursos locales al cerrar cada prueba.

## Comandos

```bash
./scripts/verify-module.sh M01
./scripts/verify-module.sh M02
./scripts/verify-module.sh M03
./scripts/verify-module.sh M04
./scripts/verify-module.sh M05
./scripts/verify-module.sh M06
```

`verify-module.sh ALL` invoca M01-M06; ya no sustituye M05/M06 con la compilacion de M04.

## Hallazgos corregidos durante la prueba

1. Se cambiaron los puertos externos PostgreSQL/MQTT a 15432/11883 para no interferir con instalaciones locales.
2. El driver JDBC se carga explicitamente en los WAR de laboratorio.
3. JSF 2.3 incorpora Weld porque Tomcat puro no proporciona CDI BeanManager.
4. Cada perfil se elimina con `--profile "*"` para evitar contenedores residuales.
5. Angular 16 conserva advertencias de dependencias obsoletas; se mantiene solo por compatibilidad con la planeacion Java 11/Node 18.

## Recuperacion

```bash
./scripts/cleanup.sh
```

La limpieza afecta exclusivamente contenedores, volumenes y artefactos locales declarados por el starter.
