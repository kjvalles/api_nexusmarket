package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Product;

import java.util.List;

/**
 * Puerto de entrada para operaciones públicas y autenticación.
 */
public interface PublicAccessPort {

    String login(String emailOrDocument, String rawPassword);

    void logout(String userId);

    Buyer registerBuyer(Buyer buyer, String rawPassword);

    List<Product> getPublicCatalog();

    Product getPublicProductDetails(String productId);
}
