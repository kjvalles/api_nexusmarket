# [TASKS-006] Plan de Tareas — Fase 6: Capa de Presentación REST (adapters/rest/)

- [x] **TASK-01 / AC-07, AC-08**: Crear DTOs de Request y Response con Bean Validation (`@NotBlank`, `@Email`, `@NotNull`, `@Positive`, `@Min`), `ErrorResponseDto` y `GlobalExceptionHandler` con `@RestControllerAdvice` y soporte para `X-Request-Id`.
  - Archivos: `nexusmarcket/src/main/java/application/adapters/rest/dtos/requests/*`, `nexusmarcket/src/main/java/application/adapters/rest/dtos/responses/*`, `nexusmarcket/src/main/java/application/adapters/rest/exceptions/GlobalExceptionHandler.java`.
  - Verificación: Compilación limpia de DTOs y validaciones declarativas.

- [x] **TASK-02 / AC-01**: Implementar `PublicAccessController` y `AuthRestMapper` para endpoints de login, logout, registro de comprador, catálogo y detalle público de productos.
  - Archivos: `nexusmarcket/src/main/java/application/adapters/rest/controllers/PublicAccessController.java`, `nexusmarcket/src/main/java/application/adapters/rest/mappers/AuthRestMapper.java`.
  - Verificación: Endpoints `/api/v1/auth/**` y `/api/v1/public/**` respondiendo 200/201 con tokens y DTOs esperados.

- [x] **TASK-03 / AC-02**: Implementar `BuyerController` y `BuyerRestMapper` para gestión de carrito (`GET`, `POST`, `DELETE`), checkout de orden, historial y cancelación.
  - Archivos: `nexusmarcket/src/main/java/application/adapters/rest/controllers/BuyerController.java`, `nexusmarcket/src/main/java/application/adapters/rest/mappers/BuyerRestMapper.java`.
  - Verificación: Endpoints `/api/v1/buyer/**` vinculados a `BuyerPort` con contexto de usuario autenticado.

- [x] **TASK-04 / AC-03**: Implementar `SellerController` y `SellerRestMapper` para creación, actualización, publicación, suspensión de productos e inventario propio.
  - Archivos: `nexusmarcket/src/main/java/application/adapters/rest/controllers/SellerController.java`, `nexusmarcket/src/main/java/application/adapters/rest/mappers/SellerRestMapper.java`.
  - Verificación: Endpoints `/api/v1/seller/**` vinculados a `SellerPort` con rol `SELLER`.

- [x] **TASK-05 / AC-04**: Implementar `LogisticsOperatorController` y `LogisticsRestMapper` para entradas y ajustes de inventario, consulta de existencias por bodega, despacho con guía y entrega.
  - Archivos: `nexusmarcket/src/main/java/application/adapters/rest/controllers/LogisticsOperatorController.java`, `nexusmarcket/src/main/java/application/adapters/rest/mappers/LogisticsRestMapper.java`.
  - Verificación: Endpoints `/api/v1/logistics/**` vinculados a `LogisticsOperatorPort`.

- [x] **TASK-06 / AC-05**: Implementar `AdministratorController` y `AdminRestMapper` para registro de vendedores, creación de bodegas, cambio de estado de usuarios y listados globales.
  - Archivos: `nexusmarcket/src/main/java/application/adapters/rest/controllers/AdministratorController.java`, `nexusmarcket/src/main/java/application/adapters/rest/mappers/AdminRestMapper.java`.
  - Verificación: Endpoints `/api/v1/admin/**` vinculados a `AdministratorPort`.

- [x] **TASK-07 / AC-06**: Implementar `SupervisorController` y `SupervisorRestMapper` para consulta de auditoría con filtros, trazabilidad global de pedidos y resumen de existencias.
  - Archivos: `nexusmarcket/src/main/java/application/adapters/rest/controllers/SupervisorController.java`, `nexusmarcket/src/main/java/application/adapters/rest/mappers/SupervisorRestMapper.java`.
  - Verificación: Endpoints `/api/v1/supervisor/**` vinculados a `SupervisorPort`.

- [x] **TASK-08 / AC-09**: Implementar la suite de pruebas unitarias y de integración `RestControllersTest` utilizando `MockMvc` para validar códigos HTTP (200, 201, 204, 400, 401, 403, 404, 409), manejo de excepciones y cabeceras `X-Request-Id`.
  - Archivos: `nexusmarcket/src/test/java/application/adapters/rest/RestControllersTest.java`.
  - Verificación: Ejecución de la suite `RestControllersTest` sin fallos (19/19 passing).

- [x] **TASK-09 / AC-01 a AC-09**: Ejecutar la suite completa de pruebas en el contenedor Docker (`docker compose exec -T nexusmarcket-app mvn test`), registrar la matriz de verificación en `verification.md` y completar la especificación 006.
  - Verificación: 100% de pruebas pasando (65/65 passing: 46 previas + 19 nuevas de REST) con 0 errores y 0 fallos.
