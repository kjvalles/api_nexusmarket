package application.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class CartItem {
    private String productId;
    private String variantId;
    private String productName;
    private String variantSku;
    private int quantity;
    private BigDecimal unitPrice;
    private LocalDateTime addedAt;

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
