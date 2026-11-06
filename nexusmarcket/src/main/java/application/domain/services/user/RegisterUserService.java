package application.domain.services.user;

import application.domain.exceptions.DomainException;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.UserStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Servicio de dominio para registro de usuarios internos del sistema.
 */
@RequiredArgsConstructor
public class RegisterUserService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public User registerUser(User user, String initialPassword) {
        if (user == null) {
            throw new IllegalArgumentException("La información del usuario no puede ser nula.");
        }
        if (initialPassword == null || initialPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña inicial no puede estar vacía.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo electrónico del usuario es obligatorio.");
        }
        if (user.getIdentityDocument() == null || user.getIdentityDocument().isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio.");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio.");
        }

        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new DomainException("El correo electrónico ya se encuentra registrado: " + user.getEmail());
        }
        if (userRepositoryPort.existsByIdentityDocument(user.getIdentityDocument())) {
            throw new DomainException("El documento de identidad ya se encuentra registrado: " + user.getIdentityDocument());
        }

        user.setPassword(passwordServicePort.encode(initialPassword));
        user.setStatus(UserStatus.ACTIVE);
        user.setAuthTokenVersion(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepositoryPort.save(user);
    }
}
