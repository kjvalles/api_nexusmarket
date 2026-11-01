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
public class DispatchOrderRequestDto {

    @NotBlank(message = "El número de guía o seguimiento es obligatorio")
    private String trackingNumber;
}
