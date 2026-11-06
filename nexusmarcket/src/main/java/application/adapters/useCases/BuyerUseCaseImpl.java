package application.adapters.useCases;

import application.domain.exceptions.DomainException;
import application.domain.models.Order;
import application.domain.models.ShoppingCart;
import application.domain.ports.in.BuyerPort;
import application.domain.services.cart.ConsultCartService;
import application.domain.services.cart.ManageCartService;
import application.domain.services.order.CancelOrderService;
import application.domain.services.order.ConsultOrderService;
import application.domain.services.order.CreateOrderService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.CartItem;
import application.domain.valueobjects.OrderLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso para operaciones del rol Comprador (BUYER).
 * Orquesta exclusivamente servicios de dominio sin acceso directo a puertos de persistencia.
 */
@Service
@RequiredArgsConstructor
public class BuyerUseCaseImpl implements BuyerPort {

    private final ConsultCartService consultCartService;
    private final ManageCartService manageCartService;
    private final CreateOrderService createOrderService;
    private final CancelOrderService cancelOrderService;
    private final ConsultOrderService consultOrderService;

    @Override
    public ShoppingCart getCart(String buyerId) {
        return consultCartService.getCart(buyerId);
    }

    @Override
    public ShoppingCart addItemToCart(String buyerId, CartItem item) {
        return manageCartService.addItemToCart(buyerId, item);
    }

    @Override
    public ShoppingCart removeItemFromCart(String buyerId, String productId, String variantId) {
        return manageCartService.removeItemFromCart(buyerId, productId, variantId);
    }

    @Override
    public void clearCart(String buyerId) {
        manageCartService.clearCart(buyerId);
    }

    @Override
    public Order checkoutCart(String buyerId, Address deliveryAddress, String additionalNotes) {
        ShoppingCart cart = consultCartService.getCart(buyerId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new DomainException("El carrito de compras está vacío; no es posible procesar la orden.");
        }

        List<OrderLine> lines = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            lines.add(new OrderLine(
                    item.getProductId(),
                    item.getVariantId(),
                    item.getProductName(),
                    item.getUnitPrice(),
                    item.getQuantity()
            ));
        }

        Order createdOrder = createOrderService.createOrder(buyerId, lines, additionalNotes);
        manageCartService.clearCart(buyerId);
        return createdOrder;
    }

    @Override
    public List<Order> getMyOrders(String buyerId) {
        return consultOrderService.getOrdersByBuyer(buyerId);
    }

    @Override
    public Order getOrderDetails(String buyerId, Long orderId) {
        return consultOrderService.getOrderDetails(buyerId, orderId);
    }

    @Override
    public void cancelOrder(String buyerId, Long orderId, String reason) {
        consultOrderService.getOrderDetails(buyerId, orderId);
        cancelOrderService.cancelOrder(orderId, reason);
    }
}
