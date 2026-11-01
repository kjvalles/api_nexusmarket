package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.adapters.persistence.jpa.mappers.WarehouseJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaWarehouseRepository;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida para persistencia de Bodegas (MySQL vía Spring Data JPA).
 */
@Repository
@RequiredArgsConstructor
public class WarehouseJpaAdapter implements WarehouseRepositoryPort {

    private final SpringDataJpaWarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public Warehouse save(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        WarehouseJpaEntity entity = WarehouseJpaMapper.toEntity(warehouse);
        WarehouseJpaEntity saved = warehouseRepository.save(entity);
        return WarehouseJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Warehouse> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return warehouseRepository.findById(id).map(WarehouseJpaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> findByVendorId(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            return List.of();
        }
        return warehouseRepository.findByVendorId(vendorId).stream()
                .map(WarehouseJpaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll().stream()
                .map(WarehouseJpaMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return id != null && !id.isBlank() && warehouseRepository.existsById(id);
    }
}
