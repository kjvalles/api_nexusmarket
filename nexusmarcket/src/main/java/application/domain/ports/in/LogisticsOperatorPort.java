package application.domain.ports.in;

import application.domain.models.ProductStock;
import application.domain.models.Warehouse;

import java.util.List;

/**
 * Puerto de entrada para casos de uso del rol Operador Logístico (LOGISTICS_OPERATOR).
 */
public interface LogisticsOperatorPort {

    ProductStock registerInflow(String operatorId, String productId, String variantId, String warehouseId, int quantity, String notes);

    ProductStock registerAdjustment(String operatorId, String productId, String variantId, String warehouseId, int delta, String notes);

    void dispatchOrder(String operatorId, Long orderId, String trackingNumber);

    void deliverOrder(String operatorId, Long orderId);

    List<ProductStock> getWarehouseStock(String warehouseId);

    List<Warehouse> listWarehouses();
}
