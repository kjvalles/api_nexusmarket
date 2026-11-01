package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSellerRequestDto {

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String identityDocument;

    @NotBlank(message = "El nombre del representante legal es obligatorio")
    private String fullName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;

    @NotBlank(message = "La contraseña inicial es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String initialPassword;

    @NotBlank(message = "La razón social o nombre comercial es obligatoria")
    private String businessName;

    @NotBlank(message = "El NIT o identificación tributaria es obligatorio")
    private String taxId;

    private String businessDescription;
}
