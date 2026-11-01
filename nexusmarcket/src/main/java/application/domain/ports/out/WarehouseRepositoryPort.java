package application.domain.ports.out;

import application.domain.models.Warehouse;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de bodegas (MySQL).
 */
public interface WarehouseRepositoryPort {

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(String id);

    List<Warehouse> findByVendorId(String vendorId);

    List<Warehouse> findAll();

    boolean existsById(String id);
}
