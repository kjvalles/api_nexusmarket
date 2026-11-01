package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad de dominio inmutable que registra cada transacción histórica en el kardex de inventario.
 */
@Getter
@Setter
public class InventoryMovement {

    private Long id;
    private String productId;
    private String warehouseId;
    private String variantId;
    private int quantity;
    private InventoryMovementType movementType;
    private Long referenceOrderId;
    private LocalDateTime movementDate;
    private String notes;
    private String registeredByUserId;

    public InventoryMovement() {
        this.movementDate = LocalDateTime.now();
    }

    public InventoryMovement(String productId, String variantId, String warehouseId, int quantity,
                             InventoryMovementType movementType, Long referenceOrderId,
                             String registeredByUserId, String notes) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El productId del movimiento no puede ser nulo o vacío.");
        }
        if (warehouseId == null || warehouseId.isBlank()) {
            throw new IllegalArgumentException("El warehouseId del movimiento no puede ser nulo o vacío.");
        }
        if (movementType == null) {
            throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo.");
        }
        this.productId = productId;
        this.variantId = variantId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.movementType = movementType;
        this.referenceOrderId = referenceOrderId;
        this.registeredByUserId = registeredByUserId;
        this.notes = notes;
        this.movementDate = LocalDateTime.now();
    }
}
