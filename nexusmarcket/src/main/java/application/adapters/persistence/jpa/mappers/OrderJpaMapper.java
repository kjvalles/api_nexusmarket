package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.entities.OrderLineJpaEntity;
import application.domain.models.Order;
import application.domain.valueobjects.OrderLine;
import application.domain.valueobjects.OrderStatus;

public final class OrderJpaMapper {

    private OrderJpaMapper() {}

    public static OrderJpaEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(domain.getId());
        entity.setBuyerId(domain.getBuyerId());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : OrderStatus.PENDING_PAYMENT.getCode());
        entity.setSubtotal(domain.getSubtotal());
        entity.setTaxes(domain.getTaxes());
        entity.setDiscounts(domain.getDiscounts());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setAdditionalNotes(domain.getAdditionalNotes());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getOrderLines() != null) {
            for (OrderLine line : domain.getOrderLines()) {
                OrderLineJpaEntity lineEntity = new OrderLineJpaEntity();
                lineEntity.setProductId(line.getProductId());
                lineEntity.setProductName(line.getProductName());
                lineEntity.setVariantId(line.getVariantId());
                lineEntity.setUnitPrice(line.getUnitPrice());
                lineEntity.setQuantity(line.getQuantity());
                lineEntity.setSubtotal(line.getSubtotal());
                entity.addLine(lineEntity);
            }
        }
        return entity;
    }

    public static Order toDomain(OrderJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Order domain = new Order();
        domain.setId(entity.getId());
        domain.setBuyerId(entity.getBuyerId());
        domain.setSubtotal(entity.getSubtotal());
        domain.setTaxes(entity.getTaxes());
        domain.setDiscounts(entity.getDiscounts());
        domain.setTotalAmount(entity.getTotalAmount());
        domain.setAdditionalNotes(entity.getAdditionalNotes());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getLines() != null) {
            for (OrderLineJpaEntity lineEntity : entity.getLines()) {
                OrderLine line = new OrderLine(
                        lineEntity.getProductId(),
                        lineEntity.getVariantId(),
                        lineEntity.getProductName(),
                        lineEntity.getUnitPrice(),
                        lineEntity.getQuantity()
                );
                domain.addLine(line);
            }
            domain.setSubtotal(entity.getSubtotal());
            domain.setTaxes(entity.getTaxes());
            domain.setDiscounts(entity.getDiscounts());
            domain.setTotalAmount(entity.getTotalAmount());
        }

        domain.setStatus(orderStatus(entity.getStatus()));
        return domain;
    }

    public static OrderStatus orderStatus(String code) {
        if (code == null) return null;
        if (OrderStatus.PENDING_PAYMENT.getCode().equalsIgnoreCase(code)) return OrderStatus.PENDING_PAYMENT;
        if (OrderStatus.PAID.getCode().equalsIgnoreCase(code)) return OrderStatus.PAID;
        if (OrderStatus.PREPARATION.getCode().equalsIgnoreCase(code)) return OrderStatus.PREPARATION;
        if (OrderStatus.DISPATCHED.getCode().equalsIgnoreCase(code)) return OrderStatus.DISPATCHED;
        if (OrderStatus.DELIVERED.getCode().equalsIgnoreCase(code)) return OrderStatus.DELIVERED;
        if (OrderStatus.COMPLETED.getCode().equalsIgnoreCase(code)) return OrderStatus.COMPLETED;
        if (OrderStatus.CANCELLED.getCode().equalsIgnoreCase(code)) return OrderStatus.CANCELLED;
        return null;
    }
}
