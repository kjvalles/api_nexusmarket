package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedRoleOperationException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para suspender un producto publicado.
 */
@RequiredArgsConstructor
public class SuspendProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public void suspendProduct(String vendorId, String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El identificador del producto es obligatorio.");
        }
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto", productId));

        if (!vendorId.equals(product.getVendorId())) {
            throw new UnauthorizedRoleOperationException("No tiene autorización para modificar productos de otro vendedor.");
        }

        product.suspend();
        productRepositoryPort.save(product);
    }
}
