package application.domain.ports.out;

import application.domain.models.ShoppingCart;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de carritos de compras (MongoDB).
 */
public interface ShoppingCartRepositoryPort {

    ShoppingCart save(ShoppingCart cart);

    Optional<ShoppingCart> findByBuyerId(String buyerId);

    void deleteByBuyerId(String buyerId);
}
