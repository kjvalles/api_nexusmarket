package application.domain.ports.out;

/**
 * Puerto de salida para codificación y verificación de contraseñas (BCrypt).
 */
public interface PasswordServicePort {

    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);
}
