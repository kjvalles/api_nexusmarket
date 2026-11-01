package application.domain;

import application.domain.exceptions.InvalidOrderStateException;
import application.domain.models.Order;
import application.domain.valueobjects.OrderLine;
import application.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias del Agregado Order (Dominio Puro)")
class OrderDomainTest {

    @Test
    @DisplayName("Debe inicializar un pedido en estado PENDING_PAYMENT con totales en cero")
    void shouldInitializeOrderWithPendingPaymentStatus() {
        Order order = new Order("buyer-123");

        assertEquals("buyer-123", order.getBuyerId());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getSubtotal());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertTrue(order.getOrderLines().isEmpty());
    }

    @Test
    @DisplayName("Debe recalcular correctamente subtotal, impuestos (19%) y total al agregar líneas")
    void shouldCalculateSubtotalTaxesAndTotal() {
        Order order = new Order("buyer-123");

        OrderLine line1 = new OrderLine("prod-1", "var-1", "Camisa", new BigDecimal("100.00"), 2);
        OrderLine line2 = new OrderLine("prod-2", null, "Gorra", new BigDecimal("50.00"), 1);

        order.addLine(line1);
        order.addLine(line2);

        // Subtotal = (100 * 2) + (50 * 1) = 250.00
        assertEquals(new BigDecimal("250.00"), order.getSubtotal());
        // IVA 19% de 250 = 47.50
        assertEquals(new BigDecimal("47.50"), order.getTaxes());
        // Total = 250.00 + 47.50 = 297.50
        assertEquals(new BigDecimal("297.50"), order.getTotalAmount());
    }

    @Test
    @DisplayName("Debe avanzar secuencialmente a través del ciclo de vida del pedido")
    void shouldTransitionThroughLifecycleSuccessfully() {
        Order order = new Order("buyer-123");
        order.addLine(new OrderLine("prod-1", null, "Teclado", new BigDecimal("80.00"), 1));

        order.markAsPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());

        order.markAsInPreparation();
        assertEquals(OrderStatus.PREPARATION, order.getStatus());

        order.markAsDispatched();
        assertEquals(OrderStatus.DISPATCHED, order.getStatus());

        order.markAsDelivered();
        assertEquals(OrderStatus.DELIVERED, order.getStatus());

        order.markAsCompleted();
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    @DisplayName("Debe rechazar transiciones de estado ilegales lanzando InvalidOrderStateException")
    void shouldRejectInvalidStateTransitions() {
        Order order = new Order("buyer-123");
        // Intentar despachar directamente sin pagar
        assertThrows(InvalidOrderStateException.class, order::markAsDispatched);
        assertThrows(InvalidOrderStateException.class, order::markAsDelivered);
        assertThrows(InvalidOrderStateException.class, order::markAsCompleted);
    }

    @Test
    @DisplayName("Debe impedir modificar líneas cuando el pedido ya está entregado o completado")
    void shouldPreventModificationsWhenOrderIsFinalized() {
        Order order = new Order("buyer-123");
        order.addLine(new OrderLine("prod-1", null, "Mouse", new BigDecimal("30.00"), 1));
        order.markAsPaid();
        order.markAsInPreparation();
        order.markAsDispatched();
        order.markAsDelivered();

        OrderLine newLine = new OrderLine("prod-2", null, "Pad", new BigDecimal("10.00"), 1);
        assertThrows(InvalidOrderStateException.class, () -> order.addLine(newLine));
    }

    @Test
    @DisplayName("Debe impedir la cancelación de un pedido que ya fue entregado")
    void shouldPreventCancellationOfDeliveredOrder() {
        Order order = new Order("buyer-123");
        order.markAsPaid();
        order.markAsInPreparation();
        order.markAsDispatched();
        order.markAsDelivered();

        assertThrows(InvalidOrderStateException.class, () -> order.cancel("Cliente cambió de opinión"));
    }
}
