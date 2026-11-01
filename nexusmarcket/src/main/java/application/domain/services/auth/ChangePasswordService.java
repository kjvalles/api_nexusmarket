package application.domain.services.auth;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para cambio de contraseña de usuario con invalidación de sesiones activas.
 */
@RequiredArgsConstructor
public class ChangePasswordService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public void changePassword(String userId, String oldPassword, String newPassword) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("El ID de usuario no puede estar vacío.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", userId));

        if (oldPassword != null && !passwordServicePort.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta.");
        }

        user.changePassword(passwordServicePort.encode(newPassword));
        userRepositoryPort.update(user);
    }
}
