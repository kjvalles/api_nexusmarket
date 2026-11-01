# [TASKS-003] Tareas de Implementación — Fase 3: Casos de Uso de Aplicación

Lista de tareas accionables derivadas del plan técnico y mapeadas a los criterios de aceptación.

- [x] TASK-01 / AC-01: Implementar `PublicAccessUseCaseImpl` en `application.adapters.useCases` implementando `PublicAccessPort` (login, logout, registro comprador, catálogo público).
- [x] TASK-02 / AC-02: Implementar `BuyerUseCaseImpl` en `application.adapters.useCases` implementando `BuyerPort` (gestión de carrito, checkout a orden, mis pedidos, cancelar pedido).
- [x] TASK-03 / AC-03: Implementar `SellerUseCaseImpl` en `application.adapters.useCases` implementando `SellerPort` (crear, actualizar, publicar, suspender productos con control de propiedad y consultar inventario).
- [x] TASK-04 / AC-04: Implementar `LogisticsOperatorUseCaseImpl` en `application.adapters.useCases` implementando `LogisticsOperatorPort` (ingreso de stock, ajustes de inventario, despacho y entrega de pedidos).
- [x] TASK-05 / AC-05: Implementar `AdministratorUseCaseImpl` en `application.adapters.useCases` implementando `AdministratorPort` (registrar vendedor, crear bodega, cambio de estado de usuario con revocación de tokens).
- [x] TASK-06 / AC-06: Implementar `SupervisorUseCaseImpl` en `application.adapters.useCases` implementando `SupervisorPort` (consulta de logs de auditoría, resumen de inventario y pedidos globales).
- [x] TASK-07 / AC-07: Crear `DomainServicesConfig` en `application.adapters.config` para registrar beans de dominio, implementar suite de pruebas unitarias en `src/test/java/application/adapters/useCases/` y validar ejecución exitosa en Docker con `docker compose exec -T nexusmarcket-app mvn test`.
