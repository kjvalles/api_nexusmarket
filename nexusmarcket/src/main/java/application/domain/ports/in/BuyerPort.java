package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.ShoppingCart;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.CartItem;

import java.util.List;

/**
 * Puerto de entrada para casos de uso del rol Comprador (BUYER).
 */
public interface BuyerPort {

    ShoppingCart getCart(String buyerId);

    ShoppingCart addItemToCart(String buyerId, CartItem item);

    ShoppingCart removeItemFromCart(String buyerId, String productId, String variantId);

    void clearCart(String buyerId);

    Order checkoutCart(String buyerId, Address deliveryAddress, String additionalNotes);

    List<Order> getMyOrders(String buyerId);

    Order getOrderDetails(String buyerId, Long orderId);

    void cancelOrder(String buyerId, Long orderId, String reason);
}
