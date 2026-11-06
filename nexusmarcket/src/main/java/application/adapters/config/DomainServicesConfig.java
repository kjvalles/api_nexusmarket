package application.adapters.config;

import application.domain.ports.out.*;
import application.domain.services.audit.ConsultAuditLogService;
import application.domain.services.auth.ChangePasswordService;
import application.domain.services.auth.LoginService;
import application.domain.services.auth.LogoutService;
import application.domain.services.auth.RegisterBuyerService;
import application.domain.services.cart.ConsultCartService;
import application.domain.services.cart.ManageCartService;
import application.domain.services.catalog.*;
import application.domain.services.inventory.CheckStockAvailabilityService;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring para exponer los Servicios de Dominio puros como beans inyectables.
 * Mantiene la capa de dominio 100% agnóstica de frameworks sin anotaciones @Service.
 */
@Configuration
public class DomainServicesConfig {

    // --- Servicios de Autenticación y Cuentas ---
    @Bean
    public LoginService loginService(UserRepositoryPort userRepositoryPort,
                                     PasswordServicePort passwordServicePort,
                                     JwtServicePort jwtServicePort) {
        return new LoginService(userRepositoryPort, passwordServicePort, jwtServicePort);
    }

    @Bean
    public RegisterBuyerService registerBuyerService(UserRepositoryPort userRepositoryPort,
                                                     BuyerRepositoryPort buyerRepositoryPort,
                                                     PasswordServicePort passwordServicePort) {
        return new RegisterBuyerService(userRepositoryPort, buyerRepositoryPort, passwordServicePort);
    }

    @Bean
    public ChangePasswordService changePasswordService(UserRepositoryPort userRepositoryPort,
                                                       PasswordServicePort passwordServicePort) {
        return new ChangePasswordService(userRepositoryPort, passwordServicePort);
    }

    @Bean
    public LogoutService logoutService(UserRepositoryPort userRepositoryPort) {
        return new LogoutService(userRepositoryPort);
    }

    @Bean
    public RegisterUserService registerUserService(UserRepositoryPort userRepositoryPort,
                                                   PasswordServicePort passwordServicePort) {
        return new RegisterUserService(userRepositoryPort, passwordServicePort);
    }

    @Bean
    public RegisterSellerService registerSellerService(UserRepositoryPort userRepositoryPort,
                                                       SellerRepositoryPort sellerRepositoryPort,
                                                       PasswordServicePort passwordServicePort) {
        return new RegisterSellerService(userRepositoryPort, sellerRepositoryPort, passwordServicePort);
    }

    @Bean
    public ChangeUserStatusService changeUserStatusService(UserRepositoryPort userRepositoryPort) {
        return new ChangeUserStatusService(userRepositoryPort);
    }

    @Bean
    public ConsultUserService consultUserService(UserRepositoryPort userRepositoryPort,
                                                 SellerRepositoryPort sellerRepositoryPort) {
        return new ConsultUserService(userRepositoryPort, sellerRepositoryPort);
    }

    // --- Servicios de Carrito ---
    @Bean
    public ConsultCartService consultCartService(ShoppingCartRepositoryPort shoppingCartRepositoryPort) {
        return new ConsultCartService(shoppingCartRepositoryPort);
    }

    @Bean
    public ManageCartService manageCartService(ShoppingCartRepositoryPort shoppingCartRepositoryPort,
                                               ConsultCartService consultCartService) {
        return new ManageCartService(shoppingCartRepositoryPort, consultCartService);
    }

    // --- Servicios de Pedidos ---
    @Bean
    public CreateOrderService createOrderService(OrderRepositoryPort orderRepositoryPort,
                                                 ProductRepositoryPort productRepositoryPort) {
        return new CreateOrderService(orderRepositoryPort, productRepositoryPort);
    }

    @Bean
    public TransitionOrderStatusService transitionOrderStatusService(OrderRepositoryPort orderRepositoryPort) {
        return new TransitionOrderStatusService(orderRepositoryPort);
    }

    @Bean
    public CancelOrderService cancelOrderService(OrderRepositoryPort orderRepositoryPort) {
        return new CancelOrderService(orderRepositoryPort);
    }

    @Bean
    public ConsultOrderService consultOrderService(OrderRepositoryPort orderRepositoryPort) {
        return new ConsultOrderService(orderRepositoryPort);
    }

    // --- Servicios de Catálogo ---
    @Bean
    public CreateProductService createProductService(ProductRepositoryPort productRepositoryPort) {
        return new CreateProductService(productRepositoryPort);
    }

    @Bean
    public PublishProductService publishProductService(ProductRepositoryPort productRepositoryPort) {
        return new PublishProductService(productRepositoryPort);
    }

    @Bean
    public UpdateProductService updateProductService(ProductRepositoryPort productRepositoryPort) {
        return new UpdateProductService(productRepositoryPort);
    }

    @Bean
    public SuspendProductService suspendProductService(ProductRepositoryPort productRepositoryPort) {
        return new SuspendProductService(productRepositoryPort);
    }

    @Bean
    public ConsultCatalogService consultCatalogService(ProductRepositoryPort productRepositoryPort) {
        return new ConsultCatalogService(productRepositoryPort);
    }

    // --- Servicios de Inventario y Bodegas ---
    @Bean
    public RegisterInventoryMovementService registerInventoryMovementService(ProductStockRepositoryPort productStockRepositoryPort,
                                                                             InventoryMovementRepositoryPort inventoryMovementRepositoryPort) {
        return new RegisterInventoryMovementService(productStockRepositoryPort, inventoryMovementRepositoryPort);
    }

    @Bean
    public CheckStockAvailabilityService checkStockAvailabilityService(ProductStockRepositoryPort productStockRepositoryPort) {
        return new CheckStockAvailabilityService(productStockRepositoryPort);
    }

    @Bean
    public ManageWarehouseService manageWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new ManageWarehouseService(warehouseRepositoryPort);
    }

    @Bean
    public ConsultInventoryService consultInventoryService(ProductStockRepositoryPort productStockRepositoryPort,
                                                           ProductRepositoryPort productRepositoryPort) {
        return new ConsultInventoryService(productStockRepositoryPort, productRepositoryPort);
    }

    // --- Servicios de Auditoría ---
    @Bean
    public ConsultAuditLogService consultAuditLogService(AuditLogRepositoryPort auditLogRepositoryPort) {
        return new ConsultAuditLogService(auditLogRepositoryPort);
    }
}
