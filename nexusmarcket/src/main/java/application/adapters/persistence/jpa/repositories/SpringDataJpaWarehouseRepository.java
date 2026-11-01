package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJpaWarehouseRepository extends JpaRepository<WarehouseJpaEntity, String> {

    List<WarehouseJpaEntity> findByVendorId(String vendorId);
}
