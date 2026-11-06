package application.domain.services.cart;

import application.domain.models.ShoppingCart;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para consulta del carrito de compras.
 */
@RequiredArgsConstructor
public class ConsultCartService {

    private final ShoppingCartRepositoryPort shoppingCartRepositoryPort;

    public ShoppingCart getCart(String buyerId) {
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("El identificador del comprador no puede ser nulo o vacío.");
        }
        return shoppingCartRepositoryPort.findByBuyerId(buyerId)
                .orElseGet(() -> new ShoppingCart(buyerId));
    }
}
