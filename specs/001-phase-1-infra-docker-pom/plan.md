# [PLAN-001] Plan Técnico — Fase 1: Infraestructura Base y Docker Compose

## 1. Evidencia y Arquitectura Existente
- **Estado Actual:** El módulo `nexusmarcket` posee un archivo `pom.xml` con Spring Boot `4.1.1` y dependencias básicas. En el host local no existe `java` ni `mvn` configurados en PATH.
- **Referencia Probada:** El proyecto en `C:\Source\DOCS\construccion_de_software_2_2026_2` ejecuta exitosamente con `docker-compose.yml`, orquestando Java 17 / Maven 3.9, MySQL 8.0 y MongoDB 6.0 con `spring-boot-starter-parent` en versión `4.1.0`.
- **Decisión de Persistencia:** Enfoque B (MySQL para datos relacionales / transaccionales y MongoDB para catálogo, carritos y auditoría).

## 2. Cambios Propuestos y Diseño Proporcional
1. **Actualización de `nexusmarcket/pom.xml`**:
   - Reducir la versión de `spring-boot-starter-parent` de `4.1.1` a `4.1.0`.
   - Incorporar librerías de validación (`spring-boot-starter-validation`) y métricas (`spring-boot-starter-actuator`).
   - Agregar dependencias JJWT 0.12.6 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`).
   - Mantener `<java.version>17</java.version>` y configuración de plugins de compilación.
2. **Creación de `nexusmarcket/Dockerfile`**:
   - Basado en `maven:3.9-eclipse-temurin-17`.
   - Directorio de trabajo: `/workspace`.
   - Exposición de puerto HTTP 8080.
   - Comando por defecto: `["mvn", "-B", "spring-boot:run"]`.
3. **Creación de `docker-compose.yml` en la raíz**:
   - `mysql-db`: Imagen `mysql:8.0`, base de datos `nexus_db`, puerto 3306, volumen `nexus_mysql_data`.
   - `mongo-db`: Imagen `mongo:6.0`, base de datos `nexus_catalog_db`, puerto 27017, volumen `nexus_mongo_data`.
   - `nexusmarcket-app`: Construcción desde `./nexusmarcket`, montaje de código local en `/workspace`, volumen de caché `nexus_maven_cache` para descargas rápidas, dependencia condicional de bases de datos `healthy`.
4. **Parametrización de `nexusmarcket/src/main/resources/application.properties`**:
   - Configurar `spring.datasource.url=${DB_URL:...}`.
   - Configurar `spring.mongodb.uri=${MONGODB_URI:...}` (usando prefijo `spring.mongodb.uri` de Spring Boot 4.x).
   - Configurar dialecto `org.hibernate.dialect.MySQLDialect` y `spring.jpa.hibernate.ddl-auto=update`.
   - Configurar `jwt.secret` y `jwt.expiration-ms`.

## 3. Contratos y Compatibilidad (Contracts & Compatibility)
- **Contratos de Red y Puertos:**
  - Puerto 8080: Endpoint HTTP de la aplicación (`nexusmarcket-app`).
  - Puerto 3306: Base de datos relacional MySQL.
  - Puerto 27017: Base de datos NoSQL MongoDB.
- **Variables de Entorno Estándar:**
  - `DB_URL`: `jdbc:mysql://mysql-db:3306/nexus_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
  - `DB_USERNAME`: `nexus_user` (fallback `root`)
  - `DB_PASSWORD`: `nexus_password` (fallback `root_password`)
  - `MONGODB_URI`: `mongodb://mongo-db:27017/nexus_catalog_db`
  - `JWT_SECRET`: Clave HMAC de 256 bits mínima.
- **Compatibilidad de API:**
  - N/A para APIs de negocio en esta fase. Se expone únicamente el contrato estándar de Actuator: `GET /actuator/health` retornando estado HTTP 200 con `{"status":"UP"}`.

## 4. Verificación
- **AC-01:** Inspección estática del archivo `pom.xml` verificando versiones y dependencias agregadas.
- **AC-02:** Inspección estática de `nexusmarcket/Dockerfile`.
- **AC-03:** Inspección estática de `docker-compose.yml` y validación de sintaxis con `docker compose config`.
- **AC-04:** Inspección estática de `application.properties`.
- **AC-05:** Ejecución de `docker compose up -d --build` seguida de consulta HTTP `curl http://localhost:8080/actuator/health` y comprobación de logs.

## 5. Estrategia de Reversión (Rollback)
- Si la compilación dentro del contenedor falla o genera conflictos de dependencias:
  1. Detener contenedores y liberar volúmenes: `docker compose down -v`.
  2. Revertir archivos de configuración mediante control de versiones (`git checkout -- nexusmarcket/pom.xml nexusmarcket/src/main/resources/application.properties`).
  3. Eliminar archivos nuevos (`nexusmarcket/Dockerfile`, `docker-compose.yml`).
