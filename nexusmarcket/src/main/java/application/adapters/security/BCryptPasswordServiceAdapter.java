package application.adapters.security;

import application.domain.ports.out.PasswordServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador de seguridad para codificación y verificación de contraseñas usando BCrypt.
 */
@Component
@RequiredArgsConstructor
public class BCryptPasswordServiceAdapter implements PasswordServicePort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("La contraseña a codificar no puede ser nula.");
        }
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
