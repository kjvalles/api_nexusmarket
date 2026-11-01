package application.domain.valueobjects;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object inmutable que representa una línea congelada (snapshot) dentro de un pedido.
 */
@Getter
public final class OrderLine {

    private final String productId;
    private final String variantId;
    private final String productName;
    private final String variantSku;
    private final String productTypeCode;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal subtotal;

    public OrderLine(String productId, String variantId, String productName, String variantSku,
                     String productTypeCode, int quantity, BigDecimal unitPrice) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El productId de la línea de pedido no puede ser nulo o vacío.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o nulo.");
        }
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName != null ? productName : "";
        this.variantSku = variantSku != null ? variantSku : "";
        this.productTypeCode = productTypeCode != null ? productTypeCode : "PHYSICAL";
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public OrderLine(String productId, String variantId, String productName, BigDecimal unitPrice, int quantity) {
        this(productId, variantId, productName, "", "PHYSICAL", quantity, unitPrice);
    }

    public OrderLine(String productId, String variantId, String productName, String variantSku,
                     String productTypeCode, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName != null ? productName : "";
        this.variantSku = variantSku != null ? variantSku : "";
        this.productTypeCode = productTypeCode != null ? productTypeCode : "PHYSICAL";
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal != null ? subtotal : unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLine that)) return false;
        return quantity == that.quantity
                && Objects.equals(productId, that.productId)
                && Objects.equals(variantId, that.variantId)
                && Objects.equals(unitPrice, that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, variantId, quantity, unitPrice);
    }
}
