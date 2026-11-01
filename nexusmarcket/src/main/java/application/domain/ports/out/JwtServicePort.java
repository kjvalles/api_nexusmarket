package application.domain.ports.out;

import application.domain.models.User;

/**
 * Puerto de salida para emisión, validación y extracción de información en tokens JWT.
 */
public interface JwtServicePort {

    String generateToken(User user);

    boolean validateToken(String token);

    String extractUserId(String token);

    Integer extractTokenVersion(String token);

    User reconstructUser(String token);
}
