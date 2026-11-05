# [PLAN-007] Technical plan: Carga Inicial de Datos Semilla (DatabaseSeeder) y Verificación End-to-End

## Evidence and existing architecture
- **Modelo Políglota:**
  - MySQL almacena usuarios relacionales (`User`, `Buyer`, `Seller`, `DefaultUser`), bodegas (`Warehouse`), existencias de inventario (`ProductStock`), movimientos kardex (`InventoryMovement`) y pedidos (`Order`, `OrderLine`).
  - MongoDB almacena catálogo de productos con variantes (`Product`, `ProductVariant`), carritos de compras (`ShoppingCart`) y bitácoras de auditoría (`AuditLog`).
- **Seguridad:**
  - Contraseñas cifradas con BCrypt mediante `PasswordServicePort`.
  - Roles definidos en `UserRole` (`ADMINISTRATOR`, `SUPERVISOR`, `LOGISTICS_OPERATOR`, `SELLER`, `BUYER`).
- **Contratos:**
  - Todos los repositorios de salida están desacoplados mediante interfaces en `application.domain.ports.out.*`.

## Proposed change
1. Crear el cargador `DatabaseSeeder.java` en `nexusmarcket/src/main/java/application/infrastructure/seed/DatabaseSeeder.java`:
   - Anotado con `@Component`, implementando `CommandLineRunner` y `@Order(1)`.
   - Inyección de todos los puertos de salida requeridos (`UserRepositoryPort`, `BuyerRepositoryPort`, `SellerRepositoryPort`, `WarehouseRepositoryPort`, `ProductRepositoryPort`, `ProductStockRepositoryPort`, `InventoryMovementRepositoryPort`, `ShoppingCartRepositoryPort`, `AuditLogRepositoryPort`, `PasswordServicePort`).
   - Sembrado idempotente (solo crea registros si no existen previamente en la base de datos):
     - **AC-01:** Usuarios semilla para los 5 roles con clave `Password123*` (`admin@nexusmarcket.com`, `supervisor@nexusmarcket.com`, `logistica@nexusmarcket.com`, `vendedor@nexusmarcket.com`, `comprador@nexusmarcket.com`).
     - **AC-02:** Bodegas físicas (`wh-central-01` tipo MARKETPLACE en Bogotá, `wh-seller-01` tipo SELLER en Medellín vinculada al vendedor).
     - **AC-03:** Catálogo de productos en MongoDB (3 físicos con variantes, 1 digital, 1 suspendido).
     - **AC-04:** Saldos de inventario en MySQL para variantes físicas y kardex manual de entrada.
     - **AC-05:** Carrito de compras con ítems para el comprador semilla y 3 órdenes de prueba (`DELIVERED`, `DISPATCHED`, `PENDING_PAYMENT`).
     - **AC-06:** Bitácoras de auditoría en MongoDB de inicialización del sistema.
2. Crear la prueba integral `EndToEndPlatformIntegrationTest.java` en `nexusmarcket/src/test/java/application/e2e/EndToEndPlatformIntegrationTest.java`:
   - **AC-07:** Prueba E2E que ejecute login con usuario semilla, verifique catálogo, inventario, despacho y auditoría.

## Contracts and compatibility
- La ejecución es puramente aditiva y no altera las firmas de los puertos existentes ni de los controladores REST.
- Los modelos de datos sembrados respetan al 100% las restricciones de validación de Bean Validation y las invariantes de negocio de DDD.

## UI & Interaction Design
- N/A (Módulo de infraestructura backend y pruebas de integración).

## Verification
- Ejecución en contenedor Docker: `docker compose exec -T nexusmarcket-app mvn test`.
- Verificación en vivo: Login exitoso en `POST /api/v1/auth/login` con `admin@nexusmarcket.com` y `Password123*` retornando token JWT 200 OK.

## Rollback / recovery
- Al ser datos semilla idempotentes, no aplican migraciones destructivas. Un `docker compose down -v` restablece las bases de datos desde cero si se requiere reiniciar el entorno.
