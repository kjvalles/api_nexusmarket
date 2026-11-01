package application.domain.exceptions;

/**
 * Lanzada cuando se intenta una transición de estado de usuario inválida.
 */
public class InvalidUserStatusException extends DomainException {

    public InvalidUserStatusException(String message) {
        super(message);
    }
}
