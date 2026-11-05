package application.e2e;

import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.models.ShoppingCart;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.ports.in.PublicAccessPort;
import application.domain.ports.in.SupervisorPort;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ProductStockRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de Verificación End-to-End (E2E) de la plataforma NexusMarket.
 * Valida la consistencia de datos sembrados (MySQL + MongoDB) y la interacción
 * a través de los puertos de entrada por rol.
 */
@SpringBootTest
class EndToEndPlatformIntegrationTest {

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    @Autowired
    private BuyerRepositoryPort buyerRepositoryPort;

    @Autowired
    private WarehouseRepositoryPort warehouseRepositoryPort;

    @Autowired
    private ProductRepositoryPort productRepositoryPort;

    @Autowired
    private ProductStockRepositoryPort productStockRepositoryPort;

    @Autowired
    private OrderRepositoryPort orderRepositoryPort;

    @Autowired
    private AuditLogRepositoryPort auditLogRepositoryPort;

    @Autowired
    private PublicAccessPort publicAccessPort;

    @Autowired
    private BuyerPort buyerPort;

    @Autowired
    private LogisticsOperatorPort logisticsOperatorPort;

    @Autowired
    private SupervisorPort supervisorPort;

    @Test
    @DisplayName("AC-01 & AC-02: Verificar presencia de usuarios semilla y bodegas físicas")
    void testSeededUsersAndWarehouses() {
        // Verificar Administrador
        Optional<User> admin = userRepositoryPort.findByEmail("admin@nexusmarcket.com");
        assertTrue(admin.isPresent(), "El usuario admin debe haber sido sembrado");
        assertEquals("ADMINISTRATOR", admin.get().getRole().name());

        // Verificar Comprador
        Optional<User> buyer = userRepositoryPort.findByEmail("comprador@nexusmarcket.com");
        assertTrue(buyer.isPresent(), "El usuario comprador debe haber sido sembrado");
        assertEquals("BUYER", buyer.get().getRole().name());

        // Verificar Bodega Central
        assertTrue(warehouseRepositoryPort.existsById("wh-central-01"), "La bodega central debe existir");
        assertTrue(warehouseRepositoryPort.existsById("wh-seller-01"), "La bodega del vendedor debe existir");
    }

    @Test
    @DisplayName("AC-03 & AC-04: Verificar catálogo en MongoDB y existencias de inventario en MySQL")
    void testSeededProductsAndStock() {
        List<Product> publishedProducts = productRepositoryPort.findPublished();
        assertFalse(publishedProducts.isEmpty(), "Debe haber productos publicados en MongoDB");

        Optional<Product> laptop = productRepositoryPort.findById("prod-laptop-01");
        assertTrue(laptop.isPresent(), "La laptop gamer debe existir en el catálogo");
        assertEquals(2, laptop.get().getVariants().size(), "Debe tener 2 variantes registradas");

        // Verificar existencias en MySQL
        List<ProductStock> stocks = productStockRepositoryPort.findByProductId("prod-laptop-01");
        assertFalse(stocks.isEmpty(), "Debe existir registro de stock en MySQL para prod-laptop-01");
    }

    @Test
    @DisplayName("AC-05 & AC-06: Verificar carrito, órdenes de prueba y auditoría")
    void testSeededCartOrdersAndAudit() {
        ShoppingCart cart = buyerPort.getCart("usr-buyer-01");
        assertNotNull(cart, "El carrito del comprador debe existir");
        assertFalse(cart.isEmpty(), "El carrito debe tener ítems sembrados");

        List<Order> buyerOrders = orderRepositoryPort.findByBuyerId("usr-buyer-01");
        assertFalse(buyerOrders.isEmpty(), "Deben existir órdenes de prueba para el comprador");

        assertFalse(auditLogRepositoryPort.findAll().isEmpty(), "Deben existir bitácoras de auditoría");
    }

    @Test
    @DisplayName("AC-07: Flujo E2E - Autenticación, Catálogo, Despacho y Monitoreo")
    void testFullCommercialCycleE2E() {
        // 1. Login exitoso con credenciales sembradas retornando token JWT
        String token = publicAccessPort.login("comprador@nexusmarcket.com", "Password123*");
        assertNotNull(token, "La autenticación debe ser exitosa");
        assertFalse(token.isBlank(), "Debe retornar un JWT válido");

        // 2. Consulta de catálogo público
        List<Product> catalog = publicAccessPort.getPublicCatalog();
        assertTrue(catalog.size() >= 3, "El catálogo público debe retornar al menos 3 productos publicados");

        // 3. Consulta de órdenes del comprador
        List<Order> orders = buyerPort.getMyOrders("usr-buyer-01");
        assertFalse(orders.isEmpty(), "El comprador debe tener órdenes");

        // 4. Operación logística: Despachar orden PENDING_PAYMENT / PREPARATION
        Optional<Order> orderToDispatch = orders.stream()
                .filter(o -> !OrderStatus.DELIVERED.equals(o.getStatus()))
                .findFirst();

        if (orderToDispatch.isPresent()) {
            Order o = orderToDispatch.get();
            // Simular transición a PAID para permitir despacho
            o.setStatus(OrderStatus.PAID);
            orderRepositoryPort.save(o);

            logisticsOperatorPort.dispatchOrder("usr-logistica-01", o.getId(), "GUIA-E2E-TEST-999");
            Order dispatched = orderRepositoryPort.findById(o.getId()).orElseThrow();
            assertEquals(OrderStatus.DISPATCHED, dispatched.getStatus());
        }

        // 5. Monitoreo por Supervisor
        List<Order> allOrders = supervisorPort.getAllOrders("usr-supervisor-01");
        assertFalse(allOrders.isEmpty(), "El supervisor debe tener acceso a todas las órdenes globales");
    }
}
