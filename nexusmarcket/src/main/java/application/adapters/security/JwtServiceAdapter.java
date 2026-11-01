package application.adapters.security;

import application.domain.enums.UserRole;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.valueobjects.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Adaptador de salida para emisión, firma y validación criptográfica de tokens JWT usando JJWT 0.12.6.
 */
@Component
public class JwtServiceAdapter implements JwtServicePort {

    private final String secret;
    private final long expirationMs;

    public JwtServiceAdapter(
            @Value("${jwt.secret:TDEATDEATDEATDEATDEATDEATDEATDEA}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user) {
        if (user == null || user.getId() == null || user.getId().isBlank()) {
            throw new IllegalArgumentException("El usuario y su ID son obligatorios para emitir un token.");
        }

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        JwtBuilder builder = Jwts.builder()
                .subject(user.getId())
                .id(UUID.randomUUID().toString())
                .claim("email", user.getEmail() != null ? user.getEmail() : "")
                .claim("role", user.getRole() != null ? user.getRole().name() : UserRole.BUYER.name())
                .claim("ver", user.getAuthTokenVersion() != null ? user.getAuthTokenVersion() : 1)
                .claim("status", user.getStatus() != null ? user.getStatus().getCode() : "ACTIVE")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey());

        return builder.compact();
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String extractUserId(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Integer extractTokenVersion(String token) {
        try {
            Object ver = getClaims(token).get("ver");
            if (ver instanceof Number num) {
                return num.intValue();
            }
            return 1;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User reconstructUser(String token) {
        Claims claims = getClaims(token);
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);
        String roleStr = claims.get("role", String.class);
        String statusStr = claims.get("status", String.class);
        Object verObj = claims.get("ver");
        int version = (verObj instanceof Number num) ? num.intValue() : 1;

        UserRole role = UserRole.BUYER;
        if (roleStr != null) {
            try {
                role = UserRole.valueOf(roleStr);
            } catch (Exception ignored) {}
        }

        UserStatus status = UserStatus.ACTIVE;
        if ("INACTIVE".equalsIgnoreCase(statusStr)) {
            status = UserStatus.INACTIVE;
        } else if ("BLOCKED".equalsIgnoreCase(statusStr)) {
            status = UserStatus.BLOCKED;
        }

        return new SecurityUserSnapshot(userId, email, role, status, version);
    }
}
