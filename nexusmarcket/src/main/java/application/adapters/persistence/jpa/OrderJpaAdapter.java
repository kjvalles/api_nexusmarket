package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaOrderRepository;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida para persistencia de Pedidos (MySQL vía Spring Data JPA).
 */
@Repository
@RequiredArgsConstructor
public class OrderJpaAdapter implements OrderRepositoryPort {

    private final SpringDataJpaOrderRepository orderRepository;

    @Override
    @Transactional
    public Order save(Order order) {
        if (order == null) {
            return null;
        }
        OrderJpaEntity entity = OrderJpaMapper.toEntity(order);
        OrderJpaEntity saved = orderRepository.save(entity);
        return OrderJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return orderRepository.findById(id).map(OrderJpaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByBuyerId(String buyerId) {
        if (buyerId == null || buyerId.isBlank()) {
            return List.of();
        }
        return orderRepository.findByBuyerId(buyerId).stream()
                .map(OrderJpaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderJpaMapper::toDomain)
                .toList();
    }
}
