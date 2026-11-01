package application.domain;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ProductStockRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.auth.LoginService;
import application.domain.services.auth.RegisterBuyerService;
import application.domain.services.catalog.PublishProductService;
import application.domain.services.inventory.CheckStockAvailabilityService;
import application.domain.services.order.CreateOrderService;
import application.domain.valueobjects.OrderLine;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Servicios de Dominio Puros")
class ServicesDomainTest {

    // In-memory fake repository implementations for tests
    static class FakeUserRepository implements UserRepositoryPort {
        private final Map<String, User> byId = new HashMap<>();
        private final Map<String, User> byEmail = new HashMap<>();

        @Override
        public User save(User user) {
            if (user.getId() == null) user.setId("user-" + (byId.size() + 1));
            byId.put(user.getId(), user);
            byEmail.put(user.getEmail(), user);
            return user;
        }

        @Override
        public Optional<User> findById(String id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.ofNullable(byEmail.get(email));
        }

        @Override
        public Optional<User> findByIdentityDocument(String document) {
            return byId.values().stream()
                    .filter(u -> document.equals(u.getIdentityDocument()))
                    .findFirst();
        }

        @Override
        public boolean existsByEmail(String email) {
            return byEmail.containsKey(email);
        }

        @Override
        public boolean existsByIdentityDocument(String document) {
            return byId.values().stream().anyMatch(u -> document.equals(u.getIdentityDocument()));
        }

        @Override
        public void update(User user) {
            save(user);
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>(byId.values());
        }
    }

    static class FakeBuyerRepository implements BuyerRepositoryPort {
        private final Map<String, Buyer> store = new HashMap<>();

        @Override
        public Buyer save(Buyer buyer) {
            if (buyer.getId() == null) buyer.setId("buyer-" + (store.size() + 1));
            store.put(buyer.getId(), buyer);
            return buyer;
        }

        @Override
        public Optional<Buyer> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }
    }

    static class FakePasswordService implements PasswordServicePort {
        @Override
        public String encode(CharSequence rawPassword) {
            return "hashed_" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return ("hashed_" + rawPassword).equals(encodedPassword);
        }
    }

    static class FakeJwtService implements JwtServicePort {
        @Override
        public String generateToken(User user) {
            return "jwt.token.for." + user.getId();
        }

        @Override
        public boolean validateToken(String token) {
            return token != null && token.startsWith("jwt.token");
        }

        @Override
        public String extractUserId(String token) {
            return "user-1";
        }

        @Override
        public Integer extractTokenVersion(String token) {
            return 1;
        }

        @Override
        public User reconstructUser(String token) {
            return null;
        }
    }

    static class FakeProductRepository implements ProductRepositoryPort {
        private final Map<String, Product> store = new HashMap<>();

        @Override
        public Product save(Product product) {
            if (product.getId() == null) product.setId("prod-" + (store.size() + 1));
            store.put(product.getId(), product);
            return product;
        }

        @Override
        public Optional<Product> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Product> findByVendorId(String vendorId) {
            return store.values().stream().filter(p -> vendorId.equals(p.getVendorId())).toList();
        }

        @Override
        public List<Product> findPublished() {
            return store.values().stream().filter(Product::isPublished).toList();
        }

        @Override
        public List<Product> findPublishedByCategory(String category) {
            return store.values().stream().filter(p -> p.isPublished() && category.equals(p.getCategory())).toList();
        }

        @Override
        public boolean existsById(String id) {
            return store.containsKey(id);
        }
    }

    static class FakeOrderRepository implements OrderRepositoryPort {
        private final Map<Long, Order> store = new HashMap<>();
        private long counter = 1;

        @Override
        public Order save(Order order) {
            if (order.getId() == null) order.setId(counter++);
            store.put(order.getId(), order);
            return order;
        }

        @Override
        public Optional<Order> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Order> findByBuyerId(String buyerId) {
            return store.values().stream().filter(o -> buyerId.equals(o.getBuyerId())).toList();
        }

        @Override
        public List<Order> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    static class FakeProductStockRepository implements ProductStockRepositoryPort {
        private final Map<String, ProductStock> store = new HashMap<>();

        @Override
        public ProductStock save(ProductStock stock) {
            String key = stock.getProductId() + ":" + stock.getWarehouseId();
            store.put(key, stock);
            return stock;
        }

        @Override
        public Optional<ProductStock> findByProductVariantAndWarehouse(String productId, String variantId, String warehouseId) {
            return Optional.ofNullable(store.get(productId + ":" + warehouseId));
        }

        @Override
        public List<ProductStock> findByProductId(String productId) {
            return store.values().stream().filter(s -> productId.equals(s.getProductId())).toList();
        }

        @Override
        public List<ProductStock> findByWarehouseId(String warehouseId) {
            return store.values().stream().filter(s -> warehouseId.equals(s.getWarehouseId())).toList();
        }

        @Override
        public List<ProductStock> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    @Test
    @DisplayName("LoginService: debe autenticar satisfactoriamente credenciales correctas")
    void testLoginServiceSuccess() {
        FakeUserRepository userRepo = new FakeUserRepository();
        FakePasswordService pwdService = new FakePasswordService();
        FakeJwtService jwtService = new FakeJwtService();

        Buyer buyer = new Buyer();
        buyer.setEmail("user@test.com");
        buyer.setPassword(pwdService.encode("secret123"));
        buyer.setStatus(UserStatus.ACTIVE);
        userRepo.save(buyer);

        LoginService service = new LoginService(userRepo, pwdService, jwtService);
        AuthenticationResult result = service.login("user@test.com", "secret123");

        assertNotNull(result);
        assertEquals("user@test.com", result.getAuthenticatedUser().getEmail());
        assertTrue(result.getToken().startsWith("jwt.token"));
    }

    @Test
    @DisplayName("LoginService: debe rechazar contraseña incorrecta con InvalidCredentialsException")
    void testLoginServiceBadPassword() {
        FakeUserRepository userRepo = new FakeUserRepository();
        FakePasswordService pwdService = new FakePasswordService();
        FakeJwtService jwtService = new FakeJwtService();

        Buyer buyer = new Buyer();
        buyer.setEmail("user@test.com");
        buyer.setPassword(pwdService.encode("secret123"));
        buyer.setStatus(UserStatus.ACTIVE);
        userRepo.save(buyer);

        LoginService service = new LoginService(userRepo, pwdService, jwtService);
        assertThrows(InvalidCredentialsException.class, () -> service.login("user@test.com", "wrongPassword"));
    }

    @Test
    @DisplayName("RegisterBuyerService: debe registrar comprador exitosamente y hashear clave")
    void testRegisterBuyerSuccess() {
        FakeUserRepository userRepo = new FakeUserRepository();
        FakeBuyerRepository buyerRepo = new FakeBuyerRepository();
        FakePasswordService pwdService = new FakePasswordService();

        RegisterBuyerService service = new RegisterBuyerService(userRepo, buyerRepo, pwdService);

        Buyer buyer = new Buyer();
        buyer.setEmail("nuevo@test.com");
        buyer.setIdentityDocument("CC-98765432");
        buyer.setFullName("Juan Comprador");

        Buyer registered = service.registerBuyer(buyer, "mypassword");

        assertNotNull(registered);
        assertEquals("hashed_mypassword", registered.getPassword());
        assertEquals(UserStatus.ACTIVE, registered.getStatus());
        assertTrue(userRepo.existsByEmail("nuevo@test.com"));
    }

    @Test
    @DisplayName("RegisterBuyerService: debe rechazar registro con correo duplicado")
    void testRegisterBuyerDuplicateEmail() {
        FakeUserRepository userRepo = new FakeUserRepository();
        FakeBuyerRepository buyerRepo = new FakeBuyerRepository();
        FakePasswordService pwdService = new FakePasswordService();

        Buyer existing = new Buyer();
        existing.setEmail("duplicado@test.com");
        existing.setIdentityDocument("DOC-1");
        userRepo.save(existing);

        RegisterBuyerService service = new RegisterBuyerService(userRepo, buyerRepo, pwdService);

        Buyer newBuyer = new Buyer();
        newBuyer.setEmail("duplicado@test.com");
        newBuyer.setIdentityDocument("DOC-2");

        assertThrows(DomainException.class, () -> service.registerBuyer(newBuyer, "password"));
    }

    @Test
    @DisplayName("PublishProductService: debe publicar producto y cambiar su estado")
    void testPublishProductService() {
        FakeProductRepository productRepo = new FakeProductRepository();
        PublishProductService service = new PublishProductService(productRepo);

        Product product = new Product("vendor-1", "Laptop Gamer", "ELECTRONICS", new BigDecimal("1200.00"), ProductType.PHYSICAL);
        productRepo.save(product);

        Product published = service.publishProduct(product.getId());
        assertTrue(published.isPublished());
    }

    @Test
    @DisplayName("PublishProductService: debe lanzar EntityNotFoundException si el producto no existe")
    void testPublishProductNotFound() {
        FakeProductRepository productRepo = new FakeProductRepository();
        PublishProductService service = new PublishProductService(productRepo);

        assertThrows(EntityNotFoundException.class, () -> service.publishProduct("inexistente"));
    }

    @Test
    @DisplayName("CheckStockAvailabilityService: debe lanzar ProductNotAvailableException si stock es insuficiente")
    void testCheckStockInsufficient() {
        FakeProductStockRepository stockRepo = new FakeProductStockRepository();
        ProductStock stock = new ProductStock("prod-1", null, "wh-1", 5);
        stockRepo.save(stock);

        CheckStockAvailabilityService service = new CheckStockAvailabilityService(stockRepo);
        assertTrue(service.isAvailable("prod-1", null, "wh-1", 5));
        assertFalse(service.isAvailable("prod-1", null, "wh-1", 6));

        assertThrows(ProductNotAvailableException.class, () -> service.ensureAvailable("prod-1", null, "wh-1", 10));
    }

    @Test
    @DisplayName("CreateOrderService: debe crear pedido para producto publicado")
    void testCreateOrderService() {
        FakeOrderRepository orderRepo = new FakeOrderRepository();
        FakeProductRepository productRepo = new FakeProductRepository();

        Product product = new Product("vendor-1", "Monitor 4K", "TECH", new BigDecimal("400.00"), ProductType.PHYSICAL);
        product.publish();
        productRepo.save(product);

        CreateOrderService service = new CreateOrderService(orderRepo, productRepo);

        OrderLine line = new OrderLine(product.getId(), null, product.getName(), product.getBasePrice(), 2);
        Order order = service.createOrder("buyer-1", List.of(line), "Entregar en portería");

        assertNotNull(order);
        assertEquals(new BigDecimal("800.00"), order.getSubtotal());
        assertEquals("buyer-1", order.getBuyerId());
    }
}
