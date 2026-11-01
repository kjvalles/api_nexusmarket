# [SPEC-002] Fase 2: Capa de Dominio Pura (DDD Estricto), Servicios y Puertos

Fecha: 2026-10-20
Estado: ACTIVO
Tipo: feature

## 0. Fase de Clarificación y Supuestos
- **Alineación Arquitectónica:** Se implementa un Domain-Driven Design (DDD) estricto y desacoplado, siguiendo el patrón de arquitectura hexagonal validado en el repositorio de referencia (`construccion_de_software_2_2026_2`).
- **Pureza del Dominio:** La capa de dominio (`application.domain.*`) no debe tener ninguna dependencia de Spring Framework, Spring Data, JPA, MongoDB, Jackson o HTTP. Solo se permite el uso de Java Standard Library (Java 17) y anotaciones auxiliares mínimas de Lombok (`@Getter`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`).
- **Invariantes Críticas Confirmadas:**
  - El inventario físico no puede tener existencias negativas bajo ninguna circunstancia ([SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L160)).
  - Los pedidos finalizados o entregados son inmutables ([SDD/domain/Especificación Funcional del Negocio - NexusMarket.md](../../SDD/domain/Especificaci%C3%B3n%20Funcional%20del%20Negocio%20-%20NexusMarket.md#L189)).
  - Los usuarios tienen roles únicos y control de versiones de token (`authTokenVersion`) para invalidación de sesión ([CONSTITUTION.md](../../CONSTITUTION.md#L19-L22)).
  - Las líneas de pedido (`OrderLine`) son snapshots inmutables del producto al momento de compra.
- **Segregación de Puertos:**
  - Puertos de entrada (`ports/in`) organizados por rol de actor: Comprador, Vendedor, Operador Logístico, Administrador, Supervisor y Acceso Público.
  - Puertos de salida (`ports/out`) para desacoplar el almacenamiento políglota (MySQL y MongoDB) y utilidades de seguridad (JWT, Password Hashing).

## 1. Need and scope
- **Necesidad:** El proyecto cuenta actualmente con clases de modelo anémicas sin comportamiento, sin puertos y sin servicios de dominio. Es indispensable dotar a la capa de dominio de reglas de negocio ejecutables, invariantes protegidas, manejo de errores tipado y contratos de entrada/salida para habilitar las capas de adaptadores y casos de uso.
- **Alcance (Scope):**
  - Creación de jerarquía de excepciones de dominio en `application.domain.exceptions`.
  - Transformación de los modelos anémicos en entidades ricas con comportamiento, validaciones y encapsulación en `application.domain.models`.
  - Fortalecimiento de Value Objects inmutables con auto-validación en `application.domain.valueobjects`.
  - Definición de interfaces de Puertos de Salida (`domain/ports/out/`) para persistencia (MySQL/Mongo) y seguridad.
  - Definición de interfaces de Puertos de Entrada (`domain/ports/in/`) para los 5 roles del marketplace más acceso público.
  - Implementación de Servicios de Dominio (`domain/services/`) para orquestación de operaciones de negocio puras.
  - Creación de suite de pruebas unitarias de dominio con JUnit 5 verificando todas las invariantes.

## 2. Acceptance Criteria and Non-Goals
### Criterios de Aceptación (Acceptance Criteria)
- **AC-01:** Creación de la jerarquía de excepciones de dominio bajo `application.domain.exceptions` encabezada por `DomainException` (no chequeada), incluyendo `NegativeStockException`, `InvalidOrderStateException`, `InvalidUserStatusException`, `UnauthorizedRoleOperationException` y `EntityNotFoundException`.
- **AC-02:** Enriquecimiento de entidades en `application.domain.models` (`User`, `Buyer`, `Seller`, `Order`, `Product`, `ProductStock`, `InventoryMovement`, `Warehouse`, `ShoppingCart`, `Shipment`, `Invoice`, `Return`, `Refund`), incorporando métodos de negocio mutadores controlados, eliminación de setters públicos promiscuos y validación de invariantes.
- **AC-03:** Blindaje de Value Objects en `application.domain.valueobjects` asegurando inmutabilidad estricta, auto-validación en construcción y comparación semántica por valor.
- **AC-04:** Definición de Puertos de Salida en `application.domain.ports.out`: `UserRepositoryPort`, `OrderRepositoryPort`, `InventoryRepositoryPort`, `WarehouseRepositoryPort`, `ProductRepositoryPort`, `ShoppingCartRepositoryPort`, `AuditLogRepositoryPort`, `JwtServicePort` y `PasswordServicePort`.
- **AC-05:** Definición de Puertos de Entrada en `application.domain.ports.in`: `PublicAccessPort`, `BuyerPort`, `SellerPort`, `LogisticsOperatorPort`, `AdministratorPort` y `SupervisorPort`.
- **AC-06:** Implementación de Servicios de Dominio puros en `application.domain.services` organizados por subdominio (`auth`, `order`, `inventory`, `catalog`), sin anotaciones de Spring ni acoplamiento a infraestructura.
- **AC-07:** Batería de pruebas unitarias de dominio puras con JUnit 5 en `src/test/java/application/domain/` que valide las invariantes de negocio (existencias no negativas, transiciones de estado de órdenes, versionado de token de usuario) y pase con éxito en `docker compose exec -T nexusmarcket-app mvn test`.

### Fuera de Alcance (Non-Goals)
- No incluye la implementación de adaptadores de persistencia JPA (`@Entity`) ni MongoDB (`@Document`).
- No incluye controladores REST, DTOs de transporte HTTP ni filtros de Spring Security (reservados para fases posteriores).
- No incluye migraciones de base de datos ni scripts DDL.

## 3. Domain applicability
| Security & Compliance Control | Required? | Technical Enforcement |
| :--- | :---: | :--- |
| **¿Accede o procesa datos sensibles (PII)?** | SÍ | Las entidades de dominio modelan datos de usuarios (identificación, nombre, correo). Se aplican Value Objects para validación de formato y métodos en User para control estricto de contraseñas (hash delegado a `PasswordServicePort`). |
| **¿Expone datos cross-tenant?** | NO | N/A justificado. El marketplace es una plataforma unificada sin separación multi-tenant en dominio. |
| **¿Requiere audit logging en dominio?** | SÍ | Se define el puerto de salida `AuditLogRepositoryPort` y el modelo `AuditLog` para permitir que los servicios de dominio registren operaciones críticas. |
| **Integridad Transaccional de Inventario** | SÍ | La entidad `ProductStock` y el servicio `InventoryService` rechazan cualquier operación que resulte en stock menor a cero arrojando `NegativeStockException`. |

## 4. Experiencia y límites
- **Interfaces de usuario:** N/A justificado. El dominio es estrictamente lógica de negocio agnóstica de presentación.
- **Manejo de Errores de Negocio:** Toda violación de regla de negocio o invariante lanza excepciones explícitas y tipadas del dominio que serán capturadas en fases posteriores por el `GlobalExceptionHandler`.

## 5. Traceability
- **AC-01** -> Plan Sección 2 -> `TASK-01` en `tasks.md`
- **AC-02** -> Plan Sección 2 -> `TASK-02` en `tasks.md`
- **AC-03** -> Plan Sección 2 -> `TASK-03` en `tasks.md`
- **AC-04** -> Plan Sección 2 -> `TASK-04` en `tasks.md`
- **AC-05** -> Plan Sección 2 -> `TASK-05` en `tasks.md`
- **AC-06** -> Plan Sección 2 -> `TASK-06` en `tasks.md`
- **AC-07** -> Plan Sección 4 -> `TASK-07` en `tasks.md`
