package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ProductStockJpaEntity;
import application.adapters.persistence.jpa.mappers.ProductStockJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaProductStockRepository;
import application.domain.models.ProductStock;
import application.domain.ports.out.ProductStockRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida para persistencia de saldos de existencias (MySQL vía Spring Data JPA).
 */
@Repository
@RequiredArgsConstructor
public class ProductStockJpaAdapter implements ProductStockRepositoryPort {

    private final SpringDataJpaProductStockRepository stockRepository;

    @Override
    @Transactional
    public ProductStock save(ProductStock stock) {
        if (stock == null) {
            return null;
        }
        ProductStockJpaEntity entity = ProductStockJpaMapper.toEntity(stock);
        ProductStockJpaEntity saved = stockRepository.save(entity);
        return ProductStockJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductStock> findByProductVariantAndWarehouse(String productId, String variantId, String warehouseId) {
        if (productId == null || warehouseId == null) {
            return Optional.empty();
        }
        if (variantId != null && !variantId.isBlank()) {
            return stockRepository.findByProductIdAndWarehouseIdAndVariantId(productId, warehouseId, variantId)
                    .map(ProductStockJpaMapper::toDomain);
        } else {
            return stockRepository.findByProductIdAndWarehouseIdAndVariantIdIsNull(productId, warehouseId)
                    .map(ProductStockJpaMapper::toDomain);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStock> findByProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            return List.of();
        }
        return stockRepository.findByProductId(productId).stream()
                .map(ProductStockJpaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStock> findByWarehouseId(String warehouseId) {
        if (warehouseId == null || warehouseId.isBlank()) {
            return List.of();
        }
        return stockRepository.findByWarehouseId(warehouseId).stream()
                .map(ProductStockJpaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductStock> findAll() {
        return stockRepository.findAll().stream()
                .map(ProductStockJpaMapper::toDomain)
                .toList();
    }
}
