package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequestDto {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    private String description;

    @NotBlank(message = "La categoría del producto es obligatoria")
    private String category;

    @NotNull(message = "El precio base es obligatorio")
    @Positive(message = "El precio base debe ser mayor a cero")
    private BigDecimal basePrice;

    @NotBlank(message = "El tipo de producto es obligatorio (PHYSICAL o DIGITAL)")
    private String productType;

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<CreateProductVariantRequestDto> variants = new ArrayList<>();
}
