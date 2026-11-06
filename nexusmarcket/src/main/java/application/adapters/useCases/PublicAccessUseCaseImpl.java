package application.adapters.useCases;

import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.ports.in.PublicAccessPort;
import application.domain.services.auth.LoginService;
import application.domain.services.auth.LogoutService;
import application.domain.services.auth.RegisterBuyerService;
import application.domain.services.catalog.ConsultCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para operaciones públicas y autenticación.
 * Delega 100% de la lógica en los servicios de dominio correspondientes.
 */
@Service
@RequiredArgsConstructor
public class PublicAccessUseCaseImpl implements PublicAccessPort {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RegisterBuyerService registerBuyerService;
    private final ConsultCatalogService consultCatalogService;

    @Override
    public String login(String emailOrDocument, String rawPassword) {
        AuthenticationResult result = loginService.login(emailOrDocument, rawPassword);
        return result.getToken();
    }

    @Override
    public void logout(String userId) {
        if (userId != null && !userId.isBlank()) {
            logoutService.logout(userId);
        }
    }

    @Override
    public Buyer registerBuyer(Buyer buyer, String rawPassword) {
        return registerBuyerService.registerBuyer(buyer, rawPassword);
    }

    @Override
    public List<Product> getPublicCatalog() {
        return consultCatalogService.getPublicCatalog();
    }

    @Override
    public Product getPublicProductDetails(String productId) {
        return consultCatalogService.getPublicProductDetails(productId);
    }
}
