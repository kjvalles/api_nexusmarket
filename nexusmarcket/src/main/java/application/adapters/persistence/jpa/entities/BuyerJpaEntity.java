package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad JPA especializada para Compradores (tabla buyers).
 */
@Entity
@Table(name = "buyers")
@Getter
@Setter
@NoArgsConstructor
public class BuyerJpaEntity extends UserJpaEntity {

    @Column(name = "commercial_status", length = 40)
    private String commercialStatus;

    @Column(name = "virtual_balance", precision = 12, scale = 2)
    private BigDecimal virtualBalance = BigDecimal.ZERO;

    @Column(name = "completed_orders_count")
    private int completedOrdersCount = 0;

    @Column(name = "rating")
    private double rating = 5.0;

    @Column(name = "primary_street", length = 150)
    private String primaryStreet;

    @Column(name = "primary_city", length = 80)
    private String primaryCity;

    @Column(name = "primary_state", length = 80)
    private String primaryState;

    @Column(name = "primary_postal_code", length = 20)
    private String primaryPostalCode;

    @Column(name = "primary_country", length = 80)
    private String primaryCountry;

    @Column(name = "additional_addresses", columnDefinition = "TEXT")
    private String additionalAddresses;
}
