package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJpaInventoryMovementRepository extends JpaRepository<InventoryMovementJpaEntity, Long> {

    List<InventoryMovementJpaEntity> findByProductId(String productId);

    List<InventoryMovementJpaEntity> findByWarehouseId(String warehouseId);

    List<InventoryMovementJpaEntity> findByReferenceOrderId(Long referenceOrderId);
}
