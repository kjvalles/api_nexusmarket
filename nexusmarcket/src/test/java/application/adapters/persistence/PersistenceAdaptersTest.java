package application.adapters.persistence;

import application.adapters.persistence.jpa.*;
import application.adapters.persistence.jpa.entities.*;
import application.adapters.persistence.jpa.repositories.*;
import application.adapters.persistence.mongodb.*;
import application.adapters.persistence.mongodb.documents.*;
import application.adapters.persistence.mongodb.repositories.*;
import application.domain.enums.UserRole;
import application.domain.models.*;
import application.domain.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas Unitarias de Adaptadores de Persistencia Políglota (JPA y MongoDB)")
class PersistenceAdaptersTest {

    // --- 1. UserJpaAdapter ---
    @Test
    @DisplayName("UserJpaAdapter: guarda, consulta por ID/Email y valida existencia")
    void testUserJpaAdapter() {
        SpringDataJpaUserRepository userRepo = mock(SpringDataJpaUserRepository.class);
        UserJpaAdapter adapter = new UserJpaAdapter(userRepo);

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId("usr-1");
        entity.setEmail("admin@nexusmarket.com");
        entity.setRole(UserRole.ADMINISTRATOR.name());
        entity.setStatus("ACTIVE");
        entity.setAuthTokenVersion(1);

        when(userRepo.save(any(UserJpaEntity.class))).thenReturn(entity);
        when(userRepo.findById("usr-1")).thenReturn(Optional.of(entity));
        when(userRepo.findByEmail("admin@nexusmarket.com")).thenReturn(Optional.of(entity));
        when(userRepo.existsByEmail("admin@nexusmarket.com")).thenReturn(true);
        when(userRepo.findAll()).thenReturn(List.of(entity));

        User user = new DefaultUser(UserRole.ADMINISTRATOR);
        user.setId("usr-1");
        user.setEmail("admin@nexusmarket.com");

        User saved = adapter.save(user);
        assertNotNull(saved);
        assertEquals("usr-1", saved.getId());
        assertEquals("admin@nexusmarket.com", saved.getEmail());

        Optional<User> foundById = adapter.findById("usr-1");
        assertTrue(foundById.isPresent());
        assertEquals(UserRole.ADMINISTRATOR, foundById.get().getRole());

        assertTrue(adapter.existsByEmail("admin@nexusmarket.com"));
        assertEquals(1, adapter.findAll().size());
    }

    // --- 2. BuyerJpaAdapter ---
    @Test
    @DisplayName("BuyerJpaAdapter: guarda y consulta comprador con dirección y saldo")
    void testBuyerJpaAdapter() {
        SpringDataJpaBuyerRepository buyerRepo = mock(SpringDataJpaBuyerRepository.class);
        BuyerJpaAdapter adapter = new BuyerJpaAdapter(buyerRepo);

        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setId("buy-1");
        entity.setEmail("buyer@nexusmarket.com");
        entity.setRole(UserRole.BUYER.name());
        entity.setVirtualBalance(new BigDecimal("150.00"));
        entity.setPrimaryStreet("Av Principal 123");
        entity.setPrimaryCity("Medellin");

        when(buyerRepo.save(any(BuyerJpaEntity.class))).thenReturn(entity);
        when(buyerRepo.findById("buy-1")).thenReturn(Optional.of(entity));

        Buyer buyer = new Buyer();
        buyer.setId("buy-1");
        buyer.setEmail("buyer@nexusmarket.com");
        buyer.creditBalance(new BigDecimal("150.00"));
        buyer.setPrimaryAddress(new Address("Av Principal 123", "Medellin", "Antioquia", "050001", "Colombia", null));

        Buyer saved = adapter.save(buyer);
        assertNotNull(saved);
        assertEquals("buy-1", saved.getId());
        assertEquals(new BigDecimal("150.00"), saved.getVirtualBalance());
        assertNotNull(saved.getPrimaryAddress());
        assertEquals("Medellin", saved.getPrimaryAddress().getCity());

        Optional<Buyer> found = adapter.findById("buy-1");
        assertTrue(found.isPresent());
        assertEquals("Av Principal 123", found.get().getPrimaryAddress().getStreet());
    }

    // --- 3. SellerJpaAdapter ---
    @Test
    @DisplayName("SellerJpaAdapter: guarda y consulta vendedor con bodegas y estado fiscal")
    void testSellerJpaAdapter() {
        SpringDataJpaSellerRepository sellerRepo = mock(SpringDataJpaSellerRepository.class);
        SellerJpaAdapter adapter = new SellerJpaAdapter(sellerRepo);

        SellerJpaEntity entity = new SellerJpaEntity();
        entity.setId("sel-1");
        entity.setBusinessName("Tech Store SAS");
        entity.setTaxId("900123456-7");
        entity.setRole(UserRole.SELLER.name());
        entity.setWarehouseIds("wh-1,wh-2");

        when(sellerRepo.save(any(SellerJpaEntity.class))).thenReturn(entity);
        when(sellerRepo.findById("sel-1")).thenReturn(Optional.of(entity));
        when(sellerRepo.findAll()).thenReturn(List.of(entity));

        Seller seller = new Seller();
        seller.setId("sel-1");
        seller.setBusinessName("Tech Store SAS");
        seller.setTaxId("900123456-7");
        seller.addWarehouseId("wh-1");
        seller.addWarehouseId("wh-2");

        Seller saved = adapter.save(seller);
        assertNotNull(saved);
        assertEquals("Tech Store SAS", saved.getBusinessName());
        assertEquals(2, saved.getWarehouseIds().size());

        Optional<Seller> found = adapter.findById("sel-1");
        assertTrue(found.isPresent());
        assertEquals("900123456-7", found.get().getTaxId());
        assertEquals(1, adapter.findAll().size());
    }

    // --- 4. OrderJpaAdapter ---
    @Test
    @DisplayName("OrderJpaAdapter: guarda orden con líneas en cascada y consulta por comprador")
    void testOrderJpaAdapter() {
        SpringDataJpaOrderRepository orderRepo = mock(SpringDataJpaOrderRepository.class);
        OrderJpaAdapter adapter = new OrderJpaAdapter(orderRepo);

        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(101L);
        entity.setBuyerId("buy-1");
        entity.setStatus(OrderStatus.PAID.getCode());
        entity.setSubtotal(new BigDecimal("100.00"));
        entity.setTaxes(new BigDecimal("19.00"));
        entity.setTotalAmount(new BigDecimal("119.00"));

        OrderLineJpaEntity line = new OrderLineJpaEntity();
        line.setProductId("prod-1");
        line.setProductName("Laptop");
        line.setQuantity(1);
        line.setUnitPrice(new BigDecimal("100.00"));
        line.setSubtotal(new BigDecimal("100.00"));
        entity.addLine(line);

        when(orderRepo.save(any(OrderJpaEntity.class))).thenReturn(entity);
        when(orderRepo.findById(101L)).thenReturn(Optional.of(entity));
        when(orderRepo.findByBuyerId("buy-1")).thenReturn(List.of(entity));

        Order order = new Order("buy-1");
        order.setId(101L);
        order.addLine(new OrderLine("prod-1", "Laptop", null, new BigDecimal("100.00"), 1));

        Order saved = adapter.save(order);
        assertNotNull(saved);
        assertEquals(101L, saved.getId());
        assertEquals(1, saved.getOrderLines().size());

        Optional<Order> found = adapter.findById(101L);
        assertTrue(found.isPresent());
        assertEquals(1, adapter.findByBuyerId("buy-1").size());
    }

    // --- 5. ProductStockJpaAdapter ---
    @Test
    @DisplayName("ProductStockJpaAdapter: guarda existencias y consulta por producto y bodega")
    void testProductStockJpaAdapter() {
        SpringDataJpaProductStockRepository stockRepo = mock(SpringDataJpaProductStockRepository.class);
        ProductStockJpaAdapter adapter = new ProductStockJpaAdapter(stockRepo);

        ProductStockJpaEntity entity = new ProductStockJpaEntity();
        entity.setId(1L);
        entity.setProductId("prod-1");
        entity.setWarehouseId("wh-1");
        entity.setVariantId("var-1");
        entity.setAvailableQuantity(50);
        entity.setReservedQuantity(5);

        when(stockRepo.save(any(ProductStockJpaEntity.class))).thenReturn(entity);
        when(stockRepo.findByProductIdAndWarehouseIdAndVariantId("prod-1", "wh-1", "var-1")).thenReturn(Optional.of(entity));
        when(stockRepo.findByProductId("prod-1")).thenReturn(List.of(entity));

        ProductStock stock = new ProductStock("prod-1", "var-1", "wh-1", 50);

        ProductStock saved = adapter.save(stock);
        assertNotNull(saved);
        assertEquals(50, saved.getAvailableQuantity());

        Optional<ProductStock> found = adapter.findByProductVariantAndWarehouse("prod-1", "var-1", "wh-1");
        assertTrue(found.isPresent());
        assertEquals(45, found.get().getSellableQuantity());
        assertEquals(1, adapter.findByProductId("prod-1").size());
    }

    // --- 6. InventoryMovementJpaAdapter ---
    @Test
    @DisplayName("InventoryMovementJpaAdapter: registra movimientos históricos de kardex")
    void testInventoryMovementJpaAdapter() {
        SpringDataJpaInventoryMovementRepository movementRepo = mock(SpringDataJpaInventoryMovementRepository.class);
        InventoryMovementJpaAdapter adapter = new InventoryMovementJpaAdapter(movementRepo);

        InventoryMovementJpaEntity entity = new InventoryMovementJpaEntity();
        entity.setId(1L);
        entity.setProductId("prod-1");
        entity.setWarehouseId("wh-1");
        entity.setQuantity(20);
        entity.setMovementType("MANUAL_ENTRY");
        entity.setMovementDate(LocalDateTime.now());

        when(movementRepo.save(any(InventoryMovementJpaEntity.class))).thenReturn(entity);
        when(movementRepo.findByProductId("prod-1")).thenReturn(List.of(entity));

        InventoryMovement movement = new InventoryMovement("prod-1", null, "wh-1", 20,
                InventoryMovementType.MANUAL_ENTRY, null, "user-1", "Ingreso inicial");

        InventoryMovement saved = adapter.save(movement);
        assertNotNull(saved);
        assertEquals(20, saved.getQuantity());
        assertEquals(1, adapter.findByProductId("prod-1").size());
    }

    // --- 7. WarehouseJpaAdapter ---
    @Test
    @DisplayName("WarehouseJpaAdapter: persiste bodegas físicas y consulta por vendedor")
    void testWarehouseJpaAdapter() {
        SpringDataJpaWarehouseRepository warehouseRepo = mock(SpringDataJpaWarehouseRepository.class);
        WarehouseJpaAdapter adapter = new WarehouseJpaAdapter(warehouseRepo);

        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        entity.setId("wh-1");
        entity.setName("Bodega Central");
        entity.setVendorId("sel-1");
        entity.setMaxCapacity(1000);

        when(warehouseRepo.save(any(WarehouseJpaEntity.class))).thenReturn(entity);
        when(warehouseRepo.findById("wh-1")).thenReturn(Optional.of(entity));
        when(warehouseRepo.findByVendorId("sel-1")).thenReturn(List.of(entity));
        when(warehouseRepo.existsById("wh-1")).thenReturn(true);

        Warehouse warehouse = new Warehouse();
        warehouse.setId("wh-1");
        warehouse.setName("Bodega Central");
        warehouse.setVendorId("sel-1");
        warehouse.setMaxCapacity(1000);

        Warehouse saved = adapter.save(warehouse);
        assertNotNull(saved);
        assertEquals("Bodega Central", saved.getName());
        assertTrue(adapter.existsById("wh-1"));
        assertEquals(1, adapter.findByVendorId("sel-1").size());
    }

    // --- 8. ProductMongoAdapter ---
    @Test
    @DisplayName("ProductMongoAdapter: guarda producto con variantes y consulta publicados")
    void testProductMongoAdapter() {
        SpringDataMongoProductRepository productRepo = mock(SpringDataMongoProductRepository.class);
        ProductMongoAdapter adapter = new ProductMongoAdapter(productRepo);

        ProductDocument doc = new ProductDocument();
        doc.setId("prod-1");
        doc.setVendorId("sel-1");
        doc.setName("Smartphone X");
        doc.setCategory("ELECTRONICS");
        doc.setStatus(ProductStatus.PUBLISHED.getCode());
        doc.setBasePrice(new BigDecimal("599.99"));

        ProductVariantDocument varDoc = new ProductVariantDocument("var-1", "Negro", "128GB", "SKU-BLK", BigDecimal.ZERO, null, true);
        doc.setVariants(List.of(varDoc));

        when(productRepo.save(any(ProductDocument.class))).thenReturn(doc);
        when(productRepo.findById("prod-1")).thenReturn(Optional.of(doc));
        when(productRepo.findByStatus(ProductStatus.PUBLISHED.getCode())).thenReturn(List.of(doc));
        when(productRepo.existsById("prod-1")).thenReturn(true);

        Product product = new Product("sel-1", "Smartphone X", "ELECTRONICS", new BigDecimal("599.99"), ProductType.PHYSICAL);
        product.setId("prod-1");
        product.publish();

        Product saved = adapter.save(product);
        assertNotNull(saved);
        assertEquals("Smartphone X", saved.getName());
        assertEquals(1, saved.getVariants().size());

        List<Product> published = adapter.findPublished();
        assertEquals(1, published.size());
        assertTrue(adapter.existsById("prod-1"));
    }

    // --- 9. ShoppingCartMongoAdapter ---
    @Test
    @DisplayName("ShoppingCartMongoAdapter: gestiona carrito de compras y borrado pos-checkout")
    void testShoppingCartMongoAdapter() {
        SpringDataMongoShoppingCartRepository cartRepo = mock(SpringDataMongoShoppingCartRepository.class);
        ShoppingCartMongoAdapter adapter = new ShoppingCartMongoAdapter(cartRepo);

        ShoppingCartDocument doc = new ShoppingCartDocument();
        doc.setId("cart-1");
        doc.setBuyerId("buy-1");

        CartItemDocument itemDoc = new CartItemDocument("prod-1", "var-1", "Camisa", "SKU-SHIRT", 2, new BigDecimal("25.00"), LocalDateTime.now());
        doc.setItems(List.of(itemDoc));

        when(cartRepo.save(any(ShoppingCartDocument.class))).thenReturn(doc);
        when(cartRepo.findByBuyerId("buy-1")).thenReturn(Optional.of(doc));

        ShoppingCart cart = new ShoppingCart("buy-1");
        cart.setId("cart-1");
        cart.addItem(new CartItem("prod-1", "var-1", "Camisa", new BigDecimal("25.00"), 2));

        ShoppingCart saved = adapter.save(cart);
        assertNotNull(saved);
        assertEquals("buy-1", saved.getBuyerId());
        assertEquals(1, saved.getItems().size());
        assertEquals(new BigDecimal("50.00"), saved.calculateTotal());

        adapter.deleteByBuyerId("buy-1");
        verify(cartRepo, times(1)).deleteByBuyerId("buy-1");
    }

    // --- 10. AuditLogMongoAdapter ---
    @Test
    @DisplayName("AuditLogMongoAdapter: guarda eventos inmutables de auditoría y consulta por actor")
    void testAuditLogMongoAdapter() {
        SpringDataMongoAuditLogRepository auditRepo = mock(SpringDataMongoAuditLogRepository.class);
        AuditLogMongoAdapter adapter = new AuditLogMongoAdapter(auditRepo);

        AuditLogDocument doc = new AuditLogDocument();
        doc.setId("aud-1");
        doc.setActorId("adm-1");
        doc.setAction("STATUS_CHANGE");
        doc.setEntityName("User");
        doc.setEntityId("usr-99");
        doc.setDetails("Usuario bloqueado por sospecha de fraude");
        doc.setTimestamp(LocalDateTime.now());

        when(auditRepo.save(any(AuditLogDocument.class))).thenReturn(doc);
        when(auditRepo.findByActorId("adm-1")).thenReturn(List.of(doc));
        when(auditRepo.findByEntityNameAndEntityId("User", "usr-99")).thenReturn(List.of(doc));

        AuditLog auditLog = new AuditLog("adm-1", "ADMINISTRATOR", "STATUS_CHANGE", "User", "usr-99", "Usuario bloqueado por sospecha de fraude");

        AuditLog saved = adapter.save(auditLog);
        assertNotNull(saved);
        assertEquals("adm-1", saved.getActorId());
        assertEquals(1, adapter.findByActorId("adm-1").size());
        assertEquals(1, adapter.findByEntity("User", "usr-99").size());
    }
}
