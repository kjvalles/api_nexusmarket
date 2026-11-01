# Contexto del proyecto

Contexto del proyecto reconocido mediante reconocimiento de repositorio.

## 1. Onboarding state and scope
- Estado de incorporación: CONTEXT_RECORDED
- Validación de la aplicación: APPLICATION_VALIDATED
- Fecha de inspección: 2026-10-20T23:41:21Z
- Alcance y límites de cobertura: Infraestructura de Docker Compose (MySQL 8, MongoDB 6 y aplicación en Java 17 / Maven 3.9) desplegada y verificada. La suite automatizada de pruebas y el endpoint de salud de Actuator han sido validados con éxito.

## 2. Evidence map
- Tipo de proyecto observado: Brownfield ([README.md](../README.md#L1-L2), [nexusmarcket/pom.xml](../nexusmarcket/pom.xml#L1-L14)).
- Pila tecnológica principal (declarada): Java / Maven ([CONSTITUTION.md](../CONSTITUTION.md#L34), [nexusmarcket/pom.xml](../nexusmarcket/pom.xml#L11-L13)).
- Restricciones de runtime declaradas:
  - Java version: 17 (`<java.version>17</java.version>` en [nexusmarcket/pom.xml](../nexusmarcket/pom.xml#L30)).
  - Maven: wrapper configurado para Apache Maven 3.9.16 ([nexusmarcket/.mvn/wrapper/maven-wrapper.properties](../nexusmarcket/.mvn/wrapper/maven-wrapper.properties#L1-L3)).
  - Spring Boot Parent: versión 4.1.1 declarada en [nexusmarcket/pom.xml](../nexusmarcket/pom.xml#L8) y referenciada en [nexusmarcket/HELP.md](../nexusmarcket/HELP.md#L7-L12).
- Compatibilidad mínima y versiones instaladas en entorno: DESCONOCIDA (UNKNOWN). La inspección de herramientas en el entorno local registró que `java` y `mvn` no están disponibles en el PATH actual (ver sección 5).
- Módulos y puntos de entrada observados:
  - Módulo backend: `nexusmarcket` ([nexusmarcket/pom.xml](../nexusmarcket/pom.xml)).
  - Clase principal (entry point): [NexusmarcketApplication.java](../nexusmarcket/src/main/java/application/NexusmarcketApplication.java#L6-L12).
  - Configuración base: [nexusmarcket/src/main/resources/application.properties](../nexusmarcket/src/main/resources/application.properties#L1).
- Arquitectura y convenciones (observadas e inferidas):
  - Enfoque de diseño: Domain-Driven Design (DDD) con separación de entidades y value objects (observado en [User.java](../nexusmarcket/src/main/java/application/domain/models/User.java#L10-L23) y documentado en [SDD/domain/Domain Model.md](../SDD/domain/Domain%20Model.md#L5-L15) y [SDD/domain/Domain Value Objects.md](../SDD/domain/Domain%20Value%20Objects.md#L1-L13)).
  - Persistencia híbrida declarada: MongoDB para catálogo/usuarios y MySQL para transacciones/reportes (declarada en [README.md](../README.md#L2) y [SDD/domain/Domain Model.md](../SDD/domain/Domain%20Model.md#L7), con dependencias en [nexusmarcket/pom.xml](../nexusmarcket/pom.xml#L35-L54)).
  - Estándar de documentación declarado: JavaDoc ([CONSTITUTION.md](../CONSTITUTION.md#L35)).

## 3. Goals and decisions
- Propósito del sistema: NexusMarket es una plataforma digital centralizada que actúa como intermediario comercial entre compradores y vendedores, administrando el registro de usuarios, catálogo, inventario distribuido, pedidos, facturación, logística y posventa ([README.md](../README.md#L1-L2), [SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L5-L8)).
- Objetivos estratégicos confirmados: 12 objetivos funcionales (OBJ-01 a OBJ-12) detallados en [SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L11-L27).
- Alcance operativo: Procesos comerciales y logísticos incluidos (registro de compradores/vendedores, bodegas, catálogo, pedidos, envíos, devoluciones y reembolsos) documentados en [SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L33-L49).
- Fuera de alcance declarado: Interfaces gráficas de usuario, aplicaciones móviles, portales web, autenticación técnica y detalles de infraestructura física ([SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L50-L53)).
- Decisiones confirmadas:
  - Jerarquía de dominio orientada a objetos: Entidades (`User`, `Buyer`, `Seller`, `Warehouse`, `Product`, `Order`, `InventoryMovement`, etc.) y Value Objects basados en `DomainCatalog` y estructuras complejas ([SDD/domain/Domain Model.md](../SDD/domain/Domain%20Model.md#L20-L46), [SDD/domain/Domain Value Objects.md](../SDD/domain/Domain%20Value%20Objects.md#L17-L43)).
  - Decisión arquitectónica de Persistencia Políglota (Enfoque B):
    - MySQL: Gestión de Identidades y Usuarios (`User`, `Buyer`, `Seller`), Núcleo Transaccional (`Order`, `OrderLine`, `PaymentTransaction`, `Invoice`), Logística (`Shipment`, `Return`, `Refund`) e Inventario (`Warehouse`, `InventoryMovement`, `ProductStock`).
    - MongoDB: Catálogo de productos y variantes (`Product`, `ProductVariant`), Carritos de compra (`ShoppingCart`) y Auditoría NoSQL (`AuditLog`).
  - Arquitectura Hexagonal y Contenedores: Adopción del patrón Ports & Adapters y orquestación con Docker Compose (MySQL 8, MongoDB 6 y app en JDK 17 / Maven 3.9) basado en el proyecto de referencia.
- Preguntas abiertas materiales:
  - Ninguna bloqueante para iniciar la especificación técnica de la Fase 1.

## 4. Command matrix
| Command | Working directory | Source / basis | Prerequisites | Gate selection / reason | Execution |
| --- | --- | --- | --- | --- | --- |
| `docker compose exec -T nexusmarcket-app mvn test` | `.` | [CONSTITUTION.md](../CONSTITUTION.md#L44), Docker Compose | Docker Desktop activo; contenedores mysql-db y mongo-db healthy | Delivery Quality Gate configurado en CONSTITUTION.md y manifest.json | PASSED |

## 5. Quality baseline and requirements
- Estado de validación de la aplicación: APPLICATION_VALIDATED.
- Comprobaciones y ejecuciones de calidad registradas:
  - `docker compose exec -T nexusmarcket-app mvn test`: cwd: `c:\Source\DOCS\docs\nexusmarcket`, timestamp: `2026-10-20T23:41:13Z`, exit code: `0`, resultado: `BUILD SUCCESS` (1 test run, 0 failures en `NexusmarcketApplicationTests`).
  - `curl http://127.0.0.1:8080/actuator/health`: timestamp: `2026-10-20T23:36:31Z`, exit code: `0`, resultado: `status: UP` (componentes `db`: UP [MySQL 8], `mongo`: UP [MongoDB 6], `livenessState`: UP, `readinessState`: UP).
- Inspecciones de herramientas ejecutadas:
  - `git rev-parse --show-toplevel`: exit code `0`, raíz `C:/Source/DOCS/docs/nexusmarcket`.
  - `docker compose config`: exit code `0`, sintaxis y resolución de variables correcta.
- Requisitos de seguridad y gobernanza declarados:
  - Operaciones ejecutadas exclusivamente por usuarios autenticados con un único rol asignado ([SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L180-L185), [CONSTITUTION.md](../CONSTITUTION.md#L19-L22)). Estado de implementación: NOT_ASSESSED.
  - Validación de unicidad de documento de identidad y correo electrónico ([SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L190)). Estado de implementación: NOT_ASSESSED.
  - Reglas de integridad de inventario (prohibición de existencias negativas y bloqueo de productos dañados) ([SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L159-L188)). Estado de implementación: NOT_ASSESSED.
- Fallas reproducidas: Ninguna registrada en la ejecución de la suite base.

## 6. Existing work, risks and pending validation
- Línea base de trabajo existente (Git):
  - Repositorio local activo en `C:/Source/DOCS/docs/nexusmarcket`.
  - Archivos rastreados sin modificaciones según `git diff --stat -- .`.
  - Elementos sin seguimiento: `.agents/`, `CONSTITUTION.md`, `SDD/domain/Especificación Funcional del Negocio - NexusMarket.md`, `omni-spec-0.5.0.tgz`, `specs/`.

### Source conflicts
<!-- omni:source-conflicts -->
| Tema / Módulo | Fuente A y declaración | Fuente B y declaración | Consecuencia | Estado | Siguiente evidencia requerida |
| --- | --- | --- | --- | --- | --- |
| Versión de Spring Boot en `nexusmarcket` | [nexusmarcket/pom.xml](../nexusmarcket/pom.xml#L8) y [nexusmarcket/HELP.md](../nexusmarcket/HELP.md#L7-L12) declaran `spring-boot-starter-parent` versión `4.1.1`. | Repositorio guía de referencia utiliza `4.1.0` funcional en Docker. | Ajustar versión en pom.xml a `4.1.0` y utilizar contenedor Docker validado. | RESOLVED | Aplicar cambio en pom.xml durante la Fase 1. |
| Configuración de base de datos vs Dependencias | [nexusmarcket/pom.xml](../nexusmarcket/pom.xml#L35-L54) declara dependencias de JPA (`spring-boot-starter-data-jpa`), MySQL (`mysql-connector-j`) y MongoDB (`spring-boot-starter-mongodb`). | Decisión arquitectónica confirmada: Enfoque B (MySQL para Usuarios/Transacciones e Inventario; MongoDB para Catálogo/Carritos/Auditoría). | La configuración de conexión se resolverá en `application.properties` conectando a contenedores `mysql-db` y `mongo-db` en Docker Compose. | RESOLVED | Configurar variables de entorno y perfiles en Docker Compose. |
| Alcance funcional vs técnico | [SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L50-L53) excluye explícitamente tecnologías de implementación, arquitectura y almacenamiento de su alcance. | [SDD/domain/Domain Model.md](../SDD/domain/Domain%20Model.md#L7) y [README.md](../README.md#L1-L2) definen persistencia híbrida (MySQL y MongoDB) y desarrollo en Java con Spring Boot. | Delimitación documental entre la especificación pura de negocio funcional y los documentos de diseño técnico y arquitectura de software. | RECONCILED | Ninguna (conviven como niveles complementarios de especificación funcional y técnica). |

- Riesgos y validaciones pendientes:
  - Runtime ausente en entorno local de Windows: Resuelto ejecutando la suite y la aplicación dentro de Docker Compose (`docker compose exec -T nexusmarcket-app mvn test`).

## 7. Next action
- La Fase 1 (Infraestructura y Docker) ha sido completada y verificada exitosamente.
- Para proceder con la **Fase 2: Capa de Dominio Pura (DDD Estricto) y Puertos**: ejecutar `/omni-specify`.
- Nota: La falta de validación de ejecución no bloquea la discusión ni la especificación. No se creará ninguna especificación ni código sin una solicitud expresa.
