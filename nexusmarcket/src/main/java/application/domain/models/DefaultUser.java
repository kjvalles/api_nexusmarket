package application.domain.models;

import application.domain.enums.UserRole;

/**
 * Implementación concreta básica de User para roles administrativos, logísticos y de supervisión.
 */
public class DefaultUser extends User {

    public DefaultUser() {
        super();
    }

    public DefaultUser(UserRole role) {
        super();
        setRole(role);
    }
}
