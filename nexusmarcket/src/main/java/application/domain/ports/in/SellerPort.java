package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.ProductStock;

import java.util.List;

/**
 * Puerto de entrada para casos de uso del rol Vendedor (SELLER).
 */
public interface SellerPort {

    Product createProduct(String vendorId, Product product);

    Product updateProduct(String vendorId, Product product);

    void publishProduct(String vendorId, String productId);

    void suspendProduct(String vendorId, String productId);

    List<Product> getMyProducts(String vendorId);

    List<ProductStock> getMyInventory(String vendorId);
}
