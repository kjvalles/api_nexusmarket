package application.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class ProductVariant {

    private String variantId;
    private String color;
    private String size;

    /**
     * Stock Keeping Unit: código alfanumérico único por variante para control de inventario.
     */
    private String sku;

    private BigDecimal additionalPrice;
    private String imageUrl;
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariant v)) return false;
        return Objects.equals(variantId, v.variantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantId);
    }
}
