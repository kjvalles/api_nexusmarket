package application.domain.exceptions;

/**
 * Lanzada cuando se intenta una transición ilegal de estado de un pedido
 * o cuando se intenta modificar un pedido finalizado/entregado.
 */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }

    public InvalidOrderStateException(Long orderId, String currentStatus, String targetStatus) {
        super(String.format("Transición inválida de estado para el pedido '%d': de '%s' a '%s'.",
                orderId, currentStatus, targetStatus));
    }
}
