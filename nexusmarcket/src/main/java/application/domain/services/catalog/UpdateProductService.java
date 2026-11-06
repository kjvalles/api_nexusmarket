package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Servicio de dominio para actualización de atributos y precios del producto en el catálogo.
 */
@RequiredArgsConstructor
public class UpdateProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public Product updateProduct(Product product) {
        if (product == null || product.getId() == null || product.getId().isBlank()) {
            throw new IllegalArgumentException("El producto y su ID son obligatorios para actualizar.");
        }

        if (!productRepositoryPort.existsById(product.getId())) {
            throw new EntityNotFoundException("Producto", product.getId());
        }

        product.setUpdatedAt(LocalDateTime.now());
        return productRepositoryPort.save(product);
    }

    public Product updateProduct(String vendorId, Product product) {
        if (product == null || product.getId() == null || product.getId().isBlank()) {
            throw new IllegalArgumentException("El producto y su ID son obligatorios para actualizar.");
        }

        Product existing = productRepositoryPort.findById(product.getId())
                .orElseThrow(() -> new EntityNotFoundException("Producto", product.getId()));

        if (vendorId != null && !vendorId.equals(existing.getVendorId())) {
            throw new application.domain.exceptions.UnauthorizedRoleOperationException("No tiene autorización para modificar productos de otro vendedor.");
        }

        product.setVendorId(vendorId);
        product.setUpdatedAt(LocalDateTime.now());
        return productRepositoryPort.save(product);
    }
}
