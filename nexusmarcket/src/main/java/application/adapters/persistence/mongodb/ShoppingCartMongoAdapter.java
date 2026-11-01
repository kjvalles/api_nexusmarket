package application.adapters.persistence.mongodb;

import application.adapters.persistence.mongodb.documents.ShoppingCartDocument;
import application.adapters.persistence.mongodb.mappers.ShoppingCartMongoMapper;
import application.adapters.persistence.mongodb.repositories.SpringDataMongoShoppingCartRepository;
import application.domain.models.ShoppingCart;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adaptador de salida para persistencia de Carritos de Compras (MongoDB).
 */
@Repository
@RequiredArgsConstructor
public class ShoppingCartMongoAdapter implements ShoppingCartRepositoryPort {

    private final SpringDataMongoShoppingCartRepository cartRepository;

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        if (cart == null) {
            return null;
        }
        ShoppingCartDocument doc = ShoppingCartMongoMapper.toDocument(cart);
        ShoppingCartDocument saved = cartRepository.save(doc);
        return ShoppingCartMongoMapper.toDomain(saved);
    }

    @Override
    public Optional<ShoppingCart> findByBuyerId(String buyerId) {
        if (buyerId == null || buyerId.isBlank()) {
            return Optional.empty();
        }
        return cartRepository.findByBuyerId(buyerId).map(ShoppingCartMongoMapper::toDomain);
    }

    @Override
    public void deleteByBuyerId(String buyerId) {
        if (buyerId != null && !buyerId.isBlank()) {
            cartRepository.deleteByBuyerId(buyerId);
        }
    }
}
