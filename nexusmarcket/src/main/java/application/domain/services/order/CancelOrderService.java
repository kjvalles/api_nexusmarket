package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para la cancelación justificada de pedidos.
 */
@RequiredArgsConstructor
public class CancelOrderService {

    private final OrderRepositoryPort orderRepositoryPort;

    public Order cancelOrder(Long orderId, String reason) {
        if (orderId == null) {
            throw new IllegalArgumentException("El identificador del pedido no puede ser nulo.");
        }

        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", orderId.toString()));

        order.cancel(reason);
        return orderRepositoryPort.save(order);
    }
}
