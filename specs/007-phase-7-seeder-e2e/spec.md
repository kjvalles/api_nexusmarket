# [SPEC-007] Fase 7: Carga Inicial de Datos Semilla (DatabaseSeeder) y Verificación End-to-End

Fecha: 2026-10-25  
Tipo: feature  
Estado: In Progress  

## 0. Fase de Clarificación y Supuestos

- **Arquitectura de Semillado Idempotente**:
  - El componente de inicialización de datos se ubicará en `application.infrastructure.seed.DatabaseSeeder` implementando `CommandLineRunner` y `@Order(1)`.
  - La ejecución será estrictamente idempotente: antes de persistir cualquier registro (usuario, producto, bodega, pedido o log), se verificará si la clave única ya existe (e.g. `email`, `identityDocument`, `id`), garantizando que reinicios sucesivos del contenedor Docker no dupliquen datos ni provoquen colisiones de integridad relacional o documental.
  - La contraseña compartida para todas las cuentas semilla en ambientes de prueba y desarrollo será `Password123*`, encriptada utilizando [`PasswordServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/PasswordServicePort.java) (BCrypt).
- **Semillado Políglota Coherente**:
  - **MySQL (JPA)**: Persistirá usuarios (`User`, `Buyer`, `Seller`, `DefaultUser`), bodegas físicas (`Warehouse`), existencias de inventario (`ProductStock`), movimientos kardex (`InventoryMovement`) y pedidos con líneas congeladas (`Order`, `OrderLine`).
  - **MongoDB**: Persistirá el catálogo de productos con variantes embebidas (`Product`), carritos de compras activos (`ShoppingCart`) y registros inmutables de auditoría (`AuditLog`).
  - Se mantendrá coherencia referencial cruzada (los `productId` y `variantId` sembrados en MongoDB coincidirán con los `productId` y `variantId` de los registros de `ProductStock` en MySQL).
- **Verificación End-to-End (E2E)**:
  - Se creará una suite de integración comprensiva `EndToEndPlatformIntegrationTest` en `src/test/java/application/` que valide todo el flujo comercial de la plataforma con los servicios de persistencia reales (MySQL y MongoDB) y los controladores REST.

## 1. Necesidad y Alcance (Need and Scope)

- **Quién**: Desarrolladores, evaluadores técnicos, pipelines de CI/CD y clientes frontend que interactúan con la plataforma NexusMarket.
- **Qué**: Implementar el cargador de datos semilla `DatabaseSeeder` que inicialice todas las entidades y roles del sistema al arrancar la aplicación, y la suite de pruebas de integración End-to-End que verifique la trazabilidad transaccional completa.
- **Por qué**: Proveer un entorno operativo funcional desde el primer arranque, donde el 100% de los endpoints REST puedan ser consumidos con credenciales y datos coherentes, y certificar formalmente que todas las capas hexagonales (dominio, aplicación, seguridad, persistencia y presentación REST) operan de manera armónica.

## 2. Criterios de Aceptación y No-Objetivos

### Criterios de Aceptación

- [ ] **AC-01**: **Sembrador de Cuentas de Usuario y Roles del Sistema**
  - Implementar en `DatabaseSeeder` la creación idempotente de cuentas de prueba para los 5 roles del sistema con clave encriptada `Password123*`:
    - Administrador: `admin@nexusmarcket.com` (rol `ADMINISTRATOR`, documento `CC1000000001`, activo).
    - Supervisor: `supervisor@nexusmarcket.com` (rol `SUPERVISOR`, documento `CC1000000002`, activo).
    - Operador Logístico: `logistica@nexusmarcket.com` (rol `LOGISTICS_OPERATOR`, documento `CC1000000003`, activo).
    - Vendedor: `vendedor@nexusmarcket.com` (rol `SELLER`, NIT `NIT-900123456`, razón social "Tech Store Colombia", activo).
    - Comprador: `comprador@nexusmarcket.com` (rol `BUYER`, documento `CC1000000004`, saldo virtual, dirección física en Bogotá, activo).

- [ ] **AC-02**: **Sembrador de Infraestructura Logística (Bodegas)**
  - Crear en MySQL mediante [`WarehouseRepositoryPort`](../../nexusmarcket/src/main/java/application/domain/ports/out/WarehouseRepositoryPort.java) al menos dos bodegas físicas:
    - Bodega Central Marketplace (`wh-central-01`, tipo `MARKETPLACE`, capacidad 10000 unidades, dirección Bogotá).
    - Bodega Vendedor Tech (`wh-seller-01`, tipo `SELLER`, vinculada al vendedor semilla, capacidad 2500 unidades, dirección Medellín).

- [ ] **AC-03**: **Sembrador de Catálogo Políglota de Productos (MongoDB)**
  - Crear en MongoDB mediante [`ProductRepositoryPort`](../../nexusmarcket/src/main/java/application/domain/ports/out/ProductRepositoryPort.java):
    - Al menos 3 productos físicos publicados con variantes activas (SKUs, colores, precios base y adicionales): e.g. "Laptop Gamer Pro", "Teclado Mecánico RGB", "Audífonos Inalámbricos ANC".
    - Al menos 1 producto digital publicado: "Licencia Suite Antivirus Pro".
    - Al menos 1 producto en estado suspendido para validar filtros de visibilidad pública.

- [ ] **AC-04**: **Sembrador de Inventario y Movimientos Kardex (MySQL JPA)**
  - Crear en MySQL mediante [`ProductStockRepositoryPort`](../../nexusmarcket/src/main/java/application/domain/ports/out/ProductStockRepositoryPort.java) y [`InventoryMovementRepositoryPort`](../../nexusmarcket/src/main/java/application/domain/ports/out/InventoryMovementRepositoryPort.java):
    - Existencias positivas disponibles para las variantes físicas en las bodegas sembradas.
    - Registro histórico de movimiento de entrada manual (`MANUAL_ENTRY`) asociado al operador logístico.

- [ ] **AC-05**: **Sembrador de Carrito y Órdenes de Prueba**
  - Carrito en MongoDB ([`ShoppingCartRepositoryPort`](../../nexusmarcket/src/main/java/application/domain/ports/out/ShoppingCartRepositoryPort.java)): Carrito activo con ítems para el comprador semilla.
  - Órdenes en MySQL ([`OrderRepositoryPort`](../../nexusmarcket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java)):
    - Una orden en estado `DELIVERED` con líneas congeladas y dirección de entrega.
    - Una orden en estado `DISPATCHED` con guía de rastreo y transportadora.
    - Una orden en estado `PENDING_PAYMENT` para pruebas de cancelación.

- [ ] **AC-06**: **Sembrador de Bitácora de Auditoría (MongoDB)**
  - Crear en MongoDB mediante [`AuditLogRepositoryPort`](../../nexusmarcket/src/main/java/application/domain/ports/out/AuditLogRepositoryPort.java) eventos representativos de auditoría (registro de vendedor, creación de bodegas e inicialización del sistema).

- [ ] **AC-07**: **Suite de Verificación End-to-End (`EndToEndPlatformIntegrationTest`)**
  - Implementar prueba de integración integral que verifique el flujo comercial completo:
    1. Login de usuario comprador y obtención de JWT.
    2. Consulta de catálogo público con productos sembrados.
    3. Adición de ítems al carrito y checkout con cálculo de totales.
    4. Consulta y descuento de inventario en bodegas.
    5. Despacho y entrega por parte del operador logístico.
    6. Verificación de bitácora de auditoría por el supervisor.
  - Calidad de código: Paso limpio de todas las pruebas en Docker (`docker compose exec -T nexusmarcket-app mvn test`) con 0 fallos y 0 errores.

### No-Objetivos (Non-Goals)

- No incluye pasarelas de pago reales externas en vivo (Stripe/PayPal en producción); se valida el ciclo comercial a través del modelo de órdenes y saldo virtual.
- No reemplaza la configuración de despliegue a producción (los datos semilla están orientados a desarrollo, staging y evaluación funcional).

## 3. Controles de Seguridad y Cumplimiento

| Control de Seguridad / Cumplimiento | Requerido? | Aplicación Técnica |
| :--- | :---: | :--- |
| **¿Maneja datos personales sensibles (PII)?** | SÍ | Se utilizan datos sintéticos de prueba (nombres, correos, documentos y direcciones ficticias). Ningún dato personal real es expuesto ni sembrado. |
| **¿Maneja credenciales seguras?** | SÍ | Todas las contraseñas se almacenan procesadas mediante hash BCrypt (`PasswordServicePort`). Cero texto en claro en bases de datos. |
| **¿Requiere registro de auditoría?** | SÍ | El proceso de sembrado genera registros de auditoría en la colección `audit_logs` trazando las operaciones iniciales del sistema. |

## 4. Experiencia y Límites

- **Idempotencia Garantizada**:
  - `DatabaseSeeder` puede ejecutarse múltiples veces (por ejemplo, ante sucesivos `docker compose restart`) sin arrojar excepciones de clave duplicada (`DuplicateKeyException`, `DataIntegrityViolationException`) ni sobreescribir compras realizadas por usuarios.
- **Credenciales Estándar de Demostración**:
  - Todas las cuentas de prueba comparten la contraseña `Password123*` para facilitar pruebas y validación por parte de evaluadores.

## 5. Trazabilidad

- **AC-01** a **AC-06** -> `application.infrastructure.seed.DatabaseSeeder.java`.
- **AC-07** -> `nexusmarcket/src/test/java/application/e2e/EndToEndPlatformIntegrationTest.java`.
