# [SPEC-006] Fase 6: Capa de Presentación REST (adapters/rest/)

Fecha: 2026-10-25  
Tipo: feature  
Estado: In Progress  

## 0. Fase de Clarificación y Supuestos

- **Arquitectura Hexagonal y Aislamiento de Capas**:
  - La capa de presentación REST reside exclusivamente en `application.adapters.rest.*` y actúa como un adaptador primario de entrada (driving adapter).
  - Los controladores REST invocan únicamente los puertos de entrada (`application.domain.ports.in.*`) que fueron implementados por los casos de uso (`application.adapters.useCases.*`) en la Fase 3.
  - Las entidades y modelos de dominio en `application.domain.*` permanecen 100% agnósticos y desacoplados de anotaciones web (`@RestController`, `@RequestMapping`, `@Valid`, `@RequestBody`, etc.) o serializadores HTTP.
  - La transformación entre DTOs de transporte HTTP y modelos o comandos de dominio se realiza mediante mappers REST especializados en `application.adapters.rest.mappers.*`.
- **Estrategia de Seguridad y Autorización Basada en Roles**:
  - En concordancia con [SecurityConfig.java](../../nexusmarcket/src/main/java/application/adapters/security/SecurityConfig.java), los endpoints se segmentan por prefijo URI protegido por Spring Security:
    - `/api/v1/auth/**` y `/api/v1/public/**`: Acceso anónimo (`permitAll()`).
    - `/api/v1/buyer/**`: Acceso restringido a usuarios autenticados con rol `ROLE_BUYER`.
    - `/api/v1/seller/**`: Acceso restringido a usuarios con rol `ROLE_SELLER`.
    - `/api/v1/logistics/**`: Acceso restringido a usuarios con rol `ROLE_LOGISTICS_OPERATOR`.
    - `/api/v1/admin/**`: Acceso restringido a usuarios con rol `ROLE_ADMINISTRATOR`.
    - `/api/v1/supervisor/**`: Acceso restringido a usuarios con rol `ROLE_SUPERVISOR`.
  - La identidad del usuario autenticado se extrae de forma limpia y tipada mediante `@AuthenticationPrincipal AuthenticatedUserPrincipal principal`, obteniendo su identificador único de dominio (`principal.getUser().getId()`).
- **Trazabilidad y Manejo Centralizado de Excepciones**:
  - Se implementa `GlobalExceptionHandler` con `@RestControllerAdvice` siguiendo el estándar del referente `bank`.
  - Cada respuesta de error incluirá un identificador unívoco de solicitud (`requestId`), obtenido de la cabecera HTTP `X-Request-Id` o autogenerado vía `UUID.randomUUID()`, devuelto además en la cabecera de respuesta HTTP.
  - Las excepciones de dominio (`DomainException`, `NegativeStockException`, `InvalidOrderStateException`, `EntityNotFoundException`, etc.) se mapean a códigos de estado HTTP semánticos (400, 401, 403, 404, 409).

## 1. Necesidad y Alcance (Need and Scope)

- **Quién**: Clientes HTTP externos (Frontend SPA web, aplicaciones móviles, integradores y herramientas de pruebas de API).
- **Qué**: Implementar la capa de controladores REST por rol, DTOs de entrada/salida validados con Bean Validation, mappers bidireccionales y el manejador global de excepciones con respuesta estructurada.
- **Por qué**: Exponer todas las capacidades del negocio y de los casos de uso a través de una API RESTful estándar, robusta, segura y bien documentada, garantizando el aislamiento del dominio y la consistencia en el tratamiento de errores.

## 2. Criterios de Aceptación y No-Objetivos

### Criterios de Aceptación

- [ ] **AC-01**: **Controlador de Acceso Público y Autenticación (`PublicAccessController`)**
  - Implementar endpoints para login, logout, registro de comprador y consulta de catálogo público:
    - `POST /api/v1/auth/login`: Autentica credenciales y retorna token JWT y datos del usuario (`AuthResponseDto`).
    - `POST /api/v1/auth/logout`: Revoca la sesión del usuario autenticado invalidando su versión de token.
    - `POST /api/v1/auth/register/buyer`: Autoregistro público de comprador validando campos de entrada (`RegisterBuyerRequestDto`).
    - `GET /api/v1/public/products`: Consulta del catálogo de productos publicados.
    - `GET /api/v1/public/products/{id}`: Detalle público de un producto por identificador.
  - Inyectar [PublicAccessPort.java](../../nexusmarcket/src/main/java/application/domain/ports/in/PublicAccessPort.java).

- [ ] **AC-02**: **Controlador del Rol Comprador (`BuyerController`)**
  - Implementar endpoints para la experiencia del comprador bajo el prefijo `/api/v1/buyer/**`:
    - `GET /api/v1/buyer/cart`: Consulta del carrito activo del comprador autenticado.
    - `POST /api/v1/buyer/cart/items`: Agregar ítem con variante y cantidad al carrito (`AddItemToCartRequestDto`).
    - `DELETE /api/v1/buyer/cart/items/{productId}/{variantId}`: Eliminar un ítem específico del carrito.
    - `DELETE /api/v1/buyer/cart`: Vaciar completamente el carrito de compras.
    - `POST /api/v1/buyer/orders/checkout`: Procesar checkout creando orden a partir del carrito con dirección de entrega (`CheckoutOrderRequestDto`).
    - `GET /api/v1/buyer/orders`: Listar historial de órdenes de compra del comprador.
    - `GET /api/v1/buyer/orders/{orderId}`: Consultar detalle de una orden específica.
    - `POST /api/v1/buyer/orders/{orderId}/cancel`: Cancelar orden pendiente con justificación (`CancelOrderRequestDto`).
  - Inyectar [BuyerPort.java](../../nexusmarcket/src/main/java/application/domain/ports/in/BuyerPort.java).

- [ ] **AC-03**: **Controlador del Rol Vendedor (`SellerController`)**
  - Implementar endpoints para la gestión comercial bajo el prefijo `/api/v1/seller/**`:
    - `POST /api/v1/seller/products`: Registrar nuevo producto con variantes y atributos (`CreateProductRequestDto`).
    - `PUT /api/v1/seller/products/{id}`: Actualizar catálogo y precios del producto existente (`UpdateProductRequestDto`).
    - `PATCH /api/v1/seller/products/{id}/publish`: Cambiar estado del producto a publicado.
    - `PATCH /api/v1/seller/products/{id}/suspend`: Suspender temporalmente producto del catálogo.
    - `GET /api/v1/seller/products`: Listar productos propios asociados al vendedor autenticado.
    - `GET /api/v1/seller/inventory`: Consultar existencias e inventario consolidado de sus productos.
  - Inyectar [SellerPort.java](../../nexusmarcket/src/main/java/application/domain/ports/in/SellerPort.java).

- [ ] **AC-04**: **Controlador del Rol Operador Logístico (`LogisticsOperatorController`)**
  - Implementar endpoints para operaciones de bodega y despacho bajo el prefijo `/api/v1/logistics/**`:
    - `POST /api/v1/logistics/inventory/inflow`: Registrar entrada de existencias a bodega (`RegisterStockMovementRequestDto`).
    - `POST /api/v1/logistics/inventory/adjustment`: Registrar merma o ajuste de inventario.
    - `GET /api/v1/logistics/warehouses/{warehouseId}/stock`: Consultar existencias físicas por bodega.
    - `POST /api/v1/logistics/orders/{orderId}/dispatch`: Despachar pedido asignando guía de rastreo (`DispatchOrderRequestDto`).
    - `POST /api/v1/logistics/orders/{orderId}/deliver`: Confirmar entrega final del pedido al comprador.
  - Inyectar [LogisticsOperatorPort.java](../../nexusmarcket/src/main/java/application/domain/ports/in/LogisticsOperatorPort.java).

- [ ] **AC-05**: **Controlador del Rol Administrador (`AdministratorController`)**
  - Implementar endpoints de administración de plataforma bajo el prefijo `/api/v1/admin/**`:
    - `POST /api/v1/admin/sellers`: Registrar y dar de alta a un vendedor comercial (`RegisterSellerRequestDto`).
    - `POST /api/v1/admin/warehouses`: Dar de alta una nueva bodega física (`CreateWarehouseRequestDto`).
    - `PATCH /api/v1/admin/users/{userId}/status`: Cambiar estado operativo/comercial de un usuario (`ChangeUserStatusRequestDto`).
    - `GET /api/v1/admin/users`: Listar todos los usuarios registrados en el sistema.
    - `GET /api/v1/admin/sellers`: Listar todos los vendedores comerciales registrados.
  - Inyectar [AdministratorPort.java](../../nexusmarcket/src/main/java/application/domain/ports/in/AdministratorPort.java).

- [ ] **AC-06**: **Controlador del Rol Supervisor (`SupervisorController`)**
  - Implementar endpoints para auditoría y trazabilidad bajo el prefijo `/api/v1/supervisor/**`:
    - `GET /api/v1/supervisor/audit-logs`: Consultar bitácora inmutable de auditoría con filtro opcional de entidad/recurso.
    - `GET /api/v1/supervisor/orders`: Consultar la totalidad de pedidos globales del sistema.
    - `GET /api/v1/supervisor/inventory`: Consultar resumen consolidado de existencias en todas las bodegas.
  - Inyectar [SupervisorPort.java](../../nexusmarcket/src/main/java/application/domain/ports/in/SupervisorPort.java).

- [ ] **AC-07**: **DTOs de Transporte y Mappers REST Bidireccionales**
  - Implementar DTOs de solicitud (`requests/`) con validaciones declarativas Bean Validation (`@NotBlank`, `@Email`, `@NotNull`, `@Positive`, `@Min`).
  - Implementar DTOs de respuesta (`responses/`) inmutables con datos limpios para serialización JSON.
  - Implementar mappers REST (`mappers/`) para convertir DTOs a modelos/comandos y viceversa sin introducir dependencias en el dominio.

- [ ] **AC-08**: **Manejador Global de Excepciones (`GlobalExceptionHandler`) y Respuesta Estandarizada**
  - Implementar `@RestControllerAdvice` en `application.adapters.rest.exceptions`.
  - Retornar cuerpo unificado con `timestamp`, `status`, `message`, `path`, y `requestId`.
  - Mapear de forma determinista:
    - 401 Unauthorized: `InvalidCredentialsException`.
    - 404 Not Found: `EntityNotFoundException`, `ProductNotAvailableException`, `NoResourceFoundException`.
    - 403 Forbidden: `UnauthorizedRoleOperationException`, `AccessDeniedException`.
    - 409 Conflict: `InvalidOrderStateException`, `InvalidUserStatusException`, `NegativeStockException`, `DomainException`.
    - 400 Bad Request: `MethodArgumentNotValidException`, `HttpMessageNotReadableException`, `HandlerMethodValidationException`.
    - 500 Internal Server Error: Excepciones no controladas con mensaje genérico seguro.
  - Soportar e inyectar cabecera `X-Request-Id` en las respuestas.

- [ ] **AC-09**: **Quality Gate y Suite de Pruebas Web (`RestControllersTest`)**
  - Implementar pruebas unitarias y de integración de controladores utilizando `MockMvc`.
  - Validar códigos de respuesta HTTP (200, 201, 204, 400, 401, 403, 404, 409).
  - Ejecutar verificación completa en contenedor Docker (`docker compose exec -T nexusmarcket-app mvn test`) con 0 fallos y 0 errores sobre la suite total.

### No-Objetivos (Non-Goals)

- No incluye la carga de datos semilla iniciales ni datos de prueba de base de datos (`DatabaseSeeder`), lo cual corresponde a la Fase 7.
- No incluye componentes de interfaz de usuario ni frontend web (la fase se enfoca estrictamente en el backend REST API).
- No modifica las clases puras del dominio en `application.domain.*`.

## 3. Controles de Seguridad y Cumplimiento

| Control de Seguridad / Cumplimiento | Requerido? | Aplicación Técnica |
| :--- | :---: | :--- |
| **¿Maneja datos personales sensibles (PII)?** | SÍ | Se manejan correos, documentos de identificación y direcciones físicas. Las contraseñas viajan únicamente en endpoints POST autenticados/cifrados con TLS y nunca se devuelven en DTOs de respuesta. |
| **¿Expone datos entre diferentes tenants o usuarios?** | SÍ | Aislamiento estricto por rol y propiedad: los compradores solo acceden a sus propios carritos y pedidos (`principal.getUser().getId()`); los vendedores solo administran sus propios productos e inventarios. |
| **¿Requiere registro de auditoría?** | SÍ | Las acciones críticas y consultas de auditoría son auditadas y expuestas exclusivamente al rol `SUPERVISOR` a través de `SupervisorController`. |
| **¿Accede o procesa datos sensibles?** | SÍ | Tokens JWT transmitidos vía cabecera `Authorization: Bearer <token>` y validados en `JwtAuthenticationFilter`. Cero almacenamiento de contraseñas en texto claro. |

## 4. Experiencia y Límites

- **Estandarización de Códigos HTTP**:
  - `200 OK`: Operaciones exitosas de consulta o actualización sincrónica.
  - `201 CREATED`: Operaciones exitosas de creación de recursos (`POST`).
  - `204 NO CONTENT`: Operaciones exitosas sin cuerpo de retorno (e.g., `logout`, `clearCart`, cancelaciones).
  - `400 BAD REQUEST`: Errores sintácticos, payloads malformados o fallos de validación Bean Validation.
  - `401 UNAUTHORIZED`: Credenciales inválidas o ausencia de token Bearer en endpoints protegidos.
  - `403 FORBIDDEN`: Usuario autenticado sin los permisos o rol requeridos para la ruta solicitada.
  - `404 NOT FOUND`: Rutas inexistentes o identificadores de entidad no encontrados en el almacén de datos.
  - `409 CONFLICT`: Violación de invariantes de negocio o estados no permitidos del ciclo de vida.
  - `500 INTERNAL SERVER ERROR`: Fallos imprevistos del servidor sin filtrado de información interna sensible.

## 5. Trazabilidad

- **AC-01** -> `PublicAccessController.java`, DTOs de auth/públicos, `PublicAccessRestMapper.java`.
- **AC-02** -> `BuyerController.java`, DTOs de carrito/pedido, `BuyerRestMapper.java`.
- **AC-03** -> `SellerController.java`, DTOs de producto/inventario, `SellerRestMapper.java`.
- **AC-04** -> `LogisticsOperatorController.java`, DTOs de inventario/despacho, `LogisticsRestMapper.java`.
- **AC-05** -> `AdministratorController.java`, DTOs de usuario/bodega/vendedor, `AdminRestMapper.java`.
- **AC-06** -> `SupervisorController.java`, DTOs de auditoría/supervisión, `SupervisorRestMapper.java`.
- **AC-07** -> Paquetes `application.adapters.rest.dtos.*` y `application.adapters.rest.mappers.*`.
- **AC-08** -> `application.adapters.rest.exceptions.GlobalExceptionHandler.java` y `ErrorResponseDto.java`.
- **AC-09** -> `application.adapters.rest.RestControllersTest.java`.
