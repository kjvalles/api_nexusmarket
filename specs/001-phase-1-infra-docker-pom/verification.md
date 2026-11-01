# Registro de Evidencias de Verificación

Este documento registra los resultados de la ejecución de pruebas y observaciones de la Fase 1.

| Criterio | Método de Verificación | Estado | Evidencia y Resultados |
| :--- | :---: | :---: | :--- |
| **AC-01** | Inspección estática | Superado | `nexusmarcket/pom.xml` actualizado con Spring Boot `4.1.0`, dependencias JJWT `0.12.6`, `spring-boot-starter-validation` y `spring-boot-starter-actuator`. |
| **AC-02** | Inspección / Docker build | Superado | `nexusmarcket/Dockerfile` construido exitosamente sobre `maven:3.9-eclipse-temurin-17`, exponiendo puerto 8080. |
| **AC-03** | Docker Compose config & up | Superado | `docker-compose.yml` ejecutado; contenedores `nexus-mysql` (puerto 3306), `nexus-mongo` (puerto 27017) y `nexus-app` en estado `Up` y `healthy`. |
| **AC-04** | Inspección / Runtime config | Superado | `nexusmarcket/src/main/resources/application.properties` cargado con cadenas dinámicas de conexión JDBC y Mongo, dialecto MySQL y parámetros JWT. |
| **AC-05** | Ejecución HTTP & Test suite | Superado | `curl.exe http://127.0.0.1:8080/actuator/health` retorna `{"status":"UP"}` con `db: UP` (MySQL) y `mongo: UP`. Comando `docker compose exec nexusmarcket-app mvn test` exitoso: `BUILD SUCCESS` (1 test run, 0 failures). |
