package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.domain.enums.UserRole;
import application.domain.models.Buyer;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerCommercialStatus;
import application.domain.valueobjects.UserStatus;

import java.math.BigDecimal;
import java.util.ArrayList;

public final class BuyerJpaMapper {

    private BuyerJpaMapper() {}

    public static BuyerJpaEntity toEntity(Buyer domain) {
        if (domain == null) {
            return null;
        }
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setId(domain.getId());
        entity.setIdentityDocument(domain.getIdentityDocument());
        entity.setFullName(domain.getFullName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole() != null ? domain.getRole().name() : UserRole.BUYER.name());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : UserStatus.ACTIVE.getCode());
        entity.setAuthTokenVersion(domain.getAuthTokenVersion() != null ? domain.getAuthTokenVersion() : 1);
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        entity.setCommercialStatus(domain.getCommercialStatus() != null ? domain.getCommercialStatus().getCode() : null);
        entity.setVirtualBalance(domain.getVirtualBalance() != null ? domain.getVirtualBalance() : BigDecimal.ZERO);
        entity.setCompletedOrdersCount(domain.getCompletedOrdersCount());
        entity.setRating(domain.getRating());

        if (domain.getPrimaryAddress() != null) {
            entity.setPrimaryStreet(domain.getPrimaryAddress().getStreet());
            entity.setPrimaryCity(domain.getPrimaryAddress().getCity());
            entity.setPrimaryState(domain.getPrimaryAddress().getState());
            entity.setPrimaryPostalCode(domain.getPrimaryAddress().getPostalCode());
            entity.setPrimaryCountry(domain.getPrimaryAddress().getCountry());
        }
        return entity;
    }

    public static Buyer toDomain(BuyerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Buyer domain = new Buyer();
        domain.setId(entity.getId());
        domain.setIdentityDocument(entity.getIdentityDocument());
        domain.setFullName(entity.getFullName());
        domain.setEmail(entity.getEmail());
        domain.setPassword(entity.getPassword());
        if (entity.getRole() != null) {
            try {
                domain.setRole(UserRole.valueOf(entity.getRole()));
            } catch (Exception ignored) {
                domain.setRole(UserRole.BUYER);
            }
        }
        domain.setStatus(userStatus(entity.getStatus()));
        domain.setAuthTokenVersion(entity.getAuthTokenVersion());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        domain.setCommercialStatus(buyerCommercialStatus(entity.getCommercialStatus()));
        domain.setVirtualBalance(entity.getVirtualBalance() != null ? entity.getVirtualBalance() : BigDecimal.ZERO);
        domain.setCompletedOrdersCount(entity.getCompletedOrdersCount());
        domain.setRating(entity.getRating());

        if (entity.getPrimaryStreet() != null || entity.getPrimaryCity() != null) {
            domain.setPrimaryAddress(new Address(
                    entity.getPrimaryStreet(),
                    entity.getPrimaryCity(),
                    entity.getPrimaryState(),
                    entity.getPrimaryPostalCode(),
                    entity.getPrimaryCountry(),
                    null
            ));
        }
        domain.setAdditionalAddresses(new ArrayList<>());
        return domain;
    }

    public static UserStatus userStatus(String code) {
        if (code == null) return null;
        if (UserStatus.ACTIVE.getCode().equalsIgnoreCase(code)) return UserStatus.ACTIVE;
        if (UserStatus.INACTIVE.getCode().equalsIgnoreCase(code)) return UserStatus.INACTIVE;
        if (UserStatus.BLOCKED.getCode().equalsIgnoreCase(code)) return UserStatus.BLOCKED;
        return null;
    }

    public static BuyerCommercialStatus buyerCommercialStatus(String code) {
        if (code == null) return null;
        if (BuyerCommercialStatus.ENABLED.getCode().equalsIgnoreCase(code)) return BuyerCommercialStatus.ENABLED;
        if (BuyerCommercialStatus.SUSPENDED.getCode().equalsIgnoreCase(code)) return BuyerCommercialStatus.SUSPENDED;
        return null;
    }
}
