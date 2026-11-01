package application.domain.models;

import application.domain.enums.UserRole;
import application.domain.exceptions.InvalidUserStatusException;
import application.domain.valueobjects.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad base abstracta que representa a cualquier usuario autenticable en NexusMarket.
 */
@Getter
@Setter
public abstract class User {

    private String id;
    private String identityDocument;
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
    private UserStatus status;
    private Integer authTokenVersion = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Cambia el estado del usuario e invalida los tokens previamente emitidos.
     *
     * @param target Nuevo estado de usuario.
     */
    public void changeStatus(UserStatus target) {
        if (target == null) {
            throw new InvalidUserStatusException("El estado destino del usuario no puede ser nulo.");
        }
        this.status = target;
        this.updatedAt = LocalDateTime.now();
        bumpAuthTokenVersion();
    }

    /**
     * Actualiza la contraseña encriptada e invalida los tokens JWT anteriores.
     *
     * @param newHashedPassword Contraseña ya procesada con hash seguro.
     */
    public void changePassword(String newHashedPassword) {
        if (newHashedPassword == null || newHashedPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }
        this.password = newHashedPassword;
        this.updatedAt = LocalDateTime.now();
        bumpAuthTokenVersion();
    }

    /**
     * Incrementa la versión del token de autenticación para invalidar sesiones activas.
     */
    public void bumpAuthTokenVersion() {
        this.authTokenVersion = (this.authTokenVersion == null ? 1 : this.authTokenVersion + 1);
    }

    /**
     * Indica si el usuario está activo y habilitado para operar.
     */
    public boolean isActive() {
        return status != null && "ACTIVE".equalsIgnoreCase(status.getCode());
    }
}
