# [PLAN-003] Plan Técnico — Fase 3: Casos de Uso de Aplicación (Adapters / UseCases)

## 1. Evidencia y Arquitectura Existente
- **Estado Actual:**
  - En `nexusmarcket/src/main/java/application/domain/` existen:
    - 6 contratos de puertos de entrada en `ports/in/` ([`PublicAccessPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/PublicAccessPort.java), [`BuyerPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/BuyerPort.java), [`SellerPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/SellerPort.java), [`LogisticsOperatorPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/LogisticsOperatorPort.java), [`AdministratorPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/AdministratorPort.java), [`SupervisorPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/SupervisorPort.java)).
    - 12 interfaces de puertos de salida en `ports/out/` para persistencia y seguridad.
    - 12 servicios de dominio atómicos y puros en `services/` (`auth`, `order`, `inventory`, `catalog`).
    - Modelos ricos y excepciones tipadas.
  - La carpeta `application/adapters/` aún no existe en el proyecto.
- **Arquitectura de Referencia:**
  - El proyecto guía (`construccion_de_software_2_2026_2/bank`) implementa la capa de orquestación de aplicación en `adapters/useCases/` mediante clases anotadas con `@Service` y `@RequiredArgsConstructor`, que implementan los puertos de entrada e inyectan los servicios de dominio correspondientes.
  - La configuración de inyección de dependencias para los servicios de dominio puros se declara en una clase `@Configuration` dedicada (`adapters/config/DomainServicesConfig.java`) para preservar la pureza del dominio (sin anotaciones de Spring en `domain/`).

## 2. Cambios Propuestos y Diseño Proporcional

### 2.1 Paquete `application.adapters.config`
- **`DomainServicesConfig.java`**:
  - Clase anotada con `@Configuration`.
  - Expone cada servicio de dominio puro como `@Bean` de Spring:
    - `LoginService(UserRepositoryPort, PasswordServicePort, JwtServicePort)`
    - `RegisterBuyerService(UserRepositoryPort, BuyerRepositoryPort, PasswordServicePort)`
    - `ChangePasswordService(UserRepositoryPort, PasswordServicePort)`
    - `LogoutService(UserRepositoryPort)`
    - `CreateOrderService(OrderRepositoryPort, ProductRepositoryPort)`
    - `TransitionOrderStatusService(OrderRepositoryPort)`
    - `CancelOrderService(OrderRepositoryPort)`
    - `RegisterInventoryMovementService(ProductStockRepositoryPort, InventoryMovementRepositoryPort)`
    - `CheckStockAvailabilityService(ProductStockRepositoryPort)`
    - `CreateProductService(ProductRepositoryPort)`
    - `PublishProductService(ProductRepositoryPort)`
    - `UpdateProductService(ProductRepositoryPort)`

### 2.2 Paquete `application.adapters.useCases`
1. **`PublicAccessUseCaseImpl`**:
   - Implementa `PublicAccessPort`.
   - Inyecta: `LoginService`, `LogoutService`, `RegisterBuyerService`, `ProductRepositoryPort`.
   - Métodos:
     - `String login(String identifier, String rawPassword)`: Invoca `loginService.login(identifier, rawPassword).getToken()`.
     - `void logout(String userId)`: Invoca `logoutService.logout(userId)`.
     - `Buyer registerBuyer(Buyer buyer, String rawPassword)`: Invoca `registerBuyerService.registerBuyer(buyer, rawPassword)`.
     - `List<Product> getPublicCatalog()`: Invoca `productRepositoryPort.findPublished()`.
     - `Product getPublicProductDetails(String productId)`: Busca en repositorio y valida que esté publicado arrojando `ProductNotAvailableException` si no lo está.

2. **`BuyerUseCaseImpl`**:
   - Implementa `BuyerPort`.
   - Inyecta: `ShoppingCartRepositoryPort`, `CreateOrderService`, `CancelOrderService`, `OrderRepositoryPort`.
   - Métodos:
     - `ShoppingCart getCart(String buyerId)`: Obtiene carrito o inicializa uno vacío.
     - `ShoppingCart addItemToCart(String buyerId, CartItem item)`: Agrega item y guarda en `shoppingCartRepositoryPort`.
     - `ShoppingCart removeItemFromCart(String buyerId, String productId, String variantId)`: Elimina item y guarda.
     - `void clearCart(String buyerId)`: Limpia o elimina carrito.
     - `Order checkoutCart(String buyerId, Address deliveryAddress, String notes)`: Valida items del carrito, mapea a `OrderLine`, invoca `createOrderService.createOrder(...)`, vacía el carrito y retorna la orden creada.
     - `List<Order> getMyOrders(String buyerId)`: Invoca `orderRepositoryPort.findByBuyerId(buyerId)`.
     - `Order getOrderDetails(String buyerId, Long orderId)`: Valida pertenencia de la orden (`buyerId.equals(order.getBuyerId())`) o lanza `UnauthorizedRoleOperationException`.
     - `void cancelOrder(String buyerId, Long orderId, String reason)`: Valida pertenencia e invoca `cancelOrderService.cancelOrder(orderId, reason)`.

3. **`SellerUseCaseImpl`**:
   - Implementa `SellerPort`.
   - Inyecta: `CreateProductService`, `UpdateProductService`, `PublishProductService`, `ProductRepositoryPort`, `ProductStockRepositoryPort`.
   - Métodos:
     - `Product createProduct(String vendorId, Product product)`: Asigna `vendorId` e invoca `createProductService.createProduct(product)`.
     - `Product updateProduct(String vendorId, Product product)`: Valida que el producto pertenezca al vendedor y ejecuta `updateProductService.updateProduct(product)`.
     - `void publishProduct(String vendorId, String productId)`: Valida propiedad y ejecuta `publishProductService.publishProduct(productId)`.
     - `void suspendProduct(String vendorId, String productId)`: Valida propiedad, ejecuta `product.suspend()` y guarda.
     - `List<Product> getMyProducts(String vendorId)`: Invoca `productRepositoryPort.findByVendorId(vendorId)`.
     - `List<ProductStock> getMyInventory(String vendorId)`: Consulta productos del vendedor y consolida sus existencias en bodegas.

4. **`LogisticsOperatorUseCaseImpl`**:
   - Implementa `LogisticsOperatorPort`.
   - Inyecta: `RegisterInventoryMovementService`, `TransitionOrderStatusService`, `ProductStockRepositoryPort`.
   - Métodos:
     - `ProductStock registerInflow(operatorId, productId, variantId, warehouseId, quantity, notes)`: Invoca `registerMovement` con `MANUAL_ENTRY`.
     - `ProductStock registerAdjustment(operatorId, productId, variantId, warehouseId, delta, notes)`: Invoca `registerMovement` con `MANUAL_ENTRY` (delta positivo) o `LOSS_ADJUSTMENT` (delta negativo).
     - `void dispatchOrder(operatorId, orderId, trackingNumber)`: Invoca `transitionOrderStatusService.markAsDispatched(orderId)`.
     - `void deliverOrder(operatorId, orderId)`: Invoca `transitionOrderStatusService.markAsDelivered(orderId)`.
     - `List<ProductStock> getWarehouseStock(warehouseId)`: Consulta existencias físicas por bodega.

5. **`AdministratorUseCaseImpl`**:
   - Implementa `AdministratorPort`.
   - Inyecta: `UserRepositoryPort`, `SellerRepositoryPort`, `WarehouseRepositoryPort`, `PasswordServicePort`.
   - Métodos:
     - `Seller registerSeller(adminId, seller, initialPassword)`: Encripta contraseña, asigna rol `SELLER` y persiste en repositorios de vendedores y usuarios.
     - `Warehouse createWarehouse(adminId, warehouse)`: Persiste bodega en `warehouseRepositoryPort`.
     - `void changeUserStatus(adminId, targetUserId, statusCode)`: Cambia estado de usuario e incrementa `authTokenVersion` para invalidar sesiones viejas.
     - `List<User> listUsers()`: Consulta listado de usuarios.
     - `List<Seller> listSellers()`: Consulta listado de vendedores.

6. **`SupervisorUseCaseImpl`**:
   - Implementa `SupervisorPort`.
   - Inyecta: `AuditLogRepositoryPort`, `OrderRepositoryPort`, `ProductStockRepositoryPort`.
   - Métodos:
     - `List<AuditLog> getAuditLogs(supervisorId, resourceFilter)`: Consulta logs de auditoría.
     - `List<Order> getAllOrders(supervisorId)`: Consulta consolidado de pedidos.
     - `List<ProductStock> getInventorySummary(supervisorId)`: Consulta resumen de inventario global.

## 3. Contratos y Compatibilidad (Contracts & Compatibility)
- **Contratos de Entrada:** Todos los casos de uso implementan estrictamente las interfaces definidas en `application.domain.ports.in.*`.
- **Inversión de Dependencias:** Los casos de uso dependen exclusivamente de contratos (`ports/in`, `ports/out`) y de servicios atómicos inyectados.
- **Manejo de Excepciones:** Se propagan las excepciones tipadas del dominio (`DomainException`, `NegativeStockException`, `InvalidOrderStateException`, `UnauthorizedRoleOperationException`, `EntityNotFoundException`) sin atraparlas de forma defensiva ni enmascararlas.

## 4. UI & Interaction Design
- **N/A Justificado:** Esta fase concierne exclusivamente a la capa de orquestación de aplicación backend. No existen interfaces gráficas de usuario.

## 5. Verificación
- **Pruebas Unitarias de Casos de Uso:**
  - Suite JUnit 5 en `nexusmarcket/src/test/java/application/adapters/useCases/` verificando cada caso de uso:
    - `PublicAccessUseCaseTest`: login exitoso, login con error, registro de comprador, consulta de catálogo.
    - `BuyerUseCaseTest`: adición al carrito, checkout con cálculo de orden, consulta de pedidos propios y rechazo de consulta de orden ajena.
    - `SellerUseCaseTest`: creación de producto, publicación y rechazo de modificación de producto de otro vendedor.
    - `LogisticsOperatorUseCaseTest`: entrada de mercancía, despacho y entrega de pedidos.
    - `AdministratorUseCaseTest`: registro de vendedor, cambio de estado de usuario con revocación de tokens.
    - `SupervisorUseCaseTest`: consulta de auditoría y consolidado de pedidos.
- **Quality Gate:** Ejecución del comando automatizado en el contenedor:
  ```powershell
  docker compose exec -T nexusmarcket-app mvn test
  ```
  Debe resultar en `BUILD SUCCESS` con 0 fallos y 0 errores.

## 6. Estrategia de Reversión (Rollback)
- Si la compilación o pruebas de casos de uso fallan:
  1. Revertir mediante Git los archivos generados en `nexusmarcket/src/main/java/application/adapters/useCases/` y `nexusmarcket/src/main/java/application/adapters/config/`.
  2. Ejecutar `docker compose exec -T nexusmarcket-app mvn test` para confirmar el restablecimiento a la línea base limpia anterior.
