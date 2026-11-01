package application.domain.exceptions;

/**
 * Excepción base y concreta para violaciones de reglas de negocio en la capa de dominio.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
