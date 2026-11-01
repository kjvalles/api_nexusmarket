package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductVariantRequestDto {

    private String variantId;
    private String color;
    private String size;

    @NotBlank(message = "El SKU de la variante es obligatorio")
    private String sku;

    @PositiveOrZero(message = "El precio adicional debe ser mayor o igual a cero")
    private BigDecimal additionalPrice;

    private String imageUrl;
}
