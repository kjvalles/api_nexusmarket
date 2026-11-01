package application.domain.exceptions;

/**
 * Lanzada cuando un usuario intenta realizar una operación restringida para su rol asignado.
 */
public class UnauthorizedRoleOperationException extends DomainException {

    public UnauthorizedRoleOperationException(String message) {
        super(message);
    }
}
