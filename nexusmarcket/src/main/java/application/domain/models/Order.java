package application.domain.models;

import application.domain.exceptions.InvalidOrderStateException;
import application.domain.valueobjects.OrderLine;
import application.domain.valueobjects.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Raíz del Agregado de Pedidos (Order Aggregate Root).
 * Encapsula el ciclo de vida comercial, cálculo de totales y protección de inmutabilidad.
 */
@Getter
@Setter
public class Order {

    private Long id;
    private String buyerId;
    private OrderStatus status;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal taxes = BigDecimal.ZERO;
    private BigDecimal discounts = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private String additionalNotes;
    private List<OrderLine> orderLines = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order() {
        this.status = OrderStatus.PENDING_PAYMENT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Order(String buyerId) {
        this();
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("El identificador del comprador no puede ser nulo o vacío.");
        }
        this.buyerId = buyerId;
    }

    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }

    public void addLine(OrderLine line) {
        assertModifiable();
        if (line == null) {
            throw new IllegalArgumentException("La línea de pedido no puede ser nula.");
        }
        this.orderLines.add(line);
        recalculateTotals();
    }

    public void removeLine(String productId, String variantId) {
        assertModifiable();
        this.orderLines.removeIf(line ->
                line.getProductId().equals(productId) &&
                (variantId == null || variantId.equals(line.getVariantId())));
        recalculateTotals();
    }

    public void recalculateTotals() {
        BigDecimal sumSubtotal = BigDecimal.ZERO;
        for (OrderLine line : orderLines) {
            sumSubtotal = sumSubtotal.add(line.getSubtotal());
        }
        this.subtotal = sumSubtotal;
        // Impuestos base estimados (19% IVA estándar por defecto)
        this.taxes = this.subtotal.multiply(new BigDecimal("0.19")).setScale(2, RoundingMode.HALF_UP);
        if (this.discounts == null) {
            this.discounts = BigDecimal.ZERO;
        }
        this.totalAmount = this.subtotal.add(this.taxes).subtract(this.discounts);
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsPaid() {
        if (!OrderStatus.PENDING_PAYMENT.equals(this.status)) {
            throw new InvalidOrderStateException(id, getStatusCode(), OrderStatus.PAID.getCode());
        }
        this.status = OrderStatus.PAID;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsInPreparation() {
        if (!OrderStatus.PAID.equals(this.status)) {
            throw new InvalidOrderStateException(id, getStatusCode(), OrderStatus.PREPARATION.getCode());
        }
        this.status = OrderStatus.PREPARATION;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDispatched() {
        if (!OrderStatus.PREPARATION.equals(this.status) && !OrderStatus.PAID.equals(this.status)) {
            throw new InvalidOrderStateException(id, getStatusCode(), OrderStatus.DISPATCHED.getCode());
        }
        this.status = OrderStatus.DISPATCHED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDelivered() {
        if (!OrderStatus.DISPATCHED.equals(this.status)) {
            throw new InvalidOrderStateException(id, getStatusCode(), OrderStatus.DELIVERED.getCode());
        }
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCompleted() {
        if (!OrderStatus.DELIVERED.equals(this.status)) {
            throw new InvalidOrderStateException(id, getStatusCode(), OrderStatus.COMPLETED.getCode());
        }
        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (OrderStatus.COMPLETED.equals(this.status) || OrderStatus.DELIVERED.equals(this.status)) {
            throw new InvalidOrderStateException("No se puede cancelar un pedido que ya ha sido entregado o completado.");
        }
        this.status = OrderStatus.CANCELLED;
        this.additionalNotes = (this.additionalNotes == null ? "" : this.additionalNotes + " | ") + "Motivo de cancelación: " + reason;
        this.updatedAt = LocalDateTime.now();
    }

    private void assertModifiable() {
        if (OrderStatus.COMPLETED.equals(this.status) || OrderStatus.DELIVERED.equals(this.status) || OrderStatus.CANCELLED.equals(this.status)) {
            throw new InvalidOrderStateException("El pedido finalizado, entregado o cancelado es inmutable y no puede modificarse.");
        }
    }

    private String getStatusCode() {
        return this.status != null ? this.status.getCode() : "UNKNOWN";
    }
}
