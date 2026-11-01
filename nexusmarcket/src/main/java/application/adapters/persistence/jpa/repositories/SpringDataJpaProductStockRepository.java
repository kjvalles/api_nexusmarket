package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ProductStockJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataJpaProductStockRepository extends JpaRepository<ProductStockJpaEntity, Long> {

    Optional<ProductStockJpaEntity> findByProductIdAndWarehouseIdAndVariantId(String productId, String warehouseId, String variantId);

    Optional<ProductStockJpaEntity> findByProductIdAndWarehouseIdAndVariantIdIsNull(String productId, String warehouseId);

    List<ProductStockJpaEntity> findByProductId(String productId);

    List<ProductStockJpaEntity> findByWarehouseId(String warehouseId);
}
