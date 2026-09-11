# Acta breve de proyecto — P02

**Experiencia educativa:** Desarrollo de Sistemas Web (DSW-19559)
**Módulo / Actividad:** M02 / P02 — Aplicación Web 1.0 con JSP, Tomcat y PostgreSQL
**Proyecto integrador:** PR10 — Gestión de prácticas profesionales
**Equipo:** Equipo 4
**Integrantes:** William Tehuatle Sosa, Daniel Méndez Ramos, Gael Domínguez García, Alan Toríz Hernández

## Problema y usuarios

El seguimiento de prácticas profesionales se apoya en documentos y comunicaciones dispersas, lo que
dificulta reconocer avances y pendientes. Los usuarios son el estudiante, el asesor y el enlace de
organización simulado. Todos los datos manejados son ficticios.

## Alcance del incremento P02 (Web 1.0)

Catálogo de organizaciones y registro básico de postulaciones con JSP y Servlets, con persistencia en
PostgreSQL mediante JDBC parametrizado, empaquetado como WAR para Tomcat 9. Quedan fuera de este
incremento las capas Web 2.0 a 4.0 (JSF, API/Angular e IoT simulado), que se abordarán en P03–P08.

## Flujo principal y resultado observable

Registrar una organización ficticia, postular a un estudiante ficticio a esa organización y consultar el
catálogo de postulaciones con su estado. El resultado observable es la persistencia del registro,
comprobable con un GET posterior.

## Requisitos funcionales priorizados en P02

- Registro y consulta de organizaciones (catálogo de oportunidades).
- Registro de postulaciones (creación de una práctica que relaciona estudiante y organización).
- Consulta del catálogo de postulaciones con estado.

## Modelo de datos (6 entidades)

`organizacion`, `estudiante_ficticio`, `practica`, `avance`, `evidencia`, `evento_plazo`, con llaves
primarias, llaves foráneas y restricciones de integridad (unicidad y validación de estado y rangos).

## Roles

Estudiante, asesor y enlace de organización simulado (máximo tres roles, sin administración avanzada de
identidad).

## Riesgo principal y control inicial

Riesgo: exposición de expedientes o datos personales. Control: uso exclusivo de identidades sintéticas,
validación de entradas y externalización de credenciales fuera del repositorio.

## Criterios de aceptación considerados

- Desde una base con datos sintéticos se completa el flujo principal y se observa el cambio persistido.
- Un caso inválido se rechaza con mensaje comprensible sin alterar datos.
- Otra persona puede preparar, ejecutar y limpiar el incremento mediante el README.

## Continuidad

El identificador PR10 se conserva en P02–P08. El incremento, sus pruebas y su documentación forman parte
de la trazabilidad del proyecto hasta el cierre del curso.
