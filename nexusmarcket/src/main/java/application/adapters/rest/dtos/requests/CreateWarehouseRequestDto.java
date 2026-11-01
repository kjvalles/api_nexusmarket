package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseRequestDto {

    private String id;

    @NotBlank(message = "El nombre de la bodega es obligatorio")
    private String name;

    private String vendorId;

    @NotBlank(message = "El tipo de bodega es obligatorio (INTERNAL o EXTERNAL)")
    private String type;

    private String description;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Positive(message = "La capacidad máxima debe ser mayor a cero")
    private Integer maxCapacity;

    @NotBlank(message = "La calle o dirección física es obligatoria")
    private String street;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El departamento o estado es obligatorio")
    private String state;

    @NotBlank(message = "El código postal es obligatorio")
    private String postalCode;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    private String additionalInstructions;
}
