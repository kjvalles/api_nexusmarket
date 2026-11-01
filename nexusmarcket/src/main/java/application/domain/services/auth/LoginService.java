package application.domain.services.auth;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.models.AuthenticationResult;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Servicio de dominio para autenticación y emisión de tokens.
 */
@RequiredArgsConstructor
public class LoginService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final JwtServicePort jwtServicePort;

    public AuthenticationResult login(String identifier, String rawPassword) {
        if (identifier == null || identifier.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidCredentialsException("El identificador y la contraseña son obligatorios.");
        }

        // Búsqueda por email o por documento de identidad
        Optional<User> userOpt = userRepositoryPort.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepositoryPort.findByIdentityDocument(identifier);
        }

        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        User user = userOpt.get();

        if (!passwordServicePort.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new DomainException("La cuenta de usuario se encuentra inactiva o bloqueada.");
        }

        String token = jwtServicePort.generateToken(user);
        return new AuthenticationResult(user, token);
    }
}
