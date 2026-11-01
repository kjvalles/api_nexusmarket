package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ProductStockJpaEntity;
import application.domain.models.ProductStock;

public final class ProductStockJpaMapper {

    private ProductStockJpaMapper() {}

    public static ProductStockJpaEntity toEntity(ProductStock domain) {
        if (domain == null) {
            return null;
        }
        ProductStockJpaEntity entity = new ProductStockJpaEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setVariantId(domain.getVariantId());
        entity.setAvailableQuantity(domain.getAvailableQuantity());
        entity.setReservedQuantity(domain.getReservedQuantity());
        entity.setLastUpdatedAt(domain.getLastUpdatedAt());
        return entity;
    }

    public static ProductStock toDomain(ProductStockJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        ProductStock domain = new ProductStock();
        domain.setId(entity.getId());
        domain.setProductId(entity.getProductId());
        domain.setWarehouseId(entity.getWarehouseId());
        domain.setVariantId(entity.getVariantId());
        domain.setAvailableQuantity(entity.getAvailableQuantity());
        domain.setReservedQuantity(entity.getReservedQuantity());
        domain.setLastUpdatedAt(entity.getLastUpdatedAt());
        return domain;
    }
}
