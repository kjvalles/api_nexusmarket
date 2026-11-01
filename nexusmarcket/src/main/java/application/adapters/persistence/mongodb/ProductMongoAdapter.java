package application.adapters.persistence.mongodb;

import application.adapters.persistence.mongodb.documents.ProductDocument;
import application.adapters.persistence.mongodb.mappers.ProductMongoMapper;
import application.adapters.persistence.mongodb.repositories.SpringDataMongoProductRepository;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida para persistencia de Catálogo de Productos (MongoDB).
 */
@Repository
@RequiredArgsConstructor
public class ProductMongoAdapter implements ProductRepositoryPort {

    private final SpringDataMongoProductRepository productRepository;

    @Override
    public Product save(Product product) {
        if (product == null) {
            return null;
        }
        ProductDocument doc = ProductMongoMapper.toDocument(product);
        ProductDocument saved = productRepository.save(doc);
        return ProductMongoMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return productRepository.findById(id).map(ProductMongoMapper::toDomain);
    }

    @Override
    public List<Product> findByVendorId(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            return List.of();
        }
        return productRepository.findByVendorId(vendorId).stream()
                .map(ProductMongoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findPublished() {
        return productRepository.findByStatus(ProductStatus.PUBLISHED.getCode()).stream()
                .map(ProductMongoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findPublishedByCategory(String category) {
        if (category == null || category.isBlank()) {
            return findPublished();
        }
        return productRepository.findByStatusAndCategory(ProductStatus.PUBLISHED.getCode(), category).stream()
                .map(ProductMongoMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(String id) {
        return id != null && !id.isBlank() && productRepository.existsById(id);
    }
}
