# [PLAN-006] Plan Técnico — Fase 6: Capa de Presentación REST (adapters/rest/)

## 1. Evidencia y Arquitectura Existente

- **Puertos de Entrada Definidos ([ports/in/](../../nexusmarcket/src/main/java/application/domain/ports/in/))**:
  - [`PublicAccessPort.java`](../../nexusmarcket/src/main/java/application/domain/ports/in/PublicAccessPort.java): Operaciones públicas de login, logout, registro de comprador y consulta de catálogo público.
  - [`BuyerPort.java`](../../nexusmarcket/src/main/java/application/domain/ports/in/BuyerPort.java): Operaciones de comprador autenticado: carrito, checkout de orden, historial y cancelación.
  - [`SellerPort.java`](../../nexusmarcket/src/main/java/application/domain/ports/in/SellerPort.java): Operaciones de vendedor: gestión de productos, publicación, suspensión y consulta de inventario propio.
  - [`LogisticsOperatorPort.java`](../../nexusmarcket/src/main/java/application/domain/ports/in/LogisticsOperatorPort.java): Operaciones de bodega: entradas de existencias, ajustes/mermas, stock por bodega, despacho y entrega de pedidos.
  - [`AdministratorPort.java`](../../nexusmarcket/src/main/java/application/domain/ports/in/AdministratorPort.java): Operaciones de administrador: alta de vendedores, creación de bodegas, cambio de estado de usuario, listado de usuarios y vendedores.
  - [`SupervisorPort.java`](../../nexusmarcket/src/main/java/application/domain/ports/in/SupervisorPort.java): Operaciones de supervisor: bitácora de auditoría, trazabilidad de órdenes globales y resumen consolidado de existencias.
- **Seguridad y Filtros Existentes ([adapters/security/](../../nexusmarcket/src/main/java/application/adapters/security/))**:
  - [`SecurityConfig.java`](../../nexusmarcket/src/main/java/application/adapters/security/SecurityConfig.java): Configuración de Spring Security con autorización declarativa por roles (`BUYER`, `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`) y rutas públicas (`/api/v1/auth/**`, `/api/v1/public/**`).
  - [`AuthenticatedUserPrincipal.java`](../../nexusmarcket/src/main/java/application/adapters/security/AuthenticatedUserPrincipal.java): Principal que expone el agregado de dominio `User` del usuario autenticado vía `principal.getUser().getId()`.
- **Excepciones de Dominio Existentes ([domain/exceptions/](../../nexusmarcket/src/main/java/application/domain/exceptions/))**:
  - `DomainException`, `EntityNotFoundException`, `InvalidCredentialsException`, `InvalidOrderStateException`, `InvalidUserStatusException`, `NegativeStockException`, `ProductNotAvailableException`, `UnauthorizedRoleOperationException`.
- **Referente Arquitectónico**:
  - La arquitectura de referencia `bank` implementa una separación limpia en `application.adapters.rest`: `controllers/`, `dtos/requests/`, `dtos/responses/`, `mappers/` y `exception/GlobalExceptionHandler.java`.

---

## 2. Estructura Exacta de Paquetes (`application.adapters.rest.*`)

```
nexusmarcket/src/main/java/application/adapters/rest/
├── controllers/
│   ├── PublicAccessController.java
│   ├── BuyerController.java
│   ├── SellerController.java
│   ├── LogisticsOperatorController.java
│   ├── AdministratorController.java
│   └── SupervisorController.java
├── dtos/
│   ├── requests/
│   │   ├── LoginRequestDto.java
│   │   ├── RegisterBuyerRequestDto.java
│   │   ├── RegisterSellerRequestDto.java
│   │   ├── CreateProductRequestDto.java
│   │   ├── CreateProductVariantRequestDto.java
│   │   ├── UpdateProductRequestDto.java
│   │   ├── AddItemToCartRequestDto.java
│   │   ├── CheckoutOrderRequestDto.java
│   │   ├── CancelOrderRequestDto.java
│   │   ├── RegisterStockMovementRequestDto.java
│   │   ├── DispatchOrderRequestDto.java
│   │   ├── CreateWarehouseRequestDto.java
│   │   └── ChangeUserStatusRequestDto.java
│   └── responses/
│       ├── AuthResponseDto.java
│       ├── UserResponseDto.java
│       ├── BuyerResponseDto.java
│       ├── SellerResponseDto.java
│       ├── ProductResponseDto.java
│       ├── ProductVariantResponseDto.java
│       ├── ShoppingCartResponseDto.java
│       ├── CartItemResponseDto.java
│       ├── OrderResponseDto.java
│       ├── OrderLineResponseDto.java
│       ├── ProductStockResponseDto.java
│       ├── WarehouseResponseDto.java
│       ├── AuditLogResponseDto.java
│       ├── AddressDto.java
│       └── ErrorResponseDto.java
├── mappers/
│   ├── AuthRestMapper.java
│   ├── BuyerRestMapper.java
│   ├── SellerRestMapper.java
│   ├── LogisticsRestMapper.java
│   ├── AdminRestMapper.java
│   └── SupervisorRestMapper.java
└── exceptions/
    └── GlobalExceptionHandler.java
```

---

## 3. Contratos JSON de Requests y Responses con Bean Validation

### 3.1 DTOs de Solicitud (Requests)

| DTO Request | Atributos Principales | Restricciones Bean Validation |
| :--- | :--- | :--- |
| `LoginRequestDto` | `emailOrDocument`, `password` | `@NotBlank(message = "El email o documento es obligatorio")`<br>`@NotBlank(message = "La contraseña es obligatoria")` |
| `RegisterBuyerRequestDto` | `identityDocument`, `fullName`, `email`, `password`, `street`, `city`, `state`, `postalCode`, `country`, `additionalInstructions` | `@NotBlank` en identificación, nombre y contraseña.<br>`@Email @NotBlank` en email.<br>`@Size(min = 6)` en contraseña.<br>`@NotBlank` en `street`, `city`, `state`, `postalCode`, `country`. |
| `RegisterSellerRequestDto` | `identityDocument`, `fullName`, `email`, `initialPassword`, `businessName`, `taxId`, `businessDescription` | `@NotBlank` en documento, nombre comercial, NIT/taxId e inicial.<br>`@Email @NotBlank` en email.<br>`@Size(min = 6)` en contraseña. |
| `CreateProductRequestDto` | `name`, `description`, `category`, `basePrice`, `productType`, `imageUrls`, `variants` | `@NotBlank` en `name`, `category`, `productType`.<br>`@NotNull @Positive` en `basePrice`.<br>`@Valid` en lista de variantes embebidas. |
| `CreateProductVariantRequestDto` | `sku`, `attributeName`, `attributeValue`, `additionalPrice` | `@NotBlank` en `sku`.<br>`@PositiveOrZero` en `additionalPrice`. |
| `UpdateProductRequestDto` | `name`, `description`, `category`, `basePrice`, `imageUrls` | `@NotBlank` en `name` y `category`.<br>`@NotNull @Positive` en `basePrice`. |
| `AddItemToCartRequestDto` | `productId`, `productName`, `variantId`, `sku`, `productType`, `unitPrice`, `quantity` | `@NotBlank` en `productId`, `productName`, `productType`.<br>`@NotNull @Positive` en `unitPrice`.<br>`@NotNull @Min(1)` en `quantity`. |
| `CheckoutOrderRequestDto` | `street`, `city`, `state`, `postalCode`, `country`, `additionalInstructions`, `additionalNotes` | `@NotBlank` en `street`, `city`, `state`, `postalCode`, `country`. |
| `CancelOrderRequestDto` | `reason` | `@NotBlank(message = "El motivo de cancelación es obligatorio")` |
| `RegisterStockMovementRequestDto` | `productId`, `variantId`, `warehouseId`, `quantity`, `notes` | `@NotBlank` en `productId` y `warehouseId`.<br>`@NotNull` en `quantity`. |
| `DispatchOrderRequestDto` | `trackingNumber` | `@NotBlank(message = "El número de guía de seguimiento es obligatorio")` |
| `CreateWarehouseRequestDto` | `id`, `name`, `type`, `description`, `maxCapacity`, `street`, `city`, `state`, `postalCode`, `country`, `additionalInstructions` | `@NotBlank` en `name`, `type`, `street`, `city`, `state`, `postalCode`, `country`.<br>`@NotNull @Positive` en `maxCapacity`. |
| `ChangeUserStatusRequestDto` | `status` | `@NotBlank(message = "El nuevo estado es obligatorio")` |

### 3.2 DTOs de Respuesta (Responses)

| DTO Response | Campos Retornados |
| :--- | :--- |
| `AuthResponseDto` | `token` (JWT String), `tokenType` ("Bearer"), `userId`, `email`, `role`, `expiresInSeconds`. |
| `BuyerResponseDto` | `id`, `identityDocument`, `fullName`, `email`, `role`, `status`, `commercialStatus`, `virtualBalance`, `completedOrdersCount`, `rating`, `primaryAddress`, `additionalAddresses`. |
| `SellerResponseDto` | `id`, `identityDocument`, `fullName`, `email`, `role`, `status`, `businessName`, `taxId`, `sellerStatus`, `businessDescription`, `rating`, `incorporationDate`, `activeProductsCount`, `warehouseIds`. |
| `ProductResponseDto` | `id`, `vendorId`, `name`, `description`, `category`, `basePrice`, `type`, `status`, `variants`, `imageUrls`, `averageRating`, `totalSales`, `publishedAt`, `updatedAt`. |
| `ShoppingCartResponseDto` | `id`, `buyerId`, `items` (List de `CartItemResponseDto`), `totalItems`, `createdAt`, `lastModifiedAt`. |
| `OrderResponseDto` | `id`, `buyerId`, `status`, `subtotal`, `taxes`, `discounts`, `totalAmount`, `additionalNotes`, `lines` (List de `OrderLineResponseDto`), `createdAt`, `updatedAt`. |
| `ProductStockResponseDto` | `id`, `productId`, `warehouseId`, `variantId`, `availableQuantity`, `reservedQuantity`, `lastUpdatedAt`. |
| `WarehouseResponseDto` | `id`, `name`, `vendorId`, `type`, `status`, `description`, `maxCapacity`, `address` (`AddressDto`), `createdAt`, `updatedAt`. |
| `AuditLogResponseDto` | `id`, `actorId`, `actorRole`, `action`, `entityName`, `entityId`, `details`, `timestamp`. |
| `ErrorResponseDto` | `timestamp`, `status`, `error`, `message`, `path`, `requestId`, `validationErrors` (Map<String, String> opcional). |

---

## 4. Firmas e Inyecciones de Dependencias de los 6 Controladores REST

### 4.1 `PublicAccessController` (`/api/v1/auth`, `/api/v1/public`)
- **Dependencias Inyectadas**:
  - `PublicAccessPort publicAccessPort`
  - `JwtServicePort jwtServicePort`
- **Firmas de Métodos**:
  ```java
  @PostMapping("/api/v1/auth/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request);

  @PostMapping("/api/v1/auth/logout")
  public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthenticatedUserPrincipal principal);

  @PostMapping("/api/v1/auth/register/buyer")
  public ResponseEntity<BuyerResponseDto> registerBuyer(@Valid @RequestBody RegisterBuyerRequestDto request);

  @GetMapping("/api/v1/public/products")
  public ResponseEntity<List<ProductResponseDto>> getPublicCatalog();

  @GetMapping("/api/v1/public/products/{id}")
  public ResponseEntity<ProductResponseDto> getPublicProductDetails(@PathVariable String id);
  ```

### 4.2 `BuyerController` (`/api/v1/buyer`)
- **Dependencias Inyectadas**:
  - `BuyerPort buyerPort`
- **Firmas de Métodos**:
  ```java
  @GetMapping("/cart")
  public ResponseEntity<ShoppingCartResponseDto> getCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal);

  @PostMapping("/cart/items")
  public ResponseEntity<ShoppingCartResponseDto> addItemToCart(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @Valid @RequestBody AddItemToCartRequestDto request);

  @DeleteMapping("/cart/items/{productId}/{variantId}")
  public ResponseEntity<ShoppingCartResponseDto> removeItemFromCart(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable String productId,
          @PathVariable String variantId);

  @DeleteMapping("/cart")
  public ResponseEntity<Void> clearCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal);

  @PostMapping("/orders/checkout")
  public ResponseEntity<OrderResponseDto> checkoutCart(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @Valid @RequestBody CheckoutOrderRequestDto request);

  @GetMapping("/orders")
  public ResponseEntity<List<OrderResponseDto>> getMyOrders(@AuthenticationPrincipal AuthenticatedUserPrincipal principal);

  @GetMapping("/orders/{orderId}")
  public ResponseEntity<OrderResponseDto> getOrderDetails(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable Long orderId);

  @PostMapping("/orders/{orderId}/cancel")
  public ResponseEntity<Void> cancelOrder(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable Long orderId,
          @Valid @RequestBody CancelOrderRequestDto request);
  ```

### 4.3 `SellerController` (`/api/v1/seller`)
- **Dependencias Inyectadas**:
  - `SellerPort sellerPort`
- **Firmas de Métodos**:
  ```java
  @PostMapping("/products")
  public ResponseEntity<ProductResponseDto> createProduct(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @Valid @RequestBody CreateProductRequestDto request);

  @PutMapping("/products/{id}")
  public ResponseEntity<ProductResponseDto> updateProduct(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable String id,
          @Valid @RequestBody UpdateProductRequestDto request);

  @PatchMapping("/products/{id}/publish")
  public ResponseEntity<Void> publishProduct(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable String id);

  @PatchMapping("/products/{id}/suspend")
  public ResponseEntity<Void> suspendProduct(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable String id);

  @GetMapping("/products")
  public ResponseEntity<List<ProductResponseDto>> getMyProducts(@AuthenticationPrincipal AuthenticatedUserPrincipal principal);

  @GetMapping("/inventory")
  public ResponseEntity<List<ProductStockResponseDto>> getMyInventory(@AuthenticationPrincipal AuthenticatedUserPrincipal principal);
  ```

### 4.4 `LogisticsOperatorController` (`/api/v1/logistics`)
- **Dependencias Inyectadas**:
  - `LogisticsOperatorPort logisticsOperatorPort`
- **Firmas de Métodos**:
  ```java
  @PostMapping("/inventory/inflow")
  public ResponseEntity<ProductStockResponseDto> registerInflow(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @Valid @RequestBody RegisterStockMovementRequestDto request);

  @PostMapping("/inventory/adjustment")
  public ResponseEntity<ProductStockResponseDto> registerAdjustment(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @Valid @RequestBody RegisterStockMovementRequestDto request);

  @GetMapping("/warehouses/{warehouseId}/stock")
  public ResponseEntity<List<ProductStockResponseDto>> getWarehouseStock(@PathVariable String warehouseId);

  @PostMapping("/orders/{orderId}/dispatch")
  public ResponseEntity<Void> dispatchOrder(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable Long orderId,
          @Valid @RequestBody DispatchOrderRequestDto request);

  @PostMapping("/orders/{orderId}/deliver")
  public ResponseEntity<Void> deliverOrder(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable Long orderId);
  ```

### 4.5 `AdministratorController` (`/api/v1/admin`)
- **Dependencias Inyectadas**:
  - `AdministratorPort administratorPort`
- **Firmas de Métodos**:
  ```java
  @PostMapping("/sellers")
  public ResponseEntity<SellerResponseDto> registerSeller(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @Valid @RequestBody RegisterSellerRequestDto request);

  @PostMapping("/warehouses")
  public ResponseEntity<WarehouseResponseDto> createWarehouse(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @Valid @RequestBody CreateWarehouseRequestDto request);

  @PatchMapping("/users/{userId}/status")
  public ResponseEntity<Void> changeUserStatus(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @PathVariable String userId,
          @Valid @RequestBody ChangeUserStatusRequestDto request);

  @GetMapping("/users")
  public ResponseEntity<List<UserResponseDto>> listUsers();

  @GetMapping("/sellers")
  public ResponseEntity<List<SellerResponseDto>> listSellers();
  ```

### 4.6 `SupervisorController` (`/api/v1/supervisor`)
- **Dependencias Inyectadas**:
  - `SupervisorPort supervisorPort`
- **Firmas de Métodos**:
  ```java
  @GetMapping("/audit-logs")
  public ResponseEntity<List<AuditLogResponseDto>> getAuditLogs(
          @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
          @RequestParam(required = false) String resourceFilter);

  @GetMapping("/orders")
  public ResponseEntity<List<OrderResponseDto>> getAllOrders(@AuthenticationPrincipal AuthenticatedUserPrincipal principal);

  @GetMapping("/inventory")
  public ResponseEntity<List<ProductStockResponseDto>> getInventorySummary(@AuthenticationPrincipal AuthenticatedUserPrincipal principal);
  ```

---

## 5. Mapeo Exhaustivo de Excepciones y `GlobalExceptionHandler`

El controlador de excepciones se ubicará en `application.adapters.rest.exceptions.GlobalExceptionHandler` con `@RestControllerAdvice`.
Garantiza el formato `ErrorResponseDto` y la inyección obligatoria de la cabecera `X-Request-Id`.

| Excepción Capturada | Código HTTP | Mensaje / Diagnóstico |
| :--- | :---: | :--- |
| `InvalidCredentialsException` | `401 UNAUTHORIZED` | Credenciales incorrectas o usuario no activo. |
| `EntityNotFoundException`<br>`ProductNotAvailableException` | `404 NOT_FOUND` | Recurso o entidad de negocio no encontrada en el repositorio. |
| `NoResourceFoundException` | `404 NOT_FOUND` | Ruta o endpoint HTTP no existente en el servidor. |
| `UnauthorizedRoleOperationException`<br>`AccessDeniedException` | `403 FORBIDDEN` | El rol o identidad autenticada no tiene permisos sobre la operación. |
| `NegativeStockException`<br>`InvalidOrderStateException`<br>`InvalidUserStatusException`<br>`DomainException` | `409 CONFLICT` | Violación de regla de negocio o conflicto de estado del ciclo de vida. |
| `MethodArgumentNotValidException` | `400 BAD_REQUEST` | Fallo de validación Bean Validation; incluye mapa `validationErrors`. |
| `HttpMessageNotReadableException`<br>`HandlerMethodValidationException`<br>`IllegalArgumentException` | `400 BAD_REQUEST` | Cuerpo JSON malformado o parámetro sintácticamente inválido. |
| `Exception` (cualquier error no controlado) | `500 INTERNAL_SERVER_ERROR` | Error interno inesperado con ocultación de trazas sensibles. |

---

## 6. Diseño de la Suite de Pruebas MockMvc (`RestControllersTest`)

- **Ubicación**: `nexusmarcket/src/test/java/application/adapters/rest/RestControllersTest.java`.
- **Estrategia**:
  - Pruebas web con `MockMvc` configurando controladores, mappers y el `GlobalExceptionHandler`.
  - Simulación aislada de los 6 puertos de entrada (`PublicAccessPort`, `BuyerPort`, `SellerPort`, `LogisticsOperatorPort`, `AdministratorPort`, `SupervisorPort`) y `JwtServicePort` mediante `Mockito`.
- **Casos de Prueba Planificados**:
  1. `PublicAccessController`:
     - Login exitoso (200 OK + `AuthResponseDto`).
     - Login fallido por credenciales inválidas (401 UNAUTHORIZED + `ErrorResponseDto`).
     - Registro de comprador válido (201 CREATED + `BuyerResponseDto`).
     - Registro de comprador con email inválido (400 BAD REQUEST + mapa `validationErrors`).
     - Consulta de catálogo público (200 OK).
     - Detalle de producto público no encontrado (404 NOT FOUND).
  2. `BuyerController`:
     - Consulta de carrito (200 OK).
     - Agregar ítem con cantidad negativa o cero (400 BAD REQUEST).
     - Checkout exitoso (201 CREATED + `OrderResponseDto`).
     - Checkout fallido por stock insuficiente (409 CONFLICT).
  3. `SellerController`:
     - Creación de producto válido (201 CREATED).
     - Publicación y suspensión de producto (200 OK).
  4. `LogisticsOperatorController`:
     - Entrada manual de existencias a bodega (200 OK).
     - Despacho de pedido con número de guía (200 OK).
  5. `AdministratorController`:
     - Registro de vendedor y creación de bodega (201 CREATED).
     - Cambio de estado de usuario (200 OK).
  6. `SupervisorController`:
     - Consulta de bitácora de auditoría y stock general (200 OK).
  7. `GlobalExceptionHandler`:
     - Verificación del header `X-Request-Id` y cuerpo JSON uniforme en todos los escenarios de error.

---

## 7. Mapeo de Criterios de Aceptación a Componentes (Trazabilidad)

| Criterio | Componente Principal | Test en MockMvc |
| :--- | :--- | :--- |
| **AC-01** | `PublicAccessController`, `AuthRestMapper`, DTOs de Auth | `testLoginSuccess`, `testLoginInvalidCredentials`, `testRegisterBuyer` |
| **AC-02** | `BuyerController`, `BuyerRestMapper`, DTOs de Carrito y Orden | `testGetCart`, `testAddItemToCart`, `testCheckoutOrderSuccess`, `testCheckoutNegativeStock` |
| **AC-03** | `SellerController`, `SellerRestMapper`, DTOs de Producto | `testCreateProduct`, `testPublishProduct`, `testGetSellerInventory` |
| **AC-04** | `LogisticsOperatorController`, `LogisticsRestMapper`, DTOs de Stock | `testRegisterInflow`, `testDispatchOrder`, `testDeliverOrder` |
| **AC-05** | `AdministratorController`, `AdminRestMapper`, DTOs de Admin | `testRegisterSeller`, `testCreateWarehouse`, `testChangeUserStatus` |
| **AC-06** | `SupervisorController`, `SupervisorRestMapper`, DTOs de Auditoría | `testGetAuditLogs`, `testGetAllOrders`, `testGetInventorySummary` |
| **AC-07** | Paquetes `dtos/` y `mappers/` | Validaciones Bean Validation y serializaciones JSON |
| **AC-08** | `GlobalExceptionHandler`, `ErrorResponseDto` | Validación de códigos 400, 401, 403, 404, 409 y header `X-Request-Id` |
| **AC-09** | Quality Gate en Docker (`nexusmarcket-app mvn test`) | 100% de tests passing (46 previos + nuevos de REST) |

---

## 8. Estrategia de Rollback y Recuperación

- La capa REST es puramente consumidora de los puertos de entrada existentes (`ports.in`); no altera contratos de dominio ni esquemas de base de datos relacionales o documentales.
- Si una prueba o controlador presenta incompatibilidad durante el ensamblaje, la reversión se limita a los archivos dentro del paquete `application.adapters.rest.*` y su archivo de pruebas correspondiente, sin riesgo de corrupción en los casos de uso ni en la persistencia probada en las Fases 1 a 5.
