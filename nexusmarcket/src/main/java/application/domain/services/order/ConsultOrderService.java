package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedRoleOperationException;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Servicio de dominio para consulta de pedidos comerciales.
 */
@RequiredArgsConstructor
public class ConsultOrderService {

    private final OrderRepositoryPort orderRepositoryPort;

    public List<Order> getOrdersByBuyer(String buyerId) {
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("El identificador del comprador no puede ser nulo o vacío.");
        }
        return orderRepositoryPort.findByBuyerId(buyerId);
    }

    public Order getOrderDetails(String buyerId, Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("El identificador del pedido no puede ser nulo.");
        }
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", orderId.toString()));

        if (!buyerId.equals(order.getBuyerId())) {
            throw new UnauthorizedRoleOperationException("No tiene permisos para consultar este pedido.");
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepositoryPort.findAll();
    }
}
