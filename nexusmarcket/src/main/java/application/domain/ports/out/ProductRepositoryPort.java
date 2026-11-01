package application.domain.ports.out;

import application.domain.models.Product;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de Catálogo de Productos (MongoDB).
 */
public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(String id);

    List<Product> findByVendorId(String vendorId);

    List<Product> findPublished();

    List<Product> findPublishedByCategory(String category);

    boolean existsById(String id);
}
