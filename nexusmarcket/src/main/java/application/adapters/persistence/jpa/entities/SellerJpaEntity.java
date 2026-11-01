package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA especializada para Vendedores (tabla sellers).
 */
@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
public class SellerJpaEntity extends UserJpaEntity {

    @Column(name = "business_name", length = 150)
    private String businessName;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "seller_status", length = 40)
    private String sellerStatus;

    @Column(name = "business_description", length = 500)
    private String businessDescription;

    @Column(name = "rating")
    private double rating = 5.0;

    @Column(name = "incorporation_date")
    private LocalDateTime incorporationDate;

    @Column(name = "active_products_count")
    private int activeProductsCount = 0;

    @Column(name = "warehouse_ids", columnDefinition = "TEXT")
    private String warehouseIds;
}
