package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.domain.models.InventoryMovement;
import application.domain.valueobjects.InventoryMovementType;

public final class InventoryMovementJpaMapper {

    private InventoryMovementJpaMapper() {}

    public static InventoryMovementJpaEntity toEntity(InventoryMovement domain) {
        if (domain == null) {
            return null;
        }
        InventoryMovementJpaEntity entity = new InventoryMovementJpaEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setWarehouseId(domain.getWarehouseId());
        entity.setVariantId(domain.getVariantId());
        entity.setQuantity(domain.getQuantity());
        entity.setMovementType(domain.getMovementType() != null ? domain.getMovementType().getCode() : "MANUAL_ENTRY");
        entity.setReferenceOrderId(domain.getReferenceOrderId());
        entity.setRegisteredByUserId(domain.getRegisteredByUserId());
        entity.setNotes(domain.getNotes());
        entity.setMovementDate(domain.getMovementDate());
        return entity;
    }

    public static InventoryMovement toDomain(InventoryMovementJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        InventoryMovement domain = new InventoryMovement();
        domain.setId(entity.getId());
        domain.setProductId(entity.getProductId());
        domain.setWarehouseId(entity.getWarehouseId());
        domain.setVariantId(entity.getVariantId());
        domain.setQuantity(entity.getQuantity());
        domain.setMovementType(inventoryMovementType(entity.getMovementType()));
        domain.setReferenceOrderId(entity.getReferenceOrderId());
        domain.setRegisteredByUserId(entity.getRegisteredByUserId());
        domain.setNotes(entity.getNotes());
        domain.setMovementDate(entity.getMovementDate());
        return domain;
    }

    public static InventoryMovementType inventoryMovementType(String code) {
        if (code == null) return null;
        if (InventoryMovementType.MANUAL_ENTRY.getCode().equalsIgnoreCase(code)) return InventoryMovementType.MANUAL_ENTRY;
        if (InventoryMovementType.PURCHASE_RESERVATION.getCode().equalsIgnoreCase(code)) return InventoryMovementType.PURCHASE_RESERVATION;
        if (InventoryMovementType.SALE_EXIT.getCode().equalsIgnoreCase(code)) return InventoryMovementType.SALE_EXIT;
        if (InventoryMovementType.LOSS_ADJUSTMENT.getCode().equalsIgnoreCase(code)) return InventoryMovementType.LOSS_ADJUSTMENT;
        if (InventoryMovementType.RESERVATION_RELEASE.getCode().equalsIgnoreCase(code)) return InventoryMovementType.RESERVATION_RELEASE;
        if (InventoryMovementType.RETURN_ENTRY.getCode().equalsIgnoreCase(code)) return InventoryMovementType.RETURN_ENTRY;
        return null;
    }
}
