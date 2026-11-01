package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;

public final class WarehouseJpaMapper {

    private WarehouseJpaMapper() {}

    public static WarehouseJpaEntity toEntity(Warehouse domain) {
        if (domain == null) {
            return null;
        }
        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setVendorId(domain.getVendorId());
        entity.setType(domain.getType() != null ? domain.getType().getCode() : null);
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : WarehouseStatus.ACTIVE.getCode());
        entity.setDescription(domain.getDescription());
        entity.setMaxCapacity(domain.getMaxCapacity());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getAddress() != null) {
            entity.setStreet(domain.getAddress().getStreet());
            entity.setCity(domain.getAddress().getCity());
            entity.setState(domain.getAddress().getState());
            entity.setPostalCode(domain.getAddress().getPostalCode());
            entity.setCountry(domain.getAddress().getCountry());
        }
        return entity;
    }

    public static Warehouse toDomain(WarehouseJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Warehouse domain = new Warehouse();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setVendorId(entity.getVendorId());
        domain.setType(warehouseType(entity.getType()));
        domain.setStatus(warehouseStatus(entity.getStatus()));
        domain.setDescription(entity.getDescription());
        domain.setMaxCapacity(entity.getMaxCapacity());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getStreet() != null || entity.getCity() != null) {
            domain.setAddress(new Address(
                    entity.getStreet(),
                    entity.getCity(),
                    entity.getState(),
                    entity.getPostalCode(),
                    entity.getCountry(),
                    null
            ));
        }
        return domain;
    }

    public static WarehouseType warehouseType(String code) {
        if (code == null) return null;
        if (WarehouseType.MARKETPLACE.getCode().equalsIgnoreCase(code)) return WarehouseType.MARKETPLACE;
        if (WarehouseType.SELLER.getCode().equalsIgnoreCase(code)) return WarehouseType.SELLER;
        return null;
    }

    public static WarehouseStatus warehouseStatus(String code) {
        if (code == null) return null;
        if (WarehouseStatus.ACTIVE.getCode().equalsIgnoreCase(code)) return WarehouseStatus.ACTIVE;
        if (WarehouseStatus.CLOSED.getCode().equalsIgnoreCase(code)) return WarehouseStatus.CLOSED;
        return null;
    }
}
