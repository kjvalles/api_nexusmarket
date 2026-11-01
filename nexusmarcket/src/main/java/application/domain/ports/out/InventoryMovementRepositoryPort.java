package application.domain.ports.out;

import application.domain.models.InventoryMovement;

import java.util.List;

/**
 * Puerto de salida para persistencia de movimientos históricos de inventario (MySQL).
 */
public interface InventoryMovementRepositoryPort {

    InventoryMovement save(InventoryMovement movement);

    List<InventoryMovement> findByProductId(String productId);

    List<InventoryMovement> findByWarehouseId(String warehouseId);

    List<InventoryMovement> findByReferenceOrderId(Long orderId);
}
