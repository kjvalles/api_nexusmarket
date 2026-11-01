package application.domain.services.inventory;

import application.domain.models.InventoryMovement;
import application.domain.models.ProductStock;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.ProductStockRepositoryPort;
import application.domain.valueobjects.InventoryMovementType;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para registro atómico de movimientos físicos y contables de inventario.
 */
@RequiredArgsConstructor
public class RegisterInventoryMovementService {

    private final ProductStockRepositoryPort productStockRepositoryPort;
    private final InventoryMovementRepositoryPort inventoryMovementRepositoryPort;

    public ProductStock registerMovement(String productId, String variantId, String warehouseId,
                                         InventoryMovementType movementType, int quantity,
                                         Long referenceOrderId, String registeredByUserId, String notes) {
        if (movementType == null) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser estrictamente positiva.");
        }

        ProductStock stock = productStockRepositoryPort
                .findByProductVariantAndWarehouse(productId, variantId, warehouseId)
                .orElseGet(() -> new ProductStock(productId, variantId, warehouseId, 0));

        if (InventoryMovementType.MANUAL_ENTRY.equals(movementType) || InventoryMovementType.RETURN_ENTRY.equals(movementType)) {
            stock.addStock(quantity);
        } else if (InventoryMovementType.PURCHASE_RESERVATION.equals(movementType)) {
            stock.reserve(quantity);
        } else if (InventoryMovementType.RESERVATION_RELEASE.equals(movementType)) {
            stock.releaseReservation(quantity);
        } else if (InventoryMovementType.SALE_EXIT.equals(movementType)) {
            stock.confirmSale(quantity);
        } else if (InventoryMovementType.LOSS_ADJUSTMENT.equals(movementType)) {
            stock.adjustStock(-quantity);
        }

        ProductStock savedStock = productStockRepositoryPort.save(stock);

        InventoryMovement movement = new InventoryMovement(
                productId, variantId, warehouseId, quantity, movementType, referenceOrderId, registeredByUserId, notes);
        inventoryMovementRepositoryPort.save(movement);

        return savedStock;
    }
}
