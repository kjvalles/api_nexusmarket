package application.domain.services.cart;

import application.domain.models.ShoppingCart;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import application.domain.valueobjects.CartItem;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para manipulación de ítems en el carrito de compras.
 */
@RequiredArgsConstructor
public class ManageCartService {

    private final ShoppingCartRepositoryPort shoppingCartRepositoryPort;
    private final ConsultCartService consultCartService;

    public ShoppingCart addItemToCart(String buyerId, CartItem item) {
        ShoppingCart cart = consultCartService.getCart(buyerId);
        cart.addItem(item);
        return shoppingCartRepositoryPort.save(cart);
    }

    public ShoppingCart removeItemFromCart(String buyerId, String productId, String variantId) {
        ShoppingCart cart = consultCartService.getCart(buyerId);
        cart.removeItem(productId, variantId);
        return shoppingCartRepositoryPort.save(cart);
    }

    public void clearCart(String buyerId) {
        shoppingCartRepositoryPort.deleteByBuyerId(buyerId);
    }
}
