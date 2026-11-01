# [TASKS-001] Tareas de Implementación — Fase 1: Infraestructura y Docker

Lista de tareas accionables derivadas del plan técnico y mapeadas a los criterios de aceptación.

- [x] TASK-01 / AC-01: Actualizar `nexusmarcket/pom.xml` con Spring Boot `4.1.0`, dependencias de JJWT 0.12.6, Jakarta Validation y Spring Boot Actuator.
- [x] TASK-02 / AC-02: Crear `nexusmarcket/Dockerfile` basado en `maven:3.9-eclipse-temurin-17` con configuración de entorno para Spring Boot.
- [x] TASK-03 / AC-03: Crear `docker-compose.yml` en la raíz del repositorio orquestando `nexus-mysql`, `nexus-mongo` y `nexusmarcket-app` con healthchecks y volúmenes persistentes.
- [x] TASK-04 / AC-04: Parametrizar `nexusmarcket/src/main/resources/application.properties` para conexión dinámica a MySQL, MongoDB, dialectos y JWT.
- [x] TASK-05 / AC-05: Ejecutar levantamiento de contenedores con `docker compose up -d --build` y verificar salud con `GET /actuator/health`.
