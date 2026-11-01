package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutOrderRequestDto {

    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String street;

    @NotBlank(message = "La ciudad de entrega es obligatoria")
    private String city;

    @NotBlank(message = "El departamento o estado de entrega es obligatorio")
    private String state;

    @NotBlank(message = "El código postal es obligatorio")
    private String postalCode;

    @NotBlank(message = "El país de entrega es obligatorio")
    private String country;

    private String additionalInstructions;

    private String additionalNotes;
}
