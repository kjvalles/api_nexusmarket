# [SPEC-003] Fase 3: Casos de Uso de Aplicación (Adapters / UseCases)

Fecha: 2026-10-25
Estado: ACTIVO
Tipo: feature

## 0. Fase de Clarificación y Supuestos
- **Alineación Arquitectónica:** Se implementan los adaptadores primarios o de entrada (Driving Adapters) en `application.adapters.useCases.*`, siguiendo la arquitectura hexagonal del repositorio guía (`construccion_de_software_2_2026_2/bank`).
- **Separación de Responsabilidades:**
  - Los contratos de entrada ([`domain/ports/in/`](../../nexusmarcket/src/main/java/application/domain/ports/in/)) definen los casos de uso del sistema segregados por rol de actor ([`UserRole`](../../nexusmarcket/src/main/java/application/domain/enums/UserRole.java)).
  - Los servicios de dominio atómicos ([`domain/services/`](../../nexusmarcket/src/main/java/application/domain/services/)) contienen las reglas de negocio puras.
  - Los casos de uso de aplicación (`adapters/useCases/`) implementan las interfaces de los puertos de entrada, orquestan múltiples servicios de dominio y puertos de salida, y serán consumidos posteriormente por los controladores REST.
- **Inyección y Ciclo de Vida Spring:**
  - Las clases de casos de uso se marcan con `@Service` de Spring (`org.springframework.stereotype.Service`) y utilizan inyección por constructor mediante `@RequiredArgsConstructor` de Lombok.
  - Los servicios de dominio puros (desprovistos de anotaciones Spring en su código fuente) se registran como beans o se instancian e inyectan transparentemente para mantener el dominio completamente puro.
- **Seguridad en Casos de Uso:**
  - Las validaciones de propiedad y aislamiento (ej. un comprador solo consulta/cancela sus propios pedidos, un vendedor solo publica/modifica sus propios productos) se aplican a nivel de caso de uso arrojando [`UnauthorizedRoleOperationException`](../../nexusmarcket/src/main/java/application/domain/exceptions/UnauthorizedRoleOperationException.java).

## 1. Need and scope
- **Necesidad:** En la Fase 2 se construyeron los contratos de los puertos de entrada (`ports/in`) y los servicios de dominio atómicos (`domain/services/`). Sin embargo, no existe la capa de orquestación de aplicación que conecte estos componentes para ejecutar los flujos integrados de cada rol del marketplace.
- **Alcance (Scope):**
  - Implementación de 6 casos de uso en `nexusmarcket/src/main/java/application/adapters/useCases/`:
    1. `PublicAccessUseCaseImpl` implementando [`PublicAccessPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/PublicAccessPort.java).
    2. `BuyerUseCaseImpl` implementando [`BuyerPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/BuyerPort.java).
    3. `SellerUseCaseImpl` implementando [`SellerPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/SellerPort.java).
    4. `LogisticsOperatorUseCaseImpl` implementando [`LogisticsOperatorPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/LogisticsOperatorPort.java).
    5. `AdministratorUseCaseImpl` implementando [`AdministratorPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/AdministratorPort.java).
    6. `SupervisorUseCaseImpl` implementando [`SupervisorPort`](../../nexusmarcket/src/main/java/application/domain/ports/in/SupervisorPort.java).
  - Configuración de beans de dominio en `application/adapters/config/DomainServicesConfig.java` para exponer los servicios de dominio puro al contenedor de inversión de control de Spring Boot.
  - Implementación de suite de pruebas unitarias de casos de uso con JUnit 5 en `src/test/java/application/adapters/useCases/`.
  - Verificación de compilación y ejecución de pruebas en Docker Compose (`docker compose exec -T nexusmarcket-app mvn test`).

## 2. Acceptance Criteria and Non-Goals
### Criterios de Aceptación (Acceptance Criteria)
- **AC-01:** Implementación de `PublicAccessUseCaseImpl` en `application.adapters.useCases` implementando `PublicAccessPort`, orquestando login, logout, registro de comprador, consulta de catálogo y detalles de producto público.
- **AC-02:** Implementación de `BuyerUseCaseImpl` en `application.adapters.useCases` implementando `BuyerPort`, orquestando la gestión del carrito (`ShoppingCart`), checkout a pedido (`Order`) con cálculo automático de totales, listado de órdenes del comprador y cancelación con justificación.
- **AC-03:** Implementación de `SellerUseCaseImpl` en `application.adapters.useCases` implementando `SellerPort`, orquestando creación, actualización, publicación y suspensión de productos con validación estricta de propiedad (`vendorId`), y consulta de existencias propias.
- **AC-04:** Implementación de `LogisticsOperatorUseCaseImpl` en `application.adapters.useCases` implementando `LogisticsOperatorPort`, orquestando entradas de inventario, ajustes por merma, transiciones de pedido a despachado/entregado y consulta de stock por bodega.
- **AC-05:** Implementación de `AdministratorUseCaseImpl` en `application.adapters.useCases` implementando `AdministratorPort`, orquestando registro de vendedores, alta de bodegas, cambio de estado de usuarios con invalidación de versión de token y consultas globales.
- **AC-06:** Implementación de `SupervisorUseCaseImpl` en `application.adapters.useCases` implementando `SupervisorPort`, orquestando consulta de bitácoras de auditoría, trazabilidad consolidada de órdenes y resumen general de existencias.
- **AC-07:** Implementación de `DomainServicesConfig` para cablear servicios puros y suite de pruebas unitarias en `src/test/java/application/adapters/useCases/` validando orquestación, manejo de excepciones y cumplimiento del quality gate automatizado (`docker compose exec -T nexusmarcket-app mvn test` con resultado `BUILD SUCCESS`).

### Fuera de Alcance (Non-Goals)
- No incluye adaptadores de persistencia relacional JPA ni de documentos MongoDB (Fase 5).
- No incluye adaptadores de seguridad HTTP ni filtros de Spring Security (Fase 4).
- No incluye controladores REST ni DTOs HTTP (Fase 6).

## 3. Domain applicability
| Security & Compliance Control | Required? | Technical Enforcement |
| :--- | :---: | :--- |
| **¿Accede o procesa datos sensibles (PII)?** | SÍ | Se orquesta el registro y autenticación de usuarios. El caso de uso delega la encriptación a los servicios de dominio y no expone claves en texto plano. |
| **¿Expone datos cross-tenant o cross-role?** | SÍ | Cada caso de uso valida explícitamente que el actor ejecute operaciones únicamente sobre sus propios recursos (`buyerId`, `vendorId`), arrojando `UnauthorizedRoleOperationException` en caso contrario. |
| **¿Requiere audit logging?** | SÍ | `SupervisorUseCaseImpl` expone la consulta de logs de auditoría sin filtrar información confidencial sensible. |
| **Integridad Transaccional de Negocio** | SÍ | Las transacciones de checkout vacían el carrito y crean la orden protegiendo las invariantes del agregado. |

## 4. Experiencia y límites
- **Interfaces de usuario:** N/A justificado. Esta fase construye los casos de uso backend desacoplados de la capa de presentación.
- **Manejo de Errores de Aplicación:** Las excepciones de dominio arrojadas por los servicios atómicos se propagan limpiamente hacia arriba para ser capturadas por el futuro `GlobalExceptionHandler`.

## 5. Traceability
- **AC-01** -> Plan Sección 2 -> `TASK-01` en `tasks.md`
- **AC-02** -> Plan Sección 2 -> `TASK-02` en `tasks.md`
- **AC-03** -> Plan Sección 2 -> `TASK-03` en `tasks.md`
- **AC-04** -> Plan Sección 2 -> `TASK-04` en `tasks.md`
- **AC-05** -> Plan Sección 2 -> `TASK-05` en `tasks.md`
- **AC-06** -> Plan Sección 2 -> `TASK-06` en `tasks.md`
- **AC-07** -> Plan Sección 4 -> `TASK-07` en `tasks.md`
