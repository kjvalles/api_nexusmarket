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
public class LoginRequestDto {

    @NotBlank(message = "El email o documento de identidad es obligatorio")
    private String emailOrDocument;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
