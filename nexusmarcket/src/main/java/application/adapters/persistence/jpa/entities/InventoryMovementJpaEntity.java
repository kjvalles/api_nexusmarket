package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA para persistencia del kardex de movimientos de inventario (tabla inventory_movements).
 */
@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    @Column(name = "variant_id", length = 50)
    private String variantId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "movement_type", length = 40, nullable = false)
    private String movementType;

    @Column(name = "reference_order_id")
    private Long referenceOrderId;

    @Column(name = "registered_by_user_id", length = 36)
    private String registeredByUserId;

    @Column(name = "notes", length = 255)
    private String notes;

    @Column(name = "movement_date")
    private LocalDateTime movementDate;
}
