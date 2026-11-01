package application.domain.models;

import application.domain.enums.UserRole;
import application.domain.exceptions.DomainException;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerCommercialStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio que representa a un Comprador registrado.
 */
@Getter
@Setter
public class Buyer extends User {

    private Address primaryAddress;
    private List<Address> additionalAddresses = new ArrayList<>();
    private BuyerCommercialStatus commercialStatus;
    private BigDecimal virtualBalance = BigDecimal.ZERO;
    private int completedOrdersCount = 0;
    private double rating = 5.0;

    public Buyer() {
        setRole(UserRole.BUYER);
    }

    public void addAdditionalAddress(Address address) {
        if (address != null) {
            if (this.additionalAddresses == null) {
                this.additionalAddresses = new ArrayList<>();
            }
            this.additionalAddresses.add(address);
        }
    }

    public void creditBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a acreditar debe ser mayor a cero.");
        }
        this.virtualBalance = (this.virtualBalance == null ? BigDecimal.ZERO : this.virtualBalance).add(amount);
    }

    public void debitBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a debitar debe ser mayor a cero.");
        }
        if (this.virtualBalance == null || this.virtualBalance.compareTo(amount) < 0) {
            throw new DomainException("Saldo virtual insuficiente para realizar la transacción.");
        }
        this.virtualBalance = this.virtualBalance.subtract(amount);
    }

    public void incrementCompletedOrders() {
        this.completedOrdersCount++;
    }
}
