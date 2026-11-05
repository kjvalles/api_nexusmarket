package application.infrastructure.seed;

import application.domain.enums.UserRole;
import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.DefaultUser;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.models.Seller;
import application.domain.models.ShoppingCart;
import application.domain.models.Warehouse;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ProductStockRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerCommercialStatus;
import application.domain.valueobjects.CartItem;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.OrderLine;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Componente de inicialización y sembrado de datos (DatabaseSeeder).
 * Idempotente: se ejecuta al arrancar el servidor sin duplicar registros.
 */
@Slf4j
@Component
@org.springframework.core.annotation.Order(1)
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepositoryPort userRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductStockRepositoryPort productStockRepositoryPort;
    private final InventoryMovementRepositoryPort inventoryMovementRepositoryPort;
    private final ShoppingCartRepositoryPort shoppingCartRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final AuditLogRepositoryPort auditLogRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public static final String DEFAULT_PASSWORD = "Password123*";

    @Override
    public void run(String... args) {
        log.info("==> [DATABASE SEEDER] Iniciando verificación y carga de datos semilla...");
        try {
            seedUsers();
            seedWarehouses();
            seedProducts();
            seedInventoryAndKardex();
            seedCartAndOrders();
            seedAuditLogs();
            log.info("==> [DATABASE SEEDER] Carga inicial completada con éxito.");
        } catch (Exception e) {
            log.error("==> [DATABASE SEEDER] Error durante el sembrado de datos: {}", e.getMessage(), e);
        }
    }

    private void seedUsers() {
        String encodedPassword = passwordServicePort.encode(DEFAULT_PASSWORD);

        // 1. Administrador (ADMINISTRATOR)
        if (!userRepositoryPort.existsByEmail("admin@nexusmarcket.com")) {
            DefaultUser admin = new DefaultUser(UserRole.ADMINISTRATOR);
            admin.setId("usr-admin-01");
            admin.setEmail("admin@nexusmarcket.com");
            admin.setFullName("Administrador del Sistema");
            admin.setIdentityDocument("CC1000000001");
            admin.setPassword(encodedPassword);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setAuthTokenVersion(1);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepositoryPort.save(admin);
            log.info("Sembrado usuario Administrador: admin@nexusmarcket.com");
        }

        // 2. Supervisor (SUPERVISOR)
        if (!userRepositoryPort.existsByEmail("supervisor@nexusmarcket.com")) {
            DefaultUser supervisor = new DefaultUser(UserRole.SUPERVISOR);
            supervisor.setId("usr-supervisor-01");
            supervisor.setEmail("supervisor@nexusmarcket.com");
            supervisor.setFullName("Supervisor de Operaciones");
            supervisor.setIdentityDocument("CC1000000002");
            supervisor.setPassword(encodedPassword);
            supervisor.setStatus(UserStatus.ACTIVE);
            supervisor.setAuthTokenVersion(1);
            supervisor.setCreatedAt(LocalDateTime.now());
            supervisor.setUpdatedAt(LocalDateTime.now());
            userRepositoryPort.save(supervisor);
            log.info("Sembrado usuario Supervisor: supervisor@nexusmarcket.com");
        }

        // 3. Operador Logístico (LOGISTICS_OPERATOR)
        if (!userRepositoryPort.existsByEmail("logistica@nexusmarcket.com")) {
            DefaultUser logistica = new DefaultUser(UserRole.LOGISTICS_OPERATOR);
            logistica.setId("usr-logistica-01");
            logistica.setEmail("logistica@nexusmarcket.com");
            logistica.setFullName("Operador Logístico Central");
            logistica.setIdentityDocument("CC1000000003");
            logistica.setPassword(encodedPassword);
            logistica.setStatus(UserStatus.ACTIVE);
            logistica.setAuthTokenVersion(1);
            logistica.setCreatedAt(LocalDateTime.now());
            logistica.setUpdatedAt(LocalDateTime.now());
            userRepositoryPort.save(logistica);
            log.info("Sembrado usuario Logística: logistica@nexusmarcket.com");
        }

        // 4. Vendedor (SELLER)
        if (!userRepositoryPort.existsByEmail("vendedor@nexusmarcket.com")) {
            Seller seller = new Seller();
            seller.setId("usr-seller-01");
            seller.setEmail("vendedor@nexusmarcket.com");
            seller.setFullName("Representante Tech Store");
            seller.setIdentityDocument("CC1000000005");
            seller.setTaxId("NIT-900123456");
            seller.setBusinessName("Tech Store Colombia");
            seller.setBusinessDescription("Tienda especializada en tecnología y hardware gamer");
            seller.setPassword(encodedPassword);
            seller.setStatus(UserStatus.ACTIVE);
            seller.setSellerStatus(SellerStatus.ACTIVE);
            seller.setRating(4.9);
            seller.setWarehouseIds(List.of("wh-seller-01"));
            seller.setAuthTokenVersion(1);
            seller.setCreatedAt(LocalDateTime.now());
            seller.setUpdatedAt(LocalDateTime.now());
            sellerRepositoryPort.save(seller);
            log.info("Sembrado usuario Vendedor: vendedor@nexusmarcket.com");
        }

        // 5. Comprador (BUYER)
        if (!userRepositoryPort.existsByEmail("comprador@nexusmarcket.com")) {
            Buyer buyer = new Buyer();
            buyer.setId("usr-buyer-01");
            buyer.setEmail("comprador@nexusmarcket.com");
            buyer.setFullName("Carlos Comprador");
            buyer.setIdentityDocument("CC1000000004");
            buyer.setPassword(encodedPassword);
            buyer.setStatus(UserStatus.ACTIVE);
            buyer.setCommercialStatus(BuyerCommercialStatus.ENABLED);
            buyer.setVirtualBalance(BigDecimal.valueOf(15000000.0));
            buyer.setRating(5.0);
            buyer.setPrimaryAddress(new Address("Calle 100 # 15-20", "Bogotá", "Cundinamarca", "110111", "Colombia", "Apto 502"));
            buyer.setAuthTokenVersion(1);
            buyer.setCreatedAt(LocalDateTime.now());
            buyer.setUpdatedAt(LocalDateTime.now());
            buyerRepositoryPort.save(buyer);
            log.info("Sembrado usuario Comprador: comprador@nexusmarcket.com");
        }
    }

    private void seedWarehouses() {
        if (!warehouseRepositoryPort.existsById("wh-central-01")) {
            Warehouse wh1 = new Warehouse();
            wh1.setId("wh-central-01");
            wh1.setName("Bodega Central Bogotá");
            wh1.setType(WarehouseType.MARKETPLACE);
            wh1.setStatus(WarehouseStatus.ACTIVE);
            wh1.setMaxCapacity(10000);
            wh1.setDescription("Centro de distribución principal Marketplace");
            wh1.setAddress(new Address("Av. El Dorado # 100-50", "Bogotá", "Cundinamarca", "110911", "Colombia", "Muelle 4"));
            wh1.setCreatedAt(LocalDateTime.now());
            wh1.setUpdatedAt(LocalDateTime.now());
            warehouseRepositoryPort.save(wh1);
            log.info("Sembrada bodega: wh-central-01");
        }

        if (!warehouseRepositoryPort.existsById("wh-seller-01")) {
            Warehouse wh2 = new Warehouse();
            wh2.setId("wh-seller-01");
            wh2.setName("Bodega Tech Medellín");
            wh2.setVendorId("usr-seller-01");
            wh2.setType(WarehouseType.SELLER);
            wh2.setStatus(WarehouseStatus.ACTIVE);
            wh2.setMaxCapacity(2500);
            wh2.setDescription("Bodega satélite de Tech Store Colombia");
            wh2.setAddress(new Address("Cra 43A # 1-50", "Medellín", "Antioquia", "050021", "Colombia", "El Poblado"));
            wh2.setCreatedAt(LocalDateTime.now());
            wh2.setUpdatedAt(LocalDateTime.now());
            warehouseRepositoryPort.save(wh2);
            log.info("Sembrada bodega: wh-seller-01");
        }
    }

    private void seedProducts() {
        // Producto 1: Laptop Gamer Pro (Físico, Publicado con 2 variantes)
        if (!productRepositoryPort.existsById("prod-laptop-01")) {
            Product p1 = new Product("usr-seller-01", "Laptop Gamer Pro 16", "Computadores", BigDecimal.valueOf(4500000.0), ProductType.PHYSICAL);
            p1.setId("prod-laptop-01");
            p1.setDescription("Portátil de alto rendimiento con procesador Intel i9 y RTX 4080");
            p1.addVariant(new ProductVariant("var-laptop-01", "Negro", "16GB RAM / 1TB SSD", "LAP-GAM-16-BLK", BigDecimal.ZERO, "https://nexus.com/img/lap1.png", true));
            p1.addVariant(new ProductVariant("var-laptop-02", "Gris Titanio", "32GB RAM / 2TB SSD", "LAP-GAM-32-GRY", BigDecimal.valueOf(800000.0), "https://nexus.com/img/lap2.png", true));
            p1.publish();
            productRepositoryPort.save(p1);
            log.info("Sembrado producto: prod-laptop-01");
        }

        // Producto 2: Teclado Mecánico RGB (Físico, Publicado con 2 variantes)
        if (!productRepositoryPort.existsById("prod-teclado-01")) {
            Product p2 = new Product("usr-seller-01", "Teclado Mecánico RGB Switch Red", "Accesorios", BigDecimal.valueOf(280000.0), ProductType.PHYSICAL);
            p2.setId("prod-teclado-01");
            p2.setDescription("Teclado gamer 75% hot-swappable con iluminación RGB personalizable");
            p2.addVariant(new ProductVariant("var-kbd-01", "Negro", "Standard", "KBD-RGB-RED-BLK", BigDecimal.ZERO, "https://nexus.com/img/kbd1.png", true));
            p2.addVariant(new ProductVariant("var-kbd-02", "Blanco", "Standard", "KBD-RGB-RED-WHT", BigDecimal.valueOf(20000.0), "https://nexus.com/img/kbd2.png", true));
            p2.publish();
            productRepositoryPort.save(p2);
            log.info("Sembrado producto: prod-teclado-01");
        }

        // Producto 3: Audífonos Inalámbricos ANC (Físico, Publicado)
        if (!productRepositoryPort.existsById("prod-audifonos-01")) {
            Product p3 = new Product("usr-seller-01", "Audífonos Inalámbricos Pro ANC", "Audio", BigDecimal.valueOf(350000.0), ProductType.PHYSICAL);
            p3.setId("prod-audifonos-01");
            p3.setDescription("Auriculares inalámbricos con cancelación activa de ruido y 40h de batería");
            p3.addVariant(new ProductVariant("var-aud-01", "Negro Mate", "Universal", "AUD-ANC-PRO-BLK", BigDecimal.ZERO, "https://nexus.com/img/aud1.png", true));
            p3.publish();
            productRepositoryPort.save(p3);
            log.info("Sembrado producto: prod-audifonos-01");
        }

        // Producto 4: Licencia Software (Digital, Publicado)
        if (!productRepositoryPort.existsById("prod-antivirus-01")) {
            Product p4 = new Product("usr-seller-01", "Licencia Suite Antivirus Pro 2026", "Software", BigDecimal.valueOf(120000.0), ProductType.DIGITAL);
            p4.setId("prod-antivirus-01");
            p4.setDescription("Protección digital en tiempo real para hasta 5 dispositivos por 1 año");
            p4.publish();
            productRepositoryPort.save(p4);
            log.info("Sembrado producto: prod-antivirus-01");
        }

        // Producto 5: Mouse Básico (Físico, Suspendido)
        if (!productRepositoryPort.existsById("prod-mouse-01")) {
            Product p5 = new Product("usr-seller-01", "Mouse Inalámbrico Básico", "Accesorios", BigDecimal.valueOf(45000.0), ProductType.PHYSICAL);
            p5.setId("prod-mouse-01");
            p5.setDescription("Mouse óptico inalámbrico 1600 DPI");
            p5.suspend();
            productRepositoryPort.save(p5);
            log.info("Sembrado producto suspendido: prod-mouse-01");
        }
    }

    private void seedInventoryAndKardex() {
        seedStockRecord("prod-laptop-01", "var-laptop-01", "wh-central-01", 30);
        seedStockRecord("prod-laptop-01", "var-laptop-02", "wh-central-01", 20);
        seedStockRecord("prod-teclado-01", "var-kbd-01", "wh-central-01", 50);
        seedStockRecord("prod-teclado-01", "var-kbd-02", "wh-central-01", 50);
        seedStockRecord("prod-audifonos-01", "var-aud-01", "wh-seller-01", 75);
    }

    private void seedStockRecord(String productId, String variantId, String warehouseId, int quantity) {
        if (productStockRepositoryPort.findByProductVariantAndWarehouse(productId, variantId, warehouseId).isEmpty()) {
            ProductStock stock = new ProductStock(productId, variantId, warehouseId, quantity);
            productStockRepositoryPort.save(stock);

            InventoryMovement movement = new InventoryMovement(
                    productId,
                    variantId,
                    warehouseId,
                    quantity,
                    InventoryMovementType.MANUAL_ENTRY,
                    null,
                    "usr-logistica-01",
                    "Carga inicial de existencias para operación comercial"
            );
            inventoryMovementRepositoryPort.save(movement);
            log.info("Stock sembrado para producto {} ({}) en bodega {}: {} unidades", productId, variantId, warehouseId, quantity);
        }
    }

    private void seedCartAndOrders() {
        // Carrito para comprador
        if (shoppingCartRepositoryPort.findByBuyerId("usr-buyer-01").isEmpty()) {
            ShoppingCart cart = new ShoppingCart("usr-buyer-01");
            cart.addItem(new CartItem(
                    "prod-teclado-01",
                    "var-kbd-01",
                    "Teclado Mecánico RGB Switch Red",
                    "KBD-RGB-RED-BLK",
                    1,
                    BigDecimal.valueOf(280000.0),
                    LocalDateTime.now()
            ));
            shoppingCartRepositoryPort.save(cart);
            log.info("Carrito inicial sembrado para usr-buyer-01");
        }

        // Órdenes de prueba para comprador
        if (orderRepositoryPort.findByBuyerId("usr-buyer-01").isEmpty()) {
            // 1. Orden DELIVERED
            Order o1 = new Order("usr-buyer-01");
            o1.addLine(new OrderLine(
                    "prod-laptop-01",
                    "var-laptop-01",
                    "Laptop Gamer Pro 16",
                    "LAP-GAM-16-BLK",
                    "PHYSICAL",
                    1,
                    BigDecimal.valueOf(4500000.0)
            ));
            o1.setStatus(OrderStatus.DELIVERED);
            o1.setAdditionalNotes("Entregado a satisfacción en portería");
            orderRepositoryPort.save(o1);

            // 2. Orden DISPATCHED
            Order o2 = new Order("usr-buyer-01");
            o2.addLine(new OrderLine(
                    "prod-audifonos-01",
                    "var-aud-01",
                    "Audífonos Inalámbricos Pro ANC",
                    "AUD-ANC-PRO-BLK",
                    "PHYSICAL",
                    1,
                    BigDecimal.valueOf(350000.0)
            ));
            o2.setStatus(OrderStatus.DISPATCHED);
            o2.setAdditionalNotes("Guía Coordinadora 8899001122");
            orderRepositoryPort.save(o2);

            // 3. Orden PENDING_PAYMENT
            Order o3 = new Order("usr-buyer-01");
            o3.addLine(new OrderLine(
                    "prod-teclado-01",
                    "var-kbd-02",
                    "Teclado Mecánico RGB Switch Red",
                    "KBD-RGB-RED-WHT",
                    "PHYSICAL",
                    1,
                    BigDecimal.valueOf(300000.0)
            ));
            o3.setStatus(OrderStatus.PENDING_PAYMENT);
            orderRepositoryPort.save(o3);

            log.info("Órdenes de prueba sembradas para usr-buyer-01");
        }
    }

    private void seedAuditLogs() {
        if (auditLogRepositoryPort.findByActorId("SYSTEM").isEmpty()) {
            auditLogRepositoryPort.save(new AuditLog(
                    "SYSTEM",
                    "SYSTEM",
                    "SYSTEM_INITIALIZATION",
                    "Marketplace",
                    "NEXUS-01",
                    "Inicialización y sembrado de la plataforma NexusMarket"
            ));
            auditLogRepositoryPort.save(new AuditLog(
                    "usr-admin-01",
                    "ADMINISTRATOR",
                    "CREATE_WAREHOUSE",
                    "Warehouse",
                    "wh-central-01",
                    "Creación de bodega central Marketplace Bogotá"
            ));
            auditLogRepositoryPort.save(new AuditLog(
                    "usr-admin-01",
                    "ADMINISTRATOR",
                    "REGISTER_SELLER",
                    "Seller",
                    "usr-seller-01",
                    "Alta administrativa de vendedor Tech Store Colombia"
            ));
            log.info("Registros de auditoría inicial sembrados");
        }
    }
}
