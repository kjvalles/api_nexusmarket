package application.adapters.useCases;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedRoleOperationException;
import application.domain.models.Order;
import application.domain.models.ShoppingCart;
import application.domain.ports.in.BuyerPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import application.domain.services.order.CancelOrderService;
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
 */
@Service
@RequiredArgsConstructor
public class BuyerUseCaseImpl implements BuyerPort {

    private final ShoppingCartRepositoryPort shoppingCartRepositoryPort;
    private final CreateOrderService createOrderService;
    private final CancelOrderService cancelOrderService;
    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    public ShoppingCart getCart(String buyerId) {
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("El identificador del comprador no puede ser nulo o vacío.");
        }
        return shoppingCartRepositoryPort.findByBuyerId(buyerId)
                .orElseGet(() -> new ShoppingCart(buyerId));
    }

    @Override
    public ShoppingCart addItemToCart(String buyerId, CartItem item) {
        ShoppingCart cart = getCart(buyerId);
        cart.addItem(item);
        return shoppingCartRepositoryPort.save(cart);
    }

    @Override
    public ShoppingCart removeItemFromCart(String buyerId, String productId, String variantId) {
        ShoppingCart cart = getCart(buyerId);
        cart.removeItem(productId, variantId);
        return shoppingCartRepositoryPort.save(cart);
    }

    @Override
    public void clearCart(String buyerId) {
        shoppingCartRepositoryPort.deleteByBuyerId(buyerId);
    }

    @Override
    public Order checkoutCart(String buyerId, Address deliveryAddress, String additionalNotes) {
        ShoppingCart cart = getCart(buyerId);
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
        shoppingCartRepositoryPort.deleteByBuyerId(buyerId);
        return createdOrder;
    }

    @Override
    public List<Order> getMyOrders(String buyerId) {
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("El identificador del comprador no puede ser nulo o vacío.");
        }
        return orderRepositoryPort.findByBuyerId(buyerId);
    }

    @Override
    public Order getOrderDetails(String buyerId, Long orderId) {
        Order order = findAndValidateOwnership(buyerId, orderId);
        return order;
    }

    @Override
    public void cancelOrder(String buyerId, Long orderId, String reason) {
        findAndValidateOwnership(buyerId, orderId);
        cancelOrderService.cancelOrder(orderId, reason);
    }

    private Order findAndValidateOwnership(String buyerId, Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("El identificador de pedido es obligatorio.");
        }
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", orderId.toString()));

        if (!order.getBuyerId().equals(buyerId)) {
            throw new UnauthorizedRoleOperationException("No tiene autorización para operar sobre este pedido.");
        }
        return order;
    }
}
