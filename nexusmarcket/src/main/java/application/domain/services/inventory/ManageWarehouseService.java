package application.domain.services.inventory;

import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Servicio de dominio para gestión y administración de bodegas físicas.
 */
@RequiredArgsConstructor
public class ManageWarehouseService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException("La información de la bodega no puede ser nula.");
        }
        if (warehouse.getName() == null || warehouse.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la bodega es obligatorio.");
        }
        return warehouseRepositoryPort.save(warehouse);
    }

    public List<Warehouse> listWarehouses() {
        return warehouseRepositoryPort.findAll();
    }
}
