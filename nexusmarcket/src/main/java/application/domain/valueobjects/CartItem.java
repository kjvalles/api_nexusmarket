package application.domain.valueobjects;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object inmutable que representa un producto añadido a un carrito de compras.
 */
@Getter
public final class CartItem {

    private final String productId;
    private final String variantId;
    private final String productName;
    private final String variantSku;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final LocalDateTime addedAt;

    public CartItem(String productId, String variantId, String productName, String variantSku,
                    int quantity, BigDecimal unitPrice, LocalDateTime addedAt) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El productId no puede ser nulo o vacío.");
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
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.addedAt = addedAt != null ? addedAt : LocalDateTime.now();
    }

    public CartItem(String productId, String variantId, String productName, BigDecimal unitPrice, int quantity) {
        this(productId, variantId, productName, "", quantity, unitPrice, LocalDateTime.now());
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public CartItem withQuantity(int newQuantity) {
        return new CartItem(productId, variantId, productName, variantSku, newQuantity, unitPrice, addedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem item)) return false;
        return Objects.equals(productId, item.productId)
                && Objects.equals(variantId, item.variantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, variantId);
    }
}
