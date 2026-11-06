package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidUserStatusException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio para transiciones controladas de estado en cuentas de usuario.
 */
@RequiredArgsConstructor
public class ChangeUserStatusService {

    private final UserRepositoryPort userRepositoryPort;

    public void changeStatus(String targetUserId, String statusCode) {
        if (targetUserId == null || targetUserId.isBlank()) {
            throw new IllegalArgumentException("El identificador del usuario objetivo es obligatorio.");
        }
        if (statusCode == null || statusCode.isBlank()) {
            throw new IllegalArgumentException("El código de estado no puede estar vacío.");
        }

        User user = userRepositoryPort.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", targetUserId));

        UserStatus targetStatus;
        if ("ACTIVE".equalsIgnoreCase(statusCode)) {
            targetStatus = UserStatus.ACTIVE;
        } else if ("INACTIVE".equalsIgnoreCase(statusCode)) {
            targetStatus = UserStatus.INACTIVE;
        } else if ("BLOCKED".equalsIgnoreCase(statusCode)) {
            targetStatus = UserStatus.BLOCKED;
        } else {
            throw new InvalidUserStatusException("Código de estado no reconocido: " + statusCode);
        }

        user.changeStatus(targetStatus);
        userRepositoryPort.update(user);
    }
}
