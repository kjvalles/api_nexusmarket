package application.domain.services.inventory;

import application.domain.exceptions.ProductNotAvailableException;
import application.domain.models.ProductStock;
import application.domain.ports.out.ProductStockRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para consulta y verificación de disponibilidad de stock.
 */
@RequiredArgsConstructor
public class CheckStockAvailabilityService {

    private final ProductStockRepositoryPort productStockRepositoryPort;

    public int getAvailableStock(String productId, String variantId, String warehouseId) {
        return productStockRepositoryPort
                .findByProductVariantAndWarehouse(productId, variantId, warehouseId)
                .map(ProductStock::getSellableQuantity)
                .orElse(0);
    }

    public boolean isAvailable(String productId, String variantId, String warehouseId, int quantityRequested) {
        return getAvailableStock(productId, variantId, warehouseId) >= quantityRequested;
    }

    public void ensureAvailable(String productId, String variantId, String warehouseId, int quantityRequested) {
        int available = getAvailableStock(productId, variantId, warehouseId);
        if (available < quantityRequested) {
            throw new ProductNotAvailableException("Stock insuficiente para el producto " + productId
                    + " en la bodega " + warehouseId + ". Disponible: " + available + ", Solicitado: " + quantityRequested);
        }
    }
}
