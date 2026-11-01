package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemToCartRequestDto {

    @NotBlank(message = "El identificador del producto es obligatorio")
    private String productId;

    private String variantId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String productName;

    private String variantSku;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal unitPrice;

    @Min(value = 1, message = "La cantidad debe ser de al menos 1 unidad")
    private int quantity;
}
