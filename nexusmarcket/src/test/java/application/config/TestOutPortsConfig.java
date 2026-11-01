package application.config;

import application.domain.ports.out.*;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Configuración de prueba que provee mocks condicionales para los puertos de salida (ports/out)
 * mientras se desarrollan los adaptadores reales de persistencia y seguridad (Fases 4 y 5).
 */
@TestConfiguration
public class TestOutPortsConfig {

    @Bean
    @ConditionalOnMissingBean
    public UserRepositoryPort userRepositoryPort() {
        return Mockito.mock(UserRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public BuyerRepositoryPort buyerRepositoryPort() {
        return Mockito.mock(BuyerRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public SellerRepositoryPort sellerRepositoryPort() {
        return Mockito.mock(SellerRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public OrderRepositoryPort orderRepositoryPort() {
        return Mockito.mock(OrderRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProductRepositoryPort productRepositoryPort() {
        return Mockito.mock(ProductRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProductStockRepositoryPort productStockRepositoryPort() {
        return Mockito.mock(ProductStockRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public InventoryMovementRepositoryPort inventoryMovementRepositoryPort() {
        return Mockito.mock(InventoryMovementRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public WarehouseRepositoryPort warehouseRepositoryPort() {
        return Mockito.mock(WarehouseRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public ShoppingCartRepositoryPort shoppingCartRepositoryPort() {
        return Mockito.mock(ShoppingCartRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditLogRepositoryPort auditLogRepositoryPort() {
        return Mockito.mock(AuditLogRepositoryPort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtServicePort jwtServicePort() {
        return Mockito.mock(JwtServicePort.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordServicePort passwordServicePort() {
        return Mockito.mock(PasswordServicePort.class);
    }
}
