package application.domain.services.auth;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para cierre de sesión e invalidación de tokens JWT activos.
 */
@RequiredArgsConstructor
public class LogoutService {

    private final UserRepositoryPort userRepositoryPort;

    public void logout(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("El ID de usuario no puede estar vacío.");
        }

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", userId));

        user.bumpAuthTokenVersion();
        userRepositoryPort.update(user);
    }
}
