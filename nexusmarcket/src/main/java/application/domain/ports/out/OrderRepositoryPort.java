package application.domain.ports.out;

import application.domain.models.Order;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de Pedidos comerciales (MySQL).
 */
public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByBuyerId(String buyerId);

    List<Order> findAll();
}
