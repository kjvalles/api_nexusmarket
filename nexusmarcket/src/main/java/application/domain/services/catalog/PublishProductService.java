package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para publicar un producto en el catálogo público.
 */
@RequiredArgsConstructor
public class PublishProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public Product publishProduct(String productId) {
        return publishProduct(null, productId);
    }

    public Product publishProduct(String vendorId, String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }

        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto", productId));

        if (vendorId != null && !vendorId.equals(product.getVendorId())) {
            throw new application.domain.exceptions.UnauthorizedRoleOperationException("No tiene autorización para modificar productos de otro vendedor.");
        }

        product.publish();
        return productRepositoryPort.save(product);
    }
}
