# [SPEC-005] Fase 5: Adaptadores de Persistencia Políglota (adapters/persistence/)

Fecha: 2026-10-25
Tipo: feature
Estado: In Progress

## 0. Fase de Clarificación y Supuestos

- **Decisión de Arquitectura**: Siguiendo la Arquitectura Hexagonal y DDD del proyecto y del referente (`bank`), la persistencia se divide en dos tecnologías complementarias (persistencia políglota):
  - **Relacional (MySQL 8.0 vía Spring Data JPA / Hibernate)**: Gestiona entidades transaccionales, coherencia ACID estricta y relaciones estructuradas: Usuarios (`User`), Compradores (`Buyer`), Vendedores (`Seller`), Pedidos y Líneas (`Order`, `OrderLine`), Saldos de Stock (`ProductStock`), Kardex Histórico (`InventoryMovement`) y Bodegas (`Warehouse`).
  - **No Relacional / Documental (MongoDB 7.0 vía Spring Data MongoDB)**: Gestiona agregados flexibles y de alta variabilidad esquemática: Catálogo de Productos con variantes embebidas (`Product`), Carrito de Compras volátil (`ShoppingCart`) y Registro Inmutable de Auditoría (`AuditLog`).
- **Aislamiento del Dominio**: Las clases de dominio en `application.domain.*` permanecen 100% puras (cero dependencias de `jakarta.persistence.*`, `org.springframework.data.*` o anotaciones BSON). Todo mapeo hacia/desde DTOs de base de datos se realiza en mappers dedicados (`*JpaMapper`, `*MongoMapper`).
- **Estrategia de Herencia JPA para Usuarios**: Se implementa `InheritanceType.JOINED` sobre `UserJpaEntity` (tabla `users`), extendida por `BuyerJpaEntity` (tabla `buyers`) y `SellerJpaEntity` (tabla `sellers`), permitiendo consultas polimórficas por ID/email y acceso tipado a atributos específicos.
- **Persistencia de Colecciones en Órdenes**: `OrderJpaEntity` mantiene una relación `@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)` con `OrderLineJpaEntity` (tabla `order_lines`), preservando la integridad del agregado `Order`.
- **Invariante de Inventario**: `ProductStockJpaEntity` indexa de manera única `(product_id, warehouse_id, variant_id)` para garantizar atomicidad y unicidad en la consulta de existencias por variante y bodega.

## 1. Necesidad y Alcance (Need and Scope)

- **Quién**: Casos de uso de aplicación (`application.adapters.useCases.*`), servicios de dominio y capa de seguridad.
- **Qué**: Implementar los 10 puertos de salida de persistencia (`ports.out`) mediante adaptadores tecnológicos concretos en `application.adapters.persistence.jpa` y `application.adapters.persistence.mongodb`.
- **Por qué**: Sustituir los mocks de prueba (`TestOutPortsConfig`) por implementaciones reales capaces de persistir y recuperar el estado transaccional en MySQL y MongoDB, permitiendo que la aplicación opere de extremo a extremo sin perder la pureza del modelo de dominio.

## 2. Criterios de Aceptación y No-Objetivos

### Criterios de Aceptación

- [ ] **AC-01**: **Adaptadores JPA de Usuarios y Roles (`UserJpaAdapter`, `BuyerJpaAdapter`, `SellerJpaAdapter`)**
  - Implementar entidades `UserJpaEntity`, `BuyerJpaEntity`, `SellerJpaEntity`, mappers bidireccionales y repositorios Spring Data JPA.
  - Implementar los puertos `UserRepositoryPort`, `BuyerRepositoryPort` y `SellerRepositoryPort`.
  - Soportar control de versión de tokens JWT (`authTokenVersion`), unicidad de email/identificación y actualización de datos de sesión.
- [ ] **AC-02**: **Adaptador JPA de Pedidos (`OrderJpaAdapter`)**
  - Implementar entidades `OrderJpaEntity` y `OrderLineJpaEntity` con persistencia en cascada de líneas de orden.
  - Implementar `OrderRepositoryPort` con operaciones `save`, `findById`, `findByBuyerId` y `findAll`.
  - Mapear correctamente totales, subtotales, impuestos, notas y estados del ciclo comercial (`OrderStatus`).
- [ ] **AC-03**: **Adaptadores JPA de Inventario y Bodegas (`ProductStockJpaAdapter`, `InventoryMovementJpaAdapter`, `WarehouseJpaAdapter`)**
  - Implementar entidades, mappers y repositorios para `ProductStock`, `InventoryMovement` y `Warehouse`.
  - Implementar `ProductStockRepositoryPort`, `InventoryMovementRepositoryPort` y `WarehouseRepositoryPort`.
  - Mapear atributos de existencias disponibles/reservadas, tipo de movimiento kardex y direcciones físicas de bodegas.
- [ ] **AC-04**: **Adaptador Mongo de Catálogo de Productos (`ProductMongoAdapter`)**
  - Implementar documento `ProductDocument` con variantes (`ProductVariantDocument`) e imágenes embebidas en la colección `products`.
  - Implementar `ProductRepositoryPort` con operaciones `save`, `findById`, `findByVendorId`, `findPublished` y `findPublishedByCategory`.
- [ ] **AC-05**: **Adaptador Mongo de Carritos de Compras (`ShoppingCartMongoAdapter`)**
  - Implementar documento `ShoppingCartDocument` con ítems de carrito (`CartItemDocument`) embebidos en la colección `shopping_carts`.
  - Implementar `ShoppingCartRepositoryPort` con operaciones `save`, `findByBuyerId` y borrado atómico `deleteByBuyerId`.
- [ ] **AC-06**: **Adaptador Mongo de Registro de Auditoría (`AuditLogMongoAdapter`)**
  - Implementar documento `AuditLogDocument` en la colección `audit_logs`.
  - Implementar `AuditLogRepositoryPort` con operaciones `save`, `findByActorId`, `findByEntity` y `findAll`.
- [ ] **AC-07**: **Quality Gate y Suite de Pruebas de Persistencia**
  - Implementar pruebas unitarias y de integración que verifiquen el ciclo de vida y mapeo de cada adaptador.
  - Compilación limpia y paso exitoso de toda la suite en Docker (`docker compose exec -T nexusmarcket-app mvn test`) con 0 fallos y 0 errores.

### No-Objetivos (Non-Goals)
- No incluye controladores REST ni endpoints HTTP (corresponde a la Fase 6).
- No incluye carga de datos semilla iniciales (`DatabaseSeeder`) para ambientes de desarrollo (corresponde a la Fase 7).
- No incluye cambios ni acoplamiento de dependencias de persistencia en las clases de dominio de `application.domain.*`.

## 3. Controles de Seguridad y Cumplimiento

| Control de Seguridad / Cumplimiento | Requerido? | Aplicación Técnica |
| :--- | :---: | :--- |
| **¿Maneja datos personales sensibles (PII)?** | SÍ | Las contraseñas se almacenan únicamente como hashes BCrypt (`UserJpaEntity.password`). No se almacenan contraseñas en texto plano ni en logs. |
| **¿Aisla datos entre vendedores/compradores?** | SÍ | Se conservan los índices y filtros por `buyerId` y `vendorId` en las consultas de repositorios JPA y Mongo. |
| **¿Requiere registro de auditoría?** | SÍ | Se habilita `AuditLogMongoAdapter` para la persistencia no bloqueante de eventos críticos en MongoDB. |
| **¿Control de tokens y sesiones concurrentes?** | SÍ | Se persiste la columna `auth_token_version` en `users` para garantizar que la revocación de tokens JWT sea consistente entre reinicios. |

## 4. Experiencia y Límites

- **Persistencia Transaccional**: Las operaciones de escritura relacionales en JPA utilizan transacciones de base de datos (`@Transactional` a nivel de servicio/caso de uso) para garantizar atomicidad en pedidos y movimientos de stock.
- **Tolerancia a Nulos**: Los mappers JPA y Mongo validan nulos defensivamente retornando `null` o listas vacías según corresponda, evitando `NullPointerException`.
- **Mapeo Tipado**: Los Enums y Value Objects del dominio (`OrderStatus`, `UserRole`, `InventoryMovementType`, `ProductType`, etc.) se mapean hacia/desde cadenas estandarizadas en BD.

## 5. Trazabilidad y Mapeo a Plan y Verificación

| Criterio | Componente / Archivo Objetivo | Plan / Tarea | Verificación Prevista |
| :--- | :--- | :--- | :--- |
| **AC-01** | `application.adapters.persistence.jpa.*` (User, Buyer, Seller) | Plan §1 / TASK-01 | Pruebas de mapeo y repositorio JPA |
| **AC-02** | `application.adapters.persistence.jpa.*` (Order, OrderLine) | Plan §2 / TASK-02 | Pruebas de cascada y cálculo de orden JPA |
| **AC-03** | `application.adapters.persistence.jpa.*` (Stock, Movement, Warehouse) | Plan §3 / TASK-03 | Pruebas de existencias y kardex JPA |
| **AC-04** | `application.adapters.persistence.mongodb.*` (Product) | Plan §4 / TASK-04 | Pruebas de catálogo y variantes Mongo |
| **AC-05** | `application.adapters.persistence.mongodb.*` (ShoppingCart) | Plan §5 / TASK-05 | Pruebas de carrito embebido Mongo |
| **AC-06** | `application.adapters.persistence.mongodb.*` (AuditLog) | Plan §6 / TASK-06 | Pruebas de bitácora de auditoría Mongo |
| **AC-07** | Suite de pruebas `src/test/java/application/adapters/persistence/` | Plan §7 / TASK-07 | `mvn test` en Docker (`BUILD SUCCESS`) |
