package application.domain.services.inventory;

import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ProductStockRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de dominio para consultas de existencias físicas en inventario y bodegas.
 */
@RequiredArgsConstructor
public class ConsultInventoryService {

    private final ProductStockRepositoryPort productStockRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public List<ProductStock> getStockByWarehouse(String warehouseId) {
        if (warehouseId == null || warehouseId.isBlank()) {
            throw new IllegalArgumentException("El identificador de bodega no puede ser nulo o vacío.");
        }
        return productStockRepositoryPort.findByWarehouseId(warehouseId);
    }

    public List<ProductStock> getInventoryByVendor(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            throw new IllegalArgumentException("El identificador del vendedor no puede ser nulo o vacío.");
        }
        List<Product> products = productRepositoryPort.findByVendorId(vendorId);
        List<ProductStock> stockList = new ArrayList<>();
        for (Product product : products) {
            if (product.getId() != null) {
                stockList.addAll(productStockRepositoryPort.findByProductId(product.getId()));
            }
        }
        return stockList;
    }

    public List<ProductStock> getGlobalInventorySummary() {
        return productStockRepositoryPort.findAll();
    }
}
