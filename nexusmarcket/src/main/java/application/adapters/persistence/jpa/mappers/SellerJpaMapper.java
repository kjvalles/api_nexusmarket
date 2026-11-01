package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import application.domain.enums.UserRole;
import application.domain.models.Seller;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SellerJpaMapper {

    private SellerJpaMapper() {}

    public static SellerJpaEntity toEntity(Seller domain) {
        if (domain == null) {
            return null;
        }
        SellerJpaEntity entity = new SellerJpaEntity();
        entity.setId(domain.getId());
        entity.setIdentityDocument(domain.getIdentityDocument());
        entity.setFullName(domain.getFullName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole() != null ? domain.getRole().name() : UserRole.SELLER.name());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : UserStatus.ACTIVE.getCode());
        entity.setAuthTokenVersion(domain.getAuthTokenVersion() != null ? domain.getAuthTokenVersion() : 1);
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        entity.setBusinessName(domain.getBusinessName());
        entity.setTaxId(domain.getTaxId());
        entity.setSellerStatus(domain.getSellerStatus() != null ? domain.getSellerStatus().getCode() : null);
        entity.setBusinessDescription(domain.getBusinessDescription());
        entity.setRating(domain.getRating());
        entity.setIncorporationDate(domain.getIncorporationDate());
        entity.setActiveProductsCount(domain.getActiveProductsCount());

        if (domain.getWarehouseIds() != null && !domain.getWarehouseIds().isEmpty()) {
            entity.setWarehouseIds(String.join(",", domain.getWarehouseIds()));
        }
        return entity;
    }

    public static Seller toDomain(SellerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Seller domain = new Seller();
        domain.setId(entity.getId());
        domain.setIdentityDocument(entity.getIdentityDocument());
        domain.setFullName(entity.getFullName());
        domain.setEmail(entity.getEmail());
        domain.setPassword(entity.getPassword());
        if (entity.getRole() != null) {
            try {
                domain.setRole(UserRole.valueOf(entity.getRole()));
            } catch (Exception ignored) {
                domain.setRole(UserRole.SELLER);
            }
        }
        domain.setStatus(BuyerJpaMapper.userStatus(entity.getStatus()));
        domain.setAuthTokenVersion(entity.getAuthTokenVersion());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        domain.setBusinessName(entity.getBusinessName());
        domain.setTaxId(entity.getTaxId());
        domain.setSellerStatus(sellerStatus(entity.getSellerStatus()));
        domain.setBusinessDescription(entity.getBusinessDescription());
        domain.setRating(entity.getRating());
        domain.setIncorporationDate(entity.getIncorporationDate());
        domain.setActiveProductsCount(entity.getActiveProductsCount());

        List<String> warehouses = new ArrayList<>();
        if (entity.getWarehouseIds() != null && !entity.getWarehouseIds().isBlank()) {
            warehouses.addAll(Arrays.asList(entity.getWarehouseIds().split(",")));
        }
        domain.setWarehouseIds(warehouses);
        return domain;
    }

    public static SellerStatus sellerStatus(String code) {
        if (code == null) return null;
        if (SellerStatus.ACTIVE.getCode().equalsIgnoreCase(code)) return SellerStatus.ACTIVE;
        if (SellerStatus.UNDER_REVIEW.getCode().equalsIgnoreCase(code)) return SellerStatus.UNDER_REVIEW;
        if (SellerStatus.SUSPENDED.getCode().equalsIgnoreCase(code)) return SellerStatus.SUSPENDED;
        return null;
    }
}
