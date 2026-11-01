package application.domain.exceptions;

/**
 * Excepción lanzada cuando la autenticación falla por credenciales erróneas.
 */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Credenciales inválidas proporcionadas.");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
