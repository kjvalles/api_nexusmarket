# [PLAN-005] Plan Técnico — Fase 5: Adaptadores de Persistencia Políglota (adapters/persistence/)

## 1. Evidencia y Arquitectura Existente

- **Puertos de Salida Definidos (`nexusmarcket/src/main/java/application/domain/ports/out/`)**:
  - Relacionales (MySQL / JPA): [`UserRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/UserRepositoryPort.java), [`BuyerRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/BuyerRepositoryPort.java), [`SellerRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/SellerRepositoryPort.java), [`OrderRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java), [`ProductStockRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/ProductStockRepositoryPort.java), [`InventoryMovementRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/InventoryMovementRepositoryPort.java), [`WarehouseRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/WarehouseRepositoryPort.java).
  - Documentales (MongoDB): [`ProductRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/ProductRepositoryPort.java), [`ShoppingCartRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/ShoppingCartRepositoryPort.java), [`AuditLogRepositoryPort`](file:///c:/Source/DOCS/docs/nexusmarcket/nexusmarcket/src/main/java/application/domain/ports/out/AuditLogRepositoryPort.java).
- **Configuración de Motores de BD**:
  - `application.properties`: MySQL configurado vía `spring.datasource.*` con Hibernate `ddl-auto=update` y dialecto MySQL; MongoDB configurado vía `spring.mongodb.uri`.
  - Contenedores Docker: `nexus-mysql` (MySQL 8.0 en puerto 3306) y `nexus-mongo` (MongoDB 6.0/7.0 en puerto 27017).
- **Referente Arquitectónico**:
  - El proyecto de referencia `bank` implementa paquetes simétricos en `application.adapters.persistence.jpa` (con subpaquetes `entities`, `mappers`, `repositories` y adaptadores raíz) y `application.adapters.persistence.mongodb` (con subpaquetes `documents`, `mappers`, `repositories` y adaptadores raíz).

## 2. Estructura Técnica y Componentes Propuestos

### 2.1 Paquete Relacional: `application.adapters.persistence.jpa`

#### Entidades (`application.adapters.persistence.jpa.entities`)
1. `UserJpaEntity`: Tabla `users`. Estrategia `@Inheritance(strategy = InheritanceType.JOINED)`. Columnas: `id` (VARCHAR 36), `identity_document` (VARCHAR 50, UNIQUE), `full_name` (VARCHAR 150), `email` (VARCHAR 120, UNIQUE), `password` (VARCHAR 200), `role` (VARCHAR 40), `status` (VARCHAR 40), `auth_token_version` (INT, default 1), `created_at`, `updated_at`.
2. `BuyerJpaEntity`: Tabla `buyers` (extiende `UserJpaEntity`). Columnas: `commercial_status`, `virtual_balance`, `completed_orders_count`, `rating`, `primary_street`, `primary_city`, `primary_state`, `primary_postal_code`, `primary_country`, `additional_addresses` (TEXT/JSON).
3. `SellerJpaEntity`: Tabla `sellers` (extiende `UserJpaEntity`). Columnas: `business_name`, `tax_id`, `seller_status`, `business_description`, `rating`, `incorporation_date`, `active_products_count`, `warehouse_ids` (TEXT/JSON).
4. `OrderJpaEntity`: Tabla `orders`. `@Id @GeneratedValue(IDENTITY) Long id`, `buyer_id`, `status`, `subtotal`, `taxes`, `discounts`, `total_amount`, `additional_notes` (TEXT), `created_at`, `updated_at`, `@OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true) List<OrderLineJpaEntity> lines`.
5. `OrderLineJpaEntity`: Tabla `order_lines`. `@Id @GeneratedValue(IDENTITY) Long id`, `@ManyToOne OrderJpaEntity order`, `product_id`, `product_name`, `variant_id`, `unit_price`, `quantity`, `subtotal`.
6. `ProductStockJpaEntity`: Tabla `product_stocks`. `@Id @GeneratedValue(IDENTITY) Long id`, `product_id`, `warehouse_id`, `variant_id`, `available_quantity`, `reserved_quantity`, `last_updated_at`. Restricción de unicidad: `(product_id, warehouse_id, variant_id)`.
7. `InventoryMovementJpaEntity`: Tabla `inventory_movements`. `@Id @GeneratedValue(IDENTITY) Long id`, `product_id`, `warehouse_id`, `variant_id`, `quantity`, `movement_type`, `reference_order_id`, `registered_by_user_id`, `notes`, `movement_date`.
8. `WarehouseJpaEntity`: Tabla `warehouses`. `id` (VARCHAR 36, PK), `name`, `vendor_id`, `type`, `status`, `description`, `max_capacity`, columnas de dirección (`street`, `city`, `state`, `postal_code`, `country`), `created_at`, `updated_at`.

#### Repositorios Spring Data JPA (`application.adapters.persistence.jpa.repositories`)
- `SpringDataJpaUserRepository extends JpaRepository<UserJpaEntity, String>`: `findByEmail`, `findByIdentityDocument`, `existsByEmail`, `existsByIdentityDocument`.
- `SpringDataJpaBuyerRepository extends JpaRepository<BuyerJpaEntity, String>`
- `SpringDataJpaSellerRepository extends JpaRepository<SellerJpaEntity, String>`
- `SpringDataJpaOrderRepository extends JpaRepository<OrderJpaEntity, Long>`: `findByBuyerId`.
- `SpringDataJpaProductStockRepository extends JpaRepository<ProductStockJpaEntity, Long>`: `findByProductIdAndWarehouseIdAndVariantId`, `findByProductIdAndWarehouseIdAndVariantIdIsNull`, `findByProductId`, `findByWarehouseId`.
- `SpringDataJpaInventoryMovementRepository extends JpaRepository<InventoryMovementJpaEntity, Long>`: `findByProductId`, `findByWarehouseId`, `findByReferenceOrderId`.
- `SpringDataJpaWarehouseRepository extends JpaRepository<WarehouseJpaEntity, String>`: `findByVendorId`.

#### Mappers JPA (`application.adapters.persistence.jpa.mappers`)
- `UserJpaMapper`: Conversión pura bidireccional entre `User` y `UserJpaEntity`.
- `BuyerJpaMapper`: Conversión de agregados `Buyer` y direcciones a `BuyerJpaEntity`.
- `SellerJpaMapper`: Conversión de agregados `Seller` a `SellerJpaEntity`.
- `OrderJpaMapper`: Conversión profunda de `Order` y sus líneas a `OrderJpaEntity` y `OrderLineJpaEntity`.
- `ProductStockJpaMapper`: Conversión de saldos de existencias e invariantes de stock.
- `InventoryMovementJpaMapper`: Conversión de transacciones de kardex.
- `WarehouseJpaMapper`: Conversión de bodegas físicas y direcciones.

#### Adaptadores JPA (`application.adapters.persistence.jpa`)
- `UserJpaAdapter implements UserRepositoryPort`
- `BuyerJpaAdapter implements BuyerRepositoryPort`
- `SellerJpaAdapter implements SellerRepositoryPort`
- `OrderJpaAdapter implements OrderRepositoryPort`
- `ProductStockJpaAdapter implements ProductStockRepositoryPort`
- `InventoryMovementJpaAdapter implements InventoryMovementRepositoryPort`
- `WarehouseJpaAdapter implements WarehouseRepositoryPort`

---

### 2.2 Paquete NoSQL: `application.adapters.persistence.mongodb`

#### Documentos (`application.adapters.persistence.mongodb.documents`)
1. `ProductDocument`: `@Document(collection = "products")`. Campos: `id`, `vendorId`, `name`, `description`, `category`, `basePrice`, `type`, `status`, `variants` (Lista de `ProductVariantDocument`), `imageUrls`, `averageRating`, `totalSales`, `publishedAt`, `updatedAt`.
2. `ProductVariantDocument`: Sub-documento embebido con `variantId`, `sku`, `attributeName`, `attributeValue`, `additionalPrice`.
3. `ShoppingCartDocument`: `@Document(collection = "shopping_carts")`. Campos: `id`, `buyerId` (`@Indexed(unique = true)`), `items` (Lista de `CartItemDocument`), `createdAt`, `lastModifiedAt`.
4. `CartItemDocument`: Sub-documento embebido con `productId`, `productName`, `variantId`, `sku`, `productType`, `unitPrice`, `quantity`, `addedAt`.
5. `AuditLogDocument`: `@Document(collection = "audit_logs")`. Campos: `id`, `actorId`, `actorRole`, `action`, `entityName`, `entityId`, `details`, `timestamp`.

#### Repositorios Spring Data MongoDB (`application.adapters.persistence.mongodb.repositories`)
- `SpringDataMongoProductRepository extends MongoRepository<ProductDocument, String>`: `findByVendorId`, `findByStatus`, `findByStatusAndCategory`.
- `SpringDataMongoShoppingCartRepository extends MongoRepository<ShoppingCartDocument, String>`: `findByBuyerId`, `deleteByBuyerId`.
- `SpringDataMongoAuditLogRepository extends MongoRepository<AuditLogDocument, String>`: `findByActorId`, `findByEntityNameAndEntityId`.

#### Mappers MongoDB (`application.adapters.persistence.mongodb.mappers`)
- `ProductMongoMapper`: Mapeo del agregado `Product` con sus variantes embebidas a `ProductDocument`.
- `ShoppingCartMongoMapper`: Mapeo de `ShoppingCart` y sus `CartItem`s a `ShoppingCartDocument`.
- `AuditLogMongoMapper`: Mapeo del registro inmutable `AuditLog` a `AuditLogDocument`.

#### Adaptadores MongoDB (`application.adapters.persistence.mongodb`)
- `ProductMongoAdapter implements ProductRepositoryPort`
- `ShoppingCartMongoAdapter implements ShoppingCartRepositoryPort`
- `AuditLogMongoAdapter implements AuditLogRepositoryPort`

---

## 3. Contratos y Compatibilidad

- **Inyección Transparente**: Al marcar los 10 adaptadores con `@Repository`, Spring Boot los inyectará automáticamente en los casos de uso (`PublicAccessUseCaseImpl`, `BuyerUseCaseImpl`, `SellerUseCaseImpl`, etc.) y en los servicios de dominio sin requerir configuración manual.
- **Transaccionalidad**: Los adaptadores JPA operan bajo transacciones de Spring / JPA (`@Transactional`).
- **Manejo de Nulos**: Los mappers validan nulos defensivamente retornando `null` o listas inmutables vacías para evitar `NullPointerException`.
- **Compatibilidad con `TestOutPortsConfig`**: Los mocks de `@ConditionalOnMissingBean` en `TestOutPortsConfig` cederán el paso a los beans reales `@Repository` conforme se cargue el contexto de la aplicación.

## 4. Diseño de UI e Interacción
- **Justificación N/A**: Esta fase corresponde exclusivamente a la capa de persistencia de datos (JPA/MySQL y MongoDB). No contiene interfaz gráfica de usuario.

## 5. Estrategia de Verificación

- **Suite de Pruebas**: Crear `nexusmarcket/src/test/java/application/adapters/persistence/PersistenceAdaptersTest.java`.
- **Cobertura**:
  1. Guardado y consulta de `User`, `Buyer` y `Seller` con mapeo de roles y versión de token.
  2. Guardado en cascada de `Order` con múltiples `OrderLine` y verificación de totales.
  3. Registro y consulta de existencias en `ProductStock`, movimientos en `InventoryMovement` y bodegas en `Warehouse`.
  4. Persistencia documental de `Product` en Mongo con variantes embebidas y filtros por categoría/estado.
  5. Operaciones de carrito `ShoppingCart` en Mongo (guardado, adición de ítems y borrado atómico pos-checkout).
  6. Registro inmutable de auditoría en `AuditLog` en Mongo.
- **Comando de Verificación**: `docker compose exec -T nexusmarcket-app mvn test` ejecutado en el contenedor para validar interacción limpia contra MySQL y MongoDB.

## 6. Rollback y Recuperación
- Las migraciones de esquema JPA utilizan `ddl-auto=update`, lo que no destruye datos preexistentes.
- Las colecciones de MongoDB son schemaless y se inicializan dinámicamente según la creación de documentos.
- Cualquier fallo se aísla a nivel de adaptador sin impacto en el núcleo de dominio (`application.domain.*`).
