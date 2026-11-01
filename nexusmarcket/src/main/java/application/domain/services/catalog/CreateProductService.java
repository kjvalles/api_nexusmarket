package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para registrar un nuevo producto en el catálogo.
 */
@RequiredArgsConstructor
public class CreateProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (product.getVendorId() == null || product.getVendorId().isBlank()) {
            throw new IllegalArgumentException("El ID del vendedor es obligatorio.");
        }
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        return productRepositoryPort.save(product);
    }
}
