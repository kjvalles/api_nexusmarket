package application.adapters.useCases;

import application.domain.models.ProductStock;
import application.domain.models.Warehouse;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.ManageWarehouseService;
import application.domain.services.inventory.RegisterInventoryMovementService;
import application.domain.services.order.TransitionOrderStatusService;
import application.domain.valueobjects.InventoryMovementType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para operaciones del rol Operador Logístico (LOGISTICS_OPERATOR).
 * Orquesta exclusivamente servicios de dominio sin acceso directo a repositorios.
 */
@Service
@RequiredArgsConstructor
public class LogisticsOperatorUseCaseImpl implements LogisticsOperatorPort {

    private final RegisterInventoryMovementService registerInventoryMovementService;
    private final TransitionOrderStatusService transitionOrderStatusService;
    private final ConsultInventoryService consultInventoryService;
    private final ManageWarehouseService manageWarehouseService;

    @Override
    public ProductStock registerInflow(String operatorId, String productId, String variantId,
                                        String warehouseId, int quantity, String notes) {
        return registerInventoryMovementService.registerMovement(
                productId,
                variantId,
                warehouseId,
                InventoryMovementType.MANUAL_ENTRY,
                quantity,
                null,
                operatorId,
                notes
        );
    }

    @Override
    public ProductStock registerAdjustment(String operatorId, String productId, String variantId,
                                            String warehouseId, int delta, String notes) {
        if (delta == 0) {
            throw new IllegalArgumentException("El delta de ajuste no puede ser cero.");
        }

        InventoryMovementType type = delta > 0 ? InventoryMovementType.MANUAL_ENTRY : InventoryMovementType.LOSS_ADJUSTMENT;
        int absQuantity = Math.abs(delta);

        return registerInventoryMovementService.registerMovement(
                productId,
                variantId,
                warehouseId,
                type,
                absQuantity,
                null,
                operatorId,
                notes
        );
    }

    @Override
    public void dispatchOrder(String operatorId, Long orderId, String trackingNumber) {
        transitionOrderStatusService.markAsDispatched(orderId, trackingNumber);
    }

    @Override
    public void deliverOrder(String operatorId, Long orderId) {
        transitionOrderStatusService.markAsDelivered(orderId);
    }

    @Override
    public List<ProductStock> getWarehouseStock(String warehouseId) {
        return consultInventoryService.getStockByWarehouse(warehouseId);
    }

    @Override
    public List<Warehouse> listWarehouses() {
        return manageWarehouseService.listWarehouses();
    }
}
