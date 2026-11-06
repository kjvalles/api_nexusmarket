package application.adapters.useCases;

import application.domain.enums.UserRole;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.exceptions.UnauthorizedRoleOperationException;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.audit.ConsultAuditLogService;
import application.domain.services.auth.LoginService;
import application.domain.services.auth.LogoutService;
import application.domain.services.auth.RegisterBuyerService;
import application.domain.services.cart.ConsultCartService;
import application.domain.services.cart.ManageCartService;
import application.domain.services.catalog.*;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.ManageWarehouseService;
import application.domain.services.inventory.RegisterInventoryMovementService;
import application.domain.services.order.CancelOrderService;
import application.domain.services.order.ConsultOrderService;
import application.domain.services.order.CreateOrderService;
import application.domain.services.order.TransitionOrderStatusService;
import application.domain.services.user.ChangeUserStatusService;
import application.domain.services.user.ConsultUserService;
import application.domain.services.user.RegisterSellerService;
import application.domain.services.user.RegisterUserService;
import application.domain.valueobjects.CartItem;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Casos de Uso (Adapters / UseCases)")
class UseCasesTest {

    // In-memory Test Doubles
    static class FakeUserRepository implements UserRepositoryPort {
        Map<String, User> users = new HashMap<>();

        @Override public User save(User user) {
            if (user.getId() == null) user.setId("usr-" + (users.size() + 1));
            users.put(user.getId(), user);
            return user;
        }
        @Override public Optional<User> findById(String id) { return Optional.ofNullable(users.get(id)); }
        @Override public Optional<User> findByEmail(String email) {
            return users.values().stream().filter(u -> email.equals(u.getEmail())).findFirst();
        }
        @Override public Optional<User> findByIdentityDocument(String doc) {
            return users.values().stream().filter(u -> doc.equals(u.getIdentityDocument())).findFirst();
        }
        @Override public boolean existsByEmail(String email) { return findByEmail(email).isPresent(); }
        @Override public boolean existsByIdentityDocument(String doc) { return findByIdentityDocument(doc).isPresent(); }
        @Override public void update(User user) { users.put(user.getId(), user); }
        @Override public List<User> findAll() { return new ArrayList<>(users.values()); }
    }

    static class FakeBuyerRepository implements BuyerRepositoryPort {
        Map<String, Buyer> buyers = new HashMap<>();
        @Override public Buyer save(Buyer buyer) {
            if (buyer.getId() == null) buyer.setId("byr-" + (buyers.size() + 1));
            buyers.put(buyer.getId(), buyer);
            return buyer;
        }
        @Override public Optional<Buyer> findById(String id) { return Optional.ofNullable(buyers.get(id)); }
    }

    static class FakeSellerRepository implements SellerRepositoryPort {
        Map<String, Seller> sellers = new HashMap<>();
        @Override public Seller save(Seller seller) {
            if (seller.getId() == null) seller.setId("slr-" + (sellers.size() + 1));
            sellers.put(seller.getId(), seller);
            return seller;
        }
        @Override public Optional<Seller> findById(String id) { return Optional.ofNullable(sellers.get(id)); }
        @Override public List<Seller> findAll() { return new ArrayList<>(sellers.values()); }
    }

    static class FakeProductRepository implements ProductRepositoryPort {
        Map<String, Product> products = new HashMap<>();
        @Override public Product save(Product product) {
            if (product.getId() == null) product.setId("prd-" + (products.size() + 1));
            products.put(product.getId(), product);
            return product;
        }
        @Override public Optional<Product> findById(String id) { return Optional.ofNullable(products.get(id)); }
        @Override public List<Product> findByVendorId(String vendorId) {
            return products.values().stream().filter(p -> vendorId.equals(p.getVendorId())).toList();
        }
        @Override public List<Product> findPublished() {
            return products.values().stream().filter(Product::isPublished).toList();
        }
        @Override public List<Product> findPublishedByCategory(String cat) {
            return products.values().stream().filter(p -> p.isPublished() && cat.equals(p.getCategory())).toList();
        }
        @Override public boolean existsById(String id) { return products.containsKey(id); }
    }

    static class FakeOrderRepository implements OrderRepositoryPort {
        Map<Long, Order> orders = new HashMap<>();
        long counter = 1;
        @Override public Order save(Order order) {
            if (order.getId() == null) order.setId(counter++);
            orders.put(order.getId(), order);
            return order;
        }
        @Override public Optional<Order> findById(Long id) { return Optional.ofNullable(orders.get(id)); }
        @Override public List<Order> findByBuyerId(String buyerId) {
            return orders.values().stream().filter(o -> buyerId.equals(o.getBuyerId())).toList();
        }
        @Override public List<Order> findAll() { return new ArrayList<>(orders.values()); }
    }

    static class FakeShoppingCartRepository implements ShoppingCartRepositoryPort {
        Map<String, ShoppingCart> carts = new HashMap<>();
        @Override public ShoppingCart save(ShoppingCart cart) {
            carts.put(cart.getBuyerId(), cart);
            return cart;
        }
        @Override public Optional<ShoppingCart> findByBuyerId(String buyerId) { return Optional.ofNullable(carts.get(buyerId)); }
        @Override public void deleteByBuyerId(String buyerId) { carts.remove(buyerId); }
    }

    static class FakeProductStockRepository implements ProductStockRepositoryPort {
        Map<String, ProductStock> stocks = new HashMap<>();
        @Override public ProductStock save(ProductStock stock) {
            stocks.put(stock.getProductId() + ":" + stock.getWarehouseId(), stock);
            return stock;
        }
        @Override public Optional<ProductStock> findByProductVariantAndWarehouse(String p, String v, String w) {
            return Optional.ofNullable(stocks.get(p + ":" + w));
        }
        @Override public List<ProductStock> findByProductId(String p) {
            return stocks.values().stream().filter(s -> p.equals(s.getProductId())).toList();
        }
        @Override public List<ProductStock> findByWarehouseId(String w) {
            return stocks.values().stream().filter(s -> w.equals(s.getWarehouseId())).toList();
        }
        @Override public List<ProductStock> findAll() { return new ArrayList<>(stocks.values()); }
    }

    static class FakeInventoryMovementRepository implements InventoryMovementRepositoryPort {
        List<InventoryMovement> movements = new ArrayList<>();
        @Override public InventoryMovement save(InventoryMovement m) { movements.add(m); return m; }
        @Override public List<InventoryMovement> findByProductId(String p) { return movements; }
        @Override public List<InventoryMovement> findByWarehouseId(String w) { return movements; }
        @Override public List<InventoryMovement> findByReferenceOrderId(Long o) { return movements; }
    }

    static class FakeWarehouseRepository implements WarehouseRepositoryPort {
        Map<String, Warehouse> warehouses = new HashMap<>();
        @Override public Warehouse save(Warehouse w) {
            if (w.getId() == null) w.setId("wh-" + (warehouses.size() + 1));
            warehouses.put(w.getId(), w);
            return w;
        }
        @Override public Optional<Warehouse> findById(String id) { return Optional.ofNullable(warehouses.get(id)); }
        @Override public List<Warehouse> findByVendorId(String v) { return new ArrayList<>(warehouses.values()); }
        @Override public List<Warehouse> findAll() { return new ArrayList<>(warehouses.values()); }
        @Override public boolean existsById(String id) { return warehouses.containsKey(id); }
    }

    static class FakeAuditLogRepository implements AuditLogRepositoryPort {
        List<AuditLog> logs = new ArrayList<>();
        @Override public AuditLog save(AuditLog a) { logs.add(a); return a; }
        @Override public List<AuditLog> findByActorId(String a) { return logs; }
        @Override public List<AuditLog> findByEntity(String n, String i) { return logs; }
        @Override public List<AuditLog> findAll() { return logs; }
    }

    static class FakePasswordService implements PasswordServicePort {
        @Override public String encode(CharSequence raw) { return "hash_" + raw; }
        @Override public boolean matches(CharSequence raw, String encoded) { return ("hash_" + raw).equals(encoded); }
    }

    static class FakeJwtService implements JwtServicePort {
        @Override public String generateToken(User user) { return "jwt." + user.getId(); }
        @Override public boolean validateToken(String token) { return true; }
        @Override public String extractUserId(String token) { return "user-1"; }
        @Override public Integer extractTokenVersion(String token) { return 1; }
        @Override public User reconstructUser(String token) { return null; }
    }

    // --- TEST 1: PublicAccessUseCaseImpl ---
    @Test
    @DisplayName("PublicAccessUseCaseImpl: login exitoso retorna token y registro de comprador crea usuario")
    void testPublicAccessUseCase() {
        FakeUserRepository userRepo = new FakeUserRepository();
        FakeBuyerRepository buyerRepo = new FakeBuyerRepository();
        FakeProductRepository productRepo = new FakeProductRepository();
        FakePasswordService pwdService = new FakePasswordService();
        FakeJwtService jwtService = new FakeJwtService();

        LoginService loginService = new LoginService(userRepo, pwdService, jwtService);
        LogoutService logoutService = new LogoutService(userRepo);
        RegisterBuyerService regService = new RegisterBuyerService(userRepo, buyerRepo, pwdService);
        ConsultCatalogService consultCatalogService = new ConsultCatalogService(productRepo);

        PublicAccessUseCaseImpl publicAccess = new PublicAccessUseCaseImpl(loginService, logoutService, regService, consultCatalogService);

        Buyer buyer = new Buyer();
        buyer.setEmail("buyer@nexusmarket.com");
        buyer.setIdentityDocument("DOC-1234");
        Buyer registered = publicAccess.registerBuyer(buyer, "securePassword");

        assertNotNull(registered);
        assertEquals("hash_securePassword", registered.getPassword());

        String token = publicAccess.login("buyer@nexusmarket.com", "securePassword");
        assertEquals("jwt." + registered.getId(), token);

        Product prod = new Product("v-1", "Camisa", "ROPA", new BigDecimal("50.00"), ProductType.PHYSICAL);
        prod.publish();
        productRepo.save(prod);

        List<Product> catalog = publicAccess.getPublicCatalog();
        assertEquals(1, catalog.size());

        Product details = publicAccess.getPublicProductDetails(prod.getId());
        assertEquals("Camisa", details.getName());
    }

    // --- TEST 2: BuyerUseCaseImpl ---
    @Test
    @DisplayName("BuyerUseCaseImpl: carrito, checkout de orden y validación de propiedad")
    void testBuyerUseCase() {
        FakeShoppingCartRepository cartRepo = new FakeShoppingCartRepository();
        FakeOrderRepository orderRepo = new FakeOrderRepository();
        FakeProductRepository prodRepo = new FakeProductRepository();

        Product product = new Product("v-1", "Zapatos", "CALZADO", new BigDecimal("100.00"), ProductType.PHYSICAL);
        product.publish();
        prodRepo.save(product);

        CreateOrderService createOrderService = new CreateOrderService(orderRepo, prodRepo);
        CancelOrderService cancelOrderService = new CancelOrderService(orderRepo);
        ConsultCartService consultCartService = new ConsultCartService(cartRepo);
        ManageCartService manageCartService = new ManageCartService(cartRepo, consultCartService);
        ConsultOrderService consultOrderService = new ConsultOrderService(orderRepo);

        BuyerUseCaseImpl buyerUseCase = new BuyerUseCaseImpl(consultCartService, manageCartService, createOrderService, cancelOrderService, consultOrderService);

        CartItem item = new CartItem(product.getId(), null, product.getName(), product.getBasePrice(), 2);
        buyerUseCase.addItemToCart("buyer-1", item);

        ShoppingCart cart = buyerUseCase.getCart("buyer-1");
        assertEquals(1, cart.getItems().size());

        Order order = buyerUseCase.checkoutCart("buyer-1", null, "Notas de prueba");
        assertNotNull(order.getId());
        assertEquals("buyer-1", order.getBuyerId());
        assertEquals(new BigDecimal("200.00"), order.getSubtotal());

        // Carrito debe haber sido limpiado tras checkout
        assertTrue(buyerUseCase.getCart("buyer-1").getItems().isEmpty());

        // Consulta de orden propia permitida
        Order fetched = buyerUseCase.getOrderDetails("buyer-1", order.getId());
        assertEquals(order.getId(), fetched.getId());

        // Consulta de orden ajena rechazada con UnauthorizedRoleOperationException
        assertThrows(UnauthorizedRoleOperationException.class,
                () -> buyerUseCase.getOrderDetails("buyer-hacker", order.getId()));

        // Cancelación exitosa
        buyerUseCase.cancelOrder("buyer-1", order.getId(), "Cancelado por el comprador");
        assertEquals(OrderStatus.CANCELLED, orderRepo.findById(order.getId()).get().getStatus());
    }

    // --- TEST 3: SellerUseCaseImpl ---
    @Test
    @DisplayName("SellerUseCaseImpl: creación, publicación y rechazo de modificación de producto ajeno")
    void testSellerUseCase() {
        FakeProductRepository prodRepo = new FakeProductRepository();
        FakeProductStockRepository stockRepo = new FakeProductStockRepository();

        CreateProductService createService = new CreateProductService(prodRepo);
        UpdateProductService updateService = new UpdateProductService(prodRepo);
        PublishProductService publishService = new PublishProductService(prodRepo);
        SuspendProductService suspendService = new SuspendProductService(prodRepo);
        ConsultCatalogService consultCatalogService = new ConsultCatalogService(prodRepo);
        ConsultInventoryService consultInventoryService = new ConsultInventoryService(stockRepo, prodRepo);

        SellerUseCaseImpl sellerUseCase = new SellerUseCaseImpl(createService, updateService, publishService, suspendService, consultCatalogService, consultInventoryService);

        Product product = new Product("vendor-1", "Gorra", "ACCESORIOS", new BigDecimal("25.00"), ProductType.PHYSICAL);
        Product created = sellerUseCase.createProduct("vendor-1", product);

        assertEquals("vendor-1", created.getVendorId());
        assertFalse(created.isPublished());

        // Publicación por vendedor propietario
        sellerUseCase.publishProduct("vendor-1", created.getId());
        assertTrue(prodRepo.findById(created.getId()).get().isPublished());

        // Modificación por otro vendedor rechazada
        assertThrows(UnauthorizedRoleOperationException.class,
                () -> sellerUseCase.publishProduct("vendor-impostor", created.getId()));

        assertThrows(UnauthorizedRoleOperationException.class,
                () -> sellerUseCase.suspendProduct("vendor-impostor", created.getId()));
    }

    // --- TEST 4: LogisticsOperatorUseCaseImpl ---
    @Test
    @DisplayName("LogisticsOperatorUseCaseImpl: ingresos físicos, ajustes y transiciones de despacho")
    void testLogisticsOperatorUseCase() {
        FakeProductStockRepository stockRepo = new FakeProductStockRepository();
        FakeInventoryMovementRepository movRepo = new FakeInventoryMovementRepository();
        FakeOrderRepository orderRepo = new FakeOrderRepository();
        FakeWarehouseRepository whRepo = new FakeWarehouseRepository();

        Warehouse wh = new Warehouse();
        wh.setId("wh-1");
        wh.setName("Bodega Principal");
        whRepo.save(wh);

        RegisterInventoryMovementService invService = new RegisterInventoryMovementService(stockRepo, movRepo);
        TransitionOrderStatusService transService = new TransitionOrderStatusService(orderRepo);
        ConsultInventoryService consultInventoryService = new ConsultInventoryService(stockRepo, null);
        ManageWarehouseService manageWarehouseService = new ManageWarehouseService(whRepo);

        LogisticsOperatorUseCaseImpl logistics = new LogisticsOperatorUseCaseImpl(invService, transService, consultInventoryService, manageWarehouseService);

        ProductStock stock = logistics.registerInflow("op-1", "prod-1", null, "wh-1", 50, "Entrada lote 1");
        assertEquals(50, stock.getAvailableQuantity());

        ProductStock adjusted = logistics.registerAdjustment("op-1", "prod-1", null, "wh-1", -5, "Merma detectada");
        assertEquals(45, adjusted.getAvailableQuantity());

        Order order = new Order("buyer-1");
        order.markAsPaid();
        orderRepo.save(order);

        logistics.dispatchOrder("op-1", order.getId(), "GUIA-999");
        Order dispatchedOrder = orderRepo.findById(order.getId()).get();
        assertEquals(OrderStatus.DISPATCHED, dispatchedOrder.getStatus());
        assertTrue(dispatchedOrder.getAdditionalNotes().contains("GUIA-999"));

        logistics.deliverOrder("op-1", order.getId());
        assertEquals(OrderStatus.DELIVERED, orderRepo.findById(order.getId()).get().getStatus());

        List<Warehouse> warehouses = logistics.listWarehouses();
        assertEquals(1, warehouses.size());
        assertEquals("wh-1", warehouses.get(0).getId());
    }

    // --- TEST 5: AdministratorUseCaseImpl ---
    @Test
    @DisplayName("AdministratorUseCaseImpl: registro de vendedores y cambio de estado con bump de token")
    void testAdministratorUseCase() {
        FakeUserRepository userRepo = new FakeUserRepository();
        FakeSellerRepository sellerRepo = new FakeSellerRepository();
        FakeWarehouseRepository whRepo = new FakeWarehouseRepository();
        FakePasswordService pwdService = new FakePasswordService();

        RegisterUserService registerUserService = new RegisterUserService(userRepo, pwdService);
        RegisterSellerService registerSellerService = new RegisterSellerService(userRepo, sellerRepo, pwdService);
        ChangeUserStatusService changeUserStatusService = new ChangeUserStatusService(userRepo);
        ConsultUserService consultUserService = new ConsultUserService(userRepo, sellerRepo);
        ManageWarehouseService manageWarehouseService = new ManageWarehouseService(whRepo);

        AdministratorUseCaseImpl admin = new AdministratorUseCaseImpl(
                registerUserService, registerSellerService, changeUserStatusService, consultUserService, manageWarehouseService);

        Seller seller = new Seller();
        seller.setEmail("tienda@nexusmarket.com");
        seller.setIdentityDocument("NIT-900123");
        seller.setBusinessName("Tienda Oficial");

        Seller registered = admin.registerSeller("admin-1", seller, "adminCreatedPass");
        assertNotNull(registered);
        assertEquals("hash_adminCreatedPass", registered.getPassword());
        assertEquals(UserRole.SELLER, registered.getRole());

        // Cambio de estado de usuario
        admin.changeUserStatus("admin-1", registered.getId(), "BLOCKED");
        User blocked = userRepo.findById(registered.getId()).get();
        assertEquals(UserStatus.BLOCKED, blocked.getStatus());
        assertEquals(2, blocked.getAuthTokenVersion()); // Token invalidado

        // Registro de usuario interno
        DefaultUser newUser = new DefaultUser(UserRole.LOGISTICS_OPERATOR);
        newUser.setEmail("operador@nexusmarket.com");
        newUser.setIdentityDocument("CC-554433");
        newUser.setFullName("Operador Logístico 1");

        User registeredUser = admin.registerUser("admin-1", newUser, "operadorPass123");
        assertNotNull(registeredUser);
        assertEquals("hash_operadorPass123", registeredUser.getPassword());
        assertEquals(UserRole.LOGISTICS_OPERATOR, registeredUser.getRole());
        assertEquals(UserStatus.ACTIVE, registeredUser.getStatus());
        assertEquals(1, registeredUser.getAuthTokenVersion());

        // Consulta de usuario por ID
        User foundUser = admin.getUserById(registeredUser.getId());
        assertNotNull(foundUser);
        assertEquals("operador@nexusmarket.com", foundUser.getEmail());

        // Consulta de usuario no existente lanza EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () -> admin.getUserById("usr-inexistente"));

        // Creación y listado de bodegas
        Warehouse wh = new Warehouse();
        wh.setId("wh-admin-1");
        wh.setName("Bodega Admin");
        admin.createWarehouse("admin-1", wh);
        List<Warehouse> warehouses = admin.listWarehouses();
        assertFalse(warehouses.isEmpty());
        assertEquals("wh-admin-1", warehouses.get(0).getId());
    }

    // --- TEST 6: SupervisorUseCaseImpl ---
    @Test
    @DisplayName("SupervisorUseCaseImpl: consulta de auditoría, pedidos y resumen de inventario")
    void testSupervisorUseCase() {
        FakeAuditLogRepository auditRepo = new FakeAuditLogRepository();
        FakeOrderRepository orderRepo = new FakeOrderRepository();
        FakeProductStockRepository stockRepo = new FakeProductStockRepository();

        auditRepo.save(new AuditLog("op-1", "LOGISTICS_OPERATOR", "DISPATCH", "Order", "order-1", "Despacho"));
        orderRepo.save(new Order("buyer-1"));
        stockRepo.save(new ProductStock("prod-1", null, "wh-1", 100));

        ConsultAuditLogService consultAuditLogService = new ConsultAuditLogService(auditRepo);
        ConsultOrderService consultOrderService = new ConsultOrderService(orderRepo);
        ConsultInventoryService consultInventoryService = new ConsultInventoryService(stockRepo, null);

        SupervisorUseCaseImpl supervisor = new SupervisorUseCaseImpl(
                consultAuditLogService, consultOrderService, consultInventoryService);

        assertEquals(1, supervisor.getAuditLogs("sup-1", null).size());
        assertEquals(1, supervisor.getAllOrders("sup-1").size());
        assertEquals(1, supervisor.getInventorySummary("sup-1").size());
    }
}
