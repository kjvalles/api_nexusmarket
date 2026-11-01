package application.domain.exceptions;

/**
 * Lanzada cuando una operación causaría un saldo de inventario negativo.
 */
public class NegativeStockException extends DomainException {

    public NegativeStockException(String message) {
        super(message);
    }

    public NegativeStockException(String productId, String warehouseId, int requested, int currentStock) {
        super(String.format("Existencias insuficientes para el producto '%s' en la bodega '%s'. Solicitado: %d, Disponible: %d.",
                productId, warehouseId, requested, currentStock));
    }
}
