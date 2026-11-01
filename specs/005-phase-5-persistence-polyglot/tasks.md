# [TASKS-005] Tareas de Implementación — Fase 5: Adaptadores de Persistencia Políglota

Lista de tareas de implementación derivadas del plan técnico y mapeadas a los criterios de aceptación:

- [x] TASK-01 / AC-01: Implementar entidades JPA (`UserJpaEntity`, `BuyerJpaEntity`, `SellerJpaEntity`), repositorios Spring Data, mappers y adaptadores `UserJpaAdapter`, `BuyerJpaAdapter`, `SellerJpaAdapter` en `application.adapters.persistence.jpa`.
- [x] TASK-02 / AC-02: Implementar entidades JPA (`OrderJpaEntity`, `OrderLineJpaEntity`), repositorio Spring Data, mapper `OrderJpaMapper` y adaptador `OrderJpaAdapter` con persistencia en cascada.
- [x] TASK-03 / AC-03: Implementar entidades JPA (`ProductStockJpaEntity`, `InventoryMovementJpaEntity`, `WarehouseJpaEntity`), repositorios, mappers y adaptadores `ProductStockJpaAdapter`, `InventoryMovementJpaAdapter`, `WarehouseJpaAdapter`.
- [x] TASK-04 / AC-04: Implementar documento MongoDB `ProductDocument` con variantes embebidas, repositorio Spring Data Mongo, mapper `ProductMongoMapper` y adaptador `ProductMongoAdapter` en `application.adapters.persistence.mongodb`.
- [x] TASK-05 / AC-05: Implementar documento MongoDB `ShoppingCartDocument` con ítems de carrito, repositorio Spring Data Mongo, mapper `ShoppingCartMongoMapper` y adaptador `ShoppingCartMongoAdapter`.
- [x] TASK-06 / AC-06: Implementar documento MongoDB `AuditLogDocument`, repositorio Spring Data Mongo, mapper `AuditLogMongoMapper` y adaptador `AuditLogMongoAdapter`.
- [x] TASK-07 / AC-07: Implementar suite de pruebas de persistencia `PersistenceAdaptersTest` en `src/test/java/application/adapters/persistence/` y validar ejecución limpia en Docker con `docker compose exec -T nexusmarcket-app mvn test`.
