package application.adapters.useCases;

import application.domain.exceptions.ProductNotAvailableException;
import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.ports.in.PublicAccessPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.auth.LoginService;
import application.domain.services.auth.LogoutService;
import application.domain.services.auth.RegisterBuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para operaciones públicas y autenticación.
 */
@Service
@RequiredArgsConstructor
public class PublicAccessUseCaseImpl implements PublicAccessPort {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RegisterBuyerService registerBuyerService;
    private final ProductRepositoryPort productRepositoryPort;

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
        return productRepositoryPort.findPublished();
    }

    @Override
    public Product getPublicProductDetails(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El identificador del producto no puede ser nulo o vacío.");
        }
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new ProductNotAvailableException("El producto solicitado no existe: " + productId));

        if (!product.isPublished()) {
            throw new ProductNotAvailableException("El producto solicitado no está disponible para consulta pública.");
        }
        return product;
    }
}
