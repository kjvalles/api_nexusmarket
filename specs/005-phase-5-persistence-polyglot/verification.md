# [VERIFICATION-005] Reporte de Verificación — Fase 5: Adaptadores de Persistencia Políglota

Fecha de Verificación: 2026-10-25
Resultado Global: SUPERADO (PASSED)

## 1. Mapeo Criterio a Evidencia

| Criterio | Estado | Método | Evidencia / Ubicación |
| :--- | :---: | :---: | :--- |
| **AC-01: Adaptadores JPA de Usuarios y Roles** | SUPERADO | Automatizado | `UserJpaAdapter`, `BuyerJpaAdapter`, `SellerJpaAdapter` en `application.adapters.persistence.jpa`: Gestionan persistencia en MySQL con herencia `JOINED`, mapeo de roles, datos de contacto, saldos y control de versión JWT (`authTokenVersion`). Verificado en `PersistenceAdaptersTest.testUserJpaAdapter`, `testBuyerJpaAdapter` y `testSellerJpaAdapter`. |
| **AC-02: Adaptador JPA de Pedidos** | SUPERADO | Automatizado | `OrderJpaAdapter` en `application.adapters.persistence.jpa`: Persistencia de órdenes con `OrderLine` en cascada (`CascadeType.ALL`), persistencia de estados de ciclo de vida (`OrderStatus`) y consultas por comprador. Verificado en `PersistenceAdaptersTest.testOrderJpaAdapter`. |
| **AC-03: Adaptadores JPA de Inventario y Bodegas** | SUPERADO | Automatizado | `ProductStockJpaAdapter`, `InventoryMovementJpaAdapter`, `WarehouseJpaAdapter` en `application.adapters.persistence.jpa`: Persistencia de saldos de stock garantizando existencias no negativas, kardex transaccional de movimientos y bodegas con direcciones físicas. Verificado en `PersistenceAdaptersTest.testProductStockJpaAdapter`, `testInventoryMovementJpaAdapter` y `testWarehouseJpaAdapter`. |
| **AC-04: Adaptador Mongo de Catálogo de Productos** | SUPERADO | Automatizado | `ProductMongoAdapter` en `application.adapters.persistence.mongodb`: Almacenamiento en colección `products` de MongoDB con variantes embebidas (`ProductVariantDocument`), filtrado de productos publicados y por vendedor. Verificado en `PersistenceAdaptersTest.testProductMongoAdapter`. |
| **AC-05: Adaptador Mongo de Carritos de Compras** | SUPERADO | Automatizado | `ShoppingCartMongoAdapter` en `application.adapters.persistence.mongodb`: Almacenamiento de carritos en colección `shopping_carts` de MongoDB con ítems embebidos y borrado atómico pos-checkout (`deleteByBuyerId`). Verificado en `PersistenceAdaptersTest.testShoppingCartMongoAdapter`. |
| **AC-06: Adaptador Mongo de Bitácora de Auditoría** | SUPERADO | Automatizado | `AuditLogMongoAdapter` en `application.adapters.persistence.mongodb`: Almacenamiento inmutable de eventos en colección `audit_logs` con trazabilidad por actor y entidad. Verificado en `PersistenceAdaptersTest.testAuditLogMongoAdapter`. |
| **AC-07: Quality Gate y Suite de Pruebas en Docker** | SUPERADO | Automatizado | Ejecución en contenedor: `docker compose exec -T nexusmarcket-app mvn test` con resultado `BUILD SUCCESS` (46 pruebas ejecutadas en total, 0 fallos, 0 errores). |

## 2. Registro de Comandos de Prueba
- **Comando Ejecutado:** `docker compose exec -T nexusmarcket-app mvn test`
- **Resultado:**
  ```text
  [INFO] Running Pruebas Unitarias de Adaptadores de Persistencia Políglota (JPA y MongoDB)
  [INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.941 s -- in Pruebas Unitarias de Adaptadores de Persistencia Políglota (JPA y MongoDB)
  [INFO] Running Pruebas Unitarias de Adaptadores de Seguridad y JWT
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.722 s -- in Pruebas Unitarias de Adaptadores de Seguridad y JWT
  [INFO] Running Pruebas Unitarias de Casos de Uso (Adapters / UseCases)
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.242 s -- in Pruebas Unitarias de Casos de Uso (Adapters / UseCases)
  [INFO] Running Pruebas Unitarias del Agregado Order (Dominio Puro)
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.018 s -- in Pruebas Unitarias del Agregado Order (Dominio Puro)
  [INFO] Running Pruebas Unitarias del Agregado ProductStock (Invariante de Inventario No Negativo)
  [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.011 s -- in Pruebas Unitarias del Agregado ProductStock (Invariante de Inventario No Negativo)
  [INFO] Running Pruebas Unitarias de Servicios de Dominio Puros
  [INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.026 s -- in Pruebas Unitarias de Servicios de Dominio Puros
  [INFO] Running Pruebas Unitarias del Agregado User / Buyer (Control de Sesión e Invariantes)
  [INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in Pruebas Unitarias del Agregado User / Buyer (Control de Sesión e Invariantes)
  [INFO] Running application.NexusmarcketApplicationTests
  [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 32.77 s -- in application.NexusmarcketApplicationTests
  [INFO] 
  [INFO] Results:
  [INFO] Tests run: 46, Failures: 0, Errors: 0, Skipped: 0
  [INFO] ------------------------------------------------------------------------
  [INFO] BUILD SUCCESS
  [INFO] Total time:  55.840 s
  [INFO] Finished at: 2026-10-25T05:00:11Z
  ```
