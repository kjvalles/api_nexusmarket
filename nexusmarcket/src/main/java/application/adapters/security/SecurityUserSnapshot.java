package application.adapters.security;

import application.domain.enums.UserRole;
import application.domain.models.User;
import application.domain.valueobjects.UserStatus;

/**
 * Representación concreta y ligera de un usuario autenticado reconstruido desde claims JWT
 * para evaluación de autorizaciones en memoria sin round-trip a base de datos.
 */
public class SecurityUserSnapshot extends User {

    public SecurityUserSnapshot(String id, String email, UserRole role, UserStatus status, Integer version) {
        setId(id);
        setEmail(email);
        setRole(role != null ? role : UserRole.BUYER);
        setStatus(status != null ? status : UserStatus.ACTIVE);
        setAuthTokenVersion(version != null ? version : 1);
    }
}
