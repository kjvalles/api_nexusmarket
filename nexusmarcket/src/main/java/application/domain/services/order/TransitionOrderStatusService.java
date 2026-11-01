package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para transiciones controladas de estado en el ciclo de vida del pedido.
 */
@RequiredArgsConstructor
public class TransitionOrderStatusService {

    private final OrderRepositoryPort orderRepositoryPort;

    public Order markAsPaid(Long orderId) {
        Order order = findOrder(orderId);
        order.markAsPaid();
        return orderRepositoryPort.save(order);
    }

    public Order markAsInPreparation(Long orderId) {
        Order order = findOrder(orderId);
        order.markAsInPreparation();
        return orderRepositoryPort.save(order);
    }

    public Order markAsDispatched(Long orderId) {
        Order order = findOrder(orderId);
        order.markAsDispatched();
        return orderRepositoryPort.save(order);
    }

    public Order markAsDelivered(Long orderId) {
        Order order = findOrder(orderId);
        order.markAsDelivered();
        return orderRepositoryPort.save(order);
    }

    public Order markAsCompleted(Long orderId) {
        Order order = findOrder(orderId);
        order.markAsCompleted();
        return orderRepositoryPort.save(order);
    }

    private Order findOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("El identificador del pedido no puede ser nulo.");
        }
        return orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", orderId.toString()));
    }
}
