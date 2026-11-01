package application.domain.exceptions;

/**
 * Lanzada cuando no se encuentra una entidad requerida en la capa de dominio.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, Object identifier) {
        super(String.format("%s con identificador '%s' no fue encontrado.", entityName, identifier));
    }
}
