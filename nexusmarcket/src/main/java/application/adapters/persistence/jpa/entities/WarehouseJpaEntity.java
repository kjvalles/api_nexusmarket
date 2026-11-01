package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA para persistencia de Bodegas (tabla warehouses).
 */
@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class WarehouseJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "vendor_id", length = 36)
    private String vendorId;

    @Column(name = "type", length = 40)
    private String type;

    @Column(name = "status", length = 40)
    private String status;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "max_capacity")
    private int maxCapacity;

    @Column(name = "street", length = 150)
    private String street;

    @Column(name = "city", length = 80)
    private String city;

    @Column(name = "state", length = 80)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 80)
    private String country;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
