# [SPEC-001] Fase 1: Infraestructura Base, Dependencias pom.xml y Docker Compose

Fecha: 2026-10-20
Estado: ACTIVO
Tipo: feature

## 0. Fase de Clarificación y Supuestos
- **Decisión de Arquitectura Confirmada:** Se adopta la persistencia políglota Enfoque B (MySQL para identidades/transacciones e inventario; MongoDB para catálogo, carritos y auditoría) documentada en [specs/project-context.md](../../specs/project-context.md).
- **Entorno de Ejecución:** El host local de Windows no cuenta con JDK 17 ni Maven en PATH. La infraestructura de ejecución se estandariza mediante contenedores Docker (Docker Desktop 29.8.0 disponible en el sistema).
- **Alineación de Versiones:** La versión de Spring Boot se ajusta a `4.1.0` en correspondencia con el repositorio de referencia funcional (`construccion_de_software_2_2026_2`), resolviendo la inconsistencia de la versión `4.1.1` inexistente en repositorios públicos.
- **Supuestos Técnicos:**
  - Los puertos estándar 3306 (MySQL), 27017 (MongoDB) y 8080 (App) están disponibles en el host o parametrizables mediante variables de entorno.
  - Las credenciales por defecto son exclusivamente para entornos locales/académicos y se sobrescriben por variables de entorno.

## 1. Need and scope
- **Necesidad:** Proporcionar una plataforma de desarrollo y ejecución reproducible e independiente del host que permita compilar, ejecutar pruebas y desplegar el backend de NexusMarket con sus motores de bases de datos relacional y NoSQL.
- **Alcance (Scope):**
  - Actualización del archivo `nexusmarcket/pom.xml` con Spring Boot 4.1.0 y dependencias de JJWT (0.12.6), Jakarta Validation y Spring Boot Actuator.
  - Creación de `nexusmarcket/Dockerfile` con imagen base `maven:3.9-eclipse-temurin-17`.
  - Creación del archivo `docker-compose.yml` en la raíz del proyecto para orquestar `nexus-mysql` (MySQL 8.0), `nexus-mongo` (MongoDB 6.0) y el contenedor de la aplicación `nexusmarcket-app`.
  - Parametrización de `nexusmarcket/src/main/resources/application.properties` para conexión dinámica a MySQL y MongoDB, dialectos y configuración base de seguridad y puertos.

## 2. Acceptance Criteria and Non-Goals
### Criterios de Aceptación (Acceptance Criteria)
- **AC-01:** El archivo `nexusmarcket/pom.xml` declara `spring-boot-starter-parent` en versión `4.1.0`, incluye `spring-boot-starter-validation`, `spring-boot-starter-actuator`, `io.jsonwebtoken:jjwt-api:0.12.6`, `jjwt-impl` y `jjwt-jackson`, y mantiene Java 17.
- **AC-02:** El archivo `nexusmarcket/Dockerfile` define el entorno de compilación y ejecución basado en `maven:3.9-eclipse-temurin-17`, exponiendo el puerto 8080 y montando el código fuente en `/workspace`.
- **AC-03:** El archivo `docker-compose.yml` define los servicios `mysql-db` (imagen `mysql:8.0`, BD `nexus_db`), `mongo-db` (imagen `mongo:6.0`, BD `nexus_catalog_db`) y `nexusmarcket-app` con enlaces de red, volúmenes de datos persistentes y comprobaciones de salud (`healthcheck`).
- **AC-04:** El archivo `nexusmarcket/src/main/resources/application.properties` expone la configuración de `spring.datasource.*` (MySQL), `spring.mongodb.uri` (MongoDB), `server.port` y parámetros JWT con variables de entorno parametrizadas con valores por defecto.
- **AC-05:** La ejecución de `docker compose up -d --build` levanta los tres contenedores en estado *healthy/running* y la aplicación responde exitosamente en su endpoint de salud `/actuator/health`.

### Fuera de Alcance (Non-Goals)
- No incluye desarrollo de controladores REST del negocio, servicios de aplicación ni lógica de dominio.
- No incluye creación de entidades JPA, repositorios ni documentos Mongo de los modelos de negocio (reservado para fases posteriores).
- No incluye despliegues a entornos de nube ni canalizaciones de CI/CD externas.

## 3. Domain applicability
| Security & Compliance Control | Required? | Technical Enforcement |
| :--- | :---: | :--- |
| **¿Accede o procesa datos sensibles (PII)?** | NO | N/A justificado. Esta fase solo establece contenedores e infraestructura de compilación. No maneja registros de usuarios reales. |
| **¿Expone datos cross-tenant?** | NO | N/A. NexusMarket es una plataforma unificada sin separación multi-inquilino a nivel de base de datos. |
| **¿Requiere audit logging en esta fase?** | NO | N/A justificado. La infraestructura de MongoDB queda lista para la colección `audit_logs`, pero la emisión de eventos de auditoría se implementará en fases de lógica de negocio. |
| **Gestión de Secretos en Contenedores** | SÍ | Las contraseñas de bases de datos y la clave `JWT_SECRET` se parametrizan mediante variables de entorno en `docker-compose.yml` con valores por defecto locales para desarrollo. |

## 4. Experiencia y límites
- **Interfaces de usuario:** N/A justificado. Esta especificación es estrictamente de infraestructura de backend.
- **Estados de servicio:** Los contenedores cuentan con healthchecks explícitos (`mysqladmin ping`, `mongosh ping`) para garantizar que la aplicación no intente iniciar antes de que las bases de datos estén listas para aceptar conexiones.

## 5. Traceability
- **AC-01** -> Plan Sección 2 -> `TASK-01` en `tasks.md`
- **AC-02** -> Plan Sección 2 -> `TASK-02` en `tasks.md`
- **AC-03** -> Plan Sección 2 -> `TASK-03` en `tasks.md`
- **AC-04** -> Plan Sección 2 -> `TASK-04` en `tasks.md`
- **AC-05** -> Plan Sección 4 -> `TASK-05` en `tasks.md`
