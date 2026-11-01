package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.domain.enums.UserRole;
import application.domain.models.Buyer;
import application.domain.models.DefaultUser;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.UserStatus;

public final class UserJpaMapper {

    private UserJpaMapper() {}

    public static UserJpaEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        if (domain instanceof Buyer buyer) {
            return BuyerJpaMapper.toEntity(buyer);
        }
        if (domain instanceof Seller seller) {
            return SellerJpaMapper.toEntity(seller);
        }
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setIdentityDocument(domain.getIdentityDocument());
        entity.setFullName(domain.getFullName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole() != null ? domain.getRole().name() : null);
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : UserStatus.ACTIVE.getCode());
        entity.setAuthTokenVersion(domain.getAuthTokenVersion() != null ? domain.getAuthTokenVersion() : 1);
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof BuyerJpaEntity buyerEntity) {
            return BuyerJpaMapper.toDomain(buyerEntity);
        }
        if (entity instanceof SellerJpaEntity sellerEntity) {
            return SellerJpaMapper.toDomain(sellerEntity);
        }
        if (UserRole.BUYER.name().equalsIgnoreCase(entity.getRole())) {
            Buyer buyer = new Buyer();
            populateCommonFields(entity, buyer);
            return buyer;
        }
        if (UserRole.SELLER.name().equalsIgnoreCase(entity.getRole())) {
            Seller seller = new Seller();
            populateCommonFields(entity, seller);
            return seller;
        }
        DefaultUser user = new DefaultUser();
        populateCommonFields(entity, user);
        return user;
    }

    private static void populateCommonFields(UserJpaEntity entity, User domain) {
        domain.setId(entity.getId());
        domain.setIdentityDocument(entity.getIdentityDocument());
        domain.setFullName(entity.getFullName());
        domain.setEmail(entity.getEmail());
        domain.setPassword(entity.getPassword());
        if (entity.getRole() != null) {
            try {
                domain.setRole(UserRole.valueOf(entity.getRole()));
            } catch (Exception ignored) {
            }
        }
        domain.setStatus(BuyerJpaMapper.userStatus(entity.getStatus()));
        domain.setAuthTokenVersion(entity.getAuthTokenVersion());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
    }
}
