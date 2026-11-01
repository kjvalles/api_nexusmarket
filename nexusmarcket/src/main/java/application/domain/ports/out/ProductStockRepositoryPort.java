package application.domain.ports.out;

import application.domain.models.ProductStock;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de saldos de inventario (MySQL).
 */
public interface ProductStockRepositoryPort {

    ProductStock save(ProductStock stock);

    Optional<ProductStock> findByProductVariantAndWarehouse(String productId, String variantId, String warehouseId);

    List<ProductStock> findByProductId(String productId);

    List<ProductStock> findByWarehouseId(String warehouseId);

    List<ProductStock> findAll();
}
