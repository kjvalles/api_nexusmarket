package application.adapters.rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponseDto {
    private String variantId;
    private String color;
    private String size;
    private String sku;
    private BigDecimal additionalPrice;
    private String imageUrl;
    private boolean active;
}
