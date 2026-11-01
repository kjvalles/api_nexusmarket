# [TASKS-002] Tareas de Implementación — Fase 2: Capa de Dominio Pura y Puertos

Lista de tareas accionables derivadas del plan técnico y mapeadas a los criterios de aceptación.

- [x] TASK-01 / AC-01: Crear la jerarquía de excepciones de negocio en `application.domain.exceptions` (`DomainException`, `NegativeStockException`, `InvalidOrderStateException`, `InvalidUserStatusException`, `UnauthorizedRoleOperationException`, `EntityNotFoundException`, `InvalidCredentialsException`, `ProductNotAvailableException`).
- [x] TASK-02 / AC-02: Enriquecer los modelos de entidad en `application.domain.models` con métodos de negocio y protección de invariantes (`User`, `Buyer`, `Seller`, `Order`, `Product`, `ProductStock`, `InventoryMovement`, `Warehouse`, `ShoppingCart`, `AuditLog`, `Shipment`, `Invoice`, `Return`, `Refund`).
- [x] TASK-03 / AC-03: Fortalecer Value Objects en `application.domain.valueobjects` asegurando inmutabilidad, auto-validación y métodos de dominio (`OrderLine`, `CartItem`, `Address`, etc.).
- [x] TASK-04 / AC-04: Definir las interfaces de Puertos de Salida en `application.domain.ports.out` para repositorios políglotas (MySQL/Mongo) y servicios de seguridad.
- [x] TASK-05 / AC-05: Definir las interfaces de Puertos de Entrada en `application.domain.ports.in` segregados por roles de actor (`PublicAccessPort`, `BuyerPort`, `SellerPort`, `LogisticsOperatorPort`, `AdministratorPort`, `SupervisorPort`).
- [x] TASK-06 / AC-06: Implementar Servicios de Dominio atómicos en `application.domain.services` para subdominios `auth`, `order`, `inventory` y `catalog`.
- [x] TASK-07 / AC-07: Implementar suite de pruebas unitarias puras con JUnit 5 en `src/test/java/application/domain/` y validar ejecución exitosa con `docker compose exec -T nexusmarcket-app mvn test`.
