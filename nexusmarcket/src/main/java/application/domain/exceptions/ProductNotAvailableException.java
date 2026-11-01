package application.domain.exceptions;

/**
 * Lanzada cuando un producto solicitado no está disponible para compra o publicación.
 */
public class ProductNotAvailableException extends DomainException {

    public ProductNotAvailableException(String message) {
        super(message);
    }
}
