package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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
}
