package application.adapters.config;

import application.domain.ports.out.*;
import application.domain.services.auth.ChangePasswordService;
import application.domain.services.auth.LoginService;
import application.domain.services.auth.LogoutService;
import application.domain.services.auth.RegisterBuyerService;
import application.domain.services.catalog.CreateProductService;
import application.domain.services.catalog.PublishProductService;
import application.domain.services.catalog.UpdateProductService;
import application.domain.services.inventory.CheckStockAvailabilityService;
import application.domain.services.inventory.RegisterInventoryMovementService;
import application.domain.services.order.CancelOrderService;
import application.domain.services.order.CreateOrderService;
import application.domain.services.order.TransitionOrderStatusService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring para exponer los Servicios de Dominio puros como beans inyectables.
 * Mantiene la capa de dominio 100% agnóstica de frameworks sin anotaciones @Service.
 */
@Configuration
public class DomainServicesConfig {

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
    public RegisterInventoryMovementService registerInventoryMovementService(ProductStockRepositoryPort productStockRepositoryPort,
                                                                             InventoryMovementRepositoryPort inventoryMovementRepositoryPort) {
        return new RegisterInventoryMovementService(productStockRepositoryPort, inventoryMovementRepositoryPort);
    }

    @Bean
    public CheckStockAvailabilityService checkStockAvailabilityService(ProductStockRepositoryPort productStockRepositoryPort) {
        return new CheckStockAvailabilityService(productStockRepositoryPort);
    }

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
}
