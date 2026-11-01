# [PLAN-002] Plan Técnico — Fase 2: Capa de Dominio Pura, Servicios y Puertos

## 1. Evidencia y Arquitectura Existente
- **Estado Actual:** En `nexusmarcket/src/main/java/application/domain/` existen clases anémicas (`models/`, `valueobjects/`, `enums/`). No existen carpetas para `exceptions/`, `ports/in/`, `ports/out/` ni `services/`.
- **Arquitectura de Referencia:** El proyecto guía (`construccion_de_software_2_2026_2`) implementa puertos segregados por rol en `domain/ports/in/`, puertos de persistencia y servicios en `domain/ports/out/`, servicios de dominio atómicos y entidades con invariantes protegidas (ej. `User.changeStatus()`, `bumpAuthTokenVersion()`).
- **Persistencia Políglota (Enfoque B):** Los modelos se comunican inter-agregado mediante identificadores (`userId`, `productId`, `orderId`) para desacoplar el almacenamiento en MySQL y MongoDB.

## 2. Cambios Propuestos y Diseño Proporcional
1. **Paquete `application.domain.exceptions`**:
   - `DomainException` (clase base abstracta no chequeada).
   - `NegativeStockException` (para violaciones de existencia en inventario).
   - `InvalidOrderStateException` (para transiciones ilegales en ciclo de pedidos).
   - `InvalidUserStatusException` (para transiciones de estado de usuario).
   - `UnauthorizedRoleOperationException` (para violaciones de roles de negocio).
   - `EntityNotFoundException` (cuando un agregado requerido no existe).
2. **Paquete `application.domain.models`**:
   - `User`: encapsulación de contraseña, `bumpAuthTokenVersion()`, validación de rol único y `changeStatus()`.
   - `Buyer` y `Seller`: validaciones de estado comercial y datos fiscales.
   - `Order`: gestión de ciclo de vida (`PENDING_PAYMENT -> PAID -> SHIPPED -> DELIVERED -> COMPLETED / CANCELLED`), inmutabilidad al finalizar, método `addLine()`, recálculo automático de totales, impuestos y descuentos.
   - `ProductStock`: método `applyMovement(quantity, type)` asegurando que el saldo final nunca sea menor a cero.
   - `Product`: métodos de negocio para publicar, suspender y administrar variantes.
   - `ShoppingCart`: métodos `addItem()`, `removeItem()`, `clear()`.
   - `AuditLog`: modelo de evento de auditoría con actor, acción, recurso, detalle y marca de tiempo.
3. **Paquete `application.domain.valueobjects`**:
   - Reforzar inmutabilidad (`final`), métodos de validación de formato (correo, documento) y cálculo en `OrderLine`, `CartItem`, `Address`.
4. **Paquete `application.domain.ports.out`**:
   - Contratos para persistencia: `UserRepositoryPort`, `BuyerRepositoryPort`, `SellerRepositoryPort`, `ProductRepositoryPort`, `OrderRepositoryPort`, `InventoryMovementRepositoryPort`, `ProductStockRepositoryPort`, `WarehouseRepositoryPort`, `ShoppingCartRepositoryPort`, `AuditLogRepositoryPort`.
   - Contratos de servicios de infraestructura: `JwtServicePort`, `PasswordServicePort`.
5. **Paquete `application.domain.ports.in`**:
   - `PublicAccessPort`: login, registro de comprador, consulta pública de catálogo.
   - `BuyerPort`: carrito, confirmación de orden, seguimiento de envíos, solicitar devolución.
   - `SellerPort`: publicación de producto, gestión de variantes, consulta de inventario y ventas.
   - `LogisticsOperatorPort`: administración de bodegas, despacho de pedidos, recepción de devoluciones.
   - `AdministratorPort`: registro de vendedores, asignación de bodegas, bloqueo de usuarios, gestión de reembolsos.
   - `SupervisorPort`: consulta de métricas y auditoría.
6. **Paquete `application.domain.services`**:
   - Subdominios:
     - `auth/`: `LoginService`, `RegisterBuyerService`, `ChangePasswordService`.
     - `order/`: `CreateOrderService`, `TransitionOrderStatusService`, `CancelOrderService`.
     - `inventory/`: `RegisterInventoryMovementService`, `CheckStockAvailabilityService`.
     - `catalog/`: `PublishProductService`, `UpdateProductService`.

## 3. Contratos y Compatibilidad (Contracts & Compatibility)
- **Contratos de Dominio:**
  - Todas las operaciones reciben y retornan modelos de dominio o tipos primitivos Java estándar.
  - Ninguna clase del dominio importa paquetes `org.springframework.*`, `jakarta.persistence.*`, `org.springframework.data.*` ni librerías de serialización.
- **Inversión de Dependencias (API Contracts):**
  - Los servicios de dominio dependen exclusivamente de las interfaces definidas en `ports/out/`.
  - La compatibilidad entre MongoDB y MySQL se asegura mediante el uso de IDs de referencia explícitos en lugar de punteros de objetos en memoria.

## 4. Verificación
- **AC-01 a AC-06:** Inspección estática de código verificando pureza del paquete `application.domain` (cero imports de Spring/JPA/Mongo) y completitud de métodos de negocio y puertos.
- **AC-07:** Implementación de pruebas unitarias con JUnit 5 puro en `nexusmarcket/src/test/java/application/domain/`:
  - `OrderTest`: prueba de cálculo de totales y rechazo de modificaciones en orden finalizada.
  - `ProductStockTest`: prueba de rechazo de movimientos que causen inventario negativo.
  - `UserTest`: prueba de incremento de versión de token en cambio de estado o clave.
  - Verificación ejecutada en el contenedor: `docker compose exec -T nexusmarcket-app mvn test`.

## 5. Estrategia de Reversión (Rollback)
- Si los cambios de dominio rompen la compilación o las pruebas:
  1. Revertir mediante Git: `git checkout -- nexusmarcket/src/main/java/application/domain/`.
  2. Limpiar archivos no rastreados generados en la capa de dominio si fuera necesario (`git clean -fd nexusmarcket/src/main/java/application/domain/exceptions/ nexusmarcket/src/main/java/application/domain/ports/ nexusmarcket/src/main/java/application/domain/services/`).
  3. Ejecutar `docker compose exec -T nexusmarcket-app mvn test` para confirmar el restablecimiento al estado limpio anterior.
