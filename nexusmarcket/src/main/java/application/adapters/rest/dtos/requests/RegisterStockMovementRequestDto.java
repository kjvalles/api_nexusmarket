package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterStockMovementRequestDto {

    @NotBlank(message = "El identificador del producto es obligatorio")
    private String productId;

    private String variantId;

    @NotBlank(message = "El identificador de la bodega es obligatorio")
    private String warehouseId;

    @NotNull(message = "La cantidad es obligatoria")
    private Integer quantity;

    private String notes;
}
