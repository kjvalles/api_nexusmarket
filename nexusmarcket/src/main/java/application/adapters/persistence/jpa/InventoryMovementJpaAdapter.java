package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.adapters.persistence.jpa.mappers.InventoryMovementJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInventoryMovementRepository;
import application.domain.models.InventoryMovement;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Adaptador de salida para persistencia del kardex de inventario (MySQL vía Spring Data JPA).
 */
@Repository
@RequiredArgsConstructor
public class InventoryMovementJpaAdapter implements InventoryMovementRepositoryPort {

    private final SpringDataJpaInventoryMovementRepository movementRepository;

    @Override
    @Transactional
    public InventoryMovement save(InventoryMovement movement) {
        if (movement == null) {
            return null;
        }
        InventoryMovementJpaEntity entity = InventoryMovementJpaMapper.toEntity(movement);
        InventoryMovementJpaEntity saved = movementRepository.save(entity);
        return InventoryMovementJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovement> findByProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            return List.of();
        }
        return movementRepository.findByProductId(productId).stream()
                .map(InventoryMovementJpaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovement> findByWarehouseId(String warehouseId) {
        if (warehouseId == null || warehouseId.isBlank()) {
            return List.of();
        }
        return movementRepository.findByWarehouseId(warehouseId).stream()
                .map(InventoryMovementJpaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovement> findByReferenceOrderId(Long orderId) {
        if (orderId == null) {
            return List.of();
        }
        return movementRepository.findByReferenceOrderId(orderId).stream()
                .map(InventoryMovementJpaMapper::toDomain)
                .toList();
    }
}
