package application.domain.services.catalog;

import application.domain.exceptions.ProductNotAvailableException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Servicio de dominio para consultas del catálogo de productos.
 */
@RequiredArgsConstructor
public class ConsultCatalogService {

    private final ProductRepositoryPort productRepositoryPort;

    public List<Product> getPublicCatalog() {
        return productRepositoryPort.findPublished();
    }

    public Product getPublicProductDetails(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El identificador del producto no puede ser nulo o vacío.");
        }
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new ProductNotAvailableException("El producto solicitado no existe: " + productId));

        if (!product.isPublished()) {
            throw new ProductNotAvailableException("El producto solicitado no está disponible para compra pública.");
        }
        return product;
    }

    public List<Product> getProductsByVendor(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            throw new IllegalArgumentException("El identificador del vendedor no puede ser nulo o vacío.");
        }
        return productRepositoryPort.findByVendorId(vendorId);
    }
}
