package application.adapters.persistence.mongodb.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Sub-documento embebido para variantes de producto en MongoDB.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDocument {

    private String variantId;
    private String color;
    private String size;
    private String sku;
    private BigDecimal additionalPrice;
    private String imageUrl;
    private boolean active;
}
